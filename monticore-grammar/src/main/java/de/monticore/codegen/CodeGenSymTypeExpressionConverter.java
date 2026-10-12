/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen;

import com.google.common.base.Preconditions;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.logging.Log;

import java.util.Collection;
import java.util.List;

import static de.monticore.types3.SymTypeRelations.normalize;

public abstract class CodeGenSymTypeExpressionConverter {

  protected static CodeGenSymTypeExpressionConverter delegate;

  protected List<Collection<ICodeGenSymTypeExpressionConversionHandler>>
      conversionHandlersByPriority;

  /**
   * The actual handlers managing the conversion.
   *
   * @return A list of collections of handlers;
   *     the collections are ordered by priority.
   *     If a handler with a higher priority handles the conversion,
   *     the lower priority handlers are not called.
   *     If two handlers with the same priority handle the conversion,
   *     the handlers are not configured correctly.
   */
  protected List<Collection<ICodeGenSymTypeExpressionConversionHandler>>
  getConversionHandlersByPriority() {
    return this.conversionHandlersByPriority;
  }

  protected void setConversionHandlersByPriority(
      List<Collection<ICodeGenSymTypeExpressionConversionHandler>> conversionHandlersByPriority
  ) {
    this.conversionHandlersByPriority = conversionHandlersByPriority;
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
    int numTimesPrinted = 0;
    for (
        Collection<ICodeGenSymTypeExpressionConversionHandler>
            conversionHandlers : getConversionHandlersByPriority()
    ) {
      for (
          ICodeGenSymTypeExpressionConversionHandler
              conversionHandler : conversionHandlers
      ) {
        if (conversionHandler.tryPrintConverted(
            printer,
            targetNormalized,
            sourceNormalized,
            sourceExprPrintAction)
        ) {
          numTimesPrinted++;
        }
      }
      if (numTimesPrinted == 1) {
        break;
      }
      else if (numTimesPrinted > 1) {
        Log.error("0xFD222 internal error: "
            + "Found multiple conversions to convert "
            + modelSourceType.printFullName() + " to "
            + modelTargetType.printFullName() + "."
            + " This should never happen!"
        );
        break;
      }
    }

    if (numTimesPrinted == 0) {
      Log.warn("0xFD220 Could not convert " + modelSourceType.printFullName()
          + " to " + modelTargetType.printFullName()
      );
      sourceExprPrintAction.print(printer);
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
