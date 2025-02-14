package de.monticore.expressions.uglyexpressions._visitor;

import de.monticore.expressions.uglyexpressions._ast.ASTTypeCastExpression;
import de.monticore.interpreter.ModelInterpreter;
import de.monticore.interpreter.Value;
import de.monticore.interpreter.ValueFactory;
import de.monticore.interpreter.values.ErrorValue;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Log;

public class UglyExpressionsInterpreter extends UglyExpressionsInterpreterTOP {
  
  public UglyExpressionsInterpreter(ModelInterpreter realThis) {
    super(realThis);
  }
  
  public UglyExpressionsInterpreter() {
    super();
  }
  
  @Override
  public Value interpret(ASTTypeCastExpression node) {
    SymTypeExpression afterType = TypeCheck3.symTypeFromAST(node.getMCType());
    SymTypeExpression beforeType = TypeCheck3.typeOf(node.getExpression());
    
    Value value = node.getExpression().evaluate(getRealThis());
    
    if (afterType.isPrimitive() && beforeType.isPrimitive()) {
      return convertPrimitive(beforeType.asPrimitive().getPrimitiveName(),
          afterType.asPrimitive().getPrimitiveName(), value);
    }
    
    String errorMsg = "Type Cast operation from " + beforeType.print() + " to " + afterType.print()
        + " is not supported.";
    Log.error(errorMsg);
    return new ErrorValue(errorMsg);
  }
  
  public Value convertPrimitive(String fromType, String toType, Value value) {
    if (toType.equals(BasicSymbolsMill.BOOLEAN) || fromType.equals(BasicSymbolsMill.BOOLEAN)) {
      String errorMsg = "Cast to or from boolean is not supported.";
      Log.error(errorMsg);
      return new ErrorValue(errorMsg);
    }
    if (toType.equals(BasicSymbolsMill.BYTE)) {
      if (fromType.equals(BasicSymbolsMill.DOUBLE)) {
        return ValueFactory.createValue((byte)value.asDouble());
      } else if (fromType.equals(BasicSymbolsMill.FLOAT)) {
        return ValueFactory.createValue((byte)value.asFloat());
      } else if (fromType.equals(BasicSymbolsMill.LONG)) {
        return ValueFactory.createValue((byte)value.asLong());
      } else if (fromType.equals(BasicSymbolsMill.INT)) {
        return ValueFactory.createValue((byte)value.asInt());
      } else if (fromType.equals(BasicSymbolsMill.SHORT)) {
        return ValueFactory.createValue((byte)value.asShort());
      } else if (fromType.equals(BasicSymbolsMill.CHAR)) {
        return ValueFactory.createValue((byte)value.asChar());
      }
      return ValueFactory.createValue(value.asByte());
    } else if (toType.equals(BasicSymbolsMill.SHORT)) {
      if (fromType.equals(BasicSymbolsMill.DOUBLE)) {
        return ValueFactory.createValue((short)value.asDouble());
      } else if (fromType.equals(BasicSymbolsMill.FLOAT)) {
        return ValueFactory.createValue((short)value.asFloat());
      } else if (fromType.equals(BasicSymbolsMill.LONG)) {
        return ValueFactory.createValue((short)value.asLong());
      } else if (fromType.equals(BasicSymbolsMill.INT)) {
        return ValueFactory.createValue((short)value.asInt());
      } else if (fromType.equals(BasicSymbolsMill.CHAR)) {
        return ValueFactory.createValue((short)value.asChar());
      }
      return ValueFactory.createValue(value.asShort());
    } else if (toType.equals(BasicSymbolsMill.CHAR)) {
      if (fromType.equals(BasicSymbolsMill.DOUBLE)) {
        return ValueFactory.createValue((char)value.asDouble());
      } else if (fromType.equals(BasicSymbolsMill.FLOAT)) {
        return ValueFactory.createValue((char)value.asFloat());
      } else if (fromType.equals(BasicSymbolsMill.LONG)) {
        return ValueFactory.createValue((char)value.asLong());
      } else if (fromType.equals(BasicSymbolsMill.INT)) {
        return ValueFactory.createValue((char)value.asInt());
      } else if (fromType.equals(BasicSymbolsMill.SHORT)) {
        return ValueFactory.createValue((char)value.asShort());
      } else if (fromType.equals(BasicSymbolsMill.BYTE)) {
        return ValueFactory.createValue((char)value.asByte());
      }
      return ValueFactory.createValue(value.asChar());
    } else if (toType.equals(BasicSymbolsMill.INT)) {
      if (fromType.equals(BasicSymbolsMill.DOUBLE)) {
        return ValueFactory.createValue((int)value.asDouble());
      } else if (fromType.equals(BasicSymbolsMill.FLOAT)) {
        return ValueFactory.createValue((int)value.asFloat());
      } else if (fromType.equals(BasicSymbolsMill.LONG)) {
        return ValueFactory.createValue((int)value.asLong());
      }
      return ValueFactory.createValue(value.asInt());
    } else if (toType.equals(BasicSymbolsMill.LONG)) {
      if (fromType.equals(BasicSymbolsMill.DOUBLE)) {
        return ValueFactory.createValue((long)value.asDouble());
      } else if (fromType.equals(BasicSymbolsMill.FLOAT)) {
        return ValueFactory.createValue((long)value.asFloat());
      }
      return ValueFactory.createValue(value.asLong());
    } else if (toType.equals(BasicSymbolsMill.FLOAT)) {
      if (fromType.equals(BasicSymbolsMill.DOUBLE)) {
        return ValueFactory.createValue((float)value.asDouble());
      }
      return ValueFactory.createValue(value.asFloat());
      
    } else if (toType.equals(BasicSymbolsMill.DOUBLE)) {
      return ValueFactory.createValue(value.asDouble());
    }
    
    String errorMsg = "Cast from " + fromType + " to " + toType + " is not supported.";
    Log.error(errorMsg);
    return new ErrorValue(errorMsg);
  }
}
