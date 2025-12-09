/* (c) https://github.com/MontiCore/monticore */

import automata.AutomataMill;
import automata._ast.ASTAutomaton;
import automata._ast.ASTState;
import automata._parser.AutomataParser;
import de.monticore.runtime.junit.MCAssertions;
import de.monticore.runtime.junit.TestWithMCLanguage;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Main class for the some Demonstration to Parse
 */
@TestWithMCLanguage(AutomataMill.class)
public class LexicalModesParseTest {

  @Test
  public void testParse1() throws IOException {
    AutomataParser p = AutomataMill.parser();
    String aut = "automaton PingPong {"
            + "state Ping;"
            + "}";
    Optional<ASTAutomaton> at = p.parse_String(aut);
    assertTrue(at.isPresent());
    MCAssertions.assertNoFindings();
  }

  @Test
  public void testParse2() throws IOException {
    AutomataParser p = AutomataMill.parser();
    String aut = "automaton PingPong {"
            + "state Ping;"
            + "<dies ist beliebiger Text mit state>"
            + "}";
    Optional<ASTAutomaton> at = p.parse_String(aut);
    assertTrue(at.isPresent());
    ASTAutomaton ast = at.get();
    assertEquals(1, ast.sizeStates());
    assertEquals(1, ast.sizeTags());
    assertEquals("dies ist beliebiger Text mit state", ast.getTag(0).getText2());
    MCAssertions.assertNoFindings();
  }

}
