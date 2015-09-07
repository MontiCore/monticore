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
public class TypeParametersTest {
  
  @Test
  public void testTypeParameters1() {
    try {
      TypesTestHelper.getInstance().testTypeParameter("<T extends SuperClassA<S> & SuperClassB," + " S extends a.b.c.SuperClassC<T>>");
      TypesTestHelper.getInstance().testTypeParameter("<T extends mc.examples.ClassA>");
      TypesTestHelper.getInstance().testTypeParameter("<T extends ClassA.ClassB<String>.ClassC<Object>>");
      TypesTestHelper.getInstance().testTypeParameter("<T extends A.B<String[]>.C<Object>>");
      TypesTestHelper.getInstance().testTypeParameter("<S>");
      TypesTestHelper.getInstance().testTypeParameter("<T extends A.B<C[]>.D<E<F<G>>,H> & I<J>>");
      TypesTestHelper.getInstance().testTypeParameter("<T1 extends A<B<C<D>>>, T2 extends A>");
    }
    catch (Exception e) {
      fail(e.getMessage());
    }
  }
  
  // copied from JavaDSL
  @Test
  public void testTypeParameters2() {
    try {
      // Test for a simple type parameter
      TypesTestHelper.getInstance().testTypeParameter("<Class1>");
      
      // Test for more than one type type parameter
      TypesTestHelper.getInstance().testTypeParameter("<Class1, Class2, ExtendedClass extends UpperBound, Class3>");
      
      // Test for a extended type parameter with a parameterized super class
      TypesTestHelper.getInstance().testTypeParameter("<Class1,  ExtendedClass extends UpperBound<Arg1>>");
      
      // Test for a extended type parameter with a parameterized super
      // class with a parameterized super class
      TypesTestHelper.getInstance().testTypeParameter("<Class1,  ExtendedClass extends UpperBound<Arg1<Arg2> >>");
      
      // Test for a extended type parameter with a parameterized super
      // class with a parameterized super class with a parameterized super
      // class
      TypesTestHelper.getInstance().testTypeParameter("<Class1,  ExtendedClass extends UpperBound<Arg1<Arg2<Arg3>>>>");
    }
    catch (Exception e) {
      fail(e.getMessage());
    }
  }
  
  @Test
  public void testNegativeTypeParameters1() {
    try {
      TypesTestHelper.getInstance().testTypeParameter("<T extends A.B<String[]>.C<Object>[]>");
      fail("The test should fail");
    }
    catch (Exception e) {
    }
    try {
      TypesTestHelper.getInstance().testTypeParameter("<T extends String[]>");
      fail("The test should fail");
    }
    catch (Exception e) {
    }
    try {
      TypesTestHelper.getInstance().testTypeParameter("<T extends int[]>");
      fail("The test should fail");
    }
    catch (Exception e) {
    }
  }
  
  // copied from JavaDSL
  @Test
  public void testNegativeTypeParameters2() {
    // Negative test with 2 '<' in the beginning
    try {
      TypesTestHelper.getInstance().testTypeParameter("<<Class1>");
      fail("The test should fail");
    }
    catch (Exception e) {
    }
    // Negative test with 2 '>' in the end
    try {
      TypesTestHelper.getInstance().testTypeParameter("<Class1>>");
      fail("The test should fail");
    }
    catch (Exception e) {
    }
    // Negative test with double '<< >>'
    try {
      TypesTestHelper.getInstance().testTypeParameter("<<Class1>>");
      fail("The test should fail");
    }
    catch (Exception e) {
    }
    // Negative test with type argument syntax instead of type parameter
    try {
      TypesTestHelper.getInstance().testTypeParameter("<Class1<Class2>>");
      fail("The test should fail");
    }
    catch (Exception e) {
    }
    // Negative test with one '>' unsufficient
    try {
      TypesTestHelper.getInstance().testTypeParameter("<Class1,  ExtendedClass extends UpperBound<Arg1<Arg2<Arg3>>>");
      fail("The test should fail");
    }
    catch (Exception e) {
    }
    // Negative test with one '>' unsufficient
    try {
      TypesTestHelper.getInstance().testTypeParameter("<Class1,  ExtendedClass extends UpperBound<Arg1<Arg2<Arg3<Arg>>>>");
      fail("The test should fail");
    }
    catch (Exception e) {
    }
    // Negative test with a missing comma
    try {
      TypesTestHelper.getInstance().testTypeParameter("<Class1,ExtendedClass extends UpperBound<Arg1> Class2 >");
      fail("The test should fail");
    }
    catch (Exception e) {
    }
  }
  
}
