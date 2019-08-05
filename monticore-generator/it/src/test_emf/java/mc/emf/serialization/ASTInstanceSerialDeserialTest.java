/* (c)  https://github.com/MontiCore/monticore */
package mc.emf.serialization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.antlr.v4.runtime.RecognitionException;
import org.eclipse.emf.compare.diff.metamodel.DiffElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.junit.Test;

//import de.monticore.emf.fautomaton.automatonwithaction.actionautomaton._ast.ActionAutomatonPackage;
import de.monticore.emf.util.AST2ModelFiles;
import de.monticore.emf.util.compare.AstEmfDiffUtility;
import mc.GeneratorIntegrationsTest;
import mc.feature.fautomaton.automaton.flatautomaton._ast.ASTAutomaton;
import mc.feature.fautomaton.automaton.flatautomaton._ast.ASTState;
import mc.feature.fautomaton.automaton.flatautomaton._ast.ASTTransition;
import mc.feature.fautomaton.automaton.flatautomaton._ast.FlatAutomatonNodeFactory;
import mc.feature.fautomaton.automaton.flatautomaton._ast.FlatAutomatonPackage;
import mc.feature.fautomaton.automaton.flatautomaton._parser.FlatAutomatonParser;

public class ASTInstanceSerialDeserialTest extends GeneratorIntegrationsTest {
  
  @Test
  public void testSerializeAndDeserializeParseInstance() {
    try {
      Optional<ASTAutomaton> transB = new FlatAutomatonParser()
          .parse("src/test/resources/mc/emf/diff/Testautomat.aut");
          
      Optional<ASTAutomaton> transC = new FlatAutomatonParser()
          .parse("src/test/resources/mc/emf/diff/Testautomat2.aut");
      if (transB.isPresent() && transC.isPresent()) {
        AST2ModelFiles.get().serializeASTInstance(transB.get(),
            "B");
        AST2ModelFiles.get().serializeASTInstance(transC.get(),
            "C");
            
        EObject deserAstTransB = AST2ModelFiles.get().deserializeASTInstance("ASTAutomaton_B",
            FlatAutomatonPackage.eINSTANCE);
        assertNotNull(deserAstTransB);
        assertTrue(deserAstTransB instanceof ASTAutomaton);
        assertTrue(transB.get().deepEquals(deserAstTransB));
        assertFalse(transC.get().deepEquals(deserAstTransB));
        assertEquals("Testautomat", ((ASTAutomaton) deserAstTransB).getName());
        
        List<DiffElement> diffs = AstEmfDiffUtility.getAllAstDiffs(transB.get(),
            (ASTAutomaton) deserAstTransB);
        assertTrue(diffs.isEmpty());
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
  
  @Test
  public void testSerializeAndDeserializeCreatedInstance() {
    try {
      ASTAutomaton aut = FlatAutomatonNodeFactory.createASTAutomaton();
      ASTTransition trans = FlatAutomatonNodeFactory.createASTTransition();
      ASTState state1 = FlatAutomatonNodeFactory.createASTState();
      ASTState state2 = FlatAutomatonNodeFactory.createASTState();
      aut.setName("aut1");
      aut.getTransitionList().add(trans);
      aut.getStateList().add(state1);
      aut.getStateList().add(state2);
      state1.setName("state1");
      state2.setName("state2");
      
      trans.setFrom(state1.getName());
      trans.setTo(state2.getName());
      trans.setActivate("trans1");
      
      state1.setInitial(true);
      state2.setFinal(true);
      
      AST2ModelFiles.get().serializeASTInstance(aut, "Aut1");
      EObject deserObject = AST2ModelFiles.get().deserializeASTInstance("ASTAutomaton_Aut1",
          FlatAutomatonPackage.eINSTANCE);
      assertNotNull(deserObject);
      assertTrue(deserObject instanceof ASTAutomaton);
      ASTAutomaton serializedAut = (ASTAutomaton) deserObject;
      
      assertTrue(EcoreUtil.equals(aut, serializedAut));
      assertTrue(aut.deepEquals(serializedAut));
      assertTrue(serializedAut.getStateList().get(0).isInitial());
    }
    catch (IOException e) {
      fail("Should not reach this, but: " + e);
    }
  }
  
}
