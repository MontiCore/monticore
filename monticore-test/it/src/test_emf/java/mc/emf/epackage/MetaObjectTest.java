/* (c) https://github.com/MontiCore/monticore */

package mc.emf.epackage;

import mc.GeneratorIntegrationsTest;
import mc.feature.fautomaton.action.expression._ast.ASTAssignment;
import mc.feature.fautomaton.action.expression._ast.ExpressionPackage;
import mc.feature.fautomaton.automaton.flatautomaton._ast.ASTAutomaton;
import mc.feature.fautomaton.automaton.flatautomaton._ast.FlatAutomatonPackage;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.*;
import org.junit.Test;
import org.junit.Ignore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MetaObjectTest extends GeneratorIntegrationsTest {

  public void setup() {

  }
  @Test
  @Ignore
  public void testSuperTypes() {
    EClass compAssig = ExpressionPackage.eINSTANCE.getASTComplexAssigment();
    
    EList<EClass> supertypes = compAssig.getEAllSuperTypes();
    
    assertTrue(supertypes.contains(ExpressionPackage.eINSTANCE.getASTAssignment()));
    assertTrue(supertypes.contains(ExpressionPackage.eINSTANCE.getASTValue()));
    assertTrue(supertypes.contains(ExpressionPackage.eINSTANCE.getASTExpression()));
  }

  public void testEClass() {
    EClass exp = ExpressionPackage.eINSTANCE.getASTExpression();
    EClass incExp = (EClass) ExpressionPackage.eINSTANCE
        .getEClassifier("ASTIncreaseExpression");
    EClass assig = (EClass) ExpressionPackage.eINSTANCE.getEClassifiers()
        .get(ExpressionPackage.ASTAssignment);

    assertEquals(ExpressionPackage.eINSTANCE.getASTExpression(), exp);
    assertEquals(ExpressionPackage.eINSTANCE.getASTIncreaseExpression(), incExp);
    assertEquals(ExpressionPackage.eINSTANCE.getASTAssignment(), assig);

    assertTrue(exp.isInterface());
    assertFalse(incExp.isInterface());

    assertEquals(ASTAssignment.class, assig.getInstanceClass());
    assertEquals(3, assig.getFeatureCount());

}
  
  @Test
  public void testEDataType() {
    EDataType eVector = ExpressionPackage.eINSTANCE.getVector();
    
    assertEquals(java.util.Vector.class, eVector.getInstanceClass());
  }
  
  @Test
  public void testEAttribute() {
    EAttribute varName = ExpressionPackage.eINSTANCE.getASTExpression_Varname();
    
    assertEquals(varName.getFeatureID(),
        ExpressionPackage.ASTExpression_Varname);
    assertEquals(EcorePackage.Literals.ESTRING, varName.getEType());
    assertEquals("Varname", varName.getName());
  }
  
  @Test
  public void testEReference() {
    EReference state = FlatAutomatonPackage.eINSTANCE.getASTAutomaton_States();
    
    // check feature ids
    assertEquals(state.getFeatureID(),
        FlatAutomatonPackage.ASTAutomaton_States);
        
    assertEquals(FlatAutomatonPackage.eINSTANCE.getASTState(), state
        .getEReferenceType());
    assertTrue(state.isMany());
    assertTrue(state.isContainment());
    
    assertEquals(ASTAutomaton.class, state.getContainerClass());
    assertEquals(-1, state.getUpperBound());
    
  }
  
}
