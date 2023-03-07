/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

public class SymTypeOfWildcard extends SymTypeExpression {

  protected SymTypeExpression bound;
  protected boolean isUpper;

  public SymTypeOfWildcard(){

  }

  public SymTypeOfWildcard(boolean isUpper, SymTypeExpression bound){
    this.bound = bound;
    this.isUpper = isUpper;
  }

  @Override
  public String print() {
    if(bound==null){
      return "?";
    }else if(isUpper){
      return "? extends "+bound.print();
    }else{
      return "? super "+bound.print();
    }
  }

  @Override
  public String printFullName() {
    if(bound==null){
      return "?";
    }else if(isUpper){
      return "? extends "+bound.printFullName();
    }else{
      return "? super "+bound.printFullName();
    }
  }

  @Override
  public SymTypeOfWildcard deepClone() {
    SymTypeOfWildcard clone = new SymTypeOfWildcard(this.isUpper, this.bound);
    clone.typeSymbol = this.typeSymbol;
    clone.functionList = this.functionList;
    return clone;
  }

  public boolean isUpper() {
    return isUpper;
  }

  public SymTypeExpression getBound() {
    return bound;
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
  public boolean deepEquals(SymTypeExpression sym){
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
}
