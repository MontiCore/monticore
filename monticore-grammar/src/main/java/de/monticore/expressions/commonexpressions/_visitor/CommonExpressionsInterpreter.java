/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.commonexpressions._visitor;

import de.monticore.expressions.commonexpressions._ast.*;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpression;
import de.monticore.interpreter.InterpreterUtils;
import de.monticore.interpreter.ModelInterpreter;
import de.monticore.interpreter.MIValue;
import de.monticore.interpreter.values.*;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symboltable.ISymbol;
import de.monticore.symboltable.modifiers.StaticAccessModifier;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypePrimitive;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static de.monticore.interpreter.MIValueFactory.createValue;

public class CommonExpressionsInterpreter extends CommonExpressionsInterpreterTOP {

  public CommonExpressionsInterpreter() {
    super();
  }

  public CommonExpressionsInterpreter(ModelInterpreter realThis) {
    super(realThis);
  }
  
  public SymTypePrimitive getCompatibleType(SymTypePrimitive type1, SymTypePrimitive type2) {
    return SymTypeRelations.isCompatible(type1, type2)
      ? type1
      : (
        SymTypeRelations.isCompatible(type2, type1)
          ? type2
          : null
    );
  }

  /**
   * Checks whether the left and right value are equal.
   */
  public MIValue isEqual(SymTypePrimitive leftType, MIValue left, SymTypePrimitive rightType, MIValue right) {
    SymTypePrimitive compatibleType = getCompatibleType(leftType, rightType);
    if (compatibleType == null) {
      String errorMsg = "0x57000 Equality operation with operands ot type '" + leftType.print()
          + "' and '" + rightType.print() + "' is not supported.";
      Log.error(errorMsg);
      return new ErrorMIValue(errorMsg);
    }
    
    String primitive = compatibleType.getPrimitiveName();
    if (primitive.equals(BasicSymbolsMill.BOOLEAN)) {
      return createValue(left.asBoolean() == right.asBoolean());
    } else if (primitive.equals(BasicSymbolsMill.BYTE)) {
      return createValue(left.asByte() == right.asByte());
    } else if (primitive.equals(BasicSymbolsMill.SHORT)) {
      return createValue(left.asShort() == right.asShort());
    } else if (primitive.equals(BasicSymbolsMill.CHAR)) {
      return createValue(left.asChar() == right.asChar());
    } else if (primitive.equals(BasicSymbolsMill.INT)) {
      return createValue(left.asInt() == right.asInt());
    } else if (primitive.equals(BasicSymbolsMill.LONG)) {
      return createValue(left.asLong() == right.asLong());
    } else if (primitive.equals(BasicSymbolsMill.FLOAT)) {
      return createValue(left.asFloat() == right.asFloat());
    } else if (primitive.equals(BasicSymbolsMill.DOUBLE)) {
      return createValue(left.asDouble() == right.asDouble());
    }
    
    String errorMsg = "0x57001 Equality operator with operands of type '" + leftType.print()
        + "' and '" + rightType.print() + "' is not implemented.";
    Log.error(errorMsg);
    return new ErrorMIValue(errorMsg);
  }

  /**
   * Subtracts the right value from the left value while minding the types
   */
  public MIValue subtract(SymTypePrimitive leftType, MIValue left, SymTypePrimitive rightType, MIValue right) {
    SymTypePrimitive compatibleType = getCompatibleType(leftType, rightType);
    if (compatibleType == null) {
      String errorMsg = "0x57002 Greater or Lesser operation with operands ot type '" + leftType.print()
          + "' and '" + rightType.print() + "' is not supported.";
      Log.error(errorMsg);
      return new ErrorMIValue(errorMsg);
    }
    
    String primitive = compatibleType.getPrimitiveName();
    if (primitive.equals(BasicSymbolsMill.BYTE)) {
      return createValue(left.asByte() - right.asByte());
    } else if (primitive.equals(BasicSymbolsMill.SHORT)) {
      return createValue(left.asShort() - right.asShort());
    } else if (primitive.equals(BasicSymbolsMill.CHAR)) {
      return createValue(left.asChar() - right.asChar());
    } else if (primitive.equals(BasicSymbolsMill.INT)) {
      return createValue(left.asInt() - right.asInt());
    } else if (primitive.equals(BasicSymbolsMill.LONG)) {
      return createValue(left.asLong() - right.asLong());
    } else if (primitive.equals(BasicSymbolsMill.FLOAT)) {
      return createValue(left.asFloat() - right.asFloat());
    } else if (primitive.equals(BasicSymbolsMill.DOUBLE)) {
      return createValue(left.asDouble() - right.asDouble());
    }
    
    String errorMsg = "0x57003 Greater or Lesser operation with operands of type '" + leftType.print()
        + "' and '" + rightType.print() + "' is not supported.";
    Log.error(errorMsg);
    return new ErrorMIValue(errorMsg);
  }

