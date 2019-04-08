/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.resolving;

import static com.google.common.collect.ImmutableList.copyOf;

import java.util.ArrayList;
import java.util.Collection;

import de.monticore.symboltable.ISymbol;

public class ResolvedSeveralEntriesException extends RuntimeException {

  private static final long serialVersionUID = 931330102959575779L;

  private Collection<? extends ISymbol<?>> symbols = new ArrayList<>();
  
  @Deprecated //for conformance with old symbol table only
  public ResolvedSeveralEntriesException(String message) {
    super(message);
    this.symbols = new ArrayList<>();
  }

  public ResolvedSeveralEntriesException(String message, Collection<? extends ISymbol<?>> symbols) {
    super(message);
    this.symbols = new ArrayList<>(symbols);
  }

  public ResolvedSeveralEntriesException(Collection<? extends ISymbol<?>> symbols) {
    this("", symbols);
  }

  @SuppressWarnings("unchecked")
  public <S extends ISymbol<?>> Collection<S> getSymbols() {
    return (Collection<S>) copyOf(symbols);
  }

}
