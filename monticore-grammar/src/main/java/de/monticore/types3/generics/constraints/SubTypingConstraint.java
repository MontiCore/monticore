// (c) https://github.com/MontiCore/monticore
package de.monticore.types3.generics.constraints;

import de.monticore.types.check.SymTypeExpression;

import java.util.List;

public class SubTypingConstraint extends Constraint {

  protected SymTypeExpression subType;
  protected SymTypeExpression superType;

  public SubTypingConstraint(
      SymTypeExpression subType,
      SymTypeExpression superType
  ) {
    this.subType = subType;
    this.superType = superType;
  }

  public SymTypeExpression getSubType() {
    return subType;
  }

  public SymTypeExpression getSuperType() {
    return superType;
  }

  @Override
  public boolean isSubTypingConstraint() {
    return true;
  }

  @Override
  public boolean deepEquals(Constraint other) {
    if (this == other) {
      return true;
    }
    if (!other.isSubTypingConstraint()) {
      return false;
    }
    SubTypingConstraint otherSubTyping = (SubTypingConstraint) other;
    return getSubType().deepEquals(otherSubTyping.getSubType()) &&
        getSuperType().deepEquals(otherSubTyping.getSuperType());
  }

  @Override
  public String print() {
    return "<" + subType.printFullName()
        + " <: " + superType.printFullName()
        + ">";
  }

  @Override
  public List<SymTypeExpression> getIncludedTypes() {
    return List.of(getSubType(), getSuperType());
  }
}
