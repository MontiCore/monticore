/* (c) https://github.com/MontiCore/monticore */
package mc.feature.hwc.tagging2._ast;

public class ASTTaggingUnit extends ASTTaggingUnitTOP {

  private String name;

  public ASTTaggingUnit() {
    super();
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
