// (c) https://github.com/MontiCore/monticore
package de.monticore.types3.util;

import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.basicsymbols._util.IBasicSymbolsTypeDispatcher;
import de.monticore.symbols.basicsymbols._visitor.BasicSymbolsTraverser;
import de.monticore.symbols.basicsymbols._visitor.BasicSymbolsVisitor2;
import de.monticore.symboltable.IScope;
import de.monticore.symboltable.ISymbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.symboltable.modifiers.BasicAccessModifier;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeInferenceVariable;
import de.monticore.types.check.SymTypeOfFunction;
import de.monticore.types.check.SymTypeOfGenerics;
import de.monticore.types.check.SymTypeOfUnion;
import de.monticore.types.check.SymTypeVariable;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.generics.TypeParameterRelations;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * resolves within a type,
 * but in a more type correct way than our resolve algorithm,
 * as some additions cannot be (simply) added to it.
 * E.g., given generics, the correct type parameters will be set.
 * The results will be in form of SymTypeExpressions.
 */
public class WithinTypeBasicSymbolsResolver {

  protected static final String LOG_NAME = "WithinTypeResolving";

  protected static WithinTypeBasicSymbolsResolver delegate;

  // methods

  /**
   * resolves within a type including supertypes
   */
  public static Optional<SymTypeExpression> resolveVariable(
      SymTypeExpression thisType,
      String name,
      AccessModifier accessModifier,
      Predicate<VariableSymbol> predicate
  ) {
    return getDelegate().
        _resolveVariable(thisType, name, accessModifier, predicate);
  }

  protected Optional<SymTypeExpression> _resolveVariable(
      SymTypeExpression thisType,
      String name,
      AccessModifier accessModifier,
      Predicate<VariableSymbol> predicate) {
    Optional<SymTypeExpression> resolvedSymType;
    Optional<IBasicSymbolsScope> spannedScopeOpt = getSpannedScope(thisType);
    if (spannedScopeOpt.isEmpty()) {
      resolvedSymType = Optional.empty();
    }
    // search in this scope
    else {
      Optional<VariableSymbol> resolvedSymbolOpt = resolveVariableLocally(
          spannedScopeOpt.get(),
          name,
          accessModifier,
          predicate
      );
      if (resolvedSymbolOpt.isPresent()) {
        VariableSymbol resolvedSymbol = resolvedSymbolOpt.get();
        SymTypeExpression varType = replaceVariablesIfNecessary(
            thisType, resolvedSymbol.getType()
        );
        varType.getSourceInfo().setSourceSymbol(resolvedSymbol);
        resolvedSymType = Optional.of(varType);
      }
      else {
        resolvedSymType = Optional.empty();
      }
    }
    // search in super types
    if (resolvedSymType.isEmpty()) {
      // private -> protected while searching in super types
      AccessModifier superModifier = private2Protected(accessModifier);
      List<SymTypeExpression> superTypes = getSuperTypes(thisType);
      resolvedSymType = Optional.empty();
      for (SymTypeExpression superType : superTypes) {
        Optional<SymTypeExpression> resolvedInSuper =
            resolveVariable(superType, name, superModifier, predicate);
        if (resolvedSymType.isPresent() && resolvedInSuper.isPresent()) {
          Log.error("0xFD222 found variables with name \""
              + name + "\" in multiple super types of \""
              + thisType.printFullName() + "\"");
        }
        else if (resolvedSymType.isEmpty() && resolvedInSuper.isPresent()) {
          resolvedSymType = resolvedInSuper;
        }
        //filter based on local variables
      }
    }

    // not expecting free type variables for fields,
    // e.g., class C { <T> T tVar; }
    if (resolvedSymType.isPresent()) {
      SymTypeExpression type = resolvedSymType.get();
      if (!replaceFreeTypeVariables(thisType, type).deepEquals(type)) {
        Log.error("0xFD228 unexpected free type variable in variable "
            + name + " resolved in " + thisType.printFullName() + ": "
            + type.printFullName()
        );
      }
    }

    return resolvedSymType;
  }

  /**
   * resolves all variables within the type including supertypes
   */
  public static Map<String, SymTypeExpression> getAllVariables(
      SymTypeExpression thisType,
      AccessModifier accessModifier,
      Predicate<VariableSymbol> predicate
  ) {
    return getDelegate()._getAllVariables(thisType, accessModifier, predicate);
  }

