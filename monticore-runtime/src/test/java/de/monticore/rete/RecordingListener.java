/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rete;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Records match events and maintains the set of matches derived from them.
 */
public class RecordingListener implements MatchListener {

  public final Set<Match> matches = new HashSet<>();

  public final List<String> events = new ArrayList<>();

  /**
   * Records the event and adds the match, failing if it is already known.
   */
  @Override
  public void matchAppeared(Match match) {
    assertTrue(matches.add(match), "appeared twice: " + match);
    events.add("+" + match);
  }

  /**
   * Records the event and removes the match, failing if it is unknown.
   */
  @Override
  public void matchDisappeared(Match match) {
    assertTrue(matches.remove(match), "disappeared without appearing: " + match);
    events.add("-" + match);
  }
}
