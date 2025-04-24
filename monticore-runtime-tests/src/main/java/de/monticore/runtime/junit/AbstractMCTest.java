/* (c) https://github.com/MontiCore/monticore */
package de.monticore.runtime.junit;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

/**
 * Common abstract super class for MontiCore language tests
 * Ensures, that the correct Log is initialized
 * and no findings are present after a test;
 * findings are either expected to not occur at all
 * or, if they occur, are checked and removed afterward,
 * s.a. {@link MCAssertions}.
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
      MCAssertions.assertNoFindings(
          "After the test has run, findings were present."
      );
    } finally {
      Log.clearFindings();
    }
  }

}
