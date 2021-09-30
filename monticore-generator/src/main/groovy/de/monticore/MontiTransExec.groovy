/* (c) https://github.com/MontiCore/monticore */
package de.monticore

import com.google.common.hash.Hashing
import com.google.common.io.Files
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*

import java.lang.reflect.Method

/**
 * This task enables the calling of MontiTrans TFGenCLIs
 * in a subsequent matter, reducing the class loading by reusing a classloader,
 * while still enabling gradle to control individual files.
 *
 * Current JVM implementations are slow at loading MontiCore Mill classes,
 * for example loading the CD4CodeTRMill requires ~30seconds on a decent computer.
 *
 * It behaves similar to the JavaExec task, but does not fork the JVM.
 */
abstract class MontiTransExec extends DefaultTask {

  @Input
  abstract Property<String> getTFGenCLI();

  @Classpath
  abstract ConfigurableFileCollection getClassPath();

  @InputFile
  abstract RegularFileProperty getInput();

  @OutputDirectory
  abstract DirectoryProperty getOutputDir();

  @Input@Optional
  abstract Property<Boolean> getUseCache();

  // We use static by design here to cache the TFGenCLIs main method
  protected static Map<Integer, Method> tfgenMethodCache = new HashMap<>()

  MontiTransExec() {
    group = 'MontiTrans'
  }

  protected Method loadTFGenMain() {
    // Prepare the classpath
    URL[] urls = new URL[getClassPath().getFiles().size()]
    int i = 0
    for (File f : getClassPath().getFiles())
      urls[i++] = f.toURI().toURL()

    // Construct a new classloader without a delegated-parent and with the configured classpath
    ClassLoader newCL = new URLClassLoader(urls, (ClassLoader) null)
    return newCL.loadClass(getTFGenCLI().get()) // Load the TFGenClass
            .getMethod("main", String[].class) // and return the main method
  }

  /**
   * Returns a hash for the name of the TFGenCLI and the contents of the files within the classpath.
   * @see Objects#hash(java.lang.Object...)
   * @return the hash
   */
  private int getHash() {
    int result = getTFGenCLI().get().hashCode() // hash of the TRGenCLI class
    for (File f : getClassPath()) // hash of the content of the files within the classpath
      result = 31 * result + getHash(f)
    return result
  }

  /**
   * Returns the hash for the content of a file or all files within a directory.
   * The
   */
  private int getHash(File f) {
    if (f.isDirectory()) {
      int result = f.hashCode()
      for (File inner : f.listFiles())
        result = 31 * result + getHash(inner)
      return result
    } else {
      return Files.asByteSource(f).hash(Hashing.goodFastHash(32)).hashCode()
    }
  }


  @TaskAction
  void exec() throws Exception {
    // Get the TFGen method
    def m
    if (getUseCache().isPresent() && !getUseCache().get()) {
      m = loadTFGenMain() // cache disabled using config
    } else {
      // it may be cached depending on the TFGenCLI class name and classpath
      m = tfgenMethodCache.computeIfAbsent(getHash(), { loadTFGenMain() })
    }

    // Arguments for the TFGen CLI
    String[] args = ["-i", getInput().get().toString(),
            "-o", getOutputDir().getAsFile().get().toPath().toString()]

    // Finally, invoke the static TFGen CLIs main method
    m.invoke(null, (Object[]) [args])
  }

}
