/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import com.google.common.collect.Lists;
import de.monticore.expressions.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import de.monticore.expressions.combineexpressionswithliterals._parser.CombineExpressionsWithLiteralsParser;
import de.monticore.expressions.combineexpressionswithliterals._symboltable.CombineExpressionsWithLiteralsScope;
import de.monticore.expressions.combineexpressionswithliterals._symboltable.ICombineExpressionsWithLiteralsScope;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.oosymbols.OOSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.se_rwth.commons.logging.*;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static de.monticore.types.check.DefsTypeBasic.*;
import static org.junit.Assert.*;

public class DeriveSymTypeOfJavaClassExpressionsTest {

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

  // This is an auxiliary
  DeriveSymTypeOfCombineExpressionsDelegator derLit = new DeriveSymTypeOfCombineExpressionsDelegator();

  // This is the TypeChecker under Test:
  TypeCheck tc = new TypeCheck(null, derLit);

  /*--------------------------------------------------- TESTS ---------------------------------------------------------*/


  @Test
  public void deriveFromPrimarySuperExpression() throws IOException {
    OOTypeSymbol supType = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(OOSymbolsMill.oOSymbolsScopeBuilder().build())
        .setName("A")
        .setEnclosingScope(scope)
        .build();
    SymTypeExpression sup = SymTypeExpressionFactory.createTypeObject("A",scope);
    supType.setIsClass(true);
    add2scope(scope,supType);

    MethodSymbol get = OOSymbolsMill.methodSymbolBuilder()
        .setName("get")
        .setReturnType(_voidSymType)
        .build();
    get.setSpannedScope(CombineExpressionsWithLiteralsMill.combineExpressionsWithLiteralsScopeBuilder().build());
    OOTypeSymbol p = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(CombineExpressionsWithLiteralsMill.combineExpressionsWithLiteralsScopeBuilder().build())
        .setName("AB")
        .setSuperTypesList(Lists.newArrayList(sup))
        .setEnclosingScope(scope)
        .build();
    p.setIsClass(true);
    get.getSpannedScope().setEnclosingScope(p.getSpannedScope());
    add2scope(scope,p);

    //use the spanned scope of the type
    flatExpressionScopeSetter = new FlatExpressionScopeSetter((ICombineExpressionsWithLiteralsScope) p.getSpannedScope());
    CombineExpressionsWithLiteralsParser parser = new CombineExpressionsWithLiteralsParser();

    Optional<ASTExpression> a = parser.parse_StringExpression("super");
    assertTrue(a.isPresent());
    ASTExpression astex = a.get();
    astex.accept(flatExpressionScopeSetter);
    assertEquals("A",tc.typeOf(astex).print());

    //use the spanned scope of the method
    astex.accept(new FlatExpressionScopeSetter((ICombineExpressionsWithLiteralsScope) get.getSpannedScope()));
    assertEquals("A",tc.typeOf(astex).print());
  }

  @Test
  public void failDeriveFromPrimarySuperExpression() throws IOException{
    //use the current scope -> there is no type spanning any enclosing scope
    Optional<ASTExpression> a = p.parse_StringExpression("super");
    assertTrue(a.isPresent());
    ASTExpression astex = a.get();
    astex.accept(flatExpressionScopeSetter);
    try{
      tc.typeOf(astex);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0280"));
    }
  }

  @Test
  public void deriveFromPrimaryThisExpression() throws IOException {
    MethodSymbol get = OOSymbolsMill.methodSymbolBuilder()
        .setName("get")
        .setReturnType(_voidSymType)
        .build();
    get.setSpannedScope(CombineExpressionsWithLiteralsMill.combineExpressionsWithLiteralsScopeBuilder().build());
    OOTypeSymbol p = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(CombineExpressionsWithLiteralsMill.combineExpressionsWithLiteralsScopeBuilder().build())
        .setName("AB")
        .setEnclosingScope(scope)
        .build();
    p.addMethodSymbol(get);
    get.getSpannedScope().setEnclosingScope(p.getSpannedScope());
    add2scope(scope,p);

    //use the spanned scope of the type
    flatExpressionScopeSetter = new FlatExpressionScopeSetter((ICombineExpressionsWithLiteralsScope) p.getSpannedScope());
    CombineExpressionsWithLiteralsParser parser = new CombineExpressionsWithLiteralsParser();

    Optional<ASTExpression> a = parser.parse_StringExpression("this");
    assertTrue(a.isPresent());
    ASTExpression astex = a.get();
    astex.accept(flatExpressionScopeSetter);
    assertEquals("AB",tc.typeOf(astex).print());

    //use the spanned scope of the method
    flatExpressionScopeSetter = new FlatExpressionScopeSetter((ICombineExpressionsWithLiteralsScope) get.getSpannedScope());
    astex.accept(flatExpressionScopeSetter);
    assertEquals("AB",tc.typeOf(astex).print());
  }

  @Test
  public void failDeriveFromPrimaryThisExpression() throws IOException{
    //use the current scope -> there is no type spanning any enclosing scope
    Optional<ASTExpression> a = p.parse_StringExpression("this");
    assertTrue(a.isPresent());
    ASTExpression astex = a.get();
    astex.accept(flatExpressionScopeSetter);
    try{
      tc.typeOf(astex);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0272"));
    }
  }

  @Test
  public void deriveFromThisExpression() throws IOException{
    /*
    * example for this test:
    * class Outer{
    *   class Inner{
    *     public void methodInner(){
    *       Outer outer = Outer.this;
    *     }
    *     class Innerinner{
    *       public void methodInnerInner(){
    *         Outer outer = Outer.this;
    *         Inner inner = Inner.this;
    *       }
    *     }
    *   }
    * }
    *
    * one class Outer with an inner class Inner with a method methodInner
    * -> try reaching the instance of Outer from this method
    * the class Inner contains an inner class InnerInner with a method methodInnerInner
    * -> try reaching the instances of Inner and Outer from this method
    * */

    //build infrastructure for previous comment
    OOTypeSymbol outer = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(CombineExpressionsWithLiteralsMill.combineExpressionsWithLiteralsScopeBuilder().build())
        .setName("Outer")
        .setEnclosingScope(scope)
        .build();
    outer.getSpannedScope().setEnclosingScope(outer.getEnclosingScope());
    add2scope(scope,outer);
    MethodSymbol methodInner = OOSymbolsMill.methodSymbolBuilder()
        .setName("methodInner")
        .setReturnType(_voidSymType)
        .build();
    methodInner.setSpannedScope(CombineExpressionsWithLiteralsMill.combineExpressionsWithLiteralsScopeBuilder().build());
    OOTypeSymbol inner = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(CombineExpressionsWithLiteralsMill.combineExpressionsWithLiteralsScopeBuilder().build())
        .setName("Inner")
        .setEnclosingScope(outer.getSpannedScope())
        .build();
    inner.addMethodSymbol(methodInner);
    inner.getSpannedScope().setEnclosingScope(inner.getEnclosingScope());
    methodInner.getSpannedScope().setEnclosingScope(inner.getSpannedScope());
    add2scope(outer.getEnclosingScope(),inner);
    MethodSymbol methodInnerInner = OOSymbolsMill.methodSymbolBuilder()
        .setName("methodInnerInner")
        .setReturnType(_voidSymType)
        .build();
    methodInnerInner.setSpannedScope(CombineExpressionsWithLiteralsMill.combineExpressionsWithLiteralsScopeBuilder().build());
    OOTypeSymbol innerinner = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(CombineExpressionsWithLiteralsMill.combineExpressionsWithLiteralsScopeBuilder().build())
        .setName("InnerInner")
        .setEnclosingScope(inner.getSpannedScope())
        .build();
    innerinner.addMethodSymbol(methodInnerInner);
    innerinner.getSpannedScope().setEnclosingScope(innerinner.getEnclosingScope());
    methodInnerInner.getSpannedScope().setEnclosingScope(innerinner.getSpannedScope());
    add2scope(inner.getSpannedScope(),innerinner);

    //test 1: Outer.this in methodInner()
    flatExpressionScopeSetter = new FlatExpressionScopeSetter((ICombineExpressionsWithLiteralsScope) methodInner.getSpannedScope());

    Optional<ASTExpression> this1 = p.parse_StringExpression("Outer.this");
    Optional<ASTExpression> this2 = p.parse_StringExpression("Inner.this");

    assertTrue(this1.isPresent());
    assertTrue(this2.isPresent());
    ASTExpression t1 = this1.get();
    ASTExpression t2 = this2.get();
    t1.accept(flatExpressionScopeSetter);

    assertEquals("Outer",tc.typeOf(t1).print());

    //test 2&3: Outer.this and Inner.this in methodInnerInner()
    flatExpressionScopeSetter = new FlatExpressionScopeSetter((ICombineExpressionsWithLiteralsScope) methodInnerInner.getSpannedScope());
    t1.accept(flatExpressionScopeSetter);
    t2.accept(flatExpressionScopeSetter);

    assertEquals("Inner",tc.typeOf(t2).print());
    assertEquals("Outer",tc.typeOf(t1).print());
  }

