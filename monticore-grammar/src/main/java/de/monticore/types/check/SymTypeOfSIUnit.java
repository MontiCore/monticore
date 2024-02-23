// (c) https://github.com/MontiCore/monticore
package de.monticore.types.check;

import de.monticore.types3.ISymTypeVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * SymTypeOfSIUnit stores any kind of derived SIUnit with prefixes, such as
 * m, km/h, m^2, ...
 */
public class SymTypeOfSIUnit extends SymTypeExpression {

  protected List<SIUnitBasic> numerator;
  protected List<SIUnitBasic> denominator;

  public SymTypeOfSIUnit(
      List<SIUnitBasic> numerator,
      List<SIUnitBasic> denominator
  ) {
    this.numerator = new ArrayList<>(numerator);
    this.denominator = new ArrayList<>(denominator);
  }

  public List<SIUnitBasic> getNumerator() {
    return this.numerator;
  }

  public List<SIUnitBasic> getDenominator() {
    return this.denominator;
  }

  @Override
  public boolean isSIUnitType() {
    return true;
  }

  @Override
  public SymTypeOfSIUnit asSIUnitType() {
    return this;
  }

  @Override
  public String print() {
    String result = "[";
    if (getNumerator().isEmpty()) {
      result += "1";
    }
    else {
      for (int i = 0; i < getNumerator().size(); i++) {
        SIUnitBasic unitBasic = getNumerator().get(i);
        result += unitBasic.print();
        if (i < getNumerator().size() - 1 && unitBasic.getExponent() == 1) {
          result += "^1";
        }
      }
    }
    if (getDenominator().size() >= 1) {
      result += "/";
      for (int i = 0; i < getDenominator().size(); i++) {
        SIUnitBasic unitBasic = getDenominator().get(i);
        result += unitBasic.print();
        if (i < getDenominator().size() - 1 && unitBasic.getExponent() == 1) {
          result += "^1";
        }
      }
    }
    result += "]";
    return result;
  }

  @Override
  public String printFullName() {
    return print();
  }

  @Override
  public SymTypeOfSIUnit deepClone() {
    return (SymTypeOfSIUnit) super.deepClone();
  }

  @Override
  public boolean deepEquals(SymTypeExpression sym) {
    // in basically all cases requires normalized SymTypeExpressions
    if (!sym.isSIUnitType()) {
      return false;
    }
    SymTypeOfSIUnit symSIUnit = (SymTypeOfSIUnit) sym;
    if (this.getNumerator().size() != symSIUnit.getNumerator().size() ||
        this.getDenominator().size() != symSIUnit.getDenominator().size()) {
      return false;
    }
    for (int i = 0; i < this.getNumerator().size(); i++) {
      if (!this.getNumerator().get(i)
          .deepEquals(symSIUnit.getNumerator().get(i))) {
        return false;
      }
    }
    for (int i = 0; i < this.getDenominator().size(); i++) {
      if (!this.getDenominator().get(i)
          .deepEquals(symSIUnit.getDenominator().get(i))) {
        return false;
      }
    }
    return true;
  }

  @Override
  public void accept(ISymTypeVisitor visitor) {
    visitor.visit(this);
  }
}
