/* (c) https://github.com/MontiCore/monticore */

package mc.feature.linepreprocess.embedding;

import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.feature.linepreprocess.embedding.automaton._ast.ASTAutomaton;
import mc.feature.linepreprocess.embedding.automatonwithaction.AutomatonWithActionMill;
import mc.feature.linepreprocess.embedding.automatonwithaction._parser.AutomatonWithActionParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestWithMCLanguage(AutomatonWithActionMill.class)
public class AutomatonOverallParserTest {

  @Test
  public void testRun() throws IOException {
    AutomatonWithActionParser p = AutomatonWithActionMill.parser();
    Optional<ASTAutomaton> ast =
        p.parse_StringAutomaton("automaton foo { a-e>b / { DUMMY_ACTION } ; } ");
    assertFalse(p.hasErrors());
    assertTrue(ast.isPresent());
  }
  
}
