/* (c) https://github.com/MontiCore/monticore */
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Optional;

import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar_withconcepts._parser.Grammar_WithConceptsParser;


public class GrammarParseTest {
  
  public static void main(String[] args) {

    Optional<ASTMCGrammar> ast = Optional.empty();
    String filename = "automaton/Automaton.mc4";
    Grammar_WithConceptsParser p =
            new Grammar_WithConceptsParser();

    try {
      // Create the AST
      ast = p.parseMCGrammar(filename);
    }
    catch (IOException e) {
      fail();
    }
    assertTrue(ast.isPresent());
  }
}
