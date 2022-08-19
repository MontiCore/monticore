/* (c) https://github.com/MontiCore/monticore */
package automata;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GeneratorTest {

  @Before
  public void init(){
    LogStub.init();         // replace log by a sideffect free variant
    // LogStub.initPlusLog();  // for manual testing purpose only
    Log.enableFailQuick(false);
  }

  @Before
  public void clearFindings(){
    Log.getFindings().clear();
  }

  /* We execute the Generator here, but do not really test it:
   * (instead we use tests against the generated classes)
   */
  @Test
  public void testPingPong(){
    TemplatesTool.main(new String[] { "src/test/resources/example/PingPong.aut", "src/product/java", "target/statepattern" });
    assertEquals(0, Log.getErrorCount());
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSimple12(){
    TemplatesTool.main(new String[] { "src/test/resources/example/Simple12.aut","src/product/java", "target/statepattern" });
    assertEquals(0, Log.getErrorCount());
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testWrongArguments(){
    TemplatesTool.main(new String[] {  });
    assertEquals(1, Log.getErrorCount());
    assertEquals("0xEE631 Please specify at least 3 arguments: \n"
        + "1. automata modelfile,\n"
        + "2. handcodedPath,\n"
        + "3. output directory.\n"
        + "4. (optional) templatePath", Log.getFindings().get(0).buildMsg());
  }
}
