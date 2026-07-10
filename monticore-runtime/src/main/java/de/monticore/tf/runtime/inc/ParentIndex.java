package de.monticore.tf.runtime.inc;

import de.monticore.ast.ASTNode;
import de.monticore.ast.Comment;
import de.monticore.visitor.ITraverser;
import de.monticore.visitor.IVisitor;
import de.se_rwth.commons.logging.Log;

import java.util.Stack;

public class ParentIndex<E extends ITraverser> implements IModelIndex<E> {
  
  @Override
  public void onASTNodeAddition(ASTNode node, ASTNode parent) {
    attachComment(node, parent);
    Log.debug(() -> "Added parent comment to node with type %s!".formatted(node.getClass()), "ParentIndex");
  }
  
  @Override
  public void onASTNodeRemoval(ASTNode node, ASTNode parent) {
    // Most likely not needed, since children are either removed or moved to a new position
    // In that case, their parent comment should be overwritten
  }
  
  @Override
  public void onASTNodeModification(ASTNode node, ASTNode parent) {
    // Most likely not needed...
  }
  
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
  
  public ASTNode getParent(ASTNode node) {
    return ((WComment) node.get_PostCommentList().getFirst()).getParent();
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
