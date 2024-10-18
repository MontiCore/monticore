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

import static de.monticore.types.check.DefsTypeBasic._booleanSymType;
import static de.monticore.types.check.DefsTypeBasic._byteSymType;
import static de.monticore.types.check.DefsTypeBasic._charSymType;
import static de.monticore.types.check.DefsTypeBasic._doubleSymType;
import static de.monticore.types.check.DefsTypeBasic._floatSymType;
import static de.monticore.types.check.DefsTypeBasic._intSymType;
import static de.monticore.types.check.DefsTypeBasic._longSymType;
import static de.monticore.types.check.DefsTypeBasic._shortSymType;
import static de.monticore.types.check.DefsTypeBasic.add2scope;
import static de.monticore.types.check.DefsTypeBasic.field;

public class DeriveSymTypeOfAssignmentExpressionTest extends DeriveSymTypeAbstractTest {

  // Parser used for convenience:
  // (may be any other Parser that understands CommonExpressions)
  CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();

  @Override
  protected void setupTypeCheck() {
    // This is an auxiliary
    FullDeriveFromCombineExpressionsWithLiterals derLit = new FullDeriveFromCombineExpressionsWithLiterals();

    // other arguments not used (and therefore deliberately null)
    // This is the TypeChecker under Test:
    setTypeCheck(new TypeCalculator(null, derLit));
  }

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

  @BeforeEach
  public void init() {
    CombineExpressionsWithLiteralsMill.reset();
    CombineExpressionsWithLiteralsMill.init();
    BasicSymbolsMill.initializePrimitives();
    // Setting up a Scope Infrastructure (without a global Scope)
    DefsTypeBasic.setup();
    ICombineExpressionsWithLiteralsScope scope = CombineExpressionsWithLiteralsMill.scope();
    scope.setEnclosingScope(null);       // No enclosing Scope: Search ending here
    scope.setExportingSymbols(true);
    scope.setAstNode(null);
    // we add a variety of TypeSymbols to the same scope (which in reality doesn't happen)

    add2scope(scope, DefsTypeBasic._array);
    add2scope(scope, DefsTypeBasic._Object);
    add2scope(scope, DefsTypeBasic._String);

    // some FieldSymbols (ie. Variables, Attributes)
    OOTypeSymbol p = new OOTypeSymbol("Person");
    add2scope(scope, p);
    OOTypeSymbol s = new OOTypeSymbol("Student");
    add2scope(scope, s);
    s.setSuperTypesList(Lists.newArrayList(SymTypeExpressionFactory.createTypeObject("Person", scope)));
    OOTypeSymbol f = new OOTypeSymbol("FirstSemesterStudent");
    add2scope(scope, f);
    f.setSuperTypesList(Lists.newArrayList(SymTypeExpressionFactory.createTypeObject("Student", scope)));
    add2scope(scope, field("varboolean", _booleanSymType));
    add2scope(scope, field("varbyte", _byteSymType));
    add2scope(scope, field("varchar", _charSymType));
    add2scope(scope, field("varshort", _shortSymType));
    add2scope(scope, field("varint", _intSymType));
    add2scope(scope, field("varlong", _longSymType));
    add2scope(scope, field("varfloat", _floatSymType));
    add2scope(scope, field("vardouble", _doubleSymType));
    add2scope(scope, field("varString", SymTypeExpressionFactory.createTypeObject("String", scope)));
    add2scope(scope, field("person1", SymTypeExpressionFactory.createTypeObject("Person", scope)));
    add2scope(scope, field("person2", SymTypeExpressionFactory.createTypeObject("Person", scope)));
    add2scope(scope, field("student1", SymTypeExpressionFactory.createTypeObject("Student", scope)));
    add2scope(scope, field("student2", SymTypeExpressionFactory.createTypeObject("Student", scope)));
    add2scope(scope, field("firstsemester", SymTypeExpressionFactory.createTypeObject("FirstSemesterStudent", scope)));
    setFlatExpressionScopeSetter(scope);
  }

  /*--------------------------------------------------- TESTS ---------------------------------------------------------*/

  /**
   * test IncSuffixExpression
   */
  @Test
  public void deriveFromIncSuffixExpression() throws IOException {

    //example with byte
    check("varbyte++", "byte");

    //example with short
    check("varshort++", "short");

    //example with char
    check("varchar++", "char");

    //example with int
    check("varint++", "int");

    //example with float
    check("varfloat++", "float");

    //example with char
    check("varchar++", "char");
  }