  @Test
  public void failDeriveFromThisExpression1() throws IOException{
    //.this in enclosing class
    OOTypeSymbol fail = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(CombineExpressionsWithLiteralsMill.combineExpressionsWithLiteralsScopeBuilder().build())
        .setName("Fail")
        .setEnclosingScope(scope)
        .build();
    add2scope(scope,fail);

    flatExpressionScopeSetter = new FlatExpressionScopeSetter(scope);

    Optional<ASTExpression> this1 = p.parse_StringExpression("Fail.this");
    assertTrue(this1.isPresent());
    ASTExpression t1 = this1.get();
    t1.accept(flatExpressionScopeSetter);
    try{
      tc.typeOf(t1);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0252"));
    }
    //.this without a type -> field or literal
  }

  @Test
  public void failDeriveFromThisExpression2() throws IOException{
    //.this without a type
    Optional<ASTExpression> this1 = p.parse_StringExpression("person1.this");
    assertTrue(this1.isPresent());
    ASTExpression t1 = this1.get();
    t1.accept(flatExpressionScopeSetter);
    try{
      tc.typeOf(this1.get());
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0252"));
    }
  }

  @Test
  public void deriveFromArrayExpressionTest() throws IOException{
    SymTypeArray arrType = SymTypeExpressionFactory.createTypeArray("int",scope,1,_intSymType);
    FieldSymbol a = field("a",arrType);
    add2scope(scope,a);

    MethodSymbol get = method("get",arrType);
    add2scope(scope,get);

    SymTypeArray arrarrType = SymTypeExpressionFactory.createTypeArray("int",scope,2,_intSymType);
    FieldSymbol b = field("b",arrarrType);
    add2scope(scope,b);

    MethodSymbol test = method("test",arrarrType);
    add2scope(scope,test);

    TypeVarSymbol t = typeVariable("T");
    add2scope(scope,t);
    OOTypeSymbol list = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(CombineExpressionsWithLiteralsMill.combineExpressionsWithLiteralsScopeBuilder().build())
        .setName("List")
        .setEnclosingScope(scope)
        .build();
    list.addTypeVarSymbol(t);
    add2scope(scope,list);
    SymTypeExpression generic = SymTypeExpressionFactory.createGenerics("List",scope,_intSymType);
    SymTypeArray genarrType = SymTypeExpressionFactory.createTypeArray("List",scope,1,generic);
    FieldSymbol c = field("c",genarrType);
    add2scope(scope,c);

    Optional<ASTExpression> arr1 = p.parse_StringExpression("b[3]");
    Optional<ASTExpression> arr2 = p.parse_StringExpression("a[3]");
    Optional<ASTExpression> arr3 = p.parse_StringExpression("get()[3]");
    Optional<ASTExpression> arr4 = p.parse_StringExpression("test()[3]");
    Optional<ASTExpression> arr5 = p.parse_StringExpression("b[3][4]");
    Optional<ASTExpression> arr6 = p.parse_StringExpression("test()[3][4]");
    Optional<ASTExpression> arr7 = p.parse_StringExpression("c[1]");

    assertTrue(arr1.isPresent());
    assertTrue(arr2.isPresent());
    assertTrue(arr3.isPresent());
    assertTrue(arr4.isPresent());
    assertTrue(arr5.isPresent());
    assertTrue(arr6.isPresent());
    assertTrue(arr7.isPresent());

    ASTExpression a1 = arr1.get();
    ASTExpression a2 = arr2.get();
    ASTExpression a3 = arr3.get();
    ASTExpression a4 = arr4.get();
    ASTExpression a5 = arr5.get();
    ASTExpression a6 = arr6.get();
    ASTExpression a7 = arr7.get();

    a1.accept(flatExpressionScopeSetter);
    a2.accept(flatExpressionScopeSetter);
    a3.accept(flatExpressionScopeSetter);
    a4.accept(flatExpressionScopeSetter);
    a5.accept(flatExpressionScopeSetter);
    a6.accept(flatExpressionScopeSetter);
    a7.accept(flatExpressionScopeSetter);

    assertEquals("int[]",tc.typeOf(a1).print());
    assertEquals("int",tc.typeOf(a2).print());
    assertEquals("int",tc.typeOf(a3).print());
    assertEquals("int[]",tc.typeOf(a4).print());
    assertEquals("int",tc.typeOf(a5).print());
    assertEquals("int",tc.typeOf(a6).print());
    assertEquals("List<int>",tc.typeOf(a7).print());
  }

  @Test 
  public void failDeriveSymTypeOfArrayExpression() throws IOException {
    //assert that a type will throw an error -> no int[4]
    OOTypeSymbol test = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(CombineExpressionsWithLiteralsMill.combineExpressionsWithLiteralsScopeBuilder().build())
        .setName("Test")
        .setEnclosingScope(scope)
        .build();
    add2scope(scope,test);

    flatExpressionScopeSetter = new FlatExpressionScopeSetter(scope);

    Optional<ASTExpression> arr1 = p.parse_StringExpression("Test[4]");
    assertTrue(arr1.isPresent());
    ASTExpression a1 = arr1.get();
    a1.accept(flatExpressionScopeSetter);
    try{
      tc.typeOf(a1);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0255"));
    }
  }

  @Test 
  public void failDeriveSymTypeOfArrayExpression2() throws IOException{
    //no integral type in the brackets
    SymTypeArray arrType = SymTypeExpressionFactory.createTypeArray("int",scope,1,_intSymType);
    FieldSymbol a = field("a",arrType);
    add2scope(scope,a);

    Optional<ASTExpression> arr1 = p.parse_StringExpression("a[7.5]");
    assertTrue(arr1.isPresent());
    ASTExpression a1 = arr1.get();
    a1.accept(flatExpressionScopeSetter);

    try{
      tc.typeOf(a1);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0257"));
    }
  }

  @Test
  public void failDeriveSymTypeOfArrayExpression3() throws IOException{
    //type in the array brackets
    SymTypeArray arrType = SymTypeExpressionFactory.createTypeArray("int",scope,1,_intSymType);
    FieldSymbol a = field("a",arrType);
    add2scope(scope,a);

    Optional<ASTExpression> arr1 = p.parse_StringExpression("a[Person]");
    assertTrue(arr1.isPresent());
    ASTExpression a1 = arr1.get();
    a1.accept(flatExpressionScopeSetter);

    try{
      tc.typeOf(a1);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0253"));
    }
  }

  @Test
  public void deriveSymTypeOfClassExpression() throws IOException{
    Optional<ASTExpression> class1 = p.parse_StringExpression("String.class");
    Optional<ASTExpression> class2 = p.parse_StringExpression("Integer.class");
    Optional<ASTExpression> class3 = p.parse_StringExpression("java.util.Set<double>.class");

    assertTrue(class1.isPresent());
    assertTrue(class2.isPresent());
    assertTrue(class3.isPresent());

    ASTExpression c1 = class1.get();
    ASTExpression c2 = class2.get();
    ASTExpression c3 = class3.get();

    c1.accept(flatExpressionScopeSetter);
    c2.accept(flatExpressionScopeSetter);
    c3.accept(flatExpressionScopeSetter);

    //soll es so funktionieren wie in Java? Dann duerfte man naemlich keine Generics akzeptieren
    //hier sind erst einmal Generics mit dabei, s. Testfall #3
    //falls man einschränken möchte, kann man die CoCo NoClassExpressionForGenerics

    assertEquals("Class<String>",tc.typeOf(c1).print());
    assertEquals("Class<Integer>",tc.typeOf(c2).print());
    assertEquals("Class<java.util.Set<double>>",tc.typeOf(c3).print());
  }

