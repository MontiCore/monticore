// (c) https://github.com/MontiCore/monticore
package de.monticore.expressions.commonexpressions.interpreter;

import com.google.common.base.Preconditions;
import de.monticore.expressions.commonexpressions._ast.*;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsInheritanceHandler;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis.interpreter.SymbolAccessHandler;
import de.monticore.expressions.interpreter.util.InterpreterOperatorTraverser;
import de.monticore.interpreter.calculations.MICalculation;
import de.monticore.interpreter.calculations.MICalculationBoolean;
import de.monticore.interpreter.calculations.MICalculationDouble;
import de.monticore.interpreter.calculations.MICalculationInt;
import de.monticore.interpreter.calculations.MICalculationValue;
import de.monticore.interpreter.calculations.MICalculationVoid;
import de.monticore.interpreter.frames.MIFrameLayout;
import de.monticore.interpreter.setters.MISetter;
import de.monticore.interpreter.util.InterpreterData;
import de.monticore.interpreter.util.InterpreterVisitorOperatorCalculator;
import de.monticore.interpreter.util.TypeDispatcherHotfix;
import de.monticore.interpreter.values.MIValueFunction;
import de.monticore.interpreter.values.MISignalReturn;
import de.monticore.interpreter.values.MIValue;
import de.monticore.interpreter.values.MIValueVoid;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.symboltable.ISymbol;
import de.monticore.types.check.SymTypeExpression;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static de.monticore.interpreter.util.NativeStorageSelector.isStoredAsBoolean;
import static de.monticore.interpreter.util.NativeStorageSelector.isStoredAsDouble;
import static de.monticore.interpreter.util.NativeStorageSelector.isStoredAsInt;
import static de.monticore.interpreter.util.NativeStorageSelector.switchByFormat;
import static de.monticore.types3.SymTypeRelations.normalize;
import static de.monticore.types3.TypeCheck3.typeOf;

/**
 * Interpreter Visitor for CommonExpressions
 */
