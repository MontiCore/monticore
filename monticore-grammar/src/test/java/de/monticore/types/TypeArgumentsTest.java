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

import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * @author Martin Schindler
 */
public class TypeArgumentsTest {
  
  @Test
  public void testWildcardType() {
    try {
      
      // Test for a simple type argument
      TypesTestHelper.getInstance().testType("ParameterizedType<Arg1>");
      
      // Test for a empty type argument
      TypesTestHelper.getInstance().testType("ParameterizedType<>");
      
      // Test for more than one type argument
      TypesTestHelper.getInstance().testType("ParameterizedType<Arg1, Arg2, ? extends Arg1, Arg3>");
      
      // Test for a nested type argument
      TypesTestHelper.getInstance().testType("ParameterizedType<Arg1<Arg2>>");
      
      // Test for a nested type argument. One nested level more
      TypesTestHelper.getInstance().testType("ParameterizedType<Arg1<Arg2<Arg3>>>");
      
      // Same test as above, but another argument on same level as Arg2
      TypesTestHelper.getInstance().testType("ParameterizedType<Arg1<Arg2<Arg3>, Arg2>>");
      
      // Test for a nested type argument. One nested level more
      TypesTestHelper.getInstance().testType("ParameterizedType<Arg1<Arg2<Arg3<?>>>>");
    }
    catch (Exception e) {
      fail(e.getMessage());
    }
  }
  
  public void testNegativeWildcardType() {
    // Negative test with 2 '<' in the beginning
    try {
      TypesTestHelper.getInstance().testType("ParameterizedType<<Arg1>");
      fail("The test should fail");
    }
    catch (Exception e) {
    }
    
    // Negative test with 2 '>' in the end
    try {
      TypesTestHelper.getInstance().testType("ParameterizedType<Arg1>>");
      fail("The test should fail");
    }
    catch (Exception e) {
    }
    
    // Negative test with doubel '<< >>'
    try {
      TypesTestHelper.getInstance().testType("ParameterizedType<<Arg1>>");
      fail("The test should fail");
    }
    catch (Exception e) {
    }
    
    // Negative test with one '>' too much in the end
    try {
      TypesTestHelper.getInstance().testType("ParameterizedType<Arg1<Arg2>>>");
      fail("The test should fail");
    }
    catch (Exception e) {
    }
    
    // Negative test with one '>' unsufficient
    try {
      TypesTestHelper.getInstance().testType("ParameterizedType<Arg1<Arg2<Arg3, Arg4>>");
      fail("The test should fail");
    }
    catch (Exception e) {
    }
    
    // Negative test with one '>' unsufficient
    try {
      TypesTestHelper.getInstance().testType("ParameterizedType<Arg1<Arg2<Arg3>, Arg4>");
      fail("The test should fail");
    }
    catch (Exception e) {
    }
    
    // Negative test with a missing comma
    try {
      TypesTestHelper.getInstance().testType("ParameterizedType<Arg1<Arg2<Arg3> Arg2>>");
      fail("The test should fail");
    }
    catch (Exception e) {
    }
    
    // Test for a nested type argument with primitive type
    try {
      TypesTestHelper.getInstance().testType("ParameterizedType<Arg1<char<Arg3>>>");
      fail("The test should fail");
    }
    catch (Exception e) {
    }
    
    // to be checked by context-analysis:
    // // Test for a nested type argument with primitive type
    // try {
    // TypeTestHelper.getInstance().testType("ParameterizedType<Arg1<Arg2<Arg3>, int>>");
    // fail("The test should fail");
    // }
    // catch (Exception e) {
    // }
  }
  
}
