package de.monticore.types3.generics;

import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeInferenceVariable;
import de.monticore.types.check.SymTypeVariable;
import de.monticore.types3.generics.util.TypeParameterRelationsDefaultDelegatee;
import de.se_rwth.commons.logging.Log;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * A collection of often used helper functions w.r.t. type parameters;
 * * Free type variables
 * * Bound type variables
 * * Inference variables
 * * Wildcards
 */
public abstract class TypeParameterRelations {

  protected static TypeParameterRelations delegate;

  // methods

  /**
   * replaces bound TypeVariables using a given map
   * e.g., {@code T, {T->int,U->float} -> int}
   * e.g., {@code List<T>, {T->int} -> List<int>}
   */
  public static SymTypeExpression replaceTypeVariables(
      SymTypeExpression type,
      Map<SymTypeVariable, ? extends SymTypeExpression> replaceMap
  ) {
    return getDelegate()._replaceTypeVariables(type, replaceMap);
  }

  protected abstract SymTypeExpression _replaceTypeVariables(
      SymTypeExpression type,
      Map<SymTypeVariable, ? extends SymTypeExpression> replaceMap
  );

  /**
   * replaces InferenceVariables using a given map
   * e.g., {@code a, {a->int,b->float} -> int}
   * e.g., {@code List<a>, {a->int} -> List<int>}
   */
  public static SymTypeExpression replaceInferenceVariables(
      SymTypeExpression type,
      Map<SymTypeInferenceVariable, ? extends SymTypeExpression> replaceMap
  ) {
    return getDelegate()._replaceInferenceVariables(type, replaceMap);
  }

  protected abstract SymTypeExpression _replaceInferenceVariables(
      SymTypeExpression type,
      Map<SymTypeInferenceVariable, ? extends SymTypeExpression> replaceMap
  );

  /**
   * Returns a map that can be used to replace
   * free type variables with inference variables.
   *
   * @param type           the type (potentially) containing free type variables
   * @param enclosingScope the enclosing scope used to check
   *                       if the variables are free or bound
   */
  public static Map<SymTypeVariable, SymTypeInferenceVariable> getFreeVariableReplaceMap(
      SymTypeExpression type,
      IBasicSymbolsScope enclosingScope
  ) {
    return getDelegate()._getFreeVariableReplaceMap(type, enclosingScope);
  }

  protected abstract Map<SymTypeVariable, SymTypeInferenceVariable> _getFreeVariableReplaceMap(
      SymTypeExpression type,
      IBasicSymbolsScope enclosingScope
  );

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

  /**
   * use type.isInferenceVariable
   */
  @Deprecated(forRemoval = true)
  public static boolean isInferenceVariable(SymTypeExpression type) {
    return type.isInferenceVariable();
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
  public static List<SymTypeInferenceVariable> getIncludedInferenceVariables(
      SymTypeExpression... types
  ) {
    return getIncludedInferenceVariables(Arrays.asList(types));
  }

  public static List<SymTypeInferenceVariable> getIncludedInferenceVariables(
      Collection<? extends SymTypeExpression> types
  ) {
    return getDelegate()._getIncludedInferenceVariables(types);
  }

  protected abstract List<SymTypeInferenceVariable> _getIncludedInferenceVariables(
      Collection<? extends SymTypeExpression> types
  );

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
    return getDelegate()._hasWildcards(type);
  }

  protected abstract boolean _hasWildcards(SymTypeExpression type);

  /**
   * Capture Conversion (s. Java Spec 20 5.1.10).
   * Replaces wildcards with fresh type variables.
   * Hint: This is not applied recursively.
   * If no type arguments exist, this is the identity relation.
   */
  public static <T extends SymTypeExpression> T getCaptureConverted(T type) {
    return getDelegate()._getCaptureConverted(type);
  }

  protected abstract <T extends SymTypeExpression> T _getCaptureConverted(T type);

  // static delegate

  public static void init() {
    Log.trace("init default TypeParameterRelations", "TypeCheck setup");
    setDelegate(new TypeParameterRelationsDefaultDelegatee());
  }

  public static void reset() {
    TypeParameterRelations.delegate = null;
  }

  protected static void setDelegate(TypeParameterRelations newDelegate) {
    TypeParameterRelations.delegate = Log.errorIfNull(newDelegate);
  }

  protected static TypeParameterRelations getDelegate() {
    if (TypeParameterRelations.delegate == null) {
      init();
    }
    return TypeParameterRelations.delegate;
  }

}
