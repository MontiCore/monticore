// (c) https://github.com/MontiCore/monticore
package de.monticore.interpreter.util;

import com.google.common.base.Preconditions;
import de.monticore.interpreter.calculations.MICalculation;
import de.monticore.interpreter.calculations.MICalculationBoolean;
import de.monticore.interpreter.calculations.MICalculationDouble;
import de.monticore.interpreter.calculations.MICalculationInt;
import de.monticore.interpreter.calculations.MICalculationValue;
import de.monticore.interpreter.frames.MIFrame;
import de.monticore.interpreter.setters.MISetter;
import de.monticore.interpreter.setters.MISetterBoolean;
import de.monticore.interpreter.setters.MISetterDouble;
import de.monticore.interpreter.setters.MISetterInt;
import de.monticore.interpreter.values.MCValueFunctionOfMethodHandle;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.basicsymbols.interpreter.frames.MIFrameLayoutForBasicSymbols;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.symboltable.ISymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.values.MCValueFactory;
import de.monticore.values.MCValueFunction;
import org.apache.commons.lang3.NotImplementedException;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static de.monticore.interpreter.util.NativeStorageSelector.switchByFormat;
import static de.monticore.interpreter.util.TypeSymbolNativityChecker.getNativeJavaClass;
import static de.monticore.interpreter.util.TypeSymbolNativityChecker.isNativeJavaFunction;
import static de.monticore.interpreter.util.TypeSymbolNativityChecker.isNativeJavaType;
import static de.monticore.interpreter.util.TypeSymbolNativityChecker.isNativeJavaVariable;
import static de.monticore.types3.util.TypeContextCalculator.getEnclosingType;

/**
 * Handles access of methods/fields of objects, including static elements.
 * <p>
 * Class2MC-Symbols are supported directly,
 * for other symbols, this needs to be extended
 * with generator specific modifications.
 */
public class SymbolAccessHandler {

  public record SymbolAccess(
      MICalculation getter,
      Optional<MISetter> setter
  ) {
  }

  public SymbolAccess getSymbolAccess(
      ISymbol exprSourceSym,
      MIFrameLayoutForBasicSymbols frameLayout,
      InterpreterDataForBasicSymbols iData
  ) {
    MICalculation getter;
    Optional<MISetter> setter = Optional.empty();
    if (TypeDispatcherHotfix.isVariableSymbol(exprSourceSym)) {
      VariableSymbol varSym = (VariableSymbol) exprSourceSym;
      if (isNativeJavaVariable(varSym)) {
        FieldSymbol fieldSym = (FieldSymbol) exprSourceSym;
        getter = createJavaFieldGetterOfStatic(fieldSym);
        setter = Optional.of(createJavaFieldSetterOfStatic(fieldSym));
      }
      else {
        getter = frameLayout.getVariableGetter(varSym);
        setter = Optional.of(frameLayout.getVariableSetter(varSym));
      }
    }
    else if (TypeDispatcherHotfix.isFunctionSymbol(exprSourceSym)) {
      FunctionSymbol funcSym = (FunctionSymbol) exprSourceSym;
      if (isNativeJavaFunction(funcSym)) {
        if (TypeDispatcherHotfix.isMethodSymbol(funcSym)) {
          MethodSymbol methodSym = (MethodSymbol) funcSym;
          final MethodHandle methodHandle = getHandleOfSymbol(methodSym);
          final MCValueFunctionOfMethodHandle staticMethodHandle =
              new MCValueFunctionOfMethodHandle(methodHandle);
          getter = (MICalculationValue) frame -> staticMethodHandle;
        }
        else {
          throw new NotImplementedException(
              "Native Java function not mapped as OOSymbols method");
        }
      }
      else {
        final Supplier<MCValueFunction> functionSupplier =
            iData.getFunctionSupplier(funcSym);
        getter = (MICalculationValue) frame -> functionSupplier.get();
      }
    }
    else {
      throw new NotImplementedException();
    }
    return new SymbolAccess(getter, setter);
  }

