package de.monticore.interpreter;

import de.monticore.interpreter.values.ErrorMIValue;
import de.monticore.interpreter.values.FunctionMIValue;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.HashMap;
import java.util.Map;

public class MIScope {
  
  protected Map<FunctionSymbol, FunctionMIValue> functionMap = new HashMap<>();
  protected Map<VariableSymbol, MIValue> variableMap = new HashMap<>();
  
  protected MIScope parent;
  
  public MIScope() {
    this.parent = null;
  }
  
  public MIScope(MIScope parent) {
    this.parent = parent;
  }
  
  public void declareFunction(FunctionSymbol symbol, FunctionMIValue value) {
    if (functionMap.containsKey(symbol)) {
      Log.error("0x57068 Function was already declared");
    }
    this.functionMap.put(symbol, value);
  }
  
  public MIValue loadFunction(FunctionSymbol symbol) {
    FunctionMIValue value = functionMap.get(symbol);
    if (value != null) {
      return value;
    }
    
    if (parent != null) {
      return parent.loadFunction(symbol);
    }
    
    String errorMsg = "0x57069 Failed to load Function by Symbol. Could not find Symbol in the current or any parent scope";
    Log.error(errorMsg);
    return new ErrorMIValue(errorMsg);
  }
  
  public void declareVariable(VariableSymbol symbol, MIValue value) {
    if (variableMap.containsKey(symbol)) {
      Log.error("0x57070 Variable was already declared");
    }
    this.variableMap.put(symbol, value);
  }
  
  public MIValue loadVariable(VariableSymbol symbol) {
    MIValue value = variableMap.get(symbol);
    if (value != null) {
      return value;
    }
    
    if (parent != null) {
      return parent.loadVariable(symbol);
    }
    
    String errorMsg = "0x57071 Failed to load Variable by Symbol. Could not find Symbol in the current or any parent scope";
    Log.error(errorMsg);
    return new ErrorMIValue(errorMsg);
  }
  
  public void storeVariable(VariableSymbol symbol, MIValue value) {
    if (variableMap.containsKey(symbol)) {
      variableMap.put(symbol, value);
    } else if (parent != null){
      parent.storeVariable(symbol, value);
    } else {
      Log.error("0x57072 Failed to store Value in Symbol. Could not find Symbol in the current or any parent scope");
    }
  }
  
}
