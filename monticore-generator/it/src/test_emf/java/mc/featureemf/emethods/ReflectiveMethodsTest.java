package mc.featureemf.emethods;

import static org.junit.Assert.*;
import mc.featureemf.fautomaton.automaton._ast.*;

import org.junit.Before;
import org.junit.Test;

public class ReflectiveMethodsTest {
  
  ASTAutomaton aut;
  ASTState state1;
  ASTState state2;
  ASTTransition transition;
  
  @Before
  public void setUp() throws Exception {
    aut = FlatAutomatonFactory.eINSTANCE.createAutomaton();
    aut.setName("aut1");
    state1 = FlatAutomatonFactory.eINSTANCE.createState();
    state2 = FlatAutomatonFactory.eINSTANCE.createState();
    
    transition = FlatAutomatonFactory.eINSTANCE.createTransition();
    
    aut.getState().add(state1);
    aut.getState().add(state2);
    aut.getTransition().add(transition);
    
    state1.setName("state1");
    state2.setName("state2");
    
  }
  
  @Test
  public void testEGet() {
    
    // Get name of automaton with reflective methods
    String nameFromID = (String) aut.eGet(
        FlatAutomatonPackage.AUTOMATON__NAME, false, false);
    String nameFromMetaObject = (String) aut
        .eGet(FlatAutomatonPackage.Literals.AUTOMATON__NAME);
    
    ASTStateList stateFromID = (ASTStateList) aut.eGet(
        FlatAutomatonPackage.AUTOMATON__STATE, false, false);
    ASTStateList stateFromMetaObject = (ASTStateList) aut
        .eGet(FlatAutomatonPackage.Literals.AUTOMATON__STATE);
    
    String expectedName = "aut1";
    ASTStateList expectedState = aut.getState();
    
    assertEquals(expectedName, nameFromID);
    assertEquals(expectedName, nameFromMetaObject);
    assertEquals(expectedState, stateFromID);
    assertEquals(expectedState, stateFromMetaObject);
    assertNull(transition
        .eGet(FlatAutomatonPackage.Literals.TRANSITION__FROMSTATE));
  }
  
  @Test
  public void testESet() {
    transition.eSet(FlatAutomatonPackage.TRANSITION__FROM, "from");
    transition.eSet(FlatAutomatonPackage.TRANSITION__FROMSTATE, state1);
    transition.eSet(FlatAutomatonPackage.Literals.TRANSITION__TOSTATE,
        state2);
    
    String expectedFrom = "from";
    ASTState expectedFromState = state1;
    ASTState expectedToState = state2;
    
    assertEquals(expectedFrom, transition.getFrom());
    assertEquals(expectedFromState, transition.getFromState());
    assertEquals(expectedToState, transition.getToState());
    assertTrue(state1.containsOutgoingTransitions(transition));
    
  }
  
  @Test
  public void testEUnSet() {
    aut.eUnset(FlatAutomatonPackage.AUTOMATON__STATE);
    aut.eUnset(FlatAutomatonPackage.Literals.AUTOMATON__NAME);
    
    assertNull(aut.getName());
    assertTrue(aut.getState().isEmpty());
  }
  
  @Test
  public void testEIsSet() {
    assertFalse(transition.eIsSet(FlatAutomatonPackage.TRANSITION__TO));
    assertTrue(aut.eIsSet(FlatAutomatonPackage.Literals.AUTOMATON__NAME));
    assertFalse(state2
        .eIsSet(FlatAutomatonPackage.STATE__OUTGOINGTRANSITIONS));
    assertTrue(aut.eIsSet(FlatAutomatonPackage.AUTOMATON__TRANSITION));
    
  }
  
}
