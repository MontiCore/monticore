package mc.featureemf.epackage;

import static org.junit.Assert.*;
import mc.featureemf.fautomaton.action._ast.*;
import mc.featureemf.fautomaton.automatonwithaction._ast.*;
import mc.featureemf.fautomaton.automaton._ast.*;
import org.junit.Test;

public class IDTest {
  
  @Test
  public void testFeatureIDs() {
    assertEquals(5, ActionAutomatonPackage.TRANSITIONWITHACTION__ACTION);
    assertEquals(1, ActionAutomatonPackage.COUNTER_FEATURE_COUNT);
    
    // test feature ids for inheritance
    assertEquals(ExpressionPackage.ASSIGNMENT__VARNAME, ExpressionPackage.COMPLEXASSIGMENT__VARNAME);
    assertEquals(ExpressionPackage.COMPLEXASSIGMENT__RHS, ExpressionPackage.ASSIGNMENT__RHS);
  }
  
  @Test
  public void testClassIDs() {
    // test EDatatype
    assertEquals(ExpressionPackage.Literals.EVECTOR.getClassifierID(), ExpressionPackage.EVECTOR);
    // test Classes
    assertEquals(ExpressionPackage.Literals.DECREASEEXPRESSION.getClassifierID(), ExpressionPackage.DECREASEEXPRESSION);
    assertEquals(FlatAutomatonPackage.Literals.TRANSITION, FlatAutomatonPackageImpl.eINSTANCE.getTransition());
    
  }
  
  @Test
  public void testFeatureIDMetaObjectRelation() {
    assertEquals(FlatAutomatonPackage.Literals.AUTOMATON__NAME, FlatAutomatonPackage.Literals.AUTOMATON.getEAllStructuralFeatures().get(FlatAutomatonPackage.AUTOMATON__NAME));
    
    assertEquals(FlatAutomatonPackage.Literals.AUTOMATON__STATE,
        ActionAutomatonPackage.Literals.AUTOMATON.getEAllStructuralFeatures().get(ActionAutomatonPackage.AUTOMATON__STATE));
    assertEquals(ActionAutomatonPackage.Literals.TRANSITIONWITHACTION__ACTION,
        ActionAutomatonPackage.Literals.TRANSITIONWITHACTION.getEAllStructuralFeatures().get(ActionAutomatonPackage.TRANSITIONWITHACTION__ACTION));
  }
}
