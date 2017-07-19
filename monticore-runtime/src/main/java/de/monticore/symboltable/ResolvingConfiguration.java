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

import com.google.common.collect.ImmutableSet;
import de.monticore.symboltable.resolving.ResolvingFilter;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Maintains a mapping from names and kinds of symbols to {@link ResolvingFilter}s.
 *
 * @author Pedram Mir Seyed Nazari, Sebastian Oberhoff
 */
public final class ResolvingConfiguration {
  
  private final Map<String, Set<ResolvingFilter<? extends Symbol>>> specificFilters = new HashMap<>();

  private final Set<ResolvingFilter<? extends Symbol>> defaultFilters = new LinkedHashSet<>();

  /**
   * @deprecated use {@link #addFilter(String, ResolvingFilter)} instead
   */
  @Deprecated
  public void addResolver(String scopeName,
      ResolvingFilter<? extends Symbol> resolvingFilter) {
    addFilter(scopeName, resolvingFilter);
  }

  /**
   * Adds a resolving filter to the set of filters for the specified scope name.
   *
   * @param scopeName the name of the scope
   * @param resolvingFilter the filter to add for the specified scope
   */
  public void addFilter(String scopeName,
      ResolvingFilter<? extends Symbol> resolvingFilter) {
    if (!specificFilters.containsKey(scopeName)) {
      specificFilters.put(scopeName, new LinkedHashSet<>());
    }
    specificFilters.get(scopeName).add(resolvingFilter);
  }
  
  /**
   * Retrieves the resolving filters associated with the scope having the specified name.
   * 
   * @param scopeName the name of the scope who's filters should be retrieved
   * @return the set of filters added for the scope
   */
  public Set<ResolvingFilter<? extends Symbol>> getFilters(String scopeName) {
    Set<ResolvingFilter<? extends Symbol>> resolvingFilters = specificFilters.get(scopeName);
    return resolvingFilters != null ? resolvingFilters : Collections.emptySet();
  }

  /**
   * @param defaultFilter the default filter to add
   */
  public void addDefaultFilter(ResolvingFilter<? extends Symbol> defaultFilter) {
    this.defaultFilters.add(defaultFilter);
  }

  /**
   * @param defaultFilters the default filters to add
   */
  public void addDefaultFilters(Collection<ResolvingFilter<? extends Symbol>> defaultFilters) {
    this.defaultFilters.addAll(defaultFilters);
  }

  /**
   * @return the set of default filters
   */
  public Set<ResolvingFilter<? extends Symbol>> getDefaultFilters() {
    return ImmutableSet.copyOf(defaultFilters);
  }

  /**
   * @deprecated use {@link #addDefaultFilter(ResolvingFilter)} instead
   */
  @Deprecated
  public void addTopScopeResolver(ResolvingFilter<? extends Symbol> topScopeResolvingFilter) {
    this.addDefaultFilter(topScopeResolvingFilter);
  }

  /**
   * @deprecated use {@link #addDefaultFilters(Collection)} instead
   */
  @Deprecated
  public void addTopScopeResolvers(Collection<ResolvingFilter<? extends Symbol>> topScopeResolvingFilters) {
    this.addDefaultFilters(topScopeResolvingFilters);
  }
  
  /**
   * @deprecated use {@link #getDefaultFilters()} instead
   */
  @Deprecated
  public Set<ResolvingFilter<? extends Symbol>> getTopScopeResolvingFilters() {
    return this.getDefaultFilters();
  }
}