  protected Map<String, SymTypeExpression> _getAllVariables(
      SymTypeExpression thisType,
      AccessModifier accessModifier,
      Predicate<VariableSymbol> predicate
  ) {
    Map<String, SymTypeExpression> allVariables = new LinkedHashMap<>();
    Collection<String> names = _internal_getMemberNames(thisType);
    for (String name : names) {
      Optional<SymTypeExpression> varOpt =
          resolveVariable(thisType, name, accessModifier, predicate);
      if (varOpt.isPresent()) {
        allVariables.put(name, varOpt.get());
      }
    }
    return allVariables;
  }

  /**
   * resolves within a type including supertypes
   */
  public static List<SymTypeOfFunction> resolveFunctions(
      SymTypeExpression thisType,
      String name,
      AccessModifier accessModifier,
      Predicate<FunctionSymbol> predicate
  ) {
    return getDelegate()
        ._resolveFunctions(thisType, name, accessModifier, predicate);
  }

  protected List<SymTypeOfFunction> _resolveFunctions(
      SymTypeExpression thisType,
      String name,
      AccessModifier accessModifier,
      Predicate<FunctionSymbol> predicate
  ) {
    List<SymTypeOfFunction> resolvedSymTypes = new ArrayList<>();
    List<SymTypeOfFunction> resolvedInThis =
        resolveFunctionsInThisType(
            thisType, name, accessModifier, predicate
        );
    resolvedSymTypes.addAll(resolvedInThis);
    // search in super types
    List<SymTypeOfFunction> resolvedInSuper =
        resolvedFunctionsInSuperTypes(
            thisType, name, accessModifier, predicate
        );
    // filter based on being overridden / hidden (static)
    // Java Spec 20 8.4.8.1 overriding methods need to have the SAME signature,
    // e.g., Integer getX() overrides Number getX()
    // e.g., void setX(Number x) does not override void setX(Integer x)
    // we assume that CoCos corresponding to the compile time errors of
    // Java Spec 20 8.4.8 are used
    for (SymTypeOfFunction superFunc : resolvedInSuper) {
      if (resolvedInThis.stream()
          .noneMatch((f -> f.deepEqualsSignature(superFunc)))) {
        resolvedSymTypes.add(superFunc);
      }
    }

    // replace type variables
    List<SymTypeOfFunction> symTypesFreeVarsReplaced = resolvedSymTypes.stream()
        .map(t -> replaceFreeTypeVariables(thisType, t))
        .map(SymTypeExpression::asFunctionType)
        .collect(Collectors.toList());

    return symTypesFreeVarsReplaced;
  }

  protected List<SymTypeOfFunction> resolveFunctionsInThisType(
      SymTypeExpression thisType,
      String name,
      AccessModifier accessModifier,
      Predicate<FunctionSymbol> predicate) {
    List<SymTypeOfFunction> resolvedSymTypes;
    Optional<IBasicSymbolsScope> spannedScopeOpt = getSpannedScope(thisType);
    if (spannedScopeOpt.isEmpty()) {
      resolvedSymTypes = new ArrayList<>();
    }
    // search in this scope
    else {
      //todo outer types (and vs. supertypes) not really?
      List<FunctionSymbol> resolvedSymbols = resolveFunctionLocallyMany(
          thisType.getTypeInfo().getSpannedScope(),
          name,
          accessModifier,
          predicate
      );
      resolvedSymTypes = new ArrayList<>(resolvedSymbols.size());
      for (FunctionSymbol funcSym : resolvedSymbols) {
        SymTypeOfFunction funcTypeUnmodified = funcSym.getFunctionType();
        SymTypeOfFunction funcType = replaceVariablesIfNecessary(
            thisType, funcTypeUnmodified
        ).asFunctionType();
        funcType.getSourceInfo().setSourceSymbol(funcSym);
        resolvedSymTypes.add(funcType);
      }
    }
    return resolvedSymTypes;
  }

