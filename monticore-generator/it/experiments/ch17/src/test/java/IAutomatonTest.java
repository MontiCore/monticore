/* (c) https://github.com/MontiCore/monticore */
import de.se_rwth.commons.logging.Log;
import iautomaton._parser.IAutomatonParser;
import iautomatoncomp._ast.ASTAutomaton;
import org.junit.BeforeClass;
import org.junit.Test;
import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertTrue;

public class IAutomatonTest {

  @BeforeClass
  public static void setup() {
    Log.init();
    Log.enableFailQuick(false);
  }

  @Test
  public void testPingPong() throws IOException {
    Optional<ASTAutomaton> a = new IAutomatonParser().parse("src/main/resources/iautomaton/PingPong.aut");
    assertTrue(a.isPresent());
  }
}
