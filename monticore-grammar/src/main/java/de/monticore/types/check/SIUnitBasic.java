// (c) https://github.com/MontiCore/monticore
package de.monticore.types.check;

import de.se_rwth.commons.logging.Log;

public class SIUnitBasic {

  protected String dimension;

  protected String prefix;

  protected int exponent;

  public SIUnitBasic(
      String dimension,
      String prefix,
      int exponent
  ) {
    this.dimension = Log.errorIfNull(dimension);
    this.prefix = Log.errorIfNull(prefix);
    this.exponent = exponent;
  }

  public String getDimension() {
    return dimension;
  }

  public void setDimension(String dimension) {
    this.dimension = dimension;
  }

  public String getPrefix() {
    return prefix;
  }

  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }

  public int getExponent() {
    return exponent;
  }

  public void setExponent(int exponent) {
    this.exponent = exponent;
  }

  public String print() {
    String printed = getPrefix() + getDimension();
    if (getExponent() != 1) {
      printed += "^" + getExponent();
    }
    return printed;
  }

  public boolean deepEquals(SIUnitBasic other) {
    return this.getDimension().equals(other.getDimension()) &&
        this.getPrefix().equals(other.getPrefix()) &&
        this.getExponent() == other.getExponent();
  }

  public SIUnitBasic deepClone() {
    return new SIUnitBasic(getDimension(), getPrefix(), getExponent());
  }

}
