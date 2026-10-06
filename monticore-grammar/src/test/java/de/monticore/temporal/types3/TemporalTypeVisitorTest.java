/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal.types3;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.runtime.junit.MCAssertions;
import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCBlockStatement;
import de.monticore.statements.mcvardeclarationstatements._cocos.VarDeclarationInitializationHasCorrectType;
import de.monticore.statements.mcvardeclarationstatements._symboltable.MCVarDeclarationStatementsSymTabCompletion;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.temporal.TemporalTestCases;
import de.monticore.temporal.temporalscript.TemporalScriptMill;
import de.monticore.temporal.temporalscript._ast.ASTScript;
import de.monticore.temporal.temporalscript._cocos.TemporalScriptCoCoChecker;
import de.monticore.temporal.temporalscript._visitor.TemporalScriptTraverser;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.TypeCheck3;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@TestWithMCLanguage(TemporalScriptMill.class)
public class TemporalTypeVisitorTest extends TemporalTestCases {

  protected static final String TIME_POINT = TemporalSymTypeRelations.TIME_POINT;
  protected static final String DAY_TIME = TemporalSymTypeRelations.DAY_TIME;
  protected static final String PERIOD = TemporalSymTypeRelations.PERIOD;

  @BeforeEach
  public void setup() {
    BasicSymbolsMill.initializePrimitives();
    TemporalTypes.init();
    TemporalScriptTypeCheck3.init();
    addVariable("tp", TemporalSymTypeFactory.createTimePoint());
    addVariable("dt", TemporalSymTypeFactory.createDayTime());
    addVariable("p", TemporalSymTypeFactory.createPeriod());
    addVariable("dur", TemporalSymTypeFactory.createDuration());
  }

  protected void addVariable(String name, SymTypeExpression type) {
    BasicSymbolsMill.globalScope().add(BasicSymbolsMill.variableSymbolBuilder()
        .setName(name)
        .setFullName(name)
        .setType(type)
        .setEnclosingScope(BasicSymbolsMill.globalScope())
        .setAccessModifier(AccessModifier.ALL_INCLUSION)
        .build());
  }

  @AfterEach
  public void tearDown() {
    TemporalScriptTypeCheck3.reset();
  }

  // literals

  protected static Stream<Arguments> escapedDates() {
    return Stream.of(calendarDates(), ordinalDates(), weekDates(),
            numericDates(), alphanumericDates())
        .flatMap(s -> s)
        .map(args -> Arguments.of(args.get()[0]));
  }

  @ParameterizedTest
  @MethodSource("escapedDates")
  public void testEscapedDate(String date) throws IOException {
    checkExpr("d\"" + date + "\"", TIME_POINT);
  }

  protected static Stream<Arguments> escapedDateTimes() {
    return Stream.of(calendarDateTimes(), ordinalDateTimes(),
            weekDateTimes(), germanDateTimes())
        .flatMap(s -> s)
        .map(args -> Arguments.of(args.get()[0]));
  }

  @ParameterizedTest
  @MethodSource("escapedDateTimes")
  public void testEscapedDateTime(String dateTime) throws IOException {
    checkExpr("d\"" + dateTime + "\"", TIME_POINT);
  }

  protected static Stream<Arguments> escapedTimes() {
    return Stream.of(isoTimes(), germanTimes())
        .flatMap(s -> s)
        .map(args -> Arguments.of(args.get()[0]));
  }

  @ParameterizedTest
  @MethodSource("escapedTimes")
  public void testEscapedTime(String time) throws IOException {
    checkExpr("d\"" + time + "\"", DAY_TIME);
  }

  protected static Stream<Arguments> escapedPeriods() {
    return Stream.of(fullPeriods(), weekPeriods())
        .flatMap(s -> s)
        .map(args -> Arguments.of(args.get()[0]));
  }

  @ParameterizedTest
  @MethodSource("escapedPeriods")
  public void testEscapedPeriod(String period) throws IOException {
    checkExpr("d\"" + period + "\"", PERIOD);
  }

