/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import com.google.common.collect.Lists;
import de.monticore.expressions.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import de.monticore.expressions.combineexpressionswithliterals._parser.CombineExpressionsWithLiteralsParser;
import de.monticore.expressions.combineexpressionswithliterals._symboltable.CombineExpressionsWithLiteralsScope;
import de.monticore.expressions.combineexpressionswithliterals._symboltable.ICombineExpressionsWithLiteralsArtifactScope;
import de.monticore.expressions.combineexpressionswithliterals._symboltable.ICombineExpressionsWithLiteralsGlobalScope;
import de.monticore.expressions.combineexpressionswithliterals._symboltable.ICombineExpressionsWithLiteralsScope;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisTraverser;
import de.monticore.io.paths.MCPath;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.oosymbols.OOSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsScope;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static de.monticore.types.check.DefsTypeBasic.*;
import static org.junit.Assert.assertTrue;

public class DeriveSymTypeOfCommonExpressionTest extends DeriveSymTypeAbstractTest {

  @Override
  protected void setupTypeCheck() {
    // This is an auxiliary
    DeriveSymTypeOfCombineExpressionsDelegator derLit = new DeriveSymTypeOfCombineExpressionsDelegator();

    // other arguments not used (and therefore deliberately null)
    // This is the TypeChecker under Test:
    setTypeCheck(new TypeCheck(null, derLit));
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

  /**
   * Focus: Deriving Type of Literals, here:
   * literals/MCLiteralsBasis.mc4
   */

  @BeforeClass
  public static void setup() {
    LogStub.enableFailQuick(false);
  }

  @Before
  public void doBefore() {
    LogStub.init();
    CombineExpressionsWithLiteralsMill.reset();
    CombineExpressionsWithLiteralsMill.init();
    BasicSymbolsMill.initializePrimitives();
    CombineExpressionsWithLiteralsMill.globalScope().setSymbolPath(new MCPath());
    CombineExpressionsWithLiteralsMill.globalScope().setFileExt("ce");
  }

  /*--------------------------------------------------- TESTS ---------------------------------------------------------*/

  /**
   * test correctness of addition
   */
  @Test
  public void deriveFromPlusExpression() throws IOException {
    // example with two ints
    check("3+4", "int");

    // example with double and int
    check("4.9+12", "double");

    // example with String
    check("3 + \"Hallo\"", "String");
  }

  @Test
  public void testInvalidPlusExpression() throws IOException {
    checkError("3+true", "0xA0210");
  }

  /**
   * test correctness of subtraction
   */
  @Test
  public void deriveFromMinusExpression() throws IOException {
    // example with two ints
    check("7-2", "int");

    //example with float and long
    check("7.9f-3L", "float");
  }

  @Test
  public void testInvalidMinusExpression() throws IOException {
    checkError("3-true", "0xA0213");
  }

  /**
   * test correctness of multiplication
   */
  @Test
  public void deriveFromMultExpression() throws IOException {
    //example with two ints
    check("2*19", "int");

    //example with long and char
    check("\'a\'*3L", "long");
  }

  @Test
  public void testInvalidMultExpression() throws IOException {
    checkError("3*true", "0xA0211");
  }

  /**
   * test correctness of division
   */
  @Test
  public void deriveFromDivideExpression() throws IOException {
    //example with two ints
    check("7/12", "int");

    //example with float and double
    check("5.4f/3.9", "double");
  }

  @Test
  public void testInvalidDivideExpression() throws IOException {
    checkError("3/true", "0xA0212");
  }

  /**
   * tests correctness of modulo
   */
  @Test
  public void deriveFromModuloExpression() throws IOException {
    //example with two ints
    check("3%1", "int");

    //example with long and double
    check("0.8%3L", "double");
  }

  @Test
  public void testInvalidModuloExpression() throws IOException {
    checkError("3%true", "0xA0214");
  }

  /**
   * test LessEqualExpression
   */
  @Test
  public void deriveFromLessEqualExpression() throws IOException {
    //example with two ints
    check("4<=9", "boolean");

    //example with two other numeric types
    check("2.4f<=3L", "boolean");
  }

  @Test
  public void testInvalidLessEqualExpression() throws IOException {
    checkError("3<=true", "0xA0215");
  }

  /**
   * test GreaterEqualExpression
   */
  @Test
  public void deriveFromGreaterEqualExpression() throws IOException {
    //example with two ints
    check("7>=2", "boolean");

    //example with two other numeric types
    check("2.5>=\'d\'", "boolean");
  }

  @Test
  public void testInvalidGreaterEqualExpression() throws IOException {
    checkError("3>=true", "0xA0216");
  }

  /**
   * test LessThanExpression
   */
  @Test
  public void deriveFromLessThanExpression() throws IOException {
    //example with two ints
    check("4<9", "boolean");

    //example with two other numeric types
    check("2.4f<3L", "boolean");
  }

  @Test
  public void testInvalidLessThanExpression() throws IOException {
    checkError("3<true", "0xA0217");
  }

  /**
   * test GreaterThanExpression
   */
  @Test
  public void deriveFromGreaterThanExpression() throws IOException {
    //example with two ints
    check("7>2", "boolean");

    //example with two other numeric types
    check("2.5>\'d\'", "boolean");
  }

  @Test
  public void testInvalidGreaterThanExpression() throws IOException {
    checkError("3>true", "0xA0218");
  }

  /**
   * initialize basic scope and a few symbols for testing
   */
  public void init_basic() {
    // No enclosing Scope: Search ending here

    ICombineExpressionsWithLiteralsScope scope = CombineExpressionsWithLiteralsMill.scope();
    scope.setEnclosingScope(null);
    scope.setExportingSymbols(true);
    scope.setAstNode(null);

    OOTypeSymbol person = OOSymbolsMill.oOTypeSymbolBuilder()
        .setName("Person")
        .setSpannedScope(OOSymbolsMill.scope())
        .setEnclosingScope(scope)
        .build();
    add2scope(scope, person);
    OOTypeSymbol student = OOSymbolsMill.oOTypeSymbolBuilder()
        .setName("Student")
        .setSpannedScope(OOSymbolsMill.scope())
        .setSuperTypesList(Lists.newArrayList(SymTypeExpressionFactory.createTypeObject("Person",scope)))
        .setEnclosingScope(scope)
        .build();
    add2scope(scope, student);
    OOTypeSymbol firstsemesterstudent = OOSymbolsMill.oOTypeSymbolBuilder()
        .setName("FirstSemesterStudent")
        .setSpannedScope(OOSymbolsMill.scope())
        .setSuperTypesList(Lists.newArrayList(SymTypeExpressionFactory.createTypeObject("Student",scope)))
        .setEnclosingScope(scope)
        .build();
    add2scope(scope, firstsemesterstudent);
    add2scope(scope, field("foo", _intSymType));
    add2scope(scope, field("bar2", _booleanSymType));
    add2scope(scope, field("person1", SymTypeExpressionFactory.createTypeObject("Person", scope)));
    add2scope(scope, field("person2", SymTypeExpressionFactory.createTypeObject("Person", scope)));
    add2scope(scope, field("student1", SymTypeExpressionFactory.createTypeObject("Student", scope)));
    add2scope(scope, field("student2", SymTypeExpressionFactory.createTypeObject("Student", scope)));
    add2scope(scope, field("firstsemester", SymTypeExpressionFactory.
        createTypeObject("FirstSemesterStudent", scope)));
    add2scope(scope, method("isInt", _booleanSymType));
    add2scope(scope, add(method("isInt", _booleanSymType), field("maxLength", _intSymType)));

    setFlatExpressionScopeSetter(scope);
  }

  /**
   * test EqualsExpression
   */
  @Test
  public void deriveFromEqualsExpression() throws IOException {
    //initialize symbol table
    init_basic();

    //example with two primitives
    check("7==9.5f", "boolean");

    //example with two objects of the same class
    check("student1==student2", "boolean");

    //example with two objects in sub-supertype relation
    check("person1==student1", "boolean");
  }

  @Test
  public void testInvalidEqualsExpression() throws IOException {
    init_basic();

    checkError("3==true", "0xA0219");
  }

  @Test
  public void testInvalidEqualsExpression2() throws IOException{
    init_basic();

    //person1 has the type Person, foo is a boolean
    checkError("person1==foo", "0xA0219");
  }

  /**
   * test NotEqualsExpression
   */
  @Test
  public void deriveFromNotEqualsExpression() throws IOException {
    //initialize symbol table
    init_basic();

    //example with two primitives
    check("true!=false", "boolean");

    //example with two objects of the same class
    check("person1!=person2", "boolean");

    //example with two objects in sub-supertype relation
    check("student2!=person2", "boolean");
  }

  @Test
  public void testInvalidNotEqualsExpression() throws IOException {
    init_basic();

    checkError("3!=true", "0xA0220");
  }

  @Test
  public void testInvalidNotEqualsExpression2() throws IOException{
    init_basic();
    //person1 is a Person, foo is a boolean
    checkError("person1!=foo", "0xA0220");
  }

  /**
   * test BooleanAndOpExpression
   */
  @Test
  public void deriveFromBooleanAndOpExpression() throws IOException {
    //only possible with two booleans
    check("true&&true", "boolean");

    check("(3<=4&&5>6)", "boolean");
  }

  @Test
  public void testInvalidAndOpExpression() throws IOException {
    //only possible with two booleans
    checkError("3&&true", "0xA0223");
  }

  /**
   * test BooleanOrOpExpression
   */
  @Test
  public void deriveFromBooleanOrOpExpression() throws IOException {
    //only possible with two booleans
    check("true||false", "boolean");

    check("(3<=4.5f||5.3>6)", "boolean");
  }

  @Test
  public void testInvalidOrOpExpression() throws IOException {
    //only possible with two booleans
    checkError("3||true", "0xA0226");
  }

  /**
   * test LogicalNotExpression
   */
  @Test
  public void deriveFromLogicalNotExpression() throws IOException {
    //only possible with boolean as inner expression
    check("!true", "boolean");

    check("!(2.5>=0.3)", "boolean");
  }

  @Test
  public void testInvalidLogicalNotExpression() throws IOException {
    //only possible with a boolean as inner expression
    checkError("!4", "0xA0228");
  }

  /**
   * test BracketExpression
   */
  @Test
  public void deriveFromBracketExpression() throws IOException {
    //initialize symbol table
    init_basic();

    //test with only a literal in the inner expression
    check("(3)", "int");

    //test with a more complex inner expression
    check("(3+4*(18-7.5))", "double");

    //test without primitive types in inner expression
    check("(person1)", "Person");
  }

  @Test
  public void testInvalidBracketExpression() throws IOException {
    //a cannot be resolved -> a has no type
    init_basic();
    checkError("(a)", "0xA0229");
  }

  /**
   * test ConditionalExpression
   */
  @Test
  public void deriveFromConditionalExpression() throws IOException {
    //initialize symbol table
    init_basic();

    //test with two ints as true and false expression
    check("3<4?9:10", "int");

    //test with float and long
    check("3>4?4.5f:10L", "float");

    //test without primitive types as true and false expression
    check("3<9?person1:person2", "Person");

    //test with two objects in a sub-supertype relation
    check("3<9?student1:person2", "Person");
  }

  @Test
  public void testInvalidConditionalExpression() throws IOException {
    //true and 7 are not of the same type
    checkError("3<4?true:7", "0xA0234");
  }

  @Test
  public void testInvalidConditionalExpression2() throws IOException {
    //3 is not a boolean condition
    checkError("3?true:false", "0xA0234");
  }
  /**
   * test BooleanNotExpression
   */
  @Test
  public void deriveFromBooleanNotExpression() throws IOException {
    //test with a int
    check("~3", "int");
    //test with a char
    check("~\'a\'", "int");
  }

  @Test
  public void testInvalidBooleanNotExpression() throws IOException {
    //only possible with an integral type (int, long, char, short, byte)
    checkError("~3.4", "0xA0236");
  }

  /**
   * initialize symboltable including global scope, artifact scopes and scopes with symbols for
   * testing (mostly used for FieldAccessExpressions)
   */
  public void init_advanced() {
    ICombineExpressionsWithLiteralsGlobalScope globalScope = CombineExpressionsWithLiteralsMill.globalScope();

    ICombineExpressionsWithLiteralsArtifactScope artifactScope2 = CombineExpressionsWithLiteralsMill.artifactScope();
    artifactScope2.setEnclosingScope(globalScope);
    artifactScope2.setImportsList(Lists.newArrayList());
    artifactScope2.setPackageName("");
    artifactScope2.setName("types");

    ICombineExpressionsWithLiteralsArtifactScope artifactScope3 = CombineExpressionsWithLiteralsMill.artifactScope();
    artifactScope3.setEnclosingScope(globalScope);
    artifactScope3.setImportsList(Lists.newArrayList());
    artifactScope3.setName("types2");

    ICombineExpressionsWithLiteralsArtifactScope artifactScope4 = CombineExpressionsWithLiteralsMill.artifactScope();
    artifactScope4.setEnclosingScope(globalScope);
    artifactScope4.setImportsList(Lists.newArrayList());
    artifactScope4.setName("types3");
    artifactScope4.setPackageName("types3");

    ICombineExpressionsWithLiteralsScope scope = globalScope;
    // No enclosing Scope: Search ending here

    ICombineExpressionsWithLiteralsScope scope3 = CombineExpressionsWithLiteralsMill.scope();
    scope3.setName("types2");
    scope3.setEnclosingScope(artifactScope4);
    scope3.setEnclosingScope(artifactScope4);

    // some FieldSymbols (ie. Variables, Attributes)
    OOTypeSymbol person = OOSymbolsMill.oOTypeSymbolBuilder()
        .setName("Person")
        .setSpannedScope(OOSymbolsMill.scope())
        .setEnclosingScope(scope)
        .build();
    OOTypeSymbol student = OOSymbolsMill.oOTypeSymbolBuilder()
        .setName("Student")
        .setSpannedScope(OOSymbolsMill.scope())
        .setSuperTypesList(Lists.newArrayList(SymTypeExpressionFactory.createTypeObject("Person",scope)))
        .setEnclosingScope(scope)
        .build();
    OOTypeSymbol firstsemesterstudent = OOSymbolsMill.oOTypeSymbolBuilder()
        .setName("FirstSemesterStudent")
        .setSpannedScope(OOSymbolsMill.scope())
        .setSuperTypesList(Lists.newArrayList(SymTypeExpressionFactory.createTypeObject("Student",scope)))
        .setEnclosingScope(scope)
        .build();
    add2scope(artifactScope2, person);
    add2scope(scope3, person);
    add2scope(scope, person);

    add2scope(artifactScope2, student);
    add2scope(scope3, student);
    add2scope(scope, student);

    add2scope(artifactScope2, firstsemesterstudent);
    add2scope(scope3, firstsemesterstudent);
    add2scope(scope, firstsemesterstudent);

    add2scope(scope, field("foo", _intSymType));
    add2scope(scope, field("bar2", _booleanSymType));
    add2scope(scope, field("person1", SymTypeExpressionFactory.createTypeObject("Person", scope)));
    add2scope(scope, field("person2", SymTypeExpressionFactory.createTypeObject("Person", scope)));
    add2scope(scope, field("student1", SymTypeExpressionFactory.createTypeObject("Student", scope)));
    add2scope(scope, field("student2", SymTypeExpressionFactory.createTypeObject("Student", scope)));
    add2scope(scope, field("firstsemester",
        SymTypeExpressionFactory.createTypeObject("FirstSemesterStudent", scope))
    );
    add2scope(scope, method("isInt", _booleanSymType));
    add2scope(scope, add(method("isInt", _booleanSymType), field("maxLength", _intSymType)));
    FieldSymbol fs = field("variable", _intSymType);
    fs.setIsStatic(true);
    MethodSymbol ms = method("store", _doubleSymType);
    ms.setIsStatic(true);
    MethodSymbol ms1 = add(method("pay", _voidSymType), field("cost",_intSymType));
    ms1.setIsStatic(true);
    OOTypeSymbol testType = OOSymbolsMill.oOTypeSymbolBuilder()
        .setName("Test")
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
        .setEnclosingScope(scope)
        .build();
    testType.setMethodList(Lists.newArrayList(ms,ms1));
    testType.addFieldSymbol(fs);
    OOTypeSymbol testType2 = OOSymbolsMill.oOTypeSymbolBuilder()
        .setName("Test")
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
        .setEnclosingScope(artifactScope2)
        .build();
    testType2.setMethodList(Lists.newArrayList(ms,ms1));
    testType2.addFieldSymbol(fs);

    OOTypeSymbol testType3 = OOSymbolsMill.oOTypeSymbolBuilder()
        .setName("Test")
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
        .setEnclosingScope(scope3)
        .build();
    testType3.setMethodList(Lists.newArrayList(ms,ms1));
    testType3.addFieldSymbol(fs);
    IOOSymbolsScope testScope = testType3.getSpannedScope();

    FieldSymbol testVariable = field("testVariable",_shortSymType);
    testVariable.setIsStatic(true);
    OOTypeSymbol testInnerType = OOSymbolsMill.oOTypeSymbolBuilder()
        .setName("TestInnerType")
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
        .setEnclosingScope(testScope)
        .build();
    testInnerType.addFieldSymbol(testVariable);
    testInnerType.setIsStatic(true);
    add2scope(testScope,testInnerType);
    add2scope(testInnerType.getSpannedScope(),testVariable);

    testType3.setSpannedScope(testScope);

    add2scope(artifactScope2, testType2);
    add2scope(scope3, testType3);
    add2scope(scope,testType);

    setFlatExpressionScopeSetter(scope);
  }

  /**
   * test FieldAccessExpression
   */
  @Test
  public void deriveFromFieldAccessExpression() throws IOException {
    //initialize symbol table
    init_advanced();

    //test for type with only one package
    check("types.Test", "Test");

    //test for variable of a type with one package
    check("types.Test.variable", "int");

    //test for type with more than one package
    check("types3.types2.Test", "Test");

    //test for variable of type with more than one package
    check("types3.types2.Test.variable", "int");

    check("Test", "Test");

    //test for variable in inner type
    check("types3.types2.Test.TestInnerType.testVariable", "short");
  }

  /**
   * test CallExpression
   */
  @Test
  public void deriveFromCallExpression() throws IOException {
    //initialize symbol table
    init_advanced();

    //test for method with unqualified name without parameters
    check("isInt()", "boolean");

    //test for method with unqualified name with parameters
    check("isInt(4)", "boolean");

    //test for method with qualified name without parameters
    check("types.Test.store()", "double");

    //test for method with qualified name with parameters
    check("types.Test.pay(4)", "void");
  }

  @Test
  public void testInvalidCallExpression() throws IOException {
    //method isNot() is not in scope -> method cannot be resolved -> method has no return type
    init_advanced();
    checkError("isNot()", "0xA0240");
  }

  /**
   * initialize the symbol table for a basic inheritance example
   * we only have one scope and the symbols are all in this scope or in subscopes
   */
  public void init_inheritance() {
    // No enclosing Scope: Search ending here
    ICombineExpressionsWithLiteralsScope scope = CombineExpressionsWithLiteralsMill.scope();
    scope.setEnclosingScope(null);       // No enclosing Scope: Search ending here
    scope.setExportingSymbols(true);
    scope.setAstNode(null);

    //inheritance example
    //super
    MethodSymbol add = add(method("add", _voidSymType), field("element", _StringSymType));
    FieldSymbol field = field("field", _booleanSymType);
    OOTypeSymbol superclass = OOSymbolsMill.oOTypeSymbolBuilder()
        .setName("AList")
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
        .setEnclosingScope(scope)
        .build();
    superclass.addMethodSymbol(add);
    superclass.addFieldSymbol(field);
    add2scope(scope, superclass);
    SymTypeExpression supclass = SymTypeExpressionFactory.createTypeObject("AList", scope);

    //sub
    OOTypeSymbol subclass = OOSymbolsMill.oOTypeSymbolBuilder()
        .setName("MyList")
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
        .setSuperTypesList(Lists.newArrayList(supclass))
        .setEnclosingScope(scope)
        .build();
    add2scope(scope, subclass);

    SymTypeExpression sub = SymTypeExpressionFactory.createTypeObject("MyList", scope);
    FieldSymbol myList = field("myList", sub);
    add2scope(scope, myList);

    //subsub
    OOTypeSymbol subsubclass = OOSymbolsMill.oOTypeSymbolBuilder()
        .setName("MySubList")
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
        .setSuperTypesList(Lists.newArrayList(sub))
        .setEnclosingScope(scope)
        .build();
    add2scope(scope, subsubclass);
    SymTypeExpression subsub = SymTypeExpressionFactory.createTypeObject("MySubList", scope);
    FieldSymbol mySubList = field("mySubList", subsub);
    add2scope(scope, mySubList);

    setFlatExpressionScopeSetter(scope);
  }

  /**
   * test if the methods and fields of superclasses can be used by subclasses
   */
  @Test
  public void testInheritance() throws IOException {
    //initialize symbol table
    init_inheritance();

    //methods
    //test normal inheritance
    check("myList.add(\"Hello\")", "void");

    //test inheritance over two levels
    check("mySubList.add(\"World\")", "void");

    //fields
    check("myList.field", "boolean");

    check("mySubList.field", "boolean");
  }

  /**
   * initialize an empty scope
   * @return
   */
  public ICombineExpressionsWithLiteralsScope init_scope() {
    // No enclosing Scope: Search ending here
    ICombineExpressionsWithLiteralsScope scope = CombineExpressionsWithLiteralsMill.scope();
    scope.setEnclosingScope(null);       // No enclosing Scope: Search ending here
    scope.setExportingSymbols(true);
    scope.setAstNode(null);
    CombineExpressionsWithLiteralsMill.globalScope().addSubScope(scope);
    return scope;
  }

  /**
   * test the inheritance of a generic type with one type variable
   */
  @Test
  public void testListAndArrayListInheritance() throws IOException {
    ICombineExpressionsWithLiteralsScope scope = init_scope();
    //initialize symboltable
    //one generic parameter, supertype List<T>
    TypeVarSymbol t = typeVariable("T");
    add2scope(scope, t);
    MethodSymbol addMethod = add(method("add", _booleanSymType),
        field("x", SymTypeExpressionFactory.createTypeVariable("T", scope))
    );
    FieldSymbol nextField = field("next", SymTypeExpressionFactory.createTypeVariable("T", scope));
    OOTypeSymbol sym = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(OOSymbolsMill.scope())
        .setName("List")
        .setEnclosingScope(scope)
        .build();
    sym.addMethodSymbol(addMethod);
    sym.addFieldSymbol(nextField);
    sym.addTypeVarSymbol(t);
    add2scope(scope, sym);
    SymTypeExpression listIntSymTypeExp = SymTypeExpressionFactory
        .createGenerics("List", scope, _intSymType);
    FieldSymbol listVar = field("listVar", listIntSymTypeExp);
    add2scope(scope, listVar);

    //one generic parameter, subtype ArrayList<T>
    TypeVarSymbol arrayListT = typeVariable("T");
    SymTypeExpression listTSymTypeExp = SymTypeExpressionFactory
        .createGenerics("List", scope,
            Lists.newArrayList(SymTypeExpressionFactory.createTypeVariable("T", scope)));
    OOTypeSymbol subsym = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(OOSymbolsMill.scope())
        .setName("ArrayList")
        .setSuperTypesList(Lists.newArrayList(listTSymTypeExp))
        .setEnclosingScope(scope)
        .build();
    subsym.addTypeVarSymbol(arrayListT);
    add2scope(scope, subsym);
    SymTypeExpression subsymexp = SymTypeExpressionFactory.
        createGenerics("ArrayList", scope, Lists.newArrayList(_intSymType));
    FieldSymbol arraylistVar = field("arraylistVar", subsymexp);
    add2scope(scope, arraylistVar);

    setFlatExpressionScopeSetter(scope);

    //test methods and fields of the supertype
    check("listVar.add(2)", "boolean");

    check("listVar.next", "int");

    //test inherited methods and fields of the subtype
    check("arraylistVar.add(3)", "boolean");

    check("arraylistVar.next", "int");
  }

  /**
   * test the inheritance of generic types with two type variables
   */
  @Test
  public void testGenericInheritanceTwoTypeVariables() throws IOException {
    //initialize symboltable
    ICombineExpressionsWithLiteralsScope scope = init_scope();

    //two generic parameters, supertype GenSup<S,V>, create SymType GenSup<String,int>
    TypeVarSymbol t1 = typeVariable("S");
    TypeVarSymbol t2 = typeVariable("V");
    add2scope(scope, t1);
    add2scope(scope, t2);
    MethodSymbol load = add(method("load",
        SymTypeExpressionFactory.createTypeVariable("S", scope)),
        field("x", SymTypeExpressionFactory.createTypeVariable("V", scope))
    );
    FieldSymbol f1 = field("f1", SymTypeExpressionFactory.createTypeVariable("S", scope));
    FieldSymbol f2 = field("f2", SymTypeExpressionFactory.createTypeVariable("V", scope));
    OOTypeSymbol genSup = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(OOSymbolsMill.scope())
        .setName("GenSup")
        .setEnclosingScope(scope)
        .build();
    genSup.setMethodList(Lists.newArrayList(load,load.deepClone()));
    genSup.addFieldSymbol(f1);
    genSup.addFieldSymbol(f2);
    genSup.addTypeVarSymbol(t1);
    genSup.addTypeVarSymbol(t2);
    add2scope(scope, genSup);
    SymTypeExpression genSupType = SymTypeExpressionFactory.
        createGenerics("GenSup", scope, Lists.newArrayList(_StringSymType, _intSymType));
    FieldSymbol genSupVar = field("genSupVar", genSupType);
    add2scope(scope, genSupVar);

    //two generic parameters, subtype GenSub<S,V>, create SymType GenSub<String,int>
    t1 = typeVariable("S");
    t2 = typeVariable("V");
    SymTypeExpression genTypeSV = SymTypeExpressionFactory.
        createGenerics("GenSup", scope, Lists.newArrayList(SymTypeExpressionFactory.
            createTypeVariable("S", scope), SymTypeExpressionFactory.createTypeVariable("V", scope)));
    OOTypeSymbol genSub = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(OOSymbolsMill.scope())
        .setName("GenSub")
        .setSuperTypesList(Lists.newArrayList(genTypeSV))
        .setEnclosingScope(scope).build();
    genSub.addFieldSymbol(f1.deepClone());
    genSub.addTypeVarSymbol(t1);
    genSub.addTypeVarSymbol(t2);

    add2scope(scope, genSub);
    SymTypeExpression genSubType = SymTypeExpressionFactory.
        createGenerics("GenSub", scope, Lists.newArrayList(_StringSymType, _intSymType));
    FieldSymbol genSubVar = field("genSubVar", genSubType);
    add2scope(scope, genSubVar);

    //two generic parameters, subsubtype GenSubSub<V,S>, create GenSubSub<String,int>
    t1 = typeVariable("S");
    t2 = typeVariable("V");
    SymTypeExpression genSubTypeSV = SymTypeExpressionFactory.
        createGenerics("GenSub", scope, Lists.newArrayList(SymTypeExpressionFactory.createTypeVariable("S", scope),
            SymTypeExpressionFactory.createTypeVariable("V", scope)));
    OOTypeSymbol genSubSub = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(OOSymbolsMill.scope())
        .setName("GenSubSub")
        .setSuperTypesList(Lists.newArrayList(genSubTypeSV))
        .setEnclosingScope(scope)
        .build();
    genSubSub.addTypeVarSymbol(t2);
    genSubSub.addTypeVarSymbol(t1);
    add2scope(scope, genSubSub);
    SymTypeExpression genSubSubType = SymTypeExpressionFactory.
        createGenerics("GenSubSub", scope, Lists.newArrayList(_StringSymType, _intSymType));
    FieldSymbol genSubSubVar = field("genSubSubVar", genSubSubType);
    add2scope(scope, genSubSubVar);


    setFlatExpressionScopeSetter(scope);

    //supertype: test methods and fields
    check("genSupVar.load(3)", "String");

    check("genSupVar.f1", "String");

    check("genSupVar.f2", "int");

    //subtype: test inherited methods and fields
    check("genSubVar.load(3)", "String");

    check("genSubVar.f1", "String");

    check("genSubVar.f2", "int");

    //subsubtype: test inherited methods and fields
    check("genSubSubVar.load(\"Hello\")", "int");

    check("genSubSubVar.f1", "int");

    check("genSubSubVar.f2", "String");
  }

  /**
   * test if methods and a field from a fixed subtype(generic type, but instead of type variable concrete type)
   * are inherited correctly
   */
  @Test
  public void testSubVarSupFix() throws IOException {
    //initialize symboltable
    ICombineExpressionsWithLiteralsScope scope = init_scope();

    //subtype with variable generic parameter, supertype with fixed generic parameter
    //supertype with fixed generic parameter FixGen<A> and SymType FixGen<int>
    TypeVarSymbol a = typeVariable("A");
    add2scope(scope, a);
    MethodSymbol add2 = add(method("add", _booleanSymType),
        field("a", SymTypeExpressionFactory.createTypeVariable("A", scope))
    );
    FieldSymbol next2 = field("next", SymTypeExpressionFactory.createTypeVariable("A", scope));
    OOTypeSymbol fixGen = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(OOSymbolsMill.scope())
        .setName("FixGen")
        .setEnclosingScope(scope)
        .build();
    fixGen.addMethodSymbol(add2);
    fixGen.addFieldSymbol(next2);
    fixGen.addTypeVarSymbol(a);
    add2scope(scope, fixGen);
    SymTypeExpression fixGenType = SymTypeExpressionFactory.createGenerics("FixGen", scope,
        Lists.newArrayList(_intSymType));
    FieldSymbol fixGenVar = field("fixGenVar", fixGenType);
    add2scope(scope, fixGenVar);

    //subtype with variable generic parameter VarGen<N> which extends FixGen<int>, SymType VarGen<String>
    TypeVarSymbol n = typeVariable("N");
    add2scope(scope, n);
    MethodSymbol calculate = method("calculate", SymTypeExpressionFactory.createTypeVariable("N", scope));
    OOTypeSymbol varGenType = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(OOSymbolsMill.scope())
        .setName("VarGen")
        .setSuperTypesList(Lists.newArrayList(fixGenType))
        .setEnclosingScope(scope)
        .build();
    varGenType.addMethodSymbol(calculate);
    varGenType.addTypeVarSymbol(n);
    add2scope(scope, varGenType);
    SymTypeExpression varGenSym = SymTypeExpressionFactory.
        createGenerics("VarGen", scope, Lists.newArrayList(_StringSymType));
    FieldSymbol varGen = field("varGen", varGenSym);
    add2scope(scope, varGen);

    setFlatExpressionScopeSetter(scope);

    //test own methods first
    check("varGen.calculate()", "String");

    //test inherited methods and fields
    check("varGen.add(4)", "boolean");

    check("varGen.next", "int");
  }

  /**
   * Test-Case: SubType has more generic parameters than its supertype
   */
  @Test
  public void testSubTypeWithMoreGenericParameters() throws IOException {
    //initialize symboltable
    ICombineExpressionsWithLiteralsScope scope = init_scope();

    //one generic parameter, supertype List<T>
    TypeVarSymbol t = typeVariable("T");
    add2scope(scope, t);
    MethodSymbol addMethod = add(method("add", _booleanSymType),
        field("x", SymTypeExpressionFactory.createTypeVariable("T", scope))
    );
    FieldSymbol nextField = field("next", SymTypeExpressionFactory.createTypeVariable("T", scope));
    OOTypeSymbol sym = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(OOSymbolsMill.scope())
        .setName("List")
        .setEnclosingScope(scope)
        .build();
    sym.addMethodSymbol(addMethod);
    sym.addFieldSymbol(nextField);
    sym.addTypeVarSymbol(t);
    add2scope(scope, sym);
    SymTypeExpression listIntSymTypeExp = SymTypeExpressionFactory
        .createGenerics("List", scope, Lists.newArrayList(_intSymType));
    FieldSymbol listVar = field("listVar", listIntSymTypeExp);
    add2scope(scope, listVar);

    //two generic parameters, subtype MoreGen<T,F>
    t = typeVariable("T");
    TypeVarSymbol moreType1 = typeVariable("F");
    add2scope(scope, moreType1);
    SymTypeExpression listTSymTypeExp = SymTypeExpressionFactory
        .createGenerics("List", scope, Lists.newArrayList(SymTypeExpressionFactory.createTypeVariable("T", scope)));
    MethodSymbol insert = add(
        method("insert", SymTypeExpressionFactory.createTypeVariable("T", scope)),
        field("x", SymTypeExpressionFactory.createTypeVariable("F", scope))
    );
    OOTypeSymbol moreGenType = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(OOSymbolsMill.scope())
        .setName("MoreGen")
        .setSuperTypesList(Lists.newArrayList(listTSymTypeExp))
        .setEnclosingScope(scope)
        .build();
    moreGenType.addMethodSymbol(insert);
    moreGenType.addTypeVarSymbol(t);
    moreGenType.addTypeVarSymbol(moreType1);
    add2scope(scope, moreGenType);
    SymTypeExpression moreGenSym = SymTypeExpressionFactory.
        createGenerics("MoreGen", scope, Lists.newArrayList(_intSymType, _longSymType));
    FieldSymbol moreGen = field("moreGen", moreGenSym);
    add2scope(scope, moreGen);

    setFlatExpressionScopeSetter(scope);

    //test own method
    check("moreGen.insert(12L)", "int");

    //test inherited methods and fields
    check("moreGen.add(12)", "boolean");

    check("moreGen.next", "int");
  }

  /**
   * Test-Case: SubType is a normal object type and extends a fixed generic type
   */
  @Test
  public void testSubTypeWithoutGenericParameter() throws IOException {
    //initialize symboltable
    ICombineExpressionsWithLiteralsScope scope = init_scope();

    //one generic parameter, supertype List<T>
    TypeVarSymbol t = typeVariable("T");
    add2scope(scope, t);
    MethodSymbol addMethod = add(method("add", _booleanSymType),
        field("x", SymTypeExpressionFactory.createTypeVariable("T", scope))
    );
    FieldSymbol nextField = field("next", SymTypeExpressionFactory.createTypeVariable("T", scope));
    OOTypeSymbol sym = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(OOSymbolsMill.scope())
        .setName("List")
        .setEnclosingScope(scope)
        .build();
    sym.addMethodSymbol(addMethod);
    sym.addFieldSymbol(nextField);
    sym.addTypeVarSymbol(t);
    add2scope(scope, sym);
    SymTypeExpression listIntSymTypeExp = SymTypeExpressionFactory
        .createGenerics("List", scope, Lists.newArrayList(_intSymType));
    FieldSymbol listVar = field("listVar", listIntSymTypeExp);
    add2scope(scope, listVar);

    //subtype without generic parameter NotGen extends List<int>
    OOTypeSymbol notgeneric = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(OOSymbolsMill.scope())
        .setName("NotGen")
        .setSuperTypesList(Lists.newArrayList(listIntSymTypeExp))
        .setEnclosingScope(scope)
        .build();
    add2scope(scope, notgeneric);
    SymTypeExpression notgenericType = SymTypeExpressionFactory.createTypeObject("NotGen", scope);
    FieldSymbol ng = field("notGen", notgenericType);
    add2scope(scope, ng);

    setFlatExpressionScopeSetter(scope);

    //test inherited methods and fields
    check("notGen.add(14)", "boolean");

    check("notGen.next", "int");
  }

  /**
   * Test-Case: Multi-Inheritance 1, test if the methods and fields are inherited correctly
   * every type in the example has exactly one type variable
   */
  @Test
  public void testMultiInheritance() throws IOException {
    //initialize symboltable
    ICombineExpressionsWithLiteralsScope scope = init_scope();

    //supertype SupA<T>
    TypeVarSymbol t = typeVariable("T");
    add2scope(scope, t);
    MethodSymbol testA = method("testA", SymTypeExpressionFactory.createTypeVariable("T", scope));
    FieldSymbol currentA = field("currentA", SymTypeExpressionFactory.createTypeVariable("T", scope));
    OOTypeSymbol supA = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(OOSymbolsMill.scope())
        .setName("SupA")
        .setEnclosingScope(scope)
        .build();
    supA.addMethodSymbol(testA);
    supA.addFieldSymbol(currentA);
    supA.addTypeVarSymbol(t);
    add2scope(scope, supA);
    SymTypeExpression supATExpr = SymTypeExpressionFactory
        .createGenerics("SupA", scope, Lists.newArrayList(SymTypeExpressionFactory.createTypeVariable("T", scope)));

    //supertype SupB<T>
    t = typeVariable("T");
    MethodSymbol testB = method("testB", SymTypeExpressionFactory.createTypeVariable("T", scope));
    FieldSymbol currentB = field("currentB", SymTypeExpressionFactory.createTypeVariable("T", scope));
    OOTypeSymbol supB = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(OOSymbolsMill.scope())
        .setName("SupB")
        .setEnclosingScope(scope)
        .build();
    supB.addMethodSymbol(testB);
    supB.addFieldSymbol(currentB);
    supB.addTypeVarSymbol(t);
    add2scope(scope, supB);
    SymTypeExpression supBTExpr = SymTypeExpressionFactory.
        createGenerics("SupB", scope, Lists.newArrayList(SymTypeExpressionFactory.createTypeVariable("T", scope)));

    //subType SubA<T>
    t = typeVariable("T");
    OOTypeSymbol subA = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(OOSymbolsMill.scope())
        .setName("SubA")
        .setSuperTypesList(Lists.newArrayList(supATExpr, supBTExpr))
        .setEnclosingScope(scope)
        .build();
    subA.addTypeVarSymbol(t);
    add2scope(scope, subA);
    SymTypeExpression subATExpr = SymTypeExpressionFactory
        .createGenerics("SubA", scope, Lists.newArrayList(_charSymType));
    FieldSymbol sub = field("sub", subATExpr);
    add2scope(scope, sub);

    setFlatExpressionScopeSetter(scope);

    check("sub.testA()", "char");

    check("sub.currentA", "char");

    check("sub.testB()", "char");

    check("sub.currentB", "char");
  }

  /**
   * Test-Case: Multi-Inheritance 1, test if the methods and fields are inherited correctly
   * the supertypes have one type variable and the subtype has two type variables
   */
  @Test
  public void testMultiInheritanceSubTypeMoreGen() throws IOException {
    //initialize symboltable
    ICombineExpressionsWithLiteralsScope scope = init_scope();

    //supertype SupA<T>
    TypeVarSymbol t = typeVariable("T");
    add2scope(scope, t);
    MethodSymbol testA = method("testA", SymTypeExpressionFactory.createTypeVariable("T", scope));
    FieldSymbol currentA = field("currentA", SymTypeExpressionFactory.createTypeVariable("T", scope));
    OOTypeSymbol supA = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(OOSymbolsMill.scope())
        .setName("SupA")
        .setEnclosingScope(scope)
        .build();
    supA.addMethodSymbol(testA);
    supA.addFieldSymbol(currentA);
    supA.addTypeVarSymbol(t);
    add2scope(scope, supA);
    SymTypeExpression supATExpr = SymTypeExpressionFactory
        .createGenerics("SupA", scope, Lists.newArrayList(SymTypeExpressionFactory.createTypeVariable("T", scope)));
    //supertype SupB<T>
    TypeVarSymbol s = typeVariable("S");
    MethodSymbol testB = method("testB", SymTypeExpressionFactory.createTypeVariable("S", scope));
    FieldSymbol currentB = field("currentB", SymTypeExpressionFactory.createTypeVariable("S", scope));
    OOTypeSymbol supB = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(OOSymbolsMill.scope())
        .setName("SupB")
        .setEnclosingScope(scope)
        .build();
    supB.addMethodSymbol(testB);
    supB.addFieldSymbol(currentB);
    supB.addTypeVarSymbol(s);
    add2scope(scope, supB);
    SymTypeExpression supBTExpr = SymTypeExpressionFactory
        .createGenerics("SupB", scope, Lists.newArrayList(SymTypeExpressionFactory.createTypeVariable("S", scope)));

    //subType SubA<T>
    t = typeVariable("T");
    s = typeVariable("S");
    OOTypeSymbol subA = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(OOSymbolsMill.scope())
        .setName("SubA")
        .setSuperTypesList(Lists.newArrayList(supATExpr, supBTExpr))
        .setEnclosingScope(scope)
        .build();
    subA.addTypeVarSymbol(s);
    subA.addTypeVarSymbol(t);
    add2scope(scope, subA);
    SymTypeExpression subATExpr = SymTypeExpressionFactory
        .createGenerics("SubA", scope, Lists.newArrayList(_charSymType, _booleanSymType));
    FieldSymbol sub = field("sub", subATExpr);
    add2scope(scope, sub);

    setFlatExpressionScopeSetter(scope);

    check("sub.testA()", "boolean");

    check("sub.currentA", "boolean");

    check("sub.testB()", "char");

    check("sub.currentB", "char");
  }

  /**
   * test if you can use methods, types and fields of the type or its supertypes in its method scopes
   */
  @Test
  public void testMethodScope() throws IOException {
    ICombineExpressionsWithLiteralsScope scope = init_scope();

    //super
    FieldSymbol elementField = field("element", _StringSymType);
    MethodSymbol add = OOSymbolsMill.methodSymbolBuilder()
        .setReturnType(_voidSymType)
        .setName("add")
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
        .build();
    add2scope(add.getSpannedScope(), elementField);
    FieldSymbol field = field("field", _booleanSymType);
    OOTypeSymbol superclass = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(OOSymbolsMill.scope())
        .setName("AList")
        .setEnclosingScope(scope)
        .build();
    superclass.addMethodSymbol(add);
    superclass.addFieldSymbol(field);
    add2scope(scope, superclass);
    SymTypeExpression supclass = SymTypeExpressionFactory.createTypeObject("AList", scope);

    //sub
    OOTypeSymbol subclass = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(OOSymbolsMill.scope())
        .setName("MyList")
        .setSuperTypesList(Lists.newArrayList(supclass))
        .setEnclosingScope(scope)
        .build();
    add2scope(scope, subclass);

    SymTypeExpression sub = SymTypeExpressionFactory.createTypeObject("MyList", scope);
    FieldSymbol myList = field("myList", sub);
    add2scope(scope, myList);

    //subsub
    FieldSymbol myNext = field("myNext", _StringSymType);
    MethodSymbol myAdd = OOSymbolsMill.methodSymbolBuilder()
        .setName("myAdd")
        .setReturnType(_voidSymType)
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
        .build();
    OOTypeSymbol subsubclass = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
        .setName("MySubList")
        .setSuperTypesList(Lists.newArrayList(sub))
        .setEnclosingScope(scope)
        .build();
    subsubclass.addMethodSymbol(myAdd);
    subsubclass.addFieldSymbol(myNext);
    //set correct scopes
    subsubclass.getSpannedScope().setEnclosingScope(scope);
    myAdd.getSpannedScope().setEnclosingScope(subsubclass.getSpannedScope());
    add2scope(scope, subsubclass);
    SymTypeExpression subsub = SymTypeExpressionFactory.createTypeObject("MySubList", scope);
    FieldSymbol mySubList = field("mySubList", subsub);
    add2scope(scope, mySubList);

    //set scope of method myAdd as standard resolving scope
    setFlatExpressionScopeSetter((CombineExpressionsWithLiteralsScope) myAdd.getSpannedScope());

    check("mySubList", "MySubList");

    check("myAdd()", "void");

    check("myNext", "String");

    check("add(\"Hello\")", "void");

    check("field", "boolean");
  }

  public void init_static_example(){
    //types A and B
    ICombineExpressionsWithLiteralsScope scope = init_scope();
    MethodSymbol atest = method("test",_voidSymType);
    atest.setIsStatic(true);
    FieldSymbol afield = field("field",_intSymType);
    afield.setIsStatic(true);
    OOTypeSymbol a = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
        .setName("A")
        .setEnclosingScope(scope)
        .build();
    a.addFieldSymbol(afield);
    a.addMethodSymbol(atest);
    //A has static inner type D
    OOTypeSymbol aD = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(OOSymbolsMill.scope())
        .setName("D")
        .setEnclosingScope(a.getSpannedScope())
        .build();
    aD.setIsStatic(true);
    add2scope(a.getSpannedScope(), aD);

    add2scope(scope,a);

    MethodSymbol btest = method("test",_voidSymType);
    FieldSymbol bfield = field("field",_intSymType);
    OOTypeSymbol b = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
        .setName("B")
        .setEnclosingScope(scope)
        .build();
    b.addFieldSymbol(bfield);
    b.addMethodSymbol(btest);
    //B has not static inner type D
    OOTypeSymbol bD = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(OOSymbolsMill.scope())
        .setName("D")
        .setEnclosingScope(b.getSpannedScope())
        .build();
    add2scope(b.getSpannedScope(), bD);

    add2scope(scope,b);
    //A has static method test, static field field, static type D
    //B has normal method test, normal field field, normal type D
    //type C extends A and has no method, field or type
    SymTypeExpression aSymType = SymTypeExpressionFactory.createTypeObject("A",scope);
    OOTypeSymbol c = OOSymbolsMill.oOTypeSymbolBuilder()
        .setSpannedScope(OOSymbolsMill.scope())
        .setName("C")
        .setSuperTypesList(Lists.newArrayList(aSymType))
        .setEnclosingScope(scope)
        .build();
    add2scope(scope,c);

    setFlatExpressionScopeSetter(scope);
  }

  @Test
  public void testStaticType() throws IOException {
    init_static_example();

    check("A.D", "D");
  }

  @Test
  public void testInvalidStaticType() throws IOException {
    init_static_example();

    checkError("B.D", "0xA0303");
  }

  @Test
  public void testStaticField() throws IOException {
    init_static_example();

    check("A.field", "int");
  }

  @Test
  public void testInvalidStaticField() throws IOException {
    init_static_example();

    checkError("B.field", "0xA0237");
  }

  @Test
  public void testStaticMethod() throws IOException {
    init_static_example();

    check("A.test()", "void");
  }

  @Test
  public void testInvalidStaticMethod() throws IOException {
    init_static_example();

    checkError("B.test()", "0xA0239");
  }

  @Test
  public void testSubClassesDoNotKnowStaticMethodsOfSuperClasses() throws IOException{
    init_static_example();

    checkError("C.test()", "0xA0239");
  }

  @Test
  public void testSubClassesDoNotKnowStaticFieldsOfSuperClasses() throws IOException{
    init_static_example();

    checkError("C.field", "0xA0306");
  }

  @Test
  public void testSubClassesDoNotKnowStaticTypesOfSuperClasses() throws IOException{
    init_static_example();

    Optional<ASTExpression> sType = p.parse_StringExpression("C.D");
    assertTrue(sType.isPresent());
    //TODO ND: complete when inner types are added
  }
}
