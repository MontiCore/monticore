package de.monticore.interpreter.values;

import de.monticore.interpreter.InterpreterUtils;
import de.monticore.interpreter.MIValue;
import de.se_rwth.commons.logging.Log;

import java.lang.reflect.Field;
import java.util.Optional;

public class JavaAttributeMIValue extends WriteableMIValue {

  /**
   * if null, this represents a static attribute;
   * concept taken from Java Reflection API
   */
  Object obj;

  Field attribute;

  Optional<MIValue> innerValue = Optional.empty();

  public JavaAttributeMIValue(Optional<Object> obj, Field attribute) {
    this.obj = obj.orElse(null);
    this.attribute = attribute;
  }

  @Override
  public void write(MIValue value) {
    try {
      attribute.set(obj, InterpreterUtils.valueToObject(value));
      innerValue = Optional.of(value);
    }
    catch (IllegalAccessException e) {
      String errorMsg = "0x57094 Failed to assign value "
          + value.printType() + "(" + value.printValue() + ")"
          + " to  attribute '" + attribute.getName() + "'"
          + " of class '" + attribute.getDeclaringClass().getName() + "'.";
      Log.error(errorMsg);
    }
  }

  @Override
  public MIValue getMIValue() {
    if (!innerValue.isPresent()) {
      try {
        innerValue = Optional.of(
            InterpreterUtils.objectToValue(attribute.get(obj)));
      }
      catch (IllegalAccessException e) {
        String errorMsg = "0x57093 Failed to access attribute '"
            + attribute.getName() + "' of class '"
            + attribute.getDeclaringClass().getName() + "'.";
        Log.error(errorMsg);
        innerValue = Optional.of(new ErrorMIValue(errorMsg));
      }
    }

    return innerValue.get();
  }

  @Override
  public String printType() {
    return "Java-Field";
  }

  @Override
  public String printValue() {
    return getMIValue().printType() + " (" + getMIValue().printValue() + ")";
  }
}
