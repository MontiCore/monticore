/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rete.nodes;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import de.monticore.ast.ASTNode;
import de.monticore.rete.Feature;
import de.monticore.rete.ReteNetwork;
import de.monticore.rete.tuple.Tuple;

import java.util.List;

/**
 * Input node containing binary tuples {@code [owner, value]} for all values
 * of a {@link Feature} reported to the network.
 * <p>
 * The contents form a multiset, as list-valued features may contain the same
 * value several times (e.g. a list of Strings). Obtain instances via
 * {@link ReteNetwork#featureInput(Feature)}.
 *
 * @param <O> type of the owning AST node
 * @param <V> type of the feature values
 */
public class FeatureInputNode<O extends ASTNode, V> extends AbstractSourceNode {

  protected final Feature<O, V> feature;

  protected final Multiset<Tuple> facts = HashMultiset.create();

  /**
   * Creates the input node and registers it at the network. Use
   * {@link ReteNetwork#featureInput(Feature)} instead, which reuses existing
   * nodes.
   *
   * @throws IllegalStateException if the network already received facts or
   *                               already has an input node for {@code feature}
   */
  public FeatureInputNode(ReteNetwork network, Feature<O, V> feature) {
    super(network, List.of(feature.getOwnerType(), feature.getValueType()));
    this.feature = feature;
    network.registerFeatureInput(this);
  }

  public Feature<O, V> getFeature() {
    return feature;
  }

  /**
   * @return how often {@code value} is currently contained in the feature of
   * {@code owner} (0 if not at all)
   */
  public int count(O owner, V value) {
    return facts.count(Tuple.of(owner, value));
  }

  public int size() {
    return facts.size();
  }

  /**
   * Inserts one occurrence of the fact and propagates the change. Called by
   * the network only.
   *
   * @throws IllegalStateException if the network is not propagating a change
   *                               notification
   */
  public void insert(O owner, V value) {
    network.checkPropagating();
    Tuple fact = Tuple.of(owner, value);
    facts.add(fact);
    propagate(Direction.INSERT, fact);
  }

  /**
   * Deletes one occurrence of the fact and propagates the change. Called by
   * the network only.
   *
   * @throws IllegalStateException if the network is not propagating a change
   *                               notification or the fact is not contained
   */
  public void delete(O owner, V value) {
    network.checkPropagating();
    Tuple fact = Tuple.of(owner, value);
    if (!facts.remove(fact)) {
      throw new IllegalStateException("Value " + value + " of feature " + feature
          + " of " + owner + " is not known");
    }
    propagate(Direction.DELETE, fact);
  }

  /**
   * Adds all contained {@code [owner, value]} facts with their multiplicities.
   */
  @Override
  public void pullInto(Multiset<Tuple> collector) {
    collector.addAll(facts);
  }

  /**
   * Input nodes have no parents; disposing unregisters the node from the
   * network instead, so that it no longer receives notifications.
   */
  @Override
  protected void detachFromParents() {
    network.unregisterFeatureInput(this);
  }

  @Override
  public String toString() {
    return "FeatureInputNode[" + feature + "]";
  }
}
