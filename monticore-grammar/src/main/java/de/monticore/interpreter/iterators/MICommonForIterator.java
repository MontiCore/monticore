package de.monticore.interpreter.iterators;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.interpreter.MIValue;
import de.monticore.interpreter.MIValueFactory;
import de.monticore.interpreter.ModelInterpreter;
import de.monticore.interpreter.values.VoidMIValue;
import de.monticore.statements.mccommonstatements._ast.ASTForInit;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCStatement;

import java.util.List;
import java.util.Optional;

public class MICommonForIterator implements MIForIterator {

  protected Optional<ASTForInit> initNode;

  protected Optional<ASTExpression> condition;

  protected List<ASTExpression> expressions;

  public MICommonForIterator(Optional<ASTForInit> initNode, Optional<ASTExpression> condition, List<ASTExpression> expressions) {
    this.initNode = initNode;
    this.condition = condition;
    this.expressions = expressions;
  }

  @Override
  public MIValue execute(ModelInterpreter interpreter, ASTMCStatement body) {
    if (initNode.isPresent()) {
      MIValue result = initNode.get().evaluate(interpreter);
      if (result.isFlowControlSignal())
        return result;
    }

    MIValue conditionResult = checkCondition(interpreter);
    while (conditionResult.isBoolean() && conditionResult.asBoolean()) {
      MIValue statementResult = body.evaluate(interpreter);
      if (statementResult.isBreak())
        break;
      if (statementResult.isFlowControlSignal() && !statementResult.isContinue())
        return statementResult;

      MIValue incrementResult = increment(interpreter);
      if (incrementResult.isFlowControlSignal()) {
        return incrementResult;
      }

      conditionResult = checkCondition(interpreter);
    }

    return conditionResult.isFlowControlSignal() ? conditionResult : new VoidMIValue();
  }

  // helper

  protected MIValue checkCondition(ModelInterpreter interpreter) {
    if (condition.isPresent()) {
      return condition.get().evaluate(interpreter);
    }
    else {
      return MIValueFactory.createValue(true);
    }
  }

  protected MIValue increment(ModelInterpreter interpreter) {
    for (ASTExpression expression : expressions) {
      MIValue result = expression.evaluate(interpreter);
      if (result.isError())
        return result;
    }
    return new VoidMIValue();
  }

}
