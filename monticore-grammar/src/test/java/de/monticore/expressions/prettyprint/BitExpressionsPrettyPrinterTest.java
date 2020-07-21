/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.prettyprint;

import de.monticore.expressions.bitexpressions.BitExpressionsMill;
import de.monticore.expressions.bitexpressions._ast.*;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.testbitexpressions._parser.TestBitExpressionsParser;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.*;

public class BitExpressionsPrettyPrinterTest {

  private TestBitExpressionsParser parser = new TestBitExpressionsParser();

  private BitExpressionsPrettyPrinter prettyPrinter= new BitExpressionsPrettyPrinter(new IndentPrinter());

  @BeforeClass
  public static void setUp() {
    LogStub.init();
    Log.enableFailQuick(false);
  }

  @Before
  public void init() {
    prettyPrinter.getPrinter().clearBuffer();
  }

  @Test
  public void testLeftShiftExpression() throws IOException {
    Optional<ASTExpression> a = parser.parse_StringExpression("a");
    Optional<ASTExpression> b = parser.parse_StringExpression("b");
    assertFalse(parser.hasErrors());
    assertTrue(a.isPresent());
    assertTrue(b.isPresent());
    ASTLeftShiftExpression result = BitExpressionsMill.leftShiftExpressionBuilder()
        .setLeft(a.get())
        .setShiftOp("<<")
        .setRight(b.get())
        .build();

    String output = prettyPrinter.prettyprint(result);

    assertEquals("a<<b", output);
  }

  @Test
  public void testRightShiftExpression() throws IOException {
    Optional<ASTExpression> a = parser.parse_StringExpression("a");
    Optional<ASTExpression> b = parser.parse_StringExpression("b");
    assertFalse(parser.hasErrors());
    assertTrue(a.isPresent());
    assertTrue(b.isPresent());
    ASTRightShiftExpression result = BitExpressionsMill.rightShiftExpressionBuilder()
        .setLeft(a.get())
        .setShiftOp(">>")
        .setRight(b.get())
        .build();

    String output = prettyPrinter.prettyprint(result);

    assertEquals("a>>b", output);
  }

  @Test
  public void testLogicalRightShiftExpression() throws IOException {
    Optional<ASTExpression> a = parser.parse_StringExpression("a");
    Optional<ASTExpression> b = parser.parse_StringExpression("b");
    assertFalse(parser.hasErrors());
    assertTrue(a.isPresent());
    assertTrue(b.isPresent());
    ASTLogicalRightShiftExpression result = BitExpressionsMill.logicalRightShiftExpressionBuilder()
        .setLeft(a.get())
        .setShiftOp(">>>")
        .setRight(b.get())
        .build();

    String output = prettyPrinter.prettyprint(result);

    assertEquals("a>>>b", output);
  }

  @Test
  public void testBinaryOrOpExpression() throws IOException {
    Optional<ASTExpression> a = parser.parse_StringExpression("a");
    Optional<ASTExpression> b = parser.parse_StringExpression("b");
    assertFalse(parser.hasErrors());
    assertTrue(a.isPresent());
    assertTrue(b.isPresent());
    ASTBinaryOrOpExpression result = BitExpressionsMill.binaryOrOpExpressionBuilder()
        .setLeft(a.get())
        .setRight(b.get())
        .setOperator("|")
        .build();

    String output = prettyPrinter.prettyprint(result);

    assertEquals("a|b", output);
  }

  @Test
  public void testBinaryXorExpression() throws IOException {
    Optional<ASTExpression> a = parser.parse_StringExpression("a");
    Optional<ASTExpression> b = parser.parse_StringExpression("b");
    assertFalse(parser.hasErrors());
    assertTrue(a.isPresent());
    assertTrue(b.isPresent());
    ASTBinaryXorExpression result = BitExpressionsMill.binaryXorExpressionBuilder()
        .setLeft(a.get())
        .setRight(b.get())
        .setOperator("^")
        .build();

    String output = prettyPrinter.prettyprint(result);

    assertEquals("a^b", output);
  }

  @Test
  public void testBinaryAndExpression() throws IOException {
    Optional<ASTExpression> a = parser.parse_StringExpression("a");
    Optional<ASTExpression> b = parser.parse_StringExpression("b");
    assertFalse(parser.hasErrors());
    assertTrue(a.isPresent());
    assertTrue(b.isPresent());
    ASTBinaryAndExpression result = BitExpressionsMill.binaryAndExpressionBuilder()
        .setLeft(a.get())
        .setOperator("&")
        .setRight(b.get())
        .build();

    String output = prettyPrinter.prettyprint(result);

    assertEquals("a&b", output);
  }
}
