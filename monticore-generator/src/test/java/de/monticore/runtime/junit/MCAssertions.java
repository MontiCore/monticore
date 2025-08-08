package de.monticore.runtime.junit;

import com.google.common.collect.Streams;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;

import java.util.stream.Collectors;

/**
 * A copy of the MCAssertions class from the runtime test fixtures
 */
public class MCAssertions {
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
    } else {
      messageWithFindings.append("Failed (no reason stated)");
    }
    messageWithFindings.append(System.lineSeparator());
    if (Log.getFindings().isEmpty()) {
      messageWithFindings.append("Got no Log-Findings.");
    } else {
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
