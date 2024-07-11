package de.monticore.types3.generics;

import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeVariable;
import de.monticore.types3.generics.util.SymTypeFreeVariableReplaceVisitor;
import de.monticore.types3.generics.util.SymTypeVariableReplaceVisitor;
import de.monticore.types3.generics.util.WildcardCapturer;
import de.monticore.types3.util.SymTypeCollectionVisitor;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A collection of often used helper functions w.r.t. type parameters;
 * * Free type variables
 * * Bound type variables
 * * Inference variables
 * * Wildcards
 */
public class TypeParameterRelations {

  // static delegate

  protected static TypeParameterRelations delegate;

  public static void init() {
    Log.trace("init default TypeParameterRelations", "TypeCheck setup");
    TypeParameterRelations.delegate = new TypeParameterRelations();
  }

  protected static TypeParameterRelations getDelegate() {
    if (delegate == null) {
      init();
    }
    return delegate;
  }

  protected TypeParameterRelations() {
    this.typeVarReplacer = new SymTypeVariableReplaceVisitor();
    this.freeVariableReplacer = new SymTypeFreeVariableReplaceVisitor();
    this.symTypeCollectionVisitor = new SymTypeCollectionVisitor();
    this.wildCardCapturer = new WildcardCapturer();
  }

  // delegates

  SymTypeVariableReplaceVisitor typeVarReplacer;

  SymTypeFreeVariableReplaceVisitor freeVariableReplacer;

  SymTypeCollectionVisitor symTypeCollectionVisitor;

  WildcardCapturer wildCardCapturer;

  /**
   * replaces TypeVariables using a given map
   * e.g., T, {T->int,U->float} -> int
   * e.g., List<T>, {T->int} -> List<int>
   */
  public static SymTypeExpression replaceTypeVariables(
      SymTypeExpression type,
      Map<SymTypeVariable, ? extends SymTypeExpression> replaceMap
  ) {
    return getDelegate().calculateReplaceTypeVariables(type, replaceMap);
  }

  protected SymTypeExpression calculateReplaceTypeVariables(
      SymTypeExpression type,
      Map<SymTypeVariable, ? extends SymTypeExpression> replaceMap
  ) {
    return typeVarReplacer.calculate(type, replaceMap);
  }

  /**
   * Returns a map that can be used to replace
   * free type variables with inference variables.
   *
   * @param type           the type (potentially) containing free type variables
   * @param enclosingScope the enclosing scope used to check
   *                       if the variables are free or bound
   */
  public static Map<SymTypeVariable, SymTypeVariable> getFreeVariableReplaceMap(
      SymTypeExpression type,
      IBasicSymbolsScope enclosingScope
  ) {
    return getDelegate().calculateGetFreeVariableReplaceMap(type, enclosingScope);
  }

  protected Map<SymTypeVariable, SymTypeVariable> calculateGetFreeVariableReplaceMap(
      SymTypeExpression type,
      IBasicSymbolsScope enclosingScope
  ) {
    // we are not using the calculated type, this could be optimized.
    return freeVariableReplacer.calculate(type, enclosingScope).getReplaceMap();
  }

  /**
   * Replaces free type variables with inference variables.
   * s. {@link #getFreeVariableReplaceMap(SymTypeExpression, IBasicSymbolsScope)}
   */
  public static SymTypeExpression replaceFreeTypeVariables(
      SymTypeExpression type,
      IBasicSymbolsScope enclosingScope
  ) {
    return replaceTypeVariables(type, getFreeVariableReplaceMap(type, enclosingScope));
  }

  public static boolean isInferenceVariable(SymTypeExpression type) {
    return getDelegate().calculateIsInferenceVariable(type);
  }

  protected boolean calculateIsInferenceVariable(SymTypeExpression type) {
    return type.isTypeVariable() &&
        type.asTypeVariable().isInferenceVariable();
  }

  /**
   * returns all inference variables contained in the SymTypes.
   * Inference variable can stem from
   * 1. replacing free type variables
   * {@link #getFreeVariableReplaceMap(SymTypeExpression, IBasicSymbolsScope)}
   * 2. capturing wildcards
   * {@link #getCaptureConverted(SymTypeExpression)}
   *
   * In the Java Spec (v.21 chap. 18) types with inference variables
   * would be called "improper".
   */
  public static List<SymTypeVariable> getIncludedInferenceVariables(SymTypeExpression... types) {
    return getIncludedInferenceVariables(Arrays.asList(types));
  }

  public static List<SymTypeVariable> getIncludedInferenceVariables(
      Collection<? extends SymTypeExpression> types
  ) {
    return getDelegate().calculateGetIncludedInferenceVariables(types);
  }

  protected List<SymTypeVariable> calculateGetIncludedInferenceVariables(
      Collection<? extends SymTypeExpression> types
  ) {
    List<SymTypeVariable> infVars = new ArrayList<>();
    for (SymTypeExpression type : types) {
      infVars.addAll(
          symTypeCollectionVisitor.calculate(type, t ->
                  t.isTypeVariable() && t.asTypeVariable().isInferenceVariable()
              ).stream()
              .map(SymTypeExpression::asTypeVariable)
              .collect(Collectors.toList())
      );
    }
    return infVars;
  }

  /**
   * s. {@link #getIncludedInferenceVariables(SymTypeExpression...)}
   */
  public static boolean hasInferenceVariables(SymTypeExpression type) {
    return !getIncludedInferenceVariables(type).isEmpty();
  }

  /**
   * Whether the SymType contains any wildcards.
   */
  public static boolean hasWildcards(SymTypeExpression type) {
    return getDelegate().calculateHasWildcards(type);
  }

  protected boolean calculateHasWildcards(SymTypeExpression type) {
    return !symTypeCollectionVisitor
        .calculate(type, SymTypeExpression::isWildcard)
        .isEmpty();
  }

  /**
   * Capture Conversion (s. Java Spec 20 5.1.10).
   * Replaces wildcards with fresh type variables.
   * Hint: This is not applied recursively.
   * If no type arguments exist, this is the identity relation.
   */
  public static <T extends SymTypeExpression> T getCaptureConverted(T type) {
    return getDelegate().calculateGetCaptureConverted(type);
  }

  protected <T extends SymTypeExpression> T calculateGetCaptureConverted(T type) {
    return wildCardCapturer.getCaptureConverted(type);
  }

}
