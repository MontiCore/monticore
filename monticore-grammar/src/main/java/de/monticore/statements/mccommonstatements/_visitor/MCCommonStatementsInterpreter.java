package de.monticore.statements.mccommonstatements._visitor;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.interpreter.MIScope;
import de.monticore.interpreter.MIValue;
import de.monticore.interpreter.MIValueFactory;
import de.monticore.interpreter.ModelInterpreter;
import de.monticore.interpreter.iterators.MICommonForIterator;
import de.monticore.interpreter.iterators.MIForEachIterator;
import de.monticore.interpreter.iterators.MIForIterator;
import de.monticore.interpreter.values.ErrorMIValue;
import de.monticore.interpreter.values.MIBreakSignal;
import de.monticore.interpreter.values.VoidMIValue;
import de.monticore.statements.mccommonstatements._ast.ASTBreakStatement;
import de.monticore.statements.mccommonstatements._ast.ASTCommonForControl;
import de.monticore.statements.mccommonstatements._ast.ASTDoWhileStatement;
import de.monticore.statements.mccommonstatements._ast.ASTEmptyStatement;
import de.monticore.statements.mccommonstatements._ast.ASTEnhancedForControl;
import de.monticore.statements.mccommonstatements._ast.ASTExpressionStatement;
import de.monticore.statements.mccommonstatements._ast.ASTForInit;
import de.monticore.statements.mccommonstatements._ast.ASTForInitByExpressions;
import de.monticore.statements.mccommonstatements._ast.ASTForStatement;
import de.monticore.statements.mccommonstatements._ast.ASTFormalParameter;
import de.monticore.statements.mccommonstatements._ast.ASTIfStatement;
import de.monticore.statements.mccommonstatements._ast.ASTMCJavaBlock;
import de.monticore.statements.mccommonstatements._ast.ASTWhileStatement;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCBlockStatement;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class MCCommonStatementsInterpreter extends MCCommonStatementsInterpreterTOP {

  public MCCommonStatementsInterpreter() {
    super();
  }

  public MCCommonStatementsInterpreter(ModelInterpreter realThis) {
    super(realThis);
  }

  @Override
  public MIValue interpret(ASTMCJavaBlock node) {
    MIScope scope = new MIScope(getRealThis().getCurrentScope());
    pushScope(scope);

    for (ASTMCBlockStatement statement : node.getMCBlockStatementList()) {
      MIValue result = statement.evaluate(getRealThis());
      if (result.isFlowControlSignal()) {
        popScope();
        return result;
      }
    }

    popScope();
    return new VoidMIValue();
  }

  @Override
  public MIValue interpret(ASTIfStatement node) {
    MIValue condition = node.getCondition().evaluate(getRealThis());
    if (condition.isFlowControlSignal())
      return condition;

    if (condition.asBoolean()) {
      MIValue result = node.getThenStatement().evaluate(getRealThis());
      if (result.isFlowControlSignal())
        return result;
    }
    else if (node.isPresentElseStatement()) {
      MIValue result = node.getElseStatement().evaluate(getRealThis());
      if (result.isFlowControlSignal())
        return result;
    }
    return new VoidMIValue();
  }

  @Override
  public MIValue interpret(ASTWhileStatement node) {
    MIScope scope = new MIScope(getRealThis().getCurrentScope());
    pushScope(scope);

    MIValue condition = node.getCondition().evaluate(getRealThis());
    while (condition.isBoolean() && condition.asBoolean()) {
      MIValue result = node.getMCStatement().evaluate(getRealThis());
      if (result.isBreak())
        break;
      if (result.isContinue())
        continue;
      if (result.isFlowControlSignal()) {
        popScope();
        return result;
      }

      condition = node.getCondition().evaluate(getRealThis());
    }

    popScope();
    if (!condition.isBoolean()) {
      String errorMsg = "0x57009 While condition must be of type boolean. Got " + condition.printType()
          + " (" + condition.printValue() + ").";
      Log.error(errorMsg, node.getCondition().get_SourcePositionStart(), node.getCondition().get_SourcePositionEnd());
      return new ErrorMIValue(errorMsg);
    }
    else if (condition.isFlowControlSignal()) {
      return condition;
    }
    else {
      return new VoidMIValue();
    }
  }

  @Override
  public MIValue interpret(ASTDoWhileStatement node) {
    MIScope scope = new MIScope(getRealThis().getCurrentScope());
    pushScope(scope);

    MIValue condition = MIValueFactory.createValue(true);
    while (condition.isBoolean() && condition.asBoolean()) {
      MIValue result = node.getMCStatement().evaluate(getRealThis());
      if (result.isBreak())
        break;
      if (result.isContinue())
        continue;
      if (result.isFlowControlSignal()) {
        popScope();
        return result;
      }

      condition = node.getCondition().evaluate(getRealThis());
    }

    popScope();
    if (!condition.isBoolean()) {
      String errorMsg = "0x57009 While condition must be of type boolean. Got " + condition.printType()
          + " (" + condition.printValue() + ").";
      Log.error(errorMsg, node.getCondition().get_SourcePositionStart(), node.getCondition().get_SourcePositionEnd());
      return new ErrorMIValue(errorMsg);
    }
    else if (condition.isFlowControlSignal()) {
      return condition;
    }
    else {
      return new VoidMIValue();
    }
  }

  @Override
  public MIValue interpret(ASTEmptyStatement node) {
    return new VoidMIValue();
  }

  @Override
  public MIValue interpret(ASTExpressionStatement node) {
    MIValue result = node.getExpression().evaluate(getRealThis());
    if (result.isFlowControlSignal())
      return result;
    return new VoidMIValue();
  }

  @Override
  public MIValue interpret(ASTForStatement node) {
    MIScope scope = new MIScope(getRealThis().getCurrentScope());
    pushScope(scope);

    MIValue control = node.getForControl().evaluate(getRealThis());
    if (control.isFlowControlSignal()) {
      popScope();
      return control;
    }

    MIForIterator controlIterator = (MIForIterator) control.asObject();

    MIValue result = controlIterator.execute(getRealThis(), node.getMCStatement());
    popScope();
    return result.isFlowControlSignal() ? result : new VoidMIValue();
  }

  @Override
  public MIValue interpret(ASTCommonForControl node) {
    Optional<ASTForInit> initNode = node.isPresentForInit() ? Optional.of(node.getForInit()) : Optional.empty();
    Optional<ASTExpression> condition = node.isPresentCondition() ? Optional.of(node.getCondition()) : Optional.empty();
    List<ASTExpression> expressions = node.getExpressionList();
    MICommonForIterator iterator = new MICommonForIterator(initNode, condition, expressions);
    return MIValueFactory.createValue(iterator);
  }

  @Override
  public MIValue interpret(ASTForInit node) {
    if (node.isPresentForInitByExpressions()) {
      return node.getForInitByExpressions().evaluate(getRealThis());
    }
    else if (node.isPresentLocalVariableDeclaration()) {
      return node.getLocalVariableDeclaration().evaluate(getRealThis());
    }

    return new VoidMIValue();
  }

  @Override
  public MIValue interpret(ASTForInitByExpressions node) {
    for (ASTExpression expression : node.getExpressionList()) {
      MIValue result = expression.evaluate(getRealThis());
      if (result.isFlowControlSignal())
        return result;
    }
    return new VoidMIValue();
  }

  @Override
  public MIValue interpret(ASTEnhancedForControl node) {
    MIValue collectionValue = node.getExpression().evaluate(getRealThis());
    if (collectionValue.isFlowControlSignal())
      return collectionValue;

    if (!(collectionValue.asObject() instanceof Collection<?>)) {
      String errorMsg = "0x57082 Expected a collection in for-each loop. Got " + collectionValue.printType()
          + " (" + collectionValue.printValue() + ").";
      Log.error(errorMsg, node.getExpression().get_SourcePositionStart(), node.getExpression().get_SourcePositionEnd());
      return new ErrorMIValue(errorMsg);
    }

    Collection<Object> collection = (Collection<Object>) (collectionValue.asObject());
    VariableSymbol symbol = node.getFormalParameter().getDeclarator().getSymbol();
    MIForIterator iterator = new MIForEachIterator(symbol, collection.iterator());
    return MIValueFactory.createValue(iterator);
  }

  @Override
  public MIValue interpret(ASTFormalParameter node) {
    MIValue result = node.getDeclarator().evaluate(getRealThis());
    if (result.isFlowControlSignal()) {
      return result;
    }
    else {
      return new VoidMIValue();
    }
  }

  @Override
  public MIValue interpret(ASTBreakStatement node) {
    return new MIBreakSignal();
  }
}
