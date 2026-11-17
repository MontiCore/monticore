/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types3;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.temporal.temporalbasis.TemporalBasisMill;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types3.temporal.TemporalSymTypeFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static de.monticore.runtime.junit.MCAssertions.assertNoFindings;
import static de.monticore.types3.util.DefsTypesForTests.inScope;
import static de.monticore.types3.util.DefsTypesForTests.variable;

/**
 * Tests the temporal type system,
 * s. Appendix B.1 of the documentation of the temporal languages.
 * The functions are tested in {@link TemporalFunctionsTypeVisitorTest}.
 */
public class TemporalTypeVisitorTest extends AbstractTypeVisitorTest {

  protected static final String ARITHMETIC_OPERATOR_ERROR_CODE = "0xB0163";
  protected static final String NUMERIC_PREFIX_ERROR_CODE = "0xA017D";
  protected static final String EQUALITY_OPERATOR_ERROR_CODE = "0xB0166";
  protected static final String NUMERIC_COMPARISON_ERROR_CODE = "0xB0167";
  protected static final String ASSIGNMENT_ERROR_CODE = "0xA0179";
  protected static final String COMPOUND_ASSIGNMENT_ERROR_CODE = "0xA0178";

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
    assertNoFindings();
  }

  @Test
  public void deriveFromEscapedTemporalLiterals() throws IOException {
    checkExpr("d\"2017-12-04\"", "de.monticore.temporal.TimePoint");
    checkExpr("d\"2017-338\"", "de.monticore.temporal.TimePoint");
    checkExpr("d\"2017-W48-1\"", "de.monticore.temporal.TimePoint");
    checkExpr("d\"2017-12-04T12:30:01\"", "de.monticore.temporal.TimePoint");
    checkExpr("d\"04.12.2017\"", "de.monticore.temporal.TimePoint");
    checkExpr("d\"4. April 2017\"", "de.monticore.temporal.TimePoint");
    checkExpr("d\"04.12.2017 12:30 Uhr\"", "de.monticore.temporal.TimePoint");
    checkExpr("d\"12:30:01\"", "de.monticore.temporal.DayTime");
    checkExpr("d\"12:30 Uhr\"", "de.monticore.temporal.DayTime");
    checkExpr("d\"P1Y2M3DT4H5M6S\"", "de.monticore.temporal.Period");
    checkExpr("d\"P2W\"", "de.monticore.temporal.Period");
  }

  @Test
  public void deriveFromUnescapedTemporalLiterals() throws IOException {
    checkExpr("2017-12-04T12:30:01", "de.monticore.temporal.TimePoint");
    checkExpr("2017W481", "de.monticore.temporal.TimePoint");
    checkExpr("4. April 2017", "de.monticore.temporal.TimePoint");
    checkExpr("04.12.2017 12:30 Uhr", "de.monticore.temporal.TimePoint");
    // a DETime by itself, e.g., "12:30 Uhr", cannot be parsed
    // in CombineExpressionsWithLiterals, as "30 Uhr" is parsed as SIUnitLiteral
    // (and "12 :" as part of an AppendStreamExpression)
  }

  @Test
  public void deriveFromTemporalTypes() throws IOException {
    checkType("de.monticore.temporal.TimePoint", "de.monticore.temporal.TimePoint");
    checkType("de.monticore.temporal.DayTime", "de.monticore.temporal.DayTime");
    checkType("de.monticore.temporal.Period", "de.monticore.temporal.Period");
  }

  @Test
  public void deriveFromInstantComparisons() throws IOException {
    for (String op : new String[]{"<", "<=", ">=", ">"}) {
      checkExpr("tp " + op + " tp", "boolean");
      checkExpr("dt " + op + " dt", "boolean");
      checkErrorExpr("tp " + op + " dt", NUMERIC_COMPARISON_ERROR_CODE);
      checkErrorExpr("tp " + op + " p", NUMERIC_COMPARISON_ERROR_CODE);
      checkErrorExpr("dt " + op + " i", NUMERIC_COMPARISON_ERROR_CODE);
    }
    for (String op : new String[]{"==", "!="}) {
      checkExpr("tp " + op + " tp", "boolean");
      checkExpr("dt " + op + " dt", "boolean");
      checkErrorExpr("tp " + op + " dt", EQUALITY_OPERATOR_ERROR_CODE);
      checkErrorExpr("dt " + op + " d", EQUALITY_OPERATOR_ERROR_CODE);
    }
    checkExpr("d\"2015-04-01\" < tp", "boolean");
  }

  @Test
  public void deriveFromPeriodComparisons() throws IOException {
    for (String op : new String[]{"<", "<=", "==", "!=", ">=", ">"}) {
      checkExpr("p " + op + " p", "boolean");
      checkExpr("d " + op + " d", "boolean");
      checkExpr("d " + op + " 1min", "boolean");
    }
    checkErrorExpr("p < d", NUMERIC_COMPARISON_ERROR_CODE);
    checkErrorExpr("p == d", EQUALITY_OPERATOR_ERROR_CODE);
    checkErrorExpr("p < i", NUMERIC_COMPARISON_ERROR_CODE);
    checkErrorExpr("p < 1d", NUMERIC_COMPARISON_ERROR_CODE);
  }

  @Test
  public void deriveFromDurationArithmetic() throws IOException {
    // provided by the SIUnit types, tested exemplary
    checkExpr("-d", "[s]<double>");
    checkExpr("d + d", "[s]<double>");
    checkExpr("d - 1h", "[s]<double>");
    checkExpr("2 * d", "[s]<double>");
    checkExpr("d / 2", "[s]<double>");
    checkExpr("d / d", "double");
    checkExpr("1d + 5min", "[s]<int>");
    checkExpr("1d < 5min", "boolean");
  }

  @Test
  public void deriveFromPeriodArithmetic() throws IOException {
    checkExpr("-p", "de.monticore.temporal.Period");
    checkExpr("p + p", "de.monticore.temporal.Period");
    checkExpr("p + d", "de.monticore.temporal.Period");
    checkExpr("d + p", "de.monticore.temporal.Period");
    checkExpr("p - p", "de.monticore.temporal.Period");
    checkExpr("p - d", "de.monticore.temporal.Period");
    checkExpr("d - p", "de.monticore.temporal.Period");
    checkExpr("d\"P1M\" + 2min", "de.monticore.temporal.Period");
    for (String n : new String[]{"2", "2L", "2.5f", "2.5"}) {
      checkExpr(n + " * p", "de.monticore.temporal.Period");
      checkExpr("p * " + n, "de.monticore.temporal.Period");
      checkExpr("p / " + n, "de.monticore.temporal.Period");
    }
    checkErrorExpr("p + i", ARITHMETIC_OPERATOR_ERROR_CODE);
    checkErrorExpr("p * p", ARITHMETIC_OPERATOR_ERROR_CODE);
    checkErrorExpr("p * d", ARITHMETIC_OPERATOR_ERROR_CODE);
    checkErrorExpr("p / p", ARITHMETIC_OPERATOR_ERROR_CODE);
    checkErrorExpr("2 / p", ARITHMETIC_OPERATOR_ERROR_CODE);
    checkErrorExpr("p % 2", ARITHMETIC_OPERATOR_ERROR_CODE);
    checkErrorExpr("+p", NUMERIC_PREFIX_ERROR_CODE);
  }

  @Test
  public void deriveFromInstantArithmetic() throws IOException {
    checkExpr("tp + p", "de.monticore.temporal.TimePoint");
    checkExpr("p + tp", "de.monticore.temporal.TimePoint");
    checkExpr("tp - p", "de.monticore.temporal.TimePoint");
    checkExpr("tp + d", "de.monticore.temporal.TimePoint");
    checkExpr("d + tp", "de.monticore.temporal.TimePoint");
    checkExpr("tp - d", "de.monticore.temporal.TimePoint");
    checkExpr("tp + i * 1s", "de.monticore.temporal.TimePoint");
    checkExpr("tp + 2h", "de.monticore.temporal.TimePoint");
    checkExpr("d\"2015-12-04\" + p", "de.monticore.temporal.TimePoint");
    checkExpr("tp + d\"P1D\"", "de.monticore.temporal.TimePoint");
    checkExpr("d\"2017-12-04\" - 1d", "de.monticore.temporal.TimePoint");
    checkExpr("5min + 2017-12-04T12:30", "de.monticore.temporal.TimePoint");
    checkExpr("dt + d", "de.monticore.temporal.DayTime");
    checkExpr("d + dt", "de.monticore.temporal.DayTime");
    checkExpr("dt - d", "de.monticore.temporal.DayTime");
    checkExpr("dt + 30min", "de.monticore.temporal.DayTime");
    checkExpr("tp - tp", "[s]<double>");
    checkExpr("dt - dt", "[s]<double>");

    checkErrorExpr("tp + tp", ARITHMETIC_OPERATOR_ERROR_CODE);
    checkErrorExpr("tp + dt", ARITHMETIC_OPERATOR_ERROR_CODE);
    checkErrorExpr("tp - dt", ARITHMETIC_OPERATOR_ERROR_CODE);
    checkErrorExpr("tp + i", ARITHMETIC_OPERATOR_ERROR_CODE);
    checkErrorExpr("tp + 1", ARITHMETIC_OPERATOR_ERROR_CODE);
    checkErrorExpr("tp + 1m", ARITHMETIC_OPERATOR_ERROR_CODE);
    checkErrorExpr("p - tp", ARITHMETIC_OPERATOR_ERROR_CODE);
    checkErrorExpr("d - tp", ARITHMETIC_OPERATOR_ERROR_CODE);
    checkErrorExpr("1d - tp", ARITHMETIC_OPERATOR_ERROR_CODE);
    checkErrorExpr("dt + p", ARITHMETIC_OPERATOR_ERROR_CODE);
    checkErrorExpr("dt - p", ARITHMETIC_OPERATOR_ERROR_CODE);
    checkErrorExpr("tp * 2", ARITHMETIC_OPERATOR_ERROR_CODE);
    checkErrorExpr("dt / 2", ARITHMETIC_OPERATOR_ERROR_CODE);
    checkErrorExpr("-tp", NUMERIC_PREFIX_ERROR_CODE);
    checkErrorExpr("tp + \"str\"", ARITHMETIC_OPERATOR_ERROR_CODE);
  }

  @Test
  public void deriveFromAssignments() throws IOException {
    checkExpr("tp = d\"2015-04-01\"", "de.monticore.temporal.TimePoint");
    checkExpr("tp = 2017-12-04T12:30 + p", "de.monticore.temporal.TimePoint");
    checkExpr("dt = d\"12:30\"", "de.monticore.temporal.DayTime");
    checkExpr("p = d\"P1D\" * 2", "de.monticore.temporal.Period");
    checkExpr("d = tp - 2017-12-01T00:00", "[s]<double>");
    checkExpr("d = 5.0min", "[s]<double>");
    checkExpr("tp += p", "de.monticore.temporal.TimePoint");
    checkExpr("tp -= 1d", "de.monticore.temporal.TimePoint");
    checkExpr("dt -= 1s", "de.monticore.temporal.DayTime");
    checkExpr("p *= 2", "de.monticore.temporal.Period");
    checkExpr("d += tp - tp", "[s]<double>");

    checkErrorExpr("dt = d\"2017-12-04\"", ASSIGNMENT_ERROR_CODE);
    checkErrorExpr("tp = d\"P1D\"", ASSIGNMENT_ERROR_CODE);
    checkErrorExpr("p = 1d", ASSIGNMENT_ERROR_CODE);
    checkErrorExpr("d = d\"P1D\"", ASSIGNMENT_ERROR_CODE);
    checkErrorExpr("tp *= 2", COMPOUND_ASSIGNMENT_ERROR_CODE);
    checkErrorExpr("dt += p", COMPOUND_ASSIGNMENT_ERROR_CODE);
  }

}