  @Override
  public MIValue interpret(ASTPlusExpression node) {
    MIValue left = node.getLeft().evaluate(getRealThis());
    MIValue right = node.getRight().evaluate(getRealThis());
    if (left.isFlowControlSignal()) return left;
    if (right.isFlowControlSignal()) return right;
    
    SymTypeExpression type = TypeCheck3.typeOf(node);
    return InterpreterUtils.calcOp(left, right, type, Integer::sum, Long::sum, Float::sum, Double::sum, "0x57037 Plus");
  }

  @Override
  public MIValue interpret(ASTMinusExpression node) {
    MIValue left = node.getLeft().evaluate(getRealThis());
    MIValue right = node.getRight().evaluate(getRealThis());
    if (left.isFlowControlSignal()) return left;
    if (right.isFlowControlSignal()) return right;
    
    SymTypeExpression type = TypeCheck3.typeOf(node);
    return InterpreterUtils.calcOp(left, right, type, (a, b) -> a - b, (a, b) -> a - b,
        (a, b) -> a - b, (a, b) -> a - b, "0x57038 Minus");
  }

  @Override
  public MIValue interpret(ASTMultExpression node) {
    MIValue left = node.getLeft().evaluate(getRealThis());
    MIValue right = node.getRight().evaluate(getRealThis());
    if (left.isFlowControlSignal()) return left;
    if (right.isFlowControlSignal()) return right;
    
    SymTypeExpression type = TypeCheck3.typeOf(node);
    return InterpreterUtils.calcOp(left, right, type, (a, b) -> a * b, (a, b) -> a * b,
        (a, b) -> a * b, (a, b) -> a * b, "0x57039 Multiplication");
  }

  @Override
  public MIValue interpret(ASTDivideExpression node) {
    MIValue left = node.getLeft().evaluate(getRealThis());
    MIValue right = node.getRight().evaluate(getRealThis());
    if (left.isFlowControlSignal()) return left;
    if (right.isFlowControlSignal()) return right;
    
    SymTypeExpression type = TypeCheck3.typeOf(node);
    if (type.isPrimitive()) {
      String resultPrimitive = type.asPrimitive().getPrimitiveName();
      
      if (right.asDouble() == 0.0) {
        String errorMsg = "0x57004 Division by zero is undefined";
        Log.error(errorMsg, node.getRight().get_SourcePositionStart(), node.getRight().get_SourcePositionEnd());
        return new ErrorMIValue(errorMsg);
      }
      
      return InterpreterUtils.calcOpPrimitive(left, right, resultPrimitive,
          (a, b) -> a / b, (a, b) -> a / b, (a, b) -> a / b, (a, b) -> a / b,
          "0x57040 Division");
    }
    
    String errorMsg = "0x57005 Division operation with result of type '" + type.print() + "' is not supported.";
    Log.error(errorMsg, node.get_SourcePositionStart(), node.get_SourcePositionEnd());
    return new ErrorMIValue(errorMsg);
  }

  @Override
  public MIValue interpret(ASTModuloExpression node) {
    MIValue left = node.getLeft().evaluate(getRealThis());
    MIValue right = node.getRight().evaluate(getRealThis());
    if (left.isFlowControlSignal()) return left;
    if (right.isFlowControlSignal()) return right;
    
    SymTypeExpression type = TypeCheck3.typeOf(node);
    return InterpreterUtils.calcOp(left, right, type, (a, b) -> a % b, (a, b) -> a % b,
        (a, b) -> a % b, (a, b) -> a % b, "0x57041 Modulo");
  }

