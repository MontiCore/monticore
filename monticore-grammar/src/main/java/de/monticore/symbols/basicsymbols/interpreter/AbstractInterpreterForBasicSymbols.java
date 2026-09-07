// (c) https://github.com/MontiCore/monticore
package de.monticore.symbols.basicsymbols.interpreter;

import com.google.common.base.Preconditions;
import de.monticore.ast.ASTNode;
import de.monticore.interpreter.AbstractInterpreter;
import de.monticore.interpreter.calculations.MICalculation;
import de.monticore.interpreter.frames.MIFrame;
import de.monticore.interpreter.util.InterpreterDataForBasicSymbols;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.basicsymbols.interpreter.frames.MIFrameForBasicSymbols;
import de.monticore.symbols.basicsymbols.interpreter.frames.MIFrameLayoutForBasicSymbols;
import de.monticore.values.MCValue;
import de.monticore.values.MCValueFunction;
import de.monticore.visitor.ITraverser;

public class AbstractInterpreterForBasicSymbols extends AbstractInterpreter {

  protected ITraverser traverser;
  protected InterpreterDataForBasicSymbols iData;
  protected MIFrameForBasicSymbols topMostFrame =
      new MIFrameForBasicSymbols(new MIFrameLayoutForBasicSymbols());

  public AbstractInterpreterForBasicSymbols(
      ITraverser traverser,
      InterpreterDataForBasicSymbols iData
  ) {
    this.traverser = Preconditions.checkNotNull(traverser);
    this.iData = Preconditions.checkNotNull(iData);
  }

  @Override
  public MIFrameForBasicSymbols getTopMostFrame() {
    return topMostFrame;
  }

  // frame setup

  /**
   * {@link #addVariable(VariableSymbol)} and then
   * {@link #setVariable(VariableSymbol, MCValue)}.
   */
  public void addVariable(VariableSymbol varSym, MCValue value) {
    addVariable(varSym);
    setVariable(varSym, value);
  }

  /**
   * Declares a new(!) variable into the topmost {@link MIFrame}.
   * <p>
   * Note: Variables in subscopes that are available via qualification
   * belong in the topmost frame (e.g., static fields of classes).
   *
   * @param varSym the variable that has not been declared yet
   */
  public void addVariable(VariableSymbol varSym) {
    Preconditions.checkNotNull(varSym);
    MIFrameLayoutForBasicSymbols newLayout = topMostFrame.getFrameLayout().clone();
    newLayout.declareVariable(varSym);
    topMostFrame = topMostFrame.createExpandedCopy(newLayout);
  }

  /**
   * Sets a value for a variable in the topmost {@link MIFrame}.
   * <p>
   * WARNING: This does not check for type correctness,
   * it is up to the caller to ensure that the value fits the variable.
   *
   * @param varSym the variable that has been declared
   * @param value  the value of the variable
   */
  public void setVariable(VariableSymbol varSym, MCValue value) {
    Preconditions.checkNotNull(varSym);
    Preconditions.checkNotNull(value);
    // This may seem very roundabout;
    // it is.
    // However, this should never happen within the interpreter visitors,
    // making this close to the only place where it is written like this.
    topMostFrame.getFrameLayout()
        .getVariableSetter(varSym)
        .set(topMostFrame, value);
  }

  /**
   * Declares a new(!) function into the topmost {@link MIFrame}
   * <p>
   * WARNING: This does not check for type correctness,
   * it is up to the caller to ensure that the value fits the symbol.
   *
   * @param functionSym the function symbol that has not been declared yet
   * @param impl        the implementation to be called
   */
  public void addFunction(
      FunctionSymbol functionSym,
      MCValueFunction impl
  ) {
    Preconditions.checkNotNull(functionSym);
    Preconditions.checkNotNull(impl);
    iData.defineFunction(functionSym, impl);
  }

  /**
   * Gets the value of a variable in the top-most frame.
   *
   * @param varSym the variable to load
   * @return the value of the variable
   */
  public MCValue getVariable(VariableSymbol varSym) {
    return getTopMostFrame().getFrameLayout()
        .getVariableGetter(varSym)
        .asCalculationValue()
        .calculate(getTopMostFrame());
  }

  // evaluation

  protected MICalculation getCalculationWithoutCaching(ASTNode node) {
    Preconditions.checkState(
        !iData.isPresentCalculation() &&
            iData.getFrameLayoutStack().isEmpty(),
        "Interpreter stacks are not empty before traversal."
            + " The interpreter is misconfigured."
            + " " + node.get_SourcePositionStart().toString()
    );
    iData.reset();

    iData.getFrameLayoutStack().push(topMostFrame.getFrameLayout());
    node.accept(traverser);
    if (!iData.isPresentCalculation() ||
        iData.getFrameLayoutStack().size() != 1
    ) {
      throw new IllegalStateException(
          "0xF1111 internal error: "
              + "Interpreter stack is not clean after traversal."
              + " The interpreter is misconfigured."
              + " " + node.get_SourcePositionStart().toString()
      );
    }
    iData.getFrameLayoutStack().pop();
    MICalculation calculation = iData.popCalculation();
    // make space on the toplevel for the variables within the calculation
    topMostFrame = topMostFrame
        .createExpandedCopy(topMostFrame.getFrameLayout());

    return calculation;
  }

}
