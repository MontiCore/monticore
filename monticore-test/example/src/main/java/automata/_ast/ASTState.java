/* (c) https://github.com/MontiCore/monticore */

package automata._ast;

import java.util.ArrayList;
import java.util.List;

public class ASTState extends ASTStateTOP {
  
  protected List<ASTState> reachableStates = new ArrayList<ASTState>();
  
  protected ASTState() {
    super();
  }

  public boolean isReachable(ASTState s) {
    return reachableStates.contains(s);
  }
  
  public List<ASTState> getReachableStates() {
    return this.reachableStates;
  }
  
  public void setReachableStates(List<ASTState> reachableStates) {
    this.reachableStates = reachableStates;
  }
  
  public void addReachableState(ASTState s) {
    this.reachableStates.add(s);
  }
  
}
