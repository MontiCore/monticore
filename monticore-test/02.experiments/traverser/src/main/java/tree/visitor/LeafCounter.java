// (c) https://github.com/MontiCore/monticore
package tree.visitor;

import trees._visitor.TreesVisitor2;

public class LeafCounter implements TreesVisitor2 {
  
  int count = 0;
  
  public LeafCounter() {
    count = 0;
  }
  
  @Override
  public void visit(trees._ast.ASTLeaf node) {
    count++;
  }
  
  public void resetCount() {
    count = 0;
  }
  
  public int getCount() {
    return count;
  }
  
}
