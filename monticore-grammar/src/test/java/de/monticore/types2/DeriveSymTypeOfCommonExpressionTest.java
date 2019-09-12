package de.monticore.types2;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._symboltable.ExpressionsBasisScope;
import de.monticore.expressions.expressionsbasis._symboltable.ExpressionsBasisSymTabMill;
import de.monticore.types.typesymbols._symboltable.TypeSymbol;
import de.monticore.typescalculator.CombineExpressionsWithLiteralsTypesCalculator;
import de.monticore.typescalculator.combineexpressionswithliterals._parser.CombineExpressionsWithLiteralsParser;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static de.monticore.types2.DefsTypeBasic.*;
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
    add2scope(scope, field("foo", _intSymType));
    add2scope(scope, field("bar", _booleanSymType));
    TypeSymbol p = new TypeSymbol("Person");
    add2scope(scope, field("person1",SymTypeExpressionFactory.createTypeObject("person1",p)));
    add2scope(scope, field("person2",SymTypeExpressionFactory.createTypeObject("person2",p)));
  }

  // Parer used for convenience:
  // (may be any other Parser that understands CommonExpressions)
  CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();


  // other arguments not used (and therefore deliberately null)

  // This is an auxiliary
  CombineExpressionsWithLiteralsTypesCalculator derLit = new CombineExpressionsWithLiteralsTypesCalculator(scope);

  // This is the TypeChecker under Test:
  TypeCheck tc = new TypeCheck(null,derLit);

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

  @Test
  public void deriveFromEqualsExpression() throws IOException{
    //example with two primitives
    String s = "7==9.5f";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("boolean",tc.typeOf(astex).print());

    //TODO: example with two objects of the same class
    //TODO: example with two objects in sub-supertype relation
  }

  @Test
  public void deriveFromNotEqualsExpression() throws IOException{
    //example with two primitives
    String s = "true!=false";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("boolean",tc.typeOf(astex).print());

    //TODO: example with two objects of the same class
    //TODO: example with two objects in sub-supertype relation
  }
}
