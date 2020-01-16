package de.monticore.types.check;

import com.google.common.collect.Lists;
import de.monticore.expressions.combineexpressionswithliterals._parser.CombineExpressionsWithLiteralsParser;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._symboltable.ExpressionsBasisScope;
import de.monticore.expressions.expressionsbasis._symboltable.ExpressionsBasisSymTabMill;
import de.monticore.expressions.prettyprint.CombineExpressionsWithLiteralsPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.typesymbols._symboltable.FieldSymbol;
import de.monticore.types.typesymbols._symboltable.MethodSymbol;
import de.monticore.types.typesymbols._symboltable.TypeSymbol;
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

  // This is an auxiliary
  DeriveSymTypeOfCombineExpressionsDelegator derLit = new DeriveSymTypeOfCombineExpressionsDelegator(ExpressionsBasisSymTabMill.expressionsBasisScopeBuilder().build(),
      new CombineExpressionsWithLiteralsPrettyPrinter(new IndentPrinter()));

  // This is the TypeChecker under Test:
  TypeCheck tc = new TypeCheck(null, derLit);

  /*--------------------------------------------------- TESTS ---------------------------------------------------------*/


  @Test
  public void deriveFromPrimarySuperExpression() throws IOException {
    TypeSymbol supType = type("A",Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(),scope);
    SymTypeExpression sup = SymTypeExpressionFactory.createTypeObject("A",scope);
    supType.setClass(true);
    add2scope(scope,supType);

    MethodSymbol get = method("get",_voidSymType);
    TypeSymbol p = type("AB",Lists.newArrayList(get),Lists.newArrayList(),Lists.newArrayList(sup),Lists.newArrayList(),scope);
    p.setClass(true);
    add2scope(scope,p);

    //use the spanned scope of the type
    derLit.setScope((ExpressionsBasisScope)p.getSpannedScope());
    tc = new TypeCheck(null, derLit);
    CombineExpressionsWithLiteralsParser parser = new CombineExpressionsWithLiteralsParser();

    Optional<ASTExpression> a = parser.parse_StringExpression("super");
    assertTrue(a.isPresent());
    assertEquals("A",tc.typeOf(a.get()).print());

    //use the spanned scope of the method
    derLit.setScope((ExpressionsBasisScope)get.getSpannedScope());
    tc = new TypeCheck(null, derLit);

    assertEquals("A",tc.typeOf(a.get()).print());
  }

  @Test(expected = RuntimeException.class)
  public void failDeriveFromPrimarySuperExpression() throws IOException{
    //use the current scope -> there is no type spanning any enclosing scope
    Optional<ASTExpression> a = p.parse_StringExpression("super");
    assertTrue(a.isPresent());
    tc.typeOf(a.get());
  }

  @Test
  public void deriveFromPrimaryThisExpression() throws IOException {
    MethodSymbol get = method("get",_voidSymType);
    TypeSymbol p = type("AB",Lists.newArrayList(get),Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(),scope);
    add2scope(scope,p);

    //use the spanned scope of the type
    derLit.setScope((ExpressionsBasisScope)p.getSpannedScope());
    tc = new TypeCheck(null, derLit);
    CombineExpressionsWithLiteralsParser parser = new CombineExpressionsWithLiteralsParser();

    Optional<ASTExpression> a = parser.parse_StringExpression("this");
    assertTrue(a.isPresent());
    assertEquals("AB",tc.typeOf(a.get()).print());

    //use the spanned scope of the method
    derLit.setScope((ExpressionsBasisScope)get.getSpannedScope());
    tc = new TypeCheck(null, derLit);

    assertEquals("AB",tc.typeOf(a.get()).print());
  }

  @Test(expected = RuntimeException.class)
  public void failDeriveFromPrimaryThisExpression() throws IOException{
    //use the current scope -> there is no type spanning any enclosing scope
    Optional<ASTExpression> a = p.parse_StringExpression("this");
    assertTrue(a.isPresent());
    tc.typeOf(a.get());
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
    TypeSymbol outer = type("Outer",Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(),scope);
    add2scope(scope,outer);
    MethodSymbol methodInner = method("methodInner",_voidSymType);
    TypeSymbol inner = type("Inner",Lists.newArrayList(methodInner),Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(),(ExpressionsBasisScope)outer.getSpannedScope());
    add2scope(outer.getEnclosingScope(),inner);
    MethodSymbol methodInnerInner = method("methodInnerInner",_voidSymType);
    TypeSymbol innerinner = type("InnerInner", Lists.newArrayList(methodInnerInner),Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(),(ExpressionsBasisScope)inner.getSpannedScope());
    add2scope(inner.getSpannedScope(),innerinner);

    //test 1: Outer.this in methodInner()
    derLit.setScope((ExpressionsBasisScope) methodInner.getSpannedScope());
    tc = new TypeCheck(null, derLit);

    Optional<ASTExpression> this1 = p.parse_StringExpression("Outer.this");
    Optional<ASTExpression> this2 = p.parse_StringExpression("Inner.this");

    assertTrue(this1.isPresent());
    assertTrue(this2.isPresent());

    assertEquals("Outer",tc.typeOf(this1.get()).print());

    //test 2&3: Outer.this and Inner.this in methodInnerInner()
    derLit = new DeriveSymTypeOfCombineExpressionsDelegator((ExpressionsBasisScope) methodInnerInner.getSpannedScope(),new CombineExpressionsWithLiteralsPrettyPrinter(new IndentPrinter()));
    tc = new TypeCheck(null, derLit);

    assertEquals("Inner",tc.typeOf(this2.get()).print());
    assertEquals("Outer",tc.typeOf(this1.get()).print());
  }

  @Test(expected = RuntimeException.class)
  public void failDeriveFromThisExpression1() throws IOException{
    //.this in enclosing class
    TypeSymbol fail = type("Fail",Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(),scope);
    add2scope(scope,fail);

    derLit.setScope(scope);
    tc = new TypeCheck(null,derLit);

    Optional<ASTExpression> this1 = p.parse_StringExpression("Fail.this");

    assertTrue(this1.isPresent());

    tc.typeOf(this1.get());
    //.this without a type -> field or literal
  }

  @Test(expected = RuntimeException.class)
  public void failDeriveFromThisExpression2() throws IOException{
    //.this without a type
    Optional<ASTExpression> this1 = p.parse_StringExpression("person1.this");

    assertTrue(this1.isPresent());

    tc.typeOf(this1.get());
  }

  @Test
  public void deriveFromArrayExpressionTest() throws IOException{
    TypeSymbol intArray = type("int[]",Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(),scope);
    add2scope(scope,intArray);

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

    derLit.setScope(scope);
    tc = new TypeCheck(null,derLit);


    Optional<ASTExpression> arr1 = p.parse_StringExpression("b[3]");
    Optional<ASTExpression> arr2 = p.parse_StringExpression("a[3]");
    Optional<ASTExpression> arr3 = p.parse_StringExpression("get()[3]");
    Optional<ASTExpression> arr4 = p.parse_StringExpression("test()[3]");
    Optional<ASTExpression> arr5 = p.parse_StringExpression("b[3][4]");
    Optional<ASTExpression> arr6 = p.parse_StringExpression("test()[3][4]");

    assertTrue(arr1.isPresent());
    assertTrue(arr2.isPresent());
    assertTrue(arr3.isPresent());
    assertTrue(arr4.isPresent());
    assertTrue(arr5.isPresent());
    assertTrue(arr6.isPresent());

    assertEquals("int[]",tc.typeOf(arr1.get()).print());
    assertEquals("int",tc.typeOf(arr2.get()).print());
    assertEquals("int",tc.typeOf(arr3.get()).print());
    assertEquals("int[]",tc.typeOf(arr4.get()).print());
    assertEquals("int",tc.typeOf(arr5.get()).print());
    assertEquals("int",tc.typeOf(arr6.get()).print());
  }

  @Test (expected = RuntimeException.class)
  public void failDeriveSymTypeOfArrayExpression() throws IOException {
    //assert that a type will throw an error -> no int[4]
    TypeSymbol test = type("Test",Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(),scope);
    add2scope(scope,test);

    derLit.setScope(scope);
    tc = new TypeCheck(null, derLit);

    Optional<ASTExpression> arr1 = p.parse_StringExpression("Test[4]");

    assertTrue(arr1.isPresent());

    tc.typeOf(arr1.get());
  }

  @Test (expected = RuntimeException.class)
  public void failDeriveSymTypeOfArrayExpression2() throws IOException{
    //no integral type in the brackets
    SymTypeArray arrType = SymTypeExpressionFactory.createTypeArray("int",scope,1,_intSymType);
    FieldSymbol a = field("a",arrType);
    add2scope(scope,a);

    derLit.setScope(scope);
    tc = new TypeCheck(null,derLit);

    Optional<ASTExpression> arr1 = p.parse_StringExpression("a[7.5]");

    assertTrue(arr1.isPresent());

    tc.typeOf(arr1.get());
  }

  @Test
  public void deriveSymTypeOfClassExpression() throws IOException{
    Optional<ASTExpression> class1 = p.parse_StringExpression("String.class");
    Optional<ASTExpression> class2 = p.parse_StringExpression("Integer.class");
    Optional<ASTExpression> class3 = p.parse_StringExpression("java.util.Set<double>.class");

    assertTrue(class1.isPresent());
    assertTrue(class2.isPresent());
    assertTrue(class3.isPresent());

    //soll es so funktionieren wie in Java? Dann duerfte man naemlich keine Generics akzeptieren
    //hier sind erst einmal Generics mit dabei, s. Testfall #3

    assertEquals("Class<String>",tc.typeOf(class1.get()).print());
    assertEquals("Class<Integer>",tc.typeOf(class2.get()).print());
    assertEquals("Class<java.util.Set<double>>",tc.typeOf(class3.get()).print());
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

    assertEquals("boolean",tc.typeOf(inst1.get()).print());
    assertEquals("boolean",tc.typeOf(inst2.get()).print());
    assertEquals("boolean",tc.typeOf(inst3.get()).print());
  }

  @Test
  public void failDeriveSymTypeOfInstanceOfExpression() throws IOException{
    //the expression is a type
    Optional<ASTExpression> inst1 = p.parse_StringExpression("Student instanceof Person");

    assertTrue(inst1.isPresent());

    tc.typeOf(inst1.get());
    assertFalse(LogStub.getFindings().isEmpty());
    assertEquals("0xA0312 the left type of the InstanceofExpression cannot be a type",LogStub.getFindings().get(0).getMsg());
  }

  @Test
  public void deriveSymTypeOfTypeCastExpression() throws IOException {
    Optional<ASTExpression> cast1 = p.parse_StringExpression("(Person) student1");
    Optional<ASTExpression> cast2 = p.parse_StringExpression("(Person) person1");
    Optional<ASTExpression> cast3 = p.parse_StringExpression("(Student) person1");

    assertTrue(cast1.isPresent());
    assertTrue(cast2.isPresent());
    assertTrue(cast3.isPresent());

    assertEquals("Person",tc.typeOf(cast1.get()).print());
    assertEquals("Person",tc.typeOf(cast2.get()).print());
    assertEquals("Student",tc.typeOf(cast3.get()).print());
  }

  @Test
  public void failDeriveSymTypeOfTypeCastExpression() throws IOException{
    //the expression is a type
    Optional<ASTExpression> cast1 = p.parse_StringExpression("(Student)Person");

    assertTrue(cast1.isPresent());

    tc.typeOf(cast1.get());
    assertFalse(LogStub.getFindings().isEmpty());
    assertEquals("0xA0310 the inner expression of the TypeCastExpression cannot be a type",LogStub.getFindings().get(0).getMsg());
  }

  @Test(expected = RuntimeException.class)
  public void failDeriveSymTypeOfTypeCastExpression2() throws IOException{
    //there exists no sub/super type relation between the expression and the type
    Optional<ASTExpression> cast1 = p.parse_StringExpression("(String)person1");

    assertTrue(cast1.isPresent());

    tc.typeOf(cast1.get());
  }

  @Test
  public void deriveSymTypeOfSuperExpression() throws IOException {
    /*
     * example for this test:
     * class SuperOuter{
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
    TypeSymbol superOuter = type("SuperOuter",Lists.newArrayList(test),Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(),scope);
    superOuter.setClass(true);
    SymTypeExpression superOuterType = SymTypeExpressionFactory.createTypeObject("SuperOuter",scope);
    add2scope(scope,superOuter);

    TypeSymbol outer = type("Outer",Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(superOuterType),Lists.newArrayList(),scope);
    add2scope(scope,outer);
    MethodSymbol methodInner = method("methodInner",_voidSymType);
    TypeSymbol inner = type("Inner",Lists.newArrayList(methodInner),Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(),(ExpressionsBasisScope)outer.getSpannedScope());
    add2scope(outer.getEnclosingScope(),inner);
    MethodSymbol methodInnerInner = method("methodInnerInner",_voidSymType);
    TypeSymbol innerinner = type("InnerInner", Lists.newArrayList(methodInnerInner),Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(),(ExpressionsBasisScope)inner.getSpannedScope());
    add2scope(inner.getSpannedScope(),innerinner);

    Optional<ASTExpression> super1 = p.parse_StringExpression("Outer.super.test()");

    derLit.setScope((ExpressionsBasisScope) methodInner.getSpannedScope());
    tc = new TypeCheck(null,derLit);

    assertTrue(super1.isPresent());

    assertEquals("int",tc.typeOf(super1.get()).print());

    derLit = new DeriveSymTypeOfCombineExpressionsDelegator((ExpressionsBasisScope)methodInnerInner.getSpannedScope(),new CombineExpressionsWithLiteralsPrettyPrinter(new IndentPrinter()));
    tc = new TypeCheck(null,derLit);

    assertEquals("int",tc.typeOf(super1.get()).print());
  }

  @@Test(expected = RuntimeException.class)
  public void failDeriveFromSuperExpression1() throws IOException{
    //.super in enclosing class
    TypeSymbol fail = type("Fail",Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(),scope);
    add2scope(scope,fail);

    derLit.setScope(scope);
    tc = new TypeCheck(null,derLit);

    Optional<ASTExpression> this1 = p.parse_StringExpression("Fail.super.get()");

    assertTrue(this1.isPresent());

    tc.typeOf(this1.get());
    //.this without a type -> field or literal
  }

  @Test(expected = RuntimeException.class)
  public void failDeriveFromSuperExpression2() throws IOException{
    //.super without a type
    Optional<ASTExpression> this1 = p.parse_StringExpression("person1.super.get()");

    assertTrue(this1.isPresent());

    tc.typeOf(this1.get());
  }

  @Test(expected = RuntimeException.class)
  public void failDeriveFromSuperExpression3() throws IOException{
    //.super with more than one super type
  }

  @Test(expected = RuntimeException.class)
  public void failDeriveFromSuperExpression4() throws IOException{
    //.super without a super type
  }

  @Test(expected = RuntimeException.class)
  public void failDeriveFromSuperExpression5() throws IOException{
    //.super with a super type but a wrong method
  }


  @Test
  public void deriveSymTypeOfPrimaryGenericInvocationExpression(){

  }

  @Test
  public void deriveSymTypeOfGenericInvocationExpression(){

  }

}