  @Test
  public void failDeriveSymTypeOfClassExpression() throws IOException{
    //test that types must have a name
    Optional<ASTExpression> class1 = p.parse_StringExpression("3.class");

    assertFalse(class1.isPresent());

    Optional<ASTExpression> class2 = p.parse_StringExpression("\"Hallo\".class");
    assertFalse(class2.isPresent());
  }

  @Test
  public void deriveSymTypeOfInstanceofExpression() throws IOException{
    Optional<ASTExpression> inst1 = p.parse_StringExpression("person1 instanceof Person");
    Optional<ASTExpression> inst2 = p.parse_StringExpression("person2 instanceof Student");
    Optional<ASTExpression> inst3 = p.parse_StringExpression("student1 instanceof Person");

    assertTrue(inst1.isPresent());
    assertTrue(inst2.isPresent());
    assertTrue(inst3.isPresent());

    ASTExpression i1 = inst1.get();
    ASTExpression i2 = inst2.get();
    ASTExpression i3 = inst3.get();

    i1.accept(flatExpressionScopeSetter);
    i2.accept(flatExpressionScopeSetter);
    i3.accept(flatExpressionScopeSetter);

    assertEquals("boolean",tc.typeOf(i1).print());
    assertEquals("boolean",tc.typeOf(i2).print());
    assertEquals("boolean",tc.typeOf(i3).print());
  }

  @Test
  public void failDeriveSymTypeOfInstanceOfExpression() throws IOException{
    //the expression is a type
    Optional<ASTExpression> inst1 = p.parse_StringExpression("Student instanceof Person");
    assertTrue(inst1.isPresent());
    ASTExpression i1 = inst1.get();
    i1.accept(flatExpressionScopeSetter);

    try{
      tc.typeOf(i1);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0267"));
    }
  }

  @Test
  public void deriveSymTypeOfTypeCastExpression() throws IOException {
    Optional<ASTExpression> cast1 = p.parse_StringExpression("(Person) student1");
    Optional<ASTExpression> cast2 = p.parse_StringExpression("(Person)  person1");
    Optional<ASTExpression> cast3 = p.parse_StringExpression("(Student)person1");

    assertTrue(cast1.isPresent());
    assertTrue(cast2.isPresent());
    assertTrue(cast3.isPresent());

    ASTExpression c1 = cast1.get();
    ASTExpression c2 = cast2.get();
    ASTExpression c3 = cast3.get();

    c1.accept(flatExpressionScopeSetter);
    c2.accept(flatExpressionScopeSetter);
    c3.accept(flatExpressionScopeSetter);

    assertEquals("Person",tc.typeOf(c1).print());
    assertEquals("Person",tc.typeOf(c2).print());
    assertEquals("Student",tc.typeOf(c3).print());
  }

  @Test
  public void failDeriveSymTypeOfTypeCastExpression() throws IOException{
    //the expression is a type
    Optional<ASTExpression> cast1 = p.parse_StringExpression("(Student)Person");
    assertTrue(cast1.isPresent());
    ASTExpression c1 = cast1.get();
    c1.accept(flatExpressionScopeSetter);

    try{
      tc.typeOf(c1);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0262"));
    }
  }

  @Test
  public void failDeriveSymTypeOfTypeCastExpression2() throws IOException{
    //there exists no sub/super type relation between the expression and the type
    Optional<ASTExpression> cast1 = p.parse_StringExpression("(String)person1");
    assertTrue(cast1.isPresent());
    ASTExpression c1 = cast1.get();
    c1.accept(flatExpressionScopeSetter);

    try{
      tc.typeOf(c1);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0266"));
    }
  }

  @Test
  public void deriveSymTypeOfSuperExpression() throws IOException {
    /*
     * example for this test:
     * class SuperOuter{
     *    int field;
     *    public int test(){
     *      return 3;
     *    }
     * }
     *
     *
     *
     * class Outer extends SuperOuter{
     *   class Inner{
     *     public void methodInner(){
     *       Outer outer = Outer.this;
     *       int a = Outer.super.test();
     *       int b = Outer.super.field;
     *     }
     *     class Innerinner{
     *       public void methodInnerInner(){
     *         Outer outer = Outer.this;
     *         Inner inner = Inner.this;
     *         int c = Outer.super.test();
     *         int d = Outer.super.field;
     *       }
     *     }
     *   }
     * }
     *
     * one class SuperOuter
     * one class Outer that extends SuperOuter with an inner class Inner with a method methodInner
     * -> try reaching the get()-Method of SuperOuter from this method
     * the class Inner contains an inner class InnerInner with a method methodInnerInner
     * -> try reaching the get()-Method of SuperOuter from this method
     * */
    //build infrastructure for previous comment
    //class SuperOuter and its methods and fields
    MethodSymbol test = OOSymbolsMill.methodSymbolBuilder()
        .setName("test")
        .setReturnType(_intSymType)
        .build();
    test.setSpannedScope(CombineExpressionsWithLiteralsMill.combineExpressionsWithLiteralsScopeBuilder().build());
    FieldSymbol field = field("field",_intSymType);
    OOTypeSymbol superOuter = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(CombineExpressionsWithLiteralsMill.combineExpressionsWithLiteralsScopeBuilder().build())
        .setName("SuperOuter")
        .setEnclosingScope(scope)
        .build();
    superOuter.addMethodSymbol(test);
    superOuter.addFieldSymbol(field);
    superOuter.getSpannedScope().setEnclosingScope(superOuter.getEnclosingScope());
    test.getSpannedScope().setEnclosingScope(superOuter.getSpannedScope());
    superOuter.setIsClass(true);
    SymTypeExpression superOuterType = SymTypeExpressionFactory.createTypeObject("SuperOuter",scope);
    add2scope(scope,superOuter);

    //class Outer
    OOTypeSymbol outer = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(CombineExpressionsWithLiteralsMill.combineExpressionsWithLiteralsScopeBuilder().build())
        .setName("Outer")
        .setSuperTypesList(Lists.newArrayList(superOuterType))
        .setEnclosingScope(scope)
        .build();
    outer.getSpannedScope().setEnclosingScope(outer.getEnclosingScope());
    add2scope(scope,outer);
    MethodSymbol methodInner = OOSymbolsMill.methodSymbolBuilder()
        .setName("methodInner")
        .setReturnType(_voidSymType)
        .build();
    methodInner.setSpannedScope(CombineExpressionsWithLiteralsMill.combineExpressionsWithLiteralsScopeBuilder().build());

    //class Inner
    OOTypeSymbol inner = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(CombineExpressionsWithLiteralsMill.combineExpressionsWithLiteralsScopeBuilder().build())
        .setName("Inner")
        .setEnclosingScope(outer.getSpannedScope())
        .build();
    inner.addMethodSymbol(methodInner);
    inner.getSpannedScope().setEnclosingScope(inner.getEnclosingScope());
    methodInner.getSpannedScope().setEnclosingScope(inner.getSpannedScope());
    add2scope(outer.getEnclosingScope(),inner);
    MethodSymbol methodInnerInner = OOSymbolsMill.methodSymbolBuilder()
        .setName("methodInnerInner")
        .setReturnType(_voidSymType)
        .build();
    methodInnerInner.setSpannedScope(CombineExpressionsWithLiteralsMill.combineExpressionsWithLiteralsScopeBuilder().build());

    //class InnerInner
    OOTypeSymbol innerinner = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(CombineExpressionsWithLiteralsMill.combineExpressionsWithLiteralsScopeBuilder().build())
        .setName("InnerInner")
        .setEnclosingScope(inner.getSpannedScope())
        .build();
    innerinner.addMethodSymbol(methodInnerInner);
    innerinner.getSpannedScope().setEnclosingScope(innerinner.getEnclosingScope());
    methodInnerInner.getSpannedScope().setEnclosingScope(innerinner.getSpannedScope());
    add2scope(inner.getSpannedScope(),innerinner);

    flatExpressionScopeSetter = new FlatExpressionScopeSetter((CombineExpressionsWithLiteralsScope) methodInner.getSpannedScope());

    Optional<ASTExpression> super1 = p.parse_StringExpression("Outer.super.test()");
    Optional<ASTExpression> super2 = p.parse_StringExpression("Outer.super.field");

    assertTrue(super1.isPresent());
    assertTrue(super2.isPresent());

    ASTExpression s1 = super1.get();
    ASTExpression s2 = super2.get();

    s1.accept(flatExpressionScopeSetter);
    s2.accept(flatExpressionScopeSetter);

    assertEquals("int",tc.typeOf(s1).print());
    assertEquals("int",tc.typeOf(s2).print());

    flatExpressionScopeSetter = new FlatExpressionScopeSetter((CombineExpressionsWithLiteralsScope) methodInnerInner.getSpannedScope());
    s1.accept(flatExpressionScopeSetter);
    s2.accept(flatExpressionScopeSetter);
    assertEquals("int",tc.typeOf(s1).print());
    assertEquals("int",tc.typeOf(s2).print());
  }

