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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.SymbolKind;

/**
 * TODO: Write me!
 *
 * @author Pedram Mir Seyed Nazari
 *
 */
public interface ResolvingFilter<S extends Symbol> {

  Optional<S> filter(ResolvingInfo resolvingInfo, String name, List<Symbol> symbols);

  Collection<S> filter(ResolvingInfo resolvingInfo, List<Symbol> symbols);
  SymbolKind getTargetKind();


  static Collection<ResolvingFilter<? extends Symbol>> getFiltersForTargetKind
      (Collection<ResolvingFilter<? extends Symbol>> resolvingFilters, SymbolKind targetKind) {

    Collection<ResolvingFilter<? extends Symbol>> resolversForKind = new LinkedHashSet<>();

    resolversForKind.addAll(resolvingFilters.stream()
        .filter(resolver -> resolver.getTargetKind().isKindOf(targetKind))
        .collect(Collectors.toList()));

    return resolversForKind;
  }

  static <T extends Symbol> Optional<T> getResolvedOrThrowException(final Collection<T> resolved) {
    if (resolved.size() == 1) {
      return Optional.of(resolved.iterator().next());
    }
    else if (resolved.size() > 1) {
      throw new ResolvedSeveralEntriesException("0xA4095 Found " + resolved.size() + " symbols: " + resolved,
          resolved);
    }

    return Optional.empty();
  }
}
