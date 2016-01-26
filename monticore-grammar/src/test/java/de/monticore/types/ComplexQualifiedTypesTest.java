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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;

/**
 * @author Martin Schindler
 */
public class ComplexQualifiedTypesTest {
  
  @Test
  public void testComplexQualifiedType() {
    try {
      assertTrue(TypesTestHelper.getInstance().testType("Type"));
      assertTrue(TypesTestHelper.getInstance().testType("Type<Arg>"));
      assertTrue(TypesTestHelper.getInstance().testType("package.Type"));
      assertTrue(TypesTestHelper.getInstance().testType("packageName.OuterClass.Type<Arg>"));
      assertTrue(TypesTestHelper.getInstance().testType("a.b.Type<Arg>.C"));
      assertTrue(TypesTestHelper.getInstance().testType("a.b.Type<Arg>.C.D"));
      assertTrue(TypesTestHelper.getInstance().testType("OwnClass"));
      assertTrue(TypesTestHelper.getInstance().testType("a.b.c"));
      assertTrue(TypesTestHelper.getInstance().testType("_$testABC_1._5"));
      assertTrue(TypesTestHelper.getInstance().testType("a.b<c>"));
      assertTrue(TypesTestHelper.getInstance().testType("Seq<Pair<T,S>>"));
      assertTrue(TypesTestHelper.getInstance().testType("Pair<T,S>"));
      assertTrue(TypesTestHelper.getInstance().testType("Seq<Pair<String,Number>>"));
      assertTrue(TypesTestHelper.getInstance().testType("A<B<C,D<E,F<G>>>>"));
      // TODO MB Fix failing test
      //assertTrue(TypesTestHelper.getInstance().testType("A<B<C,D<E,F<G<H>>>>,I<J>>"));
      assertTrue(TypesTestHelper.getInstance().testType("Vector<String>"));
      assertTrue(TypesTestHelper.getInstance().testType("A.B<String>.C<Object>"));
      assertTrue(TypesTestHelper.getInstance().testType("A.B<int[][]>.C<int[]>"));
      assertTrue(TypesTestHelper.getInstance().testType("L<A[]>"));
      assertTrue(TypesTestHelper.getInstance().testType("C<L<A>[]>"));
      assertTrue(TypesTestHelper.getInstance().testType("a.b.c<arg>"));
      assertTrue(TypesTestHelper.getInstance().testType("a.b.c<arg>.d"));
      // Wildcards:
      assertTrue(TypesTestHelper.getInstance().testType("Collection<?>"));
      assertTrue(TypesTestHelper.getInstance().testType("List<? extends Number>"));
      assertTrue(TypesTestHelper.getInstance().testType("ReferenceQueue<? super T>"));
      assertTrue(TypesTestHelper.getInstance().testType("Pair<String,?>"));
      assertTrue(TypesTestHelper.getInstance().testType("B<? extends int[]>"));
      assertTrue(TypesTestHelper.getInstance().testType("Pair<T, ? super Object>"));
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
  }
  
}
