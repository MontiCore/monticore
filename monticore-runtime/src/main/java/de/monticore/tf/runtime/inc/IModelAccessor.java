package de.monticore.tf.runtime.inc;

import de.monticore.ast.ASTNode;
import de.monticore.visitor.ITraverser;

public interface IModelAccessor<E extends ITraverser> {
  
  void notifyNodeAttach(ASTNode node, ASTNode parent);
  
  void notifyNodeDetach(ASTNode node, ASTNode parent);
  
  void notifyModification(ASTNode node, ASTNode parent, String attributeName, Object oldValue, Object newValue);
  
  IndexHandler<E> indices();
}
