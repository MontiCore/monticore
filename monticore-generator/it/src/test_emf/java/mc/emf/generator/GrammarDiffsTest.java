/* (c)  https://github.com/MontiCore/monticore */

package mc.emf.generator;

import de.monticore.emf.util.compare.AstEmfDiffUtility;
import mc.grammar.ittestgrammar._ast.ASTMCGrammar;
import mc.grammar.ittestgrammar_withconcepts._parser.ItTestGrammar_WithConceptsParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.antlr.v4.runtime.RecognitionException;
import org.eclipse.emf.compare.diff.metamodel.DiffElement;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class GrammarDiffsTest {
  @BeforeClass
  public static void setup() {
    LogStub.init();
    Log.enableFailQuick(false);
  }

  @Test
  public void testAstGrammarDiffs() {
    try {
      Optional<ASTMCGrammar> grammar1 = new ItTestGrammar_WithConceptsParser()
          .parse("src/test/resources/mc/emf/generator/Automaton.mc4");

      Optional<ASTMCGrammar> grammar2 = new ItTestGrammar_WithConceptsParser()
          .parse("src/test/resources/mc/emf/generator/Automaton2.mc4");

      if (grammar1.isPresent() && grammar2.isPresent()) {

        List<DiffElement> diffs = AstEmfDiffUtility.getAllAstDiffs(grammar2.get(), grammar1.get());

        assertEquals(4, diffs.size()); // is 4

        assertEquals("Attribute Name in Automaton2 has changed from Automaton to Automaton2",
            diffs.get(0).toString());

        assertTrue(diffs.get(1).toString().contains("Attribute Name in \">>\" has changed from \">\" to \">>\""));

        assertTrue(diffs.get(2).toString().contains("ASTNonTerminal"));
        assertTrue(diffs.get(2).toString().contains("Action"));
        assertTrue(diffs.get(2).toString().contains("has been added"));

        assertEquals("Action has been added", diffs.get(3).toString());
      }
      else {
        fail("Parse errors");
      }
    }
    catch (RecognitionException | IOException e) {
      fail("Should not reach this, but: " + e);
    }
    catch (InterruptedException e) {
      fail("Should not reach this, but: " + e);
    }
  }

}
