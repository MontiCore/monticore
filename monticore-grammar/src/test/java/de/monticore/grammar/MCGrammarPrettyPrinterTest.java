/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar;

import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar_withconcepts.Grammar_WithConceptsMill;
import de.monticore.grammar.grammar_withconcepts._parser.Grammar_WithConceptsParser;
import de.monticore.runtime.junit.TestWithMCLanguage;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestWithMCLanguage(Grammar_WithConceptsMill.class)
public class MCGrammarPrettyPrinterTest {

  @Test
  // Test simple grammar
  public void testStatechart() throws IOException {
    String model = "target/resources/test/de/monticore/Statechart.mc4";
    
    // Parsing input
    Grammar_WithConceptsParser parser = Grammar_WithConceptsMill.parser();
    Optional<ASTMCGrammar> result = parser.parseMCGrammar(model);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTMCGrammar grammar = result.get();
    
    // Prettyprinting input
    String output = Grammar_WithConceptsMill.prettyPrint(grammar, false);

    // Parsing printed input
    result = parser.parseMCGrammar(new StringReader (output));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(grammar.deepEquals(result.get()), "Failed to deep equals: \n" + output);
  }
  
  @Test
  // Test grammar with symbols and scopes
  public void testAutomaton() throws IOException {
    String model = "target/resources/test/Automaton.mc4";
    
    // Parsing input
    Grammar_WithConceptsParser parser = Grammar_WithConceptsMill.parser();
    Optional<ASTMCGrammar> result = parser.parseMCGrammar(model);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTMCGrammar grammar = result.get();
    
    // Prettyprinting input
    String output = Grammar_WithConceptsMill.prettyPrint(grammar, false);
    
    // Parsing printed input
    result = parser.parseMCGrammar(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(grammar.deepEquals(result.get()), "Failed to deep equals: \n" + output);
  }

  @Test
  // Test grammar with symbols and scopes
  public void testGrammar() throws IOException {
    String model = "target/resources/test/de/monticore/TestGrammar.mc4";

    // Parsing input
    Grammar_WithConceptsParser parser = Grammar_WithConceptsMill.parser();
    Optional<ASTMCGrammar> result = parser.parseMCGrammar(model);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTMCGrammar grammar = result.get();

    // Prettyprinting input
    String output = Grammar_WithConceptsMill.prettyPrint(grammar, false);

    // Parsing printed input
    result = parser.parseMCGrammar(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(grammar.deepEquals(result.get()), "Failed to deep equals: \n" + output);
  }

  @Test
  // test lexicals with lexer commands and end actions
  public void testLexicals() throws IOException {
    String model = "target/resources/test/de/monticore/common/TestLexicals.mc4";

    // Parsing input
    Grammar_WithConceptsParser parser = Grammar_WithConceptsMill.parser();
    Optional<ASTMCGrammar> result = parser.parseMCGrammar(model);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTMCGrammar grammar = result.get();

    // Prettyprinting input
    String output = Grammar_WithConceptsMill.prettyPrint(grammar, false);

    // Parsing printed input
    result = parser.parseMCGrammar(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(grammar.deepEquals(result.get()));
  }

  @Test
  // test annotations
  public void testAnnotations() throws IOException {
    String model = "target/resources/test/de/monticore/Annotations.mc4";

    // Parsing input
    Grammar_WithConceptsParser parser = Grammar_WithConceptsMill.parser();
    Optional<ASTMCGrammar> result = parser.parseMCGrammar(model);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTMCGrammar grammar = result.get();

    // Prettyprinting input
    String output = Grammar_WithConceptsMill.prettyPrint(grammar, false);

    // Parsing printed input
    result = parser.parseMCGrammar(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(grammar.deepEquals(result.get()), "Failed to deep equals: \n" + output);
  }

}
