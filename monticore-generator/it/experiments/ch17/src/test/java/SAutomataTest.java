/* (c) https://github.com/MontiCore/monticore */

import de.se_rwth.commons.logging.*;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.BeforeClass;
import org.junit.Test;
import sautomata._ast.ASTAutomaton;
import sautomata._parser.SAutomataParser;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertTrue;

public class SAutomataTest {

  @BeforeClass
  public static void setup() {
    LogStub.init();         // replace log by a sideffect free variant
        // LogStub.initPlusLog();  // for manual testing purpose only
    Log.enableFailQuick(false);
  }

  @Test
  public void testPingPong() throws IOException {
    Optional<ASTAutomaton> a = new SAutomataParser().parse("src/main/resources/PingPong.aut");
    assertTrue(a.isPresent());
  }

  @Test
  public void testSimple12() throws RecognitionException, IOException {
    Optional<ASTAutomaton> a = new SAutomataParser().parse("src/main/resources/Simple12.aut");
    assertTrue(a.isPresent());
  }

}
