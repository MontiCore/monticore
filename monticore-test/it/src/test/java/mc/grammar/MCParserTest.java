/* (c) https://github.com/MontiCore/monticore */

package mc.grammar;

import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.grammar.ittestgrammar._ast.ASTMCGrammar;
import mc.grammar.ittestgrammar_withconcepts.ItTestGrammar_WithConceptsMill;
import mc.grammar.ittestgrammar_withconcepts._parser.ItTestGrammar_WithConceptsParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestWithMCLanguage(ItTestGrammar_WithConceptsMill.class)
public class MCParserTest {
  
  @Test
  public void test1() throws IOException {
    
    ItTestGrammar_WithConceptsParser parser = ItTestGrammar_WithConceptsMill.parser();
    
    Optional<ASTMCGrammar> ast = parser.parseMCGrammar("src/test/resources/mc/grammar/SimpleGrammarWithConcept.mc4");
    
    assertTrue(ast.isPresent());
  }
}
