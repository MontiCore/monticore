package de.monticore.tf.runtime.inc;

import de.monticore.ast.ASTNode;
import de.monticore.visitor.ITraverser;
import de.monticore.visitor.IVisitor;

import java.util.Stack;

/**
 * Simulates the construction of an already existing model for a
 * {@link IModelAccessor}.
 *
 * <p>When a model accessor is created, its internal indices have to be
 * initialized. Instead of rebuilding the model with a dedicated
 * implementation, this messenger traverses the existing model and reports the
 * visited nodes to the accessor. This effectively simulates the original model
 * construction process so that the accessor can rebuild its internal state.
 *
 * @param <E> concrete traverser type used to visit the model
 */
public class ModelInitializationMessenger<E extends ITraverser> {
  
  protected final E traverser;
  protected final IModelAccessor<E> accessor;
  protected final Stack<ASTNode> parentStack;
  
  /**
   * Creates a messenger that traverses an existing model and replays its
   * structure to the given accessor.
   *
   * @param accessor accessor whose indices are initialized from the traversal
   * @param traverser traverser used to visit all nodes in the model
   */
  public ModelInitializationMessenger(IModelAccessor<E> accessor, E traverser) {
    this.accessor = accessor;
    this.traverser = traverser;
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
        parentStack.push(node);
      }
      
      @Override
      public void endVisit(ASTNode node) {
        parentStack.pop();
        
        ASTNode parent = parentStack.isEmpty() ? null : parentStack.peek();
        accessor.notifyNodeAttach(node, parent);
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
