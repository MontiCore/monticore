/* (c) https://github.com/MontiCore/monticore */

import automata.AutomataTool;
import org.junit.*;
import de.se_rwth.commons.logging.Log;
import java.util.*;
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
    // LogStub.printPrints();
    List<String> p = LogStub.getPrints();
    assertEquals(1, p.size());
    // TODO XXX    assertEquals("XXX", p.get(0));
    // TODO XXX handle Log.findings
  }
  
  @Test
  public void executeSimple12() {
    AutomataTool.main(new String[] { "src/test/resources/example/Simple12.aut", "target" });
    assertTrue(!false);
  }
  
  @Test
  public void executeHierarchyPingPong() {
    AutomataTool.main(new String[] { "src/test/resources/example/HierarchyPingPong.aut", "target" });
    assertTrue(!false);
  }
  
}
