/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.types3.ISymTypeVisitor;

import java.util.Optional;

public class SymTypeOfWildcard extends SymTypeExpression {

  protected Optional<SymTypeExpression> bound;
  protected boolean isUpper;

  /**
   * @deprecated use the Factory,
   * the Factory uses the constructor below
   */
  @Deprecated
  public SymTypeOfWildcard(){
  }

  /**
   * To be used by the SymTypeExpressionFactory
   */
  public SymTypeOfWildcard(SymTypeExpression bound, boolean isUpper){
    this.bound = Optional.ofNullable(bound);
    this.isUpper = isUpper;
  }

  public boolean hasBound() {
    return !bound.isEmpty();
  }

  public SymTypeExpression getBound() {
    if(hasBound()){
      return bound.get();
    }
    else {
      // deprecated behaviour -> reduce null-checks in code
      // this behaviour seems unused in our main projects (except tests)
      // later: replace with log.error
      return null;
    }
  }

  public boolean isUpper() {
    return isUpper;
  }

  @Override
  public String print() {
    if (!hasBound()) {
      return "?";
    }
    else if (isUpper()) {
      return "? extends " + getBound().print();
    }
    else {
      return "? super " + getBound().print();
    }
  }

  @Override
  public String printFullName() {
    if (!hasBound()) {
      return "?";
    }
    else if (isUpper()) {
      return "? extends " + getBound().printFullName();
    }
    else {
      return "? super " + getBound().printFullName();
    }
  }

  @Override
  public SymTypeOfWildcard deepClone() {
    SymTypeOfWildcard clone;
    if (hasBound()) {
      clone = new SymTypeOfWildcard(getBound().deepClone(), isUpper());
    }
    else {
      clone = new SymTypeOfWildcard(null, false);
    }
    clone.typeSymbol = this.typeSymbol;
    clone.functionList = this.functionList;
    return clone;
  }

  @Override
  public boolean isValidType() {
    return false;
  }

  @Override
  public boolean isWildcard() {
    return true;
  }

  @Override
  public SymTypeOfWildcard asWildcard() {
    return this;
  }

  @Override
  public boolean deepEquals(SymTypeExpression sym){
    //supporting deprecated code:
    if(typeSymbol != null) {
    if(!(sym instanceof SymTypeOfWildcard)){
      return false;
    }
    SymTypeOfWildcard symWil = (SymTypeOfWildcard) sym;
    if(this.isUpper()!=symWil.isUpper()){
      return false;
    }
    if((this.getBound()==null && symWil.getBound()!=null) || (this.getBound()!=null && symWil.getBound()==null)){
      return false;
    }
    if(this.getBound()!=null && symWil.getBound()!=null && !this.getBound().deepEquals(symWil.getBound())){
      return false;
    }
    return this.print().equals(symWil.print());
    }
    if (!sym.isWildcard()) {
      return false;
    }
    SymTypeOfWildcard symWil = (SymTypeOfWildcard) sym;
    if (hasBound() != symWil.hasBound()) {
      return false;
    }
    if (hasBound()) {
      if (!getBound().deepEquals(symWil.getBound())) {
        return false;
      }
      if (isUpper() != symWil.isUpper()) {
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
