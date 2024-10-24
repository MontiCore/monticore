// (c) https://github.com/MontiCore/monticore
package de.monticore.types3.generics.util;

import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.generics.bounds.Bound;
import de.monticore.types3.generics.constraints.BoundWrapperConstraint;
import de.monticore.types3.generics.constraints.Constraint;
import de.monticore.types3.generics.constraints.SubTypingConstraint;
import de.monticore.types3.generics.constraints.TypeCompatibilityConstraint;
import de.monticore.types3.generics.constraints.TypeEqualityConstraint;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ConstraintReduction {

  /**
   * the name to be used for Log.info, etc.
   */
  protected static final String LOG_NAME = "ConstraintReduction";

  protected static ConstraintReduction delegate;

  public static void init() {
    Log.trace("init default ConstraintReduction", "TypeCheck setup");
    ConstraintReduction.delegate = new ConstraintReduction();
  }

  protected static ConstraintReduction getDelegate() {
    if (delegate == null) {
      init();
    }
    return delegate;
  }

  /**
   * reduces constraints to bounds
   * the resulting set of bounds may not be valid
   */
  public static List<Bound> reduce(List<Constraint> constraints) {
    return getDelegate().calculateReduce(constraints);
  }

  public static List<Bound> reduce(Constraint constraint) {
    return reduce(List.of(constraint));
  }

  protected List<Bound> calculateReduce(List<Constraint> constraintsIn) {
    // shortcut to reduce log:
    if (constraintsIn.isEmpty()) {
      return Collections.emptyList();
    }
    Log.trace("START reducing constraints:" + System.lineSeparator()
            + this.printConstraints(constraintsIn),
        LOG_NAME
    );
    List<Bound> bounds = new ArrayList<>();
    // no need to calculate further if the set of bounds cannot be satisfied
    List<Constraint> constraints = new ArrayList<>(constraintsIn);
    while (!constraints.isEmpty()
        && bounds.stream().noneMatch(Bound::isUnsatisfiableBound)
    ) {
      Constraint constraint = constraints.get(0);
      constraints.remove(constraint);
      List<Bound> newBounds;
      Log.trace("reducing: " + constraint.print(), LOG_NAME);
      if (constraint.isSubTypingConstraint()) {
        newBounds = reduce(((SubTypingConstraint) constraint));
      }
      else if (constraint.isTypeCompatibilityConstraint()) {
        newBounds = reduce((TypeCompatibilityConstraint) constraint);
      }
      else if (constraint.isTypeEqualityConstraint()) {
        newBounds = reduce((TypeEqualityConstraint) constraint);
      }
      else if (constraint.isBoundWrapperConstraint()) {
        newBounds = Collections.singletonList(
            ((BoundWrapperConstraint) constraint).getBound()
        );
      }
      else {
        Log.error("0xFD070 internal error:"
            + "unexpected constraint " + constraint.print()
        );
        newBounds = Collections.emptyList();
      }
      bounds.addAll(newBounds);
    }
    Log.trace("END reduced constraints:" + System.lineSeparator()
            + printConstraints(constraintsIn) + System.lineSeparator()
            + (bounds.isEmpty() ? "to no bounds." : "to bounds: "
            + System.lineSeparator() + printBounds(bounds)),
        LOG_NAME
    );
    return bounds;
  }

  /**
   * based on Java Spec 20 18.2.2
   */
  protected List<Bound> reduce(TypeCompatibilityConstraint constraint) {
    return SymTypeRelations.constrainCompatible(
        constraint.getTargetType(), constraint.getSourceType()
    );
  }

  /**
   * based on Java Spec 20 18.2.3
   */
  protected List<Bound> reduce(SubTypingConstraint constraint) {
    return SymTypeRelations.constrainSubTypeOf(
        constraint.getSubType(), constraint.getSuperType()
    );
  }

  protected List<Bound> reduce(TypeEqualityConstraint constraint) {
    return SymTypeRelations.constrainSameType(
        constraint.getFirstType(), constraint.getSecondType()
    );
  }

  // Helper

  protected String printConstraints(List<Constraint> constraints) {
    return constraints.stream()
        .map(Constraint::print)
        .collect(Collectors.joining(System.lineSeparator()));
  }

  protected String printBounds(List<Bound> constraints) {
    return constraints.stream()
        .map(Bound::print)
        .collect(Collectors.joining(System.lineSeparator()));
  }
}
