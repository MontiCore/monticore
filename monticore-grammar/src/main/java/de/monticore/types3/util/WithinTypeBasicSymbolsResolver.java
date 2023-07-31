// (c) https://github.com/MontiCore/monticore
package de.monticore.types3.util;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.basicsymbols._util.BasicSymbolsTypeDispatcher;
import de.monticore.symboltable.IScope;
import de.monticore.symboltable.ISymbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.symboltable.modifiers.BasicAccessModifier;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfFunction;
import de.monticore.types.check.SymTypeOfGenerics;
import de.monticore.types.check.SymTypeOfUnion;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * resolves within a type,
 * but in a more type correct way then our resolve algorithm,
 * as some additions cannot be (simply) added to it.
 * E.g., given generics, the correct type parameters will be set.
 * The results will be in form of SymTypeExpressions.
 */
public class WithinTypeBasicSymbolsResolver {

  protected static final String LOG_NAME = "WithinTypeResolving";

  protected SymTypeVariableReplaceVisitor replaceVisitor;

  protected SymTypeRelations symTypeRelations;

  public WithinTypeBasicSymbolsResolver(SymTypeRelations symTypeRelations) {
    this.symTypeRelations = symTypeRelations;
    // default values
    replaceVisitor = new SymTypeVariableReplaceVisitor();
  }

  public WithinTypeBasicSymbolsResolver() {
    // default values
    replaceVisitor = new SymTypeVariableReplaceVisitor();
    symTypeRelations = new SymTypeRelations();
  }

  protected SymTypeRelations getSymTypeRelations() {
    return symTypeRelations;
  }

  /**
   * resolves within a type including supertypes
   */
  public Optional<SymTypeExpression> resolveVariable(
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
      Optional<VariableSymbol> resolvedSymbol = resolveVariableLocally(
          spannedScopeOpt.get(),
          name,
          accessModifier,
          predicate
      );
      resolvedSymType = resolvedSymbol.map(
          s -> replaceVariablesIfNecessary(thisType, s.getType())
      );
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
    return resolvedSymType;
  }

