/* (c) https://github.com/MontiCore/monticore */

package mc.grammar;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Optional;

import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import mc.GeneratorIntegrationsTest;
import mc.grammar.ittestgrammar._ast.ASTMCGrammar;
import mc.grammar.ittestgrammar_withconcepts._parser.ItTestGrammar_WithConceptsParser;
import de.se_rwth.commons.logging.Log;

public class MCParserTest extends GeneratorIntegrationsTest {
  
  @Before
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void test1() throws IOException {
    
    ItTestGrammar_WithConceptsParser parser = new ItTestGrammar_WithConceptsParser();
    
    Optional<ASTMCGrammar> ast = parser.parseMCGrammar("src/test/resources/mc/grammar/SimpleGrammarWithConcept.mc4");
    
    assertTrue(ast.isPresent());
  
  
    assertTrue(Log.getFindings().isEmpty());
  }
}
