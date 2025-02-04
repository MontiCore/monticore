/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import com.google.common.collect.Lists;
import de.monticore.expressions.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import de.monticore.expressions.combineexpressionswithliterals._parser.CombineExpressionsWithLiteralsParser;
import de.monticore.expressions.combineexpressionswithliterals._symboltable.CombineExpressionsWithLiteralsScope;
import de.monticore.expressions.combineexpressionswithliterals._symboltable.ICombineExpressionsWithLiteralsGlobalScope;
import de.monticore.expressions.combineexpressionswithliterals._symboltable.ICombineExpressionsWithLiteralsScope;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisTraverser;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.oosymbols.OOSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static de.monticore.types.check.DefsTypeBasic.*;

public class DeriveSymTypeOfJavaClassExpressionsTest extends DeriveSymTypeAbstractTest {

  protected ICombineExpressionsWithLiteralsScope scope;
  /**
   * Focus: Deriving Type of Literals, here:
   * literals/MCLiteralsBasis.mc4
   */

  @BeforeEach
  public void init() {
    CombineExpressionsWithLiteralsMill.reset();
    CombineExpressionsWithLiteralsMill.init();
    BasicSymbolsMill.initializePrimitives();
    // Setting up a Scope Infrastructure (without a global Scope)
    DefsTypeBasic.setup();
    ICombineExpressionsWithLiteralsGlobalScope gs = CombineExpressionsWithLiteralsMill.globalScope();
    scope = CombineExpressionsWithLiteralsMill.scope();
    scope.setExportingSymbols(true);
    scope.setAstNode(null);
    scope.setEnclosingScope(gs);
    // we add a variety of TypeSymbols to the same scope (which in reality doesn't happen)

    add2scope(scope, DefsTypeBasic._array);
    add2scope(scope, DefsTypeBasic._Object);
    add2scope(scope, DefsTypeBasic._String);

    IOOSymbolsArtifactScope scope2 = CombineExpressionsWithLiteralsMill.artifactScope();
    scope2.setPackageName("java.util");
    scope2.setName("Set");
    scope2.setEnclosingScope(gs);
    OOTypeSymbol set = new OOTypeSymbol("Set");
    add2scope(scope2, set);
    // some FieldSymbols (ie. Variables, Attributes)
    OOTypeSymbol integer = new OOTypeSymbol("Integer");
    add2scope(scope, integer);
    OOTypeSymbol p = new OOTypeSymbol("Person");
    add2scope(scope,p);
    OOTypeSymbol s = new OOTypeSymbol("Student");
    add2scope(scope,s);
    s.setSuperTypesList(Lists.newArrayList(SymTypeExpressionFactory.createTypeObject("Person", scope)));
    OOTypeSymbol f = new OOTypeSymbol("FirstSemesterStudent");
    add2scope(scope,f);
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
    setFlatExpressionScopeSetter(scope);
  }

  @Override
  protected void setupTypeCheck() {
    // This is an auxiliary
    FullDeriveFromCombineExpressionsWithLiterals derLit = new FullDeriveFromCombineExpressionsWithLiterals();

    // other arguments not used (and therefore deliberately null)
    // This is the TypeChecker under Test:
    setTypeCheck(new TypeCalculator(null, derLit));
  }

  // Parser used for convenience:
  // (may be any other Parser that understands CommonExpressions)
  CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();
  @Override
  protected Optional<ASTExpression> parseStringExpression(String expression) throws IOException {
    return p.parse_StringExpression(expression);
  }

  @Override
  protected ExpressionsBasisTraverser getUsedLanguageTraverser() {
    return CombineExpressionsWithLiteralsMill.traverser();
  }

  /*--------------------------------------------------- TESTS ---------------------------------------------------------*/


  @Test
  public void deriveFromPrimarySuperExpression() throws IOException {
    OOTypeSymbol supType = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(OOSymbolsMill.scope())
        .setName("A")
        .setEnclosingScope(scope)
        .build();
    SymTypeExpression sup = SymTypeExpressionFactory.createTypeObject("A",scope);
    supType.setIsClass(true);
    add2scope(scope,supType);

    MethodSymbol get = OOSymbolsMill.methodSymbolBuilder()
        .setName("get")
        .setType(_voidSymType)
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
        .build();
    OOTypeSymbol p = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
        .setName("AB")
        .setSuperTypesList(Lists.newArrayList(sup))
        .setEnclosingScope(scope)
        .build();
    p.setIsClass(true);
    get.getSpannedScope().setEnclosingScope(p.getSpannedScope());
    add2scope(scope,p);

    //use the spanned scope of the type
    setFlatExpressionScopeSetter((ICombineExpressionsWithLiteralsScope) p.getSpannedScope());
    check("super", "A");

    //use the spanned scope of the method
    setFlatExpressionScopeSetter((ICombineExpressionsWithLiteralsScope) get.getSpannedScope());
    check("super", "A");
  }

