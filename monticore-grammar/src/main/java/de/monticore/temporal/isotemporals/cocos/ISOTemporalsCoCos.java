/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal.isotemporals.cocos;

import de.monticore.temporal.isotemporals._cocos.ISOTemporalsCoCoChecker;

/**
 * The CoCos specific to ISOTemporals. They complement the CoCos of {@code TemporalBasisCoCos},
 * which are not included, so that languages using several temporal grammars check them once.
 * {@link PeriodIsNonemptyCoCo} is not included either, as it is already checked while parsing.
 */
public class ISOTemporalsCoCos {
  public ISOTemporalsCoCoChecker getCoCoChecker() {
    ISOTemporalsCoCoChecker checker = new ISOTemporalsCoCoChecker();
    checker.addCoCo(new WeekDateFieldsInRangeCoCo());
    checker.addCoCo(new ISOTimeFieldsInRangeCoCo());
    return checker;
  }
}
