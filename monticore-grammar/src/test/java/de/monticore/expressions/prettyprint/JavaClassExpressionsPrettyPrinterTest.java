/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.prettyprint;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.javaclassexpressions.JavaClassExpressionsMill;
import de.monticore.expressions.javaclassexpressions._ast.*;
import de.monticore.expressions.testjavaclassexpressions._ast.ASTExtType;
import de.monticore.expressions.testjavaclassexpressions._parser.TestJavaClassExpressionsParser;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.*;

public class JavaClassExpressionsPrettyPrinterTest {

  private TestJavaClassExpressionsParser parser = new TestJavaClassExpressionsParser();

  private JavaClassExpressionsPrettyPrinter prettyPrinter= new JavaClassExpressionsPrettyPrinter(new IndentPrinter());

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
  public void testPrimaryThisExpression() throws IOException {
    Optional<ASTPrimaryThisExpression> result = parser.parse_StringPrimaryThisExpression("this");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTPrimaryThisExpression ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringPrimaryThisExpression(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testPrimarySuperExpression() throws IOException {
    Optional<ASTPrimarySuperExpression> result = parser.parse_StringPrimarySuperExpression("super");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTPrimarySuperExpression ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringPrimarySuperExpression(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testTypeCaseExpression() throws IOException {
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
  public void testClassExpression() throws IOException {
    Optional<ASTClassExpression> result = parser.parse_StringClassExpression("Integer.class");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTClassExpression ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    // does not print 'Integer' because functionality for type printing has to be added over delegation form
    // prettyprinter of langauge that fills the external
    assertEquals(".class", output);
  }

  @Test
  public void testPrimaryGenericInvocationExpressionExpression() throws IOException {
    Optional<ASTPrimaryGenericInvocationExpression> result = parser.parse_StringPrimaryGenericInvocationExpression("<Integer> super(a)");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTPrimaryGenericInvocationExpression ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    // does not print 'Integer' because functionality for type printing has to be added over delegation form
    // prettyprinter of langauge that fills the external
    assertEquals("<> super(a)", output);
  }

  @Test
  public void testInstanceofExpression() throws IOException {
    Optional<ASTExpression> a = parser.parse_StringExpression("a");
    Optional<ASTExtType> type = parser.parse_StringExtType("Integer");
    assertFalse(parser.hasErrors());
    assertTrue(a.isPresent());
    assertTrue(type.isPresent());
    ASTInstanceofExpression result = JavaClassExpressionsMill.instanceofExpressionBuilder()
        .setExpression(a.get())
        .setExtType(type.get())
        .build();
    String output = prettyPrinter.prettyprint(result);

    // does not print 'Integer' because functionality for type printing has to be added over delegation form
    // prettyprinter of langauge that fills the external
    assertEquals("a instanceof ", output);
  }

  @Test
  public void testThisExpression() throws IOException {
    Optional<ASTExpression> a = parser.parse_StringExpression("a");
    assertFalse(parser.hasErrors());
    assertTrue(a.isPresent());
    ASTThisExpression result = JavaClassExpressionsMill.thisExpressionBuilder()
        .setExpression(a.get())
        .build();

    String output = prettyPrinter.prettyprint(result);

    assertEquals("a.this", output);
  }

  @Test
  public void testArrayExpression() throws IOException {
    Optional<ASTExpression> a = parser.parse_StringExpression("a");
    Optional<ASTExpression> b = parser.parse_StringExpression("b");
    assertFalse(parser.hasErrors());
    assertTrue(a.isPresent());
    assertTrue(b.isPresent());
    ASTArrayExpression result = JavaClassExpressionsMill.arrayExpressionBuilder()
        .setExpression(a.get())
        .setIndexExpression(b.get())
        .build();

    String output = prettyPrinter.prettyprint(result);

    assertEquals("a[b]", output);
  }

  @Test
  public void testSuperExpression() throws IOException {
    Optional<ASTExpression> a = parser.parse_StringExpression("a");
    Optional<ASTSuperSuffix> b = parser.parse_StringSuperSuffix("(b)");
    assertFalse(parser.hasErrors());
    assertTrue(a.isPresent());
    assertTrue(b.isPresent());
    ASTSuperExpression result = JavaClassExpressionsMill.superExpressionBuilder()
        .setExpression(a.get())
        .setSuperSuffix(b.get())
        .build();

    String output = prettyPrinter.prettyprint(result);

    assertEquals("a.super(b)", output);
  }
  @Test
  public void testGenericInvocationExpressionExpression() throws IOException {
    Optional<ASTExpression> a = parser.parse_StringExpression("a");
    Optional<ASTPrimaryGenericInvocationExpression> b = parser.parse_StringPrimaryGenericInvocationExpression("<D> c(b)");
    assertFalse(parser.hasErrors());
    assertTrue(a.isPresent());
    assertTrue(b.isPresent());
    ASTGenericInvocationExpression result = JavaClassExpressionsMill.genericInvocationExpressionBuilder()
        .setExpression(a.get())
        .setPrimaryGenericInvocationExpression(b.get())
        .build();

    String output = prettyPrinter.prettyprint(result);

    // does not print 'd' because functionality for type printing has to be added over delegation form
    // prettyprinter of langauge that fills the external
    assertEquals("a.<> c(b)", output);
  }

  @Test
  public void testGenericInvocationSuffixThis() throws IOException {
    Optional<ASTGenericInvocationSuffix> result = parser.parse_StringGenericInvocationSuffix("this(a)");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTGenericInvocationSuffix ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringGenericInvocationSuffix(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testGenericInvocationSuffixSuper() throws IOException {
    Optional<ASTGenericInvocationSuffix> result = parser.parse_StringGenericInvocationSuffix("super(b)");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTGenericInvocationSuffix ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringGenericInvocationSuffix(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testGenericInvocationSuffixSimple() throws IOException {
    Optional<ASTGenericInvocationSuffix> result = parser.parse_StringGenericInvocationSuffix("a(b)");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTGenericInvocationSuffix ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringGenericInvocationSuffix(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testCreatorExpression() throws IOException {
    Optional<ASTCreatorExpression> result = parser.parse_StringCreatorExpression("new Integer(a,b)");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTCreatorExpression ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    // does not print 'Integer' because functionality for type printing has to be added over delegation from
    // prettyprinter of language that fills the external
    assertEquals(" new (a,b)", output);
  }

  @Test
  public void testCreatorExpression2() throws IOException {
    Optional<ASTCreatorExpression> result = parser.parse_StringCreatorExpression("new double[a][]");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTCreatorExpression ast = result.get();
    String output = prettyPrinter.prettyprint(ast);
    // does not print 'double' because functionality for type printing has to be added over delegation from
    // prettyprinter of language that fills the external
    assertEquals(" new [a][]", output);
  }
}
