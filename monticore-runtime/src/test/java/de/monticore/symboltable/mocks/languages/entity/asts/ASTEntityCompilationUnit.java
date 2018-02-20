/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.mocks.languages.entity.asts;

import de.monticore.symboltable.mocks.asts.ASTCompilationUnit;

public class ASTEntityCompilationUnit extends ASTCompilationUnit implements ASTEntityBase {

  private ASTEntity classNode;

  public void setClassNode(ASTEntity classNode) {
    this.classNode = classNode;
    addChild(classNode);
  }

  public ASTEntity getClassNode() {
    return classNode;
  }

  @Override
  public void accept(EntityLanguageVisitor visitor) {
    visitor.traverse(this);
  }
}
