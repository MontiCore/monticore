/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen;

import com.google.common.base.Preconditions;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.logging.Log;

import java.util.Collection;

import static de.monticore.types3.SymTypeRelations.normalize;

public abstract class CodeGenSymTypeExpressionConverter {

  protected static CodeGenSymTypeExpressionConverter delegate;

  protected Collection<ICodeGenSymTypeExpressionConversionHandler> conversionHandlers;

  protected Collection<ICodeGenSymTypeExpressionConversionHandler> getConversionHandlers() {
    return this.conversionHandlers;
  }

  protected void setConversionHandlers(
      Collection<ICodeGenSymTypeExpressionConversionHandler> conversionHandlers
  ) {
    this.conversionHandlers = conversionHandlers;
  }

  // methods

  /**
   * calculates the target language expression
   * that converts a given expression
   * to the provided type.
   *
   * @param modelTargetType target MontiCore expression type
   * @param modelSourceType source MontiCore expression type
   */
  public static void printConverted(
      IndentPrinter printer,
      SymTypeExpression modelTargetType,
      SymTypeExpression modelSourceType,
      CodeGenPrintAction sourceExprPrintAction
  ) {
    getDelegate()._printConverted(printer, modelTargetType, modelSourceType, sourceExprPrintAction);
  }

  protected void _printConverted(
      IndentPrinter printer,
      SymTypeExpression modelTargetType,
      SymTypeExpression modelSourceType,
      CodeGenPrintAction sourceExprPrintAction
  ) {
    SymTypeExpression targetNormalized = normalize(modelTargetType);
    SymTypeExpression sourceNormalized = normalize(modelSourceType);
    // temporary workaround due to odd SymbolSurrogate behavior
    if (targetNormalized.deepEquals(sourceNormalized) || sourceNormalized.deepEquals(targetNormalized)) {
      sourceExprPrintAction.print(printer);
    }
    else {
      int numTimesPrinted = 0;
      for (ICodeGenSymTypeExpressionConversionHandler conversionHandler : getConversionHandlers()) {
        if (conversionHandler.tryPrintConverted(printer, targetNormalized, sourceNormalized, sourceExprPrintAction)) {
          numTimesPrinted++;
        }
      }
      if (numTimesPrinted == 0) {
        Log.warn("0xFD220 Could not convert " + modelSourceType.printFullName()
            + " to " + modelTargetType.printFullName()
        );
        sourceExprPrintAction.print(printer);
      }
      else if (numTimesPrinted > 1) {
        Log.error("0xFD222 internal error: "
            + "Found multiple conversions to convert "
            + modelSourceType.printFullName() + " to "
            + modelTargetType.printFullName() + "."
            + " This should never happen!"
        );
      }
    }
  }

  // static delegate
  public static void reset() {
    CodeGenSymTypeExpressionConverter.delegate = null;
  }

  protected static void setDelegate(CodeGenSymTypeExpressionConverter newDelegate) {
    CodeGenSymTypeExpressionConverter.delegate =
        Preconditions.checkNotNull(newDelegate);
  }

  protected static CodeGenSymTypeExpressionConverter getDelegate() {
    Preconditions.checkNotNull(CodeGenSymTypeExpressionConverter.delegate,
        "0xFDB11 internal error: "
            + "no CodeGenSymTypeExpressionConverter has been init()-ialized."
    );
    return CodeGenSymTypeExpressionConverter.delegate;
  }

}
