package mc.emf.epackage;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import mc.feature.fautomaton.action.expression._ast.ExpressionPackage;
import mc.feature.fautomaton.automaton.flatautomaton._ast.FlatAutomatonPackage;
import mc.feature.fautomaton.automaton.flatautomaton._ast.FlatAutomatonPackageImpl;
import mc.feature.fautomaton.automatonwithaction.actionautomaton._ast.ActionAutomatonPackage;

public class IDTest {
  
  @Test
  public void testFeatureIDs() {
    assertEquals(3, FlatAutomatonPackage.ASTTransition);
    assertEquals(2, FlatAutomatonPackage.ASTTransition_To);
  }
  
  public void testInheritanceFeatureIDs() {
    // test feature ids for inheritance
    assertEquals(ExpressionPackage.ASTAssignment_Varname,
        ExpressionPackage.ASTComplexAssigment_Varname);
    assertEquals(ExpressionPackage.ASTAssignment_RHS, ExpressionPackage.ASTComplexAssigment_RHS);
  }
  
  @Test
  public void testClassIDs() {
    // test EDatatype
    assertEquals(ExpressionPackage.eINSTANCE.getEVector().getClassifierID(),
        ExpressionPackage.EVector);
    // test Classes
    assertEquals(ExpressionPackage.eINSTANCE.getDecreaseExpression().getClassifierID(),
        ExpressionPackage.ASTDecreaseExpression);
    assertEquals(FlatAutomatonPackage.eINSTANCE.getTransition(),
        FlatAutomatonPackageImpl.eINSTANCE.getTransition());
        
  }
  
  @Test
  public void testFeatureIDMetaObjectRelation() {
    assertEquals(FlatAutomatonPackage.eINSTANCE.getASTAutomaton_Name(), FlatAutomatonPackage.eINSTANCE
        .getAutomaton().getEAllStructuralFeatures().get(FlatAutomatonPackage.ASTAutomaton_Name));
        
    assertEquals(FlatAutomatonPackage.eINSTANCE.getASTAutomaton_States(),
        FlatAutomatonPackage.eINSTANCE.getAutomaton().getEAllStructuralFeatures()
            .get(FlatAutomatonPackage.ASTAutomaton_States));
  }
}
