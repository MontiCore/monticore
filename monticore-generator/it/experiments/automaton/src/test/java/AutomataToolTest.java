/* (c) https://github.com/MontiCore/monticore */

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
  public static void init() {
    // replace log by a sideffect free variant
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Before
  public void setUp() {
    Log.clearFindings();
    LogStub.clearPrints();
  }
  
  @Test
  public void executePingPong() {
    AutomataTool.main(new String[] { "src/test/resources/example/PingPong.aut", "target" });
    Log.printFindings();
    assertEquals(0, Log.getFindings().size());
    // LogStub.printPrints();
    List<String> p = LogStub.getPrints();
    assertEquals(1, p.size());
    String res = p.get(0).replaceAll("[\r\n]", " ");
    assertEquals(231, res.length());
    assertTrue(p.get(0), res.matches(".*state.*"));
    assertTrue(p.get(0), res.matches(".*state NoGame <<initial>>.*"));
    assertTrue(p.get(0), res.matches(".*Pong - returnBall > Ping;.*"));
  }
  
  @Test
  public void executeSimple12() {
    AutomataTool.main(new String[] { "src/test/resources/example/Simple12.aut", "target" });
    Log.printFindings();
    assertEquals(0, Log.getFindings().size());
    // LogStub.printPrints();
    List<String> p = LogStub.getPrints();
    assertEquals(1, p.size());
  }
  
  @Test
  public void executeHierarchyPingPong() {
    AutomataTool.main(new String[] { "src/test/resources/example/HierarchyPingPong.aut", "target" });
    Log.printFindings();
    assertEquals(0, Log.getFindings().size());
    // LogStub.printPrints();
    List<String> p = LogStub.getPrints();
    assertEquals(1, p.size());
  }
  
}
