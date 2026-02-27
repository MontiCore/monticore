package de.monticore.expressions.lambdaexpressions;

import de.monticore.expressions.AbstractExpressionInterpreterTest;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import org.junit.jupiter.api.Test;

import static de.monticore.interpreter.MIValueFactory.createValue;

public class LambdaExpressionsInterpreterTest extends AbstractExpressionInterpreterTest {

  @Test
  public void testSimpleLambda() {
    // testValidExpression("(() -> \"a\"+1)()", createValue("a1"));
    // #0 : 0x57037 Plus operation with result of type R"(a)(.*)" is not supported.
    // This default method is called and the we get an error
    // /**
    //   * Am I primitive? (such as "int")
    //   * (default: no)
    //   */
    //  public boolean isPrimitive() {
    //    return false;
    //  }

    // testValidExpression("(() -> a+1)()", createValue("1"));
    // #0 : StringReader:<1,7> - StringReader:<1,8>: 0xFD118 could not find symbol for expression "a"
    // #1 : Invalid Model: (() -> a+1)()

    //testValidExpression("(() -> \"a\"+1)()", createValue("a1"));
    testValidExpression("(() -> 1)()", createValue(1));
    testValidExpression("(() -> () -> 2)()()", createValue(2));
    testValidExpression("((long a) -> a + 1)(41L)", createValue(42L));
    
    testValidExpression("((long a) -> (byte b) -> a + b)(41L)((byte)28)", createValue(69L));
    testValidExpression("((long a, byte b) -> a + b)(41L,(byte)28)", createValue(69L));
    
    testValidExpression("(() -> () -> (int a) -> () -> () -> a)()()(42)()()", createValue(42));
    
    testValidExpression("((byte b) -> (char c) -> b + c)((byte)25)('a') == 'z'", createValue(true));
  }

}

