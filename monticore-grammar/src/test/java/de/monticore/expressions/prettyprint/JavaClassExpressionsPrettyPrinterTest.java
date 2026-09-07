/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.prettyprint;

import de.monticore.expressions.commonexpressions._ast.ASTArrayAccessExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.javaclassexpressions.JavaClassExpressionsMill;
import de.monticore.expressions.javaclassexpressions._ast.*;
import de.monticore.expressions.testjavaclassexpressions.TestJavaClassExpressionsMill;
import de.monticore.expressions.testjavaclassexpressions._parser.TestJavaClassExpressionsParser;
import de.monticore.expressions.uglyexpressions._ast.ASTInstanceofExpression;
import de.monticore.expressions.uglyexpressions._ast.ASTTypeCastExpression;
import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestWithMCLanguage(JavaClassExpressionsMill.class)
public class JavaClassExpressionsPrettyPrinterTest {

  private TestJavaClassExpressionsParser parser;
  
  @BeforeEach
  public void init() {
    parser = TestJavaClassExpressionsMill.parser();
    BasicSymbolsMill.initializePrimitives();
  }


  @Test
  public void testPrimaryThisExpression() throws IOException {
    Optional<ASTPrimaryThisExpression> result = parser.parse_StringPrimaryThisExpression("this");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTPrimaryThisExpression ast = result.get();

    String output = TestJavaClassExpressionsMill.prettyPrint(ast, false);

    result = parser.parse_StringPrimaryThisExpression(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));

    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testPrimarySuperExpression() throws IOException {
    Optional<ASTPrimarySuperExpression> result = parser.parse_StringPrimarySuperExpression("super");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTPrimarySuperExpression ast = result.get();

    String output = TestJavaClassExpressionsMill.prettyPrint(ast, false);

    result = parser.parse_StringPrimarySuperExpression(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));

    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testTypeCaseExpression() throws IOException {
    Optional<ASTTypeCastExpression> result = parser.parse_StringTypeCastExpression("(Integer) a");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTTypeCastExpression ast = result.get();

    String output = TestJavaClassExpressionsMill.prettyPrint(ast, false);

    // does not print 'Integer' because functionality for type printing has to be added over delegation form
    // prettyprinter of langauge that fills the external
    String pattern = "^\\(.*\\)a$";
    boolean matches = output.matches(pattern);
    assertTrue(matches);
  }

  @Test
  public void testClassExpression() throws IOException {
    Optional<ASTClassExpression> result = parser.parse_StringClassExpression("Integer.class");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTClassExpression ast = result.get();

    String output = TestJavaClassExpressionsMill.prettyPrint(ast, false);

    assertEquals("Integer.class", output);
  }

  @Test
  public void testPrimaryGenericInvocationExpressionExpression() throws IOException {
    Optional<ASTPrimaryGenericInvocationExpression> result = parser.parse_StringPrimaryGenericInvocationExpression("<Integer> super(a)");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTPrimaryGenericInvocationExpression ast = result.get();

    String output = TestJavaClassExpressionsMill.prettyPrint(ast, false);

    assertEquals("<Integer>super(a)", output);
  }

  @Test
  public void testInstanceofExpression() throws IOException {
    Optional<ASTExpression> a = parser.parse_StringExpression("a");
    Optional<ASTMCType> type = parser.parse_StringMCType("Integer");
    assertFalse(parser.hasErrors());
    assertTrue(a.isPresent());
    assertTrue(type.isPresent());
    ASTInstanceofExpression result = JavaClassExpressionsMill.instanceofExpressionBuilder()
            .setExpression(a.get())
            .setMCType(type.get())
            .build();
    String output = TestJavaClassExpressionsMill.prettyPrint(result, false);

    assertEquals("a instanceof Integer", output);
  }

  @Test
  public void testThisExpression() throws IOException {
    Optional<ASTExpression> a = parser.parse_StringExpression("a");
    assertFalse(parser.hasErrors());
    assertTrue(a.isPresent());
    ASTThisExpression result = JavaClassExpressionsMill.thisExpressionBuilder()
            .setExpression(a.get())
            .build();

    String output = TestJavaClassExpressionsMill.prettyPrint(result, false);

    assertEquals("a.this", output);

    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testArrayExpression() throws IOException {
    Optional<ASTExpression> a = parser.parse_StringExpression("a");
    Optional<ASTExpression> b = parser.parse_StringExpression("b");
    assertFalse(parser.hasErrors());
    assertTrue(a.isPresent());
    assertTrue(b.isPresent());
    ASTArrayAccessExpression result = JavaClassExpressionsMill.arrayAccessExpressionBuilder()
            .setExpression(a.get())
            .setIndexExpression(b.get())
            .build();

    String output = TestJavaClassExpressionsMill.prettyPrint(result, false);

    assertEquals("a[b]", output);

    assertTrue(Log.getFindings().isEmpty());
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

    String output = TestJavaClassExpressionsMill.prettyPrint(result, false);

    assertEquals("a.super(b)", output);

    assertTrue(Log.getFindings().isEmpty());
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

    String output = TestJavaClassExpressionsMill.prettyPrint(result, false);

    assertEquals("a.<D>c(b)", output);
  }

  @Test
  public void testGenericInvocationSuffixThis() throws IOException {
    Optional<ASTGenericInvocationSuffix> result = parser.parse_StringGenericInvocationSuffix("this(a)");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTGenericInvocationSuffix ast = result.get();

    String output = TestJavaClassExpressionsMill.prettyPrint(ast, false);

    result = parser.parse_StringGenericInvocationSuffix(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));

    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGenericInvocationSuffixSuper() throws IOException {
    Optional<ASTGenericInvocationSuffix> result = parser.parse_StringGenericInvocationSuffix("super(b)");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTGenericInvocationSuffix ast = result.get();

    String output = TestJavaClassExpressionsMill.prettyPrint(ast, false);

    result = parser.parse_StringGenericInvocationSuffix(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));

    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGenericInvocationSuffixSimple() throws IOException {
    Optional<ASTGenericInvocationSuffix> result = parser.parse_StringGenericInvocationSuffix("a(b)");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTGenericInvocationSuffix ast = result.get();

    String output = TestJavaClassExpressionsMill.prettyPrint(ast, false);

    result = parser.parse_StringGenericInvocationSuffix(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));

    assertTrue(Log.getFindings().isEmpty());
  }
}
