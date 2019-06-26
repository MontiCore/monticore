package de.monticore.typescalculator;

import de.monticore.antlr4.MCConcreteParser;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._symboltable.*;
import de.monticore.types.mcbasictypes._ast.*;
import de.monticore.types.mcbasictypes._symboltable.MCTypeSymbol;
import de.monticore.typescalculator.combineexpressions._parser.CombineExpressionsParser;
import de.monticore.typescalculator.testassignmentexpressions._parser.TestAssignmentExpressionsParser;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class AssignmentExpressionsTest {

  private ExpressionsBasisScope scope;

  @Before
  public void setup(){
    ExpressionsBasisLanguage expressionsBasisLanguage=new ExpressionsBasisLanguage("CombineExpressions","exp") {
      @Override
      public MCConcreteParser getParser() {
        return new CombineExpressionsParser();
      }
    };
    Log.enableFailQuick(false);

    this.scope=new ExpressionsBasisScope();
    scope.setResolvingFilters(expressionsBasisLanguage.getResolvingFilters());

    EVariableSymbol symbol = new EVariableSymbol("varInt");
    MCTypeSymbol typeSymbol = new MCTypeSymbol("int");
    typeSymbol.setASTMCType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build());
    typeSymbol.setEVariableSymbol(symbol);
    symbol.setMCTypeSymbol(typeSymbol);
    scope.add(symbol);

    symbol = new EVariableSymbol("varDouble");
    typeSymbol = new MCTypeSymbol("double");
    typeSymbol.setASTMCType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build());
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
    typeSymbol.setASTMCType(MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(name).build()).build());
    symbol.setMCTypeSymbol(typeSymbol);
    scope.add(symbol);

    symbol = new EVariableSymbol("varList");
    name = new ArrayList<>();
    name.add("java");
    name.add("util");
    name.add("List");
    typeSymbol = new MCTypeSymbol("java.lang.String");
    typeSymbol.setASTMCType(MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(name).build()).build());
    typeSymbol.setEVariableSymbol(symbol);
    symbol.setMCTypeSymbol(typeSymbol);
    scope.add(symbol);

    symbol = new EVariableSymbol("varChar");
    typeSymbol= new MCTypeSymbol("char");
    typeSymbol.setASTMCType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.CHAR).build());
    typeSymbol.setEVariableSymbol(symbol);
    symbol.setMCTypeSymbol(typeSymbol);
    scope.add(symbol);

    symbol = new EVariableSymbol("varInteger");
    name=new ArrayList<>();
    name.add("java");
    name.add("lang");
    name.add("Integer");
    typeSymbol=new MCTypeSymbol("java.lang.Integer");
    typeSymbol.setASTMCType(MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(name).build()).build());
    typeSymbol.setEVariableSymbol(symbol);
    symbol.setMCTypeSymbol(typeSymbol);
    scope.add(symbol);

    EVariableSymbol symbolB = new EVariableSymbol("varB");
    name=new ArrayList<>();
    name.add("B");
    typeSymbol=new MCTypeSymbol("B");
    typeSymbol.setEVariableSymbol(symbolB);
    typeSymbol.setASTMCType(MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(name).build()).build());
    symbolB.setMCTypeSymbol(typeSymbol);

    symbol = new EVariableSymbol("varA");
    name=new ArrayList<>();
    name.add("A");
    typeSymbol=new MCTypeSymbol("A");
    List<MCTypeSymbol> subtypes= new ArrayList<>();
    subtypes.add(symbolB.getMCTypeSymbol());
    typeSymbol.setSubtypes(subtypes);
    typeSymbol.setASTMCType(MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(name).build()).build());
    typeSymbol.setEVariableSymbol(symbol);
    symbol.setMCTypeSymbol(typeSymbol);
    List<MCTypeSymbol> superTypes= new ArrayList<>();
    superTypes.add(symbol.getMCTypeSymbol());
    symbolB.getMCTypeSymbol().setSupertypes(superTypes);
    scope.add(symbolB);
    scope.add(symbol);

    symbol = new EVariableSymbol("varName");
    name=new ArrayList<>();
    name.add("Test");
    typeSymbol= new MCTypeSymbol("Name");
    ASTMCType type = MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(name).build()).build();
    typeSymbol.setASTMCType(type);
    typeSymbol.setEVariableSymbol(symbol);
    symbol.setMCTypeSymbol(typeSymbol);
    scope.add(symbol);

    EMethodSymbol methodSymbol = new EMethodSymbol("call");
    typeSymbol = new MCTypeSymbol("call");
    typeSymbol.setMethodSymbol(methodSymbol);
    methodSymbol.setMCTypeSymbol(typeSymbol);
    methodSymbol.setReturnType(MCBasicTypesMill.mCReturnTypeBuilder().setMCType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build()).build());
    scope.add(methodSymbol);

    ExpressionsBasisScope ascope = new ExpressionsBasisScope();
    scope.addSubScope(ascope);
    ascope.setName("A");
    ExpressionsBasisScope bscope = new ExpressionsBasisScope();
    bscope.setName("B");
    ascope.addSubScope(bscope);
    ExpressionsBasisScope cscope = new ExpressionsBasisScope();
    cscope.setName("C");
    bscope.addSubScope(cscope);

    cscope.add(methodSymbol);


    symbol = new EVariableSymbol("QName");
    name=new ArrayList<>();
    typeSymbol= new MCTypeSymbol("QName");
    type = MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(name).build()).build();
    typeSymbol.setASTMCType(type);
    typeSymbol.setEVariableSymbol(symbol);
    symbol.setMCTypeSymbol(typeSymbol);
    cscope.add(symbol);

    symbol = new EVariableSymbol("varBool");
    typeSymbol=new MCTypeSymbol("boolean");
    typeSymbol.setEVariableSymbol(symbol);
    typeSymbol.setASTMCType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build());
    symbol.setMCTypeSymbol(typeSymbol);
    scope.add(symbol);

    symbol = new EVariableSymbol("varBool2");
    typeSymbol=new MCTypeSymbol("boolean");
    typeSymbol.setEVariableSymbol(symbol);
    typeSymbol.setASTMCType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build());
    symbol.setMCTypeSymbol(typeSymbol);
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
    CombineExpressionsParser p = new CombineExpressionsParser();
    CombineExpressionsTypesCalculator calc = new CombineExpressionsTypesCalculator(scope);
    Optional<ASTExpression> o = p.parse_StringExpression("4++");
    Optional<ASTExpression> r = p.parse_StringExpression("7++");

    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(o.get())));


    assertTrue(r.isPresent());
    r.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(r.get())));

  }

  @Test
  public void decSuffixTest() throws IOException{
    CombineExpressionsParser p = new CombineExpressionsParser();
    CombineExpressionsTypesCalculator calc = new CombineExpressionsTypesCalculator(scope);
    Optional<ASTExpression> o = p.parse_StringExpression("9--");
    Optional<ASTExpression> r = p.parse_StringExpression("34--");

    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(o.get())));


    assertTrue(r.isPresent());
    r.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(r.get())));

  }

  @Test
  public void incPrefixTest() throws IOException{
    CombineExpressionsParser p = new CombineExpressionsParser();
    CombineExpressionsTypesCalculator calc = new CombineExpressionsTypesCalculator(scope);
    Optional<ASTExpression> o = p.parse_StringExpression("++2");
    Optional<ASTExpression> r = p.parse_StringExpression("++23");

    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(o.get())));


    assertTrue(r.isPresent());
    r.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(r.get())));

  }

  @Test
  public void decPrefixTest() throws IOException{
    CombineExpressionsParser p = new CombineExpressionsParser();
    CombineExpressionsTypesCalculator calc = new CombineExpressionsTypesCalculator(scope);
    Optional<ASTExpression> o = p.parse_StringExpression("--28");
    Optional<ASTExpression> r = p.parse_StringExpression("--12");

    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(o.get())));


    assertTrue(r.isPresent());
    r.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(r.get())));

  }

  @Test
  public void plusPrefixTest() throws IOException{
    CombineExpressionsParser p = new CombineExpressionsParser();
    CombineExpressionsTypesCalculator calc = new CombineExpressionsTypesCalculator(scope);
    Optional<ASTExpression> o = p.parse_StringExpression("+4");
    Optional<ASTExpression> r = p.parse_StringExpression("+52");

    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(o.get())));


    assertTrue(r.isPresent());
    r.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(r.get())));

  }

  @Test
  public void minusPrefixTest() throws IOException{
    CombineExpressionsParser p = new CombineExpressionsParser();
    CombineExpressionsTypesCalculator calc = new CombineExpressionsTypesCalculator(scope);
    Optional<ASTExpression> o = p.parse_StringExpression("-varInt");
    Optional<ASTExpression> r = p.parse_StringExpression("-96");

    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(o.get())));


    assertTrue(r.isPresent());
    r.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(r.get())));

  }

  @Test
  public void plusAssignmentTest() throws IOException{
    CombineExpressionsParser p = new CombineExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("varInt+=3");
    Optional<ASTExpression> r = p.parse_StringExpression("varDouble+=5");
    Optional<ASTExpression> s = p.parse_StringExpression("varDouble+=12.7");
    Optional<ASTExpression> t = p.parse_StringExpression("varChar+=12");
    Optional<ASTExpression> u = p.parse_StringExpression("varString+=varString");
    List<String> name = new ArrayList<>();
    name.add("java");
    name.add("lang");
    name.add("String");
    CombineExpressionsTypesCalculator calc = new CombineExpressionsTypesCalculator(scope);

    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(o.get())));


    assertTrue(r.isPresent());
    r.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(r.get())));


    assertTrue(s.isPresent());
    s.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(s.get())));


    //    assertTrue(t.isPresent());
    //    t.get().accept(calc);
    //    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(t.get())));


    assertTrue(u.isPresent());
    u.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(name).build()).build().deepEquals(calc.calculateType(u.get())));


  }

  @Test
  public void minusAssignmentTest() throws IOException{
    CombineExpressionsParser p = new CombineExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("varInt-=3");
    Optional<ASTExpression> r = p.parse_StringExpression("varDouble-=5");
    Optional<ASTExpression> s = p.parse_StringExpression("varDouble-=12.7");
    Optional<ASTExpression> t = p.parse_StringExpression("varChar-=12");
    CombineExpressionsTypesCalculator calc = new CombineExpressionsTypesCalculator(scope);

    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(o.get())));


    assertTrue(r.isPresent());
    r.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(r.get())));


    assertTrue(s.isPresent());
    s.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(s.get())));


    //    assertTrue(t.isPresent());
    //    t.get().accept(calc);
    //    assertEquals(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build(), calc.getResult());
    //    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(t.get())));

  }

  @Test
  public void multAssignmentTest() throws IOException{
    CombineExpressionsParser p = new CombineExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("varInt*=3");
    Optional<ASTExpression> r = p.parse_StringExpression("varDouble*=5");
    Optional<ASTExpression> s = p.parse_StringExpression("varDouble*=12.7");
    Optional<ASTExpression> t = p.parse_StringExpression("varChar*=12.7");
    CombineExpressionsTypesCalculator calc = new CombineExpressionsTypesCalculator(scope);

    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(o.get())));


    assertTrue(r.isPresent());
    r.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(r.get())));


    assertTrue(s.isPresent());
    s.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(s.get())));


    //    assertTrue(t.isPresent());
    //    t.get().accept(calc);
    //    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(t.get())));
  }

  @Test
  public void divideAssignmentTest() throws IOException{
    CombineExpressionsParser p = new CombineExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("varInt/=3");
    Optional<ASTExpression> r = p.parse_StringExpression("varDouble/=5");
    Optional<ASTExpression> s = p.parse_StringExpression("varDouble/=12.7");
    Optional<ASTExpression> t = p.parse_StringExpression("varChar/=12.7");
    CombineExpressionsTypesCalculator calc = new CombineExpressionsTypesCalculator(scope);

    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertEquals(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().getBaseName(), calc.getResult().getBaseName());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(o.get())));


    assertTrue(r.isPresent());
    r.get().accept(calc);
    assertEquals(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().getBaseName(), calc.getResult().getBaseName());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(r.get())));


    assertTrue(s.isPresent());
    s.get().accept(calc);
    assertEquals(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().getBaseName(), calc.getResult().getBaseName());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(s.get())));


    //    assertTrue(t.isPresent());
    //    t.get().accept(calc);
    //    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(t.get())));
  }

  @Test
  public void regularAssignmentTest() throws IOException{
    CombineExpressionsParser p = new CombineExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("varInt=3");
    Optional<ASTExpression> r = p.parse_StringExpression("varDouble=varInt");
    Optional<ASTExpression> s = p.parse_StringExpression("varDouble=12.7");
    Optional<ASTExpression> t = p.parse_StringExpression("varChar=12");
    Optional<ASTExpression> u = p.parse_StringExpression("varString=varString");
    Optional<ASTExpression> v = p.parse_StringExpression("varA=varB");
    CombineExpressionsTypesCalculator calc = new CombineExpressionsTypesCalculator(scope);
    List<String> name = new ArrayList<>();
    name.add("java");
    name.add("lang");
    name.add("String");

    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertEquals(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().getBaseName(), calc.getResult().getBaseName());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(o.get())));


    assertTrue(r.isPresent());
    r.get().accept(calc);
    assertEquals(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().getBaseName(), calc.getResult().getBaseName());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(r.get())));


    assertTrue(s.isPresent());
    s.get().accept(calc);
    assertEquals(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().getBaseName(), calc.getResult().getBaseName());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(s.get())));


    //    assertTrue(t.isPresent());
    //    t.get().accept(calc);
    //    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.CHAR).build().deepEquals(calc.calculateType(t.get())));


    assertTrue(u.isPresent());
    u.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(name).build()).build().deepEquals(calc.calculateType(u.get())));


    name=new ArrayList<>();
    name.add("A");

    assertTrue(v.isPresent());
    v.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(name).build()).build().deepEquals(calc.calculateType(v.get())));

  }

  @Test
  public void andAssignmentTest() throws IOException{
    CombineExpressionsParser p = new CombineExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("varInt&=3");
    Optional<ASTExpression> r = p.parse_StringExpression("varInt&=varInt");
    Optional<ASTExpression> q = p.parse_StringExpression("varBool&=false");
    CombineExpressionsTypesCalculator calc = new CombineExpressionsTypesCalculator(scope);

    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(o.get())));


    assertTrue(r.isPresent());
    r.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(r.get())));


    assertTrue(q.isPresent());
    q.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build().deepEquals(calc.calculateType(q.get())));

  }

  @Test
  public void orAssignmentTest() throws IOException{
    CombineExpressionsParser p = new CombineExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("varInt|=3");
    Optional<ASTExpression> r = p.parse_StringExpression("varInt|=varInt");
    Optional<ASTExpression> q = p.parse_StringExpression("varBool|=false");
    CombineExpressionsTypesCalculator calc = new CombineExpressionsTypesCalculator(scope);

    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(o.get())));


    assertTrue(r.isPresent());
    r.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(r.get())));


    assertTrue(q.isPresent());
    q.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build().deepEquals(calc.calculateType(q.get())));
  }

  @Test
  public void binaryXorAssignmentTest() throws IOException{
    CombineExpressionsParser p = new CombineExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("varInt^=3");
    Optional<ASTExpression> r = p.parse_StringExpression("varInt^=varInt");
    Optional<ASTExpression> q = p.parse_StringExpression("varBool^=varBool2");
    CombineExpressionsTypesCalculator calc = new CombineExpressionsTypesCalculator(scope);

    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(o.get())));


    assertTrue(r.isPresent());
    r.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(r.get())));


    assertTrue(q.isPresent());
    q.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build().deepEquals(calc.calculateType(q.get())));
  }

  @Test
  public void rightShiftAssignmentTest() throws IOException{
    CombineExpressionsParser p = new CombineExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("varInt>>=3");
    Optional<ASTExpression> r = p.parse_StringExpression("varInt>>=varInt");
    CombineExpressionsTypesCalculator calc = new CombineExpressionsTypesCalculator(scope);

    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(o.get())));


    assertTrue(r.isPresent());
    r.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(r.get())));


  }

  @Test
  public void leftShiftAssignmentTest() throws IOException{
    CombineExpressionsParser p = new CombineExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("varInt<<=3");
    Optional<ASTExpression> r = p.parse_StringExpression("varInt<<=varInt");
    CombineExpressionsTypesCalculator calc = new CombineExpressionsTypesCalculator(scope);

    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(o.get())));


    assertTrue(r.isPresent());
    r.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(r.get())));
  }

  @Test
  public void logicalRightAssignmentTest() throws IOException{
    CombineExpressionsParser p = new CombineExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("varInt>>>=3");
    Optional<ASTExpression> r = p.parse_StringExpression("varInt>>>=varInt");
    CombineExpressionsTypesCalculator calc = new CombineExpressionsTypesCalculator(scope);

    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(o.get())));


    assertTrue(r.isPresent());
    r.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(r.get())));
  }

  @Test
  public void moduloAssignmentTest() throws IOException{
    CombineExpressionsParser p = new CombineExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("varInt%=3");
    Optional<ASTExpression> r = p.parse_StringExpression("varDouble%=5");
    Optional<ASTExpression> s = p.parse_StringExpression("varDouble%=12.7");
    Optional<ASTExpression> t = p.parse_StringExpression("varChar%=12.7");
    CombineExpressionsTypesCalculator calc = new CombineExpressionsTypesCalculator(scope);

    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(o.get())));


    assertTrue(r.isPresent());
    r.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(r.get())));


    assertTrue(s.isPresent());
    s.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(s.get())));


    //    assertTrue(t.isPresent());
    //    t.get().accept(calc);
    //    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(t.get())));
  }


  @Test
  public void combineOperatorsTest() throws IOException{
    CombineExpressionsParser p = new CombineExpressionsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("varInt+=varDouble-=7");
    Optional<ASTExpression> q = p.parse_StringExpression("varInt<<=varInt*=9");
    Optional<ASTExpression> r = p.parse_StringExpression("varInt>>>=varInt+=4");
    Optional<ASTExpression> s = p.parse_StringExpression("varInt%=varDouble-=3");
    Optional<ASTExpression> t = p.parse_StringExpression("varInt&=varInt|=3");
    Optional<ASTExpression> u = p.parse_StringExpression("varDouble=varInt^=8");
    Optional<ASTExpression> v = p.parse_StringExpression("varInt+=varDouble*=9.6");

    assertTrue(o.isPresent());
    assertTrue(q.isPresent());
    assertTrue(r.isPresent());
    assertTrue(s.isPresent());
    assertTrue(t.isPresent());
    assertTrue(u.isPresent());
    assertTrue(v.isPresent());

    CombineExpressionsTypesCalculator calc = new CombineExpressionsTypesCalculator(scope);

    o.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(o.get())));


    q.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(q.get())));


    r.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(r.get())));


    s.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(s.get())));


    t.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(t.get())));


    u.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(u.get())));


    v.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(v.get())));

  }

}
