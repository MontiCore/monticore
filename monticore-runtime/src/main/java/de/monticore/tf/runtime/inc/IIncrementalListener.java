package de.monticore.tf.runtime.inc;

import de.monticore.ast.ASTNode;

public interface IIncrementalListener {
  
  void onASTNodeAttach(ASTNode node, ASTNode parent);
  
  void onASTNodeDetach(ASTNode node, ASTNode parent);
  
  void onASTNodeModification(ASTNode node, ASTNode parent, String attributeName, Object oldValue, Object newValue);
}
