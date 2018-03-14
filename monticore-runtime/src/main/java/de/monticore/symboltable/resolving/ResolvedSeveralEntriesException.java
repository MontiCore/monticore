/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.resolving;

import java.util.ArrayList;
import java.util.Collection;

import com.google.common.collect.ImmutableList;
import de.monticore.symboltable.Symbol;

/**
 * @author Pedram Mir Seyed Nazari
 *
 */
public class ResolvedSeveralEntriesException extends RuntimeException {

  private static final long serialVersionUID = 931330102959575779L;

  private Collection<? extends Symbol> symbols = new ArrayList<>();

  public ResolvedSeveralEntriesException (String message, Collection<? extends Symbol> symbols) {
    super(message);
    this.symbols = new ArrayList<>(symbols);
  }

  public ResolvedSeveralEntriesException(Collection<? extends Symbol> symbols) {
    this("", symbols);
  }

  public Collection<? extends Symbol> getSymbols() {
    return ImmutableList.copyOf(symbols);
  }

}