  protected List<SymTypeOfFunction> resolvedFunctionsInSuperTypes(
      SymTypeExpression thisType,
      String name,
      AccessModifier accessModifier,
      Predicate<FunctionSymbol> predicate) {
    // private -> protected while searching in super types
    AccessModifier superModifier = private2Protected(accessModifier);
    List<SymTypeExpression> superTypes = getSuperTypes(thisType);
    List<SymTypeOfFunction> superFuncs = new ArrayList<>();
    for (SymTypeExpression superType : superTypes) {
      List<SymTypeOfFunction> resolvedInSuper =
          resolveFunctions(superType, name, superModifier, predicate);
      superFuncs.addAll(resolvedInSuper);
    }
    // filter based on being inherited twice (diamond pattern)
    // we cannot (solely) rely on the symbols in case of generics
    List<SymTypeOfFunction> filteredSuperFuncs = new ArrayList<>();
    for (SymTypeOfFunction func1 : superFuncs) {
      boolean duplicate = false;
      for (SymTypeOfFunction func2 : filteredSuperFuncs) {
        if (func1.getSymbol() == func2.getSymbol()
            && func1.deepEqualsSignature(func2)) {
          duplicate = true;
        }
      }
      if (!duplicate) {
        filteredSuperFuncs.add(func1);
      }
    }
    return filteredSuperFuncs;
  }

  /**
   * resolves all functions within the type including supertypes
   */
  public static Map<String, List<SymTypeOfFunction>> getAllFunctions(
      SymTypeExpression thisType,
      AccessModifier accessModifier,
      Predicate<FunctionSymbol> predicate
  ) {
    return getDelegate()._getAllFunctions(thisType, accessModifier, predicate);
  }

  protected Map<String, List<SymTypeOfFunction>> _getAllFunctions(
      SymTypeExpression thisType,
      AccessModifier accessModifier,
      Predicate<FunctionSymbol> predicate
  ) {
    Map<String, List<SymTypeOfFunction>> allFunctions = new LinkedHashMap<>();
    Collection<String> names = _internal_getMemberNames(thisType);
    for (String name : names) {
      List<SymTypeOfFunction> functions =
          resolveFunctions(thisType, name, accessModifier, predicate);
      if (!functions.isEmpty()) {
        allFunctions.put(name, functions);
      }
    }
    return allFunctions;
  }

  /**
   * resolves within a type including supertypes
   */
  public static Optional<SymTypeExpression> resolveType(
      SymTypeExpression thisType,
      String name,
      AccessModifier accessModifier,
      Predicate<TypeSymbol> predicate
  ) {
    return getDelegate()
        ._resolveType(thisType, name, accessModifier, predicate);
  }

  protected Optional<SymTypeExpression> _resolveType(
      SymTypeExpression thisType,
      String name,
      AccessModifier accessModifier,
      Predicate<TypeSymbol> predicate) {
    Optional<SymTypeExpression> resolvedSymType;
    Optional<IBasicSymbolsScope> spannedScopeOpt = getSpannedScope(thisType);
    if (spannedScopeOpt.isEmpty()) {
      resolvedSymType = Optional.empty();
    }
    // search in this scope
    else {
      Optional<TypeSymbol> resolvedSymbol = resolveTypeLocally(
          spannedScopeOpt.get(),
          name,
          accessModifier,
          predicate
      );
      if (resolvedSymbol.isPresent()) {
        SymTypeExpression resolvedTypeUnmodified =
            SymTypeExpressionFactory.createFromSymbol(resolvedSymbol.get());
        SymTypeExpression objectType =
            replaceVariablesIfNecessary(thisType, resolvedTypeUnmodified);
        objectType.getSourceInfo().setSourceSymbol(resolvedSymbol.get());
        resolvedSymType = Optional.of(objectType);
      }
      else {
        resolvedSymType = Optional.empty();
      }
    }
    // search in super types
    if (resolvedSymType.isEmpty()) {
      // private -> protected while searching in super types
      AccessModifier superModifier = private2Protected(accessModifier);
      List<SymTypeExpression> superTypes = getSuperTypes(thisType);
      resolvedSymType = Optional.empty();
      for (SymTypeExpression superType : superTypes) {
        Optional<SymTypeExpression> resolvedInSuper =
            resolveType(superType, name, superModifier, predicate);
        if (resolvedSymType.isPresent() && resolvedInSuper.isPresent()) {
          Log.error("0xFD224 found type with name \""
              + name + "\" in multiple super types of \""
              + thisType.printFullName() + "\"");
        }
        resolvedSymType = resolvedInSuper;
      }
    }

    Optional<SymTypeExpression> symTypeFreeVarsReplaced =
        resolvedSymType.map(t -> replaceFreeTypeVariables(thisType, t));

    return symTypeFreeVarsReplaced;
  }