  @Test
  public void testInvalidIncSuffixExpression() throws IOException {
    //only possible wit variables
    checkError("1++", "0xA0183");

    //not applicable to Strings
    checkError("varString++", "0xA0184");
  }

  /**
   * test DecSuffixExpression
   */
  @Test
  public void deriveFromDecSuffixExpression() throws IOException {
    //example with byte
    check("varbyte--", "byte");

    //example with short
    check("varshort--", "short");

    //example with char
    check("varchar--", "char");

    //example with int
    check("varint--", "int");

    //example with double
    check("vardouble--", "double");
  }

  @Test
  public void testInvalidDecSuffixExpression() throws IOException {
    //only possible wit variables
    checkError("1--", "0xA0183");

    //not applicable to Strings
    checkError("varString--", "0xA0184");
  }

  /**
   * test IncPrefixExpression
   */
  @Test
  public void deriveFromIncPrefixExpression() throws IOException {
    //example with byte
    check("++varbyte", "byte");

    //example with short
    check("++varshort", "short");

    //example with char
    check("++varchar", "char");

    //example with int
    check("++varint", "int");

    //example with long
    check("++varlong", "long");
  }

  @Test
  public void testInvalidIncPrefixExpression() throws IOException {
    //only possible wit variables
    checkError("++1", "0xA0183");

    //not applicable to Strings
    checkError("++varString", "0xA0184");
  }

  /**
   * test DecPrefixExpression
   */
  @Test
  public void deriveFromDecPrefixExpression() throws IOException {
    //example with byte
    check("--varbyte", "byte");

    //example with short
    check("--varshort", "short");

    //example with char
    check("--varchar", "char");

    //example with int
    check("--varint", "int");

    //example with float
    check("--varfloat", "float");
  }

  @Test
  public void testInvalidDecPrefixExpression() throws IOException {
    //only possible wit variables
    checkError("--1", "0xA0183");

    //not applicable to Strings
    checkError("--varString", "0xA0184");
  }

  /**
   * test MinusPrefixExpression
   */
  @Test
  public void deriveFromMinusPrefixExpression() throws IOException {
    //example with int
    check("-5", "int");

    //example with double
    check("-15.7", "double");
  }

  @Test
  public void testInvalidMinusPrefixExpression() throws IOException {
    //only possible with numeric types
    checkError("-\"Hello\"", "0xA0175");
  }

  /**
   * test PlusPrefixExpression
   */
  @Test
  public void deriveFromPlusPrefixExpression() throws IOException {
    //example with int
    check("+34", "int");

    //example with long
    check("+4L", "long");
  }


  @Test
  public void testInvalidPlusPrefixExpression() throws IOException {
    //only possible with numeric types
    checkError("+\"Hello\"", "0xA0175");
  }

  /**
   * test PlusAssignmentExpression
   */
  @Test
  public void deriveFromPlusAssignmentExpression() throws IOException {
    //example with int - int
    check("varint+=7", "int");
    //example with long - double
    check("varlong+=5.6", "long");
    //example with String - Person
    check("varString+=person1", "String");
  }

  @Test
  public void testInvalidPlusAssignmentExpression() throws IOException {
    //not possible because int = int + (int) String returns a casting error
    checkError("varint+=\"Hello\"", "0xA0178");
  }

  /**
   * test MinusAssignmentExpression
   */
  @Test
  public void deriveFromMinusAssignmentExpression() throws IOException {
    //example with int - int
    check("varint-=9", "int");
    //example with char - float
    check("varchar-=4.5f", "char");
  }

  @Test
  public void testInvalidMinusAssignmentExpression() throws IOException {
    //not possible because int = int - (int) String returns a casting error
    checkError("varint-=\"Hello\"", "0xA0178");
  }

  /**
   * test MultAssignmentExpression
   */
  @Test
  public void deriveFromMultAssignmentExpression() throws IOException {
    //example with int - int
    check("varint*=9", "int");
    //example with double - int
    check("vardouble*=5", "double");
  }

  @Test
  public void testInvalidMultAssignmentExpression() throws IOException {
    //not possible because int = int * (int) String returns a casting error
    checkError("varint*=\"Hello\"", "0xA0178");
  }

  /**
   * test DivideAssignmentExpression
   */
  @Test
  public void deriveFromDivideAssignmentExpression() throws IOException {
    //example with int - int
    check("varint/=9", "int");
    //example with float - long
    check("varfloat/=4L", "float");
  }

