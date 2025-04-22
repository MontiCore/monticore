/* (c) https://github.com/MontiCore/monticore */
package de.monticore.runtime.junit.jupyter;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

/**
 * Common abstract super class for MontiCore language tests
 * Ensures, that the correct Log is initialized
 * and no findings are present after a test
 */
public abstract class AbstractMCTest {
  @BeforeEach
  public void initAbstract() {
    Log.clearFindings();
    LogStub.init();
    Log.enableFailQuick(false);
  }

  @AfterEach
  public void resetAndCheckLog() {
    try {
      if (Log.getFindingsCount() > 0) {
        // output
        MCAssertions.failFindings("After the test has run, findings were still present");
      }
    } finally {
      Log.clearFindings();
    }
  }

}