  @Test
  public void failDeriveFromPrimarySuperExpression() throws IOException{
    //use the current scope -> there is no type spanning any enclosing scope
    checkError("super", "0xA0280");
  }

  @Test
  public void deriveFromPrimaryThisExpression() throws IOException {
    MethodSymbol get = OOSymbolsMill.methodSymbolBuilder()
        .setName("get")
        .setType(_voidSymType)
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
        .build();
    OOTypeSymbol p = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
        .setName("AB")
        .setEnclosingScope(scope)
        .build();
    p.addMethodSymbol(get);
    get.getSpannedScope().setEnclosingScope(p.getSpannedScope());
    add2scope(scope,p);

    //use the spanned scope of the type
    setFlatExpressionScopeSetter((ICombineExpressionsWithLiteralsScope) p.getSpannedScope());
    check("this", "AB");

    //use the spanned scope of the method
    setFlatExpressionScopeSetter((ICombineExpressionsWithLiteralsScope) get.getSpannedScope());
    check("this", "AB");
  }

  @Test
  public void failDeriveFromPrimaryThisExpression() throws IOException{
    //use the current scope -> there is no type spanning any enclosing scope
    checkError("this", "0xA0272");
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
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
        .setName("Outer")
        .setEnclosingScope(scope)
        .build();
    outer.getSpannedScope().setEnclosingScope(outer.getEnclosingScope());
    add2scope(scope,outer);
    MethodSymbol methodInner = OOSymbolsMill.methodSymbolBuilder()
        .setName("methodInner")
        .setType(_voidSymType)
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
        .build();
    OOTypeSymbol inner = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
        .setName("Inner")
        .setEnclosingScope(outer.getSpannedScope())
        .build();
    inner.addMethodSymbol(methodInner);
    inner.getSpannedScope().setEnclosingScope(inner.getEnclosingScope());
    methodInner.getSpannedScope().setEnclosingScope(inner.getSpannedScope());
    add2scope(outer.getEnclosingScope(),inner);
    MethodSymbol methodInnerInner = OOSymbolsMill.methodSymbolBuilder()
        .setName("methodInnerInner")
        .setType(_voidSymType)
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
        .build();
    OOTypeSymbol innerinner = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
        .setName("InnerInner")
        .setEnclosingScope(inner.getSpannedScope())
        .build();
    innerinner.addMethodSymbol(methodInnerInner);
    innerinner.getSpannedScope().setEnclosingScope(innerinner.getEnclosingScope());
    methodInnerInner.getSpannedScope().setEnclosingScope(innerinner.getSpannedScope());
    add2scope(inner.getSpannedScope(),innerinner);

    //test 1: Outer.this in methodInner()
    setFlatExpressionScopeSetter((ICombineExpressionsWithLiteralsScope) methodInner.getSpannedScope());
    check("Outer.this", "Outer");

    //test 2&3: Outer.this and Inner.this in methodInnerInner()
    setFlatExpressionScopeSetter((ICombineExpressionsWithLiteralsScope) methodInnerInner.getSpannedScope());
    check("Outer.this", "Outer");
    check("Inner.this", "Inner");
  }

  @Test
  public void failDeriveFromThisExpression1() throws IOException{
    //.this in enclosing class
    OOTypeSymbol fail = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
        .setName("Fail")
        .setEnclosingScope(scope)
        .build();
    add2scope(scope,fail);

    setFlatExpressionScopeSetter(scope);
    checkError("Fail.this", "0xA0252");
  }

