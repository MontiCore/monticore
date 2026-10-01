/* (c) https://github.com/MontiCore/monticore */

package mc.feature.hwc.statechartdsl._ast;

public class ASTStatechart extends mc.feature.hwc.statechartdsl._ast.ASTStatechartTOP {
  
  protected ASTStatechart()
  {
    super();
  }
  
  public String toString() {
    return "My statechart is " + getName();
  }
  
}
