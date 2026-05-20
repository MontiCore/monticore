// (c) https://github.com/MontiCore/monticore
package de.monticore.expressions.interpreter;

import com.google.common.base.Preconditions;
import de.monticore.ast.ASTNode;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.interpreter.calculations.MICalculation;
import de.monticore.interpreter.calculations.MICalculationValue;
import de.monticore.interpreter.calculations.MICalculationVoid;
import de.monticore.interpreter.frames.MIFrame;
import de.monticore.interpreter.frames.MIFrameForBasicSymbols;
import de.monticore.interpreter.frames.MIFrameLayoutForBasicSymbols;
import de.monticore.interpreter.util.InterpreterData;
import de.monticore.interpreter.util.TraverserAndIData;
import de.monticore.interpreter.values.MISignalFlowControl;
import de.monticore.interpreter.values.MIValue;
import de.monticore.interpreter.values.MIValueError;
import de.monticore.interpreter.values.MIValueFunction;
import de.monticore.interpreter.values.MIValueVoid;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.visitor.ITraverser;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * API to use the interpreter.
 * This Version is only for languages
 * that solely have expressions to interpret.
 */
public class ExpressionsInterpreter {

  protected TraverserAndIData<InterpreterData> interpreterTraverser;
  protected MIFrameForBasicSymbols topMostFrame =
      new MIFrameForBasicSymbols(new MIFrameLayoutForBasicSymbols());
  protected Map<ASTNode, MICalculation> calculationCache = new WeakHashMap<>();

  public ExpressionsInterpreter(
      TraverserAndIData<InterpreterData> interpreterTraverser
  ) {
    Preconditions.checkNotNull(interpreterTraverser);
    Preconditions.checkNotNull(interpreterTraverser.traverser());
    Preconditions.checkNotNull(interpreterTraverser.data());
    this.interpreterTraverser = interpreterTraverser;
  }

  public ExpressionsInterpreter(
      ITraverser traverser,
      InterpreterData iData
  ) {
    this(new TraverserAndIData<>(traverser, iData));
  }

  public MIFrameForBasicSymbols getTopMostFrame() {
    return topMostFrame;
  }

  // interpretation

  public MIValue interpret(ASTExpression expression) {
    return interpretNode(expression);
  }

  public MIValue interpret(ASTLiteral literal) {
    return interpretNode(literal);
  }

  protected MIValue interpretNode(ASTNode node) {
    MICalculation calculation = getCalculation(node);
    MIValue value = calculateWithCatch(calculation, topMostFrame);
    return value;
  }

  /**
   * Prepares the interpretation
   * by creating the corresponding calculation object.
   * You don't need to call this method!
   * <p>
   * This method is only used to benchmark
   * the timing of the preparation phase.
   *
   * @param node The node to prepare for interpretation
   */
  public void benchmark_internal_prepareInterpretation(ASTExpression node) {
    getCalculation(node);
  }

  // frame setup

  /**
   * {@link #addVariable(VariableSymbol)} and then
   * {@link #setVariable(VariableSymbol, MIValue)}.
   */
  public void addVariable(VariableSymbol varSym, MIValue value) {
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
  public void setVariable(VariableSymbol varSym, MIValue value) {
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
      MIValueFunction impl
  ) {
    Preconditions.checkNotNull(functionSym);
    Preconditions.checkNotNull(impl);
    interpreterTraverser.data().defineFunction(functionSym, impl);
  }

  /**
   * Gets the value of a variable in the top-most frame.
   *
   * @param varSym the variable to load
   * @return the value of the variable
   */
  public MIValue getVariable(VariableSymbol varSym) {
    return getTopMostFrame().getFrameLayout()
        .getVariableGetter(varSym)
        .asCalculationValue()
        .calculate(getTopMostFrame());
  }

  // evaluation

  /**
   * Converts an executable {@link ASTNode} into a {@link MICalculation}
   * that can be executed with a given frame.
   * <p>
   * IMPORTANT: Any used variable must be declared
   * in their respective {@link MIFrameLayoutForBasicSymbols}
   * _before_ calling this method.
   *
   * @param node the node describing the behavior
   * @return an {@link MICalculation} representing the behavior
   */
  protected MICalculation getCalculation(
      ASTNode node
  ) {
    InterpreterData iData = interpreterTraverser.data();
    Preconditions.checkNotNull(node);
    Preconditions.checkState(
        !iData.isPresentCalculation() &&
            iData.getFrameLayoutStack().isEmpty(),
        "Interpreter stacks are not empty before traversal."
            + " The interpreter is misconfigured."
            + " " + node.get_SourcePositionStart().toString()
    );
    iData.reset();

    // create calculation if not already in cache
    if (!calculationCache.containsKey(node)) {
      iData.getFrameLayoutStack().push(topMostFrame.getFrameLayout());
      node.accept(interpreterTraverser.traverser());
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
      calculationCache.put(node, iData.popCalculation());
      // make space on the toplevel for the variables within the calculation
      topMostFrame = topMostFrame
          .createExpandedCopy(topMostFrame.getFrameLayout());
    }

    MICalculation calculation = calculationCache.get(node);
    return calculation;
  }

  /**
   * Takes any {@link MICalculation} and executes it with the given frame.
   * <p>
   * Mostly to handle the {@link MICalculationVoid} case.
   *
   * @param calculation    to be executed. May or may not return a value.
   * @param enclosingFrame the enclosing frame.
   * @return The value of the calculation or {@link MIValueVoid}.
   */
  protected MIValue calculate(
      MICalculation calculation,
      MIFrame enclosingFrame
  ) {
    MICalculationValue valueCalc;
    if (calculation.isCalculationVoid()) {
      valueCalc = frame -> {
        calculation.asCalculationVoid().calculate(frame);
        return MIValueVoid.INSTANCE;
      };
    }
    else {
      valueCalc = calculation.asCalculationValue();
    }
    return valueCalc.calculate(enclosingFrame);
  }

  protected MIValue calculateWithCatch(
      MICalculation calculation,
      MIFrame enclosingFrame
  ) {
    MIValue value;
    try {
      value = calculate(calculation, enclosingFrame);
    }
    catch (MISignalFlowControl signal) {
      value = signal;
    }
    // catch everything for now,
    // there may be exceptions to this rule
    catch (Throwable e) {
      value = new MIValueError(e);
    }
    return value;
  }

}
