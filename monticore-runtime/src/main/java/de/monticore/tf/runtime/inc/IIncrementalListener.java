package de.monticore.tf.runtime.inc;

import de.monticore.ast.ASTNode;

public interface IIncrementalListener {
  
  void onASTNodeAddition(ASTNode node, ASTNode parent);
  
  void onASTNodeRemoval(ASTNode node, ASTNode parent);
  
  void onASTNodeModification(ASTNode node, ASTNode parent);
}
