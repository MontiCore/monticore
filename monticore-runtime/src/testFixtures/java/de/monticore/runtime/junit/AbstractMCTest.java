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
              "After the test has run, findings were present.\n" +
                      "(In case they are expected: Use the MCAssertions#assertHasFinding methods to check for them first)"
      );
    } finally {
      Log.clearFindings();
    }
  }

}
