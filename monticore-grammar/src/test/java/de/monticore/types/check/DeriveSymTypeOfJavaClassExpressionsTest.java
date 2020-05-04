/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import com.google.common.collect.Lists;
import de.monticore.expressions.combineexpressionswithliterals._parser.CombineExpressionsWithLiteralsParser;
import de.monticore.expressions.combineexpressionswithliterals._symboltable.CombineExpressionsWithLiteralsSymTabMill;
import de.monticore.expressions.combineexpressionswithliterals._symboltable.ICombineExpressionsWithLiteralsScope;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.prettyprint.CombineExpressionsWithLiteralsPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.typesymbols._symboltable.*;
import de.se_rwth.commons.logging.Log;
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
    Log.init();
    Log.enableFailQuick(false);
  }

  @Before
  public void setupForEach() {
    // Setting up a Scope Infrastructure (without a global Scope)
    DefsTypeBasic.setup();
    scope =
        CombineExpressionsWithLiteralsSymTabMill.combineExpressionsWithLiteralsScopeBuilder()
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
    flatExpressionScopeSetter = new FlatExpressionScopeSetter(scope);
    LogStub.init();
  }

  // Parer used for convenience:
  // (may be any other Parser that understands CommonExpressions)
  CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();

  // This is an auxiliary
  DeriveSymTypeOfCombineExpressionsDelegator derLit = new DeriveSymTypeOfCombineExpressionsDelegator(TypeSymbolsSymTabMill.typeSymbolsScopeBuilder().build(),
      new CombineExpressionsWithLiteralsPrettyPrinter(new IndentPrinter()));

  // This is the TypeChecker under Test:
  TypeCheck tc = new TypeCheck(null, derLit);

  /*--------------------------------------------------- TESTS ---------------------------------------------------------*/


  @Test
  public void deriveFromPrimarySuperExpression() throws IOException {
    TypeSymbol supType = TypeSymbolsSymTabMill.typeSymbolBuilder()
        .setSpannedScope(TypeSymbolsSymTabMill.typeSymbolsScopeBuilder().build())
        .setName("A")
        .setEnclosingScope(scope)
        .build();
    SymTypeExpression sup = SymTypeExpressionFactory.createTypeObject("A",scope);
    supType.setClass(true);
    add2scope(scope,supType);

    MethodSymbol get = TypeSymbolsSymTabMill.methodSymbolBuilder()
        .setName("get")
        .setReturnType(_voidSymType)
        .build();
    get.setSpannedScope(CombineExpressionsWithLiteralsSymTabMill.combineExpressionsWithLiteralsScopeBuilder().build());
    TypeSymbol p = TypeSymbolsSymTabMill.typeSymbolBuilder()
        .setSpannedScope(CombineExpressionsWithLiteralsSymTabMill.combineExpressionsWithLiteralsScopeBuilder().build())
        .setName("AB")
        .setSuperTypeList(Lists.newArrayList(sup))
        .setEnclosingScope(scope)
        .build();
    p.setClass(true);
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
    MethodSymbol get = TypeSymbolsSymTabMill.methodSymbolBuilder()
        .setName("get")
        .setReturnType(_voidSymType)
        .build();
    get.setSpannedScope(CombineExpressionsWithLiteralsSymTabMill.combineExpressionsWithLiteralsScopeBuilder().build());
    TypeSymbol p = TypeSymbolsSymTabMill.typeSymbolBuilder()
        .setSpannedScope(CombineExpressionsWithLiteralsSymTabMill.combineExpressionsWithLiteralsScopeBuilder().build())
        .setMethodList(Lists.newArrayList(get))
        .setName("AB")
        .setEnclosingScope(scope)
        .build();
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
    TypeSymbol outer = TypeSymbolsSymTabMill.typeSymbolBuilder()
        .setSpannedScope(CombineExpressionsWithLiteralsSymTabMill.combineExpressionsWithLiteralsScopeBuilder().build())
        .setName("Outer")
        .setEnclosingScope(scope)
        .build();
    outer.getSpannedScope().setEnclosingScope(outer.getEnclosingScope());
    add2scope(scope,outer);
    MethodSymbol methodInner = TypeSymbolsSymTabMill.methodSymbolBuilder()
        .setName("methodInner")
        .setReturnType(_voidSymType)
        .build();
    methodInner.setSpannedScope(CombineExpressionsWithLiteralsSymTabMill.combineExpressionsWithLiteralsScopeBuilder().build());
    TypeSymbol inner = TypeSymbolsSymTabMill.typeSymbolBuilder()
        .setSpannedScope(CombineExpressionsWithLiteralsSymTabMill.combineExpressionsWithLiteralsScopeBuilder().build())
        .setName("Inner")
        .setMethodList(Lists.newArrayList(methodInner))
        .setEnclosingScope(outer.getSpannedScope())
        .build();
    inner.getSpannedScope().setEnclosingScope(inner.getEnclosingScope());
    methodInner.getSpannedScope().setEnclosingScope(inner.getSpannedScope());
    add2scope(outer.getEnclosingScope(),inner);
    MethodSymbol methodInnerInner = TypeSymbolsSymTabMill.methodSymbolBuilder()
        .setName("methodInnerInner")
        .setReturnType(_voidSymType)
        .build();
    methodInnerInner.setSpannedScope(CombineExpressionsWithLiteralsSymTabMill.combineExpressionsWithLiteralsScopeBuilder().build());
    TypeSymbol innerinner = TypeSymbolsSymTabMill.typeSymbolBuilder()
        .setSpannedScope(CombineExpressionsWithLiteralsSymTabMill.combineExpressionsWithLiteralsScopeBuilder().build())
        .setName("InnerInner")
        .setMethodList(Lists.newArrayList(methodInnerInner))
        .setEnclosingScope(inner.getSpannedScope())
        .build();
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
    TypeSymbol fail = TypeSymbolsSymTabMill.typeSymbolBuilder()
        .setSpannedScope(CombineExpressionsWithLiteralsSymTabMill.combineExpressionsWithLiteralsScopeBuilder().build())
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
    TypeSymbol list = TypeSymbolsSymTabMill.typeSymbolBuilder()
        .setSpannedScope(CombineExpressionsWithLiteralsSymTabMill.combineExpressionsWithLiteralsScopeBuilder().build())
        .setName("List")
        .setTypeParameterList(Lists.newArrayList(t))
        .setEnclosingScope(scope)
        .build();
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
    TypeSymbol test = TypeSymbolsSymTabMill.typeSymbolBuilder()
        .setSpannedScope(CombineExpressionsWithLiteralsSymTabMill.combineExpressionsWithLiteralsScopeBuilder().build())
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
     * one class SuperOuter
     * one class Outer that extends SuperOuter with an inner class Inner with a method methodInner
     * -> try reaching the get()-Method of SuperOuter from this method
     * the class Inner contains an inner class InnerInner with a method methodInnerInner
     * -> try reaching the get()-Method of SuperOuter from this method
     * */
    //build infrastructure for previous comment
    MethodSymbol test = method("test",_intSymType);
    FieldSymbol field = field("field",_intSymType);
    TypeSymbol superOuter = type("SuperOuter",Lists.newArrayList(test),Lists.newArrayList(field),Lists.newArrayList(),Lists.newArrayList(),scope);
    superOuter.setClass(true);
    SymTypeExpression superOuterType = SymTypeExpressionFactory.createTypeObject("SuperOuter",scope);
    add2scope(scope,superOuter);

    TypeSymbol outer = type("Outer",Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(superOuterType),Lists.newArrayList(),scope);
    add2scope(scope,outer);
    MethodSymbol methodInner = method("methodInner",_voidSymType);
    TypeSymbol inner = type("Inner",Lists.newArrayList(methodInner),Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(),outer.getSpannedScope());
    add2scope(outer.getEnclosingScope(),inner);
    MethodSymbol methodInnerInner = method("methodInnerInner",_voidSymType);
    TypeSymbol innerinner = type("InnerInner", Lists.newArrayList(methodInnerInner),Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(),inner.getSpannedScope());
    add2scope(inner.getSpannedScope(),innerinner);

    Optional<ASTExpression> super1 = p.parse_StringExpression("Outer.super.test()");
    Optional<ASTExpression> super2 = p.parse_StringExpression("Outer.super.field");

    tc = new TypeCheck(null,derLit);

    assertTrue(super1.isPresent());
    assertTrue(super2.isPresent());

    assertEquals("int",tc.typeOf(super1.get()).print());
    assertEquals("int",tc.typeOf(super2.get()).print());

    derLit = new DeriveSymTypeOfCombineExpressionsDelegator(methodInnerInner.getSpannedScope(),new CombineExpressionsWithLiteralsPrettyPrinter(new IndentPrinter()));
    tc = new TypeCheck(null,derLit);

    assertEquals("int",tc.typeOf(super1.get()).print());
  }

  @Test
  public void failDeriveFromSuperExpression1() throws IOException{
    //.super in enclosing class
    TypeSymbol fail = type("Fail",Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(),scope);
    add2scope(scope,fail);

    tc = new TypeCheck(null,derLit);

    Optional<ASTExpression> this1 = p.parse_StringExpression("Fail.super.get()");

    assertTrue(this1.isPresent());

    try{
      tc.typeOf(this1.get());
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0261"));
    }
  }

  @Test
  public void failDeriveFromSuperExpression2() throws IOException{
    //.super without a type
    Optional<ASTExpression> this1 = p.parse_StringExpression("person1.super.get()");

    assertTrue(this1.isPresent());

    try{
      tc.typeOf(this1.get());
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0259"));
    }
  }

  @Test
  public void failDeriveFromSuperExpression3() throws IOException{
    //.super with more than one super type
    //first super type
    MethodSymbol test = method("test",_intSymType);
    TypeSymbol superOuter = type("SuperOuter",Lists.newArrayList(test),Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(),scope);
    superOuter.setClass(true);
    SymTypeExpression superOuterType = SymTypeExpressionFactory.createTypeObject("SuperOuter",scope);
    add2scope(scope,superOuter);

    //second super type
    TypeSymbol superOuterTwo = type("SuperOuterTwo",Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(),scope);
    superOuterTwo.setClass(true);
    SymTypeExpression superOuterTwoType = SymTypeExpressionFactory.createTypeObject("SuperOuterTwo",scope);
    add2scope(scope,superOuterTwo);

    TypeSymbol outer = type("Outer",Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(superOuterType, superOuterTwoType),Lists.newArrayList(),scope);
    add2scope(scope,outer);
    MethodSymbol methodInner = method("methodInner",_voidSymType);
    TypeSymbol inner = type("Inner",Lists.newArrayList(methodInner),Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(),outer.getSpannedScope());
    add2scope(outer.getEnclosingScope(),inner);
    MethodSymbol methodInnerInner = method("methodInnerInner",_voidSymType);
    TypeSymbol innerinner = type("InnerInner", Lists.newArrayList(methodInnerInner),Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(),inner.getSpannedScope());
    add2scope(inner.getSpannedScope(),innerinner);

    tc = new TypeCheck(null,derLit);

    Optional<ASTExpression> super1 = p.parse_StringExpression("Outer.super.test()");

    assertTrue(super1.isPresent());

    try{
      tc.typeOf(super1.get());
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0261"));
    }
  }

  @Test
  public void failDeriveFromSuperExpression4() throws IOException{
    //.super without a super type
    TypeSymbol outer = type("Outer",Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(),scope);
    add2scope(scope,outer);
    MethodSymbol methodInner = method("methodInner",_voidSymType);
    TypeSymbol inner = type("Inner",Lists.newArrayList(methodInner),Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(),outer.getSpannedScope());
    add2scope(outer.getEnclosingScope(),inner);
    MethodSymbol methodInnerInner = method("methodInnerInner",_voidSymType);
    TypeSymbol innerinner = type("InnerInner", Lists.newArrayList(methodInnerInner),Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(),inner.getSpannedScope());
    add2scope(inner.getSpannedScope(),innerinner);

    tc = new TypeCheck(null,derLit);

    Optional<ASTExpression> super1 = p.parse_StringExpression("Outer.super.test()");

    assertTrue(super1.isPresent());

    try{
      tc.typeOf(super1.get());
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0261"));
    }
  }

  @Test
  public void failDeriveFromSuperExpression5() throws IOException{
    //.super with a super type but a wrong method
    MethodSymbol test = method("test",_intSymType);
    TypeSymbol superOuter = type("SuperOuter",Lists.newArrayList(test),Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(),scope);
    superOuter.setClass(true);
    SymTypeExpression superOuterType = SymTypeExpressionFactory.createTypeObject("SuperOuter",scope);
    add2scope(scope,superOuter);

    TypeSymbol outer = type("Outer",Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(superOuterType),Lists.newArrayList(),scope);
    add2scope(scope,outer);
    MethodSymbol methodInner = method("methodInner",_voidSymType);
    TypeSymbol inner = type("Inner",Lists.newArrayList(methodInner),Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(),outer.getSpannedScope());
    add2scope(outer.getEnclosingScope(),inner);
    MethodSymbol methodInnerInner = method("methodInnerInner",_voidSymType);
    TypeSymbol innerinner = type("InnerInner", Lists.newArrayList(methodInnerInner),Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(),inner.getSpannedScope());
    add2scope(inner.getSpannedScope(),innerinner);

    //SuperOuter does not have a method "get"
    Optional<ASTExpression> super1 = p.parse_StringExpression("Outer.super.get()");

    tc = new TypeCheck(null,derLit);

    assertTrue(super1.isPresent());

    try{
      tc.typeOf(super1.get());
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
    add2scope(scope,t);
    add2scope(scope,s);
    //type ASuper with constructor
    MethodSymbol asuperconstr = method("ASuper",_intSymType);
    asuperconstr.getSpannedScope().add(t);
    TypeSymbol aSuper = type("ASuper",Lists.newArrayList(asuperconstr),Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(),scope);
    aSuper.setClass(true);
    SymTypeExpression aSuperType = SymTypeExpressionFactory.createTypeObject("ASuper",scope);
    asuperconstr.setReturnType(aSuperType);
    add2scope(scope,aSuper);

    //type A with constructor and methods test,get and set
    MethodSymbol test = method("test",_intSymType);
    test.getSpannedScope().add(t);
    MethodSymbol get = method("get",tType);
    get.getSpannedScope().add(t);
    MethodSymbol aconstr = method("A",_intSymType);
    aconstr.getSpannedScope().add(t);
    MethodSymbol set = add(add(method("set",_StringSymType),field("s",sType)),field("t",tType));
    set.getSpannedScope().add(t);
    set.getSpannedScope().add(s);
    TypeSymbol a = type("A",Lists.newArrayList(test,aconstr,get,set),Lists.newArrayList(),Lists.newArrayList(aSuperType),Lists.newArrayList(),scope);
    SymTypeExpression aType = SymTypeExpressionFactory.createTypeObject("A",scope);
    aconstr.setReturnType(aType);
    add2scope(scope, a);

    tc = new TypeCheck(null,derLit);

    //basic test
    Optional<ASTExpression> pgie1 = p.parse_StringExpression("<int>test()");
    assertTrue(pgie1.isPresent());
    assertEquals("int",tc.typeOf(pgie1.get()).print());

    //test with variable return type of method
    Optional<ASTExpression> pgie2 = p.parse_StringExpression("<int>get()");
    assertTrue(pgie2.isPresent());
    assertEquals("int",tc.typeOf(pgie2.get()).print());

    //test with more than one type variable and with args in method
    Optional<ASTExpression> pgie3 = p.parse_StringExpression("<int,double>set(3.2,4)");
    assertTrue(pgie3.isPresent());
    assertEquals("String",tc.typeOf(pgie3.get()).print());

    //test with super()
    Optional<ASTExpression> pgie4 = p.parse_StringExpression("<int>super()");
    assertTrue(pgie4.isPresent());
    assertEquals("ASuper",tc.typeOf(pgie4.get()).print());

    //test with this()
    Optional<ASTExpression> pgie5 = p.parse_StringExpression("<int>this()");
    assertTrue(pgie5.isPresent());
    assertEquals("A",tc.typeOf(pgie5.get()).print());
  }

  @Test
  public void failDeriveSymTypeOfPrimaryGenericInvocationExpression() throws IOException {
    //<TypeArg>super.method()
    MethodSymbol help = method("help",_doubleSymType);
    TypeSymbol sup = type("Sup",Lists.newArrayList(help),Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(),scope);
    sup.setClass(true);
    add2scope(scope,sup);
    SymTypeExpression supType = SymTypeExpressionFactory.createTypeObject("Sup",scope);
    TypeSymbol sub = type("Sub",Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(supType),Lists.newArrayList(),scope);
    add2scope(scope,sub);

    tc = new TypeCheck(null,derLit);

    Optional<ASTExpression> pgie1 = p.parse_StringExpression("<int>super.help()");

    assertTrue(pgie1.isPresent());

    try {
      tc.typeOf(pgie1.get());
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0285"));
    }
  }

  @Test
  public void failDeriveSymTypeOfPrimaryGenericInvocationExpression2() throws IOException {
    //<TypeArg>super.<TypeArg>method(arg)
    MethodSymbol help = add(method("help",_doubleSymType),field("x",_intSymType));
    help.getSpannedScope().add(typeVariable("T"));
    TypeSymbol sup = type("Sup",Lists.newArrayList(help),Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(),scope);
    sup.setClass(true);
    add2scope(scope,sup);
    SymTypeExpression supType = SymTypeExpressionFactory.createTypeObject("Sup",scope);
    TypeSymbol sub = type("Sub",Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(supType),Lists.newArrayList(),scope);
    add2scope(scope,sub);

    tc = new TypeCheck(null,derLit);

    Optional<ASTExpression> pgie1 = p.parse_StringExpression("<int>super.<double>help(2)");

    assertTrue(pgie1.isPresent());

    try {
      tc.typeOf(pgie1.get());
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0285"));
    }
  }

  @Test
  public void failDeriveSymTypeOfPrimaryGenericInvocationExpression3() throws IOException {
    //<TypeArg>super.field
    FieldSymbol test = field("test",_booleanSymType);
    TypeSymbol sup = type("Sup",Lists.newArrayList(),Lists.newArrayList(test),Lists.newArrayList(),Lists.newArrayList(),scope);
    sup.setClass(true);
    add2scope(scope,sup);
    SymTypeExpression supType = SymTypeExpressionFactory.createTypeObject("Sup",scope);
    TypeSymbol sub = type("Sub",Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(supType),Lists.newArrayList(),scope);
    add2scope(scope,sub);

    tc = new TypeCheck(null,derLit);

    Optional<ASTExpression> pgie1 = p.parse_StringExpression("<int>super.test");

    assertTrue(pgie1.isPresent());

    try {
      tc.typeOf(pgie1.get());
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0285"));
    }
  }

  @Test
  public void deriveSymTypeOfGenericInvocationExpression() throws IOException {
    //build symbol table for the test
    TypeVarSymbol t = typeVariable("T");
    add2scope(scope,t);
    MethodSymbol test = method("test",_charSymType);
    test.getSpannedScope().add(t);
    TypeSymbol a = type("A",Lists.newArrayList(test),Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(),scope);
    SymTypeExpression aType = SymTypeExpressionFactory.createTypeObject("A",scope);
    FieldSymbol aField = field("a",aType);
    add2scope(scope,aField);
    add2scope(scope,a);

    tc = new TypeCheck(null,derLit);

    Optional<ASTExpression> gie1 = p.parse_StringExpression("a.<int>test()");
    assertTrue(gie1.isPresent());
    assertEquals("char",tc.typeOf(gie1.get()).print());
    //test variable.<TypeArg>method() with and without args
  }

  @Test
  public void failDeriveSymTypeOfGenericInvocationExpression() throws IOException{
    //a.<int>this()
    TypeVarSymbol t = typeVariable("T");
    add2scope(scope,t);
    MethodSymbol constr = method("A",_charSymType);
    constr.getSpannedScope().add(t);
    TypeSymbol a = type("A",Lists.newArrayList(constr),Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(),scope);
    SymTypeExpression aType = SymTypeExpressionFactory.createTypeObject("A",scope);
    constr.setReturnType(aType);
    FieldSymbol aField = field("a",aType);
    add2scope(scope,aField);
    add2scope(scope,a);

    tc = new TypeCheck(null,derLit);

    Optional<ASTExpression> gie1 = p.parse_StringExpression("a.<int>this()");

    assertTrue(gie1.isPresent());

    try{
      tc.typeOf(gie1.get());
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0282"));
    }
  }

  @Test
  public void failDeriveSymTypeOfGenericInvocationExpression2() throws IOException{
    //a.<int>super()
    TypeVarSymbol t = typeVariable("T");
    add2scope(scope,t);
    MethodSymbol constr = method("ASuper",_charSymType);
    constr.getSpannedScope().add(t);
    TypeSymbol asuper = type("ASuper",Lists.newArrayList(constr),Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(),scope);
    SymTypeExpression asupertype = SymTypeExpressionFactory.createTypeObject("ASuper",scope);
    constr.setReturnType(asupertype);
    add2scope(scope,asuper);
    TypeSymbol a = type("A",Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(asupertype),Lists.newArrayList(),scope);
    SymTypeExpression aType = SymTypeExpressionFactory.createTypeObject("A",scope);
    FieldSymbol aField = field("a",aType);
    add2scope(scope,aField);
    add2scope(scope,a);

    tc = new TypeCheck(null,derLit);

    Optional<ASTExpression> gie1 = p.parse_StringExpression("a.<int>super()");

    assertTrue(gie1.isPresent());

    try{
      tc.typeOf(gie1.get());
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0282"));
    }
  }

  @Test
  public void failDeriveSymTypeOfGenericInvocationExpression3() throws IOException{
    //Type.<int>test()
    //build symbol table for the test
    TypeVarSymbol t = typeVariable("T");
    add2scope(scope,t);
    MethodSymbol test = method("test",_charSymType);
    test.getSpannedScope().add(t);
    TypeSymbol a = type("A",Lists.newArrayList(test),Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(),scope);
    add2scope(scope,a);

    tc = new TypeCheck(null,derLit);

    Optional<ASTExpression> gie1 = p.parse_StringExpression("A.<int>test()");

    assertTrue(gie1.isPresent());

    try{
      tc.typeOf(gie1.get());
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0282"));
    }
  }

  @Test
  public void testGenericInvocationExpressionStatic() throws IOException {
    //Type.<int>test()
    //build symbol table for the test
    TypeVarSymbol t = typeVariable("T");
    add2scope(scope,t);
    MethodSymbol test = method("test",_charSymType);
    test.getSpannedScope().add(t);
    test.setIsStatic(true);
    TypeSymbol a = type("A",Lists.newArrayList(test),Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(),scope);
    add2scope(scope,a);

    tc = new TypeCheck(null,derLit);

    Optional<ASTExpression> gie1 = p.parse_StringExpression("A.<int>test()");

    assertTrue(gie1.isPresent());

    assertEquals("char",tc.typeOf(gie1.get()).print());
  }

}
