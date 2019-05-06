package de.monticore.typescalculator;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.types.mcbasictypes._ast.ASTConstantsMCBasicTypes;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import de.monticore.typescalculator.testassignmentexpressions._parser.TestAssignmentExpressionsParser;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertTrue;

public class AssignmentExpressionsTest {

  @Test
  public void parserTest() throws IOException {
    TestAssignmentExpressionsParser p = new TestAssignmentExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("9--");
    Optional<ASTExpression> r = p.parse_StringExpression("++12");
    Optional<ASTExpression> s = p.parse_StringExpression("-1");
  }

  @Test
  public void incSuffixTest() throws IOException{
    TestAssignmentExpressionsParser p = new TestAssignmentExpressionsParser();
    TestAssignmentExpressionTypesCalculator calc = new TestAssignmentExpressionTypesCalculator();
    Optional<ASTExpression> o = p.parse_StringExpression("4++");
    Optional<ASTExpression> r = p.parse_StringExpression("7.3++");

    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT).deepEquals(calc.getResult()));

    assertTrue(r.isPresent());
    r.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE).deepEquals(calc.getResult()));
  }

  @Test
  public void decSuffixTest() throws IOException{
    TestAssignmentExpressionsParser p = new TestAssignmentExpressionsParser();
    TestAssignmentExpressionTypesCalculator calc = new TestAssignmentExpressionTypesCalculator();
    Optional<ASTExpression> o = p.parse_StringExpression("9--");
    Optional<ASTExpression> r = p.parse_StringExpression("34.34++");

    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT).deepEquals(calc.getResult()));

    assertTrue(r.isPresent());
    r.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE).deepEquals(calc.getResult()));
  }

  @Test
  public void incPrefixTest() throws IOException{
    TestAssignmentExpressionsParser p = new TestAssignmentExpressionsParser();
    TestAssignmentExpressionTypesCalculator calc = new TestAssignmentExpressionTypesCalculator();
    Optional<ASTExpression> o = p.parse_StringExpression("++2");
    Optional<ASTExpression> r = p.parse_StringExpression("++23.4");

    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT).deepEquals(calc.getResult()));

    assertTrue(r.isPresent());
    r.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE).deepEquals(calc.getResult()));
  }

  @Test
  public void decPrefixTest() throws IOException{
    TestAssignmentExpressionsParser p = new TestAssignmentExpressionsParser();
    TestAssignmentExpressionTypesCalculator calc = new TestAssignmentExpressionTypesCalculator();
    Optional<ASTExpression> o = p.parse_StringExpression("--28");
    Optional<ASTExpression> r = p.parse_StringExpression("--12.5");

    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT).deepEquals(calc.getResult()));

    assertTrue(r.isPresent());
    r.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE).deepEquals(calc.getResult()));
  }

  @Test
  public void plusPrefixTest() throws IOException{
    TestAssignmentExpressionsParser p = new TestAssignmentExpressionsParser();
    TestAssignmentExpressionTypesCalculator calc = new TestAssignmentExpressionTypesCalculator();
    Optional<ASTExpression> o = p.parse_StringExpression("+4");
    Optional<ASTExpression> r = p.parse_StringExpression("+52.6");

    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT).deepEquals(calc.getResult()));

    assertTrue(r.isPresent());
    r.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE).deepEquals(calc.getResult()));
  }

  @Test
  public void minusPrefixTest() throws IOException{
    TestAssignmentExpressionsParser p = new TestAssignmentExpressionsParser();
    TestAssignmentExpressionTypesCalculator calc = new TestAssignmentExpressionTypesCalculator();
    Optional<ASTExpression> o = p.parse_StringExpression("-456");
    Optional<ASTExpression> r = p.parse_StringExpression("-96.07");

    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT).deepEquals(calc.getResult()));

    assertTrue(r.isPresent());
    r.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE).deepEquals(calc.getResult()));
  }

  @Test
  public void plusAssignmentTest() throws IOException{

  }

  @Test
  public void minusAssignmentTest() throws IOException{

  }

  @Test
  public void multAssignmentTest() throws IOException{

  }

  @Test
  public void divideAssignmentTest() throws IOException{

  }

  @Test
  public void regularAssignmentTest() throws IOException{

  }

  @Test
  public void andAssignmentTest() throws IOException{

  }

  @Test
  public void orAssignmentTest() throws IOException{

  }

  @Test
  public void binaryXorAssignmentTest() throws IOException{

  }

  @Test
  public void rightShiftAssignmentTest() throws IOException{

  }

  @Test
  public void leftShiftAssignmentTest() throws IOException{

  }

  @Test
  public void logicalRightAssignmentTest() throws IOException{

  }

  @Test
  public void moduloAssignmentTest() throws IOException{

  }

}
