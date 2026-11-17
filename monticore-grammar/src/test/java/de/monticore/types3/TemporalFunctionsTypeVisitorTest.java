/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types3;

import de.monticore.expressions.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import de.monticore.expressions.combineexpressionswithliterals._ast.ASTFoo;
import de.monticore.expressions.combineexpressionswithliterals._symboltable.ICombineExpressionsWithLiteralsArtifactScope;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.temporal.temporalbasis.TemporalBasisMill;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symboltable.ImportStatement;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types3.temporal.TemporalSymTypeFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.stream.Stream;

import static de.monticore.runtime.junit.MCAssertions.assertNoFindings;
import static de.monticore.types3.util.DefsTypesForTests.inScope;
import static de.monticore.types3.util.DefsTypesForTests.variable;

/**
 * Tests the functional style operations of the temporal type system,
 * s. Appendix B.2 of the documentation of the temporal languages.
 * The operators are tested in {@link TemporalTypeVisitorTest}.
 * The functions are in the package de.monticore.temporal,
 * thus, the expressions are checked in an artifact scope
 * that imports de.monticore.temporal.*,
 * unless {@link #importTemporal} is set to false.
 */
public class TemporalFunctionsTypeVisitorTest extends AbstractTypeVisitorTest {

  protected static final String NO_APPLICABLE_FUNCTION_ERROR_CODE = "0xFD44E";
  protected static final String WRONG_ARITY_ERROR_CODE = "0xFD44D";
  protected static final String UNKNOWN_NAME_ERROR_CODE = "0xFD118";

  /**
   * whether the expressions are checked in a scope
   * that imports de.monticore.temporal.*
   */
  protected boolean importTemporal = true;

