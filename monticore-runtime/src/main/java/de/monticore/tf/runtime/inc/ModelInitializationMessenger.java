package de.monticore.tf.runtime.inc;

import de.monticore.ast.ASTNode;
import de.monticore.visitor.ITraverser;
import de.monticore.visitor.IVisitor;

import java.util.Stack;
import java.util.function.Supplier;

/**
 * Simulates the construction of an already existing model for a
 * {@link IModelAccessor}.
 *
 * <p>When a model accessor is created, its internal indices have to be
 * initialized. Instead of rebuilding the model with a dedicated
 * implementation, this messenger traverses the existing model and reports the
 * visited nodes to the accessor. This effectively simulates the original model
 * construction process so that the accessor can rebuild its internal state.
 * Note: Only attachment notifications are emitted, no modification notifications!
 *
 */
public class ModelInitializationMessenger {
  
  protected final ITraverser traverser;
  protected final IModelAccessor accessor;
  protected final Stack<ASTNode> parentStack;
  
  /**
   * Creates a messenger that traverses an existing model and replays its
   * structure to the given accessor.
   *
   * @param accessor accessor whose indices are initialized from the traversal
   * @param traverser traverser used to visit all nodes in the model
   */
  public ModelInitializationMessenger(IModelAccessor accessor, Supplier<ITraverser> traverser) {
    this.accessor = accessor;
    this.traverser = traverser.get();
    this.parentStack = new Stack<>();
    
    setupTraverser();
  }
  
  /**
   * Registers a visitor on the traverser that tracks parent-child relations
   * during traversal and notifies the accessor once a node has been processed.
   */
  protected void setupTraverser() {
    this.traverser.add4IVisitor(new IVisitor() {
      
      @Override
      public void visit(ASTNode node) {
        ASTNode parent = parentStack.isEmpty() ? null : parentStack.peek();
        accessor.notifyNodeCreation(node);
        accessor.notifyNodeAttach(node, parent);
        
        parentStack.push(node);
      }
      
      @Override
      public void endVisit(ASTNode node) {
        parentStack.pop();
      }
    });
  }
  
  /**
   * Traverses the given model once to initialize the accessor's indices as if
   * the model had just been built.
   *
   * @param root root node of the model whose structure should be replayed
   */
  public void initialize(ASTNode root) {
    this.parentStack.clear();
    root.accept(this.traverser);
    this.parentStack.clear();
  }
}
