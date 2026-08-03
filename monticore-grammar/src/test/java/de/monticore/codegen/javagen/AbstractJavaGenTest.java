// (c) https://github.com/MontiCore/monticore
package de.monticore.codegen.javagen;

import de.monticore.ast.ASTNode;
import de.monticore.codegen.CodeGenerator;
import de.monticore.codegen.util.Node2Name;
import de.monticore.runtime.junit.AbstractMCTest;
import de.monticore.symbols.util.Class2MCTestUtil;
import de.monticore.tests.expressionsandstatements.ExpressionsAndStatementsMill;
import de.monticore.tests.expressionsandstatements.ExpressionsAndStatementsUtil;
import de.monticore.tests.expressionsandstatements._ast.ASTBehaviorInput;
import de.monticore.tests.expressionsandstatements.codegen.javagen.ExpressionsAndStatementsJavaGenerator;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import static de.monticore.codegen.javagen.SymTypeExpression2JavaConverter.getJavaTypePrint;
import static de.monticore.runtime.junit.MCAssertions.assertNoFindings;
import static de.monticore.types3.SymTypeRelations.normalize;
import static de.monticore.types3.TypeCheck3.typeOf;
import static java.lang.System.lineSeparator;
import static org.junit.jupiter.api.Assertions.*;

public abstract class AbstractJavaGenTest extends AbstractMCTest {

  // enable for experimenting:
  // it will print the generated code before evaluation
  protected static final boolean PRINT_GENERATED_CODE = true;

  protected static final String GENERATED_METHOD_NAME =
      "executeSystemUnderTest";

  protected static final Path GENERATED_SOURCE_DIR =
      Path.of("target", "codegen-test", "java");

  protected static final Path GENERATED_CLASS_DIR =
      Path.of("target", "codegen-test", "class");

  CodeGenerator generator;

  protected static JavaCompiler javac;

  @BeforeAll
  protected static void setup() throws IOException {
    javac = ToolProvider.getSystemJavaCompiler();
    if (javac == null) {
      fail("Java Compiler could not be found.");
    }

    // assure that the paths exist for .java and .class artifacts
    Files.createDirectories(GENERATED_SOURCE_DIR);
    Files.createDirectories(GENERATED_CLASS_DIR);
  }

  @AfterAll
  protected static void cleanUp() {
    javac = null;
  }

  @BeforeEach
  void beforeEach() {
    LogStub.initPlusLog();
    ExpressionsAndStatementsUtil.init();
    Class2MCTestUtil.initializeClass2MC4OOSymbols();
    generator = new ExpressionsAndStatementsJavaGenerator();
  }

  /**
   * Executes the model and compares the result with the expected value.
   *
   * @param behaviorModelStr the model to execute
   * @param expectedValue    the expected value
   */
  protected void checkValue(String behaviorModelStr, Object expectedValue) {
    Path javaArtifact = generateJavaArtifact(behaviorModelStr);
    Callable<Object> invoker = compileJavaArtifactAndGetCallable(javaArtifact);
    Object result;
    try {
      result = invoker.call();
    }
    catch (Exception e) {
      fail(e);
      return;
    }

    // compare
    assertEquals(expectedValue, result,
        "The resulting value is not the same as the expected value."
            + lineSeparator() + " File: " + javaArtifact.toAbsolutePath()
            + lineSeparator() + "********** Generated **********"
            + lineSeparator() + readFile(javaArtifact.toFile())
    );
  }

  /**
   * compiles the model and returns a callable to invoke the generated code.
   *
   * @param behaviorModelStr the model to compile
   * @return a callable to invoke the generated code
   */
  protected Callable<Object> compileToCallable(String behaviorModelStr) {
    Path javaArtifact = generateJavaArtifact(behaviorModelStr);
    return compileJavaArtifactAndGetCallable(javaArtifact);
  }

  protected Path generateJavaArtifact(String behaviorModelStr) {
    // setup
    ASTBehaviorInput ast = ExpressionsAndStatementsUtil
        .getPreparedAST(behaviorModelStr);
    return createJavaSource(ast);
  }

  protected Callable<Object> compileJavaArtifactAndGetCallable(Path javaArtifact) {
    String fileName = javaArtifact.getFileName().toString();
    String className =
        fileName.substring(0, fileName.length() - ".java".length());
    // compile and get invocation object
    compile(List.of(javaArtifact.toFile()));
    return () -> invokeGeneratedMethod(className);
  }

