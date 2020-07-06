/* (c) https://github.com/MontiCore/monticore */

import automata.AutomataTool;
import org.junit.*;
import de.se_rwth.commons.logging.Log;
import java.util.*;
import de.se_rwth.commons.logging.LogStub;

import static org.junit.Assert.assertTrue;


public class AutomataToolTest {
  
  @BeforeClass
  public static void init() {
    // replace log by a sideffect free variant
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Before
  public void setUp() {
    Log.getFindings().clear();
  }
  
  @Test
  public void executePingPong() {
    AutomataTool.main(new String[] { "src/test/resources/example/PingPong.aut", "target" });
    LogStub.printPrints();
    List<String> p = LogStub.getPrints();
    assertEquals(p.size(), 1);
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