  @BeforeEach
  public void setupTemporalSymbols() {
    TemporalBasisMill.initializeTemporalSymbols();
    IBasicSymbolsScope gs = BasicSymbolsMill.globalScope();
    inScope(gs, variable("tp", TemporalSymTypeFactory.createTimePoint()));
    inScope(gs, variable("dt", TemporalSymTypeFactory.createDayTime()));
    inScope(gs, variable("p", TemporalSymTypeFactory.createPeriod()));
    inScope(gs, variable("d", TemporalSymTypeFactory.createDuration(
        SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.DOUBLE))));
    inScope(gs, variable("i", SymTypeExpressionFactory
        .createPrimitive(BasicSymbolsMill.INT)));
    inScope(gs, variable("l", SymTypeExpressionFactory
        .createPrimitive(BasicSymbolsMill.LONG)));
    inScope(gs, variable("s", SymTypeExpressionFactory.createStringType()));
    assertNoFindings();
  }

  @Override
  protected void generateScopes(ASTExpression expr) {
    // same as the super method, but with the import (if set)
    ASTFoo rootNode = CombineExpressionsWithLiteralsMill.fooBuilder()
        .setExpression(expr)
        .build();
    ICombineExpressionsWithLiteralsArtifactScope rootScope =
        CombineExpressionsWithLiteralsMill.scopesGenitorDelegator()
            .createFromAST(rootNode);
    rootScope.setName("fooRoot");
    if (importTemporal) {
      rootScope.addImports(new ImportStatement("de.monticore.temporal", true));
    }
    expr.accept(getSymbolTableCompleter());
  }

  /**
   * each signature of Appendix B.2, called with arguments of the parameter types
   */
  @ParameterizedTest
  @MethodSource
  public void deriveFromFunctionCalls(String expr, String expectedType)
      throws IOException {
    checkExpr(expr, expectedType);
  }

  public static Stream<Arguments> deriveFromFunctionCalls() {
    return Stream.of(
        // B.2.1 getters for instant types
        Arguments.of("getDayTime(tp)", "de.monticore.temporal.DayTime"),
        Arguments.of("getCentury(tp)", "int"),
        Arguments.of("getDecade(tp)", "int"),
        Arguments.of("getYear(tp)", "int"),
        Arguments.of("getWeekYear(tp)", "int"),
        Arguments.of("getMonth(tp)", "int"),
        Arguments.of("getWeek(tp)", "int"),
        Arguments.of("getDayOfYear(tp)", "int"),
        Arguments.of("getDay(tp)", "int"),
        Arguments.of("getDayOfWeek(tp)", "int"),
        Arguments.of("getHour(tp)", "int"),
        Arguments.of("getHour(dt)", "int"),
        Arguments.of("getAmPm(tp)", "int"),
        Arguments.of("getAmPm(dt)", "int"),
        Arguments.of("getClockHour(tp)", "int"),
        Arguments.of("getClockHour(dt)", "int"),
        Arguments.of("getMinute(tp)", "int"),
        Arguments.of("getMinute(dt)", "int"),
        Arguments.of("getSecond(tp)", "int"),
        Arguments.of("getSecond(dt)", "int"),
        Arguments.of("getMillisecond(tp)", "int"),
        Arguments.of("getMillisecond(dt)", "int"),
        Arguments.of("getNanosecond(tp)", "int"),
        Arguments.of("getNanosecond(dt)", "int"),
        Arguments.of("getOffset(tp)", "int"),
        Arguments.of("getOffset(dt)", "int"),
        Arguments.of("getOffsetHour(tp)", "int"),
        Arguments.of("getOffsetHour(dt)", "int"),
        Arguments.of("getOffsetMinute(tp)", "int"),
        Arguments.of("getOffsetMinute(dt)", "int"),
        Arguments.of("getZone(tp)", "String"),
        Arguments.of("getZone(dt)", "String"),
        // B.2.2 withers for instant types
        Arguments.of("withDayTime(tp, dt)", "de.monticore.temporal.TimePoint"),
        Arguments.of("withCentury(tp, i)", "de.monticore.temporal.TimePoint"),
        Arguments.of("withDecade(tp, i)", "de.monticore.temporal.TimePoint"),
        Arguments.of("withYear(tp, i)", "de.monticore.temporal.TimePoint"),
        Arguments.of("withWeekYear(tp, i)", "de.monticore.temporal.TimePoint"),
        Arguments.of("withMonth(tp, i)", "de.monticore.temporal.TimePoint"),
        Arguments.of("withWeek(tp, i)", "de.monticore.temporal.TimePoint"),
        Arguments.of("withDayOfYear(tp, i)", "de.monticore.temporal.TimePoint"),
        Arguments.of("withDay(tp, i)", "de.monticore.temporal.TimePoint"),
        Arguments.of("withDayOfWeek(tp, i)", "de.monticore.temporal.TimePoint"),
        Arguments.of("withHour(tp, i)", "de.monticore.temporal.TimePoint"),
        Arguments.of("withHour(dt, i)", "de.monticore.temporal.DayTime"),
        Arguments.of("withAmPm(tp, i)", "de.monticore.temporal.TimePoint"),
        Arguments.of("withAmPm(dt, i)", "de.monticore.temporal.DayTime"),
        Arguments.of("withClockHour(tp, i)", "de.monticore.temporal.TimePoint"),
        Arguments.of("withClockHour(dt, i)", "de.monticore.temporal.DayTime"),
        Arguments.of("withMinute(tp, i)", "de.monticore.temporal.TimePoint"),
        Arguments.of("withMinute(dt, i)", "de.monticore.temporal.DayTime"),
        Arguments.of("withSecond(tp, i)", "de.monticore.temporal.TimePoint"),
        Arguments.of("withSecond(dt, i)", "de.monticore.temporal.DayTime"),
        Arguments.of("withMillisecond(tp, i)", "de.monticore.temporal.TimePoint"),
        Arguments.of("withMillisecond(dt, i)", "de.monticore.temporal.DayTime"),
        Arguments.of("withNanosecond(tp, i)", "de.monticore.temporal.TimePoint"),
        Arguments.of("withNanosecond(dt, i)", "de.monticore.temporal.DayTime"),
        Arguments.of("withZone(tp, s)", "de.monticore.temporal.TimePoint"),
        Arguments.of("withZone(dt, s)", "de.monticore.temporal.DayTime"),
        Arguments.of("adjustToZone(tp, s)", "de.monticore.temporal.TimePoint"),
        Arguments.of("adjustToZone(dt, s)", "de.monticore.temporal.DayTime"),
        // B.2.3 getters for the type Period (long instead of int)
        Arguments.of("getYears(p)", "long"),
        Arguments.of("getMonths(p)", "long"),
        Arguments.of("getWeeks(p)", "long"),
        Arguments.of("getDays(p)", "long"),
        Arguments.of("getWeekDays(p)", "long"),
        Arguments.of("getHours(p)", "long"),
        Arguments.of("getMinutes(p)", "long"),
        Arguments.of("getSeconds(p)", "long"),
        Arguments.of("getMilliseconds(p)", "long"),
        Arguments.of("getNanoseconds(p)", "long"),
        // B.2.4 setters for the type Period (long instead of int)
        Arguments.of("withYears(p, l)", "de.monticore.temporal.Period"),
        Arguments.of("withMonths(p, l)", "de.monticore.temporal.Period"),
        Arguments.of("withWeeks(p, l)", "de.monticore.temporal.Period"),
        Arguments.of("withDays(p, l)", "de.monticore.temporal.Period"),
        Arguments.of("withWeekDays(p, l)", "de.monticore.temporal.Period"),
        Arguments.of("withHours(p, l)", "de.monticore.temporal.Period"),
        Arguments.of("withMinutes(p, l)", "de.monticore.temporal.Period"),
        Arguments.of("withSeconds(p, l)", "de.monticore.temporal.Period"),
        Arguments.of("withMilliseconds(p, l)", "de.monticore.temporal.Period"),
        Arguments.of("withNanoseconds(p, l)", "de.monticore.temporal.Period"),
        // B.2.5 formatters for instant types
        Arguments.of("format(tp, s)", "String"),
        Arguments.of("format(dt, s)", "String"),
        Arguments.of("format(tp)", "String"),
        Arguments.of("format(dt)", "String"),
        Arguments.of("formatZoned(tp)", "String"),
        Arguments.of("formatZoned(dt)", "String"),
        Arguments.of("formatDate(tp)", "String"),
        Arguments.of("formatOrdinalDate(tp)", "String"),
        Arguments.of("formatWeekDate(tp)", "String"),
        Arguments.of("formatTime(tp)", "String"),
        Arguments.of("formatTime(dt)", "String"),
        Arguments.of("formatOffsetTime(tp)", "String"),
        Arguments.of("formatOffsetTime(dt)", "String"),
        Arguments.of("formatDateTime(tp)", "String"),
        Arguments.of("formatOffsetDateTime(tp)", "String"),
        Arguments.of("formatDE(tp)", "String"),
        Arguments.of("formatDE(dt)", "String"),
        Arguments.of("formatDEDate(tp)", "String"),
        Arguments.of("formatDEAlnumDate(tp)", "String"),
        Arguments.of("formatDETime(tp)", "String"),
        Arguments.of("formatDETime(dt)", "String"),
        Arguments.of("formatUS(tp)", "String"),
        Arguments.of("formatUS(dt)", "String"),
        Arguments.of("formatUSDate(tp)", "String"),
        Arguments.of("formatUSAlnumDate(tp)", "String"),
        Arguments.of("formatUSTime(tp)", "String"),
        Arguments.of("formatUSTime(dt)", "String"),
        // B.2.6 formatters for the type Period
        Arguments.of("format(p)", "String"),
        Arguments.of("formatNormalized(p)", "String"),
        Arguments.of("formatWeeks(p)", "String"),
        // B.2.7 miscellaneous
        Arguments.of("now()", "de.monticore.temporal.TimePoint"),
        Arguments.of("utc()", "de.monticore.temporal.TimePoint"),
        Arguments.of("minus(tp, tp)", "de.monticore.temporal.Period"),
        Arguments.of("minus(dt, dt)", "de.monticore.temporal.Period"),
        Arguments.of("abs(p)", "de.monticore.temporal.Period")
    );
  }

  /**
   * arguments that are not exactly of the parameter types
   */
  @ParameterizedTest
  @MethodSource
  public void deriveFromFunctionCallsWithOtherArguments(
      String expr, String expectedType
  ) throws IOException {
    checkExpr(expr, expectedType);
  }

  public static Stream<Arguments> deriveFromFunctionCallsWithOtherArguments() {
    return Stream.of(
        // literals
        Arguments.of("getYear(d\"2015-04-01\")", "int"),
        Arguments.of("getHour(d\"12:30\")", "int"),
        Arguments.of("getHour(2017-12-04T12:30)", "int"),
        Arguments.of("getDays(d\"P1D\")", "long"),
        Arguments.of("withZone(tp, \"Europe/Berlin\")", "de.monticore.temporal.TimePoint"),
        Arguments.of("format(dt, \"HH:mm\")", "String"),
        Arguments.of("withMonth(tp, 5)", "de.monticore.temporal.TimePoint"),
        // widening (int to long)
        Arguments.of("withYears(p, i)", "de.monticore.temporal.Period"),
        Arguments.of("withYears(p, 3)", "de.monticore.temporal.Period"),
        Arguments.of("withYears(p, 3L)", "de.monticore.temporal.Period"),
        Arguments.of("withHour(tp, 'a')", "de.monticore.temporal.TimePoint"),
        // expressions
        Arguments.of("getYear(tp + p)", "int"),
        Arguments.of("getMinute(dt - 30min)", "int"),
        Arguments.of("abs(-p)", "de.monticore.temporal.Period"),
        Arguments.of("getYears(minus(now(), d\"1987-06-05\"))", "long"),
        Arguments.of("withDayTime(utc(), getDayTime(now()))", "de.monticore.temporal.TimePoint"),
        Arguments.of("now() < tp + i * 1s", "boolean"),
        Arguments.of("getYear(now()) + 1", "int"),
        Arguments.of("getYears(p) + 1", "long")
    );
  }

  @ParameterizedTest
  @MethodSource
  public void deriveFromInvalidFunctionCalls(String expr) throws IOException {
    checkErrorExpr(expr, NO_APPLICABLE_FUNCTION_ERROR_CODE);
  }

  public static Stream<String> deriveFromInvalidFunctionCalls() {
    return Stream.of(
        // wrong temporal type
        "getYear(dt)", "getYear(p)", "getYear(d)",
        "getYear(d\"P1D\")", "getDayTime(dt)",
        "getHour(p)", "getYears(tp)", "getYears(d)",
        "withMonth(dt, i)", "withDays(d, i)", "withDayTime(dt, dt)",
        "formatDate(dt)", "formatWeeks(tp)", "abs(d)", "minus(tp, dt)",
        // wrong other arguments
        "withMonth(tp, \"May\")", "withMonth(tp, l)", "withHour(tp, 2.5)",
        "withYears(p, 2.5)", "withZone(tp, 1)", "format(p, s)"
    );
  }

  @ParameterizedTest
  @MethodSource
  public void deriveFromFunctionCallsWithWrongArity(String expr)
      throws IOException {
    checkErrorExpr(expr, WRONG_ARITY_ERROR_CODE);
  }

  public static Stream<String> deriveFromFunctionCallsWithWrongArity() {
    return Stream.of(
        "getYear()", "getYear(tp, tp)", "now(tp)", "withYear(tp)",
        "format(tp, s, s)"
    );
  }

  @Test
  public void deriveFromFunctionsWithoutCall() throws IOException {
    // non-overloaded functions can be used as values
    checkExpr("now", "() -> de.monticore.temporal.TimePoint");
    checkExpr("getDayTime",
        "de.monticore.temporal.TimePoint -> de.monticore.temporal.DayTime");
    checkExpr("withDayTime",
        "(de.monticore.temporal.TimePoint, de.monticore.temporal.DayTime)"
            + " -> de.monticore.temporal.TimePoint");
  }

  @Test
  public void deriveFromQualifiedFunctionCalls() throws IOException {
    importTemporal = false;
    checkExpr("de.monticore.temporal.getYear(tp)", "int");
    checkExpr("de.monticore.temporal.now()", "de.monticore.temporal.TimePoint");
    checkExpr("de.monticore.temporal.withYears(p, i)",
        "de.monticore.temporal.Period");
    checkErrorExpr("getYear(tp)", UNKNOWN_NAME_ERROR_CODE);
  }

}