  /**
   * creates a .java artifact
   *
   * @param ast the behavior that will be turned into a .java artifact
   * @return the Path of the created .java artifact
   */
  protected Path createJavaSource(ASTBehaviorInput ast) {
    String className = Node2Name.getName(ast);
    String javaStatementsStr = generateCode(ast);
    String javaReturnType = ast.isPresentExpression() ?
        getJavaTypePrint(normalize(typeOf(ast.getExpression()))) :
        "void";
    assertNoFindings();
    // assure that we don't have any comments
    String prettyPrintedModel =
        ExpressionsAndStatementsMill.prettyPrint(ast, false);

    String javaMethodStr = "public static " + javaReturnType + " "
        + GENERATED_METHOD_NAME + "() {" + lineSeparator()
        + javaStatementsStr
        + "}";
    String javaClassStr = "public class " + className + " {"
        + lineSeparator() + javaMethodStr + lineSeparator()
        + "}" + lineSeparator();
    // prints the generated code,
    // can help during development
    if (PRINT_GENERATED_CODE) {
      System.out.println("***** Generated Code *****");
      System.out.println(javaClassStr);
      System.out.println("**************************");
    }
    // add a comment of the original input
    // to better identify the generated artifacts
    String artifactFullStr =
        javaClassStr + lineSeparator()
            + "// ********** Original Model **********" + lineSeparator()
            + "/*" + lineSeparator()
            + prettyPrintedModel + lineSeparator()
            + "*/" + lineSeparator();

    // store as a file
    Path sourceFile = GENERATED_SOURCE_DIR.resolve(className + ".java");
    assertDoesNotThrow(() ->
        Files.writeString(sourceFile, artifactFullStr, StandardCharsets.UTF_8)
    );

    return sourceFile;
  }

  /**
   * produces Java code given a model.
   * HookPoint
   *
   * @param node the node to generate code from
   * @return the generated Java code
   */
  protected String generateCode(ASTNode node) {
    return generator.generateCode(node);
  }

  /**
   * compiles the provided Java artifacts
   *
   * @param files the Java artifacts to compile
   */
  protected void compile(
      List<File> files
  ) {
    DiagnosticCollector<JavaFileObject> diagnosticsCollector =
        new DiagnosticCollector<>();
    StandardJavaFileManager javaFileManager = javac.getStandardFileManager(
        diagnosticsCollector, null, StandardCharsets.UTF_8
    );
    Iterable<? extends JavaFileObject> compilationUnits =
        javaFileManager.getJavaFileObjectsFromFiles(files);

    String classPath = System.getProperty("java.class.path");
    List<String> options = List.of(
        "-d", GENERATED_CLASS_DIR.toString(),
        "-classpath", classPath
    );
    JavaCompiler.CompilationTask task = javac.getTask(
        null,
        javaFileManager,
        diagnosticsCollector,
        options,
        null,
        compilationUnits
    );

    boolean success = task.call();
    assertTrue(success,
        () -> formatDiagnostics(diagnosticsCollector.getDiagnostics())
            + lineSeparator() + "********** Models ***********"
            + lineSeparator() + files.stream()
            .map(this::readFile)
            .collect(Collectors.joining(
                lineSeparator()
                    + "---------------------------"
                    + lineSeparator()
            ))
    );
  }

  /**
   * executes the test method and returns the result
   *
   * @param className the class containing the method
   * @return the result of the method call
   */
  protected Object invokeGeneratedMethod(String className) throws Exception {
    URL outputDirURL =
        assertDoesNotThrow(() -> GENERATED_CLASS_DIR.toUri().toURL());
    try (URLClassLoader classLoader = new URLClassLoader(
        new URL[] { outputDirURL }, getClass().getClassLoader()
    )) {
      Class<?> compiledClass =
          Class.forName(className, true, classLoader);
      Method method = compiledClass.getDeclaredMethod(GENERATED_METHOD_NAME);
      return method.invoke(null);
    }
    // unpack InvocationTargetException if possible
    catch (InvocationTargetException ite) {
      if (ite.getCause() instanceof Exception e) {
        throw e;
      }
      else if (ite.getCause() instanceof Error err) {
        throw err;
      }
      else {
        throw ite;
      }
    }
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

  protected String readFile(File file) {
    try {
      return Files.readString(file.toPath(), StandardCharsets.UTF_8);
    }
    catch (IOException e) {
      return fail(e);
    }
  }
}
