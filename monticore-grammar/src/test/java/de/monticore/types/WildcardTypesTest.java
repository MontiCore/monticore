/* (c) https://github.com/MontiCore/monticore */

package de.monticore.types;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class WildcardTypesTest {
  
  @BeforeClass
  public static void disableFailQuick() {
    LogStub.init();
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
