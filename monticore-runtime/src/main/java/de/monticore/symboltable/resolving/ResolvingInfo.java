/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.resolving;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableList;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.SymbolKind;
import de.se_rwth.commons.logging.Log;

/**
 * Provides useful information of the current resolution process, e.g., the
 * scope that started the process.
 *
 * @author Pedram Mir Seyed Nazari
 *
 */
public class ResolvingInfo {

  private boolean areSymbolsFound = false;

  private Collection<ResolvingFilter<? extends Symbol>> resolvingFilters = new ArrayList<>();

  /**
   * A list of scopes that where involved in the resolving process until now.
   */
  private final List<MutableScope> involvedScopes = new ArrayList<>();
  private final List<SymbolKind> handledTargetKinds = new ArrayList<>();

  public ResolvingInfo(Collection<ResolvingFilter<? extends Symbol>> resolvingFilters) {
    this.resolvingFilters.addAll(Log.errorIfNull(resolvingFilters));
  }

  public boolean areSymbolsFound() {
    return areSymbolsFound;
  }

  public void updateSymbolsFound(boolean areSymbolsFound) {
    this.areSymbolsFound = this.areSymbolsFound || areSymbolsFound;
  }

  public void addInvolvedScope(final MutableScope scope) {
    involvedScopes.add(Log.errorIfNull(scope));
  }

  public List<MutableScope> getInvolvedScopes() {
    return ImmutableList.copyOf(involvedScopes);
  }

  public void addHandledTargetKind(final SymbolKind targetKind) {
    checkArgument(!isTargetKindHandled(targetKind));

    this.handledTargetKinds.add(targetKind);
  }

  public void removeTargetKind(SymbolKind targetKind) {
    checkArgument(isTargetKindHandled(targetKind));

    this.handledTargetKinds.remove(targetKind);
  }

  public boolean isTargetKindHandled(SymbolKind targetKind) {
    return handledTargetKinds.contains(targetKind);
  }

  public Collection<ResolvingFilter<? extends Symbol>> getResolvingFilters() {
    return ImmutableList.copyOf(resolvingFilters);
  }

}
