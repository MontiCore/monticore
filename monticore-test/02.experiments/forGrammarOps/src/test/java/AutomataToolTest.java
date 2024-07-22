/* (c) https://github.com/MontiCore/monticore */

import automata.AutomataTool;
import de.se_rwth.commons.logging.Log;
import java.util.*;

import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;


public class AutomataToolTest {
  
  @BeforeEach
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
    Assertions.assertEquals(0, Log.getFindings().size());
    // LogStub.printPrints();
    List<String> p = LogStub.getPrints();
    Assertions.assertEquals(6, p.size());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
}
