/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rete.nodes;

import de.monticore.rete.ReteNetwork;

import java.util.List;

/**
 * Base class of nodes with exactly one parent {@link TupleSource}.
 * Subclasses are attached to their parent at the end of construction by
 * calling {@link #attach()}.
 */
public abstract class AbstractSingleParentNode extends AbstractSourceNode implements Receiver {

  protected final TupleSource parent;

  /**
   * @param columnTypes       the types of the output columns
   * @param columnOptionality for each output column, whether it may contain
   *                          {@link de.monticore.rete.tuple.Tuple#ABSENT}
   */
  protected AbstractSingleParentNode(ReteNetwork network, TupleSource parent,
      List<Class<?>> columnTypes, List<Boolean> columnOptionality) {
    super(network, columnTypes, columnOptionality);
    checkSameNetwork(parent);
    this.parent = parent;
  }

  public TupleSource getParent() {
    return parent;
  }

  /**
   * Attaches this node to its parent. Must be called by subclass constructors
   * after the node has been fully initialized (including any state pulled
   * from the parent), so that no delta is received earlier.
   */
  protected void attach() {
    parent.attachChild(this);
  }

  @Override
  protected void detachFromParents() {
    parent.detachChild(this);
  }
}
