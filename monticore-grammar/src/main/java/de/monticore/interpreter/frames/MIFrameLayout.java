// (c) https://github.com/MontiCore/monticore
package de.monticore.interpreter.frames;

import com.google.common.base.Preconditions;
import de.monticore.interpreter.calculations.MICalculation;
import de.monticore.interpreter.calculations.MICalculationBoolean;
import de.monticore.interpreter.calculations.MICalculationDouble;
import de.monticore.interpreter.calculations.MICalculationInt;
import de.monticore.interpreter.calculations.MICalculationValue;
import de.monticore.interpreter.calculations.MICalculationVoid;
import de.monticore.interpreter.setters.MISetter;
import de.monticore.interpreter.setters.MISetterBoolean;
import de.monticore.interpreter.setters.MISetterDouble;
import de.monticore.interpreter.setters.MISetterInt;
import de.monticore.interpreter.setters.MISetterValue;
import de.monticore.interpreter.util.NativeStorageSelector;
import de.monticore.interpreter.values.FunctionMIValue;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.check.SymTypeExpression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;

import static de.monticore.types3.SymTypeRelations.normalize;

/**
 * describes the layout of an {@link MIFrame},
 * each layout can be used to create multiple frames,
 * e.g., calls of a recursive function.
 */
public class MIFrameLayout {

  Optional<MIFrameLayout> parentFrame;

  //Symbols
  List<VariableSymbol> booleans = new ArrayList<>();
  List<VariableSymbol> integers = new ArrayList<>();
  List<VariableSymbol> doubles = new ArrayList<>();
  List<VariableSymbol> objects = new ArrayList<>();

  // shared with other frames (that share a topmost frame)
  Map<FunctionSymbol, FunctionMIValue> functions;

  /**
   * the "default" constructor
   *
   * @param parentFrame the parent frame
   */
  public MIFrameLayout(MIFrameLayout parentFrame) {
    this.parentFrame = Optional.of(parentFrame);
    this.functions = parentFrame.functions;
  }

  /**
   * for the topmost frame only
   */
  public MIFrameLayout() {
    this.parentFrame = Optional.empty();
    this.functions = new HashMap<>();
  }

  public MIFrameLayout clone() {
    MIFrameLayout copy = hasParentFrame()
        ? new MIFrameLayout(getParentFrame())
        : new MIFrameLayout();
    copy.booleans = new ArrayList<>(booleans);
    copy.integers = new ArrayList<>(integers);
    copy.doubles = new ArrayList<>(doubles);
    copy.objects = new ArrayList<>(objects);
    copy.functions = this.functions;
    return copy;
  }

  public boolean hasParentFrame() {
    return parentFrame.isPresent();
  }

  public MIFrameLayout getParentFrame() {
    if (!hasParentFrame()) {
      throw new IllegalStateException(
          "0xF1009 internal error: "
              + "called getParentFrame(), but no parent frame is present."
      );
    }
    return parentFrame.get();
  }

  public void declareVariable(VariableSymbol varSym) {
    NativeStorageSelector.<Runnable> switchByFormat(varSym,
        () -> declareBoolean(varSym),
        () -> declareInt(varSym),
        () -> declareDouble(varSym),
        () -> declareObject(varSym)
    ).run();
  }

  protected void declareBoolean(VariableSymbol varSym) {
    assertVariableHasNotBeenRegisteredYet(varSym);
    booleans.add(varSym);
  }

  protected void declareInt(VariableSymbol varSym) {
    assertVariableHasNotBeenRegisteredYet(varSym);
    integers.add(varSym);
  }

  protected void declareDouble(VariableSymbol varSym) {
    assertVariableHasNotBeenRegisteredYet(varSym);
    doubles.add(varSym);
  }

  protected void declareObject(VariableSymbol varSym) {
    assertVariableHasNotBeenRegisteredYet(varSym);
    objects.add(varSym);
  }

  public void defineFunction(FunctionSymbol funcSym, FunctionMIValue value) {
    Preconditions.checkNotNull(funcSym);
    Preconditions.checkNotNull(value);
    Preconditions.checkState(!functions.containsKey(funcSym),
        "FunctionSymbol " + funcSym.getFullName()
            + " has already been registered");
    functions.put(funcSym, value);
  }

  public int sizeBooleans() {
    return booleans.size();
  }

  public int sizeIntegers() {
    return integers.size();
  }

  public int sizeDoubles() {
    return doubles.size();
  }

  public int sizeObjects() {
    return objects.size();
  }

  public VariableSymbol getBooleanSymbol(int index) {
    return booleans.get(index);
  }

