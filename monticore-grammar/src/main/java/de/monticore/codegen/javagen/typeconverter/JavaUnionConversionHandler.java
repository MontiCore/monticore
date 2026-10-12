/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.javagen.typeconverter;

import de.monticore.codegen.CodeGenPrintAction;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfSIUnit;
import de.monticore.types.check.SymTypeOfUnion;
import de.monticore.types3.util.SIUnitTypeRelations;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static de.monticore.codegen.CodeGenSymTypeExpressionConverter.printConverted;
import static de.monticore.types3.SymTypeRelations.isCompatible;
import static de.monticore.types3.SymTypeRelations.isSubTypeOf;

/**
 * Conversions to unions, e.g., int -> int | String,
 * using the compatible type of the union,
 * e.g., {@code [km]<double> -> [m]<double> | [s]<double>}
 * converts to {@code [m]<double>}.
 * Unions of different SIUnits of the same dimension,
 * e.g., {@code [km]<double> | [m]<double>}, are not supported,
 * as the unit of the value cannot be determined at runtime.
 * Should have the lowest priority.
 */
public class JavaUnionConversionHandler
    extends AbstractJavaTypeConverter {

  @Override
  public boolean tryPrintConverted(
      IndentPrinter printer,
      SymTypeExpression targetType,
      SymTypeExpression sourceType,
      CodeGenPrintAction sourceExprPrintAction
  ) {
    // conversions between unions are not supported yet
    if (!targetType.isUnionType() || sourceType.isUnionType()) {
      return false;
    }
    if (containsSIUnitsOfSameDimension(targetType.asUnionType())) {
      Log.error("0xFD242 Cannot convert " + sourceType.printFullName()
          + " to " + targetType.printFullName()
          + ", as unions of different SIUnits of the same dimension"
          + " are not supported."
      );
      sourceExprPrintAction.print(printer);
      return true;
    }
    Optional<SymTypeExpression> unionizedTargetType =
        getUnionizedTargetType(targetType.asUnionType().getUnionizedTypeSet()
            .stream().toList(), sourceType
        );
    if (unionizedTargetType.isEmpty()) {
      Log.error("0xFD241 Cannot convert " + sourceType.printFullName()
          + " to " + targetType.printFullName()
          + ", as no unique type of the union has been found to convert to."
      );
      sourceExprPrintAction.print(printer);
      return true;
    }
    printJavaCasted(printer, targetType, p ->
        printConverted(p, unionizedTargetType.get(),
            sourceType, sourceExprPrintAction
        )
    );
    return true;
  }

  protected boolean containsSIUnitsOfSameDimension(SymTypeOfUnion union) {
    List<SymTypeOfSIUnit> siUnits = union.getUnionizedTypeSet().stream()
        .filter(SIUnitTypeRelations::hasSIUnit)
        .map(SIUnitTypeRelations::getSIUnit)
        .collect(Collectors.toList());
    for (int i = 0; i < siUnits.size(); i++) {
      for (int j = i + 1; j < siUnits.size(); j++) {
        if (SIUnitTypeRelations.isOfSameDimension(siUnits.get(i), siUnits.get(j))
            && !siUnits.get(i).deepEquals(siUnits.get(j))) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * @return the same type if available, the most specific compatible otherwise
   */
  protected Optional<SymTypeExpression> getUnionizedTargetType(
      List<SymTypeExpression> unionizedTypes,
      SymTypeExpression sourceType
  ) {
    Optional<SymTypeExpression> sameType = unionizedTypes.stream()
        .filter(t -> t.deepEquals(sourceType))
        .findFirst();
    if (sameType.isPresent()) {
      return sameType;
    }
    List<SymTypeExpression> compatibleTypes = unionizedTypes.stream()
        .filter(t -> isCompatible(t, sourceType))
        .collect(Collectors.toList());
    return compatibleTypes.stream()
        .filter(t -> compatibleTypes.stream().allMatch(o -> isSubTypeOf(t, o)))
        .findFirst();
  }

}
