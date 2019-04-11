package de.monticore.typescalculator;

import de.monticore.expressions.commonexpressions._ast.ASTDivideExpression;
import de.monticore.expressions.commonexpressions._ast.ASTMinusExpression;
import de.monticore.expressions.commonexpressions._ast.ASTMultExpression;
import de.monticore.expressions.commonexpressions._ast.ASTPlusExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypestest._parser.MCBasicTypesTestParser;
import de.monticore.typescalculator.testcommonexpressions._ast.ASTDoubleExpression;
import de.monticore.typescalculator.testcommonexpressions._ast.ASTIntExpression;
import de.monticore.typescalculator.testcommonexpressions._parser.TestCommonExpressionsParser;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CommonExpressionsTest {

  @Test
  public void parseTest() throws IOException {
    TestCommonExpressionsParser p = new TestCommonExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("9+9");
    Optional<ASTExpression> r = p.parse_StringExpression("9.32+4.08");
    Optional<ASTExpression> s = p.parse_StringExpression("3*4");
  }

  @Test
  public void plusIntTest() throws IOException{
    TestCommonExpressionTypesCalculator calc = new TestCommonExpressionTypesCalculator();
    MCBasicTypesTestParser parser = new MCBasicTypesTestParser();
    TestCommonExpressionsParser p = new TestCommonExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("9+9");
    ASTPlusExpression expr = (ASTPlusExpression) o.get();
    calc.endVisit((ASTIntExpression) expr.getLeft());
    calc.endVisit((ASTIntExpression) expr.getRight());
    calc.endVisit(expr);
    Map<ASTExpression,ASTMCType> types = calc.getTypes();
    ASTExpression left = expr.getLeft();
    ASTExpression right = expr.getRight();
    assertTrue(types.size()==3);
    ASTMCPrimitiveType lefttype = (ASTMCPrimitiveType) types.get(left);
    ASTMCPrimitiveType righttype = (ASTMCPrimitiveType) types.get(right);
    ASTMCPrimitiveType plustype = (ASTMCPrimitiveType) types.get(expr);
    assertTrue(lefttype.isInt());
    assertTrue(righttype.isInt());
    assertTrue(plustype.isInt());
    ASTMCType type = calc.getResult();
    ASTMCType type2 = parser.parse_StringMCType("int").get();
    assertTrue(type.deepEquals(type2));
  }

  @Test
  public void plusIntVisitorTest() throws IOException{
    TestCommonExpressionTypesCalculator calc = new TestCommonExpressionTypesCalculator();
    MCBasicTypesTestParser parser = new MCBasicTypesTestParser();
    TestCommonExpressionsParser p = new TestCommonExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("9+7");
    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(6).deepEquals(calc.getResult()));
  }

  @Test
  public void multIntTest() throws IOException{
    TestCommonExpressionTypesCalculator calc = new TestCommonExpressionTypesCalculator();
    MCBasicTypesTestParser parser = new MCBasicTypesTestParser();
    TestCommonExpressionsParser p = new TestCommonExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("7*8");
    ASTMultExpression expr = (ASTMultExpression) o.get();
    calc.endVisit((ASTIntExpression) expr.getLeft());
    calc.endVisit((ASTIntExpression) expr.getRight());
    calc.endVisit(expr);
    Map<ASTExpression,ASTMCType> types = calc.getTypes();
    ASTExpression left = expr.getLeft();
    ASTExpression right = expr.getRight();
    assertTrue(types.size()==3);
    ASTMCPrimitiveType lefttype = (ASTMCPrimitiveType) types.get(left);
    ASTMCPrimitiveType righttype = (ASTMCPrimitiveType) types.get(right);
    ASTMCPrimitiveType multtype = (ASTMCPrimitiveType) types.get(expr);
    assertTrue(lefttype.isInt());
    assertTrue(righttype.isInt());
    assertTrue(multtype.isInt());
    ASTMCType type = calc.getResult();
    ASTMCType type2 = parser.parse_StringMCType("int").get();
    assertTrue(type.deepEquals(type2));
  }

  @Test
  public void multIntVisitorTest() throws IOException{
    TestCommonExpressionTypesCalculator calc = new TestCommonExpressionTypesCalculator();
    MCBasicTypesTestParser parser = new MCBasicTypesTestParser();
    TestCommonExpressionsParser p = new TestCommonExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("9*7");
    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(6).deepEquals(calc.getResult()));
  }

  @Test
  public void divideIntTest() throws IOException{
    TestCommonExpressionTypesCalculator calc = new TestCommonExpressionTypesCalculator();
    MCBasicTypesTestParser parser = new MCBasicTypesTestParser();
    TestCommonExpressionsParser p = new TestCommonExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("7/8");
    ASTDivideExpression expr = (ASTDivideExpression) o.get();
    calc.endVisit((ASTIntExpression) expr.getLeft());
    calc.endVisit((ASTIntExpression) expr.getRight());
    calc.endVisit(expr);
    Map<ASTExpression,ASTMCType> types = calc.getTypes();
    ASTExpression left = expr.getLeft();
    ASTExpression right = expr.getRight();
    assertTrue(types.size()==3);
    ASTMCPrimitiveType lefttype = (ASTMCPrimitiveType) types.get(left);
    ASTMCPrimitiveType righttype = (ASTMCPrimitiveType) types.get(right);
    ASTMCPrimitiveType dividetype = (ASTMCPrimitiveType) types.get(expr);
    assertTrue(lefttype.isInt());
    assertTrue(righttype.isInt());
    assertTrue(dividetype.isInt());
    ASTMCType type = calc.getResult();
    ASTMCType type2 = parser.parse_StringMCType("int").get();
    assertTrue(type.deepEquals(type2));
  }

  @Test
  public void divideIntVisitorTest() throws IOException{
    TestCommonExpressionTypesCalculator calc = new TestCommonExpressionTypesCalculator();
    MCBasicTypesTestParser parser = new MCBasicTypesTestParser();
    TestCommonExpressionsParser p = new TestCommonExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("9/7");
    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(6).deepEquals(calc.getResult()));
  }

  @Test
  public void minusIntTest() throws IOException{
    TestCommonExpressionTypesCalculator calc = new TestCommonExpressionTypesCalculator();
    MCBasicTypesTestParser parser = new MCBasicTypesTestParser();
    TestCommonExpressionsParser p = new TestCommonExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("7-8");
    ASTMinusExpression expr = (ASTMinusExpression) o.get();
    calc.endVisit((ASTIntExpression) expr.getLeft());
    calc.endVisit((ASTIntExpression) expr.getRight());
    calc.endVisit(expr);
    Map<ASTExpression,ASTMCType> types = calc.getTypes();
    ASTExpression left = expr.getLeft();
    ASTExpression right = expr.getRight();
    assertTrue(types.size()==3);
    ASTMCPrimitiveType lefttype = (ASTMCPrimitiveType) types.get(left);
    ASTMCPrimitiveType righttype = (ASTMCPrimitiveType) types.get(right);
    ASTMCPrimitiveType minustype = (ASTMCPrimitiveType) types.get(expr);
    assertTrue(lefttype.isInt());
    assertTrue(righttype.isInt());
    assertTrue(minustype.isInt());
    ASTMCType type = calc.getResult();
    ASTMCType type2 = parser.parse_StringMCType("int").get();
    assertTrue(type.deepEquals(type2));
  }

  @Test
  public void minusIntVisitorTest() throws IOException{
    TestCommonExpressionTypesCalculator calc = new TestCommonExpressionTypesCalculator();
    MCBasicTypesTestParser parser = new MCBasicTypesTestParser();
    TestCommonExpressionsParser p = new TestCommonExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("9-7");
    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(6).deepEquals(calc.getResult()));
  }

  @Test
  public void plusDoubleTest() throws IOException{
    TestCommonExpressionTypesCalculator calc = new TestCommonExpressionTypesCalculator();
    MCBasicTypesTestParser parser = new MCBasicTypesTestParser();
    TestCommonExpressionsParser p = new TestCommonExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("3.14+9.07");
    ASTPlusExpression expr = (ASTPlusExpression) o.get();
    calc.endVisit((ASTDoubleExpression) expr.getLeft());
    calc.endVisit((ASTDoubleExpression) expr.getRight());
    calc.endVisit(expr);
    Map<ASTExpression,ASTMCType> types = calc.getTypes();
    ASTExpression left = expr.getLeft();
    ASTExpression right = expr.getRight();
    assertTrue(types.size()==3);
    ASTMCPrimitiveType lefttype = (ASTMCPrimitiveType) types.get(left);
    ASTMCPrimitiveType righttype = (ASTMCPrimitiveType) types.get(right);
    ASTMCPrimitiveType plustype = (ASTMCPrimitiveType) types.get(expr);
    assertTrue(lefttype.isDouble());
    assertTrue(righttype.isDouble());
    assertTrue(plustype.isDouble());
    ASTMCType type = calc.getResult();
    ASTMCType type2 = parser.parse_StringMCType("double").get();
    assertTrue(type.deepEquals(type2));
  }

  @Test
  public void plusDoubleVisitorTest() throws IOException{
    TestCommonExpressionTypesCalculator calc = new TestCommonExpressionTypesCalculator();
    MCBasicTypesTestParser parser = new MCBasicTypesTestParser();
    TestCommonExpressionsParser p = new TestCommonExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("9.13+7.73");
    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(4).deepEquals(calc.getResult()));
  }

  @Test
  public void multDoubleTest() throws IOException{
    TestCommonExpressionTypesCalculator calc = new TestCommonExpressionTypesCalculator();
    MCBasicTypesTestParser parser = new MCBasicTypesTestParser();
    TestCommonExpressionsParser p = new TestCommonExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("7.67*8.05");
    ASTMultExpression expr = (ASTMultExpression) o.get();
    calc.endVisit((ASTDoubleExpression) expr.getLeft());
    calc.endVisit((ASTDoubleExpression) expr.getRight());
    calc.endVisit(expr);
    Map<ASTExpression,ASTMCType> types = calc.getTypes();
    ASTExpression left = expr.getLeft();
    ASTExpression right = expr.getRight();
    assertTrue(types.size()==3);
    ASTMCPrimitiveType lefttype = (ASTMCPrimitiveType) types.get(left);
    ASTMCPrimitiveType righttype = (ASTMCPrimitiveType) types.get(right);
    ASTMCPrimitiveType multtype = (ASTMCPrimitiveType) types.get(expr);
    assertTrue(lefttype.isDouble());
    assertTrue(righttype.isDouble());
    assertTrue(multtype.isDouble());
    ASTMCType type = calc.getResult();
    ASTMCType type2 = parser.parse_StringMCType("double").get();
    assertTrue(type.deepEquals(type2));
  }

  @Test
  public void multDoubleVisitorTest() throws IOException{
    TestCommonExpressionTypesCalculator calc = new TestCommonExpressionTypesCalculator();
    MCBasicTypesTestParser parser = new MCBasicTypesTestParser();
    TestCommonExpressionsParser p = new TestCommonExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("9.13*7.73");
    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(4).deepEquals(calc.getResult()));
  }

  @Test
  public void divideDoubleTest() throws IOException{
    TestCommonExpressionTypesCalculator calc = new TestCommonExpressionTypesCalculator();
    MCBasicTypesTestParser parser = new MCBasicTypesTestParser();
    TestCommonExpressionsParser p = new TestCommonExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("3.14/9.07");
    ASTDivideExpression expr = (ASTDivideExpression) o.get();
    calc.endVisit((ASTDoubleExpression) expr.getLeft());
    calc.endVisit((ASTDoubleExpression) expr.getRight());
    calc.endVisit(expr);
    Map<ASTExpression,ASTMCType> types = calc.getTypes();
    ASTExpression left = expr.getLeft();
    ASTExpression right = expr.getRight();
    assertTrue(types.size()==3);
    ASTMCPrimitiveType lefttype = (ASTMCPrimitiveType) types.get(left);
    ASTMCPrimitiveType righttype = (ASTMCPrimitiveType) types.get(right);
    ASTMCPrimitiveType dividetype = (ASTMCPrimitiveType) types.get(expr);
    assertTrue(lefttype.isDouble());
    assertTrue(righttype.isDouble());
    assertTrue(dividetype.isDouble());
    ASTMCType type = calc.getResult();
    ASTMCType type2 = parser.parse_StringMCType("double").get();
    assertTrue(type.deepEquals(type2));
  }

  @Test
  public void divideDoubleVisitorTest() throws IOException{
    TestCommonExpressionTypesCalculator calc = new TestCommonExpressionTypesCalculator();
    MCBasicTypesTestParser parser = new MCBasicTypesTestParser();
    TestCommonExpressionsParser p = new TestCommonExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("9.13/7.73");
    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(4).deepEquals(calc.getResult()));
  }

  @Test
  public void minusDoubleTest() throws IOException{
    TestCommonExpressionTypesCalculator calc = new TestCommonExpressionTypesCalculator();
    MCBasicTypesTestParser parser = new MCBasicTypesTestParser();
    TestCommonExpressionsParser p = new TestCommonExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("3.14-9.07");
    ASTMinusExpression expr = (ASTMinusExpression) o.get();
    calc.endVisit((ASTDoubleExpression) expr.getLeft());
    calc.endVisit((ASTDoubleExpression) expr.getRight());
    calc.endVisit(expr);
    Map<ASTExpression,ASTMCType> types = calc.getTypes();
    ASTExpression left = expr.getLeft();
    ASTExpression right = expr.getRight();
    assertTrue(types.size()==3);
    ASTMCPrimitiveType lefttype = (ASTMCPrimitiveType) types.get(left);
    ASTMCPrimitiveType righttype = (ASTMCPrimitiveType) types.get(right);
    ASTMCPrimitiveType minustype = (ASTMCPrimitiveType) types.get(expr);
    assertTrue(lefttype.isDouble());
    assertTrue(righttype.isDouble());
    assertTrue(minustype.isDouble());
    ASTMCType type = calc.getResult();
    ASTMCType type2 = parser.parse_StringMCType("double").get();
    assertTrue(type.deepEquals(type2));
  }

  @Test
  public void minusDoubleVisitorTest() throws IOException{
    TestCommonExpressionTypesCalculator calc = new TestCommonExpressionTypesCalculator();
    MCBasicTypesTestParser parser = new MCBasicTypesTestParser();
    TestCommonExpressionsParser p = new TestCommonExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("9.13-7.73");
    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(4).deepEquals(calc.getResult()));
  }

  @Test
  public void plusDoubleIntTest() throws IOException{
    TestCommonExpressionTypesCalculator calc = new TestCommonExpressionTypesCalculator();
    MCBasicTypesTestParser parser = new MCBasicTypesTestParser();
    TestCommonExpressionsParser p = new TestCommonExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("9+7.73");
    Optional<ASTExpression> q = p.parse_StringExpression("7.4+3");

    ASTPlusExpression expr = (ASTPlusExpression) o.get();
    calc.endVisit((ASTIntExpression) expr.getLeft());
    calc.endVisit((ASTDoubleExpression) expr.getRight());
    calc.endVisit(expr);
    Map<ASTExpression,ASTMCType> types = calc.getTypes();
    ASTExpression left = expr.getLeft();
    ASTExpression right = expr.getRight();
    assertTrue(types.size()==3);
    ASTMCPrimitiveType lefttype = (ASTMCPrimitiveType) types.get(left);
    ASTMCPrimitiveType righttype = (ASTMCPrimitiveType) types.get(right);
    ASTMCPrimitiveType plustype = (ASTMCPrimitiveType) types.get(expr);
    assertTrue(lefttype.isInt());
    assertTrue(righttype.isDouble());
    assertTrue(plustype.isDouble());
    ASTMCType type = calc.getResult();
    ASTMCType type2 = parser.parse_StringMCType("double").get();
    assertTrue(type.deepEquals(type2));

    calc = new TestCommonExpressionTypesCalculator();
    expr = (ASTPlusExpression) q.get();
    calc.endVisit((ASTDoubleExpression) expr.getLeft());
    calc.endVisit((ASTIntExpression) expr.getRight());
    calc.endVisit(expr);
    types = calc.getTypes();
    left = expr.getLeft();
    right = expr.getRight();
    assertTrue(types.size()==3);
    lefttype = (ASTMCPrimitiveType) types.get(left);
    righttype = (ASTMCPrimitiveType) types.get(right);
    plustype = (ASTMCPrimitiveType) types.get(expr);
    assertTrue(lefttype.isDouble());
    assertTrue(righttype.isInt());
    assertTrue(plustype.isDouble());
    type = calc.getResult();
    type2 = parser.parse_StringMCType("double").get();
    assertTrue(type.deepEquals(type2));
  }

  @Test
  public void plusIntDoubleVisitorTest() throws IOException{
    TestCommonExpressionTypesCalculator calc = new TestCommonExpressionTypesCalculator();
    MCBasicTypesTestParser parser = new MCBasicTypesTestParser();
    TestCommonExpressionsParser p = new TestCommonExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("9+7.73");
    Optional<ASTExpression> q = p.parse_StringExpression("7.4+3");

    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(4).deepEquals(calc.getResult()));

    assertTrue(q.isPresent());
    q.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(4).deepEquals(calc.getResult()));
  }

  @Test
  public void multDoubleIntTest() throws IOException{
    TestCommonExpressionTypesCalculator calc = new TestCommonExpressionTypesCalculator();
    MCBasicTypesTestParser parser = new MCBasicTypesTestParser();
    TestCommonExpressionsParser p = new TestCommonExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("9*7.73");
    Optional<ASTExpression> q = p.parse_StringExpression("7.4*3");

    ASTMultExpression expr = (ASTMultExpression) o.get();
    calc.endVisit((ASTIntExpression) expr.getLeft());
    calc.endVisit((ASTDoubleExpression) expr.getRight());
    calc.endVisit(expr);
    Map<ASTExpression,ASTMCType> types = calc.getTypes();
    ASTExpression left = expr.getLeft();
    ASTExpression right = expr.getRight();
    assertTrue(types.size()==3);
    ASTMCPrimitiveType lefttype = (ASTMCPrimitiveType) types.get(left);
    ASTMCPrimitiveType righttype = (ASTMCPrimitiveType) types.get(right);
    ASTMCPrimitiveType multtype = (ASTMCPrimitiveType) types.get(expr);
    assertTrue(lefttype.isInt());
    assertTrue(righttype.isDouble());
    assertTrue(multtype.isDouble());
    ASTMCType type = calc.getResult();
    ASTMCType type2 = parser.parse_StringMCType("double").get();
    assertTrue(type.deepEquals(type2));

    calc = new TestCommonExpressionTypesCalculator();
    expr = (ASTMultExpression) q.get();
    calc.endVisit((ASTDoubleExpression) expr.getLeft());
    calc.endVisit((ASTIntExpression) expr.getRight());
    calc.endVisit(expr);
    types = calc.getTypes();
    left = expr.getLeft();
    right = expr.getRight();
    assertTrue(types.size()==3);
    lefttype = (ASTMCPrimitiveType) types.get(left);
    righttype = (ASTMCPrimitiveType) types.get(right);
    multtype = (ASTMCPrimitiveType) types.get(expr);
    assertTrue(lefttype.isDouble());
    assertTrue(righttype.isInt());
    assertTrue(multtype.isDouble());
    type = calc.getResult();
    type2 = parser.parse_StringMCType("double").get();
    assertTrue(type.deepEquals(type2));
  }

  @Test
  public void multIntDoubleVisitorTest() throws IOException{
    TestCommonExpressionTypesCalculator calc = new TestCommonExpressionTypesCalculator();
    MCBasicTypesTestParser parser = new MCBasicTypesTestParser();
    TestCommonExpressionsParser p = new TestCommonExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("9*7.73");
    Optional<ASTExpression> q = p.parse_StringExpression("7.4*3");

    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(4).deepEquals(calc.getResult()));

    assertTrue(q.isPresent());
    q.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(4).deepEquals(calc.getResult()));
  }

  @Test
  public void divideDoubleIntTest() throws IOException{
    TestCommonExpressionTypesCalculator calc = new TestCommonExpressionTypesCalculator();
    MCBasicTypesTestParser parser = new MCBasicTypesTestParser();
    TestCommonExpressionsParser p = new TestCommonExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("9/7.73");
    Optional<ASTExpression> q = p.parse_StringExpression("7.4/3");

    ASTDivideExpression expr = (ASTDivideExpression) o.get();
    calc.endVisit((ASTIntExpression) expr.getLeft());
    calc.endVisit((ASTDoubleExpression) expr.getRight());
    calc.endVisit(expr);
    Map<ASTExpression,ASTMCType> types = calc.getTypes();
    ASTExpression left = expr.getLeft();
    ASTExpression right = expr.getRight();
    assertTrue(types.size()==3);
    ASTMCPrimitiveType lefttype = (ASTMCPrimitiveType) types.get(left);
    ASTMCPrimitiveType righttype = (ASTMCPrimitiveType) types.get(right);
    ASTMCPrimitiveType dividetype = (ASTMCPrimitiveType) types.get(expr);
    assertTrue(lefttype.isInt());
    assertTrue(righttype.isDouble());
    assertTrue(dividetype.isDouble());
    ASTMCType type = calc.getResult();
    ASTMCType type2 = parser.parse_StringMCType("double").get();
    assertTrue(type.deepEquals(type2));

    calc = new TestCommonExpressionTypesCalculator();
    expr = (ASTDivideExpression) q.get();
    calc.endVisit((ASTDoubleExpression) expr.getLeft());
    calc.endVisit((ASTIntExpression) expr.getRight());
    calc.endVisit(expr);
    types = calc.getTypes();
    left = expr.getLeft();
    right = expr.getRight();
    assertTrue(types.size()==3);
    lefttype = (ASTMCPrimitiveType) types.get(left);
    righttype = (ASTMCPrimitiveType) types.get(right);
    dividetype = (ASTMCPrimitiveType) types.get(expr);
    assertTrue(lefttype.isDouble());
    assertTrue(righttype.isInt());
    assertTrue(dividetype.isDouble());
    type = calc.getResult();
    type2 = parser.parse_StringMCType("double").get();
    assertTrue(type.deepEquals(type2));
  }

  @Test
  public void divideIntDoubleVisitorTest() throws IOException{
    TestCommonExpressionTypesCalculator calc = new TestCommonExpressionTypesCalculator();
    MCBasicTypesTestParser parser = new MCBasicTypesTestParser();
    TestCommonExpressionsParser p = new TestCommonExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("9/7.73");
    Optional<ASTExpression> q = p.parse_StringExpression("7.4/3");

    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(4).deepEquals(calc.getResult()));

    assertTrue(q.isPresent());
    q.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(4).deepEquals(calc.getResult()));
  }

  @Test
  public void minusDoubleIntTest() throws IOException{
    TestCommonExpressionTypesCalculator calc = new TestCommonExpressionTypesCalculator();
    MCBasicTypesTestParser parser = new MCBasicTypesTestParser();
    TestCommonExpressionsParser p = new TestCommonExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("9-7.73");
    Optional<ASTExpression> q = p.parse_StringExpression("7.4-3");

    ASTMinusExpression expr = (ASTMinusExpression) o.get();
    calc.endVisit((ASTIntExpression) expr.getLeft());
    calc.endVisit((ASTDoubleExpression) expr.getRight());
    calc.endVisit(expr);
    Map<ASTExpression,ASTMCType> types = calc.getTypes();
    ASTExpression left = expr.getLeft();
    ASTExpression right = expr.getRight();
    assertTrue(types.size()==3);
    ASTMCPrimitiveType lefttype = (ASTMCPrimitiveType) types.get(left);
    ASTMCPrimitiveType righttype = (ASTMCPrimitiveType) types.get(right);
    ASTMCPrimitiveType minustype = (ASTMCPrimitiveType) types.get(expr);
    assertTrue(lefttype.isInt());
    assertTrue(righttype.isDouble());
    assertTrue(minustype.isDouble());
    ASTMCType type = calc.getResult();
    ASTMCType type2 = parser.parse_StringMCType("double").get();
    assertTrue(type.deepEquals(type2));

    calc = new TestCommonExpressionTypesCalculator();
    expr = (ASTMinusExpression) q.get();
    calc.endVisit((ASTDoubleExpression) expr.getLeft());
    calc.endVisit((ASTIntExpression) expr.getRight());
    calc.endVisit(expr);
    types = calc.getTypes();
    left = expr.getLeft();
    right = expr.getRight();
    assertTrue(types.size()==3);
    lefttype = (ASTMCPrimitiveType) types.get(left);
    righttype = (ASTMCPrimitiveType) types.get(right);
    minustype = (ASTMCPrimitiveType) types.get(expr);
    assertTrue(lefttype.isDouble());
    assertTrue(righttype.isInt());
    assertTrue(minustype.isDouble());
    type = calc.getResult();
    type2 = parser.parse_StringMCType("double").get();
    assertTrue(type.deepEquals(type2));
  }

  @Test
  public void minusIntDoubleVisitorTest() throws IOException{
    TestCommonExpressionTypesCalculator calc = new TestCommonExpressionTypesCalculator();
    MCBasicTypesTestParser parser = new MCBasicTypesTestParser();
    TestCommonExpressionsParser p = new TestCommonExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("9-7.73");
    Optional<ASTExpression> q = p.parse_StringExpression("7.4-3");

    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(4).deepEquals(calc.getResult()));

    assertTrue(q.isPresent());
    q.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(4).deepEquals(calc.getResult()));
  }

  @Test
  public void combineOperations() throws IOException{
    TestCommonExpressionTypesCalculator calc = new TestCommonExpressionTypesCalculator();
    MCBasicTypesTestParser parser = new MCBasicTypesTestParser();
    TestCommonExpressionsParser p = new TestCommonExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("9*7.73-3.5/2+4.2345");
    Optional<ASTExpression> q = p.parse_StringExpression("9*4+3-5/2");

    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(4).deepEquals(calc.getResult()));

    assertTrue(q.isPresent());
    q.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(6).deepEquals(calc.getResult()));
  }

//  @Test
//  public void distributiveTest() throws IOException{
//    TestCommonExpressionTypesCalculator calc = new TestCommonExpressionTypesCalculator();
//    MCBasicTypesTestParser parser = new MCBasicTypesTestParser();
//    TestCommonExpressionsParser p = new TestCommonExpressionsParser();
//    Optional<ASTExpression> o = p.parse_StringExpression("9*(7.73+3)");
//
//    assertTrue(o.isPresent());
//    o.get().accept(calc);
//    assertTrue(new ASTMCPrimitiveType(4).deepEquals(calc.getResult()));
//  }

}
