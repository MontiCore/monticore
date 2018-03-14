/* (c) https://github.com/MontiCore/monticore */

package mc.grammar;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Optional;

import org.junit.Test;

import mc.GeneratorIntegrationsTest;
import mc.grammar.ittestgrammar._ast.ASTMCGrammar;
import mc.grammar.ittestgrammar_withconcepts._parser.ItTestGrammar_WithConceptsParser;

public class MCParserTest extends GeneratorIntegrationsTest {
  
  @Test
  public void test1() throws IOException {
    
    ItTestGrammar_WithConceptsParser parser = new ItTestGrammar_WithConceptsParser();
    
    Optional<ASTMCGrammar> ast = parser.parseMCGrammar("src/test/resources/mc/grammar/SimpleGrammarWithConcept.mc4");
    
    assertTrue(ast.isPresent());
    
    
  }
}
