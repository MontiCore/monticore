/* (c) https://github.com/MontiCore/monticore */

package mc.feature.visitor;

import mc.feature.visitor.sup._ast.ASTSomeNode;
import mc.feature.visitor.sup._visitor.SupVisitor2;

public class NodeCounter implements SupVisitor2 {
  
  protected int num = 0;

  @Override
  public void visit(ASTSomeNode node) {
    num++;
  }
  
  public int getNum() {
    return num;
  }
  
  public void setNum(int num) {
    this.num = num;
  }
  
}
