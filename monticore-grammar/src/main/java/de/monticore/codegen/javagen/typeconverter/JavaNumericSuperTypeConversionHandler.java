// (c) https://github.com/MontiCore/monticore
package de.monticore.codegen.javagen.typeconverter;

import de.monticore.codegen.CodeGenPrintAction;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.check.SymTypeExpression;

import static de.monticore.types3.SymTypeRelations.box;
import static de.monticore.types3.SymTypeRelations.isNumericType;
import static de.monticore.types3.SymTypeRelations.isSubTypeOf;

/**
 * Conversions between Java primitive numeric types to their supertypes,
 * e.g, {@code Comparable<Float>}
 *
 * If used, must run after
 * {@link JavaNumericConversionHandler}.
 */
public class JavaNumericSuperTypeConversionHandler
    extends AbstractJavaTypeConverter {

  @Override
  public boolean tryPrintConverted(
      IndentPrinter printer,
      SymTypeExpression modelTargetType,
      SymTypeExpression modelSourceType,
      CodeGenPrintAction sourceExprPrintAction
  ) {
    if (
        (
            modelSourceType.isPrimitive() &&
                isNumericType(modelSourceType) &&
                isSubTypeOf(box(modelSourceType), modelTargetType)
        ) || (
            modelTargetType.isPrimitive() &&
                isNumericType(modelTargetType) &&
                isSubTypeOf(box(modelTargetType), modelSourceType)
        )
    ) {
      printJavaCasted(printer, modelTargetType, sourceExprPrintAction);
      return true;
    }
    return false;
  }

}
