package mc.featureemf.emethods;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import mc.featureemf.fautomaton.action._ast.*;

public class FeatureIDConversionTest {
  
  ASTComplexAssigment ass;
  
  @Before
  public void setUp() throws Exception {
    ass = ExpressionFactory.eINSTANCE.createComplexAssigment();
  }
  
  @Test
  public void testDerivedFeatureID() {
    int derivedID = ass.eDerivedStructuralFeatureID(
        ExpressionPackage.VALUE__A, ASTValue.class);
    
    int expectedDerivedID = ExpressionPackage.COMPLEXASSIGMENT__A;
    
    assertEquals(expectedDerivedID, derivedID);
  }
  
  @Test
  public void testBaseFeatureID() {
    int baseID = ass.eBaseStructuralFeatureID(
        ExpressionPackage.COMPLEXASSIGMENT__A, ASTValue.class);
    
    int expectedBaseID = ExpressionPackage.VALUE__A;
    
    assertEquals(expectedBaseID, baseID);
  }
}
