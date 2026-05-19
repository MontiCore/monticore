// (c) https://github.com/MontiCore/monticore
package de.monticore.interpreter.frames;

import com.google.common.base.Preconditions;
import de.monticore.interpreter.values.MIValueFunction;
import de.monticore.interpreter.values.MIValue;
import de.monticore.interpreter.values.MIValueFunctionOfModel;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;

import java.util.Map;
import java.util.Optional;

/**
 * A calculation frame: It contains local variables
 * (or global ones for the topmost frame).
 * <p>
 * E.g., a new frame is opened
 * for the execution of a function defined in a model,
 * s. a. {@link MIValueFunctionOfModel}
 */
public class MIFrame {

  // one could try out different memory layouts;
  // one with only a single Object array
  // and one with all primitives separately.
  // One version with Object[] arrs = {new int[3], new Object[2]};

  protected final Optional<MIFrame> parentFrame;

  // only kept to print
  // not peak efficient,
  // but relevant enough to allow for a bit of suboptimal memory usage
  protected final MIFrameLayout frameLayout;

  // note: JVMs have a word size (usually 32/64bit),
  // thus smaller primitives tend to be less efficient
  // and take more space if not stored in arrays.
  // Whether int or long are the more efficient primitive
  // tends to be JVM dependent, but int is usually more efficient than long.
  // We do take double over float for the increased accuracy, though.

  //values
  protected final boolean[] booleans;
  protected final int[] ints;
  protected final double[] doubles;
  protected final MIValue[] objects;

  // functions
  // they need to be referencable, so that we can break up recursive calls
  // unlike other elements, this is the same value in every frame,
  // simply to provide fast non-static access.
  // layout could be an array or at least an ArrayList,
  // but for now it is a map for simplicity,
  protected final Map<FunctionSymbol, MIValueFunction> functions;

  public MIFrame(
      MIFrameLayout frameLayout,
      MIFrame parentFrame
  ) {
    this.frameLayout = Preconditions.checkNotNull(frameLayout);
    this.booleans = new boolean[frameLayout.sizeBooleans()];
    this.ints = new int[frameLayout.sizeIntegers()];
    this.doubles = new double[frameLayout.sizeDoubles()];
    this.objects = new MIValue[frameLayout.sizeObjects()];
    this.functions = frameLayout.getFunctions();
    this.parentFrame = Optional.ofNullable(parentFrame);
  }

  public MIFrame(MIFrameLayout frameLayout) {
    this(frameLayout, null);
  }

  public boolean hasParentFrame() {
    return parentFrame.isPresent();
  }

  public MIFrame getParentFrame() {
    return parentFrame.get();
  }

  public MIFrame getParentFrame(int frameLevel) {
    return frameLevel == 0 ?
        this :
        getParentFrame().getParentFrame(frameLevel - 1);
  }

  // JLS 21 10.4: One might assume that a char index would be more efficient,
  // as they are non-negative.
  // However, it will be converted to int anyway

  public boolean getBoolean(int idx) {
    return booleans[idx];
  }

  public void setBoolean(int idx, boolean value) {
    booleans[idx] = value;
  }

  public int getInt(int idx) {
    return ints[idx];
  }

  public void setInt(int idx, int value) {
    ints[idx] = value;
  }

  public double getDouble(int idx) {
    return doubles[idx];
  }

  public void setDouble(int idx, double value) {
    doubles[idx] = value;
  }

  public MIValue getObject(int idx) {
    return objects[idx];
  }

  public void setObject(int idx, MIValue value) {
    // could check here that it's not primitive,
    // but leaving out for efficiency
    objects[idx] = value;
  }

  public MIValueFunction getFunction(FunctionSymbol symbol) {
    MIValueFunction function = functions.get(symbol);
    if (function != null) {
      return function;
    }
    else {
      // not expected to happen in a properly set up interpreter
      throw new RuntimeException(
          "Function symbol " + symbol.getFullName() + "has not been defined."
      );
    }
  }

  // helper

  /**
   * Clones this frame data into a new frame with extended layout.
   * Used first and foremost to extend the topmost frame.
   * <p>
   * This should not be called under most circumstances.
   */
  public MIFrame createExpandedCopy(
      MIFrameLayout newLayout
  ) {
    // assert that initial layout did not change
    if (!frameLayout.isPrefixOf(newLayout)) {
      throw new IllegalArgumentException(
          "Expected the frame layout to be a prefix"
      );
    }
    MIFrame copy = new MIFrame(newLayout, parentFrame.orElse(null));
    System.arraycopy(booleans, 0, copy.booleans, 0, booleans.length);
    System.arraycopy(ints, 0, copy.ints, 0, ints.length);
    System.arraycopy(doubles, 0, copy.doubles, 0, doubles.length);
    System.arraycopy(objects, 0, copy.objects, 0, objects.length);
    return copy;
  }

  public MIFrameLayout getFrameLayout() {
    return frameLayout;
  }

  public String printVariablesForLog() {
    StringBuilder r = new StringBuilder();
    boolean printedSomething = false;
    if (booleans.length > 0) {
      r.append("  booleans: ").append(System.lineSeparator());
      for (int i = 0; i < booleans.length; i++) {
        r.append("    ")
            .append(printVariableSymbolForLog(frameLayout.getBooleanSymbol(i)))
            .append(": ")
            .append(booleans[i])
            .append(System.lineSeparator());
      }
      printedSomething = true;
    }
    if (ints.length > 0) {
      r.append("  ints: ").append(System.lineSeparator());
      for (int i = 0; i < ints.length; i++) {
        r.append("    ")
            .append(printVariableSymbolForLog(frameLayout.getIntSymbol(i)))
            .append(": ")
            .append(ints[i])
            .append(System.lineSeparator());
      }
      printedSomething = true;
    }
    if (doubles.length > 0) {
      r.append("  doubles: ").append(System.lineSeparator());
      for (int i = 0; i < doubles.length; i++) {
        r.append("    ")
            .append(printVariableSymbolForLog(frameLayout.getDoubleSymbol(i)))
            .append(": ")
            .append(doubles[i])
            .append(System.lineSeparator());
      }
      printedSomething = true;
    }
    if (objects.length > 0) {
      r.append("  objects: ").append(System.lineSeparator());
      for (int i = 0; i < objects.length; i++) {
        r.append("    ")
            .append(printVariableSymbolForLog(frameLayout.getObjectSymbol(i)))
            .append(": ");
        if (objects[i] != null) {
          r.append(objects[i].printValue())
              .append(" : ")
              .append(objects[i].printType());
        }
        else {
          r.append("null");
        }
        r.append(System.lineSeparator());
      }
      printedSomething = true;
    }
    if (!printedSomething) {
      r.append("No variables in the frame.");
    }
    return r.toString();
  }

  protected String printVariableSymbolForLog(VariableSymbol symbol) {
    StringBuilder r = new StringBuilder();
    r.append(symbol.getName());
    if (symbol.isPresentAstNode()) {
      r.append(" (")
          .append(symbol.getSourcePosition().toString())
          .append(")");
    }
    return r.toString();
  }

}
