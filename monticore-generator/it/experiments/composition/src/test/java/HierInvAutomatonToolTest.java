import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;


public class HierInvAutomatonToolTest {
  
  @Test
  public void executePingPong() throws IOException {
    HierInvAutomatonTool.main(new String[] { "src/test/resources/example/PingPongInv.aut" }); 
    assertTrue(!false);
  }
  
  @Test
  public void executeSimple12() throws IOException {
    HierInvAutomatonTool.main(new String[] { "src/test/resources/example/Simple12Inv.aut" }); 
    assertTrue(!false);
  }
  
  @Test
  public void executeHierarchyPingPong() throws IOException {
    HierInvAutomatonTool.main(new String[] { "src/test/resources/example/Hierarchy12Inv.aut" }); 
    assertTrue(!false);
  }
  
}
