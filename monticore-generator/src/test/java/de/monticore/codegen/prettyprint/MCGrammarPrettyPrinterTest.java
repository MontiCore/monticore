/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.prettyprint;

import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar_withconcepts.Grammar_WithConceptsMill;
import de.monticore.grammar.grammar_withconcepts._parser.Grammar_WithConceptsParser;
import de.monticore.grammar.grammarfamily.GrammarFamilyMill;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MCGrammarPrettyPrinterTest {

  @Before
  public void setup(){
    LogStub.init();
    Log.enableFailQuick(false);
    GrammarFamilyMill.reset();
    GrammarFamilyMill.init();
  }

  @Test
  // Test simple grammar
  public void testStatechart() throws IOException {
    String model = "src/test/resources/de/monticore/statechart/Statechart.mc4";
    
    // Parsing input
    Grammar_WithConceptsParser parser = new Grammar_WithConceptsParser();
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

    assertTrue(grammar.deepEquals(result.get()));
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  // Test grammar with symbols and scopes
  public void testAutomaton() throws IOException {
    String model = "src/test/resources/Automaton.mc4";
    
    // Parsing input
    Grammar_WithConceptsParser parser = new Grammar_WithConceptsParser();
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
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  // Test grammar with symbols and scopes
  public void testGrammar() throws IOException {
    String model = "src/test/resources/mc/grammars/TestGrammar.mc4";

    // Parsing input
    Grammar_WithConceptsParser parser = new Grammar_WithConceptsParser();
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
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  // test lexicals with lexer commands and end actions
  public void testLexicals() throws IOException {
    String model = "src/test/resources/mc/grammars/lexicals/TestLexicals.mc4";

    // Parsing input
    Grammar_WithConceptsParser parser = new Grammar_WithConceptsParser();
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
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  // test annotations
  public void testAnnotations() throws IOException {
    String model = "src/test/resources/de/monticore/Annotations.mc4";

    // Parsing input
    Grammar_WithConceptsParser parser = new Grammar_WithConceptsParser();
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
  
    assertTrue(Log.getFindings().isEmpty());
  }

}
