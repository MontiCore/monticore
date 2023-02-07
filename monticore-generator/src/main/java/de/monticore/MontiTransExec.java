/* (c) https://github.com/MontiCore/monticore */
package de.monticore;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This task enables the calling of MontiTrans TFGenTools
 * in a subsequent matter, reducing the class loading by reusing a classloader,
 * while still enabling gradle to control individual files.
 *
 * Current JVM implementations are slow at loading MontiCore Mill classes,
 * for example loading the CD4CodeTRMill requires ~30seconds on a decent computer.
 *
 * It behaves similar to the JavaExec task, but does not fork the JVM.
 */
public abstract class MontiTransExec extends DefaultTask {

  @Input
  public abstract Property<String> getTFGenTool();

  @Classpath
  public abstract ConfigurableFileCollection getClassPath();

  @InputFile
  public abstract RegularFileProperty getInput();

  @OutputDirectory
  public abstract DirectoryProperty getOutputDir();

  @Input@Optional
  public abstract Property<Boolean> getUseCache();

  // We use static by design here to cache the TFGenTools main method
  protected static Map<Integer, Method> tfgenMethodCache = new LinkedHashMap<>();

  public MontiTransExec() {
    setGroup("MontiTrans");
  }

  protected Method loadTFGenMain() throws ClassNotFoundException, NoSuchMethodException, MalformedURLException {
    // Prepare the classpath
    URL[] urls = new URL[getClassPath().getFiles().size()];
    int i = 0;
    for (File f : getClassPath().getFiles())
      urls[i++] = f.toURI().toURL();

    // Construct a new classloader without a delegated-parent and with the configured classpath
    ClassLoader newCL = new URLClassLoader(urls, (ClassLoader) null);
    return newCL.loadClass(getTFGenTool().get()) // Load the TFGenClass
            .getMethod("main", String[].class); // and return the main method
  }

  /**
   * Returns a hash for the name of the TFGenTool and the contents of the files within the classpath.
   * @see java.util.Objects#hash(java.lang.Object...)
   * @return the hash
   */
  private int getHash() throws IOException {
    int result = getTFGenTool().get().hashCode(); // hash of the TFGenTool class
    for (File f : getClassPath()) // hash of the content of the files within the classpath
      result = 31 * result + getHash(f);
    return result;
  }

  /**
   * Returns the hash for the content of a file or all files within a directory.
   * The
   */
  private int getHash(File f) throws IOException {
    if (!f.exists()) {
      return 0;
    } else if (f.isDirectory()) {
      int result = f.hashCode();
      for (File inner : f.listFiles())
        result = 31 * result + getHash(inner);
      return result;
    } else {
      return Files.asByteSource(f).hash(Hashing.goodFastHash(32)).hashCode();
    }
  }


  @TaskAction
  void exec() throws Exception {
    // Get the TFGen method
    Method m;
    if (getUseCache().isPresent() && !getUseCache().get()) {
      m = loadTFGenMain(); // cache disabled using config
    } else {
      // it may be cached depending on the TFGenTool class name and classpath
      m = tfgenMethodCache.computeIfAbsent(getHash(), i -> {
        try {
          return loadTFGenMain();
        } catch (ClassNotFoundException | MalformedURLException | NoSuchMethodException  e) {
          throw new RuntimeException(e);
        }
      });
    }

    // Arguments for the TFGen CLI
    String[] args = {"-i", getInput().get().toString(),
            "-o", getOutputDir().getAsFile().get().toPath().toString()};

    Object[] arg = {args};
    // Finally, invoke the static TFGen CLIs main method
    m.invoke(null, arg);
  }

}
