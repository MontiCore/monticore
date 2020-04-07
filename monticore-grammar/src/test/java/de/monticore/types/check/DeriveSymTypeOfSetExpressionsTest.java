/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import com.google.common.collect.Lists;
import de.monticore.expressions.combineexpressionswithliterals._parser.CombineExpressionsWithLiteralsParser;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._symboltable.ExpressionsBasisScope;
import de.monticore.expressions.expressionsbasis._symboltable.ExpressionsBasisSymTabMill;
import de.monticore.expressions.prettyprint.CombineExpressionsWithLiteralsPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.typesymbols._symboltable.FieldSymbol;
import de.monticore.types.typesymbols._symboltable.TypeSymbol;
import de.monticore.types.typesymbols._symboltable.TypeSymbolLoader;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static de.monticore.types.check.DefsTypeBasic.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DeriveSymTypeOfSetExpressionsTest {

  private ExpressionsBasisScope scope;

  /**
   * Focus: Deriving Type of Literals, here:
   * literals/MCLiteralsBasis.mc4
   */

  @BeforeClass
  public static void setup() {
    Log.init();
    Log.enableFailQuick(false);
  }

  @Before
  public void setupForEach() {
    // Setting up a Scope Infrastructure (without a global Scope)
    DefsTypeBasic.setup();
    scope =
        ExpressionsBasisSymTabMill.expressionsBasisScopeBuilder()
            .setEnclosingScope(null)       // No enclosing Scope: Search ending here
            .setExportingSymbols(true)
            .setAstNode(null)
            .setName("Phantasy2").build();     // hopefully unused
    // we add a variety of TypeSymbols to the same scope (which in reality doesn't happen)
    add2scope(scope, DefsTypeBasic._int);
    add2scope(scope, DefsTypeBasic._char);
    add2scope(scope, DefsTypeBasic._boolean);
    add2scope(scope, DefsTypeBasic._double);
    add2scope(scope, DefsTypeBasic._float);
    add2scope(scope, DefsTypeBasic._long);

    add2scope(scope, DefsTypeBasic._array);
    add2scope(scope, DefsTypeBasic._Object);
    add2scope(scope, DefsTypeBasic._String);

    // some FieldSymbols (ie. Variables, Attributes)
    TypeSymbol p = new TypeSymbol("Person");
    scope.add(p);
    TypeSymbol s = new TypeSymbol("Student");
    scope.add(s);
    s.setSuperTypeList(Lists.newArrayList(SymTypeExpressionFactory.createTypeObject("Person", scope)));
    TypeSymbol f = new TypeSymbol("FirstSemesterStudent");
    scope.add(f);
    f.setSuperTypeList(Lists.newArrayList(SymTypeExpressionFactory.createTypeObject("Student", scope)));
    add2scope(scope, field("foo", _intSymType));
    add2scope(scope, field("bar2", _booleanSymType));
    add2scope(scope, field("vardouble", _doubleSymType));
    add2scope(scope, field("varchar", _charSymType));
    add2scope(scope, field("varfloat", _floatSymType));
    add2scope(scope, field("varlong", _longSymType));
    add2scope(scope, field("varint", _intSymType));
    add2scope(scope, field("varString", SymTypeExpressionFactory.createTypeObject("String", scope)));
    add2scope(scope, field("person1", SymTypeExpressionFactory.createTypeObject("Person", scope)));
    add2scope(scope, field("person2", SymTypeExpressionFactory.createTypeObject("Person", scope)));
    add2scope(scope, field("student1", SymTypeExpressionFactory.createTypeObject("Student", scope)));
    add2scope(scope, field("student2", SymTypeExpressionFactory.createTypeObject("Student", scope)));
    add2scope(scope, field("firstsemester", SymTypeExpressionFactory.createTypeObject("FirstSemesterStudent", scope)));
    derLit.setScope(scope);

    LogStub.init();
  }

  // Parer used for convenience:
  // (may be any other Parser that understands CommonExpressions)
  CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();

  // This is the core Visitor under Test (but rather empty)
  DeriveSymTypeOfExpression derEx = new DeriveSymTypeOfExpression();

  // This is an auxiliary
  DeriveSymTypeOfCombineExpressionsDelegator derLit = new DeriveSymTypeOfCombineExpressionsDelegator(ExpressionsBasisSymTabMill.expressionsBasisScopeBuilder().build(), new CombineExpressionsWithLiteralsPrettyPrinter(new IndentPrinter()));

  // other arguments not used (and therefore deliberately null)

  // This is the TypeChecker under Test:
  TypeCheck tc = new TypeCheck(null, derLit);

  /*--------------------------------------------------- TESTS ---------------------------------------------------------*/

  @Test
  public void testSetInExpression() throws IOException{
    //TEST 1: double in Set<double>
    TypeSymbolLoader loader = new TypeSymbolLoader("Set",scope);
    TypeSymbol setDoubleType = type("Set",Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(typeVariable("T")),scope);
    add2scope(scope,setDoubleType);
    SymTypeExpression setDouble = SymTypeExpressionFactory.createGenerics(loader,_doubleSymType);
    setDouble.typeSymbolLoader = loader;
    FieldSymbol number = field("number",_doubleSymType);
    FieldSymbol setDoubleField = field("setdouble",setDouble);
    add2scope(scope,number);
    add2scope(scope,setDoubleField);

    derLit.setScope(scope);
    tc = new TypeCheck(null, derLit);

    ASTExpression a = p.parse_StringExpression("number in setdouble").get();
    assertEquals("double",tc.typeOf(a).print());

    //TEST 2: int in Set<double> -> subtype of the argument
    ASTExpression b = p.parse_StringExpression("3 in setdouble").get();
    assertEquals("double",tc.typeOf(b).print());
  }

  @Test
  public void testInvalidSetInExpression() throws IOException{
    //TEST 1: Error: double in Set<int>
    TypeSymbolLoader loader = new TypeSymbolLoader("Set",scope);
    TypeSymbol setIntType = type("Set",Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(typeVariable("T")),scope);
    add2scope(scope,setIntType);
    SymTypeExpression setInt = SymTypeExpressionFactory.createGenerics(loader,_intSymType);
    setInt.typeSymbolLoader = loader;
    FieldSymbol number = field("number",_doubleSymType);
    FieldSymbol setIntField = field("setint",setInt);
    add2scope(scope,number);
    add2scope(scope,setIntField);

    derLit.setScope(scope);
    tc = new TypeCheck(null, derLit);

    ASTExpression a = p.parse_StringExpression("number in setint").get();
    try{
      tc.typeOf(a);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0291"));
    }
  }

  @Test
  public void testIsInExpression() throws IOException{
    //TEST 1: double in Set<double>
    TypeSymbolLoader loader = new TypeSymbolLoader("Set",scope);
    TypeSymbol setDoubleType = type("Set",Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(typeVariable("T")),scope);
    add2scope(scope,setDoubleType);
    SymTypeExpression setDouble = SymTypeExpressionFactory.createGenerics(loader,_doubleSymType);
    setDouble.typeSymbolLoader = loader;
    FieldSymbol number = field("number",_doubleSymType);
    FieldSymbol setDoubleField = field("setdouble",setDouble);
    add2scope(scope,number);
    add2scope(scope,setDoubleField);

    derLit.setScope(scope);
    tc = new TypeCheck(null, derLit);

    ASTExpression a = p.parse_StringExpression("number isin setdouble").get();
    assertEquals("boolean",tc.typeOf(a).print());

    //TEST 2: int in Set<double> -> subtype of the argument
    ASTExpression b = p.parse_StringExpression("3 isin setdouble").get();
    assertEquals("boolean",tc.typeOf(b).print());
  }

  @Test
  public void testInvalidIsInExpression() throws IOException{
    //TEST 1: Error: double isin Set<int>
    TypeSymbolLoader loader = new TypeSymbolLoader("Set",scope);
    TypeSymbol setIntType = type("Set",Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(typeVariable("T")),scope);
    add2scope(scope,setIntType);
    SymTypeExpression setInt = SymTypeExpressionFactory.createGenerics(loader,_intSymType);
    setInt.typeSymbolLoader = loader;
    FieldSymbol number = field("number",_doubleSymType);
    FieldSymbol setIntField = field("setint",setInt);
    add2scope(scope,number);
    add2scope(scope,setIntField);

    derLit.setScope(scope);
    tc = new TypeCheck(null, derLit);

    ASTExpression a = p.parse_StringExpression("number isin setint").get();
    try{
      tc.typeOf(a);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0288"));
    }
  }

  @Test
  public void testUnionExpressionInfix() throws IOException{
    //create Set<int> and Set<double>
    TypeSymbolLoader loader = new TypeSymbolLoader("Set",scope);
    TypeSymbol setinttype = type("Set",Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(typeVariable("T")),scope);
    add2scope(scope,setinttype);
    SymTypeExpression setint = SymTypeExpressionFactory.createGenerics(loader,_intSymType);
    setint.typeSymbolLoader = loader;
    FieldSymbol setintfield = field("setint",setint);
    add2scope(scope,setintfield);

    SymTypeExpression setdouble = SymTypeExpressionFactory.createGenerics(loader,_doubleSymType);
    setdouble.typeSymbolLoader = loader;
    FieldSymbol setdoublefield = field("setdouble",setdouble);
    add2scope(scope,setdoublefield);

    derLit.setScope(scope);
    tc = new TypeCheck(null, derLit);

    //TEST 1: Set<int> union Set<int>
    ASTExpression a = p.parse_StringExpression("setint union setint").get();
    assertEquals("Set<int>",tc.typeOf(a).print());

    //TEST 2: Set<int> union Set<double> -> int subtype of double
    ASTExpression b = p.parse_StringExpression("setint union setdouble").get();
    assertEquals("Set<double>",tc.typeOf(b).print());
  }

  @Test
  public void testInvalidUnionInfixExpression() throws IOException{
    //TEST 1: Error: no SetType union SetType
    TypeSymbolLoader loader = new TypeSymbolLoader("Set",scope);
    TypeSymbol setIntType = type("Set",Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(typeVariable("T")),scope);
    add2scope(scope,setIntType);
    SymTypeExpression setInt = SymTypeExpressionFactory.createGenerics(loader,_intSymType);
    setInt.typeSymbolLoader = loader;
    FieldSymbol number = field("number",_doubleSymType);
    FieldSymbol setIntField = field("setint",setInt);
    add2scope(scope,number);
    add2scope(scope,setIntField);

    derLit.setScope(scope);
    tc = new TypeCheck(null, derLit);

    ASTExpression a = p.parse_StringExpression("number union setint").get();
    try{
      tc.typeOf(a);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0292"));
    }
  }

  @Test
  public void testInvalidUnionInfixExpression2() throws IOException{
    //TEST 2: Error: set<boolean> union set<int>
    TypeSymbolLoader loader = new TypeSymbolLoader("Set",scope);
    TypeSymbol setIntType = type("Set",Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(typeVariable("T")),scope);
    add2scope(scope,setIntType);
    SymTypeExpression setInt = SymTypeExpressionFactory.createGenerics(loader,_intSymType);
    setInt.typeSymbolLoader = loader;
    FieldSymbol setIntField = field("setint",setInt);
    add2scope(scope,setIntField);

    SymTypeExpression setBool = SymTypeExpressionFactory.createGenerics(loader,_booleanSymType);
    setBool.typeSymbolLoader = loader;
    FieldSymbol setBoolField = field("setbool",setBool);
    add2scope(scope,setBoolField);

    derLit.setScope(scope);
    tc = new TypeCheck(null, derLit);

    ASTExpression a = p.parse_StringExpression("setbool union setint").get();
    try{
      tc.typeOf(a);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0292"));
    }
  }

  @Test
  public void testIntersectionExpressionInfix() throws IOException{
    //create Set<int> and Set<double>
    TypeSymbolLoader loader = new TypeSymbolLoader("Set",scope);
    TypeSymbol setinttype = type("Set",Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(typeVariable("T")),scope);
    add2scope(scope,setinttype);
    SymTypeExpression setint = SymTypeExpressionFactory.createGenerics(loader,_intSymType);
    setint.typeSymbolLoader = loader;
    FieldSymbol setintfield = field("setint",setint);
    add2scope(scope,setintfield);

    SymTypeExpression setchar = SymTypeExpressionFactory.createGenerics(loader,_charSymType);
    setchar.typeSymbolLoader = loader;
    FieldSymbol setcharfield = field("setchar",setchar);
    add2scope(scope,setcharfield);

    derLit.setScope(scope);
    tc = new TypeCheck(null, derLit);

    //TEST 1: Set<double> intersect Set<double>
    ASTExpression a = p.parse_StringExpression("setchar intersect setchar").get();
    assertEquals("Set<char>",tc.typeOf(a).print());

    //TEST 2: Set<double> intersect Set<int> -> int subtype of double
    ASTExpression b = p.parse_StringExpression("setint intersect setchar").get();
    assertEquals("Set<int>",tc.typeOf(b).print());
  }

  @Test
  public void testInvalidIntersectionInfixExpression() throws IOException{
    //TEST 1: Error: no SetType intersect SetType
    TypeSymbolLoader loader = new TypeSymbolLoader("Set",scope);
    TypeSymbol setIntType = type("Set",Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(typeVariable("T")),scope);
    add2scope(scope,setIntType);
    SymTypeExpression setInt = SymTypeExpressionFactory.createGenerics(loader,_intSymType);
    setInt.typeSymbolLoader = loader;
    FieldSymbol number = field("number",_doubleSymType);
    FieldSymbol setIntField = field("setint",setInt);
    add2scope(scope,number);
    add2scope(scope,setIntField);

    derLit.setScope(scope);
    tc = new TypeCheck(null, derLit);

    ASTExpression a = p.parse_StringExpression("number intersect setint").get();
    try{
      tc.typeOf(a);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0293"));
    }
  }

  @Test
  public void testInvalidIntersectionInfixExpression2() throws IOException{
    //TEST 2: Error: set<boolean> intersect set<int>
    TypeSymbolLoader loader = new TypeSymbolLoader("Set",scope);
    TypeSymbol setIntType = type("Set",Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(typeVariable("T")),scope);
    add2scope(scope,setIntType);
    SymTypeExpression setInt = SymTypeExpressionFactory.createGenerics(loader,_intSymType);
    setInt.typeSymbolLoader = loader;
    FieldSymbol setIntField = field("setint",setInt);
    add2scope(scope,setIntField);

    SymTypeExpression setBool = SymTypeExpressionFactory.createGenerics(loader,_booleanSymType);
    setBool.typeSymbolLoader = loader;
    FieldSymbol setBoolField = field("setbool",setBool);
    add2scope(scope,setBoolField);

    derLit.setScope(scope);
    tc = new TypeCheck(null, derLit);

    ASTExpression a = p.parse_StringExpression("setbool intersect setint").get();
    try{
      tc.typeOf(a);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0293"));
    }
  }
}
