/* (c) https://github.com/MontiCore/monticore */

package mc.feature.linepreprocess.embedding;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;

import mc.GeneratorIntegrationsTest;
import mc.feature.linepreprocess.embedding.automaton._ast.ASTAutomaton;
import mc.feature.linepreprocess.embedding.automatonwithaction._parser.AutomatonWithActionParser;

public class AutomatonOverallParserTest extends GeneratorIntegrationsTest {
  
  @Test
  public void testRun() throws IOException {
    StringReader s = new StringReader("automaton foo { a-e>b / { DUMMY_ACTION } ; } ");
    AutomatonWithActionParser p = new  AutomatonWithActionParser();
    java.util.Optional<ASTAutomaton> ast = p.parseAutomaton(s);
    assertFalse(p.hasErrors());
    assertTrue(ast.isPresent());
  }
  
}
