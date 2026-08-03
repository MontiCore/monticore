// (c) https://github.com/MontiCore/monticore
package de.monticore.tests.expressionsandstatements.behavior;

import de.monticore.codegen.util.Node2Name;
import de.monticore.symbols.util.Class2MCTestUtil;
import de.monticore.tests.expressionsandstatements.TestExpressionsAndStatementsTool;
import de.monticore.tests.expressionsandstatements._ast.ASTBehaviorInput;
import de.monticore.values.MCValue;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * A collection of complex behavior tests;
 * the Interpreter and JavaCode Generator are compared against each other.
 */
public class BehaviorTest {

  protected TestExpressionsAndStatementsTool testTool =
      new TestExpressionsAndStatementsTool();

  protected static Path resourceDir = resolveResourceDir();

  /**
   * contains additional checks to run on the result
   */
  protected static final Map<String, Consumer<Object>> additionalChecks = Map.of(
      "Fannkuch.bhv",
      result -> assertEquals("228\nPfannkuchen(7) = 16", result),
      "Mandelbrot.bhv",
      result -> {
        byte[] reference = readFileBytes(
            resourceDir.resolve("mandelbrot-200-reference.pbm")
        );
        assertEqualsWithArraySupport(reference, result);
      }
  );

  @BeforeEach
  public void setup() {
    LogStub.initPlusLog();
    TestExpressionsAndStatementsTool.initLanguage();
    Class2MCTestUtil.initializeClass2MC4OOSymbols();
  }

  @ParameterizedTest
  @MethodSource
  public void compareInterpreterWithJavaGenerator(
      Path modelPath,
      Consumer<Object> additionalCheck
  ) {
    String modelStr = readFile(modelPath);
    ASTBehaviorInput ast = testTool.getASTWithSymbolTable(modelStr);

    TestExpressionsAndStatementsTool.JavaGenResult genRunResult =
        testTool.generateJavaAndRun(ast, getClassName(ast));
    Object javaGenResult = genRunResult.result();

    MCValue interpreterResult = testTool.interpret(ast);
    Object interpreterResultNative = interpreterResult.asNativeObject();

    assertEqualsWithArraySupport(javaGenResult, interpreterResultNative);
    additionalCheck.accept(javaGenResult);
  }

  static protected Stream<Arguments> compareInterpreterWithJavaGenerator() {
    List<Arguments> arguments = new ArrayList<>();
    for (File resource : resourceDir.toFile().listFiles()) {
      if (!resource.isFile()) {
        continue;
      }
      if (!resource.getName().endsWith(".bhv")) {
        continue;
      }
      Consumer<Object> additionalCheck = additionalChecks
          .getOrDefault(resource.getName(), result -> {
          });
      arguments.add(Arguments.of(resource.toPath(), additionalCheck));
    }
    return arguments.stream();
  }

  /**
   * JUnit is missing an assertEquals that supports equality checks on arrays
   * _without_ knowing the type of the array.
   * <p>
   * This issue is circumvented by converting both arrays to {@code Object[]},
   * boxing primitives in the process.
   *
   * @param e expected value
   * @param a actual value
   */
  protected static void assertEqualsWithArraySupport(Object e, Object a) {
    if (e.getClass().isArray() && a.getClass().isArray()) {
      List<Object> eArray = new ArrayList<>(Array.getLength(e));
      for (int i = 0; i < Array.getLength(e); i++) {
        eArray.add(Array.get(e, i));
      }
      List<Object> aArray = new ArrayList<>(Array.getLength(a));
      for (int i = 0; i < Array.getLength(a); i++) {
        aArray.add(Array.get(a, i));
      }
      assertArrayEquals(eArray.toArray(), aArray.toArray());
    }
    else {
      assertEquals(e, a);
    }
  }

  static protected String readFile(Path filePath) {
    try {
      return Files.readString(filePath, StandardCharsets.UTF_8);
    }
    catch (IOException e) {
      return fail(e);
    }
  }

  static protected byte[] readFileBytes(Path filePath) {
    try {
      return Files.readAllBytes(filePath);
    }
    catch (IOException e) {
      return fail(e);
    }
  }

  protected String getClassName(ASTBehaviorInput ast) {
    return this.getClass().getSimpleName() + Node2Name.getName(ast);
  }

  protected static Path resolveResourceDir() {
    URL modelUrl = BehaviorTest.class.getClassLoader().getResource(
        "de/monticore/tests/expressionsandstatements/Mandelbrot.bhv"
    );
    assertNotNull(modelUrl, "could not find test resources");
    return assertDoesNotThrow(() -> Paths.get(modelUrl.toURI()).getParent());
  }

}
