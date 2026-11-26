/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal;

import de.monticore.antlr4.MCConcreteParser;
import de.monticore.ast.ASTNode;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.runtime.junit.MCAssertions;
import de.monticore.runtime.junit.PrettyPrinterTester.ParseFunction;
import de.monticore.temporal.detemporals._ast.ASTDEAlphanumericDate;
import de.monticore.temporal.detemporals._ast.ASTDEDateTime;
import de.monticore.temporal.detemporals._ast.ASTDENumericDate;
import de.monticore.temporal.detemporals._ast.ASTDETime;
import de.monticore.temporal.detemporals._visitor.DETemporalsTraverser;
import de.monticore.temporal.detemporals._visitor.DETemporalsVisitor2;
import de.monticore.temporal.escapedtemporalliterals._ast.ASTEscapedTemporalLiteral;
import de.monticore.temporal.escapedtemporalliterals._visitor.EscapedTemporalLiteralsTraverser;
import de.monticore.temporal.escapedtemporalliterals._visitor.EscapedTemporalLiteralsVisitor2;
import de.monticore.temporal.isotemporals._ast.ASTBasicWeekDate;
import de.monticore.temporal.isotemporals._ast.ASTCalendarDate;
import de.monticore.temporal.isotemporals._ast.ASTExtendedWeekDate;
import de.monticore.temporal.isotemporals._ast.ASTFullPeriod;
import de.monticore.temporal.isotemporals._ast.ASTISODateTime;
import de.monticore.temporal.isotemporals._ast.ASTISOTemporalsNode;
import de.monticore.temporal.isotemporals._ast.ASTISOTime;
import de.monticore.temporal.isotemporals._ast.ASTOrdinalDate;
import de.monticore.temporal.isotemporals._ast.ASTWeekPeriod;
import de.monticore.temporal.isotemporals._visitor.ISOTemporalsTraverser;
import de.monticore.temporal.isotemporals._visitor.ISOTemporalsVisitor2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static de.monticore.runtime.junit.PrettyPrinterTester.testPrettyPrinter;

/**
 * Tests whether the temporal grammars still work when combined with another grammar.
 * <p>
 * Each subclass in {@code de.monticore.temporal.compatibility} tests one host language
 * {@code TemporalsWith<X>}, which extends ISOTemporals, DETemporals, EscapedTemporalLiterals,
 * and the grammar X. Hosts with expressions also extend CommonExpressions, so that the
 * expression templates can be parsed. {@code TemporalsWithMCCommonLiterals} is the control:
 * it adds nothing to the temporal grammars.
 * <p>
 * The inputs are checked in three contexts:
 * <ol>
 *   <li>standalone: every valid input of {@link TemporalTestCases} as {@code Instant | Period},</li>
 *   <li>as a literal in an expression, e.g. {@code x < 2017-12-04T12:30}, for the productions
 *   that implement {@code Literal},</li>
 *   <li>as the content of escaped literals, e.g. {@code d"2017-12-04" < d"2017-12-05"}.</li>
 * </ol>
 * The contexts 2 and 3 use the same lexer as context 1, so they only take a few
 * representatives of each kind of input.
 * <p>
 * A known incompatibility is skipped with its reason, see {@link KnownTemporalIncompatibilities}.
 */
public abstract class AbstractTemporalCompatibilityTest {

  protected static final int REPRESENTATIVES = 3;

  protected static final Pattern ISO_DATE_TIME = Pattern.compile("[+-]?\\d+(-?W)?[\\d-]*T.*");

  protected static final List<String> LITERAL_TEMPLATES = List.of(
      "%1$s",
      "x < %1$s",
      "%1$s < x",
      "f(%1$s, y)",
      "b ? %1$s : y",
      "%1$s == %1$s"
  );

  protected static final List<String> ESCAPED_TEMPLATES = List.of(
      "d\"%1$s\"",
      "d\"%1$s\" d\"%1$s\"",
      "d\"%1$s\"\nd\"%1$s\""
  );

  protected static final List<String> ESCAPED_EXPRESSION_TEMPLATES = List.of(
      "d\"%1$s\" < 1",
      "d\"%1$s\" < d\"%1$s\"",
      "f(d\"%1$s\", d\"%1$s\")"
  );

  protected abstract MCConcreteParser getParser();

  /** Parses {@code Instant | Period} and returns the instant or period. */
  protected abstract ParseFunction<ASTNode> temporalParser();

  /** Parses {@code EscapedTemporalLiteral+}. */
  protected abstract ParseFunction<ASTNode> escapedParser();

  /** Parses an expression, or is empty if the host has no expressions. */
  protected abstract Optional<ParseFunction<ASTNode>> expressionParser();

