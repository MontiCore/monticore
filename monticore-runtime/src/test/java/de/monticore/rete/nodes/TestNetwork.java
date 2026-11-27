/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rete.nodes;

import de.monticore.rete.ReteNetwork;

/**
 * A network whose changes can be triggered directly instead of via AST
 * change notifications. Used to drive single nodes with {@link TestSource}s.
 */
public class TestNetwork extends ReteNetwork {

  /**
   * Executes the change like a change notification: within a transaction,
   * propagating all deltas enqueued by {@code change} before returning.
   */
  public void change(Runnable change) {
    notifyChange(change);
  }
}
