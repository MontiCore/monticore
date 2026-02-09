package de.monticore.interpreter;

import de.monticore.interpreter.values.ErrorMIValue;
import de.monticore.interpreter.values.FunctionMIValue;
import de.monticore.symboltable.ISymbol;
import de.se_rwth.commons.logging.Log;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MIScope {

  protected Map<ISymbol, MIValue> functionMap = new HashMap<>();
  protected Map<ISymbol, Optional<MIValue>> variableMap = new HashMap<>();

  protected Optional<MIScope> parent;

  public MIScope() {
    this.parent = Optional.empty();
  }

  public MIScope(MIScope parent) {
    this.parent = Optional.of(parent);
  }

  public MIScope clone() {
    MIScope clone = new MIScope();
    clone.parent = parent;
    clone.variableMap = new HashMap<>(variableMap);
    clone.functionMap = new HashMap<>(functionMap);
    return clone;
  }

  public void declareFunction(ISymbol symbol, FunctionMIValue value) {
    if (functionMap.containsKey(symbol)) {
      Log.error("0x57068 Function was already declared");
    }
    this.functionMap.put(symbol, value);
  }

  public MIValue loadFunction(ISymbol symbol) {
    if (functionMap.containsKey(symbol)) {
      return functionMap.get(symbol);
    }

    if (parent.isPresent()) {
      return parent.get().loadFunction(symbol);
    }

    String errorMsg = "0x57069 Failed to load Function by Symbol. Could not find Symbol in the current or any parent scope";
    Log.error(errorMsg);
    return new ErrorMIValue(errorMsg);
  }

  public void declareVariable(ISymbol symbol, Optional<MIValue> value) {
    if (variableMap.containsKey(symbol)) {
      Log.error("0x57070 Variable was already declared");
    }
    this.variableMap.put(symbol, value);
  }

  public MIValue loadVariable(ISymbol symbol) {
    Optional<MIValue> value = variableMap.get(symbol);
    if (value != null) {
      if (value.isPresent()) {
        return value.get();
      }
      else {
        String errorMsg = "0x57087 Failed to load Variable by Symbol. Variable was declared but never initialized.";
        Log.error(errorMsg);
        return new ErrorMIValue(errorMsg);
      }
    }

    if (parent.isPresent()) {
      return parent.get().loadVariable(symbol);
    }

    String errorMsg = "0x57071 Failed to load Variable by Symbol. Could not find Symbol in the current or any parent scope";
    Log.error(errorMsg);
    return new ErrorMIValue(errorMsg);
  }

  public void storeVariable(ISymbol symbol, MIValue value) {
    if (variableMap.containsKey(symbol)) {
      variableMap.put(symbol, Optional.of(value));
    }
    else if (parent.isPresent()) {
      parent.get().storeVariable(symbol, value);
    }
    else {
      Log.error("0x57072 Failed to store Value in Symbol. Could not find Symbol in the current or any parent scope");
    }
  }
}
