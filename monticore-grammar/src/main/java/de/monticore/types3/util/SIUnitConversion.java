// (c) https://github.com/MontiCore/monticore
package de.monticore.types3.util;

import com.google.common.base.Preconditions;

import java.math.BigDecimal;

/**
 * conversion of a value between two SIUnits,
 * {@code valueInTarget = valueInSource * factor + offset}, e.g.,
 * km -> m: (1000, 0), ºC -> K: (1, 273.15),
 * s. {@link SIUnitTypeRelations#getConversion}
 */
public class SIUnitConversion {

  protected BigDecimal factor;

  protected BigDecimal offset;

  public SIUnitConversion(BigDecimal factor, BigDecimal offset) {
    this.factor = Preconditions.checkNotNull(factor);
    this.offset = Preconditions.checkNotNull(offset);
  }

  public BigDecimal getFactor() {
    return factor;
  }

  public BigDecimal getOffset() {
    return offset;
  }

  /**
   * whether the value does not change, e.g., ºC -> ºC, rad -> 1
   */
  public boolean isIdentity() {
    return factor.compareTo(BigDecimal.ONE) == 0 && offset.signum() == 0;
  }

}
