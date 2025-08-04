package de.monticore.expressions.uglyexpressions._visitor;

import de.monticore.expressions.uglyexpressions._ast.ASTClassCreator;
import de.monticore.expressions.uglyexpressions._ast.ASTCreatorExpression;
import de.monticore.expressions.uglyexpressions._ast.ASTTypeCastExpression;
import de.monticore.interpreter.ModelInterpreter;
import de.monticore.interpreter.MIValue;
import de.monticore.interpreter.MIValueFactory;
import de.monticore.interpreter.values.ErrorMIValue;
import de.monticore.interpreter.values.VoidMIValue;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symboltable.ISymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

public class UglyExpressionsInterpreter extends UglyExpressionsInterpreterTOP {
  
  public UglyExpressionsInterpreter(ModelInterpreter realThis) {
    super(realThis);
  }
  
  public UglyExpressionsInterpreter() {
    super();
  }
  
  @Override
  public MIValue interpret(ASTTypeCastExpression node) {
    SymTypeExpression afterType = TypeCheck3.symTypeFromAST(node.getMCType());
    SymTypeExpression beforeType = TypeCheck3.typeOf(node.getExpression());
    
    MIValue value = node.getExpression().evaluate(getRealThis());
    
    if (afterType.isGenericType() || beforeType.isGenericType()) {
      String errorMsg = "0x57089 Explicit casts with generic types are not supported yet.";
      Log.error(errorMsg, node.get_SourcePositionStart(), node.get_SourcePositionEnd());
      return new ErrorMIValue(errorMsg);
    }
    
    if (afterType.isPrimitive() && beforeType.isPrimitive()) {
      return convertPrimitive(beforeType.asPrimitive().getPrimitiveName(),
          afterType.asPrimitive().getPrimitiveName(), value);
    }
    
    if (afterType.isObjectType() && beforeType.isObjectType()) {
      Class afterClassType;
      try {
        afterClassType = Class.forName(afterType.printFullName());
      } catch (ClassNotFoundException e) {
        String errorMsg = "0x57089 Failed to load class '" + afterType.printFullName() + "'.";
        Log.error(errorMsg, node.get_SourcePositionStart(), node.get_SourcePositionEnd());
        return new ErrorMIValue(errorMsg);
      }
      
      if (!afterClassType.isInstance(value.asObject())) {
        String errorMsg = "0x57090 Failed to explicitly cast object from '" + value.asObject().getClass().getName()
            + "' to '" + afterType.printFullName() + "'.";
        Log.error(errorMsg, node.get_SourcePositionStart(), node.get_SourcePositionEnd());
        return new ErrorMIValue(errorMsg);
      }
      return value;
    }
    
    String errorMsg = "0x57055 Type Cast operation from " + beforeType.print() + " to " + afterType.print()
        + " is not supported.";
    Log.error(errorMsg, node.get_SourcePositionStart(), node.get_SourcePositionEnd());
    return new ErrorMIValue(errorMsg);
  }
  
