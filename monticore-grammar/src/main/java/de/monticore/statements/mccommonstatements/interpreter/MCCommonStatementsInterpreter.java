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
import de.monticore.statements.mclowlevelstatements._symboltable.LabelSymbol;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCStatement;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.util.OOWithinTypeBasicSymbolsResolver;
import de.monticore.values.MCValue;
import de.monticore.values.MCValueFactory;
import de.monticore.values.MCValueObject;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

import static de.monticore.symbols.oosymbols.types3.OOSymbolsSymTypeRelations.isEnum;
import static de.monticore.symbols.oosymbols.types3.OOSymbolsSymTypeRelations.sourceIsEnumConstant;
import static de.monticore.types3.SymTypeRelations.normalize;
import static de.monticore.types3.TypeCheck3.typeOf;

/**
 * Interpreter Visitor for MCCommonStatements
 * <p>
 * Note: For simplicity, has a dependency on MCLowLevelStatements
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
    // for control
    node.getForControl().accept(getTraverser());
    MICalculationValue forControlCalc =
        iData.popCalculation().asCalculationValue();
    // null if initialization has not happened.
    // needs to be reset after the loop
    final BooleanSupplier[] updateAndCheckIter = new BooleanSupplier[1];
    MICalculationBoolean forControlAsConditionCalc = frame -> {
      if (updateAndCheckIter[0] == null) {
        updateAndCheckIter[0] =
            forControlCalc.calculate(frame).asObject().unsafeCast();
      }
      return updateAndCheckIter[0].getAsBoolean();
    };
    MICalculationVoid resetInitialized = frame -> updateAndCheckIter[0] = null;

    // body
    node.getMCStatement().accept(getTraverser());
    MICalculationVoid bodyCalc =
        iData.popCalculation().asCalculationVoid();

    // full loop, resets initialization afterwarts
    MICalculationVoid forCalcWithOutReset =
        createWhileLoop(node, forControlAsConditionCalc, bodyCalc);
    MICalculationVoid forCalc = frame -> {
      try {
        forCalcWithOutReset.calculate(frame);
      }
      finally {
        resetInitialized.calculate(frame);
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
        final MCValueObject arrayObj = arrayCalc.calculate(frame).asObject();
        return new MCValueObject(arrayObj.arrayToObjectList());
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
    MICalculationVoid whileCalc =
        createWhileLoop(node, conditionCalc, bodyCalc);
    iData.putCalculation(whileCalc);
  }

  @Override
  public void traverse(ASTDoWhileStatement node) {
    // condition:
    // as it is a do-while loop, it is skipped during the first run.
    node.getCondition().accept(getTraverser());
    MICalculationBoolean conditionCalc =
        iData.popCalculation().asCalculationBoolean();
    final boolean[] firstRun = new boolean[] { true };
    MICalculationBoolean conditionCalcSkipFirst = frame ->
        firstRun[0] ? !(firstRun[0] = false) : conditionCalc.calculate(frame);

    // body
    node.getMCStatement().accept(getTraverser());
    MICalculationVoid bodyCalc =
        iData.popCalculation().asCalculationVoid();

    MICalculationVoid doWhileCalc =
        createWhileLoop(node, conditionCalcSkipFirst, bodyCalc);
    iData.putCalculation(doWhileCalc);
  }

  // relies on ASTSwitchLabel calculating to a Predicate<MCValue>,
  // which states weather the value matches the label
  @Override
  public void traverse(ASTSwitchStatement node) {
    Optional<LabelSymbol> labelSymbolOpt =
        LabelSymbol.getLabelOfStatement(node);
    @Nullable final String labelStr = labelSymbolOpt
        .map(LabelSymbol::getName)
        .orElse(null);
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
      catch (MCSignalBreak signal) {
        // an unlabeled break always ends the switch
        if (signal.getLabel().map(l -> !l.equals(labelStr)).orElse(false)) {
          throw signal;
        }
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

  /**
   * Creates the calculation of a while-loop.
   * <p>
   * This will handle {@code break} and {@code continue} statements,
   * including labels.
   *
   * @param node          the node that represents the loop,
   *                      it must be the loop that has the label iff applicable.
   * @param conditionCalc the condition if the loop body should be executed.
   *                      Needs to carry out any setup iff required.
   * @param bodyCalc      the body of the loop to be executed
   *                      based on the condition.
   * @return A calculation representing the while-loop.
   */
  protected MICalculationVoid createWhileLoop(
      ASTMCStatement node,
      MICalculationBoolean conditionCalc,
      MICalculationVoid bodyCalc
  ) {
    Optional<LabelSymbol> labelSymbolOpt =
        LabelSymbol.getLabelOfStatement(node);
    @Nullable final String labelStr = labelSymbolOpt
        .map(LabelSymbol::getName)
        .orElse(null);
    return frame -> {
      while (conditionCalc.calculate(frame)) {
        try {
          bodyCalc.calculate(frame);
        }
        catch (MCSignalBreak signal) {
          if (signal.getLabel().map(l -> !l.equals(labelStr)).orElse(false)) {
            throw signal;
          }
          break;
        }
        catch (MCSignalContinue signal) {
          if (signal.getLabel().map(l -> !l.equals(labelStr)).orElse(false)) {
            throw signal;
          }
          // explicit continue
          //noinspection UnnecessaryContinue
          continue;
        }
      }
    };
  }

}
