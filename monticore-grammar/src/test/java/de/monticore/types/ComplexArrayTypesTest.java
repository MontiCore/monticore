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
import de.monticore.types.types._ast.ASTComplexReferenceType;
import de.monticore.types.types._ast.ASTType;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.Slf4jLog;

/**
 * @author Martin Schindler
 */
public class ComplexArrayTypesTest {
  
  @BeforeClass
  public static void disableFailQuick() {
    Slf4jLog.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testComplexArrayTypes1() {
    // SimpleReferenceTypes:
    try {
      // test-data
      HashMap<String, Integer> testdata = new HashMap<String, Integer>();
      testdata.put("Collection<?>[]", 1);
      testdata.put("L<A>[]", 1);
      testdata.put("C<L<A>>[]", 1);
      testdata.put("Pair<String,String>[]", 1);
      testdata.put("A<B<C,D<E,F<G>>>>[]", 1);
      testdata.put("A<B<C,D<E,F<G<H>>>>,I<J>>[]", 1);
      
      // checks
      for (String teststring : testdata.keySet()) {
        ASTType type = TypesTestHelper.getInstance().parseType(teststring);
        assertNotNull(type);
        // check typing and dimension:
        assertTrue(type instanceof ASTArrayType);
        ASTArrayType arrayType = (ASTArrayType) type;
        assertEquals(testdata.get(teststring).intValue(), arrayType.getDimensions());
        assertTrue(arrayType.getComponentType() instanceof ASTComplexReferenceType);
      }
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
  }
  
  @Test
  public void testComplexArrayTypes2() {
    // QualifiedTypes:
    try {
      // test-data
      HashMap<String, Integer> testdata = new HashMap<String, Integer>();
      testdata.put("A.B<int[][]>.C<int[]>[][][][  ]", 4);
      
      // checks
      for (String teststring : testdata.keySet()) {
        ASTType type = TypesTestHelper.getInstance().parseType(teststring);
        assertNotNull(type);
        // check typing and dimension:
        assertTrue(type instanceof ASTArrayType);
        ASTArrayType arrayType = (ASTArrayType) type;
        assertEquals(testdata.get(teststring).intValue(), arrayType.getDimensions());
        assertTrue(arrayType.getComponentType() instanceof ASTComplexReferenceType);
      }
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
  }
  
  // copied from JavaDSL
  @Test
  public void testComplexArrayTypes3() {
    try {
      // Test for a simple array type with one dimension
      assertTrue(TypesTestHelper.getInstance().testType("Type[]"));
      
      // Test for a simple array type with mor than one dimension
      assertTrue(TypesTestHelper.getInstance().testType("Type[][][][][]"));
      
      // Test for a parameterized array type
      assertTrue(TypesTestHelper.getInstance().testType("Type<?>[]"));
      
      // Test for a parameterized type argument
      assertTrue(TypesTestHelper.getInstance().testType("Type<Arg1<Arg2>>[]"));
      
      // Test for a parameterized type argument, which is also an array
      assertTrue(TypesTestHelper.getInstance().testType("Type<Arg1<Arg2>[][]>[]"));
      
      // Test for a parameterized type argument. Only the innermost argument
      // is an array
      assertTrue(TypesTestHelper.getInstance().testType("Type<Arg1<Arg2[]>>"));
      
      // Test for a parameterized type argument. Just a step more
      assertTrue(TypesTestHelper.getInstance().testType("Type<Arg1<Arg2<Arg3>>>[]"));
      
      // Test for a parameterized type argument. Just a step more
      assertTrue(TypesTestHelper.getInstance().testType("Type<Arg1<Arg2<Arg3<Arg4>>>>[]"));
      
      // Same test as above, but with an array type as Arg2
      assertTrue(TypesTestHelper.getInstance().testType("Type<Arg1<Arg2<Arg3<Arg4>>[]>>[]"));
      
      // Same as above, but differnt place for the brackets
      assertTrue(TypesTestHelper.getInstance().testType("Type<Arg1<Arg2<Arg3<Arg4>>>[]>[]"));
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
  }
  
  @Test
  public void testNegativeComplexArrayTypes() {
    try {
      // Negative test for a qualified type, whose qualification is an array type
      assertNull(TypesTestHelper.getInstance().parseType("packageName.Type[].Invalid"));
      
      // Test for a parameterized array type with additional ">"
      assertNull(TypesTestHelper.getInstance().parseType("Type<?>[]>"));
      
      // Test for a parameterized type argument with missing ">"
      assertNull(TypesTestHelper.getInstance().parseType("Type<Arg1<Arg2>[]"));
      
      // Test for a parameterized type argument with missing "]"
      assertNull(TypesTestHelper.getInstance().parseType("Type<Arg1<Arg2>[[]>[]"));
      
      // Test for a parameterized type argument with missing ">"
      assertNull(TypesTestHelper.getInstance().parseType("Type<Arg1<Arg2<Arg3<Arg4>>>[]"));
      
      // Same test as above, but with an array type as Arg2 and with additional
      // ">"
      assertNull(TypesTestHelper.getInstance().parseType("Type<Arg1<Arg2<Arg3<Arg4>>[]>>>[]"));
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
  }
  
}
