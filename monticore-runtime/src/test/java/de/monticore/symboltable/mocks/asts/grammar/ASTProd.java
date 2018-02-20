/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.mocks.asts.grammar;

import com.google.common.collect.Lists;
import de.monticore.symboltable.mocks.asts.ASTNodeMock;
import de.monticore.ast.ASTNode;

import java.util.List;

public class ASTProd extends ASTNodeMock {
  
  private String name;
  
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
  
  public List<ASTProd> getSubProductions() {
    List<ASTProd> subProductions = Lists.newArrayList();
    
    for (ASTNode child : get_Children()) {
      if (child instanceof ASTProd) {
        subProductions.add((ASTProd) child);
      }
    }
    
    return subProductions;
  }
  
  /**
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return getClass().getSimpleName() + " " + getName();
  }

}
