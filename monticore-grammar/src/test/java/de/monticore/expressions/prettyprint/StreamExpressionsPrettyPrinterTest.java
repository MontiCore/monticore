/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.prettyprint;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.streamexpressions._ast.*;
import de.monticore.expressions.streamexpressions._prettyprint.StreamExpressionsFullPrettyPrinter;
import de.monticore.expressions.teststreamexpressions.TestStreamExpressionsMill;
import de.monticore.expressions.teststreamexpressions._parser.TestStreamExpressionsParser;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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

  @Test
  public void testEmptyStream() throws IOException {
    Optional<ASTEmptyStreamExpression> result = parser.parse_StringEmptyStreamExpression("<>");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTEmptyStreamExpression ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringEmptyStreamExpression(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());

    Assertions.assertTrue(ast.deepEquals(result.get()));

    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testAppendStream() throws IOException {
    Optional<ASTExpression> result = parser.parse_StringExpression("stream1 : stream2");

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

  @Test
  public void testConcatStream() throws IOException {
    Optional<ASTExpression> result = parser.parse_StringExpression("stream1 ^^ stream2");

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

  @Test
  public void testLengthStream() throws IOException {
    Optional<ASTLengthStreamExpression> result = parser.parse_StringLengthStreamExpression("#stream");

    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTLengthStreamExpression ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringLengthStreamExpression(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());

    Assertions.assertTrue(ast.deepEquals(result.get()));

    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

}
