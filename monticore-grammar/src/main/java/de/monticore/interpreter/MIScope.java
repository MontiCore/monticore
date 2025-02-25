package de.monticore.interpreter;

import de.monticore.interpreter.values.ErrorMIValue;
import de.monticore.symboltable.ISymbol;
import de.se_rwth.commons.logging.Log;

import java.util.HashMap;
import java.util.Map;

public class MIScope {
  
  protected Map<ISymbol, MIValue> contextMap = new HashMap<>();
  
  protected MIScope parent;
  
  public MIScope() {
    this.parent = null;
  }
  
  public MIScope(MIScope parent) {
    this.parent = parent;
  }
  
  public void declareVariable(ISymbol symbol, MIValue value) {
    if (contextMap.containsKey(symbol)) {
      Log.error("Variable was already declared");
    }
    this.contextMap.put(symbol, value);
  }
  
  public MIValue load(ISymbol symbol) {
    MIValue value = contextMap.get(symbol);
    if (value != null) {
      return value;
    }
    
    if (parent != null) {
      return parent.load(symbol);
    }
    
    Log.error("Failed to load Value of Symbol. Could not find Symbol in the current or any parent scope");
    return new ErrorMIValue("Failed to load Value of Symbol. Could not find Symbol in the current or any parent scope");
  }
  
  public void store(ISymbol symbol, MIValue value) {
    if (contextMap.containsKey(symbol)) {
      contextMap.put(symbol, value);
    } else if (parent != null){
      parent.store(symbol, value);
    } else {
      Log.error("Failed to store Value in Symbol. Could not find Symbol in the current or any parent scope");
    }
  }
  
}
