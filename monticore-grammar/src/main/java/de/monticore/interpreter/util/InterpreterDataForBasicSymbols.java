// (c) https://github.com/MontiCore/monticore
package de.monticore.interpreter.util;

import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols.interpreter.frames.MIFrameLayoutForBasicSymbols;
import de.monticore.values.MCValueFunction;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.function.Supplier;

public class InterpreterDataForBasicSymbols extends InterpreterData {

  protected Stack<MIFrameLayoutForBasicSymbols> frameLayoutStack = new Stack<>();

  // functions
  // they need to be referencable, so that we can break up recursive calls
  // unlike other elements, this is the same value in every frame,
  // simply to provide fast non-static access.
  // layout could be an array or at least an ArrayList,
  // but for now it is a map for simplicity,
  Map<FunctionSymbol, MCValueFunction> functions = new HashMap<>();

  public boolean isPresentCalculation() {
    return calculation != null;
  }

  /**
   * The current {@link MIFrameLayoutForBasicSymbols}s.
   *
   * @return the current frame layout.
   */
  public Stack<MIFrameLayoutForBasicSymbols> getFrameLayoutStack() {
    return frameLayoutStack;
  }

  /**
   * Never(!) edit this map yourself.
   *
   * @return the map of defined functions.
   */
  public Map<FunctionSymbol, MCValueFunction> getFunctions() {
    // making it unmodifiable here would create a lot of new objects,
    // thus, it is avoided and simply assumed that no-one modifies it.
    return functions;
  }

  public void defineFunction(FunctionSymbol funcSym, MCValueFunction value) {
    Preconditions.checkNotNull(funcSym);
    Preconditions.checkNotNull(value);
    Preconditions.checkState(!functions.containsKey(funcSym),
        "FunctionSymbol " + funcSym.getFullName()
            + " has already been registered");
    functions.put(funcSym, value);
  }

  public Supplier<MCValueFunction> getFunctionSupplier(FunctionSymbol funcSym) {
    return () -> {
      MCValueFunction function = functions.get(funcSym);
      if (function != null) {
        return function;
      }
      else {
        // not expected to happen in a properly set up interpreter
        throw new RuntimeException(
            "Function symbol " + funcSym.getFullName() + "has not been defined."
        );
      }
    };
  }

  public void reset() {
    super.reset();
    frameLayoutStack.clear();
  }

}
