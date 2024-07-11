// (c) https://github.com/MontiCore/monticore
package de.monticore.types3.generics.bounds;

import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.generics.TypeParameterRelations;
import de.se_rwth.commons.logging.Log;

import java.util.List;

public class SubTypingBound extends Bound {

  protected SymTypeExpression subType;
  protected SymTypeExpression superType;

  public SubTypingBound(
      SymTypeExpression subType,
      SymTypeExpression superType
  ) {
    this.subType = subType;
    this.superType = superType;
    if (!TypeParameterRelations.isInferenceVariable(subType) &&
        !TypeParameterRelations.isInferenceVariable(superType)
    ) {
      Log.error("0xFD337 internal error: "
          + "expected at least one inference variable, but got "
          + subType.printFullName() + " and " + superType.printFullName()
      );
    }
  }

  public SymTypeExpression getSubType() {
    return subType;
  }

  public SymTypeExpression getSuperType() {
    return superType;
  }

  @Override
  public boolean isSubTypingBound() {
    return true;
  }

  @Override
  public boolean deepEquals(Bound other) {
    if (this == other) {
      return true;
    }
    if (!other.isSubTypingBound()) {
      return false;
    }
    SubTypingBound otherSubTyping = (SubTypingBound) other;
    return getSubType().deepEquals(otherSubTyping.getSubType()) &&
        getSuperType().deepEquals(otherSubTyping.getSuperType());
  }

  @Override
  public String print() {
    return subType.printFullName()
        + " <: " + superType.printFullName();
  }

  @Override
  public List<SymTypeExpression> getIncludedTypes() {
    return List.of(getSubType(), getSuperType());
  }
}
