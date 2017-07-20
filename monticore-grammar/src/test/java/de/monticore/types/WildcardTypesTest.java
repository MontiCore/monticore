/*
 * ******************************************************************************
 * MontiCore Language Workbench, www.monticore.de
 * Copyright (c) 2017, MontiCore, All rights reserved.
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
