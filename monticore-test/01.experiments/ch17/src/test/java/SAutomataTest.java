/* (c) https://github.com/MontiCore/monticore */

import de.monticore.runtime.junit.TestWithMCLanguage;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.jupiter.api.Test;
import sautomata.SAutomataMill;
import sautomata._ast.ASTAutomaton;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static de.monticore.runtime.junit.MCAssertions.assertNoFindings;

@TestWithMCLanguage(SAutomataMill.class)
public class SAutomataTest {

  @Test
  public void testPingPong() throws IOException {
    Optional<ASTAutomaton> a = SAutomataMill.parser().parse("src/main/resources/PingPong.aut");
    assertTrue(a.isPresent());
    assertNoFindings();
  }

  @Test
  public void testSimple12() throws RecognitionException, IOException {
    Optional<ASTAutomaton> a = SAutomataMill.parser().parse("src/main/resources/Simple12.aut");
    assertTrue(a.isPresent());
    assertNoFindings();
  }

}
