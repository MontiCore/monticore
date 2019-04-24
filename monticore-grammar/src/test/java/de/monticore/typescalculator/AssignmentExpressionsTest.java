package de.monticore.typescalculator;

import de.monticore.antlr4.MCConcreteParser;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ExpressionsBasisMill;
import de.monticore.expressions.expressionsbasis._symboltable.*;
import de.monticore.types.mcbasictypes._ast.ASTConstantsMCBasicTypes;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._symboltable.MCTypeSymbol;
import de.monticore.typescalculator.testassignmentexpressions._parser.TestAssignmentExpressionsParser;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class AssignmentExpressionsTest {

  private ExpressionsBasisLanguage expressionsBasisLanguage;

  private ExpressionsBasisScope scope;

  @Before
  public void setup() {


    expressionsBasisLanguage = new ExpressionsBasisLanguage("AssignmentExpressions","exp") {
      @Override
      public MCConcreteParser getParser() {
        return new TestAssignmentExpressionsParser();
      }
    };

    scope = new ExpressionsBasisScope();
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
  public void parserTest() throws IOException {
    TestAssignmentExpressionsParser p = new TestAssignmentExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("9--");
    Optional<ASTExpression> r = p.parse_StringExpression("++12");
    Optional<ASTExpression> s = p.parse_StringExpression("-1");

    assertFalse(p.hasErrors());
    assertTrue(o.isPresent());
    assertTrue(r.isPresent());
    assertTrue(s.isPresent());
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
    calc.setScope(scope);
    Optional<ASTExpression> o = p.parse_StringExpression("-varInt");
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
    TestAssignmentExpressionsParser p = new TestAssignmentExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("varInt+=3");
    Optional<ASTExpression> r = p.parse_StringExpression("varDouble+=5");
    Optional<ASTExpression> s = p.parse_StringExpression("varDouble+=12.7");
    Optional<ASTExpression> t = p.parse_StringExpression("varChar+=12.7");
    Optional<ASTExpression> u = p.parse_StringExpression("varString+=varString");
    List<String> name = new ArrayList<>();
    name.add("java");
    name.add("lang");
    name.add("String");
    TestAssignmentExpressionTypesCalculator calc = new TestAssignmentExpressionTypesCalculator();
    calc.setScope(scope);

    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertEquals(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT).getBaseName(), calc.getResult().getBaseName());
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT).deepEquals(calc.getResult()));

    assertTrue(r.isPresent());
    r.get().accept(calc);
    assertEquals(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE).getBaseName(), calc.getResult().getBaseName());
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE).deepEquals(calc.getResult()));

    assertTrue(s.isPresent());
    s.get().accept(calc);
    assertEquals(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE).getBaseName(), calc.getResult().getBaseName());
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE).deepEquals(calc.getResult()));
//
//    assertTrue(t.isPresent());
//    t.get().accept(calc);
//    assertEquals(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE), calc.getResult());

    assertTrue(u.isPresent());
    u.get().accept(calc);
    assertTrue(new ASTMCQualifiedType(new ASTMCQualifiedName(name)).deepEquals(calc.getResult()));

  }

  @Test
  public void minusAssignmentTest() throws IOException{
    TestAssignmentExpressionsParser p = new TestAssignmentExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("varInt-=3");
    Optional<ASTExpression> r = p.parse_StringExpression("varDouble-=5");
    Optional<ASTExpression> s = p.parse_StringExpression("varDouble-=12.7");
    Optional<ASTExpression> t = p.parse_StringExpression("varChar-=12.7");
    TestAssignmentExpressionTypesCalculator calc = new TestAssignmentExpressionTypesCalculator();
    calc.setScope(scope);

    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertEquals(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT).getBaseName(), calc.getResult().getBaseName());
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT).deepEquals(calc.getResult()));

    assertTrue(r.isPresent());
    r.get().accept(calc);
    assertEquals(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE).getBaseName(), calc.getResult().getBaseName());
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE).deepEquals(calc.getResult()));

    assertTrue(s.isPresent());
    s.get().accept(calc);
    assertEquals(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE).getBaseName(), calc.getResult().getBaseName());
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE).deepEquals(calc.getResult()));

    //    assertTrue(t.isPresent());
    //    t.get().accept(calc);
    //    assertEquals(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE), calc.getResult());
  }

  @Test
  public void multAssignmentTest() throws IOException{
    TestAssignmentExpressionsParser p = new TestAssignmentExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("varInt*=3");
    Optional<ASTExpression> r = p.parse_StringExpression("varDouble*=5");
    Optional<ASTExpression> s = p.parse_StringExpression("varDouble*=12.7");
    Optional<ASTExpression> t = p.parse_StringExpression("varChar*=12.7");
    TestAssignmentExpressionTypesCalculator calc = new TestAssignmentExpressionTypesCalculator();
    calc.setScope(scope);

    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertEquals(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT).getBaseName(), calc.getResult().getBaseName());
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT).deepEquals(calc.getResult()));

    assertTrue(r.isPresent());
    r.get().accept(calc);
    assertEquals(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE).getBaseName(), calc.getResult().getBaseName());
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE).deepEquals(calc.getResult()));

    assertTrue(s.isPresent());
    s.get().accept(calc);
    assertEquals(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE).getBaseName(), calc.getResult().getBaseName());
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE).deepEquals(calc.getResult()));
    //
    //    assertTrue(t.isPresent());
    //    t.get().accept(calc);
    //    assertEquals(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE), calc.getResult());
  }

  @Test
  public void divideAssignmentTest() throws IOException{
    TestAssignmentExpressionsParser p = new TestAssignmentExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("varInt/=3");
    Optional<ASTExpression> r = p.parse_StringExpression("varDouble/=5");
    Optional<ASTExpression> s = p.parse_StringExpression("varDouble/=12.7");
    Optional<ASTExpression> t = p.parse_StringExpression("varChar/=12.7");
    TestAssignmentExpressionTypesCalculator calc = new TestAssignmentExpressionTypesCalculator();
    calc.setScope(scope);

    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertEquals(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT).getBaseName(), calc.getResult().getBaseName());
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT).deepEquals(calc.getResult()));

    assertTrue(r.isPresent());
    r.get().accept(calc);
    assertEquals(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE).getBaseName(), calc.getResult().getBaseName());
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE).deepEquals(calc.getResult()));

    assertTrue(s.isPresent());
    s.get().accept(calc);
    assertEquals(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE).getBaseName(), calc.getResult().getBaseName());
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE).deepEquals(calc.getResult()));
    //
    //    assertTrue(t.isPresent());
    //    t.get().accept(calc);
    //    assertEquals(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE), calc.getResult());
  }

  @Test
  public void regularAssignmentTest() throws IOException{
    TestAssignmentExpressionsParser p = new TestAssignmentExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("varInt=3");
    Optional<ASTExpression> r = p.parse_StringExpression("varDouble=varInt");
    Optional<ASTExpression> s = p.parse_StringExpression("varDouble=12.7");
//    Optional<ASTExpression> t = p.parse_StringExpression("varChar=12.7");
    Optional<ASTExpression> u = p.parse_StringExpression("varString=varString");
    Optional<ASTExpression> v = p.parse_StringExpression("varA=varB");
    TestAssignmentExpressionTypesCalculator calc = new TestAssignmentExpressionTypesCalculator();
    List<String> name = new ArrayList<>();
    name.add("java");
    name.add("lang");
    name.add("String");
    calc.setScope(scope);

    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertEquals(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT).getBaseName(), calc.getResult().getBaseName());
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT).deepEquals(calc.getResult()));

    assertTrue(r.isPresent());
    r.get().accept(calc);
    assertEquals(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE).getBaseName(), calc.getResult().getBaseName());
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE).deepEquals(calc.getResult()));

    assertTrue(s.isPresent());
    s.get().accept(calc);
    assertEquals(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE).getBaseName(), calc.getResult().getBaseName());
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE).deepEquals(calc.getResult()));

    //    assertTrue(t.isPresent());
    //    t.get().accept(calc);
    //    assertEquals(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.CHAR), calc.getResult());

    assertTrue(u.isPresent());
    u.get().accept(calc);
    assertTrue(new ASTMCQualifiedType(new ASTMCQualifiedName(name)).deepEquals(calc.getResult()));

    name=new ArrayList<>();
    name.add("A");

    assertTrue(v.isPresent());
    v.get().accept(calc);
    assertTrue(new ASTMCQualifiedType(new ASTMCQualifiedName(name)).deepEquals(calc.getResult()));
  }

  @Test
  public void andAssignmentTest() throws IOException{
    TestAssignmentExpressionsParser p = new TestAssignmentExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("varInt&=3");
    Optional<ASTExpression> r = p.parse_StringExpression("varInt&=varInt");;
    Optional<ASTExpression> q = p.parse_StringExpression("true&=false");
    TestAssignmentExpressionTypesCalculator calc = new TestAssignmentExpressionTypesCalculator();
    calc.setScope(scope);

    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT).deepEquals(calc.getResult()));

    assertTrue(r.isPresent());
    r.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT).deepEquals(calc.getResult()));

    assertTrue(q.isPresent());
    q.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.BOOLEAN).deepEquals(calc.getResult()));
  }

  @Test
  public void orAssignmentTest() throws IOException{
    TestAssignmentExpressionsParser p = new TestAssignmentExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("varInt|=3");
    Optional<ASTExpression> r = p.parse_StringExpression("varInt|=varInt");;
    Optional<ASTExpression> q = p.parse_StringExpression("true|=false");
    TestAssignmentExpressionTypesCalculator calc = new TestAssignmentExpressionTypesCalculator();
    calc.setScope(scope);

    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT).deepEquals(calc.getResult()));

    assertTrue(r.isPresent());
    r.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT).deepEquals(calc.getResult()));

    assertTrue(q.isPresent());
    q.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.BOOLEAN).deepEquals(calc.getResult()));
  }

  @Test
  public void binaryXorAssignmentTest() throws IOException{
    TestAssignmentExpressionsParser p = new TestAssignmentExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("varInt^=3");
    Optional<ASTExpression> r = p.parse_StringExpression("varInt^=varInt");;
    Optional<ASTExpression> q = p.parse_StringExpression("true^=false");
    TestAssignmentExpressionTypesCalculator calc = new TestAssignmentExpressionTypesCalculator();
    calc.setScope(scope);

    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT).deepEquals(calc.getResult()));

    assertTrue(r.isPresent());
    r.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT).deepEquals(calc.getResult()));

    assertTrue(q.isPresent());
    q.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.BOOLEAN).deepEquals(calc.getResult()));
  }

  @Test
  public void rightShiftAssignmentTest() throws IOException{
    TestAssignmentExpressionsParser p = new TestAssignmentExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("varInt>>=3");
    Optional<ASTExpression> r = p.parse_StringExpression("varInt>>=varInt");;
    TestAssignmentExpressionTypesCalculator calc = new TestAssignmentExpressionTypesCalculator();
    calc.setScope(scope);

    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT).deepEquals(calc.getResult()));

    assertTrue(r.isPresent());
    r.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT).deepEquals(calc.getResult()));

  }

  @Test
  public void leftShiftAssignmentTest() throws IOException{
    TestAssignmentExpressionsParser p = new TestAssignmentExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("varInt<<=3");
    Optional<ASTExpression> r = p.parse_StringExpression("varInt<<=varInt");;
    TestAssignmentExpressionTypesCalculator calc = new TestAssignmentExpressionTypesCalculator();
    calc.setScope(scope);

    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT).deepEquals(calc.getResult()));

    assertTrue(r.isPresent());
    r.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT).deepEquals(calc.getResult()));
  }

  @Test
  public void logicalRightAssignmentTest() throws IOException{
    TestAssignmentExpressionsParser p = new TestAssignmentExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("varInt>>>=3");
    Optional<ASTExpression> r = p.parse_StringExpression("varInt>>>=varInt");;
    TestAssignmentExpressionTypesCalculator calc = new TestAssignmentExpressionTypesCalculator();
    calc.setScope(scope);

    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT).deepEquals(calc.getResult()));

    assertTrue(r.isPresent());
    r.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT).deepEquals(calc.getResult()));
  }

  @Test
  public void moduloAssignmentTest() throws IOException{
    TestAssignmentExpressionsParser p = new TestAssignmentExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("varInt%=3");
    Optional<ASTExpression> r = p.parse_StringExpression("varDouble%=5");
    Optional<ASTExpression> s = p.parse_StringExpression("varDouble%=12.7");
    Optional<ASTExpression> t = p.parse_StringExpression("varChar%=12.7");
    TestAssignmentExpressionTypesCalculator calc = new TestAssignmentExpressionTypesCalculator();
    calc.setScope(scope);

    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertEquals(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT).getBaseName(), calc.getResult().getBaseName());
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT).deepEquals(calc.getResult()));

    assertTrue(r.isPresent());
    r.get().accept(calc);
    assertEquals(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE).getBaseName(), calc.getResult().getBaseName());
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE).deepEquals(calc.getResult()));

    assertTrue(s.isPresent());
    s.get().accept(calc);
    assertEquals(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE).getBaseName(), calc.getResult().getBaseName());
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE).deepEquals(calc.getResult()));
    //
    //    assertTrue(t.isPresent());
    //    t.get().accept(calc);
    //    assertEquals(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT), calc.getResult());
  }

  @Test
  public void combineOperatorsTest() throws IOException{
    TestAssignmentExpressionsParser p = new TestAssignmentExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("varInt+=varDouble-=7");
    Optional<ASTExpression> q = p.parse_StringExpression("varInt<<=varInt*=9");
    Optional<ASTExpression> r = p.parse_StringExpression("varInt>>>=varInt+=4");
    Optional<ASTExpression> s = p.parse_StringExpression("varInt%=varDouble-=3");
    Optional<ASTExpression> t = p.parse_StringExpression("varInt&=varInt|=3");
    Optional<ASTExpression> u = p.parse_StringExpression("varDouble=varInt^=8");
    Optional<ASTExpression> v = p.parse_StringExpression("varInt+=varDouble*=9.6");

    assertFalse(p.hasErrors());
    assertTrue(o.isPresent());
    assertTrue(q.isPresent());
    assertTrue(r.isPresent());
    assertTrue(s.isPresent());
    assertTrue(t.isPresent());
    assertTrue(u.isPresent());
    assertTrue(v.isPresent());

    TestAssignmentExpressionTypesCalculator calc = new TestAssignmentExpressionTypesCalculator();
    calc.setScope(scope);

    o.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE).deepEquals(calc.getResult()));

    q.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT).deepEquals(calc.getResult()));

    r.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT).deepEquals(calc.getResult()));

    s.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE).deepEquals(calc.getResult()));

    t.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.INT).deepEquals(calc.getResult()));

    u.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE).deepEquals(calc.getResult()));

    v.get().accept(calc);
    assertTrue(new ASTMCPrimitiveType(ASTConstantsMCBasicTypes.DOUBLE).deepEquals(calc.getResult()));
  }

}
