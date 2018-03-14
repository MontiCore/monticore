/* (c) https://github.com/MontiCore/monticore */

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

  public static ResolvingFilter create(SymbolKind symbolKind) {
    return new CommonResolvingFilter<>(symbolKind);
  }

  public CommonResolvingFilter(SymbolKind targetKind) {
    this.targetKind = targetKind;
  }

  @Override
  public Optional<Symbol> filter(ResolvingInfo resolvingInfo, String name, Map<String, Collection<Symbol>> symbols) {
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
  public Collection<Symbol> filter(ResolvingInfo resolvingInfo, Collection<Symbol> symbols) {
    final Collection<Symbol> foundSymbols = new LinkedHashSet<>();

    for (Symbol symbol : symbols) {
      if (symbol.isKindOf(targetKind)) {
        foundSymbols.add(symbol);
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
