/* (c) https://github.com/MontiCore/monticore */
package automata;

import static org.junit.Assert.assertTrue;

import automata.AutomataMill;
import org.junit.Before;
import org.junit.Test;

public class AutomatonToolTest {

  @Before
  public void setup(){
    AutomataMill.automataGlobalScope().clear();
  }
  
  @Test
  public void executeMain() {
    AutomataTool.main(new String[] { "src/main/resources/example/PingPong.aut" });
    
    assertTrue(!false);
  }
  
}
