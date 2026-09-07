// (c) https://github.com/MontiCore/monticore
package de.monticore.interpreter.values;

import com.google.common.base.Preconditions;
import de.monticore.values.MCValue;
import de.monticore.values.MCValueFactory;
import de.monticore.values.MCValueFunction;
import de.monticore.values.MCValueVoid;

import java.lang.invoke.MethodHandle;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MCValueFunctionOfMethodHandle implements MCValueFunction {

  // non-wrapped method
  protected final MethodHandle methodHandle;
  // wrapped method that has specific arity
  protected final Map<Integer, MethodHandle> spreaderByArity;

  public MCValueFunctionOfMethodHandle(MethodHandle methodHandle) {
    this.methodHandle = methodHandle;
    if (!methodHandle.isVarargsCollector()) {
      int arity = methodHandle.type().parameterCount();
      MethodHandle spreader = methodHandle.asSpreader(Object[].class, arity);
      spreaderByArity = Collections.singletonMap(arity, spreader);
    }
    else {
      spreaderByArity = new HashMap<>();
    }
  }

  /**
   * binds a {@code this} object to the MethodHandle
   * to create a function that already contains the reference to the object.
   * Only applicable to non-static methods.
   *
   * @param thisPtr the {@code this} object to be bound
   * @return a new function bound to the {@code this} object
   */
  public MCValueFunctionOfMethodHandle withBoundThisPtr(Object thisPtr) {
    return new MCValueFunctionOfMethodHandle(methodHandle.bindTo(thisPtr));
  }

  @Override
  public MCValue execute(
      MCValue[] arguments
  ) {
    Object[] nativeArguments = new Object[arguments.length];
    Class<?>[] parameterTypes =
        methodHandle.type().parameterArray();
    for (int i = 0; i < arguments.length; i++) {
      MCValue arg = arguments[i];
      nativeArguments[i] = adaptArgument(
          arg.asNativeObject(),
          parameterTypes[i]
      );
    }

    Object result;
    try {
      result = getSpreader(nativeArguments.length).invoke(nativeArguments);
    }
    catch (Throwable t) {
      throw new IllegalStateException(t);
    }
    if (methodHandle.type().returnType().equals(void.class)) {
      return MCValueVoid.INSTANCE;
    }
    return MCValueFactory.createMIValueOfNativeObject(result);
  }

  @Override
  public String printType() {
    return "MethodHandle(" + methodHandle.type().toString() + ")";
  }

  // helper

  protected static Object adaptArgument(
      Object value,
      Class<?> targetType
  ) {
    if (value == null) {
      return null;
    }

    if (targetType == boolean.class || targetType == Boolean.class) {
      return value;
    }

    if (targetType == char.class || targetType == Character.class) {
      if (value instanceof Character) {
        return value;
      }
      return (char) ((Number) value).intValue();
    }

    if (value instanceof Number number) {
      if (targetType == byte.class || targetType == Byte.class) {
        return number.byteValue();
      }
      if (targetType == short.class || targetType == Short.class) {
        return number.shortValue();
      }
      if (targetType == int.class || targetType == Integer.class) {
        return number.intValue();
      }
      if (targetType == long.class || targetType == Long.class) {
        return number.longValue();
      }
      if (targetType == float.class || targetType == Float.class) {
        return number.floatValue();
      }
      if (targetType == double.class || targetType == Double.class) {
        return number.doubleValue();
      }
    }

    return targetType.cast(value);
  }

  protected MethodHandle getSpreader(int arity) {
    MethodHandle cached = spreaderByArity.get(arity);
    if (cached != null) {
      return cached;
    }

    Preconditions.checkState(methodHandle.isVarargsCollector());
    int minVarArgCallArity = methodHandle.type().parameterCount() - 1;
    Preconditions.checkState(arity >= minVarArgCallArity);
    int collectedVarArgCount = arity - minVarArgCallArity;
    Class<?> varArgArrayType = methodHandle.type().lastParameterType();
    MethodHandle fixedArity = methodHandle.asFixedArity()
        .asCollector(varArgArrayType, collectedVarArgCount);
    MethodHandle spreader = fixedArity.asSpreader(Object[].class, arity);
    spreaderByArity.put(arity, spreader);
    return spreader;
  }

}
