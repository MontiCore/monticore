/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.symboltable.serialization;

import java.util.Optional;

import de.monticore.symboltable.IScope;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class ScopeDeserializationResult<T extends IScope> {
  
  public ScopeDeserializationResult(
      T scope,
      Optional<SpanningSymbolReference> spanningSymbol) {
    this.spanningSymbol = spanningSymbol;
    this.scope = scope;
  }
  
  protected Optional<SpanningSymbolReference> spanningSymbol;
  
  protected T scope;
  
  /**
   * @return spanningSymbol
   */
  public Optional<SpanningSymbolReference> getSpanningSymbol() {
    return this.spanningSymbol;
  }
  
  /**
   * @return scope
   */
  public T getScope() {
    return this.scope;
  }
  
  public boolean hasSpanningSymbol() {
    return spanningSymbol.isPresent();
  }
  
  public String getSpanningSymbolName() {
    if(!spanningSymbol.isPresent()) {
      return "";
    }
    return spanningSymbol.get().getName();
  }
  
  public String getSpanningSymbolKind() {
    if(!spanningSymbol.isPresent()) {
      return "";
    }
    return spanningSymbol.get().getKind();
  }
  
}
