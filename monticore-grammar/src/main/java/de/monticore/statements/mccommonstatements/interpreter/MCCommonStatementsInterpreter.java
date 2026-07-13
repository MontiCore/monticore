// (c) https://github.com/MontiCore/monticore
package de.monticore.statements.mccommonstatements.interpreter;

import com.google.common.base.Preconditions;
import de.monticore.ast.ASTNode;
import de.monticore.interpreter.calculations.MICalculationBoolean;
import de.monticore.interpreter.calculations.MICalculationValue;
import de.monticore.interpreter.calculations.MICalculationVoid;
import de.monticore.interpreter.setters.MISetter;
import de.monticore.interpreter.signals.MCSignalBreak;
import de.monticore.interpreter.signals.MCSignalContinue;
import de.monticore.interpreter.util.InterpreterDataForBasicSymbols;
import de.monticore.statements.mccommonstatements._ast.ASTBreakStatement;
import de.monticore.statements.mccommonstatements._ast.ASTCommonForControl;
import de.monticore.statements.mccommonstatements._ast.ASTDoWhileStatement;
import de.monticore.statements.mccommonstatements._ast.ASTEmptyStatement;
import de.monticore.statements.mccommonstatements._ast.ASTEnhancedForControl;
import de.monticore.statements.mccommonstatements._ast.ASTExpressionStatement;
import de.monticore.statements.mccommonstatements._ast.ASTForInitByExpressions;
import de.monticore.statements.mccommonstatements._ast.ASTForStatement;
import de.monticore.statements.mccommonstatements._ast.ASTIfStatement;
import de.monticore.statements.mccommonstatements._ast.ASTMCJavaBlock;
import de.monticore.statements.mccommonstatements._ast.ASTWhileStatement;
import de.monticore.statements.mccommonstatements._visitor.MCCommonStatementsInheritanceHandler;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.values.MCValue;
import de.monticore.values.MCValueFactory;
import de.monticore.values.MCValueObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BooleanSupplier;

import static de.monticore.types3.SymTypeRelations.normalize;
import static de.monticore.types3.TypeCheck3.symTypeFromAST;
import static de.monticore.types3.TypeCheck3.typeOf;

/**
 * Interpreter Visitor for MCCommonStatements
 */