  @Test
  public void failDeriveFromSuperExpression1() throws IOException{
    //.super in enclosing class
    OOTypeSymbol fail = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(CombineExpressionsWithLiteralsMill.combineExpressionsWithLiteralsScopeBuilder().build())
        .setName("Fail")
        .setEnclosingScope(scope)
        .build();
    fail.getSpannedScope().setEnclosingScope(fail.getEnclosingScope());
    add2scope(scope,fail);

    flatExpressionScopeSetter = new FlatExpressionScopeSetter(scope);

    Optional<ASTExpression> super1 = p.parse_StringExpression("Fail.super.get()");
    assertTrue(super1.isPresent());
    ASTExpression s1 = super1.get();
    s1.accept(flatExpressionScopeSetter);
    try{
      tc.typeOf(s1);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0261"));
    }
  }

  @Test
  public void failDeriveFromSuperExpression2() throws IOException{
    //.super without a type
    Optional<ASTExpression> super1 = p.parse_StringExpression("person1.super.get()");
    assertTrue(super1.isPresent());
    ASTExpression s1 = super1.get();
    s1.accept(flatExpressionScopeSetter);
    try{
      tc.typeOf(s1);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0259"));
    }
  }

  @Test
  public void failDeriveFromSuperExpression3() throws IOException{
    //.super with more than one super type
    //first super type
    MethodSymbol test = OOSymbolsMill.methodSymbolBuilder()
        .setName("test")
        .setReturnType(_intSymType)
        .build();
    test.setSpannedScope(CombineExpressionsWithLiteralsMill.combineExpressionsWithLiteralsScopeBuilder().build());
    OOTypeSymbol superOuter = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(CombineExpressionsWithLiteralsMill.combineExpressionsWithLiteralsScopeBuilder().build())
        .setName("SuperOuter")
        .setEnclosingScope(scope)
        .build();
    superOuter.addMethodSymbol(test);
    superOuter.getSpannedScope().setEnclosingScope(superOuter.getEnclosingScope());
    test.getSpannedScope().setEnclosingScope(superOuter.getSpannedScope());
    superOuter.setIsClass(true);
    SymTypeExpression superOuterType = SymTypeExpressionFactory.createTypeObject("SuperOuter",scope);
    add2scope(scope,superOuter);

    //second super type
    OOTypeSymbol superOuterTwo = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(CombineExpressionsWithLiteralsMill.combineExpressionsWithLiteralsScopeBuilder().build())
        .setName("SuperOuterTwo")
        .setEnclosingScope(scope)
        .build();
    superOuterTwo.getSpannedScope().setEnclosingScope(superOuterTwo.getEnclosingScope());
    superOuterTwo.setIsClass(true);
    SymTypeExpression superOuterTwoType = SymTypeExpressionFactory.createTypeObject("SuperOuterTwo",scope);
    add2scope(scope,superOuterTwo);

    //class Outer
    OOTypeSymbol outer = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(CombineExpressionsWithLiteralsMill.combineExpressionsWithLiteralsScopeBuilder().build())
        .setName("Outer")
        .setSuperTypesList(Lists.newArrayList(superOuterType, superOuterTwoType))
        .setEnclosingScope(scope)
        .build();
    outer.getSpannedScope().setEnclosingScope(outer.getEnclosingScope());
    add2scope(scope,outer);
    MethodSymbol methodInner = OOSymbolsMill.methodSymbolBuilder()
        .setName("methodInner")
        .setReturnType(_voidSymType)
        .build();
    methodInner.setSpannedScope(CombineExpressionsWithLiteralsMill.combineExpressionsWithLiteralsScopeBuilder().build());

    //class Inner
    OOTypeSymbol inner = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(CombineExpressionsWithLiteralsMill.combineExpressionsWithLiteralsScopeBuilder().build())
        .setName("Inner")
        .setEnclosingScope(outer.getSpannedScope())
        .build();
    inner.addMethodSymbol(methodInner);
    inner.getSpannedScope().setEnclosingScope(inner.getEnclosingScope());
    methodInner.getSpannedScope().setEnclosingScope(inner.getSpannedScope());
    add2scope(outer.getEnclosingScope(),inner);
    MethodSymbol methodInnerInner = OOSymbolsMill.methodSymbolBuilder()
        .setName("methodInnerInner")
        .setReturnType(_voidSymType)
        .build();
    methodInnerInner.setSpannedScope(CombineExpressionsWithLiteralsMill.combineExpressionsWithLiteralsScopeBuilder().build());

    //class InnerInner
    OOTypeSymbol innerinner = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(CombineExpressionsWithLiteralsMill.combineExpressionsWithLiteralsScopeBuilder().build())
        .setName("InnerInner")
        .setEnclosingScope(inner.getSpannedScope())
        .build();
    innerinner.addMethodSymbol(methodInnerInner);
    innerinner.getSpannedScope().setEnclosingScope(innerinner.getEnclosingScope());
    methodInnerInner.getSpannedScope().setEnclosingScope(innerinner.getSpannedScope());
    add2scope(inner.getSpannedScope(),innerinner);

    flatExpressionScopeSetter = new FlatExpressionScopeSetter((CombineExpressionsWithLiteralsScope) methodInnerInner.getSpannedScope());

    Optional<ASTExpression> super1 = p.parse_StringExpression("Outer.super.test()");
    assertTrue(super1.isPresent());
    ASTExpression s1 = super1.get();
    s1.accept(flatExpressionScopeSetter);
    try{
      tc.typeOf(s1);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0261"));
    }
  }

  @Test
  public void failDeriveFromSuperExpression4() throws IOException{
    //.super without a super type
    //class Outer
    OOTypeSymbol outer = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(CombineExpressionsWithLiteralsMill.combineExpressionsWithLiteralsScopeBuilder().build())
        .setName("Outer")
        .setSuperTypesList(Lists.newArrayList())
        .setEnclosingScope(scope)
        .build();
    outer.getSpannedScope().setEnclosingScope(outer.getEnclosingScope());
    add2scope(scope,outer);

    flatExpressionScopeSetter = new FlatExpressionScopeSetter(scope);

    Optional<ASTExpression> super1 = p.parse_StringExpression("Outer.super.test()");
    assertTrue(super1.isPresent());
    ASTExpression s1 = super1.get();
    s1.accept(flatExpressionScopeSetter);

    try{
      tc.typeOf(s1);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0261"));
    }
  }

  @Test
  public void failDeriveFromSuperExpression5() throws IOException{
    //.super with a super type but a method that the super type does not know
    MethodSymbol test = OOSymbolsMill.methodSymbolBuilder()
        .setName("test")
        .setReturnType(_intSymType)
        .build();
    test.setSpannedScope(CombineExpressionsWithLiteralsMill.combineExpressionsWithLiteralsScopeBuilder().build());
    OOTypeSymbol superOuter = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(CombineExpressionsWithLiteralsMill.combineExpressionsWithLiteralsScopeBuilder().build())
        .setName("SuperOuter")
        .setEnclosingScope(scope)
        .build();
    superOuter.addMethodSymbol(test);
    superOuter.getSpannedScope().setEnclosingScope(superOuter.getEnclosingScope());
    test.getSpannedScope().setEnclosingScope(superOuter.getSpannedScope());
    superOuter.setIsClass(true);
    SymTypeExpression superOuterType = SymTypeExpressionFactory.createTypeObject("SuperOuter",scope);
    add2scope(scope,superOuter);

    //class Outer
    OOTypeSymbol outer = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(CombineExpressionsWithLiteralsMill.combineExpressionsWithLiteralsScopeBuilder().build())
        .setName("Outer")
        .setSuperTypesList(Lists.newArrayList(superOuterType))
        .setEnclosingScope(scope)
        .build();
    outer.getSpannedScope().setEnclosingScope(outer.getEnclosingScope());
    add2scope(scope,outer);

    flatExpressionScopeSetter = new FlatExpressionScopeSetter(scope);

    //SuperOuter does not have a method "get"
    Optional<ASTExpression> super1 = p.parse_StringExpression("Outer.super.get()");
    assertTrue(super1.isPresent());
    ASTExpression s1 = super1.get();
    s1.accept(flatExpressionScopeSetter);
    try{
      tc.typeOf(s1);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0261"));
    }
  }


