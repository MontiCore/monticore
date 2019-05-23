package de.monticore.typescalculator;

import de.monticore.antlr4.MCConcreteParser;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._symboltable.EMethodSymbol;
import de.monticore.expressions.expressionsbasis._symboltable.EVariableSymbol;
import de.monticore.expressions.expressionsbasis._symboltable.ExpressionsBasisLanguage;
import de.monticore.expressions.expressionsbasis._symboltable.ExpressionsBasisScope;
import de.monticore.symboltable.ArtifactScope;
import de.monticore.types.mcbasictypes._ast.ASTConstantsMCBasicTypes;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypes._ast.MCBasicTypesMill;
import de.monticore.types.mcbasictypes._symboltable.MCTypeSymbol;
import de.monticore.typescalculator.combineexpressionswithliterals._parser.CombineExpressionsWithLiteralsParser;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class CombineExpressionsWithLiteralsTest {

  private ExpressionsBasisScope scope;

  private ArtifactScope artifactScope;

  @Before
  public void setup(){
    ExpressionsBasisLanguage expressionsBasisLanguage=new ExpressionsBasisLanguage("CombineExpressionsWithLiteralsWithLiterals","exp") {
      @Override
      public MCConcreteParser getParser() {
        return new CombineExpressionsWithLiteralsParser();
      }
    };
    Log.enableFailQuick(false);

    this.scope=new ExpressionsBasisScope();
    this.artifactScope=new ArtifactScope("",new ArrayList<>());
    artifactScope.addSubScope(scope);
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


    symbol = new EVariableSymbol("QName");
    name=new ArrayList<>();
    typeSymbol= new MCTypeSymbol("QName");
    type = MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(name).build()).build();
    typeSymbol.setASTMCType(type);
    typeSymbol.setEVariableSymbol(symbol);
    symbol.setMCTypeSymbol(typeSymbol);
    cscope.add(symbol);
  }

  @Test
  public void testCommonExpressions() throws IOException {
    CombineExpressionsWithLiteralsParser parser = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> p = parser.parse_StringExpression("3+4");

    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);
    assertTrue(p.isPresent());
    p.get().accept(calc.getRealThis());

    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(p.get())));

  }

  @Test
  public void testAssignmentExpressions() throws IOException{
    CombineExpressionsWithLiteralsParser parser = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> p = parser.parse_StringExpression("varInt-=4");

    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);
    assertTrue(p.isPresent());
    p.get().accept(calc.getRealThis());

    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(p.get())));
  }

  @Test
  public void testCombination() throws IOException{
    CombineExpressionsWithLiteralsParser parser = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> p = parser.parse_StringExpression("varDouble+=3+4");

    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);
    assertTrue(p.isPresent());
    p.get().accept(calc.getRealThis());

    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(p.get())));
  }
  
  /*
        ###################################ASSIGNMENT EXPRESSIONS######################################################
   */

  @Test
  public void incSuffixTest() throws IOException{
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);
    Optional<ASTExpression> o = p.parse_StringExpression("4++");
    Optional<ASTExpression> r = p.parse_StringExpression("7++");

    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(o.get())));


    assertTrue(r.isPresent());
    r.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(r.get())));

  }

  @Test
  public void decSuffixTest() throws IOException{
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);
    Optional<ASTExpression> o = p.parse_StringExpression("9--");
    Optional<ASTExpression> r = p.parse_StringExpression("34--");

    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(o.get())));


    assertTrue(r.isPresent());
    r.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(r.get())));

  }

  @Test
  public void incPrefixTest() throws IOException{
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);
    Optional<ASTExpression> o = p.parse_StringExpression("++2");
    Optional<ASTExpression> r = p.parse_StringExpression("++23");

    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(o.get())));


    assertTrue(r.isPresent());
    r.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(r.get())));

  }

  @Test
  public void decPrefixTest() throws IOException{
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);
    Optional<ASTExpression> o = p.parse_StringExpression("--28");
    Optional<ASTExpression> r = p.parse_StringExpression("--12");

    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(o.get())));


    assertTrue(r.isPresent());
    r.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(r.get())));

  }

  @Test
  public void plusPrefixTest() throws IOException{
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);
    Optional<ASTExpression> o = p.parse_StringExpression("+4");
    Optional<ASTExpression> r = p.parse_StringExpression("+52");

    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(o.get())));


    assertTrue(r.isPresent());
    r.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(r.get())));

  }

  @Test
  public void minusPrefixTest() throws IOException{
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);
    Optional<ASTExpression> o = p.parse_StringExpression("-varInt");
    Optional<ASTExpression> r = p.parse_StringExpression("-96");

    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(o.get())));


    assertTrue(r.isPresent());
    r.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(r.get())));

  }

  @Test
  public void plusAssignmentTest() throws IOException{
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("varInt+=3");
    Optional<ASTExpression> r = p.parse_StringExpression("varDouble+=5");
    Optional<ASTExpression> s = p.parse_StringExpression("varDouble+=12.7");
    Optional<ASTExpression> t = p.parse_StringExpression("varChar+=12");
    Optional<ASTExpression> u = p.parse_StringExpression("varString+=varString");
    List<String> name = new ArrayList<>();
    name.add("java");
    name.add("lang");
    name.add("String");
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);

    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertEquals(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().getBaseName(), calc.getResult().getBaseName());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(o.get())));


    assertTrue(r.isPresent());
    r.get().accept(calc);
    assertEquals(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().getBaseName(), calc.getResult().getBaseName());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(r.get())));


    assertTrue(s.isPresent());
    s.get().accept(calc);
    assertEquals(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().getBaseName(), calc.getResult().getBaseName());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(s.get())));


    //    assertTrue(t.isPresent());
    //    t.get().accept(calc);
    //    assertEquals(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build(), calc.getResult());
    //    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(t.get())));


    assertTrue(u.isPresent());
    u.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(name).build()).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(name).build()).build().deepEquals(calc.calculateType(u.get())));


  }

  @Test
  public void minusAssignmentTest() throws IOException{
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("varInt-=3");
    Optional<ASTExpression> r = p.parse_StringExpression("varDouble-=5");
    Optional<ASTExpression> s = p.parse_StringExpression("varDouble-=12.7");
    Optional<ASTExpression> t = p.parse_StringExpression("varChar-=12");
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);

    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertEquals(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().getBaseName(), calc.getResult().getBaseName());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(o.get())));


    assertTrue(r.isPresent());
    r.get().accept(calc);
    assertEquals(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().getBaseName(), calc.getResult().getBaseName());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(r.get())));


    assertTrue(s.isPresent());
    s.get().accept(calc);
    assertEquals(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().getBaseName(), calc.getResult().getBaseName());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(s.get())));


    //    assertTrue(t.isPresent());
    //    t.get().accept(calc);
    //    assertEquals(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build(), calc.getResult());
    //    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(t.get())));

  }

  @Test
  public void multAssignmentTest() throws IOException{
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("varInt*=3");
    Optional<ASTExpression> r = p.parse_StringExpression("varDouble*=5");
    Optional<ASTExpression> s = p.parse_StringExpression("varDouble*=12.7");
    Optional<ASTExpression> t = p.parse_StringExpression("varChar*=12.7");
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);

    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertEquals(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().getBaseName(), calc.getResult().getBaseName());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(o.get())));


    assertTrue(r.isPresent());
    r.get().accept(calc);
    assertEquals(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().getBaseName(), calc.getResult().getBaseName());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(r.get())));


    assertTrue(s.isPresent());
    s.get().accept(calc);
    assertEquals(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().getBaseName(), calc.getResult().getBaseName());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(s.get())));


    //    assertTrue(t.isPresent());
    //    t.get().accept(calc);
    //    assertEquals(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build(), calc.getResult());
    //    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(t.get())));
  }

  @Test
  public void divideAssignmentTest() throws IOException{
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("varInt/=3");
    Optional<ASTExpression> r = p.parse_StringExpression("varDouble/=5");
    Optional<ASTExpression> s = p.parse_StringExpression("varDouble/=12.7");
    Optional<ASTExpression> t = p.parse_StringExpression("varChar/=12.7");
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);

    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertEquals(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().getBaseName(), calc.getResult().getBaseName());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(o.get())));


    assertTrue(r.isPresent());
    r.get().accept(calc);
    assertEquals(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().getBaseName(), calc.getResult().getBaseName());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(r.get())));


    assertTrue(s.isPresent());
    s.get().accept(calc);
    assertEquals(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().getBaseName(), calc.getResult().getBaseName());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(s.get())));


    //    assertTrue(t.isPresent());
    //    t.get().accept(calc);
    //    assertEquals(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build(), calc.getResult());
    //    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(t.get())));
  }

  @Test
  public void regularAssignmentTest() throws IOException{
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("varInt=3");
    Optional<ASTExpression> r = p.parse_StringExpression("varDouble=varInt");
    Optional<ASTExpression> s = p.parse_StringExpression("varDouble=12.7");
    Optional<ASTExpression> t = p.parse_StringExpression("varChar=12");
    Optional<ASTExpression> u = p.parse_StringExpression("varString=varString");
    Optional<ASTExpression> v = p.parse_StringExpression("varA=varB");
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);
    List<String> name = new ArrayList<>();
    name.add("java");
    name.add("lang");
    name.add("String");

    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertEquals(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().getBaseName(), calc.getResult().getBaseName());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(o.get())));


    assertTrue(r.isPresent());
    r.get().accept(calc);
    assertEquals(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().getBaseName(), calc.getResult().getBaseName());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(r.get())));


    assertTrue(s.isPresent());
    s.get().accept(calc);
    assertEquals(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().getBaseName(), calc.getResult().getBaseName());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(s.get())));


    //    assertTrue(t.isPresent());
    //    t.get().accept(calc);
    //    assertEquals(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.CHAR).build(), calc.getResult());
    //    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.CHAR).build().deepEquals(calc.calculateType(t.get())));


    assertTrue(u.isPresent());
    u.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(name).build()).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(name).build()).build().deepEquals(calc.calculateType(u.get())));


    name=new ArrayList<>();
    name.add("A");

    assertTrue(v.isPresent());
    v.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(name).build()).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(name).build()).build().deepEquals(calc.calculateType(v.get())));

  }

  @Test
  public void andAssignmentTest() throws IOException{
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("varInt&=3");
    Optional<ASTExpression> r = p.parse_StringExpression("varInt&=varInt");
    Optional<ASTExpression> q = p.parse_StringExpression("varBool&=false");
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);

    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(o.get())));


    assertTrue(r.isPresent());
    r.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(r.get())));


    assertTrue(q.isPresent());
    q.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build().deepEquals(calc.calculateType(q.get())));

  }

  @Test
  public void orAssignmentTest() throws IOException{
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("varInt|=3");
    Optional<ASTExpression> r = p.parse_StringExpression("varInt|=varInt");
    Optional<ASTExpression> q = p.parse_StringExpression("varBool|=false");
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);

    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(o.get())));


    assertTrue(r.isPresent());
    r.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(r.get())));


    assertTrue(q.isPresent());
    q.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build().deepEquals(calc.calculateType(q.get())));
  }

  @Test
  public void binaryXorAssignmentTest() throws IOException{
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("varInt^=3");
    Optional<ASTExpression> r = p.parse_StringExpression("varInt^=varInt");
    Optional<ASTExpression> q = p.parse_StringExpression("varBool^=varBool2");
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);

    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(o.get())));


    assertTrue(r.isPresent());
    r.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(r.get())));


    assertTrue(q.isPresent());
    q.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build().deepEquals(calc.calculateType(q.get())));
  }

  @Test
  public void rightShiftAssignmentTest() throws IOException{
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("varInt>>=3");
    Optional<ASTExpression> r = p.parse_StringExpression("varInt>>=varInt");
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);

    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(o.get())));


    assertTrue(r.isPresent());
    r.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(r.get())));


  }

  @Test
  public void leftShiftAssignmentTest() throws IOException{
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("varInt<<=3");
    Optional<ASTExpression> r = p.parse_StringExpression("varInt<<=varInt");
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);

    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(o.get())));


    assertTrue(r.isPresent());
    r.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(r.get())));
  }

  @Test
  public void logicalRightAssignmentTest() throws IOException{
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("varInt>>>=3");
    Optional<ASTExpression> r = p.parse_StringExpression("varInt>>>=varInt");
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);

    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(o.get())));


    assertTrue(r.isPresent());
    r.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(r.get())));
  }

  @Test
  public void moduloAssignmentTest() throws IOException{
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("varInt%=3");
    Optional<ASTExpression> r = p.parse_StringExpression("varDouble%=5");
    Optional<ASTExpression> s = p.parse_StringExpression("varDouble%=12.7");
    Optional<ASTExpression> t = p.parse_StringExpression("varChar%=12.7");
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);

    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertEquals(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().getBaseName(), calc.getResult().getBaseName());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(o.get())));


    assertTrue(r.isPresent());
    r.get().accept(calc);
    assertEquals(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().getBaseName(), calc.getResult().getBaseName());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(r.get())));


    assertTrue(s.isPresent());
    s.get().accept(calc);
    assertEquals(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().getBaseName(), calc.getResult().getBaseName());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(s.get())));


    //    assertTrue(t.isPresent());
    //    t.get().accept(calc);
    //    assertEquals(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build(), calc.getResult());
    //    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(t.get())));
  }


  @Test
  public void combineOperatorsTest() throws IOException{
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
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

    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);

    o.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(o.get())));


    q.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(q.get())));


    r.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(r.get())));


    s.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(s.get())));


    t.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(t.get())));


    u.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(u.get())));


    v.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(v.get())));

  }
  
  /*
      ############################################COMMON EXPRESSIONS################################################################
   */

  @Test
  public void moduloIntVisitorTest() throws IOException{
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("9%7");
    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(o.get())));
  }

  @Test
  public void plusIntVisitorTest() throws IOException{
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("9+7");
    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(o.get())));
  }

  @Test
  public void multIntVisitorTest() throws IOException{
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("9*7");
    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(o.get())));
  }

  @Test
  public void divideIntVisitorTest() throws IOException{
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("9/7");
    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(o.get())));
  }

  @Test
  public void minusIntVisitorTest() throws IOException{
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("9-7");
    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(o.get())));
  }

  @Test
  public void plusDoubleVisitorTest() throws IOException{
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("9.13+7.73");
    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(o.get())));
  }

  @Test
  public void moduloDoubleVisitorTest() throws IOException{
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("9.13%7.73");
    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(o.get())));
  }

  @Test
  public void multDoubleVisitorTest() throws IOException{
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("9.13*7.73");
    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(o.get())));
  }

  @Test
  public void divideDoubleVisitorTest() throws IOException{
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("9.13/7.73");
    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(o.get())));
  }

  @Test
  public void minusDoubleVisitorTest() throws IOException{
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("9.13-7.73");
    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(o.get())));
  }

  @Test
  public void plusIntDoubleVisitorTest() throws IOException{
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("9+7.73");
    Optional<ASTExpression> q = p.parse_StringExpression("7.4+3");

    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(o.get())));

    assertTrue(q.isPresent());
    q.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(q.get())));
  }

  @Test
  public void moduloIntDoubleVisitorTest() throws IOException{
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("9%7.73");
    Optional<ASTExpression> q = p.parse_StringExpression("7.4%3");

    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(o.get())));

    assertTrue(q.isPresent());
    q.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(q.get())));
  }

  @Test
  public void multIntDoubleVisitorTest() throws IOException{
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("9*7.73");
    Optional<ASTExpression> q = p.parse_StringExpression("7.4*3");

    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(o.get())));

    assertTrue(q.isPresent());
    q.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(q.get())));
  }

  @Test
  public void divideIntDoubleVisitorTest() throws IOException{
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("9/7.73");
    Optional<ASTExpression> q = p.parse_StringExpression("7.4/3");

    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(o.get())));

    assertTrue(q.isPresent());
    q.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(q.get())));
  }

  @Test
  public void minusIntDoubleVisitorTest() throws IOException{
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("9-7.73");
    Optional<ASTExpression> q = p.parse_StringExpression("7.4-3");

    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(o.get())));

    assertTrue(q.isPresent());
    q.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(q.get())));
  }

  @Test
  public void lessThanTest() throws IOException{
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("4<7");
    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build().deepEquals(calc.calculateType(o.get())));
  }

  @Test
  public void greaterThanTest() throws IOException{
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("15>9.2");
    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build().deepEquals(calc.calculateType(o.get())));
  }

  @Test
  public void lessEqualTest() throws IOException{
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("4<=7");
    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build().deepEquals(calc.calculateType(o.get())));
  }

  @Test
  public void greaterEqualTest() throws IOException{
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("4>=2.7");
    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build().deepEquals(calc.calculateType(o.get())));
  }

  @Test
  public void logicalNotTest() throws IOException{
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("!true");
    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build().deepEquals(calc.calculateType(o.get())));
  }

  @Test
  public void booleanOrOpTest() throws IOException{
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("true||false");
    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build().deepEquals(calc.calculateType(o.get())));
  }

  @Test
  public void booleanAndOpTest() throws IOException{
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("true&&true");
    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build().deepEquals(calc.calculateType(o.get())));
  }

  @Test
  public void notEqualsTest() throws IOException{
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("3!=4");
    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build().deepEquals(calc.calculateType(o.get())));
  }

  @Test
  public void equalsTest() throws IOException{
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("7==7");
    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build().deepEquals(calc.calculateType(o.get())));
  }

  @Test
  public void bracketTest() throws IOException{
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("(7+8)");
    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(o.get())));

    p = new CombineExpressionsWithLiteralsParser();
    o = p.parse_StringExpression("(7-2.5)");
    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(o.get())));
  }

  @Test
  public void simpleAssignmentTest() throws IOException{
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("7+=9");
    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(o.get())));

    Optional<ASTExpression> r = p.parse_StringExpression("7.4+=9");
    assertTrue(r.isPresent());
    r.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(r.get())));
  }

  @Test
  public void conditionalTest() throws IOException{
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("7<3? 7 : 3");
    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(o.get())));

    Optional<ASTExpression> r = p.parse_StringExpression("7.2<3? 7.2 : 3");
    assertTrue(r.isPresent());
    r.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(r.get())));
  }

  @Test
  public void booleanNotTest() throws IOException{
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("~7");
    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(o.get())));
  }

  @Test
  public void combineOperationsTest() throws IOException{
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("9*7.4-3/2+4");
    Optional<ASTExpression> q = p.parse_StringExpression("9*4+3-5/2");
    Optional<ASTExpression> r = p.parse_StringExpression("3<4");
    Optional<ASTExpression> s = p.parse_StringExpression("(4<6)&&true");
    Optional<ASTExpression> t = p.parse_StringExpression("~3<4? ~3 : 4");
    Optional<ASTExpression> u = p.parse_StringExpression("9*(7.2+8)");
    Optional<ASTExpression> v = p.parse_StringExpression("false&&true");

    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(o.get())));

    assertTrue(q.isPresent());
    q.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(q.get())));

    assertTrue(r.isPresent());
    r.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build().deepEquals(calc.calculateType(r.get())));

    assertTrue(s.isPresent());
    s.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build().deepEquals(calc.calculateType(s.get())));

    assertTrue(t.isPresent());
    t.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(t.get())));

    assertTrue(u.isPresent());
    u.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(u.get())));

    assertTrue(v.isPresent());
    v.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build().deepEquals(calc.calculateType(v.get())));
  }

  @Test
  public void callTest() throws IOException{
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("call()");

    assertTrue(o.isPresent());
    ASTExpression expr = o.get();

    expr.accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(o.get())));

    Optional<ASTExpression> q = p.parse_StringExpression("A.B.C.call()");

    assertTrue(q.isPresent());
    expr = q.get();

    expr.accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(q.get())));
  }

  @Test
  public void callStringTest() throws IOException{
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("(\"3\"+\"7\").toString()");

    assertTrue(o.isPresent());
    ASTExpression expr = o.get();

    expr.accept(calc);
  }
  
  /*
      ################################################EXPRESSIONS BASIS#################################################################
   */

  @Test
  public void nameTest() throws IOException {
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("varInt");

    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(o.get())));

  }

  @Test
  public void qualifiedNameTest() throws IOException{
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("A.B.C.QName");


    assertTrue(o.isPresent());
    o.get().accept(calc);
    List<String> nameList = new ArrayList<>();
    nameList.add("A");
    nameList.add("B");
    nameList.add("C");
    nameList.add("QName");
    assertTrue(MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(nameList).build()).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(nameList).build()).build().deepEquals(calc.calculateType(o.get())));

  }

  @Test
  public void literalTest() throws IOException{
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("3");

    assertTrue(o.isPresent());
    o.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(o.get())));


    Optional<ASTExpression> q = p.parse_StringExpression("true");

    assertTrue(q.isPresent());
    q.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build().deepEquals(calc.calculateType(q.get())));


    Optional<ASTExpression> r = p.parse_StringExpression("4.5");
    assertTrue(r.isPresent());
    r.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(r.get())));

    List<String> nameList = new ArrayList<>();
    nameList.add("String");

    Optional<ASTExpression> s = p.parse_StringExpression("\"Hallo\"");
    assertTrue(MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(nameList).build()).build().deepEquals(calc.getResult()));
    assertTrue(MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(nameList).build()).build().deepEquals(calc.calculateType(s.get())));

  }
}
