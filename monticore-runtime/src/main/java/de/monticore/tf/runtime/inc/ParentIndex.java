/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.runtime.inc;

import de.monticore.ast.ASTNode;
import de.monticore.ast.Comment;
import de.monticore.visitor.ITraverser;
import de.monticore.visitor.IVisitor;
import de.se_rwth.commons.logging.Log;
import org.jspecify.annotations.NonNull;

import javax.annotation.Nullable;
import java.util.Stack;

/**
 * Maintains parent relationships for AST nodes by storing parent references in
 * wrapped post-comments.
 *
 * @param <E> the traverser type used to initialize this index
 */
public class ParentIndex<E extends ITraverser> implements IModelIndex<E> {
  
  /**
   * Attaches parent information to a newly attached node.
   *
   * @param node the attached node
   * @param parent the parent the node was attached to
   */
  @Override
  public void onASTNodeAttach(@NonNull ASTNode node, @Nullable ASTNode parent) {
    if (parent != null) {
      attachComment(node, parent);
      Log.debug(() -> "Added parent comment to node with type %s!".formatted(node.getClass()), "ParentIndex");
    }
  }
  
  /**
   * Handles node detach events. No explicit action is required because parent
   * information is typically replaced when a node is reattached elsewhere.
   *
   * @param node the detached node
   * @param parent the parent the node was detached from
   */
  @Override
  public void onASTNodeDetach(@NonNull ASTNode node, @NonNull ASTNode parent) {
    // Most likely not needed, since children are either removed or moved to a new position
    // In that case, their parent comment should be overwritten
  }
  
  /**
   * Handles node modification events. Parent information does not need to be
   * updated for plain attribute changes.
   *
   * @param node the modified node
   * @param parent the parent containing the node
   * @param attributeName the name of the modified attribute
   * @param oldValue the previous attribute value
   * @param newValue the new attribute value
   */
  @Override
  public void onASTNodeModification(@NonNull ASTNode node, ASTNode parent, String attributeName,
      Object oldValue, Object newValue) {
    // Most likely not needed...
  }
  
  /**
   * Registers a visitor that records the current parent node while traversing
   * the AST and stores it on each visited child node.
   *
   * @param traverser the traverser to register this index into
   */
  @Override
  public void registerIntoTraverser(E traverser) {
    traverser.add4IVisitor(new IVisitor() {
      
      private final Stack<ASTNode> parent = new Stack<>();
      
      @Override
      public void visit(ASTNode node) {
        if (!parent.isEmpty()) {
          attachComment(node, parent.peek());
        }
        parent.push(node);
      }
      
      @Override
      public void endVisit(ASTNode node) {
        parent.pop();
      }
    });
  }
  
  /**
   * Stores the given parent reference on the node by using a wrapped
   * post-comment. Existing wrapped comments are updated, while regular comments
   * are preserved.
   *
   * @param node the node to enrich with parent information
   * @param parent the parent node to store
   */
  protected void attachComment(ASTNode node, ASTNode parent) {
    if (node.get_PostCommentList().isEmpty()) {
      node.get_PostCommentList().add(new WComment("", parent));
    } else {
      if (node.get_PostComment(0) instanceof WComment) {
        // if already set by a previous run: overwrite parent
        ((WComment) node.get_PostComment(0)).parent = parent;
      } else {
        // keep the comment
        node.get_PostCommentList()
            .set(0, new WComment(node.get_PostCommentList().getFirst(), parent));
      }
    }
  }
  
  /**
   * Returns the parent stored for the given node.
   *
   * @param node the node whose parent should be returned
   * @return the stored parent node
   */
  public ASTNode getParent(ASTNode node) {
    return ((WComment) node.get_PostCommentList().getFirst()).getParent();
  }
  
  /**
   * Comment wrapper that carries an additional parent reference for an AST
   * node.
   */
  public static class WComment extends Comment {
    
    protected ASTNode parent;
    
    /**
     * Creates a wrapped comment with the given text and parent reference.
     *
     * @param text the comment text
     * @param parent the stored parent node
     */
    public WComment(String text, ASTNode parent) {
      super(text);
      this.parent = parent;
    }
    
    /**
     * Creates a wrapped comment from an existing comment and stores the given
     * parent reference while preserving source positions.
     *
     * @param c the original comment
     * @param parent the stored parent node
     */
    public WComment(Comment c, ASTNode parent) {
      this(c.getText(), parent);
      this.set_SourcePositionStart(c.get_SourcePositionStart());
      this.set_SourcePositionEnd(c.get_SourcePositionEnd());
    }
    
    /**
     * Returns the stored parent node.
     *
     * @return the parent node
     */
    public ASTNode getParent() {
      return parent;
    }
  }
}
