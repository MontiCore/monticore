package de.monticore.expressions.streamexpressions.parser;

import de.monticore.expressions.commonexpressions._ast.ASTCallExpression;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.commonexpressions._ast.ASTGreaterThanExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.javaclassexpressions._ast.ASTPrimaryGenericInvocationExpression;
import de.monticore.expressions.javaclassexpressions._ast.ASTSuperExpression;
import de.monticore.expressions.streamexpressions._ast.ASTAppendStreamExpression;
import de.monticore.expressions.streamexpressions._ast.ASTConstantsStreamExpressions;
import de.monticore.expressions.streamexpressions._ast.ASTStreamConstructorExpression;
import de.monticore.expressions.teststreamexpressions.TestStreamExpressionsMill;
import de.monticore.expressions.teststreamexpressions._parser.TestStreamExpressionsParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This test checks for parser clashes of stream expressions with the expression universe. Additionally, it checks if
 * parsed expressions are conforming to the priorities set by the grammar
 */
public class CombinedStreamsExpressionsParserTest {
  private final TestStreamExpressionsParser p = TestStreamExpressionsMill.parser();

  @BeforeAll
  public static void setup() {
    LogStub.init();
    TestStreamExpressionsMill.init();
  }

  public static Stream<Arguments> createInputs() {
    return Stream.of(
        Arguments.of("<A> this()", ASTPrimaryGenericInvocationExpression.class),
        Arguments.of("<A> super()", ASTPrimaryGenericInvocationExpression.class),
        Arguments.of("<A> super.foo", ASTPrimaryGenericInvocationExpression.class),
        Arguments.of("<A> foo()", ASTPrimaryGenericInvocationExpression.class),
        Arguments.of("<A, B, C> this()", ASTPrimaryGenericInvocationExpression.class),
        Arguments.of("<A, B, C> super()", ASTPrimaryGenericInvocationExpression.class),
        Arguments.of("<A, B, C> super.foo", ASTPrimaryGenericInvocationExpression.class),
        Arguments.of("<A, B, C> foo()", ASTPrimaryGenericInvocationExpression.class),
        Arguments.of("event < A > 1", ASTGreaterThanExpression.class),
        Arguments.of("event<A> 1", ASTGreaterThanExpression.class),
        Arguments.of("1: c + 3", ASTAppendStreamExpression.class)
    );
  }

  @ParameterizedTest
  @MethodSource(value = "createInputs")
  public void testStreamExprParsing(String input, Class<?> expectedExpression) throws IOException {
    // tests most relevant parser clashes + priority interaction
    Optional<ASTExpression> expr = p.parse_StringExpression(input);
    assertTrue(expr.isPresent());
    assertInstanceOf(expectedExpression, expr.get());
  }

  @Test
  public void testCallExprSingle() throws IOException {
    // should parse to a call expression on a stream expression
    Optional<ASTExpression> expr = p.parse_StringExpression("<A>.foo()");
    assertTrue(expr.isPresent());
    assertInstanceOf(ASTCallExpression.class, expr.get());
    assertInstanceOf(ASTFieldAccessExpression.class, ((ASTCallExpression) expr.get()).getExpression());
    ASTFieldAccessExpression fieldAccessExpr = ((ASTFieldAccessExpression)((ASTCallExpression) expr.get()).getExpression());
    assertInstanceOf(ASTStreamConstructorExpression.class, fieldAccessExpr.getExpression());
  }

  @Test
  public void testCallExprMultiple() throws IOException {
    // should parse to a call expression on a stream expression
    Optional<ASTExpression> expr = p.parse_StringExpression("<A, B, C>.foo()");
    assertTrue(expr.isPresent());
    assertInstanceOf(ASTCallExpression.class, expr.get());
    assertInstanceOf(ASTFieldAccessExpression.class, ((ASTCallExpression) expr.get()).getExpression());
    ASTFieldAccessExpression fieldAccessExpr = ((ASTFieldAccessExpression)((ASTCallExpression) expr.get()).getExpression());
    assertInstanceOf(ASTStreamConstructorExpression.class, fieldAccessExpr.getExpression());
  }

  @Test
  public void testSuperSingle() throws IOException {
    Optional<ASTExpression> expr = p.parse_StringExpression("<A>.super(p)");
    assertTrue(expr.isPresent());
    assertInstanceOf(ASTSuperExpression.class, expr.get());
    assertInstanceOf(ASTStreamConstructorExpression.class, ((ASTSuperExpression) expr.get()).getExpression());
  }

  @Test
  public void testSuperMultiple() throws IOException {
    Optional<ASTExpression> expr = p.parse_StringExpression("<A, B, C>.super()");
    assertTrue(expr.isPresent());
    assertInstanceOf(ASTSuperExpression.class, expr.get());
    assertInstanceOf(ASTStreamConstructorExpression.class, ((ASTSuperExpression) expr.get()).getExpression());
  }

  @Test
  public void testWithIdentifierWhitespace() throws IOException {
    // should not parse
    Optional<ASTExpression> expr = p.parse_StringExpression("event <A>");
    assertTrue(expr.isPresent());
    assertInstanceOf(ASTStreamConstructorExpression.class, expr.get());
  }

  @Test
  public void testFalseBitshiftInteraction() throws IOException {
    Optional<ASTExpression> expr = p.parse_StringExpression("event < A > > 1");
    assertTrue(expr.isPresent());
    assertInstanceOf(ASTGreaterThanExpression.class, expr.get());
    ASTGreaterThanExpression greater = (ASTGreaterThanExpression) expr.get();
    assertInstanceOf(ASTStreamConstructorExpression.class, greater.getLeft());
  }

  @Test
  public void testBitshiftInteraction() throws IOException {
    // should still parse to "stream > 1"
    Optional<ASTExpression> expr = p.parse_StringExpression("< A >> 1");
    assertTrue(expr.isPresent());
    assertInstanceOf(ASTGreaterThanExpression.class, expr.get());
    ASTGreaterThanExpression greater = (ASTGreaterThanExpression) expr.get();
    assertInstanceOf(ASTStreamConstructorExpression.class, greater.getLeft());
  }

  @Test
  public void testDefaultTiming() throws IOException {
    Optional<ASTStreamConstructorExpression> expr = p.parse_StringStreamConstructorExpression("<A>");
    assertTrue(expr.isPresent());
    ASTStreamConstructorExpression ast = expr.get();
    assertEquals(ASTConstantsStreamExpressions.EVENT, ast.getTiming());
    assertTrue(ast.isEventTimed());
    assertFalse(ast.isSyncTimed());
    assertFalse(ast.isToptTimed());
    assertFalse(ast.isUntimed());
  }

  @AfterAll
  public static void cleanUp() {
    TestStreamExpressionsMill.reset();
  }

}
