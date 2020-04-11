/* (c) https://github.com/MontiCore/monticore */
package automata;

import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;
import org.junit.Test;

public class GeneratorTest {
  
  @BeforeClass
  public static void init(){
    LogStub.init();
  }
  
  /* We execute the Generator here, but do not really test it:
   * (instead we use tests against the generated classes)
   */
  
  @Test
  public void testPingPong(){
    AutomataTool.main(new String[] { "src/test/resources/example/PingPong.aut" });
  }
  
  @Test
  public void testSimple12(){
    AutomataTool.main(new String[] { "src/test/resources/example/Simple12.aut" });
  }
}
