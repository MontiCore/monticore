/* (c) https://github.com/MontiCore/monticore */
package automata;

import org.junit.*;
import de.se_rwth.commons.logging.Log;
import java.util.*;

import de.se_rwth.commons.logging.LogStub;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;


public class AutomataToolTest {
  
  @Before
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
    Log.clearFindings();
    LogStub.clearPrints();
  }

  @Test
  public void executePingPong() {
    AutomataTool.main(new String[] { "-i", "src/test/resources/example/PingPong.aut", "-s", "target/PingPong.autsym" });
    Log.printFindings();
    assertEquals(0, Log.getFindings().size());
    // LogStub.printPrints();  // for manual testing purpose only

    List<String> p = LogStub.getPrints();
    assertEquals(7, p.size());

    // Check some "[INFO]" outputs
    assertTrue(p.get(0), p.get(0).matches(".*.INFO.  AutomataTool Automata DSL Tool.*(\r)?\n"));
    assertTrue(p.get(5), p.get(5).matches(".*.INFO.  AutomataTool Pretty printing automaton into console.*(\r)?\n"));
  
    // Check resulting pretty print:
    String res = p.get(p.size()-1).replaceAll("\r\n", " ").replaceAll("\n", " ");
    assertEquals(231, res.length());
    assertTrue(res, res.matches(".*state.*"));
    assertTrue(res, res.matches(".*state NoGame <<initial>>.*"));
    assertTrue(res, res.matches(".*Pong - returnBall > Ping;.*"));
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void executeSimple12() {
    AutomataTool.main(new String[] { "-i", "src/test/resources/example/Simple12.aut", "-s", "target/Simple12.autsym" });
    Log.printFindings();
    assertEquals(0, Log.getFindings().size());
    // LogStub.printPrints();
    List<String> p = LogStub.getPrints();
    assertEquals(7, p.size());
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void executeHierarchyPingPong() {
    AutomataTool.main(new String[] { "-i", "src/test/resources/example/HierarchyPingPong.aut", "-s", "target/HierarchyPingPong.autsym" });
    Log.printFindings();
    assertEquals(0, Log.getFindings().size());
    // LogStub.printPrints();
    List<String> p = LogStub.getPrints();
    assertEquals(7, p.size());
    assertTrue(Log.getFindings().isEmpty());
  }
  
}
