// (c) https://github.com/MontiCore/monticore
package de.monticore.codegen.javagen.typeconverter;

import de.monticore.codegen.CodeGenPrintAction;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.check.SymTypeExpression;

import static de.monticore.types3.SymTypeRelations.box;
import static de.monticore.types3.SymTypeRelations.isBoolean;
import static de.monticore.types3.SymTypeRelations.isSubTypeOf;

/**
 * Conversions between Java `boolean` and `Boolean` (or its supertypes)
 */
public class JavaBooleanConversionHandler
    extends AbstractJavaTypeConverter {

  @Override
  public boolean tryPrintConverted(
      IndentPrinter printer,
      SymTypeExpression modelTargetType,
      SymTypeExpression modelSourceType,
      CodeGenPrintAction sourceExrPrintAction
  ) {
    if (
        (
            isBoolean(modelSourceType) &&
                isSubTypeOf(box(modelSourceType), modelTargetType)
        ) || (
            isBoolean(modelTargetType) &&
                isSubTypeOf(box(modelTargetType), modelSourceType)
        )
    ) {
      printJavaCasted(printer, modelTargetType, sourceExrPrintAction);
      return true;
    }
    return false;
  }

}
