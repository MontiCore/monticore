/* (c) https://github.com/MontiCore/monticore */

package mc;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;

public abstract class GeneratorIntegrationsTest {

  @Before
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
}
