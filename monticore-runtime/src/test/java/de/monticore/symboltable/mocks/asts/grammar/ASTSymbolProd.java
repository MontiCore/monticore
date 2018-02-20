/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.mocks.asts.grammar;

import de.monticore.ast.ASTNode;

public class ASTSymbolProd extends ASTProd {

  private boolean definesNamespace;
  
  /**
   * @return true, if a child ast exists that is a symbol (definition) production.
   */
  public boolean spansScope() {
    return containsSymbolProductionReference(this);
  }

  private boolean containsSymbolProductionReference(ASTNode node) {
    for(ASTNode child : node.get_Children()) {
      if (((child instanceof ASTSymbolProdReference) && ((ASTSymbolProdReference)child).isSubRule()) 
          || containsSymbolProductionReference(child)) {
        // at least one child found
        return true;
      }
    }
    
    return false;
  }
  
  public boolean definesNamespace() {
    return definesNamespace;
  }
  
  public void setDefinesNamespace(boolean definesNamespace) {
    // TODO PN wird beim Parsen schon erkannt, dass es ein Namespace definiert oder wird es eher
    //         dynamisch berechnet (beim Generieren der Symtab), z.B. hasNameProduction()
    this.definesNamespace = definesNamespace;
  }
}
