/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types3;

import org.junit.Test;

import java.io.IOException;

import static de.monticore.siunit.siunits._parser.SIUnitsAntlrParser.prefix;
import static de.monticore.types3.util.SIUnitIteratorForTests.get2UnitsGroup;
import static de.monticore.types3.util.SIUnitIteratorForTests.getPrefixedUnits;
import static de.monticore.types3.util.SIUnitIteratorForTests.getUnits;

public class SIUnitTypes4MathTypeVisitorTest
    extends AbstractTypeVisitorTest {
/*
  @Test
  public void synTFromSIUnitTypes4MathSimple() throws IOException {
    checkSIUnit("°C", "°C", "K");
    checkSIUnit("°F", "°F", "K");
    checkSIUnit("kg", "kg", "kg");
    checkSIUnit("cd", "cd", "cd");
    checkSIUnit("m", "m", "m");
    checkSIUnit("deg", "deg", "1");

    checkSIUnit("s^2", "s^2", "s^2");
    checkSIUnit("s^2/kg", "s^2/kg", "s^2/kg");
    checkSIUnit("s^2/min", "s^2/min", "s");
    checkSIUnit("kgs^2/minm", "kg*s^2/(m*min)", "kg*s/m");

    checkSIUnit("s^-1", "1/s", "1/s");
    checkSIUnit("1/s", "1/s", "1/s");


    checkTypeRoundTrip("Ω");
    checkSIUnit("kΩ", "kΩ", "kg*m^2/(A^2*s^3)");
    checkSIUnit("µg", "µg", "kg");
    checkSIUnit("µΩ", "µΩ", "kg*m^2/(A^2*s^3)");

    parseSIUnit("kV^2A^3/m^2");
    parseSIUnit("s^-1");
    parseSIUnit("kVA");
    parseSIUnit("kVAh");
    parseSIUnit("kVAh/°C");
    parseSIUnit("kV^2A^3h")

  }
*/

  @Test
  public void synTFromSIUnitTypes4MathSimpleAll() throws IOException {
    // converting units to SI
    for (String siUnit : (Iterable<String>) getUnits()::iterator) {
      checkTypeRoundTrip("[" + siUnit + "]");
    }
    for (String siUnit : (Iterable<String>) getPrefixedUnits()::iterator) {
      checkTypeRoundTrip("[" + siUnit + "]");
    }
    for (String siUnit : (Iterable<String>) get2UnitsGroup()::iterator) {
      checkTypeRoundTrip("[" + siUnit + "]");
    }
  }
/*
  @Test
  public void symTypeFromAST_OhmAndMu() throws IOException {
    checkTypeRoundTrip("Ω");
    checkSIUnit("kΩ", "kΩ", "kg*m^2/(A^2*s^3)");
    checkSIUnit("µg", "µg", "kg");
    checkSIUnit("µΩ", "µΩ", "kg*m^2/(A^2*s^3)");
  }
*/

}
