/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.check.SymTypeExpression;

public interface ICodeGenSymTypeExpressionConversionHandler {

  boolean tryPrintConverted(
      IndentPrinter printer,
      SymTypeExpression modelTargetType,
      SymTypeExpression modelSourceType,
      CodeGenPrintAction sourceExprPrintAction
  );

}
