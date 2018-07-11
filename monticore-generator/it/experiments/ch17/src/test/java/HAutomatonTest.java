/* (c) Monticore license: https://github.com/MontiCore/monticore */
import hautomaton._parser.HAutomatonParser;
import org.junit.Test;
import sautomaton._ast.ASTAutomaton;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertTrue;

/**
 * Created by
 *
 * @author KH
 */
public class HAutomatonTest {
  
  
  @Test
  public void testPingPong() throws IOException {
    Optional<ASTAutomaton> a = new HAutomatonParser().parse("src/main/resources/hautomaton/PingPong.aut");
    assertTrue(a.isPresent());
  }
}
