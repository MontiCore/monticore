/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.prettyprint;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.streamexpressions._prettyprint.StreamExpressionsFullPrettyPrinter;
import de.monticore.expressions.teststreamexpressions.TestStreamExpressionsMill;
import de.monticore.expressions.teststreamexpressions._parser.TestStreamExpressionsParser;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.Optional;

public class StreamExpressionsPrettyPrinterTest {

  protected TestStreamExpressionsParser parser;
  protected StreamExpressionsFullPrettyPrinter prettyPrinter;

  @BeforeEach
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    TestStreamExpressionsMill.reset();
    TestStreamExpressionsMill.init();
    parser = TestStreamExpressionsMill.parser();
    prettyPrinter =
            new StreamExpressionsFullPrettyPrinter(new IndentPrinter());
    prettyPrinter.getPrinter().clearBuffer();
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "<>",
      "<A>",
      "<A, B, C>",
      "event<A>",
      "A:<B,C>",
      "stream1 : stream2",
      "stream1 ^^ stream2",
      "#stream"
  })
  public void testPrettyPrint(String input) throws IOException {
    Optional<ASTExpression> result = parser.parse_StringExpression(input);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTExpression ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringExpression(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());

    Assertions.assertTrue(ast.deepEquals(result.get()));

    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
}
