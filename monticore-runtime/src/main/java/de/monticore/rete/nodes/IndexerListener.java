/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rete.nodes;

import de.monticore.rete.tuple.Tuple;

/**
 * Listener notified by an {@link Indexer} after its memory has been updated.
 */
public interface IndexerListener {

  /**
   * @param indexer   the updated indexer (already reflecting the change)
   * @param direction direction of the change
   * @param tuple     the inserted or deleted tuple
   * @param key       the index key of {@code tuple}
   */
  void indexerUpdated(Indexer indexer, Direction direction, Tuple tuple, Tuple key);
}
