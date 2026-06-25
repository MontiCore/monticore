// (c) https://github.com/MontiCore/monticore
package de.monticore.codegen.javagen.typeconverter;

import de.monticore.codegen.CodeGenPrintAction;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.check.SymTypeExpression;

import static de.monticore.types3.SymTypeRelations.isNumericType;
import static de.monticore.types3.SymTypeRelations.isStringOrSubType;

/**
 * Conversions between Objects (including generics)
 * Note: OCL collection types are not supported yet
 * (but they also do not exist yet, e.g., FList)
 */
public class JavaObjectConversionHandler
    extends AbstractJavaTypeConverter {

  @Override
  public boolean tryPrintConverted(
      IndentPrinter printer,
      SymTypeExpression modelTargetType,
      SymTypeExpression modelSourceType,
      CodeGenPrintAction sourceExprPrintAction
  ) {
    if (// Is source and target an object type
      ((modelTargetType.isObjectType() || modelTargetType.isGenericType()) && (modelSourceType.isObjectType() || modelSourceType.isGenericType()) ||
        // Is source a string but target an object type
        (modelTargetType.isObjectType() || modelTargetType.isGenericType()) && isStringOrSubType(modelSourceType) ||
        // Is source numeric and target Object (but not numeric -> excluded with overlap numericConversionHandler). E.g. double -> Object
        (modelTargetType.isObjectType() || modelTargetType.isGenericType()) && isNumericType(modelSourceType)) &&
        // Avoid overlap StringConversionHandler
        !(isStringOrSubType(modelTargetType) && isStringOrSubType(modelSourceType)) &&
        // Avoid overlap NumericConversionHandler
        !(isNumericType(modelTargetType) && isNumericType(modelSourceType))
    ) {
      printJavaCasted(printer, modelTargetType, sourceExprPrintAction);
      return true;
    }
    return false;
  }

}
