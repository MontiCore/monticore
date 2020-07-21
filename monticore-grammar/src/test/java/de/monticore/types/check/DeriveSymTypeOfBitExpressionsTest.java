/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import com.google.common.collect.Lists;
import de.monticore.expressions.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import de.monticore.expressions.combineexpressionswithliterals._parser.CombineExpressionsWithLiteralsParser;
import de.monticore.expressions.combineexpressionswithliterals._symboltable.ICombineExpressionsWithLiteralsScope;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static de.monticore.types.check.DefsTypeBasic.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DeriveSymTypeOfBitExpressionsTest {

  private ICombineExpressionsWithLiteralsScope scope;
  private FlatExpressionScopeSetter flatExpressionScopeSetter;

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
        CombineExpressionsWithLiteralsMill.combineExpressionsWithLiteralsScopeBuilder()
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
    OOTypeSymbol p = new OOTypeSymbol("Person");
    OOTypeSymbol s = new OOTypeSymbol("Student");
    s.setSuperTypesList(Lists.newArrayList(SymTypeExpressionFactory.createTypeObject("Person", scope)));
    OOTypeSymbol f = new OOTypeSymbol("FirstSemesterStudent");
    f.setSuperTypesList(Lists.newArrayList(SymTypeExpressionFactory.createTypeObject("Student", scope)));
    add2scope(scope, field("foo", _intSymType));
    add2scope(scope, field("bar2", _booleanSymType));
    add2scope(scope, field("vardouble", _doubleSymType));
    add2scope(scope, field("varchar", _charSymType));
    add2scope(scope, field("varfloat", _floatSymType));
    add2scope(scope, field("varlong", _longSymType));
    add2scope(scope, field("varint", _intSymType));
    add2scope(scope, field("varString",SymTypeExpressionFactory.createTypeObject("String",scope)));
    add2scope(scope, field("person1",SymTypeExpressionFactory.createTypeObject("Person",scope)));
    add2scope(scope, field("person2",SymTypeExpressionFactory.createTypeObject("Person",scope)));
    add2scope(scope, field("student1",SymTypeExpressionFactory.createTypeObject("Student",scope)));
    add2scope(scope,field("student2",SymTypeExpressionFactory.createTypeObject("Student",scope)));
    add2scope(scope,field("firstsemester",SymTypeExpressionFactory.createTypeObject("FirstSemesterStudent",scope)));
    flatExpressionScopeSetter = new FlatExpressionScopeSetter(scope);
    LogStub.init();
  }

  // Parer used for convenience:
  // (may be any other Parser that understands CommonExpressions)
  CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();

  // This is the core Visitor under Test (but rather empty)
  DeriveSymTypeOfExpression derEx = new DeriveSymTypeOfExpression();

  // This is an auxiliary
  DeriveSymTypeOfCombineExpressionsDelegator derLit = new DeriveSymTypeOfCombineExpressionsDelegator();

  // other arguments not used (and therefore deliberately null)

  // This is the TypeChecker under Test:
  TypeCheck tc = new TypeCheck(null,derLit);

  /*--------------------------------------------------- TESTS ---------------------------------------------------------*/

  /**
   * test LeftShiftExpression
   */
  @Test
  public void deriveFromLeftShiftExpressionTest() throws IOException {
    //example with int - int
    String s = "3<<5";
    ASTExpression astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    assertEquals("int",tc.typeOf(astex).print());

    //example with char - long
    s = "\'a\'<<4L";
    astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    assertEquals("int",tc.typeOf(astex).print());
  }

  @Test
  public void testInvalidLeftShiftExpression() throws IOException{
    //only possible with integral types
    String s = "3<<4.5";
    ASTExpression astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    try{
      tc.typeOf(astex);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0200"));
    }
  }

  /**
   * test rightShiftExpression
   */
  @Test
  public void deriveFromRightShiftExpression() throws IOException {
    //example with int - int
    String s = "3>>5";
    ASTExpression astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    assertEquals("int",tc.typeOf(astex).print());

    //example with long - long
    s = "6L>>4L";
    astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    assertEquals("long",tc.typeOf(astex).print());
  }

  @Test
  public void testInvalidRightShiftExpression() throws IOException{
    //only possible with integral types
    String s = "3>>4.5";
    ASTExpression astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    try{
      tc.typeOf(astex);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0201"));
    }
  }

  /**
   * test LogicalRightExpression
   */
  @Test
  public void deriveFromLogicalRightExpression() throws IOException {
    //example with int - int
    String s = "3>>>5";
    ASTExpression astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    assertEquals("int",tc.typeOf(astex).print());

    //example with int - long
    s = "12>>>4L";
    astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    assertEquals("int",tc.typeOf(astex).print());
  }

  @Test
  public void testInvalidLogicalRightExpression() throws IOException{
    //only possible with integral types
    String s = "3>>>4.5";
    ASTExpression astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    try{
      tc.typeOf(astex);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0202"));
    }
  }

  /**
   * test BinaryOrOpExpression
   */
  @Test
  public void deriveFromBinaryOrOpExpression() throws IOException {
    //example with int - int
    String s = "3|5";
    ASTExpression astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    assertEquals("int",tc.typeOf(astex).print());

    //example with char - long
    s = "\'a\'|4L";
    astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    assertEquals("long",tc.typeOf(astex).print());
  }

  @Test
  public void testInvalidBinaryOrOpExpression() throws IOException{
    //only possible with integral types
    String s = "3|4.5";
    ASTExpression astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    try{
      tc.typeOf(astex);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0204"));
    }
  }

  /**
   * test BinaryAndExpression
   */
  @Test
  public void deriveFromBinaryAndExpression() throws IOException {
    //example with int - int
    String s = "3&5";
    ASTExpression astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    assertEquals("int",tc.typeOf(astex).print());

    //example with long - long
    s = "4L&12L";
    astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    assertEquals("long",tc.typeOf(astex).print());
  }

  @Test
  public void testInvalidBinaryAndExpression() throws IOException{
    //only possible with integral types
    String s = "3&4.5";
    ASTExpression astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    try{
      tc.typeOf(astex);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0203"));
    }
  }

  /**
   * test BinaryXorExpression
   */
  @Test
  public void deriveFromBinaryXorExpression() throws IOException {
    //example with int - int
    String s = "3^5";
    ASTExpression astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    assertEquals("int",tc.typeOf(astex).print());

    //example with boolean - boolean
    s = "true^false";
    astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    assertEquals("boolean",tc.typeOf(astex).print());
  }

  @Test
  public void testInvalidBinaryXorExpression() throws IOException{
    //only possible with integral types
    String s = "3^4.5";
    ASTExpression astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    try{
      tc.typeOf(astex);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0205"));
    }
  }
}