  @Override
  public MIValue interpret(ASTMinusPrefixExpression node) {
    MIValue value = node.getExpression().evaluate(getRealThis());
    if (value.isFlowControlSignal()) return value;
    
    SymTypeExpression type = TypeCheck3.typeOf(node);
    if (type.isPrimitive()) {
      String primitive = type.asPrimitive().getPrimitiveName();
      
      if (primitive.equals(BasicSymbolsMill.BYTE)) {
        return createValue(-value.asByte());
      } else if (primitive.equals(BasicSymbolsMill.SHORT)) {
        return createValue(-value.asShort());
      } else if (primitive.equals(BasicSymbolsMill.CHAR)) {
        return createValue(-value.asChar());
      } else if (primitive.equals(BasicSymbolsMill.INT)) {
        return createValue(-value.asInt());
      } else if (primitive.equals(BasicSymbolsMill.LONG)) {
        return createValue(-value.asLong());
      } else if (primitive.equals(BasicSymbolsMill.FLOAT)) {
        return createValue(-value.asFloat());
      } else if (primitive.equals(BasicSymbolsMill.DOUBLE)) {
        return createValue(-value.asDouble());
      }
    }
    
    String errorMsg = "0x57006 Minus Prefix operation with result of type '" + type.print() + "' is not supported.";
    Log.error(errorMsg, node.get_SourcePositionStart(), node.get_SourcePositionEnd());
    return new ErrorMIValue(errorMsg);
  }

  @Override
  public MIValue interpret(ASTPlusPrefixExpression node) {
    MIValue value = node.getExpression().evaluate(getRealThis());
    if (value.isFlowControlSignal()) return value;
    
    SymTypeExpression type = TypeCheck3.typeOf(node);
    if (type.isPrimitive() && (type.asPrimitive().isNumericType() || type.asPrimitive().isIntegralType())) {
      return value;
    }
    
    String errorMsg = "0x57007 Minus Prefix operation with result of type '" + type.print() + "' is not supported.";
    Log.error(errorMsg, node.get_SourcePositionStart(), node.get_SourcePositionEnd());
    return new ErrorMIValue(errorMsg);
  }

  @Override
  public MIValue interpret(ASTEqualsExpression node) {
    MIValue left = node.getLeft().evaluate(getRealThis());
    MIValue right = node.getRight().evaluate(getRealThis());
    if (left.isFlowControlSignal()) return left;
    if (right.isFlowControlSignal()) return right;
    
    SymTypeExpression leftType = TypeCheck3.typeOf(node.getLeft());
    SymTypeExpression rightType = TypeCheck3.typeOf(node.getRight());
    if (leftType.isPrimitive() && rightType.isPrimitive()) {
      return isEqual(leftType.asPrimitive(), left, rightType.asPrimitive(), right);
    }
    
    String errorMsg = "0x57008 Equality operation with operands of type '" + leftType.print()
        + "' and '" + rightType.print() + "' is not supported.";
    Log.error(errorMsg, node.get_SourcePositionStart(), node.get_SourcePositionEnd());
    return new ErrorMIValue(errorMsg);
  }

  @Override
  public MIValue interpret(ASTNotEqualsExpression node) {
    MIValue left = node.getLeft().evaluate(getRealThis());
    MIValue right = node.getRight().evaluate(getRealThis());
    if (left.isFlowControlSignal()) return left;
    if (right.isFlowControlSignal()) return right;
    
    SymTypeExpression leftType = TypeCheck3.typeOf(node.getLeft());
    SymTypeExpression rightType = TypeCheck3.typeOf(node.getRight());
    if (leftType.isPrimitive() && rightType.isPrimitive()) {
      MIValue result = isEqual(leftType.asPrimitive(), left, rightType.asPrimitive(), right);
      if (result.isFlowControlSignal()) return result;
      
      return createValue(!result.asBoolean());
    }
    
    String errorMsg = "0x57009 Inequality operation with operands of type '" + leftType.print()
        + "' and '" + rightType.print() + "' is not supported.";
    Log.error(errorMsg, node.get_SourcePositionStart(), node.get_SourcePositionEnd());
    return new ErrorMIValue(errorMsg);
  }

