/* (c) https://github.com/MontiCore/monticore */
package simpleequations._visitor;

import de.monticore.interpreter.MIScope;
import de.monticore.interpreter.MIValue;
import de.monticore.interpreter.MIValueFactory;
import de.monticore.interpreter.values.ErrorMIValue;
import de.monticore.interpreter.values.MIReturnSignal;
import de.monticore.interpreter.values.ModelFunctionMIValue;
import de.monticore.interpreter.values.VoidMIValue;
import de.monticore.javalight._ast.ASTMethodDeclaration;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.SymTypeExpressionFactory;
import simpleequations._ast.*;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.stream.Collectors;

public class SimpleEquationsInterpreter extends SimpleEquationsInterpreterTOP {

  public SimpleEquationsInterpreter() {
    super();
  }


  public MIValue interpret(ASTSimpleEquationCompilationUnit node) {
    MIValue result;
    for(ASTFunctionDefinition method : node.getFunctionDefinitionList()){
      method.evaluate(getRealThis());
    }

    result = node.getProgramBlock().evaluate(getRealThis());
    if(result.isError()){
      return new ErrorMIValue("Error ASTSimpleEquationCompilationUnit wrong return type from ASTProgramBlock");
    }
    return result;
  }

  public MIValue interpret(ASTProgramBlock node) {
    MIValue result = new ErrorMIValue("Error ASTProgram node");

    for (ASTStatement s : node.getStatementList()) {
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
    Optional<VariableSymbol> symbols = node.getEnclosingScope().resolveVariable(node.getName());

    if (symbols.isPresent()) {
      getRealThis().storeVariable(symbols.get(), value);
    } else {
      throw new RuntimeException("CRITICAL: Variable '" + node.getName() + "' not found in scope!");
    }

    return value;
  }

  public MIValue interpret(ASTPrintStatement node) {
    MIValue output = node.getExpression().evaluate(getRealThis());

    //if (output.isInt()) {
    //  System.out.println(output.asInt());
    //} else if (output.isFloat()) {
    //  System.out.println(output.asFloat());
    //}

    return output;
  }

  public MIValue interpret(ASTNameExpression node) {
    Optional<VariableSymbol> typeVars = node.getEnclosingScope().resolveVariable(node.getName());

    if (!typeVars.isEmpty()) {
      return getRealThis().loadVariable(typeVars.get());
    } else {
      throw new RuntimeException("0x57071: Variable '" + node.getName() + "' not found.");
    }
  }

  public MIValue interpret(ASTFunctionDefinition node) {
    //this must be init to create int types
    BasicSymbolsMill.initializePrimitives();

    FunctionSymbol funcSym = node.getSymbol();

    //set return type of function like specified (only primitive allowed)
    funcSym.setType(SymTypeExpressionFactory.createPrimitive(node.getReturnType()));

    List<VariableSymbol> parameterSymbols = node.getFormalParameters()
        .getFormalParameterListing()
        .streamVariableAsParameters()
        .map(ASTVariableAsParameter::getSymbol)
        .collect(Collectors.toList());

    //set types of the parameters
    for (VariableSymbol param : parameterSymbols) {
      String typeName = param.getType() != null ? param.getType().print() : "int";
      param.setType(SymTypeExpressionFactory.createPrimitive(typeName));
    }

    ModelFunctionMIValue functionValue = new ModelFunctionMIValue(
        getRealThis().getCurrentScope(),
        parameterSymbols,
        node.getFunctionBlock()
    );

    getRealThis().declareFunction(funcSym, functionValue);
    return new VoidMIValue();
  }

  public MIValue interpret(ASTFunctionBlock node){
    MIValue result = new ErrorMIValue("Error ASTFunctionBlock node");
    for (ASTStatement s : node.getStatementList()) {
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

  public MIValue interpret(ASTFunctionCall node) {
    Optional<FunctionSymbol> functionSymbol = node.getEnclosingScope().resolveFunction(node.getName());

    if (functionSymbol.isPresent()) {
      MIValue funcValue = getRealThis().loadFunction(functionSymbol.get());

      if (funcValue instanceof ModelFunctionMIValue) {
        ModelFunctionMIValue modelFunc = (ModelFunctionMIValue) funcValue;

        List<MIValue> args = new ArrayList<>();
        for (ASTExpression expr : node.getArgList().getArgsList()) {
          args.add(expr.evaluate(getRealThis()));
        }

        MIValue result = modelFunc.execute(getRealThis(), args);

        while (result.isReturn()){
          result = result.asReturnValue();
        }
        return result;
      }
    }
    return new ErrorMIValue("Function '" + node.getName() + "' not found.");
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

  public MIValue interpret(ASTVariableAsParameter node) {
    return new ErrorMIValue("Error ASTVariableAsParameter node");
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