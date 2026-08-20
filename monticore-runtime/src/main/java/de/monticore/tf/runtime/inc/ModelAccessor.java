/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.runtime.inc;

import de.monticore.ast.ASTNode;
import de.monticore.visitor.ITraverser;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Supplier;

/**
 * Provides a facade for propagating model change notifications and accessing
 * the managed indices.
 *
 */
public class ModelAccessor implements IModelAccessor {
  
  private final ParentIndex parentIndex;
  
  private final CandidateIndex candidateIndex;
  
  private final Map<String, IModelIndex> customIndices;
  
  private final Set<IIncrementalListener> listeners;
  
  /**
   * Creates a model accessor with the default indices and the given root nodes.
   *
   * @param traverser the traverser used to initialize the indices
   * @param roots the root nodes used for initialization
   */
  public ModelAccessor(Supplier<ITraverser> traverser, ASTNode... roots) {
    this(traverser, Arrays.stream(roots).toList());
  }
  
  /**
   * Creates a model accessor with the given custom indices and root nodes.
   *
   * @param traverser the traverser used to initialize the indices
   * @param customIndices the custom indices to register by name
   * @param roots the root nodes used for initialization
   */
  public ModelAccessor(Supplier<ITraverser> traverser, Map<String, IModelIndex> customIndices, ASTNode... roots) {
    this(traverser, Arrays.stream(roots).toList(), customIndices);
  }
  
  /**
   * Creates a model accessor with the default indices and the given root nodes.
   *
   * @param traverser the traverser used to initialize the indices
   * @param roots the root nodes used for initialization
   */
  public ModelAccessor(Supplier<ITraverser> traverser, List<ASTNode> roots) {
    this(traverser, roots, new HashMap<>(), new HashSet<>());
  }
  
