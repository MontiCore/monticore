/* (c) https://github.com/MontiCore/monticore */

import de.se_rwth.commons.logging.*;
import hautomata._parser.HAutomataParser;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import sautomata._ast.ASTAutomaton;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertTrue;

public class HAutomataTest {
  
  @Before
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testPingPong() throws IOException {
    Optional<ASTAutomaton> a = new HAutomataParser().parse("src/main/resources/hautomata/PingPong.aut");
    assertTrue(a.isPresent());
    assertTrue(Log.getFindings().isEmpty());
  }
}
