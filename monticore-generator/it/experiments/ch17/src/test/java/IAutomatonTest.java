/* (c) https://github.com/MontiCore/monticore */
import iautomaton._parser.IAutomatonParser;
import iautomatoncomp._ast.ASTAutomaton;
import org.junit.Test;
import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertTrue;

public class IAutomatonTest {


  @Test
  public void testPingPong() throws IOException {
    Optional<ASTAutomaton> a = new IAutomatonParser().parse("src/main/resources/iautomaton/PingPong.aut");
    assertTrue(a.isPresent());
  }
}
