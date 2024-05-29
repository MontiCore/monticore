/* (c) https://github.com/MontiCore/monticore */

package sm2;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class SM2ToolTest {
  
  @Before
  public void init() {
    LogStub.init();         // replace log by a sideffect free variant
    // LogStub.initPlusLog();  // for manual testing purpose only
    Log.enableFailQuick(false);
    Log.clearFindings();
    LogStub.clearPrints();
    SM2Mill.globalScope().clear();
  }
  
  @Test
  public void test() {
    String[] args = {"-i","src/test/resources/example/PingPong.aut"};
    SM2Tool.main(args);
    assertTrue(Log.getFindings().isEmpty());
  
    List<String> p = LogStub.getPrints();
    assertEquals(9, p.size());
  
    // Check some "[INFO]" outputs
    Assert.assertTrue(p.get(0), p.get(0).matches(".*.INFO.  SM2Tool SM2 DSL Tool.*(\r)?\n"));
    Assert.assertTrue(p.get(3), p.get(3).matches(".*.INFO. .* StateSymbol defined for NoGame.*(\r)?\n"));
    Assert.assertTrue(p.get(6), p.get(6).matches(".*.INFO. .* The model contains 3 states.*(\r)?\n"));
  
    // Check resulting pretty print:
    String res = p.get(p.size()-1).replaceAll("\r\n", " ").replaceAll("\n", " ");
    assertEquals(231, res.length());
    Assert.assertTrue(res, res.matches(".*state NoGame <<initial>>.*"));
    Assert.assertTrue(res, res.matches(".*Pong - returnBall > Ping;.*"));
    Assert.assertTrue(Log.getFindings().isEmpty());
  }
}
