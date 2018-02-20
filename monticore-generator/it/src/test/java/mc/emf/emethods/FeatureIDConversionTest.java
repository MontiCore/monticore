/* (c) https://github.com/MontiCore/monticore */

package mc.emf.emethods;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import mc.GeneratorIntegrationsTest;
import mc.feature.fautomaton.action.expression._ast.ASTComplexAssigment;
import mc.feature.fautomaton.action.expression._ast.ASTValue;
import mc.feature.fautomaton.action.expression._ast.ExpressionNodeFactory;
import mc.feature.fautomaton.action.expression._ast.ExpressionPackage;


public class FeatureIDConversionTest extends GeneratorIntegrationsTest {
  
  ASTComplexAssigment ast;
  
  @Before
  public void setUp() throws Exception {
    ast = ExpressionNodeFactory.createASTComplexAssigment();
  }
  
  // TODO GV
  public void testDerivedFeatureID() {
    int derivedID = ast.eDerivedStructuralFeatureID(ExpressionPackage.ASTValue, ASTValue.class);
    
    int expectedDerivedID = ExpressionPackage.ASTComplexAssigment_A;
    
    assertEquals(expectedDerivedID, derivedID);
  }
  
  public void testBaseFeatureID() {
    int baseID = ast.eBaseStructuralFeatureID(
        ExpressionPackage.ASTComplexAssigment_A, ASTValue.class);
    
    int expectedBaseID = ExpressionPackage.ASTValue;
    
    assertEquals(expectedBaseID, baseID);
  }
}