  @Override
  public MIValue interpret(ASTGreaterThanExpression node) {
    MIValue left = node.getLeft().evaluate(getRealThis());
    MIValue right = node.getRight().evaluate(getRealThis());
    if (left.isFlowControlSignal()) return left;
    if (right.isFlowControlSignal()) return right;
    
    SymTypeExpression leftType = TypeCheck3.typeOf(node.getLeft());
    SymTypeExpression rightType = TypeCheck3.typeOf(node.getRight());
    if (leftType.isPrimitive() && rightType.isPrimitive()) {
      MIValue result = subtract(leftType.asPrimitive(), left, rightType.asPrimitive(), right);
      if (result.isFlowControlSignal()) return result;
      
      return createValue(result.asDouble() > 0.0);
    }
    
    String errorMsg = "0x57010 Greater than operation with operands of type '" + leftType.print()
        + "' and '" + rightType.print() + "' is not supported.";
    Log.error(errorMsg, node.get_SourcePositionStart(), node.get_SourcePositionEnd());
    return new ErrorMIValue(errorMsg);
  }

  @Override
  public MIValue interpret(ASTLessThanExpression node) {
    MIValue left = node.getLeft().evaluate(getRealThis());
    MIValue right = node.getRight().evaluate(getRealThis());
    if (left.isFlowControlSignal()) return left;
    if (right.isFlowControlSignal()) return right;
    
    SymTypeExpression leftType = TypeCheck3.typeOf(node.getLeft());
    SymTypeExpression rightType = TypeCheck3.typeOf(node.getRight());
    if (leftType.isPrimitive() && rightType.isPrimitive()) {
      MIValue result = subtract(leftType.asPrimitive(), left, rightType.asPrimitive(), right);
      if (result.isFlowControlSignal()) return result;
      
      return createValue(result.asDouble() < 0.0);
    }
    
    String errorMsg = "0x57011 Less than operation with operands of type '" + leftType.print()
        + "' and '" + rightType.print() + "' is not supported.";
    Log.error(errorMsg, node.get_SourcePositionStart(), node.get_SourcePositionEnd());
    return new ErrorMIValue(errorMsg);
  }

  @Override
  public MIValue interpret(ASTGreaterEqualExpression node) {
    MIValue left = node.getLeft().evaluate(getRealThis());
    MIValue right = node.getRight().evaluate(getRealThis());
    if (left.isFlowControlSignal()) return left;
    if (right.isFlowControlSignal()) return right;
    
    SymTypeExpression leftType = TypeCheck3.typeOf(node.getLeft());
    SymTypeExpression rightType = TypeCheck3.typeOf(node.getRight());
    if (leftType.isPrimitive() && rightType.isPrimitive()) {
      MIValue result = subtract(leftType.asPrimitive(), left, rightType.asPrimitive(), right);
      if (result.isFlowControlSignal()) return result;
      
      return createValue(result.asDouble() >= 0.0);
    }
    
    String errorMsg = "0x57012Greater equal operation with operands of type '" + leftType.print()
        + "' and '" + rightType.print() + "' is not supported.";
    Log.error(errorMsg, node.get_SourcePositionStart(), node.get_SourcePositionEnd());
    return new ErrorMIValue(errorMsg);
  }

  @Override
  public MIValue interpret(ASTLessEqualExpression node) {
    MIValue left = node.getLeft().evaluate(getRealThis());
    MIValue right = node.getRight().evaluate(getRealThis());
    if (left.isFlowControlSignal()) return left;
    if (right.isFlowControlSignal()) return right;
    
    SymTypeExpression leftType = TypeCheck3.typeOf(node.getLeft());
    SymTypeExpression rightType = TypeCheck3.typeOf(node.getRight());
    if (leftType.isPrimitive() && rightType.isPrimitive()) {
      MIValue result = subtract(leftType.asPrimitive(), left, rightType.asPrimitive(), right);
      if (result.isFlowControlSignal()) return result;
      
      return createValue(result.asDouble() <= 0.0);
    }
    
    String errorMsg = "0x57013 Less equal operation with operands of type '" + leftType.print()
        + "' and '" + rightType.print() + "' is not supported.";
    Log.error(errorMsg, node.get_SourcePositionStart(), node.get_SourcePositionEnd());
    return new ErrorMIValue(errorMsg);
  }

