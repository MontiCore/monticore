/* (c) https://github.com/MontiCore/monticore */
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


public class Automata3ToolTest {

  @BeforeClass
  public static void init() {
    // replace log by a sideffect free variant
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Before
  public void setUp() {
    Log.clearFindings();
    LogStub.clearPrints();
  }
  
  @Test
  public void executePingPong() throws IOException {
    Automata3Tool.main(new String[] { "src/test/resources/example/PingPongInv.aut" });
    Log.printFindings();
    assertEquals(0, Log.getFindings().size());
    // LogStub.printPrints();
    List<String> p = LogStub.getPrints();
// XXX    assertEquals(1, p.size());
// XXX    String res = p.get(0).replaceAll("[\r\n]", " ");
// XXX    assertTrue(p.get(0), res.matches(".*state.*"));
// XXX    assertTrue(p.get(0), res.matches(".*state Pung ..... true && v1 && !false && v2 ......*"));
  }
  
  @Test
  public void executeSimple12() throws IOException {
    Automata3Tool.main(new String[] { "src/test/resources/example/Simple12Inv.aut" });
    Log.printFindings();
    assertEquals(0, Log.getFindings().size());
    // LogStub.printPrints();
// XXX    List<String> p = LogStub.getPrints();
// XXX    assertEquals(1, p.size());
  }
  
}
