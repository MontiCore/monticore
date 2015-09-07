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
      TypesTestHelper.getInstance().testType("ParameterizedType<?>");
      // Test with a upper bound
      TypesTestHelper.getInstance().testType("ParameterizedType<? extends ParentClass>");
      // Test with a lower bound
      TypesTestHelper.getInstance().testType("ParameterizedType<? super ChildClass>");
    }
    catch (Exception e) {
      fail(e.getMessage());
    }
  }
  
  @Test
  public void testNegativeWildcardType() {
    // Negative test with a upper and lower bound
    try {
      TypesTestHelper.getInstance().testType("ParameterizedType<? extends ParentClass super ChildClass>");
      fail("The test should fail");
    }
    catch (Exception e) {
    }
    // Negative test with a wildcard as upper bound
    try {
      TypesTestHelper.getInstance().testType("ParameterizedType<? extends ? extends Invalid>");
      fail("The test should fail");
    }
    catch (Exception e) {
    }
    // to be checked by context-analysis:
    // // Negative test with primitive type array as upper bound
    // try {
    // TypeTestHelper.getInstance().testType("ParameterizedType<? extends int>");
    // fail("The test should fail");
    // }
    // catch (Exception e) {
    // }
    // // Negative test with primitive type as lower bound
    // try {
    // TypeTestHelper.getInstance().testType("ParameterizedType<? super char>");
    // fail("The test should fail");
    // }
    // catch (Exception e) {
    // }
  }
  
}
