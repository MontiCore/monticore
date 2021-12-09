/* (c) https://github.com/MontiCore/monticore */

import automata.AutomataMill;
import automata.AutomataTool;
import org.junit.*;
import de.se_rwth.commons.logging.Log;
import java.util.*;
import java.util.regex.Pattern;

import de.se_rwth.commons.logging.LogStub;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;


public class AutomataToolTest {
  
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
   */
  @Test
  public void testPingPong(){
    AutomataTool.main(new String[] { "-i", "src/test/resources/example/PingPong.aut",
                                     "-path", "target/PingPong.autsym",
                                     "-hw", "src/product/java",
                                     "-o", "target/statepattern" });
    assertEquals(0, Log.getErrorCount());
  }
  
  @Test
  public void testSimple12(){
    AutomataTool.main(new String[] { "-i", "src/test/resources/example/Simple12.aut",
                                     "-path", "target/Simple12.autsym",
                                     "-hw", "src/product/java",
                                     "-o", "target/statepattern" });
    assertEquals(0, Log.getErrorCount());
  }
  
  @Test
  public void testWrongArguments(){
    AutomataTool.main(new String[] {  });
    assertEquals(1, Log.getErrorCount());
    assertEquals("0xDE631 Please specify at least 4 arguments: \n"
          + "1. automata modelfile,\n"
          + "2. symbol file,\n"
          + "3. handcodedPath,\n"
          + "4. output directory,\n"
          + "5. (optional) templatePath\n"
          , Log.getFindings().get(0).buildMsg());
  }
  
}