  @Test
  public void deriveSymTypeOfPrimaryGenericInvocationExpression() throws IOException {
    //build symbol table for the test
    //type variables for the methods
    TypeVarSymbol t = typeVariable("T");
    TypeVarSymbol s = typeVariable("S");
    SymTypeExpression tType = SymTypeExpressionFactory.createTypeVariable("T",scope);
    SymTypeExpression sType = SymTypeExpressionFactory.createTypeVariable("S",scope);
    //type ASuper with constructor
    MethodSymbol asuperconstr = OOSymbolsMill.methodSymbolBuilder()
        .setName("ASuper")
        .setReturnType(_intSymType)
        .build();
    asuperconstr.setSpannedScope(CombineExpressionsWithLiteralsMill.combineExpressionsWithLiteralsScopeBuilder().build());
    asuperconstr.getSpannedScope().add(t);
    OOTypeSymbol aSuper = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(CombineExpressionsWithLiteralsMill.combineExpressionsWithLiteralsScopeBuilder().build())
        .setName("ASuper")
        .setEnclosingScope(scope)
        .build();
    aSuper.addMethodSymbol(asuperconstr);
    aSuper.setIsClass(true);
    aSuper.getSpannedScope().setEnclosingScope(aSuper.getEnclosingScope());
    asuperconstr.getSpannedScope().setEnclosingScope(aSuper.getSpannedScope());
    SymTypeExpression aSuperType = SymTypeExpressionFactory.createTypeObject("ASuper",scope);
    asuperconstr.setReturnType(aSuperType);
    add2scope(scope,aSuper);

    //type A with constructor and methods test,get and set
    MethodSymbol test = OOSymbolsMill.methodSymbolBuilder()
        .setName("test")
        .setReturnType(_intSymType)
        .build();
    test.setSpannedScope(CombineExpressionsWithLiteralsMill.combineExpressionsWithLiteralsScopeBuilder().build());
    test.getSpannedScope().add(t);
    MethodSymbol get = OOSymbolsMill.methodSymbolBuilder()
        .setName("get")
        .setReturnType(tType)
        .build();
    get.setSpannedScope(CombineExpressionsWithLiteralsMill.combineExpressionsWithLiteralsScopeBuilder().build());
    get.getSpannedScope().add(t);
    MethodSymbol aconstr = OOSymbolsMill.methodSymbolBuilder()
        .setName("A")
        .setReturnType(_intSymType)
        .build();
    aconstr.setSpannedScope(CombineExpressionsWithLiteralsMill.combineExpressionsWithLiteralsScopeBuilder().build());
    aconstr.getSpannedScope().add(t);
    FieldSymbol sParam = field("s", sType);
    FieldSymbol tParam = field("t", tType);
    MethodSymbol set = OOSymbolsMill.methodSymbolBuilder()
        .setName("set")
        .setReturnType(_StringSymType)
        .build();
    set.setSpannedScope(CombineExpressionsWithLiteralsMill.combineExpressionsWithLiteralsScopeBuilder().build());
    set.getSpannedScope().add(sParam);
    set.getSpannedScope().add(tParam);
    set.getSpannedScope().add(t);
    set.getSpannedScope().add(s);
    OOTypeSymbol a = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(CombineExpressionsWithLiteralsMill.combineExpressionsWithLiteralsScopeBuilder().build())
        .setName("A")
        .setSuperTypesList(Lists.newArrayList(aSuperType))
        .setEnclosingScope(scope)
        .build();
    a.setMethodList(Lists.newArrayList(test, aconstr, get, set));
    a.getSpannedScope().setEnclosingScope(a.getEnclosingScope());
    test.getSpannedScope().setEnclosingScope(a.getSpannedScope());
    get.getSpannedScope().setEnclosingScope(a.getSpannedScope());
    aconstr.getSpannedScope().setEnclosingScope(a.getSpannedScope());
    set.getSpannedScope().setEnclosingScope(a.getSpannedScope());
    SymTypeExpression aType = SymTypeExpressionFactory.createTypeObject("A",scope);
    aconstr.setReturnType(aType);
    add2scope(scope, a);

    flatExpressionScopeSetter = new FlatExpressionScopeSetter((CombineExpressionsWithLiteralsScope) a.getSpannedScope());

    //basic test
    Optional<ASTExpression> pgie1 = p.parse_StringExpression("<int>test()");
    assertTrue(pgie1.isPresent());
    ASTExpression pg1 = pgie1.get();
    pg1.accept(flatExpressionScopeSetter);
    assertEquals("int",tc.typeOf(pg1).print());

    //test with variable return type of method
    Optional<ASTExpression> pgie2 = p.parse_StringExpression("<int>get()");
    assertTrue(pgie2.isPresent());
    ASTExpression pg2 = pgie2.get();
    pg2.accept(flatExpressionScopeSetter);
    assertEquals("int",tc.typeOf(pg2).print());

    //test with more than one type variable and with args in method
    Optional<ASTExpression> pgie3 = p.parse_StringExpression("<int,double>set(3.2,4)");
    assertTrue(pgie3.isPresent());
    ASTExpression pg3 = pgie3.get();
    pg3.accept(flatExpressionScopeSetter);
    assertEquals("String",tc.typeOf(pg3).print());

    //test with super()
    Optional<ASTExpression> pgie4 = p.parse_StringExpression("<int>super()");
    assertTrue(pgie4.isPresent());
    ASTExpression pg4 = pgie4.get();
    pg4.accept(flatExpressionScopeSetter);
    assertEquals("ASuper",tc.typeOf(pg4).print());

    //test with this()
    Optional<ASTExpression> pgie5 = p.parse_StringExpression("<int>this()");
    assertTrue(pgie5.isPresent());
    ASTExpression pg5 = pgie5.get();
    pg5.accept(flatExpressionScopeSetter);
    assertEquals("A",tc.typeOf(pg5).print());
  }

  @Test
  public void failDeriveSymTypeOfPrimaryGenericInvocationExpression() throws IOException {
    //<TypeArg>super.method()
    MethodSymbol help = OOSymbolsMill.methodSymbolBuilder()
        .setName("help")
        .setReturnType(_doubleSymType)
        .build();
    help.setSpannedScope(CombineExpressionsWithLiteralsMill.combineExpressionsWithLiteralsScopeBuilder().build());
    OOTypeSymbol sup = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(CombineExpressionsWithLiteralsMill.combineExpressionsWithLiteralsScopeBuilder().build())
        .setName("Sup")
        .setEnclosingScope(scope)
        .build();
    sup.addMethodSymbol(help);
    sup.setIsClass(true);
    sup.getSpannedScope().setEnclosingScope(sup.getEnclosingScope());
    help.getSpannedScope().setEnclosingScope(sup.getSpannedScope());
    add2scope(scope,sup);
    SymTypeExpression supType = SymTypeExpressionFactory.createTypeObject("Sup",scope);
    OOTypeSymbol sub = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(CombineExpressionsWithLiteralsMill.combineExpressionsWithLiteralsScopeBuilder().build())
        .setName("Sub")
        .setSuperTypesList(Lists.newArrayList(supType))
        .setEnclosingScope(scope)
        .build();
    sub.getSpannedScope().setEnclosingScope(sub.getEnclosingScope());
    add2scope(scope,sub);

    flatExpressionScopeSetter = new FlatExpressionScopeSetter((CombineExpressionsWithLiteralsScope) sub.getSpannedScope());

    Optional<ASTExpression> pgie1 = p.parse_StringExpression("<int>super.help()");
    assertTrue(pgie1.isPresent());
    ASTExpression pg1 = pgie1.get();
    pg1.accept(flatExpressionScopeSetter);

    try {
      tc.typeOf(pg1);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0285"));
    }
  }

