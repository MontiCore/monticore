/* (c) https://github.com/MontiCore/monticore */

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

}
