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
public class TypeArgumentsTest {
  
  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testWildcardType() {
    try {
      
      // Test for a simple type argument
      assertTrue(TypesTestHelper.getInstance().testType("ParameterizedType<Arg1>"));
      
      // Test for a empty type argument
      assertTrue(TypesTestHelper.getInstance().testType("ParameterizedType<>"));
      
      // Test for more than one type argument
      assertTrue(TypesTestHelper.getInstance().testType("ParameterizedType<Arg1, Arg2, ? extends Arg1, Arg3>"));
      
      // Test for a nested type argument
      assertTrue(TypesTestHelper.getInstance().testType("ParameterizedType<Arg1<Arg2>>"));
      
      // Test for a nested type argument. One nested level more
      assertTrue(TypesTestHelper.getInstance().testType("ParameterizedType<Arg1<Arg2<Arg3>>>"));
      
      // Same test as above, but another argument on same level as Arg2
      assertTrue(TypesTestHelper.getInstance().testType("ParameterizedType<Arg1<Arg2<Arg3>, Arg2>>"));
      
      // Test for a nested type argument. One nested level more
      assertTrue(TypesTestHelper.getInstance().testType("ParameterizedType<Arg1<Arg2<Arg3<?>>>>"));
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
  }
  
  @Test
  public void testNegativeWildcardType() {
    // Negative test with 2 '<' in the beginning
    try {
      assertNull(TypesTestHelper.getInstance().parseType("ParameterizedType<<Arg1>"));
      
      // Negative test with 2 '>' in the end
      assertNull(TypesTestHelper.getInstance().parseType("ParameterizedType<Arg1>>"));
      
      // Negative test with doubel '<< >>'
      assertNull(TypesTestHelper.getInstance().parseType("ParameterizedType<<Arg1>>"));
      
      // Negative test with one '>' too much in the end
      assertNull(TypesTestHelper.getInstance().parseType("ParameterizedType<Arg1<Arg2>>>"));
      
      // Negative test with one '>' unsufficient
      assertNull(TypesTestHelper.getInstance().parseType("ParameterizedType<Arg1<Arg2<Arg3, Arg4>>"));
      
      // Negative test with one '>' unsufficient
      assertNull(TypesTestHelper.getInstance().parseType("ParameterizedType<Arg1<Arg2<Arg3>, Arg4>"));
      
      // Negative test with a missing comma
      assertNull(TypesTestHelper.getInstance().parseType("ParameterizedType<Arg1<Arg2<Arg3> Arg2>>"));
      
      // Test for a nested type argument with primitive type
      assertNull(TypesTestHelper.getInstance().parseType("ParameterizedType<Arg1<char<Arg3>>>"));
    }
    catch (IOException e) {
      fail(e.getMessage());
      
    }
    
  }
  
}
