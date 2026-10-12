/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.javagen.typeconverter;

import de.monticore.codegen.CodeGenPrintAction;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfSIUnit;
import de.monticore.types3.util.SIUnitTypeRelations;
import de.se_rwth.commons.logging.Log;

import java.math.BigDecimal;
import java.util.Optional;

import static de.monticore.codegen.CodeGenSymTypeExpressionConverter.printConverted;
import static de.monticore.codegen.javagen.JavaGenSymTypeRelations.getSIUnitValueType;
import static de.monticore.types3.SymTypeRelations.isNumericType;
import static de.monticore.types3.util.SIUnitTypeRelations.getSIUnit;
import static de.monticore.types3.util.SIUnitTypeRelations.hasSIUnit;

/**
 * Conversions between SIUnits,
 * e.g., 5.0 of {@code [km]<double>} -> 5000.0 of {@code [m]<double>}.
 * Values are in the units of their types;
 * scaling is calculated using double.
 * Conversions like ºC -> K or deg -> rad are not supported.
 */
public class JavaSIUnitConversionHandler
    extends AbstractJavaTypeConverter {

  @Override
  public boolean tryPrintConverted(
      IndentPrinter printer,
      SymTypeExpression targetType,
      SymTypeExpression sourceType,
      CodeGenPrintAction sourceExprPrintAction
  ) {
    if (!(hasSIUnit(targetType) || hasSIUnit(sourceType))
        || !isNumericWithOptionalSIUnit(targetType)
        || !isNumericWithOptionalSIUnit(sourceType)
    ) {
      return false;
    }

    SymTypeExpression targetValueType = getSIUnitValueType(targetType);
    SymTypeExpression sourceValueType = getSIUnitValueType(sourceType);
    Optional<BigDecimal> factor = SIUnitTypeRelations.getConversionFactor(
        getSIUnit(sourceType), getSIUnit(targetType)
    );
    if (factor.isEmpty()) {
      Log.error("0xFD240 Cannot convert " + sourceType.printFullName()
          + " to " + targetType.printFullName()
          + ", as there is no conversion factor."
          + " Conversions between, e.g., ºC and K, or deg and rad,"
          + " are not supported."
      );
      sourceExprPrintAction.print(printer);
    }
    else if (factor.get().compareTo(BigDecimal.ONE) == 0) {
      printConverted(printer, targetValueType, sourceValueType,
          sourceExprPrintAction
      );
    }
    else {
      SymTypeExpression doubleType =
          SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.DOUBLE);
      printConverted(printer, targetValueType, doubleType, p -> {
        p.print("((");
        printConverted(p, doubleType, sourceValueType, sourceExprPrintAction);
        p.print(")");
        printScaling(p, getSIUnit(sourceType), getSIUnit(targetType));
        p.print(")");
      });
    }
    return true;
  }

  /**
   * prints, e.g., {@code * 1000.0} for km -> m,
   * and {@code / 1000.0} for m -> km,
   * as dividing by 1000 is more precise than multiplying by 0.001
   */
  protected void printScaling(
      IndentPrinter printer,
      SymTypeOfSIUnit sourceUnit,
      SymTypeOfSIUnit targetUnit
  ) {
    BigDecimal factor = SIUnitTypeRelations
        .getConversionFactor(sourceUnit, targetUnit).orElseThrow();
    if (factor.compareTo(BigDecimal.ONE) >= 0) {
      printer.print(" * ");
      printer.print(Double.toString(factor.doubleValue()));
    }
    else {
      BigDecimal divisor = SIUnitTypeRelations
          .getConversionFactor(targetUnit, sourceUnit).orElseThrow();
      printer.print(" / ");
      printer.print(Double.toString(divisor.doubleValue()));
    }
  }

  // Helper

  protected boolean isNumericWithOptionalSIUnit(SymTypeExpression type) {
    return hasSIUnit(type) || isNumericType(type);
  }

}
