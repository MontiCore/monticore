/* (c) https://github.com/MontiCore/monticore */

import de.monticore.runtime.junit.MCAssertions;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import strules.STRulesTool;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class STRulesToolTest {
  
  @BeforeEach
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
    MCAssertions.assertNoFindings();
  }

}