  //~ -> behaves as a bitwise complement
  @Override
  public MIValue interpret(ASTBooleanNotExpression node) {
    MIValue value = node.getExpression().evaluate(getRealThis());
    if (value.isFlowControlSignal()) return value;

    SymTypeExpression type = TypeCheck3.typeOf(node.getExpression());
    if (type.isPrimitive() && type.asPrimitive().isIntegralType()) {
      String primitive = type.asPrimitive().getPrimitiveName();
      if (primitive.equals(BasicSymbolsMill.BYTE)) {
        return createValue(~value.asByte());
      } else if (primitive.equals(BasicSymbolsMill.SHORT)) {
        return createValue(~value.asShort());
      } else if (primitive.equals(BasicSymbolsMill.CHAR)) {
        return createValue(~value.asChar());
      } else if (primitive.equals(BasicSymbolsMill.INT)) {
        return createValue(~value.asInt());
      } else if (primitive.equals(BasicSymbolsMill.LONG)) {
        return createValue(~value.asLong());
      }
    }
    
    String errorMsg = "0x57014 Bitwise Not operation with operand of type '" + type.print() + "' is not supported.";
    Log.error(errorMsg, node.get_SourcePositionStart(), node.get_SourcePositionEnd());
    return new ErrorMIValue(errorMsg);
  }

  /*=================================================================*/
  //Logical and boolean operations
  /*=================================================================*/

  @Override
  public MIValue interpret(ASTLogicalNotExpression node) {
    MIValue value = node.getExpression().evaluate(getRealThis());
    if (value.isFlowControlSignal()) return value;
    
    SymTypeExpression type = TypeCheck3.typeOf(node.getExpression());
    
    if (value.isBoolean()) {
      return createValue(!value.asBoolean());
    }
    
    String errorMsg = "0x57015 Logical Not operation with operand of type '" + type.print() + "' is not supported.";
    Log.error(errorMsg, node.get_SourcePositionStart(), node.get_SourcePositionEnd());
    return new ErrorMIValue(errorMsg);
  }

  @Override
  public MIValue interpret(ASTBooleanAndOpExpression node) {
    MIValue left = node.getLeft().evaluate(getRealThis());
    MIValue right = node.getRight().evaluate(getRealThis());
    if (left.isFlowControlSignal()) return left;
    if (right.isFlowControlSignal()) return right;
    
    SymTypeExpression leftType = TypeCheck3.typeOf(node.getLeft());
    SymTypeExpression rightType = TypeCheck3.typeOf(node.getRight());

    if (left.isBoolean() && right.isBoolean()) {
      return createValue(left.asBoolean() && right.asBoolean());
    }
    
    String errorMsg = "0x57016 Logical And operation with operands of type '" + leftType.print()
        + "' and '" + rightType.print() + "' is not supported.";
    Log.error(errorMsg, node.get_SourcePositionStart(), node.get_SourcePositionEnd());
    return new ErrorMIValue(errorMsg);
  }

  @Override
  public MIValue interpret(ASTBooleanOrOpExpression node) {
    MIValue left = node.getLeft().evaluate(getRealThis());
    MIValue right = node.getRight().evaluate(getRealThis());
    if (left.isFlowControlSignal()) return left;
    if (right.isFlowControlSignal()) return right;
    
    SymTypeExpression leftType = TypeCheck3.typeOf(node.getLeft());
    SymTypeExpression rightType = TypeCheck3.typeOf(node.getRight());
    
    if (left.isBoolean() && right.isBoolean()) {
      return createValue(left.asBoolean() || right.asBoolean());
    }
    
    String errorMsg = "0x57017 Logical Or operation with operands of type '" + leftType.print()
        + "' and '" + rightType.print() + "' is not supported.";
    Log.error(errorMsg, node.get_SourcePositionStart(), node.get_SourcePositionEnd());
    return new ErrorMIValue(errorMsg);
  }

  @Override
  public MIValue interpret(ASTBracketExpression node) {
    return node.getExpression().evaluate(getRealThis());
  }

