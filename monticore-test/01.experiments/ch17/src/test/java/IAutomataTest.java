/* (c) https://github.com/MontiCore/monticore */

import de.monticore.runtime.junit.MCAssertions;
import de.monticore.runtime.junit.TestWithMCLanguage;
import iautomata.IAutomataMill;
import iautomatacomp._ast.ASTAutomaton;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestWithMCLanguage(IAutomataMill.class)
public class IAutomataTest {

  @Test
  public void testPingPong() throws IOException {
    Optional<ASTAutomaton> a = IAutomataMill.parser().parse("src/main/resources/iautomata/PingPong.aut");
    assertTrue(a.isPresent());
    MCAssertions.assertNoFindings();
  }
}
