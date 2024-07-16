/* (c) https://github.com/MontiCore/monticore */

package mc;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeEach;

public abstract class GeneratorIntegrationsTest {

  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
}