public class CommonExpressionsInterpreter
    extends CommonExpressionsInheritanceHandler {

  protected InterpreterData iData;

  protected InterpreterVisitorOperatorCalculator opCalculator =
      new InterpreterVisitorOperatorCalculator();
  protected InterpreterOperatorTraverser opTraverser =
      new InterpreterOperatorTraverser();
  protected SymbolAccessHandler symbolAccessHandler =
      new SymbolAccessHandler();

  public CommonExpressionsInterpreter(InterpreterData iData) {
    this.iData = Preconditions.checkNotNull(iData);
  }

  @Override
  public void traverse(ASTPlusExpression node) {
    opTraverser.traverseBinaryOperator(getTraverser(), iData,
        node, node.getLeft(), node.getRight(), opCalculator::handlePlus
    );
  }

  @Override
  public void traverse(ASTMinusExpression node) {
    opTraverser.traverseBinaryOperator(getTraverser(), iData,
        node, node.getLeft(), node.getRight(), opCalculator::handleMinus);
  }

  @Override
  public void traverse(ASTMultExpression node) {
    opTraverser.traverseBinaryOperator(getTraverser(), iData,
        node, node.getLeft(), node.getRight(), opCalculator::handleMultiply);
  }

  @Override
  public void traverse(ASTDivideExpression node) {
    opTraverser.traverseBinaryOperator(getTraverser(), iData,
        node, node.getLeft(), node.getRight(), opCalculator::handleDivide);
  }

  @Override
  public void traverse(ASTModuloExpression node) {
    opTraverser.traverseBinaryOperator(getTraverser(), iData,
        node, node.getLeft(), node.getRight(), opCalculator::handleModulo);
  }

  @Override
  public void traverse(ASTPlusPrefixExpression node) {
    opTraverser.traverseUnaryOperator(getTraverser(), iData,
        node, node.getExpression(), opCalculator::handlePlusPrefix);
  }

  @Override
  public void traverse(ASTMinusPrefixExpression node) {
    opTraverser.traverseUnaryOperator(getTraverser(), iData,
        node, node.getExpression(), opCalculator::handleMinusPrefix);
  }

  @Override
  public void traverse(ASTEqualsExpression node) {
    opTraverser.traverseBinaryOperator(getTraverser(), iData,
        node, node.getLeft(), node.getRight(), opCalculator::handleEquals);
  }

  @Override
  public void traverse(ASTNotEqualsExpression node) {
    opTraverser.traverseBinaryOperator(getTraverser(), iData,
        node, node.getLeft(), node.getRight(), opCalculator::handleNotEquals);
  }

  @Override
  public void traverse(ASTLessThanExpression node) {
    opTraverser.traverseBinaryOperator(getTraverser(), iData,
        node, node.getLeft(), node.getRight(), opCalculator::handleLessThan);
  }

  @Override
  public void traverse(ASTLessEqualExpression node) {
    opTraverser.traverseBinaryOperator(getTraverser(), iData,
        node, node.getLeft(), node.getRight(), opCalculator::handleLessEqual);
  }

  @Override
  public void traverse(ASTGreaterThanExpression node) {
    opTraverser.traverseBinaryOperator(getTraverser(), iData,
        node, node.getLeft(), node.getRight(),
        opCalculator::handleGreaterThan);
  }

  @Override
  public void traverse(ASTGreaterEqualExpression node) {
    opTraverser.traverseBinaryOperator(getTraverser(), iData,
        node, node.getLeft(), node.getRight(),
        opCalculator::handleGreaterEqual);
  }

  @Override
  public void traverse(ASTBooleanAndOpExpression node) {
    opTraverser.traverseBinaryOperator(getTraverser(), iData,
        node, node.getLeft(), node.getRight(), opCalculator::handleBooleanAnd);
  }

  @Override
  public void traverse(ASTBooleanOrOpExpression node) {
    opTraverser.traverseBinaryOperator(getTraverser(), iData,
        node, node.getLeft(), node.getRight(), opCalculator::handleBooleanOr);
  }

  @Override
  public void traverse(ASTBooleanNotExpression node) {
    opTraverser.traverseUnaryOperator(getTraverser(), iData,
        node, node.getExpression(), opCalculator::handleBooleanNot);
  }

  @Override
  public void traverse(ASTLogicalNotExpression node) {
    opTraverser.traverseUnaryOperator(getTraverser(), iData,
        node, node.getExpression(), opCalculator::handleLogicalNot);
  }

  @Override
  public void traverse(ASTConditionalExpression node) {
    SymTypeExpression exprType = normalize(typeOf(node));
    node.getCondition().accept(getTraverser());
    MICalculationBoolean conditionCalc =
        iData.popCalculation().asCalculationBoolean();
    node.getTrueExpression().accept(getTraverser());
    MICalculation trueCalc = iData.popCalculation();
    node.getFalseExpression().accept(getTraverser());
    MICalculation falseCalc = iData.popCalculation();
    MICalculation exprCalc;
    // there should be a simpler way to create this?
    if (isStoredAsBoolean(exprType)) {
      MICalculationBoolean trueCalcBoolean = trueCalc.asCalculationBoolean();
      MICalculationBoolean falseCalcBoolean = falseCalc.asCalculationBoolean();
      exprCalc = (MICalculationBoolean) frame ->
          conditionCalc.calculate(frame) ?
              trueCalcBoolean.calculate(frame) :
              falseCalcBoolean.calculate(frame);
    }
    else if (isStoredAsInt(exprType)) {
      MICalculationInt trueCalcInt = trueCalc.asCalculationInt();
      MICalculationInt falseCalcInt = falseCalc.asCalculationInt();
      exprCalc = (MICalculationInt) frame ->
          conditionCalc.calculate(frame) ?
              trueCalcInt.calculate(frame) :
              falseCalcInt.calculate(frame);
    }
    else if (isStoredAsDouble(exprType)) {
      MICalculationDouble trueCalcDouble = trueCalc.asCalculationDouble();
      MICalculationDouble falseCalcDouble = falseCalc.asCalculationDouble();
      exprCalc = (MICalculationDouble) frame ->
          conditionCalc.calculate(frame) ?
              trueCalcDouble.calculate(frame) :
              falseCalcDouble.calculate(frame);
    }
    else {
      MICalculationValue trueCalcValue = trueCalc.asCalculationValue();
      MICalculationValue falseCalcValue = falseCalc.asCalculationValue();
      exprCalc = (MICalculationValue) frame ->
          conditionCalc.calculate(frame) ?
              trueCalcValue.calculate(frame) :
              falseCalcValue.calculate(frame);
    }
    iData.putCalculation(exprCalc);
  }

  @Override
  public void traverse(ASTCallExpression node) {
    node.getExpression().accept(getTraverser());
    MICalculationValue functionCalc =
        iData.popCalculation().asCalculationValue();
    Preconditions.checkNotNull(functionCalc);

    List<MICalculation> argumentCalcs = new ArrayList<>();
    for (ASTExpression argumentExpr : node.getArguments().getExpressionList()) {
      argumentExpr.accept(getTraverser());
      argumentCalcs.add(iData.popCalculation());
    }

    SymTypeExpression returnType = normalize(typeOf(node));
    MICalculationValue callCalcValue = frame -> {
      final MIValueFunction functionValue =
          functionCalc.calculate(frame).asFunction();
      final MIValue[] argumentValues =
          new MIValue[argumentCalcs.size()];
      for (int i = 0; i < argumentCalcs.size(); i++) {
        argumentValues[i] = argumentCalcs.get(i)
            .asCalculationValue().calculate(frame);
      }
      try {
        return functionValue.asFunction().execute(argumentValues);
      }
      catch (MISignalReturn returnSignal) {
        // could be split
        return returnSignal.getValue().orElseGet(() -> MIValueVoid.INSTANCE);
      }
    };
    // make the calculation not break due to recursion
    MICalculationValue recursableCallCalcValue =
        segmentStack(callCalcValue);

    // improvable
    MICalculation callCalc = switchByFormat(returnType,
        (MICalculationBoolean) frame -> recursableCallCalcValue.calculate(frame).asBoolean(),
        (MICalculationInt) frame -> recursableCallCalcValue.calculate(frame).asInt(),
        (MICalculationDouble) frame -> recursableCallCalcValue.calculate(frame).asDouble(),
        (MICalculationValue) frame -> recursableCallCalcValue.calculate(frame),
        (MICalculationVoid) frame -> recursableCallCalcValue.calculate(frame)
    );
    iData.putCalculation(callCalc);
  }

  @Override
  public void traverse(ASTFieldAccessExpression node) {
    MICalculation resGetter;
    Optional<MISetter> resSetterOpt;
    SymTypeExpression exprType = normalize(typeOf(node));
    Preconditions.checkState(exprType.getSourceInfo().getSourceSymbol().isPresent());
    ISymbol exprSourceSym = exprType.getSourceInfo().getSourceSymbol().get();
    MIFrameLayout frameLayout = iData.getFrameLayoutStack().peek();

    // aka not static, but BasicSymbols count as non-relative,
    // even though they are technically not marked as static
    boolean isRelativToObject;
    if (TypeDispatcherHotfix.isFieldSymbol(exprSourceSym)) {
      isRelativToObject = !((FieldSymbol) exprSourceSym).isIsStatic();
    }
    else if (TypeDispatcherHotfix.isMethodSymbol(exprSourceSym)) {
      isRelativToObject = !((MethodSymbol) exprSourceSym).isIsStatic();
    }
    else {
      isRelativToObject = false;
    }

    if (isRelativToObject) {
      SymTypeExpression objType = normalize(typeOf(node.getExpression()));
      node.getExpression().accept(getTraverser());
      MICalculationValue objCalc =
          iData.popCalculation().asCalculationValue();

      SymbolAccessHandler.SymbolAccess symbolAccess = symbolAccessHandler
          .getSymbolAccess(exprSourceSym, frameLayout, objType, objCalc);
      resGetter = symbolAccess.getter();
      resSetterOpt = symbolAccess.setter();
    }
    else {
      SymbolAccessHandler.SymbolAccess symbolAccess =
          symbolAccessHandler.getSymbolAccess(exprSourceSym, frameLayout);
      resGetter = symbolAccess.getter();
      resSetterOpt = symbolAccess.setter();
    }

    iData.putCalculation(resGetter);
    if (resSetterOpt.isPresent()) {
      iData.putSetter(resSetterOpt.get());
    }
  }

  // Stack Segmentation ~ Dark Magic, only touch with care!

  // For context: REPL test without stack segmentation:
  // int s(int n) = n > 0 ? n + s(n-1) : 0;
  // breaks for s(1000) once, afterwards, was able to go up to s(3600).
  // that is too low for a functional language,
  // as such, the stack needs to be either segmented or reified.

  /**
   * only for {@link #segmentStack(MICalculationValue)}
   */
  static protected final ThreadLocal<Integer> callDepth =
      ThreadLocal.withInitial(() -> 1);

  /**
   * only for {@link #segmentStack(MICalculationValue)}
   */
  static protected final ThreadLocal<Boolean> isOnCustomStack =
      ThreadLocal.withInitial(() -> false);

  /**
   * only for {@link #segmentStack(MICalculationValue)}
   */
  static protected final ThreadGroup threadGroup
      = new ThreadGroup("Interpreter-Stack-Segmentation");

  /**
   * This implements stack segmentation without reification;
   * This avoids us having to reify the execution stack,
   * by being able to increase the native stack size dynamically.
   * This is done as a reified stack tends to be rather costly
   * (s., e.g., JRuby).
   * <p>
   * Stack Segmentation is done via creation of new thread objects.
   * Note that a Thread in Java does not have to match an OS thread
   * -> Java threads can be way cheaper.
   * Nonetheless, we try to avoid creating too many segments,
   * thus, each new thread has several MB of stack size.
   * <p>
   * Additionally, these are only used for deep recursion,
   * and otherwise are not created at all.
   * <p>
   * One segmentation point per recursion possibility is required.
   * We choose callExpressions,
   * as they are basically guaranteed
   * to be the cause for recursion in the first place,
   * which cannot be said for other (currently any) expressions.
   *
   * @param recursiveCalc The calculation that could be part of deep recursion.
   * @return The same calculation that segments the stack if necessary.
   */
  protected MICalculationValue segmentStack(
      MICalculationValue recursiveCalc
  ) {
    final int mb = 1024 * 1024;
    final int callDepthPerCustomThread = 1000;
    // rather conservative estimate, several times lower than tested:
    final int callDepthPerMainThread = 50;
    // current assumption: 100 calls per 1MB
    // this may need to be even more conservative for extreme cases
    final int stackSizePerCustomThread = mb / 100 * callDepthPerCustomThread;
    return currentFrame -> {
      final MIValue result;
      final int currentCallDepth = callDepth.get();
      if (
          (
              isOnCustomStack.get()
                  && currentCallDepth <= callDepthPerCustomThread
          ) || currentCallDepth <= callDepthPerMainThread
      ) {
        callDepth.set(callDepth.get() + 1);
        result = recursiveCalc.calculate(currentFrame);
        callDepth.set(callDepth.get() - 1);
      }
      else {
        final MIValue[] resultStorage = new MIValue[1];
        final Runnable recursiveRunnable = () -> {
          isOnCustomStack.set(true);
          resultStorage[0] = recursiveCalc.calculate(currentFrame);
          isOnCustomStack.set(false);
        };
        final Thread t = new Thread(
            threadGroup,
            recursiveRunnable,
            "interpreter",
            stackSizePerCustomThread
        );
        t.start();
        try {
          t.join();
        }
        catch (InterruptedException e) {
          threadGroup.interrupt();
          Thread.currentThread().interrupt();
        }
        result = resultStorage[0];
      }
      return result;
    };
  }

}
