/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.mocks.asts.grammar;

import de.monticore.symboltable.mocks.asts.ASTNodeMock;

public class ASTSymbolProdReference extends ASTNodeMock {
 
  // TODO PN die Reference wird später über die Symtab aufgelöst.
  private ASTSymbolProd referencedProd;
  private String referenceName;
  
  private boolean include = true;
  
  public ASTSymbolProdReference(ASTSymbolProd referencedProd, String referenceName) {
    this.referencedProd = referencedProd;
    this.referenceName = referenceName;
  }
  
  public ASTSymbolProd getReferencedProd() {
    return this.referencedProd;
  }
  
  public String getReferenceName() {
    return this.referenceName;
  }
  
  public void setSubRule(boolean include) {
    this.include = include;
  }
  
  /**
   * 
   * @return true, if referenced production is a sub rule
   */
  public boolean isSubRule() {
    return include;
  }
  
  
  
}
