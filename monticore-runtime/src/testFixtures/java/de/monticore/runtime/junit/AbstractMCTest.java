/* (c) https://github.com/MontiCore/monticore */
package de.monticore.runtime.junit;

import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.util.*;

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
    if (MCAssertions.notifierAndCondition == null) {
      MCAssertions.notifierAndCondition = f -> {
        // if already reported -> next via return false
        boolean notAlreadyPresent = checkedFindings.stream().noneMatch(finding -> finding == f);
        checkedFindings.add(f);
        return notAlreadyPresent;
      };
    }
  }

  @AfterEach
  public void checkLogAfterTest() {
    defaultCheckLogAfterTest();
  }

  /**
   * The list of checked findings.
   * Used to check that all findings were actually checked and not forgotten
   */
  static List<Finding> checkedFindings = new ArrayList<>();

  static void defaultCheckLogAfterTest() {
    try {
      // Ensure, no Findings are present
      // the various Finding-methods of MCAssertions check for
      // and then report expected findings (using an identity check)
      List<Finding> leftovers = Log.getFindings().stream()
              .filter(f -> checkedFindings.stream().noneMatch(checkedF -> checkedF == f)).toList();
      if (!leftovers.isEmpty()) {
        MCAssertions.failAndPrintFindings(
                "After the test has run, findings were present.\n" +
                        "(In case they are expected: Use the MCAssertions#assertHasFinding methods to check for them first)",
                leftovers
                                         );
      }

    } finally {
      Log.clearFindings();
    }
  }

}
