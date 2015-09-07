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

package de.monticore.symboltable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Table;
import de.monticore.symboltable.resolving.ResolvingFilter;

/**
 * The resolver configuration maintains a mapping from names and kinds of symbols to resolvers
 * responsible for that combination of name and kind.
 * 
 * @author Pedram Mir Seyed Nazari, Sebastian Oberhoff
 */
public final class ResolverConfiguration {
  
  private final Table<String, SymbolKind, Set<ResolvingFilter<? extends Symbol>>> resolverTable = HashBasedTable
      .create();
  
  private final Set<ResolvingFilter<? extends Symbol>> topScopeResolvingFilters = new HashSet<>();
  
  /**
   * Adds a resolver to the set of resolvers for the specified symbol name and kind. Constructing a
   * new set if no resolvers were previously added for that combination of name and kind.
   * 
   * @param symbolName the name of the symbol
   * @param symbolKind the kind of the symbol
   * @param resolvingFilter the resolver to add to that combination of name and kind
   */
  public void addResolver(String symbolName, SymbolKind symbolKind,
      ResolvingFilter<? extends Symbol> resolvingFilter) {
    if (resolverTable.get(symbolName, symbolKind) == null) {
      resolverTable.put(symbolName, symbolKind, new HashSet<>());
    }
    resolverTable.get(symbolName, symbolKind).add(resolvingFilter);
  }
  
  /**
   * Retrieves the resolvers associated with the name and kind of the specified symbol.
   * 
   * @param symbol the symbol who's resolvers should be retrieved
   * @return the set of resolvers added for the given symbol
   */
  // TODO PN resolver should be configured by scope not by symbol.
  public Set<ResolvingFilter<? extends Symbol>> getResolver(Symbol symbol) {
    Set<ResolvingFilter<? extends Symbol>> resolvingFilters = resolverTable.get(symbol.getName(),
        symbol.getKind());
    return resolvingFilters != null ? resolvingFilters : Collections.emptySet();
  }
  
  /**
   * @param topScopeResolvingFilter the topScopeResolver to add
   */
  public void addTopScopeResolver(ResolvingFilter<? extends Symbol> topScopeResolvingFilter) {
    this.topScopeResolvingFilters.add(topScopeResolvingFilter);
  }

  public void addTopScopeResolvers(Collection<ResolvingFilter<? extends Symbol>> topScopeResolvingFilters) {
    this.topScopeResolvingFilters.addAll(topScopeResolvingFilters);
  }
  
  /**
   * @return the Set of top scope resolvers constructed through.
   */
  public Set<ResolvingFilter<? extends Symbol>> getTopScopeResolvingFilters() {
    return ImmutableSet.copyOf(topScopeResolvingFilters);
  }
}
