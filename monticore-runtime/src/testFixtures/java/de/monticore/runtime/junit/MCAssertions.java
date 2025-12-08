/* (c) https://github.com/MontiCore/monticore */
package de.monticore.runtime.junit;

import com.google.common.collect.Streams;
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
  public static Finding assertHasFindingStartingWith(String expectedPrefix, String message) {
    return assertHasFinding(f -> f.getMsg().startsWith(expectedPrefix), message);
  }

  /**
   * Asserts that at least one Finding starts with the expected prefix
   * and removes that Finding from the Log
   *
   * @param expectedPrefix the expected prefix
   * @return returns the found Finding
   * @see MCAssertions#assertHasFinding(Predicate)
   */
  public static Finding assertHasFindingStartingWith(String expectedPrefix) {
    return assertHasFindingStartingWith(expectedPrefix,
        "Expected a Log-Finding with prefix " + expectedPrefix +
            ", but did not find any"
    );
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
  public static Collection<Finding> assertHasFindingsStartingWith(String expectedPrefix, String message) {
    return assertHasFindings(f -> f.getMsg().startsWith(expectedPrefix), message);
  }

  /**
   * Asserts that at least one Finding starts with the expected prefix
   * and removes all matching Findings from the Log
   *
   * @param expectedPrefix the expected prefix
   * @return returns the list of found Findings
   * @see MCAssertions#assertHasFinding(Predicate)
   */
  public static Collection<Finding> assertHasFindingsStartingWith(String expectedPrefix) {
    return assertHasFindingsStartingWith(expectedPrefix,
        "Expected Log-Findings with prefix " + expectedPrefix +
            ", but did not find any"
    );
  }

  /**
   * Asserts that at least one Finding matches the predicate
   * and removes that Finding from the Log.
   * If multiple Findings match, only the first of them will be removed.
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
    return failAndPrintFindings(message);
  }

  /**
   * Asserts that at least one Finding matches the predicate
   * and removes that Finding from the Log.
   * If multiple Findings match, only the first of them will be removed.
   *
   * @param predicate the predicate
   * @return returns the found Finding
   */
  public static Finding assertHasFinding(Predicate<Finding> predicate) {
    return assertHasFinding(predicate,
        "Expected a Log-Finding matching given predicate" +
            ", but did not find any"
    );
  }

  /**
   * Asserts that at least one Finding is present
   * and removes that Finding from the Log.
   * If multiple Findings match, only the first of them will be removed.
   * This method should NOT be used in conjunction with CoCo-Tests.
   *
   * @return returns the found Finding
   */
  public static Finding assertHasFinding() {
    return assertHasFinding(f -> true,
            "Expected any Log-Finding" +
                    ", but none were present"
    );
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
      return failAndPrintFindings(message);
    return matchingFindings;
  }

  /**
   * Asserts that at least one Finding matches the predicate
   * and removes all matching Findings from the Log
   *
   * @param predicate the predicate
   * @return returns the list of found Findings
   */
  public static Collection<Finding> assertHasFindings(Predicate<Finding> predicate) {
    return assertHasFindings(predicate,
        "Expected Log-Findings matching given predicate" +
            ", but did not find any"
    );
  }

  /**
   * Asserts that no findings are present.
   *
   * @param message the message to fail with iff findings were found
   */
  public static void assertNoFindings(String message) {
    if (!Log.getFindings().isEmpty()) {
      failAndPrintFindings(message);
    }
  }

  /**
   * Asserts that no findings are present.
   */
  public static void assertNoFindings() {
    assertNoFindings("Encountered Log-Findings while expecting none");
  }

  /**
   * Fails a test.
   * Additionally, Lists the state of Log-Findings.
   * See Javadoc for {@link Assertions#fail(String, Throwable)}
   * for an explanation of this method's generic return type V.
   *
   * @return nothing
   */
  public static <V> V failAndPrintFindings() {
    return failAndPrintFindings("");
  }


    /**
     * Fails a test with the given failure message.
     * Additionally, Lists the state of Log-Findings.
     * See Javadoc for {@link Assertions#fail(String, Throwable)}
     * for an explanation of this method's generic return type V.
     *
     * @param message message to print
     * @return nothing
     */
  public static <V> V failAndPrintFindings(String message) {
    StringBuilder messageWithFindings = new StringBuilder();
    if (!message.isBlank()) {
      messageWithFindings.append(message);
    }
    else {
      messageWithFindings.append("Failed (no reason stated)");
    }
    messageWithFindings.append(System.lineSeparator());
    if (Log.getFindings().isEmpty()) {
      messageWithFindings.append("Got no Log-Findings.");
    }
    else {
      messageWithFindings.append("Got Log-Findings:");
      messageWithFindings.append(System.lineSeparator());
      messageWithFindings.append(Streams.mapWithIndex(
                      Log.getFindings().stream().map(Finding::buildMsg),
                      (str, index) -> "[" + index + "]" + str)
              .collect(Collectors.joining(System.lineSeparator()))
      );
    }
    return Assertions.fail(messageWithFindings.toString());
  }

}