  /**
   * resolves within a type including supertypes
   */
  public List<SymTypeOfFunction> resolveFunctions(
      SymTypeExpression thisType,
      String name,
      AccessModifier accessModifier,
      Predicate<FunctionSymbol> predicate) {
    List<SymTypeOfFunction> resolvedSymTypes = new ArrayList<>();
    Optional<IBasicSymbolsScope> spannedScopeOpt = getSpannedScope(thisType);
    if (spannedScopeOpt.isEmpty()) {
      resolvedSymTypes = new ArrayList<>();
    }
    // search in this scope
    else {
      //todo outer types (and vs. supertypes) not really?
      List<FunctionSymbol> resolvedSymbols = resolveFunctionLocally(
          thisType.getTypeInfo().getSpannedScope(),
          name,
          accessModifier,
          predicate
      );
      List<SymTypeOfFunction> resolvedTypesUnmodified = resolvedSymbols.stream()
          .map(FunctionSymbol::getFunctionType)
          .collect(Collectors.toList());
      resolvedSymTypes = resolvedTypesUnmodified.stream()
          .map(f -> (SymTypeOfFunction) replaceVariablesIfNecessary(thisType, f))
          .collect(Collectors.toList());
    }
    // search in super types
    // private -> protected while searching in super types
    AccessModifier superModifier = private2Protected(accessModifier);
    List<SymTypeExpression> superTypes = getSuperTypes(thisType);
    List<SymTypeOfFunction> superFuncs = new ArrayList<>();
    for (SymTypeExpression superType : superTypes) {
      List<SymTypeOfFunction> resolvedInSuper =
          resolveFunctions(superType, name, superModifier, predicate);
      // filter based on being overridden / hidden (static)
      // Java Spec 20 8.4.8.1 overriding methods need to have the SAME signature,
      // e.g., Integer getX() overrides Number getX()
      // e.g., void setX(Number x) does not override void setX(Integer x)
      // we assume that CoCos corresponding to the compile time errors of
      // Java Spec 20 8.4.8 are used
      for (Iterator<SymTypeOfFunction> fItr = resolvedInSuper.iterator();
           fItr.hasNext(); ) {
        SymTypeOfFunction superFunc = fItr.next();
        if (resolvedSymTypes.stream()
            .anyMatch(f -> f.deepEqualsSignature(superFunc))) {
          fItr.remove();
        }
      }
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
    resolvedSymTypes.addAll(filteredSuperFuncs);

    return resolvedSymTypes;
  }

  /**
   * resolves within a type including supertypes
   */
  public Optional<SymTypeExpression> resolveType(
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
        resolvedSymType = Optional.of(
            replaceVariablesIfNecessary(thisType, resolvedTypeUnmodified)
        );
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

    return resolvedSymType;
  }

  /**
   * checks if the symtypeExpression is of a (sym)type to be resolved in,
   * e.g., this includes objects but excludes primitives.
   * This method is intended to be used
   * to increase the specificity of error messages,
   * it is NOT necessary to check a type with it before calling resolve[...]().
   * s. a. {@link NominalSuperTypeCalculator}
   */
  public boolean canResolveIn(SymTypeExpression thisType) {
    return thisType.isObjectType() ||
        thisType.isGenericType() ||
        thisType.isTypeVariable() ||
        thisType.isUnionType() ||
        thisType.isIntersectionType();
    // array.size not supported yet
  }

  // Helper

  /**
   * resolves locally, EXCLUDING supertypes
   */
  protected Optional<VariableSymbol> resolveVariableLocally(
      IBasicSymbolsScope scope,
      String name,
      AccessModifier accessModifier,
      Predicate<VariableSymbol> predicate) {
    // may include symbols of supertypes, thus the predicate
    Optional<VariableSymbol> resolved = scope.resolveVariable(
        name,
        accessModifier,
        predicate.and(getIsLocalSymbolPredicate(scope))
    );
    return resolved;
  }

  /**
   * resolves locally, EXCLUDING supertypes
   */
  protected List<FunctionSymbol> resolveFunctionLocally(
      IBasicSymbolsScope scope,
      String name,
      AccessModifier accessModifier,
      Predicate<FunctionSymbol> predicate) {
    // may include symbols of supertypes, thus the predicate
    List<FunctionSymbol> resolved = scope.resolveFunctionLocallyMany(
        false,
        name,
        accessModifier,
        predicate.and(getIsLocalSymbolPredicate(scope))
    );
    return resolved;
  }

  /**
   * resolves locally, EXCLUDING supertypes
   */
  protected Optional<TypeSymbol> resolveTypeLocally(
      IBasicSymbolsScope scope,
      String name,
      AccessModifier accessModifier,
      Predicate<TypeSymbol> predicate) {
    // may include symbols of supertypes, thus the predicate
    List<TypeSymbol> resolved = scope.resolveTypeLocallyMany(
        false,
        name,
        accessModifier,
        predicate.and(getIsLocalSymbolPredicate(scope))
    );
    // prefer variables to concrete types in the same scope,
    // e.g., class A<B>{class B{} B b = new B();} is not valid Java
    if (resolved.stream().anyMatch(getTypeDispatcher()::isTypeVar)) {
      resolved = resolved.stream()
          .filter(Predicate.not(getTypeDispatcher()::isTypeVar))
          .collect(Collectors.toList());
    }
    if (resolved.size() > 1) {
      Log.error("0xFD221 resolved multiple types \""
          + name + "\" (locally in the same scope)");
    }
    return resolved.stream().findAny();
  }

  /**
   * Even more legacy code workarounds:
   * filter out anything that is not in the exact scope
   * due to questionable resolve strategy overrides in Scope classes...
   */
  protected Predicate<ISymbol> getIsLocalSymbolPredicate(IScope localScope) {
    return s -> {
      if (s.getEnclosingScope() != localScope) {
        Log.info("filtered symbol '"
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

  protected BasicSymbolsTypeDispatcher getTypeDispatcher() {
    return BasicSymbolsMill.typeDispatcher();
  }

  protected SymTypeExpression replaceVariablesIfNecessary(
      SymTypeExpression dependencyType,
      SymTypeExpression dependentType) {
    if (dependencyType.isGenericType()) {
      Map<TypeVarSymbol, SymTypeExpression> replaceMap =
          ((SymTypeOfGenerics) dependencyType).getTypeVariableReplaceMap();
      return replaceVariables(dependentType, replaceMap);
    }
    else {
      return dependentType;
    }
  }

  protected SymTypeExpression replaceVariables(
      SymTypeExpression type,
      Map<TypeVarSymbol, SymTypeExpression> replaceMap) {
    return replaceVisitor.calculate(type, replaceMap);
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
          getSymTypeRelations().leastUpperBound(unionizedTypes);
      spannedScope = lubOpt.flatMap(lub -> getSpannedScope(lub));
    }
    // extension point
    else {
      Log.info("tried to get the spanned scope of "
              + type.printFullName()
              + " which is currently not supported",
          LOG_NAME
      );
      spannedScope = Optional.empty();
    }
    return spannedScope;
  }

  protected List<SymTypeExpression> getSuperTypes(SymTypeExpression thisType) {
    return getSymTypeRelations().getNominalSuperTypes(thisType);
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
}
