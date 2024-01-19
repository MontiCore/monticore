/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.exptojava;

import de.monticore.expressions.commonexpressions.CommonExpressionsMill;
import de.monticore.expressions.commonexpressions._ast.*;
import de.monticore.expressions.commonexpressions._prettyprint.CommonExpressionsFullPrettyPrinter;
import de.monticore.expressions.expressionsbasis._ast.ASTArguments;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.javaclassexpressions.JavaClassExpressionsMill;
import de.monticore.expressions.testcommonexpressions.TestCommonExpressionsMill;
import de.monticore.expressions.testcommonexpressions._parser.TestCommonExpressionsParser;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class CommonExpressionsJavaPrinterTest {
  
  protected TestCommonExpressionsParser parser;
  protected CommonExpressionsFullPrettyPrinter javaPrinter;

  CommonExpressionsFullPrettyPrinter prepareJavaPrinter(){
    CommonExpressionsFullPrettyPrinter commonExpressionsFullPrettyPrinter = new CommonExpressionsFullPrettyPrinter(new IndentPrinter());
    CommonExpressionsJavaPrinter.applyJavaPrinter(commonExpressionsFullPrettyPrinter.getTraverser(), commonExpressionsFullPrettyPrinter.getPrinter(), true);
    return commonExpressionsFullPrettyPrinter;
  }

  @Before
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    TestCommonExpressionsMill.reset();
    TestCommonExpressionsMill.init();
    parser = new TestCommonExpressionsParser();
    javaPrinter = prepareJavaPrinter();
    IndentPrinter indentPrinter = new IndentPrinter();
    javaPrinter.getPrinter().clearBuffer();
  }
  
  @Test
  public void testMinusPrefixExpression() throws IOException {
    Optional<ASTMinusPrefixExpression> result = parser.parse_StringMinusPrefixExpression("-a");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTMinusPrefixExpression ast = result.get();
    
    String output = javaPrinter.prettyprint(ast);
    
    result = parser.parse_StringMinusPrefixExpression(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(ast.deepEquals(result.get()));
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testPlusPrefixExpression() throws IOException {
    Optional<ASTPlusPrefixExpression> result = parser.parse_StringPlusPrefixExpression("+a");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTPlusPrefixExpression ast = result.get();
    
    String output = javaPrinter.prettyprint(ast);
    
    result = parser.parse_StringPlusPrefixExpression(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(ast.deepEquals(result.get()));
  
    assertTrue(Log.getFindings().isEmpty());
  }
  @Test
  public void testBooleanNotExpression() throws IOException {
    Optional<ASTBooleanNotExpression> result = parser.parse_StringBooleanNotExpression("~a");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTBooleanNotExpression ast = result.get();
    
    String output = javaPrinter.prettyprint(ast);
    
    result = parser.parse_StringBooleanNotExpression(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(ast.deepEquals(result.get()));
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testLogicalNotExpression() throws IOException {
    Optional<ASTLogicalNotExpression> result = parser.parse_StringLogicalNotExpression("!a");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTLogicalNotExpression ast = result.get();
    
    String output = javaPrinter.prettyprint(ast);
    
    result = parser.parse_StringLogicalNotExpression(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(ast.deepEquals(result.get()));
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
  
  @Test
  public void testBracketExpression() throws IOException {
    Optional<ASTBracketExpression> result = parser.parse_StringBracketExpression("(a == b)");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTBracketExpression ast = result.get();
    
    String output = javaPrinter.prettyprint(ast);
    
    result = parser.parse_StringBracketExpression(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(ast.deepEquals(result.get()));
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
  
  @Test
  public void testArguments() throws IOException {
    Optional<ASTArguments> result = parser.parse_StringArguments("(a , b , c)");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTArguments ast = result.get();
    
    String output = javaPrinter.prettyprint(ast);
    
    result = parser.parse_StringArguments(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(ast.deepEquals(result.get()));
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testCallExpression() throws IOException {
    Optional<ASTExpression> a = parser.parse_StringExpression("a");
    Optional<ASTArguments> arguments = parser.parse_StringArguments("(b, c)");
    assertFalse(parser.hasErrors());
    assertTrue(a.isPresent());
    assertTrue(arguments.isPresent());
    ASTCallExpression result = CommonExpressionsMill.callExpressionBuilder()
      .setExpression(a.get())
      .setArguments(arguments.get())
      .build();
    
    String output = javaPrinter.prettyprint(result);
    
    assertEquals("a(b,c)", output);
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testFieldAccessExpression() throws IOException {
    Optional<ASTExpression> a = parser.parse_StringExpression("a");
    assertFalse(parser.hasErrors());
    assertTrue(a.isPresent());
    ASTFieldAccessExpression result = CommonExpressionsMill.fieldAccessExpressionBuilder()
      .setExpression(a.get())
      .setName("foo")
      .build();
    
    String output = javaPrinter.prettyprint(result);
    
    assertEquals("a.getFoo()", output);
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testMultExpression() throws IOException {
    Optional<ASTExpression> a = parser.parse_StringExpression("a");
    Optional<ASTExpression> b = parser.parse_StringExpression("b");
    assertFalse(parser.hasErrors());
    assertTrue(a.isPresent());
    assertTrue(b.isPresent());
    ASTMultExpression result = CommonExpressionsMill.multExpressionBuilder()
      .setLeft(a.get())
      .setOperator("*")
      .setRight(b.get())
      .build();
    
    String output = javaPrinter.prettyprint(result);
    
    assertEquals("a*b", output);
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testDivideExpression() throws IOException {
    Optional<ASTExpression> a = parser.parse_StringExpression("a");
    Optional<ASTExpression> b = parser.parse_StringExpression("b");
    assertFalse(parser.hasErrors());
    assertTrue(a.isPresent());
    assertTrue(b.isPresent());
    ASTDivideExpression result = CommonExpressionsMill.divideExpressionBuilder()
      .setLeft(a.get())
      .setOperator("/")
      .setRight(b.get())
      .build();
    
    String output = javaPrinter.prettyprint(result);
    
    assertEquals("a/b", output);
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testModuloExpression() throws IOException {
    Optional<ASTExpression> a = parser.parse_StringExpression("a");
    Optional<ASTExpression> b = parser.parse_StringExpression("b");
    assertFalse(parser.hasErrors());
    assertTrue(a.isPresent());
    assertTrue(b.isPresent());
    ASTModuloExpression result = CommonExpressionsMill.moduloExpressionBuilder()
      .setLeft(a.get())
      .setOperator("%")
      .setRight(b.get())
      .build();
    
    String output = javaPrinter.prettyprint(result);
    
    assertEquals("a%b", output);
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testPlusExpression() throws IOException {
    Optional<ASTExpression> a = parser.parse_StringExpression("a");
    Optional<ASTExpression> b = parser.parse_StringExpression("b");
    assertFalse(parser.hasErrors());
    assertTrue(a.isPresent());
    assertTrue(b.isPresent());
    ASTPlusExpression result = CommonExpressionsMill.plusExpressionBuilder()
      .setLeft(a.get())
      .setOperator("+")
      .setRight(b.get())
      .build();
    
    String output = javaPrinter.prettyprint(result);
    
    assertEquals("a+b", output);
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testMinusExpression() throws IOException {
    Optional<ASTExpression> a = parser.parse_StringExpression("a");
    Optional<ASTExpression> b = parser.parse_StringExpression("b");
    assertFalse(parser.hasErrors());
    assertTrue(a.isPresent());
    assertTrue(b.isPresent());
    ASTMinusExpression result = CommonExpressionsMill.minusExpressionBuilder()
      .setLeft(a.get())
      .setOperator("-")
      .setRight(b.get())
      .build();
    
    String output = javaPrinter.prettyprint(result);
    
    assertEquals("a-b", output);
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testLessEqualExpression() throws IOException {
    Optional<ASTExpression> a = parser.parse_StringExpression("a");
    Optional<ASTExpression> b = parser.parse_StringExpression("b");
    assertFalse(parser.hasErrors());
    assertTrue(a.isPresent());
    assertTrue(b.isPresent());
    ASTLessEqualExpression result = CommonExpressionsMill.lessEqualExpressionBuilder()
      .setLeft(a.get())
      .setOperator("<=")
      .setRight(b.get())
      .build();
    
    String output = javaPrinter.prettyprint(result);
    
    assertEquals("a<=b", output);
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testGreaterEqualExpression() throws IOException {
    Optional<ASTExpression> a = parser.parse_StringExpression("a");
    Optional<ASTExpression> b = parser.parse_StringExpression("b");
    assertFalse(parser.hasErrors());
    assertTrue(a.isPresent());
    assertTrue(b.isPresent());
    ASTGreaterEqualExpression result = CommonExpressionsMill.greaterEqualExpressionBuilder()
      .setLeft(a.get())
      .setOperator(">=")
      .setRight(b.get())
      .build();
    
    String output = javaPrinter.prettyprint(result);
    
    assertEquals("a>=b", output);
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testLessThanExpression() throws IOException {
    Optional<ASTExpression> a = parser.parse_StringExpression("a");
    Optional<ASTExpression> b = parser.parse_StringExpression("b");
    assertFalse(parser.hasErrors());
    assertTrue(a.isPresent());
    assertTrue(b.isPresent());
    ASTLessThanExpression result = CommonExpressionsMill.lessThanExpressionBuilder()
      .setLeft(a.get())
      .setOperator("<")
      .setRight(b.get())
      .build();
    
    String output = javaPrinter.prettyprint(result);
    
    assertEquals("a<b", output);
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testGreaterThanExpression() throws IOException {
    Optional<ASTExpression> a = parser.parse_StringExpression("a");
    Optional<ASTExpression> b = parser.parse_StringExpression("b");
    assertFalse(parser.hasErrors());
    assertTrue(a.isPresent());
    assertTrue(b.isPresent());
    ASTGreaterThanExpression result = CommonExpressionsMill.greaterThanExpressionBuilder()
      .setLeft(a.get())
      .setOperator(">")
      .setRight(b.get())
      .build();
    
    String output = javaPrinter.prettyprint(result);
    
    assertEquals("a>b", output);
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testEqualsExpression() throws IOException {
    Optional<ASTExpression> a = parser.parse_StringExpression("a");
    Optional<ASTExpression> b = parser.parse_StringExpression("b");
    assertFalse(parser.hasErrors());
    assertTrue(a.isPresent());
    assertTrue(b.isPresent());
    ASTEqualsExpression result = CommonExpressionsMill.equalsExpressionBuilder()
      .setLeft(a.get())
      .setOperator("==")
      .setRight(b.get())
      .build();
    
    String output = javaPrinter.prettyprint(result);
    
    assertEquals("a==b", output);
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testNotEqualsExpression() throws IOException {
    Optional<ASTExpression> a = parser.parse_StringExpression("a");
    Optional<ASTExpression> b = parser.parse_StringExpression("b");
    assertFalse(parser.hasErrors());
    assertTrue(a.isPresent());
    assertTrue(b.isPresent());
    ASTNotEqualsExpression result = CommonExpressionsMill.notEqualsExpressionBuilder()
      .setLeft(a.get())
      .setOperator("!=")
      .setRight(b.get())
      .build();
    
    String output = javaPrinter.prettyprint(result);
    
    assertEquals("a!=b", output);
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testBooleanAndOpExpression() throws IOException {
    Optional<ASTExpression> a = parser.parse_StringExpression("a");
    Optional<ASTExpression> b = parser.parse_StringExpression("b");
    assertFalse(parser.hasErrors());
    assertTrue(a.isPresent());
    assertTrue(b.isPresent());
    ASTBooleanAndOpExpression result = CommonExpressionsMill.booleanAndOpExpressionBuilder()
      .setLeft(a.get())
      .setOperator("&&")
      .setRight(b.get())
      .build();
    
    String output = javaPrinter.prettyprint(result);
    
    assertEquals("a&&b", output);
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testBooleanOrOpExpression() throws IOException {
    Optional<ASTExpression> a = parser.parse_StringExpression("a");
    Optional<ASTExpression> b = parser.parse_StringExpression("b");
    assertFalse(parser.hasErrors());
    assertTrue(a.isPresent());
    assertTrue(b.isPresent());
    ASTBooleanOrOpExpression result = CommonExpressionsMill.booleanOrOpExpressionBuilder()
      .setLeft(a.get())
      .setOperator("||")
      .setRight(b.get())
      .build();
    
    String output = javaPrinter.prettyprint(result);
    
    assertEquals("a||b", output);
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testConditionalExpression() throws IOException {
    Optional<ASTExpression> a = parser.parse_StringExpression("a");
    Optional<ASTExpression> b = parser.parse_StringExpression("b");
    Optional<ASTExpression> c = parser.parse_StringExpression("c");
    assertFalse(parser.hasErrors());
    assertTrue(a.isPresent());
    assertTrue(b.isPresent());
    assertTrue(c.isPresent());
    ASTConditionalExpression result = CommonExpressionsMill.conditionalExpressionBuilder()
      .setCondition(a.get())
      .setTrueExpression(b.get())
      .setFalseExpression(c.get())
      .build();
    
    String output = javaPrinter.prettyprint(result);
    
    assertEquals("a ? b:c", output);
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testArrayExpression() throws IOException {
    Optional<ASTExpression> a = parser.parse_StringExpression("a");
    Optional<ASTExpression> b = parser.parse_StringExpression("b");
    assertFalse(parser.hasErrors());
    assertTrue(a.isPresent());
    assertTrue(b.isPresent());
    ASTArrayAccessExpression result =
        CommonExpressionsMill.arrayAccessExpressionBuilder()
        .setExpression(a.get())
        .setIndexExpression(b.get())
        .build();

    String output = javaPrinter.prettyprint(result);

    assertEquals("a[b]", output);

    assertTrue(Log.getFindings().isEmpty());
  }

}
