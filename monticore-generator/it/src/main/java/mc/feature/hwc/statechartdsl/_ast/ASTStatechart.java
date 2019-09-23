/* (c) https://github.com/MontiCore/monticore */

package mc.feature.hwc.statechartdsl._ast;

import java.util.List;

import mc.feature.hwc.statechartdsl._ast.ASTCode;
import mc.feature.hwc.statechartdsl._ast.ASTState;
import mc.feature.hwc.statechartdsl._ast.ASTTransition;

public class ASTStatechart extends mc.feature.hwc.statechartdsl._ast.ASTStatechartTOP {
  
  protected ASTStatechart()
  {
    super();
  }
  
  public String toString() {
    return "My statechart is " + getName();
  }
  
}
