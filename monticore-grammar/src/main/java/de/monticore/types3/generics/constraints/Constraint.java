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

  public BoundWrapperConstraint asBoundWrapperConstraint() {
    throw new UnsupportedOperationException(
        "Tried to convert constraint " + print()
            + " to BoundWrapperConstraint, which it is not."
    );
  }

  public boolean isExpressionCompatibilityConstraint() {
    return false;
  }

  public ExpressionCompatibilityConstraint asExpressionCompatibilityConstraint() {
    throw new UnsupportedOperationException(
        "Tried to convert constraint " + print()
            + " to ExpressionCompatibilityConstraint, which it is not."
    );
  }

  public boolean isSubTypingConstraint() {
    return false;
  }

  public SubTypingConstraint asSubTypingConstraint() {
    throw new UnsupportedOperationException(
        "Tried to convert constraint " + print()
            + " to SubTypingConstraint, which it is not."
    );
  }

  public boolean isTypeCompatibilityConstraint() {
    return false;
  }

  public TypeCompatibilityConstraint asTypeCompatibilityConstraint() {
    throw new UnsupportedOperationException(
        "Tried to convert constraint " + print()
            + " to TypeCompatibilityConstraint, which it is not."
    );
  }

  public boolean isTypeEqualityConstraint() {
    return false;
  }

  public TypeEqualityConstraint asTypeEqualityConstraint() {
    throw new UnsupportedOperationException(
        "Tried to convert constraint " + print()
            + " to TypeEqualityConstraint, which it is not."
    );
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
