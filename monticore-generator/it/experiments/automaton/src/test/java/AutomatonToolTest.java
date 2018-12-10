/* (c) Monticore license: https://github.com/MontiCore/monticore */

import static org.junit.Assert.assertTrue;
import org.junit.Test;


public class AutomatonToolTest {
  
  @Test
  public void executePingPong() {
    AutomatonTool.main(new String[] { "src/test/resources/example/PingPong.aut" }); 
    assertTrue(!false);
  }
  
  @Test
  public void executeSimple12() {
    AutomatonTool.main(new String[] { "src/test/resources/example/Simple12.aut" }); 
    assertTrue(!false);
  }
  
  @Test
  public void executeHierarchyPingPong() {
    AutomatonTool.main(new String[] { "src/test/resources/example/HierarchyPingPong.aut" }); 
    assertTrue(!false);
  }
  
}
