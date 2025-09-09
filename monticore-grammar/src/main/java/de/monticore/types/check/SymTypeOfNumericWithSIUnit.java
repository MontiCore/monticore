/* (c) https://github.com/MontiCore/monticore */

package de.monticore.types.check;

import de.monticore.types3.ISymTypeVisitor;
import de.se_rwth.commons.logging.Log;

/**
 * SymTypeOfNumericWithSIUnit stores any kind of Numerics
 * combined with SIUnit applied such as
 * {@code m<int>}
 * {@code m/s<double>}
 */
public class SymTypeOfNumericWithSIUnit extends SymTypeExpression {

  protected SymTypeOfSIUnit siUnitType;
  protected SymTypeExpression numericType;

  public SymTypeOfNumericWithSIUnit(
      SymTypeOfSIUnit siUnitType,
      SymTypeExpression numericType
  ) {
    this.siUnitType = Log.errorIfNull(siUnitType);
    this.numericType = Log.errorIfNull(numericType);
  }

  public SymTypeOfSIUnit getSIUnitType() {
    return siUnitType;
  }

  public void setSIUnitType(SymTypeOfSIUnit siUnitType) {
    this.siUnitType = siUnitType;
  }

  public SymTypeExpression getNumericType() {
    return numericType;
  }

  public void setNumericType(SymTypeExpression numericType) {
    this.numericType = numericType;
  }

  @Override
  public boolean isNumericWithSIUnitType() {
    return true;
  }

  @Override
  public SymTypeOfNumericWithSIUnit asNumericWithSIUnitType() {
    return this;
  }

  @Override
  public String print() {
    return siUnitType.print() + "<" + numericType.print() + ">";
  }

  @Override
  public String printFullName() {
    return siUnitType.printFullName()
        + "<" + numericType.printFullName() + ">";
  }

  @Override
  public SymTypeOfNumericWithSIUnit deepClone() {
    return (SymTypeOfNumericWithSIUnit) super.deepClone();
  }

  @Override
  public boolean deepEquals(SymTypeExpression sym) {
    if (!sym.isNumericWithSIUnitType()) {
      return false;
    }
    SymTypeOfNumericWithSIUnit other = sym.asNumericWithSIUnitType();
    if (!this.getNumericType().deepEquals(other.getNumericType())) {
      return false;
    }
    if (!this.getSIUnitType().deepEquals(other.getSIUnitType())) {
      return false;
    }
    return true;
  }

  @Override
  public void accept(ISymTypeVisitor visitor) {
    visitor.visit(this);
  }
}
