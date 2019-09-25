package de.monticore.types.check;

import de.se_rwth.commons.logging.Log;

import java.util.Optional;

public class LastResult{

  //used to check if methodpreferred was used more than once while true
  //methodpreferred should only be true for the next astnode, must be reset before subsequent astnodes
  private int count=0;
  private Optional<SymTypeExpression> last;
  private boolean methodpreferred = false;

  public LastResult(){
    this.last = Optional.empty();
  }

  public static <T> Optional<T> setAbsentLast() {
    return Optional.empty();
  }

  public SymTypeExpression getLast() {
    return last.get();
  }

  public boolean isPresentLast() {
    return last.isPresent();
  }

  public void setLastOpt(Optional<SymTypeExpression> last){
    this.last = last;
  }

  public Optional<SymTypeExpression> getLastOpt(){
    return this.last;
  }

  public void setLast(SymTypeExpression last){
    this.last = Optional.of(last);
  }

  public void setMethodpreferred(boolean methodpreferred){
    count=0;
    this.methodpreferred=methodpreferred;
  }

  public boolean isMethodpreferred() {
    if(methodpreferred) {
      count++;
    }
    if(count>1){
      Log.warn("Used multiple times without reset. Possible error.");
    }
    return methodpreferred;
  }
}