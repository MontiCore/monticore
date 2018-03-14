/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.mocks.languages.entity.asts;

import de.monticore.symboltable.mocks.asts.ASTSymbol;

public class ASTEntity extends ASTSymbol implements ASTEntityBase {

  public ASTEntity() {
    setSpansScope(true);
    setDefinesNamespace(true);
  }
  
  @Override
  public void accept(EntityLanguageVisitor visitor) {
    visitor.traverse(this);
  }
}