  public VariableSymbol getIntSymbol(int index) {
    return integers.get(index);
  }

  public VariableSymbol getDoubleSymbol(int index) {
    return doubles.get(index);
  }

  public VariableSymbol getObjectSymbol(int index) {
    return objects.get(index);
  }

  /**
   * Never(!) edit this map yourself.
   * You probably don't need this method anyway,
   * it is for the MIScope to access the functions.
   *
   * @return the map of defined functions.
   */
  public Map<FunctionSymbol, FunctionMIValue> getFunctions() {
    // making it unmodifiable here would create a lot of new objects,
    // thus, it is avoided and simply assumed that no-one modifies it.
    return functions;
  }

  public MICalculationVoid getSetterCalculation(
      VariableSymbol symbol,
      MICalculation valueCalc
  ) {
    return getSetterCalculation(symbol, valueCalc, 0);
  }

  protected MICalculationVoid getSetterCalculation(
      VariableSymbol varSym,
      MICalculation valueCalc,
      int scopeLevelOfVariable
  ) {
    final int booleanPos = booleans.indexOf(varSym);
    final int integerPos = integers.indexOf(varSym);
    final int doublePos = doubles.indexOf(varSym);
    final int objectPos = objects.indexOf(varSym);
    if (booleanPos >= 0) {
      final MICalculationBoolean booleanCalc = valueCalc.asCalculationBoolean();
      return currentFrame ->
          currentFrame.getParentFrame(scopeLevelOfVariable)
              .setBoolean(booleanPos, booleanCalc.calculate(currentFrame));
    }
    else if (integerPos >= 0) {
      final MICalculationInt intCalc = valueCalc.asCalculationInt();
      return currentFrame ->
          currentFrame.getParentFrame(scopeLevelOfVariable)
              .setInt(integerPos, intCalc.calculate(currentFrame));

    }
    else if (doublePos >= 0) {
      final MICalculationDouble doubleCalc = valueCalc.asCalculationDouble();
      return currentFrame ->
          currentFrame.getParentFrame(scopeLevelOfVariable)
              .setDouble(doublePos, doubleCalc.calculate(currentFrame));

    }
    else if (objectPos >= 0) {
      final MICalculationValue objectCalc = valueCalc.asCalculationValue();
      return currentFrame ->
          currentFrame.getParentFrame(scopeLevelOfVariable)
              .setObject(objectPos, objectCalc.calculate(currentFrame));
    }
    else if (hasParentFrame()) {
      return getSetterCalculation(varSym, valueCalc, scopeLevelOfVariable + 1);
    }
    else {
      throw new IllegalArgumentException(
          "VariableSymbol" + varSym.getFullName()
              + " had not been registered."
      );
    }
  }

  // generic variable setter

  /**
   * Provides a function to set the correct variable
   *
   * @param varSym the variable that is to be set.
   * @return A function that sets the variable.
   */
  public MISetter getVariableSetter(VariableSymbol varSym) {
    final int scopeLevel = getScopeLevelOfVarOrThrow(varSym);
    final int idxInScope = getIdxInScope(varSym, scopeLevel);
    return NativeStorageSelector.switchByFormat(varSym,
        (MISetterBoolean) (currentFrame, value) ->
            currentFrame.getParentFrame(scopeLevel)
                .setBoolean(idxInScope, value),
        (MISetterInt) (currentFrame, value) ->
            currentFrame.getParentFrame(scopeLevel)
                .setInt(idxInScope, value),
        (MISetterDouble) (currentFrame, value) ->
            currentFrame.getParentFrame(scopeLevel)
                .setDouble(idxInScope, value),
        (MISetterValue) (currentFrame, value) ->
            currentFrame.getParentFrame(scopeLevel)
                .setObject(idxInScope, value)
    );
  }

  /**
   * A more optimized variant than {@link #getVariableSetter},
   * In cases in which The calculation is ensured to create a fitting primitive.
   *
   * @param varSym    the variable that is to be set.
   * @param valueCalc the function that calculates the variable
   * @return A function that calculates the value and stores it in the variable.
   */
  public MICalculationVoid getCalcAndStore(
      VariableSymbol varSym,
      MICalculation valueCalc
  ) {
    if (valueCalc.isCalculationBoolean() && booleans.contains(varSym)) {
      final MICalculationBoolean calcBool = valueCalc.asCalculationBoolean();
      final int booleanPos = booleans.indexOf(varSym);
      return (frame) ->
          frame.setBoolean(booleanPos, calcBool.calculate(frame));
    }
    else if (valueCalc.isCalculationInt() && integers.contains(varSym)) {
      final MICalculationInt calcInt = valueCalc.asCalculationInt();
      final int intPos = integers.indexOf(varSym);
      return (frame) ->
          frame.setInt(intPos, calcInt.calculate(frame));
    }
    else if (valueCalc.isCalculationDouble() && doubles.contains(varSym)) {
      final MICalculationDouble calcDouble = valueCalc.asCalculationDouble();
      final int doublePos = doubles.indexOf(varSym);
      return (frame) ->
          frame.setDouble(doublePos, calcDouble.calculate(frame));
    }
    else {
      final MICalculationValue calcValue = valueCalc.asCalculationValue();
      final int objectPos = objects.indexOf(varSym);
      return (frame) ->
          frame.setObject(objectPos, calcValue.calculate(frame));
    }
  }

