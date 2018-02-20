/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable;

import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.symboltable.modifiers.IncludesAccessModifierPredicate;
import de.se_rwth.commons.logging.Log;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Provides helper methods for {@link de.monticore.symboltable.Scope}.
 *
 * @author Pedram Mir Seyed Nazari
 */
public final class Scopes {

  private Scopes(){
  }

  public static Scope getTopScope(final Scope scope) {
    Log.errorIfNull(scope);

    Scope currentScope = scope;

    while (currentScope.getEnclosingScope().isPresent()) {
      currentScope = currentScope.getEnclosingScope().get();
    }

    return currentScope;
  }

  public static Optional<GlobalScope> getGlobalScope(final Scope scope) {
    Scope topScope = getTopScope(scope);

    return (topScope instanceof GlobalScope)
        ? Optional.of((GlobalScope) topScope)
        : Optional.empty();
  }

  public static Optional<ArtifactScope> getArtifactScope(final Scope scope) {
    Log.errorIfNull(scope);

    if (scope instanceof ArtifactScope) {
      return Optional.of((ArtifactScope) scope);
    }

    Scope currentScope = scope;

    while (currentScope.getEnclosingScope().isPresent()) {
      currentScope = currentScope.getEnclosingScope().get();

      if (currentScope instanceof ArtifactScope) {
        return Optional.of((ArtifactScope)currentScope);
      }
    }

    return Optional.empty();

  }
  
  public static boolean isDescendant(final Scope descendant, final Scope ancestor) {
    Log.errorIfNull(descendant);
    Log.errorIfNull(ancestor);
    
    if (descendant == ancestor) {
      return false;
    }
    
    Optional<? extends Scope> parent = descendant.getEnclosingScope();
    while (parent.isPresent()) {
      if (parent.get() == ancestor) {
        return true;
      }
      parent = parent.get().getEnclosingScope();
    }
    
    return false;
  }
  
  public static Optional<? extends Scope> getFirstShadowingScope(final Scope scope) {
    Log.errorIfNull(scope);

    Optional<? extends Scope> currentScope = Optional.of(scope);
    
    while (currentScope.isPresent()) {
      if (currentScope.get().isShadowingScope()) {
        return currentScope;
      }
      currentScope = currentScope.get().getEnclosingScope();
    }
    
    return Optional.empty();
    
  }

  public static Collection<? extends Symbol> getAllEncapsulatedSymbols(Scope scope) {
    if(scope == null) {
      return new LinkedHashSet<>();
    }

    final Set<Symbol> encapsulatedSymbols = new LinkedHashSet<>();

    Scope nextScope = scope;

    while (true) {
      encapsulatedSymbols.addAll(nextScope.resolveLocally(SymbolKind.KIND));

      if (!nextScope.getEnclosingScope().isPresent() || (nextScope.getEnclosingScope().get() instanceof GlobalScope)) {
        break;
      }

      nextScope = nextScope.getEnclosingScope().orElse(null);
    }

    return encapsulatedSymbols;
  }

  public static Collection<Symbol> getLocalSymbolsAsCollection(final Scope scope) {
    final Set<Symbol> allSymbols = new LinkedHashSet<>();
    scope.getLocalSymbols().values().forEach(allSymbols::addAll);
    return allSymbols;
  }

  public static <T extends Symbol> Set<T> filterSymbolsByAccessModifier(AccessModifier modifier, Collection<T> resolvedUnfiltered) {
    return new LinkedHashSet<>(resolvedUnfiltered.stream().filter(new IncludesAccessModifierPredicate(modifier)).collect(Collectors.toSet()));
  }


}
