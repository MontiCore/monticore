/* (c) https://github.com/MontiCore/monticore */

package mc.feature.hwc.statechartdsl._ast;

public class MyTransitionBuilder extends ASTTransitionBuilder {
  
  public ASTTransitionBuilder setFrom(String from) {
    this.from = from + "Suf2";
    return this;
  }
  
}
