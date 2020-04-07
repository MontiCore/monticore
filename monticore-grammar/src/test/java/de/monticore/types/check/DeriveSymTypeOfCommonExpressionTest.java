/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import com.google.common.collect.Lists;
import de.monticore.ast.ASTNode;
import de.monticore.expressions.combineexpressionswithliterals._parser.CombineExpressionsWithLiteralsParser;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._symboltable.*;
import de.monticore.expressions.prettyprint.CombineExpressionsWithLiteralsPrettyPrinter;
import de.monticore.expressions.testoclexpressions._ast.ASTExtType;
import de.monticore.io.paths.ModelPath;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symboltable.ImportStatement;
import de.monticore.types.typesymbols._symboltable.*;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static de.monticore.types.check.DefsTypeBasic.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DeriveSymTypeOfCommonExpressionTest {

  private ExpressionsBasisScope scope;

  /**
   * Focus: Deriving Type of Literals, here:
   * literals/MCLiteralsBasis.mc4
   */

  @BeforeClass
  public static void setup() {
    LogStub.init();
    LogStub.enableFailQuick(false);
  }

  @Before
  public void doBefore() {
    LogStub.init();
  }

  // Parser used for convenience:
  // (may be any other Parser that understands CommonExpressions)
  CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();

  // This is the core Visitor under Test (but rather empty)
  DeriveSymTypeOfExpression derEx = new DeriveSymTypeOfExpression();

  // This is an auxiliary
  DeriveSymTypeOfCombineExpressionsDelegator derLit = new DeriveSymTypeOfCombineExpressionsDelegator(ExpressionsBasisSymTabMill
      .expressionsBasisScopeBuilder()
      .build(),
      new CombineExpressionsWithLiteralsPrettyPrinter(new IndentPrinter()));

  // other arguments not used (and therefore deliberately null)

  // This is the TypeChecker under Test:
  TypeCheck tc = new TypeCheck(null, derLit);

  /*--------------------------------------------------- TESTS ---------------------------------------------------------*/

  /**
   * test correctness of addition
   */
  @Test
  public void deriveFromPlusExpression() throws IOException {
    // example with two ints
    String s = "3+4";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("int", tc.typeOf(astex).print());

    // example with double and int
    s = "4.9+12";
    astex = p.parse_StringExpression(s).get();
    assertEquals("double", tc.typeOf(astex).print());

    // example with String
    s = "3 + \"Hallo\"";
    astex = p.parse_StringExpression(s).get();
    assertEquals("String", tc.typeOf(astex).print());
  }

  @Test
  public void testInvalidPlusExpression() throws IOException {
    String s = "3+true";
    ASTExpression astex = p.parse_StringExpression(s).get();
    try{
      tc.typeOf(astex);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0210"));
    }
  }

  /**
   * test correctness of subtraction
   */
  @Test
  public void deriveFromMinusExpression() throws IOException {
    // example with two ints
    String s = "7-2";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("int", tc.typeOf(astex).print());

    //example with float and long
    s = "7.9f-3L";
    astex = p.parse_StringExpression(s).get();
    assertEquals("float", tc.typeOf(astex).print());
  }

  @Test
  public void testInvalidMinusExpression() throws IOException {
    String s = "3-true";
    ASTExpression astex = p.parse_StringExpression(s).get();
    try{
      tc.typeOf(astex);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0213"));
    }
  }

  /**
   * test correctness of multiplication
   */
  @Test
  public void deriveFromMultExpression() throws IOException {
    //example with two ints
    String s = "2*19";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("int", tc.typeOf(astex).print());

    //example with long and char
    s = "\'a\'*3L";
    astex = p.parse_StringExpression(s).get();
    assertEquals("long", tc.typeOf(astex).print());
  }

  @Test
  public void testInvalidMultExpression() throws IOException {
    String s = "3*true";
    ASTExpression astex = p.parse_StringExpression(s).get();
    try{
      tc.typeOf(astex);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0211"));
    }
  }

  /**
   * test correctness of division
   */
  @Test
  public void deriveFromDivideExpression() throws IOException {
    //example with two ints
    String s = "7/12";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("int", tc.typeOf(astex).print());

    //example with float and double
    s = "5.4f/3.9";
    astex = p.parse_StringExpression(s).get();
    assertEquals("double", tc.typeOf(astex).print());
  }

  @Test
  public void testInvalidDivideExpression() throws IOException {
    String s = "3/true";
    ASTExpression astex = p.parse_StringExpression(s).get();
    try{
      tc.typeOf(astex);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0212"));
    }
  }

  /**
   * tests correctness of modulo
   */
  @Test
  public void deriveFromModuloExpression() throws IOException {
    //example with two ints
    String s = "3%1";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("int", tc.typeOf(astex).print());

    //example with long and double
    s = "0.8%3L";
    astex = p.parse_StringExpression(s).get();
    assertEquals("double", tc.typeOf(astex).print());
  }

  @Test
  public void testInvalidModuloExpression() throws IOException {
    String s = "3%true";
    ASTExpression astex = p.parse_StringExpression(s).get();
    try{
      tc.typeOf(astex);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0214"));
    }
  }

  /**
   * test LessEqualExpression
   */
  @Test
  public void deriveFromLessEqualExpression() throws IOException {
    //example with two ints
    String s = "4<=9";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("boolean", tc.typeOf(astex).print());

    //example with two other numeric types
    s = "2.4f<=3L";
    astex = p.parse_StringExpression(s).get();
    assertEquals("boolean", tc.typeOf(astex).print());
  }

  @Test
  public void testInvalidLessEqualExpression() throws IOException {
    String s = "3<=true";
    ASTExpression astex = p.parse_StringExpression(s).get();
    try{
      tc.typeOf(astex);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0215"));
    }
  }

  /**
   * test GreaterEqualExpression
   */
  @Test
  public void deriveFromGreaterEqualExpression() throws IOException {
    //example with two ints
    String s = "7>=2";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("boolean", tc.typeOf(astex).print());

    //example with two other numeric types
    s = "2.5>=\'d\'";
    astex = p.parse_StringExpression(s).get();
    assertEquals("boolean", tc.typeOf(astex).print());
  }

  @Test
  public void testInvalidGreaterEqualExpression() throws IOException {
    String s = "3>=true";
    ASTExpression astex = p.parse_StringExpression(s).get();
    try{
      tc.typeOf(astex);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0216"));
    }
  }

  /**
   * test LessThanExpression
   */
  @Test
  public void deriveFromLessThanExpression() throws IOException {
    //example with two ints
    String s = "4<9";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("boolean", tc.typeOf(astex).print());

    //example with two other numeric types
    s = "2.4f<3L";
    astex = p.parse_StringExpression(s).get();
    assertEquals("boolean", tc.typeOf(astex).print());
  }

  @Test
  public void testInvalidLessThanExpression() throws IOException {
    String s = "3<true";
    ASTExpression astex = p.parse_StringExpression(s).get();
    try{
      tc.typeOf(astex);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0217"));
    }
  }

  /**
   * test GreaterThanExpression
   */
  @Test
  public void deriveFromGreaterThanExpression() throws IOException {
    //example with two ints
    String s = "7>2";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("boolean", tc.typeOf(astex).print());

    //example with two other numeric types
    s = "2.5>\'d\'";
    astex = p.parse_StringExpression(s).get();
    assertEquals("boolean", tc.typeOf(astex).print());
  }

  @Test
  public void testInvalidGreaterThanExpression() throws IOException {
    String s = "3>true";
    ASTExpression astex = p.parse_StringExpression(s).get();
    try{
      tc.typeOf(astex);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0218"));
    }
  }

  /**
   * initialize basic scope and a few symbols for testing
   */
  public void init_basic() {
    // No enclosing Scope: Search ending here
    scope = scope(null, true, null, "Phantasy2");

    TypeSymbol person = DefsTypeBasic.type("Person");
    add2scope(scope, person);
    TypeSymbol student = DefsTypeBasic.type("Student",
        Lists.newArrayList(SymTypeExpressionFactory.createTypeObject("Person", scope))
    );
    add2scope(scope, student);
    TypeSymbol firstsemesterstudent = DefsTypeBasic.type("FirstSemesterStudent",
        Lists.newArrayList(SymTypeExpressionFactory.createTypeObject("Student", scope))
    );
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

    derLit.setScope(scope);
    tc = new TypeCheck(null, derLit);
  }

  /**
   * test EqualsExpression
   */
  @Test
  public void deriveFromEqualsExpression() throws IOException {
    //initialize symbol table
    init_basic();

    //example with two primitives
    String s = "7==9.5f";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("boolean", tc.typeOf(astex).print());

    //example with two objects of the same class
    s = "student1==student2";
    astex = p.parse_StringExpression(s).get();
    assertEquals("boolean", tc.typeOf(astex).print());

    //example with two objects in sub-supertype relation
    s = "person1==student1";
    astex = p.parse_StringExpression(s).get();
    assertEquals("boolean", tc.typeOf(astex).print());
  }

  @Test
  public void testInvalidEqualsExpression() throws IOException {
    init_basic();

    String s = "3==true";
    ASTExpression astex = p.parse_StringExpression(s).get();
    try{
      tc.typeOf(astex);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0219"));
    }
  }

  @Test
  public void testInvalidEqualsExpression2() throws IOException{
    init_basic();

    //person1 has the type Person, foo is a boolean
    String s = "person1==foo";
    ASTExpression astex = p.parse_StringExpression(s).get();
    try{
      tc.typeOf(astex);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0219"));
    }
  }

  /**
   * test NotEqualsExpression
   */
  @Test
  public void deriveFromNotEqualsExpression() throws IOException {
    //initialize symbol table
    init_basic();

    //example with two primitives
    String s = "true!=false";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("boolean", tc.typeOf(astex).print());

    //example with two objects of the same class
    s = "person1!=person2";
    astex = p.parse_StringExpression(s).get();
    assertEquals("boolean", tc.typeOf(astex).print());

    //example with two objects in sub-supertype relation
    s = "student2!=person2";
    astex = p.parse_StringExpression(s).get();
    assertEquals("boolean", tc.typeOf(astex).print());
  }

  @Test
  public void testInvalidNotEqualsExpression() throws IOException {
    init_basic();

    String s = "3!=true";
    ASTExpression astex = p.parse_StringExpression(s).get();
    try{
      tc.typeOf(astex);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0220"));
    }
  }

  @Test
  public void testInvalidNotEqualsExpression2() throws IOException{
    init_basic();
    //person1 is a Person, foo is a boolean
    String s = "person1!=foo";
    ASTExpression astex = p.parse_StringExpression(s).get();
    try{
      tc.typeOf(astex);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0220"));
    }
  }

  /**
   * test BooleanAndOpExpression
   */
  @Test
  public void deriveFromBooleanAndOpExpression() throws IOException {
    //only possible with two booleans
    String s = "true&&true";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("boolean", tc.typeOf(astex).print());

    s = "(3<=4&&5>6)";
    astex = p.parse_StringExpression(s).get();
    assertEquals("boolean", tc.typeOf(astex).print());
  }

  @Test
  public void testInvalidAndOpExpression() throws IOException {
    //only possible with two booleans
    String s = "3&&true";
    ASTExpression astex = p.parse_StringExpression(s).get();
    try{
      tc.typeOf(astex);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0223"));
    }
  }

  /**
   * test BooleanOrOpExpression
   */
  @Test
  public void deriveFromBooleanOrOpExpression() throws IOException {
    //only possible with two booleans
    String s = "true||false";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("boolean", tc.typeOf(astex).print());

    s = "(3<=4.5f||5.3>6)";
    astex = p.parse_StringExpression(s).get();
    assertEquals("boolean", tc.typeOf(astex).print());
  }

  @Test
  public void testInvalidOrOpExpression() throws IOException {
    //only possible with two booleans
    String s = "3||true";
    ASTExpression astex = p.parse_StringExpression(s).get();
    try{
      tc.typeOf(astex);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0226"));
    }
  }

  /**
   * test LogicalNotExpression
   */
  @Test
  public void deriveFromLogicalNotExpression() throws IOException {
    //only possible with boolean as inner expression
    String s = "!true";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("boolean", tc.typeOf(astex).print());

    s = "!(2.5>=0.3)";
    astex = p.parse_StringExpression(s).get();
    assertEquals("boolean", tc.typeOf(astex).print());
  }

  @Test
  public void testInvalidLogicalNotExpression() throws IOException {
    //only possible with a boolean as inner expression
    String s = "!4";
    ASTExpression astex = p.parse_StringExpression(s).get();
    try{
      tc.typeOf(astex);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0228"));
    }
  }

  /**
   * test BracketExpression
   */
  @Test
  public void deriveFromBracketExpression() throws IOException {
    //initialize symbol table
    init_basic();

    //test with only a literal in the inner expression
    String s = "(3)";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("int", tc.typeOf(astex).print());

    //test with a more complex inner expression
    s = "(3+4*(18-7.5))";
    astex = p.parse_StringExpression(s).get();
    assertEquals("double", tc.typeOf(astex).print());

    //test without primitive types in inner expression
    s = "(person1)";
    astex = p.parse_StringExpression(s).get();
    assertEquals("Person", tc.typeOf(astex).print());
  }

  @Test
  public void testInvalidBracketExpression() throws IOException {
    //a cannot be resolved -> a has no type
    String s = "(a)";
    ASTExpression astex = p.parse_StringExpression(s).get();
    try{
      tc.typeOf(astex);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0229"));
    }
  }

  /**
   * test ConditionalExpression
   */
  @Test
  public void deriveFromConditionalExpression() throws IOException {
    //initialize symbol table
    init_basic();

    //test with two ints as true and false expression
    String s = "3<4?9:10";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("int", tc.typeOf(astex).print());

    //test with float and long
    s = "3>4?4.5f:10L";
    astex = p.parse_StringExpression(s).get();
    assertEquals("float", tc.typeOf(astex).print());

    //test without primitive types as true and false expression
    s = "3<9?person1:person2";
    astex = p.parse_StringExpression(s).get();
    assertEquals("Person", tc.typeOf(astex).print());

    //test with two objects in a sub-supertype relation
    s = "3<9?student1:person2";
    astex = p.parse_StringExpression(s).get();
    assertEquals("Person", tc.typeOf(astex).print());
  }

  @Test
  public void testInvalidConditionalExpression() throws IOException {
    //true and 7 are not of the same type
    String s = "3<4?true:7";
    ASTExpression astex = p.parse_StringExpression(s).get();
    try{
      tc.typeOf(astex);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0234"));
    }
  }

  @Test
  public void testInvalidConditionalExpression2() throws IOException {
    //3 is not a boolean condition
    String s = "3?true:false";
    ASTExpression astex = p.parse_StringExpression(s).get();
    try{
      tc.typeOf(astex);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0234"));
    }
  }
  /**
   * test BooleanNotExpression
   */
  @Test
  public void deriveFromBooleanNotExpression() throws IOException {
    //test with a int
    String s = "~3";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("int", tc.typeOf(astex).print());
    //test with a char
    s = "~\'a\'";
    astex = p.parse_StringExpression(s).get();
    assertEquals("int", tc.typeOf(astex).print());
  }

  @Test
  public void testInvalidBooleanNotExpression() throws IOException {
    //only possible with an integral type (int, long, char, short, byte)
    String s = "~3.4";
    ASTExpression astex = p.parse_StringExpression(s).get();
    try{
      tc.typeOf(astex);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0236"));
    }
  }

  /**
   * initialize symboltable including global scope, artifact scopes and scopes with symbols for
   * testing (mostly used for FieldAccessExpressions)
   */
  public void init_advanced() {
    ExpressionsBasisGlobalScope globalScope = globalScope(new ExpressionsBasisLanguage(), new ModelPath());
    ExpressionsBasisArtifactScope artifactScope1 = artifactScope(globalScope, Lists.newArrayList(), "");
    ExpressionsBasisArtifactScope artifactScope2 = artifactScope(globalScope, Lists.newArrayList(), "");
    ExpressionsBasisArtifactScope artifactScope3 = artifactScope(globalScope, Lists.newArrayList(), "types2");
    ExpressionsBasisArtifactScope artifactScope4 = artifactScope(artifactScope3, Lists.newArrayList(), "types3");
    scope = scope(artifactScope1, true, null, "Phantasy2"); // No enclosing Scope: Search ending here
    ExpressionsBasisScope scope2 = scope(artifactScope2, "types");
    ExpressionsBasisScope scope3 = scope(artifactScope4, "types2");
    scope3.setEnclosingScope(artifactScope4);

    // some FieldSymbols (ie. Variables, Attributes)
    TypeSymbol person = DefsTypeBasic.type("Person");
    add2scope(scope2, person);
    add2scope(scope3, person);
    add2scope(scope, person);

    TypeSymbol student = DefsTypeBasic.type("Student",
        Lists.newArrayList(SymTypeExpressionFactory.createTypeObject("Person", scope))
    );
    add2scope(scope2, student);
    add2scope(scope3, student);
    add2scope(scope, student);
    TypeSymbol firstsemesterstudent = DefsTypeBasic.type("FirstSemesterStudent",
        Lists.newArrayList(SymTypeExpressionFactory.createTypeObject("Student", scope))
    );
    add2scope(scope2, firstsemesterstudent);
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
    TypeSymbol testType = type("Test",Lists.newArrayList(ms,ms1),Lists.newArrayList(fs),Lists.newArrayList(),Lists.newArrayList(),scope);
    TypeSymbol testType2 = type("Test",Lists.newArrayList(ms,ms1),Lists.newArrayList(fs),Lists.newArrayList(),Lists.newArrayList(),scope2);
    TypeSymbol testType3 = type("Test",Lists.newArrayList(ms,ms1),Lists.newArrayList(fs),Lists.newArrayList(),Lists.newArrayList(),scope3);
    ExpressionsBasisScope testScope = (ExpressionsBasisScope) testType3.getSpannedScope();

    FieldSymbol testVariable = field("testVariable",_shortSymType);
    testVariable.setIsStatic(true);
    TypeSymbol testInnerType = type("TestInnerType",Lists.newArrayList(),Lists.newArrayList(testVariable),Lists.newArrayList(),Lists.newArrayList(),testScope);
    testInnerType.setIsStatic(true);
    add2scope(testScope,testInnerType);
    add2scope(testInnerType.getSpannedScope(),testVariable);

    testType3.setSpannedScope(testScope);

    add2scope(scope2, testType2);
    add2scope(scope3, testType3);
    add2scope(scope,testType);

    derLit.setScope(scope);
    tc = new TypeCheck(null, derLit);
  }

  /**
   * test FieldAccessExpression
   */
  @Test
  public void deriveFromFieldAccessExpression() throws IOException {
    //initialize symbol table
    init_advanced();

    //test for type with only one package
    String s = "types.Test";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("Test", tc.typeOf(astex).print());

    //test for variable of a type with one package
    s = "types.Test.variable";
    astex = p.parse_StringExpression(s).get();
    assertEquals("int", tc.typeOf(astex).print());

    //test for type with more than one package
    s = "types2.types3.types2.Test";
    astex = p.parse_StringExpression(s).get();
    assertEquals("Test", tc.typeOf(astex).print());

    //test for variable of type with more than one package
    s = "types2.types3.types2.Test.variable";
    astex = p.parse_StringExpression(s).get();
    assertEquals("int", tc.typeOf(astex).print());

    s = "Test";
    astex = p.parse_StringExpression(s).get();
    assertEquals("Test", tc.typeOf(astex).print());

    //test for variable in inner type
    s="types2.types3.types2.Test.TestInnerType.testVariable";
    astex = p.parse_StringExpression(s).get();
    assertEquals("short",tc.typeOf(astex).print());
  }

  /**
   * test CallExpression
   */
  @Test
  public void deriveFromCallExpression() throws IOException {
    //initialize symbol table
    init_advanced();

    //test for method with unqualified name without parameters
    String s = "isInt()";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("boolean", tc.typeOf(astex).print());

    //test for method with unqualified name with parameters
    s = "isInt(4)";
    astex = p.parse_StringExpression(s).get();
    assertEquals("boolean", tc.typeOf(astex).print());

    //test for method with qualified name without parameters
    s = "types.Test.store()";
    astex = p.parse_StringExpression(s).get();
    assertEquals("double", tc.typeOf(astex).print());

    //test for method with qualified name with parameters
    s = "types.Test.pay(4)";
    astex = p.parse_StringExpression(s).get();
    assertEquals("void", tc.typeOf(astex).print());

    //test for String method
    s = "\"test\".hashCode()";
    astex = p.parse_StringExpression(s).get();
    assertEquals("int", tc.typeOf(astex).print());

    //test for multiple CallExpressions in a row
    s = "\"test\".toString().charAt(1)";
    astex = p.parse_StringExpression(s).get();
    assertEquals("char", tc.typeOf(astex).print());
  }

  @Test
  public void testInvalidCallExpression() throws IOException {
    //method isNot() is not in scope -> method cannot be resolved -> method has no return type
    String s = "isNot()";
    ASTExpression astex = p.parse_StringExpression(s).get();
    try{
      tc.typeOf(astex);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0240"));
    }
  }

  /**
   * initialize the symbol table for a basic inheritance example
   * we only have one scope and the symbols are all in this scope or in subscopes
   */
  public void init_inheritance() {
    // No enclosing Scope: Search ending here
    scope = scope(null, true, null, "Phantasy2");

    //inheritance example
    //super
    MethodSymbol add = add(method("add", _voidSymType), field("element", _StringSymType));
    FieldSymbol field = field("field", _booleanSymType);
    TypeSymbol superclass = type("AList", Lists.newArrayList(add), Lists.newArrayList(field),
        Lists.newArrayList(), Lists.newArrayList()
    );
    add2scope(scope, superclass);
    SymTypeExpression supclass = SymTypeExpressionFactory.createTypeObject("AList", scope);

    //sub
    TypeSymbol subclass = type("MyList", Lists.newArrayList(), Lists.newArrayList(),
        Lists.newArrayList(supclass), Lists.newArrayList()
    );
    add2scope(scope, subclass);

    SymTypeExpression sub = SymTypeExpressionFactory.createTypeObject("MyList", scope);
    FieldSymbol myList = field("myList", sub);
    add2scope(scope, myList);

    //subsub
    TypeSymbol subsubclass = type("MySubList", Lists.newArrayList(), Lists.newArrayList(),
        Lists.newArrayList(supclass), Lists.newArrayList()
    );
    add2scope(scope, subsubclass);
    SymTypeExpression subsub = SymTypeExpressionFactory.createTypeObject("MySubList", scope);
    FieldSymbol mySubList = field("mySubList", subsub);
    add2scope(scope, mySubList);

    derLit.setScope(scope);
    tc = new TypeCheck(null, derLit);
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
    String s = "myList.add(\"Hello\")";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("void", tc.typeOf(astex).print());

    //test inheritance over two levels
    s = "mySubList.add(\"World\")";
    astex = p.parse_StringExpression(s).get();
    assertEquals("void", tc.typeOf(astex).print());

    //fields
    s = "myList.field";
    astex = p.parse_StringExpression(s).get();
    assertEquals("boolean", tc.typeOf(astex).print());

    s = "mySubList.field";
    astex = p.parse_StringExpression(s).get();
    assertEquals("boolean", tc.typeOf(astex).print());
  }

  /**
   * initialize an empty scope
   */
  public void init_scope() {
    // No enclosing Scope: Search ending here
    scope = scope(null, true, null, "Phantasy2");
  }

  /**
   * test the inheritance of a generic type with one type variable
   */
  @Test
  public void testListAndArrayListInheritance() throws IOException {
    //initialize symboltable
    init_scope();
    //one generic parameter, supertype List<T>
    TypeVarSymbol t = typeVariable("T");
    add2scope(scope, t);
    MethodSymbol addMethod = add(method("add", _booleanSymType),
        field("x", SymTypeExpressionFactory.createTypeVariable("T", scope))
    );
    FieldSymbol nextField = field("next", SymTypeExpressionFactory.createTypeVariable("T", scope));
    TypeSymbol sym = type("List", Lists.newArrayList(addMethod), Lists.newArrayList(nextField),
        Lists.newArrayList(), Lists.newArrayList(t)
    );
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
    TypeSymbol subsym = type("ArrayList", Lists.newArrayList(), Lists.newArrayList(),
        Lists.newArrayList(listTSymTypeExp), Lists.newArrayList(arrayListT)
    );
    add2scope(scope, subsym);
    SymTypeExpression subsymexp = SymTypeExpressionFactory.
        createGenerics("ArrayList", scope, Lists.newArrayList(_intSymType));
    FieldSymbol arraylistVar = field("arraylistVar", subsymexp);
    add2scope(scope, arraylistVar);

    derLit.setScope(scope);
    tc = new TypeCheck(null, derLit);

    //test methods and fields of the supertype
    String s = "listVar.add(2)";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("boolean", tc.typeOf(astex).print());

    s = "listVar.next";
    astex = p.parse_StringExpression(s).get();
    assertEquals("int", tc.typeOf(astex).print());

    //test inherited methods and fields of the subtype
    s = "arraylistVar.add(3)";
    astex = p.parse_StringExpression(s).get();
    assertEquals("boolean", tc.typeOf(astex).print());

    s = "arraylistVar.next";
    astex = p.parse_StringExpression(s).get();
    assertEquals("int", tc.typeOf(astex).print());
  }

  /**
   * test the inheritance of generic types with two type variables
   */
  @Test
  public void testGenericInheritanceTwoTypeVariables() throws IOException {
    //initialize symboltable
    init_scope();

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
    TypeSymbol genSup = type("GenSup", Lists.newArrayList(load,load.deepClone()), Lists.newArrayList(f1, f2),
        Lists.newArrayList(), Lists.newArrayList(t1, t2)
    );
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
    TypeSymbol genSub = type("GenSub", Lists.newArrayList(), Lists.newArrayList(f1.deepClone()),
        Lists.newArrayList(genTypeSV.deepClone()), Lists.newArrayList(t1, t2)
    );
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
    TypeSymbol genSubSub = type("GenSubSub", Lists.newArrayList(), Lists.newArrayList(),
        Lists.newArrayList(genSubTypeSV.deepClone()), Lists.newArrayList(t2, t1)
    );
    add2scope(scope, genSubSub);
    SymTypeExpression genSubSubType = SymTypeExpressionFactory.
        createGenerics("GenSubSub", scope, Lists.newArrayList(_StringSymType, _intSymType));
    FieldSymbol genSubSubVar = field("genSubSubVar", genSubSubType);
    add2scope(scope, genSubSubVar);


    derLit.setScope(scope);
    tc = new TypeCheck(null, derLit);

    //supertype: test methods and fields
    String s = "genSupVar.load(3)";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("String", tc.typeOf(astex).print());

    s = "genSupVar.f1";
    astex = p.parse_StringExpression(s).get();
    assertEquals("String", tc.typeOf(astex).print());

    s = "genSupVar.f2";
    astex = p.parse_StringExpression(s).get();
    assertEquals("int", tc.typeOf(astex).print());

    //subtype: test inherited methods and fields
    s = "genSubVar.load(3)";
    astex = p.parse_StringExpression(s).get();
    assertEquals("String", tc.typeOf(astex).print());

    s = "genSubVar.f1";
    astex = p.parse_StringExpression(s).get();
    assertEquals("String", tc.typeOf(astex).print());

    s = "genSubVar.f2";
    astex = p.parse_StringExpression(s).get();
    assertEquals("int", tc.typeOf(astex).print());

    //subsubtype: test inherited methods and fields
    s = "genSubSubVar.load(\"Hello\")";
    astex = p.parse_StringExpression(s).get();
    assertEquals("int", tc.typeOf(astex).print());

    s = "genSubSubVar.f1";
    astex = p.parse_StringExpression(s).get();
    assertEquals("int", tc.typeOf(astex).print());

    s = "genSubSubVar.f2";
    astex = p.parse_StringExpression(s).get();
    assertEquals("String", tc.typeOf(astex).print());
  }

  /**
   * test if methods and a field from a fixed subtype(generic type, but instead of type variable concrete type)
   * are inherited correctly
   */
  @Test
  public void testSubVarSupFix() throws IOException {
    //initialize symboltable
    init_scope();

    //subtype with variable generic parameter, supertype with fixed generic parameter
    //supertype with fixed generic parameter FixGen<A> and SymType FixGen<int>
    TypeVarSymbol a = typeVariable("A");
    add2scope(scope, a);
    MethodSymbol add2 = add(method("add", _booleanSymType),
        field("a", SymTypeExpressionFactory.createTypeVariable("A", scope))
    );
    FieldSymbol next2 = field("next", SymTypeExpressionFactory.createTypeVariable("A", scope));
    TypeSymbol fixGen = type("FixGen", Lists.newArrayList(add2), Lists.newArrayList(next2),
        Lists.newArrayList(), Lists.newArrayList(a)
    );
    add2scope(scope, fixGen);
    SymTypeExpression fixGenType = SymTypeExpressionFactory.createGenerics("FixGen", scope,
        Lists.newArrayList(_intSymType));
    FieldSymbol fixGenVar = field("fixGenVar", fixGenType);
    add2scope(scope, fixGenVar);

    //subtype with variable generic parameter VarGen<N> which extends FixGen<int>, SymType VarGen<String>
    TypeVarSymbol n = typeVariable("N");
    add2scope(scope, n);
    MethodSymbol calculate = method("calculate", SymTypeExpressionFactory.createTypeVariable("N", scope));
    TypeSymbol varGenType = type("VarGen", Lists.newArrayList(calculate), Lists.newArrayList(),
        Lists.newArrayList(fixGenType), Lists.newArrayList(n)
    );
    add2scope(scope, varGenType);
    SymTypeExpression varGenSym = SymTypeExpressionFactory.
        createGenerics("VarGen", scope, Lists.newArrayList(_StringSymType));
    FieldSymbol varGen = field("varGen", varGenSym);
    add2scope(scope, varGen);

    derLit.setScope(scope);
    tc = new TypeCheck(null, derLit);

    //test own methods first
    String s = "varGen.calculate()";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("String", tc.typeOf(astex).print());

    //test inherited methods and fields
    s = "varGen.add(4)";
    astex = p.parse_StringExpression(s).get();
    assertEquals("boolean", tc.typeOf(astex).print());

    s = "varGen.next";
    astex = p.parse_StringExpression(s).get();
    assertEquals("int", tc.typeOf(astex).print());
  }

  /**
   * Test-Case: SubType has more generic parameters than its supertype
   */
  @Test
  public void testSubTypeWithMoreGenericParameters() throws IOException {
    //initialize symboltable
    init_scope();

    //one generic parameter, supertype List<T>
    TypeVarSymbol t = typeVariable("T");
    add2scope(scope, t);
    MethodSymbol addMethod = add(method("add", _booleanSymType),
        field("x", SymTypeExpressionFactory.createTypeVariable("T", scope))
    );
    FieldSymbol nextField = field("next", SymTypeExpressionFactory.createTypeVariable("T", scope));
    TypeSymbol sym = type("List", Lists.newArrayList(addMethod), Lists.newArrayList(nextField),
        Lists.newArrayList(), Lists.newArrayList(t)
    );
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
    TypeSymbol moreGenType = type("MoreGen", Lists.newArrayList(insert), Lists.newArrayList(),
        Lists.newArrayList(listTSymTypeExp), Lists.newArrayList(t, moreType1)
    );
    add2scope(scope, moreGenType);
    SymTypeExpression moreGenSym = SymTypeExpressionFactory.
        createGenerics("MoreGen", scope, Lists.newArrayList(_intSymType, _longSymType));
    FieldSymbol moreGen = field("moreGen", moreGenSym);
    add2scope(scope, moreGen);

    derLit.setScope(scope);
    tc = new TypeCheck(null, derLit);

    //test own method
    String s = "moreGen.insert(12L)";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("int", tc.typeOf(astex).print());

    //test inherited methods and fields
    s = "moreGen.add(12)";
    astex = p.parse_StringExpression(s).get();
    assertEquals("boolean", tc.typeOf(astex).print());

    s = "moreGen.next";
    astex = p.parse_StringExpression(s).get();
    assertEquals("int", tc.typeOf(astex).print());
  }

  /**
   * Test-Case: SubType is a normal object type and extends a fixed generic type
   */
  @Test
  public void testSubTypeWithoutGenericParameter() throws IOException {
    //initialize symboltable
    init_scope();

    //one generic parameter, supertype List<T>
    TypeVarSymbol t = typeVariable("T");
    add2scope(scope, t);
    MethodSymbol addMethod = add(method("add", _booleanSymType),
        field("x", SymTypeExpressionFactory.createTypeVariable("T", scope))
    );
    FieldSymbol nextField = field("next", SymTypeExpressionFactory.createTypeVariable("T", scope));
    TypeSymbol sym = type("List", Lists.newArrayList(addMethod), Lists.newArrayList(nextField),
        Lists.newArrayList(), Lists.newArrayList(t)
    );
    add2scope(scope, sym);
    SymTypeExpression listIntSymTypeExp = SymTypeExpressionFactory
        .createGenerics("List", scope, Lists.newArrayList(_intSymType));
    FieldSymbol listVar = field("listVar", listIntSymTypeExp);
    add2scope(scope, listVar);

    //subtype without generic parameter NotGen extends List<int>
    TypeSymbol notgeneric = type("NotGen", Lists.newArrayList(), Lists.newArrayList(),
        Lists.newArrayList(listIntSymTypeExp), Lists.newArrayList()
    );
    add2scope(scope, notgeneric);
    SymTypeExpression notgenericType = SymTypeExpressionFactory.createTypeObject("NotGen", scope);
    FieldSymbol ng = field("notGen", notgenericType);
    add2scope(scope, ng);

    derLit.setScope(scope);
    tc = new TypeCheck(null, derLit);

    //test inherited methods and fields
    String s = "notGen.add(14)";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("boolean", tc.typeOf(astex).print());

    s = "notGen.next";
    astex = p.parse_StringExpression(s).get();
    assertEquals("int", tc.typeOf(astex).print());
  }

  /**
   * Test-Case: Multi-Inheritance 1, test if the methods and fields are inherited correctly
   * every type in the example has exactly one type variable
   */
  @Test
  public void testMultiInheritance() throws IOException {
    //initialize symboltable
    init_scope();

    //supertype SupA<T>
    TypeVarSymbol t = typeVariable("T");
    add2scope(scope, t);
    MethodSymbol testA = method("testA", SymTypeExpressionFactory.createTypeVariable("T", scope));
    FieldSymbol currentA = field("currentA", SymTypeExpressionFactory.createTypeVariable("T", scope));
    TypeSymbol supA = type("SupA", Lists.newArrayList(testA), Lists.newArrayList(currentA),
        Lists.newArrayList(), Lists.newArrayList(t)
    );
    add2scope(scope, supA);
    SymTypeExpression supATExpr = SymTypeExpressionFactory
        .createGenerics("SupA", scope, Lists.newArrayList(SymTypeExpressionFactory.createTypeVariable("T", scope)));

    //supertype SupB<T>
    t = typeVariable("T");
    MethodSymbol testB = method("testB", SymTypeExpressionFactory.createTypeVariable("T", scope));
    FieldSymbol currentB = field("currentB", SymTypeExpressionFactory.createTypeVariable("T", scope));
    TypeSymbol supB = type("SupB", Lists.newArrayList(testB), Lists.newArrayList(currentB),
        Lists.newArrayList(), Lists.newArrayList(t));
    add2scope(scope, supB);
    SymTypeExpression supBTExpr = SymTypeExpressionFactory.
        createGenerics("SupB", scope, Lists.newArrayList(SymTypeExpressionFactory.createTypeVariable("T", scope)));

    //subType SubA<T>
    t = typeVariable("T");
    TypeSymbol subA = type("SubA", Lists.newArrayList(), Lists.newArrayList(),
        Lists.newArrayList(supATExpr, supBTExpr), Lists.newArrayList(t));
    add2scope(scope, subA);
    SymTypeExpression subATExpr = SymTypeExpressionFactory
        .createGenerics("SubA", scope, Lists.newArrayList(_charSymType));
    FieldSymbol sub = field("sub", subATExpr);
    add2scope(scope, sub);

    derLit.setScope(scope);
    tc = new TypeCheck(null, derLit);

    String s = "sub.testA()";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("char", tc.typeOf(astex).print());

    s = "sub.currentA";
    astex = p.parse_StringExpression(s).get();
    assertEquals("char", tc.typeOf(astex).print());

    s = "sub.testB()";
    astex = p.parse_StringExpression(s).get();
    assertEquals("char", tc.typeOf(astex).print());

    s = "sub.currentB";
    astex = p.parse_StringExpression(s).get();
    assertEquals("char", tc.typeOf(astex).print());
  }

  /**
   * Test-Case: Multi-Inheritance 1, test if the methods and fields are inherited correctly
   * the supertypes have one type variable and the subtype has two type variables
   */
  @Test
  public void testMultiInheritanceSubTypeMoreGen() throws IOException {
    //initialize symboltable
    init_scope();

    //supertype SupA<T>
    TypeVarSymbol t = typeVariable("T");
    add2scope(scope, t);
    MethodSymbol testA = method("testA", SymTypeExpressionFactory.createTypeVariable("T", scope));
    FieldSymbol currentA = field("currentA", SymTypeExpressionFactory.createTypeVariable("T", scope));
    TypeSymbol supA = type("SupA", Lists.newArrayList(testA), Lists.newArrayList(currentA),
        Lists.newArrayList(), Lists.newArrayList(t)
    );
    add2scope(scope, supA);
    SymTypeExpression supATExpr = SymTypeExpressionFactory
        .createGenerics("SupA", scope, Lists.newArrayList(SymTypeExpressionFactory.createTypeVariable("T", scope)));
    //supertype SupB<T>
    TypeVarSymbol s = typeVariable("S");
    MethodSymbol testB = method("testB", SymTypeExpressionFactory.createTypeVariable("S", scope));
    FieldSymbol currentB = field("currentB", SymTypeExpressionFactory.createTypeVariable("S", scope));
    TypeSymbol supB = type("SupB", Lists.newArrayList(testB), Lists.newArrayList(currentB),
        Lists.newArrayList(), Lists.newArrayList(s)
    );
    add2scope(scope, supB);
    SymTypeExpression supBTExpr = SymTypeExpressionFactory
        .createGenerics("SupB", scope, Lists.newArrayList(SymTypeExpressionFactory.createTypeVariable("S", scope)));

    //subType SubA<T>
    t = typeVariable("T");
    s = typeVariable("S");
    TypeSymbol subA = type("SubA", Lists.newArrayList(), Lists.newArrayList(),
        Lists.newArrayList(supATExpr, supBTExpr), Lists.newArrayList(s, t)
    );
    add2scope(scope, subA);
    SymTypeExpression subATExpr = SymTypeExpressionFactory
        .createGenerics("SubA", scope, Lists.newArrayList(_charSymType, _booleanSymType));
    FieldSymbol sub = field("sub", subATExpr);
    add2scope(scope, sub);

    derLit.setScope(scope);
    tc = new TypeCheck(null, derLit);

    String s1 = "sub.testA()";
    ASTExpression astex = p.parse_StringExpression(s1).get();
    assertEquals("boolean", tc.typeOf(astex).print());

    s1 = "sub.currentA";
    astex = p.parse_StringExpression(s1).get();
    assertEquals("boolean", tc.typeOf(astex).print());

    s1 = "sub.testB()";
    astex = p.parse_StringExpression(s1).get();
    assertEquals("char", tc.typeOf(astex).print());

    s1 = "sub.currentB";
    astex = p.parse_StringExpression(s1).get();
    assertEquals("char", tc.typeOf(astex).print());
  }

  /**
   * test if you can use methods, types and fields of the type or its supertypes in its method scopes
   */
  @Test
  public void testMethodScope() throws IOException {
    init_scope();

    //super
    MethodSymbol add = add(method("add", _voidSymType), field("element", _StringSymType));
    FieldSymbol field = field("field", _booleanSymType);
    TypeSymbol superclass = type("AList", Lists.newArrayList(add), Lists.newArrayList(field),
        Lists.newArrayList(), Lists.newArrayList()
    );
    add2scope(scope, superclass);
    SymTypeExpression supclass = SymTypeExpressionFactory.createTypeObject("AList", scope);

    //sub
    TypeSymbol subclass = type("MyList", Lists.newArrayList(), Lists.newArrayList(),
        Lists.newArrayList(supclass), Lists.newArrayList()
    );
    add2scope(scope, subclass);

    SymTypeExpression sub = SymTypeExpressionFactory.createTypeObject("MyList", scope);
    FieldSymbol myList = field("myList", sub);
    add2scope(scope, myList);

    //subsub
    FieldSymbol myNext = field("myNext", _StringSymType);
    MethodSymbol myAdd = method("myAdd", _voidSymType);
    TypeSymbol subsubclass = type("MySubList", Lists.newArrayList(myAdd), Lists.newArrayList(myNext),
        Lists.newArrayList(supclass), Lists.newArrayList(), scope);
    add2scope(scope, subsubclass);
    SymTypeExpression subsub = SymTypeExpressionFactory.createTypeObject("MySubList", scope);
    FieldSymbol mySubList = field("mySubList", subsub);
    add2scope(scope, mySubList);

    //set scope of method myAdd as standard resolving scope
    derLit.setScope((ExpressionsBasisScope)myAdd.getSpannedScope());
    tc = new TypeCheck(null, derLit);

    String s = "mySubList";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("MySubList", tc.typeOf(astex).print());

    s = "myAdd()";
    astex = p.parse_StringExpression(s).get();
    assertEquals("void", tc.typeOf(astex).print());

    s = "myNext";
    astex = p.parse_StringExpression(s).get();
    assertEquals("String", tc.typeOf(astex).print());

    s = "add(\"Hello\")";
    astex = p.parse_StringExpression(s).get();
    assertEquals("void", tc.typeOf(astex).print());

    s = "field";
    astex = p.parse_StringExpression(s).get();
    assertEquals("boolean", tc.typeOf(astex).print());
  }

  public void init_static_example(){
    //types A and B
    scope = scope();
    MethodSymbol atest = method("test",_voidSymType);
    atest.setIsStatic(true);
    FieldSymbol afield = field("field",_intSymType);
    afield.setIsStatic(true);
    TypeSymbol a = type("A",Lists.newArrayList(atest),Lists.newArrayList(afield),Lists.newArrayList(),Lists.newArrayList(),scope);
    TypeSymbol aD = type("D",Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(),(ExpressionsBasisScope) a.getSpannedScope());
    aD.setIsStatic(true);
    a.getSpannedScope().add(aD);

    add2scope(scope,a);

    MethodSymbol btest = method("test",_voidSymType);
    FieldSymbol bfield = field("field",_intSymType);
    TypeSymbol b = type("B",Lists.newArrayList(btest),Lists.newArrayList(bfield),Lists.newArrayList(),Lists.newArrayList(),scope);
    TypeSymbol bD = type("D",Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(),(ExpressionsBasisScope) b.getSpannedScope());
    b.getSpannedScope().add(bD);

    add2scope(scope,b);
    //A has static method test, static field field, static type D
    //B has normal method test, normal field field, normal type D
    //type C extends A and has no method, field or type
    SymTypeExpression aSymType = SymTypeExpressionFactory.createTypeObject("A",scope);
    TypeSymbol c = type("C",Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(aSymType),Lists.newArrayList(),scope);
    add2scope(scope,c);

    derLit.setScope(scope);
    tc = new TypeCheck(null,derLit);
  }

  @Test
  public void testStaticType() throws IOException {
    init_static_example();

    Optional<ASTExpression> sType = p.parse_StringExpression("A.D");
    assertTrue(sType.isPresent());
    assertEquals("D",tc.typeOf(sType.get()).print());
  }

  @Test
  public void testInvalidStaticType() throws IOException {
    init_static_example();

    Optional<ASTExpression> sType = p.parse_StringExpression("B.D");
    assertTrue(sType.isPresent());
    try{
      tc.typeOf(sType.get());
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0303"));
    }
  }

  @Test
  public void testStaticField() throws IOException {
    init_static_example();

    Optional<ASTExpression> sField = p.parse_StringExpression("A.field");
    assertTrue(sField.isPresent());
    assertEquals("int",tc.typeOf(sField.get()).print());
  }

  @Test
  public void testInvalidStaticField() throws IOException {
    init_static_example();

    Optional<ASTExpression> sField = p.parse_StringExpression("B.field");
    assertTrue(sField.isPresent());
    try{
      tc.typeOf(sField.get());
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0237"));
    }
  }

  @Test
  public void testStaticMethod() throws IOException {
    init_static_example();

    Optional<ASTExpression> sMethod = p.parse_StringExpression("A.test()");
    assertTrue(sMethod.isPresent());
    assertEquals("void",tc.typeOf(sMethod.get()).print());
  }

  @Test
  public void testInvalidStaticMethod() throws IOException {
    init_static_example();

    Optional<ASTExpression> sMethod = p.parse_StringExpression("B.test()");
    assertTrue(sMethod.isPresent());
    try{
      tc.typeOf(sMethod.get());
    }catch (RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0239"));
    }
  }

  @Test
  public void testSubClassesDoNotKnowStaticMethodsOfSuperClasses() throws IOException{
    init_static_example();

    Optional<ASTExpression> sMethod = p.parse_StringExpression("C.test()");
    assertTrue(sMethod.isPresent());

    try{
      tc.typeOf(sMethod.get());
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0239"));
    }
  }

  @Test
  public void testSubClassesDoNotKnowStaticFieldsOfSuperClasses() throws IOException{
    init_static_example();

    Optional<ASTExpression> sField = p.parse_StringExpression("C.field");
    assertTrue(sField.isPresent());

    try{
      tc.typeOf(sField.get());
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0306"));
    }
  }

  //TODO: the same for types
  @Test
  public void testSubClassesDoNotKnowStaticTypesOfSuperClasses() throws IOException{
    init_static_example();

    Optional<ASTExpression> sType = p.parse_StringExpression("C.D");
    assertTrue(sType.isPresent());
    //TODO
  }

  /**
   * create a scope (some defaults apply)
   */
  public static ExpressionsBasisScope scope() {
    return ExpressionsBasisSymTabMill.expressionsBasisScopeBuilder().build();
  }

  public static ExpressionsBasisScope scope(IExpressionsBasisScope enclosingScope, boolean exportingSymbols, ASTNode astnode, String name) {
    return ExpressionsBasisSymTabMill.expressionsBasisScopeBuilder()
        .setEnclosingScope(enclosingScope)
        .setExportingSymbols(exportingSymbols)
        .setAstNode(astnode)
        .setName(name)
        .build();
  }

  public static ExpressionsBasisScope scope(IExpressionsBasisScope enclosingScope, String name) {
    return ExpressionsBasisSymTabMill.expressionsBasisScopeBuilder()
        .setEnclosingScope(enclosingScope)
        .setName(name)
        .build();
  }

  /**
   * create a global scope (some defaults apply)
   */
  public static ExpressionsBasisGlobalScope globalScope(ExpressionsBasisLanguage expressionsBasisLanguage, ModelPath modelPath) {
    return ExpressionsBasisSymTabMill.expressionsBasisGlobalScopeBuilder()
        .setExpressionsBasisLanguage(expressionsBasisLanguage)
        .setModelPath(modelPath)
        .build();
  }

  /**
   * create an artifact scope (some defaults apply)
   */
  public static ExpressionsBasisArtifactScope artifactScope(IExpressionsBasisScope enclosingScope, List<ImportStatement> importList, String packageName) {
    return ExpressionsBasisSymTabMill.expressionsBasisArtifactScopeBuilder()
        .setEnclosingScope(enclosingScope)
        .setImportList(importList)
        .setPackageName(packageName)
        .build();
  }
}
