/* (c) https://github.com/MontiCore/monticore */

package mc.emf.generator;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;

public class GrammarSerDeserTest {

  @BeforeClass
  public static void setup() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  //todo: reactivate test
  // line 45 has NullPointer exception, because inherited attribute from ITTerminal in Terminal
  // if ASTTerminal_Name is not called in initializePackageContents, this should work

/*
  @Test
  public void testSerializeDesirializeASTMCGrammarInstance() {
    try {

      String path1 = "mc/emf/generator/Automata.mc4";
      Optional<ASTMCGrammar> automatonGrammar = new ItTestGrammar_WithConceptsParser()
          .parse("src/test/resources/" + path1);
      assertTrue(automatonGrammar.isPresent());
      AST2ModelFiles.get().serializeASTInstance(automatonGrammar.get(), "Automaton");

      EObject deserAutomatonGrammar = AST2ModelFiles.get().deserializeASTInstance("ASTMCGrammar_Automaton",
          ItTestGrammarPackage.eINSTANCE);
      assertNotNull(deserAutomatonGrammar);
      assertTrue(deserAutomatonGrammar instanceof ASTMCGrammar);

      assertTrue(automatonGrammar.get().deepEquals(deserAutomatonGrammar));
      assertEquals("Automaton", ((ASTMCGrammar) deserAutomatonGrammar).getName());

      List<DiffElement> diffs = AstEmfDiffUtility.getAllAstDiffs(automatonGrammar.get(),
          (ASTMCGrammar) deserAutomatonGrammar);
      assertTrue(diffs.isEmpty());
    }
    catch (RecognitionException | IOException e) {
      fail("Should not reach this, but: " + e);
    }
    catch (InterruptedException e) {
      fail("Should not reach this, but: " + e);
    }
  }*/
}
