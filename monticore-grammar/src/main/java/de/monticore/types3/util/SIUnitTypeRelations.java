// (c) https://github.com/MontiCore/monticore
package de.monticore.types3.util;

import com.google.common.base.Preconditions;
import de.monticore.types.check.SIUnitBasic;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfNumericWithSIUnit;
import de.monticore.types.check.SymTypeOfSIUnit;
import de.monticore.types3.SymTypeRelations;
import de.se_rwth.commons.logging.Log;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
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

  /**
   * List of the base units (without prefixes)
   */
  protected static final List<String> baseUnitStrings =
      List.of("s", "m", "g", "A", "K", "mol", "cd");

  /**
   * to convert to base units, e.g.,
   * {@code Hz -> s^-1}
   * {@code J -> m^2*g*s^-2}
   */
  protected static final Map<String, List<SIUnitBasic>> conversionTable;

  // initializes the conversion table
  static {
    Map<String, List<SIUnitBasic>> conversionTableTmp = new LinkedHashMap<>();
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
   * factors of the prefixes, e.g., {@code k -> 1000}
   */
  protected static final Map<String, BigDecimal> prefixFactors = Map.ofEntries(
      Map.entry("Y", BigDecimal.TEN.pow(24)),
      Map.entry("Z", BigDecimal.TEN.pow(21)),
      Map.entry("E", BigDecimal.TEN.pow(18)),
      Map.entry("P", BigDecimal.TEN.pow(15)),
      Map.entry("T", BigDecimal.TEN.pow(12)),
      Map.entry("G", BigDecimal.TEN.pow(9)),
      Map.entry("M", BigDecimal.TEN.pow(6)),
      Map.entry("k", BigDecimal.TEN.pow(3)),
      Map.entry("h", BigDecimal.TEN.pow(2)),
      Map.entry("da", BigDecimal.TEN),
      Map.entry("", BigDecimal.ONE),
      Map.entry("d", BigDecimal.ONE.scaleByPowerOfTen(-1)),
      Map.entry("c", BigDecimal.ONE.scaleByPowerOfTen(-2)),
      Map.entry("m", BigDecimal.ONE.scaleByPowerOfTen(-3)),
      Map.entry("u", BigDecimal.ONE.scaleByPowerOfTen(-6)),
      Map.entry("µ", BigDecimal.ONE.scaleByPowerOfTen(-6)),
      Map.entry("n", BigDecimal.ONE.scaleByPowerOfTen(-9)),
      Map.entry("p", BigDecimal.ONE.scaleByPowerOfTen(-12)),
      Map.entry("f", BigDecimal.ONE.scaleByPowerOfTen(-15)),
      Map.entry("a", BigDecimal.ONE.scaleByPowerOfTen(-18)),
      Map.entry("z", BigDecimal.ONE.scaleByPowerOfTen(-21)),
      Map.entry("y", BigDecimal.ONE.scaleByPowerOfTen(-24))
  );

  /**
   * factors to convert to SI base units, e.g., {@code h -> 3600} (s);
   * does not contain, e.g., ºC (affine), dB (logarithmic), deg (angle)
   */
  protected static final Map<String, BigDecimal> unitFactors;

  static {
    Map<String, BigDecimal> unitFactorsTmp = new LinkedHashMap<>();
    for (String coherentUnit : List.of(
        "m", "s", "A", "K", "mol", "cd",
        "Hz", "N", "Pa", "J", "W", "C", "V", "F", "Ohm", "Ω", "S",
        "Wb", "T", "H", "lm", "lx", "Bq", "Gy", "Sv", "kat", "rad", "sr"
    )) {
      unitFactorsTmp.put(coherentUnit, BigDecimal.ONE);
    }
    unitFactorsTmp.put("g", new BigDecimal("0.001"));
    unitFactorsTmp.put("l", new BigDecimal("0.001"));
    unitFactorsTmp.put("L", new BigDecimal("0.001"));
    unitFactorsTmp.put("min", new BigDecimal("60"));
    unitFactorsTmp.put("h", new BigDecimal("3600"));
    unitFactorsTmp.put("d", new BigDecimal("86400"));
    unitFactorsTmp.put("ha", new BigDecimal("10000"));
    unitFactorsTmp.put("t", new BigDecimal("1000"));
    unitFactorsTmp.put("au", new BigDecimal("149597870700"));
    unitFactorsTmp.put("eV", new BigDecimal("1.602176634E-19"));
    unitFactorsTmp.put("Da", new BigDecimal("1.66053906660E-27"));
    unitFactorsTmp.put("u", new BigDecimal("1.66053906660E-27"));
    unitFactors = Collections.unmodifiableMap(unitFactorsTmp);
  }

  /**
   * order of units (ignoring the exponents) used for normalization:
   * SI base units first, then the other units of the conversion table;
   * for the same unit, the larger prefixes come first.
   */
  protected static final Comparator<SIUnitBasic> unitOrder = createUnitOrder();

  protected static Comparator<SIUnitBasic> createUnitOrder() {
    Map<String, Integer> dimension2Index = new LinkedHashMap<>();
    for (String dimension : baseUnitStrings) {
      dimension2Index.putIfAbsent(dimension, dimension2Index.size());
    }
    for (String dimension : conversionTable.keySet()) {
      dimension2Index.putIfAbsent(dimension, dimension2Index.size());
    }
    Comparator<String> byDimension = Comparator
        .<String>comparingInt(dimension ->
            dimension2Index.getOrDefault(dimension, dimension2Index.size()))
        .thenComparing(Comparator.naturalOrder());
    Comparator<String> byPrefix = Comparator
        .<String, BigDecimal>comparing(
            prefix -> prefixFactors.getOrDefault(prefix, BigDecimal.ZERO),
            Comparator.reverseOrder()
        )
        .thenComparing(Comparator.naturalOrder());
    return Comparator.comparing(SIUnitBasic::getDimension, byDimension)
        .thenComparing(SIUnitBasic::getPrefix, byPrefix);
  }

  // methods

  /**
   * whether this is of dimension 1,
   * s. DIN EN ISO 80000-1:2023-08 (chap. 5)
   * e.g.: m/m,º
   */
  public static boolean isOfDimensionOne(SymTypeOfSIUnit siUnit) {
    return getDelegate()._isOfDimensionOne(siUnit);
  }

  protected boolean _isOfDimensionOne(SymTypeOfSIUnit siUnit) {
    SymTypeOfSIUnit siUnitNormalized = normalizeDimension(siUnit);
    return siUnitNormalized.getNumerator().isEmpty() && siUnitNormalized.getDenominator().isEmpty();
  }

  /**
   * whether both SIUnits have the same dimension, e.g., km and m,
   * s. DIN EN ISO 80000-1:2023-08 (chap. 5)
   */
  public static boolean isOfSameDimension(
      SymTypeOfSIUnit siUnitA,
      SymTypeOfSIUnit siUnitB
  ) {
    return getDelegate()._isOfSameDimension(siUnitA, siUnitB);
  }

  protected boolean _isOfSameDimension(
      SymTypeOfSIUnit siUnitA,
      SymTypeOfSIUnit siUnitB
  ) {
    return isOfDimensionOne(multiply(siUnitA, invert(siUnitB)));
  }

  /**
   * normalizes the representation of the SIUnit
   * without changing the units, as they represent different values, e.g.,
   * {@code m*km*s^-1/m -> km/s}, but km is not converted to m.
   * Only one of each unit exists in the SymType,
   * every exponent is positive,
   * and the order is deterministic (SI base units first).
   * <p>
   * s.a. {@link #normalizeDimension(SymTypeOfSIUnit)}
   */
  public static SymTypeOfSIUnit normalize(SymTypeOfSIUnit siUnit) {
    return getDelegate()._normalize(siUnit);
  }

  protected SymTypeOfSIUnit _normalize(SymTypeOfSIUnit siUnit) {
    Map<SIUnitBasic, Integer> unit2Exp = new TreeMap<>(unitOrder);
    for (SIUnitBasic siUnitBasic : siUnit.getNumerator()) {
      unit2Exp.merge(siUnitBasic, siUnitBasic.getExponent(), Integer::sum);
    }
    for (SIUnitBasic siUnitBasic : siUnit.getDenominator()) {
      unit2Exp.merge(siUnitBasic, -siUnitBasic.getExponent(), Integer::sum);
    }
    List<SIUnitBasic> numerator = new ArrayList<>();
    List<SIUnitBasic> denominator = new ArrayList<>();
    for (Map.Entry<SIUnitBasic, Integer> entry : unit2Exp.entrySet()) {
      SIUnitBasic unit = entry.getKey();
      if (entry.getValue() > 0) {
        numerator.add(SymTypeExpressionFactory.createSIUnitBasic(
            unit.getDimension(), unit.getPrefix(), entry.getValue()));
      }
      else if (entry.getValue() < 0) {
        denominator.add(SymTypeExpressionFactory.createSIUnitBasic(
            unit.getDimension(), unit.getPrefix(), -entry.getValue()));
      }
    }
    return SymTypeExpressionFactory.createSIUnit(numerator, denominator);
  }

  /**
   * returns a SymTypeOfSIUnit only consisting of the seven SI base units
   * (s, m, kg, A, K, mol, cd)
   * any prefixes are removed (except "k" of kg)
   * Additionally, only one of each base unit exists in the SymType, e.g.,
   * {@code kg^2*m*kg -> kg^3*m}
   * and every exponent is positive, e.g.,
   * {@code kg^-2*m^0*s/K^-2 -> s*K^2/kg^2}
   * <p>
   * This removes information about the value,
   * e.g., km and m have the same dimension, but 1km != 1m.
   * s.a. {@link #normalize(SymTypeOfSIUnit)}
   * <p>
   * this is implemented here (instead of the normalize visitor),
   * as it requires a lot of domain-specific knowledge / calculations.
   */
  public static SymTypeOfSIUnit normalizeDimension(SymTypeOfSIUnit siUnit) {
    return getDelegate()._normalizeDimension(siUnit);
  }

  /**
   * @deprecated use {@link #normalizeDimension(SymTypeOfSIUnit)}
   */
  @Deprecated(forRemoval = true)
  public static SymTypeOfSIUnit internal_normalize(SymTypeOfSIUnit siUnit) {
    return normalizeDimension(siUnit);
  }

  protected SymTypeOfSIUnit _normalizeDimension(SymTypeOfSIUnit siUnit) {
    SymTypeOfSIUnit siUnitWithBaseUnits = convertToSIBaseUnits(siUnit);
    // collect all exponents
    Map<String, Integer> unit2Exp = new LinkedHashMap<>();
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
    return getDelegate()._convertToSIBaseUnits(siUnit);
  }

  protected SymTypeOfSIUnit _convertToSIBaseUnits(SymTypeOfSIUnit siUnit) {
    List<SIUnitBasic> numerator = siUnit.getNumerator().stream()
        .flatMap(unitBasic -> convertToSIBaseUnits(unitBasic).stream())
        .collect(Collectors.toList());
    List<SIUnitBasic> denominator = siUnit.getDenominator().stream()
        .flatMap(unitBasic -> convertToSIBaseUnits(unitBasic).stream())
        .collect(Collectors.toList());
    return SymTypeExpressionFactory.createSIUnit(numerator, denominator);
  }

  protected static List<SIUnitBasic> convertToSIBaseUnits(SIUnitBasic unitBasic) {
    return getDelegate()._convertToSIBaseUnits(unitBasic);
  }

  protected List<SIUnitBasic> _convertToSIBaseUnits(
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
    return getDelegate()._multiply(siUnits);
  }

  protected SymTypeOfSIUnit _multiply(
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

  /**
   * the unit of the result of a multiplication, e.g.,
   * {@code km * s -> s*km}, {@code km/h * h -> km}.
   * If different units of the same dimension are multiplied,
   * the result is converted to SI base units
   * with engineering prefixes (powers of 1000), e.g.,
   * {@code km * m -> m^2}, {@code km^2 * 1/m -> Mm}.
   * Any remaining factor is part of the value.
   */
  public static SymTypeOfSIUnit multiplyForOperation(
      SymTypeOfSIUnit left,
      SymTypeOfSIUnit right
  ) {
    return getDelegate()._multiplyForOperation(left, right);
  }

  protected SymTypeOfSIUnit _multiplyForOperation(
      SymTypeOfSIUnit left,
      SymTypeOfSIUnit right
  ) {
    SymTypeOfSIUnit product = normalize(multiply(left, right));
    if (!containsDifferentUnitsOfSameDimension(product)) {
      return product;
    }
    SymTypeOfSIUnit dimension = normalizeDimension(product);
    Optional<BigDecimal> factor = getConversionFactor(product, dimension);
    if (factor.isEmpty()) {
      return product;
    }
    int exponent = factor.get().precision() - factor.get().scale() - 1;
    return normalize(applyEngineeringPrefixes(dimension, exponent));
  }

  /**
   * the unit of the result of a division,
   * s. {@link #multiplyForOperation(SymTypeOfSIUnit, SymTypeOfSIUnit)}
   */
  public static SymTypeOfSIUnit divideForOperation(
      SymTypeOfSIUnit left,
      SymTypeOfSIUnit right
  ) {
    return multiplyForOperation(left, invert(right));
  }

  protected boolean containsDifferentUnitsOfSameDimension(
      SymTypeOfSIUnit siUnit
  ) {
    // ignoring the exponents, e.g., km^2 and m are of the same dimension
    List<SymTypeOfSIUnit> dimensions = new ArrayList<>();
    for (SIUnitBasic unit : siUnit.getNumerator()) {
      dimensions.add(normalizeDimension(createSIUnitWithoutExponent(unit)));
    }
    for (SIUnitBasic unit : siUnit.getDenominator()) {
      dimensions.add(normalizeDimension(createSIUnitWithoutExponent(unit)));
    }
    for (int i = 0; i < dimensions.size(); i++) {
      SymTypeOfSIUnit dimensionA = dimensions.get(i);
      if (dimensionA.getNumerator().isEmpty()
          && dimensionA.getDenominator().isEmpty()) {
        continue;
      }
      SymTypeOfSIUnit inverseA = invert(dimensionA);
      for (int j = i + 1; j < dimensions.size(); j++) {
        if (dimensionA.deepEquals(dimensions.get(j))
            || inverseA.deepEquals(dimensions.get(j))) {
          return true;
        }
      }
    }
    return false;
  }

  protected SymTypeOfSIUnit createSIUnitWithoutExponent(SIUnitBasic unit) {
    return SymTypeExpressionFactory.createSIUnit(
        List.of(SymTypeExpressionFactory.createSIUnitBasic(
            unit.getDimension(), unit.getPrefix(), 1)),
        List.of()
    );
  }

  /**
   * distributes the factor 10^exponent onto the units as prefixes,
   * as far as possible using engineering prefixes, e.g.,
   * {@code (m^2, 6) -> km^2}, {@code (m^2, 3) -> m^2}
   */
  protected SymTypeOfSIUnit applyEngineeringPrefixes(
      SymTypeOfSIUnit siUnit,
      int exponent
  ) {
    List<SIUnitBasic> numerator = siUnit.getNumerator().stream()
        .map(SIUnitBasic::deepClone).collect(Collectors.toList());
    List<SIUnitBasic> denominator = siUnit.getDenominator().stream()
        .map(SIUnitBasic::deepClone).collect(Collectors.toList());
    // the units with their exponents (negative in the denominator)
    List<Map.Entry<SIUnitBasic, Integer>> units = new ArrayList<>();
    numerator.forEach(unit -> units.add(Map.entry(unit, unit.getExponent())));
    denominator.forEach(unit -> units.add(Map.entry(unit, -unit.getExponent())));
    // larger exponents first, as they can hold more
    units.sort(Comparator.comparingInt(entry -> -Math.abs(entry.getValue())));
    int remaining = exponent;
    for (Map.Entry<SIUnitBasic, Integer> entry : units) {
      SIUnitBasic unit = entry.getKey();
      int signedExp = entry.getValue();
      int oldPrefixExp = getPrefixExponent(unit.getPrefix());
      int bestPrefixExp = oldPrefixExp;
      for (int prefixExp = -24; prefixExp <= 24; prefixExp += 3) {
        int extracted = (prefixExp - oldPrefixExp) * signedExp;
        int bestExtracted = (bestPrefixExp - oldPrefixExp) * signedExp;
        if (Integer.signum(extracted) == Integer.signum(remaining)
            && Math.abs(extracted) <= Math.abs(remaining)
            && Math.abs(extracted) > Math.abs(bestExtracted)
        ) {
          bestPrefixExp = prefixExp;
        }
      }
      unit.setPrefix(getEngineeringPrefix(bestPrefixExp));
      remaining -= (bestPrefixExp - oldPrefixExp) * signedExp;
    }
    return SymTypeExpressionFactory.createSIUnit(numerator, denominator);
  }

  protected int getPrefixExponent(String prefix) {
    return prefixFactors.get(prefix).precision()
        - prefixFactors.get(prefix).scale() - 1;
  }

  protected String getEngineeringPrefix(int exponent) {
    return prefixFactors.entrySet().stream()
        .filter(e -> getPrefixExponent(e.getKey()) == exponent)
        .map(Map.Entry::getKey)
        // u and µ are the same, use µ
        .filter(prefix -> !prefix.equals("u"))
        .findFirst()
        .orElseThrow();
  }

  /**
   * the larger of two units of the same dimension, e.g.,
   * {@code (m, km) -> km}, {@code (km/h, m/s) -> m/s}.
   * If both are of the same size or cannot be compared (e.g., deg and rad),
   * the first unit is returned.
   */
  public static SymTypeOfSIUnit getLargerUnit(
      SymTypeOfSIUnit siUnitA,
      SymTypeOfSIUnit siUnitB
  ) {
    return getDelegate()._getLargerUnit(siUnitA, siUnitB);
  }

  protected SymTypeOfSIUnit _getLargerUnit(
      SymTypeOfSIUnit siUnitA,
      SymTypeOfSIUnit siUnitB
  ) {
    // 1 A = factor B
    Optional<BigDecimal> factor = getConversionFactor(siUnitA, siUnitB);
    if (factor.isPresent() && factor.get().compareTo(BigDecimal.ONE) < 0) {
      return siUnitB;
    }
    return siUnitA;
  }

  /**
   * the smaller of two units of the same dimension, e.g.,
   * {@code (m, km) -> m}, s. {@link #getLargerUnit}
   */
  public static SymTypeOfSIUnit getSmallerUnit(
      SymTypeOfSIUnit siUnitA,
      SymTypeOfSIUnit siUnitB
  ) {
    return getDelegate()._getSmallerUnit(siUnitA, siUnitB);
  }

  protected SymTypeOfSIUnit _getSmallerUnit(
      SymTypeOfSIUnit siUnitA,
      SymTypeOfSIUnit siUnitB
  ) {
    Optional<BigDecimal> factor = getConversionFactor(siUnitA, siUnitB);
    if (factor.isPresent() && factor.get().compareTo(BigDecimal.ONE) > 0) {
      return siUnitB;
    }
    return siUnitA;
  }

  public static SymTypeOfNumericWithSIUnit multiplyWithNumerics(
      SymTypeOfNumericWithSIUnit... numericWithSIUnits
  ) {
    return multiplyWithNumerics(List.of(numericWithSIUnits));
  }

  public static SymTypeOfNumericWithSIUnit multiplyWithNumerics(
      Collection<SymTypeOfNumericWithSIUnit> numericWithSIUnits
  ) {
    return getDelegate()._multiplyWithNumerics(numericWithSIUnits);
  }

  protected SymTypeOfNumericWithSIUnit _multiplyWithNumerics(
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
    return getDelegate()._invert(siUnit);
  }

  protected SymTypeOfSIUnit _invert(SymTypeOfSIUnit siUnit) {
    return SymTypeExpressionFactory.createSIUnit(
        siUnit.getDenominator(), siUnit.getNumerator()
    );
  }

  /**
   * Returns true iff the type is, e.g., {@code [km]} or {@code [km]<int>}
   */
  public static boolean hasSIUnit(SymTypeExpression type) {
    return getDelegate()._hasSIUnit(type);
  }

  protected boolean _hasSIUnit(SymTypeExpression type) {
    return type.isSIUnitType() || type.isNumericWithSIUnitType();
  }

  /**
   * Returns the SIUnit, e.g.,
   * {@code [km]<int> -> [km]}, {@code int -> []}
   */
  public static SymTypeOfSIUnit getSIUnit(SymTypeExpression type) {
    return getDelegate()._getSIUnit(type);
  }

  protected SymTypeOfSIUnit _getSIUnit(SymTypeExpression type) {
    if (type.isSIUnitType()) {
      return type.asSIUnitType();
    }
    else if (type.isNumericWithSIUnitType()) {
      return type.asNumericWithSIUnitType().getSIUnitType();
    }
    else {
      return SymTypeExpressionFactory.createSIUnit(List.of(), List.of());
    }
  }

  /**
   * calculates {@code factor} with
   * {@code valueInTarget = valueInSource * factor}, e.g., km -> m: 1000.
   * Units like ºC, dB, or deg are only supported if they cancel out.
   *
   * @return the factor or empty, e.g., if the dimensions differ
   */
  public static Optional<BigDecimal> getConversionFactor(
      SymTypeOfSIUnit source,
      SymTypeOfSIUnit target
  ) {
    return getDelegate()._getConversionFactor(source, target);
  }

  protected Optional<BigDecimal> _getConversionFactor(
      SymTypeOfSIUnit source,
      SymTypeOfSIUnit target
  ) {
    // source / target
    List<SIUnitBasic> numerator = new ArrayList<>();
    numerator.addAll(source.getNumerator());
    numerator.addAll(target.getDenominator());
    List<SIUnitBasic> denominator = new ArrayList<>();
    denominator.addAll(source.getDenominator());
    denominator.addAll(target.getNumerator());

    BigDecimal factorNumerator = BigDecimal.ONE;
    BigDecimal factorDenominator = BigDecimal.ONE;
    List<SIUnitBasic> scalableNumerator = new ArrayList<>();
    List<SIUnitBasic> scalableDenominator = new ArrayList<>();
    // these have to cancel each other out
    Map<String, Integer> nonScalable2Exp = new LinkedHashMap<>();
    for (int i = 0; i < numerator.size() + denominator.size(); i++) {
      boolean isNumerator = i < numerator.size();
      SIUnitBasic unitBasic = isNumerator ?
          numerator.get(i) :
          denominator.get(i - numerator.size());
      Optional<BigDecimal> unitFactor = getFactor(unitBasic);
      if (unitFactor.isPresent()) {
        boolean isFactorNumerator =
            isNumerator == (unitBasic.getExponent() >= 0);
        BigDecimal factor =
            unitFactor.get().pow(Math.abs(unitBasic.getExponent()));
        if (isFactorNumerator) {
          factorNumerator = factorNumerator.multiply(factor);
        }
        else {
          factorDenominator = factorDenominator.multiply(factor);
        }
        (isNumerator ? scalableNumerator : scalableDenominator).add(unitBasic);
      }
      else {
        String key = unitBasic.getPrefix() + unitBasic.getDimension();
        int exp = isNumerator ?
            unitBasic.getExponent() :
            -unitBasic.getExponent();
        nonScalable2Exp.merge(key, exp, Integer::sum);
      }
    }

    if (nonScalable2Exp.values().stream().anyMatch(exp -> exp != 0)) {
      return Optional.empty();
    }
    if (!isOfDimensionOne(SymTypeExpressionFactory.createSIUnit(
        scalableNumerator, scalableDenominator))
    ) {
      return Optional.empty();
    }

    BigDecimal factor;
    try {
      factor = factorNumerator.divide(factorDenominator);
    }
    catch (ArithmeticException nonTerminatingDecimalExpansion) {
      factor = factorNumerator.divide(
          factorDenominator, MathContext.DECIMAL128
      );
    }
    return Optional.of(factor.stripTrailingZeros());
  }

  /**
   * @return the factor of the unit with prefix, ignoring the exponent
   */
  protected Optional<BigDecimal> getFactor(SIUnitBasic unitBasic) {
    if (!unitFactors.containsKey(unitBasic.getDimension())
        || !prefixFactors.containsKey(unitBasic.getPrefix())
    ) {
      return Optional.empty();
    }
    return Optional.of(prefixFactors.get(unitBasic.getPrefix())
        .multiply(unitFactors.get(unitBasic.getDimension()))
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

  // static delegate

  public static void init() {
    Log.trace("init default SIUnitTypeRelations", "TypeCheck setup");
    setDelegate(new SIUnitTypeRelations());
  }

  public static void reset() {
    SIUnitTypeRelations.delegate = null;
  }

  protected static void setDelegate(SIUnitTypeRelations newDelegate) {
    SIUnitTypeRelations.delegate = Preconditions.checkNotNull(newDelegate);
  }

  protected static SIUnitTypeRelations getDelegate() {
    if (SIUnitTypeRelations.delegate == null) {
      init();
    }
    return SIUnitTypeRelations.delegate;
  }

}
