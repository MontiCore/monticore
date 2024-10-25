// (c) https://github.com/MontiCore/monticore
package de.monticore.types3.generics.bounds;

import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.generics.TypeParameterRelations;
import de.se_rwth.commons.logging.Log;

import java.util.List;

/**
 * Specifies, that either
 * A) the inference variable is compatible to the SymTypeExpression, or
 * B) the specified SymTypeExpression is compatible to inference variable.
 * s.a. {@link de.monticore.types3.generics.constraints.TypeCompatibilityConstraint}
 * s.a. {@link SubTypingBound}
 * <p>
 * This bound does not exist in the Java language specification.
 * Instead, the corresponding TypeCompatibilityConstraint in Java
 * is reduced to a SubTypingBound.
 * As our Type System is (slightly) less restrictive with
 * A) what types can be used as type arguments, and
 * B) which types are compatible to another,
 * using a SubTypingBound would be too restrictive, e.g.,
 * (Bad example, only used for simplicity)
 * {@code List<Integer> l1 = [1];
 * List<int> l2 = [1];
 * }
 * The above example cannot be type-checked with the standard JLS algorithm.
 * <p>
 * Note: As of writing, replacing every creation of these
 * with the creation of a SubTypingBound,
 * the implementation should behave very comparable to JLS 21 chapter 18.
 */
public class TypeCompatibilityBound extends Bound {

  protected SymTypeExpression sourceType;
  protected SymTypeExpression targetType;

  public TypeCompatibilityBound(
      SymTypeExpression targetType,
      SymTypeExpression sourceType
  ) {
    this.sourceType = sourceType;
    this.targetType = targetType;
    if (!TypeParameterRelations.isInferenceVariable(sourceType) &&
        !TypeParameterRelations.isInferenceVariable(targetType)
    ) {
      Log.error("0xFD334 internal error: "
          + "expected at least one inference variable, but got "
          + sourceType.printFullName() + " and " + targetType.printFullName()
      );
    }
  }

  public SymTypeExpression getSourceType() {
    return sourceType;
  }

  public SymTypeExpression getTargetType() {
    return targetType;
  }

  @Override
  public boolean isTypeCompatibilityBound() {
    return true;
  }

  @Override
  public boolean deepEquals(Bound other) {
    if (this == other) {
      return true;
    }
    if (!other.isTypeCompatibilityBound()) {
      return false;
    }
    TypeCompatibilityBound otherComp = (TypeCompatibilityBound) other;
    return getSourceType().deepEquals(otherComp.getSourceType()) &&
        getTargetType().deepEquals(otherComp.getTargetType());
  }

  @Override
  public String print() {
    return sourceType.printFullName()
        + " --> " + targetType.printFullName();
  }

  @Override
  public List<SymTypeExpression> getIncludedTypes() {
    return List.of(getSourceType(), getTargetType());
  }
}
