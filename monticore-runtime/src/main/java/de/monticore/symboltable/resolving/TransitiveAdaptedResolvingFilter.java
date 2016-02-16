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
import java.util.Set;
import java.util.stream.Collectors;

import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.SymbolKind;

/**
 * TODO: Write me!
 *
 * @author Pedram Mir Seyed Nazari
 *
 */
// TODO PN remove formal type argument, since not needed anymore
public abstract class TransitiveAdaptedResolvingFilter<S extends Symbol>
    extends CommonAdaptedResolvingFilter<S> implements AdaptedResolvingFilter<S> {

  /**
   * @param targetSymbolClass
   * @param targetKind
   */
  public TransitiveAdaptedResolvingFilter(SymbolKind sourceKind, Class<S> targetSymbolClass, SymbolKind targetKind) {
    super(sourceKind, targetSymbolClass, targetKind);
  }

  @Override
  public Optional<Symbol> filter(ResolvingInfo resolvingInfo, String symbolName, List<Symbol> symbols) {
    // This checks prevents circular dependencies in adapted resolving filters, e.g.,
    // A -> B (i.e., A is resolved and adapted to B) and B -> A would lead to a circular dependency:
    // A -> B -> A -> B -> A -> ...
    if (resolvingInfo.isTargetKindHandled(getTargetKind())) {
      return Optional.empty();
    }

    // Prevents circular dependencies. Note that the handled target kind is removed at the end of
    // this method.
    resolvingInfo.addHandledTargetKind(getTargetKind());

    final Set<Symbol> resolvedSymbols = new LinkedHashSet<>();

    final Collection<ResolvingFilter<? extends Symbol>> filtersForTargetKind = ResolvingFilter.
        getFiltersForTargetKind(resolvingInfo.getResolvingFilters(), getSourceKind());



    for (ResolvingFilter<? extends Symbol> resolvingFilter : filtersForTargetKind) {

      Optional<? extends Symbol> optSymbol = resolvingFilter.filter(resolvingInfo, symbolName, symbols);

      // TODO PN Remove this whole if-statement, if adaptors should be created eager.
      if (optSymbol.isPresent()) {
        resolvedSymbols.add(createAdapter(optSymbol.get()));
      }
    }

    // Removes the handled target kind. This is important, since other resolving
    // filters may handle this kind
    resolvingInfo.removeTargetKind(getTargetKind());

    return ResolvingFilter.getResolvedOrThrowException(resolvedSymbols);
  }

  @Override
  public Collection<? extends Symbol> filter(ResolvingInfo resolvingInfo, List<Symbol> symbols) {
    // TODO PN override implementation
    return super.filter(resolvingInfo, symbols);
  }

  public static Collection<CommonAdaptedResolvingFilter<? extends Symbol>> getFiltersForSourceKind
      (Collection<ResolvingFilter<? extends Symbol>> resolvingFilters, SymbolKind sourceKind) {

    return resolvingFilters.stream()
        .filter(resolvingFilter -> (resolvingFilter instanceof CommonAdaptedResolvingFilter)
            && ((CommonAdaptedResolvingFilter) resolvingFilter).getSourceKind().isKindOf(sourceKind))
        .map(resolvingFilter -> (CommonAdaptedResolvingFilter<? extends Symbol>) resolvingFilter)
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  @Override
  public String toString() {
    return CommonAdaptedResolvingFilter.class.getSimpleName() + " [" + getSourceKind().getName() + " -> " +
        getTargetKind().getName() + "]";
  }
}
