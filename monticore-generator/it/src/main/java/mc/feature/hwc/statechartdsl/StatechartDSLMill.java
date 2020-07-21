/* (c) https://github.com/MontiCore/monticore */

package mc.feature.hwc.statechartdsl;

import mc.feature.hwc.statechartdsl._ast.ASTTransitionBuilder;
import mc.feature.hwc.statechartdsl._ast.MyTransitionBuilder;

public class StatechartDSLMill extends StatechartDSLMillTOP {
  
  @Override
  protected ASTTransitionBuilder _transitionBuilder()   {
     return new MyTransitionBuilder();
  }

}
