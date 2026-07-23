/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.runtime.inc;

import de.monticore.ast.ASTNode;
import org.jspecify.annotations.NonNull;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Coordinates built-in and custom model indices and forwards incremental
 * model change events to all managed indices.
 *
 * <p>The handler only stores and dispatches to indices. The initial traversal
 * over root nodes is performed externally (for example by
 * {@link ModelInitializationMessenger}).</p>
 */
public class IndexHandler implements IIncrementalListener {
  
  private final CandidateIndex candidateIndex;
  private final ParentIndex parentIndex;
  
  private final Map<String, IModelIndex> customIndices;
  
  /**
   * Creates an index handler with the built-in indices and no custom indices.
   *
   */
  public IndexHandler() {
    this(new HashMap<>());
  }
  
  /**
   * Creates an index handler with the built-in indices and custom indices.
   *
   * <p>No root traversal is triggered here. Index population happens via
   * incremental events sent to this handler. The given map is copied
   * defensively.</p>
   *
   * @param customIndices the custom indices to register by name
   */
  public IndexHandler(Map<String, IModelIndex> customIndices) {
    this.candidateIndex = new CandidateIndex();
    this.parentIndex = new ParentIndex();
    this.customIndices = new HashMap<>(customIndices);
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
   * Forwards a node attach event to all managed indices.
   *
   * @param node the attached node
   * @param parent the parent the node was attached to
   */
  @Override
  public void onASTNodeAttach(@NonNull ASTNode node, @Nullable ASTNode parent) {
    this.candidateIndex.onASTNodeAttach(node, parent);
    this.parentIndex.onASTNodeAttach(node, parent);
    
    this.customIndices.values().forEach(index -> index.onASTNodeAttach(node, parent));
  }
  
  /**
   * Forwards a node detach event to all managed indices.
   *
   * @param node the detached node
   * @param parent the parent the node was detached from
   */
  @Override
  public void onASTNodeDetach(@NonNull ASTNode node, @NonNull ASTNode parent) {
    this.candidateIndex.onASTNodeDetach(node, parent);
    this.parentIndex.onASTNodeDetach(node, parent);
    
    this.customIndices.values().forEach(index -> index.onASTNodeDetach(node, parent));
  }
  
  /**
   * Forwards a node modification event to all managed indices.
   *
   * @param node the modified node
   * @param parent the parent containing the node, or {@code null} if the node
   *     has no parent (for example, root-level updates)
   * @param attributeName the name of the modified attribute
   * @param oldValue the previous attribute value
   * @param newValue the new attribute value
   */
  @Override
  public void onASTNodeModification(@NonNull ASTNode node, @Nullable ASTNode parent, String attributeName,
      Object oldValue, Object newValue) {
    this.candidateIndex.onASTNodeModification(node, parent, attributeName, oldValue, newValue);
    this.parentIndex.onASTNodeModification(node, parent, attributeName, oldValue, newValue);
    
    this.customIndices.values().forEach(index -> index.onASTNodeModification(node, parent, attributeName, oldValue, newValue));
  }
}
