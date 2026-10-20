/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal.types3;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsArtifactScope;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsGlobalScope;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Adds the built-in temporal types and the temporal library functions
 * to the global scope, s.a. {@link BasicSymbolsMill#initializeString()}.
 * <p>
 * The types TimePoint, DayTime, and Period are added within the package
 * {@value TemporalSymTypeRelations#PACKAGE}.
 * The library functions (getters, withers, formatters, ...)
 * are added unqualified, e.g., {@code getYear(TimePoint): int}.
 * <p>
 * Like the primitives, these have to be re-added
 * after every reset of the global scope.
 */
public class TemporalTypes {

  /**
   * The artifact scope containing the temporal types.
   */
  public static final String ARTIFACT_NAME = "TemporalTypes";

  // short names used in the signature table
  protected static final String T = "TimePoint";
  protected static final String D = "DayTime";
  protected static final String P = "Period";
  protected static final String INT = BasicSymbolsMill.INT;
  protected static final String STR = BasicSymbolsMill.STRING;

  /**
   * The signatures of the temporal library functions,
   * each given as {name, return type, parameter types...}.
   * s. B.2 of the documentation of the temporal operations.
   */
  protected static final String[][] FUNCTIONS = {
      // getters for instant types
      {"getDayTime", D, T},
      {"getCentury", INT, T},
      {"getDecade", INT, T},
      {"getYear", INT, T},
      {"getWeekYear", INT, T},
      {"getMonth", INT, T},
      {"getWeek", INT, T},
      {"getDay", INT, T},
      {"getDayOfWeek", INT, T},
      {"getDayOfYear", INT, T},
      {"getAmPm", INT, D}, {"getAmPm", INT, T},
      {"getClockHour", INT, D}, {"getClockHour", INT, T},
      {"getHour", INT, D}, {"getHour", INT, T},
      {"getMinute", INT, D}, {"getMinute", INT, T},
      {"getSecond", INT, D}, {"getSecond", INT, T},
      {"getMillisecond", INT, D}, {"getMillisecond", INT, T},
      {"getNanosecond", INT, D}, {"getNanosecond", INT, T},
      {"getOffset", INT, D}, {"getOffset", INT, T},
      {"getOffsetHour", INT, D}, {"getOffsetHour", INT, T},
      {"getOffsetMinute", INT, D}, {"getOffsetMinute", INT, T},
      {"getZone", STR, D}, {"getZone", STR, T},
      // withers for instant types
      {"withDayTime", T, T, D},
      {"withCentury", T, T, INT},
      {"withDecade", T, T, INT},
      {"withYear", T, T, INT},
      {"withWeekYear", T, T, INT},
      {"withMonth", T, T, INT},
      {"withWeek", T, T, INT},
      {"withDay", T, T, INT},
      {"withDayOfWeek", T, T, INT},
      {"withDayOfYear", T, T, INT},
      {"withAmPm", D, D, INT}, {"withAmPm", T, T, INT},
      {"withClockHour", D, D, INT}, {"withClockHour", T, T, INT},
      {"withHour", D, D, INT}, {"withHour", T, T, INT},
      {"withMinute", D, D, INT}, {"withMinute", T, T, INT},
      {"withSecond", D, D, INT}, {"withSecond", T, T, INT},
      {"withMillisecond", D, D, INT}, {"withMillisecond", T, T, INT},
      {"withNanosecond", D, D, INT}, {"withNanosecond", T, T, INT},
      {"withZone", D, D, STR}, {"withZone", T, T, STR},
      {"adjustToZone", D, D, STR}, {"adjustToZone", T, T, STR},
      // getters for periods
      {"getYears", INT, P},
      {"getMonths", INT, P},
      {"getWeeks", INT, P},
      {"getWeekDays", INT, P},
      {"getDays", INT, P},
      {"getHours", INT, P},
      {"getMinutes", INT, P},
      {"getSeconds", INT, P},
      {"getMilliseconds", INT, P},
      {"getNanoseconds", INT, P},
      // withers for periods
      {"withYears", P, P, INT},
      {"withMonths", P, P, INT},
      {"withWeeks", P, P, INT},
      {"withWeekDays", P, P, INT},
      {"withDays", P, P, INT},
      {"withHours", P, P, INT},
      {"withMinutes", P, P, INT},
      {"withSeconds", P, P, INT},
      {"withMilliseconds", P, P, INT},
      {"withNanoseconds", P, P, INT},
      // formatters for instant types
      {"format", STR, D}, {"format", STR, T},
      {"format", STR, D, STR}, {"format", STR, T, STR},
      {"formatDate", STR, T},
      {"formatDateTime", STR, T},
      {"formatOrdinalDate", STR, T},
      {"formatWeekDate", STR, T},
      {"formatOffsetDateTime", STR, T},
      {"formatTime", STR, D}, {"formatTime", STR, T},
      {"formatOffsetTime", STR, D}, {"formatOffsetTime", STR, T},
      {"formatZoned", STR, D}, {"formatZoned", STR, T},
      {"formatDE", STR, D}, {"formatDE", STR, T},
      {"formatDEDate", STR, T},
      {"formatDEAlnumDate", STR, T},
      {"formatDETime", STR, D}, {"formatDETime", STR, T},
      {"formatUS", STR, D}, {"formatUS", STR, T},
      {"formatUSDate", STR, T},
      {"formatUSAlnumDate", STR, T},
      {"formatUSTime", STR, D}, {"formatUSTime", STR, T},
      // formatters for periods
      {"format", STR, P},
      {"formatNormalized", STR, P},
      {"formatWeeks", STR, P},
      // miscellaneous
      {"minus", P, D, D}, {"minus", P, T, T},
      {"abs", P, P},
      {"now", T},
      {"utc", T},
  };

  /**
   * Adds the temporal types and functions to the global scope.
   * Does nothing if they have already been added.
   * Adds the primitives and String if they are missing.
   */
  public static void init() {
    IBasicSymbolsGlobalScope gs = BasicSymbolsMill.globalScope();
    if (gs.resolveType(TemporalSymTypeRelations.TIME_POINT).isPresent()) {
      return;
    }
    if (gs.resolveType(BasicSymbolsMill.INT).isEmpty()) {
      BasicSymbolsMill.initializePrimitives();
    }
    if (gs.resolveType(BasicSymbolsMill.STRING).isEmpty()) {
      BasicSymbolsMill.initializeString();
    }

    IBasicSymbolsArtifactScope as = BasicSymbolsMill.artifactScope();
    as.setName(ARTIFACT_NAME);
    as.setPackageName(TemporalSymTypeRelations.PACKAGE);
    as.setImportsList(new ArrayList<>());
    as.setEnclosingScope(gs);
    addType(as, TemporalSymTypeRelations.TIME_POINT_NAME);
    addType(as, TemporalSymTypeRelations.DAY_TIME_NAME);
    addType(as, TemporalSymTypeRelations.PERIOD_NAME);

    for (String[] signature : FUNCTIONS) {
      addFunction(gs, signature);
    }
  }

  // Helper

  protected static void addType(IBasicSymbolsArtifactScope as, String name) {
    IBasicSymbolsScope spannedScope = BasicSymbolsMill.scope();
    TypeSymbol type = BasicSymbolsMill.typeSymbolBuilder()
        .setName(name)
        .setFullName(TemporalSymTypeRelations.PACKAGE + "." + name)
        .setPackageName(TemporalSymTypeRelations.PACKAGE)
        .setEnclosingScope(as)
        .setSpannedScope(spannedScope)
        .setAccessModifier(AccessModifier.ALL_INCLUSION)
        .build();
    spannedScope.setEnclosingScope(as);
    as.add(type);
  }

  protected static void addFunction(IBasicSymbolsGlobalScope gs, String[] signature) {
    IBasicSymbolsScope spannedScope = BasicSymbolsMill.scope();
    spannedScope.setOrdered(true);
    spannedScope.setShadowing(true);
    for (int i = 2; i < signature.length; i++) {
      spannedScope.add(BasicSymbolsMill.variableSymbolBuilder()
          .setName("arg" + (i - 2))
          .setFullName("arg" + (i - 2))
          .setType(createType(signature[i]))
          .setEnclosingScope(spannedScope)
          .setAccessModifier(AccessModifier.ALL_INCLUSION)
          .build()
      );
    }
    FunctionSymbol function = BasicSymbolsMill.functionSymbolBuilder()
        .setName(signature[0])
        .setFullName(signature[0])
        .setType(createType(signature[1]))
        .setEnclosingScope(gs)
        .setSpannedScope(spannedScope)
        .setAccessModifier(AccessModifier.ALL_INCLUSION)
        .setIsElliptic(false)
        .build();
    spannedScope.setEnclosingScope(gs);
    gs.add(function);
  }

  protected static SymTypeExpression createType(String name) {
    switch (name) {
      case T:
        return TemporalSymTypeFactory.createTimePoint();
      case D:
        return TemporalSymTypeFactory.createDayTime();
      case P:
        return TemporalSymTypeFactory.createPeriod();
      case STR:
        return SymTypeExpressionFactory.createTypeObject(
            BasicSymbolsMill.globalScope().resolveType(STR).get());
      default:
        return SymTypeExpressionFactory.createPrimitive(name);
    }
  }

  /**
   * @return the signatures of the temporal library functions,
   *     each as {name, return type, parameter types...}
   */
  public static List<List<String>> getFunctionSignatures() {
    List<List<String>> result = new ArrayList<>();
    for (String[] signature : FUNCTIONS) {
      result.add(List.of(signature));
    }
    return result;
  }

}
