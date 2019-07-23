/* (c) https://github.com/MontiCore/monticore */

package mc.emf.epackage;

import mc.GeneratorIntegrationsTest;
import mc.feature.fautomaton.action.expression._ast.ExpressionPackage;
import mc.feature.fautomaton.automaton.flatautomaton._ast.FlatAutomatonPackage;
import mc.feature.fautomaton.automaton.flatautomaton._ast.FlatAutomatonPackageImpl;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class IDTest extends GeneratorIntegrationsTest {
  
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
    assertEquals(ExpressionPackage.eINSTANCE.getVector().getClassifierID(),
        ExpressionPackage.Vector);
    // test Classes
    assertEquals(ExpressionPackage.eINSTANCE.getASTDecreaseExpression().getClassifierID(),
        ExpressionPackage.ASTDecreaseExpression);
    assertEquals(FlatAutomatonPackage.eINSTANCE.getASTTransition(),
        FlatAutomatonPackageImpl.eINSTANCE.getASTTransition());
        
  }
  
  @Test
  public void testFeatureIDMetaObjectRelation() {
    assertEquals(FlatAutomatonPackage.eINSTANCE.getASTAutomaton_Name(), FlatAutomatonPackage.eINSTANCE
        .getASTAutomaton().getEAllStructuralFeatures().get(FlatAutomatonPackage.ASTAutomaton_Name));
        
    assertEquals(FlatAutomatonPackage.eINSTANCE.getASTAutomaton_States(),
        FlatAutomatonPackage.eINSTANCE.getASTAutomaton().getEAllStructuralFeatures()
            .get(FlatAutomatonPackage.ASTAutomaton_States));
  }
}
