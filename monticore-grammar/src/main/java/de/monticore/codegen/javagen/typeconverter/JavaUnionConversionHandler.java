/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.javagen.typeconverter;

import de.monticore.codegen.CodeGenPrintAction;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static de.monticore.codegen.CodeGenSymTypeExpressionConverter.printConverted;
import static de.monticore.types3.SymTypeRelations.isCompatible;
import static de.monticore.types3.SymTypeRelations.isSubTypeOf;
import static de.monticore.types3.SymTypeRelations.normalize;

/**
 * Conversions to unions, e.g., int -> int | String,
 * using the compatible type of the union,
 * e.g., [km]<int> -> [m]<int> | [s]<int> converts to [m]<int>.
 * Should have the lowest priority.
 */
public class JavaUnionConversionHandler
    extends AbstractJavaTypeConverter {

  @Override
  public boolean tryPrintConverted(
      IndentPrinter printer,
      SymTypeExpression nonNormalizedTargetType,
      SymTypeExpression nonNormalizedSourceType,
      CodeGenPrintAction sourceExprPrintAction
  ) {
    SymTypeExpression targetType = normalize(nonNormalizedTargetType);
    SymTypeExpression sourceType = normalize(nonNormalizedSourceType);
    // conversions between unions are not supported yet
    if (!targetType.isUnionType() || sourceType.isUnionType()) {
      return false;
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
            nonNormalizedSourceType, sourceExprPrintAction
        )
    );
    return true;
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
