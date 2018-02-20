/* (c) https://github.com/MontiCore/monticore */

package de.monticore.ast;

import java.util.Collection;
import java.util.List;

import de.monticore.ast.ASTNode;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * Mock for ASTCNode.
 *
 * @author  (last commit) $Author$
 *          $Date$
 *
 */
public class ASTCNodeMock extends ASTCNode {

  public static final ASTCNode INSTANCE = new ASTCNodeMock();
  
  private List<ASTNode> children = Lists.newArrayList();
    
  public void addChild(ASTNode child) {
    children.add(child);
  }

  /**
   * @see de.monticore.ast.ASTNode#get_Children()
   */
  @Override
  public Collection<ASTNode> get_Children() {
    return ImmutableList.copyOf(children);
  }

  /**
   * @see de.monticore.ast.ASTNode#remove_Child(de.monticore.ast.ASTNode)
   */
  @Override
  public void remove_Child(ASTNode child) {
  }

  /**
   * @see de.monticore.ast.ASTCNode#deepClone()
   */
  @Override
  public ASTNode deepClone() {
    return null;
  }

  
}
