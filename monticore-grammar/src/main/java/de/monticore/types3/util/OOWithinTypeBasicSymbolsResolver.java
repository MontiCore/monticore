// (c) https://github.com/MontiCore/monticore
package de.monticore.types3.util;

import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.oosymbols.OOSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.symboltable.modifiers.StaticAccessModifier;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfFunction;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
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

  /**
   * resolves within a type including supertypes
   */
  public List<SymTypeOfFunction> resolveConstructors(
      SymTypeExpression thisType,
      AccessModifier accessModifier,
      Predicate<FunctionSymbol> predicate) {
    List<SymTypeOfFunction> resolvedSymTypes = new ArrayList<>();
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
      List<SymTypeOfFunction> resolvedTypesUnmodified = resolvedSymbols.stream()
          .map(FunctionSymbol::getFunctionType)
          .collect(Collectors.toList());
      // checking for broken symbol table
      if (resolvedTypesUnmodified.stream().anyMatch(f ->
          !f.getType().hasTypeInfo() ||
              !f.getType().getTypeInfo().getFullName()
                  .equals(thisType.getTypeInfo().getFullName())
      )) {
        Log.error("0xFDCC2 unexpected constructor return type(s) of type "
            + thisType.printFullName()
            + ", constructors are " + System.lineSeparator()
            + resolvedTypesUnmodified.stream()
            .map(SymTypeOfFunction::printFullName)
            .collect(Collectors.joining(System.lineSeparator()))
        );
      }
      resolvedSymTypes = resolvedTypesUnmodified.stream()
          .map(f -> (SymTypeOfFunction) replaceVariablesIfNecessary(thisType, f))
          .collect(Collectors.toList());
    }

    // do not search super types for constructors

    return resolvedSymTypes;
  }

  // Helper

  /**
   * resolves locally, EXCLUDING supertypes
   * this filters out constructors
   */
  @Override
  protected List<FunctionSymbol> resolveFunctionLocally(
      IBasicSymbolsScope scope,
      String name,
      AccessModifier accessModifier,
      Predicate<FunctionSymbol> predicate) {
    return super.resolveFunctionLocally(
        scope, name, accessModifier,
        predicate.and(Predicate.not(this::isConstructor))
    );
  }

  /**
   * same as {@link #resolveFunctionLocally}
   * but does only returns constructors
   *
   * @deprecated is to be made private, use {@link #resolveConstructors}
   */
  @Deprecated
  public List<FunctionSymbol> resolveConstructorLocally(
      IBasicSymbolsScope scope,
      String name,
      AccessModifier accessModifier,
      Predicate<FunctionSymbol> predicate
  ) {
    return super.resolveFunctionLocally(
        scope, name,
        removeStaticness(accessModifier),
        predicate.and(this::isConstructor)
    );
  }

  // Helper

  protected boolean isConstructor(FunctionSymbol func) {
    if (OOSymbolsMill.typeDispatcher().isOOSymbolsMethod(func)) {
      MethodSymbol method =
          OOSymbolsMill.typeDispatcher().asOOSymbolsMethod(func);
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

}
