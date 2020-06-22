/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.JsonPrinter;

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
      return "? super "+bound.print();
    }else{
      return "? extends "+bound.print();
    }
  }

  @Override
  protected String printAsJson() {
    JsonPrinter jp = new JsonPrinter();
    jp.beginObject();
    // Care: the following String needs to be adapted if the package was renamed
    jp.member(JsonDeSers.KIND, "de.monticore.types.check.SymTypeOfWildcard");
    jp.member("isUpper", isUpper);
    if(bound!=null){
      jp.memberJson("bound", bound.printAsJson());
    }
    jp.endObject();
    return jp.getContent();
  }

  @Override
  public SymTypeOfWildcard deepClone() {
    SymTypeOfWildcard clone = new SymTypeOfWildcard(this.isUpper, this.bound);
    clone.typeSymbolSurrogate = this.typeSymbolSurrogate;
    clone.methodList = this.methodList;
    return clone;
  }

  public boolean isUpper() {
    return isUpper;
  }

  public SymTypeExpression getBound() {
    return bound;
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
