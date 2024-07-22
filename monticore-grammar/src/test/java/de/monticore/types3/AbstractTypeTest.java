// (c) https://github.com/MontiCore/monticore
package de.monticore.types3;

import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;

public class AbstractTypeTest {

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

}
