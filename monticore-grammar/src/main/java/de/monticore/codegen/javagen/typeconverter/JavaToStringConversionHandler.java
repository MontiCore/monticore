// (c) https://github.com/MontiCore/monticore
package de.monticore.codegen.javagen.typeconverter;

import de.monticore.codegen.CodeGenPrintAction;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.check.SymTypeExpression;

import static de.monticore.types3.SymTypeRelations.isStringOrSubType;

/**
 * Conversions of Objects (including generics) to string
 */
public class JavaToStringConversionHandler
    extends AbstractJavaTypeConverter {

  @Override
  public boolean tryPrintConverted(
      IndentPrinter printer,
      SymTypeExpression modelTargetType,
      SymTypeExpression modelSourceType,
      CodeGenPrintAction sourceExprPrintAction
  ) {
    if (// Target Type is String:
        isStringOrSubType(modelTargetType) &&
        // Avoid overlap with Object conversion handler:
        !(modelTargetType.isObjectType() || modelTargetType.isGenericType()) && (modelSourceType.isObjectType() || modelSourceType.isGenericType()) &&
        // Avoid overlap ObjectConversionHandler
        !((modelTargetType.isObjectType() || modelTargetType.isGenericType()) && isStringOrSubType(modelSourceType)) &&
        // Avoid overlap with String conversion Handler
        !(isStringOrSubType(modelTargetType) && isStringOrSubType(modelSourceType))
    ) {
      printer.print("String.valueOf(");
      sourceExprPrintAction.print(printer);
      printer.print(")");
      return true;
    }
    return false;
  }

}
