package de.monticore.interpreter;

import de.monticore.interpreter.values.ErrorValue;
import de.monticore.symboltable.ISymbol;
import de.se_rwth.commons.logging.Log;

import java.util.HashMap;
import java.util.Map;

public class MIScope {
  
  private Map<ISymbol, Value> contextMap;
  
  private MIScope parent;
  
  public MIScope() {
    this.contextMap = new HashMap<ISymbol, Value>();
    this.parent = null;
  }
  
  public void declareVariable(ISymbol symbol, Value value) {
    if (contextMap.containsKey(symbol)) {
      Log.error("Variable was already declared");
    }
    this.contextMap.put(symbol, value);
  }
  
  public Value load(ISymbol symbol) {
    Value value = contextMap.get(symbol);
    if (value != null) {
      return value;
    }
    
    if (parent != null) {
      return parent.load(symbol);
    }
    
    Log.error("Failed to load Value of Symbol. Could not find Symbol in the current or any parent scope");
    return new ErrorValue("Failed to load Value of Symbol. Could not find Symbol in the current or any parent scope");
  }
  
  public void store(ISymbol symbol, Value value) {
    if (contextMap.containsKey(symbol)) {
      contextMap.put(symbol, value);
    } else if (parent != null){
      parent.store(symbol, value);
    } else {
      Log.error("Failed to store Value in Symbol. Could not find Symbol in the current or any parent scope");
    }
  }
  
}
