package de.monticore.types.check;

import com.google.common.collect.Lists;
import de.monticore.expressions.combineexpressionswithliterals._parser.CombineExpressionsWithLiteralsParser;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._symboltable.ExpressionsBasisScope;
import de.monticore.expressions.expressionsbasis._symboltable.ExpressionsBasisSymTabMill;
import de.monticore.expressions.prettyprint.CombineExpressionsWithLiteralsPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.typesymbols._symboltable.TypeSymbol;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static de.monticore.types.check.DefsTypeBasic.*;
import static org.junit.Assert.assertEquals;

public class DeriveSymTypeOfAssignmentExpressionTest {

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
  DeriveSymTypeOfCombineExpressionsDelegator derLit = new DeriveSymTypeOfCombineExpressionsDelegator(ExpressionsBasisSymTabMill.expressionsBasisScopeBuilder().build(), new CombineExpressionsWithLiteralsPrettyPrinter(new IndentPrinter()));

  // other arguments not used (and therefore deliberately null)

  // This is the TypeChecker under Test:
  TypeCheck tc = new TypeCheck(null, derLit);

  /*--------------------------------------------------- TESTS ---------------------------------------------------------*/

  /**
   * test IncSuffixExpression
   */
  @Test
  public void deriveFromIncSuffixExpression() throws IOException {
    //example with int
    String s = "3++";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("int", tc.typeOf(astex).print());

    //example with float
    s = "4.5f++";
    astex = p.parse_StringExpression(s).get();
    assertEquals("float", tc.typeOf(astex).print());

    //example with char
    s = "\'e\'++";
    astex = p.parse_StringExpression(s).get();
    assertEquals("int", tc.typeOf(astex).print());
  }

  @Test(expected = RuntimeException.class)
  public void testInvalidIncSuffixExpression() throws IOException {
    //only possible with numeric types
    String s = "\"Hello\"++";
    ASTExpression astex = p.parse_StringExpression(s).get();
    tc.typeOf(astex);
  }

  /**
   * test DecSuffixExpression
   */
  @Test
  public void deriveFromDecSuffixExpression() throws IOException {
    //example with int
    String s = "12--";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("int", tc.typeOf(astex).print());

    //example with double
    s = "4.2--";
    astex = p.parse_StringExpression(s).get();
    assertEquals("double", tc.typeOf(astex).print());
  }

  @Test(expected = RuntimeException.class)
  public void testInvalidDecSuffixExpression() throws IOException {
    //only possible with numeric types
    String s = "\"Hello\"--";
    ASTExpression astex = p.parse_StringExpression(s).get();
    tc.typeOf(astex);
  }

  /**
   * test IncPrefixExpression
   */
  @Test
  public void deriveFromIncPrefixExpression() throws IOException {
    //example with int
    String s = "++3";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("int", tc.typeOf(astex).print());

    //example with long
    s = "++6L";
    astex = p.parse_StringExpression(s).get();
    assertEquals("long", tc.typeOf(astex).print());
  }

  @Test(expected = RuntimeException.class)
  public void testInvalidIncPrefixExpression() throws IOException {
    //only possible with numeric types
    String s = "++\"Hello\"";
    ASTExpression astex = p.parse_StringExpression(s).get();
    tc.typeOf(astex);
  }

  /**
   * test DecPrefixExpression
   */
  @Test
  public void deriveFromDecPrefixExpression() throws IOException {
    //example with int
    String s = "--1";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("int", tc.typeOf(astex).print());

    //example with float
    s = "--6.7f";
    astex = p.parse_StringExpression(s).get();
    assertEquals("float", tc.typeOf(astex).print());
  }

  @Test (expected = RuntimeException.class)
  public void testInvalidDecPrefixExpression() throws IOException {
    //only possible with numeric types
    String s = "--\"Hello\"";
    ASTExpression astex = p.parse_StringExpression(s).get();
    tc.typeOf(astex);
  }

  /**
   * test MinusPrefixExpression
   */
  @Test
  public void deriveFromMinusPrefixExpression() throws IOException {
    //example with int
    String s = "-5";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("int", tc.typeOf(astex).print());

    //example with double
    s = "-15.7";
    astex = p.parse_StringExpression(s).get();
    assertEquals("double", tc.typeOf(astex).print());
  }

  @Test (expected = RuntimeException.class)
  public void testInvalidMinusPrefixExpression() throws IOException {
    //only possible with numeric types
    String s = "-\"Hello\"";
    ASTExpression astex = p.parse_StringExpression(s).get();
    tc.typeOf(astex);
  }

  /**
   * test PlusPrefixExpression
   */
  @Test
  public void deriveFromPlusPrefixExpression() throws IOException {
    //example with int
    String s = "+34";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("int", tc.typeOf(astex).print());

    //example with long
    s = "+4L";
    astex = p.parse_StringExpression(s).get();
    assertEquals("long", tc.typeOf(astex).print());
  }


