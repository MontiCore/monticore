/* (c) https://github.com/MontiCore/monticore */

package de.monticore.types;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.HashMap;

import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.types.types._ast.ASTArrayType;
import de.monticore.types.types._ast.ASTPrimitiveType;
import de.monticore.types.types._ast.ASTType;
import de.se_rwth.commons.logging.Log;

/**
 * @author Martin Schindler
 */
public class PrimitiveArrayTypesTest {
  
  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testPrimitiveArrayTypes() {
    try {
      // test-data
      HashMap<String, Integer> testdata = new HashMap<String, Integer>();
      testdata.put("boolean[][]", 2);
      testdata.put("byte[] []", 2);
      testdata.put("char [] []", 2);
      testdata.put("short[]", 1);
      testdata.put("int[][][]", 3);
      testdata.put("long[][][][][][][ ] [][][][][]", 12);
      
      // checks
      for (String teststring : testdata.keySet()) {
        ASTType type = TypesTestHelper.getInstance().parseType(teststring);
        assertNotNull(type);
        // check typing and dimension:
        assertTrue(type instanceof ASTArrayType);
        ASTArrayType arrayType = (ASTArrayType) type;
        assertEquals(testdata.get(teststring).intValue(), arrayType.getDimensions());
        assertTrue(arrayType.getComponentType() instanceof ASTPrimitiveType);
      }
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
  }
  
  @Test
  public void testNegativePrimitiveArrayTypes() {
    try {
      assertNull(TypesTestHelper.getInstance().parseType("long["));
      
      // Negative test for a array type with a missing ']'
      assertNull(TypesTestHelper.getInstance().parseType("long[][][][[]"));
      
      // Negative test for a array type with a missing ']' in the end
      assertNull(TypesTestHelper.getInstance().parseType("long[][][][]["));
      
      // Negative test for a array type with a missing '['
      assertNull(TypesTestHelper.getInstance().parseType("long[] [] ] []"));
      
      // Negative test for a array type with a missing ']' in the beginning
      assertNull(TypesTestHelper.getInstance().parseType("long][][]"));
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
  }
}
