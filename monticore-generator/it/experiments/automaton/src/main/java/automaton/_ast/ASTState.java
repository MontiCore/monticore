package automaton._ast;

import java.util.ArrayList;
import java.util.List;

public class ASTState extends ASTStateTOP {
  
  protected List<ASTState> reachableStates = new ArrayList<ASTState>();
  
  protected ASTState() {
    super();
  }
  
  protected ASTState(
      String name,
      List<ASTState> states,
      List<ASTTransition> transitions,
      boolean r_final,
      boolean initial) {
    super(name, states, transitions, r_final, initial);
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