  @Test
  public void failDeriveFromThisExpression2() throws IOException{
    //.this without a type
    checkError("person1.this", "0xA0252");
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
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
        .setName("List")
        .setEnclosingScope(scope)
        .build();
    list.addTypeVarSymbol(t);
    add2scope(scope,list);
    SymTypeExpression generic = SymTypeExpressionFactory.createGenerics("List",scope,_intSymType);
    SymTypeArray genarrType = SymTypeExpressionFactory.createTypeArray("List",scope,1,generic);
    FieldSymbol c = field("c",genarrType);
    add2scope(scope,c);

    setFlatExpressionScopeSetter(scope);

    check("b[3]", "int[]");
    check("a[3]", "int");
    check("get()[3]", "int");
    check("test()[3]", "int[]");
    check("b[3][4]", "int");
    check("test()[3][4]", "int");
    check("c[1]", "List<int>");
  }

  @Test 
  public void failDeriveSymTypeOfArrayExpression() throws IOException {
    //assert that a type will throw an error -> no int[4]
    OOTypeSymbol test = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
        .setName("Test")
        .setEnclosingScope(scope)
        .build();
    add2scope(scope,test);

    setFlatExpressionScopeSetter(scope);
    checkError("Test[4]", "0xA0255");
  }

  @Test 
  public void failDeriveSymTypeOfArrayExpression2() throws IOException{
    //no integral type in the brackets
    SymTypeArray arrType = SymTypeExpressionFactory.createTypeArray("int",scope,1,_intSymType);
    FieldSymbol a = field("a",arrType);
    add2scope(scope,a);

    setFlatExpressionScopeSetter(scope);
    checkError("a[7.5]", "0xA0257");
  }

  @Test
  public void failDeriveSymTypeOfArrayExpression3() throws IOException{
    //type in the array brackets
    SymTypeArray arrType = SymTypeExpressionFactory.createTypeArray("int",scope,1,_intSymType);
    FieldSymbol a = field("a",arrType);
    add2scope(scope,a);

    setFlatExpressionScopeSetter(scope);
    checkError("a[Person]", "0xA0253");
  }

  @Test
  public void deriveSymTypeOfClassExpression() throws IOException{
    //soll es so funktionieren wie in Java? Dann duerfte man naemlich keine Generics akzeptieren
    //hier sind erst einmal Generics mit dabei, s. Testfall #3
    //falls man einschränken möchte, kann man die CoCo NoClassExpressionForGenerics
    check("String.class", "Class<String>");
    check("Integer.class", "Class<Integer>");
    check("java.util.Set<double>.class", "Class<Set<double>>");
  }

  @Test
  public void failDeriveSymTypeOfClassExpression() throws IOException{
    //test that types must have a name
    Optional<ASTExpression> class1 = p.parse_StringExpression("3.class");

    Assertions.assertFalse(class1.isPresent());

    Optional<ASTExpression> class2 = p.parse_StringExpression("\"Hallo\".class");
    Assertions.assertFalse(class2.isPresent());
  }

  @Test
  public void deriveSymTypeOfInstanceofExpression() throws IOException{
    check("person1 instanceof Person", "boolean");
    check("person2 instanceof Student", "boolean");
    check("student1 instanceof Person", "boolean");
  }

  @Test
  public void failDeriveSymTypeOfInstanceOfExpression() throws IOException{
    //the expression is a type
    checkError("Student instanceof Person", "0xA0267");
  }

  @Test
  public void deriveSymTypeOfTypeCastExpression() throws IOException {
    check("(Person) student1", "Person");
    check("(Person) person1", "Person");
    check("(Student) person1", "Student");
  }

  @Test
  public void failDeriveSymTypeOfTypeCastExpression() throws IOException{
    //the expression is a type
    checkError("(Student) Person", "0xA0262");
  }

