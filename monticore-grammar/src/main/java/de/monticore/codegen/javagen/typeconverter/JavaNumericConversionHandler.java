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
      SymTypeExpression targetType,
      SymTypeExpression sourceType,
      CodeGenPrintAction sourceExprPrintAction
  ) {
    if (isNumericType(targetType) && isNumericType(sourceType)) {
      // unbox iff required
      CodeGenPrintAction printUnboxedAction = sourceType.isPrimitive() ?
          sourceExprPrintAction :
          p -> printJavaCasted(p, unbox(sourceType), sourceExprPrintAction);
      // cast as primitive
      CodeGenPrintAction printCastedAction =
          p -> printJavaCasted(p, unbox(targetType), printUnboxedAction);
      // box iff required
      CodeGenPrintAction printAsTargetTypeAction = targetType.isPrimitive() ?
          printCastedAction :
          p -> printJavaCasted(p, targetType, printCastedAction);
      // actually print
      printAsTargetTypeAction.print(printer);
      return true;
    }
    return false;
  }

}
