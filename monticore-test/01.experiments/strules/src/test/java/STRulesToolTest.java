/* (c) https://github.com/MontiCore/monticore */

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class STRulesToolTest {

  @Test
  public void testFooFileSystem() {
    LogStub.init();
    //    Log.enableFailQuick(false);
    STRulesTool.main(new String[] { "src/test/resources/FooFileSystem.str"});
    assertEquals(0, Log.getErrorCount());
  }

}