  @Override
  public MIValue interpret(ASTConditionalExpression node) {
    MIValue condition = node.getCondition().evaluate(getRealThis());
    if (condition.isFlowControlSignal()) return condition;
    
    return condition.asBoolean()
        ? node.getTrueExpression().evaluate(getRealThis())
        : node.getFalseExpression().evaluate(getRealThis());
  }

  @Override
  public MIValue interpret(ASTFieldAccessExpression node) {
    SymTypeExpression type = TypeCheck3.typeOf(node);
    Optional<ISymbol> symbolOptional = type.getSourceInfo().getSourceSymbol();
    // TODO Definition of Classes with Attributes/Methods in Model
    if (symbolOptional.isEmpty()) {
      String errorMsg = "0x57018 Field Access operation expected a symbol as source.";
      Log.error(errorMsg, node.get_SourcePositionStart(), node.get_SourcePositionEnd());
      return new ErrorMIValue(errorMsg);
    } else {
      // Java Method/Attribute
      ISymbol symbol = symbolOptional.get();
      if (symbol.getAccessModifier().getDimensionToModifierMap()
              .getOrDefault(StaticAccessModifier.DIMENSION,
                  StaticAccessModifier.NON_STATIC) == StaticAccessModifier.STATIC) {
        // static

        // get Java-Class from symbol
        String fieldName = symbol.getName();
        String fullName = symbol.getFullName();
        String className = fullName.substring(0, (fullName.length()
                - fieldName.length() - 1));
        Class classType;
        try {
          classType = Class.forName(className);
        } catch (ClassNotFoundException e) {
          String errorMsg = "0x57018 Failed to load class '" + className + "'.";
          Log.error(errorMsg, node.get_SourcePositionStart(), node.get_SourcePositionEnd());
          return new ErrorMIValue(errorMsg);
        }

        if (type.isFunctionType()) {
          // static method
          return new JavaStaticMethodMIValue(classType, fieldName);
        } else {
          // static attribute
          return InterpreterUtils.getStaticObjectAttribute(classType, fieldName);
        }
        
      } else {
        // non-static
        MIValue leftValue = node.getExpression().evaluate(getRealThis());
        if (!leftValue.isObject()) {
          String errorMsg = "0x57019 The Field Access operation expected an object as left side.";
          Log.error(errorMsg, node.getExpression().get_SourcePositionStart(), node.getExpression().get_SourcePositionEnd());
          return new ErrorMIValue(errorMsg);
        }
        
        // If class-declarations are supported this needs to be expanded
        if (type.isFunctionType()) {
          // non-static method
          FunctionSymbol funcSymbol = (FunctionSymbol)symbol;
          String name = funcSymbol.getName();
          return new JavaNonStaticMethodMIValue(leftValue.asObject(), name);
        } else {
          // non-static attribute
          return InterpreterUtils.getNonStaticObjectAttribute((ObjectMIValue)leftValue, node.getName());
        }
      }
    }
  }

  @Override
  public MIValue interpret(ASTLiteralExpression node) {
    return node.getLiteral().evaluate(getRealThis());
  }
  
  @Override
  public MIValue interpret(ASTCallExpression node) {
    // evaluate expression that gives lambda/function
    // get original parent scope of lambda/function declaration
    // create Scope with parent and arguments
    // evaluate arguments in current scope & put into new scope
    MIValue value = node.getExpression().evaluate(getRealThis());
    if (!value.isFunction()) {
      String errorMsg = "0x57021 Call expression expected a function but got " + TypeCheck3.typeOf(node.getExpression()).print() + ".";
      Log.error(errorMsg, node.getExpression().get_SourcePositionStart(), node.getExpression().get_SourcePositionEnd());
      return new ErrorMIValue(errorMsg);
    }
    
    List<MIValue> args = new ArrayList<>();
    for (ASTExpression argument : node.getArguments().getExpressionList()) {
      args.add(argument.evaluate(getRealThis()));
    }
    
    // cast needed in case of subtyping
    SymTypeExpression returnType = TypeCheck3.typeOf(node);
    MIValue returnValue = value.asFunction().execute(getRealThis(), args);
    return InterpreterUtils.convertImplicit(returnType, returnValue);
  }
  
  
}