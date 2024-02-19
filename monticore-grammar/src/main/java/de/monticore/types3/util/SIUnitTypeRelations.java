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
 * calculates, e.g., the product or inverse of SymTypeOfSIUnits
 * one may assume this functionality ought to be in SymTypeOfSIUnit,
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

  // initializes this delegate
  static {
    init();
  }

  /**
   * List of the base units (without prefixes)
   */
  protected static final List<String> baseUnitStrings =
      List.of("s", "m", "g", "A", "K", "mol", "cd");

  /**
   * to convert to base units, e.g.,
   * Hz -> s^-1
   * J -> m^2*g*s^-2
   */
  protected static final Map<String, List<SIUnitBasic>> conversionTable;

  // initializes the conversion table
  static {
    Map<String, List<SIUnitBasic>> conversionTableTmp = new HashMap<>();
    // already base units:
    conversionTableTmp.put("m", List.of(createSIBaseUnit("m")));
    conversionTableTmp.put("g", List.of(createSIBaseUnit("g")));
    conversionTableTmp.put("s", List.of(createSIBaseUnit("s")));
    conversionTableTmp.put("A", List.of(createSIBaseUnit("A")));
    conversionTableTmp.put("K", List.of(createSIBaseUnit("K")));
    conversionTableTmp.put("mol", List.of(createSIBaseUnit("mol")));
    conversionTableTmp.put("cd", List.of(createSIBaseUnit("cd")));
    // further bases:
    conversionTableTmp.put("Hz", List.of(createSIBaseUnit("s", -1)));
    conversionTableTmp.put("N", List.of(
        createSIBaseUnit("m"),
        createSIBaseUnit("g"),
        createSIBaseUnit("s", -2)
    ));
    conversionTableTmp.put("Pa", List.of(
        createSIBaseUnit("m", -1),
        createSIBaseUnit("g"),
        createSIBaseUnit("s", -2)
    ));
    conversionTableTmp.put("J", List.of(
        createSIBaseUnit("m", 2),
        createSIBaseUnit("g"),
        createSIBaseUnit("s", -2)
    ));
    conversionTableTmp.put("W", List.of(
        createSIBaseUnit("m", 2),
        createSIBaseUnit("g"),
        createSIBaseUnit("s", -3)
    ));
    conversionTableTmp.put("C", List.of(
        createSIBaseUnit("s"),
        createSIBaseUnit("A")
    ));
    conversionTableTmp.put("V", List.of(
        createSIBaseUnit("m", 2),
        createSIBaseUnit("g"),
        createSIBaseUnit("s", -3),
        createSIBaseUnit("A", -1)
    ));
    conversionTableTmp.put("F", List.of(
        createSIBaseUnit("m", -2),
        createSIBaseUnit("g", -1),
        createSIBaseUnit("s", 4),
        createSIBaseUnit("A", 2)
    ));
    conversionTableTmp.put("Ohm", List.of(
        createSIBaseUnit("m", 2),
        createSIBaseUnit("g"),
        createSIBaseUnit("s", -3),
        createSIBaseUnit("A", -2)
    ));
    conversionTableTmp.put("Ω", List.of(
        createSIBaseUnit("m", 2),
        createSIBaseUnit("g"),
        createSIBaseUnit("s", -3),
        createSIBaseUnit("A", -2)
    ));
    conversionTableTmp.put("S", List.of(
        createSIBaseUnit("m", -2),
        createSIBaseUnit("g", -1),
        createSIBaseUnit("s", 3),
        createSIBaseUnit("A", 2)
    ));
    conversionTableTmp.put("Wb", List.of(
        createSIBaseUnit("m", 2),
        createSIBaseUnit("g"),
        createSIBaseUnit("s", -2),
        createSIBaseUnit("A", -1)
    ));
    conversionTableTmp.put("T", List.of(
        createSIBaseUnit("g"),
        createSIBaseUnit("s", -2),
        createSIBaseUnit("A", -1)
    ));
    conversionTableTmp.put("H", List.of(
        createSIBaseUnit("m", 2),
        createSIBaseUnit("g"),
        createSIBaseUnit("s", -2),
        createSIBaseUnit("A", -2)
    ));
    conversionTableTmp.put("lm", List.of(createSIBaseUnit("cd")));
    conversionTableTmp.put("lx", List.of(
        createSIBaseUnit("m", -2),
        createSIBaseUnit("cd")
    ));
    conversionTableTmp.put("Bq", List.of(createSIBaseUnit("s", -1)));
    conversionTableTmp.put("Gy", List.of(
        createSIBaseUnit("m", 2),
        createSIBaseUnit("s", -2)
    ));
    conversionTableTmp.put("Sv", List.of(
        createSIBaseUnit("m", 2),
        createSIBaseUnit("s", -2)
    ));
    conversionTableTmp.put("kat", List.of(
        createSIBaseUnit("s", -1),
        createSIBaseUnit("mol")
    ));
    conversionTableTmp.put("l", List.of(createSIBaseUnit("m", 3)));
    conversionTableTmp.put("L", List.of(createSIBaseUnit("m", 3)));
    conversionTableTmp.put("min", List.of(createSIBaseUnit("s")));
    conversionTableTmp.put("h", List.of(createSIBaseUnit("s")));
    conversionTableTmp.put("d", List.of(createSIBaseUnit("s")));
    conversionTableTmp.put("ha", List.of(createSIBaseUnit("m", 2)));
    conversionTableTmp.put("t", List.of(createSIBaseUnit("g")));
    conversionTableTmp.put("au", List.of(createSIBaseUnit("m")));
    conversionTableTmp.put("eV", List.of(
        createSIBaseUnit("m", 2),
        createSIBaseUnit("g"),
        createSIBaseUnit("s", -2)
    ));
    conversionTableTmp.put("Da", List.of(createSIBaseUnit("g")));
    conversionTableTmp.put("u", List.of(createSIBaseUnit("g")));
    conversionTableTmp.put("ºC", List.of(createSIBaseUnit("K")));
    conversionTableTmp.put("ºF", List.of(createSIBaseUnit("K")));
    conversionTableTmp.put("Np", List.of());
    conversionTableTmp.put("B", List.of());
    conversionTableTmp.put("dB", List.of());
    conversionTableTmp.put("°", List.of());
    conversionTableTmp.put("deg", List.of());
    conversionTableTmp.put("rad", List.of());
    conversionTableTmp.put("sr", List.of());

    conversionTable = Collections.unmodifiableMap(conversionTableTmp);
  }

  /**
   * whether this is of dimension 1,
   * s. DIN EN ISO 80000-1:2023-08 (chap. 5)
   * e.g.: m/m,º
   */
  public static boolean isOfDimensionOne(SymTypeOfSIUnit siUnit) {
    return getSIUnitTypeRelations().calculateIsOfDimensionOne(siUnit);
  }

  protected boolean calculateIsOfDimensionOne(SymTypeOfSIUnit siUnit) {
    SymTypeOfSIUnit siUnitNormalized = internal_normalize(siUnit);
    return siUnitNormalized.getNumerator().size() == 0 &&
        siUnitNormalized.getDenominator().size() == 0;
  }

  /**
   * returns a SymTypeOfSIUnit only consisting of the seven SI base units
   * (s, m, kg, A, K, mol, cd)
   * any prefixes are removed (except "k" of kg)
   * Additionally, only one of each base unit exists in the SymType, e.g.,
   * kg^2*m*kg -> kg^3*m
   * and every exponent is positive, e.g.,
   * kg^-2*m^0*s/K^-2 -> s*K^2/kg^2
   * <p/>
   * this is implemented here (instead of the normalize visitor),
   * as it requires a lot of domain-specific knowledge / calculations.
   */
  public static SymTypeOfSIUnit internal_normalize(SymTypeOfSIUnit siUnit) {
    return getSIUnitTypeRelations().calculateNormalize(siUnit);
  }

  protected SymTypeOfSIUnit calculateNormalize(SymTypeOfSIUnit siUnit) {
    SymTypeOfSIUnit siUnitWithBaseUnits = convertToSIBaseUnits(siUnit);
    // collect all exponents
    Map<String, Integer> unit2Exp = new HashMap<>();
    for (String dimension : baseUnitStrings) {
      unit2Exp.put(dimension, 0);
    }
    for (SIUnitBasic siUnitBasic : siUnitWithBaseUnits.getNumerator()) {
      if (unit2Exp.containsKey(siUnitBasic.getDimension())) {
        unit2Exp.put(
            siUnitBasic.getDimension(),
            unit2Exp.get(siUnitBasic.getDimension()) + siUnitBasic.getExponent()
        );
      }
      else {
        Log.error("0xFD511 internal error: "
            + "expected an SI base unit (s, m, kg, A, K, mol, cd), but got \""
            + siUnitBasic.getDimension()
        );
      }
    }
    for (SIUnitBasic siUnitBasic : siUnitWithBaseUnits.getDenominator()) {
      if (unit2Exp.containsKey(siUnitBasic.getDimension())) {
        unit2Exp.put(
            siUnitBasic.getDimension(),
            unit2Exp.get(siUnitBasic.getDimension()) - siUnitBasic.getExponent()
        );
      }
      else {
        Log.error("0xFD512 internal error: "
            + "expected an SI base unit (s, m, kg, A, K, mol, cd), but got \""
            + siUnitBasic.getDimension()
        );
      }
    }

    // use the exponents to create a new SymTypeOfSiUnit
    // we require a deterministic order
    List<SIUnitBasic> numerator = new ArrayList<>();
    List<SIUnitBasic> denominator = new ArrayList<>();
    for (String dimension : baseUnitStrings) {
      if (unit2Exp.get(dimension) > 0) {
        numerator.add(createSIBaseUnit(dimension, unit2Exp.get(dimension)));
      }
      else if (unit2Exp.get(dimension) < 0) {
        denominator.add(createSIBaseUnit(dimension, -unit2Exp.get(dimension)));
      }
    }

    return SymTypeExpressionFactory.createSIUnit(numerator, denominator);
  }

  /**
   * returns a SymTypeOfSIUnit only consisting of the seven SI base units
   * (s, m, kg, A, K, mol, cd)
   * any prefixes are removed (except "k" of kg)
   */
  protected static SymTypeOfSIUnit convertToSIBaseUnits(SymTypeOfSIUnit siUnit) {
    return getSIUnitTypeRelations().calculateConvertToSIBaseUnits(siUnit);
  }

  protected SymTypeOfSIUnit calculateConvertToSIBaseUnits(SymTypeOfSIUnit siUnit) {
    List<SIUnitBasic> numerator = siUnit.getNumerator().stream()
        .flatMap(unitBasic -> convertToSIBaseUnits(unitBasic).stream())
        .collect(Collectors.toList());
    List<SIUnitBasic> denominator = siUnit.getDenominator().stream()
        .flatMap(unitBasic -> convertToSIBaseUnits(unitBasic).stream())
        .collect(Collectors.toList());
    return SymTypeExpressionFactory.createSIUnit(numerator, denominator);
  }

  protected static List<SIUnitBasic> convertToSIBaseUnits(SIUnitBasic unitBasic) {
    return getSIUnitTypeRelations().calculateConvertToSIBaseUnits(unitBasic);
  }

  protected List<SIUnitBasic> calculateConvertToSIBaseUnits(
      SIUnitBasic unitBasic
  ) {
    List<SIUnitBasic> converted;
    // load the conversion for exponent == 1
    if (!conversionTable.containsKey(unitBasic.getDimension())) {
      Log.error("0xFD510 tried to convert the unknown SI unit \""
          + unitBasic.getDimension()
          + "\" to the Si base units"
      );
      converted = Collections.emptyList();
    }
    else {
      converted = conversionTable.get(unitBasic.getDimension()).stream()
          .map(SIUnitBasic::deepClone)
          .collect(Collectors.toList());
    }
    // multiply the exponents
    for (SIUnitBasic convBasic : converted) {
      convBasic.setExponent(convBasic.getExponent() * unitBasic.getExponent());
    }
    return converted;
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
    return getSIUnitTypeRelations().calculateInvert(siUnit);
  }

  protected SymTypeOfSIUnit calculateInvert(SymTypeOfSIUnit siUnit) {
    return SymTypeExpressionFactory.createSIUnit(
        siUnit.getDenominator(), siUnit.getNumerator()
    );
  }

  // helper

  protected static SIUnitBasic createSIBaseUnit(String dimension) {
    return createSIBaseUnit(dimension, 1);
  }

  protected static SIUnitBasic createSIBaseUnit(String dimension, int exponent) {
    if (dimension.equals("g")) {
      return SymTypeExpressionFactory.createSIUnitBasic("g", "k", exponent);
    }
    else {
      return SymTypeExpressionFactory.createSIUnitBasic(dimension, exponent);
    }
  }

  protected static SIUnitTypeRelations getSIUnitTypeRelations() {
    if (delegate == null) {
      Log.error("0xFD9CD internal error: "
          + "SIUnitRelations were not init()-ialized."
      );
    }
    return delegate;
  }
}
