/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2015, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package de.monticore.types;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
        // check typing and dimension:
        assertTrue(type instanceof ASTArrayType);
        ASTArrayType arrayType = (ASTArrayType) type;
        assertEquals(testdata.get(teststring).intValue(), arrayType.getDimensions());
        assertTrue(arrayType.getComponentType() instanceof ASTComplexReferenceType);
      }
    }
    catch (Exception e) {
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
        // check typing and dimension:
        assertTrue(type instanceof ASTArrayType);
        ASTArrayType arrayType = (ASTArrayType) type;
        assertEquals(testdata.get(teststring).intValue(), arrayType.getDimensions());
        assertTrue(arrayType.getComponentType() instanceof ASTComplexReferenceType);
      }
    }
    catch (Exception e) {
      fail(e.getMessage());
    }
  }
  
  // copied from JavaDSL
  @Test
  public void testComplexArrayTypes3() {
    try {
      // Test for a simple array type with one dimension
      TypesTestHelper.getInstance().testType("Type[]");
      
      // Test for a simple array type with mor than one dimension
      TypesTestHelper.getInstance().testType("Type[][][][][]");
      
      // Test for a parameterized array type
      TypesTestHelper.getInstance().testType("Type<?>[]");
      
      // Test for a parameterized type argument
      TypesTestHelper.getInstance().testType("Type<Arg1<Arg2>>[]");
      
      // Test for a parameterized type argument, which is also an array
      TypesTestHelper.getInstance().testType("Type<Arg1<Arg2>[][]>[]");
      
      // Test for a parameterized type argument. Only the innermost argument
      // is an array
      TypesTestHelper.getInstance().testType("Type<Arg1<Arg2[]>>");
      
      // Test for a parameterized type argument. Just a step more
      TypesTestHelper.getInstance().testType("Type<Arg1<Arg2<Arg3>>>[]");
      
      // Test for a parameterized type argument. Just a step more
      TypesTestHelper.getInstance().testType("Type<Arg1<Arg2<Arg3<Arg4>>>>[]");
      
      // Same test as above, but with an array type as Arg2
      TypesTestHelper.getInstance().testType("Type<Arg1<Arg2<Arg3<Arg4>>[]>>[]");
      
      // Same as above, but differnt place for the brackets
      TypesTestHelper.getInstance().testType("Type<Arg1<Arg2<Arg3<Arg4>>>[]>[]");
    }
    catch (Exception e) {
      fail(e.getMessage());
    }
  }
  
  @Test
  public void testNegativeComplexArrayTypes() {
    // Negative test for a qualified type, whose qualification is an array type
    try {
      TypesTestHelper.getInstance().testType("packageName.Type[].Invalid");
      fail("The test should fail");
    }
    catch (Exception e) {
    }
    
    // Test for a parameterized array type with additional ">"
    try {
      TypesTestHelper.getInstance().testType("Type<?>[]>");
      fail("The test should fail");
    }
    catch (Exception e) {
    }
    
    // Test for a parameterized type argument with missing ">"
    try {
      TypesTestHelper.getInstance().testType("Type<Arg1<Arg2>[]");
      fail("The test should fail");
    }
    catch (Exception e) {
    }
    
    // Test for a parameterized type argument with missing "]"
    try {
      TypesTestHelper.getInstance().testType("Type<Arg1<Arg2>[[]>[]");
      fail("The test should fail");
    }
    catch (Exception e) {
    }
    
    // to be checked by context-analysis:
    // // Test for a parameterized type argument with a primitive type
    // try {
    // TypeTestHelper.getInstance().testType("Type<Arg1<int>>");
    // fail("The test should fail");
    // }
    // catch (Exception e) {
    // }
    
    // Test for a parameterized type argument with missing ">"
    try {
      TypesTestHelper.getInstance().testType("Type<Arg1<Arg2<Arg3<Arg4>>>[]");
      fail("The test should fail");
    }
    catch (Exception e) {
    }
    
    // Same test as above, but with an array type as Arg2 and with additional
    // ">"
    try {
      TypesTestHelper.getInstance().testType("Type<Arg1<Arg2<Arg3<Arg4>>[]>>>[]");
      fail("The test should fail");
    }
    catch (Exception e) {
    }
  }
  
}