  @Test
  public void failDeriveSymTypeOfPrimaryGenericInvocationExpression2() throws IOException {
    //<TypeArg>super.<TypeArg>method(arg)
    FieldSymbol xParam = field("x", _intSymType);
    MethodSymbol help = OOSymbolsMill.methodSymbolBuilder()
        .setName("help")
        .setReturnType(_doubleSymType)
        .build();
    help.setSpannedScope(CombineExpressionsWithLiteralsMill.combineExpressionsWithLiteralsScopeBuilder().build());
    help.getSpannedScope().add(typeVariable("T"));
    help.getSpannedScope().add(xParam);
    OOTypeSymbol sup = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(CombineExpressionsWithLiteralsMill.combineExpressionsWithLiteralsScopeBuilder().build())
        .setName("Sup")
        .setEnclosingScope(scope)
        .build();
    sup.addMethodSymbol(help);
    sup.setIsClass(true);
    sup.getSpannedScope().setEnclosingScope(sup.getEnclosingScope());
    help.getSpannedScope().setEnclosingScope(sup.getSpannedScope());
    add2scope(scope,sup);
    SymTypeExpression supType = SymTypeExpressionFactory.createTypeObject("Sup",scope);
    OOTypeSymbol sub = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(CombineExpressionsWithLiteralsMill.combineExpressionsWithLiteralsScopeBuilder().build())
        .setName("Sub")
        .setSuperTypesList(Lists.newArrayList(supType))
        .setEnclosingScope(scope)
        .build();
    sub.getSpannedScope().setEnclosingScope(sub.getEnclosingScope());
    add2scope(scope,sub);

    flatExpressionScopeSetter = new FlatExpressionScopeSetter((CombineExpressionsWithLiteralsScope) sub.getSpannedScope());

    Optional<ASTExpression> pgie1 = p.parse_StringExpression("<int>super.<double>help(2)");
    assertTrue(pgie1.isPresent());
    ASTExpression pg1 = pgie1.get();
    pg1.accept(flatExpressionScopeSetter);

    try {
      tc.typeOf(pg1);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0285"));
    }
  }

  @Test
  public void failDeriveSymTypeOfPrimaryGenericInvocationExpression3() throws IOException {
    //<TypeArg>super.field
    FieldSymbol test = field("test",_booleanSymType);
    OOTypeSymbol sup = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(CombineExpressionsWithLiteralsMill.combineExpressionsWithLiteralsScopeBuilder().build())
        .setName("Sup")
        .setEnclosingScope(scope)
        .build();
    sup.addFieldSymbol(test);
    sup.setIsClass(true);
    sup.getSpannedScope().setEnclosingScope(sup.getEnclosingScope());
    add2scope(scope,sup);
    SymTypeExpression supType = SymTypeExpressionFactory.createTypeObject("Sup",scope);
    OOTypeSymbol sub = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(CombineExpressionsWithLiteralsMill.combineExpressionsWithLiteralsScopeBuilder().build())
        .setName("Sub")
        .setSuperTypesList(Lists.newArrayList(supType))
        .setEnclosingScope(scope)
        .build();
    sub.getSpannedScope().setEnclosingScope(sub.getEnclosingScope());
    add2scope(scope,sub);

    flatExpressionScopeSetter = new FlatExpressionScopeSetter((CombineExpressionsWithLiteralsScope) sub.getSpannedScope());

    Optional<ASTExpression> pgie1 = p.parse_StringExpression("<int>super.test");
    assertTrue(pgie1.isPresent());
    ASTExpression pg1 = pgie1.get();
    pg1.accept(flatExpressionScopeSetter);
    try {
      tc.typeOf(pg1);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0285"));
    }
  }

  @Test
  public void deriveSymTypeOfGenericInvocationExpression() throws IOException {
    //build symbol table for the test
    TypeVarSymbol t = typeVariable("T");
    MethodSymbol test = OOSymbolsMill.methodSymbolBuilder()
        .setName("test")
        .setReturnType(_charSymType)
        .build();
    test.setSpannedScope(CombineExpressionsWithLiteralsMill.combineExpressionsWithLiteralsScopeBuilder().build());
    test.getSpannedScope().add(t);
    OOTypeSymbol a = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(CombineExpressionsWithLiteralsMill.combineExpressionsWithLiteralsScopeBuilder().build())
        .setName("A")
        .setEnclosingScope(scope)
        .build();
    a.addMethodSymbol(test);
    a.getSpannedScope().setEnclosingScope(a.getEnclosingScope());
    test.getSpannedScope().setEnclosingScope(a.getSpannedScope());
    SymTypeExpression aType = SymTypeExpressionFactory.createTypeObject("A",scope);
    FieldSymbol aField = field("a",aType);
    add2scope(scope,aField);
    add2scope(scope,a);

    flatExpressionScopeSetter = new FlatExpressionScopeSetter(scope);

    Optional<ASTExpression> gie1 = p.parse_StringExpression("a.<int>test()");
    assertTrue(gie1.isPresent());
    ASTExpression g1 = gie1.get();
    g1.accept(flatExpressionScopeSetter);
    assertEquals("char",tc.typeOf(g1).print());
  }

  @Test
  public void failDeriveSymTypeOfGenericInvocationExpression() throws IOException{
    //a.<int>this()
    TypeVarSymbol t = typeVariable("T");
    MethodSymbol constr = OOSymbolsMill.methodSymbolBuilder()
        .setName("A")
        .setReturnType(_charSymType)
        .build();
    constr.setSpannedScope(CombineExpressionsWithLiteralsMill.combineExpressionsWithLiteralsScopeBuilder().build());
    constr.getSpannedScope().add(t);
    OOTypeSymbol a = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(CombineExpressionsWithLiteralsMill.combineExpressionsWithLiteralsScopeBuilder().build())
        .setName("A")
        .setEnclosingScope(scope)
        .build();
    a.addMethodSymbol(constr);
    a.getSpannedScope().setEnclosingScope(a.getEnclosingScope());
    constr.getSpannedScope().setEnclosingScope(a.getSpannedScope());
    SymTypeExpression aType = SymTypeExpressionFactory.createTypeObject("A",scope);
    constr.setReturnType(aType);
    FieldSymbol aField = field("a",aType);
    add2scope(scope,aField);
    add2scope(scope,a);

    flatExpressionScopeSetter = new FlatExpressionScopeSetter(scope);

    Optional<ASTExpression> gie1 = p.parse_StringExpression("a.<int>this()");
    assertTrue(gie1.isPresent());
    ASTExpression g1 = gie1.get();
    g1.accept(flatExpressionScopeSetter);

    try{
      tc.typeOf(g1);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0282"));
    }
  }

  @Test
  public void failDeriveSymTypeOfGenericInvocationExpression2() throws IOException{
    //a.<int>super()
    TypeVarSymbol t = typeVariable("T");
    MethodSymbol constr = OOSymbolsMill.methodSymbolBuilder()
        .setName("ASuper")
        .setReturnType(_charSymType)
        .build();
    constr.setSpannedScope(CombineExpressionsWithLiteralsMill.combineExpressionsWithLiteralsScopeBuilder().build());
    constr.getSpannedScope().add(t);
    OOTypeSymbol aSuper = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(CombineExpressionsWithLiteralsMill.combineExpressionsWithLiteralsScopeBuilder().build())
        .setName("ASuper")
        .setEnclosingScope(scope)
        .build();
    aSuper.addMethodSymbol(constr);
    aSuper.getSpannedScope().setEnclosingScope(aSuper.getEnclosingScope());
    constr.getSpannedScope().setEnclosingScope(aSuper.getSpannedScope());
    SymTypeExpression asupertype = SymTypeExpressionFactory.createTypeObject("ASuper",scope);
    constr.setReturnType(asupertype);
    add2scope(scope,aSuper);
    OOTypeSymbol a = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(CombineExpressionsWithLiteralsMill.combineExpressionsWithLiteralsScopeBuilder().build())
        .setName("A")
        .setEnclosingScope(scope)
        .build();
    a.getSpannedScope().setEnclosingScope(a.getEnclosingScope());
    SymTypeExpression aType = SymTypeExpressionFactory.createTypeObject("A",scope);
    FieldSymbol aField = field("a",aType);
    add2scope(scope,aField);
    add2scope(scope,a);

    flatExpressionScopeSetter = new FlatExpressionScopeSetter(scope);

    Optional<ASTExpression> gie1 = p.parse_StringExpression("a.<int>super()");
    assertTrue(gie1.isPresent());
    ASTExpression g1 = gie1.get();
    g1.accept(flatExpressionScopeSetter);

    try{
      tc.typeOf(g1);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0282"));
    }
  }

