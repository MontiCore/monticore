/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.prettyprint;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.oclexpressions.OCLExpressionsMill;
import de.monticore.expressions.oclexpressions._ast.*;
import de.monticore.expressions.testoclexpressions._ast.ASTExtType;
import de.monticore.expressions.testoclexpressions._parser.TestOCLExpressionsParser;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.*;

public class OCLExpressionsPrettyPrinterTest {

  private TestOCLExpressionsParser parser = new TestOCLExpressionsParser();

  private OCLExpressionsPrettyPrinter prettyPrinter = new OCLExpressionsPrettyPrinter(new IndentPrinter());

  @BeforeClass
  public static void setUp() {
    LogStub.init();
    Log.enableFailQuick(false);
  }

  @Before
  public void init() {
    prettyPrinter.getPrinter().clearBuffer();
  }

  @Test
  public void testPrimaryThenExpression() throws IOException {
    Optional<ASTThenExpressionPart> result = parser.parse_StringThenExpressionPart(" then a");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTThenExpressionPart ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringThenExpressionPart(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testPrimaryElseExpression() throws IOException {
    Optional<ASTElseExpressionPart> result = parser.parse_StringElseExpressionPart(" else a");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTElseExpressionPart ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringElseExpressionPart(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testIfThenElseExpression() throws IOException {
    Optional<ASTIfThenElseExpr> result = parser.parse_StringIfThenElseExpr("if a then b else c");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTIfThenElseExpr ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringIfThenElseExpr(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testInstanceofExpression() throws IOException {
    Optional<ASTExpression> a = parser.parse_StringExpression("a");
    Optional<ASTExtType> type = parser.parse_StringExtType("Integer");
    assertFalse(parser.hasErrors());
    assertTrue(a.isPresent());
    assertTrue(type.isPresent());
    ASTInstanceOfExpression result = OCLExpressionsMill.instanceOfExpressionBuilder()
        .setLeft(a.get())
        .setExtType(type.get())
        .build();
    String output = prettyPrinter.prettyprint(result);

    // does not print 'Integer' because functionality for type printing has to be added over delegation form
    // prettyprinter of langauge that fills the external
    assertEquals("a instanceof ", output);
  }

  @Test
  public void testImpliesExpression() throws IOException {
    Optional<ASTExpression> a = parser.parse_StringExpression("a");
    Optional<ASTExpression> b = parser.parse_StringExpression("b");
    assertFalse(parser.hasErrors());
    assertTrue(a.isPresent());
    assertTrue(b.isPresent());
    ASTImpliesExpression result = OCLExpressionsMill.impliesExpressionBuilder()
        .setLeft(a.get())
        .setRight(b.get())
        .build();

    String output = prettyPrinter.prettyprint(result);

    assertEquals("a implies b", output);
  }

  @Test
  public void testSingleLogicalANDExpr() throws IOException {
    Optional<ASTExpression> a = parser.parse_StringExpression("a");
    Optional<ASTExpression> b = parser.parse_StringExpression("b");
    assertFalse(parser.hasErrors());
    assertTrue(a.isPresent());
    assertTrue(b.isPresent());
    ASTSingleLogicalANDExpr result = OCLExpressionsMill.singleLogicalANDExprBuilder()
        .setLeft(a.get())
        .setRight(b.get())
        .build();

    String output = prettyPrinter.prettyprint(result);

    assertEquals("a & b", output);
  }

  @Test
  public void testSingleLogicalORExpr() throws IOException {
    Optional<ASTExpression> a = parser.parse_StringExpression("a");
    Optional<ASTExpression> b = parser.parse_StringExpression("b");
    assertFalse(parser.hasErrors());
    assertTrue(a.isPresent());
    assertTrue(b.isPresent());
    ASTSingleLogicalORExpr result = OCLExpressionsMill.singleLogicalORExprBuilder()
        .setLeft(a.get())
        .setRight(b.get())
        .build();

    String output = prettyPrinter.prettyprint(result);

    assertEquals("a | b", output);
  }

  @Test
  public void testForallExpression() throws IOException {
    Optional<ASTForallExpr> result = parser.parse_StringForallExpr("forall a, b, c in foo : bla");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTForallExpr ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringForallExpr(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testExistsExpression() throws IOException {
    Optional<ASTExistsExpr> result = parser.parse_StringExistsExpr("exists a, b, c in foo : bla");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTExistsExpr ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringExistsExpr(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testAnyExpression() throws IOException {
    Optional<ASTAnyExpr> result = parser.parse_StringAnyExpr("any a");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTAnyExpr ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringAnyExpr(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testLetinExpr() throws IOException {
    Optional<ASTLetinExpr> result = parser.parse_StringLetinExpr("let a; c in foo");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTLetinExpr ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    // does not print 'a' and 'c' because functionality for type printing has to be added over delegation form
    // prettyprinter of langauge that fills the external
    assertEquals("let ; ; in foo", output);
  }

  @Test
  public void testIterateExpr() throws IOException {
    Optional<ASTIterateExpr> result = parser.parse_StringIterateExpr("iterate { a in b; c: foo = bla }");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTIterateExpr ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    // does not print 'c' because functionality for type printing has to be added over delegation form
    // prettyprinter of langauge that fills the external
    assertEquals("iterate { a in b;  : foo = bla }", output);
  }

  @Test
  public void testTypeCastExpression() throws IOException {
    Optional<ASTTypeCastExpression> result = parser.parse_StringTypeCastExpression("(Integer) a");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTTypeCastExpression ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    // does not print 'Integer' because functionality for type printing has to be added over delegation form
    // prettyprinter of langauge that fills the external
    assertEquals("()a", output);
  }

  @Test
  public void testParenthizedExpression() throws IOException {
    Optional<ASTParenthizedExpression> result = parser.parse_StringParenthizedExpression("(a&b)");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTParenthizedExpression ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringParenthizedExpression(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testInExpr() throws IOException {
    Optional<ASTInExpr> result = parser.parse_StringInExpr("a, b, c, d in a&b");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTInExpr ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringInExpr(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testOCLIsNewPrimary() throws IOException {
    Optional<ASTOCLIsNewPrimary> result = parser.parse_StringOCLIsNewPrimary("isnew(a&b)");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTOCLIsNewPrimary ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringOCLIsNewPrimary(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testOCLDefinedPrimary() throws IOException {
    Optional<ASTOCLDefinedPrimary> result = parser.parse_StringOCLDefinedPrimary("defined (c|d)");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTOCLDefinedPrimary ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringOCLDefinedPrimary(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testOCLQualifiedPrimary() throws IOException {
    Optional<ASTOCLQualifiedPrimary> result = parser.parse_StringOCLQualifiedPrimary("a.b.C(a).a@pre ");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTOCLQualifiedPrimary ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringOCLQualifiedPrimary(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testOCLTransitiveQualification() throws IOException {
    Optional<ASTOCLTransitiveQualification> result = parser.parse_StringOCLTransitiveQualification("**");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTOCLTransitiveQualification ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringOCLTransitiveQualification(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testOCLAtPreQualification() throws IOException {
    Optional<ASTOCLAtPreQualification> result = parser.parse_StringOCLAtPreQualification("@pre ");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTOCLAtPreQualification ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringOCLAtPreQualification(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testOCLArgumentQualification() throws IOException {
    Optional<ASTOCLArgumentQualification> result = parser.parse_StringOCLArgumentQualification("(a, b, c, foo)");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTOCLArgumentQualification ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringOCLArgumentQualification(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testOCLArrayQualification() throws IOException {
    Optional<ASTOCLArrayQualification> result = parser.parse_StringOCLArrayQualification("[a&b]");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTOCLArrayQualification ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringOCLArrayQualification(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testOCLComprehensionPrimary() throws IOException {
    Optional<ASTOCLComprehensionPrimary> result = parser.parse_StringOCLComprehensionPrimary("Integer {a}.isnew(c)");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTOCLComprehensionPrimary ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    // does not print 'Integer' because functionality for type printing has to be added over delegation form
    // prettyprinter of langauge that fills the external
    assertEquals("{a}.isnew(c)", output);
  }

  @Test
  public void testOCLComprehensionExpressionStyle() throws IOException {
    Optional<ASTOCLComprehensionExpressionStyle> result = parser.parse_StringOCLComprehensionExpressionStyle("a | a, b, c, d in a&b, d in e");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTOCLComprehensionExpressionStyle ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringOCLComprehensionExpressionStyle(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testOCLOCLCollectionItem() throws IOException {
    Optional<ASTOCLCollectionItem> result = parser.parse_StringOCLCollectionItem("ab .. ac&c");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTOCLCollectionItem ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringOCLCollectionItem(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testOCLOCLCollectionItem2() throws IOException {
    Optional<ASTOCLCollectionItem> result = parser.parse_StringOCLCollectionItem("ab, foo, bar");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTOCLCollectionItem ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringOCLCollectionItem(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testLeftShiftExpression() throws IOException {
    Optional<ASTExpression> a = parser.parse_StringExpression("a");
    Optional<ASTExpression> b = parser.parse_StringExpression("b");
    assertFalse(parser.hasErrors());
    assertTrue(a.isPresent());
    assertTrue(b.isPresent());
    ASTOCLEquivalentExpression result = OCLExpressionsMill.oCLEquivalentExpressionBuilder()
        .setLeft(a.get())
        .setOperator("<=>")
        .setRight(b.get())
        .build();

    String output = prettyPrinter.prettyprint(result);

    assertEquals("a<=>b", output);
  }
}
