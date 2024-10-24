// (c) https://github.com/MontiCore/monticore
package de.monticore.types3.util;

import de.monticore.types.check.*;
import de.monticore.types3.ISymTypeVisitor;

import java.util.function.Predicate;

/**
 * a visitor that is a predicate over SymTypeExpressions
 */
public abstract class SymTypePredicateVisitor
    implements ISymTypeVisitor, Predicate<SymTypeExpression> {

  protected boolean result;

  protected void setResult(boolean result) {
    this.result = result;
  }

  /**
   * sets the default value
   */
  abstract protected void setDefaultValue();

  /**
   * uses this visitor with the provided symType and returns the result.
   * it is reset during the process.
   */
  public boolean test(SymTypeExpression symType) {
    setDefaultValue();
    symType.accept(this);
    return result;
  }

}