  /**
   * resolves all types within the type including supertypes
   */
  public static Map<String, SymTypeExpression> getAllTypes(
      SymTypeExpression thisType,
      AccessModifier accessModifier,
      Predicate<TypeSymbol> predicate
  ) {
    return getDelegate()._getAllTypes(thisType, accessModifier, predicate);
  }

  protected Map<String, SymTypeExpression> _getAllTypes(
      SymTypeExpression thisType,
      AccessModifier accessModifier,
      Predicate<TypeSymbol> predicate
  ) {
    Map<String, SymTypeExpression> allTypes = new LinkedHashMap<>();
    Collection<String> names = _internal_getMemberNames(thisType);
    for (String name : names) {
      Optional<SymTypeExpression> typeOpt =
          resolveType(thisType, name, accessModifier, predicate);
      if (typeOpt.isPresent()) {
        allTypes.put(name, typeOpt.get());
      }
    }
    return allTypes;
  }

  /**
   * checks if the symtypeExpression is of a (sym)type to be resolved in,
   * e.g., this includes objects but excludes primitives.
   * This method is intended to be used
   * to increase the specificity of error messages,
   * it is NOT necessary to check a type with it before calling resolve[...]().
   * s. a. {@link NominalSuperTypeCalculator}
   */
  public static boolean canResolveIn(SymTypeExpression thisType) {
    return getDelegate()._canResolveIn(thisType);
  }

  protected boolean _canResolveIn(SymTypeExpression thisType) {
    return thisType.isObjectType() ||
        thisType.isGenericType() ||
        thisType.isTypeVariable() ||
        thisType.isUnionType() ||
        thisType.isIntersectionType() ||
        thisType.isRegExType();
    // array.size not supported yet
  }

  // Helper / Extension Points

  /**
   * Resolves locally, EXCLUDING supertypes.
   * This can be used as an extension point.
   * S.a. {@link WithinScopeBasicSymbolsResolver#resolveType(IBasicSymbolsScope, String, AccessModifier, Predicate)}
   */
  protected Optional<VariableSymbol> resolveVariableLocally(
      IBasicSymbolsScope scope,
      String name,
      AccessModifier accessModifier,
      Predicate<VariableSymbol> predicate) {
    // todo replace with resolveVariableLocally, as soon as it supports
    // Accessmodifier and Predicate.
    List<VariableSymbol> resolved = scope.resolveVariableLocallyMany(
        false,
        name,
        accessModifier,
        // assure that no symbols of supertypes are added by the resolver
        predicate.and(getIsLocalSymbolPredicate(scope))
    );
    // todo remove given a fixed resolver
    resolved = resolved.stream()
        .filter(predicate.and(getIsLocalSymbolPredicate(scope)))
        .collect(Collectors.toList());
    // todo remove as soon as resolveVariableLocally is used
    if (resolved.size() > 1) {
      Log.error("0xFD225 resolved " + resolved.size()
          + "occurences of variable " + name
          + ", but expected only one:" + System.lineSeparator()
          + resolved.stream()
          .map(ISymbol::getFullName)
          .collect(Collectors.joining(System.lineSeparator()))
      );
      resolved = Collections.emptyList();
    }
    return resolved.stream().findAny();
  }

  /**
   * Resolves locally, EXCLUDING supertypes.
   * This can be used as an extension point.
   */
  protected List<FunctionSymbol> resolveFunctionLocallyMany(
      IBasicSymbolsScope scope,
      String name,
      AccessModifier accessModifier,
      Predicate<FunctionSymbol> predicate) {
    List<FunctionSymbol> resolved = scope.resolveFunctionLocallyMany(
        false,
        name,
        accessModifier,
        // assure that no symbols of supertypes are added by the resolver
        predicate.and(getIsLocalSymbolPredicate(scope))
    );
    // todo remove given a fixed resolver
    resolved = resolved.stream()
        .filter(predicate.and(getIsLocalSymbolPredicate(scope)))
        .collect(Collectors.toList());
    return resolved;
  }

