/* (c) https://github.com/MontiCore/monticore */

package mc.feature.linepreprocess.embedding;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mc.GeneratorIntegrationsTest;
import mc.feature.linepreprocess.embedding.automaton._ast.ASTAutomaton;
import mc.feature.linepreprocess.embedding.automatonwithaction._parser.AutomatonWithActionParser;

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
    Assertions.assertFalse(p.hasErrors());
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
}
