/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.prettyprint;

import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar_withconcepts.Grammar_WithConceptsMill;
import de.monticore.grammar.grammar_withconcepts._parser.Grammar_WithConceptsParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MCGrammarPrettyPrinterTest {

  @BeforeEach
  public void setup(){
    LogStub.init();
    Log.enableFailQuick(false);
    Grammar_WithConceptsMill.reset();
    Grammar_WithConceptsMill.init();
  }

  @Test
  // Test simple grammar
  public void testStatechart() throws IOException {
    String model = "src/test/resources/de/monticore/statechart/Statechart.mc4";
    
    // Parsing input
    Grammar_WithConceptsParser parser = Grammar_WithConceptsMill.parser();
    Optional<ASTMCGrammar> result = parser.parseMCGrammar(model);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTMCGrammar grammar = result.get();
    
    // Prettyprinting input
    String output = Grammar_WithConceptsMill.prettyPrint(grammar, false);

    // Parsing printed input
    result = parser.parseMCGrammar(new StringReader (output));
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());

    Assertions.assertTrue(grammar.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  // Test grammar with symbols and scopes
  public void testAutomaton() throws IOException {
    String model = "src/test/resources/Automaton.mc4";
    
    // Parsing input
    Grammar_WithConceptsParser parser = new Grammar_WithConceptsParser();
    Optional<ASTMCGrammar> result = parser.parseMCGrammar(model);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTMCGrammar grammar = result.get();
    
    // Prettyprinting input
    String output = Grammar_WithConceptsMill.prettyPrint(grammar, false);
    
    // Parsing printed input
    result = parser.parseMCGrammar(new StringReader(output));
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());

    Assertions.assertTrue(grammar.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  // Test grammar with symbols and scopes
  public void testGrammar() throws IOException {
    String model = "src/test/resources/mc/grammars/TestGrammar.mc4";

    // Parsing input
    Grammar_WithConceptsParser parser = new Grammar_WithConceptsParser();
    Optional<ASTMCGrammar> result = parser.parseMCGrammar(model);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTMCGrammar grammar = result.get();

    // Prettyprinting input
    String output = Grammar_WithConceptsMill.prettyPrint(grammar, false);

    // Parsing printed input
    result = parser.parseMCGrammar(new StringReader(output));
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());

    Assertions.assertTrue(grammar.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  // test lexicals with lexer commands and end actions
  public void testLexicals() throws IOException {
    String model = "src/test/resources/mc/grammars/lexicals/TestLexicals.mc4";

    // Parsing input
    Grammar_WithConceptsParser parser = new Grammar_WithConceptsParser();
    Optional<ASTMCGrammar> result = parser.parseMCGrammar(model);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTMCGrammar grammar = result.get();

    // Prettyprinting input
    String output = Grammar_WithConceptsMill.prettyPrint(grammar, false);

    // Parsing printed input
    result = parser.parseMCGrammar(new StringReader(output));
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());

    Assertions.assertTrue(grammar.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  // test annotations
  public void testAnnotations() throws IOException {
    String model = "src/test/resources/de/monticore/Annotations.mc4";

    // Parsing input
    Grammar_WithConceptsParser parser = new Grammar_WithConceptsParser();
    Optional<ASTMCGrammar> result = parser.parseMCGrammar(model);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTMCGrammar grammar = result.get();

    // Prettyprinting input
    String output = Grammar_WithConceptsMill.prettyPrint(grammar, false);

    // Parsing printed input
    result = parser.parseMCGrammar(new StringReader(output));
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());

    Assertions.assertTrue(grammar.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

}
