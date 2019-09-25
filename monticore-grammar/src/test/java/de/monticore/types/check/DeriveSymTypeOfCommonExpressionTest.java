package de.monticore.types.check;

import com.google.common.collect.Lists;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._symboltable.*;
import de.monticore.io.paths.ModelPath;
import de.monticore.types.typesymbols._symboltable.FieldSymbol;
import de.monticore.types.typesymbols._symboltable.MethodSymbol;
import de.monticore.types.typesymbols._symboltable.TypeSymbol;
import de.monticore.types.typesymbols._symboltable.TypeSymbolsSymTabMill;
import de.monticore.expressions.combineexpressionswithliterals._parser.CombineExpressionsWithLiteralsParser;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static de.monticore.types.check.DefsTypeBasic.*;
import static org.junit.Assert.assertEquals;

public class DeriveSymTypeOfCommonExpressionTest {

  private ExpressionsBasisScope scope;

  /**
   * Focus: Deriving Type of Literals, here:
   *    literals/MCLiteralsBasis.mc4
   */

  @BeforeClass
  public static void setup() {
    LogStub.init();
    LogStub.enableFailQuick(false);
  }

  @Before
  public void setupForEach() {
    // Setting up a Scope Infrastructure (without a global Scope)
    scope =
        ExpressionsBasisSymTabMill.expressionsBasisScopeBuilder()
            .setEnclosingScope(null)       // No enclosing Scope: Search ending here
            .setExportsSymbols(true)
            .setAstNode(null)
            .setName("Phantasy2").build();     // hopefully unused

   ExpressionsBasisGlobalScope globalScope = ExpressionsBasisSymTabMill.expressionsBasisGlobalScopeBuilder().setLanguage(new ExpressionsBasisLanguage()).setModelPath(new ModelPath()).build();
   ExpressionsBasisArtifactScope artifactScope1 = ExpressionsBasisSymTabMill.expressionsBasisArtifactScopeBuilder().setEnclosingScope(globalScope).setImportList(Lists.newArrayList()).setPackageName("").build();
   ExpressionsBasisArtifactScope artifactScope2 = ExpressionsBasisSymTabMill.expressionsBasisArtifactScopeBuilder().setEnclosingScope(globalScope).setImportList(Lists.newArrayList()).setPackageName("").build();
   ExpressionsBasisArtifactScope artifactScope3 = ExpressionsBasisSymTabMill.expressionsBasisArtifactScopeBuilder().setEnclosingScope(globalScope).setImportList(Lists.newArrayList()).setPackageName("types2").build();
   ExpressionsBasisArtifactScope artifactScope4 = ExpressionsBasisSymTabMill.expressionsBasisArtifactScopeBuilder().setEnclosingScope(artifactScope3).setImportList(Lists.newArrayList()).setPackageName("types3").build();
   scope.setEnclosingScope(artifactScope1);
   ExpressionsBasisScope scope2 = ExpressionsBasisSymTabMill.expressionsBasisScopeBuilder().setName("types").build();
   scope2.setEnclosingScope(artifactScope2);
   ExpressionsBasisScope scope3 = ExpressionsBasisSymTabMill.expressionsBasisScopeBuilder().setName("types2").build();
   scope3.setEnclosingScope(artifactScope4);

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
    TypeSymbol s = new TypeSymbol("Student");
    s.setSuperTypes(Lists.newArrayList(SymTypeExpressionFactory.createTypeObject("Person",p)));
    TypeSymbol f = new TypeSymbol("FirstSemesterStudent");
    f.setSuperTypes(Lists.newArrayList(SymTypeExpressionFactory.createTypeObject("Student",s)));
    add2scope(scope, field("foo", _intSymType));
    add2scope(scope, field("bar2", _booleanSymType));
    add2scope(scope, field("person1",SymTypeExpressionFactory.createTypeObject("Person",p)));
    add2scope(scope, field("person2",SymTypeExpressionFactory.createTypeObject("Person",p)));
    add2scope(scope, field("student1",SymTypeExpressionFactory.createTypeObject("Student",s)));
    add2scope(scope, field("student2",SymTypeExpressionFactory.createTypeObject("Student",s)));
    add2scope(scope, field("firstsemester",SymTypeExpressionFactory.createTypeObject("FirstSemesterStudent",f)));
    add2scope(scope, method("isInt",_booleanSymType));
    add2scope(scope,add(method("isInt",_booleanSymType),TypeSymbolsSymTabMill.fieldSymbolBuilder().setName("maxLength").setType(_intSymType).build()));
    TypeSymbol ts = type("Test","Test");
    FieldSymbol fs = field("variable",_intSymType);
    MethodSymbol ms = method("store",_doubleSymType);
    MethodSymbol ms1 = add(method("pay",_voidSymType),TypeSymbolsSymTabMill.fieldSymbolBuilder().setName("cost").setType(_intSymType).build());

    ts.setSpannedScope(ExpressionsBasisSymTabMill.expressionsBasisScopeBuilder().setEnclosingScope(scope2).build());
    add2scope(scope2,add(add(add(ts,fs),ms),ms1));
    add2scope(scope3,add(add(add(ts,fs),ms),ms1));
    ts.getSpannedScope().add(fs);
    ts.getSpannedScope().add(ms);
    ts.getSpannedScope().add(ms1);
    derLit.setScope(scope);
  }