  public SymbolAccess getSymbolAccess(
      ISymbol exprSourceSym,
      MIFrameLayoutForBasicSymbols frameLayout,
      SymTypeExpression objType,
      MICalculationValue objCalc
  ) {
    MICalculation getter;
    Optional<MISetter> setter = Optional.empty();
    if (objType.isObjectType() || objType.isGenericType()) {
      if (isNativeJavaType(objType.getTypeInfo())) {
        if (TypeDispatcherHotfix.isFieldSymbol(exprSourceSym)) {
          FieldSymbol fieldSym = (FieldSymbol) exprSourceSym;
          getter = createJavaFieldGetter(fieldSym, objCalc);
          setter = Optional.of(createJavaFieldSetter(fieldSym, objCalc));
        }
        else if (TypeDispatcherHotfix.isMethodSymbol(exprSourceSym)) {
          MethodSymbol methodSym = (MethodSymbol) exprSourceSym;
          MethodHandle methodHandle = getHandleOfSymbol(methodSym);
          final MCValueFunctionOfMethodHandle unboundMethod =
              new MCValueFunctionOfMethodHandle(methodHandle);
          getter = (MICalculationValue) frame -> {
            final Object thisPtr = objCalc.calculate(frame).asNativeObject();
            return unboundMethod.withBoundThisPtr(thisPtr);
          };
        }
        else {
          throw new NotImplementedException();
        }
      }
      else {
        throw new IllegalStateException(
            // design decision to not interpret other symbols
            // made/discussed on 12.05.2026.
            // This does not enforce the symbols to be loaded by Class2MC,
            // however, this does require either
            //  * the symbols to be fully Java compatible or
            //  * a generator specific information that describes
            //    how the generated symbols look like and how to use them.
            "Expected a symbol of a native Java object type."
                + " non-Java objects are deliberately not supported, "
                + " as it is expected that types are all set by compile time,"
                + " not during runtime."
                + " Thus, we exclusively interpret objects per reflection,"
                + " which in turn requires the classes"
                + " to be available in the classpath."
        );
      }
    }
    else {
      throw new NotImplementedException();
    }
    return new SymbolAccess(getter, setter);
  }

  // helper

  /**
   * creates a getter for a field
   *
   * @param fieldSymbol The field to read
   * @param objCalc     the calculation
   * @return a calculation that gets the field-value of that object.
   */
  protected MICalculation createJavaFieldGetter(
      FieldSymbol fieldSymbol,
      MICalculationValue objCalc
  ) {
    final Field field = getJavaField(fieldSymbol);
    final MethodHandle handle;
    try {
      handle = MethodHandles.lookup().unreflectGetter(field);
    }
    catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }

    // not the most efficient implementation, but should be good enough
    final Function<MIFrame, Object> genericLoader;
    if (handle.type().parameterCount() == 0) {
      // static
      genericLoader = frame -> {
        try {
          return handle.invoke();
        }
        catch (Throwable t) {
          throw new RuntimeException(t);
        }
      };
    }
    else {
      // not static
      genericLoader = frame -> {
        final Object thisPtr = objCalc.calculate(frame).asNativeObject();
        try {
          return handle.invoke(thisPtr);
        }
        catch (Throwable t) {
          throw new RuntimeException(t);
        }
      };
    }

    Class<?> javaType = field.getType();
    MICalculationBoolean booleanCalc = frame ->
        (boolean) genericLoader.apply(frame);
    MICalculationInt intCalc;
    if (javaType == char.class || javaType == Character.class) {
      intCalc = frame -> (int) (Character) genericLoader.apply(frame);
    }
    else {
      intCalc = frame ->
          ((Number) genericLoader.apply(frame)).intValue();
    }
    MICalculationDouble doubleCalc;
    if (javaType == char.class || javaType == Character.class) {
      doubleCalc = frame -> (double) (Character) genericLoader.apply(frame);
    }
    else {
      doubleCalc = frame ->
          ((Number) genericLoader.apply(frame)).doubleValue();
    }
    MICalculationValue valueCalc = frame ->
        MCValueFactory.createMIValueOfNativeObject(genericLoader.apply(frame));

