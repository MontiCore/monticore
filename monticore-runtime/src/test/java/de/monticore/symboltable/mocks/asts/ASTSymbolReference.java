/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.mocks.asts;

public class ASTSymbolReference extends ASTNodeMock {
  
  private String symbolName;
  
  private Class<? extends ASTSymbol> referencedSymbolClass;
  
  public ASTSymbolReference(Class<? extends ASTSymbol> referencedClass) {
    this.referencedSymbolClass = referencedClass;
  }
  
  public void setSymbolName(String symbolName) {
    this.symbolName = symbolName;
  }
  
  public String getSymbolName() {
    return this.symbolName;
  }
  
  /**
   * @return referencedSymbolClass
   */
  public Class<? extends ASTSymbol> getReferencedSymbolClass() {
    return this.referencedSymbolClass;
  }
  
}
