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
  
  @Before
  public void init(){
    LogStub.init();// replace log by a sideffect free variant
    Log.enableFailQuick(false);
    // LogStub.initPlusLog();  // for manual testing purpose only
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
                                     "-o", "target/statepattern",
                                      "-s", "target/example/PingPong.autsym"});
    assertEquals(0, Log.getErrorCount());
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testSimple12(){
    AutomataTool.main(new String[] { "-i", "src/test/resources/example/Simple12.aut",
                                     "-path", "target/Simple12.autsym",
                                     "-hw", "src/product/java",
                                     "-o", "target/statepattern",
                                     "-s", "target/example/Simple12.autsym"});
    assertEquals(0, Log.getErrorCount());
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testWrongArguments(){
    AutomataTool.main(new String[] {  });
    assertEquals(1, Log.getErrorCount());
    assertEquals("0xA5C02 Must specify an Input file."
          , Log.getFindings().get(0).buildMsg());
  }
  
}