  // Parer used for convenience:
  // (may be any other Parser that understands CommonExpressions)
  CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();

  // This is the core Visitor under Test (but rather empty)
  DeriveSymTypeOfExpression derEx = new DeriveSymTypeOfExpression();

  // This is an auxiliary
  DeriveSymTypeOfCombineExpressions derLit = new DeriveSymTypeOfCombineExpressions(ExpressionsBasisSymTabMill.expressionsBasisScopeBuilder().build());

  // other arguments not used (and therefore deliberately null)

  // This is the TypeChecker under Test:
  TypeCheck tc = new TypeCheck(null,derLit);

  /*--------------------------------------------------- TESTS ---------------------------------------------------------*/

  /**
   * test correctness of addition
   */
  @Test
  public void deriveFromPlusExpression() throws IOException {
    // example with two ints
    String s = "3+4";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("int",tc.typeOf(astex).print());

    // example with double and int
    s = "4.9+12";
    astex = p.parse_StringExpression(s).get();
    assertEquals("double",tc.typeOf(astex).print());

    // example with String
    s = "3 + \"Hallo\"";
    astex = p.parse_StringExpression(s).get();
    assertEquals("String",tc.typeOf(astex).print());
  }

  /**
   * test correctness of subtraction
   */
  @Test
  public void deriveFromMinusExpression() throws IOException{
    // example with two ints
    String s = "7-2";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("int",tc.typeOf(astex).print());

    //example with float and long
    s = "7.9f-3L";
    astex = p.parse_StringExpression(s).get();
    assertEquals("float",tc.typeOf(astex).print());
  }

  /**
   * test correctness of multiplication
   */
  @Test
  public void deriveFromMultExpression() throws IOException{
    //example with two ints
    String s = "2*19";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("int",tc.typeOf(astex).print());

    //example with long and char
    s = "\'a\'*3L";
    astex = p.parse_StringExpression(s).get();
    assertEquals("long",tc.typeOf(astex).print());
  }

  /**
   * test correctness of division
   */
  @Test
  public void deriveFromDivideExpression() throws IOException{
    //example with two ints
    String s = "7/12";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("int", tc.typeOf(astex).print());

    //example with float and double
    s = "5.4f/3.9";
    astex = p.parse_StringExpression(s).get();
    assertEquals("double",tc.typeOf(astex).print());
  }

