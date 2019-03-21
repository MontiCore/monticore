/*
 * Copyright (c) 2018 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.symboltable.serialization;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.Symbol;

public class SymbolTableSerializationHelper {
  
  /**
   * Returns all Symbols that are directly contained within a given scope and returns these as a
   * Collection
   * 
   * @param scope
   * @return
   */
  public static Collection<Symbol> getLocalSymbols(Scope scope) {
    Collection<Symbol> symbols = new ArrayList<>();
    for (Collection<Symbol> s : scope.getLocalSymbols().values()) {
      symbols.addAll(s);
    }
    return symbols;
  }
  
  /**
   * Given a scope, returns a collection of direct subscopes that export symbols and that contain at
   * least one symbol TODO AB: in any transitive subscope
   * 
   * @param src
   * @return
   */
  public static Collection<Scope> filterRelevantSubScopes(MutableScope src) {
    return src.getSubScopes()
        .stream()
        .filter(s -> s.exportsSymbols())
        .filter(s -> containsSymbolsInTransitiveSubScopes(s))
        .collect(Collectors.toList());
  }
  
  protected static boolean containsSymbolsInTransitiveSubScopes(Scope s) {
    // TODO@AB: Tiefensuche durch Scopes implementieren
    return s.getSymbolsSize() > 0;
  }
}
