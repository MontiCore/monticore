package de.monticore.typescalculator;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._symboltable.EMethodSymbol;
import de.monticore.expressions.expressionsbasis._symboltable.EVariableSymbol;
import de.monticore.expressions.expressionsbasis._symboltable.ExpressionsBasisScope;
import de.monticore.expressions.expressionsbasis._symboltable.ExpressionsBasisSymTabMill;
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

import static org.junit.Assert.assertTrue;

public class AssignmentExpressionsTest {

  private ExpressionsBasisScope scope;

  @Before
  public void setup(){
    Log.enableFailQuick(false);

    this.scope=ExpressionsBasisSymTabMill.expressionsBasisScopeBuilder().build();

    EVariableSymbol symbol = ExpressionsBasisSymTabMill.eVariableSymbolBuilder().setName("varInt").build();
    MCTypeSymbol typeSymbol = new MCTypeSymbol("int");
    typeSymbol.setASTMCType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build());
    typeSymbol.setEVariableSymbol(symbol);
    symbol.setMCTypeSymbol(typeSymbol);
    scope.add(symbol);

    symbol = ExpressionsBasisSymTabMill.eVariableSymbolBuilder().setName("varDouble").build();
    typeSymbol = new MCTypeSymbol("double");
    typeSymbol.setASTMCType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build());
    typeSymbol.setEVariableSymbol(symbol);
    symbol.setMCTypeSymbol(typeSymbol);
    scope.add(symbol);

    symbol = ExpressionsBasisSymTabMill.eVariableSymbolBuilder().setName("varString").build();
    List<String> name = new ArrayList<>();
    name.add("java");
    name.add("lang");
    name.add("String");
    typeSymbol = new MCTypeSymbol("java.lang.String");
    typeSymbol.setEVariableSymbol(symbol);
    typeSymbol.setASTMCType(MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(name).build()).build());
    symbol.setMCTypeSymbol(typeSymbol);
    scope.add(symbol);

    symbol = ExpressionsBasisSymTabMill.eVariableSymbolBuilder().setName("varList").build();
    name = new ArrayList<>();
    name.add("java");
    name.add("util");
    name.add("List");
    typeSymbol = new MCTypeSymbol("java.lang.String");
    typeSymbol.setASTMCType(MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(name).build()).build());
    typeSymbol.setEVariableSymbol(symbol);
    symbol.setMCTypeSymbol(typeSymbol);
    scope.add(symbol);

    symbol = ExpressionsBasisSymTabMill.eVariableSymbolBuilder().setName("varChar").build();
    typeSymbol= new MCTypeSymbol("char");
    typeSymbol.setASTMCType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.CHAR).build());
    typeSymbol.setEVariableSymbol(symbol);
    symbol.setMCTypeSymbol(typeSymbol);
    scope.add(symbol);

    symbol = ExpressionsBasisSymTabMill.eVariableSymbolBuilder().setName("varInteger").build();
    name=new ArrayList<>();
    name.add("java");
    name.add("lang");
    name.add("Integer");
    typeSymbol=new MCTypeSymbol("java.lang.Integer");
    typeSymbol.setASTMCType(MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(name).build()).build());
    typeSymbol.setEVariableSymbol(symbol);
    symbol.setMCTypeSymbol(typeSymbol);
    scope.add(symbol);

    EVariableSymbol symbolB = ExpressionsBasisSymTabMill.eVariableSymbolBuilder().setName("varB").build();
    name=new ArrayList<>();
    name.add("B");
    typeSymbol=new MCTypeSymbol("B");
    typeSymbol.setEVariableSymbol(symbolB);
    typeSymbol.setASTMCType(MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(name).build()).build());
    symbolB.setMCTypeSymbol(typeSymbol);

    symbol = ExpressionsBasisSymTabMill.eVariableSymbolBuilder().setName("varA").build();
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

    symbol = ExpressionsBasisSymTabMill.eVariableSymbolBuilder().setName("varName").build();
    name=new ArrayList<>();
    name.add("Test");
    typeSymbol= new MCTypeSymbol("Name");
    ASTMCType type = MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(name).build()).build();
    typeSymbol.setASTMCType(type);
    typeSymbol.setEVariableSymbol(symbol);
    symbol.setMCTypeSymbol(typeSymbol);
    scope.add(symbol);

    EMethodSymbol methodSymbol = ExpressionsBasisSymTabMill.eMethodSymbolBuilder().setName("call").build();
    typeSymbol = new MCTypeSymbol("call");
    typeSymbol.setMethodSymbol(methodSymbol);
    methodSymbol.setMCTypeSymbol(typeSymbol);
    methodSymbol.setReturnType(MCBasicTypesMill.mCReturnTypeBuilder().setMCType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build()).build());
    scope.add(methodSymbol);

    ExpressionsBasisScope ascope = ExpressionsBasisSymTabMill.expressionsBasisScopeBuilder().build();
    scope.addSubScope(ascope);
    ascope.setName("A");
    ExpressionsBasisScope bscope = ExpressionsBasisSymTabMill.expressionsBasisScopeBuilder().build();
    bscope.setName("B");
    ascope.addSubScope(bscope);
    ExpressionsBasisScope cscope = ExpressionsBasisSymTabMill.expressionsBasisScopeBuilder().build();
    cscope.setName("C");
    bscope.addSubScope(cscope);

    cscope.add(methodSymbol);


    symbol = ExpressionsBasisSymTabMill.eVariableSymbolBuilder().setName("QName").build();
    name=new ArrayList<>();
    typeSymbol= new MCTypeSymbol("QName");
    type = MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(name).build()).build();
    typeSymbol.setASTMCType(type);
    typeSymbol.setEVariableSymbol(symbol);
    symbol.setMCTypeSymbol(typeSymbol);
    cscope.add(symbol);

    symbol = ExpressionsBasisSymTabMill.eVariableSymbolBuilder().setName("varBool").build();
    typeSymbol=new MCTypeSymbol("boolean");
    typeSymbol.setEVariableSymbol(symbol);
    typeSymbol.setASTMCType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build());
    symbol.setMCTypeSymbol(typeSymbol);
    scope.add(symbol);

    symbol = ExpressionsBasisSymTabMill.eVariableSymbolBuilder().setName("varBool2").build();
    typeSymbol=new MCTypeSymbol("boolean");
    typeSymbol.setEVariableSymbol(symbol);
    typeSymbol.setASTMCType(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build());
    symbol.setMCTypeSymbol(typeSymbol);
    scope.add(symbol);
  }

  @Test
  public void incSuffixTest() throws IOException{
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);
    Optional<ASTExpression> o = p.parse_StringExpression("4++");
    Optional<ASTExpression> r = p.parse_StringExpression("7.3++");
    Optional<ASTExpression> q = p.parse_StringExpression("8l++");
    Optional<ASTExpression> s = p.parse_StringExpression("\'a\'++");
    Optional<ASTExpression> t = p.parse_StringExpression("4.5f++");

    assertTrue(o.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(o.get())));


    assertTrue(r.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(r.get())));

    assertTrue(q.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.LONG).build().deepEquals(calc.calculateType(q.get())));

    assertTrue(s.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(s.get())));

    assertTrue(t.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.FLOAT).build().deepEquals(calc.calculateType(t.get())));
  }

  @Test
  public void decSuffixTest() throws IOException{
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);
    Optional<ASTExpression> o = p.parse_StringExpression("4--");
    Optional<ASTExpression> r = p.parse_StringExpression("7.3--");
    Optional<ASTExpression> q = p.parse_StringExpression("8l--");
    Optional<ASTExpression> s = p.parse_StringExpression("\'a\'--");
    Optional<ASTExpression> t = p.parse_StringExpression("4.5f--");

    assertTrue(o.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(o.get())));


    assertTrue(r.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(r.get())));

    assertTrue(q.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.LONG).build().deepEquals(calc.calculateType(q.get())));

    assertTrue(s.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(s.get())));

    assertTrue(t.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.FLOAT).build().deepEquals(calc.calculateType(t.get())));

  }

  @Test
  public void incPrefixTest() throws IOException{
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);
    Optional<ASTExpression> o = p.parse_StringExpression("++4");
    Optional<ASTExpression> r = p.parse_StringExpression("++7.3");
    Optional<ASTExpression> q = p.parse_StringExpression("++8l");
    Optional<ASTExpression> s = p.parse_StringExpression("++\'a\'");
    Optional<ASTExpression> t = p.parse_StringExpression("++4.5f");

    assertTrue(o.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(o.get())));


    assertTrue(r.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(r.get())));

    assertTrue(q.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.LONG).build().deepEquals(calc.calculateType(q.get())));

    assertTrue(s.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(s.get())));

    assertTrue(t.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.FLOAT).build().deepEquals(calc.calculateType(t.get())));

  }

  @Test
  public void decPrefixTest() throws IOException{
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);
    Optional<ASTExpression> o = p.parse_StringExpression("--4");
    Optional<ASTExpression> r = p.parse_StringExpression("--7.3");
    Optional<ASTExpression> q = p.parse_StringExpression("--8l");
    Optional<ASTExpression> s = p.parse_StringExpression("--\'a\'");
    Optional<ASTExpression> t = p.parse_StringExpression("--4.5f");

    assertTrue(o.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(o.get())));


    assertTrue(r.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(r.get())));

    assertTrue(q.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.LONG).build().deepEquals(calc.calculateType(q.get())));

    assertTrue(s.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(s.get())));

    assertTrue(t.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.FLOAT).build().deepEquals(calc.calculateType(t.get())));

  }

  @Test
  public void plusPrefixTest() throws IOException{
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);
    Optional<ASTExpression> o = p.parse_StringExpression("+4");
    Optional<ASTExpression> r = p.parse_StringExpression("+7.3");
    Optional<ASTExpression> q = p.parse_StringExpression("+8l");
    Optional<ASTExpression> s = p.parse_StringExpression("+\'a\'");
    Optional<ASTExpression> t = p.parse_StringExpression("+4.5f");

    assertTrue(o.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(o.get())));


    assertTrue(r.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(r.get())));

    assertTrue(q.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.LONG).build().deepEquals(calc.calculateType(q.get())));

    assertTrue(s.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(s.get())));

    assertTrue(t.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.FLOAT).build().deepEquals(calc.calculateType(t.get())));

  }

  @Test
  public void minusPrefixTest() throws IOException{
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);
    Optional<ASTExpression> o = p.parse_StringExpression("-varInt");
    Optional<ASTExpression> r = p.parse_StringExpression("-7.3");
    Optional<ASTExpression> q = p.parse_StringExpression("-8l");
    Optional<ASTExpression> s = p.parse_StringExpression("-\'a\'");
    Optional<ASTExpression> t = p.parse_StringExpression("-4.5f");

    assertTrue(o.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(o.get())));


    assertTrue(r.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(r.get())));

    assertTrue(q.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.LONG).build().deepEquals(calc.calculateType(q.get())));

    assertTrue(s.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(s.get())));

    assertTrue(t.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.FLOAT).build().deepEquals(calc.calculateType(t.get())));

  }

  @Test
  public void plusAssignmentTest() throws IOException{
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("varInt+=3");
    Optional<ASTExpression> r = p.parse_StringExpression("varDouble+=5");
    Optional<ASTExpression> s = p.parse_StringExpression("varDouble+=12.7");
    Optional<ASTExpression> t = p.parse_StringExpression("varChar+=12");
    Optional<ASTExpression> u = p.parse_StringExpression("varString+=varString");
    Optional<ASTExpression> v = p.parse_StringExpression("varChar+=13.3f");
    Optional<ASTExpression> w = p.parse_StringExpression("varDouble+=3L");
    Optional<ASTExpression> x = p.parse_StringExpression("varString+=13.2");
    List<String> name = new ArrayList<>();
    name.add("java");
    name.add("lang");
    name.add("String");
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);

    assertTrue(o.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(o.get())));


    assertTrue(r.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(r.get())));


    assertTrue(s.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(s.get())));


    assertTrue(t.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.CHAR).build().deepEquals(calc.calculateType(t.get())));


    assertTrue(u.isPresent());
    assertTrue(MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(name).build()).build().deepEquals(calc.calculateType(u.get())));

    assertTrue(v.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.CHAR).build().deepEquals(calc.calculateType(v.get())));

    assertTrue(w.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(w.get())));

    name.remove("java");
    name.remove("lang");
    assertTrue(x.isPresent());
    assertTrue(MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(name).build()).build().deepEquals(calc.calculateType(x.get())));

  }

  @Test
  public void minusAssignmentTest() throws IOException{
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("varInt-=3");
    Optional<ASTExpression> r = p.parse_StringExpression("varDouble-=5");
    Optional<ASTExpression> s = p.parse_StringExpression("varDouble-=12.7");
    Optional<ASTExpression> t = p.parse_StringExpression("varChar-=12");
    Optional<ASTExpression> v = p.parse_StringExpression("varChar-=13.3f");
    Optional<ASTExpression> w = p.parse_StringExpression("varDouble-=3L");
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);

    assertTrue(o.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(o.get())));


    assertTrue(r.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(r.get())));


    assertTrue(s.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(s.get())));


    assertTrue(t.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.CHAR).build().deepEquals(calc.calculateType(t.get())));

    assertTrue(v.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.CHAR).build().deepEquals(calc.calculateType(v.get())));


    assertTrue(w.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(w.get())));

  }

  @Test
  public void multAssignmentTest() throws IOException{
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("varInt*=3");
    Optional<ASTExpression> r = p.parse_StringExpression("varDouble*=5");
    Optional<ASTExpression> s = p.parse_StringExpression("varDouble*=12.7");
    Optional<ASTExpression> t = p.parse_StringExpression("varChar*=12.7");
    Optional<ASTExpression> v = p.parse_StringExpression("varChar*=13.3f");
    Optional<ASTExpression> w = p.parse_StringExpression("varDouble*=3L");
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);

    assertTrue(o.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(o.get())));


    assertTrue(r.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(r.get())));


    assertTrue(s.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(s.get())));


    assertTrue(t.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.CHAR).build().deepEquals(calc.calculateType(t.get())));

    assertTrue(v.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.CHAR).build().deepEquals(calc.calculateType(v.get())));


    assertTrue(w.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(w.get())));
  }

  @Test
  public void divideAssignmentTest() throws IOException{
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("varInt/=3");
    Optional<ASTExpression> r = p.parse_StringExpression("varDouble/=5");
    Optional<ASTExpression> s = p.parse_StringExpression("varDouble/=12.7");
    Optional<ASTExpression> t = p.parse_StringExpression("varChar/=12.7");
    Optional<ASTExpression> v = p.parse_StringExpression("varChar/=13.3f");
    Optional<ASTExpression> w = p.parse_StringExpression("varDouble/=3L");
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);

    assertTrue(o.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(o.get())));


    assertTrue(r.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(r.get())));


    assertTrue(s.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(s.get())));


    assertTrue(t.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.CHAR).build().deepEquals(calc.calculateType(t.get())));

    assertTrue(v.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.CHAR).build().deepEquals(calc.calculateType(v.get())));


    assertTrue(w.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(w.get())));
  }

  @Test
  public void regularAssignmentTest() throws IOException{
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("varInt=3.5f");
    Optional<ASTExpression> r = p.parse_StringExpression("varDouble=varInt");
    Optional<ASTExpression> s = p.parse_StringExpression("varDouble=12.7");
    Optional<ASTExpression> t = p.parse_StringExpression("varChar=12");
    Optional<ASTExpression> u = p.parse_StringExpression("varString=varString");
    Optional<ASTExpression> v = p.parse_StringExpression("varA=varB");
    Optional<ASTExpression> w = p.parse_StringExpression("varChar=\'a\'");
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);
    List<String> name = new ArrayList<>();
    name.add("java");
    name.add("lang");
    name.add("String");

    assertTrue(o.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(o.get())));


    assertTrue(r.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(r.get())));


    assertTrue(s.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(s.get())));


    assertTrue(t.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.CHAR).build().deepEquals(calc.calculateType(t.get())));


    assertTrue(u.isPresent());
    assertTrue(MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(name).build()).build().deepEquals(calc.calculateType(u.get())));


    name=new ArrayList<>();
    name.add("A");

    assertTrue(v.isPresent());
    assertTrue(MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(name).build()).build().deepEquals(calc.calculateType(v.get())));

    assertTrue(w.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.CHAR).build().deepEquals(calc.calculateType(w.get())));
  }

  @Test
  public void andAssignmentTest() throws IOException{
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("varInt&=3");
    Optional<ASTExpression> r = p.parse_StringExpression("varInt&=varInt");
    Optional<ASTExpression> q = p.parse_StringExpression("varBool&=false");
    Optional<ASTExpression> s = p.parse_StringExpression("varInt&=7L");
    Optional<ASTExpression> t = p.parse_StringExpression("varChar&=3L");
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);

    assertTrue(o.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(o.get())));


    assertTrue(r.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(r.get())));


    assertTrue(q.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build().deepEquals(calc.calculateType(q.get())));

    assertTrue(s.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(s.get())));

    assertTrue(t.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.CHAR).build().deepEquals(calc.calculateType(t.get())));

  }

  @Test
  public void orAssignmentTest() throws IOException{
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("varInt|=3");
    Optional<ASTExpression> r = p.parse_StringExpression("varInt|=varInt");
    Optional<ASTExpression> q = p.parse_StringExpression("varBool|=false");
    Optional<ASTExpression> s = p.parse_StringExpression("varInt|=7L");
    Optional<ASTExpression> t = p.parse_StringExpression("varChar|=3L");
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);

    assertTrue(o.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(o.get())));


    assertTrue(r.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(r.get())));


    assertTrue(q.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build().deepEquals(calc.calculateType(q.get())));

    assertTrue(s.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(s.get())));

    assertTrue(t.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.CHAR).build().deepEquals(calc.calculateType(t.get())));
  }

  @Test
  public void binaryXorAssignmentTest() throws IOException{
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("varInt^=3");
    Optional<ASTExpression> r = p.parse_StringExpression("varInt^=varInt");
    Optional<ASTExpression> q = p.parse_StringExpression("varBool^=varBool2");
    Optional<ASTExpression> s = p.parse_StringExpression("varInt^=7L");
    Optional<ASTExpression> t = p.parse_StringExpression("varChar^=3L");
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);

    assertTrue(o.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(o.get())));


    assertTrue(r.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(r.get())));


    assertTrue(q.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build().deepEquals(calc.calculateType(q.get())));

    assertTrue(s.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(s.get())));

    assertTrue(t.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.CHAR).build().deepEquals(calc.calculateType(t.get())));
  }

  @Test
  public void rightShiftAssignmentTest() throws IOException{
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("varInt>>=3");
    Optional<ASTExpression> r = p.parse_StringExpression("varInt>>=varInt");
    Optional<ASTExpression> s = p.parse_StringExpression("varInt>>=7L");
    Optional<ASTExpression> t = p.parse_StringExpression("varChar>>=3L");
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);

    assertTrue(o.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(o.get())));


    assertTrue(r.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(r.get())));

    assertTrue(s.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(s.get())));

    assertTrue(t.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.CHAR).build().deepEquals(calc.calculateType(t.get())));
  }

  @Test
  public void leftShiftAssignmentTest() throws IOException{
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("varInt<<=3");
    Optional<ASTExpression> r = p.parse_StringExpression("varInt<<=varInt");
    Optional<ASTExpression> s = p.parse_StringExpression("varInt<<=7L");
    Optional<ASTExpression> t = p.parse_StringExpression("varChar<<=3L");
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);

    assertTrue(o.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(o.get())));


    r.get().accept(calc);
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(r.get())));

    assertTrue(s.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(s.get())));

    assertTrue(t.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.CHAR).build().deepEquals(calc.calculateType(t.get())));
  }

  @Test
  public void logicalRightAssignmentTest() throws IOException{
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("varInt>>>=3");
    Optional<ASTExpression> r = p.parse_StringExpression("varInt>>>=varInt");
    Optional<ASTExpression> s = p.parse_StringExpression("varInt>>>=7L");
    Optional<ASTExpression> t = p.parse_StringExpression("varChar>>>=3L");
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);

    assertTrue(o.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(o.get())));


    assertTrue(r.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(r.get())));

    assertTrue(s.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(s.get())));

    assertTrue(t.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.CHAR).build().deepEquals(calc.calculateType(t.get())));
  }

  @Test
  public void moduloAssignmentTest() throws IOException{
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("varInt%=3");
    Optional<ASTExpression> r = p.parse_StringExpression("varDouble%=5");
    Optional<ASTExpression> s = p.parse_StringExpression("varDouble%=12.7");
    Optional<ASTExpression> t = p.parse_StringExpression("varChar%=12.7");
    Optional<ASTExpression> v = p.parse_StringExpression("varChar%=13.3f");
    Optional<ASTExpression> w = p.parse_StringExpression("varDouble%=3L");
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);

    assertTrue(o.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(o.get())));


    assertTrue(r.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(r.get())));


    assertTrue(s.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(s.get())));


    assertTrue(t.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.CHAR).build().deepEquals(calc.calculateType(t.get())));

    assertTrue(v.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.CHAR).build().deepEquals(calc.calculateType(v.get())));


    assertTrue(w.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(w.get())));
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

    assertTrue(o.isPresent());
    assertTrue(q.isPresent());
    assertTrue(r.isPresent());
    assertTrue(s.isPresent());
    assertTrue(t.isPresent());
    assertTrue(u.isPresent());
    assertTrue(v.isPresent());

    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);

    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(o.get())));


    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(q.get())));


    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(r.get())));


    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(s.get())));


    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(t.get())));


    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(u.get())));


    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(v.get())));

  }

}
