package de.monticore.expressions.streamexpressions.parser;

import de.monticore.expressions.AbstractExpressionInterpreterTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static de.monticore.interpreter.MIValueFactory.createValue;

public class StreamExpressionsInterpreterTest extends AbstractExpressionInterpreterTest {
  
  @Test
  public void testSimpleLambda() throws IOException {
    testValidExpression("(() -> 1)()", createValue(1));
  }
  
}
