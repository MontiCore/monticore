// (c) https://github.com/MontiCore/monticore
package de.monticore.interpreter.frames;

import de.monticore.values.MCValue;

import java.util.Optional;

/**
 * A calculation frame: It contains local variables
 * (or global ones for the topmost frame).
 * <p>
 * E.g., a new frame is opened
 * for the execution of a function defined in a model,
 * s. a. MIValueFunctionOfModel
 */
public class MIFrame {

  // one could try out different memory layouts;
  // one with only a single Object array
  // and one with all primitives separately.
  // One version with Object[] arrs = {new int[3], new Object[2]};

  protected final Optional<MIFrame> parentFrame;

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
  protected final MCValue[] objects;

  public MIFrame(
      MIFrameLayout frameLayout,
      MIFrame parentFrame
  ) {
    this.booleans = new boolean[frameLayout.sizeBooleans()];
    this.ints = new int[frameLayout.sizeIntegers()];
    this.doubles = new double[frameLayout.sizeDoubles()];
    this.objects = new MCValue[frameLayout.sizeObjects()];
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

  public MCValue getObject(int idx) {
    return objects[idx];
  }

  public void setObject(int idx, MCValue value) {
    // could check here that it's not primitive,
    // but leaving out for efficiency
    objects[idx] = value;
  }

}