  /**
   * temporal values which can be used without escaping
   */
  @ParameterizedTest
  @CsvSource(delimiter = ';', value = {
      // ISO date and time
      "2017-12-04T12:30:00;   " + TIME_POINT,
      "2017-12-04T12:30:00Z;  " + TIME_POINT,
      "20171204T123000+0100;  " + TIME_POINT,
      // ISO week date in basic format
      "2017W481;              " + TIME_POINT,
      // German formats
      "4. Dezember 2017;      " + TIME_POINT,
      "4. Dezember 2017 12:30 Uhr; " + TIME_POINT,
      "4.12.2017 12:30 Uhr;   " + TIME_POINT,
      "Jan. 2017;             " + TIME_POINT,
      "12:30 Uhr;             " + DAY_TIME,
      "12:30:01 Uhr;          " + DAY_TIME,
  })
  public void testUnescapedLiterals(String expr, String expectedType) throws IOException {
    checkExpr(expr, expectedType);
  }

  // operators

  /**
   * Note: An escaped temporal literal cannot be followed by a String
   * or a further escaped temporal literal,
   * as, e.g., {@code d"2017-12-04" + d"P1D"} is lexed with the String
   * {@code " + d"}. Thus, the variables tp, dt, p, and dur are used.
   */
  @ParameterizedTest
  @CsvSource(delimiter = ';', value = {
      // +
      "tp + p;                               " + TIME_POINT,
      "p + tp;                               " + TIME_POINT,
      "d\"2017-12-04\" + 1d;                  " + TIME_POINT,
      "5min + 2017-12-04T12:30;              " + TIME_POINT,
      "tp + dur;                             " + TIME_POINT,
      "12:30 Uhr + 1.5h;                     " + DAY_TIME,
      "30s + d\"12:30\";                      " + DAY_TIME,
      "dt + dur;                             " + DAY_TIME,
      "p + d\"P2M\";                          " + PERIOD,
      "d\"P1D\" + 2min;                       " + PERIOD,
      "2min + d\"P1D\";                       " + PERIOD,
      // -
      "tp - d\"P1D\";                         " + TIME_POINT,
      "d\"2017-12-04\" - 1d;                  " + TIME_POINT,
      "d\"12:30\" - 1h;                       " + DAY_TIME,
      "tp - d\"2017-12-01\";                  [s]<long>",
      "dt - d\"11:30\";                       [s]<long>",
      "2017-12-04T12:30 - tp;                [s]<long>",
      "p - d\"P2M\";                          " + PERIOD,
      "d\"P1D\" - 2min;                       " + PERIOD,
      "2min - d\"P1D\";                       " + PERIOD,
      "-d\"P1D\";                             " + PERIOD,
      // *, /
      "d\"P1D\" * 2;                          " + PERIOD,
      "2L * d\"P1D\";                         " + PERIOD,
      "d\"P1D\" * 0.5;                        " + PERIOD,
      "d\"P1D\" / 2;                          " + PERIOD,
      "d\"P1D\" / 2.5f;                       " + PERIOD,
      // comparisons
      "d\"2017-12-04\" < 2017-12-04T12:30;    boolean",
      "d\"12:30\" >= 12:30 Uhr;               boolean",
      "p <= d\"P1M\";                         boolean",
      "tp > 2017-12-04T12:30;                boolean",
      "tp == 2017-12-04T12:30;               boolean",
      "p != d\"P1M\";                         boolean",
      // Durations are SIUnit types
      "1d + 5min;                            [s]<int>",
      "1d < 5min;                            boolean",
      "dur / 2;                              [s]<long>",
      // compound assignments
      "tp += p;                              " + TIME_POINT,
      "tp -= 1d;                             " + TIME_POINT,
      "p *= 2;                               " + PERIOD,
  })
  public void testOperators(String expr, String expectedType) throws IOException {
    checkExpr(expr, expectedType);
  }

  @Test
  public void testStringConcatenation() throws IOException {
    ASTExpression expr = parseExpr("\"today is \" + d\"2017-12-04\"");
    SymTypeExpression type = TypeCheck3.typeOf(expr);
    MCAssertions.assertNoFindings();
    assertTrue(SymTypeRelations.isStringOrSubType(type), type.printFullName());
  }