  /**
   * tests correctness of modulo
   */
  @Test
  public void deriveFromModuloExpression() throws IOException{
    //example with two ints
    String s = "3%1";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("int",tc.typeOf(astex).print());

    //example with long and double
    s = "0.8%3L";
    astex = p.parse_StringExpression(s).get();
    assertEquals("double",tc.typeOf(astex).print());
  }

  /**
   * test LessEqualExpression
   */
  @Test
  public void deriveFromLessEqualExpression() throws IOException{
    //example with two ints
    String s = "4<=9";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("boolean",tc.typeOf(astex).print());

    //example with two other numeric types
    s = "2.4f<=3L";
    astex = p.parse_StringExpression(s).get();
    assertEquals("boolean",tc.typeOf(astex).print());
  }

  /**
   * test GreaterEqualExpression
   */
  @Test
  public void deriveFromGreaterEqualExpression() throws IOException{
    //example with two ints
    String s = "7>=2";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("boolean",tc.typeOf(astex).print());

    //example with two other numeric types
    s = "2.5>=\'d\'";
    astex = p.parse_StringExpression(s).get();
    assertEquals("boolean",tc.typeOf(astex).print());
  }

  /**
   * test LessThanExpression
   */
  @Test
  public void deriveFromLessThanExpression() throws IOException{
    //example with two ints
    String s = "4<9";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("boolean",tc.typeOf(astex).print());

    //example with two other numeric types
    s = "2.4f<3L";
    astex = p.parse_StringExpression(s).get();
    assertEquals("boolean",tc.typeOf(astex).print());
  }

  /**
   * test GreaterThanExpression
   */
  @Test
  public void deriveFromGreaterThanExpression() throws IOException{
    //example with two ints
    String s = "7>2";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("boolean",tc.typeOf(astex).print());

    //example with two other numeric types
    s = "2.5>\'d\'";
    astex = p.parse_StringExpression(s).get();
    assertEquals("boolean",tc.typeOf(astex).print());
  }

  /**
   * test EqualsExpression
   */
  @Test
  public void deriveFromEqualsExpression() throws IOException{
    //example with two primitives
    String s = "7==9.5f";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("boolean",tc.typeOf(astex).print());

    //example with two objects of the same class
    s = "student1==student2";
    astex = p.parse_StringExpression(s).get();
    assertEquals("boolean",tc.typeOf(astex).print());

    //example with two objects in sub-supertype relation
    s = "student1==person1";
    astex = p.parse_StringExpression(s).get();
    assertEquals("boolean",tc.typeOf(astex).print());
  }

  /**
   * test NotEqualsExpression
   */
  @Test
  public void deriveFromNotEqualsExpression() throws IOException{
    //example with two primitives
    String s = "true!=false";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("boolean",tc.typeOf(astex).print());

    //example with two objects of the same class
    s = "person1!=person2";
    astex = p.parse_StringExpression(s).get();
    assertEquals("boolean",tc.typeOf(astex).print());

    //example with two objects in sub-supertype relation
    s = "student2!=person2";
    astex = p.parse_StringExpression(s).get();
    assertEquals("boolean",tc.typeOf(astex).print());
  }

  /**
   * test BooleanAndOpExpression
   */
  @Test
  public void deriveFromBooleanAndOpExpression() throws IOException{
    //only possible with two booleans
    String s = "true&&true";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("boolean",tc.typeOf(astex).print());

    s = "(3<=4&&5>6)";
    astex = p.parse_StringExpression(s).get();
    assertEquals("boolean", tc.typeOf(astex).print());
  }

  /**
   * test BooleanOrOpExpression
   */
  @Test
  public void deriveFromBooleanOrOpExpression() throws IOException{
    //only possible with two booleans
    String s = "true||false";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("boolean",tc.typeOf(astex).print());

    s = "(3<=4.5f||5.3>6)";
    astex = p.parse_StringExpression(s).get();
    assertEquals("boolean", tc.typeOf(astex).print());
  }

