package de.monticore.types3.generics.context;

import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfFunction;
import de.monticore.types.check.SymTypeVariable;
import de.monticore.types3.generics.TypeParameterRelations;
import de.monticore.types3.generics.bounds.Bound;
import de.monticore.types3.generics.constraints.ExpressionCompatibilityConstraint;
import de.monticore.types3.generics.util.BoundResolution;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Internal results of inference.
 * This one stores information to be passed 'upwards' in the AST.
 * This is done as, depending on the expression,
 * multiple passes can be required.
 * It is used until a compile-time type (a type without free type variables)
 * can be calculated and stored in {@link de.monticore.types3.Type4Ast}.
 */
public class InferenceResult {

  protected Optional<InferenceVisitorMode> lastMode = Optional.empty();

  /**
   * A type-check error occurred (and has been logged).
   * Stop the inference process.
   */
  protected boolean hasErrorOccurred = false;

  // potential results of inference, at maximum only on is set at a time:
  protected Optional<SymTypeOfFunction> resolvedFunction = Optional.empty();
  protected Optional<SymTypeExpression> resolvedNonInvocationType = Optional.empty();

  // these fields are filled given that they are required by the visitor mode
  // s. InferenceVisitorMode
  // Their names are directly based on JLS 21 chapter 18.
  // Further information of what each bound set represent can be found
  // in the comments of the corresponding getters.
  protected Optional<List<Bound>> b0 = Optional.empty();
  protected Optional<List<Bound>> b2 = Optional.empty();
  protected Optional<List<Bound>> b3 = Optional.empty();
  protected Optional<List<ExpressionCompatibilityConstraint>> b4C =
      Optional.empty();
  protected Optional<List<Bound>> b4 = Optional.empty();

  // getter / setter

  /**
   * @return the last mode used to collect information
   */
  public InferenceVisitorMode getLastInferenceMode() {
    return lastMode.get();
  }

  public void setLastInferenceMode(InferenceVisitorMode mode) {
    this.lastMode = Optional.of(mode);
  }

  public boolean hasErrorOccurred() {
    return hasErrorOccurred;
  }

  public void setHasErrorOccurred() {
    hasErrorOccurred = true;
  }

  public boolean hasResolvedFunction() {
    return resolvedFunction.isPresent();
  }

  public boolean hasResolvedNonInvocationType() {
    return resolvedNonInvocationType.isPresent();
  }

  /**
   * A function, either required by an invocation, or by assignment, e.g.,
   * (a?b:c)(1) // here, b and c are required to be functions
   * (int) -> void f = a; // here, a is required to be a function
   * Only call if {@link #hasResolvedFunction()} returns true.
   */
  public SymTypeOfFunction getResolvedFunction() {
    return resolvedFunction.get();
  }

  public void setResolvedFunction(SymTypeOfFunction resolvedFunction) {
    this.resolvedFunction = Optional.of(resolvedFunction);
  }

  /**
   * Can be a function, but in the context, no function was required.
   * There are no bounds and the type cannot have inference vars.
   * Only call if {@link #hasResolvedFunction()} returns false.
   */
  public SymTypeExpression getResolvedNonInvocationType() {
    return resolvedNonInvocationType.get();
  }

  public void setResolvedNonInvocationType(SymTypeExpression resolvedNonInvocationType) {
    this.resolvedNonInvocationType = Optional.of(resolvedNonInvocationType);
  }

  /**
   * Function parameter bounds
   * s. JLS 21 18.1.3 B0
   */
  public List<Bound> getB0() {
    return b0.get();
  }

  public void setB0(List<Bound> b0) {
    this.b0 = Optional.of(b0);
  }

  /**
   * Function applicability bounds
   * s. JLS 21 18.5.1 B2
   */
  public List<Bound> getB2() {
    return b2.get();
  }

  public void setB2(List<Bound> b2) {
    this.b2 = Optional.of(b2);
  }

  public Optional<SymTypeOfFunction> getApplicabilityInstantiation() {
    return getResolvedFunction(getB2());
  }

  /**
   * Function invocation compatibility bounds
   * s. JLS 21 18.5.2.1 B3
   */
  public List<Bound> getB3() {
    return b3.get();
  }

  public void setB3(List<Bound> b3) {
    this.b3 = Optional.of(b3);
  }

  public Optional<SymTypeOfFunction> getInvocationCompatibilityInstantiation() {
    return getResolvedFunction(getB3());
  }

  /**
   * Function invocation type constraints
   * used to get function invocation type bounds
   * s. JLS 21 18.5.2.2 C
   */
  public List<ExpressionCompatibilityConstraint> getB4C() {
    return b4C.get();
  }

  public void setB4C(List<ExpressionCompatibilityConstraint> b4C) {
    this.b4C = Optional.of(b4C);
  }

  /**
   * Function invocation type bounds
   * s. JLS 21 18.5.2.2 B4
   */
  public List<Bound> getB4() {
    return b4.get();
  }

  public void setB4(List<Bound> b4) {
    this.b4 = Optional.of(b4);
  }

  public Optional<SymTypeOfFunction> getInvocationType() {
    return getResolvedFunction(getB4());
  }

  /**
   * Gets the compile-time type.
   * This is either the {@link #getInvocationType()} or
   * {@link #getResolvedNonInvocationType()}.
   */
  public Optional<SymTypeExpression> getCompileTimeType() {
    if (!hasResolvedFunction()) {
      return Optional.of(getResolvedNonInvocationType());
    }
    else {
      return getInvocationType().map(f -> f);
    }
  }

  // Helper

  /**
   * During many checks (s. InferenceVisitorMode),
   * it is required to check whether resolution of a set of bounds succeeds.
   * In Addition to this information, the corresponding
   * instantiation of the function is returned on success.
   */
  protected Optional<SymTypeOfFunction> getResolvedFunction(
      List<Bound> bounds
  ) {
    Optional<Map<SymTypeVariable, SymTypeExpression>> instantiations =
        BoundResolution.resolve(bounds);
    if (instantiations.isPresent()) {
      SymTypeOfFunction instantiatedFunc = TypeParameterRelations
          .replaceTypeVariables(getResolvedFunction(), instantiations.get())
          .asFunctionType();
      return Optional.of(instantiatedFunc);
    }
    else {
      return Optional.empty();
    }
  }

}