  @Test
  public void testInvalidDivideAssignmentExpression() throws IOException {
    //not possible because int = int / (int) String returns a casting error
    checkError("varint/=\"Hello\"", "0xA0178");
  }

  /**
   * test ModuloAssignmentExpression
   */
  @Test
  public void deriveFromModuloAssignmentExpression() throws IOException {
    //example with int - int
    check("varint%=9", "int");
    //example with int - float
    check("varint%=9.8f", "int");
  }

  @Test
  public void testInvalidModuloAssignmentExpression() throws IOException {
    //not possible because int = int % (int) String returns a casting error
    checkError("varint%=\"Hello\"", "0xA0178");
  }

  /**
   * test AndAssignmentExpression
   */
  @Test
  public void deriveFromAndAssignmentExpression() throws IOException {
    //example with int - int
    check("varint&=9", "int");
    //example with boolean - boolean
    check("varboolean&=false", "boolean");
    //example with char - int
    check("varchar&=4", "char");
  }

  @Test
  public void testInvalidAndAssignmentExpression() throws IOException {
    //not possible because int = int & (int) String returns a casting error
    checkError("varint&=\"Hello\"", "0xA0176");
  }

  /**
   * test OrAssignmentExpression
   */
  @Test
  public void deriveFromOrAssignmentExpression() throws IOException {
    //example with int - int
    check("varint|=9", "int");
    //example with boolean - boolean
    check("varboolean|=true", "boolean");
  }

  @Test
  public void testInvalidOrAssignmentExpression() throws IOException {
    //not possible because int = int | (int) String returns a casting error
    checkError("varint|=\"Hello\"", "0xA0176");
  }

  /**
   * test BinaryXorAssignmentExpression
   */
  @Test
  public void deriveFromBinaryXorAssignmentExpression() throws IOException {
    //example with int - int
    check("varint^=9", "int");
    //example with boolean - boolean
    check("varboolean^=false", "boolean");
  }

  @Test
  public void testInvalidBinaryXorAssignmentExpression() throws IOException {
    //not possible because int = int ^ (int) String returns a casting error
    checkError("varint^=\"Hello\"", "0xA0176");
  }

  /**
   * test DoubleLeftAssignmentExpression
   */
  @Test
  public void deriveFromDoubleLeftAssignmentExpression() throws IOException {
    //example with int - int
    check("varint<<=9", "int");
    //example with int - char
    check("varint<<='c'", "int");
  }

  @Test
  public void testInvalidDoubleLeftAssignmentExpression() throws IOException {
    //not possible because int = int << (int) String returns a casting error
    checkError("varint<<=\"Hello\"", "0xA0177");
  }

  /**
   * test DoubleRightAssignmentExpression
   */
  @Test
  public void deriveFromDoubleRightAssignmentExpression() throws IOException {
    //example with int - int
    check("varint>>=9", "int");
    //example with char - int
    check("varchar>>=12", "char");
  }

  @Test
  public void testInvalidDoubleRightAssignmentExpression() throws IOException {
    //not possible because int = int >> (int) String returns a casting error
    checkError("varint>>=\"Hello\"", "0xA0177");
  }

  /**
   * test LogicalRightAssignmentExpression
   */
  @Test
  public void deriveFromLogicalRightAssignmentExpression() throws IOException {
    //example with int - int
    check("varint>>>=9", "int");
    //example with char - char
    check("varchar>>>='3'", "char");
  }

  @Test
  public void testInvalidLogicalRightAssignmentExpression() throws IOException {
    //not possible because int = int >>> (int) String returns a casting error
    checkError("varint>>>=\"Hello\"", "0xA0177");
  }

  /**
   * test RegularAssignmentExpression
   */
  @Test
  public void deriveFromRegularAssignmentExpression() throws IOException {
    //example with int - int
    check("varint=9", "int");
    //example with double - int
    check("vardouble=12", "double");
    //example with person - student
    check("person1 = student2", "Person");
    //example with person - firstsemesterstudent
    check("person2 = firstsemester", "Person");
  }

  @Test
  public void testInvalidRegularAssignmentExpression() throws IOException {
    //not possible because int = (int) String returns a casting error
    checkError("varint=\"Hello\"", "0xA0179");
  }

  @Test
  public void testInvalidRegularAssignmentExpression2() throws IOException {
    //test with no field on the left side of the assignment
    checkError("3=4", "0xA0181");
  }
}
