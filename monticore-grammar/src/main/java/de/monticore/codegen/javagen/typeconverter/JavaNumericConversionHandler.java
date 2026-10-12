// (c) https://github.com/MontiCore/monticore
package de.monticore.codegen.javagen.typeconverter;

import de.monticore.codegen.CodeGenPrintAction;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.check.SymTypeExpression;

import static de.monticore.types3.SymTypeRelations.isNumericType;
import static de.monticore.types3.SymTypeRelations.unbox;

/**
 * Conversions between Java numeric types
 */
public class JavaNumericConversionHandler
    extends AbstractJavaTypeConverter {

  @Override
  public boolean tryPrintConverted(
      IndentPrinter printer,
      SymTypeExpression modelTargetType,
      SymTypeExpression modelSourceType,
      CodeGenPrintAction sourceExprPrintAction
  ) {
    if (isNumericType(modelTargetType) && isNumericType(modelSourceType)) {
      // unbox iff required
      CodeGenPrintAction printUnboxedAction = modelSourceType.isPrimitive() ?
          sourceExprPrintAction :
          p -> printJavaCasted(p, unbox(modelSourceType), sourceExprPrintAction);
      // cast as primitive
      CodeGenPrintAction printCastedAction =
          p -> printJavaCasted(p, unbox(modelTargetType), printUnboxedAction);
      // box iff required
      CodeGenPrintAction printAsTargetTypeAction = modelTargetType.isPrimitive() ?
          printCastedAction :
          p -> printJavaCasted(p, modelTargetType, printCastedAction);
      // actually print
      printAsTargetTypeAction.print(printer);
      return true;
    }
    return false;
  }

}
