package de.monticore.typescalculator;

import de.monticore.antlr4.MCConcreteParser;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._symboltable.EVariableSymbol;
import de.monticore.expressions.expressionsbasis._symboltable.ExpressionsBasisLanguage;
import de.monticore.expressions.expressionsbasis._symboltable.ExpressionsBasisScope;
import de.monticore.types.mcbasictypes._ast.*;
import de.monticore.types.mcbasictypes._symboltable.MCTypeSymbol;
import de.monticore.types.mcbasictypestest._parser.MCBasicTypesTestParser;
import de.monticore.typescalculator.testcommonexpressions._parser.TestCommonExpressionsParser;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertTrue;

public class CommonExpressionsTest {

  private ExpressionsBasisScope scope;

  private ExpressionsBasisLanguage expressionsBasisLanguage;

  @Before
  public void setup(){
    this.expressionsBasisLanguage=new ExpressionsBasisLanguage("CommonExpressions","exp") {
      @Override
      public MCConcreteParser getParser() {
        return new TestCommonExpressionsParser();
      }
    };

    this.scope=new ExpressionsBasisScope();
    scope.setResolvingFilters(expressionsBasisLanguage.getResolvingFilters());

    EVariableSymbol symbol = new EVariableSymbol("varInt");
    MCTypeSymbol typeSymbol = new MCTypeSymbol("int");
    typeSymbol.setASTMCType(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT));
    typeSymbol.setEVariableSymbol(symbol);
    symbol.setMCTypeSymbol(typeSymbol);
    scope.add(symbol);

