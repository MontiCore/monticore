/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.basicsymbols._symboltable;

import de.monticore.symboltable.resolving.ResolvedSeveralEntriesForSymbolException;
import de.monticore.symboltable.stereotypes.IStereotypeReference;
import de.monticore.symboltable.stereotypes.IStereotypeSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

/**
 * Implementation of {@link IStereotypeReference} that is backed by the name of
 * a stereotype and a scope from which the name can be resolved to a stereotype
 * symbol.
 */
public class BasicSymbolsStereotypeReference implements IStereotypeReference {
  protected final String stereotypeName;
  protected final IBasicSymbolsScope enclosingScope;

  protected Optional<? extends IStereotypeSymbol> resolvedSym = Optional.empty();

  public BasicSymbolsStereotypeReference(String stereotypeName, IBasicSymbolsScope enclosingScope) {
    this.stereotypeName = stereotypeName;
    this.enclosingScope = enclosingScope;
  }

  @Override
  public Optional<? extends IStereotypeSymbol> getResolved() {
    try {
      if (resolvedSym.isEmpty()) {
        resolvedSym = enclosingScope.resolveMCStereotype(stereotypeName);
      }

      if (resolvedSym.isEmpty()) {
        Log.error("0x82406 Could not load full information of stereotype " + stereotypeName);
      }

    } catch (ResolvedSeveralEntriesForSymbolException e) {
      Log.error(e.getMessage());
    }

    return resolvedSym;
  }
}
