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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;

/**
 * @author Martin Schindler
 */
public class NestedTypeArgumentsTest {
  
  @Test
  public void testNestedTypeArgument1() {
    try {
      assertTrue(TypesTestHelper.getInstance().testType("C<L<A>>[]"));
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
  }
  
  @Test
  public void testNestedTypeArgument2() {
    try {
      assertTrue(TypesTestHelper.getInstance().testType("C<L<A>[]>"));
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
  }
  
  @Test
  public void testNestedTypeArgument3() {
    try {
      assertTrue(TypesTestHelper.getInstance().testType("var<O1<I1<M1>>, O2<I2>>"));
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
  }
  
  @Test
  public void testNestedTypeArgument4() {
    try {
      assertTrue(TypesTestHelper.getInstance().testType("var<O1<I1<int[]>>, O2<char[][]>>"));
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
  }
  
}
