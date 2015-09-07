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
public class ComplexQualifiedTypesTest {
  
  @Test
  public void testComplexQualifiedType() {
    try {
      TypesTestHelper.getInstance().testType("Type");
      TypesTestHelper.getInstance().testType("Type<Arg>");
      TypesTestHelper.getInstance().testType("package.Type");
      TypesTestHelper.getInstance().testType("packageName.OuterClass.Type<Arg>");
      TypesTestHelper.getInstance().testType("a.b.Type<Arg>.C");
      TypesTestHelper.getInstance().testType("a.b.Type<Arg>.C.D");
      TypesTestHelper.getInstance().testType("OwnClass");
      TypesTestHelper.getInstance().testType("a.b.c");
      TypesTestHelper.getInstance().testType("_$testABC_1._5");
      TypesTestHelper.getInstance().testType("a.b<c>");
      TypesTestHelper.getInstance().testType("Seq<Pair<T,S>>");
      TypesTestHelper.getInstance().testType("Pair<T,S>");
      TypesTestHelper.getInstance().testType("Seq<Pair<String,Number>>");
      TypesTestHelper.getInstance().testType("A<B<C,D<E,F<G>>>>");
      TypesTestHelper.getInstance().testType("A<B<C,D<E,F<G<H>>>>,I<J>>");
      TypesTestHelper.getInstance().testType("Vector<String>");
      TypesTestHelper.getInstance().testType("A.B<String>.C<Object>");
      TypesTestHelper.getInstance().testType("A.B<int[][]>.C<int[]>");
      TypesTestHelper.getInstance().testType("L<A[]>");
      TypesTestHelper.getInstance().testType("C<L<A>[]>");
      TypesTestHelper.getInstance().testType("a.b.c<arg>");
      TypesTestHelper.getInstance().testType("a.b.c<arg>.d");
      // Wildcards:
      TypesTestHelper.getInstance().testType("Collection<?>");
      TypesTestHelper.getInstance().testType("List<? extends Number>");
      TypesTestHelper.getInstance().testType("ReferenceQueue<? super T>");
      TypesTestHelper.getInstance().testType("Pair<String,?>");
      TypesTestHelper.getInstance().testType("B<? extends int[]>");
      TypesTestHelper.getInstance().testType("Pair<T, ? super Object>");
    }
    catch (Exception e) {
      fail(e.getMessage());
    }
  }
  
}