    symbol = new EVariableSymbol("varDouble");
    typeSymbol = new MCTypeSymbol("double");
    typeSymbol.setASTMCType(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE));
    typeSymbol.setEVariableSymbol(symbol);
    symbol.setMCTypeSymbol(typeSymbol);
    scope.add(symbol);

    symbol = new EVariableSymbol("varString");
    List<String> name = new ArrayList<>();
    name.add("java");
    name.add("lang");
    name.add("String");
    typeSymbol = new MCTypeSymbol("java.lang.String");
    typeSymbol.setEVariableSymbol(symbol);
    typeSymbol.setASTMCType(new ASTMCQualifiedType(new ASTMCQualifiedName(name)));
    symbol.setMCTypeSymbol(typeSymbol);
    scope.add(symbol);

    symbol = new EVariableSymbol("varList");
    name = new ArrayList<>();
    name.add("java");
    name.add("util");
    name.add("List");
    typeSymbol = new MCTypeSymbol("java.lang.String");
    typeSymbol.setASTMCType(new ASTMCQualifiedType(new ASTMCQualifiedName(name)));
    typeSymbol.setEVariableSymbol(symbol);
    symbol.setMCTypeSymbol(typeSymbol);
    scope.add(symbol);

    symbol = new EVariableSymbol("varChar");
    typeSymbol= new MCTypeSymbol("char");
    typeSymbol.setASTMCType(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.CHAR));
    typeSymbol.setEVariableSymbol(symbol);
    symbol.setMCTypeSymbol(typeSymbol);
    scope.add(symbol);

    symbol = new EVariableSymbol("varInteger");
    name=new ArrayList<>();
    name.add("java");
    name.add("lang");
    name.add("Integer");
    typeSymbol=new MCTypeSymbol("java.lang.Integer");
    typeSymbol.setASTMCType(new ASTMCQualifiedType(new ASTMCQualifiedName(name)));
    typeSymbol.setEVariableSymbol(symbol);
    symbol.setMCTypeSymbol(typeSymbol);
    scope.add(symbol);

    EVariableSymbol symbolB = new EVariableSymbol("varB");
    name=new ArrayList<>();
    name.add("B");
    typeSymbol=new MCTypeSymbol("B");
    typeSymbol.setEVariableSymbol(symbolB);
    typeSymbol.setASTMCType(new ASTMCQualifiedType(new ASTMCQualifiedName(name)));
    symbolB.setMCTypeSymbol(typeSymbol);

    symbol = new EVariableSymbol("varA");
    name=new ArrayList<>();
    name.add("A");
    typeSymbol=new MCTypeSymbol("A");
    List<MCTypeSymbol> subtypes= new ArrayList<>();
    subtypes.add(symbolB.getMCTypeSymbol());
    typeSymbol.setSubtypes(subtypes);
    typeSymbol.setASTMCType(new ASTMCQualifiedType(new ASTMCQualifiedName(name)));
    typeSymbol.setEVariableSymbol(symbol);
    symbol.setMCTypeSymbol(typeSymbol);
    List<MCTypeSymbol> superTypes= new ArrayList<>();
    superTypes.add(symbol.getMCTypeSymbol());
    symbolB.getMCTypeSymbol().setSupertypes(superTypes);
    scope.add(symbolB);
    scope.add(symbol);
  }

  @Test
  public void parseTest() throws IOException {
    TestCommonExpressionsParser p = new TestCommonExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("9+9");
    Optional<ASTExpression> r = p.parse_StringExpression("9.32+4.08");
    Optional<ASTExpression> s = p.parse_StringExpression("3*4");
  }

  @Test
  public void moduloIntVisitorTest() throws IOException{
    TestCommonExpressionTypesCalculator calc = new TestCommonExpressionTypesCalculator();
    TestCommonExpressionsParser p = new TestCommonExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("9%7");
    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT).deepEquals(calc.getResult()));
  }

  @Test
  public void plusIntVisitorTest() throws IOException{
    TestCommonExpressionTypesCalculator calc = new TestCommonExpressionTypesCalculator();
    MCBasicTypesTestParser parser = new MCBasicTypesTestParser();
    TestCommonExpressionsParser p = new TestCommonExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("9+7");
    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT).deepEquals(calc.getResult()));
  }

  @Test
  public void multIntVisitorTest() throws IOException{
    TestCommonExpressionTypesCalculator calc = new TestCommonExpressionTypesCalculator();
    TestCommonExpressionsParser p = new TestCommonExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("9*7");
    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT).deepEquals(calc.getResult()));
  }

  @Test
  public void divideIntVisitorTest() throws IOException{
    TestCommonExpressionTypesCalculator calc = new TestCommonExpressionTypesCalculator();
    TestCommonExpressionsParser p = new TestCommonExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("9/7");
    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT).deepEquals(calc.getResult()));
  }

  @Test
  public void minusIntVisitorTest() throws IOException{
    TestCommonExpressionTypesCalculator calc = new TestCommonExpressionTypesCalculator();
    TestCommonExpressionsParser p = new TestCommonExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("9-7");
    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT).deepEquals(calc.getResult()));
  }

  @Test
  public void plusDoubleVisitorTest() throws IOException{
    TestCommonExpressionTypesCalculator calc = new TestCommonExpressionTypesCalculator();
    TestCommonExpressionsParser p = new TestCommonExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("9.13+7.73");
    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE).deepEquals(calc.getResult()));
  }

  @Test
  public void moduloDoubleVisitorTest() throws IOException{
    TestCommonExpressionTypesCalculator calc = new TestCommonExpressionTypesCalculator();
    TestCommonExpressionsParser p = new TestCommonExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("9.13%7.73");
    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE).deepEquals(calc.getResult()));
  }

  @Test
  public void multDoubleVisitorTest() throws IOException{
    TestCommonExpressionTypesCalculator calc = new TestCommonExpressionTypesCalculator();
    TestCommonExpressionsParser p = new TestCommonExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("9.13*7.73");
    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE).deepEquals(calc.getResult()));
  }

  @Test
  public void divideDoubleVisitorTest() throws IOException{
    TestCommonExpressionTypesCalculator calc = new TestCommonExpressionTypesCalculator();
    TestCommonExpressionsParser p = new TestCommonExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("9.13/7.73");
    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE).deepEquals(calc.getResult()));
  }

  @Test
  public void minusDoubleVisitorTest() throws IOException{
    TestCommonExpressionTypesCalculator calc = new TestCommonExpressionTypesCalculator();
    TestCommonExpressionsParser p = new TestCommonExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("9.13-7.73");
    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE).deepEquals(calc.getResult()));
  }

  @Test
  public void plusIntDoubleVisitorTest() throws IOException{
    TestCommonExpressionTypesCalculator calc = new TestCommonExpressionTypesCalculator();
    TestCommonExpressionsParser p = new TestCommonExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("9+7.73");
    Optional<ASTExpression> q = p.parse_StringExpression("7.4+3");

    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE).deepEquals(calc.getResult()));

    assertTrue(q.isPresent());
    q.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE).deepEquals(calc.getResult()));
  }

  @Test
  public void moduloIntDoubleVisitorTest() throws IOException{
    TestCommonExpressionTypesCalculator calc = new TestCommonExpressionTypesCalculator();
    TestCommonExpressionsParser p = new TestCommonExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("9%7.73");
    Optional<ASTExpression> q = p.parse_StringExpression("7.4%3");

    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE).deepEquals(calc.getResult()));

    assertTrue(q.isPresent());
    q.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE).deepEquals(calc.getResult()));
  }

  @Test
  public void multIntDoubleVisitorTest() throws IOException{
    TestCommonExpressionTypesCalculator calc = new TestCommonExpressionTypesCalculator();
    TestCommonExpressionsParser p = new TestCommonExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("9*7.73");
    Optional<ASTExpression> q = p.parse_StringExpression("7.4*3");

    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE).deepEquals(calc.getResult()));

    assertTrue(q.isPresent());
    q.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE).deepEquals(calc.getResult()));
  }

  @Test
  public void divideIntDoubleVisitorTest() throws IOException{
    TestCommonExpressionTypesCalculator calc = new TestCommonExpressionTypesCalculator();
    TestCommonExpressionsParser p = new TestCommonExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("9/7.73");
    Optional<ASTExpression> q = p.parse_StringExpression("7.4/3");

    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE).deepEquals(calc.getResult()));

    assertTrue(q.isPresent());
    q.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE).deepEquals(calc.getResult()));
  }

  @Test
  public void minusIntDoubleVisitorTest() throws IOException{
    TestCommonExpressionTypesCalculator calc = new TestCommonExpressionTypesCalculator();
    TestCommonExpressionsParser p = new TestCommonExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("9-7.73");
    Optional<ASTExpression> q = p.parse_StringExpression("7.4-3");

    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE).deepEquals(calc.getResult()));

    assertTrue(q.isPresent());
    q.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE).deepEquals(calc.getResult()));
  }

  @Test
  public void lessThanTest() throws IOException{
    TestCommonExpressionTypesCalculator calc = new TestCommonExpressionTypesCalculator();
    TestCommonExpressionsParser p = new TestCommonExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("4<7");
    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.BOOLEAN).deepEquals(calc.getResult()));
  }

  @Test
  public void greaterThanTest() throws IOException{
    TestCommonExpressionTypesCalculator calc = new TestCommonExpressionTypesCalculator();
    TestCommonExpressionsParser p = new TestCommonExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("15>9");
    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.BOOLEAN).deepEquals(calc.getResult()));
  }

  @Test
  public void lessEqualTest() throws IOException{
    TestCommonExpressionTypesCalculator calc = new TestCommonExpressionTypesCalculator();
    MCBasicTypesTestParser parser = new MCBasicTypesTestParser();
    TestCommonExpressionsParser p = new TestCommonExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("4<=7");
    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.BOOLEAN).deepEquals(calc.getResult()));
  }

  @Test
  public void greaterEqualTest() throws IOException{
    TestCommonExpressionTypesCalculator calc = new TestCommonExpressionTypesCalculator();
    TestCommonExpressionsParser p = new TestCommonExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("4>=7");
    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.BOOLEAN).deepEquals(calc.getResult()));
  }

  @Test
  public void logicalNotTest() throws IOException{
    TestCommonExpressionTypesCalculator calc = new TestCommonExpressionTypesCalculator();
    TestCommonExpressionsParser p = new TestCommonExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("!true");
    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.BOOLEAN).deepEquals(calc.getResult()));
  }

  @Test
  public void booleanOrOpTest() throws IOException{
    TestCommonExpressionTypesCalculator calc = new TestCommonExpressionTypesCalculator();
    TestCommonExpressionsParser p = new TestCommonExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("true||false");
    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.BOOLEAN).deepEquals(calc.getResult()));
  }

  @Test
  public void booleanAndOpTest() throws IOException{
    TestCommonExpressionTypesCalculator calc = new TestCommonExpressionTypesCalculator();
    TestCommonExpressionsParser p = new TestCommonExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("true&&true");
    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.BOOLEAN).deepEquals(calc.getResult()));
  }

  @Test
  public void notEqualsTest() throws IOException{
    TestCommonExpressionTypesCalculator calc = new TestCommonExpressionTypesCalculator();
    TestCommonExpressionsParser p = new TestCommonExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("3!=4");
    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.BOOLEAN).deepEquals(calc.getResult()));
  }

  @Test
  public void equalsTest() throws IOException{
    TestCommonExpressionTypesCalculator calc = new TestCommonExpressionTypesCalculator();
    TestCommonExpressionsParser p = new TestCommonExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("7==7");
    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.BOOLEAN).deepEquals(calc.getResult()));
  }

  @Test
  public void bracketTest() throws IOException{
    TestCommonExpressionTypesCalculator calc = new TestCommonExpressionTypesCalculator();
    TestCommonExpressionsParser p = new TestCommonExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("(7+8)");
    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT).deepEquals(calc.getResult()));

    calc = new TestCommonExpressionTypesCalculator();
    p = new TestCommonExpressionsParser();
    o = p.parse_StringExpression("(7.56-2.67)");
    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE).deepEquals(calc.getResult()));
  }

  @Test
  public void simpleAssignmentTest() throws IOException{
    TestCommonExpressionTypesCalculator calc = new TestCommonExpressionTypesCalculator();
    TestCommonExpressionsParser p = new TestCommonExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("7+=9");
    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT).deepEquals(calc.getResult()));
  }

  @Test
  public void conditionalTest() throws IOException{
    TestCommonExpressionTypesCalculator calc = new TestCommonExpressionTypesCalculator();
    TestCommonExpressionsParser p = new TestCommonExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("7.32<3? 7.32 : 3");
    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE).deepEquals(calc.getResult()));
  }

  @Test
  public void booleanNotTest() throws IOException{
    TestCommonExpressionTypesCalculator calc = new TestCommonExpressionTypesCalculator();
    TestCommonExpressionsParser p = new TestCommonExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("~7");
    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT).deepEquals(calc.getResult()));
  }

  @Test
  public void combineOperationsTest() throws IOException{
    TestCommonExpressionTypesCalculator calc = new TestCommonExpressionTypesCalculator();
    MCBasicTypesTestParser parser = new MCBasicTypesTestParser();
    TestCommonExpressionsParser p = new TestCommonExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("9*7.73-3.5/2+4.2345");
    Optional<ASTExpression> q = p.parse_StringExpression("9*4+3-5/2");
    Optional<ASTExpression> r = p.parse_StringExpression("3<4");
    Optional<ASTExpression> s = p.parse_StringExpression("(4<6)&&true");
    Optional<ASTExpression> t = p.parse_StringExpression("~3<4? ~3 : 4");
    Optional<ASTExpression> u = p.parse_StringExpression("9*(7.5+8)");
    Optional<ASTExpression> v = p.parse_StringExpression("false&&true");

    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE).deepEquals(calc.getResult()));

    assertTrue(q.isPresent());
    q.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT).deepEquals(calc.getResult()));

    assertTrue(r.isPresent());
    r.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.BOOLEAN).deepEquals(calc.getResult()));

    assertTrue(s.isPresent());
    s.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.BOOLEAN).deepEquals(calc.getResult()));

    assertTrue(t.isPresent());
    t.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT).deepEquals(calc.getResult()));

    assertTrue(u.isPresent());
    u.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE).deepEquals(calc.getResult()));

    assertTrue(v.isPresent());
    v.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.BOOLEAN).deepEquals(calc.getResult()));
  }

  @Test
  public void callTest(){

  }

  @Test
  public void nameTest(){

  }

  @Test
  public void qualifiedNameTest(){

  }

  @Test
  public void literalTest(){

  }
}
