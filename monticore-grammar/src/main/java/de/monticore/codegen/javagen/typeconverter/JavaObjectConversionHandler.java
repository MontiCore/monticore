// (c) https://github.com/MontiCore/monticore
package de.monticore.codegen.javagen.typeconverter;

import de.monticore.codegen.CodeGenPrintAction;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.check.SymTypeExpression;

import static de.monticore.codegen.javagen.SymTypeExpression2JavaConverter.getAsJavaType;

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
    if ((modelTargetType.isObjectType() || modelTargetType.isGenericType()) &&
        (modelSourceType.isObjectType() || modelSourceType.isGenericType())
    ) {
      printJavaCasted(printer, getAsJavaType(modelTargetType), sourceExprPrintAction);
      return true;
    }
    return false;
  }

}
