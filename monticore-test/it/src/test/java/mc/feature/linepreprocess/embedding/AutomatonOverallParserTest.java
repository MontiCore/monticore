/* (c) https://github.com/MontiCore/monticore */

package mc.feature.linepreprocess.embedding;

import java.io.IOException;
import java.io.StringReader;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mc.GeneratorIntegrationsTest;
import mc.feature.linepreprocess.embedding.automaton._ast.ASTAutomaton;
import mc.feature.linepreprocess.embedding.automatonwithaction._parser.AutomatonWithActionParser;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AutomatonOverallParserTest extends GeneratorIntegrationsTest {
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testRun() throws IOException {
    StringReader s = new StringReader("automaton foo { a-e>b / { DUMMY_ACTION } ; } ");
    AutomatonWithActionParser p = new  AutomatonWithActionParser();
    java.util.Optional<ASTAutomaton> ast = p.parseAutomaton(s);
    assertFalse(p.hasErrors());
    assertTrue(ast.isPresent());
    assertTrue(Log.getFindings().isEmpty());
  }
  
}
