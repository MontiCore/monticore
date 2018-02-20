/* (c) https://github.com/MontiCore/monticore */

package de.monticore.types;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import de.se_rwth.commons.logging.Log;

/**
 * @author Martin Schindler
 */
public class WildcardTypesTest {
  
  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testWildcardType() {
    try {
      // Test for a simple wildcard type
      assertTrue(TypesTestHelper.getInstance().testType("ParameterizedType<?>"));
      // Test with a upper bound
      assertTrue(TypesTestHelper.getInstance().testType("ParameterizedType<? extends ParentClass>"));
      // Test with a lower bound
      assertTrue(TypesTestHelper.getInstance().testType("ParameterizedType<? super ChildClass>"));
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
  }
  
  @Test
  public void testNegativeWildcardType() {
    try {
      // Negative test with a upper and lower bound
      assertNull(TypesTestHelper.getInstance()
          .parseType("ParameterizedType<? extends ParentClass super ChildClass>"));
          
      // Negative test with a wildcard as upper bound
      assertNull(
          TypesTestHelper.getInstance().parseType("ParameterizedType<? extends ? extends Invalid>"));         
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
  }
  
}
