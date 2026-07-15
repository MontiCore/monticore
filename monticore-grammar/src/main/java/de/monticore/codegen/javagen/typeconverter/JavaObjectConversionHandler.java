// (c) https://github.com/MontiCore/monticore
package de.monticore.codegen.javagen.typeconverter;

import de.monticore.codegen.CodeGenPrintAction;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.check.SymTypeExpression;

/**
 * Conversions between Objects (including generics)
 * Note: OCL collection types are not supported yet
 * (but they also do not exist yet, e.g., FList)
 * <p>
 * One of the last Type converters in order of priority.
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
    boolean sourceIsObject =
        modelSourceType.isObjectType() || modelSourceType.isGenericType();
    boolean targetIsObject =
        modelTargetType.isObjectType() || modelTargetType.isGenericType();
    if (sourceIsObject && targetIsObject) {
      printJavaCasted(printer, modelTargetType, sourceExprPrintAction);
      return true;
    }
    return false;
  }

}
