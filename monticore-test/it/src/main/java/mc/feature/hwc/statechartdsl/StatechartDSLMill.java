/* (c) https://github.com/MontiCore/monticore */

package mc.feature.hwc.statechartdsl;

import mc.feature.hwc.statechartdsl._ast.ASTStatechartBuilder;
import mc.feature.hwc.statechartdsl._ast.ASTTransitionBuilder;
import mc.feature.hwc.statechartdsl._ast.MyTransitionBuilder;

public class StatechartDSLMill extends StatechartDSLMillTOP {

  @Override
  protected ASTStatechartBuilder _statechartBuilder() {
    ASTStatechartBuilder b = super._statechartBuilder();
    b.setName("default");
    return b;
  }

  @Override
  protected ASTTransitionBuilder _transitionBuilder()   {
     return new MyTransitionBuilder();
  }

}
