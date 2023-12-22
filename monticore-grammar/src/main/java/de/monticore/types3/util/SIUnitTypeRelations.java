// (c) https://github.com/MontiCore/monticore
package de.monticore.types3.util;

import de.monticore.types.check.SIUnitBasic;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfNumericWithSIUnit;
import de.monticore.types.check.SymTypeOfSIUnit;
import de.monticore.types3.SymTypeRelations;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * calculates, e.g., whether a function can be called given specified arguments,
 * one may assume this functionality ought to be in SymTypeOfFunction,
 * however, it relies on other functionality in SymTypeRelations,
 * and the behaviour of SymTypeClasses
 * should not be dependent on the current type system
 * (or one would need to pass the SymTypeRelations to the SymTypes)
 * delegate of SymTypeRelations
 */
public class SIUnitTypeRelations {

  protected static SIUnitTypeRelations delegate;

  public static void init() {
    Log.trace("init default SIUnitRelations", "TypeCheck setup");
    SIUnitTypeRelations.delegate = new SIUnitTypeRelations();
  }

  static {
    init();
  }

  /**
   * returns a SymTypeOfSIUnit only consisting of the seven SI base units
   * (s, m, kg, A, K, mol, cd)
   * any prefixes are removed
   */
  public static SymTypeOfSIUnit convertToSIBaseUnits(SymTypeOfSIUnit siUnit) {
    return getSIUnitTypeRelations().calculateConvertToSIBaseUnits(siUnit);
  }

  protected SymTypeOfSIUnit calculateConvertToSIBaseUnits(SymTypeOfSIUnit siUnit) {

  }

  protected static final Map<String, List<SIUnitBasic>> conversionTable;

