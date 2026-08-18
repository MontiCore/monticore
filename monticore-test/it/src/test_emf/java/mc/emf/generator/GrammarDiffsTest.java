/* (c) https://github.com/MontiCore/monticore */

package mc.emf.generator;

import de.monticore.emf.util.compare.AstEmfDiffUtility;
import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.grammar.ittestgrammar._ast.ASTMCGrammar;
import mc.grammar.ittestgrammar_withconcepts.ItTestGrammar_WithConceptsMill;
import org.eclipse.emf.compare.diff.metamodel.DiffElement;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestWithMCLanguage(ItTestGrammar_WithConceptsMill.class)
public class GrammarDiffsTest {
  
  @Disabled // TODO
  @Test
  public void testAstGrammarDiffs() throws IOException, InterruptedException {
    Optional<ASTMCGrammar> grammar1 = ItTestGrammar_WithConceptsMill.parser()
        .parse("src/test/resources/mc/emf/generator/Automaton.mc4");
    
    Optional<ASTMCGrammar> grammar2 = ItTestGrammar_WithConceptsMill.parser()
        .parse("src/test/resources/mc/emf/generator/Automaton2.mc4");
    
    assertTrue(grammar1.isPresent());
    assertTrue(grammar2.isPresent());
    
    List<DiffElement> diffs = AstEmfDiffUtility.getAllAstDiffs(grammar2.get(), grammar1.get());
    
    assertEquals(4, diffs.size()); // is 4
    
    assertEquals("Attribute Name in Automaton2 has changed from Automaton to Automaton2",
        diffs.get(0).toString());
    
    assertTrue(diffs.get(1).toString()
        .contains("Attribute Name in \">>\" has changed from \">\" to \">>\""));
    
    assertTrue(diffs.get(2).toString().contains("ASTNonTerminal"));
    assertTrue(diffs.get(2).toString().contains("Action"));
    assertTrue(diffs.get(2).toString().contains("has been added"));
    
    assertEquals("Action has been added", diffs.get(3).toString());
  }
  
}
