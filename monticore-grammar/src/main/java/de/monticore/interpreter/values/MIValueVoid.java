// (c) https://github.com/MontiCore/monticore
package de.monticore.interpreter.values;

/**
 * Only used sparingly to avoid having to do too many case distinctions.
 * It is not expected to operate on it like a real value.
 * <p>
 * Usually(TM), this is only used as a result of a full interpretation,
 * and _not_ used within the interpreter itself.
 * <p>
 * This _could_ be removed, but is most likely not worth the effort.
 */
public class MIValueVoid implements MIValue {

  public static MIValueVoid INSTANCE = new MIValueVoid();

  protected MIValueVoid() {
  }

  @Override
  public boolean isVoid() {
    return true;
  }

  @Override
  public Object asNativeObject() throws UnsupportedOperationException {
    throw new UnsupportedOperationException(
        "Objects of MIValue of void should never be in places"
            + " there they are used like values."
    );
  }

  @Override
  public String printType() {
    return "void";
  }

  @Override
  public String printValue() {
    return "none";
  }

  @Override
  public boolean checkEqualityOperator(MIValue other) {
    return other.isVoid();
  }

}
