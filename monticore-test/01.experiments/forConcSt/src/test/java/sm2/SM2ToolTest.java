/* (c) https://github.com/MontiCore/monticore */

package sm2;

import de.monticore.runtime.junit.MCAssertions;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SM2ToolTest {
  
  @BeforeEach
  public void init() {
    LogStub.init();         // replace log by a sideffect free variant
    // LogStub.initPlusLog();  // for manual testing purpose only
    Log.enableFailQuick(false);
    Log.clearFindings();
    LogStub.clearPrints();
    SM2Mill.reset();
  }
  
  @Test
  public void test() {
    String[] args = {"-i","src/test/resources/example/PingPong.aut"};
    new SM2Tool().run(args);
    MCAssertions.assertNoFindings();
  
    List<String> p = LogStub.getPrints();
    assertEquals(9, p.size());
  
    // Check some "[INFO]" outputs
    assertTrue(p.get(0).matches(".*.INFO.  SM2Tool SM2 DSL Tool.*(\r)?\n"), p.get(0));
    assertTrue(p.get(3).matches(".*.INFO. .* StateSymbol defined for NoGame.*(\r)?\n"), p.get(3));
    assertTrue(p.get(6).matches(".*.INFO. .* The model contains 3 states.*(\r)?\n"), p.get(6));
  
    // Check resulting pretty print:
    String res = p.get(p.size()-1).replaceAll("\r\n", " ").replaceAll("\n", " ");
    assertEquals(231, res.length());
    assertTrue(res.matches(".*state NoGame <<initial>>.*"), res);
    assertTrue(res.matches(".*Pong - returnBall > Ping;.*"), res);
    MCAssertions.assertNoFindings();
  }
}
