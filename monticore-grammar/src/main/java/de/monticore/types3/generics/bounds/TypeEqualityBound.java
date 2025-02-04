// (c) https://github.com/MontiCore/monticore
package de.monticore.types3.generics.bounds;

import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeInferenceVariable;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.Optional;

/**
 * States that an inference variable equals another type.
 * If the other type does not contain inference variables,
 * this describes an "instantiation".
 * <p>
 * The order of types is predefined,
 * e.g., for {@link #getFirstType()};
 * First is always a type variable,
 * if both are type variables, the order is based on the id.
 */
public class TypeEqualityBound extends Bound {

  protected SymTypeInferenceVariable firstType;
  protected SymTypeExpression secondType;

  public TypeEqualityBound(
      SymTypeExpression typeA,
      SymTypeExpression typeB
  ) {
    if (typeA.isInferenceVariable() && typeB.isInferenceVariable()) {
      SymTypeInferenceVariable typeVarA = typeA.asInferenceVariable();
      SymTypeInferenceVariable typeVarB = typeB.asInferenceVariable();
      // assure a predefined order, no matter whether it has been
      // created with (A,B) oder (B,A)
      // this is used to
      // 1. find duplicates easier
      // 2. reduce the number of possibilities how this is printed to a user
      // 3. reduce cases that can lead to hard to spot errors
      if (typeVarA._internal_getID() < typeVarB._internal_getID()) {
        this.firstType = typeVarA;
        this.secondType = typeVarB;
      }
      else {
        this.firstType = typeVarB;
        this.secondType = typeVarA;
      }
    }
    else if (typeA.isInferenceVariable()) {
      this.firstType = typeA.asInferenceVariable();
      this.secondType = typeB;
    }
    else if (typeB.isInferenceVariable()) {
      this.firstType = typeB.asInferenceVariable();
      this.secondType = typeA;
    }
    else {
      Log.error("0xFD338 internal error: "
          + "expected at least one inference variable, but got "
          + typeA.printFullName() + " and " + typeB.printFullName()
      );
    }
  }

  protected TypeEqualityBound() {
  }

  public SymTypeInferenceVariable getFirstType() {
    return firstType;
  }

  public SymTypeExpression getSecondType() {
    return secondType;
  }

  /**
   * If the second type is a typeVariable as well,
   * returns the Bound with its types order exchanged.
   */
  public Optional<TypeEqualityBound> getFlipped() {
    if (getSecondType().isInferenceVariable()) {
      TypeEqualityBound flipped = new TypeEqualityBound();
      flipped.firstType = getSecondType().asInferenceVariable();
      flipped.secondType = getFirstType();
      return Optional.of(flipped);
    }
    return Optional.empty();
  }

  @Override
  public boolean isTypeEqualityBound() {
    return true;
  }

  @Override
  public boolean deepEquals(Bound other) {
    if (this == other) {
      return true;
    }
    if (!other.isTypeEqualityBound()) {
      return false;
    }
    TypeEqualityBound otherTypeEquality = (TypeEqualityBound) other;
    return getFirstType().deepEquals(otherTypeEquality.getFirstType()) &&
        getSecondType().deepEquals(otherTypeEquality.getSecondType());
  }

  @Override
  public String print() {
    return getFirstType().printFullName()
        + " = " + getSecondType().printFullName();
  }

  @Override
  public List<SymTypeExpression> getIncludedTypes() {
    return List.of(getFirstType(), getSecondType());
  }

}
