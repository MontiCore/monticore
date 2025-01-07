/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.streamexpressions.parser;

import de.monticore.expressions.commonexpressions._ast.ASTCallExpression;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.commonexpressions._ast.ASTGreaterThanExpression;
import de.monticore.expressions.commonexpressions._ast.ASTLessEqualExpression;
import de.monticore.expressions.commonexpressions._ast.ASTLessThanExpression;
import de.monticore.expressions.commonexpressions._ast.ASTPlusExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.javaclassexpressions._ast.ASTPrimaryGenericInvocationExpression;
import de.monticore.expressions.javaclassexpressions._ast.ASTSuperExpression;
import de.monticore.expressions.streamexpressions._ast.ASTAppendAbsentStreamExpression;
import de.monticore.expressions.streamexpressions._ast.ASTAppendTickStreamExpression;
import de.monticore.expressions.streamexpressions._ast.ASTStreamConstructorExpression;
import de.monticore.expressions.teststreamexpressions.TestStreamExpressionsMill;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * This test checks for parser clashes of stream expressions with the expression universe. Additionally, it checks if
 * parsed expressions are conforming to the priorities set by the grammar
 */
public class CombinedStreamsExpressionsParserTest {

  @BeforeEach
  public void setup() {
    LogStub.init();
    TestStreamExpressionsMill.init();
  }

  @ParameterizedTest
  @MethodSource
  public void testStreamExprParsing(String input, Class<?> expectedExpression) throws IOException {
    // tests most relevant parser clashes + priority interaction
    ASTExpression expr = parseExpression(input);
    assertInstanceOf(expectedExpression, expr);
  }

  public static Stream<Arguments> testStreamExprParsing() {
    return Stream.of(
        Arguments.of("< foo (1,2) >", ASTStreamConstructorExpression.class),
        Arguments.of("<1,2>3,3>", ASTStreamConstructorExpression.class),
        Arguments.of("<2>3>", ASTStreamConstructorExpression.class),
        Arguments.of("Event< 1", ASTLessThanExpression.class),
        Arguments.of("Event < 1", ASTLessThanExpression.class),
        Arguments.of("Event <= 1", ASTLessEqualExpression.class),
        Arguments.of("<A> this()", ASTPrimaryGenericInvocationExpression.class),
        Arguments.of("<A> super()", ASTPrimaryGenericInvocationExpression.class),
        Arguments.of("<A> super.foo", ASTPrimaryGenericInvocationExpression.class),
        Arguments.of("<A> foo()", ASTPrimaryGenericInvocationExpression.class),
        Arguments.of("<A, B, C> this()", ASTPrimaryGenericInvocationExpression.class),
        Arguments.of("<A, B, C> super()", ASTPrimaryGenericInvocationExpression.class),
        Arguments.of("<A, B, C> super.foo", ASTPrimaryGenericInvocationExpression.class),
        Arguments.of("<A, B, C> foo()", ASTPrimaryGenericInvocationExpression.class),
        Arguments.of("Event < A > 1", ASTGreaterThanExpression.class),
        Arguments.of("Event< A > 1", ASTGreaterThanExpression.class),
        Arguments.of("1: c + 3", ASTPlusExpression.class),
        Arguments.of("Tick", ASTNameExpression.class),
        Arguments.of("Abs", ASTNameExpression.class),
        Arguments.of("Tick : a", ASTAppendTickStreamExpression.class),
        Arguments.of("Abs : a", ASTAppendAbsentStreamExpression.class)
    );
  }

  @Test
  public void testCallExprSingle() throws IOException {
    // should parse to a call expression on a stream expression
    ASTExpression expr = parseExpression("<A>.foo()");
    assertInstanceOf(ASTCallExpression.class, expr);
    assertInstanceOf(ASTFieldAccessExpression.class, ((ASTCallExpression) expr).getExpression());
    ASTFieldAccessExpression fieldAccessExpr = ((ASTFieldAccessExpression) ((ASTCallExpression) expr).getExpression());
    assertInstanceOf(ASTStreamConstructorExpression.class, fieldAccessExpr.getExpression());
  }

  @Test
  public void testCallExprMultiple() throws IOException {
    // should parse to a call expression on a stream expression
    ASTExpression expr = parseExpression("<A, B, C>.foo()");
    assertInstanceOf(ASTCallExpression.class, expr);
    assertInstanceOf(ASTFieldAccessExpression.class, ((ASTCallExpression) expr).getExpression());
    ASTFieldAccessExpression fieldAccessExpr = ((ASTFieldAccessExpression) ((ASTCallExpression) expr).getExpression());
    assertInstanceOf(ASTStreamConstructorExpression.class, fieldAccessExpr.getExpression());
  }

  @Test
  public void testSuperSingle() throws IOException {
    ASTExpression expr = parseExpression("<A>.super(p)");
    assertInstanceOf(ASTSuperExpression.class, expr);
    assertInstanceOf(ASTStreamConstructorExpression.class, ((ASTSuperExpression) expr).getExpression());
  }

  @Test
  public void testSuperMultiple() throws IOException {
    ASTExpression expr = parseExpression("<A, B, C>.super()");
    assertInstanceOf(ASTSuperExpression.class, expr);
    assertInstanceOf(ASTStreamConstructorExpression.class, ((ASTSuperExpression) expr).getExpression());
  }

  @Test
  public void testWithIdentifierWhitespace() throws IOException {
    assertNotAnExpression("Event <A>");
  }

