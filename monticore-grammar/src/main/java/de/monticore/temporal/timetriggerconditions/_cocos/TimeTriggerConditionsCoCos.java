/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal.timetriggerconditions._cocos;

/** Creates fully configured CoCo checkers for the TimeTriggerConditions grammar. */
public final class TimeTriggerConditionsCoCos {
  
  private TimeTriggerConditionsCoCos() {
  }
  
  /**
   * Creates a new checker containing every TimeTrigger condition validation.
   *
   * @return a newly configured checker
   */
  public static TimeTriggerConditionsCoCoChecker createChecker() {
    TimeTriggerConditionsCoCoChecker checker = new TimeTriggerConditionsCoCoChecker();
    checker.addCoCo(new AfterDurationConditionHasValidDuration());
    checker.addCoCo(new AfterISOPeriodConditionHasValidPeriod());
    checker.addCoCo(new OnConditionHasValidValue());
    checker.addCoCo(new EveryTimeConditionHasValidRepetitions());
    checker.addCoCo(new CronExpressionIsValid());
    return checker;
  }
  
}