public class MCCommonStatementsInterpreter
    extends MCCommonStatementsInheritanceHandler {

  protected InterpreterDataForBasicSymbols iData;

  public MCCommonStatementsInterpreter(InterpreterDataForBasicSymbols iData) {
    this.iData = Preconditions.checkNotNull(iData);
  }

  @Override
  public void traverse(ASTMCJavaBlock node) {
    // note that this does not open a scope by itself.

    // avoiding a loop by chaining the statements,
    // This is supposedly faster (Edit: it is in my tests)
    // (cf. "Efficient hosted interpreters on the JVM")
    MICalculationVoid calcChain = MICalculationVoid.NOOP_CALC;
    for (int i = 0; i < node.sizeMCBlockStatements(); i++) {
      node.getMCBlockStatement(i).accept(getTraverser());
      MICalculationVoid stmtCalc =
          iData.popCalculation().asCalculationVoid();
      calcChain = calcChain.getChainedBefore(stmtCalc);
    }
    iData.putCalculation(calcChain);
  }

  @Override
  public void traverse(ASTIfStatement node) {
    node.getCondition().accept(getTraverser());
    MICalculationBoolean conditionCalc =
        iData.popCalculation().asCalculationBoolean();
    node.getThenStatement().accept(getTraverser());
    MICalculationVoid thenCalc =
        iData.popCalculation().asCalculationVoid();
    MICalculationVoid elseCalc;
    if (node.isPresentElseStatement()) {
      node.getElseStatement().accept(getTraverser());
      elseCalc = iData.popCalculation().asCalculationVoid();
    }
    else {
      elseCalc = MICalculationVoid.NOOP_CALC;
    }
    MICalculationVoid ifCalc = frame -> {
      if (conditionCalc.calculate(frame)) {
        thenCalc.calculate(frame);
      }
      else {
        elseCalc.calculate(frame);
      }
    };
    iData.putCalculation(ifCalc);
  }

  // relies on ASTForControl calculating to a BooleanSupplier
  // which handles initialization/updating of the for-control;
  // It returns if the body should be calculated.
  @Override
  public void traverse(ASTForStatement node) {
    node.getForControl().accept(getTraverser());
    MICalculationValue forControlCalc =
        iData.popCalculation().asCalculationValue();
    node.getMCStatement().accept(getTraverser());
    MICalculationVoid bodyCalc =
        iData.popCalculation().asCalculationVoid();
    MICalculationVoid forCalc = frame -> {
      BooleanSupplier updateAndCheckIter =
          forControlCalc.calculate(frame).asObject().unsafeCast();
      while (updateAndCheckIter.getAsBoolean()) {
        bodyCalc.calculate(frame);
      }
    };
    iData.putCalculation(forCalc);
  }

  @Override
  public void traverse(ASTCommonForControl node) {
    MICalculationVoid forInitCalc;
    if (node.isPresentForInit()) {
      node.getForInit().accept(getTraverser());
      forInitCalc = iData.popCalculation().asCalculationVoid();
    }
    else {
      forInitCalc = MICalculationVoid.NOOP_CALC;
    }

    node.getCondition().accept(getTraverser());
    MICalculationBoolean conditionCalc =
        iData.popCalculation().asCalculationBoolean();

    MICalculationVoid updateCalc = chainBehavior(node.getExpressionList());

    MICalculationValue forControlCalc = frame -> {
      final boolean[] isFirstIter = { true };
      BooleanSupplier updateAndCheckIter = () -> {
        if (isFirstIter[0]) {
          forInitCalc.calculate(frame);
          isFirstIter[0] = false;
        }
        else {
          updateCalc.calculate(frame);
        }
        return conditionCalc.calculate(frame);
      };
      return new MCValueObject(updateAndCheckIter);
    };
    iData.putCalculation(forControlCalc);
  }

  @Override
  public void traverse(ASTForInitByExpressions node) {
    iData.putCalculation(chainBehavior(node.getExpressionList()));
  }

  @Override
  public void traverse(ASTEnhancedForControl node) {
    SymTypeExpression exprType = normalize(typeOf(node.getExpression()));
    FieldSymbol varSym = node.getFormalParameter().getDeclarator().getSymbol();
    iData.getFrameLayoutStack().peek().declareVariable(varSym);
    MISetter varSetter =
        iData.getFrameLayoutStack().peek().getVariableSetter(varSym);

    // get the expression as an iterable
    node.getExpression().accept(getTraverser());
    // s. JLS 21 14.14.2
    MICalculationValue iterableCalc;
    if (exprType.isArrayType()) {
      // convert the array into an iterable
      MICalculationValue arrayCalc =
          iData.popCalculation().asCalculationValue();
      iterableCalc = frame -> {
        Object arrayObj = arrayCalc.calculate(frame).asNativeObject();
        final int length = Array.getLength(arrayObj);
        List<Object> list = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
          list.add(Array.get(arrayObj, i));
        }
        return new MCValueObject(list);
      };
    }
    else {
      iterableCalc = iData.popCalculation().asCalculationValue();
    }

    // turn the iterable into a BooleanSupplier
    // which updates the variable
    MICalculationValue forControlCalc = frame -> {
      final Iterable<?> expressionIterable = iterableCalc.asCalculationValue()
          .calculate(frame).asObject().unsafeCast();
      final Iterator<?> expressionIterator = expressionIterable.iterator();
      BooleanSupplier updateAndCheckIter = () -> {
        if (expressionIterator.hasNext()) {
          final MCValue nextValue = MCValueFactory
              .createMIValueOfNativeObject(expressionIterator.next());
          varSetter.set(frame, nextValue);
          return true;
        }
        else {
          return false;
        }
      };
      return new MCValueObject(updateAndCheckIter);
    };

    iData.putCalculation(forControlCalc);
  }

  @Override
  public void traverse(ASTWhileStatement node) {
    node.getCondition().accept(getTraverser());
    MICalculationBoolean conditionCalc =
        iData.popCalculation().asCalculationBoolean();
    node.getMCStatement().accept(getTraverser());
    MICalculationVoid bodyCalc =
        iData.popCalculation().asCalculationVoid();
    MICalculationVoid whileCalc = frame -> {
      while (conditionCalc.calculate(frame)) {
        try {
          bodyCalc.calculate(frame);
        }
        catch (MCSignalBreak ignored) {
          break;
        }
        catch (MCSignalContinue ignored) {
          // no-op
        }
      }
    };
    iData.putCalculation(whileCalc);
  }

  @Override
  public void traverse(ASTDoWhileStatement node) {
    node.getMCStatement().accept(getTraverser());
    MICalculationVoid bodyCalc =
        iData.popCalculation().asCalculationVoid();
    node.getCondition().accept(getTraverser());
    MICalculationBoolean conditionCalc =
        iData.popCalculation().asCalculationBoolean();
    MICalculationVoid doWhileCalc = frame -> {
      do {
        try {
          bodyCalc.calculate(frame);
        }
        catch (MCSignalBreak ignored) {
          break;
        }
        catch (MCSignalContinue ignored) {
          // no-op
        }
      }
      while (conditionCalc.calculate(frame));
    };
    iData.putCalculation(doWhileCalc);
  }

  @Override
  public void traverse(ASTExpressionStatement node) {
    // one could make it void, but there is no reason to
    node.getExpression().accept(getTraverser());
  }

  @Override
  public void traverse(ASTEmptyStatement node) {
    iData.putCalculation(MICalculationVoid.NOOP_CALC);
  }

  @Override
  public void traverse(ASTBreakStatement node) {
    MICalculationVoid breakCalc = frame ->
        MCSignalBreak.signal();
    iData.putCalculation(breakCalc);
  }

  // helper

  /**
   * Chains the behavior of nodes.
   *
   * @param nodes the nodes to chain together
   * @return the behavior of the nodes in order.
   */
  protected MICalculationVoid chainBehavior(List<? extends ASTNode> nodes) {
    MICalculationVoid expressionsCalc = MICalculationVoid.NOOP_CALC;
    for (ASTNode expression : nodes) {
      expression.accept(getTraverser());
      MICalculationVoid exprCalc = iData.popCalculation().asCalculationVoid();
      expressionsCalc = expressionsCalc.getChainedBefore(exprCalc);
    }
    return expressionsCalc;
  }

}