  static {
    Map<String, List<SIUnitBasic>> conversionTableTmp = new HashMap<>();
    // already base units:
    conversionTableTmp.put("m", List.of(SymTypeExpressionFactory.createSIUnitBasic("m")));
    conversionTableTmp.put("g", List.of(SymTypeExpressionFactory.createSIUnitBasic("g")));
    conversionTableTmp.put("s", List.of(SymTypeExpressionFactory.createSIUnitBasic("s")));
    conversionTableTmp.put("A", List.of(SymTypeExpressionFactory.createSIUnitBasic("A")));
    conversionTableTmp.put("K", List.of(SymTypeExpressionFactory.createSIUnitBasic("K")));
    conversionTableTmp.put("mol", List.of(SymTypeExpressionFactory.createSIUnitBasic("mol")));
    conversionTableTmp.put("cd", List.of(SymTypeExpressionFactory.createSIUnitBasic("cd")));
    // further bases:
    conversionTableTmp.put("Hz", List.of(SymTypeExpressionFactory.createSIUnitBasic("s", -1)));
    conversionTableTmp.put("N", List.of(
        SymTypeExpressionFactory.createSIUnitBasic("m"),
        SymTypeExpressionFactory.createSIUnitBasic("g"),
        SymTypeExpressionFactory.createSIUnitBasic("s", -2)
    ));
    conversionTableTmp.put("Pa", List.of(
        SymTypeExpressionFactory.createSIUnitBasic("m", -1),
        SymTypeExpressionFactory.createSIUnitBasic("g"),
        SymTypeExpressionFactory.createSIUnitBasic("s", -2)
    ));
    conversionTableTmp.put("J", List.of(
        SymTypeExpressionFactory.createSIUnitBasic("m", 2),
        SymTypeExpressionFactory.createSIUnitBasic("g"),
        SymTypeExpressionFactory.createSIUnitBasic("s", -2)
    ));
    conversionTableTmp.put("W", List.of(
        SymTypeExpressionFactory.createSIUnitBasic("m", 2),
        SymTypeExpressionFactory.createSIUnitBasic("g"),
        SymTypeExpressionFactory.createSIUnitBasic("s", -3)
    ));
    conversionTableTmp.put("C", List.of(
        SymTypeExpressionFactory.createSIUnitBasic("s"),
        SymTypeExpressionFactory.createSIUnitBasic("A")
    ));
    conversionTableTmp.put("V", List.of(
        SymTypeExpressionFactory.createSIUnitBasic("m", 2),
        SymTypeExpressionFactory.createSIUnitBasic("g"),
        SymTypeExpressionFactory.createSIUnitBasic("s", -3),
        SymTypeExpressionFactory.createSIUnitBasic("A", -1)
    ));
    conversionTableTmp.put("F", List.of(
        SymTypeExpressionFactory.createSIUnitBasic("m", -2),
        SymTypeExpressionFactory.createSIUnitBasic("g", -1),
        SymTypeExpressionFactory.createSIUnitBasic("s", 4),
        SymTypeExpressionFactory.createSIUnitBasic("A", 2)
    ));
    conversionTableTmp.put("Ohm", List.of(
        SymTypeExpressionFactory.createSIUnitBasic("m", 2),
        SymTypeExpressionFactory.createSIUnitBasic("g"),
        SymTypeExpressionFactory.createSIUnitBasic("s", -3),
        SymTypeExpressionFactory.createSIUnitBasic("A", -2)
    ));
    conversionTableTmp.put("Ω", List.of(
        SymTypeExpressionFactory.createSIUnitBasic("m", 2),
        SymTypeExpressionFactory.createSIUnitBasic("g"),
        SymTypeExpressionFactory.createSIUnitBasic("s", -3),
        SymTypeExpressionFactory.createSIUnitBasic("A", -2)
    ));
    conversionTableTmp.put("S", List.of(
        SymTypeExpressionFactory.createSIUnitBasic("m", -2),
        SymTypeExpressionFactory.createSIUnitBasic("g", -1),
        SymTypeExpressionFactory.createSIUnitBasic("s", 3),
        SymTypeExpressionFactory.createSIUnitBasic("A", 2)
    ));
    conversionTableTmp.put("Wb", List.of(
        SymTypeExpressionFactory.createSIUnitBasic("m", 2),
        SymTypeExpressionFactory.createSIUnitBasic("g"),
        SymTypeExpressionFactory.createSIUnitBasic("s", -2),
        SymTypeExpressionFactory.createSIUnitBasic("A", -1)
    ));
    conversionTableTmp.put("T", List.of(
        SymTypeExpressionFactory.createSIUnitBasic("g"),
        SymTypeExpressionFactory.createSIUnitBasic("s", -2),
        SymTypeExpressionFactory.createSIUnitBasic("A", -1)
    ));
    conversionTableTmp.put("H", List.of(
        SymTypeExpressionFactory.createSIUnitBasic("g"),
        SymTypeExpressionFactory.createSIUnitBasic("s", -2),
        SymTypeExpressionFactory.createSIUnitBasic("A", -1)
    ));
    conversionTableTmp.put("lm", List.of(SymTypeExpressionFactory.createSIUnitBasic("cd")));
    conversionTableTmp.put("lx", List.of(
        SymTypeExpressionFactory.createSIUnitBasic("m", -2),
        SymTypeExpressionFactory.createSIUnitBasic("cd")
    ));
    conversionTableTmp.put("Bq", List.of(SymTypeExpressionFactory.createSIUnitBasic("s", -1)));
    conversionTableTmp.put("Gy", List.of(
        SymTypeExpressionFactory.createSIUnitBasic("m", 2),
        SymTypeExpressionFactory.createSIUnitBasic("s", -2)
    ));
    conversionTableTmp.put("Sv", List.of(
        SymTypeExpressionFactory.createSIUnitBasic("m", 2),
        SymTypeExpressionFactory.createSIUnitBasic("m", -2)
    ));
    conversionTableTmp.put("kat", List.of(
        SymTypeExpressionFactory.createSIUnitBasic("s", -1),
        SymTypeExpressionFactory.createSIUnitBasic("mol")
    ));
    conversionTableTmp.put("l", List.of(SymTypeExpressionFactory.createSIUnitBasic("m", 3)));
    conversionTableTmp.put("L", List.of(SymTypeExpressionFactory.createSIUnitBasic("m", 3)));
    conversionTableTmp.put("min", List.of(SymTypeExpressionFactory.createSIUnitBasic("s")));
    conversionTableTmp.put("h", List.of(SymTypeExpressionFactory.createSIUnitBasic("s")));
    conversionTableTmp.put("d", List.of(SymTypeExpressionFactory.createSIUnitBasic("s")));
    conversionTableTmp.put("ha", List.of(SymTypeExpressionFactory.createSIUnitBasic("m", 2)));
    conversionTableTmp.put("t", List.of(SymTypeExpressionFactory.createSIUnitBasic("g")));
    conversionTableTmp.put("au", List.of(SymTypeExpressionFactory.createSIUnitBasic("m")));
    conversionTableTmp.put("eV", List.of(
        SymTypeExpressionFactory.createSIUnitBasic("m", 2),
        SymTypeExpressionFactory.createSIUnitBasic("g"),
        SymTypeExpressionFactory.createSIUnitBasic("s", -2)
    ));
    conversionTableTmp.put("Da", List.of(SymTypeExpressionFactory.createSIUnitBasic("g")));
    conversionTableTmp.put("u", List.of(SymTypeExpressionFactory.createSIUnitBasic("g")));
    conversionTableTmp.put("ºC", List.of(SymTypeExpressionFactory.createSIUnitBasic("K")));
    conversionTableTmp.put("ºF", List.of(SymTypeExpressionFactory.createSIUnitBasic("K")));
    conversionTableTmp.put("Np", List.of());
    conversionTableTmp.put("B", List.of());
    conversionTableTmp.put("dB", List.of());
    conversionTableTmp.put("°", List.of());
    conversionTableTmp.put("deg", List.of());
    conversionTableTmp.put("rad", List.of());
    conversionTableTmp.put("sr", List.of());

    conversionTable = Collections.unmodifiableMap(conversionTableTmp);
  }