  /**
   * @deprecated renamend to
   *     {@link #resolveFunctionLocallyMany(IBasicSymbolsScope, String, AccessModifier, Predicate)}
   */
  @Deprecated(forRemoval = true)
  protected List<FunctionSymbol> resolveFunctionLocally(
      IBasicSymbolsScope scope,
      String name,
      AccessModifier accessModifier,
      Predicate<FunctionSymbol> predicate) {
    return resolveFunctionLocallyMany(scope, name, accessModifier, predicate);
  }

  /**
   * Resolves locally, EXCLUDING supertypes.
   * This can be used as an extension point.
   * S.a. {@link WithinScopeBasicSymbolsResolver#resolveType(IBasicSymbolsScope, String, AccessModifier, Predicate)}
   */
  protected Optional<TypeSymbol> resolveTypeLocally(
      IBasicSymbolsScope scope,
      String name,
      AccessModifier accessModifier,
      Predicate<TypeSymbol> predicate) {
    // todo replace with resolveTypeLocally, as soon as it supports
    //  Accessmodifier and Predicate.
    List<TypeSymbol> resolved = scope.resolveTypeLocallyMany(
        false,
        name,
        accessModifier,
        predicate
            // todo removed as soon as
            //  TypeVarSymbol does not extend TypeSymbol anymore
            .and(getIsNotTypeVarSymbolPredicate())
            // assure that no symbols of supertypes are added by the resolver
            .and(getIsLocalSymbolPredicate(scope))
    );
    // todo remove given a fixed resolver
    resolved = resolved.stream()
        .filter(predicate
            .and(getIsNotTypeVarSymbolPredicate())
            .and(getIsLocalSymbolPredicate(scope))
        )
        .collect(Collectors.toList());
    // todo remove as soon as resolveTypeLocally is used
    if (resolved.size() > 1) {
      Log.error("0xFD221 resolved multiple types \""
          + name + "\" (locally in the same scope)");
    }
    return resolved.stream().findAny();
  }

  // Helper

  /**
   * Even more legacy code workarounds:
   * filter out anything that is not in the exact scope
   * due to questionable resolve strategy overrides in Scope classes...
   */
  protected Predicate<ISymbol> getIsLocalSymbolPredicate(IScope localScope) {
    return s -> {
      if (s.getEnclosingScope() != localScope) {
        Log.trace("filtered symbol '"
                + s.getFullName() + "' as it was resolved "
                + "in a different scope, even though "
                + "\"resolve[...]Locally[...]\" was used",
            LOG_NAME
        );
        return false;
      }
      return true;
    };
  }

  protected Predicate<TypeSymbol> getIsNotTypeVarSymbolPredicate() {
    return ts -> {
      if (BasicSymbolsMill.typeDispatcher().isBasicSymbolsTypeVar(ts)) {
        Log.trace("filtered symbol '"
                + ts.getFullName() + "' as it is a type variable"
                + ", which are only resolved based on scopes" +
                ", e.g., not TypeWithVar.typeVarName.",
            LOG_NAME
        );
        return false;
      }
      return true;
    };
  }

  protected IBasicSymbolsTypeDispatcher getTypeDispatcher() {
    return BasicSymbolsMill.typeDispatcher();
  }

  protected SymTypeExpression replaceVariablesIfNecessary(
      SymTypeExpression dependencyType,
      SymTypeExpression dependentType) {
    if (dependencyType.isGenericType()) {
      Map<SymTypeVariable, SymTypeExpression> replaceMap =
          ((SymTypeOfGenerics) dependencyType).getTypeVariableReplaceMap();
      return TypeParameterRelations.replaceTypeVariables(dependentType, replaceMap);
    }
    else {
      return dependentType;
    }
  }

