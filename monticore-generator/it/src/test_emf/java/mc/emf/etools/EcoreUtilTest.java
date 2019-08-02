/* (c)  https://github.com/MontiCore/monticore */
package mc.emf.etools;

import java.io.IOException;
import java.util.Optional;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.antlr.v4.runtime.RecognitionException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.monticore.emf.util.AST2ModelFiles;
import mc.GeneratorIntegrationsTest;
import mc.feature.fautomaton.automaton.flatautomaton._ast.ASTAutomaton;
import mc.feature.fautomaton.automaton.flatautomaton._ast.ASTState;
import mc.feature.fautomaton.automaton.flatautomaton._ast.ASTTransition;
import mc.feature.fautomaton.automaton.flatautomaton._ast.FlatAutomatonNodeFactory;
import mc.feature.fautomaton.automaton.flatautomaton._ast.FlatAutomatonPackage;
import mc.feature.fautomaton.automaton.flatautomaton._parser.FlatAutomatonParser;

import static org.junit.Assert.*;

public class EcoreUtilTest extends GeneratorIntegrationsTest {

  @Test
  public void testSerializeAndDeserializeParseInstance() {
    try {
      
      Optional<ASTAutomaton> transC = new FlatAutomatonParser()
          .parse("src/test/resources/mc/emf/Testautomat.aut");
      
      Optional<ASTAutomaton> transA = new FlatAutomatonParser()
          .parse("src/test/resources/mc/emf/diff/Testautomat2.aut");
      
      if (transC.isPresent() && transA.isPresent()) {
        
        
        AST2ModelFiles.get().serializeASTInstance(transC.get(),
            "C");
        AST2ModelFiles.get().serializeASTInstance(transA.get(),
            "A");
  
        EObject deserAstTransC = AST2ModelFiles.get().deserializeASTInstance("ASTAutomaton_C",
            FlatAutomatonPackage.eINSTANCE);
        assertNotNull(deserAstTransC);
        assertTrue(deserAstTransC instanceof ASTAutomaton);
        
        
        
        EObject deserAstTransA = AST2ModelFiles.get().deserializeASTInstance("ASTAutomaton_A",
            FlatAutomatonPackage.eINSTANCE);
        assertNotNull(deserAstTransA);
        assertTrue(deserAstTransA instanceof ASTAutomaton);
        assertNotEquals(deserAstTransA.toString(),deserAstTransC.toString());
        
        assertFalse(EcoreUtil.equals(deserAstTransA, deserAstTransC));
        
      }
      else {
        fail("Parse errors");
      }
      
    }
    catch (RecognitionException | IOException e) {
      fail("Should not reach this, but: " + e);
    }
  }
  
  @Test
  public void testSerializeAndDeserializeParseInstance2() {
    try {
      Optional<ASTAutomaton> transB = new FlatAutomatonParser()
          .parse("src/test/resources/mc/emf/diff/Testautomat.aut");
      
      Optional<ASTAutomaton> transC = new FlatAutomatonParser()
          .parse("src/test/resources/mc/emf/Testautomat.aut");
      
      
      if (transB.isPresent() && transC.isPresent()) {
        
        assertTrue(EcoreUtil.equals(transB.get(), transC.get()));
        
        
        AST2ModelFiles.get().serializeASTInstance(transB.get(),
            "B2");
        AST2ModelFiles.get().serializeASTInstance(transC.get(),
            "C2");
        
        EObject deserAstTransC = AST2ModelFiles.get().deserializeASTInstance("ASTAutomaton_C2",
            FlatAutomatonPackage.eINSTANCE);
        assertNotNull(deserAstTransC);
        assertTrue(deserAstTransC instanceof ASTAutomaton);
        
        EObject deserAstTransB = AST2ModelFiles.get().deserializeASTInstance("ASTAutomaton_B2",
            FlatAutomatonPackage.eINSTANCE);
        assertNotNull(deserAstTransB);
        assertTrue(deserAstTransB instanceof ASTAutomaton);
        
        
        assertEquals(deserAstTransB.toString(),deserAstTransC.toString());
        assertTrue(EcoreUtil.equals(deserAstTransB, deserAstTransC));
        
      }
      else {
        fail("Parse errors");
      }
      
    }
    catch (RecognitionException | IOException e) {
      fail("Should not reach this, but: " + e);
    }
  }
  
  @Test
  public void testSerializeAndDeserializeCreatedInstance() {
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
    
    ASTAutomaton aut2 = FlatAutomatonNodeFactory.createASTAutomaton();
    ASTTransition trans2 = FlatAutomatonNodeFactory.createASTTransition();
    ASTState state1a = FlatAutomatonNodeFactory.createASTState();
    ASTState state2a = FlatAutomatonNodeFactory.createASTState();
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
