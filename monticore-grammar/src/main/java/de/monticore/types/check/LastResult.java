/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import java.util.Optional;

/**
 * Wrapper class for the last result in a type check
 */
public class LastResult{

  private Optional<SymTypeExpression> last;

  public LastResult(){
    this.last = Optional.empty();
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
}