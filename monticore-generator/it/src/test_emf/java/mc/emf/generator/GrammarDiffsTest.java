
package mc.emf.generator;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;

public class GrammarDiffsTest {
  @BeforeClass
  public static void setup() {
    LogStub.init();
    Log.enableFailQuick(false);
  }

  //todo: reactivate test
  // line 45 has NullPointer exception, because inherited attribute from ITTerminal in Terminal
  // if ASTTerminal_Name is not called in initializePackageContents, this should work

 /* @Test
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
  }*/

}
