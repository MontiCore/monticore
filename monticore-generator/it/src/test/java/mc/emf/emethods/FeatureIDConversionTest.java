package mc.emf.emethods;

import org.junit.Before;

import mc.feature.fautomaton.action.expression._ast.ASTComplexAssigment;
import mc.feature.fautomaton.action.expression._ast.ExpressionNodeFactory;


public class FeatureIDConversionTest {
  
  ASTComplexAssigment ast;
  
  @Before
  public void setUp() throws Exception {
    ast = ExpressionNodeFactory.createASTComplexAssigment();
  }
  
  // TODO GV
//  @Test
//  public void testDerivedFeatureID() {
//    int derivedID = ast.eDerivedStructuralFeatureID(ExpressionPackage.VALUE__A, ASTValue.class);
//    
//    int expectedDerivedID = ExpressionPackage.COMPLEXASSIGMENT__A;
//    
//    assertEquals(expectedDerivedID, derivedID);
//  }
//  
//  @Test
//  public void testBaseFeatureID() {
//    int baseID = ast.eBaseStructuralFeatureID(
//        ExpressionPackage.COMPLEXASSIGMENT__A, ASTValue.class);
//    
//    int expectedBaseID = ExpressionPackage.VALUE__A;
//    
//    assertEquals(expectedBaseID, baseID);
//  }
}