  protected abstract ISOTemporalsTraverser traverser();

  protected abstract String prettyPrint(ASTNode ast);

  @ParameterizedTest(name = "standalone: {0}")
  @MethodSource("standaloneCases")
  void testStandalone(String input, Class<? extends ASTNode> expected) throws IOException {
    assumeCompatible("standalone", input);
    ASTNode ast = temporalParser().apply(input).orElseGet(MCAssertions::failAndPrintFindings);
    Assertions.assertInstanceOf(expected, ast);

    // ISO literals are printed as written, German ones are only compared as ASTs
    Predicate<String> printedAsWritten = ASTISOTemporalsNode.class.isAssignableFrom(expected)
        ? printed -> input.equals(printed.trim())
        : printed -> true;
    testPrettyPrinter(input, getParser(), temporalParser(), this::prettyPrint, printedAsWritten);
  }

  @ParameterizedTest(name = "expression: {0}")
  @MethodSource("literalCases")
  void testLiteralInExpression(String input, Class<? extends ASTNode> expected,
      int occurrences) throws IOException {
    Assumptions.assumeTrue(expressionParser().isPresent(), "The host has no expressions");
    assumeCompatible("expression", input);
    ParseFunction<ASTNode> parser = expressionParser().get();
    ASTNode ast = parser.apply(input).orElseGet(MCAssertions::failAndPrintFindings);
    Assertions.assertEquals(occurrences, count(ast, expected),
        "Expected " + occurrences + " " + expected.getSimpleName());

    testPrettyPrinter(input, getParser(), parser, this::prettyPrint);
  }

  @ParameterizedTest(name = "escaped: {0}")
  @MethodSource("escapedCases")
  void testEscaped(String input, Class<? extends ASTNode> expected, int occurrences)
      throws IOException {
    assumeCompatible("escaped", input);
    checkEscaped(escapedParser(), input, expected, occurrences);
  }

  @ParameterizedTest(name = "escaped expression: {0}")
  @MethodSource("escapedExpressionCases")
  void testEscapedInExpression(String input, Class<? extends ASTNode> expected,
      int occurrences) throws IOException {
    Assumptions.assumeTrue(expressionParser().isPresent(), "The host has no expressions");
    assumeCompatible("escaped expression", input);
    checkEscaped(expressionParser().get(), input, expected, occurrences);
  }

  protected void checkEscaped(ParseFunction<ASTNode> parser, String input,
      Class<? extends ASTNode> expected, int occurrences) throws IOException {
    ASTNode ast = parser.apply(input).orElseGet(MCAssertions::failAndPrintFindings);
    Assertions.assertEquals(occurrences, count(ast, ASTEscapedTemporalLiteral.class),
        "Expected " + occurrences + " escaped literals");
    Assertions.assertEquals(occurrences, count(ast, expected),
        "Expected " + occurrences + " " + expected.getSimpleName());

    testPrettyPrinter(input, getParser(), parser, this::prettyPrint);
  }

  protected void assumeCompatible(String context, String input) {
    Optional<String> reason = KnownTemporalIncompatibilities.reason(
        getClass().getSimpleName(), context, input);
    Assumptions.assumeTrue(reason.isEmpty(), reason.orElse(""));
  }

  /** Counts the temporal nodes of the given class in the AST. */
  protected int count(ASTNode ast, Class<? extends ASTNode> nodeClass) {
    NodeCollector collector = new NodeCollector();
    ISOTemporalsTraverser traverser = traverser();
    traverser.add4ISOTemporals(collector);
    ((DETemporalsTraverser) traverser).add4DETemporals(collector);
    ((EscapedTemporalLiteralsTraverser) traverser).add4EscapedTemporalLiterals(collector);
    ast.accept(traverser);
    return (int) collector.nodes.stream().filter(nodeClass::isInstance).count();
  }

  /**
   * Collects the temporal nodes of the classes the test cases expect. The traverser only
   * calls the visit methods of the concrete classes, so each of them is overridden.
   */
  protected static class NodeCollector implements ISOTemporalsVisitor2, DETemporalsVisitor2,
      EscapedTemporalLiteralsVisitor2 {
    protected List<ASTNode> nodes = new ArrayList<>();

    @Override
    public void visit(ASTCalendarDate node) {
      nodes.add(node);
    }

    @Override
    public void visit(ASTOrdinalDate node) {
      nodes.add(node);
    }

    @Override
    public void visit(ASTBasicWeekDate node) {
      nodes.add(node);
    }

    @Override
    public void visit(ASTExtendedWeekDate node) {
      nodes.add(node);
    }

    @Override
    public void visit(ASTISOTime node) {
      nodes.add(node);
    }

    @Override
    public void visit(ASTISODateTime node) {
      nodes.add(node);
    }

    @Override
    public void visit(ASTFullPeriod node) {
      nodes.add(node);
    }

    @Override
    public void visit(ASTWeekPeriod node) {
      nodes.add(node);
    }

    @Override
    public void visit(ASTDENumericDate node) {
      nodes.add(node);
    }

    @Override
    public void visit(ASTDEAlphanumericDate node) {
      nodes.add(node);
    }

    @Override
    public void visit(ASTDETime node) {
      nodes.add(node);
    }

    @Override
    public void visit(ASTDEDateTime node) {
      nodes.add(node);
    }

    @Override
    public void visit(ASTEscapedTemporalLiteral node) {
      nodes.add(node);
    }
  }