  @Test
  public void failDeriveSymTypeOfTypeCastExpression2() throws IOException{
    //there exists no sub/super type relation between the expression and the type
    checkError("(String) person1", "0xA0266");
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
        .setType(_intSymType)
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
        .build();
    FieldSymbol field = field("field",_intSymType);
    OOTypeSymbol superOuter = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
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
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
        .setName("Outer")
        .setSuperTypesList(Lists.newArrayList(superOuterType))
        .setEnclosingScope(scope)
        .build();
    outer.getSpannedScope().setEnclosingScope(outer.getEnclosingScope());
    add2scope(scope,outer);
    MethodSymbol methodInner = OOSymbolsMill.methodSymbolBuilder()
        .setName("methodInner")
        .setType(_voidSymType)
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
        .build();

    //class Inner
    OOTypeSymbol inner = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
        .setName("Inner")
        .setEnclosingScope(outer.getSpannedScope())
        .build();
    inner.addMethodSymbol(methodInner);
    inner.getSpannedScope().setEnclosingScope(inner.getEnclosingScope());
    methodInner.getSpannedScope().setEnclosingScope(inner.getSpannedScope());
    add2scope(outer.getEnclosingScope(),inner);
    MethodSymbol methodInnerInner = OOSymbolsMill.methodSymbolBuilder()
        .setName("methodInnerInner")
        .setType(_voidSymType)
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
        .build();

    //class InnerInner
    OOTypeSymbol innerinner = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
        .setName("InnerInner")
        .setEnclosingScope(inner.getSpannedScope())
        .build();
    innerinner.addMethodSymbol(methodInnerInner);
    innerinner.getSpannedScope().setEnclosingScope(innerinner.getEnclosingScope());
    methodInnerInner.getSpannedScope().setEnclosingScope(innerinner.getSpannedScope());
    add2scope(inner.getSpannedScope(),innerinner);

    setFlatExpressionScopeSetter((ICombineExpressionsWithLiteralsScope) methodInner.getSpannedScope());

    check("Outer.super.test()", "int");
    check("Outer.super.field", "int");

    setFlatExpressionScopeSetter((ICombineExpressionsWithLiteralsScope) methodInnerInner.getSpannedScope());
    check("Outer.super.test()", "int");
    check("Outer.super.field", "int");
  }

  @Test
  public void failDeriveFromSuperExpression1() throws IOException{
    //.super in enclosing class
    OOTypeSymbol fail = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
        .setName("Fail")
        .setEnclosingScope(scope)
        .build();
    fail.getSpannedScope().setEnclosingScope(fail.getEnclosingScope());
    add2scope(scope,fail);

    setFlatExpressionScopeSetter(scope);
    checkError("Fail.super.get()", "0xA0261");
  }

  @Test
  public void failDeriveFromSuperExpression2() throws IOException{
    //.super without a type
    checkError("person1.super.get()", "0xA0259");
  }

  @Test
  public void failDeriveFromSuperExpression3() throws IOException{
    //.super with more than one super type
    //first super type
    MethodSymbol test = OOSymbolsMill.methodSymbolBuilder()
        .setName("test")
        .setType(_intSymType)
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
        .build();
    OOTypeSymbol superOuter = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
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
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
        .setName("SuperOuterTwo")
        .setEnclosingScope(scope)
        .build();
    superOuterTwo.getSpannedScope().setEnclosingScope(superOuterTwo.getEnclosingScope());
    superOuterTwo.setIsClass(true);
    SymTypeExpression superOuterTwoType = SymTypeExpressionFactory.createTypeObject("SuperOuterTwo",scope);
    add2scope(scope,superOuterTwo);

    //class Outer
    OOTypeSymbol outer = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
        .setName("Outer")
        .setSuperTypesList(Lists.newArrayList(superOuterType, superOuterTwoType))
        .setEnclosingScope(scope)
        .build();
    outer.getSpannedScope().setEnclosingScope(outer.getEnclosingScope());
    add2scope(scope,outer);
    MethodSymbol methodInner = OOSymbolsMill.methodSymbolBuilder()
        .setName("methodInner")
        .setType(_voidSymType)
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
        .build();

    //class Inner
    OOTypeSymbol inner = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
        .setName("Inner")
        .setEnclosingScope(outer.getSpannedScope())
        .build();
    inner.addMethodSymbol(methodInner);
    inner.getSpannedScope().setEnclosingScope(inner.getEnclosingScope());
    methodInner.getSpannedScope().setEnclosingScope(inner.getSpannedScope());
    add2scope(outer.getEnclosingScope(),inner);
    MethodSymbol methodInnerInner = OOSymbolsMill.methodSymbolBuilder()
        .setName("methodInnerInner")
        .setType(_voidSymType)
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
        .build();

    //class InnerInner
    OOTypeSymbol innerinner = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
        .setName("InnerInner")
        .setEnclosingScope(inner.getSpannedScope())
        .build();
    innerinner.addMethodSymbol(methodInnerInner);
    innerinner.getSpannedScope().setEnclosingScope(innerinner.getEnclosingScope());
    methodInnerInner.getSpannedScope().setEnclosingScope(innerinner.getSpannedScope());
    add2scope(inner.getSpannedScope(),innerinner);

    setFlatExpressionScopeSetter((CombineExpressionsWithLiteralsScope) methodInnerInner.getSpannedScope());
    checkError("Outer.super.test()", "0xA0261");
  }

