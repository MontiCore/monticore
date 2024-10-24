/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import com.google.common.collect.Lists;
import de.monticore.expressions.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import de.monticore.expressions.combineexpressionswithliterals._parser.CombineExpressionsWithLiteralsParser;
import de.monticore.expressions.combineexpressionswithliterals._symboltable.ICombineExpressionsWithLiteralsScope;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisTraverser;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static de.monticore.types.check.DefsTypeBasic.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DeriveSymTypeOfBitExpressionsTest extends DeriveSymTypeAbstractTest {

  /**
   * Focus: Deriving Type of Literals, here:
   *    literals/MCLiteralsBasis.mc4
   */

  @BeforeEach
  public void init() {
    CombineExpressionsWithLiteralsMill.reset();
    CombineExpressionsWithLiteralsMill.init();
    BasicSymbolsMill.initializePrimitives();
    // Setting up a Scope Infrastructure (without a global Scope)
    ICombineExpressionsWithLiteralsScope scope = CombineExpressionsWithLiteralsMill.scope();
    scope.setEnclosingScope(null);       // No enclosing Scope: Search ending here
    scope.setExportingSymbols(true);
    scope.setAstNode(null); // hopefully unused
    // we add a variety of TypeSymbols to the same scope (which in reality doesn't happen)

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

  /**
   * test LeftShiftExpression
   */
  @Test
  public void deriveFromLeftShiftExpressionTest() throws IOException {
    //example with int - int
    check("3<<5", "int");

    //example with char - long
    check("\'a\'<<4L", "int");
  }

  @Test
  public void testInvalidLeftShiftExpression() throws IOException{
    //only possible with integral types
    checkError("3<<4.5", "0xA0201");
  }

  /**
   * test rightShiftExpression
   */
  @Test
  public void deriveFromRightShiftExpression() throws IOException {
    //example with int - int
    check("3>>5", "int");

    //example with long - long
    check("6L>>4L", "long");
  }

  @Test
  public void testInvalidRightShiftExpression() throws IOException{
    //only possible with integral types
    checkError("3>>4.5", "0xA0201");
  }

  /**
   * test LogicalRightExpression
   */
  @Test
  public void deriveFromLogicalRightExpression() throws IOException {
    //example with int - int
    check("3>>>5", "int");

    //example with int - long
    check("12>>>4L", "int");
  }

  @Test
  public void testInvalidLogicalRightExpression() throws IOException{
    //only possible with integral types
    checkError("3>>>4.5", "0xA0201");
  }

  /**
   * test BinaryOrOpExpression
   */
  @Test
  public void deriveFromBinaryOrOpExpression() throws IOException {
    //example with int - int
    check("3|5", "int");

    //example with char - long
    check("\'a\'|4L", "long");
  }

  @Test
  public void testInvalidBinaryOrOpExpression() throws IOException{
    //only possible with integral types
    checkError("3|4.5", "0xA0203");
  }

  /**
   * test BinaryAndExpression
   */
  @Test
  public void deriveFromBinaryAndExpression() throws IOException {
    //example with int - int
    check("3&5", "int");

    //example with long - long
    check("4L&12L", "long");
  }

  @Test
  public void testInvalidBinaryAndExpression() throws IOException{
    //only possible with integral types
    checkError("3&4.5", "0xA0203");
  }

  /**
   * test BinaryXorExpression
   */
  @Test
  public void deriveFromBinaryXorExpression() throws IOException {
    //example with int - int
    check("3^5", "int");

    //example with boolean - boolean
    check("true^false", "boolean");
  }

  @Test
  public void testInvalidBinaryXorExpression() throws IOException{
    //only possible with integral types
    checkError("3^4.5", "0xA0203");
  }
}
