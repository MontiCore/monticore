// (c) https://github.com/MontiCore/monticore
package de.monticore.types3.generics.constraints;

import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.generics.util.ConstraintReduction;

import java.util.List;

/**
 * A Constraint is a statement that needs to hold,
 * to find an instantiation for a generic.
 * Constraints lead to {@link de.monticore.types3.generics.bounds.Bound}s,
 * by means of {@link ConstraintReduction}.
 */
public abstract class Constraint {

  public boolean isBoundWrapperConstraint() {
    return false;
  }

  public boolean isExpressionCompatibilityConstraint() {
    return false;
  }

  public boolean isSubTypingConstraint() {
    return false;
  }

  public boolean isTypeCompatibilityConstraint() {
    return false;
  }

  public boolean isTypeEqualityConstraint() {
    return false;
  }

  public abstract boolean deepEquals(Constraint other);

  /**
   * returns a human-readable String, e.g., for the log
   */
  public abstract String print();

  /**
   * Helper function;
   *
   * @return the types included in the constraints (most have two).
   */
  public abstract List<SymTypeExpression> getIncludedTypes();

}
