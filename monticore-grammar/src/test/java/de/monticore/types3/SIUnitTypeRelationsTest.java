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
import static de.monticore.types3.util.DefsTypesForTests.*;
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
    // rad and sr are coherent units of dimension one (factor 1)
    checkFactor(unit("rad"), createSIUnit(List.of(), List.of()), 1);
    checkFactor(unit("m", "rad"), unit("rad"), 0.001);
    assertNoFindings();
  }

  @Test
  public void normalizeDimensionSimple() {
    checkDimension(_m_SISymType, "[m]");
    checkDimension(_g_SISymType, "[kg]");
    checkDimension(_s_SISymType, "[s]");
    checkDimension(_A_SISymType, "[A]");
    checkDimension(_K_SISymType, "[K]");
    checkDimension(_mol_SISymType, "[mol]");
    checkDimension(_cd_SISymType, "[cd]");
    checkDimension(_kg_SISymType, "[kg]");

    checkDimension(_Hz_SISymType, "[1/s]");
    checkDimension(_N_SISymType, "[m^1kg/s^2]");
    checkDimension(_Pa_SISymType, "[kg/s^2m]");
    checkDimension(_J_SISymType, "[m^2kg/s^2]");
    checkDimension(_W_SISymType, "[m^2kg/s^3]");
    checkDimension(_C_SISymType, "[s^1A]");
    checkDimension(_V_SISymType, "[m^2kg/s^3A]");
    checkDimension(_F_SISymType, "[s^4A^2/m^2kg]");
    checkDimension(_Ohm_SISymType, "[m^2kg/s^3A^2]");
    checkDimension(_ohm_SISymType, "[m^2kg/s^3A^2]");
    checkDimension(_S_SISymType, "[s^3A^2/m^2kg]");
    checkDimension(_Wb_SISymType, "[m^2kg/s^2A]");
    checkDimension(_T_SISymType, "[kg/s^2A]");
    checkDimension(_H_SISymType, "[m^2kg/s^2A^2]");
    checkDimension(_lm_SISymType, "[cd]");
    checkDimension(_lx_SISymType, "[cd/m^2]");
    checkDimension(_Bq_SISymType, "[1/s]");
    checkDimension(_Gy_SISymType, "[m^2/s^2]");
    checkDimension(_Sv_SISymType, "[m^2/s^2]");
    checkDimension(_kat_SISymType, "[mol/s]");
    checkDimension(_l_SISymType, "[m^3]");
    checkDimension(_L_SISymType, "[m^3]");
    checkDimension(_min_SISymType, "[s]");
    checkDimension(_h_SISymType, "[s]");
    checkDimension(_d_SISymType, "[s]");
    checkDimension(_ha_SISymType, "[m^2]");
    checkDimension(_t_SISymType, "[kg]");
    checkDimension(_au_SISymType, "[m]");
    checkDimension(_eV_SISymType, "[m^2kg/s^2]");
    checkDimension(_Da_SISymType, "[kg]");
    checkDimension(_u_SISymType, "[kg]");
    checkDimension(_celsius_SISymType, "[K]");
    checkDimension(_fahrenheit_SISymType, "[K]");
    checkDimension(_Np_SISymType, "[1]");
    checkDimension(_B_SISymType, "[1]");
    checkDimension(_dB_SISymType, "[1]");
    checkDimension(_degSym_SISymType, "[1]");
    checkDimension(_deg_SISymType, "[1]");
    checkDimension(_rad_SISymType, "[1]");
    checkDimension(_sr_SISymType, "[1]");
    assertNoFindings();
  }

  @Test
  public void normalizeDimensionComplex() {
    checkDimension(
        createSIUnit(List.of(createSIUnitBasic("s", 2)), List.of()),
        "[s^2]"
    );
    checkDimension(
        createSIUnit(List.of(createSIUnitBasic("s", -1)), List.of()),
        "[1/s]"
    );
    checkDimension(
        createSIUnit(List.of(createSIUnitBasic("s", -2)), List.of()),
        "[1/s^2]"
    );
    checkDimension(
        createSIUnit(List.of(), List.of(_s_SIUnitBasic)),
        "[1/s]"
    );
    checkDimension(
        createSIUnit(List.of(createSIUnitBasic("s", 0)), List.of()),
        "[1]"
    );

    checkDimension(createSIUnit(List.of(createSIUnitBasic("N", 3)), List.of()),
        "[m^3kg^3/s^6]"
    );
    checkDimension(createSIUnit(List.of(createSIUnitBasic("N", -2)), List.of()),
        "[s^4/m^2kg^2]"
    );
    checkDimension(createSIUnit(List.of(createSIUnitBasic("N", 0)), List.of()),
        "[1]"
    );

    checkDimension(createSIUnit(
            List.of(createSIUnitBasic("s", 2)),
            List.of(_g_SIUnitBasic)
        ),
        "[s^2/kg]"
    );
    checkDimension(createSIUnit(
            List.of(createSIUnitBasic("s", 2)),
            List.of(_min_SIUnitBasic)
        ),
        "[s]"
    );
    checkDimension(createSIUnit(
            List.of(_kg_SIUnitBasic, createSIUnitBasic("s", 2)),
            List.of(_min_SIUnitBasic, _m_SIUnitBasic)
        ),
        "[s^1kg/m]"
    );
    assertNoFindings();
  }

  @Test
  public void largerAndSmallerUnit() {
    SymTypeOfSIUnit kmPerH =
        createSIUnit(List.of(basic("k", "m")), List.of(basic("", "h")));
    SymTypeOfSIUnit mPerS =
        createSIUnit(List.of(basic("", "m")), List.of(basic("", "s")));
    checkPrint("[km]", SIUnitTypeRelations.getLargerUnit(unit("m"), unit("k", "m")));
    checkPrint("[km]", SIUnitTypeRelations.getLargerUnit(unit("k", "m"), unit("m")));
    checkPrint("[m/s]", SIUnitTypeRelations.getLargerUnit(kmPerH, mPerS));
    checkPrint("[m]", SIUnitTypeRelations.getSmallerUnit(unit("m"), unit("k", "m")));
    checkPrint("[m]", SIUnitTypeRelations.getSmallerUnit(unit("k", "m"), unit("m")));
    checkPrint("[km/h]", SIUnitTypeRelations.getSmallerUnit(kmPerH, mPerS));
    // same size or not comparable: the first one
    checkPrint("[L]", SIUnitTypeRelations.getLargerUnit(unit("L"), unit("l")));
    checkPrint("[l]", SIUnitTypeRelations.getSmallerUnit(unit("l"), unit("L")));
    checkPrint("[deg]", SIUnitTypeRelations.getLargerUnit(unit("deg"), unit("rad")));
    assertNoFindings();
  }

  @Test
  public void multiplyAndDivideForOperation() {
    // different dimensions or the same units
    checkPrint("[s^1km]", SIUnitTypeRelations.multiplyForOperation(
        unit("k", "m"), unit("s")));
    checkPrint("[km^2]", SIUnitTypeRelations.multiplyForOperation(
        unit("k", "m"), unit("k", "m")));
    checkPrint("[km/h]", SIUnitTypeRelations.divideForOperation(
        unit("k", "m"), unit("h")));
    checkPrint("[km]", SIUnitTypeRelations.multiplyForOperation(
        createSIUnit(List.of(basic("k", "m")), List.of(basic("", "h"))),
        unit("h")));
    checkPrint("[deg^1rad]", SIUnitTypeRelations.multiplyForOperation(
        unit("deg"), unit("rad")));
    // different units of the same dimension
    checkPrint("[m^2]", SIUnitTypeRelations.multiplyForOperation(
        unit("k", "m"), unit("m")));
    checkPrint("[km^2]", SIUnitTypeRelations.multiplyForOperation(
        unit("k", "m"), unit("M", "m")));
    checkPrint("[1]", SIUnitTypeRelations.divideForOperation(
        unit("m"), unit("k", "m")));
    checkPrint("[Mm]", SIUnitTypeRelations.divideForOperation(
        createSIUnit(List.of(createSIUnitBasic("m", "k", 2)), List.of()),
        unit("m")));
    checkPrint("[1/mm]", SIUnitTypeRelations.divideForOperation(
        unit("k", "m"),
        createSIUnit(List.of(createSIUnitBasic("m", 2)), List.of())));
    checkPrint("[ms^2]", SIUnitTypeRelations.divideForOperation(
        unit("m", "s"), unit("k", "Hz")));
    checkPrint("[kg^2]", SIUnitTypeRelations.multiplyForOperation(
        unit("k", "g"), unit("g")));
    assertNoFindings();
  }

  // Helper

  protected void checkPrint(String expected, SymTypeOfSIUnit siUnit) {
    assertEquals(expected, siUnit.printFullName());
  }

  protected void checkDimension(SymTypeOfSIUnit siUnit, String expectedPrint) {
    SymTypeOfSIUnit normalized = SIUnitTypeRelations.normalizeDimension(siUnit);
    assertNoFindings();
    assertEquals(expectedPrint, normalized.printFullName());
  }

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
