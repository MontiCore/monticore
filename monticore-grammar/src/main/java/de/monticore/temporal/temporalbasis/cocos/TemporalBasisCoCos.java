/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal.temporalbasis.cocos;

import de.monticore.temporal.temporalbasis._cocos.TemporalBasisCoCoChecker;

/**
 * The CoCos of TemporalBasis, which apply to the dates and times of all temporal grammars.
 * Languages add this checker to their own, together with the checkers of the temporal grammars
 * they use, e.g. {@code ISOTemporalsCoCos} and {@code DETemporalsCoCos}.
 */
public class TemporalBasisCoCos {
  public TemporalBasisCoCoChecker getCoCoChecker() {
    TemporalBasisCoCoChecker checker = new TemporalBasisCoCoChecker();
    checker.addCoCo(new DateFieldsInRangeCoCo());
    checker.addCoCo(new TimeFieldsInRangeCoCo());
    return checker;
  }
}
