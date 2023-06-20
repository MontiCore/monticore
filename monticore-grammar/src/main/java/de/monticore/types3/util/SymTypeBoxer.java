// (c) https://github.com/MontiCore/monticore
package de.monticore.types3.util;

import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.SymTypeRelations;

/**
 * boxes and unboxes Symtypes
 * delegate of SymTypeRelations
 */
public class SymTypeBoxer extends SymTypeRelations {

  protected SymTypeBoxingVisitor boxingVisitor;

  protected SymTypeUnboxingVisitor unboxingVisitor;

  public SymTypeBoxer() {
    // default values
    this.boxingVisitor = new SymTypeBoxingVisitor();
    this.unboxingVisitor = new SymTypeUnboxingVisitor();
  }

  @Override
  public SymTypeExpression box(SymTypeExpression unboxed) {
    return boxingVisitor.calculate(unboxed);
  }

  @Override
  public SymTypeExpression unbox(SymTypeExpression boxed) {
    return unboxingVisitor.calculate(boxed);
  }

}