  @Test(expected = RuntimeException.class)
  public void testInvalidPlusPrefixExpression() throws IOException {
    //only possible with numeric types
    String s = "+\"Hello\"";
    ASTExpression astex = p.parse_StringExpression(s).get();
    tc.typeOf(astex);
  }

  /**
   * test PlusAssignmentExpression
   */
  @Test
  public void deriveFromPlusAssignmentExpression() throws IOException {
    //example with int - int
    String s = "foo+=7";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("int", tc.typeOf(astex).print());
    //example with long - double
    s = "varlong+=5.6";
    astex = p.parse_StringExpression(s).get();
    assertEquals("long", tc.typeOf(astex).print());
    //example with String - Person
    s = "varString+=person1";
    astex = p.parse_StringExpression(s).get();
    assertEquals("String", tc.typeOf(astex).print());
  }

  @Test (expected = RuntimeException.class)
  public void testInvalidPlusAssignmentExpression() throws IOException {
    //not possible because int = int + (int) String returns a casting error
    String s = "varint+=\"Hello\"";
    ASTExpression astex = p.parse_StringExpression(s).get();
    tc.typeOf(astex);
  }

  /**
   * test MinusAssignmentExpression
   */
  @Test
  public void deriveFromMinusAssignmentExpression() throws IOException {
    //example with int - int
    String s = "varint-=9";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("int", tc.typeOf(astex).print());
    //example with char - float
    s = "varchar-=4.5f";
    astex = p.parse_StringExpression(s).get();
    assertEquals("char", tc.typeOf(astex).print());
  }

  @Test(expected = RuntimeException.class)
  public void testInvalidMinusAssignmentExpression() throws IOException {
    //not possible because int = int - (int) String returns a casting error
    String s = "varint-=\"Hello\"";
    ASTExpression astex = p.parse_StringExpression(s).get();
    tc.typeOf(astex);
  }

  /**
   * test MultAssignmentExpression
   */
  @Test
  public void deriveFromMultAssignmentExpression() throws IOException {
    //example with int - int
    String s = "varint*=9";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("int", tc.typeOf(astex).print());
    //example with double - int
    s = "vardouble*=5";
    astex = p.parse_StringExpression(s).get();
    assertEquals("double", tc.typeOf(astex).print());
  }

  @Test(expected = RuntimeException.class)
  public void testInvalidMultAssignmentExpression() throws IOException {
    //not possible because int = int * (int) String returns a casting error
    String s = "varint*=\"Hello\"";
    ASTExpression astex = p.parse_StringExpression(s).get();
    tc.typeOf(astex);
  }

  /**
   * test DivideAssignmentExpression
   */
  @Test
  public void deriveFromDivideAssignmentExpression() throws IOException {
    //example with int - int
    String s = "varint/=9";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("int", tc.typeOf(astex).print());
    //example with float - long
    s = "varfloat/=4L";
    astex = p.parse_StringExpression(s).get();
    assertEquals("float", tc.typeOf(astex).print());
  }

  @Test(expected = RuntimeException.class)
  public void testInvalidDivideAssignmentExpression() throws IOException {
    //not possible because int = int / (int) String returns a casting error
    String s = "varint/=\"Hello\"";
    ASTExpression astex = p.parse_StringExpression(s).get();
    tc.typeOf(astex);
  }

  /**
   * test ModuloAssignmentExpression
   */
  @Test
  public void deriveFromModuloAssignmentExpression() throws IOException {
    //example with int - int
    String s = "varint%=9";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("int", tc.typeOf(astex).print());
    //example with int - float
    s = "foo%=9.8f";
    astex = p.parse_StringExpression(s).get();
    assertEquals("int", tc.typeOf(astex).print());
  }

  @Test(expected = RuntimeException.class)
  public void testInvalidModuloAssignmentExpression() throws IOException {
    //not possible because int = int % (int) String returns a casting error
    String s = "varint%=\"Hello\"";
    ASTExpression astex = p.parse_StringExpression(s).get();
    tc.typeOf(astex);
  }

  /**
   * test AndAssignmentExpression
   */
  @Test
  public void deriveFromAndAssignmentExpression() throws IOException {
    //example with int - int
    String s = "varint&=9";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("int", tc.typeOf(astex).print());
    //example with boolean - boolean
    s = "bar2&=false";
    astex = p.parse_StringExpression(s).get();
    assertEquals("boolean", tc.typeOf(astex).print());
    //example with char - int
    s = "varchar&=4";
    astex = p.parse_StringExpression(s).get();
    assertEquals("char", tc.typeOf(astex).print());
  }

  @Test(expected = RuntimeException.class)
  public void testInvalidAndAssignmentExpression() throws IOException {
    //not possible because int = int & (int) String returns a casting error
    String s = "varint&=\"Hello\"";
    ASTExpression astex = p.parse_StringExpression(s).get();
    tc.typeOf(astex);
  }

