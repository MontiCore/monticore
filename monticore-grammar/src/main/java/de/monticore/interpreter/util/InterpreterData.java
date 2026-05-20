// (c) https://github.com/MontiCore/monticore
package de.monticore.interpreter.util;

import com.google.common.base.Preconditions;
import de.monticore.interpreter.calculations.MICalculation;
import de.monticore.symbols.basicsymbols.interpreter.frames.MIFrameLayoutForBasicSymbols;
import de.monticore.interpreter.setters.MISetter;
import de.monticore.values.MCValueFunction;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.function.Supplier;

/**
 * Contains the shared data between interpreter visitors.
 * Depending on the Interpreter, this may need to be extended.
 */
public class InterpreterData {

  // null if not present
  protected MICalculation calculation;

  // null if not present
  protected MISetter setter;

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
   * If a node is traversed,
   * this contains the {@link MICalculation} to interpret it.
   * <p>
   * SideEffect: This will clear the calculation,
   * so only call this once.
   *
   * @return The current calculation.
   */
  public MICalculation popCalculation() {
    Preconditions.checkNotNull(calculation);
    MICalculation calculation = this.calculation;
    this.calculation = null;
    return calculation;
  }

  public void putCalculation(MICalculation calculation) {
    Preconditions.checkState(this.calculation == null);
    this.calculation = Preconditions.checkNotNull(calculation);
  }

  /**
   * If a node is an LValue,
   * this contains the {@link MISetter} to set the new value.
   * <p>
   * SideEffect: This will clear the setter,
   * so only call this once.
   *
   * @return The current setter.
   */
  public MISetter popSetter() {
    Preconditions.checkState(setter != null,
        "Expected a setter at this point, but non has been created."
            + " This is an internal tooling error;"
            + " likely, either CoCos are missing,"
            + " or the interpreter's visitors are not configured properly"
    );
    MISetter setter = this.setter;
    this.setter = null;
    return setter;
  }

  /**
   * Sets the current {@link MISetter},
   * used while traversing LValues.
   *
   * @param setter the current
   */
  public void putSetter(MISetter setter) {
    Preconditions.checkNotNull(setter);
    this.setter = setter;
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
    calculation = null;
    setter = null;
    frameLayoutStack.clear();
  }

}
