/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.javagen;

import de.monticore.types.check.SymTypeExpression;

import java.util.List;

/**
 * Prints SymTypeExpressions to Java compatible Strings.
 * Any type arguments are erased.
 */
public class SymTypeExpressionTypeErasedJavaPrinterVisitor
    extends SymTypeExpressionJavaPrinterVisitor {

  /**
   * prints {@code <?,?,?...>}
   */
  @Override
  protected void printTypeArguments(List<SymTypeExpression> typeArgs) {
    int amount = typeArgs.size();
    if (amount == 0) {
      return;
    }
    getPrint().append('<');
    for (int i = 0; i < amount; i++) {
      getPrint().append("?");
      if (i < amount - 1) {
        getPrint().append(',');
      }
    }
    getPrint().append('>');
  }

}
