/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types3.util;

import java.util.List;
import java.util.stream.Stream;

/**
 * offers ways to iterate over (nearly) all Basic SIUnit Types
 */
public class SIUnitIteratorForTests {
  //Based on SIUnits.mc4:
  protected static final List<String> prefixes = List.of(
      "Y", "Z", "E", "P", "T", "G", "M", "k", "h", "da",
      "d", "c", "m", "u", "µ", "n", "p", "f", "a", "z", "y"
  );

  protected static final List<String> unitsWithPrefix = List.of(
      "m", "g", "s", "A", "K", "mol", "cd", "Hz", "N", "Pa",
      "J", "W", "C", "V", "F", "Ohm", "Ω", "S", "Wb", "T",
      "H", "lm", "lx", "Bq", "Gy", "Sv", "kat", "l", "L"
  );

  protected static final List<String> unitsWithoutPrefix = List.of(
      "min", "h", "d", "ha", "t", "au", "Np", "B", "dB", "eV",
      "Da", "u"
  );

  public static Stream<String> getUnits() {
    return Stream.concat(getUnitsWithPrefix(), getUnitsWithoutPrefix());
  }

  public static Stream<String> getPrefixes() {
    return prefixes.stream();
  }

  /**
   * units that CAN have a prefix (but don't have any).
   * s. {@link #getPrefixedUnits()}
   */
  public static Stream<String> getUnitsWithPrefix() {
    return unitsWithPrefix.stream();
  }

  /**
   * units that cannot have a prefix
   */
  public static Stream<String> getUnitsWithoutPrefix() {
    return unitsWithoutPrefix.stream();
  }

  /**
   * prefix + unit, all (read: A LOT) combinations
   */
  public static Stream<String> getPrefixedUnits() {
    return getUnitsWithPrefix().flatMap(
        u -> getPrefixes().map(p -> p + u)
    );
  }

  /**
   * 2 units and up to one prefix
   * (> 10.000 elements, do NOT overuse this)
   * @param delimiter placed between the (prefixed) units
   */
  public static Stream<String> get2UnitsGroup(String delimiter) {
    // note that only specific combinations
    // containing "Ω", and "µ" can be parsed, so others are filtered out
    return Stream.concat(
            // no prefix
            getUnits()
                .filter(u1 -> !u1.contains("Ω"))
                .flatMap(u1 -> getUnits().map(u2 -> u1 + delimiter + u2)),
            Stream.concat(
                // prefix in front
                getPrefixedUnits()
                    .filter(pu -> !pu.contains("Ω"))
                    .flatMap(pu -> getUnits().map(u -> pu + delimiter + u))
                    .filter(puu -> !(puu.startsWith("µ") && puu.endsWith("Ω")))
                ,
                //prefix between units
                getPrefixedUnits()
                    .filter(pu -> !pu.contains("µ"))
                    .flatMap(pu -> getUnits()
                        .filter(u -> !u.contains("Ω"))
                        .map(u -> u + delimiter + pu))
            )
        );
  }

}
