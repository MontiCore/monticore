/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.types;

import de.monticore.symboltable.CommonScope;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ScopeSpanningSymbol;
import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.SymbolKind;
import de.monticore.symboltable.SymbolPredicate;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.symboltable.resolving.ResolvingInfo;
import de.monticore.symboltable.types.references.JTypeReference;
import de.se_rwth.commons.logging.Log;

import java.util.Collection;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static de.monticore.symboltable.modifiers.AccessModifier.ALL_INCLUSION;
import static de.monticore.symboltable.modifiers.BasicAccessModifier.PACKAGE_LOCAL;
import static de.monticore.symboltable.modifiers.BasicAccessModifier.PRIVATE;
import static de.monticore.symboltable.modifiers.BasicAccessModifier.PROTECTED;

/**
 * @author Pedram Mir Seyed Nazari
 */
public class CommonJTypeScope extends CommonScope {

  public CommonJTypeScope(Optional<MutableScope> enclosingScope) {
    super(enclosingScope, true);
  }

  @Override
  public void setSpanningSymbol(ScopeSpanningSymbol symbol) {
    checkArgument(symbol instanceof JTypeSymbol);
    super.setSpanningSymbol(symbol);
  }

  @Override
  @SuppressWarnings("unchecked")
  public Optional<? extends JTypeSymbol> getSpanningSymbol() {
    return (Optional <? extends JTypeSymbol>) super.getSpanningSymbol();
  }

  @Override
  public <T extends Symbol> Optional<T> resolve(String symbolName, SymbolKind kind) {
    return this.resolve(symbolName, kind, ALL_INCLUSION);
  }

  @Override
  public <T extends Symbol> Optional<T> resolve(String name, SymbolKind kind, AccessModifier modifier) {
    Optional<T> resolvedSymbol = this.resolveImported(name, kind, modifier);

    if (!resolvedSymbol.isPresent()) {
      resolvedSymbol = resolveInSuperTypes(name, kind, modifier);
    }

    if (!resolvedSymbol.isPresent()) {
      // continue with enclosing scope
      resolvedSymbol = super.resolve(name, kind, modifier);
    }

    return resolvedSymbol;
  }

  protected <T extends Symbol> Optional<T> resolveInSuperTypes(String name, SymbolKind kind, AccessModifier modifier) {
    Optional<T> resolvedSymbol = Optional.empty();

    final JTypeSymbol spanningSymbol = getSpanningSymbol().get();

    // resolve in super class
    if (spanningSymbol.getSuperClass().isPresent()) {
      final JTypeSymbol superClass = spanningSymbol.getSuperClass().get().getReferencedSymbol();
      resolvedSymbol = resolveInSuperType(name, kind, modifier, superClass);
    }

    // resolve in interfaces
    if (!resolvedSymbol.isPresent()) {
      for (JTypeReference<? extends JTypeSymbol> interfaceRef : spanningSymbol.getInterfaces()) {
        final JTypeSymbol interfaze = interfaceRef.getReferencedSymbol();
        resolvedSymbol = resolveInSuperType(name, kind, modifier, interfaze);

        // Stop as soon as symbol is found in an interface. Note that the other option is to
        // search in all interfaces and throw an ambiguous exception if more than one symbol is
        // found. => TODO discuss it!
        if (resolvedSymbol.isPresent()) {
          break;
        }
      }
    }

    return resolvedSymbol;
  }

  private <T extends Symbol> Optional<T> resolveInSuperType(String name, SymbolKind kind,
      final AccessModifier modifier, JTypeSymbol superType) {

    Log.trace("Continue in scope of super class " + superType.getName(), CommonJTypeScope.class
        .getSimpleName());
    // Private symbols cannot be resolved from the super class. So, the modifier must at
    // least be protected when searching in the super class scope
    AccessModifier modifierForSuperClass = getModifierForSuperClass(modifier, superType);

    return superType.getSpannedScope().resolveImported(name, kind, modifierForSuperClass);
  }

  private AccessModifier getModifierForSuperClass(AccessModifier modifier, JTypeSymbol superType) {
    if (modifier.equals(ALL_INCLUSION) || modifier.equals(PRIVATE) || modifier.equals(PACKAGE_LOCAL)) {
      if (getSpanningSymbol().get().getPackageName().equals(superType.getPackageName())) {
        return PACKAGE_LOCAL;
      }
      else {
        return PROTECTED;
      }
    }
    return modifier;
  }

  @Override
  public <T extends Symbol> Optional<T> resolveImported(String name, SymbolKind kind, AccessModifier modifier) {
    final Collection<T> resolvedSymbols = resolveManyLocally(new ResolvingInfo(getResolvingFilters()), name, kind, modifier, x -> true);

    if (resolvedSymbols.isEmpty()) {
      return resolveInSuperTypes(name, kind, modifier);
    }

    return getResolvedOrThrowException(resolvedSymbols);
  }
}
