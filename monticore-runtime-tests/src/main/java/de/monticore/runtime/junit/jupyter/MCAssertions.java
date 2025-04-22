/* (c) https://github.com/MontiCore/monticore */
package de.monticore.runtime.junit.jupyter;

import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * MCAssertions is a collection of utility methods that support asserting
 * conditions in MontiCore tests.
 * Unless otherwise noted, a failed assertion will throw an
 * {@link org.opentest4j.AssertionFailedError} or a subclass thereof.
 */
@SuppressWarnings("unused")
public class MCAssertions {

  /**
   * Asserts that at least one Finding starts with the expected prefix
   * and removes that Finding from the Log
   *
   * @param expectedPrefix the expected prefix
   * @param message        the message to fail with iff no finding was found
   * @return returns the found Finding
   * @see MCAssertions#assertHasFinding(Predicate, String)
   */
  public static Finding assertHasFindingStartsWith(String expectedPrefix, String message) {
    return assertHasFinding(f -> f.getMsg().startsWith(expectedPrefix), message);
  }

  /**
   * Asserts that at least one Finding starts with the expected prefix
   * and removes all matching Findings from the Log
   *
   * @param expectedPrefix the expected prefix
   * @param message        the message to fail with iff no findings were found
   * @return returns the list of found Findings
   * @see MCAssertions#assertHasFindings(Predicate, String)
   */
  public static Collection<Finding> assertHasFindingsStartsWith(String expectedPrefix, String message) {
    return assertHasFindings(f -> f.getMsg().startsWith(expectedPrefix), message);
  }

  /**
   * Asserts that at least one Finding matches the predicate
   * and removes that Finding from the Log
   *
   * @param predicate the predicate
   * @param message   the message to fail with iff no finding was found
   * @return returns the found Finding
   */
  public static Finding assertHasFinding(Predicate<Finding> predicate, String message) {
    var it = Log.getFindings().iterator();
    while (it.hasNext()) {
      var f = it.next();
      if (predicate.test(f)) {
        it.remove();
        return f;
      }
    }
    return failFindings(message);
  }

  /**
   * Asserts that at least one Finding matches the predicate
   * and removes all matching Findings from the Log
   *
   * @param predicate the predicate
   * @param message   the message to fail with iff no findings were found
   * @return returns the list of found Findings
   */
  public static Collection<Finding> assertHasFindings(Predicate<Finding> predicate, String message) {
    var it = Log.getFindings().iterator();
    List<Finding> matchingFindings = new ArrayList<>();
    while (it.hasNext()) {
      var f = it.next();
      if (predicate.test(f)) {
        it.remove();
        matchingFindings.add(f);
      }
    }
    if (matchingFindings.isEmpty())
      return failFindings(message);
    return matchingFindings;
  }

  /**
   * Fails a test with the given failure message.
   * See Javadoc for {@link Assertions#fail(String, Throwable)} for an explanation of this method's generic return type V.
   *
   * @param message message to print
   * @return nothing
   */
  public static <V> V failFindings(String message) {
    return Assertions.fail(message + ", got findings " +
            Log.getFindings().stream().map(Finding::toString)
                    .collect(Collectors.joining(System.lineSeparator())));
  }
}
