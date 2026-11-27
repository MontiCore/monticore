/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rete.nodes;

import com.google.common.collect.Multiset;
import de.monticore.ast.ASTNode;
import de.monticore.rete.ReteNetwork;
import de.monticore.rete.tuple.Tuple;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

/**
 * Input node containing unary tuples {@code [n]} for all AST nodes {@code n}
 * reported to the network that are instances of the given type (including
 * subtypes).
 * <p>
 * Obtain instances via {@link ReteNetwork#typeInput(Class)}. The contents are
 * modified by {@link ReteNetwork#nodeAdded(ASTNode)} and
 * {@link ReteNetwork#nodeRemoved(ASTNode)}.
 *
 * @param <T> the AST type
 */
public class TypeInputNode<T extends ASTNode> extends AbstractSourceNode {

  protected final Class<T> type;

  protected final Set<ASTNode> instances = Collections.newSetFromMap(new IdentityHashMap<>());

  /**
   * Creates the input node and registers it at the network. Use
   * {@link ReteNetwork#typeInput(Class)} instead, which reuses existing nodes.
   *
   * @throws IllegalStateException if the network already received facts or
   *                               already has an input node for {@code type}
   */
  public TypeInputNode(ReteNetwork network, Class<T> type) {
    super(network, List.of(type));
    this.type = type;
    network.registerTypeInput(this);
  }

  public Class<T> getType() {
    return type;
  }

  public boolean contains(ASTNode node) {
    return instances.contains(node);
  }

  public int size() {
    return instances.size();
  }

  /**
   * Inserts the node and propagates the change. Called by the network only.
   *
   * @throws IllegalStateException if the network is not propagating a change
   *                               notification or the node is already contained
   */
  public void insert(ASTNode node) {
    network.checkPropagating();
    if (!instances.add(node)) {
      throw new IllegalStateException("AST node " + node + " was already added");
    }
    propagate(Direction.INSERT, Tuple.of(node));
  }

  /**
   * Deletes the node and propagates the change. Called by the network only.
   *
   * @throws IllegalStateException if the network is not propagating a change
   *                               notification or the node is not contained
   */
  public void delete(ASTNode node) {
    network.checkPropagating();
    if (!instances.remove(node)) {
      throw new IllegalStateException("AST node " + node + " is not known");
    }
    propagate(Direction.DELETE, Tuple.of(node));
  }

  /**
   * Adds a unary tuple for each contained AST node.
   */
  @Override
  public void pullInto(Multiset<Tuple> collector) {
    for (ASTNode node : instances) {
      collector.add(Tuple.of(node));
    }
  }

  /**
   * Input nodes have no parents; disposing unregisters the node from the
   * network instead, so that it no longer receives notifications.
   */
  @Override
  protected void detachFromParents() {
    network.unregisterTypeInput(this);
  }

  @Override
  public String toString() {
    return "TypeInputNode[" + type.getSimpleName() + "]";
  }
}
