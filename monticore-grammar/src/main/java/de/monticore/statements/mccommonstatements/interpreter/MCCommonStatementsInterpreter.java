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
import de.monticore.interpreter.util.SymbolAccessHandler;
import de.monticore.statements.mccommonstatements.MCCommonStatementsMill;
import de.monticore.statements.mccommonstatements._ast.*;
import de.monticore.statements.mccommonstatements._visitor.MCCommonStatementsInheritanceHandler;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.util.OOWithinTypeBasicSymbolsResolver;
import de.monticore.values.MCValue;
import de.monticore.values.MCValueFactory;
import de.monticore.values.MCValueObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

import static de.monticore.symbols.oosymbols.types3.OOSymbolsSymTypeRelations.isEnum;
import static de.monticore.symbols.oosymbols.types3.OOSymbolsSymTypeRelations.sourceIsEnumConstant;
import static de.monticore.types3.SymTypeRelations.normalize;
import static de.monticore.types3.TypeCheck3.symTypeFromAST;
import static de.monticore.types3.TypeCheck3.typeOf;

/**
 * Interpreter Visitor for MCCommonStatements
 */
public class MCCommonStatementsInterpreter
    extends MCCommonStatementsInheritanceHandler {

  protected InterpreterDataForBasicSymbols iData;
  protected SymbolAccessHandler symbolAccessHandler =
      new SymbolAccessHandler();

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
    SymTypeExpression varType =
        normalize(symTypeFromAST(node.getFormalParameter().getMCType()));
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

  // relies on ASTSwitchLabel calculating to a Predicate<MCValue>,
  // which states weather the value matches the label
  @Override
  public void traverse(ASTSwitchStatement node) {
    SymTypeExpression switchType = normalize(typeOf(node.getExpression()));
    node.getExpression().accept(getTraverser());
    MICalculationValue switchExprCalc =
        iData.popCalculation().asCalculationValue();

    // collect all the groups first
    record SwitchGroup(
        MICalculationValue predicateCalc,
        MICalculationVoid statementCalc
    ) {
    }
    final List<SwitchGroup> switchGroups =
        new ArrayList<>(node.sizeSwitchBlockStatementGroups() + 1);
    for (ASTSwitchBlockStatementGroup group : node.getSwitchBlockStatementGroupList()) {
      switchGroups.add(new SwitchGroup(
          getPredicateCalc(group.getSwitchLabelList(), switchType),
          chainBehavior(group.getMCBlockStatementList())
      ));
    }
    // Add the empty labels as its own group.
    // This, in most cases should not change anything.
    switchGroups.add(new SwitchGroup(
        getPredicateCalc(node.getSwitchLabelList(), switchType),
        MICalculationVoid.NOOP_CALC
    ));

    MICalculationVoid switchCalc = frame -> {
      final MCValue switchExprValue = switchExprCalc.calculate(frame);
      try {
        boolean shouldExecute = false;
        for (SwitchGroup group : switchGroups) {
          // this could be optimized by creating the predicates only once
          // (since all cases are constants).
          // As of writing, not considered important
          final Predicate<MCValue> predicate =
              group.predicateCalc().calculate(frame).asObject().unsafeCast();
          shouldExecute = shouldExecute || predicate.test(switchExprValue);
          if (shouldExecute) {
            group.statementCalc().calculate(frame);
          }
        }
      }
      catch (MCSignalBreak ignored) {
        // no-op
      }
    };
    iData.putCalculation(switchCalc);
  }

  protected MICalculationValue getPredicateCalc(
      List<ASTSwitchLabel> labels,
      SymTypeExpression switchType
  ) {
    final List<MICalculationValue> predicateCalcs = labels.stream()
        .map(l -> getPredicateCalc(l, switchType))
        .toList();
    MICalculationValue predicateCalc = frame -> {
      @SuppressWarnings("unchecked") final Predicate<MCValue>[] predicates =
          predicateCalcs.stream()
              .<Predicate<MCValue>> map(
                  c -> c.calculate(frame).asObject().unsafeCast()
              )
              .toArray(Predicate[]::new);
      final Predicate<MCValue> compoundPredicate = value -> {
        for (Predicate<MCValue> predicate : predicates) {
          if (predicate.test(value)) {
            return true;
          }
        }
        return false;
      };
      return new MCValueObject(compoundPredicate);
    };
    return predicateCalc;
  }

  protected MICalculationValue getPredicateCalc(
      ASTSwitchLabel label,
      SymTypeExpression switchType
  ) {
    if (
        MCCommonStatementsMill.typeDispatcher()
            .isMCCommonStatementsASTEnumConstantSwitchLabel(label)
    ) {
      ASTEnumConstantSwitchLabel enumConstantSwitchLabel =
          MCCommonStatementsMill.typeDispatcher()
              .asMCCommonStatementsASTEnumConstantSwitchLabel(label);
      return getEnumConstantPredicateCalc(enumConstantSwitchLabel, switchType);
    }
    else {
      label.accept(getTraverser());
      return iData.popCalculation().asCalculationValue();
    }
  }

  protected MICalculationValue getEnumConstantPredicateCalc(
      ASTEnumConstantSwitchLabel node,
      SymTypeExpression switchType
  ) {
    // todo check if modifications are needed after
    //  https://git.rwth-aachen.de/monticore/monticore/-/work_items/4997
    Preconditions.checkArgument(isEnum(switchType));
    // assumed to exist at this point:
    SymTypeExpression enumConstantType = normalize(
        OOWithinTypeBasicSymbolsResolver.resolveVariable(
            switchType, node.getEnumConstant(),
            AccessModifier.ALL_INCLUSION, f -> true
        ).get()
    );
    Preconditions.checkState(sourceIsEnumConstant(enumConstantType));
    VariableSymbol enumConstantSym = (VariableSymbol)
        enumConstantType.getSourceInfo().getSourceSymbol().get();
    MICalculationValue enumConstantCalc = symbolAccessHandler.getSymbolAccess(
            enumConstantSym, iData.getFrameLayoutStack().peek(), iData
        ).getter()
        .asCalculationValue();

    MICalculationValue predicateCalc = frame -> {
      final Object enumConstant = enumConstantCalc.calculate(frame);
      final Predicate<MCValue> predicate = enumConstant::equals;
      return new MCValueObject(predicate);
    };
    return predicateCalc;
  }

  /**
   * Instead of this,
   * {@link #getEnumConstantPredicateCalc(ASTEnumConstantSwitchLabel, SymTypeExpression)}
   * should be called.
   *
   * @param node that is traversed
   */
  @Override
  public void traverse(ASTEnumConstantSwitchLabel node) {
    throw new IllegalCallerException(
        "0xFD924 this is not expected to be called."
    );
  }

  @Override
  public void traverse(ASTConstantExpressionSwitchLabel node) {
    // the expression is expected to be a constant.
    // Thus, we rely on this and only calculate the values once.
    // Unlike Java, though, we cannot rely on String interning (JLS 21 12.29),
    // wherefore, we use `equals` in this case.
    SymTypeExpression constantType = normalize(typeOf(node.getConstant()));
    node.getConstant().accept(getTraverser());
    MICalculationValue constantCalc =
        iData.popCalculation().asCalculationValue();
    MICalculationValue predicateCalc;
    predicateCalc = frame -> {
      final MCValue constant = constantCalc.calculate(frame);
      // should be == rather than .equals,
      // but works out regardless (necessary due to lack of String interning)
      final Predicate<MCValue> predicate = constant::equals;
      return new MCValueObject(predicate);
    };

    iData.putCalculation(predicateCalc);
  }

  @Override
  public void traverse(ASTDefaultSwitchLabel node) {
    Predicate<MCValue> predicate = value -> true;
    MICalculationValue predicateCalc =
        frame -> new MCValueObject(predicate);
    iData.putCalculation(predicateCalc);
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
