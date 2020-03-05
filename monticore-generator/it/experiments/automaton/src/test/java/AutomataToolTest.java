/* (c) https://github.com/MontiCore/monticore */

import automata.AutomataTool;
import org.junit.Test;

import static org.junit.Assert.assertTrue;


public class AutomataToolTest {
  
  @Test
  public void executePingPong() {
    AutomataTool.main(new String[] { "src/test/resources/example/PingPong.aut" });
    assertTrue(!false);
  }
  
  @Test
  public void executeSimple12() {
    AutomataTool.main(new String[] { "src/test/resources/example/Simple12.aut" });
    assertTrue(!false);
  }
  
  @Test
  public void executeHierarchyPingPong() {
    AutomataTool.main(new String[] { "src/test/resources/example/HierarchyPingPong.aut" });
    assertTrue(!false);
  }
  
}