  protected Optional<IBasicSymbolsScope> getSpannedScope(SymTypeExpression type) {
    // note that this function has to be kept in sync with
    // SymTypeRelations::getNominalSuperTypes,
    // e.g., the union's lub is considered to be
    // an alternative (, less specified) representation of the union's type,
    // while the type's in an intersection are the intersection's supertypes.
    Optional<IBasicSymbolsScope> spannedScope;
    // object
    if (type.isObjectType() || type.isGenericType()) {
      spannedScope = Optional.of(type.getTypeInfo().getSpannedScope());
    }
    // type variable
    // considered unknown scope with super types
    else if (type.isTypeVariable()) {
      spannedScope = Optional.empty();
    }
    // intersection
    // considered empty scope with super types
    else if (type.isIntersectionType()) {
      spannedScope = Optional.empty();
    }
    // union
    // only its lub is known to be present
    else if (type.isUnionType()) {
      Collection<SymTypeExpression> unionizedTypes =
          ((SymTypeOfUnion) type).getUnionizedTypeSet();
      Optional<SymTypeExpression> lubOpt =
          SymTypeRelations.leastUpperBound(unionizedTypes);
      spannedScope = lubOpt.flatMap(lub -> getSpannedScope(lub));
    }
    else if (type.isRegExType()) {
      // considered empty, String is the (direct) nominal supertype,
      // as such, those methods are resolved later
      spannedScope = Optional.empty();
    }
    // extension point
    else {
      Log.debug("tried to get the spanned scope of "
              + type.printFullName()
              + " which is currently not supported",
          LOG_NAME
      );
      spannedScope = Optional.empty();
    }
    return spannedScope;
  }

  protected List<SymTypeExpression> getSuperTypes(SymTypeExpression thisType) {
    return SymTypeRelations.getNominalSuperTypes(thisType);
  }

  /**
   * internal; gets all member names,
   * does not filter in any way, thus not generally applicable
   */
  protected List<String> _internal_getMemberNames(SymTypeExpression thisType) {
    LinkedHashSet<String> names = new LinkedHashSet<>();
    Optional<IBasicSymbolsScope> thisScopeOpt = getSpannedScope(thisType);
    if (thisScopeOpt.isPresent()) {
      IBasicSymbolsScope thisScope = thisScopeOpt.get();
      names.addAll(_internal_getMemberNamesLocally(thisScope));
    }
    List<SymTypeExpression> superTypes = getSuperTypes(thisType);
    // in rare cases exponential, could be optimized
    for (SymTypeExpression superType : superTypes) {
      names.addAll(_internal_getMemberNames(superType));
    }
    return new ArrayList<>(names);
  }

  protected List<String> _internal_getMemberNamesLocally(
      IBasicSymbolsScope scope
  ) {
    LinkedHashSet<String> names = new LinkedHashSet<>();
    // todo severely inefficient, replace after https://git.rwth-aachen.de/monticore/monticore/-/issues/4732
    BasicSymbolsTraverser traverser = BasicSymbolsMill.inheritanceTraverser();
    traverser.add4BasicSymbols(
        new BasicSymbolsVisitor2() {
          @Override
          public void visit(TypeSymbol sym) {
            if (sym.getEnclosingScope() == scope) {
              names.add(sym.getName());
            }
          }

          @Override
          public void visit(TypeVarSymbol sym) {
            if (sym.getEnclosingScope() == scope) {
              names.add(sym.getName());
            }
          }

          @Override
          public void visit(VariableSymbol sym) {
            if (sym.getEnclosingScope() == scope) {
              names.add(sym.getName());
            }
          }

          @Override
          public void visit(FunctionSymbol sym) {
            if (sym.getEnclosingScope() == scope) {
              names.add(sym.getName());
            }
          }
        }
    );
    scope.accept(traverser);
    return new ArrayList<>(names);
  }

  /**
   * replaces any private access with protected access
   * this is done to resolve in supertypes
   * if there is no private access, this is id()
   */
  protected AccessModifier private2Protected(AccessModifier accessModifier) {
    AccessModifier newModifier = accessModifier.shallowCopy();
    Map<String, AccessModifier> map = newModifier.getDimensionToModifierMap();
    if (map.containsKey(BasicAccessModifier.DIMENSION)
        && map.get(BasicAccessModifier.DIMENSION) == BasicAccessModifier.PRIVATE) {
      map.put(BasicAccessModifier.DIMENSION, BasicAccessModifier.PROTECTED);
    }
    return newModifier;
  }

