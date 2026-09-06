// (c) https://github.com/MontiCore/monticore
package de.monticore.codegen.javagen;

import de.monticore.codegen.util.Node2Name;
import de.monticore.runtime.junit.AbstractMCTest;
import de.monticore.symbols.util.Class2MCTestUtil;
import de.monticore.tests.expressionsandstatements.TestExpressionsAndStatementsTool;
import de.monticore.tests.expressionsandstatements._ast.ASTBehaviorInput;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeEach;

import java.nio.file.Path;
import java.util.concurrent.Callable;

import static java.lang.System.lineSeparator;
import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class AbstractJavaGenTest extends AbstractMCTest {

  protected TestExpressionsAndStatementsTool testTool =
      new TestExpressionsAndStatementsTool();

  @BeforeEach
  void beforeEach() {
    LogStub.initPlusLog();
    TestExpressionsAndStatementsTool.initLanguage();
    Class2MCTestUtil.initializeClass2MC4OOSymbols();
  }

  /**
   * Executes the model and compares the result with the expected value.
   *
   * @param behaviorModelStr the model to execute
   * @param expectedValue    the expected value
   */
  protected void checkValue(String behaviorModelStr, Object expectedValue) {
    // setup
    ASTBehaviorInput ast = testTool.getASTWithSymbolTable(behaviorModelStr);

    // compile and invoke
    TestExpressionsAndStatementsTool.JavaGenResult genResult =
        testTool.generateJavaAndRun(ast, getClassName(ast));

    // compare
    assertEquals(expectedValue, genResult.result(),
        "The resulting value is not the same as the expected value."
            + lineSeparator() + " File: " + genResult.sourceFile().toAbsolutePath()
            + lineSeparator() + "********** Generated **********"
            + lineSeparator() + genResult.getSourceCode()
    );
  }

  protected Callable<Object> compileToCallable(String behaviorModelStr) {
    ASTBehaviorInput ast = testTool.getASTWithSymbolTable(behaviorModelStr);
    Path javaArtifact = testTool.createJavaSource(ast, getClassName(ast));
    Class<?> generatedClass = testTool.compile(javaArtifact);
    return testTool.getGeneratedMethodInvoker(generatedClass);
  }

  // small helper

  protected String getClassName(ASTBehaviorInput ast) {
    return this.getClass().getSimpleName() + "_" + Node2Name.getName(ast);
  }

}
