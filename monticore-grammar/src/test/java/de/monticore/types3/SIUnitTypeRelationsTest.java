// (c) https://github.com/MontiCore/monticore
package de.monticore.types3;

import de.monticore.expressions.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import de.monticore.runtime.junit.AbstractMCTest;
import de.monticore.types.check.SIUnitBasic;
import de.monticore.types.check.SymTypeOfSIUnit;
import de.monticore.types3.util.DefsTypesForTests;
import de.monticore.types3.util.SIUnitTypeRelations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static de.monticore.runtime.junit.MCAssertions.assertNoFindings;
import static de.monticore.types.check.SymTypeExpressionFactory.createSIUnit;
import static de.monticore.types.check.SymTypeExpressionFactory.createSIUnitBasic;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SIUnitTypeRelationsTest extends AbstractMCTest {

  @BeforeEach
  public void init() {
    CombineExpressionsWithLiteralsMill.reset();
    CombineExpressionsWithLiteralsMill.init();
    DefsTypesForTests.setup();
    SymTypeRelations.init();
  }

  @Test
  public void conversionFactorIdentity() {
    checkFactor(unit("m"), unit("m"), 1);
    checkFactor(unit("s"), unit("s"), 1);
    checkFactor(unit("k", "g"), unit("k", "g"), 1);
    checkFactor(unit("N"), unit("N"), 1);
    assertNoFindings();
  }

  @Test
  public void conversionFactorPrefixes() {
    checkFactor(unit("k", "m"), unit("m"), 1000);
    checkFactor(unit("m"), unit("k", "m"), 0.001);
    checkFactor(unit("m", "m"), unit("k", "m"), 1e-6);
    checkFactor(unit("c", "m"), unit("m", "m"), 10);
    checkFactor(unit("da", "m"), unit("m"), 10);
    checkFactor(unit("µ", "s"), unit("s"), 1e-6);
    checkFactor(unit("u", "s"), unit("n", "s"), 1000);
    checkFactor(unit("G", "Hz"), unit("M", "Hz"), 1000);
    checkFactor(unit("k", "N"), unit("N"), 1000);
    assertNoFindings();
  }

  @Test
  public void conversionFactorGram() {
    // the base unit is kg, not g
    checkFactor(unit("g"), unit("k", "g"), 0.001);
    checkFactor(unit("k", "g"), unit("g"), 1000);
    checkFactor(unit("m", "g"), unit("g"), 0.001);
    checkFactor(unit("t"), unit("k", "g"), 1000);
    assertNoFindings();
  }

  @Test
  public void conversionFactorNonSIUnits() {
    checkFactor(unit("min"), unit("s"), 60);
    checkFactor(unit("h"), unit("s"), 3600);
    checkFactor(unit("h"), unit("min"), 60);
    checkFactor(unit("min"), unit("h"), 1.0 / 60);
    checkFactor(unit("d"), unit("h"), 24);
    checkFactor(unit("ha"), createSIUnit(
        List.of(createSIUnitBasic("m", 2)), List.of()), 10000);
    checkFactor(unit("l"), createSIUnit(
        List.of(createSIUnitBasic("m", 3)), List.of()), 0.001);
    checkFactor(unit("m", "l"), createSIUnit(
        List.of(createSIUnitBasic("m", "c", 3)), List.of()), 1);
    checkFactor(unit("L"), unit("l"), 1);
    checkFactor(unit("au"), unit("m"), 149597870700.0);
    checkFactor(unit("eV"), unit("J"), 1.602176634e-19);
    checkFactor(unit("Da"), unit("k", "g"), 1.66053906660e-27);
    checkFactor(unit("u"), unit("Da"), 1);
    assertNoFindings();
  }

  @Test
  public void conversionFactorCompoundUnits() {
    // km^2 -> m^2
    checkFactor(
        createSIUnit(List.of(createSIUnitBasic("m", "k", 2)), List.of()),
        createSIUnit(List.of(createSIUnitBasic("m", 2)), List.of()),
        1e6
    );
    // km/h -> m/s
    checkFactor(
        createSIUnit(List.of(basic("k", "m")), List.of(basic("", "h"))),
        createSIUnit(List.of(basic("", "m")), List.of(basic("", "s"))),
        1000.0 / 3600
    );
    // m*s^-1 -> m/s
    checkFactor(
        createSIUnit(
            List.of(basic("", "m"), createSIUnitBasic("s", -1)), List.of()),
        createSIUnit(List.of(basic("", "m")), List.of(basic("", "s"))),
        1
    );
    // kN -> kg*m/s^2
    checkFactor(
        unit("k", "N"),
        createSIUnit(
            List.of(basic("k", "g"), basic("", "m")),
            List.of(createSIUnitBasic("s", 2))),
        1000
    );
    // Hz -> 1/min
    checkFactor(
        unit("Hz"),
        createSIUnit(List.of(), List.of(basic("", "min"))),
        60
    );
    assertNoFindings();
  }

  @Test
  public void noConversionFactorForDifferentDimensions() {
    checkNoFactor(unit("m"), unit("s"));
    checkNoFactor(unit("k", "m"), unit("g"));
    checkNoFactor(unit("N"), unit("J"));
    checkNoFactor(
        unit("m"),
        createSIUnit(List.of(createSIUnitBasic("m", 2)), List.of())
    );
    assertNoFindings();
  }

  @Test
  public void conversionFactorAffineUnits() {
    // only identical affine units can be "converted"
    checkFactor(unit("ºC"), unit("ºC"), 1);
    checkFactor(unit("°C"), unit("°C"), 1);
    checkFactor(
        createSIUnit(List.of(basic("", "ºC")), List.of(basic("", "s"))),
        createSIUnit(List.of(basic("", "ºC")), List.of(basic("m", "s"))),
        0.001
    );
    checkNoFactor(unit("ºC"), unit("K"));
    checkNoFactor(unit("K"), unit("ºC"));
    checkNoFactor(unit("ºF"), unit("ºC"));
    checkNoFactor(unit("°F"), unit("°C"));
    assertNoFindings();
  }

  @Test
  public void conversionFactorLogarithmicAndAngleUnits() {
    // only identical units can be "converted"
    checkFactor(unit("dB"), unit("dB"), 1);
    checkFactor(unit("deg"), unit("deg"), 1);
    checkFactor(unit("rad"), unit("rad"), 1);
    checkNoFactor(unit("deg"), unit("rad"));
    checkNoFactor(unit("°"), unit("rad"));
    checkNoFactor(unit("dB"), unit("B"));
    checkNoFactor(unit("Np"), unit("dB"));
    checkNoFactor(unit("sr"), unit("rad"));
    assertNoFindings();
  }

  // Helper

  protected void checkFactor(
      SymTypeOfSIUnit source,
      SymTypeOfSIUnit target,
      double expected
  ) {
    Optional<BigDecimal> factor =
        SIUnitTypeRelations.getConversionFactor(source, target);
    assertTrue(factor.isPresent(), "expected a conversion factor from "
        + source.printFullName() + " to " + target.printFullName()
    );
    assertEquals(expected, factor.get().doubleValue(),
        Math.abs(expected) * 1e-12,
        "unexpected conversion factor from "
            + source.printFullName() + " to " + target.printFullName()
    );
  }

  protected void checkNoFactor(
      SymTypeOfSIUnit source,
      SymTypeOfSIUnit target
  ) {
    Optional<BigDecimal> factor =
        SIUnitTypeRelations.getConversionFactor(source, target);
    assertTrue(factor.isEmpty(), "expected no conversion factor from "
        + source.printFullName() + " to " + target.printFullName()
        + ", but got " + factor.map(BigDecimal::toString).orElse("")
    );
  }

  protected SymTypeOfSIUnit unit(String dimension) {
    return unit("", dimension);
  }

  protected SymTypeOfSIUnit unit(String prefix, String dimension) {
    return createSIUnit(List.of(basic(prefix, dimension)), List.of());
  }

  protected SIUnitBasic basic(String prefix, String dimension) {
    return createSIUnitBasic(dimension, prefix, 1);
  }

}
