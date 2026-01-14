// (c) https://github.com/MontiCore/monticore
package de.monticore.types3.generics.constraints;

import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;

import java.util.Comparator;

import static de.monticore.types3.generics.bounds.BoundComparator.compareBounds;
import static de.monticore.types3.util.SymTypeExpressionComparator.compareSymTypeExpressions;

public class ConstraintComparator implements Comparator<Constraint> {

  protected static ConstraintComparator delegate;

  public static int compareConstraints(Constraint c1, Constraint c2) {
    return getDelegate().compare(c1, c2);
  }

  @Override
  public int compare(Constraint c1, Constraint c2) {
    Preconditions.checkNotNull(c1);
    Preconditions.checkNotNull(c2);

    int res;

    int orderOfSubType1 = getOrderOfSubType(c1);
    int orderOfSubType2 = getOrderOfSubType(c2);
    int subTypeOrdering = Integer.compare(orderOfSubType1, orderOfSubType2);

    if (subTypeOrdering != 0) {
      res = subTypeOrdering;
    }
    else if (c1.isBoundWrapperConstraint() && c2.isBoundWrapperConstraint()) {
      res = compareBoundWrapperConstraints(
          c1.asBoundWrapperConstraint(), c2.asBoundWrapperConstraint());
    }
    else if (c1.isExpressionCompatibilityConstraint() && c2.isExpressionCompatibilityConstraint()) {
      res = compareExpressionCompatibilityConstraints(
          c1.asExpressionCompatibilityConstraint(),
          c2.asExpressionCompatibilityConstraint()
      );
    }
    else if (c1.isSubTypingConstraint() && c2.isSubTypingConstraint()) {
      res = compareSubTypingConstraints(
          c1.asSubTypingConstraint(),
          c2.asSubTypingConstraint()
      );
    }
    else if (c1.isTypeCompatibilityConstraint() && c2.isTypeCompatibilityConstraint()) {
      res = compareTypeCompatibilityConstraints(
          c1.asTypeCompatibilityConstraint(),
          c2.asTypeCompatibilityConstraint()
      );
    }
    else if (c1.isTypeEqualityConstraint() && c2.isTypeEqualityConstraint()) {
      res = compareTypeEqualityConstraints(
          c1.asTypeEqualityConstraint(),
          c2.asTypeEqualityConstraint()
      );
    }
    else {
      throwUnimplemented();
      res = -42;
    }

    return res;
  }

  protected int getOrderOfSubType(Constraint constraint) {
    if (constraint.isBoundWrapperConstraint()) {
      return 0;
    }
    else if (constraint.isTypeEqualityConstraint()) {
      return 1;
    }
    else if (constraint.isSubTypingConstraint()) {
      return 2;
    }
    else if (constraint.isTypeCompatibilityConstraint()) {
      return 3;
    }
    else if (constraint.isExpressionCompatibilityConstraint()) {
      return 4;
    }
    else {
      throwUnimplemented();
      return -42;
    }
  }

  protected int compareBoundWrapperConstraints(
      BoundWrapperConstraint c1,
      BoundWrapperConstraint c2
  ) {
    return compareBounds(c1.getBound(), c2.getBound());
  }

  protected int compareExpressionCompatibilityConstraints(
      ExpressionCompatibilityConstraint c1,
      ExpressionCompatibilityConstraint c2
  ) {
    int res;
    int startComp = c1.getExpr().get_SourcePositionStart().compareTo(
        c2.getExpr().get_SourcePositionStart()
    );
    if (startComp != 0) {
      res = startComp;
    }
    else {
      int endComp = c1.getExpr().get_SourcePositionEnd().compareTo(
          c2.getExpr().get_SourcePositionEnd()
      );
      if (endComp != 0) {
        res = endComp;
      }
      else {
        res = compareSymTypeExpressions(c1.getTargetType(), c2.getTargetType());
      }
    }
    return res;
  }

  protected int compareSubTypingConstraints(
      SubTypingConstraint c1,
      SubTypingConstraint c2
  ) {
    int res;
    int subTypeComp = compareSymTypeExpressions(c1.getSubType(), c2.getSubType());
    if (subTypeComp != 0) {
      res = subTypeComp;
    }
    else {
      res = compareSymTypeExpressions(c1.getSuperType(), c2.getSuperType());
    }
    return res;
  }

  protected int compareTypeCompatibilityConstraints(
      TypeCompatibilityConstraint c1,
      TypeCompatibilityConstraint c2
  ) {
    int res;
    int sourceComp = compareSymTypeExpressions(c1.getSourceType(), c2.getSourceType());
    if (sourceComp != 0) {
      res = sourceComp;
    }
    else {
      res = compareSymTypeExpressions(c1.getTargetType(), c2.getTargetType());
    }
    return res;
  }

  protected int compareTypeEqualityConstraints(
      TypeEqualityConstraint c1,
      TypeEqualityConstraint c2
  ) {
    int res;
    int first = compareSymTypeExpressions(c1.getFirstType(), c2.getFirstType());
    if (first != 0) {
      res = first;
    }
    else {
      res = compareSymTypeExpressions(c1.getSecondType(), c2.getSecondType());
    }
    return res;
  }

  // helper

  /**
   * This is not expected to be ever called.
   */
  protected void throwUnimplemented() throws UnsupportedOperationException {
    throw new UnsupportedOperationException(
        "0xFD663 unimplemented comparison."
    );
  }

  // static delegate

  public static void init() {
    Log.trace("init default ConstraintComparator", "TypeCheck setup");
    setDelegate(new ConstraintComparator());
  }

  public static void reset() {
    ConstraintComparator.delegate = null;
  }

  protected static void setDelegate(ConstraintComparator newDelegate) {
    ConstraintComparator.delegate = Preconditions.checkNotNull(newDelegate);
  }

  protected static ConstraintComparator getDelegate() {
    if (ConstraintComparator.delegate == null) {
      init();
    }
    return ConstraintComparator.delegate;
  }

}
