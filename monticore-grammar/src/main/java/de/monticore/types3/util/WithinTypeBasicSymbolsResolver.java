// (c) https://github.com/MontiCore/monticore
package de.monticore.types3.util;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.basicsymbols._util.BasicSymbolsTypeDispatcher;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.symboltable.modifiers.BasicAccessModifier;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfFunction;
import de.monticore.types.check.SymTypeOfGenerics;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
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
 * The results will be in form of SymTypeExpressions
 */
public class WithinTypeBasicSymbolsResolver {

  protected static final String VISIBILITY_MODIFIER_KEY =
      BasicAccessModifier.PUBLIC.getDimensionToModifierMap()
          .keySet().stream().findFirst().get();

  protected SymTypeVariableReplaceVisitor replaceVisitor;

  protected ExplicitSuperTypeCalculator superTypeCalculator;

  public WithinTypeBasicSymbolsResolver() {
    // default values
    replaceVisitor = new SymTypeVariableReplaceVisitor();
    superTypeCalculator = new ExplicitSuperTypeCalculator();
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
    if (!thisType.hasTypeInfo()) {
      Log.error("0xFD36A internal error: type symbol expected");
      resolvedSymType = Optional.empty();
    }
    else {
      Optional<VariableSymbol> resolvedSymbol = resolveVariableLocally(
          thisType.getTypeInfo().getSpannedScope(),
          name,
          accessModifier,
          predicate
      );
      if (resolvedSymbol.isPresent()) {
        SymTypeExpression resolvedTypeUnmodified = resolvedSymbol.get().getType();
        resolvedSymType = Optional.of(
            replaceVariablesIfNecessary(thisType, resolvedTypeUnmodified)
        );
      }
      else {
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
    if (!thisType.hasTypeInfo()) {
      Log.error("0xFD36B internal error: type symbol expected");
    }
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
    }
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
    if (!thisType.hasTypeInfo()) {
      Log.error("0xFD36A internal error: type symbol expected");
      resolvedSymType = Optional.empty();
    }
    else {
      Optional<TypeSymbol> resolvedSymbol = resolveTypeLocally(
          thisType.getTypeInfo().getSpannedScope(),
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
    }
    return resolvedSymType;
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
        predicate.and(v -> v.getEnclosingScope() == scope)
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
        predicate.and(f -> f.getEnclosingScope() == scope)
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
        predicate.and(t -> t.getEnclosingScope() == scope)
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

  protected List<SymTypeExpression> getSuperTypes(SymTypeExpression thisType) {
    return superTypeCalculator.getExplicitSuperTypes(thisType);
  }

  /**
   * replaces any private access with protected access
   * this is done to resolve in supertypes
   * if there is no private access, this is id()
   */
  protected AccessModifier private2Protected(AccessModifier accessModifier) {
    AccessModifier newModifier = accessModifier.shallowCopy();
    Map<String, AccessModifier> map = newModifier.getDimensionToModifierMap();
    if (map.containsKey(VISIBILITY_MODIFIER_KEY)
        && map.get(VISIBILITY_MODIFIER_KEY) == BasicAccessModifier.PRIVATE) {
      map.put(VISIBILITY_MODIFIER_KEY, BasicAccessModifier.PROTECTED);
    }
    return newModifier;
  }
}
