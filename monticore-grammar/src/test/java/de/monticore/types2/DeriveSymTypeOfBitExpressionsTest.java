package de.monticore.types2;

import com.google.common.collect.Lists;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._symboltable.ExpressionsBasisScope;
import de.monticore.expressions.expressionsbasis._symboltable.ExpressionsBasisSymTabMill;
import de.monticore.types.typesymbols._symboltable.TypeSymbol;
import de.monticore.typescalculator.combineexpressionswithliterals._parser.CombineExpressionsWithLiteralsParser;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static de.monticore.types2.DefsTypeBasic.*;
import static de.monticore.types2.DefsTypeBasic.field;
import static org.junit.Assert.assertEquals;

public class DeriveSymTypeOfBitExpressionsTest {

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

  @Test
  public void deriveFromLeftShiftExpressionTest() throws IOException {
    //example with int - int
    String s = "3<<5";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("int",tc.typeOf(astex).print());

    //example with char - long
    s = "\'a\'<<4L";
    astex = p.parse_StringExpression(s).get();
    assertEquals("int",tc.typeOf(astex).print());
  }

  @Test
  public void deriveFromRightShiftExpression() throws IOException {
    //example with int - int
    String s = "3>>5";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("int",tc.typeOf(astex).print());

    //example with long - long
    s = "6L>>4L";
    astex = p.parse_StringExpression(s).get();
    assertEquals("long",tc.typeOf(astex).print());
  }

  @Test
  public void deriveFromLogicalRightExpression() throws IOException {
    //example with int - int
    String s = "3>>>5";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("int",tc.typeOf(astex).print());

    //example with int - long
    s = "12>>>4L";
    astex = p.parse_StringExpression(s).get();
    assertEquals("int",tc.typeOf(astex).print());
  }

  @Test
  public void deriveFromBinaryOrOpExpression() throws IOException {
    //example with int - int
    String s = "3|5";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("int",tc.typeOf(astex).print());

    //example with char - long
    s = "\'a\'|4L";
    astex = p.parse_StringExpression(s).get();
    assertEquals("long",tc.typeOf(astex).print());
  }

  @Test
  public void deriveFromBinaryAndExpression() throws IOException {
    //example with int - int
    String s = "3&5";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("int",tc.typeOf(astex).print());

    //example with long - long
    s = "4L&12L";
    astex = p.parse_StringExpression(s).get();
    assertEquals("long",tc.typeOf(astex).print());
  }

  @Test
  public void deriveFromBinaryXorExpression() throws IOException {
    //example with int - int
    String s = "3^5";
    ASTExpression astex = p.parse_StringExpression(s).get();
    assertEquals("int",tc.typeOf(astex).print());

    //example with boolean - boolean
    s = "true^false";
    astex = p.parse_StringExpression(s).get();
    assertEquals("boolean",tc.typeOf(astex).print());
  }
}
