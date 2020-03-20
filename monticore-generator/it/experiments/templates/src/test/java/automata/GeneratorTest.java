package automata;

import org.junit.Test;

public class GeneratorTest {
  
  @Test
  public void testPingPong(){
    AutomataTool.main(new String[] { "src/test/resources/example/PingPong.aut" });
  }
}
