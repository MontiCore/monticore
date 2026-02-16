// (c) https://github.com/MontiCore/monticore
package de.monticore.types3.generics.constraints;

import com.google.common.base.Preconditions;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.generics.bounds.Bound;
import java.util.List;

/**
 * Wraps Bounds as Constraints.
 * This is useful if a Bound gets calculated
 * in a context where (usually) Constraints are expected.
 */
public class BoundWrapperConstraint extends Constraint {

  protected Bound bound;

  public BoundWrapperConstraint(
      Bound bound
  ) {
    this.bound = Preconditions.checkNotNull(bound);
  }

  public Bound getBound() {
    return bound;
  }

  @Override
  public boolean isBoundWrapperConstraint() {
    return true;
  }

  @Override
  public BoundWrapperConstraint asBoundWrapperConstraint() {
    return this;
  }

  @Override
  public boolean deepEquals(Constraint other) {
    if (this == other) {
      return true;
    }
    if (!other.isBoundWrapperConstraint()) {
      return false;
    }
    BoundWrapperConstraint otherBoundWrapper = (BoundWrapperConstraint) other;
    return getBound().deepEquals(otherBoundWrapper.getBound());
  }

  @Override
  public String print() {
    return "<Bound: " + getBound().print() + ">";
  }

  @Override
  public List<SymTypeExpression> getIncludedTypes() {
    return getBound().getIncludedTypes();
  }
}
