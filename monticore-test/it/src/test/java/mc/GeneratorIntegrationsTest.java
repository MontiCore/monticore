/* (c) https://github.com/MontiCore/monticore */

package mc;

import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.Slf4jLog;

public abstract class GeneratorIntegrationsTest {
  
  @BeforeClass
  public static void setup() {
    Slf4jLog.init();
  }
  
  @Before
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
}
