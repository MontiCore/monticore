/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.check.SymTypeExpression;

public interface ICodeGenSymTypeExpressionConversionHandler {

  /**
   * The types are not normalized,
   * as normalization removes, e.g., SIUnit prefixes (km -> m).
   *
   * @return whether this handler printed the conversion
   */
  boolean tryPrintConverted(
      IndentPrinter printer,
      SymTypeExpression modelTargetType,
      SymTypeExpression modelSourceType,
      CodeGenPrintAction sourceExprPrintAction
  );

}
