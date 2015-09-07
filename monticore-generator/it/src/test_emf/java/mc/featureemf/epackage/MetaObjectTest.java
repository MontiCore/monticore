package mc.featureemf.epackage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import mc.featureemf.fautomaton.action._ast.Assignment;
import mc.featureemf.fautomaton.action._ast.ExpressionPackage;
import mc.featureemf.fautomaton.automaton._ast.ASTAutomaton;
import mc.featureemf.fautomaton.automaton._ast.FlatAutomatonPackage;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcorePackage;
import org.junit.Test;

public class MetaObjectTest {
  
  @Test
  public void testSuperTypes() {
    EClass compAssig = ExpressionPackage.eINSTANCE.getComplexAssigment();
    
    EList<EClass> supertypes = compAssig.getEAllSuperTypes();
    
    assertTrue(supertypes.contains(ExpressionPackage.Literals.ASSIGNMENT));
    assertTrue(supertypes.contains(ExpressionPackage.Literals.VALUE));
    assertTrue(supertypes.contains(ExpressionPackage.Literals.EXPRESSION));
  }
  
  @Test
  public void testEClass() {
    EClass exp = ExpressionPackage.eINSTANCE.getExpression();
    EClass incExp = (EClass) ExpressionPackage.eINSTANCE
        .getEClassifier("IncreaseExpression");
    EClass assig = (EClass) ExpressionPackage.eINSTANCE.getEClassifiers()
        .get(ExpressionPackage.ASSIGNMENT);
    
    assertEquals(ExpressionPackage.Literals.EXPRESSION, exp);
    assertEquals(ExpressionPackage.Literals.INCREASEEXPRESSION, incExp);
    assertEquals(ExpressionPackage.Literals.ASSIGNMENT, assig);
    
    assertTrue(exp.isInterface());
    assertFalse(incExp.isInterface());
    
    assertEquals(Assignment.class, assig.getInstanceClass());
    assertEquals(ExpressionPackage.ASSIGNMENT_FEATURE_COUNT, assig
        .getFeatureCount());
    
  }
  
  @Test
  public void testEDataType() {
    EDataType eVector = ExpressionPackage.eINSTANCE.getEVector();
    
    assertEquals(java.util.Vector.class, eVector.getInstanceClass());
  }
  
  @Test
  public void testEAttribute() {
    EAttribute varName = ExpressionPackage.eINSTANCE
        .getExpression_Varname();
    
    assertEquals(varName.getFeatureID(),
        ExpressionPackage.ASSIGNMENT__VARNAME);
    assertEquals(EcorePackage.Literals.ESTRING, varName.getEType());
    assertEquals("Varname", varName.getName());
  }
  
  @Test
  public void testEReference() {
    // compositions
    EReference stateImp = FlatAutomatonPackage.Literals.AUTOMATON__IMPSTATE;
    EReference state = FlatAutomatonPackage.Literals.AUTOMATON__STATE;
    
    // association
    EReference fromState = FlatAutomatonPackage.Literals.TRANSITION__FROMSTATE;
    EReference outgoing = FlatAutomatonPackage.Literals.STATE__OUTGOINGTRANSITIONS;
    
    // check feature ids
    assertEquals(state.getFeatureID(),
        FlatAutomatonPackage.AUTOMATON__STATE);
    assertEquals(fromState.getFeatureID(),
        FlatAutomatonPackage.TRANSITION__FROMSTATE);
    
    assertEquals(FlatAutomatonPackage.Literals.STATE, stateImp
        .getEReferenceType());
    assertFalse(stateImp.isMany());
    assertTrue(stateImp.isContainment());
    
    assertTrue(state.isMany());
    assertEquals(ASTAutomaton.class, state.getContainerClass());
    assertEquals(-1, state.getUpperBound());
    
    assertFalse(fromState.isContainment());
    assertEquals("FromState", fromState.getName());
    assertEquals(1, fromState.getUpperBound());
    assertEquals(outgoing, fromState.getEOpposite());
    
    assertEquals(FlatAutomatonPackage.Literals.TRANSITION, outgoing
        .getEType());
    assertEquals(-1, outgoing.getUpperBound());
    assertEquals(fromState, outgoing.getEOpposite());
    assertTrue(outgoing.isUnique());
    
  }
  
}
