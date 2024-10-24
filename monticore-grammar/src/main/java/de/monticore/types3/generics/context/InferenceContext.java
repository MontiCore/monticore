package de.monticore.types3.generics.context;

import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.generics.util.PartialFunctionInfo;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Context information for contextual typing,
 * s.a. {@link InferenceContext4Ast}.
 */
public class InferenceContext {

  // input for type inference

  protected InferenceVisitorMode mode = InferenceVisitorMode.TYPE_CHECKING;

  protected Optional<SymTypeExpression> targetType = Optional.empty();

  protected PartialFunctionInfo partialFunctionInfo =
      new PartialFunctionInfo();

  // output for type inference

  // note: this might be replaceable with a single result.
  protected List<InferenceResult> inferredTypes = new ArrayList<>();

  // getter / setter

  public InferenceVisitorMode getVisitorMode() {
    return mode;
  }

  public void setVisitorMode(InferenceVisitorMode mode) {
    this.mode = mode;
  }

  public boolean hasTargetType() {
    return targetType.isPresent();
  }

  /**
   * type for expression compatibility constraint,
   * s. Java spec 20 18.2.1
   */
  public SymTypeExpression getTargetType() {
    if (!hasTargetType()) {
      Log.error("0xFD088 internal error: "
          + "trying to get() non-existing target type."
      );
    }
    return targetType.get();
  }

  public void setTargetType(SymTypeExpression targetType) {
    this.targetType = Optional.of(targetType);
  }

  /**
   * Returns the target type information if the target type is a function type
   * and not all information about the function is available.
   * However, if a target function type is available,
   * its information is returned instead.
   */
  public PartialFunctionInfo getPartialFunctionInfo() {
    // get target info
    PartialFunctionInfo funcInfo;
    if (hasTargetType() && getTargetType().isFunctionType()) {
      funcInfo = new PartialFunctionInfo(getTargetType().asFunctionType());
    }
    else {
      funcInfo = this.partialFunctionInfo;
    }
    return funcInfo;
  }

  public List<InferenceResult> getInferenceResults() {
    return this.inferredTypes;
  }

  public void setInferredTypes(List<InferenceResult> inferredTypes) {
    this.inferredTypes = inferredTypes;
  }

  /**
   * Warn: inferred types are cloned rather shallow,
   * as cloning them tends to not be required.
   */
  public InferenceContext deepClone() {
    InferenceContext clone = new InferenceContext();
    clone.mode = getVisitorMode();
    clone.targetType = targetType.map(SymTypeExpression::deepClone);
    clone.partialFunctionInfo = partialFunctionInfo.deepClone();
    clone.inferredTypes = new ArrayList<>(inferredTypes);
    return clone;
  }

}
