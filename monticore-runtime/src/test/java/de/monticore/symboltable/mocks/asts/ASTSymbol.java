/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.mocks.asts;

import java.util.Optional;
import com.google.common.base.Preconditions;
import de.monticore.ast.ASTNode;

public abstract class ASTSymbol extends ASTNodeMock {
  
  private String name;
  
  private boolean spansScope;
  private boolean definesNamespace;
  
  public ASTSymbol() {
  }

  /**
   * @param spansScope the spansScope to set
   */
  public void setSpansScope(boolean spansScope) {
    this.spansScope = spansScope;
  }
  
  public boolean spansScope() {
    return spansScope;
  }
  
  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }
  
  /**
   * @return name
   */
  public String getName() {
    return this.name;
  }
  
  /**
   * @param definesNamespace the definesNamespace to set
   */
  public void setDefinesNamespace(boolean definesNamespace) {
    Preconditions.checkArgument(!definesNamespace || spansScope, "Cannot define a namespace without spanning a scope");
    
    this.definesNamespace = definesNamespace;
  }
  
  public boolean definesNamespace() {
    return definesNamespace;
  }
  
  // TODO GV -> PN
  /*
  public Optional<ASTSymbol> getFirstParentWithScope() {
    ASTNode parent = get_Parent();
    
    while(parent != null) {
      if (parent instanceof ASTSymbol) {
        ASTSymbol symbolParent = (ASTSymbol) parent;
        if (symbolParent.spansScope()) {
          return Optional.of(symbolParent);
        }
      }
    }
    
    return Optional.empty();
  }*/
  
}
