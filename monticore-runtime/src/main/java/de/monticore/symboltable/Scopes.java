/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2017, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

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
