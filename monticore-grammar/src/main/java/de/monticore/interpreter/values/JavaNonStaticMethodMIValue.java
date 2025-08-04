package de.monticore.interpreter.values;

import de.monticore.interpreter.IModelInterpreter;
import de.monticore.interpreter.InterpreterUtils;
import de.monticore.interpreter.MIValue;
import de.monticore.interpreter.ModelInterpreter;
import de.se_rwth.commons.logging.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

// TODO change to getting method by SymType
public class JavaNonStaticMethodMIValue implements FunctionMIValue {
  Object object;
  String methodName;
  
  public JavaNonStaticMethodMIValue(Object object, String methodName) {
    this.object = object;
    this.methodName = methodName;
  }
  
  @Override
  public MIValue execute(IModelInterpreter interpreter, List<MIValue> arguments) {
    List<Class<?>> argumentTypes = arguments.stream()
        .map(InterpreterUtils::typeOfValue)
        .collect(Collectors.toList());
    
    Method method;
    try {
      method = object.getClass().getDeclaredMethod(methodName, argumentTypes.toArray(new Class<?>[0]));
    } catch (NoSuchMethodException e) {
      StringBuilder sb = new StringBuilder();
      sb.append("0x57059 Failed to find method '")
          .append(methodName)
          .append("' in class '")
          .append(object.getClass().getName())
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
      returnObject = method.invoke(object, argumentObjects);
    } catch (IllegalAccessException e) {
      String errorMsg = e.getMessage();
      Log.error(errorMsg);
      return new ErrorMIValue(errorMsg);
    } catch (InvocationTargetException e) {
      String errorMsg = e.getMessage();
      Log.error(errorMsg);
      return new ErrorMIValue(errorMsg);
    }
    
    return InterpreterUtils.objectToValue(returnObject);
  }
  
  @Override
  public String printType() {
    return "Java-Method";
  }
  
  @Override
  public String printValue() {
    return object.getClass().getName() + "." + methodName;
  }
}
