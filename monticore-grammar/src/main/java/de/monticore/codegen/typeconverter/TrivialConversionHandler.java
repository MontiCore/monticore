// (c) https://github.com/MontiCore/monticore
package de.monticore.codegen.typeconverter;

import de.monticore.codegen.CodeGenPrintAction;
import de.monticore.codegen.javagen.typeconverter.AbstractJavaTypeConverter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.util.SymTypeCollectionVisitor;

import static de.monticore.types3.SymTypeRelations.normalize;

/**
 * Conversions between the type and itself;
 * Prints nothing (extra)
 * <p>
 * Should have the highest priority.
 */
public class TrivialConversionHandler
    extends AbstractJavaTypeConverter {

  @Override
  public boolean tryPrintConverted(
      IndentPrinter printer,
      SymTypeExpression nonNormalizedTargetType,
      SymTypeExpression nonNormalizedSourceType,
      CodeGenPrintAction sourceExprPrintAction
  ) {
    if (isTrivial(nonNormalizedTargetType, nonNormalizedSourceType)) {
      sourceExprPrintAction.print(printer);
      return true;
    }
    else if (!containsSIUnit(nonNormalizedTargetType)
        && !containsSIUnit(nonNormalizedSourceType)
        && isTrivial(normalize(nonNormalizedTargetType), normalize(nonNormalizedSourceType))
    ) {
      sourceExprPrintAction.print(printer);
      return true;
    }
    else {
      return false;
    }
  }

  protected boolean isTrivial(
      SymTypeExpression targetType,
      SymTypeExpression sourceType
  ) {
    // temporary workaround due to odd SymbolSurrogate behavior
    return sourceType.deepEquals(targetType) || targetType.deepEquals(sourceType);
  }

  protected boolean containsSIUnit(SymTypeExpression type) {
    return !new SymTypeCollectionVisitor().calculate(type,
        t -> t.isSIUnitType() || t.isNumericWithSIUnitType()
    ).isEmpty();
  }

}