  @Test
  public void testFalseBitshiftInteraction() throws IOException {
    ASTExpression expr = parseExpression("Event< A > > 1");
    assertInstanceOf(ASTGreaterThanExpression.class, expr);
    ASTGreaterThanExpression greater = (ASTGreaterThanExpression) expr;
    assertInstanceOf(ASTStreamConstructorExpression.class, greater.getLeft());
  }

  @Test
  public void testFalseBitshiftInteraction1() throws IOException {
    ASTExpression expr = parseExpression("< A > > 1");
    assertInstanceOf(ASTGreaterThanExpression.class, expr);
    ASTGreaterThanExpression greater = (ASTGreaterThanExpression) expr;
    assertInstanceOf(ASTStreamConstructorExpression.class, greater.getLeft());
  }

  @Test
  public void testFalseBitshiftInteraction2() throws IOException {
    ASTExpression expr = parseExpression("<A> > 1");
    assertInstanceOf(ASTGreaterThanExpression.class, expr);
    ASTGreaterThanExpression greater = (ASTGreaterThanExpression) expr;
    assertInstanceOf(ASTStreamConstructorExpression.class, greater.getLeft());
  }

  @ParameterizedTest
  @MethodSource
  public void testConstructorWithAbsent(String model) throws IOException {
    ASTExpression expr = parseExpression(model);
    assertInstanceOf(ASTStreamConstructorExpression.class, expr);
  }

  protected static Stream<Arguments> testConstructorWithAbsent() {
    return Stream.of(
        Arguments.of("Topt<>"),
        Arguments.of("Topt<~>"),
        Arguments.of("Topt<~,~>"),
        Arguments.of("Topt<1,~>"),
        Arguments.of("Topt<~,1>"),
        Arguments.of("Topt<1,1>"),
        Arguments.of("Topt<~, ~, ~, ~>")
    );
  }

  @ParameterizedTest
  @MethodSource
  public void testConstructorWithTick(String model) throws IOException {
    ASTExpression expr = parseExpression(model);
    assertInstanceOf(ASTStreamConstructorExpression.class, expr);
  }

  protected static Stream<Arguments> testConstructorWithTick() {
    return Stream.of(
        Arguments.of("<>"),
        Arguments.of("<Tick>"),
        Arguments.of("<Tick,Tick>"),
        Arguments.of("<1>"),
        Arguments.of("<Tick,1>"),
        Arguments.of("<Tick,Tick,1>"),
        Arguments.of("<1,Tick>"),
        Arguments.of("<1,Tick,Tick>"),
        Arguments.of("<1,1>"),
        Arguments.of("<1,Tick,1>"),
        Arguments.of("<true, Tick, 1>2, Tick>"),
        Arguments.of("<true, Tick, 1<2, Tick>"),
        // with "~"-expression
        Arguments.of("<~1,Tick>"),
        Arguments.of("<Tick,~1>")
    );
  }

  @ParameterizedTest
  @MethodSource
  public void testInvalidConstructor(String model) throws IOException {
    assertNotAnExpression(model);
  }

  protected static Stream<Arguments> testInvalidConstructor() {
    return Stream.of(
        Arguments.of("<,>"),
        Arguments.of("<1,>"),
        Arguments.of("<,1>"),
        Arguments.of("<1,,1>"),
        Arguments.of("<;,1>"),
        Arguments.of("<1,;>"),
        Arguments.of("<;,;>"),
        Arguments.of("<;,~>"),
        Arguments.of("<~,;>"),
        Arguments.of("<;~>"),
        Arguments.of("<~;>")
    );
  }

  @Test
  public void testBitshiftInteraction() throws IOException {
    // should still parse to "stream > 1"
    ASTExpression expr = parseExpression("< A >> 1");
    assertInstanceOf(ASTGreaterThanExpression.class, expr);
    ASTGreaterThanExpression greater = (ASTGreaterThanExpression) expr;
    assertInstanceOf(ASTStreamConstructorExpression.class, greater.getLeft());
  }

  @Test
  public void testDefaultTiming() throws IOException {
    ASTExpression expr = parseExpression("<A>");
    assertInstanceOf(ASTStreamConstructorExpression.class, expr);
    ASTStreamConstructorExpression ast = (ASTStreamConstructorExpression) expr;
    assertTrue(ast.isEventTimed());
    assertFalse(ast.isSyncTimed());
    assertFalse(ast.isToptTimed());
    assertFalse(ast.isUntimed());
  }

  @AfterAll
  public static void cleanUp() {
    TestStreamExpressionsMill.reset();
  }

  // Helpers, could in part be generalized?

  protected static void assertNoFindings() {
    Assertions.assertTrue(Log.getFindings().isEmpty(), "Expected no Log findings, but got:"
        + System.lineSeparator() + getAllFindingsAsString());
  }

  /**
   * @return all findings as one String
   */
  protected static String getAllFindingsAsString() {
    return Log.getFindings().stream()
        .map(Finding::buildMsg)
        .collect(Collectors.joining(System.lineSeparator()))
        ;
  }

  protected ASTExpression parseExpression(String exprStr) throws IOException {
    Optional<ASTExpression> exprOpt = TestStreamExpressionsMill.parser()
        .parse_StringExpression(exprStr);
    assertNoFindings();
    assertTrue(exprOpt.isPresent());
    return exprOpt.get();
  }

  protected void assertNotAnExpression(String exprStr) throws IOException {
    Optional<ASTExpression> exprOpt = TestStreamExpressionsMill.parser()
        .parse_StringExpression(exprStr);
    assertTrue(exprOpt.isEmpty());
    Log.clearFindings();
  }

}
