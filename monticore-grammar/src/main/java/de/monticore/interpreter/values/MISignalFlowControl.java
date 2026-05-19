package de.monticore.interpreter.values;

/**
 * These values control the flow of the interpretation thread.
 * As we are using native Java {@link Thread},
 * this is done using unchecked {@link Throwable}.
 * Unfortunaly, there is no good candidate;
 * The Throwable should be unchecked
 * and Error seemed even less applicable (s.a.JLS 21 11.1.1.).
 */
public abstract class MISignalFlowControl
    extends RuntimeException
    implements MIValue {

  @Override
  public boolean isFlowControlSignal() {
    return true;
  }

  @Override
  public Object asNativeObject() {
    return this;
  }

  @Override
  public boolean checkEqualityOperator(MIValue other) {
    return this == other;
  }

}
