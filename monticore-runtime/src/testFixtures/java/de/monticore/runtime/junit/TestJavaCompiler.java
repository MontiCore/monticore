// (c) https://github.com/MontiCore/monticore
package de.monticore.runtime.junit;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.System.lineSeparator;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * A test utility class to call the java compiler.
 * <p>
 * The loaded {@code Class<?>} will be available until {@link #close()}.
 */
public class TestJavaCompiler implements AutoCloseable {

  static protected final Path DEFAULT_OUTPUT_PATH =
      Path.of("target", "codegen-test", "class");

  /**
   * Caches the Java compiler for efficiency.
   */
  protected JavaCompiler javac;

  /**
   * The directory to store the .class files in.
   */
  protected Path outputDir;

  /**
   * The ClassLoader to load the generated classes with.
   */
  protected URLClassLoader classLoader;

  public TestJavaCompiler() {
    this(DEFAULT_OUTPUT_PATH);
  }

  public TestJavaCompiler(Path classOutputDir) {
    javac = ToolProvider.getSystemJavaCompiler();
    assertNotNull(javac, "Java Compiler could not be found.");

    outputDir = classOutputDir;
    URL outputDirURL =
        assertDoesNotThrow(() -> classOutputDir.toUri().toURL());
    ClassLoader currentClassLoader = TestJavaCompiler.class.getClassLoader();
    classLoader = new URLClassLoader(
        new URL[] { outputDirURL },
        currentClassLoader
    );
  }

  @Override
  public void close() throws Exception {
    javac = null;
    if (classLoader != null) {
      classLoader.close();
    }
  }

  /**
   * Compiles the Java files and loads the corresponding classes.
   * This will fail the test if the files cannot be compiled.
   * <p>
   * This uses {@link JavaCompiler} to compile.
   * <p>
   * Note: This is a very general method,
   * which does not accommodate for a lot of special test cases.
   * It is expected for this class to get extended in the future if required.
   *
   * @param javaFiles the .java files to compile.
   * @return The compiled classes loaded.
   *     They are in the same order as the .java files.
   */
  public List<Class<?>> compile(List<? extends File> javaFiles) {
    return compile(javaFiles, getDefaultCompilerOptions());
  }

  /**
   * Alternative version of {@link #compile(List)} for single files.
   *
   * @param javaFile the .java file to compile
   * @return The compiled class
   */
  public Class<?> compile(File javaFile) {
    return compile(List.of(javaFile)).getFirst();
  }

  /**
   * Compiles the Java files and loads the corresponding classes.
   * S.a. {@link #compile(List)}
   *
   * @param javaFiles       the .java files to compile.
   * @param compilerOptions the options to pass to the compiler
   * @return The compiled classes loaded.
   *     They are in the same order as the .java files.
   */
  public List<Class<?>> compile(
      List<? extends File> javaFiles,
      List<String> compilerOptions
  ) {
    // collect the compilation units
    DiagnosticCollector<JavaFileObject> diagnosticsCollector =
        new DiagnosticCollector<>();
    StandardJavaFileManager javaFileManager =
        javac.getStandardFileManager(
            diagnosticsCollector, null, StandardCharsets.UTF_8
        );
    Iterable<? extends JavaFileObject> compilationUnits =
        javaFileManager.getJavaFileObjectsFromFiles(javaFiles);

    // compile
    JavaCompiler.CompilationTask task = javac.getTask(
        null,
        javaFileManager,
        diagnosticsCollector,
        compilerOptions,
        null,
        compilationUnits
    );
    boolean success = task.call();
    String diagnostics =
        formatDiagnostics(diagnosticsCollector.getDiagnostics());
    assertTrue(success,
        () -> diagnostics
            + lineSeparator() + "********** Java Files ***********"
            + lineSeparator() + javaFiles.stream()
            .map(this::readFile)
            .collect(Collectors.joining(
                lineSeparator()
                    + "---------------------------"
                    + lineSeparator()
            ))
    );

    // load the classes
    List<Class<?>> compiledClasses = new ArrayList<>(javaFiles.size());
    for (File javaFile : javaFiles) {
      String className = getClassNameOfJavaFile(javaFile);
      Class<?> generatedClass;
      try {
        generatedClass = Class.forName(className, true, classLoader);
      }
      catch (ClassNotFoundException e) {
        fail(e);
        return null;
      }
      compiledClasses.add(generatedClass);
    }
    return compiledClasses;
  }

  /**
   * Returns the default compiler options.
   * <p>
   * The classpath used for compilation uses the current classpath.
   *
   * @return the default compiler options
   */
  public List<String> getDefaultCompilerOptions() {
    assureDirectoryExists(outputDir);
    String classPath = System.getProperty("java.class.path");
    return List.of(
        "-d", outputDir.toString(),
        "-classpath", classPath
    );
  }

  // small helper

  protected String formatDiagnostics(
      List<Diagnostic<? extends JavaFileObject>> diagnostics
  ) {
    return diagnostics.stream()
        .map(this::formatDiagnostic)
        .collect(Collectors.joining(lineSeparator()));
  }

  protected String formatDiagnostic(
      Diagnostic<? extends JavaFileObject> diagnostic
  ) {
    return "Diag<" + diagnostic.getStartPosition()
        + ", " + diagnostic.getEndPosition() + ">: "
        + diagnostic.getMessage(null);
  }

  /**
   * given a .java file, this returns the name of the class
   *
   * @param javaFile the file to get the name from
   * @return the class name
   */
  protected String getClassNameOfJavaFile(File javaFile) {
    String fileName = javaFile.getName();
    assertTrue(fileName.endsWith(".java"));
    String className = fileName
        .substring(0, fileName.length() - ".java".length());
    return className;
  }

  /**
   * Assures that the given directory exists, creating it if necessary.
   *
   * @param dir the dir that has to exist.
   */
  protected void assureDirectoryExists(Path dir) {
    try {
      Files.createDirectories(dir);
    }
    catch (IOException e) {
      fail(e);
    }
  }

  protected String readFile(File file) {
    try {
      return Files.readString(file.toPath(), StandardCharsets.UTF_8);
    }
    catch (IOException e) {
      return fail(e);
    }
  }

}
