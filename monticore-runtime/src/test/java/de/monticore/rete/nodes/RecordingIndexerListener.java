/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rete.nodes;

import de.monticore.rete.tuple.Tuple;

import java.util.ArrayList;
import java.util.List;

/**
 * A listener of an indexer recording all notifications in order, together
 * with the key and the number of tuples stored for the key at notification
 * time, e.g. {@code +[a, 1] key [a] count 2}.
 */
public class RecordingIndexerListener implements IndexerListener {

  protected final List<String> events = new ArrayList<>();

  @Override
  public void indexerUpdated(Indexer indexer, Direction direction, Tuple tuple, Tuple key) {
    events.add(RecordingReceiver.format(direction, tuple) + " key " + key + " count "
        + indexer.count(key));
  }

  /**
   * @return the notifications received since the last call, in order
   */
  public List<String> take() {
    List<String> result = List.copyOf(events);
    events.clear();
    return result;
  }
}
