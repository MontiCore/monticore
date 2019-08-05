/* (c) https://github.com/MontiCore/monticore */

package de.monticore.types;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class ReturnTypesTest {
  
  @BeforeClass
  public static void disableFailQuick() {
    LogStub.init();
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
