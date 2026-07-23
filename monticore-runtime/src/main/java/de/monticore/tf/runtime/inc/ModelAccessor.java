/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.runtime.inc;

import de.monticore.ast.ASTNode;
import de.monticore.visitor.ITraverser;
import org.jspecify.annotations.NonNull;

import java.util.*;

/**
 * Provides a facade for propagating model change notifications and accessing
 * the managed indices.
 *
 * @param <E> the traverser type used to initialize the underlying indices
 */
public class ModelAccessor<E extends ITraverser> implements IModelAccessor<E> {
  
  private final IndexHandler indexHandler;
  
  private final Set<IIncrementalListener> listeners;
  
  /**
   * Creates a model accessor with the default indices and the given root nodes.
   *
   * @param traverser the traverser used to initialize the indices
   * @param roots the root nodes used for initialization
   */
  public ModelAccessor(E traverser, ASTNode... roots) {
    this(traverser, Arrays.stream(roots).toList());
  }
  
  /**
   * Creates a model accessor with the given custom indices and root nodes.
   *
   * @param traverser the traverser used to initialize the indices
   * @param customIndices the custom indices to register by name
   * @param roots the root nodes used for initialization
   */
  public ModelAccessor(E traverser, Map<String, IModelIndex> customIndices, ASTNode... roots) {
    this(traverser, Arrays.stream(roots).toList(), customIndices);
  }
  
  /**
   * Creates a model accessor with the default indices and the given root nodes.
   *
   * @param traverser the traverser used to initialize the indices
   * @param roots the root nodes used for initialization
   */
  public ModelAccessor(E traverser, List<ASTNode> roots) {
    this(traverser, roots, new HashMap<>(), new HashSet<>());
  }
  
  /**
   * Creates a model accessor with the given custom indices and root nodes.
   *
   * @param traverser the traverser used to initialize the indices
   * @param roots the root nodes used for initialization
   * @param customIndices the custom indices to register by name
   */
  public ModelAccessor(E traverser, List<ASTNode> roots, Map<String, IModelIndex> customIndices) {
    this(traverser, roots, customIndices, new HashSet<>());
  }
  
  /**
   * Creates a model accessor with explicit listener set injection.
   *
   * <p>This constructor is primarily intended for internal use and tests where
   * the listener collection should be preconfigured.</p>
   *
   * @param traverser the traverser used to initialize the indices
   * @param roots the root nodes used for initialization
   * @param customIndices the custom indices to register by name
   * @param listeners listeners that should receive incremental model events
   */
  protected ModelAccessor(E traverser, List<ASTNode> roots, Map<String, IModelIndex> customIndices, Set<IIncrementalListener> listeners) {
    this.indexHandler = new IndexHandler(customIndices);
    this.listeners = listeners;
    
    ModelInitializationMessenger<E> initializationMessenger = new ModelInitializationMessenger<>(this, traverser);
    roots.forEach(initializationMessenger::initialize);
    
    this.indexHandler.finalizeInitialization();
  }
  
  /**
   * Attaches a listener to receive incremental model change notifications.
   *
   * @param listener the listener to attach
   */
  public void attachListener(IIncrementalListener listener) {
    this.listeners.add(listener);
  }
  
  /**
   * Detaches a listener from receiving incremental model change notifications.
   *
   * @param listener the listener to detach
   * @return true if the listener was removed, false otherwise
   */
  public boolean detachListener(IIncrementalListener listener) {
    return this.listeners.remove(listener);
  }
  
  /**
   * Forwards the start of a transformation to the underlying index handler and listeners.
   *
   * @param transformationName the name of the transformation
   */
  @Override
  public void notifyTransformationStart(@NonNull String transformationName) {
    this.indexHandler.onTransformationStart(transformationName);
    
    this.listeners.forEach(listener -> listener.onTransformationStart(transformationName));
  }
  
  /**
   * Forwards the end of a transformation to the underlying index handler and listeners.
   *
   * @param transformationName the name of the transformation
   */
  @Override
  public void notifyTransformationEnd(@NonNull String transformationName) {
    this.indexHandler.onTransformationEnd(transformationName);
    
    this.listeners.forEach(listener -> listener.onTransformationEnd(transformationName));
  }
  
  /**
   * Forwards a node attach notification to the underlying index handler and listeners.
   *
   * @param node the attached node
   * @param parent the parent the node was attached to
   */
  @Override
  public void notifyNodeAttach(@NonNull ASTNode node, ASTNode parent) {
    this.indexHandler.onASTNodeAttach(node, parent);
    
    this.listeners.forEach(listener -> listener.onASTNodeAttach(node, parent));
  }
  
  /**
   * Forwards a node detach notification to the underlying index handler and listeners.
   *
   * @param node the detached node
   * @param parent the parent the node was detached from
   */
  @Override
  public void notifyNodeDetach(@NonNull ASTNode node, @NonNull ASTNode parent) {
    this.indexHandler.onASTNodeDetach(node, parent);
    
    this.listeners.forEach(listener -> listener.onASTNodeDetach(node, parent));
  }
  
  /**
   * Forwards an attribute modification notification to the underlying index
   * handler and listeners.
   *
   * @param node the modified node
   * @param parent the parent containing the node
   * @param attributeName the name of the modified attribute
   * @param oldValue the previous attribute value
   * @param newValue the new attribute value
   */
  @Override
  public void notifyModification(@NonNull ASTNode node, ASTNode parent, String attributeName, Object oldValue, Object newValue) {
    this.indexHandler.onASTNodeModification(node, parent, attributeName, oldValue, newValue);
    
    this.listeners.forEach(listener -> listener.onASTNodeModification(node, parent, attributeName, oldValue, newValue));
  }
  
  /**
   * Returns the index handler managed by this accessor.
   *
   * @return the index handler
   */
  @Override
  public IndexHandler indices() {
    return this.indexHandler;
  }
  
  /**
   * Returns the listeners currently registered for incremental model changes.
   *
   * @return a collection of listeners
   */
  @Override
  public Collection<IIncrementalListener> listeners() {
    return this.listeners;
  }
}