  @Test
  public void failDeriveFromSuperExpression4() throws IOException{
    //.super without a super type
    //class Outer
    OOTypeSymbol outer = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
        .setName("Outer")
        .setSuperTypesList(Lists.newArrayList())
        .setEnclosingScope(scope)
        .build();
    outer.getSpannedScope().setEnclosingScope(outer.getEnclosingScope());
    add2scope(scope,outer);

    setFlatExpressionScopeSetter(scope);
    checkError("Outer.super.test()", "0xA0261");
  }

  @Test
  public void failDeriveFromSuperExpression5() throws IOException{
    //.super with a super type but a method that the super type does not know
    MethodSymbol test = OOSymbolsMill.methodSymbolBuilder()
        .setName("test")
        .setType(_intSymType)
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
        .build();
    OOTypeSymbol superOuter = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
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
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
        .setName("Outer")
        .setSuperTypesList(Lists.newArrayList(superOuterType))
        .setEnclosingScope(scope)
        .build();
    outer.getSpannedScope().setEnclosingScope(outer.getEnclosingScope());
    add2scope(scope,outer);

    setFlatExpressionScopeSetter(scope);

    //SuperOuter does not have a method "get"
    checkError("Outer.super.get()", "0xA0261");
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
        .setType(_intSymType)
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
        .build();
    add2scope(asuperconstr.getSpannedScope(), t);
    OOTypeSymbol aSuper = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
        .setName("ASuper")
        .setEnclosingScope(scope)
        .build();
    aSuper.addMethodSymbol(asuperconstr);
    aSuper.setIsClass(true);
    aSuper.getSpannedScope().setEnclosingScope(aSuper.getEnclosingScope());
    asuperconstr.getSpannedScope().setEnclosingScope(aSuper.getSpannedScope());
    SymTypeExpression aSuperType = SymTypeExpressionFactory.createTypeObject("ASuper",scope);
    asuperconstr.setType(aSuperType);
    add2scope(scope,aSuper);

    //type A with constructor and methods test,get and set
    MethodSymbol test = OOSymbolsMill.methodSymbolBuilder()
        .setName("test")
        .setType(_intSymType)
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
        .build();
    add2scope(test.getSpannedScope(), t);
    MethodSymbol get = OOSymbolsMill.methodSymbolBuilder()
        .setName("get")
        .setType(tType)
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
        .build();
    add2scope(get.getSpannedScope(), t);
    MethodSymbol aconstr = OOSymbolsMill.methodSymbolBuilder()
        .setName("A")
        .setType(_intSymType)
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
        .build();
    add2scope(aconstr.getSpannedScope(),t);
    FieldSymbol sParam = field("s", sType);
    FieldSymbol tParam = field("t", tType);
    MethodSymbol set = OOSymbolsMill.methodSymbolBuilder()
        .setName("set")
        .setType(_StringSymType)
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
        .build();
    add2scope(set.getSpannedScope(), sParam);
    add2scope(set.getSpannedScope(), tParam);
    add2scope(set.getSpannedScope(), t);
    add2scope(set.getSpannedScope(), s);
    OOTypeSymbol a = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
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
    aconstr.setType(aType);
    add2scope(scope, a);

    setFlatExpressionScopeSetter((ICombineExpressionsWithLiteralsScope) a.getSpannedScope());

    //basic test
    check("<int>test()", "int");

    //test with variable return type of method
    check("<int>get()", "int");
    check("<double>get()", "double");

    //test with more than one type variable and with args in method
    check("<int,double>set(3.2,4)", "String");

    //test with super()
    check("<int>super()", "ASuper");

    //test with this()
    check("<int>this()", "A");
  }

  @Test
  public void failDeriveSymTypeOfPrimaryGenericInvocationExpression() throws IOException {
    //<TypeArg>super.method()
    MethodSymbol help = OOSymbolsMill.methodSymbolBuilder()
        .setName("help")
        .setType(_doubleSymType)
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
        .build();
    help.setSpannedScope(CombineExpressionsWithLiteralsMill.scope());
    OOTypeSymbol sup = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
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
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
        .setName("Sub")
        .setSuperTypesList(Lists.newArrayList(supType))
        .setEnclosingScope(scope)
        .build();
    sub.getSpannedScope().setEnclosingScope(sub.getEnclosingScope());
    add2scope(scope,sub);

    setFlatExpressionScopeSetter((ICombineExpressionsWithLiteralsScope) sub.getSpannedScope());
    checkError("<int>super.help()", "0xA0285");
  }

