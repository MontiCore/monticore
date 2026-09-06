// (c) https://github.com/MontiCore/monticore
package de.monticore.codegen.typeconverter;

import de.monticore.codegen.CodeGenPrintAction;
import de.monticore.codegen.javagen.typeconverter.AbstractJavaTypeConverter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.check.SymTypeExpression;

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
      SymTypeExpression modelTargetType,
      SymTypeExpression modelSourceType,
      CodeGenPrintAction sourceExprPrintAction
  ) {
    // temporary workaround due to odd SymbolSurrogate behavior
    if (modelSourceType.deepEquals(modelTargetType) || modelTargetType.deepEquals(modelSourceType)) {
      sourceExprPrintAction.print(printer);
      return true;
    }
    else {
      return false;
    }
  }

}
