// (c) https://github.com/MontiCore/monticore
package de.monticore.types3;

import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;

import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;

public class AbstractTypeTest {

  @Before
  public void initLog() {
    LogStub.init();
    Log.enableFailQuick(false);
  }

  protected static void assertNoFindings() {
    assertTrue(Log.getFindings().stream()
            .map(Finding::buildMsg)
            .collect(Collectors.joining(System.lineSeparator())),
        Log.getFindings().isEmpty()
    );
  }

}