  @Test
  public void failDeriveSymTypeOfGenericInvocationExpression3() throws IOException{
    //Type.<int>test()
    //build symbol table for the test
    TypeVarSymbol t = typeVariable("T");
    MethodSymbol test = OOSymbolsMill.methodSymbolBuilder()
        .setName("test")
        .setReturnType(_charSymType)
        .build();
    test.setSpannedScope(CombineExpressionsWithLiteralsMill.combineExpressionsWithLiteralsScopeBuilder().build());
    test.getSpannedScope().add(t);
    OOTypeSymbol a = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(CombineExpressionsWithLiteralsMill.combineExpressionsWithLiteralsScopeBuilder().build())
        .setName("A")
        .setEnclosingScope(scope)
        .build();
    a.addMethodSymbol(test);
    a.getSpannedScope().setEnclosingScope(a.getEnclosingScope());
    test.getSpannedScope().setEnclosingScope(a.getSpannedScope());
    add2scope(scope,a);

    flatExpressionScopeSetter = new FlatExpressionScopeSetter(scope);

    Optional<ASTExpression> gie1 = p.parse_StringExpression("A.<int>test()");
    assertTrue(gie1.isPresent());
    ASTExpression g1 = gie1.get();
    g1.accept(flatExpressionScopeSetter);

    try{
      tc.typeOf(g1);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0282"));
    }
  }

  @Test
  public void testGenericInvocationExpressionStatic() throws IOException {
    //Type.<int>test() with static field test
    //build symbol table for the test
    TypeVarSymbol t = typeVariable("T");
    MethodSymbol test = OOSymbolsMill.methodSymbolBuilder()
        .setName("test")
        .setReturnType(_charSymType)
        .build();
    test.setSpannedScope(CombineExpressionsWithLiteralsMill.combineExpressionsWithLiteralsScopeBuilder().build());
    test.getSpannedScope().add(t);
    test.setIsStatic(true);
    OOTypeSymbol a = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(CombineExpressionsWithLiteralsMill.combineExpressionsWithLiteralsScopeBuilder().build())
        .setName("A")
        .setEnclosingScope(scope)
        .build();
    a.addMethodSymbol(test);
    a.getSpannedScope().setEnclosingScope(a.getEnclosingScope());
    test.getSpannedScope().setEnclosingScope(a.getSpannedScope());
    add2scope(scope,a);

    flatExpressionScopeSetter = new FlatExpressionScopeSetter(scope);

    Optional<ASTExpression> gie1 = p.parse_StringExpression("A.<int>test()");
    assertTrue(gie1.isPresent());
    ASTExpression g1 = gie1.get();
    g1.accept(flatExpressionScopeSetter);

    assertEquals("char",tc.typeOf(g1).print());
  }

  @Test
  public void testDeriveFromCreatorExpressionAnonymousClass() throws IOException {
    /*Test cases:
    1) Default-Constructor, Creator-Expression without Arguments
    2) Constructor without Arguments, Creator-Expression without Arguments
    3) One Constructor without Arguments and one with Arguments, Creator-Expression with exactly fitting Arguments
    4) One Constructor without Arguments and one with Arguments, Creator-Expression with subtypes of arguments
     */

    //Bsp1
    OOTypeSymbol bsp1 = CombineExpressionsWithLiteralsMill.oOTypeSymbolBuilder()
        .setName("Bsp1")
        .setEnclosingScope(CombineExpressionsWithLiteralsMill.combineExpressionsWithLiteralsScopeBuilder().build())
        .setSpannedScope(CombineExpressionsWithLiteralsMill.combineExpressionsWithLiteralsScopeBuilder().build())
        .build();

    add2scope(scope, bsp1);

    //Bsp2
    MethodSymbol bsp2constr = CombineExpressionsWithLiteralsMill.methodSymbolBuilder()
        .setName("Bsp2")
        .setReturnType(_intSymType)
        .build();

    bsp2constr.setSpannedScope(CombineExpressionsWithLiteralsMill.combineExpressionsWithLiteralsScopeBuilder().build());

    OOTypeSymbol bsp2 = CombineExpressionsWithLiteralsMill.oOTypeSymbolBuilder()
        .setName("Bsp2")
        .setEnclosingScope(CombineExpressionsWithLiteralsMill.combineExpressionsWithLiteralsScopeBuilder().build())
        .setSpannedScope(CombineExpressionsWithLiteralsMill.combineExpressionsWithLiteralsScopeBuilder().build())
        .build();
    bsp2.addMethodSymbol(bsp2constr);

    SymTypeExpression bsp2Sym = SymTypeExpressionFactory.createTypeObject("Bsp2",scope);
    bsp2constr.setReturnType(bsp2Sym);

    bsp2constr.setEnclosingScope(bsp2.getSpannedScope());
    bsp2constr.getSpannedScope().setEnclosingScope(bsp2constr.getEnclosingScope());
    add2scope(bsp2.getSpannedScope(), bsp2constr);
    add2scope(scope, bsp2);

    //Bsp3
    FieldSymbol field1 = CombineExpressionsWithLiteralsMill.fieldSymbolBuilder()
        .setName("a")
        .setType(_intSymType)
        .build();

    FieldSymbol field2 = CombineExpressionsWithLiteralsMill.fieldSymbolBuilder()
        .setName("b")
        .setType(_doubleSymType)
        .build();

    //first constructor with arguments
    MethodSymbol bsp3constr = CombineExpressionsWithLiteralsMill.methodSymbolBuilder()
        .setName("Bsp3")
        .setReturnType(_intSymType)
        .build();

    bsp3constr.setSpannedScope(CombineExpressionsWithLiteralsMill.combineExpressionsWithLiteralsScopeBuilder().build());
    add2scope(bsp3constr.getSpannedScope(),field1);
    add2scope(bsp3constr.getSpannedScope(),field2);

    //second constructor without arguments
    MethodSymbol bsp3constr2 = CombineExpressionsWithLiteralsMill.methodSymbolBuilder()
        .setName("Bsp3")
        .setReturnType(_intSymType)
        .build();
    bsp3constr2.setSpannedScope(CombineExpressionsWithLiteralsMill.combineExpressionsWithLiteralsScopeBuilder().build());

    OOTypeSymbol bsp3 = CombineExpressionsWithLiteralsMill.oOTypeSymbolBuilder()
        .setName("Bsp3")
        .setEnclosingScope(CombineExpressionsWithLiteralsMill.combineExpressionsWithLiteralsScopeBuilder().build())
        .setSpannedScope(CombineExpressionsWithLiteralsMill.combineExpressionsWithLiteralsScopeBuilder().build())
        .build();
    bsp3.addMethodSymbol(bsp3constr2);

    SymTypeExpression bsp3Sym = SymTypeExpressionFactory.createTypeObject("Bsp3", scope);
    bsp3constr.setReturnType(bsp3Sym);
    bsp3constr2.setReturnType(bsp3Sym);

    bsp3constr.setEnclosingScope(bsp3.getSpannedScope());
    bsp3constr.getSpannedScope().setEnclosingScope(bsp3constr.getEnclosingScope());
    add2scope(bsp3.getSpannedScope(), bsp3constr);
    add2scope(bsp3.getSpannedScope(), bsp3constr2);
    add2scope(scope, bsp3);

    flatExpressionScopeSetter = new FlatExpressionScopeSetter(scope);

    Optional<ASTExpression> new1 = p.parse_StringExpression("new Bsp1()");
    Optional<ASTExpression> new2 = p.parse_StringExpression("new Bsp2()");
    Optional<ASTExpression> new3 = p.parse_StringExpression("new Bsp3(3,5.6)");
    Optional<ASTExpression> new4 = p.parse_StringExpression("new Bsp3('a',4)");

    assertTrue(new1.isPresent());
    assertTrue(new2.isPresent());
    assertTrue(new3.isPresent());
    assertTrue(new4.isPresent());

    ASTExpression n1 = new1.get();
    ASTExpression n2 = new2.get();
    ASTExpression n3 = new3.get();
    ASTExpression n4 = new4.get();

    n1.accept(flatExpressionScopeSetter);
    n2.accept(flatExpressionScopeSetter);
    n3.accept(flatExpressionScopeSetter);
    n4.accept(flatExpressionScopeSetter);

    assertEquals("Bsp1",tc.typeOf(n1).print());
    assertEquals("Bsp2",tc.typeOf(n2).print());
    assertEquals("Bsp3",tc.typeOf(n3).print());
    assertEquals("Bsp3",tc.typeOf(n4).print());
  }

