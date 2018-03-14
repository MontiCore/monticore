/* (c) https://github.com/MontiCore/monticore */

package mc.feature.hwc.statechartdsl._ast;

public class StatechartDSLMill extends StatechartDSLMillTOP {
  
  @Override
  protected  ASTTransitionBuilder _transitionBuilder()   {
     return new MyTransitionBuilder();
  }

}
