// (c) https://github.com/MontiCore/monticore
package de.monticore.types3.generics.bounds;

import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;

import java.util.Comparator;

import static de.monticore.types3.util.SymTypeExpressionComparator.compareSymTypeExpressions;

public class BoundComparator implements Comparator<Bound> {

  protected static BoundComparator delegate;

  public static int compareBounds(Bound b1, Bound b2) {
    return getDelegate().compare(b1, b2);
  }

  @Override
  public int compare(Bound b1, Bound b2) {
    Preconditions.checkNotNull(b1);
    Preconditions.checkNotNull(b2);

    int res;

    int orderOfSubType1 = getOrderOfSubType(b1);
    int orderOfSubType2 = getOrderOfSubType(b2);
    int subTypeOrdering = Integer.compare(orderOfSubType1, orderOfSubType2);

    if (subTypeOrdering != 0) {
      res = subTypeOrdering;
    }
    else if (b1.isCaptureBound() && b2.isCaptureBound()) {
      res = compareCaptureBounds(b1.asCaptureBound(), b2.asCaptureBound());
    }
    else if (b1.isSubTypingBound() && b2.isSubTypingBound()) {
      res = compareSubTypingBounds(b1.asSubTypingBound(), b2.asSubTypingBound());
    }
    else if (b1.isTypeCompatibilityBound() && b2.isTypeCompatibilityBound()) {
      res = compareTypeCompatibilityBounds(b1.asTypeCompatibilityBound(), b2.asTypeCompatibilityBound());
    }
    else if (b1.isTypeEqualityBound() && b2.isTypeEqualityBound()) {
      res = compareTypeEqualityBounds(b1.asTypeEqualityBound(), b2.asTypeEqualityBound());
    }
    else if (b1.isUnsatisfiableBound() && b2.isUnsatisfiableBound()) {
      res = compareUnsatisfiableBounds(b1.asUnsatisfiableBound(), b2.asUnsatisfiableBound());
    }
    else {
      throwUnimplemented();
      res = -42;
    }

    return res;
  }

  protected int getOrderOfSubType(Bound bound) {
    if (bound.isUnsatisfiableBound()) {
      return 0;
    }
    else if (bound.isTypeEqualityBound()) {
      return 1;
    }
    else if (bound.isSubTypingBound()) {
      return 2;
    }
    else if (bound.isTypeCompatibilityBound()) {
      return 3;
    }
    else if (bound.isCaptureBound()) {
      return 4;
    }
    else {
      throwUnimplemented();
      return -42;
    }
  }

  protected int compareCaptureBounds(CaptureBound b1, CaptureBound b2) {
    int res;
    int placeHolderComp = compareSymTypeExpressions(b1.getPlaceHolder(), b2.getPlaceHolder());
    if (placeHolderComp != 0) {
      res = placeHolderComp;
    }
    else {
      res = compareSymTypeExpressions(b1.getToBeCaptured(), b2.getToBeCaptured());
    }
    return res;
  }

  protected int compareSubTypingBounds(SubTypingBound b1, SubTypingBound b2) {
    int res;
    int subTypeComp = compareSymTypeExpressions(b1.getSubType(), b2.getSubType());
    if (subTypeComp != 0) {
      res = subTypeComp;
    }
    else {
      res = compareSymTypeExpressions(b1.getSuperType(), b2.getSuperType());
    }
    return res;
  }

  protected int compareTypeCompatibilityBounds(TypeCompatibilityBound b1, TypeCompatibilityBound b2) {
    int res;
    int sourceComp = compareSymTypeExpressions(b1.getSourceType(), b2.getSourceType());
    if (sourceComp != 0) {
      res = sourceComp;
    }
    else {
      res = compareSymTypeExpressions(b1.getTargetType(), b2.getTargetType());
    }
    return res;
  }

  protected int compareTypeEqualityBounds(TypeEqualityBound b1, TypeEqualityBound b2) {
    int res;
    int first = compareSymTypeExpressions(b1.getFirstType(), b2.getFirstType());
    if (first != 0) {
      res = first;
    }
    else {
      res = compareSymTypeExpressions(b1.getSecondType(), b2.getSecondType());
    }
    return res;
  }

  protected int compareUnsatisfiableBounds(UnsatisfiableBound b1, UnsatisfiableBound b2) {
    return b1.getDescription().compareTo(b2.getDescription());
  }

  // helper

  /**
   * This is not expected to be ever called.
   */
  protected void throwUnimplemented() throws UnsupportedOperationException {
    throw new UnsupportedOperationException(
        "0xFD662 unimplemented comparison."
    );
  }

  // static delegate

  public static void init() {
    Log.trace("init default BoundComparator", "TypeCheck setup");
    setDelegate(new BoundComparator());
  }

  public static void reset() {
    BoundComparator.delegate = null;
  }

  protected static void setDelegate(BoundComparator newDelegate) {
    BoundComparator.delegate = Preconditions.checkNotNull(newDelegate);
  }

  protected static BoundComparator getDelegate() {
    if (BoundComparator.delegate == null) {
      init();
    }
    return BoundComparator.delegate;
  }

}