  @Test
  public void failDeriveSymTypeOfPrimaryGenericInvocationExpression2() throws IOException {
    //<TypeArg>super.<TypeArg>method(arg)
    FieldSymbol xParam = field("x", _intSymType);
    MethodSymbol help = OOSymbolsMill.methodSymbolBuilder()
        .setName("help")
        .setType(_doubleSymType)
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
        .build();
    add2scope(help.getSpannedScope(), typeVariable("T"));
    add2scope(help.getSpannedScope(), xParam);
    OOTypeSymbol sup = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
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
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
        .setName("Sub")
        .setSuperTypesList(Lists.newArrayList(supType))
        .setEnclosingScope(scope)
        .build();
    sub.getSpannedScope().setEnclosingScope(sub.getEnclosingScope());
    add2scope(scope,sub);

    setFlatExpressionScopeSetter((ICombineExpressionsWithLiteralsScope) sub.getSpannedScope());
    checkError("<int>super.<double>help(2)", "0xA0285");
  }

  @Test
  public void failDeriveSymTypeOfPrimaryGenericInvocationExpression3() throws IOException {
    //<TypeArg>super.field
    FieldSymbol test = field("test",_booleanSymType);
    OOTypeSymbol sup = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
        .setName("Sup")
        .setEnclosingScope(scope)
        .build();
    sup.addFieldSymbol(test);
    sup.setIsClass(true);
    sup.getSpannedScope().setEnclosingScope(sup.getEnclosingScope());
    add2scope(scope,sup);
    SymTypeExpression supType = SymTypeExpressionFactory.createTypeObject("Sup",scope);
    OOTypeSymbol sub = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
        .setName("Sub")
        .setSuperTypesList(Lists.newArrayList(supType))
        .setEnclosingScope(scope)
        .build();
    sub.getSpannedScope().setEnclosingScope(sub.getEnclosingScope());
    add2scope(scope,sub);

    setFlatExpressionScopeSetter((ICombineExpressionsWithLiteralsScope) sub.getSpannedScope());
    checkError("<int>super.test", "0xA0285");
  }

  @Test
  public void deriveSymTypeOfGenericInvocationExpression() throws IOException {
    //build symbol table for the test
    TypeVarSymbol t = typeVariable("T");
    MethodSymbol test = OOSymbolsMill.methodSymbolBuilder()
        .setName("test")
        .setType(_charSymType)
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
        .build();
    test.setSpannedScope(CombineExpressionsWithLiteralsMill.scope());
    add2scope(test.getSpannedScope(),t);
    OOTypeSymbol a = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
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

    setFlatExpressionScopeSetter(scope);
    check("a.<int>test()", "char");
  }

  @Test
  public void failDeriveSymTypeOfGenericInvocationExpression() throws IOException{
    //a.<int>this()
    TypeVarSymbol t = typeVariable("T");
    MethodSymbol constr = OOSymbolsMill.methodSymbolBuilder()
        .setName("A")
        .setType(_charSymType)
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
        .build();
    add2scope(constr.getSpannedScope(), t);
    OOTypeSymbol a = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
        .setName("A")
        .setEnclosingScope(scope)
        .build();
    a.addMethodSymbol(constr);
    a.getSpannedScope().setEnclosingScope(a.getEnclosingScope());
    constr.getSpannedScope().setEnclosingScope(a.getSpannedScope());
    SymTypeExpression aType = SymTypeExpressionFactory.createTypeObject("A",scope);
    constr.setType(aType);
    FieldSymbol aField = field("a",aType);
    add2scope(scope,aField);
    add2scope(scope,a);

    setFlatExpressionScopeSetter(scope);
    checkError("a.<int>this()", "0xA0282");
  }

