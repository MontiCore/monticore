// (c) https://github.com/MontiCore/monticore
package de.monticore.interpreter.values;

import com.google.common.base.Preconditions;
import de.monticore.interpreter.util.SymTypeExpression2JavaClassVisitor;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static de.monticore.interpreter.util.TypeSymbolNativityChecker.isNativeJavaFunction;
import static de.monticore.types3.util.TypeContextCalculator.getEnclosingType;

public class MIValueFunctionOfMethodHandle implements MIValueFunction {

  // non-wrapped method
  protected final MethodHandle methodHandle;
  // wrapped method that has specific arity
  protected final Map<Integer, MethodHandle> spreaderByArity;

  public MIValueFunctionOfMethodHandle(MethodSymbol methodSym) {
    this(getHandleOfSymbol(methodSym));
  }

  public MIValueFunctionOfMethodHandle(MethodHandle methodHandle) {
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

  public MIValueFunctionOfMethodHandle bindThisPtr(Object thisPtr) {
    return new MIValueFunctionOfMethodHandle(methodHandle.bindTo(thisPtr));
  }

  @Override
  public MIValue execute(
      MIValue[] arguments
  ) {
    Object[] nativeArguments = new Object[arguments.length];
    Class<?>[] parameterTypes =
        methodHandle.type().parameterArray();
    for (int i = 0; i < arguments.length; i++) {
      MIValue arg = arguments[i];
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
      return new MIValueVoid();
    }
    return MIValueFactory.createMIValueOfNativeObject(result);
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

  // only for constructor
  protected static MethodHandle getHandleOfSymbol(MethodSymbol methodSym) {
    Preconditions.checkNotNull(methodSym);
    Preconditions.checkState(isNativeJavaFunction(methodSym));
    TypeSymbol surroundingType =
        getEnclosingType(methodSym.getEnclosingScope()).get();
    SymTypeExpression2JavaClassVisitor type2JavaVisitor =
        new SymTypeExpression2JavaClassVisitor();
    Class<?> clazz = type2JavaVisitor.calculate(surroundingType).get();

    MethodHandles.Lookup lookup = MethodHandles.publicLookup();
    MethodType methodType = type2JavaVisitor.calculate(
        methodSym.getFunctionType().getType(),
        methodSym.getFunctionType().getArgumentTypeList()
    );
    if (methodSym.isIsConstructor()) {
      // Constructors "return void" for some reason
      methodType = methodType.changeReturnType(void.class);
    }

    MethodHandle methodHandle;
    try {
      if (methodSym.isIsConstructor()) {
        methodHandle = lookup.findConstructor(clazz, methodType);
      }
      else if (methodSym.isIsStatic()) {
        methodHandle =
            lookup.findStatic(clazz, methodSym.getName(), methodType);
      }
      else {
        methodHandle =
            lookup.findVirtual(clazz, methodSym.getName(), methodType);
      }
    }
    catch (NoSuchMethodException | IllegalAccessException e) {
      throw new IllegalArgumentException(e);
    }
    return methodHandle;
  }

}
