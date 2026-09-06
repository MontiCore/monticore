/* (c) https://github.com/MontiCore/monticore */
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;

import automata3.Automata3Tool;
import de.monticore.runtime.junit.MCAssertions;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class Automata3ToolTest {

  @BeforeEach
  public  void init() {
    LogStub.init();         // replace log by a sideffect free variant
    // LogStub.initPlusLog();  // for manual testing purpose only
    Log.enableFailQuick(false);
    Log.clearFindings();
    LogStub.clearPrints();
  }
  
  @Test
  public void executePingPong() throws IOException {
    new Automata3Tool().run(new String[] { "-i", "src/test/resources/example/PingPongInv.aut" });
    Log.printFindings();
    assertEquals(0, Log.getFindings().size());
    // LogStub.printPrints();  // for manual testing purpose only
  
    List<String> p = LogStub.getPrints();
    assertEquals(6, p.size());
  
    // Check some "[INFO]" outputs
    assertTrue(p.get(0).matches(".*.INFO.  Automata3Tool Automata3 DSL Tool.*(\r)?\n"), p.get(0));
  
    // Check resulting pretty print:
    String res = p.get(p.size()-1).replaceAll("[\r\n]", " ");
    assertTrue(res.matches(".*print by composed Automata3PrettyPrinter.*"), res);
    // original:                         state NoGame /*[*/ true /*]*/ <<initial>>
    assertTrue(res.matches(".*state NoGame /.../ true /.../ <<initial>>.*"), res);
    assertTrue(res.matches(".*Pong - returnBall > Ping;.*"), res);
    MCAssertions.assertNoFindings();
  }
  
  @Test
  public void executeSimple12() throws IOException {
    new Automata3Tool().run(new String[] { "-i", "src/test/resources/example/Simple12Inv.aut" });
    Log.printFindings();
    assertEquals(0, Log.getFindings().size());
    // LogStub.printPrints();
    List<String> p = LogStub.getPrints();
    assertEquals(6, p.size());
    MCAssertions.assertNoFindings();
  }
  
}
