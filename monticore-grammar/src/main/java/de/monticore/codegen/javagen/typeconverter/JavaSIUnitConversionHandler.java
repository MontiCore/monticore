/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.javagen.typeconverter;

import de.monticore.codegen.CodeGenPrintAction;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types3.util.SIUnitConversion;
import de.monticore.types3.util.SIUnitTypeRelations;
import de.se_rwth.commons.logging.Log;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Optional;

import static de.monticore.codegen.CodeGenSymTypeExpressionConverter.printConverted;
import static de.monticore.codegen.javagen.JavaGenSymTypeRelations.getSIUnitValueType;
import static de.monticore.types3.SymTypeRelations.isIntegralType;
import static de.monticore.types3.SymTypeRelations.isNumericType;
import static de.monticore.types3.util.SIUnitTypeRelations.getSIUnit;
import static de.monticore.types3.util.SIUnitTypeRelations.hasSIUnit;

/**
 * Conversions between SIUnits,
 * e.g., 5.0 of {@code [km]<double>} -> 5000.0 of {@code [m]<double>}.
 * Values are in the units of their types;
 * scaling is calculated using double.
 * Scaled values are rounded if the target is integral, e.g., 1999m -> 2km.
 * Temperatures are converted using their offsets,
 * e.g., 0.0 of {@code [ºC]<double>} -> 273.15 of {@code [K]<double>}.
 * Conversions of logarithmic units like dB are not supported.
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
    Optional<SIUnitConversion> conversion = SIUnitTypeRelations.getConversion(
        getSIUnit(sourceType), getSIUnit(targetType)
    );
    if (conversion.isEmpty()) {
      Log.error("0xFD240 Cannot convert " + sourceType.printFullName()
          + " to " + targetType.printFullName()
          + ", as there is no conversion."
          + " Conversions of, e.g., dB, or from ºC to ºC/s,"
          + " are not supported."
      );
      sourceExprPrintAction.print(printer);
    }
    else if (conversion.get().isIdentity()) {
      printConverted(printer, targetValueType, sourceValueType,
          sourceExprPrintAction
      );
    }
    else {
      SymTypeExpression doubleType =
          SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.DOUBLE);
      printScaledConverted(printer, targetValueType, doubleType, p -> {
        p.print("((");
        printConverted(p, doubleType, sourceValueType, sourceExprPrintAction);
        p.print(")");
        printScaling(p, conversion.get().getFactor());
        printOffset(p, conversion.get().getOffset());
        p.print(")");
      });
    }
    return true;
  }

  /**
   * converts a value that has been scaled due to a conversion of SIUnits.
   * Integral targets are rounded to the nearest integer,
   * e.g., 1999m -> 2km, as the scaling is not exact
   * (1.001 * 1000.0 == 1000.9999999999999).
   * Conversions without scaling, e.g., (int) 1.5, are not rounded.
   */
  public static void printScaledConverted(
      IndentPrinter printer,
      SymTypeExpression targetValueType,
      SymTypeExpression scaledValueType,
      CodeGenPrintAction scaledExprPrintAction
  ) {
    if (isIntegralType(targetValueType) && !isIntegralType(scaledValueType)) {
      SymTypeExpression doubleType =
          SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.DOUBLE);
      SymTypeExpression longType =
          SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.LONG);
      printConverted(printer, targetValueType, longType, p -> {
        p.print("java.lang.Math.round(");
        printConverted(p, doubleType, scaledValueType, scaledExprPrintAction);
        p.print(")");
      });
    }
    else {
      printConverted(printer, targetValueType, scaledValueType,
          scaledExprPrintAction
      );
    }
  }

  /**
   * prints, e.g., {@code * 1000.0} for km -> m,
   * and {@code / 1000.0} for m -> km,
   * as dividing by 1000 is more precise than multiplying by 0.001
   */
  protected void printScaling(IndentPrinter printer, BigDecimal factor) {
    if (factor.compareTo(BigDecimal.ONE) == 0) {
      // no scaling, e.g., ºC -> K
    }
    else if (factor.compareTo(BigDecimal.ONE) > 0) {
      printer.print(" * ");
      printer.print(Double.toString(factor.doubleValue()));
    }
    else {
      BigDecimal divisor =
          BigDecimal.ONE.divide(factor, MathContext.DECIMAL128);
      printer.print(" / ");
      printer.print(Double.toString(divisor.doubleValue()));
    }
  }

  /**
   * prints, e.g., {@code + 273.15} for ºC -> K,
   * and {@code - 273.15} for K -> ºC
   */
  protected void printOffset(IndentPrinter printer, BigDecimal offset) {
    if (offset.signum() > 0) {
      printer.print(" + ");
      printer.print(Double.toString(offset.doubleValue()));
    }
    else if (offset.signum() < 0) {
      printer.print(" - ");
      printer.print(Double.toString(offset.negate().doubleValue()));
    }
  }

  // Helper

  protected boolean isNumericWithOptionalSIUnit(SymTypeExpression type) {
    return hasSIUnit(type) || isNumericType(type);
  }

}
