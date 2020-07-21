/* (c) https://github.com/MontiCore/monticore */

import de.se_rwth.commons.logging.*;
import hautomata._parser.HAutomataParser;
import org.junit.BeforeClass;
import org.junit.Test;
import sautomata._ast.ASTAutomaton;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertTrue;

public class HAutomataTest {

  @BeforeClass
  public static void setup() {
    LogStub.init();         // replace log by a sideffect free variant
    // LogStub.initPlusLog();  // for manual testing purpose only
    Log.enableFailQuick(false);
  }


  @Test
  public void testPingPong() throws IOException {
    Optional<ASTAutomaton> a = new HAutomataParser().parse("src/main/resources/hautomata/PingPong.aut");
    assertTrue(a.isPresent());
  }
}
