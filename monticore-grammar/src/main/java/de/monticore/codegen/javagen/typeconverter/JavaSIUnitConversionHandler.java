/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.javagen.typeconverter;

import de.monticore.codegen.CodeGenPrintAction;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types3.util.SIUnitTypeRelations;
import de.se_rwth.commons.logging.Log;

import java.math.BigDecimal;
import java.util.Optional;

import static de.monticore.codegen.CodeGenSymTypeExpressionConverter.printConverted;
import static de.monticore.codegen.javagen.JavaGenSymTypeRelations.getSIUnitValueType;
import static de.monticore.types3.SymTypeRelations.isNumericType;
import static de.monticore.types3.SymTypeRelations.normalize;
import static de.monticore.types3.util.SIUnitTypeRelations.getSIUnit;
import static de.monticore.types3.util.SIUnitTypeRelations.hasSIUnit;

/**
 * Conversions between SIUnits, e.g., 5 of [km]<int> -> 5000 of [m]<int>.
 * Values are in the units of their (non-normalized) types;
 * scaling is calculated using double.
 * Conversions like ºC -> K or deg -> rad are not supported.
 */
public class JavaSIUnitConversionHandler
    extends AbstractJavaTypeConverter {

  @Override
  public boolean tryPrintConverted(
      IndentPrinter printer,
      SymTypeExpression nonNormalizedTargetType,
      SymTypeExpression nonNormalizedSourceType,
      CodeGenPrintAction sourceExprPrintAction
  ) {
    SymTypeExpression targetType = getSIOrNormalized(nonNormalizedTargetType);
    SymTypeExpression sourceType = getSIOrNormalized(nonNormalizedSourceType);
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
        printScaling(p, factor.get());
        p.print(")");
      });
    }
    return true;
  }

  protected void printScaling(IndentPrinter printer, BigDecimal factor) {
    // dividing is more precise for, e.g., 1/1000
    Optional<BigDecimal> divisor = getExactInverse(factor);
    if (factor.compareTo(BigDecimal.ONE) < 0 && divisor.isPresent()) {
      printer.print(" / ");
      printer.print(Double.toString(divisor.get().doubleValue()));
    }
    else {
      printer.print(" * ");
      printer.print(Double.toString(factor.doubleValue()));
    }
  }

  protected Optional<BigDecimal> getExactInverse(BigDecimal factor) {
    try {
      return Optional.of(BigDecimal.ONE.divide(factor));
    }
    catch (ArithmeticException nonTerminatingDecimalExpansion) {
      return Optional.empty();
    }
  }

  // Helper

  /**
   * values of, e.g., unions are in the units of the normalized type
   */
  protected SymTypeExpression getSIOrNormalized(SymTypeExpression type) {
    return hasSIUnit(type) ? type : normalize(type);
  }

  protected boolean isNumericWithOptionalSIUnit(SymTypeExpression type) {
    return hasSIUnit(type) || isNumericType(type);
  }

}
