/* (c) https://github.com/MontiCore/monticore */
package automata;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import hooks.HooksTool;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HooksToolTest {

  @BeforeClass
  public static void init(){
    LogStub.init();         // replace log by a sideffect free variant
    // LogStub.initPlusLog();  // for manual testing purpose only
  }

  @Before
  public void clearFindings(){
    Log.getFindings().clear();
  }

  /* We execute the Generator here, but do not really test it:
   * (instead we use tests against the generated classes)
   *
   * The execution of the generator is needed to get the classes generated.
   * We reuse the automata models from the templates-Experiment
   */
  @Test
  public void testPingPong(){
    HooksTool.main(new String[] { 
         "../templates/src/test/resources/example/PingPong.aut", 
        "src/product/java", 
        "target/statepattern", 
        "src/main/resources"});
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testSimple12(){
    HooksTool.main(new String[] { 
        "../templates/src/test/resources/example/Simple12.aut",
        "src/product/java", 
        "target/statepattern",
        "src/main/resources"});
    assertEquals(0, Log.getErrorCount());
  }
  
  @Test
  public void testPingPong2(){
    TemplatesTool.main(new String[] { 
        "../templates/src/test/resources/example/PingPong.aut", 
        "src/product/java", 
        "target/statepattern2", 
        "src/main/templates"});
    assertEquals(0, Log.getErrorCount());
  }

}
