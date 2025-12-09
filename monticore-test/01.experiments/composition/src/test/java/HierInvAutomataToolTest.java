/* (c) https://github.com/MontiCore/monticore */
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import hierinvautomata.HierInvAutomataTool;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class HierInvAutomataToolTest {
  
  @BeforeEach
  public void init() {
    LogStub.init();         // replace log by a sideffect free variant
    // LogStub.initPlusLog();  // for manual testing purpose only
    Log.enableFailQuick(false);
    Log.clearFindings();
    LogStub.clearPrints();
  }
  
  @Test
  public void executePingPong() throws IOException {
    HierInvAutomataTool.main(new String[] { "-i", "src/test/resources/example/PingPongInv.aut" });
    Log.printFindings();
    assertEquals(0, Log.getFindings().size());
    // LogStub.printPrints();  // for manual testing purpose only
    // System.out.printf("\n-- Full printed output::\n%s\n--\n", String.join("",p));  // compact alternative
  
    List<String> p = LogStub.getPrints();
    assertEquals(59, p.size());   // many small prints ...
  
    // Check some "[INFO]" outputs
    assertTrue(p.get(0).matches(".*.INFO.  HierIAT HierInvAutomata DSL Tool.*(\r)?\n"), p.get(0));
    
    // Check resulting pretty print:
    String res = String.join("",p).replaceAll("\r\n", " ").replaceAll("\n", " ");
  
    // original: state Pung  <<final>>   [[  &&[  &&[  &&[  true v1 ]  ! false  ]  v2 ]  ]]
    assertTrue(res.matches(".*state Pung  <<final>>   ..  &&.  &&.  &&.  true v1 ]  ! false  ]  v2 ]  ]].*"), res);
    assertTrue(res.matches(".*Pong - returnBall > Ping;.*"), res);
    
  }
  
  @Test
  public void executeSimple12() throws IOException {
    HierInvAutomataTool.main(new String[] { "-i", "src/test/resources/example/Simple12Inv.aut" });
    Log.printFindings();
    assertEquals(0, Log.getFindings().size());
    // LogStub.printPrints();
    List<String> p = LogStub.getPrints();
    assertTrue(p.size() > 10 );
  }
  
  @Test
  public void executeHierarchyPingPong() throws IOException {
    HierInvAutomataTool.main(new String[] { "-i", "src/test/resources/example/Hierarchy12Inv.aut" });
    Log.printFindings();
    assertEquals(0, Log.getFindings().size());
    // LogStub.printPrints();
    List<String> p = LogStub.getPrints();
    assertTrue(p.size() > 10 );
  }
}