  public MIValue convertPrimitive(String fromType, String toType, MIValue value) {
    if (toType.equals(BasicSymbolsMill.BOOLEAN) || fromType.equals(BasicSymbolsMill.BOOLEAN)) {
      String errorMsg = "0x57056 Cast to or from boolean is not supported.";
      Log.error(errorMsg);
      return new ErrorMIValue(errorMsg);
    }
    if (toType.equals(BasicSymbolsMill.BYTE)) {
      if (fromType.equals(BasicSymbolsMill.DOUBLE)) {
        return MIValueFactory.createValue((byte)value.asDouble());
      } else if (fromType.equals(BasicSymbolsMill.FLOAT)) {
        return MIValueFactory.createValue((byte)value.asFloat());
      } else if (fromType.equals(BasicSymbolsMill.LONG)) {
        return MIValueFactory.createValue((byte)value.asLong());
      } else if (fromType.equals(BasicSymbolsMill.INT)) {
        return MIValueFactory.createValue((byte)value.asInt());
      } else if (fromType.equals(BasicSymbolsMill.SHORT)) {
        return MIValueFactory.createValue((byte)value.asShort());
      } else if (fromType.equals(BasicSymbolsMill.CHAR)) {
        return MIValueFactory.createValue((byte)value.asChar());
      }
      return MIValueFactory.createValue(value.asByte());
    } else if (toType.equals(BasicSymbolsMill.SHORT)) {
      if (fromType.equals(BasicSymbolsMill.DOUBLE)) {
        return MIValueFactory.createValue((short)value.asDouble());
      } else if (fromType.equals(BasicSymbolsMill.FLOAT)) {
        return MIValueFactory.createValue((short)value.asFloat());
      } else if (fromType.equals(BasicSymbolsMill.LONG)) {
        return MIValueFactory.createValue((short)value.asLong());
      } else if (fromType.equals(BasicSymbolsMill.INT)) {
        return MIValueFactory.createValue((short)value.asInt());
      } else if (fromType.equals(BasicSymbolsMill.CHAR)) {
        return MIValueFactory.createValue((short)value.asChar());
      }
      return MIValueFactory.createValue(value.asShort());
    } else if (toType.equals(BasicSymbolsMill.CHAR)) {
      if (fromType.equals(BasicSymbolsMill.DOUBLE)) {
        return MIValueFactory.createValue((char)value.asDouble());
      } else if (fromType.equals(BasicSymbolsMill.FLOAT)) {
        return MIValueFactory.createValue((char)value.asFloat());
      } else if (fromType.equals(BasicSymbolsMill.LONG)) {
        return MIValueFactory.createValue((char)value.asLong());
      } else if (fromType.equals(BasicSymbolsMill.INT)) {
        return MIValueFactory.createValue((char)value.asInt());
      } else if (fromType.equals(BasicSymbolsMill.SHORT)) {
        return MIValueFactory.createValue((char)value.asShort());
      } else if (fromType.equals(BasicSymbolsMill.BYTE)) {
        return MIValueFactory.createValue((char)value.asByte());
      }
      return MIValueFactory.createValue(value.asChar());
    } else if (toType.equals(BasicSymbolsMill.INT)) {
      if (fromType.equals(BasicSymbolsMill.DOUBLE)) {
        return MIValueFactory.createValue((int)value.asDouble());
      } else if (fromType.equals(BasicSymbolsMill.FLOAT)) {
        return MIValueFactory.createValue((int)value.asFloat());
      } else if (fromType.equals(BasicSymbolsMill.LONG)) {
        return MIValueFactory.createValue((int)value.asLong());
      }
      return MIValueFactory.createValue(value.asInt());
    } else if (toType.equals(BasicSymbolsMill.LONG)) {
      if (fromType.equals(BasicSymbolsMill.DOUBLE)) {
        return MIValueFactory.createValue((long)value.asDouble());
      } else if (fromType.equals(BasicSymbolsMill.FLOAT)) {
        return MIValueFactory.createValue((long)value.asFloat());
      }
      return MIValueFactory.createValue(value.asLong());
    } else if (toType.equals(BasicSymbolsMill.FLOAT)) {
      if (fromType.equals(BasicSymbolsMill.DOUBLE)) {
        return MIValueFactory.createValue((float)value.asDouble());
      }
      return MIValueFactory.createValue(value.asFloat());
      
    } else if (toType.equals(BasicSymbolsMill.DOUBLE)) {
      return MIValueFactory.createValue(value.asDouble());
    }
    
    String errorMsg = "0x57057 Cast from " + fromType + " to " + toType + " is not supported.";
    Log.error(errorMsg);
    return new ErrorMIValue(errorMsg);
  }
  
  @Override
  public MIValue interpret(ASTCreatorExpression node) {
    return node.getCreator().evaluate(getRealThis());
  }
  
  @Override
  public MIValue interpret(ASTClassCreator node) {
    SymTypeExpression type = TypeCheck3.symTypeFromAST(node.getMCType());
    Optional<ISymbol> optSymbol = type.getSourceInfo().getSourceSymbol();
    if (optSymbol.isEmpty()) {
      String errorMsg = "0x57081 Failed to load Symbol for Class.";
      Log.error(errorMsg, node.get_SourcePositionStart(), node.get_SourcePositionEnd());
      return new ErrorMIValue(errorMsg);
    }
    
    return new VoidMIValue();
  }
}
