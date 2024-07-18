// (c) https://github.com/MontiCore/monticore
package de.monticore.types3.generics.constraints;

import de.monticore.types.check.SymTypeExpression;

import java.util.List;

public class TypeEqualityConstraint extends Constraint {

  protected SymTypeExpression firstType;
  protected SymTypeExpression secondType;

  public TypeEqualityConstraint(
      SymTypeExpression firstType,
      SymTypeExpression secondType
  ) {
    this.firstType = firstType;
    this.secondType = secondType;
  }

  public SymTypeExpression getFirstType() {
    return firstType;
  }

  public SymTypeExpression getSecondType() {
    return secondType;
  }

  @Override
  public boolean isTypeEqualityConstraint() {
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
    TypeEqualityConstraint otherEquality = (TypeEqualityConstraint) other;
    return getFirstType().deepEquals(otherEquality.getFirstType()) &&
        getSecondType().deepEquals(otherEquality.getSecondType());
  }

  @Override
  public String print() {
    return "<" + firstType.printFullName()
        + " = " + secondType.printFullName()
        + ">";
  }

  @Override
  public List<SymTypeExpression> getIncludedTypes() {
    return List.of(getFirstType(), getSecondType());
  }
}
