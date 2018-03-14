/* (c) https://github.com/MontiCore/monticore */

package de.monticore.ast;


public class ASTNodeNull extends ASTCNode {

  /**
   * @see de.monticore.ast.ASTCNode#deepClone()
   */
  @Override
  public ASTNode deepClone() {
    return new ASTNodeNull();
  }
  
}
