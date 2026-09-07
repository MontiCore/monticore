// (c) https://github.com/MontiCore/monticore
package de.monticore.interpreter;

import de.monticore.runtime.junit.AbstractMCTest;
import de.monticore.symbols.util.Class2MCTestUtil;
import de.monticore.tests.expressionsandstatements.TestExpressionsAndStatementsTool;
import de.monticore.tests.expressionsandstatements._ast.ASTBehaviorInput;
import de.monticore.values.MCValue;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public abstract class AbstractInterpreterTest extends AbstractMCTest {

  TestExpressionsAndStatementsTool testTool =
      new TestExpressionsAndStatementsTool();

  @BeforeEach
  public void setup() {
    LogStub.initPlusLog();
    TestExpressionsAndStatementsTool.initLanguage();
    Class2MCTestUtil.initializeClass2MC4OOSymbols();
  }

  /**
   * Executes the model and compares the result with the expected value.
   *
   * @param modelStr      the model to execute
   * @param expectedValue the expected value
   */
  protected void checkValue(String modelStr, Object expectedValue) {
    MCValue value = interpret(modelStr);
    checkValue(value, expectedValue);
  }

  /**
   * compares the given interpretation result with the expected value.
   *
   * @param value         the interpretation result
   * @param expectedValue the expected value
   */
  protected void checkValue(MCValue value, Object expectedValue) {
    // handle numbers, as they are represented differently in the interpreter
    try {
      if (expectedValue instanceof Byte) {
        assertEquals(expectedValue, value.asByte());
      }
      else if (expectedValue instanceof Short) {
        assertEquals(expectedValue, value.asShort());
      }
      else if (expectedValue instanceof Character) {
        assertEquals(expectedValue, value.asChar());
      }
      else if (expectedValue instanceof Integer) {
        assertEquals(expectedValue, value.asInt());
      }
      else if (expectedValue instanceof Long) {
        assertEquals(expectedValue, value.asLong());
      }
      else if (expectedValue instanceof Float) {
        assertEquals(expectedValue, value.asFloat());
      }
      else if (expectedValue instanceof Double) {
        assertEquals(expectedValue, value.asDouble());
      }
      else {
        assertEquals(expectedValue, value.asNativeObject());
      }
    }
    catch (RuntimeException e) {
      System.out.println(
          "Exception while comparing." + System.lineSeparator()
              + "Type: " + value.printType() + System.lineSeparator()
              + "Value: " + value.printValue() + System.lineSeparator()
              + "Expected: " + expectedValue
      );
      fail(e);
    }
  }

  protected MCValue interpret(String modelStr) {
    return interpret(modelStr, false);
  }

  protected MCValue interpret(String modelStr, boolean withLog) {
    ASTBehaviorInput ast = testTool.getASTWithSymbolTable(modelStr);
    return testTool.interpret(ast, withLog);
  }

  // helper

  protected <T> T interpretAndCast(String modelStr) {
    MCValue value = interpret(modelStr);
    Object valueObj = value.asNativeObject();
    @SuppressWarnings("unchecked")
    T casted = (T) valueObj;
    return casted;
  }

}
