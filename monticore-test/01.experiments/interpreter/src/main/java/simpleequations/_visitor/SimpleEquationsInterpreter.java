/* (c) https://github.com/MontiCore/monticore */
package simpleequations._visitor;

import de.monticore.interpreter.MIScope;
import de.monticore.interpreter.MIValue;
import de.monticore.interpreter.MIValueFactory;
import de.monticore.interpreter.values.ErrorMIValue;
import de.monticore.interpreter.values.MIReturnSignal;
import de.monticore.interpreter.values.ModelFunctionMIValue;
import simpleequations._ast.*;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SimpleEquationsInterpreter extends SimpleEquationsInterpreterTOP {

  public SimpleEquationsInterpreter() {
    super();
  }

  public MIValue interpret(ASTProgram node) {
    MIValue result = new ErrorMIValue("Error ASTProgram node");
    for (ASTStatement s : node.getStatementList()) {
      //we can skip this as we only need to interpret it when it is getting called
      if (s instanceof ASTFunctionDefinition) continue;
      result = s.evaluate(getRealThis());
      if (result.isReturn()) {
          return result;
      }
    }
    if (node.isPresentExpression()) {
      return node.getExpression().evaluate(getRealThis());
    }
    return result;
  }

  public MIValue interpret(ASTPlusEquation node) {
    MIValue left = node.getLeft().evaluate(getRealThis());
    MIValue right = node.getRight().evaluate(getRealThis());

    if (left.isInt() && right.isInt()) {
      return MIValueFactory.createValue(left.asInt() + right.asInt());
    }
    return MIValueFactory.createValue(left.asFloat() + right.asFloat());
  }

  public MIValue interpret(ASTMinusEquation node) {
    MIValue left = node.getLeft().evaluate(getRealThis());
    MIValue right = node.getRight().evaluate(getRealThis());

    if (left.isInt() && right.isInt()) {
      return MIValueFactory.createValue(left.asInt() - right.asInt());
    }
    return MIValueFactory.createValue(left.asFloat() - right.asFloat());
  }

  public MIValue interpret(ASTMultiplyEquation node) {
    MIValue left = node.getLeft().evaluate(getRealThis());
    MIValue right = node.getRight().evaluate(getRealThis());

    if (left.isInt() && right.isInt()) {
      return MIValueFactory.createValue(left.asInt() * right.asInt());
    }
    return MIValueFactory.createValue(left.asFloat() * right.asFloat());
  }

  public MIValue interpret(ASTDivideEquation node) {
    MIValue left = node.getLeft().evaluate(getRealThis());
    MIValue right = node.getRight().evaluate(getRealThis());

    if (left.isInt() && right.isInt()) {
      return MIValueFactory.createValue(left.asInt() / right.asInt());
    }
    return MIValueFactory.createValue(left.asFloat() / right.asFloat());
  }

  public MIValue interpret(ASTGreaterThanExpression node) {
    MIValue left = node.getLeft().evaluate(getRealThis());
    MIValue right = node.getRight().evaluate(getRealThis());

    if (left.isInt() && right.isInt()) {
      return MIValueFactory.createValue(left.asInt() > right.asInt());
    }
    return MIValueFactory.createValue(left.asFloat() > right.asFloat());
  }

  public MIValue interpret(ASTVariableDefinition node) {
    MIValue value = node.getValue().evaluate(getRealThis());
    getRealThis().declareVariable(node.getSymbol(), Optional.of(value));
    return value;
  }

  public MIValue interpret(ASTVariableUsage node) {
    MIValue value = node.getValue().evaluate(getRealThis());
    var symbol = node.getEnclosingScope().resolveVariableDefinition(node.getName());

    if (symbol.isPresent()) {
      getRealThis().storeVariable(symbol.get(), value);
    } else {
      return new ErrorMIValue("Error ASTVariableUsage variable not found");
    }

    return value;
  }

  public MIValue interpret(ASTPrintStatement node) {
    MIValue output = node.getExpression().evaluate(getRealThis());

    if (output.isInt()) {
      System.out.println(output.asInt());
    } else if (output.isFloat()) {
      System.out.println(output.asFloat());
    }
    return output;
  }

  public MIValue interpret(ASTNameExpression node) {
    var optSymbol = node.getEnclosingScope().resolveVariableDefinition(node.getName());
    if (optSymbol.isPresent()) {
      return getRealThis().loadVariable(optSymbol.get());
    }else{
      return new ErrorMIValue("Error ASTNameExpression node");
    }
  }

  public MIValue interpret(ASTFunctionCall node) {
    Optional<FunctionSymbol> functionSymbol = node.getEnclosingScope().resolveFunction(node.getName());
    if(functionSymbol.isPresent()){
      ASTFunctionDefinition functionDefinition = (ASTFunctionDefinition) functionSymbol.get().getAstNode();
      List<MIValue> args = new ArrayList<>();
      
      for(ASTExpression expr : node.getArgList().getArgsList()){
        args.add(expr.evaluate(getRealThis()));
      }

      MIScope newScope = new MIScope(getRealThis().getCurrentScope());
      getRealThis().pushScope(newScope);

      try {
          List<ASTVariableAsParameter> parameters = functionDefinition.getFormalParameters().getFormalParameterListing().streamVariableAsParameters().collect(Collectors.toList());
          for(int i = 0; i < parameters.size(); i++){
            VariableSymbol param = parameters.get(i).getSymbol();
            getRealThis().declareVariable(param, Optional.of(args.get(i)));
          }

          MIValue result = functionDefinition.getProgram().evaluate(getRealThis());

          while (result.isReturn()){
            result = result.asReturnValue();
          }

          if (result.isError()) {
             System.out.println("Error in function call");
          }
          return result;
      } finally {
          getRealThis().popScope();
      }
    }
    return new ErrorMIValue("Error ASTFunctionCall node");
  }
  
  public MIValue interpret(ASTIfStatement node) {
      MIValue condition = node.getCondition().evaluate(getRealThis());
      if (condition.asBoolean()) {
          MIValue returnValue =  node.getThenBlock().evaluate(getRealThis());
          return returnValue;
      } else if (node.isPresentElseBlock()) {
          MIValue returnValue = node.getElseBlock().evaluate(getRealThis());
          return returnValue;
      }
      return new ErrorMIValue("IfStatement no branch executed");
  }

  public MIValue interpret(ASTFunctionBlock node) {
    MIValue result = new ErrorMIValue("Error ASTFunctionBlock node");

    for (ASTStatement s : node.getStatementList()) {
      result = s.evaluate(getRealThis());

      if (result.isReturn()) {
        return result;
      }
    }

    // Evaluate the optional trailing expression, if present
    if (node.isPresentExpression()) {
      return node.getExpression().evaluate(getRealThis());
    }

    return result;
  }
  
  public MIValue interpret(ASTReturnStatement node) {
      MIValue value = node.getExpression().evaluate(getRealThis());
      if(value.isError()){
        return new ErrorMIValue("error in return statement");
      }
      return new MIReturnSignal(value);
  }


  public MIValue interpret(ASTNumberExpression node) {
    return node.getNumber().evaluate(getRealThis());
  }

}