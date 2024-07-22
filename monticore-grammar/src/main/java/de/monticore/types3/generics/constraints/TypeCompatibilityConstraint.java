// (c) https://github.com/MontiCore/monticore
package de.monticore.types3.generics.constraints;

import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.generics.TypeParameterRelations;
import de.se_rwth.commons.logging.Log;

import java.util.List;

public class TypeCompatibilityConstraint extends Constraint {

  protected SymTypeExpression sourceType;
  protected SymTypeExpression targetType;

  public TypeCompatibilityConstraint(
      SymTypeExpression sourceType,
      SymTypeExpression targetType
  ) {
    this.sourceType = sourceType;
    this.targetType = targetType;
    if (!TypeParameterRelations.hasInferenceVariables(sourceType)) {
      if (!TypeParameterRelations.getCaptureConverted(sourceType)
          .deepEquals(sourceType)
      ) {
        Log.error("0xFD6AC internal error: "
            + "encountered unexpected non-captured wildcard: "
            + sourceType.printFullName()
        );
      }
    }
  }

  public SymTypeExpression getSourceType() {
    return sourceType;
  }

  public SymTypeExpression getTargetType() {
    return targetType;
  }

  @Override
  public boolean isTypeCompatibilityConstraint() {
    return true;
  }

  @Override
  public boolean deepEquals(Constraint other) {
    if (this == other) {
      return true;
    }
    if (!other.isTypeCompatibilityConstraint()) {
      return false;
    }
    TypeCompatibilityConstraint otherCompatibility =
        (TypeCompatibilityConstraint) other;
    return getSourceType().deepEquals(otherCompatibility.getSourceType()) &&
        getTargetType().deepEquals(otherCompatibility.getTargetType());
  }

  @Override
  public String print() {
    return "<" + sourceType.printFullName()
        + " --> " + targetType.printFullName()
        + ">";
  }

  @Override
  public List<SymTypeExpression> getIncludedTypes() {
    return List.of(getSourceType(), getTargetType());
  }
}
