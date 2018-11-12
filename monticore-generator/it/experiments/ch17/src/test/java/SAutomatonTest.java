/* (c) Monticore license: https://github.com/MontiCore/monticore */
import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;
import sautomaton._ast.ASTAutomaton;
import sautomaton._parser.SAutomatonParser;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertTrue;

public class SAutomatonTest {


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