  @Test
  public void failDeriveSymTypeOfGenericInvocationExpression2() throws IOException{
    //a.<int>super()
    TypeVarSymbol t = typeVariable("T");
    MethodSymbol constr = OOSymbolsMill.methodSymbolBuilder()
        .setName("ASuper")
        .setType(_charSymType)
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
        .build();
    add2scope(constr.getSpannedScope(), t);
    OOTypeSymbol aSuper = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
        .setName("ASuper")
        .setEnclosingScope(scope)
        .build();
    aSuper.addMethodSymbol(constr);
    aSuper.getSpannedScope().setEnclosingScope(aSuper.getEnclosingScope());
    constr.getSpannedScope().setEnclosingScope(aSuper.getSpannedScope());
    SymTypeExpression asupertype = SymTypeExpressionFactory.createTypeObject("ASuper",scope);
    constr.setType(asupertype);
    add2scope(scope,aSuper);
    OOTypeSymbol a = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
        .setName("A")
        .setEnclosingScope(scope)
        .build();
    a.getSpannedScope().setEnclosingScope(a.getEnclosingScope());
    SymTypeExpression aType = SymTypeExpressionFactory.createTypeObject("A",scope);
    FieldSymbol aField = field("a",aType);
    add2scope(scope,aField);
    add2scope(scope,a);

    setFlatExpressionScopeSetter(scope);
    checkError("a.<int>super()", "0xA0282");
  }

  @Test
  public void failDeriveSymTypeOfGenericInvocationExpression3() throws IOException{
    //Type.<int>test()
    //build symbol table for the test
    TypeVarSymbol t = typeVariable("T");
    MethodSymbol test = OOSymbolsMill.methodSymbolBuilder()
        .setName("test")
        .setType(_charSymType)
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
        .build();
    add2scope(test.getSpannedScope(), t);
    OOTypeSymbol a = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
        .setName("A")
        .setEnclosingScope(scope)
        .build();
    a.addMethodSymbol(test);
    a.getSpannedScope().setEnclosingScope(a.getEnclosingScope());
    test.getSpannedScope().setEnclosingScope(a.getSpannedScope());
    add2scope(scope,a);

    setFlatExpressionScopeSetter(scope);
    checkError("A.<int>test()", "0xA0282");
  }

  @Test
  public void testGenericInvocationExpressionStatic() throws IOException {
    //Type.<int>test() with static field test
    //build symbol table for the test
    TypeVarSymbol t = typeVariable("T");
    MethodSymbol test = OOSymbolsMill.methodSymbolBuilder()
        .setName("test")
        .setType(_charSymType)
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
        .build();
    test.setSpannedScope(CombineExpressionsWithLiteralsMill.scope());
    add2scope(test.getSpannedScope(), t);
    test.setIsStatic(true);
    OOTypeSymbol a = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
        .setName("A")
        .setEnclosingScope(scope)
        .build();
    a.addMethodSymbol(test);
    a.getSpannedScope().setEnclosingScope(a.getEnclosingScope());
    test.getSpannedScope().setEnclosingScope(a.getSpannedScope());
    add2scope(scope,a);

    setFlatExpressionScopeSetter(scope);
    check("A.<int>test()", "char");
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
        .setEnclosingScope(CombineExpressionsWithLiteralsMill.scope())
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
        .build();

    add2scope(scope, bsp1);

    //Bsp2
    MethodSymbol bsp2constr = CombineExpressionsWithLiteralsMill.methodSymbolBuilder()
        .setName("Bsp2")
        .setType(_intSymType)
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
        .build();

    OOTypeSymbol bsp2 = CombineExpressionsWithLiteralsMill.oOTypeSymbolBuilder()
        .setName("Bsp2")
        .setEnclosingScope(CombineExpressionsWithLiteralsMill.scope())
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
        .build();
    bsp2.addMethodSymbol(bsp2constr);

    SymTypeExpression bsp2Sym = SymTypeExpressionFactory.createTypeObject("Bsp2",scope);
    bsp2constr.setType(bsp2Sym);

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
        .setType(_intSymType)
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
        .build();

    add2scope(bsp3constr.getSpannedScope(),field1);
    add2scope(bsp3constr.getSpannedScope(),field2);

    //second constructor without arguments
    MethodSymbol bsp3constr2 = CombineExpressionsWithLiteralsMill.methodSymbolBuilder()
        .setName("Bsp3")
        .setType(_intSymType)
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
        .build();

    OOTypeSymbol bsp3 = CombineExpressionsWithLiteralsMill.oOTypeSymbolBuilder()
        .setName("Bsp3")
        .setEnclosingScope(CombineExpressionsWithLiteralsMill.scope())
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
        .build();
    bsp3.addMethodSymbol(bsp3constr2);

    SymTypeExpression bsp3Sym = SymTypeExpressionFactory.createTypeObject("Bsp3", scope);
    bsp3constr.setType(bsp3Sym);
    bsp3constr2.setType(bsp3Sym);

    bsp3constr.setEnclosingScope(bsp3.getSpannedScope());
    bsp3constr.getSpannedScope().setEnclosingScope(bsp3constr.getEnclosingScope());
    add2scope(bsp3.getSpannedScope(), bsp3constr);
    add2scope(bsp3.getSpannedScope(), bsp3constr2);
    add2scope(scope, bsp3);

    setFlatExpressionScopeSetter(scope);
    check("new Bsp1()", "Bsp1");
    check("new Bsp2()", "Bsp2");
    check("new Bsp3(3, 5.6)", "Bsp3");
    check("new Bsp3('a',4)", "Bsp3");
  }