  /** All valid inputs of {@link TemporalTestCases} with the class they parse to. */
  protected static Stream<Arguments> standaloneCases() {
    return Stream.of(
        withClass(TemporalTestCases.calendarDates(), ASTCalendarDate.class),
        withClass(TemporalTestCases.ordinalDates(), ASTOrdinalDate.class),
        weekDates(),
        withClass(TemporalTestCases.isoTimes(), ASTISOTime.class),
        timeShifts(),
        withClass(TemporalTestCases.calendarDateTimes(), ASTISODateTime.class),
        withClass(TemporalTestCases.ordinalDateTimes(), ASTISODateTime.class),
        withClass(TemporalTestCases.weekDateTimes(), ASTISODateTime.class),
        withClass(TemporalTestCases.fullPeriods(), ASTFullPeriod.class),
        withClass(TemporalTestCases.weekPeriods(), ASTWeekPeriod.class),
        withClass(TemporalTestCases.numericDatesWithoutYear(), ASTDENumericDate.class),
        withClass(TemporalTestCases.alphanumericDates(), ASTDEAlphanumericDate.class),
        withClass(TemporalTestCases.germanTimes(), ASTDETime.class),
        withClass(TemporalTestCases.germanDateTimes(), ASTDEDateTime.class)
    ).flatMap(Function.identity());
  }

  protected static Stream<Arguments> literalCases() {
    return representatives()
        .filter(arguments -> ASTLiteral.class.isAssignableFrom(classOf(arguments)))
        .flatMap(arguments -> fill(LITERAL_TEMPLATES, arguments));
  }

  protected static Stream<Arguments> escapedCases() {
    return representatives().flatMap(arguments -> fill(ESCAPED_TEMPLATES, arguments));
  }

  protected static Stream<Arguments> escapedExpressionCases() {
    return representatives().flatMap(arguments -> fill(ESCAPED_EXPRESSION_TEMPLATES, arguments));
  }

  /** The first inputs of each kind; the kinds are consecutive in {@link #standaloneCases()}. */
  protected static Stream<Arguments> representatives() {
    List<Arguments> result = new ArrayList<>();
    Class<?> previous = null;
    int taken = 0;
    for (Arguments arguments : standaloneCases().toList()) {
      Class<?> current = classOf(arguments);
      taken = current == previous ? taken + 1 : 0;
      previous = current;
      if (taken < REPRESENTATIVES) {
        result.add(arguments);
      }
    }
    return result.stream();
  }

  protected static Stream<Arguments> fill(List<String> templates, Arguments arguments) {
    String input = (String) arguments.get()[0];
    return templates.stream().map(template -> Arguments.of(String.format(template, input),
        classOf(arguments), occurrences(template)));
  }

  protected static int occurrences(String template) {
    return template.split(Pattern.quote("%1$s"), -1).length - 1;
  }

  @SuppressWarnings("unchecked")
  protected static Class<? extends ASTNode> classOf(Arguments arguments) {
    return (Class<? extends ASTNode>) arguments.get()[1];
  }

  protected static Stream<Arguments> withClass(Stream<Arguments> testCases,
      Class<? extends ASTNode> astClass) {
    return testCases.map(arguments -> Arguments.of(arguments.get()[0], astClass));
  }

  protected static Stream<Arguments> weekDates() {
    return TemporalTestCases.weekDates().map(arguments -> {
      String input = (String) arguments.get()[0];
      return Arguments.of(input,
          input.contains("-W") ? ASTExtendedWeekDate.class : ASTBasicWeekDate.class);
    });
  }

  protected static Stream<Arguments> timeShifts() {
    return TemporalTestCases.isoTimeShifts().map(arguments -> {
      String input = (String) arguments.get()[0];
      return Arguments.of(input,
          ISO_DATE_TIME.matcher(input).matches() ? ASTISODateTime.class : ASTISOTime.class);
    });
  }
}
