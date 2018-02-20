/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.grammar.grammar._ast.ASTASTRule;
import de.monticore.grammar.grammar._ast.ASTClassProd;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._ast.ASTNonTerminal;
import de.monticore.grammar.grammar._ast.ASTSemanticpredicateOrAction;
import de.monticore.grammar.grammar_withconcepts._parser.Grammar_WithConceptsParser;
import de.monticore.grammar.transformation.GrammarTransformer;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;

public class MCGrammarParserTest {
  
  @BeforeClass
  public static void setup() {
    LogStub.init();
    Log.enableFailQuick(false);
  }

  @Test
  public void testParse() throws IOException {
    String model = "src/test/resources/de/monticore/statechart/Statechart.mc4";
    
    Grammar_WithConceptsParser parser = new Grammar_WithConceptsParser();
    Optional<ASTMCGrammar> result = parser.parse(model);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTMCGrammar grammar = result.get();
    assertEquals("Statechart", grammar.getName());
    assertEquals(7, grammar.getClassProdList().size());
    assertEquals(3, grammar.getExternalProdList().size());
    assertEquals(1, grammar.getInterfaceProdList().size());
    GrammarTransformer.transform(grammar);
  }
  
  @Test
  public void testASTRule() throws IOException {
    String str;
    
    str = "astrule MCGrammar = GrammarOption max=1 ;";
    Grammar_WithConceptsParser parser = new Grammar_WithConceptsParser();
    Optional<ASTASTRule> result = parser.parseASTRule(new StringReader(str));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    str = " astrule State = method public String getName(){ return \"\";};";
    result = parser.parseASTRule(new StringReader(str));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
  }
  
  @Test
  public void testSematicPred() throws IOException {
    String str = "{(0 != cmpCounter)}?";
    
    Grammar_WithConceptsParser parser = new Grammar_WithConceptsParser();
    Optional<ASTSemanticpredicateOrAction> result = parser.parseSemanticpredicateOrAction(new StringReader(str));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
  }
  
  @Test
  public void testParseTypes() throws IOException {
    String model = "src/test/resources/mc/grammars/types/TestTypes.mc4";
    
    Grammar_WithConceptsParser parser = new Grammar_WithConceptsParser();
    Optional<ASTMCGrammar> result = parser.parse(model.toString());
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
  }
  
  @Test
  public void testScript() throws IOException {
    String model = "src/test/resources/de/monticore/script/ScriptExample.mc4";
    
    Grammar_WithConceptsParser parser = new Grammar_WithConceptsParser();
    Optional<ASTMCGrammar> result = parser.parse(model);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
  }

  @Test
  public void testAutomatonV1() throws IOException {
    String model = "src/test/resources/de/monticore/script/AutomatonV1.mc4";
    
    Grammar_WithConceptsParser parser = new Grammar_WithConceptsParser();
    Optional<ASTMCGrammar> result = parser.parse(model);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
  }
  
  @Test
  public void testAutomatonV2() throws IOException {
    String model = "src/test/resources/de/monticore/script/AutomatonV2.mc4";
    
    Grammar_WithConceptsParser parser = new Grammar_WithConceptsParser();
    Optional<ASTMCGrammar> result = parser.parse(model);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
  }
  
  @Test
  public void testAutomatonV3() throws IOException {
    String model = "src/test/resources/de/monticore/script/AutomatonV3.mc4";
    
    Grammar_WithConceptsParser parser = new Grammar_WithConceptsParser();
    Optional<ASTMCGrammar> result = parser.parse(model);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
  }
  
  @Test
  public void testHierarchicalAutomaton() throws IOException {
    String model = "src/test/resources/de/monticore/script/HierarchicalAutomaton.mc4";

    Grammar_WithConceptsParser parser = new Grammar_WithConceptsParser();
    Optional<ASTMCGrammar> result = parser.parse(model);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
  }

  @Test
  public void testAutomatonWithInvsComp() throws IOException {
    String model = "src/test/resources/de/monticore/script/AutomatonWithInvsComp.mc4";

    Grammar_WithConceptsParser parser = new Grammar_WithConceptsParser();
    Optional<ASTMCGrammar> result = parser.parse(model);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
  }
  
  @Test
  public void testAutomatonWithInvs() throws IOException {
    String model = "src/test/resources/de/monticore/script/AutomatonWithInvs.mc4";
    
    Grammar_WithConceptsParser parser = new Grammar_WithConceptsParser();
    Optional<ASTMCGrammar> result = parser.parse(model);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
  }

  @Test
  public void testGrammarSymbolTableInfo() throws IOException {
    String model = "src/test/resources/de/monticore/symboltable/GrammarWithSymbolTableInfo.mc4";

    Grammar_WithConceptsParser parser = new Grammar_WithConceptsParser();
    Optional<ASTMCGrammar> result = parser.parse(model);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    ASTMCGrammar grammar = result.get();
    assertEquals(3, grammar.getClassProdList().size());

    ASTClassProd transition =  grammar.getClassProdList().get(2);
    ASTNonTerminal fromState = (ASTNonTerminal) transition.getAltList().get(0).getComponentList().get(0);
    assertTrue(fromState.isPresentReferencedSymbol());
    assertEquals("State", fromState.getReferencedSymbol());

    ASTNonTerminal toState = (ASTNonTerminal) transition.getAltList().get(0).getComponentList().get(0);
    assertTrue(toState.isPresentReferencedSymbol());
    assertEquals("State", toState.getReferencedSymbol());
  }
}
