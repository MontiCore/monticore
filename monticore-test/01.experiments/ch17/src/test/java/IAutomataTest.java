/* (c) https://github.com/MontiCore/monticore */

import de.se_rwth.commons.logging.*;
import iautomata._parser.IAutomataParser;
import iautomatacomp._ast.ASTAutomaton;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertTrue;

public class IAutomataTest {

  @Before
  public void setup() {
    LogStub.init();         // replace log by a sideffect free variant
        // LogStub.initPlusLog();  // for manual testing purpose only
    Log.enableFailQuick(false);
  }

  @Test
  public void testPingPong() throws IOException {
    Optional<ASTAutomaton> a = new IAutomataParser().parse("src/main/resources/iautomata/PingPong.aut");
    assertTrue(a.isPresent());
    assertTrue(Log.getFindings().isEmpty());
  }
}
