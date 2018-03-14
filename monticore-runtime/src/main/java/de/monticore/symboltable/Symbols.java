/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.ImmutableList;

/**
 * @author Pedram Mir Seyed Nazari
 */
public class Symbols {

  private Symbols() {
  }

  public static <T extends Symbol> List<T> sortSymbolsByPosition(final Collection<T> unorderedSymbols) {
    final List<T> sortedSymbols = new ArrayList<>(unorderedSymbols);

    Collections.sort(sortedSymbols,
        (symbol1, symbol2) -> symbol1.getSourcePosition().compareTo(symbol2.getSourcePosition()));

    return ImmutableList.copyOf(sortedSymbols);
  }

}
