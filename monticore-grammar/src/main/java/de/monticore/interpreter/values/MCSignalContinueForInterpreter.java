package de.monticore.interpreter.values;

import de.monticore.values.MCSignalContinue;

public class MCSignalContinueForInterpreter
    extends AbstractMCSignalFlowControlForInterpreter
    implements MCSignalContinue {

  /**
   * signals the current thread.
   */
  public static void signal() {
    throw new MCSignalContinueForInterpreter();
  }

  @Override
  public boolean isContinue() {
    return true;
  }

  @Override
  public String printType() {
    return "Continue";
  }

  @Override
  public String printValue() {
    return "";
  }

}
