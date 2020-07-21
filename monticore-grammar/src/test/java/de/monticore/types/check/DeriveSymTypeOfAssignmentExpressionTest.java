/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import com.google.common.collect.Lists;
import de.monticore.expressions.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import de.monticore.expressions.combineexpressionswithliterals._parser.CombineExpressionsWithLiteralsParser;
import de.monticore.expressions.combineexpressionswithliterals._symboltable.ICombineExpressionsWithLiteralsScope;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.se_rwth.commons.logging.*;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static de.monticore.types.check.DefsTypeBasic.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DeriveSymTypeOfAssignmentExpressionTest {

  private ICombineExpressionsWithLiteralsScope scope;
  private FlatExpressionScopeSetter flatExpressionScopeSetter;

  /**
   * Focus: Deriving Type of Literals, here:
   * literals/MCLiteralsBasis.mc4
   */

  @BeforeClass
  public static void setup() {
    LogStub.init();         // replace log by a sideffect free variant
    // LogStub.initPlusLog();  // for manual testing purpose only
    Log.enableFailQuick(false);
  }

  @Before
  public void setupForEach() {
    // Setting up a Scope Infrastructure (without a global Scope)
    DefsTypeBasic.setup();
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
    scope.add(p);
    OOTypeSymbol s = new OOTypeSymbol("Student");
    scope.add(s);
    s.setSuperTypesList(Lists.newArrayList(SymTypeExpressionFactory.createTypeObject("Person", scope)));
    OOTypeSymbol f = new OOTypeSymbol("FirstSemesterStudent");
    scope.add(f);
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
    flatExpressionScopeSetter = new FlatExpressionScopeSetter(scope);
    LogStub.init();
  }

  // Parer used for convenience:
  // (may be any other Parser that understands CommonExpressions)
  CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();

  // This is an auxiliary
  DeriveSymTypeOfCombineExpressionsDelegator derLit = new DeriveSymTypeOfCombineExpressionsDelegator();

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
    astex.accept(flatExpressionScopeSetter);
    assertEquals("int", tc.typeOf(astex).print());

    //example with float
    s = "4.5f++";
    astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    assertEquals("float", tc.typeOf(astex).print());

    //example with char
    s = "\'e\'++";
    astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    assertEquals("int", tc.typeOf(astex).print());
  }

  @Test
  public void testInvalidIncSuffixExpression() throws IOException {
    //only possible with numeric types
    String s = "\"Hello\"++";
    ASTExpression astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    try{
      tc.typeOf(astex);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0170"));
    }
  }

  /**
   * test DecSuffixExpression
   */
  @Test
  public void deriveFromDecSuffixExpression() throws IOException {
    //example with int
    String s = "12--";
    ASTExpression astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    assertEquals("int", tc.typeOf(astex).print());

    //example with double
    s = "4.2--";
    astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    assertEquals("double", tc.typeOf(astex).print());
  }

  @Test
  public void testInvalidDecSuffixExpression() throws IOException {
    //only possible with numeric types
    String s = "\"Hello\"--";
    ASTExpression astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    try{
      tc.typeOf(astex);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0171"));
    }
  }

  /**
   * test IncPrefixExpression
   */
  @Test
  public void deriveFromIncPrefixExpression() throws IOException {
    //example with int
    String s = "++3";
    ASTExpression astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    assertEquals("int", tc.typeOf(astex).print());

    //example with long
    s = "++6L";
    astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    assertEquals("long", tc.typeOf(astex).print());
  }

  @Test
  public void testInvalidIncPrefixExpression() throws IOException {
    //only possible with numeric types
    String s = "++\"Hello\"";
    ASTExpression astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    try{
      tc.typeOf(astex);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0172"));
    }
  }

  /**
   * test DecPrefixExpression
   */
  @Test
  public void deriveFromDecPrefixExpression() throws IOException {
    //example with int
    String s = "--1";
    ASTExpression astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    assertEquals("int", tc.typeOf(astex).print());

    //example with float
    s = "--6.7f";
    astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    assertEquals("float", tc.typeOf(astex).print());
  }

  @Test
  public void testInvalidDecPrefixExpression() throws IOException {
    //only possible with numeric types
    String s = "--\"Hello\"";
    ASTExpression astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    try{
      tc.typeOf(astex);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0173"));
    }
  }

  /**
   * test MinusPrefixExpression
   */
  @Test
  public void deriveFromMinusPrefixExpression() throws IOException {
    //example with int
    String s = "-5";
    ASTExpression astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    assertEquals("int", tc.typeOf(astex).print());

    //example with double
    s = "-15.7";
    astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    assertEquals("double", tc.typeOf(astex).print());
  }

  @Test
  public void testInvalidMinusPrefixExpression() throws IOException {
    //only possible with numeric types
    String s = "-\"Hello\"";
    ASTExpression astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    try{
      tc.typeOf(astex);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0175"));
    }
  }

  /**
   * test PlusPrefixExpression
   */
  @Test
  public void deriveFromPlusPrefixExpression() throws IOException {
    //example with int
    String s = "+34";
    ASTExpression astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    assertEquals("int", tc.typeOf(astex).print());

    //example with long
    s = "+4L";
    astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    assertEquals("long", tc.typeOf(astex).print());
  }


  @Test
  public void testInvalidPlusPrefixExpression() throws IOException {
    //only possible with numeric types
    String s = "+\"Hello\"";
    ASTExpression astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    try{
      tc.typeOf(astex);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0174"));
    }
  }

  /**
   * test PlusAssignmentExpression
   */
  @Test
  public void deriveFromPlusAssignmentExpression() throws IOException {
    //example with int - int
    String s = "foo+=7";
    ASTExpression astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    assertEquals("int", tc.typeOf(astex).print());
    //example with long - double
    s = "varlong+=5.6";
    astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    assertEquals("long", tc.typeOf(astex).print());
    //example with String - Person
    s = "varString+=person1";
    astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    assertEquals("String", tc.typeOf(astex).print());
  }

  @Test
  public void testInvalidPlusAssignmentExpression() throws IOException {
    //not possible because int = int + (int) String returns a casting error
    String s = "varint+=\"Hello\"";
    ASTExpression astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    try{
      tc.typeOf(astex);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0176"));
    }
  }

  /**
   * test MinusAssignmentExpression
   */
  @Test
  public void deriveFromMinusAssignmentExpression() throws IOException {
    //example with int - int
    String s = "varint-=9";
    ASTExpression astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    assertEquals("int", tc.typeOf(astex).print());
    //example with char - float
    s = "varchar-=4.5f";
    astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    assertEquals("char", tc.typeOf(astex).print());
  }

  @Test
  public void testInvalidMinusAssignmentExpression() throws IOException {
    //not possible because int = int - (int) String returns a casting error
    String s = "varint-=\"Hello\"";
    ASTExpression astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    try{
      tc.typeOf(astex);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0177"));
    }
  }

  /**
   * test MultAssignmentExpression
   */
  @Test
  public void deriveFromMultAssignmentExpression() throws IOException {
    //example with int - int
    String s = "varint*=9";
    ASTExpression astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    assertEquals("int", tc.typeOf(astex).print());
    //example with double - int
    s = "vardouble*=5";
    astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    assertEquals("double", tc.typeOf(astex).print());
  }

  @Test
  public void testInvalidMultAssignmentExpression() throws IOException {
    //not possible because int = int * (int) String returns a casting error
    String s = "varint*=\"Hello\"";
    ASTExpression astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    try{
      tc.typeOf(astex);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0178"));
    }
  }

  /**
   * test DivideAssignmentExpression
   */
  @Test
  public void deriveFromDivideAssignmentExpression() throws IOException {
    //example with int - int
    String s = "varint/=9";
    ASTExpression astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    assertEquals("int", tc.typeOf(astex).print());
    //example with float - long
    s = "varfloat/=4L";
    astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    assertEquals("float", tc.typeOf(astex).print());
  }

  @Test
  public void testInvalidDivideAssignmentExpression() throws IOException {
    //not possible because int = int / (int) String returns a casting error
    String s = "varint/=\"Hello\"";
    ASTExpression astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    try{
      tc.typeOf(astex);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0179"));
    }
  }

  /**
   * test ModuloAssignmentExpression
   */
  @Test
  public void deriveFromModuloAssignmentExpression() throws IOException {
    //example with int - int
    String s = "varint%=9";
    ASTExpression astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    assertEquals("int", tc.typeOf(astex).print());
    //example with int - float
    s = "foo%=9.8f";
    astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    assertEquals("int", tc.typeOf(astex).print());
  }

  @Test
  public void testInvalidModuloAssignmentExpression() throws IOException {
    //not possible because int = int % (int) String returns a casting error
    String s = "varint%=\"Hello\"";
    ASTExpression astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    try{
      tc.typeOf(astex);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0189"));
    }
  }

  /**
   * test AndAssignmentExpression
   */
  @Test
  public void deriveFromAndAssignmentExpression() throws IOException {
    //example with int - int
    String s = "varint&=9";
    ASTExpression astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    assertEquals("int", tc.typeOf(astex).print());
    //example with boolean - boolean
    s = "bar2&=false";
    astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    assertEquals("boolean", tc.typeOf(astex).print());
    //example with char - int
    s = "varchar&=4";
    astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    assertEquals("char", tc.typeOf(astex).print());
  }

  @Test
  public void testInvalidAndAssignmentExpression() throws IOException {
    //not possible because int = int & (int) String returns a casting error
    String s = "varint&=\"Hello\"";
    ASTExpression astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    try{
      tc.typeOf(astex);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0183"));
    }
  }

  /**
   * test OrAssignmentExpression
   */
  @Test
  public void deriveFromOrAssignmentExpression() throws IOException {
    //example with int - int
    String s = "varint|=9";
    ASTExpression astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    assertEquals("int", tc.typeOf(astex).print());
    //example with boolean - boolean
    s = "bar2|=true";
    astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    assertEquals("boolean", tc.typeOf(astex).print());
  }

  @Test
  public void testInvalidOrAssignmentExpression() throws IOException {
    //not possible because int = int | (int) String returns a casting error
    String s = "varint|=\"Hello\"";
    ASTExpression astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    try{
      tc.typeOf(astex);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0184"));
    }
  }

  /**
   * test BinaryXorAssignmentExpression
   */
  @Test
  public void deriveFromBinaryXorAssignmentExpression() throws IOException {
    //example with int - int
    String s = "varint^=9";
    ASTExpression astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    assertEquals("int", tc.typeOf(astex).print());
    //example with boolean - boolean
    s = "bar2^=false";
    astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    assertEquals("boolean", tc.typeOf(astex).print());

    s = "true^=false";
    astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    assertEquals("boolean", tc.typeOf(astex).print());
  }

  @Test
  public void testInvalidBinaryXorAssignmentExpression() throws IOException {
    //not possible because int = int ^ (int) String returns a casting error
    String s = "varint^=\"Hello\"";
    ASTExpression astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    try{
      tc.typeOf(astex);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0185"));
    }
  }

  /**
   * test DoubleLeftAssignmentExpression
   */
  @Test
  public void deriveFromDoubleLeftAssignmentExpression() throws IOException {
    //example with int - int
    String s = "varint<<=9";
    ASTExpression astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    assertEquals("int", tc.typeOf(astex).print());
    //example with int - char
    s = "foo<<=\'c\'";
    astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    assertEquals("int", tc.typeOf(astex).print());
  }

  @Test
  public void testInvalidDoubleLeftAssignmentExpression() throws IOException {
    //not possible because int = int << (int) String returns a casting error
    String s = "varint<<=\"Hello\"";
    ASTExpression astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    try{
      tc.typeOf(astex);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0187"));
    }
  }

  /**
   * test DoubleRightAssignmentExpression
   */
  @Test
  public void deriveFromDoubleRightAssignmentExpression() throws IOException {
    //example with int - int
    String s = "varint>>=9";
    ASTExpression astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    assertEquals("int", tc.typeOf(astex).print());
    //example with char - int
    s = "varchar>>=12";
    astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    assertEquals("char", tc.typeOf(astex).print());
  }

  @Test
  public void testInvalidDoubleRightAssignmentExpression() throws IOException {
    //not possible because int = int >> (int) String returns a casting error
    String s = "varint>>=\"Hello\"";
    ASTExpression astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    try{
      tc.typeOf(astex);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0186"));
    }
  }

  /**
   * test LogicalRightAssignmentExpression
   */
  @Test
  public void deriveFromLogicalRightAssignmentExpression() throws IOException {
    //example with int - int
    String s = "varint>>>=9";
    ASTExpression astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    assertEquals("int", tc.typeOf(astex).print());
    //example with char - char
    s = "varchar>>>=\'3\'";
    astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    assertEquals("char", tc.typeOf(astex).print());
  }

  @Test
  public void testInvalidLogicalRightAssignmentExpression() throws IOException {
    //not possible because int = int >>> (int) String returns a casting error
    String s = "varint>>>=\"Hello\"";
    ASTExpression astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    try{
      tc.typeOf(astex);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0188"));
    }
  }

  /**
   * test RegularAssignmentExpression
   */
  @Test
  public void deriveFromRegularAssignmentExpression() throws IOException {
    //example with int - int
    String s = "varint=9";
    ASTExpression astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    assertEquals("int", tc.typeOf(astex).print());
    //example with double - int
    s = "vardouble=12";
    astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    assertEquals("double", tc.typeOf(astex).print());
    //example with person - student
    s = "person1 = student2";
    astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    assertEquals("Person", tc.typeOf(astex).print());
    //example with person - firstsemesterstudent
    s = "person2 = firstsemester";
    astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    assertEquals("Person", tc.typeOf(astex).print());
  }

  @Test
  public void testInvalidRegularAssignmentExpression() throws IOException {
    //not possible because int = (int) String returns a casting error
    String s = "varint=\"Hello\"";
    ASTExpression astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    try{
      tc.typeOf(astex);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0182"));
    }
  }

  @Test
  public void testInvalidRegularAssignmentExpression2() throws IOException{
    //test with no field on the left side of the assignment
    String s = "3=4";
    ASTExpression astex = p.parse_StringExpression(s).get();
    astex.accept(flatExpressionScopeSetter);
    try{
      tc.typeOf(astex);
    }catch(RuntimeException e){
      assertTrue(Log.getFindings().get(0).getMsg().startsWith("0xA0180"));
    }
  }
}
