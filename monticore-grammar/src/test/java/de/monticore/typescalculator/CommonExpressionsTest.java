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
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertTrue;

public class CommonExpressionsTest {

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

    methodSymbol = new EMethodSymbol("test");
    
  }


  @Test
  public void plusTest() throws IOException{
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("9+7.73");
    Optional<ASTExpression> q = p.parse_StringExpression("7.4+3");

    assertTrue(o.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(o.get())));

    assertTrue(q.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(q.get())));

    Optional<ASTExpression> r = p.parse_StringExpression("9.13+7.73");
    assertTrue(r.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(r.get())));

    Optional<ASTExpression> s = p.parse_StringExpression("9+7");
    assertTrue(s.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(s.get())));

    Optional<ASTExpression> t = p.parse_StringExpression("9l+10.0f");
    assertTrue(t.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.FLOAT).build().deepEquals(calc.calculateType(t.get())));

    Optional<ASTExpression> u = p.parse_StringExpression("\'a\'+13.4");
    assertTrue(u.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(u.get())));

    Optional<ASTExpression> v = p.parse_StringExpression("\'a\'+\'b\'");
    assertTrue(v.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(v.get())));

    List<String> name = new ArrayList<>();
    name.add("String");
    Optional<ASTExpression> w = p.parse_StringExpression("\"Hallo\"+\" Welt\"");
    assertTrue(w.isPresent());
    assertTrue(MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(name).build()).build().deepEquals(calc.calculateType(w.get())));

    Optional<ASTExpression> x = p.parse_StringExpression("\"Hallo\"+4.3f");
    assertTrue(x.isPresent());
    assertTrue(MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(name).build()).build().deepEquals(calc.calculateType(x.get())));
  }

  @Test
  public void moduloTest() throws IOException{
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("9%7.73");
    Optional<ASTExpression> q = p.parse_StringExpression("7.4%3");

    assertTrue(o.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(o.get())));

    assertTrue(q.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(q.get())));

    Optional<ASTExpression> r = p.parse_StringExpression("9.13%7.73");
    assertTrue(r.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(r.get())));

    Optional<ASTExpression> s = p.parse_StringExpression("9%7");
    assertTrue(s.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(s.get())));

    Optional<ASTExpression> t = p.parse_StringExpression("9l%10.0f");
    assertTrue(t.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.FLOAT).build().deepEquals(calc.calculateType(t.get())));

    Optional<ASTExpression> u = p.parse_StringExpression("\'a\'%13.4");
    assertTrue(u.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(u.get())));

    Optional<ASTExpression> v = p.parse_StringExpression("\'a\'%\'b\'");
    assertTrue(v.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(v.get())));
  }

  @Test
  public void multTest() throws IOException{
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("9*7.73");
    Optional<ASTExpression> q = p.parse_StringExpression("7.4*3");

    assertTrue(o.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(o.get())));

    assertTrue(q.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(q.get())));

    Optional<ASTExpression> r = p.parse_StringExpression("9.13*7.73");
    assertTrue(r.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(r.get())));

    Optional<ASTExpression> s = p.parse_StringExpression("9*7");
    assertTrue(s.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(s.get())));

    Optional<ASTExpression> t = p.parse_StringExpression("9l*10.0f");
    assertTrue(t.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.FLOAT).build().deepEquals(calc.calculateType(t.get())));

    Optional<ASTExpression> u = p.parse_StringExpression("\'a\'*13.4");
    assertTrue(u.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(u.get())));

    Optional<ASTExpression> v = p.parse_StringExpression("\'a\'*\'b\'");
    assertTrue(v.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(v.get())));
  }

  @Test
  public void divideTest() throws IOException{
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("9/7.73");
    Optional<ASTExpression> q = p.parse_StringExpression("7.4/3");

    assertTrue(o.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(o.get())));

    assertTrue(q.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(q.get())));

    Optional<ASTExpression> r = p.parse_StringExpression("9.13/7.73");
    assertTrue(r.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(r.get())));

    Optional<ASTExpression> s = p.parse_StringExpression("9/7");
    assertTrue(s.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(s.get())));

    Optional<ASTExpression> t = p.parse_StringExpression("9l/10.0f");
    assertTrue(t.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.FLOAT).build().deepEquals(calc.calculateType(t.get())));

    Optional<ASTExpression> u = p.parse_StringExpression("\'a\'/13.4");
    assertTrue(u.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(u.get())));

    Optional<ASTExpression> v = p.parse_StringExpression("\'a\'/\'b\'");
    assertTrue(v.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(v.get())));
  }

  @Test
  public void minusTest() throws IOException{
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("9-7.73");
    Optional<ASTExpression> q = p.parse_StringExpression("7.4-3");

    assertTrue(o.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(o.get())));

    assertTrue(q.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(q.get())));

    Optional<ASTExpression> r = p.parse_StringExpression("9.13-7.73");
    assertTrue(r.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(r.get())));

    Optional<ASTExpression> s = p.parse_StringExpression("9-7");
    assertTrue(s.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(s.get())));

    Optional<ASTExpression> t = p.parse_StringExpression("9l-10.0f");
    assertTrue(t.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.FLOAT).build().deepEquals(calc.calculateType(t.get())));

    Optional<ASTExpression> u = p.parse_StringExpression("\'a\'-13.4");
    assertTrue(u.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(u.get())));

    Optional<ASTExpression> v = p.parse_StringExpression("\'a\'-\'b\'");
    assertTrue(v.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(v.get())));
  }

  @Test
  public void lessThanTest() throws IOException{
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("4<7");
    assertTrue(o.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build().deepEquals(calc.calculateType(o.get())));

    Optional<ASTExpression> r = p.parse_StringExpression("4.5f<7l");
    assertTrue(r.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build().deepEquals(calc.calculateType(r.get())));

    Optional<ASTExpression> s = p.parse_StringExpression("4.5<\'a\'");
    assertTrue(s.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build().deepEquals(calc.calculateType(s.get())));
  }

  @Test
  public void greaterThanTest() throws IOException{
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("15>9.2");
    assertTrue(o.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build().deepEquals(calc.calculateType(o.get())));

    Optional<ASTExpression> r = p.parse_StringExpression("4.5f>7l");
    assertTrue(r.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build().deepEquals(calc.calculateType(r.get())));

    Optional<ASTExpression> s = p.parse_StringExpression("4.5>\'a\'");
    assertTrue(s.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build().deepEquals(calc.calculateType(s.get())));
  }

  @Test
  public void lessEqualTest() throws IOException{
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("4<=7");
    assertTrue(o.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build().deepEquals(calc.calculateType(o.get())));

    Optional<ASTExpression> r = p.parse_StringExpression("4.5f<=7l");
    assertTrue(r.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build().deepEquals(calc.calculateType(r.get())));

    Optional<ASTExpression> s = p.parse_StringExpression("4.5<=\'a\'");
    assertTrue(s.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build().deepEquals(calc.calculateType(s.get())));
  }

  @Test
  public void greaterEqualTest() throws IOException{
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("4>=2.7");
    assertTrue(o.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build().deepEquals(calc.calculateType(o.get())));

    Optional<ASTExpression> r = p.parse_StringExpression("4.5f>=7l");
    assertTrue(r.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build().deepEquals(calc.calculateType(r.get())));

    Optional<ASTExpression> s = p.parse_StringExpression("4.5>=\'a\'");
    assertTrue(s.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build().deepEquals(calc.calculateType(s.get())));
  }

  @Test
  public void logicalNotTest() throws IOException{
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("!true");
    assertTrue(o.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build().deepEquals(calc.calculateType(o.get())));

    Optional<ASTExpression> r = p.parse_StringExpression("!(3<=7)");
    assertTrue(r.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build().deepEquals(calc.calculateType(r.get())));
  }

  @Test
  public void booleanOrOpTest() throws IOException{
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("true||false");
    assertTrue(o.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build().deepEquals(calc.calculateType(o.get())));

    Optional<ASTExpression> r = p.parse_StringExpression("(4>3)||varBool");
    assertTrue(r.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build().deepEquals(calc.calculateType(r.get())));


  }

  @Test
  public void booleanAndOpTest() throws IOException{
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("true&&true");
    assertTrue(o.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build().deepEquals(calc.calculateType(o.get())));

    Optional<ASTExpression> r = p.parse_StringExpression("(4>3)&&varBool");
    assertTrue(r.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build().deepEquals(calc.calculateType(r.get())));
  }

  @Test
  public void notEqualsTest() throws IOException{
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("3!=4");
    assertTrue(o.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build().deepEquals(calc.calculateType(o.get())));

    Optional<ASTExpression> r = p.parse_StringExpression("\"Hallo\"!=\"Welt\"");
    assertTrue(r.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build().deepEquals(calc.calculateType(r.get())));

    Optional<ASTExpression> q = p.parse_StringExpression("varList!=varList");
    assertTrue(q.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build().deepEquals(calc.calculateType(q.get())));

    Optional<ASTExpression> s = p.parse_StringExpression("7.9f!=8l");
    assertTrue(s.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build().deepEquals(calc.calculateType(s.get())));
  }

  @Test
  public void equalsTest() throws IOException{
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("7==7");
    assertTrue(o.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build().deepEquals(calc.calculateType(o.get())));

    Optional<ASTExpression> r = p.parse_StringExpression("\"Hallo\"==\"Welt\"");
    assertTrue(r.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build().deepEquals(calc.calculateType(r.get())));

    Optional<ASTExpression> q = p.parse_StringExpression("varList==varList");
    assertTrue(q.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build().deepEquals(calc.calculateType(q.get())));

    Optional<ASTExpression> s = p.parse_StringExpression("7.9f==8l");
    assertTrue(s.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build().deepEquals(calc.calculateType(s.get())));
  }

  @Test
  public void bracketTest() throws IOException{
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("(7+8)");
    assertTrue(o.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(o.get())));

    o = p.parse_StringExpression("(7-2.5)");
    assertTrue(o.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(o.get())));

    o = p.parse_StringExpression("(false==(4<3))");
    assertTrue(o.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build().deepEquals(calc.calculateType(o.get())));

    o = p.parse_StringExpression("(7l-2.5f)");
    assertTrue(o.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.FLOAT).build().deepEquals(calc.calculateType(o.get())));
  }

  @Test
  public void conditionalTest() throws IOException{
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("7<3? 7 : 3");
    assertTrue(o.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(o.get())));

    Optional<ASTExpression> r = p.parse_StringExpression("7.2<3? 7.2 : 3");
    assertTrue(r.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(r.get())));

    Optional<ASTExpression> s = p.parse_StringExpression("7.2f<3l? 7.2f : 3l");
    assertTrue(s.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.FLOAT).build().deepEquals(calc.calculateType(s.get())));

    List<String> name = new ArrayList<>();
    name.add("String");
    Optional<ASTExpression> t = p.parse_StringExpression("(\"Hallo\"==\"Welt\")? \"Gleich\" : \"Ungleich\"");
    assertTrue(t.isPresent());
    assertTrue(MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(name).build()).build().deepEquals(calc.calculateType(t.get())));
  }

  @Test
  public void booleanNotTest() throws IOException{
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("~7");
    assertTrue(o.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(o.get())));

    Optional<ASTExpression> r = p.parse_StringExpression("~7l");
    assertTrue(r.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.LONG).build().deepEquals(calc.calculateType(r.get())));

    Optional<ASTExpression> s = p.parse_StringExpression("~\'a\'");
    assertTrue(s.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(s.get())));
  }

  @Test
  public void combineOperationsTest() throws IOException{
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("9*7.4-3/2+4");
    Optional<ASTExpression> q = p.parse_StringExpression("9l*4+3-5/2");
    Optional<ASTExpression> r = p.parse_StringExpression("3<4+\'a\'");
    Optional<ASTExpression> s = p.parse_StringExpression("(4<6)&&true");
    Optional<ASTExpression> t = p.parse_StringExpression("~3<4? ~3 : 4");
    Optional<ASTExpression> u = p.parse_StringExpression("9.2f*(7.2+8)");
    Optional<ASTExpression> v = p.parse_StringExpression("false&&true");

    assertTrue(o.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(o.get())));

    assertTrue(q.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.LONG).build().deepEquals(calc.calculateType(q.get())));

    assertTrue(r.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build().deepEquals(calc.calculateType(r.get())));

    assertTrue(s.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build().deepEquals(calc.calculateType(s.get())));

    assertTrue(t.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(t.get())));

    assertTrue(u.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build().deepEquals(calc.calculateType(u.get())));

    assertTrue(v.isPresent());
    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build().deepEquals(calc.calculateType(v.get())));
  }

  @Test
  public void callTest() throws IOException{
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("call()");

    assertTrue(o.isPresent());

    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(o.get())));

    Optional<ASTExpression> q = p.parse_StringExpression("A.B.C.call()");

    assertTrue(q.isPresent());

    assertTrue(MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build().deepEquals(calc.calculateType(q.get())));
  }

  @Ignore
  @Test
  public void callStringTest() throws IOException{
    CombineExpressionsWithLiteralsTypesCalculator calc = new CombineExpressionsWithLiteralsTypesCalculator(scope);
    CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> o = p.parse_StringExpression("(\"3\"+\"7\").toString()");

    assertTrue(o.isPresent());
    ASTExpression expr = o.get();

    expr.accept(calc);
  }
}
