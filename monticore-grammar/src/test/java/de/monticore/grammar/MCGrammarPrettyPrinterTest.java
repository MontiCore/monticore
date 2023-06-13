/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar;

import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar_withconcepts.Grammar_WithConceptsMill;
import de.monticore.grammar.grammar_withconcepts._parser.Grammar_WithConceptsParser;
import de.monticore.grammar.grammar_withconcepts._prettyprint.Grammar_WithConceptsFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MCGrammarPrettyPrinterTest {
  
  @Before
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
    Grammar_WithConceptsMill.reset();
    Grammar_WithConceptsMill.init();
  }

  @Test
  // Test simple grammar
  public void testStatechart() throws IOException {
    String model = "src/test/resources/de/monticore/Statechart.mc4";
    
    // Parsing input
    Grammar_WithConceptsParser parser = new Grammar_WithConceptsParser();
    Optional<ASTMCGrammar> result = parser.parseMCGrammar(model);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTMCGrammar grammar = result.get();
    
    // Prettyprinting input
    Grammar_WithConceptsFullPrettyPrinter prettyPrinter = new Grammar_WithConceptsFullPrettyPrinter(new IndentPrinter());
    String output = prettyPrinter.prettyprint(grammar);

    // Parsing printed input
    result = parser.parseMCGrammar(new StringReader (output));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue("Failed to deep equals: \n" + output, grammar.deepEquals(result.get()));
    
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
    Grammar_WithConceptsFullPrettyPrinter prettyPrinter = new Grammar_WithConceptsFullPrettyPrinter(new IndentPrinter());
    String output = prettyPrinter.prettyprint(grammar);
    
    // Parsing printed input
    result = parser.parseMCGrammar(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue("Failed to deep equals: \n" + output, grammar.deepEquals(result.get()));
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  // Test grammar with symbols and scopes
  public void testGrammar() throws IOException {
    String model = "src/test/resources/de/monticore/TestGrammar.mc4";

    // Parsing input
    Grammar_WithConceptsParser parser = new Grammar_WithConceptsParser();
    Optional<ASTMCGrammar> result = parser.parseMCGrammar(model);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTMCGrammar grammar = result.get();

    // Prettyprinting input
    Grammar_WithConceptsFullPrettyPrinter prettyPrinter = new Grammar_WithConceptsFullPrettyPrinter(new IndentPrinter());
    String output = prettyPrinter.prettyprint(grammar);

    // Parsing printed input
    result = parser.parseMCGrammar(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue("Failed to deep equals: \n" + output, grammar.deepEquals(result.get()));
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  // test lexicals with lexer commands and end actions
  public void testLexicals() throws IOException {
    String model = "src/test/resources/de/monticore/common/TestLexicals.mc4";

    // Parsing input
    Grammar_WithConceptsParser parser = new Grammar_WithConceptsParser();
    Optional<ASTMCGrammar> result = parser.parseMCGrammar(model);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTMCGrammar grammar = result.get();

    // Prettyprinting input
    Grammar_WithConceptsFullPrettyPrinter prettyPrinter = new Grammar_WithConceptsFullPrettyPrinter(new IndentPrinter());
    String output = prettyPrinter.prettyprint(grammar);

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
    Grammar_WithConceptsFullPrettyPrinter prettyPrinter = new Grammar_WithConceptsFullPrettyPrinter(new IndentPrinter());
    String output = prettyPrinter.prettyprint(grammar);

    // Parsing printed input
    result = parser.parseMCGrammar(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue("Failed to deep equals: \n" + output, grammar.deepEquals(result.get()));
  
    assertTrue(Log.getFindings().isEmpty());
  }

}
