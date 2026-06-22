/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.javagen;

import de.monticore.types.check.SymTypeOfFunction;
import de.monticore.types.check.SymTypeOfGenerics;
import de.monticore.types.check.SymTypeOfTuple;
import de.se_rwth.commons.logging.Log;

/**
 * Prints SymTypeExpressions in a Java compatible way,
 */
public class SymTypeExpressionTypeErasedJavaPrinterVisitor
    extends SymTypeExpressionJavaPrinterVisitor {

  @Override
  public void visit(SymTypeOfFunction func) {
    // precondition; support could be extended if required
    if (func.isElliptic()) {
      Log.error("0xFD324 internal error:" +
          "No support for elliptic Functions exists yet.");
      return;
    }

    // Main Symbol: Function or Action?
    boolean isFunc = !func.getType().isVoidType();
    if (isFunc) {
      getPrint().append(RTE_PACKAGE).append(".functions.Function");
    }
    else {
      getPrint().append(RTE_PACKAGE).append(".actions.Action");
    }
    getPrint().append(func.sizeArgumentTypes());

    int numArgs = func.sizeArgumentTypes();
    numArgs += isFunc ? 1 : 0;
    printWildcardListInBrackets(numArgs);
  }

  @Override
  public void visit(SymTypeOfGenerics generic) {
    getPrint().append(printTypeSymbol(generic.getTypeInfo()));
    printWildcardListInBrackets(generic.sizeArguments());
  }

  @Override
  public void visit(SymTypeOfTuple tuple) {
    String className = RTE_PACKAGE + ".tuples.Tuple"
        + tuple.sizeTypes();
    getPrint().append(className);
    printWildcardListInBrackets(tuple.sizeTypes());
  }

  // helper

  /**
   * prints {@code <?,?,?...>}
   *
   * @param amount how many wildcards to print
   *               0 will print nothing
   */
  protected void printWildcardListInBrackets(int amount) {
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
