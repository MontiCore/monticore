package de.monticore.types2;

import com.google.common.collect.Lists;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._symboltable.ExpressionsBasisScope;
import de.monticore.expressions.expressionsbasis._symboltable.ExpressionsBasisSymTabMill;
import de.monticore.types.typesymbols._symboltable.TypeSymbol;
import de.monticore.typescalculator.CombineExpressionsWithLiteralsTypesCalculator;
import de.monticore.typescalculator.TypesCalculator;
import de.monticore.typescalculator.combineexpressionswithliterals._parser.CombineExpressionsWithLiteralsParser;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static de.monticore.types2.DefsTypeBasic.*;
import static de.monticore.types2.DefsTypeBasic.field;
import static org.junit.Assert.assertEquals;

public class DeriveSymTypeOfAssignmentExpressionTest {

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
    add2scope(scope, field("vardouble", _doubleSymType));
    add2scope(scope, field("varchar", _charSymType));
    add2scope(scope, field("varfloat", _floatSymType));
    add2scope(scope, field("varlong", _longSymType));
    add2scope(scope, field("varint", _intSymType));
    add2scope(scope, field("varString",SymTypeExpressionFactory.createTypeObject("String",_String)));
    add2scope(scope, field("person1",SymTypeExpressionFactory.createTypeObject("Person",p)));
    add2scope(scope, field("person2",SymTypeExpressionFactory.createTypeObject("Person",p)));
    add2scope(scope, field("student1",SymTypeExpressionFactory.createTypeObject("Student",s)));
    add2scope(scope,field("student2",SymTypeExpressionFactory.createTypeObject("Student",s)));
    add2scope(scope,field("firstsemester",SymTypeExpressionFactory.createTypeObject("FirstSemesterStudent",f)));
    derLit.setScope(scope);
    TypesCalculator.setExpressionAndLiteralsTypeCalculator(derLit);
  }

  // Parer used for convenience:
  // (may be any other Parser that understands CommonExpressions)
  CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();

  // This is the core Visitor under Test (but rather empty)
  DeriveSymTypeOfExpression derEx = new DeriveSymTypeOfExpression();

  // This is an auxiliary
  CombineExpressionsWithLiteralsTypesCalculator derLit = new CombineExpressionsWithLiteralsTypesCalculator(ExpressionsBasisSymTabMill.expressionsBasisScopeBuilder().build());

  // other arguments not used (and therefore deliberately null)

  // This is the TypeChecker under Test:
  TypeCheck tc = new TypeCheck(null,derLit);

  /*--------------------------------------------------- TESTS ---------------------------------------------------------*/

  /**
   * test IncSuffixExpression
   */
  @Test
  public void deriveFromIncSuffixExpression() throws IOException{
    //example with int
    String s = "3++";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("int",tc.typeOf(astex).print());

    //example with float
    s = "4.5f++";
    astex = p.parse_StringExpression(s).get();
    assertEquals("float",tc.typeOf(astex).print());

    //example with char
    s = "\'e\'++";
    astex = p.parse_StringExpression(s).get();
    assertEquals("int",tc.typeOf(astex).print());
  }

  /**
   * test DecSuffixExpression
   */
  @Test
  public void deriveFromDecSuffixExpression() throws IOException{
    //example with int
    String s = "12--";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("int",tc.typeOf(astex).print());

    //example with double
    s = "4.2--";
    astex = p.parse_StringExpression(s).get();
    assertEquals("double",tc.typeOf(astex).print());
  }

  /**
   * test IncPrefixExpression
   */
  @Test
  public void deriveFromIncPrefixExpression() throws IOException{
    //example with int
    String s = "++3";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("int",tc.typeOf(astex).print());

    //example with long
    s = "++6L";
    astex = p.parse_StringExpression(s).get();
    assertEquals("long",tc.typeOf(astex).print());
  }

  /**
   * test DecPrefixExpression
   */
  @Test
  public void deriveFromDecPrefixExpression() throws IOException{
    //example with int
    String s = "--1";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("int",tc.typeOf(astex).print());

    //example with float
    s = "--6.7f";
    astex = p.parse_StringExpression(s).get();
    assertEquals("float",tc.typeOf(astex).print());
  }

  /**
   * test MinusPrefixExpression
   */
  @Test
  public void deriveFromMinusPrefixExpression() throws IOException{
    //example with int
    String s = "-5";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("int",tc.typeOf(astex).print());

    //example with double
    s = "-15.7";
    astex = p.parse_StringExpression(s).get();
    assertEquals("double",tc.typeOf(astex).print());
  }

  /**
   * test PlusPrefixExpression
   */
  @Test
  public void deriveFromPlusPrefixExpression() throws IOException{
    //example with int
    String s = "+34";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("int",tc.typeOf(astex).print());

    //example with long
    s = "+4L";
    astex = p.parse_StringExpression(s).get();
    assertEquals("long",tc.typeOf(astex).print());
  }

  /**
   * test PlusAssignmentExpression
   */
  @Test
  public void deriveFromPlusAssignmentExpression() throws IOException{
    //example with int - int
    String s = "foo+=7";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("int",tc.typeOf(astex).print());
    //example with long - double
    s = "varlong+=5.6";
    astex = p.parse_StringExpression(s).get();
    assertEquals("long",tc.typeOf(astex).print());
    //example with String - Person
    s = "varString+=person1";
    astex = p.parse_StringExpression(s).get();
    assertEquals("String",tc.typeOf(astex).print());
  }

  /**
   * test MinusAssignmentExpression
   */
  @Test
  public void deriveFromMinusAssignmentExpression() throws IOException{
    //example with int - int
    String s = "varint-=9";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("int",tc.typeOf(astex).print());
    //example with char - float
    s = "varchar-=4.5f";
    astex = p.parse_StringExpression(s).get();
    assertEquals("char",tc.typeOf(astex).print());
  }

  /**
   * test MultAssignmentExpression
   */
  @Test
  public void deriveFromMultAssignmentExpression() throws IOException{
    //example with int - int
    String s = "varint*=9";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("int",tc.typeOf(astex).print());
    //example with double - int
    s = "vardouble*=5";
    astex = p.parse_StringExpression(s).get();
    assertEquals("double",tc.typeOf(astex).print());
  }

  /**
   * test DivideAssignmentExpression
   */
  @Test
  public void deriveFromDivideAssignmentExpression() throws IOException{
    //example with int - int
    String s = "varint/=9";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("int",tc.typeOf(astex).print());
    //example with float - long
    s = "varfloat/=4L";
    astex = p.parse_StringExpression(s).get();
    assertEquals("float",tc.typeOf(astex).print());
  }

  /**
   * test ModuloAssignmentExpression
   */
  @Test
  public void deriveFromModuloAssignmentExpression() throws IOException{
    //example with int - int
    String s = "varint%=9";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("int",tc.typeOf(astex).print());
    //example with int - float
    s = "foo%=9.8f";
    astex = p.parse_StringExpression(s).get();
    assertEquals("int",tc.typeOf(astex).print());
  }

  /**
   * test AndAssignmentExpression
   */
  @Test
  public void deriveFromAndAssignmentExpression() throws IOException{
    //example with int - int
    String s = "varint&=9";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("int",tc.typeOf(astex).print());
    //example with boolean - boolean
    s = "bar2&=false";
    astex = p.parse_StringExpression(s).get();
    assertEquals("boolean",tc.typeOf(astex).print());
    //example with char - int
    s = "varchar&=4";
    astex = p.parse_StringExpression(s).get();
    assertEquals("char",tc.typeOf(astex).print());
  }

  /**
   * test OrAssignmentExpression
   */
  @Test
  public void deriveFromOrAssignmentExpression() throws IOException{
    //example with int - int
    String s = "varint|=9";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("int",tc.typeOf(astex).print());
    //example with boolean - boolean
    s = "bar2|=true";
    astex = p.parse_StringExpression(s).get();
    assertEquals("boolean",tc.typeOf(astex).print());
  }

  /**
   * test BinaryXorAssignmentExpression
   */
  @Test
  public void deriveFromBinaryXorAssignmentExpression() throws IOException{
    //example with int - int
    String s = "varint^=9";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("int",tc.typeOf(astex).print());
    //example with boolean - boolean
    s = "bar2^=false";
    astex = p.parse_StringExpression(s).get();
    assertEquals("boolean",tc.typeOf(astex).print());
  }

  /**
   * test DoubleLeftAssignmentExpression
   */
  @Test
  public void deriveFromDoubleLeftAssignmentExpression() throws IOException{
    //example with int - int
    String s = "varint<<=9";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("int",tc.typeOf(astex).print());
    //example with int - char
    s = "foo<<=\'c\'";
    astex = p.parse_StringExpression(s).get();
    assertEquals("int",tc.typeOf(astex).print());
  }

  /**
   * test DoubleRightAssignmentExpression
   */
  @Test
  public void deriveFromDoubleRightAssignmentExpression() throws IOException{
    //example with int - int
    String s = "varint>>=9";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("int",tc.typeOf(astex).print());
    //example with char - int
    s = "varchar>>=12";
    astex = p.parse_StringExpression(s).get();
    assertEquals("char",tc.typeOf(astex).print());
  }

  /**
   * test LogicalRightAssignmentExpression
   */
  @Test
  public void deriveFromLogicalRightAssignmentExpression() throws IOException{
    //example with int - int
    String s = "varint>>>=9";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("int",tc.typeOf(astex).print());
    //example with char - char
    s = "varchar>>>=\'3\'";
    astex = p.parse_StringExpression(s).get();
    assertEquals("char",tc.typeOf(astex).print());
  }

  /**
   * test RegularAssignmentExpression
   */
  @Test
  public void deriveFromRegularAssignmentExpression() throws IOException{
    //example with int - int
    String s = "varint=9";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("int",tc.typeOf(astex).print());
    //example with double - int
    s = "vardouble=12";
    astex = p.parse_StringExpression(s).get();
    assertEquals("double",tc.typeOf(astex).print());
    //example with person - student
    s = "person1 = student2";
    astex = p.parse_StringExpression(s).get();
    assertEquals("Person",tc.typeOf(astex).print());
    //example with person - firstsemesterstudent
    s = "person2 = firstsemester";
    astex = p.parse_StringExpression(s).get();
    assertEquals("Person",tc.typeOf(astex).print());
  }
}
