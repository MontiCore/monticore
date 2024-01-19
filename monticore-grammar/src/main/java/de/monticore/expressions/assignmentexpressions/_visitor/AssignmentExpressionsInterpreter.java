/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.assignmentexpressions._visitor;

import de.monticore.expressions.assignmentexpressions.AssignmentExpressionsMill;
import de.monticore.expressions.assignmentexpressions._ast.*;
import de.monticore.interpreter.ModelInterpreter;
import de.monticore.interpreter.Value;
import de.monticore.interpreter.ValueFactory;
import de.monticore.interpreter.values.NotAValue;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

import static de.monticore.expressions.assignmentexpressions._ast.ASTConstantsAssignmentExpressions.*;


public class AssignmentExpressionsInterpreter extends AssignmentExpressionsInterpreterTOP {

  public AssignmentExpressionsInterpreter(ModelInterpreter realThis){
    super(realThis);
  }
  
  public AssignmentExpressionsInterpreter() {
    super();
  }
  
  //i++
  @Override
  public Value interpret(ASTIncSuffixExpression n){
    String expr = AssignmentExpressionsMill.prettyPrint(n.getExpression(),false);
    Optional<VariableSymbol> symbol = ((IBasicSymbolsScope)n.getEnclosingScope()).resolveVariable(expr);
    if(symbol.isPresent()){
      Value value = load(symbol.get());
      if(value.isInt()){
        Value res = ValueFactory.createValue(value.asInt()+1);
        store(symbol.get(), res);
        return res;
      }
      else if(value.isChar()){
        Value res = ValueFactory.createValue(value.asChar()+1);
        store(symbol.get(), res);
        return res;
      }
      else if(value.isLong()){
        Value res = ValueFactory.createValue(value.asLong()+1);
        store(symbol.get(), res);
        return res;
      }
      else if(value.isFloat()){
        Value res = ValueFactory.createValue(value.asFloat()+1);
        store(symbol.get(), res);
        return(res);
      }
      else if(value.isDouble()){
        Value res = ValueFactory.createValue(value.asDouble()+1);
        store(symbol.get(), res);
        return(res);
      }
      else{
        Log.error("Suffix incrementation operation is not suitable for this type.");
      }
    }
    return new NotAValue();
  }

  @Override
  public Value interpret(ASTIncPrefixExpression n){
    String expr = AssignmentExpressionsMill.prettyPrint(n.getExpression(),false);
    Optional<VariableSymbol> symbol = ((IBasicSymbolsScope)n.getEnclosingScope()).resolveVariable(expr);
    if(symbol.isPresent()){
      Value value = load(symbol.get());
      if(value.isInt()){
        Value res = ValueFactory.createValue(value.asInt()+1);
        store(symbol.get(), res);
        return(res);
      }
      else if(value.isChar()){
        Value res = ValueFactory.createValue(value.asChar()+1);
        store(symbol.get(), res);
        return(res);
      }
      else if(value.isLong()){
        Value res = ValueFactory.createValue(value.asLong()+1);
        store(symbol.get(), res);
        return(res);

      }
      else if(value.isFloat()){
        Value res = ValueFactory.createValue(value.asFloat()+1);
        store(symbol.get(), res);
        return(res);
      }
      else if(value.isDouble()){
        Value res = ValueFactory.createValue(value.asDouble()+1);
        store(symbol.get(), res);
        return(res);
      }
      else{
        Log.error("Prefix incrementation operation is not suitable for this type.");
      }
    }
    return new NotAValue();
  }

  @Override
  public Value interpret(ASTDecSuffixExpression n){
    String expr = AssignmentExpressionsMill.prettyPrint(n.getExpression(),false);
    Optional<VariableSymbol> symbol = ((IBasicSymbolsScope)n.getEnclosingScope()).resolveVariable(expr);
    if(symbol.isPresent()){
      Value value = load(symbol.get());
      if(value.isInt()){
        Value res = ValueFactory.createValue(value.asInt()-1);
        store(symbol.get(), res);
        return(res);
      }
      else if(value.isChar()){
        Value res = ValueFactory.createValue(value.asChar()-1);
        store(symbol.get(), res);
        return(res);
      }
      else if(value.isLong()){
        Value res = ValueFactory.createValue(value.asLong()-1);
        store(symbol.get(), res);
        return(res);

      }
      else if(value.isFloat()){
        Value res = ValueFactory.createValue(value.asFloat()-1);
        store(symbol.get(), res);
        return(res);
      }
      else if(value.isDouble()){
        Value res = ValueFactory.createValue(value.asDouble()-1);
        store(symbol.get(), res);
        return(res);
      }
      else{
        Log.error("Suffix decremental operation is not suitable for this type.");
      }
    }
    return new NotAValue();
  }

