/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import com.google.common.collect.Lists;
import de.monticore.expressions.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import de.monticore.expressions.combineexpressionswithliterals._parser.CombineExpressionsWithLiteralsParser;
import de.monticore.expressions.combineexpressionswithliterals._symboltable.ICombineExpressionsWithLiteralsScope;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.oosymbols.OOSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbolSurrogate;
import de.se_rwth.commons.logging.*;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static de.monticore.types.check.DefsTypeBasic.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DeriveSymTypeOfSetExpressionsTest {

  private ICombineExpressionsWithLiteralsScope scope;
  private FlatExpressionScopeSetter flatExpressionScopeSetter;

  /**
   * Focus: Deriving Type of Literals, here:
   * literals/MCLiteralsBasis.mc4
   */

  @BeforeClass
  public static void setup() {
    LogStub.init();         // replace log by a sideffect free variant
    // LogStub.initPlusLog();  // for manual testing purpose only
    Log.enableFailQuick(false);
  }

  @Before
  public void setupForEach() {
    // Setting up a Scope Infrastructure (without a global Scope)
    DefsTypeBasic.setup();
    scope =
        CombineExpressionsWithLiteralsMill.combineExpressionsWithLiteralsScopeBuilder()
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
    OOTypeSymbol p = new OOTypeSymbol("Person");
    scope.add(p);
    OOTypeSymbol s = new OOTypeSymbol("Student");
    scope.add(s);
    s.setSuperTypesList(Lists.newArrayList(SymTypeExpressionFactory.createTypeObject("Person", scope)));
    OOTypeSymbol f = new OOTypeSymbol("FirstSemesterStudent");
    scope.add(f);
    f.setSuperTypesList(Lists.newArrayList(SymTypeExpressionFactory.createTypeObject("Student", scope)));
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

    flatExpressionScopeSetter = new FlatExpressionScopeSetter(scope);

    LogStub.init();
  }

  // Parer used for convenience:
  // (may be any other Parser that understands CommonExpressions)
  CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();

  // This is the core Visitor under Test (but rather empty)
  DeriveSymTypeOfExpression derEx = new DeriveSymTypeOfExpression();

  // This is an auxiliary
  DeriveSymTypeOfCombineExpressionsDelegator derLit = new DeriveSymTypeOfCombineExpressionsDelegator();

  // other arguments not used (and therefore deliberately null)

  // This is the TypeChecker under Test:
  TypeCheck tc = new TypeCheck(null, derLit);

  /*--------------------------------------------------- TESTS ---------------------------------------------------------*/

  @Test
  public void testSetInExpression() throws IOException{
    //TEST 1: double in Set<double>
    OOTypeSymbolSurrogate loader = new OOTypeSymbolSurrogate("Set");
    loader.setEnclosingScope(scope);
    OOTypeSymbol setDoubleType = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(OOSymbolsMill.oOSymbolsScopeBuilder().build())
        .setName("Set")
        .setEnclosingScope(scope)
        .build();
    setDoubleType.addTypeVarSymbol(typeVariable("T"));
    add2scope(scope,setDoubleType);
    SymTypeExpression setDouble = SymTypeExpressionFactory.createGenerics(loader,_doubleSymType);
    setDouble.typeSymbolSurrogate = loader;
    FieldSymbol number = field("number",_doubleSymType);
    FieldSymbol setDoubleField = field("setdouble",setDouble);
    add2scope(scope,number);
    add2scope(scope,setDoubleField);

    tc = new TypeCheck(null, derLit);
    flatExpressionScopeSetter = new FlatExpressionScopeSetter(scope);

    ASTExpression a = p.parse_StringExpression("number in setdouble").get();
    a.accept(flatExpressionScopeSetter);
    assertEquals("double",tc.typeOf(a).print());

    //TEST 2: int in Set<double> -> subtype of the argument
    ASTExpression b = p.parse_StringExpression("3 in setdouble").get();
    b.accept(flatExpressionScopeSetter);
    assertEquals("double",tc.typeOf(b).print());
  }

  @Test
  public void testInvalidSetInExpression() throws IOException{
    //TEST 1: Error: double in Set<int>
    OOTypeSymbolSurrogate loader = new OOTypeSymbolSurrogate("Set");
    loader.setEnclosingScope(scope);
    OOTypeSymbol setIntType = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(OOSymbolsMill.oOSymbolsScopeBuilder().build())
        .setName("Set")
        .setEnclosingScope(scope)
        .build();
    setIntType.addTypeVarSymbol(typeVariable("T"));
    add2scope(scope,setIntType);
    SymTypeExpression setInt = SymTypeExpressionFactory.createGenerics(loader,_intSymType);
    setInt.typeSymbolSurrogate = loader;
    FieldSymbol number = field("number",_doubleSymType);
    FieldSymbol setIntField = field("setint",setInt);
    add2scope(scope,number);
    add2scope(scope,setIntField);

    tc = new TypeCheck(null, derLit);
    flatExpressionScopeSetter = new FlatExpressionScopeSetter(scope);

    ASTExpression a = p.parse_StringExpression("number in setint").get();
    a.accept(flatExpressionScopeSetter);
    try{
      tc.typeOf(a);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0291"));
    }
  }

  @Test
  public void testIsInExpression() throws IOException{
    //TEST 1: double in Set<double>
    OOTypeSymbolSurrogate loader = new OOTypeSymbolSurrogate("Set");
    loader.setEnclosingScope(scope);
    OOTypeSymbol setDoubleType = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(OOSymbolsMill.oOSymbolsScopeBuilder().build())
        .setName("Set")
        .setEnclosingScope(scope)
        .build();
    setDoubleType.addTypeVarSymbol(typeVariable("T"));
    add2scope(scope,setDoubleType);
    SymTypeExpression setDouble = SymTypeExpressionFactory.createGenerics(loader,_doubleSymType);
    setDouble.typeSymbolSurrogate = loader;
    FieldSymbol number = field("number",_doubleSymType);
    FieldSymbol setDoubleField = field("setdouble",setDouble);
    add2scope(scope,number);
    add2scope(scope,setDoubleField);

    tc = new TypeCheck(null, derLit);
    flatExpressionScopeSetter = new FlatExpressionScopeSetter(scope);

    ASTExpression a = p.parse_StringExpression("number isin setdouble").get();
    a.accept(flatExpressionScopeSetter);
    assertEquals("boolean",tc.typeOf(a).print());

    //TEST 2: int in Set<double> -> subtype of the argument
    ASTExpression b = p.parse_StringExpression("3 isin setdouble").get();
    b.accept(flatExpressionScopeSetter);
    assertEquals("boolean",tc.typeOf(b).print());
  }

  @Test
  public void testInvalidIsInExpression() throws IOException{
    //TEST 1: Error: double isin Set<int>
    OOTypeSymbolSurrogate loader = new OOTypeSymbolSurrogate("Set");
    loader.setEnclosingScope(scope);
    OOTypeSymbol setIntType = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(OOSymbolsMill.oOSymbolsScopeBuilder().build())
        .setName("Set")
        .setEnclosingScope(scope)
        .build();
    setIntType.addTypeVarSymbol(typeVariable("T"));
    add2scope(scope,setIntType);
    SymTypeExpression setInt = SymTypeExpressionFactory.createGenerics(loader,_intSymType);
    setInt.typeSymbolSurrogate = loader;
    FieldSymbol number = field("number",_doubleSymType);
    FieldSymbol setIntField = field("setint",setInt);
    add2scope(scope,number);
    add2scope(scope,setIntField);

    tc = new TypeCheck(null, derLit);
    flatExpressionScopeSetter = new FlatExpressionScopeSetter(scope);

    ASTExpression a = p.parse_StringExpression("number isin setint").get();
    a.accept(flatExpressionScopeSetter);
    try{
      tc.typeOf(a);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0288"));
    }
  }

  @Test
  public void testUnionExpressionInfix() throws IOException{
    //create Set<int> and Set<double>
    OOTypeSymbolSurrogate loader = new OOTypeSymbolSurrogate("Set");
    loader.setEnclosingScope(scope);
    OOTypeSymbol setinttype = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(OOSymbolsMill.oOSymbolsScopeBuilder().build())
        .setName("Set")
        .setEnclosingScope(scope)
        .build();
    setinttype.addTypeVarSymbol(typeVariable("T"));
    add2scope(scope,setinttype);
    SymTypeExpression setint = SymTypeExpressionFactory.createGenerics(loader,_intSymType);
    setint.typeSymbolSurrogate = loader;
    FieldSymbol setintfield = field("setint",setint);
    add2scope(scope,setintfield);

    SymTypeExpression setdouble = SymTypeExpressionFactory.createGenerics(loader,_doubleSymType);
    setdouble.typeSymbolSurrogate = loader;
    FieldSymbol setdoublefield = field("setdouble",setdouble);
    add2scope(scope,setdoublefield);

    tc = new TypeCheck(null, derLit);
    flatExpressionScopeSetter = new FlatExpressionScopeSetter(scope);

    //TEST 1: Set<int> union Set<int>
    ASTExpression a = p.parse_StringExpression("setint union setint").get();
    a.accept(flatExpressionScopeSetter);
    assertEquals("Set<int>",tc.typeOf(a).print());

    //TEST 2: Set<int> union Set<double> -> int subtype of double
    ASTExpression b = p.parse_StringExpression("setint union setdouble").get();
    b.accept(flatExpressionScopeSetter);
    assertEquals("Set<double>",tc.typeOf(b).print());
  }

  @Test
  public void testInvalidUnionInfixExpression() throws IOException{
    //TEST 1: Error: no SetType union SetType
    OOTypeSymbolSurrogate loader = new OOTypeSymbolSurrogate("Set");
    loader.setEnclosingScope(scope);
    OOTypeSymbol setIntType = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(OOSymbolsMill.oOSymbolsScopeBuilder().build())
        .setName("Set")
        .setEnclosingScope(scope)
        .build();    add2scope(scope,setIntType);
    setIntType.addTypeVarSymbol(typeVariable("T"));
    SymTypeExpression setInt = SymTypeExpressionFactory.createGenerics(loader,_intSymType);
    setInt.typeSymbolSurrogate = loader;
    FieldSymbol number = field("number",_doubleSymType);
    FieldSymbol setIntField = field("setint",setInt);
    add2scope(scope,number);
    add2scope(scope,setIntField);

    tc = new TypeCheck(null, derLit);
    flatExpressionScopeSetter = new FlatExpressionScopeSetter(scope);

    ASTExpression a = p.parse_StringExpression("number union setint").get();
    a.accept(flatExpressionScopeSetter);
    try{
      tc.typeOf(a);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0292"));
    }
  }

  @Test
  public void testInvalidUnionInfixExpression2() throws IOException{
    //TEST 2: Error: set<boolean> union set<int>
    OOTypeSymbolSurrogate loader = new OOTypeSymbolSurrogate("Set");
    loader.setEnclosingScope(scope);
    OOTypeSymbol setIntType = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(OOSymbolsMill.oOSymbolsScopeBuilder().build())
        .setName("Set")
        .setEnclosingScope(scope)
        .build();
    setIntType.addTypeVarSymbol(typeVariable("T"));
    add2scope(scope,setIntType);
    SymTypeExpression setInt = SymTypeExpressionFactory.createGenerics(loader,_intSymType);
    setInt.typeSymbolSurrogate = loader;
    FieldSymbol setIntField = field("setint",setInt);
    add2scope(scope,setIntField);

    SymTypeExpression setBool = SymTypeExpressionFactory.createGenerics(loader,_booleanSymType);
    setBool.typeSymbolSurrogate = loader;
    FieldSymbol setBoolField = field("setbool",setBool);
    add2scope(scope,setBoolField);

    tc = new TypeCheck(null, derLit);
    flatExpressionScopeSetter = new FlatExpressionScopeSetter(scope);

    ASTExpression a = p.parse_StringExpression("setbool union setint").get();
    a.accept(flatExpressionScopeSetter);
    try{
      tc.typeOf(a);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0292"));
    }
  }

  @Test
  public void testIntersectionExpressionInfix() throws IOException{
    //create Set<int> and Set<double>
    OOTypeSymbolSurrogate loader = new OOTypeSymbolSurrogate("Set");
    loader.setEnclosingScope(scope);
    OOTypeSymbol setinttype = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(OOSymbolsMill.oOSymbolsScopeBuilder().build())
        .setName("Set")
        .setEnclosingScope(scope)
        .build();
    setinttype.addTypeVarSymbol(typeVariable("T"));
    add2scope(scope,setinttype);
    SymTypeExpression setint = SymTypeExpressionFactory.createGenerics(loader,_intSymType);
    setint.typeSymbolSurrogate = loader;
    FieldSymbol setintfield = field("setint",setint);
    add2scope(scope,setintfield);

    SymTypeExpression setchar = SymTypeExpressionFactory.createGenerics(loader,_charSymType);
    setchar.typeSymbolSurrogate = loader;
    FieldSymbol setcharfield = field("setchar",setchar);
    add2scope(scope,setcharfield);

    tc = new TypeCheck(null, derLit);
    flatExpressionScopeSetter = new FlatExpressionScopeSetter(scope);

    //TEST 1: Set<double> intersect Set<double>
    ASTExpression a = p.parse_StringExpression("setchar intersect setchar").get();
    a.accept(flatExpressionScopeSetter);
    assertEquals("Set<char>",tc.typeOf(a).print());

    //TEST 2: Set<double> intersect Set<int> -> int subtype of double
    ASTExpression b = p.parse_StringExpression("setint intersect setchar").get();
    b.accept(flatExpressionScopeSetter);
    assertEquals("Set<int>",tc.typeOf(b).print());
  }

  @Test
  public void testInvalidIntersectionInfixExpression() throws IOException{
    //TEST 1: Error: no SetType intersect SetType
    OOTypeSymbolSurrogate loader = new OOTypeSymbolSurrogate("Set");
    loader.setEnclosingScope(scope);
    OOTypeSymbol setIntType = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(OOSymbolsMill.oOSymbolsScopeBuilder().build())
        .setName("Set")
        .setEnclosingScope(scope)
        .build();
    setIntType.addTypeVarSymbol(typeVariable("T"));
    add2scope(scope,setIntType);
    SymTypeExpression setInt = SymTypeExpressionFactory.createGenerics(loader,_intSymType);
    setInt.typeSymbolSurrogate = loader;
    FieldSymbol number = field("number",_doubleSymType);
    FieldSymbol setIntField = field("setint",setInt);
    add2scope(scope,number);
    add2scope(scope,setIntField);

    tc = new TypeCheck(null, derLit);
    flatExpressionScopeSetter = new FlatExpressionScopeSetter(scope);

    ASTExpression a = p.parse_StringExpression("number intersect setint").get();
    a.accept(flatExpressionScopeSetter);
    try{
      tc.typeOf(a);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0293"));
    }
  }

  @Test
  public void testInvalidIntersectionInfixExpression2() throws IOException{
    //TEST 2: Error: set<boolean> intersect set<int>
    OOTypeSymbolSurrogate loader = new OOTypeSymbolSurrogate("Set");
    loader.setEnclosingScope(scope);
    OOTypeSymbol setIntType = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(OOSymbolsMill.oOSymbolsScopeBuilder().build())
        .setName("Set")
        .setEnclosingScope(scope)
        .build();
    setIntType.addTypeVarSymbol(typeVariable("T"));
    add2scope(scope,setIntType);
    SymTypeExpression setInt = SymTypeExpressionFactory.createGenerics(loader,_intSymType);
    setInt.typeSymbolSurrogate = loader;
    FieldSymbol setIntField = field("setint",setInt);
    add2scope(scope,setIntField);

    SymTypeExpression setBool = SymTypeExpressionFactory.createGenerics(loader,_booleanSymType);
    setBool.typeSymbolSurrogate = loader;
    FieldSymbol setBoolField = field("setbool",setBool);
    add2scope(scope,setBoolField);

    tc = new TypeCheck(null, derLit);
    flatExpressionScopeSetter = new FlatExpressionScopeSetter(scope);

    ASTExpression a = p.parse_StringExpression("setbool intersect setint").get();
    a.accept(flatExpressionScopeSetter);
    try{
      tc.typeOf(a);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0293"));
    }
  }
}
