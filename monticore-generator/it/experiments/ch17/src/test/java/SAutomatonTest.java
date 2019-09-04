/* (c) https://github.com/MontiCore/monticore */
import de.se_rwth.commons.logging.Log;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.BeforeClass;
import org.junit.Test;
import sautomaton._ast.ASTAutomaton;
import sautomaton._parser.SAutomatonParser;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertTrue;

public class SAutomatonTest {

  @BeforeClass
  public static void setup() {
    Log.init();
    Log.enableFailQuick(false);
  }

  @Test
  public void testPingPong() throws IOException {
    Optional<ASTAutomaton> a = new SAutomatonParser().parse("src/main/resources/PingPong.aut");
    assertTrue(a.isPresent());
  }

  @Test
  public void testSimple12() throws RecognitionException, IOException {
    Optional<ASTAutomaton> a = new SAutomatonParser().parse("src/main/resources/Simple12.aut");
    assertTrue(a.isPresent());
  }

}