  @Test
  public void failDeriveFromCreatorExpressionAnonymousClass1() throws IOException {
    //1) Error when using primitive types
    Optional<ASTExpression> new1 = p.parse_StringExpression("new int()");
    assertTrue(new1.isPresent());
    ASTExpression n1 = new1.get();
    n1.accept(flatExpressionScopeSetter);
    try{
      tc.typeOf(n1);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0312"));
    }
  }

  @Test
  public void failDeriveFromCreatorExpressionAnonymousClass2() throws IOException {
    //2) No constructor, Creator-Expression with Arguments
    //Bsp2
    OOTypeSymbol bsp2 = CombineExpressionsWithLiteralsMill.oOTypeSymbolBuilder()
        .setName("Bsp2")
        .setEnclosingScope(CombineExpressionsWithLiteralsMill.combineExpressionsWithLiteralsScopeBuilder().build())
        .setSpannedScope(CombineExpressionsWithLiteralsMill.combineExpressionsWithLiteralsScopeBuilder().build())
        .build();

    add2scope(scope, bsp2);
    flatExpressionScopeSetter = new FlatExpressionScopeSetter(scope);

    Optional<ASTExpression> new2 = p.parse_StringExpression("new Bsp2(3,4)");
    assertTrue(new2.isPresent());
    ASTExpression n2 = new2.get();
    n2.accept(flatExpressionScopeSetter);
    try{
      tc.typeOf(n2);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0312"));
    }
  }

  @Test
  public void failDeriveFromCreatorExpressionAnonymousClass3() throws IOException {
    //3) Creator-Expression with wrong number of Arguments -> 0 Arguments, so that it also checks that the Default-constructor is not invoked in this case
    //Bsp3
    MethodSymbol bsp3constr = CombineExpressionsWithLiteralsMill.methodSymbolBuilder()
        .setName("Bsp3")
        .setReturnType(_intSymType)
        .build();

    bsp3constr.setSpannedScope(CombineExpressionsWithLiteralsMill.combineExpressionsWithLiteralsScopeBuilder().build());
    FieldSymbol field1 = CombineExpressionsWithLiteralsMill.fieldSymbolBuilder()
        .setName("a")
        .setType(_intSymType)
        .build();

    FieldSymbol field2 = CombineExpressionsWithLiteralsMill.fieldSymbolBuilder()
        .setName("b")
        .setType(_doubleSymType)
        .build();

    add2scope(bsp3constr.getSpannedScope(), field1);
    add2scope(bsp3constr.getSpannedScope(), field2);

    OOTypeSymbol bsp3 = CombineExpressionsWithLiteralsMill.oOTypeSymbolBuilder()
        .setName("Bsp3")
        .setEnclosingScope(CombineExpressionsWithLiteralsMill.combineExpressionsWithLiteralsScopeBuilder().build())
        .setSpannedScope(CombineExpressionsWithLiteralsMill.combineExpressionsWithLiteralsScopeBuilder().build())
        .build();
    bsp3.addMethodSymbol(bsp3constr);

    SymTypeExpression bsp3Sym = SymTypeExpressionFactory.createTypeObject("Bsp3", scope);
    bsp3constr.setReturnType(bsp3Sym);

    bsp3constr.setEnclosingScope(bsp3.getSpannedScope());
    bsp3constr.getSpannedScope().setEnclosingScope(bsp3constr.getEnclosingScope());
    add2scope(bsp3.getSpannedScope(), bsp3constr);
    add2scope(scope, bsp3);
    flatExpressionScopeSetter = new FlatExpressionScopeSetter(scope);

    Optional<ASTExpression> new3 = p.parse_StringExpression("new Bsp3()");
    assertTrue(new3.isPresent());
    ASTExpression n3 = new3.get();
    n3.accept(flatExpressionScopeSetter);
    try{
      tc.typeOf(n3);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0312"));
    }
  }

  @Test
  public void failDeriveFromCreatorExpressionAnonymousClass4() throws IOException {
    //4) Creator-Expression with correct number of Arguments, but not compatible arguments
    //Bsp4
    FieldSymbol field1 = CombineExpressionsWithLiteralsMill.fieldSymbolBuilder()
        .setName("a")
        .setType(_intSymType)
        .build();

    FieldSymbol field2 = CombineExpressionsWithLiteralsMill.fieldSymbolBuilder()
        .setName("b")
        .setType(_doubleSymType)
        .build();

    MethodSymbol bsp4constr = CombineExpressionsWithLiteralsMill.methodSymbolBuilder()
        .setName("Bsp4")
        .setReturnType(_intSymType)
        .build();

    OOTypeSymbol bsp4 = CombineExpressionsWithLiteralsMill.oOTypeSymbolBuilder()
        .setName("Bsp4")
        .setEnclosingScope(CombineExpressionsWithLiteralsMill.combineExpressionsWithLiteralsScopeBuilder().build())
        .setSpannedScope(CombineExpressionsWithLiteralsMill.combineExpressionsWithLiteralsScopeBuilder().build())
        .build();
    bsp4.addMethodSymbol(bsp4constr);

    bsp4constr.setSpannedScope(CombineExpressionsWithLiteralsMill.combineExpressionsWithLiteralsScopeBuilder().build());
    add2scope(bsp4constr.getSpannedScope(),field1);
    add2scope(bsp4constr.getSpannedScope(),field2);
    SymTypeExpression bsp4Sym = SymTypeExpressionFactory.createTypeObject("Bsp4",scope);
    bsp4constr.setReturnType(bsp4Sym);
    add2scope(bsp4.getSpannedScope(), bsp4constr);
    add2scope(scope,bsp4);
    flatExpressionScopeSetter = new FlatExpressionScopeSetter(scope);

    Optional<ASTExpression> new4 = p.parse_StringExpression("new Bsp4(true,4)");
    assertTrue(new4.isPresent());
    ASTExpression n4 = new4.get();
    n4.accept(flatExpressionScopeSetter);

    try{
      tc.typeOf(n4);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0312"));
    }
  }

  @Test
  public void testDeriveSymTypeOfCreatorExpressionArrayCreator() throws IOException {
    //Tests mit ArrayInitByExpression

      //Test mit double[3][4] --> double[][]
    Optional<ASTExpression> creator1 = p.parse_StringExpression("new double[3][4]");
    assertTrue(creator1.isPresent());
    ASTExpression c1 = creator1.get();
    c1.accept(flatExpressionScopeSetter);
    assertEquals("double[][]", tc.typeOf(c1).print());

      //Test mit int[3][][] --> int[3][][]
    Optional<ASTExpression> creator2 = p.parse_StringExpression("new int[3][][]");
    assertTrue(creator2.isPresent());
    ASTExpression c2 = creator2.get();
    c2.accept(flatExpressionScopeSetter);
    assertEquals("int[][][]", tc.typeOf(c2).print());

  }

  @Test
  public void failDeriveFromCreatorExpressionArrayCreator1() throws IOException {
    //Test mit ArrayInitByExpression, keine ganzzahl in Array (z.B. new String[3.4])
    Optional<ASTExpression> creator1 = p.parse_StringExpression("new String[3.4]");
    assertTrue(creator1.isPresent());
    ASTExpression c1 = creator1.get();
    c1.accept(flatExpressionScopeSetter);
    try{
      tc.typeOf(c1);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0315"));
    }
  }

}