    return switchByFormat(fieldSymbol,
        booleanCalc,
        intCalc,
        doubleCalc,
        valueCalc
    );
  }

  protected MICalculation createJavaFieldGetterOfStatic(
      FieldSymbol fieldSymbol
  ) {
    return createJavaFieldGetter(fieldSymbol, f -> null);
  }

  /**
   * creates a setter for a field
   * that is not yet bound to a specific object.
   *
   * @param fieldSymbol The field to set a new value to
   * @return a function that, given a calculation that returns a Java object,
   *     returns a setter that sets the field-value of that object.
   */
  protected MISetter createJavaFieldSetter(
      FieldSymbol fieldSymbol,
      MICalculationValue objCalc
  ) {
    final Field field = getJavaField(fieldSymbol);
    final MethodHandle handle;
    try {
      handle = MethodHandles.lookup().unreflectSetter(field);
    }
    catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }

    final BiConsumer<MIFrame, Object> genericSetter;
    if (handle.type().parameterCount() == 0) {
      // static
      genericSetter = (frame, value) -> {
        try {
          handle.invoke(value);
        }
        catch (Throwable t) {
          throw new RuntimeException(t);
        }
      };
    }
    else {
      // not static
      genericSetter = (frame, value) -> {
        try {
          final Object thisPtr = objCalc.calculate(frame).asNativeObject();
          handle.invoke(thisPtr, value);
        }
        catch (Throwable t) {
          throw new RuntimeException(t);
        }
      };
    }

    MISetter setter;
    Class<?> fieldType = field.getType();
    if (fieldType == boolean.class || fieldType == Boolean.class) {
      setter = (MISetterBoolean) genericSetter::accept;
    }
    else if (fieldType == byte.class || fieldType == Byte.class) {
      setter = (MISetterInt) (frame, value) ->
          genericSetter.accept(frame, (byte) value);
    }
    else if (fieldType == short.class || fieldType == Short.class) {
      setter = (MISetterInt) (frame, value) ->
          genericSetter.accept(frame, (short) value);
    }
    else if (fieldType == char.class || fieldType == Character.class) {
      setter = (MISetterInt) (frame, value) ->
          genericSetter.accept(frame, (char) value);
    }
    else if (fieldType == int.class || fieldType == Integer.class) {
      setter = (MISetterInt) genericSetter::accept;
    }
    else if (fieldType == long.class || fieldType == Long.class) {
      setter = (MISetterInt) (frame, value) ->
          genericSetter.accept(frame, (long) value);
    }
    else if (fieldType == float.class || fieldType == Float.class) {
      setter = (MISetterDouble) (frame, value) ->
          genericSetter.accept(frame, (float) value);
    }
    else if (fieldType == double.class || fieldType == Double.class) {
      setter = (MISetterDouble) genericSetter::accept;
    }
    else {
      setter = (frame, value) ->
          genericSetter.accept(frame, value.asNativeObject());
    }
    return setter;
  }

  protected MISetter createJavaFieldSetterOfStatic(FieldSymbol fieldSymbol) {
    return createJavaFieldSetter(fieldSymbol, f -> null);
  }

  protected Field getJavaField(FieldSymbol fieldSymbol) {
    Preconditions.checkState(isNativeJavaVariable(fieldSymbol));
    TypeSymbol surroundingType =
        getEnclosingType(fieldSymbol.getEnclosingScope()).get();
    Class<?> clazz = getNativeJavaClass(surroundingType);
    try {
      return clazz.getField(fieldSymbol.getName());
    }
    catch (NoSuchFieldException | SecurityException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Provides a {@link MethodHandle} provided a native Java method.
   *
   * @param methodSym the symbol of the native Java method
   * @return the corresponding {@link MethodHandle}
   */
  protected MethodHandle getHandleOfSymbol(MethodSymbol methodSym) {
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
