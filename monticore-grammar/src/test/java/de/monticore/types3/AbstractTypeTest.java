// (c) https://github.com/MontiCore/monticore
package de.monticore.types3;

import de.monticore.runtime.junit.AbstractMCTest;
import de.monticore.runtime.junit.MCAssertions;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;

import java.util.stream.Collectors;

/**
 * @deprecated use {@link AbstractTypeTest} directly
 */
@Deprecated
public class AbstractTypeTest extends AbstractMCTest {

  /**
   * @return all findings as one String
   */
  protected static String getAllFindingsAsString() {
    return Log.getFindings().stream()
        .map(Finding::buildMsg)
        .collect(Collectors.joining(System.lineSeparator()))
        ;
  }

  protected static void assertNoFindings() {
    MCAssertions.assertNoFindings();
  }

}
