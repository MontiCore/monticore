/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rete;

/**
 * Listener notified about appearing and disappearing matches of a
 * {@link de.monticore.rete.nodes.ProductionNode}.
 * <p>
 * Notifications are delivered at the end of the outermost transaction (see
 * {@link ReteNetwork#transaction(Runnable)}) when the network is in a
 * consistent state. Changes cancelling each other out within a transaction
 * are not reported. Listeners may query matchers and may report further
 * model changes to the network.
 */
public interface MatchListener {

  /**
   * Called when a tuple became a match of the production during the last
   * transaction (and still is one at the time of delivery).
   *
   * @param match the new match
   */
  void matchAppeared(Match match);

  /**
   * Called when a match of the production was lost during the last
   * transaction (and still is lost at the time of delivery).
   *
   * @param match the lost match
   */
  void matchDisappeared(Match match);
}
