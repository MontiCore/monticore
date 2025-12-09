/* (c) https://github.com/MontiCore/monticore */

import de.monticore.runtime.junit.MCAssertions;
import de.monticore.runtime.junit.TestWithMCLanguage;
import de.se_rwth.commons.logging.*;
import hautomata.HAutomataMill;
import org.junit.jupiter.api.Test;
import sautomata._ast.ASTAutomaton;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestWithMCLanguage(HAutomataMill.class)
public class HAutomataTest {
  
  @Test
  public void testPingPong() throws IOException {
    Optional<ASTAutomaton> a = HAutomataMill.parser().parse("src/main/resources/hautomata/PingPong.aut");
    assertTrue(a.isPresent());
    MCAssertions.assertNoFindings();
  }
}
