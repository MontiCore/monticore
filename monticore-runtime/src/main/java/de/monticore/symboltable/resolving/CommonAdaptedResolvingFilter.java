/*
 * ******************************************************************************
 * MontiCore Language Workbench, www.monticore.de
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

package de.monticore.symboltable.resolving;

import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.SymbolKind;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author Pedram Mir Seyed Nazari
 *
 */
// TODO PN remove formal type argument, since not needed anymore
public abstract class CommonAdaptedResolvingFilter<S extends Symbol>
    extends CommonResolvingFilter<S> implements AdaptedResolvingFilter<S> {

  private final SymbolKind sourceKind;
  
  /**
   * @param targetSymbolClass
   * @param targetKind
   */
  public CommonAdaptedResolvingFilter(SymbolKind sourceKind, Class<S> targetSymbolClass, SymbolKind targetKind) {
    super(targetKind);
    this.sourceKind = sourceKind;
  }

  public SymbolKind getSourceKind() {
    return sourceKind;
  }

  @Override
  public Optional<Symbol> filter(ResolvingInfo resolvingInfo, String symbolName, Map<String, Collection<Symbol>> symbols) {
    final Set<Symbol> resolvedSymbols = new LinkedHashSet<>();

    final Collection<ResolvingFilter<? extends Symbol>> filtersWithoutAdapters =
        ResolvingFilter.getFiltersForTargetKind(resolvingInfo.getResolvingFilters(), getSourceKind())
            .stream()
            .filter(resolvingFilter -> !(resolvingFilter instanceof AdaptedResolvingFilter))
            .collect(Collectors.toSet());

    for (ResolvingFilter<? extends Symbol> resolvingFilter : filtersWithoutAdapters) {

      Optional<? extends Symbol> optSymbol = resolvingFilter.filter(resolvingInfo, symbolName, symbols);

      // remove the following if-statement, if adaptors should be created eager.
      if (optSymbol.isPresent()) {
        resolvedSymbols.add(translate(optSymbol.get()));
      }
    }

    return ResolvingFilter.getResolvedOrThrowException(resolvedSymbols);
  }

  @Override
  public Collection<Symbol> filter(ResolvingInfo resolvingInfo, Collection<Symbol> symbols) {
    // TODO override method
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
    return CommonAdaptedResolvingFilter.class.getSimpleName() + " [" + sourceKind.getName() + " -> " +
        getTargetKind().getName() + "]";
  }
}
