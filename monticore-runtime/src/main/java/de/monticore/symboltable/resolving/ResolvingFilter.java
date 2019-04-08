/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.resolving;

import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.SymbolKind;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

// TODO PN remove formal type argument, since not needed anymore
@Deprecated
public interface ResolvingFilter<S extends Symbol> {

  SymbolKind getTargetKind();

  Optional<Symbol> filter(ResolvingInfo resolvingInfo, String name, Map<String, Collection<Symbol>> symbols);

  Collection<Symbol> filter(ResolvingInfo resolvingInfo, Collection<Symbol> symbols);

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
      throw new ResolvedSeveralEntriesException("0xA4095 Found " + resolved.size() + " symbols: " + resolved);
    }

    return Optional.empty();
  }
}
