package de.monticore.tf.runtime.inc;

import de.monticore.ast.ASTNode;
import de.monticore.visitor.ITraverser;

public interface IModelAccessor<E extends ITraverser> {
  
  void notifyAdd(ASTNode node, ASTNode parent);
  
  void notifyDeletion(ASTNode node, ASTNode parent);
  
  void notifyModification(ASTNode node, ASTNode parent);
  
  IndexHandler<E> indices();
}
