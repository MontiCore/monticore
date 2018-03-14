/* (c)  https://github.com/MontiCore/monticore */

package mc.emf.generator;

import de.monticore.emf.util.AST2ModelFiles;
import de.monticore.emf.util.compare.AstEmfDiffUtility;
import mc.grammar.ittestgrammar._ast.ASTMCGrammar;
import mc.grammar.ittestgrammar._ast.ItTestGrammarPackage;
import mc.grammar.ittestgrammar_withconcepts._parser.ItTestGrammar_WithConceptsParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.antlr.v4.runtime.RecognitionException;
import org.eclipse.emf.compare.diff.metamodel.DiffElement;
import org.eclipse.emf.ecore.EObject;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.junit.Assert.fail;

public class GrammarSerDeserTest {

  @BeforeClass
  public static void setup() {
    LogStub.init();
    Log.enableFailQuick(false);
  }

  @Test
  public void testSerializeDesirializeASTMCGrammarInstance() {
    try {

      String path1 = "mc/emf/generator/Automaton.mc4";
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
  }
}
