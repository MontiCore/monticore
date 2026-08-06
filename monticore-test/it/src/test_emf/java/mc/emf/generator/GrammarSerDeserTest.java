/* (c) https://github.com/MontiCore/monticore */

package mc.emf.generator;

import de.monticore.emf.util.AST2ModelFiles;
import de.monticore.emf.util.compare.AstEmfDiffUtility;
import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.grammar.ittestgrammar._ast.ASTMCGrammar;
import mc.grammar.ittestgrammar._ast.ItTestGrammarPackage;
import mc.grammar.ittestgrammar_withconcepts.ItTestGrammar_WithConceptsMill;
import org.eclipse.emf.compare.diff.metamodel.DiffElement;
import org.eclipse.emf.ecore.EObject;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestWithMCLanguage(ItTestGrammar_WithConceptsMill.class)
public class GrammarSerDeserTest {
  
  @Disabled // TODO
  @Test
  public void testSerializeDesirializeASTMCGrammarInstance()
      throws InterruptedException, IOException {
    String path1 = "mc/emf/generator/Automaton.mc4";
    Optional<ASTMCGrammar> automatonGrammar =
        ItTestGrammar_WithConceptsMill.parser().parse("src/test/resources/" + path1);
    assertTrue(automatonGrammar.isPresent());
    AST2ModelFiles.get().serializeASTInstance(automatonGrammar.get(), "Automaton");
    
    EObject deserAutomatonGrammar = AST2ModelFiles.get()
        .deserializeASTInstance("ASTMCGrammar_Automaton", ItTestGrammarPackage.eINSTANCE);
    assertNotNull(deserAutomatonGrammar);
    assertInstanceOf(ASTMCGrammar.class, deserAutomatonGrammar);
    
    assertTrue(automatonGrammar.get().deepEquals(deserAutomatonGrammar));
    assertEquals("Automaton", ((ASTMCGrammar) deserAutomatonGrammar).getName());
    
    List<DiffElement> diffs = AstEmfDiffUtility.getAllAstDiffs(automatonGrammar.get(),
        (ASTMCGrammar) deserAutomatonGrammar);
    assertTrue(diffs.isEmpty());
  }
}
