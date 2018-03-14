/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.mocks.languages.entity.asts;

import de.monticore.symboltable.mocks.asts.ASTSymbol;
import de.monticore.symboltable.mocks.asts.ASTSymbolReference;

public class ASTProperty extends ASTSymbol implements ASTEntityBase {
  
  private ASTSymbolReference reference;

  public ASTProperty() {
    setSpansScope(false);
    setDefinesNamespace(false);
  }
  
  /**
   * @param reference the reference to set
   */
  public void setReference(ASTSymbolReference reference) {
    this.reference = reference;
    addChild(reference);
  }
  
  /**
   * @return reference
   */
  public ASTSymbolReference getReference() {
    return this.reference;
  }

  @Override
  public void accept(EntityLanguageVisitor visitor) {
    visitor.traverse(this);
  }
}
