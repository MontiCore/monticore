/* (c) https://github.com/MontiCore/monticore */
import de.se_rwth.commons.logging.Log;
import iautomata._parser.IAutomataParser;
import iautomatacomp._ast.ASTAutomaton;
import org.junit.BeforeClass;
import org.junit.Test;
import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertTrue;

public class IAutomataTest {

  @BeforeClass
  public static void setup() {
    Log.init();
    Log.enableFailQuick(false);
  }

  @Test
  public void testPingPong() throws IOException {
    Optional<ASTAutomaton> a = new IAutomataParser().parse("src/main/resources/iautomata/PingPong.aut");
    assertTrue(a.isPresent());
  }
}
