/* (c)  https://github.com/MontiCore/monticore */
package mc.feature.hwc.statechartdsl._ast;

public class ASTStateBuilder extends ASTStateBuilderTOP {

  protected ASTStateBuilder() {}

  public ASTStateBuilder setName(String name) {
    // For testing Purposes only: we adapt the name method
    this.name = name+"Suf1";
    return this;
  }
}
