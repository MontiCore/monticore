// (c) https://github.com/MontiCore/monticore
package de.monticore.codegen.javagen.typeconverter;

import de.monticore.codegen.CodeGenPrintAction;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.check.SymTypeExpression;

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
      SymTypeExpression targetType,
      SymTypeExpression sourceType,
      CodeGenPrintAction sourceExprPrintAction
  ) {
    if ((targetType.isObjectType() || targetType.isGenericType()) &&
        (sourceType.isObjectType() || sourceType.isGenericType())
    ) {
      printJavaCasted(printer, targetType, sourceExprPrintAction);
      return true;
    }
    return false;
  }

}
