/* (c) https://github.com/MontiCore/monticore */

import automata.AutomataTool;
import org.junit.*;
import de.se_rwth.commons.logging.Log;
import java.util.*;
import java.util.regex.Pattern;

import de.se_rwth.commons.logging.LogStub;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;


public class AutomataToolTest {
  
  @Before
  public void init() {
    LogStub.init();         // replace log by a sideffect free variant
    // LogStub.initPlusLog();  // for manual testing purpose only
    Log.enableFailQuick(false);
    Log.clearFindings();
    LogStub.clearPrints();
  }
  
  @Test
  public void executeSimple12() {
    AutomataTool.main(new String[] { "-i", "src/test/resources/example/Simple12.aut" });
    Log.printFindings();
    assertEquals(0, Log.getFindings().size());
    // LogStub.printPrints();
    List<String> p = LogStub.getPrints();
    assertEquals(6, p.size());
    assertTrue(Log.getFindings().isEmpty());
  }
  
}
