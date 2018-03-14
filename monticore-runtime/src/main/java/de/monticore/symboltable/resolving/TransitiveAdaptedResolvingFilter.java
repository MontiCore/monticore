/* (c) https://github.com/MontiCore/monticore */

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
  public Optional<Symbol> filter(ResolvingInfo resolvingInfo, String symbolName, Map<String, Collection<Symbol>> symbols) {
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

      // NOTE: Remove this whole if-statement, if adaptors should be created eager.
      if (optSymbol.isPresent()) {
        resolvedSymbols.add(translate(optSymbol.get()));
      }
    }

    // Removes the handled target kind. This is important, since other resolving
    // filters may handle this kind
    resolvingInfo.removeTargetKind(getTargetKind());

    return ResolvingFilter.getResolvedOrThrowException(resolvedSymbols);
  }

  @Override
  public Collection<Symbol> filter(ResolvingInfo resolvingInfo, Collection<Symbol> symbols) {
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
