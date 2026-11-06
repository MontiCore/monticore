/* (c) https://github.com/MontiCore/monticore */
package de.monticore;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.FileType;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.*;
import org.gradle.work.ChangeType;
import org.gradle.work.DisableCachingByDefault;
import org.gradle.work.FileChange;
import org.gradle.work.InputChanges;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * This task enables the calling of MontiTrans TFGenTools
 * in a subsequent matter, reducing the class loading by reusing a classloader,
 * while still enabling gradle to control individual files.
 * <p>
 * Current JVM implementations are slow at loading MontiCore Mill classes,
 * for example loading the CD4CodeTRMill requires ~30seconds on a decent computer.
 * <p>
 * It behaves similar to the JavaExec task, but does not fork the JVM.
 * <p>
 * A single task may process any number of models (see {@link #getModels()}).
 * The task is incremental: Gradle skips it, if nothing changed, and otherwise
 * the TFGenTool is only invoked for added or modified models.
 * If a non-incremental input changes (e.g., the classpath or the handwritten code),
 * Gradle removes the previous outputs of the task and all models are regenerated.
 * The TFGenTool is invoked once per model, so every TFGenTool supporting a single
 * {@code -i} argument can be used.
 * <pre>
 * tasks.register('generateStatechartTRRules', MontiTransExec) {
 *   classPath.from(...)
 *   TFGenTool = 'mc.testcases.statechart.tr.StatechartTFGenTool'
 *   models.from(fileTree("src/main/models/statechart/mtr") { include '**&#47;*.mtr' })
 *   outputDir = file("$buildDir/generated-sources/trafogen")
 *   handWrittenCodeDir.from(file("src/main/java"))
 * }
 * </pre>
 * Known issues:
 *  - task is not cached
 *  - the outputs of removed models are only removed with the next non-incremental execution
 */
@DisableCachingByDefault(because = "Multiple MontiTransExec tasks commonly share an output directory")
public abstract class MontiTransExec extends DefaultTask {

  /**
   * Maximum number of cached TFGenTool classloaders. Evicted classloaders are closed,
   * which releases the file handles on their classpath (e.g., to the jars of a previous build).
   */
  protected static final int MAX_CACHED_CLASSLOADERS = 4;

  // We use static by design here to cache the TFGenTools main method
  protected static final Map<Integer, LoadedTFGenTool> tfgenMethodCache =
      new LinkedHashMap<>(MAX_CACHED_CLASSLOADERS, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<Integer, LoadedTFGenTool> eldest) {
          if (size() > MAX_CACHED_CLASSLOADERS) {
            eldest.getValue().close();
            return true;
          }
          return false;
        }
      };

  @Input
  public abstract Property<String> getTFGenTool();

  @Classpath
  public abstract ConfigurableFileCollection getClassPath();

  /**
   * The models (e.g., .mtr files) to process, e.g., a fileTree.
   */
  @InputFiles
  @SkipWhenEmpty
  @PathSensitive(PathSensitivity.RELATIVE)
  @IgnoreEmptyDirectories
  public abstract ConfigurableFileCollection getModels();

  /**
   * A single model to process. Shorthand for {@code models.from(file)}, kept for compatibility.
   */
  @Internal("Part of the models")
  public abstract RegularFileProperty getInput();

  @OutputDirectory
  public abstract DirectoryProperty getOutputDir();

  @InputFiles
  @Optional
  @PathSensitive(PathSensitivity.RELATIVE)
  @IgnoreEmptyDirectories
  public abstract ConfigurableFileCollection getHandWrittenCodeDir();

  @Optional
  @Input
  public abstract Property<Boolean> getUseCache();

  public MontiTransExec() {
    setGroup("MontiTrans");

    getModels().from(getInput().getAsFile().map(List::of).orElse(List.of()));

    // Only one task using MontiCore Mills at the same time
    usesService(getProject().getGradle().getSharedServices().registerIfAbsent(
        MCPlugin.MC_MILL_BUILD_SERVICE, MCMillBuildService.class, spec -> spec.getMaxParallelUsages().set(1)));
  }

  @TaskAction
  void exec(InputChanges inputChanges) throws IOException {
    LoadedTFGenTool tool = loadOrReuseTFGenTool();
    for (FileChange change : inputChanges.getFileChanges(getModels())) {
      if (change.getFileType() == FileType.DIRECTORY) {
        continue;
      }
      if (change.getChangeType() == ChangeType.REMOVED) {
        getLogger().info("{} was removed, its outputs are removed with the next non-incremental execution",
            change.getFile());
        continue;
      }
      getLogger().info("Generating transformation for {}", change.getFile());
      tool.run(createArgs(change.getFile()), change.getFile());
    }
  }

  /**
   * @return the arguments for the TFGen CLI
   */
  protected String[] createArgs(File model) {
    List<String> args = new ArrayList<>();
    args.add("-i");
    args.add(model.toString());
    args.add("-o");
    args.add(getOutputDir().getAsFile().get().toPath().toString());

    if (!getHandWrittenCodeDir().isEmpty()) {
      args.add("-hwc");
      getHandWrittenCodeDir().forEach(x -> args.add(x.toPath().toString()));
    }
    return args.toArray(new String[0]);
  }

  /* ======================================================================
   * TFGenTool loading
   * ====================================================================== */

  protected LoadedTFGenTool loadOrReuseTFGenTool() throws IOException {
    if (getUseCache().isPresent() && !getUseCache().get()) {
      return loadTFGenTool(); // cache disabled using config
    }
    // it may be cached depending on the TFGenTool class name and classpath
    int hash = computeHash();
    synchronized (tfgenMethodCache) {
      LoadedTFGenTool tool = tfgenMethodCache.get(hash);
      if (tool == null) {
        tool = loadTFGenTool();
        tfgenMethodCache.put(hash, tool);
      }
      return tool;
    }
  }

  protected LoadedTFGenTool loadTFGenTool() {
    // Prepare the classpath
    URL[] urls = new URL[getClassPath().getFiles().size()];
    int i = 0;
    try {
      for (File f : getClassPath().getFiles()) {
        urls[i++] = f.toURI().toURL();
      }
    }
    catch (MalformedURLException e) {
      throw new GradleException("Invalid classpath of " + getPath(), e);
    }

    // Construct a new classloader without a delegated-parent and with the configured classpath
    URLClassLoader classLoader = new URLClassLoader(urls, null);
    try {
      Method main = classLoader.loadClass(getTFGenTool().get()) // Load the TFGenClass
          .getMethod("main", String[].class); // and return the main method
      return new LoadedTFGenTool(classLoader, main);
    }
    catch (ClassNotFoundException | NoSuchMethodException e) {
      LoadedTFGenTool.close(classLoader);
      throw new GradleException("Unable to load the TFGenTool " + getTFGenTool().get(), e);
    }
  }

  /**
   * Returns a hash for the name of the TFGenTool and the contents of the files within the classpath.
   * @see java.util.Objects#hash(java.lang.Object...)
   * @return the hash
   */
  protected int computeHash() throws IOException {
    int result = getTFGenTool().get().hashCode(); // hash of the TFGenTool class
    for (File f : getClassPath()) // hash of the content of the files within the classpath
      result = 31 * result + computeHash(f);
    return result;
  }

  /**
   * Returns the hash for the content of a file or all files within a directory.
   */
  protected static int computeHash(File f) throws IOException {
    if (!f.exists()) {
      return 0;
    } else if (f.isDirectory()) {
      int result = f.hashCode();
      for (File inner : f.listFiles())
        result = 31 * result + computeHash(inner);
      return result;
    } else {
      return Files.asByteSource(f).hash(Hashing.goodFastHash(32)).hashCode();
    }
  }

  /**
   * A TFGenTool loaded within its own classloader.
   */
  protected static class LoadedTFGenTool {
    protected final URLClassLoader classLoader;
    protected final Method main;

    protected LoadedTFGenTool(URLClassLoader classLoader, Method main) {
      this.classLoader = classLoader;
      this.main = main;
    }

    /**
     * Invokes the static main method of the TFGenTool.
     * Fails, if the tool throws an exception or logs an error
     * (otherwise, Gradle would consider the erroneous outputs to be up-to-date).
     */
    void run(String[] args, File model) {
      long errorsBefore = getErrorCount();
      try {
        main.invoke(null, (Object) args);
      }
      catch (InvocationTargetException e) {
        throw new GradleException("The TFGenTool failed for " + model + ": " + e.getCause().getMessage(), e.getCause());
      }
      catch (IllegalAccessException e) {
        throw new GradleException("Unable to invoke the TFGenTool for " + model, e);
      }
      if (getErrorCount() > errorsBefore) {
        throw new GradleException("The TFGenTool reported errors for " + model);
      }
    }

    /**
     * @return the error count of the tool's Log (which is loaded by the tool's classloader),
     * or 0 if the tool does not use it
     */
    protected long getErrorCount() {
      try {
        return (long) classLoader.loadClass("de.se_rwth.commons.logging.Log")
            .getMethod("getErrorCount").invoke(null);
      }
      catch (ReflectiveOperationException | ClassCastException e) {
        return 0;
      }
    }

    void close() {
      close(classLoader);
    }

    static void close(URLClassLoader classLoader) {
      try {
        classLoader.close();
      }
      catch (IOException ignored) {
        // the classloader is not used anymore
      }
    }
  }

}
