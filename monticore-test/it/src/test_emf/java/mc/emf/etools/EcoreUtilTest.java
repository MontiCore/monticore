/* (c) https://github.com/MontiCore/monticore */
package mc.emf.etools;

import de.monticore.emf.util.AST2ModelFiles;
import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.feature.fautomaton.automaton.flatautomaton.FlatAutomatonMill;
import mc.feature.fautomaton.automaton.flatautomaton._ast.ASTAutomaton;
import mc.feature.fautomaton.automaton.flatautomaton._ast.ASTState;
import mc.feature.fautomaton.automaton.flatautomaton._ast.ASTTransition;
import mc.feature.fautomaton.automaton.flatautomaton._ast.FlatAutomatonPackage;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
@TestWithMCLanguage(FlatAutomatonMill.class)
public class EcoreUtilTest {
  
  @Test
  public void testSerializeAndDeserializeParseInstance() throws IOException {
    Optional<ASTAutomaton> transC =
        FlatAutomatonMill.parser().parse("src/test/resources/mc/emf/Testautomat.aut");
    
    Optional<ASTAutomaton> transA =
        FlatAutomatonMill.parser().parse("src/test/resources/mc/emf/diff/Testautomat2.aut");
    
    assertTrue(transC.isPresent());
    assertTrue(transA.isPresent());
    
    AST2ModelFiles.get().serializeASTInstance(transC.get(), "C");
    AST2ModelFiles.get().serializeASTInstance(transA.get(), "A");
    
    EObject deserAstTransC = AST2ModelFiles.get()
        .deserializeASTInstance("ASTAutomaton_C", FlatAutomatonPackage.eINSTANCE);
    assertNotNull(deserAstTransC);
    assertInstanceOf(ASTAutomaton.class, deserAstTransC);
    
    EObject deserAstTransA = AST2ModelFiles.get()
        .deserializeASTInstance("ASTAutomaton_A", FlatAutomatonPackage.eINSTANCE);
    assertNotNull(deserAstTransA);
    assertInstanceOf(ASTAutomaton.class, deserAstTransA);
    assertNotEquals(deserAstTransA.toString(), deserAstTransC.toString());
    
    assertFalse(EcoreUtil.equals(deserAstTransA, deserAstTransC));
  }
  
  @Test
  public void testSerializeAndDeserializeParseInstance2() throws IOException {
    Optional<ASTAutomaton> transB =
        FlatAutomatonMill.parser().parse("src/test/resources/mc/emf/diff/Testautomat.aut");
    
    Optional<ASTAutomaton> transC =
        FlatAutomatonMill.parser().parse("src/test/resources/mc/emf/Testautomat.aut");
    
    assertTrue(transB.isPresent());
    assertTrue(transC.isPresent());
    
    assertTrue(EcoreUtil.equals(transB.get(), transC.get()));
    
    AST2ModelFiles.get().serializeASTInstance(transB.get(), "B2");
    AST2ModelFiles.get().serializeASTInstance(transC.get(), "C2");
    
    EObject deserAstTransC = AST2ModelFiles.get()
        .deserializeASTInstance("ASTAutomaton_C2", FlatAutomatonPackage.eINSTANCE);
    assertNotNull(deserAstTransC);
    assertInstanceOf(ASTAutomaton.class, deserAstTransC);
    
    EObject deserAstTransB = AST2ModelFiles.get()
        .deserializeASTInstance("ASTAutomaton_B2", FlatAutomatonPackage.eINSTANCE);
    assertNotNull(deserAstTransB);
    assertInstanceOf(ASTAutomaton.class, deserAstTransB);
    
    assertEquals(deserAstTransB.toString(), deserAstTransC.toString());
    assertTrue(EcoreUtil.equals(deserAstTransB, deserAstTransC));
    
  }
  
  @Test
  public void testSerializeAndDeserializeCreatedInstance() {
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
    
    ASTAutomaton aut2 = FlatAutomatonMill.automatonBuilder().uncheckedBuild();
    ASTTransition trans2 = FlatAutomatonMill.transitionBuilder().uncheckedBuild();
    ASTState state1a = FlatAutomatonMill.stateBuilder().uncheckedBuild();
    ASTState state2a = FlatAutomatonMill.stateBuilder().uncheckedBuild();
    aut2.setName("aut1");
    aut2.getTransitionList().add(trans2);
    aut2.getStateList().add(state1a);
    aut2.getStateList().add(state2a);
    state1a.setName("state1");
    state2a.setName("state2");
    
    trans2.setFrom(state1a.getName());
    trans2.setTo(state2a.getName());
    trans2.setActivate("trans1");
    
    state1a.setInitial(true);
    state2a.setFinal(true);
    
    assertTrue(EcoreUtil.equals(aut, aut2));
    
    state2a.setFinal(false);
    assertFalse(EcoreUtil.equals(aut, aut2));
    
    state2.setFinal(false);
    assertTrue(EcoreUtil.equals(aut, aut2));
  }
  
}
