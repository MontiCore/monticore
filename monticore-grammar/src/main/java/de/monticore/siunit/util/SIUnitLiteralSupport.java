/* (c) https://github.com/MontiCore/monticore */
package de.monticore.siunit.util;

import de.monticore.literals.mccommonliterals._ast.ASTBasicDoubleLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTBasicFloatLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTBasicLongLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTNatLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTNumericLiteral;
import de.monticore.siunit.siunits._ast.ASTSIUnit;
import de.monticore.siunit.siunits._ast.ASTSIUnitWithPrefix;
import de.monticore.siunit.siunits._ast.ASTSIUnitWithoutPrefix;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

/**
 * Shared utility for extracting numeric values and unit names from
 * {@link de.monticore.siunit.siunitliterals._ast.ASTSIUnitLiteral} nodes.
 *
 * <p>This class is useful for any CoCo that needs to validate SI-unit
 * durations without depending on a specific host language.</p>
 *
 * <p>Contributed from the TimeTrigger project
 * (se-student/ss26/lectures/sle/projects/timetrigger).</p>
 */
public final class SIUnitLiteralSupport {

  /**
   * The set of simple SI-unit spellings that represent time durations:
   * milliseconds, seconds, minutes, hours, and days.
   */
  public static final Set<String> ALLOWED_TIME_UNITS = Set.of("ms", "s", "min", "h", "d");

  private SIUnitLiteralSupport() {
  }

  /**
   * Safely extracts the numeric value of a literal without propagating
   * {@link NumberFormatException} on overflow or malformed input.
   *
   * @param literal the numeric literal to extract from
   * @return the value as a {@link BigDecimal}, or empty if it cannot be represented
   */
  public static Optional<BigDecimal> numericValue(ASTNumericLiteral literal) {
    try {
      if (literal instanceof ASTNatLiteral v) {
        return Optional.of(BigDecimal.valueOf(v.getValue()));
      }
      if (literal instanceof ASTBasicLongLiteral v) {
        return Optional.of(BigDecimal.valueOf(v.getValue()));
      }
      if (literal instanceof ASTBasicFloatLiteral v) {
        return Optional.of(BigDecimal.valueOf(v.getValue()));
      }
      if (literal instanceof ASTBasicDoubleLiteral v) {
        return Optional.of(BigDecimal.valueOf(v.getValue()));
      }
    }
    catch (NumberFormatException exception) {
      return Optional.empty();
    }
    return Optional.empty();
  }

  /**
   * Extracts the name of a simple (non-compound) SI unit.
   *
   * @param unit the SI unit AST node
   * @return the unit name (e.g. {@code "ms"}, {@code "s"}, {@code "min"}),
   *         or empty for compound or unrecognised units
   */
  public static Optional<String> simpleUnit(ASTSIUnit unit) {
    if (!unit.isPresentSIUnitPrimitive()) {
      return Optional.empty();
    }
    var primitive = unit.getSIUnitPrimitive();
    if (primitive.isPresentSIUnitWithPrefix()) {
      return unitName(primitive.getSIUnitWithPrefix());
    }
    if (primitive.isPresentSIUnitWithoutPrefix()) {
      return unitName(primitive.getSIUnitWithoutPrefix());
    }
    return Optional.empty();
  }

  private static Optional<String> unitName(ASTSIUnitWithPrefix unit) {
    if (unit.isPresentName()) {
      return Optional.of(unit.getName());
    }
    if (unit.isPresentNonNameUnit()) {
      return Optional.of(unit.getNonNameUnit());
    }
    return Optional.empty();
  }

  private static Optional<String> unitName(ASTSIUnitWithoutPrefix unit) {
    if (unit.isPresentName()) {
      return Optional.of(unit.getName());
    }
    if (unit.isPresentNonNameUnit()) {
      return Optional.of(unit.getNonNameUnit());
    }
    return Optional.empty();
  }

}
