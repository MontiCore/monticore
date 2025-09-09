/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.prettyprint;

import de.monticore.expressions.bitexpressions._prettyprint.BitExpressionsFullPrettyPrinter;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.testbitexpressions.TestBitExpressionsMill;
import de.monticore.expressions.testbitexpressions._parser.TestBitExpressionsParser;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

public class BitExpressionsPrettyPrinterTest {
  protected TestBitExpressionsParser parser;
  protected BitExpressionsFullPrettyPrinter prettyPrinter;
  
  @BeforeEach
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    TestBitExpressionsMill.reset();
    TestBitExpressionsMill.init();
    parser = new TestBitExpressionsParser();
    prettyPrinter = new BitExpressionsFullPrettyPrinter(new IndentPrinter());
    prettyPrinter.getPrinter().clearBuffer();
  }

  @Test
  public void testLeftShiftExpression() throws IOException {
    Optional<ASTExpression> result = parser.parse_StringExpression("a<<b");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
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
  public void testRightShiftExpression() throws IOException {
    Optional<ASTExpression> result = parser.parse_StringExpression("a>>b");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
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
  public void testLogicalRightShiftExpression() throws IOException {
    Optional<ASTExpression> result = parser.parse_StringExpression("a>>>b");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
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
  public void testBinaryOrOpExpression() throws IOException {
    Optional<ASTExpression> result = parser.parse_StringExpression("a|b");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
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
  public void testBinaryXorExpression() throws IOException {
    Optional<ASTExpression> result = parser.parse_StringExpression("a^b");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
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
  public void testBinaryAndExpression() throws IOException {
    Optional<ASTExpression> result = parser.parse_StringExpression("a&b");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
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
