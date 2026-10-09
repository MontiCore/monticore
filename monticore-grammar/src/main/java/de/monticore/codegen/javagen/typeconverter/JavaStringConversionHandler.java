/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.javagen.typeconverter;

import de.monticore.codegen.CodeGenPrintAction;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.check.SymTypeExpression;

import static de.monticore.types3.SymTypeRelations.normalize;
import static de.monticore.types3.SymTypeRelations.isStringOrSubType;

public class JavaStringConversionHandler extends AbstractJavaTypeConverter {

  @Override
  public boolean tryPrintConverted(
      IndentPrinter printer,
      SymTypeExpression nonNormalizedTargetType,
      SymTypeExpression nonNormalizedSourceType,
      CodeGenPrintAction sourceExprPrintAction
  ) {
    SymTypeExpression modelTargetType = normalize(nonNormalizedTargetType);
    SymTypeExpression modelSourceType = normalize(nonNormalizedSourceType);
    if (isStringOrSubType(modelTargetType) && isStringOrSubType(modelSourceType)) {
      sourceExprPrintAction.print(printer);
      return true;
    }
    return false;
  }

}
