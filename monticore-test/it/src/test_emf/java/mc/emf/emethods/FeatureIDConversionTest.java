/* (c) https://github.com/MontiCore/monticore */

package mc.emf.emethods;

import mc.GeneratorIntegrationsTest;
import mc.feature.fautomaton.action.expression._ast.ASTComplexAssigment;
import mc.feature.fautomaton.action.expression._ast.ASTValue;
import mc.feature.fautomaton.action.expression.ExpressionMill;
import mc.feature.fautomaton.action.expression._ast.ExpressionPackage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Disabled
public class FeatureIDConversionTest extends GeneratorIntegrationsTest {
  
  ASTComplexAssigment ast;
  
  @BeforeEach
  public void setUp() throws Exception {
    ast = ExpressionMill.complexAssigmentBuilder().uncheckedBuild();
  }
  public void testDerivedFeatureID() {
    int derivedID = ast.eDerivedStructuralFeatureID(ExpressionPackage.ASTValue, ASTValue.class);
    
    int expectedDerivedID = ExpressionPackage.ASTComplexAssigment_A;
    
    assertEquals(expectedDerivedID, derivedID);
  }
  
  @Test
  public void testBaseFeatureID() {
    int baseID = ast.eBaseStructuralFeatureID(
        ExpressionPackage.ASTComplexAssigment_A, ASTValue.class);
    
    int expectedBaseID = ExpressionPackage.ASTValue;
    
    assertEquals(expectedBaseID, baseID);
  }
}