  protected Map<SymTypeVariable, SymTypeInferenceVariable> getUnboundVariableReplaceMap(
      List<SymTypeVariable> varsNotToReplace, SymTypeExpression type) {
    // 1. find all variables
    Map<SymTypeVariable, SymTypeInferenceVariable> allVarMap =
        TypeParameterRelations.getFreeVariableReplaceMap(type, BasicSymbolsMill.scope());
    // 2. get variables that actually need to be replaced (unbound)
    Map<SymTypeVariable, SymTypeInferenceVariable> freeVarMap = new LinkedHashMap<>();
    for (Map.Entry<SymTypeVariable, SymTypeInferenceVariable> e : allVarMap.entrySet()) {
      if (varsNotToReplace.stream().noneMatch(e.getKey()::deepEquals)) {
        freeVarMap.put(e.getKey(), e.getValue());
      }
    }
    return freeVarMap;
  }

  protected SymTypeExpression replaceFreeTypeVariables(
      SymTypeExpression thisType,
      SymTypeExpression type
  ) {
    // E.g.:
    // class B<V,X> {
    //   <S> C<S,V,X> f();
    // }
    // class A<T> {
    //   void <R> g() {new B<T,R>().f();}
    // }
    // In the example above, resolving f in B<T,R> will result in
    // () -> C<#FV,T,R> where #FV is a free type variable

    // 1. find all type variables already bound by the type resolved in
    List<SymTypeVariable> varsAlreadyBound =
        new SymTypeCollectionVisitor().calculate(thisType, SymTypeExpression::isTypeVariable)
            .stream().map(SymTypeExpression::asTypeVariable).collect(Collectors.toList());
    // 2. get unbound variable replacement map
    Map<SymTypeVariable, SymTypeInferenceVariable> freeVarMap =
        getUnboundVariableReplaceMap(varsAlreadyBound, type);
    // 2.5 double check that the symTab does make any sense
    assertTypeVarsAreIncluded(type, freeVarMap.keySet());
    // 3. actually replace the free variables
    SymTypeExpression typeVarsReplaced =
        TypeParameterRelations.replaceTypeVariables(type, freeVarMap);

    return typeVarsReplaced;
  }

  protected void assertTypeVarsAreIncluded(
      SymTypeExpression type,
      Collection<SymTypeVariable> freeTypeVars
  ) {
    // first and foremost, check functions
    // could be extended if required
    if (type.isFunctionType() && type.asFunctionType().hasSymbol()) {
      FunctionSymbol fSym = type.asFunctionType().getSymbol();
      List<TypeVarSymbol> includedVarSyms = fSym
          .getSpannedScope().getLocalTypeVarSymbols();
      List<SymTypeVariable> includedVars = includedVarSyms.stream()
          .map(SymTypeExpressionFactory::createTypeVariable)
          .collect(Collectors.toList());
      if (freeTypeVars.stream().anyMatch(
          ftv -> includedVars.stream().noneMatch(ftv::deepEquals))
      ) {
        Log.error("0xFD570 resolved " + fSym.getFullName()
            + " with type " + type.printFullName()
            + " with free type variables " + freeTypeVars.stream()
            .map(SymTypeVariable::printFullName)
            .collect(Collectors.joining(", "))
            + ", but in the symbol there is only the type variables "
            + includedVars.stream().map(SymTypeVariable::printFullName)
            .collect(Collectors.joining(", "))
            + "! Is the symbol table correct?"
        );
      }
    }
  }

  // static delegate

  public static void init() {
    Log.trace("init default WithinTypeBasicSymbolsResolver", "TypeCheck setup");
    setDelegate(new WithinTypeBasicSymbolsResolver());
  }

  public static void reset() {
    WithinScopeBasicSymbolsResolver.delegate = null;
  }

  protected static void setDelegate(
      WithinTypeBasicSymbolsResolver newDelegate
  ) {
    WithinTypeBasicSymbolsResolver.delegate = Preconditions.checkNotNull(newDelegate);
  }

  protected static WithinTypeBasicSymbolsResolver getDelegate() {
    if (WithinTypeBasicSymbolsResolver.delegate == null) {
      init();
    }
    return WithinTypeBasicSymbolsResolver.delegate;
  }

}
