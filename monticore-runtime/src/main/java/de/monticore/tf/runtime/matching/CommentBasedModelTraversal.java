/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.runtime.matching;

import de.monticore.ast.ASTNode;
import de.monticore.ast.Comment;
import de.monticore.visitor.ITraverser;

import java.util.*;

/**
 * This CommentBasedModelTraversal is a more efficient implementation of the default
 * ModelTraversal. Instead of using a HashMap to store node parents, which showed unexpected
 * performance issues due to the suboptimal hash function of ASTNodes, this Traversal stores
 * node parents as Comment objects in the respective child nodes.
 * Thereby map lookups are prevented and replaced by efficient direct lookups.
 *
 * @param <E> a language traverser type
 */
public class CommentBasedModelTraversal<E extends ITraverser> extends ModelTraversal<E> {
  
  protected CommentBasedModelTraversal(E traverser) {
    super(traverser);
  }
  
  @Override
  public ASTNode getParent(ASTNode node) {
    return ((WComment) node.get_PostCommentList().get(0)).getParent();
  }
  
  public void init() {
    for (Map.Entry<ASTNode, ASTNode> node : this.getParents().entrySet()) {
      if (node.getKey().get_PostCommentList().isEmpty()) {
        node.getKey().get_PostCommentList().add(new WComment("", node.getValue()));
      } else {
        if (node.getKey().get_PostComment(0) instanceof WComment) {
          // if already set by a previous run: overwrite parent
          ((WComment) node.getKey().get_PostComment(0)).parent = node.getValue();
        } else {
          // keep the comment
          node.getKey().get_PostCommentList()
                  .set(0, new WComment(node.getKey().get_PostCommentList().get(0), node.getValue()));
        }
      }
    }
  }
  
  public static class WComment extends Comment {
    
    protected ASTNode parent;
    
    public WComment(String text, ASTNode parent) {
      super(text);
      this.parent = parent;
    }
    
    public WComment(Comment c, ASTNode parent) {
      this(c.getText(), parent);
      this.set_SourcePositionStart(c.get_SourcePositionStart());
      this.set_SourcePositionEnd(c.get_SourcePositionEnd());
    }
    
    public ASTNode getParent() {
      return parent;
    }
  }
  
}
