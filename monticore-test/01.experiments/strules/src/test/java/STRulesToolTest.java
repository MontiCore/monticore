/* (c) https://github.com/MontiCore/monticore */

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import strules.STRulesTool;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class STRulesToolTest {
  
  @Before
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }

  @Test
  public void testFooFileSystem() {
    LogStub.init();
    //    Log.enableFailQuick(false);
    STRulesTool.main(new String[] { "-i", "src/test/resources/FooFileSystem.str"});
    assertEquals(0, Log.getErrorCount());
    assertTrue(Log.getFindings().isEmpty());
  }

}
