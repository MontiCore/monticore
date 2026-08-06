/* (c) https://github.com/MontiCore/monticore */

package mc.emf.epackage;

import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.feature.fautomaton.action.expression._ast.ExpressionPackage;
import mc.feature.fautomaton.automaton.flatautomaton.FlatAutomatonMill;
import mc.feature.fautomaton.automaton.flatautomaton._ast.FlatAutomatonPackage;
import mc.feature.fautomaton.automaton.flatautomaton._ast.FlatAutomatonPackageImpl;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestWithMCLanguage(FlatAutomatonMill.class)
public class IDTest {
  
  @Test
  public void testFeatureIDs() {
    assertEquals(3, FlatAutomatonPackage.ASTTransition);
    assertEquals(2, FlatAutomatonPackage.ASTTransition_To);
  }
  
  @Test
  @Disabled
  public void testInheritanceFeatureIDs() {
    // test feature ids for inheritance
    assertEquals(ExpressionPackage.ASTAssignment_Varname,
        ExpressionPackage.ASTComplexAssigment_Varname);
    assertEquals(ExpressionPackage.ASTAssignment_RHS, ExpressionPackage.ASTComplexAssigment_RHS);
  }
  
  @Test
  public void testClassIDs() {
    // test EDatatype
    assertEquals(ExpressionPackage.Vector,
        ExpressionPackage.eINSTANCE.getVector().getClassifierID());
    // test Classes
    assertEquals(ExpressionPackage.ASTDecreaseExpression,
        ExpressionPackage.eINSTANCE.getASTDecreaseExpression().getClassifierID());
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