  /**
   * Creates a model accessor with the given custom indices and root nodes.
   *
   * @param traverser the traverser used to initialize the indices
   * @param roots the root nodes used for initialization
   * @param customIndices the custom indices to register by name
   */
  public ModelAccessor(Supplier<ITraverser> traverser, List<ASTNode> roots, Map<String, IModelIndex> customIndices) {
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
  protected ModelAccessor(Supplier<ITraverser> traverser, List<ASTNode> roots, Map<String, IModelIndex> customIndices, Set<IIncrementalListener> listeners) {
    this.parentIndex = new ParentIndex();
    this.candidateIndex = new CandidateIndex(traverser);
    this.customIndices = new HashMap<>(customIndices);
    this.listeners = listeners;
    
    initialize(traverser, roots);
  }

  /**
   * Initializes the managed indices by traversing all given root nodes and
   * finalizing the initialization afterwards.
   *
   * <p>A {@link ModelInitializationMessenger} is used to replay the initial
   * model structure as a sequence of creation and attach events so that all
   * indices and listeners are brought into a consistent state before any
   * transformation is applied.</p>
   *
   * @param traverser supplier for the traverser used during initialization
   * @param roots the root nodes whose subtrees are traversed for initialization
   */
  protected void initialize(Supplier<ITraverser> traverser, List<ASTNode> roots) {
    ModelInitializationMessenger initializationMessenger = new ModelInitializationMessenger(this, traverser);
    roots.forEach(initializationMessenger::initialize);

    finalizeInitialization();
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
   * Forwards a transformation start notification to the managed indices and listeners.
   *
   * @param transformationName the name of the transformation
   */
  @Override
  public void notifyTransformationStart(@Nonnull String transformationName) {
    this.parentIndex.onTransformationStart(transformationName);
    this.candidateIndex.onTransformationStart(transformationName);
    this.customIndices.values().forEach(index -> index.onTransformationStart(transformationName));
    this.listeners.forEach(listener -> listener.onTransformationStart(transformationName));
  }
  
  /**
   * Forwards a transformation end notification to the managed indices and listeners.
   *
   * @param transformationName the name of the transformation
   */
  @Override
  public void notifyTransformationEnd(@Nonnull String transformationName) {
    this.parentIndex.onTransformationEnd(transformationName);
    this.candidateIndex.onTransformationEnd(transformationName);
    this.customIndices.values().forEach(index -> index.onTransformationEnd(transformationName));
    this.listeners.forEach(listener -> listener.onTransformationEnd(transformationName));
  }

  /**
   * Forwards a node creation notification to the managed indices and listeners.
   *
   * @param node the newly created node
   */
  @Override
  public void notifyNodeCreation(@Nonnull ASTNode node) {
    this.parentIndex.onASTNodeCreation(node);
    this.candidateIndex.onASTNodeCreation(node);
    this.customIndices.values().forEach(index -> index.onASTNodeCreation(node));
    this.listeners.forEach(listener -> listener.onASTNodeCreation(node));
  }

  /**
   * Forwards a node attach notification to the managed indices and listeners.
   *
   * @param node the attached node
   * @param parent the parent the node was attached to, or {@code null} if the node is a root node
   */
  @Override
  public void notifyNodeAttach(@Nonnull ASTNode node, @Nullable ASTNode parent) {
    this.parentIndex.onASTNodeAttach(node, parent);
    this.candidateIndex.onASTNodeAttach(node, parent);
    this.customIndices.values().forEach(index -> index.onASTNodeAttach(node, parent));
    this.listeners.forEach(listener -> listener.onASTNodeAttach(node, parent));
  }
  
  /**
   * Forwards a node detach notification to the managed indices and listeners.
   *
   * @param node the detached node
   * @param parent the parent the node was detached from
   */
  @Override
  public void notifyNodeDetach(@Nonnull ASTNode node, @Nonnull ASTNode parent) {
    this.parentIndex.onASTNodeDetach(node, parent);
    this.candidateIndex.onASTNodeDetach(node, parent);
    this.customIndices.values().forEach(index -> index.onASTNodeDetach(node, parent));
    this.listeners.forEach(listener -> listener.onASTNodeDetach(node, parent));
  }
  
  /**
   * Forwards an attribute modification notification to the managed indices
   * and listeners.
   *
   * @param node the modified node
   * @param attributeName the name of the modified attribute
   * @param modificationOp the type of modification
   * @param oldValue the previous attribute value, or {@code null} if not applicable
   * @param newValue the new attribute value, or {@code null} if not applicable
   */
  @Override
  public void notifyModification(@Nonnull ASTNode node, String attributeName, ModificationOp modificationOp, @Nullable Object oldValue,
      @Nullable Object newValue) {
    this.parentIndex.onASTNodeModification(node, attributeName, modificationOp, oldValue, newValue);
    this.candidateIndex.onASTNodeModification(node, attributeName, modificationOp, oldValue, newValue);
    this.customIndices.values()
        .forEach(index -> index.onASTNodeModification(node, attributeName, modificationOp, oldValue, newValue));
    this.listeners.forEach(
        listener -> listener.onASTNodeModification(node, attributeName, modificationOp, oldValue, newValue));
  }
  
  /**
   * Forwards a list attribute modification notification to the managed indices
   * and listeners.
   *
   * @param node the modified node
   * @param attributeName the name of the modified list attribute
   * @param idx the index of the modified element within the list
   * @param modificationOp the type of list-element modification
   * @param oldValue the previous value of the element, or {@code null} if not applicable
   * @param newValue the new value of the element, or {@code null} if not applicable
   */
  @Override
  public void notifyListModification(@Nonnull ASTNode node, String attributeName, int idx,
      ModificationOp modificationOp, @Nullable Object oldValue, @Nullable Object newValue) {
    this.parentIndex.onASTNodeListModification(node, attributeName, idx, modificationOp, oldValue, newValue);
    this.candidateIndex.onASTNodeListModification(node, attributeName, idx, modificationOp, oldValue, newValue);
    this.customIndices.values().forEach(
        index -> index.onASTNodeListModification(node, attributeName, idx, modificationOp, oldValue, newValue));
    this.listeners.forEach(
        listener -> listener.onASTNodeListModification(node, attributeName, idx, modificationOp, oldValue,
            newValue));
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
  
  /**
   * Returns the built-in candidate index.
   *
   * @return the candidate index
   */
  public CandidateIndex getCandidateIndex() {
    return candidateIndex;
  }
  
  /**
   * Returns the built-in parent index.
   *
   * @return the parent index
   */
  public ParentIndex getParentIndex() {
    return parentIndex;
  }
  
  /**
   * Checks whether a custom index with the given name exists.
   *
   * @param name the index name
   * @return {@code true} if a custom index with the given name is registered
   */
  public boolean hasCustomIndex(String name) {
    return this.customIndices.containsKey(name);
  }
  
  /**
   * Returns the custom index registered under the given name.
   *
   * @param name the index name
   * @return an {@link Optional} containing the registered custom index, or an
   *     empty {@link Optional} if none exists
   */
  public Optional<IModelIndex> getCustomIndex(String name) {
    return Optional.ofNullable(this.customIndices.get(name));
  }
  
  /**
   * Finalizes initialization for all managed indices after initial events have
   * been processed.
   *
   * <p>Call this after the initial model traversal/event replay has finished
   * (for example after using {@link ModelInitializationMessenger}).</p>
   */
  public void finalizeInitialization() {
    this.candidateIndex.finalizeInitialization();
    this.parentIndex.finalizeInitialization();
    this.customIndices.values().forEach(IModelIndex::finalizeInitialization);
  }
}
