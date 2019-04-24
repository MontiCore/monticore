/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.resolving;

import de.monticore.symboltable.ISymbol;

import java.util.ArrayList;
import java.util.Collection;

import static com.google.common.collect.ImmutableList.copyOf;

public class ResolvedSeveralEntriesForSymbolException extends RuntimeException {

  private static final long serialVersionUID = 931330102959575779L;

  private Collection<? extends ISymbol> symbols = new ArrayList<>();

  public ResolvedSeveralEntriesForSymbolException(String message, Collection<? extends ISymbol> symbols) {
    super(message);
    this.symbols = new ArrayList<>(symbols);
  }

  public ResolvedSeveralEntriesForSymbolException(Collection<? extends ISymbol> symbols) {
    this("", symbols);
  }

  @SuppressWarnings("unchecked")
  public <S extends ISymbol> Collection<S> getSymbols() {
    return (Collection<S>) copyOf(symbols);
  }

}
