// (c) https://github.com/MontiCore/monticore
package de.monticore.types3.util;

import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.oosymbols.OOSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.symboltable.modifiers.StaticAccessModifier;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeInferenceVariable;
import de.monticore.types.check.SymTypeOfFunction;
import de.monticore.types.check.SymTypeVariable;
import de.monticore.types3.generics.TypeParameterRelations;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * resolves within a type,
 * unlike {@link WithinTypeBasicSymbolsResolver},
 * we further filter by "OO-rules",
 * e.g., a constructor cannot be called like other methods
 */
public class OOWithinTypeBasicSymbolsResolver
    extends WithinTypeBasicSymbolsResolver {

  protected static OOWithinTypeBasicSymbolsResolver delegate;

  // methods

  /**
   * resolves within a type including supertypes
   */
  public static List<SymTypeOfFunction> resolveConstructors(
      SymTypeExpression thisType,
      AccessModifier accessModifier,
      Predicate<FunctionSymbol> predicate) {
    return getDelegate()
        ._resolveConstructors(thisType, accessModifier, predicate);
  }

  protected List<SymTypeOfFunction> _resolveConstructors(
      SymTypeExpression thisType,
      AccessModifier accessModifier,
      Predicate<FunctionSymbol> predicate) {
    List<SymTypeOfFunction> resolvedSymTypes;
    Optional<IBasicSymbolsScope> spannedScopeOpt = getSpannedScope(thisType);
    if (spannedScopeOpt.isEmpty()) {
      resolvedSymTypes = new ArrayList<>();
    }
    // search in this scope
    else {
      List<FunctionSymbol> resolvedSymbols = resolveConstructorLocally(
          thisType.getTypeInfo().getSpannedScope(),
          thisType.getTypeInfo().getName(),
          accessModifier,
          predicate
      );
      resolvedSymTypes = new ArrayList<>(resolvedSymbols.size());
      for (FunctionSymbol funcSym : resolvedSymbols) {
        SymTypeOfFunction funcTypeUnmodified = funcSym.getFunctionType();
        // checking for broken symbol table
        if (!funcTypeUnmodified.getType().hasTypeInfo() ||
            !funcTypeUnmodified.getType().getTypeInfo().getFullName().equals(
                thisType.getTypeInfo().getFullName()
            )) {
          Log.error("0xFDCC2 unexpected constructor return type(s) of type "
              + thisType.printFullName()
              + ", constructor is " + System.lineSeparator()
              + funcTypeUnmodified.printFullName()
          );
        }
        SymTypeOfFunction funcType = replaceVariablesIfNecessary(
            thisType, funcTypeUnmodified
        ).asFunctionType();
        funcType.getSourceInfo().setSourceSymbol(funcSym);
        resolvedSymTypes.add(funcType);
      }
    }

    // do not search super types for constructors

    List<SymTypeOfFunction> symTypesFreeVarsReplaced = resolvedSymTypes.stream()
        .map(this::replaceFreeConstructorTypeVariables)
        .map(SymTypeExpression::asFunctionType)
        .collect(Collectors.toList());

    return symTypesFreeVarsReplaced;
  }

  // Helper

  /**
   * resolves locally, EXCLUDING supertypes
   * this filters out constructors
   */
  @Override
  protected List<FunctionSymbol> resolveFunctionLocallyMany(
      IBasicSymbolsScope scope,
      String name,
      AccessModifier accessModifier,
      Predicate<FunctionSymbol> predicate) {
    return super.resolveFunctionLocallyMany(
        scope, name, accessModifier,
        predicate.and(Predicate.not(this::isConstructor))
    );
  }

  /**
   * same as {@link #resolveFunctionLocallyMany}
   * but does only returns constructors
   *
   * @deprecated is to be made private, use {@link #resolveConstructors}
   */
  @Deprecated(forRemoval = true)
  public List<FunctionSymbol> resolveConstructorLocally(
      IBasicSymbolsScope scope,
      String name,
      AccessModifier accessModifier,
      Predicate<FunctionSymbol> predicate
  ) {
    return super.resolveFunctionLocallyMany(
        scope, name,
        removeStaticness(accessModifier),
        predicate.and(this::isConstructor)
    );
  }

  protected SymTypeExpression replaceFreeConstructorTypeVariables(SymTypeExpression type) {
    // 1. get unbound variable replacement map
    Map<SymTypeVariable, SymTypeInferenceVariable> freeVarMap =
        getUnboundVariableReplaceMap(Collections.emptyList(), type);
    // 2. actually replace the free variables
    SymTypeExpression typeVarsReplaced =
        TypeParameterRelations.replaceTypeVariables(type, freeVarMap);

    return typeVarsReplaced;
  }

  protected boolean isConstructor(FunctionSymbol func) {
    if (OOSymbolsMill.typeDispatcher().isOOSymbolsMethod(func)) {
      MethodSymbol method = OOSymbolsMill.typeDispatcher().asOOSymbolsMethod(func);
      return method.isIsConstructor();
    }
    return false;
  }

  /**
   * replaces any static/non-static access with all access
   * this is done as we want to ignore isStatic in constructors
   * There are some languages, where this distinction is relevant (e.g., C#)
   */
  protected AccessModifier removeStaticness(AccessModifier accessModifier) {
    AccessModifier newModifier = accessModifier.shallowCopy();
    Map<String, AccessModifier> map = newModifier.getDimensionToModifierMap();
    map.remove(StaticAccessModifier.DIMENSION);
    return newModifier;
  }

  /**
   * Allows the use of types as its constructor, e.g.,
   * {@code Person("Riley", 26)}.
   *
   * @param typeIdType     the type identifier
   * @param accessModifier the access modifier used to access the type
   * @return The intersection of the constructors
   */
  @Override
  protected Optional<SymTypeExpression> _getTypeAsExpression(
      SymTypeExpression typeIdType,
      AccessModifier accessModifier
  ) {
    Optional<SymTypeExpression> exprType;
    List<SymTypeOfFunction> constructors = resolveConstructors(
        typeIdType,
        accessModifier,
        c -> true
    );
    if (constructors.isEmpty()) {
      exprType = Optional.empty();
    }
    else {
      exprType = Optional.of(SymTypeExpressionFactory.createIntersectionOrDefault(
          SymTypeExpressionFactory.createObscureType(), constructors
      ));
    }
    return exprType;
  }

  // static delegate

  public static void init() {
    Log.trace("init OOWithinTypeBasicSymbolsResolver", "TypeCheck setup");
    setDelegate(new OOWithinTypeBasicSymbolsResolver());
  }

  public static void reset() {
    OOWithinScopeBasicSymbolsResolver.delegate = null;
    WithinTypeBasicSymbolsResolver.reset();
  }

  protected static void setDelegate(
      OOWithinTypeBasicSymbolsResolver newDelegate
  ) {
    OOWithinTypeBasicSymbolsResolver.delegate = Preconditions.checkNotNull(newDelegate);
    WithinTypeBasicSymbolsResolver.setDelegate(newDelegate);
  }

  protected static OOWithinTypeBasicSymbolsResolver getDelegate() {
    if (OOWithinTypeBasicSymbolsResolver.delegate == null) {
      init();
    }
    return OOWithinTypeBasicSymbolsResolver.delegate;
  }
}
