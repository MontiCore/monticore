// (c) https://github.com/MontiCore/monticore
package de.monticore.types3.generics.bounds;

import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.generics.util.BoundIncorporation;
import de.monticore.types3.generics.util.BoundResolution;

import java.util.List;

/**
 * Bounds limit the range of types allowed
 * for the instantiation of inference variables.
 * The (combination of) Bounds can lead to further
 * {@link de.monticore.types3.generics.constraints.Constraint}s,
 * by means of {@link BoundIncorporation},
 * which, in turn, can lead to new Bounds.
 * After collecting all bounds,
 * the instantiations will then be calculated by
 * {@link BoundResolution}.
 */
public abstract class Bound {

  public boolean isCaptureBound() {
    return false;
  }

  public boolean isSubTypingBound() {
    return false;
  }

  public boolean isTypeEqualityBound() {
    return false;
  }

  public boolean isUnsatisfiableBound() {
    return false;
  }

  public abstract boolean deepEquals(Bound other);

  /**
   * returns a human-readable String, e.g., for the log
   */
  public abstract String print();

  /**
   * Helper function;
   *
   * @return the types included in the bounds (most have two).
   */
  public abstract List<SymTypeExpression> getIncludedTypes();

}
