/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.types3.ISymTypeVisitor;

public class SymTypeObscure extends SymTypeExpression {

  @Override
  public SymTypeExpression deepClone() {
    return new SymTypeObscure();
  }

  @Override
  public boolean deepEquals(SymTypeExpression sym) {
    return sym.isObscureType();
  }

  @Override
  public boolean isValidType() {
    return false;
  }

  @Override
  public boolean isObscureType() {
    return true;
  }

  @Override
  public SymTypeObscure asObscureType() {
    return this;
  }

  @Override
  public void accept(ISymTypeVisitor visitor) {
    visitor.visit(this);
  }
}