  /**
   * test LogicalNotExpression
   */
  @Test
  public void deriveFromLogicalNotExpression() throws IOException{
    //only possible with boolean as inner expression
    String s = "!true";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("boolean",tc.typeOf(astex).print());

    s = "!(2.5>=0.3)";
    astex = p.parse_StringExpression(s).get();
    assertEquals("boolean", tc.typeOf(astex).print());
  }

  /**
   * test BracketExpression
   */
  @Test
  public void deriveFromBracketExpression() throws IOException{
    //test with only a literal in the inner expression
    String s = "(3)";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("int",tc.typeOf(astex).print());

    //test with a more complex inner expression
    s = "(3+4*(18-7.5))";
    astex = p.parse_StringExpression(s).get();
    assertEquals("double",tc.typeOf(astex).print());

    //test without primitive types in inner expression
    s = "(person1)";
    astex = p.parse_StringExpression(s).get();
    assertEquals("Person",tc.typeOf(astex).print());
  }

  /**
   * test ConditionalExpression
   */
  @Test
  public void deriveFromConditionalExpression() throws IOException{
    //test with two ints as true and false expression
    String s = "3<4?9:10";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("int",tc.typeOf(astex).print());

    //test with float and long
    s = "3>4?4.5f:10L";
    astex = p.parse_StringExpression(s).get();
    assertEquals("float",tc.typeOf(astex).print());

    //test without primitive types as true and false expression
    s = "3<9?person1:person2";
    astex = p.parse_StringExpression(s).get();
    assertEquals("Person",tc.typeOf(astex).print());

    //test with two objects in a sub-supertype relation
    s = "3<9?student1:person2";
    astex = p.parse_StringExpression(s).get();
    assertEquals("Person",tc.typeOf(astex).print());
  }

  /**
   * test BooleanNotExpression
   */
  @Test
  public void deriveFromBooleanNotExpression() throws IOException{
    //test with a int
    String s = "~3";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("int",tc.typeOf(astex).print());
    //test with a char
    s = "~\'a\'";
    astex = p.parse_StringExpression(s).get();
    assertEquals("int",tc.typeOf(astex).print());
  }

  /**
   * test FieldAccessExpression
   */
  @Test
  public void deriveFromFieldAccessExpression() throws IOException{
    //test for type with only one package
    String s = "types.Test";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("Test",tc.typeOf(astex).print());

    //test for variable of a type with one package
    s = "types.Test.variable";
    astex = p.parse_StringExpression(s).get();
    assertEquals("int",tc.typeOf(astex).print());

    //test for type with more than one package
    s = "types2.types3.types2.Test";
    astex = p.parse_StringExpression(s).get();
    assertEquals("Test",tc.typeOf(astex).print());

    //test for variable of type with more than one package
    s = "types2.types3.types2.Test.variable";
    astex = p.parse_StringExpression(s).get();
    assertEquals("int",tc.typeOf(astex).print());
  }

  /**
   * test CallExpression
   */
  @Test
  public void deriveFromCallExpression() throws IOException{
    //test for method with unqualified name without parameters
    String s = "isInt()";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("boolean",tc.typeOf(astex).print());

    //test for method with unqualified name with parameters
    s = "isInt(4)";
    astex = p.parse_StringExpression(s).get();
    assertEquals("boolean",tc.typeOf(astex).print());

    //test for method with qualified name without parameters
    s = "types.Test.store()";
    astex = p.parse_StringExpression(s).get();
    assertEquals("double",tc.typeOf(astex).print());

    //test for method with qualified name with parameters
    s = "types.Test.pay(4)";
    astex = p.parse_StringExpression(s).get();
    assertEquals("void",tc.typeOf(astex).print());

    //test for String method
    s = "\"test\".hashCode()";
    astex = p.parse_StringExpression(s).get();
    assertEquals("int",tc.typeOf(astex).print());

    //test for multiple CallExpressions in a row
    s = "\"test\".toString().charAt(1)";
    astex = p.parse_StringExpression(s).get();
    assertEquals("char",tc.typeOf(astex).print());
  }
}
