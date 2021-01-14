/* (c) https://github.com/MontiCore/monticore */

package de.monticore.ast;

import com.google.common.collect.Lists;
import de.monticore.symboltable.IScope;

import java.util.List;

/**
 * Mock for ASTCNode.
 *
 */
public class ASTCNodeMock extends ASTCNode {

  public static final ASTCNode INSTANCE = new ASTCNodeMock();
  
  private List<ASTNode> children = Lists.newArrayList();
    
  public void addChild(ASTNode child) {
    children.add(child);
  }


  @Override public IScope getEnclosingScope() {
    return null;
  }

  /**
   * @see de.monticore.ast.ASTCNode#deepClone()
   */
  @Override
  public ASTNode deepClone() {
    return null;
  }

  
}
