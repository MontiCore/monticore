package de.monticore.interpreter.values;

import de.monticore.values.MCSignalBreak;

public class MCSignalBreakForInterpreter
    extends AbstractMCSignalFlowControlForInterpreter
implements MCSignalBreak {

  /**
   * signals the current thread.
   */
  public static void signal() {
    throw new MCSignalBreakForInterpreter();
  }

  @Override
  public boolean isBreak() {
    return true;
  }

  @Override
  public String printType() {
    return "Break-Signal";
  }

  @Override
  public String printValue() {
    return "";
  }

}
