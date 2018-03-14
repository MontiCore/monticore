/* (c) https://github.com/MontiCore/monticore */

package de.monticore.types;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import de.se_rwth.commons.logging.Log;

/**
 * @author Martin Schindler
 */
public class ReturnTypesTest {
  
  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testReturnTypes() {
    try {
      String[] returntypes = new String[] { "void", "byte", "char[]", "MyClass", "a.b.MyClass",
          "MyClass<T>" };
      for (String returntype : returntypes) {
        TypesTestHelper.getInstance().parseReturnType(returntype);
      }
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
  }
  
  @Test
  public void testNegativeVoidType() {
    // "void" is no type
    try {
      assertNull(TypesTestHelper.getInstance().parseType("void"));
    }
    catch (IOException e) {
      fail(e.getMessage());
    }   
  }
}
