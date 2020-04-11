/* (c) https://github.com/MontiCore/monticore */
package automata;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GeneratorTest {

  @BeforeClass
  public static void init(){
    LogStub.init();
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
    AutomataTool.main(new String[] { "src/test/resources/example/PingPong.aut", "src/product/java", "target/statepattern" });
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testSimple12(){
    AutomataTool.main(new String[] { "src/test/resources/example/Simple12.aut","src/product/java", "target/statepattern" });
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testWrongArguments(){
    AutomataTool.main(new String[] {  });
    assertEquals(1, Log.getErrorCount());
    assertEquals("Please specify 3 arguments: \n"
        + "1. automata model,\n"
        + "2. handcodedPath,\n"
        + "3. output directory.", Log.getFindings().get(0).buildMsg());
  }
}