  public MICalculation getVariableGetter(VariableSymbol varSym) {
    final int scopeLevel = getScopeLevelOfVarOrThrow(varSym);
    final int idxInScope = getIdxInScope(varSym, scopeLevel);
    return NativeStorageSelector.switchByFormat(varSym,
        (MICalculationBoolean) frame ->
            frame.getParentFrame(scopeLevel).getBoolean(idxInScope),
        (MICalculationInt) frame ->
            frame.getParentFrame(scopeLevel).getInt(idxInScope),
        (MICalculationDouble) frame ->
            frame.getParentFrame(scopeLevel).getDouble(idxInScope),
        (MICalculationValue) frame ->
            frame.getParentFrame(scopeLevel).getObject(idxInScope)
    );
  }

  /**
   * Checks if this is a prefix of another layout.
   * <p>
   * Internally used to check if a {@link MIFrame} can be cloned.
   *
   * @param otherLayout the longer or equal layout
   * @return whether this is a prefix
   */
  public boolean isPrefixOf(MIFrameLayout otherLayout) {
    boolean res = true;
    if (hasParentFrame() && otherLayout.hasParentFrame()) {
      res = res && getParentFrame() == otherLayout.getParentFrame();
    }
    else {
      res = res && !hasParentFrame() && !otherLayout.hasParentFrame();
    }
    res = res && otherLayout.booleans.subList(0, booleans.size()).equals(booleans);
    res = res && otherLayout.integers.subList(0, integers.size()).equals(integers);
    res = res && otherLayout.doubles.subList(0, doubles.size()).equals(doubles);
    res = res && otherLayout.objects.subList(0, objects.size()).equals(objects);
    return res;
  }

  // helper

  protected void assertVariableHasNotBeenRegisteredYet(VariableSymbol varSym) {
    Preconditions.checkState(!(
            booleans.contains(varSym)
                || integers.contains(varSym)
                || doubles.contains(varSym)
                || objects.contains(varSym)
        ),
        "VariableSymbol " + varSym.getFullName()
            + " has already been registered"
    );
  }

  protected int getScopeLevelOfVarOrThrow(VariableSymbol varSym) {
    Optional<Integer> scopeLevel = getScopeLevelOfVar(varSym);
    if (scopeLevel.isPresent()) {
      return scopeLevel.get();
    }
    else {
      throw new IllegalArgumentException(
          "VariableSymbol " + varSym.getFullName()
              + " had not been registered in any (accessible) scope."
      );
    }
  }

  protected Optional<Integer> getScopeLevelOfVar(VariableSymbol varSym) {
    SymTypeExpression varType = normalize(varSym.getType());
    boolean hasVar = NativeStorageSelector.<BooleanSupplier> switchByFormat(varType,
        () -> booleans.contains(varSym),
        () -> integers.contains(varSym),
        () -> doubles.contains(varSym),
        () -> objects.contains(varSym)
    ).getAsBoolean();
    if (hasVar) {
      return Optional.of(0);
    }
    else if (hasParentFrame()) {
      return getParentFrame()
          .getScopeLevelOfVar(varSym)
          .map(level -> 1 + level);
    }
    else {
      return Optional.of(Integer.MIN_VALUE);
    }
  }

  protected int getIdxInScope(VariableSymbol varSym, int scopeLevel) {
    if (scopeLevel > 0) {
      return getParentFrame().getIdxInScope(varSym, scopeLevel - 1);
    }
    else {
      SymTypeExpression varType = normalize(varSym.getType());
      return NativeStorageSelector.<IntSupplier> switchByFormat(varType,
          () -> booleans.indexOf(varSym),
          () -> integers.indexOf(varSym),
          () -> doubles.indexOf(varSym),
          () -> objects.indexOf(varSym)
      ).getAsInt();
    }
  }

}
