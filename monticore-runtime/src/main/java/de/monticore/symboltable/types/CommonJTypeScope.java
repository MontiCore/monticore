/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2015, MontiCore, All rights reserved.
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

package de.monticore.symboltable.types;

import static com.google.common.base.Preconditions.checkArgument;
import static de.monticore.symboltable.modifiers.BasicAccessModifier.PRIVATE;
import static de.monticore.symboltable.modifiers.BasicAccessModifier.PROTECTED;

import java.util.Collection;
import java.util.Optional;

import com.google.common.collect.Collections2;
import de.monticore.symboltable.CommonScope;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ScopeSpanningSymbol;
import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.SymbolKind;
import de.monticore.symboltable.SymbolPredicate;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.symboltable.modifiers.BasicAccessModifier;
import de.monticore.symboltable.modifiers.IncludesAccessModifierPredicate;
import de.monticore.symboltable.resolving.ResolvingInfo;
import de.monticore.symboltable.types.references.JTypeReference;
import de.se_rwth.commons.logging.Log;

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
    return this.resolve(symbolName, kind, BasicAccessModifier.ABSENT);
  }

  @Override
  public <T extends Symbol> Optional<T> resolve(String name, SymbolKind kind, AccessModifier modifier) {
    // TODO PN rather resolveLocally, then in the super types, and finally in enclosing scope
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
      resolvedSymbol = resolveInSuperType(name, kind, modifier, spanningSymbol.getSuperClass().get().getReferencedSymbol());
    }

    // resolve in interfaces
    if (!resolvedSymbol.isPresent()) {
      for (JTypeReference<? extends JTypeSymbol> interfaze : spanningSymbol.getInterfaces()) {
        resolvedSymbol = resolveInSuperType(name, kind, modifier, interfaze.getReferencedSymbol());

        // Stop as soon as symbol is found in an interface. Note that the other option is to
        // search in all interfaces and throw an ambiguous exception if more than one symbol is
        // found. => TODO PN discuss it!
        if (resolvedSymbol.isPresent()) {
          break;
        }
      }
    }

    return resolvedSymbol;
  }

  private <T extends Symbol> Optional<T> resolveInSuperType(String name, SymbolKind kind,
      AccessModifier modifier, JTypeSymbol superType) {

    Log.trace("Continue in scope of super class " + superType.getName(), CommonJTypeScope.class
        .getSimpleName());
    // Private symbols cannot be resolved from the super class. So, the modifier must at
    // least be protected when searching in the super class scope
    // TODO PN use default modifier instead of protected?
    AccessModifier modifierForSuperClass = (modifier == PRIVATE) ? PROTECTED : modifier;

    // TODO PN forward current ResolverInfo?
    // TODO PN only resolve locally?
    return superType.getSpannedScope().resolveImported(name, kind, modifierForSuperClass);
  }

  @Override
  public Optional<? extends Symbol> resolve(final SymbolPredicate predicate) {
    Optional<? extends Symbol> resolvedSymbol = super.resolve(predicate);

    // TODO PN resolve in super types

    if (!resolvedSymbol.isPresent()) {
      final JTypeSymbol spanningSymbol = getSpanningSymbol().get();
      final Optional<? extends JTypeReference<? extends JTypeSymbol>> optSuperClass = spanningSymbol.getSuperClass();

      if (optSuperClass.isPresent()) {
        final JTypeSymbol superClass = optSuperClass.get().getReferencedSymbol();

        Log.trace("Continue in scope of super class " + superClass.getName(), CommonJTypeScope.class.getSimpleName());
        resolvedSymbol = superClass.getSpannedScope().resolve(predicate);
      }
    }

    return resolvedSymbol;
  }

  @Override
  public <T extends Symbol> Optional<T> resolveImported(String name, SymbolKind kind, AccessModifier modifier) {
    final Collection<T> resolvedSymbols = resolveManyLocally(new ResolvingInfo(getResolvingFilters()), name, kind);

    final Collection<T> filtered = Collections2.filter(resolvedSymbols, new IncludesAccessModifierPredicate(modifier));

    if (filtered.isEmpty()) {
      return resolveInSuperTypes(name, kind, modifier);
    }

    return getResolvedOrThrowException(filtered);
  }
}
