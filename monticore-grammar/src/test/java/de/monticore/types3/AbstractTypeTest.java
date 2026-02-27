// (c) https://github.com/MontiCore/monticore
package de.monticore.types3;

import de.monticore.runtime.junit.MCAssertions;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import java.util.stream.Collectors;

/**
 * @deprecated use {@link de.monticore.runtime.junit.AbstractMCTest} directly.
 * This class contains duplicate code from the AbstractMCTest, etc. to avoid breaking changes
 */
@Deprecated(forRemoval = true)
public class AbstractTypeTest  {

  @BeforeEach
  public void initLog() {
    LogStub.init();
    Log.enableFailQuick(false);
  }

  protected static void assertNoFindings() {
    Assertions.assertTrue(Log.getFindings().isEmpty(), "Expected no Log findings, but got:"
            + System.lineSeparator() + getAllFindingsAsString());
  }

  /**
   * @return all findings as one String
   */
  protected static String getAllFindingsAsString() {
    return Log.getFindings().stream()
        .map(Finding::buildMsg)
        .collect(Collectors.joining(System.lineSeparator()))
        ;
  }

  @BeforeEach
  public void initAbstract() {
    defaultInitAbstract();
  }

  static void defaultInitAbstract() {
    Log.clearFindings(); // clear previous findings
    LogStub.init(); // replace log by a sideeffect free variant
    Log.enableFailQuick(false); // do not fail quick/exit on the first error
  }

  @AfterEach
  public void checkLogAfterTest() {
    defaultCheckLogAfterTest();
  }

  static void defaultCheckLogAfterTest() {
    try {
      // Ensure, no Findings are present
      // the various Finding-methods of MCAssertions check for & remove
      //  expected findings
      MCAssertions.assertNoFindings(
              "After the test has run, findings were present."
      );
    } finally {
      Log.clearFindings();
    }
  }

}
