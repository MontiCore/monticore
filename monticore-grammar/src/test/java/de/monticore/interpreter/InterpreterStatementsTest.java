// (c) https://github.com/MontiCore/monticore
package de.monticore.interpreter;

import de.monticore.values.MCValue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.*;

public class InterpreterStatementsTest extends AbstractInterpreterTest {

  @ParameterizedTest(name = "[{index}] {0}")
  @MethodSource("de.monticore.tests.expressionsandstatements.StatementsTestModels#getStatementCases")
  void testStatements(String modelStr, Object expectedValue) {
    checkValue(modelStr, expectedValue);
  }

  @Test
  void testAssertStatementFailure() {
    MCValue MCValue = interpret("assert false;");
    assertTrue(MCValue.isError());
    assertInstanceOf(AssertionError.class, MCValue.asNativeObject());
  }

  @Test
  void testAssertStatementFailureWithMessage() {
    MCValue MCValue = interpret("assert false : \"message\";");
    assertTrue(MCValue.isError());
    AssertionError error =
        assertInstanceOf(AssertionError.class, MCValue.asNativeObject());
    assertEquals("message", error.getMessage());
  }

}
