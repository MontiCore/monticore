/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.prettyprint;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.teststreamexpressions.TestStreamExpressionsMill;
import de.monticore.expressions.teststreamexpressions._parser.TestStreamExpressionsParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.Optional;

public class StreamExpressionsPrettyPrinterTest {

  protected TestStreamExpressionsParser parser;

  @BeforeEach
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    TestStreamExpressionsMill.reset();
    TestStreamExpressionsMill.init();
    parser = TestStreamExpressionsMill.parser();
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "<>",
      "<A>",
      "<A, B, C>",
      "<;;1>",
      "<1;;>",
      "<;1;>",
      "<;1,2;3>",
      "Event<A>",
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

    String output = TestStreamExpressionsMill.prettyPrint(ast, true);

    result = parser.parse_StringExpression(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());

    Assertions.assertTrue(ast.deepEquals(result.get()));

    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
}