  @ParameterizedTest
  @CsvSource(delimiter = ';', value = {
      "d\"12:30\" + p;                        0xB0163",
      "dt - p;                               0xB0163",
      "tp + dt;                              0xB0163",
      "tp + d\"2017-12-04\";                  0xB0163",
      "tp - dt;                              0xB0163",
      "1d - tp;                              0xB0163",
      "tp + 1;                               0xB0163",
      "tp + 5m;                              0xB0163",
      "p + 1;                                0xB0163",
      "p % 2;                                0xB0163",
      "p * p;                                0xB0163",
      "2 / p;                                0xB0163",
      "tp * 2;                               0xB0163",
      "-tp;                                  0xA017D",
      "tp < dt;                              0xB0167",
      "p < 1d;                               0xB0167",
      "tp == dt;                             0xB0166",
  })
  public void testInvalidOperators(String expr, String expectedError) throws IOException {
    checkErrorExpr(expr, expectedError);
  }

  // library functions

  @ParameterizedTest
  @CsvSource(delimiter = ';', value = {
      "getHour(d\"12:30\");                           int",
      "getHour(2017-12-04T12:30);                    int",
      "getYear(d\"2017-12-04\");                      int",
      "getDayTime(2017-12-04T12:30);                 " + DAY_TIME,
      "withMonth(d\"2017-12-04\", 5);                 " + TIME_POINT,
      "withHour(d\"12:30\", 5);                       " + DAY_TIME,
      "withDays(d\"P1D\", 5);                         " + PERIOD,
      "getYears(minus(now(), d\"1987-06-05\"));       int",
      "minus(dt, 12:30 Uhr);                         " + PERIOD,
      "format(tp, \"yyyy\");                          String",
      "format(d\"P1D\");                              String",
      "withZone(utc(), \"Europe/Berlin\");            " + TIME_POINT,
      "abs(-d\"P1D\");                                " + PERIOD,
      "now() + 3 * 1s;                               " + TIME_POINT,
  })
  public void testFunctions(String expr, String expectedType) throws IOException {
    checkExpr(expr, expectedType);
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "getYear(d\"P1D\")",
      "getYear(d\"12:30\")",
      "withMonth(tp, \"May\")",
      "minus(tp, dt)",
  })
  public void testInvalidFunctionCalls(String expr) throws IOException {
    ASTExpression astExpr = parseExpr(expr);
    SymTypeExpression type = TypeCheck3.typeOf(astExpr);
    assertTrue(type.isObscureType(), "expected Obscure for expression \""
        + expr + "\" but got " + type.printFullName());
    MCAssertions.assertHasFinding();
  }

  // declarations

  @ParameterizedTest
  @ValueSource(strings = {
      "de.monticore.temporal.TimePoint t = d\"2017-12-04\";",
      "de.monticore.temporal.TimePoint t = 2017-12-04T12:30 + d\"P1M\";",
      "de.monticore.temporal.DayTime t = 12:30 Uhr;",
      "de.monticore.temporal.Period p = d\"P1D\" * 2;",
      "[s]<long> d = tp - 2017-12-01T00:00;",
      "[s]<double> d = 5.0min;",
  })
  public void testValidDeclaration(String decl) throws IOException {
    checkDeclaration(decl);
    MCAssertions.assertNoFindings();
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "de.monticore.temporal.DayTime t = d\"2017-12-04\";",
      "de.monticore.temporal.TimePoint t = d\"P1D\";",
      "de.monticore.temporal.Period p = 1d;",
      "[s]<long> d = d\"P1D\";",
  })
  public void testInvalidDeclaration(String decl) throws IOException {
    checkDeclaration(decl);
    MCAssertions.assertHasFindingStartingWith(
        VarDeclarationInitializationHasCorrectType.ERROR_CODE);
  }

  // setup of the types

  @Test
  public void testTemporalTypesResolvable() {
    assertEquals(TIME_POINT, TemporalSymTypeFactory.createTimePoint().printFullName());
    assertEquals(DAY_TIME, TemporalSymTypeFactory.createDayTime().printFullName());
    assertEquals(PERIOD, TemporalSymTypeFactory.createPeriod().printFullName());
    assertEquals("[s]<long>", TemporalSymTypeFactory.createDuration().printFullName());
    MCAssertions.assertNoFindings();
  }

  @Test
  public void testInitIsIdempotent() throws IOException {
    TemporalTypes.init();
    assertEquals(1, BasicSymbolsMill.globalScope()
        .resolveTypeMany(TemporalSymTypeRelations.TIME_POINT).size());
    assertEquals(2, BasicSymbolsMill.globalScope()
        .resolveFunctionMany("getHour").size());
    checkExpr("getHour(d\"12:30\")", "int");
  }

  @Test
  public void testMissingInit() {
    TemporalScriptMill.globalScope().clear();
    SymTypeExpression type = TemporalSymTypeFactory.createTimePoint();
    assertTrue(type.isObscureType());
    MCAssertions.assertHasFindingStartingWith("0xFDE01");
  }

  @Test
  public void testRelations() {
    SymTypeExpression timePoint = TemporalSymTypeFactory.createTimePoint();
    SymTypeExpression dayTime = TemporalSymTypeFactory.createDayTime();
    SymTypeExpression period = TemporalSymTypeFactory.createPeriod();
    SymTypeExpression duration = TemporalSymTypeFactory.createDuration();
    assertTrue(TemporalSymTypeRelations.isTimePoint(timePoint));
    assertTrue(TemporalSymTypeRelations.isDayTime(dayTime));
    assertTrue(TemporalSymTypeRelations.isPeriod(period));
    assertTrue(TemporalSymTypeRelations.isDuration(duration));
    assertTrue(TemporalSymTypeRelations.isInstant(timePoint));
    assertTrue(TemporalSymTypeRelations.isInstant(dayTime));
    assertTrue(!TemporalSymTypeRelations.isInstant(period));
    assertTrue(!TemporalSymTypeRelations.isDuration(period));
    assertTrue(!TemporalSymTypeRelations.isTimePoint(dayTime));
    MCAssertions.assertNoFindings();
  }

  // Helper

  protected ASTExpression parseExpr(String exprStr) throws IOException {
    Optional<ASTExpression> expr =
        TemporalScriptMill.parser().parse_StringExpression(exprStr);
    if (expr.isEmpty()) {
      return fail("unable to parse expression " + exprStr);
    }
    ASTScript script = TemporalScriptMill.scriptBuilder()
        .addMCBlockStatement(TemporalScriptMill.expressionStatementBuilder()
            .setExpression(expr.get())
            .build())
        .build();
    TemporalScriptMill.scopesGenitorDelegator().createFromAST(script)
        .setName("TestScript");
    return expr.get();
  }

  protected void checkExpr(String exprStr, String expectedType) throws IOException {
    ASTExpression expr = parseExpr(exprStr);
    MCAssertions.assertNoFindings();
    SymTypeExpression type = TypeCheck3.typeOf(expr);
    MCAssertions.assertNoFindings("Unexpected findings for expression " + exprStr);
    assertEquals(expectedType, type.printFullName(),
        "Wrong type for expression " + exprStr);
  }

  protected void checkErrorExpr(String exprStr, String expectedError) throws IOException {
    ASTExpression expr = parseExpr(exprStr);
    MCAssertions.assertNoFindings();
    SymTypeExpression type = TypeCheck3.typeOf(expr);
    assertTrue(type.isObscureType(), "expected Obscure for expression \""
        + exprStr + "\" but got " + type.printFullName());
    MCAssertions.assertHasFindingStartingWith(expectedError);
  }

  protected void checkDeclaration(String decl) throws IOException {
    Optional<ASTMCBlockStatement> stmt =
        TemporalScriptMill.parser().parse_StringMCBlockStatement(decl);
    if (stmt.isEmpty()) {
      fail("unable to parse declaration " + decl);
    }
    ASTScript script = TemporalScriptMill.scriptBuilder()
        .addMCBlockStatement(stmt.get())
        .build();
    TemporalScriptMill.scopesGenitorDelegator().createFromAST(script)
        .setName("TestScript");
    MCAssertions.assertNoFindings("after creating the scopes");
    TemporalScriptTraverser completer = TemporalScriptMill.traverser();
    completer.add4MCVarDeclarationStatements(new MCVarDeclarationStatementsSymTabCompletion());
    script.accept(completer);
    MCAssertions.assertNoFindings("after completing the symbol table");

    TemporalScriptCoCoChecker checker = new TemporalScriptCoCoChecker();
    checker.addCoCo(new VarDeclarationInitializationHasCorrectType());
    checker.checkAll(script);
  }

}
