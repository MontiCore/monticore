/* (c) https://github.com/MontiCore/monticore */
package de.monticore.timer.cocos;

import de.monticore.timer._cocos.TimerCoCoChecker;

public class TimerCoCos {

  public static TimerCoCoChecker createChecker() {
    TimerCoCoChecker checker = new TimerCoCoChecker();
    checker.addCoCo(new DateIsValidCoCo());
    checker.addCoCo(new TimeIsValidCoCo());
    checker.addCoCo(new PeriodIsValidCoCo());
    return checker;
  }
}
