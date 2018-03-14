/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.prettyprint;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar_withconcepts._parser.Grammar_WithConceptsParser;
import de.monticore.grammar.prettyprint.Grammar_WithConceptsPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;

public class MCGrammarPrettyPrinterTest {
  
  @BeforeClass
  public static void setup() {
    LogStub.init();
    Log.enableFailQuick(false);
  }

  @Test
  // Test simple grammar
  public void testStatechart() throws IOException {
    String model = "src/test/resources/de/monticore/statechart/Statechart.mc4";
    
    // Parsing input
    Grammar_WithConceptsParser parser = new Grammar_WithConceptsParser();
    Optional<ASTMCGrammar> result = parser.parse(model);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTMCGrammar grammar = result.get();
    
    // Prettyprinting input
    Grammar_WithConceptsPrettyPrinter prettyPrinter = new Grammar_WithConceptsPrettyPrinter(new IndentPrinter());
    String output = prettyPrinter.prettyprint(grammar);
    
    // Parsing printed input
    result = parser.parse(new StringReader (output));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(grammar.deepEquals(result.get()));
    
  }
  
  @Test
  // Test grammar with concepts and java
  public void testTypes() throws IOException {
    String model = "src/test/resources/mc/grammars/types/TestTypes.mc4";
    
    // Parsing input
    Grammar_WithConceptsParser parser = new Grammar_WithConceptsParser();
    Optional<ASTMCGrammar> result = parser.parse(model);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTMCGrammar grammar = result.get();
    
    // Prettyprinting input
    Grammar_WithConceptsPrettyPrinter prettyPrinter = new Grammar_WithConceptsPrettyPrinter(new IndentPrinter());
    String output = prettyPrinter.prettyprint(grammar);
    
    // Parsing printed input
    result = parser.parse(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(grammar.deepEquals(result.get()));
    
  }

}
