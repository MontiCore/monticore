/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.mocks.asts;

import java.util.Collection;
import java.util.List;

import de.monticore.ast.ASTNode;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import de.monticore.ast.ASTCNode;

public abstract class ASTNodeMock extends ASTCNode {

  private List<ASTNode> children = Lists.newArrayList();
  
  public void addChild(ASTNode child) {
    Preconditions.checkArgument(child != this);
    
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
  public ASTNodeMock deepClone() {
    return null;
  }
  
}
