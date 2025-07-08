/* (c) https://github.com/MontiCore/monticore */
package automata;

import de.monticore.runtime.junit.MCAssertions;
import org.junit.*;
import de.se_rwth.commons.logging.Log;
import java.util.*;

import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class AutomataToolTest {
  
  @BeforeEach
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
    assertTrue(p.get(0).matches(".*.INFO.  AutomataTool Automata DSL Tool.*(\r)?\n"), p.get(0));
    assertTrue(p.get(5).matches(".*.INFO.  AutomataTool Pretty printing automaton into console.*(\r)?\n"), p.get(5));
  
    // Check resulting pretty print:
    String res = p.get(p.size()-1).replaceAll("\r\n", " ").replaceAll("\n", " ");
    assertEquals(231, res.length());
    assertTrue(res.matches(".*state.*"), res);
    assertTrue(res.matches(".*state NoGame <<initial>>.*"), res);
    assertTrue(res.matches(".*Pong - returnBall > Ping;.*"), res);
    MCAssertions.assertNoFindings();
  }
  
  @Test
  public void executeSimple12() {
    AutomataTool.main(new String[] { "-i", "src/test/resources/example/Simple12.aut", "-s", "target/Simple12.autsym" });
    Log.printFindings();
    assertEquals(0, Log.getFindings().size());
    // LogStub.printPrints();
    List<String> p = LogStub.getPrints();
    assertEquals(7, p.size());
    MCAssertions.assertNoFindings();
  }
  
  @Test
  public void executeHierarchyPingPong() {
    AutomataTool.main(new String[] { "-i", "src/test/resources/example/HierarchyPingPong.aut", "-s", "target/HierarchyPingPong.autsym" });
    Log.printFindings();
    assertEquals(0, Log.getFindings().size());
    // LogStub.printPrints();
    List<String> p = LogStub.getPrints();
    assertEquals(7, p.size());
    MCAssertions.assertNoFindings();
  }
  
}
