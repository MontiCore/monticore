/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import java.util.Optional;

/**
 * Wrapper class for the last result in a type check
 */
public class LastResult{

  private Optional<SymTypeExpression> last;

  private boolean type;

  private boolean method;

  private boolean field;

  public LastResult(){
    this.last = Optional.empty();
    this.field = false;
    this.method = false;
    this.type = false;
  }

  public SymTypeExpression getLast() {
    return last.get();
  }

  public boolean isPresentLast() {
    return last.isPresent();
  }

  public void setLast(SymTypeExpression last){
    this.last = Optional.of(last);
  }
  
  public void setLastAbsent() {
    this.last = Optional.empty();
  }

  public void reset(){
    setLastAbsent();
    type = false;
    method = false;
    type = false;
  }

  public void setType() {
    this.type = true;
    this.field = false;
    this.method = false;
  }

  public void setMethod() {
    this.method = true;
    this.type=false;
    this.field=false;
  }

  public void setField() {
    this.field = true;
    this.type=false;
    this.method=false;
  }

  public void unsetType(){
    this.type=false;
  }

  public boolean isField() {
    return field;
  }

  public boolean isMethod() {
    return method;
  }

  public boolean isType() {
    return type;
  }
}