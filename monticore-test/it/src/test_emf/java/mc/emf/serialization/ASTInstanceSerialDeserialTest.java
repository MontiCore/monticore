/* (c) https://github.com/MontiCore/monticore */
package mc.emf.serialization;

import de.monticore.emf.util.AST2ModelFiles;
import de.monticore.emf.util.compare.AstEmfDiffUtility;
import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.feature.fautomaton.automaton.flatautomaton.FlatAutomatonMill;
import mc.feature.fautomaton.automaton.flatautomaton._ast.ASTAutomaton;
import mc.feature.fautomaton.automaton.flatautomaton._ast.ASTState;
import mc.feature.fautomaton.automaton.flatautomaton._ast.ASTTransition;
import mc.feature.fautomaton.automaton.flatautomaton._ast.FlatAutomatonPackage;
import org.eclipse.emf.compare.diff.metamodel.DiffElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

//import de.monticore.emf.fautomaton.automatonwithaction.actionautomaton._ast.ActionAutomatonPackage;

@Disabled
@TestWithMCLanguage(FlatAutomatonMill.class)
public class ASTInstanceSerialDeserialTest {
  
  @Test
  public void testSerializeAndDeserializeParseInstance() throws IOException, InterruptedException {
    Optional<ASTAutomaton> transB =
        FlatAutomatonMill.parser().parse("src/test/resources/mc/emf/diff/Testautomat.aut");
    
    Optional<ASTAutomaton> transC =
        FlatAutomatonMill.parser().parse("src/test/resources/mc/emf/diff/Testautomat2.aut");
    
    assertTrue(transB.isPresent());
    assertTrue(transC.isPresent());
    
    AST2ModelFiles.get().serializeASTInstance(transB.get(), "B1");
    AST2ModelFiles.get().serializeASTInstance(transC.get(), "C1");
    
    EObject deserAstTransB = AST2ModelFiles.get()
        .deserializeASTInstance("ASTAutomaton_B1", FlatAutomatonPackage.eINSTANCE);
    assertNotNull(deserAstTransB);
    assertInstanceOf(ASTAutomaton.class, deserAstTransB);
    assertTrue(transB.get().deepEquals(deserAstTransB));
    assertFalse(transC.get().deepEquals(deserAstTransB));
    assertEquals("Testautomat", ((ASTAutomaton) deserAstTransB).getName());
    
    List<DiffElement> diffs =
        AstEmfDiffUtility.getAllAstDiffs(transB.get(), (ASTAutomaton) deserAstTransB);
    assertTrue(diffs.isEmpty());
  }
  
  @Test
  public void testSerializeAndDeserializeCreatedInstance() throws IOException {
    ASTAutomaton aut = FlatAutomatonMill.automatonBuilder().uncheckedBuild();
    ASTTransition trans = FlatAutomatonMill.transitionBuilder().uncheckedBuild();
    ASTState state1 = FlatAutomatonMill.stateBuilder().uncheckedBuild();
    ASTState state2 = FlatAutomatonMill.stateBuilder().uncheckedBuild();
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
    EObject deserObject = AST2ModelFiles.get()
        .deserializeASTInstance("ASTAutomaton_Aut1", FlatAutomatonPackage.eINSTANCE);
    assertNotNull(deserObject);
    assertInstanceOf(ASTAutomaton.class, deserObject);
    ASTAutomaton serializedAut = (ASTAutomaton) deserObject;
    
    assertTrue(EcoreUtil.equals(aut, serializedAut));
    assertTrue(aut.deepEquals(serializedAut));
    assertTrue(serializedAut.getStateList().get(0).isInitial());
  }
  
}
