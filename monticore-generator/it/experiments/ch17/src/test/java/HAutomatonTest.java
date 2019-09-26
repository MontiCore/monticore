/* (c) https://github.com/MontiCore/monticore */
import de.se_rwth.commons.logging.Log;
import hautomaton._parser.HAutomatonParser;
import org.junit.BeforeClass;
import org.junit.Test;
import sautomaton._ast.ASTAutomaton;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertTrue;

public class HAutomatonTest {

  @BeforeClass
  public static void setup() {
    Log.init();
    Log.enableFailQuick(false);
  }


  @Test
  public void testPingPong() throws IOException {
    Optional<ASTAutomaton> a = new HAutomatonParser().parse("src/main/resources/hautomaton/PingPong.aut");
    assertTrue(a.isPresent());
  }
}