  protected List<SIUnitBasic> calculateConvertToSIBaseUnits(
      SIUnitBasic unitBasic
  ) {
    if(!conversionTable.containsKey(unitBasic.getDimension())) {
      Log.error("0xFD510 tried to convert the unknown SI unit \""
      + unitBasic.getDimension()
          + "\" to the Si base units"
      );
      return Collections.emptyList();
    }

  }

  public static SymTypeOfSIUnit multiply(SymTypeOfSIUnit... siUnits) {
    return multiply(List.of(siUnits));
  }

  public static SymTypeOfSIUnit multiply(Collection<SymTypeOfSIUnit> siUnits) {
    return getSIUnitTypeRelations().calculateMultiply(siUnits);
  }

  protected SymTypeOfSIUnit calculateMultiply(
      Collection<SymTypeOfSIUnit> siUnits
  ) {
    List<SIUnitBasic> newNumerator = new ArrayList<>();
    List<SIUnitBasic> newDenominator = new ArrayList<>();
    for (SymTypeOfSIUnit siUnit : siUnits) {
      newNumerator.addAll(siUnit.getNumerator());
      newDenominator.addAll(siUnit.getDenominator());
    }
    return SymTypeExpressionFactory.createSIUnit(newNumerator, newDenominator);
  }

  public static SymTypeOfNumericWithSIUnit multiplyWithNumerics(
      SymTypeOfNumericWithSIUnit... numericWithSIUnits
  ) {
    return multiplyWithNumerics(List.of(numericWithSIUnits));
  }

  public static SymTypeOfNumericWithSIUnit multiplyWithNumerics(
      Collection<SymTypeOfNumericWithSIUnit> numericWithSIUnits
  ) {
    return getSIUnitTypeRelations()
        .calculateMultiplyWithNumerics(numericWithSIUnits);
  }

  protected SymTypeOfNumericWithSIUnit calculateMultiplyWithNumerics(
      Collection<SymTypeOfNumericWithSIUnit> numericWithSIUnits
  ) {
    List<SymTypeOfSIUnit> siUnits = numericWithSIUnits.stream()
        .map(SymTypeOfNumericWithSIUnit::getSIUnitType)
        .collect(Collectors.toList());
    List<SymTypeExpression> numerics = numericWithSIUnits.stream()
        .map(SymTypeOfNumericWithSIUnit::getNumericType)
        .collect(Collectors.toList());
    return SymTypeExpressionFactory.createNumericWithSIUnit(
        multiply(siUnits),
        SymTypeRelations.numericPromotion(numerics)
    );
  }

  public static SymTypeOfNumericWithSIUnit invert(
      SymTypeOfNumericWithSIUnit numericWithSIUnit) {
    return SymTypeExpressionFactory.createNumericWithSIUnit(
        invert(numericWithSIUnit.getSIUnitType()),
        numericWithSIUnit.getNumericType()
    );
  }

  public static SymTypeOfSIUnit invert(SymTypeOfSIUnit siUnit) {
    return getSIUnitTypeRelations().calculateInverse(siUnit);
  }

  protected SymTypeOfSIUnit calculateInverse(SymTypeOfSIUnit siUnit) {
    return SymTypeExpressionFactory.createSIUnit(
        siUnit.getDenominator(), siUnit.getNumerator()
    );
  }

  // helper

  protected static SIUnitTypeRelations getSIUnitTypeRelations() {
    if (delegate == null) {
      Log.error("0xFD9CD internal error: "
          + "SIUnitRelations were not init()-ialized."
      );
    }
    return delegate;
  }
}
