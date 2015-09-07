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

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.google.common.collect.Lists;
import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.SymbolKind;

/**
 * TODO: Write me!
 *
 * @author Pedram Mir Seyed Nazari
 *
 */
public class CommonResolvingFilter<S extends Symbol> implements ResolvingFilter<S> {

  private final SymbolKind targetKind;
  private final Class<S> symbolClass;
  
  public static <S extends Symbol> ResolvingFilter<S> create(Class<S> symbolClass, SymbolKind
      symbolKind) {
    // TODO PN check einbauen dass kein Resolver existiert, dessen S und T nicht mit den aktuellen Parametern kompatibel sind
    return new CommonResolvingFilter<>(symbolClass, symbolKind);
  }
  

  public CommonResolvingFilter(Class<S> symbolClass, SymbolKind targetKind) {
    this.symbolClass = symbolClass;
    this.targetKind = targetKind;
  }

  @Override
  @SuppressWarnings("unchecked")
  public Optional<S> filter(ResolvingInfo resolvingInfo, String name, final List<Symbol> symbols) {
    final Set<S> resolvedSymbols = new HashSet<>();

    for (Symbol symbol : symbols) {
      // TODO PN in eigene Methode auslagern, damit Unterklassen das überschreiben können.
      if (symbol.isKindOf(targetKind) && symbolClass.isAssignableFrom(symbol.getClass())) {

        if (symbol.getName().equals(name) || symbol.getFullName().equals(name)) {
          resolvedSymbols.add((S) symbol);
        }
      }
    }

    return ResolvingFilter.getResolvedOrThrowException(resolvedSymbols);
  }

  @Override
  @SuppressWarnings("unchecked")
  public Collection<S> filter(ResolvingInfo resolvingInfo, List<Symbol> symbols) {
      final Collection<S> foundSymbols = Lists.newArrayList();

      for (Symbol symbol : symbols) {
        // TODO PN in eigene Methode auslagern, damit Unterklassen das überschreiben können.
        if (symbol.isKindOf(targetKind) && symbolClass.isAssignableFrom(symbol.getClass())) {
          foundSymbols.add((S)symbol);
        }
      }
      
      return foundSymbols;
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
