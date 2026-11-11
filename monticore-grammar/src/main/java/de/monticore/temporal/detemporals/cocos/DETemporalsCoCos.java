/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal.detemporals.cocos;

import de.monticore.temporal.detemporals._cocos.DETemporalsCoCoChecker;

/**
 * The CoCos specific to DETemporals. They complement the CoCos of {@code TemporalBasisCoCos},
 * which are not included, so that languages using several temporal grammars check them once.
 */
public class DETemporalsCoCos {
  public DETemporalsCoCoChecker getCoCoChecker() {
    DETemporalsCoCoChecker checker = new DETemporalsCoCoChecker();
    checker.addCoCo(new DEYearHasFourDigitsCoCo());
    return checker;
  }
}