  @Override
  public Value interpret(ASTDecPrefixExpression n){
    String expr = AssignmentExpressionsMill.prettyPrint(n.getExpression(),false);
    Optional<VariableSymbol> symbol = ((IBasicSymbolsScope)n.getEnclosingScope()).resolveVariable(expr);
    if(symbol.isPresent()){
      Value value = load(symbol.get());
      if(value.isInt()){
        Value res = ValueFactory.createValue(value.asInt()-1);
        store(symbol.get(), res);
        return(res);
      }
      else if(value.isChar()){
        Value res = ValueFactory.createValue(value.asChar()-1);
        store(symbol.get(), res);
        return(res);
      }
      else if(value.isLong()){
        Value res = ValueFactory.createValue(value.asLong()-1);
        store(symbol.get(), res);
        return(res);

      }
      else if(value.isFloat()){
        Value res = ValueFactory.createValue(value.asFloat()-1);
        store(symbol.get(), res);
        return(res);
      }
      else if(value.isDouble()){
        Value res = ValueFactory.createValue(value.asDouble()-1);
        store(symbol.get(), res);
        return(res);
      }
      else{
        Log.error("Prefix decremental operation is not suitable for this type.");
      }
    }
    return new NotAValue();
  }

  @Override
  public Value interpret(ASTAssignmentExpression n){

    String leftExpression = AssignmentExpressionsMill.prettyPrint(n.getLeft(),false);
    Optional<VariableSymbol> leftSymbol = ((IBasicSymbolsScope)n.getEnclosingScope()).resolveVariable(leftExpression);

    if(leftSymbol.isPresent()) {
      Value leftValue = load(leftSymbol.get());
      Value rightValue = n.getRight().evaluate(this);

      int operator = n.getOperator();

      switch (operator){
        case AND_EQUALS: //bitwise and

          if( !( rightValue.isInt() || rightValue.isLong() || rightValue.isChar() || leftValue.isChar() || leftValue.isInt() || leftValue.isLong() ) ){
            Log.error("&= operation is not suitable for these types.");
          }
          if(leftValue.isLong()){
            Value res = ValueFactory.createValue(leftValue.asLong() & rightValue.asInt());
            store(leftSymbol.get(), res);
            return(res);
          }
          if(leftValue.isInt()){
            Value res = ValueFactory.createValue(leftValue.asInt() & rightValue.asInt());
            store(leftSymbol.get(), res);
            return(res);
          }
          if(leftValue.isChar()){
            Value res = ValueFactory.createValue(leftValue.asChar() & rightValue.asInt());
            store(leftSymbol.get(), res);
            return(res);
          }
          break;

        case EQUALS:

          if(leftValue.isString() && rightValue.isString()){
            store(leftSymbol.get(),rightValue);
          }
          else if(leftValue.isObject() && rightValue.isObject()){
            store(leftSymbol.get(),rightValue);
          }
          else if(leftValue.isBoolean() && rightValue.isBoolean()){
            store(leftSymbol.get(),rightValue);
          }
          else if(leftValue.isChar() && rightValue.isChar()){
            store(leftSymbol.get(),rightValue);
          }
          else if(leftValue.isInt() && rightValue.isInt()){
            store(leftSymbol.get(),rightValue);
          }
          else if(leftValue.isLong() && rightValue.isLong()){
            store(leftSymbol.get(),rightValue);
          }
          else if(leftValue.isFloat() && rightValue.isFloat()){
            store(leftSymbol.get(),rightValue);
          }
          else if(leftValue.isDouble() && rightValue.isDouble()){
            store(leftSymbol.get(),rightValue);
          }
          else{
            Log.error("The interpreter only allows = operation for operands of the same type.");
          }
          break;

        case GTGTEQUALS: //bitwise rightValue shift

          if( !( rightValue.isInt() || rightValue.isLong() || rightValue.isChar() || leftValue.isChar() || leftValue.isInt() || leftValue.isLong() ) ){
            Log.error(">>= operation is not suitable for these types.");
          }
          if(leftValue.isLong()){
            Value res = ValueFactory.createValue(leftValue.asLong() >> rightValue.asInt());
            store(leftSymbol.get(), res);
            return(res);
          }
          if(leftValue.isInt()){
            Value res = ValueFactory.createValue(leftValue.asInt() >> rightValue.asInt());
            store(leftSymbol.get(),res);
            return(res);
          }
          if(leftValue.isChar()){
            Value res = ValueFactory.createValue(leftValue.asChar() >> rightValue.asInt());
            store(leftSymbol.get(), res);
            return(res);
          }
          break;

        case GTGTGTEQUALS: //bitwise rightValue shift

          if( !( rightValue.isInt() || rightValue.isLong() || rightValue.isChar() || leftValue.isChar() || leftValue.isInt() || leftValue.isLong() ) ){
            Log.error(">>>= operation is not suitable for these types.");
          }
          if(leftValue.isLong()){
            Value res = ValueFactory.createValue(leftValue.asLong() >>> rightValue.asInt());
            store(leftSymbol.get(), res);
            return(res);
          }
          if(leftValue.isInt()){
            Value res = ValueFactory.createValue(leftValue.asInt() >>> rightValue.asInt());
            store(leftSymbol.get(), res);
            return(res);
          }
          if(leftValue.isChar()){
            Value res = ValueFactory.createValue(leftValue.asChar() >>> rightValue.asInt());
            store(leftSymbol.get(), res);
            return(res);
          }
          break;

        case LTLTEQUALS:

          if( !( rightValue.isInt() || rightValue.isLong() || rightValue.isChar() || leftValue.isChar() || leftValue.isInt() || leftValue.isLong() ) ){
            Log.error("<<= operation is not suitable for these types.");
          }
          if(leftValue.isLong()){
            Value res = ValueFactory.createValue(leftValue.asLong() << rightValue.asInt());
            store(leftSymbol.get(), res);
            return(res);
          }
          if(leftValue.isInt()){
            Value res = ValueFactory.createValue(leftValue.asInt() << rightValue.asInt());
            store(leftSymbol.get(), res);
            return(res);
          }
          if(leftValue.isChar()){
            Value res = ValueFactory.createValue(leftValue.asChar() << rightValue.asInt());
            store(leftSymbol.get(), res);
            return(res);
          }
          break;

        case(MINUSEQUALS):

          if(rightValue.isObject() || rightValue.isString() || rightValue.isBoolean() || leftValue.isObject() || leftValue.isString() || rightValue.isBoolean()){
            Log.error("-= operation is not suitable for these types.");
          }
          if(leftValue.isLong()){
            Value res = ValueFactory.createValue(leftValue.asLong() - rightValue.asInt());
            store(leftSymbol.get(), res);
            return(res);
          }
          if(leftValue.isInt()){
            Value res = ValueFactory.createValue(leftValue.asInt() - rightValue.asInt());
            store(leftSymbol.get(), res);
            return(res);
          }
          if(leftValue.isChar()){
            Value res = ValueFactory.createValue(leftValue.asChar() - rightValue.asInt());
            store(leftSymbol.get(), res);
            return(res);
          }
          if(leftValue.isFloat()){
            Value res = ValueFactory.createValue(leftValue.asFloat() - rightValue.asInt());
            store(leftSymbol.get(), res);
            return(res);
          }
          if(leftValue.isDouble()){
            Value res = ValueFactory.createValue(leftValue.asDouble() - rightValue.asInt());
            store(leftSymbol.get(),res);
            return(res);
          }
          break;

        case(PERCENTEQUALS):

          if(rightValue.isObject() || rightValue.isString() || rightValue.isBoolean() || leftValue.isObject() || leftValue.isString() || rightValue.isBoolean()){
            Log.error("%= operation is not suitable for these types.");
          }
          if(leftValue.isLong()){
            Value res = ValueFactory.createValue(leftValue.asLong() % rightValue.asInt());
            store(leftSymbol.get(), res);
            return(res);
          }
          if(leftValue.isInt()){
            Value res = ValueFactory.createValue(leftValue.asInt() % rightValue.asInt());
            store(leftSymbol.get(),res);
            return(res);
          }
          if(leftValue.isChar()){
            Value res = ValueFactory.createValue(leftValue.asChar() % rightValue.asInt());
            store(leftSymbol.get(), res);
            return(res);
          }
          if(leftValue.isFloat()){
            Value res = ValueFactory.createValue(leftValue.asFloat() % rightValue.asInt());
            store(leftSymbol.get(),res);
            return(res);
          }
          if(leftValue.isDouble()){
            Value res = ValueFactory.createValue(leftValue.asDouble() % rightValue.asInt());
            store(leftSymbol.get(), res);
            return(res);
          }
          break;

        case(PIPEEQUALS):

          if( !( rightValue.isInt() || rightValue.isLong() || rightValue.isChar() || leftValue.isChar() || leftValue.isInt() || leftValue.isLong() ) ){
            Log.error("|= operation is not suitable for these types.");
          }
          if(leftValue.isLong()){
            Value res = ValueFactory.createValue(leftValue.asLong() | rightValue.asInt());
            store(leftSymbol.get(), res);
            return(res);
          }
          if(leftValue.isInt()){
            Value res = ValueFactory.createValue(leftValue.asInt() | rightValue.asInt());
            store(leftSymbol.get(), res);
            return(res);
          }
          if(leftValue.isChar()){
            Value res = ValueFactory.createValue(leftValue.asChar() | rightValue.asInt());
            store(leftSymbol.get(), res);
            return(res);
          }
          break;

        case(PLUSEQUALS):

          if(leftValue.isString()){
            if(rightValue.isString()){
              Value res = ValueFactory.createValue(leftValue.asString() + rightValue.asString());
              store(leftSymbol.get(),res);
              return(res);
            }
            else if(rightValue.isChar()){
              Value res = ValueFactory.createValue(leftValue.asString() + rightValue.asChar());
              store(leftSymbol.get(),res);
              return(res);
            }
            else if(rightValue.isInt()){
              Value res = ValueFactory.createValue(leftValue.asString() + rightValue.asInt());
              store(leftSymbol.get(),res);
              return(res);
            }
            else if(rightValue.isLong()){
              Value res = ValueFactory.createValue(leftValue.asString() + rightValue.asLong());
              store(leftSymbol.get(),res);
              return(res);
            }
            else if(rightValue.isFloat()){
              Value res = ValueFactory.createValue(leftValue.asString() + rightValue.asFloat());
              store(leftSymbol.get(),res);
              return(res);
            }
            else if(rightValue.isDouble()){
              Value res = ValueFactory.createValue(leftValue.asString() + rightValue.asDouble());
              store(leftSymbol.get(),res);
              return(res);
            }
            else if(rightValue.isBoolean()){
              Value res = ValueFactory.createValue(leftValue.asString() + rightValue.asBoolean());
              store(leftSymbol.get(),res);
              return(res);
            }
          }

          if(leftValue.isBoolean() || rightValue.isBoolean() || leftValue.isObject() || rightValue.isObject()){
            Log.error("+= operation is not suitable for these types.");
          }

          if(leftValue.isChar()) {
            Value res = ValueFactory.createValue(leftValue.asChar() + rightValue.asChar());
            store(leftSymbol.get(), res);
            return(res);
          }

          if(leftValue.isInt()){
            Value res = ValueFactory.createValue(leftValue.asInt() + rightValue.asInt());
            store(leftSymbol.get(), res);
            return(res);
          }

          if(leftValue.isLong()){
            Value res = ValueFactory.createValue(leftValue.asLong() + rightValue.asLong());
            store(leftSymbol.get(), res);
            return(res);
          }

          if(leftValue.isFloat()){
            Value res = ValueFactory.createValue(leftValue.asFloat() + rightValue.asFloat());
            store(leftSymbol.get(), res);
            return(res);
          }

          if(leftValue.isDouble()){
            Value res = ValueFactory.createValue(leftValue.asDouble() + rightValue.asDouble());
            store(leftSymbol.get(), res);
            return(res);
          }
          break;

        case(ROOFEQUALS): //XOR

          if( !( rightValue.isInt() || rightValue.isLong() || rightValue.isChar() || leftValue.isChar() || leftValue.isInt() || leftValue.isLong() ) ){
            Log.error("^= operation is not suitable for these types.");
          }
          if(leftValue.isLong()){
            Value res = ValueFactory.createValue(leftValue.asLong() ^ rightValue.asInt());
            store(leftSymbol.get(), res);
            return(res);
          }
          if(leftValue.isInt()){
            Value res = ValueFactory.createValue(leftValue.asInt() ^ rightValue.asInt());
            store(leftSymbol.get(), res);
            return(res);
          }
          if(leftValue.isChar()){
            Value res = ValueFactory.createValue(leftValue.asChar() ^ rightValue.asInt());
            store(leftSymbol.get(), res);
            return(res);
          }
          break;

        case(SLASHEQUALS):

          if(rightValue.isObject() || rightValue.isString() || rightValue.isBoolean() || leftValue.isObject() || leftValue.isString() || rightValue.isBoolean()){
            Log.error("/= operation is not suitable for these types.");
          }
          if(leftValue.isLong()){
            if(rightValue.asInt() == 0){
              Log.error("Division by 0 is not supported.");
            }
            Value res = ValueFactory.createValue(leftValue.asLong() / rightValue.asInt());
            store(leftSymbol.get(), res);
            return(res);
          }
          if(leftValue.isInt()){
            if(rightValue.asInt() == 0){
              Log.error("Division by 0 is not supported.");
            }
            Value res = ValueFactory.createValue(leftValue.asInt() / rightValue.asInt());
            store(leftSymbol.get(),res);
            return(res);
          }
          if(leftValue.isChar()){
            if(rightValue.asInt() == 0){
              Log.error("Division by 0 is not supported.");
            }
            Value res = ValueFactory.createValue(leftValue.asChar() / rightValue.asInt());
            store(leftSymbol.get(), res);
            return(res);
          }
          if(leftValue.isFloat()) {
            Value res = ValueFactory.createValue(leftValue.asFloat() / rightValue.asInt());
            store(leftSymbol.get(), res);
            return (res);
          }
          if(leftValue.isDouble()){
            Value res = ValueFactory.createValue(leftValue.asDouble() / rightValue.asInt());
            store(leftSymbol.get(), res);
            return(res);
          }
          break;

        case(STAREQUALS):

          if(rightValue.isObject() || rightValue.isString() || rightValue.isBoolean() || leftValue.isObject() || leftValue.isString() || rightValue.isBoolean()){
            Log.error("*= operation is not suitable for these types.");
          }
          if(leftValue.isLong()){
            Value res = ValueFactory.createValue(leftValue.asLong() * rightValue.asInt());
            store(leftSymbol.get(), res);
            return(res);
          }
          if(leftValue.isInt()){
            Value res = ValueFactory.createValue(leftValue.asInt() * rightValue.asInt());
            store(leftSymbol.get(), res);
            return(res);
          }
          if(leftValue.isChar()){
            Value res = ValueFactory.createValue(leftValue.asChar() * rightValue.asInt());
            store(leftSymbol.get(), res);
            return(res);
          }
          if(leftValue.isFloat()){
            Value res = ValueFactory.createValue(leftValue.asFloat() * rightValue.asInt());
            store(leftSymbol.get(), res);
            return(res);
          }
          if(leftValue.isDouble()){
            Value res = ValueFactory.createValue(leftValue.asDouble() * rightValue.asInt());
            store(leftSymbol.get(),res);
            return(res);
          }
          break;

        default:
          Log.error("Operator is not defined.");

      }
    }
    return new NotAValue();
  }
}
