/* (c) https://github.com/MontiCore/monticore */
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;


public class Automata3ToolTest {
  
  @Test
  public void executePingPong() throws IOException {
    Automata3Tool.main(new String[] { "src/test/resources/example/PingPongInv.aut" });
    assertTrue(!false);
  }
  
  @Test
  public void executeSimple12() throws IOException {
    Automata3Tool.main(new String[] { "src/test/resources/example/Simple12Inv.aut" });
    assertTrue(!false);
  }
  
}