  @Test
  public void failDeriveFromCreatorExpressionAnonymousClass1() throws IOException {
    //1) Error when using primitive types
    checkError("new int()", "0xA1312");
  }

  @Test
  public void failDeriveFromCreatorExpressionAnonymousClass2() throws IOException {
    //2) No constructor, Creator-Expression with Arguments
    //Bsp2
    OOTypeSymbol bsp2 = CombineExpressionsWithLiteralsMill.oOTypeSymbolBuilder()
        .setName("Bsp2")
        .setEnclosingScope(CombineExpressionsWithLiteralsMill.scope())
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
        .build();

    add2scope(scope, bsp2);
    setFlatExpressionScopeSetter(scope);
    checkError("new Bsp2(3,4)", "0xA1312");
  }

  @Test
  public void failDeriveFromCreatorExpressionAnonymousClass3() throws IOException {
    //3) Creator-Expression with wrong number of Arguments -> 0 Arguments, so that it also checks that the Default-constructor is not invoked in this case
    //Bsp3
    MethodSymbol bsp3constr = CombineExpressionsWithLiteralsMill.methodSymbolBuilder()
        .setName("Bsp3")
        .setType(_intSymType)
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
        .build();

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
        .setEnclosingScope(CombineExpressionsWithLiteralsMill.scope())
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
        .build();
    bsp3.addMethodSymbol(bsp3constr);

    SymTypeExpression bsp3Sym = SymTypeExpressionFactory.createTypeObject("Bsp3", scope);
    bsp3constr.setType(bsp3Sym);

    bsp3constr.setEnclosingScope(bsp3.getSpannedScope());
    bsp3constr.getSpannedScope().setEnclosingScope(bsp3constr.getEnclosingScope());
    add2scope(bsp3.getSpannedScope(), bsp3constr);
    add2scope(scope, bsp3);

    setFlatExpressionScopeSetter(scope);
    checkError("new Bsp3()", "0xA1312");
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
        .setType(_intSymType)
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
        .build();

    OOTypeSymbol bsp4 = CombineExpressionsWithLiteralsMill.oOTypeSymbolBuilder()
        .setName("Bsp4")
        .setEnclosingScope(CombineExpressionsWithLiteralsMill.scope())
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
        .build();
    bsp4.addMethodSymbol(bsp4constr);

    bsp4constr.setSpannedScope(CombineExpressionsWithLiteralsMill.scope());
    add2scope(bsp4constr.getSpannedScope(),field1);
    add2scope(bsp4constr.getSpannedScope(),field2);
    SymTypeExpression bsp4Sym = SymTypeExpressionFactory.createTypeObject("Bsp4",scope);
    bsp4constr.setType(bsp4Sym);
    add2scope(bsp4.getSpannedScope(), bsp4constr);
    add2scope(scope,bsp4);

    setFlatExpressionScopeSetter(scope);
    checkError("new Bsp4(true, 4)", "0xA1312");
  }

  @Test
  public void testDeriveSymTypeOfCreatorExpressionArrayCreator() throws IOException {
    //Tests mit ArrayInitByExpression

      //Test mit double[3][4] --> double[][]
    check("new double[3][4]", "double[][]");

      //Test mit int[3][][] --> int[3][][]
    check("new int[3][][]", "int[][][]");
  }

  @Test
  public void failDeriveFromCreatorExpressionArrayCreator1() throws IOException {
    //Test mit ArrayInitByExpression, keine ganzzahl in Array (z.B. new int[3.4])
    checkError("new int[3.4]", "0xA0315");
  }

}
