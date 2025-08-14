package de.monticore.interpreter.values;

import de.monticore.interpreter.IModelInterpreter;
import de.monticore.interpreter.InterpreterUtils;
import de.monticore.interpreter.MIValue;
import de.se_rwth.commons.logging.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

// TODO change to getting method by SymType
public class JavaStaticMethodMIValue implements FunctionMIValue {

  Class<?> classType;
  String functionName;

  public JavaStaticMethodMIValue(Class<?> classType, String methodName) {
    this.classType = classType;
    this.functionName = methodName;
  }

  @Override
  public MIValue execute(IModelInterpreter interpreter, List<MIValue> arguments) {
    List<Class<?>> argumentTypes = arguments.stream()
        .map(InterpreterUtils::typeOfValue)
        .collect(Collectors.toList());

    Method function;
    try {
      function = classType.getDeclaredMethod(functionName, argumentTypes.toArray(new Class<?>[0]));
    }
    catch (NoSuchMethodException e) {
      StringBuilder sb = new StringBuilder();
      sb.append("0x57058 Failed to find static function '")
          .append(functionName)
          .append("' in class '")
          .append(classType.getName())
          .append("' with arguments of type (");
      for (int i = 0; i < argumentTypes.size(); i++) {
        sb.append(argumentTypes.get(i).toString());
        if (i < argumentTypes.size() - 1) {
          sb.append(", ");
        }
      }
      sb.append(").");
      Log.error(sb.toString());
      return new ErrorMIValue(sb.toString());
    }

    Object[] argumentObjects = arguments.stream().map(InterpreterUtils::valueToObject).toArray();

    Object returnObject;
    try {
      returnObject = function.invoke(null, argumentObjects);
    }
    catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
    catch (InvocationTargetException e) {
      String errorMsg = e.getMessage();
      Log.error(errorMsg);
      return new ErrorMIValue(errorMsg);
    }

    return InterpreterUtils.objectToValue(returnObject);
  }

  @Override
  public String printType() {
    return "Java-Function";
  }

  @Override
  public String printValue() {
    return classType.getName() + "." + functionName;
  }
}
