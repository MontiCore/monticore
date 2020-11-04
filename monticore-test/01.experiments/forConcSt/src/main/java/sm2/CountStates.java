/* (c) https://github.com/MontiCore/monticore */
package sm2;

import sm2._ast.ASTState;
import sm2._visitor.SM2Visitor;

/**
 * Counts the states of an sm2.
 */
public class CountStates implements SM2Visitor {
  private int count = 0;
  
  @Override
  public void visit(ASTState node) {
    count++;
  }
  
  public int getCount() {
    return count;
  }
}
