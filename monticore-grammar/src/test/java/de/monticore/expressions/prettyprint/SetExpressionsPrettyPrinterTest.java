/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.prettyprint;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.setexpressions.SetExpressionsMill;
import de.monticore.expressions.setexpressions._ast.*;
import de.monticore.expressions.testsetexpressions._parser.TestSetExpressionsParser;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.*;

public class SetExpressionsPrettyPrinterTest {

  private TestSetExpressionsParser parser = new TestSetExpressionsParser();

  private SetExpressionsPrettyPrinter prettyPrinter = new SetExpressionsPrettyPrinter(new IndentPrinter());

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
  public void testUnionExpressionPrefix() throws IOException {
    Optional<ASTUnionExpressionPrefix> result = parser.parse_StringUnionExpressionPrefix("union a");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTUnionExpressionPrefix ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringUnionExpressionPrefix(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testSetOrExpression() throws IOException {
    Optional<ASTSetOrExpression> result = parser.parse_StringSetOrExpression("setor a");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTSetOrExpression ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringSetOrExpression(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testSetAndExpression() throws IOException {
    Optional<ASTSetAndExpression> result = parser.parse_StringSetAndExpression("setand a");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTSetAndExpression ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringSetAndExpression(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testIntersectionExpressionPrefix() throws IOException {
    Optional<ASTIntersectionExpressionPrefix> result = parser.parse_StringIntersectionExpressionPrefix("intersect a");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTIntersectionExpressionPrefix ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringIntersectionExpressionPrefix(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testUnionExpressionInfix() throws IOException {
    Optional<ASTExpression> a = parser.parse_StringExpression("a");
    Optional<ASTExpression> b = parser.parse_StringExpression("b");
    assertFalse(parser.hasErrors());
    assertTrue(a.isPresent());
    assertTrue(b.isPresent());
    ASTUnionExpressionInfix result = SetExpressionsMill.unionExpressionInfixBuilder()
        .setLeft(a.get())
        .setOperator("union")
        .setRight(b.get())
        .build();

    String output = prettyPrinter.prettyprint(result);

    assertEquals("a union b", output);
  }

  @Test
  public void testIntersectionExpressionInfix() throws IOException {
    Optional<ASTExpression> a = parser.parse_StringExpression("a");
    Optional<ASTExpression> b = parser.parse_StringExpression("b");
    assertFalse(parser.hasErrors());
    assertTrue(a.isPresent());
    assertTrue(b.isPresent());
    ASTIntersectionExpressionInfix result = SetExpressionsMill.intersectionExpressionInfixBuilder()
        .setLeft(a.get())
        .setOperator("intersect")
        .setRight(b.get())
        .build();

    String output = prettyPrinter.prettyprint(result);

    assertEquals("a intersect b", output);
  }

  @Test
  public void testIsInExpression() throws IOException {
    Optional<ASTExpression> a = parser.parse_StringExpression("a");
    Optional<ASTExpression> b = parser.parse_StringExpression("b");
    assertFalse(parser.hasErrors());
    assertTrue(a.isPresent());
    assertTrue(b.isPresent());
    ASTIsInExpression result = SetExpressionsMill.isInExpressionBuilder()
        .setElem(a.get())
        .setOperator("isin")
        .setSet(b.get())
        .build();

    String output = prettyPrinter.prettyprint(result);

    assertEquals("a isin b", output);
  }

  @Test
  public void testSetInExpression() throws IOException {
    Optional<ASTExpression> a = parser.parse_StringExpression("a");
    Optional<ASTExpression> b = parser.parse_StringExpression("b");
    assertFalse(parser.hasErrors());
    assertTrue(a.isPresent());
    assertTrue(b.isPresent());
    ASTSetInExpression result = SetExpressionsMill.setInExpressionBuilder()
        .setElem(a.get())
        .setOperator("in")
        .setSet(b.get())
        .build();

    String output = prettyPrinter.prettyprint(result);

    assertEquals("a in b", output);
  }
}
