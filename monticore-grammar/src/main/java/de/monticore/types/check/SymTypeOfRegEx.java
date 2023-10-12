// (c) https://github.com/MontiCore/monticore
package de.monticore.types.check;

import de.monticore.types3.ISymTypeVisitor;

public class SymTypeOfRegEx extends SymTypeExpression {

  protected String regex;

  public SymTypeOfRegEx(String regex) {
    this.regex = regex;
  }

  @Override
  public String printFullName() {
    return print();
  }

  @Override
  public String print() {
    return "R\"" + getRegExString() + "\"";
  }

  public String getRegExString() {
    return regex;
  }

  @Override
  public boolean isRegExType() {
    return true;
  }

  @Override
  public SymTypeOfRegEx asRegExType() {
    return this;
  }

  @Override
  public boolean deepEquals(SymTypeExpression sym) {
    if (!sym.isRegExType()) {
      return false;
    }
    SymTypeOfRegEx symRegEx = (SymTypeOfRegEx) sym;
    if (!this.getRegExString().equals(symRegEx.getRegExString())) {
      return false;
    }
    return true;
  }

  @Override
  public void accept(ISymTypeVisitor visitor) {
    visitor.visit(this);
  }
}