  /**
   * test OrAssignmentExpression
   */
  @Test
  public void deriveFromOrAssignmentExpression() throws IOException {
    //example with int - int
    String s = "varint|=9";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("int", tc.typeOf(astex).print());
    //example with boolean - boolean
    s = "bar2|=true";
    astex = p.parse_StringExpression(s).get();
    assertEquals("boolean", tc.typeOf(astex).print());
  }

  @Test(expected = RuntimeException.class)
  public void testInvalidOrAssignmentExpression() throws IOException {
    //not possible because int = int | (int) String returns a casting error
    String s = "varint|=\"Hello\"";
    ASTExpression astex = p.parse_StringExpression(s).get();
    tc.typeOf(astex);
  }

  /**
   * test BinaryXorAssignmentExpression
   */
  @Test
  public void deriveFromBinaryXorAssignmentExpression() throws IOException {
    //example with int - int
    String s = "varint^=9";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("int", tc.typeOf(astex).print());
    //example with boolean - boolean
    s = "bar2^=false";
    astex = p.parse_StringExpression(s).get();
    assertEquals("boolean", tc.typeOf(astex).print());

    s = "true^=false";
    astex = p.parse_StringExpression(s).get();
    assertEquals("boolean", tc.typeOf(astex).print());
  }

  @Test(expected = RuntimeException.class)
  public void testInvalidBinaryXorAssignmentExpression() throws IOException {
    //not possible because int = int ^ (int) String returns a casting error
    String s = "varint^=\"Hello\"";
    ASTExpression astex = p.parse_StringExpression(s).get();
    tc.typeOf(astex);
  }

  /**
   * test DoubleLeftAssignmentExpression
   */
  @Test
  public void deriveFromDoubleLeftAssignmentExpression() throws IOException {
    //example with int - int
    String s = "varint<<=9";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("int", tc.typeOf(astex).print());
    //example with int - char
    s = "foo<<=\'c\'";
    astex = p.parse_StringExpression(s).get();
    assertEquals("int", tc.typeOf(astex).print());
  }

  @Test(expected = RuntimeException.class)
  public void testInvalidDoubleLeftAssignmentExpression() throws IOException {
    //not possible because int = int << (int) String returns a casting error
    String s = "varint<<=\"Hello\"";
    ASTExpression astex = p.parse_StringExpression(s).get();
    tc.typeOf(astex);
  }

  /**
   * test DoubleRightAssignmentExpression
   */
  @Test
  public void deriveFromDoubleRightAssignmentExpression() throws IOException {
    //example with int - int
    String s = "varint>>=9";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("int", tc.typeOf(astex).print());
    //example with char - int
    s = "varchar>>=12";
    astex = p.parse_StringExpression(s).get();
    assertEquals("char", tc.typeOf(astex).print());
  }

  @Test(expected = RuntimeException.class)
  public void testInvalidDoubleRightAssignmentExpression() throws IOException {
    //not possible because int = int >> (int) String returns a casting error
    String s = "varint>>=\"Hello\"";
    ASTExpression astex = p.parse_StringExpression(s).get();
    tc.typeOf(astex);
  }

  /**
   * test LogicalRightAssignmentExpression
   */
  @Test
  public void deriveFromLogicalRightAssignmentExpression() throws IOException {
    //example with int - int
    String s = "varint>>>=9";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("int", tc.typeOf(astex).print());
    //example with char - char
    s = "varchar>>>=\'3\'";
    astex = p.parse_StringExpression(s).get();
    assertEquals("char", tc.typeOf(astex).print());
  }

  @Test(expected = RuntimeException.class)
  public void testInvalidLogicalRightAssignmentExpression() throws IOException {
    //not possible because int = int >>> (int) String returns a casting error
    String s = "varint>>>=\"Hello\"";
    ASTExpression astex = p.parse_StringExpression(s).get();
    tc.typeOf(astex);
  }

  /**
   * test RegularAssignmentExpression
   */
  @Test
  public void deriveFromRegularAssignmentExpression() throws IOException {
    //example with int - int
    String s = "varint=9";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("int", tc.typeOf(astex).print());
    //example with double - int
    s = "vardouble=12";
    astex = p.parse_StringExpression(s).get();
    assertEquals("double", tc.typeOf(astex).print());
    //example with person - student
    s = "person1 = student2";
    astex = p.parse_StringExpression(s).get();
    assertEquals("Person", tc.typeOf(astex).print());
    //example with person - firstsemesterstudent
    s = "person2 = firstsemester";
    astex = p.parse_StringExpression(s).get();
    assertEquals("Person", tc.typeOf(astex).print());
  }

  @Test(expected = RuntimeException.class)
  public void testInvalidRegularAssignmentExpression() throws IOException {
    //not possible because int = (int) String returns a casting error
    String s = "varint=\"Hello\"";
    ASTExpression astex = p.parse_StringExpression(s).get();
    tc.typeOf(astex);
  }
}
