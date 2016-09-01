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

package de.monticore.symboltable.resolving;

import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.SymbolKind;
import de.se_rwth.commons.Names;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Default implementation for {@link ResolvingFilter}.
 *
 * @author Pedram Mir Seyed Nazari
 *
 */
// TODO PN remove formal type argument, since not needed anymore
public class CommonResolvingFilter<S extends Symbol> implements ResolvingFilter<S> {

  private final SymbolKind targetKind;

  /**
   * @deprecated use {@link #create(SymbolKind)} instead
   */
  @Deprecated
  public static <S extends Symbol> ResolvingFilter<S> create(Class<S> symbolClass, SymbolKind symbolKind) {
    return new CommonResolvingFilter<>(symbolClass, symbolKind);
  }

  public static ResolvingFilter create(SymbolKind symbolKind) {
    return new CommonResolvingFilter<>(symbolKind);
  }

  /**
   * @deprecated use {@link #CommonResolvingFilter(SymbolKind)} instead
   */
  @Deprecated
  public CommonResolvingFilter(Class<S> symbolClass, SymbolKind targetKind) {
    this(targetKind);
  }

  public CommonResolvingFilter(SymbolKind targetKind) {
    this.targetKind = targetKind;
  }

  @Override
  @Deprecated
  public Optional<Symbol> filter(ResolvingInfo resolvingInfo, String name, final List<Symbol> symbols) {
    final Set<Symbol> resolvedSymbols = new LinkedHashSet<>();

    for (Symbol symbol : symbols) {
      // TODO PN in eigene Methode auslagern, damit Unterklassen das überschreiben können.
      if (symbol.isKindOf(targetKind)) {

        if (symbol.getName().equals(name) || symbol.getFullName().equals(name)) {
          resolvedSymbols.add(symbol);
        }
      }
    }

    return ResolvingFilter.getResolvedOrThrowException(resolvedSymbols);
  }

  @Override
  public Collection<Symbol> filter(ResolvingInfo resolvingInfo, List<Symbol> symbols) {
      // TODO PN  create new LinkedHashSet<>() instead
      final Collection<Symbol> foundSymbols = new LinkedHashSet<>();

      for (Symbol symbol : symbols) {
        // TODO PN in eigene Methode auslagern, damit Unterklassen das überschreiben können.
        if (symbol.isKindOf(targetKind)) {
          foundSymbols.add(symbol);
        }
      }
      
      return foundSymbols;
  }

  @Override
  public Optional<Symbol> filter(ResolvingInfo resolvingInfo, String name, Map<String, List<Symbol>> symbols) {
    final Set<Symbol> resolvedSymbols = new LinkedHashSet<>();

    final String simpleName = Names.getSimpleName(name);

    if (symbols.containsKey(simpleName)) {
      for (Symbol symbol : symbols.get(simpleName)) {
        if (symbol.isKindOf(targetKind)) {
          if (symbol.getName().equals(name) || symbol.getFullName().equals(name)) {
            resolvedSymbols.add(symbol);
          }
        }
      }
    }

    return ResolvingFilter.getResolvedOrThrowException(resolvedSymbols);
  }

  @Override
  public SymbolKind getTargetKind() {
    return targetKind;
  }
  
  /**
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return CommonResolvingFilter.class.getSimpleName() + " [" + targetKind.getName() + "]";
  }

}
