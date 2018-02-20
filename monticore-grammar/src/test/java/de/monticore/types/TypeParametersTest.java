/* (c) https://github.com/MontiCore/monticore */

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
public class TypeParametersTest {
  
  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testTypeParameters1() {
    try {
      assertTrue(TypesTestHelper.getInstance().testTypeParameter(
          "<T extends SuperClassA<S> & SuperClassB," + " S extends a.b.c.SuperClassC<T>>"));
      assertTrue(TypesTestHelper.getInstance().testTypeParameter("<T extends mc.examples.ClassA>"));
      assertTrue(TypesTestHelper.getInstance()
          .testTypeParameter("<T extends ClassA.ClassB<String>.ClassC<Object>>"));
      assertTrue(TypesTestHelper.getInstance().testTypeParameter("<T extends A.B<String[]>.C<Object>>"));
      assertTrue(TypesTestHelper.getInstance().testTypeParameter("<S>"));
      assertTrue(TypesTestHelper.getInstance().testTypeParameter("<T extends A.B<C[]>.D<E<F<G>>,H> & I<J>>"));
      assertTrue(TypesTestHelper.getInstance().testTypeParameter("<T1 extends A<B<C<D>>>, T2 extends A>"));
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
  }
  
  // copied from JavaDSL
  @Test
  public void testTypeParameters2() {
    try {
      // Test for a simple type parameter
      assertTrue(TypesTestHelper.getInstance().testTypeParameter("<Class1>"));
      
      // Test for more than one type type parameter
      assertTrue(TypesTestHelper.getInstance()
          .testTypeParameter("<Class1, Class2, ExtendedClass extends UpperBound, Class3>"));
          
      // Test for a extended type parameter with a parameterized super class
      assertTrue(TypesTestHelper.getInstance()
          .testTypeParameter("<Class1,  ExtendedClass extends UpperBound<Arg1>>"));
          
      // Test for a extended type parameter with a parameterized super
      // class with a parameterized super class
      assertTrue(TypesTestHelper.getInstance()
          .testTypeParameter("<Class1,  ExtendedClass extends UpperBound<Arg1<Arg2> >>"));
          
      // Test for a extended type parameter with a parameterized super
      // class with a parameterized super class with a parameterized super
      // class
      assertTrue(TypesTestHelper.getInstance()
          .testTypeParameter("<Class1,  ExtendedClass extends UpperBound<Arg1<Arg2<Arg3>>>>"));
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
  }
  
  @Test
  public void testNegativeTypeParameters1() {
    try {
      assertNull(TypesTestHelper.getInstance()
          .parseTypeParameters("<T extends A.B<String[]>.C<Object>[]>"));
          
      assertNull(TypesTestHelper.getInstance().parseTypeParameters("<T extends String[]>"));
      
      assertNull(TypesTestHelper.getInstance().parseTypeParameters("<T extends int[]>"));
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
  }
  
  // copied from JavaDSL
  @Test
  public void testNegativeTypeParameters2() {
    try {
      // Negative test with 2 '<' in the beginning
      assertNull(TypesTestHelper.getInstance().parseTypeParameters("<<Class1>"));
      
      // Negative test with 2 '>' in the end
      assertNull(TypesTestHelper.getInstance().parseTypeParameters("<Class1>>"));
      
      // Negative test with double '<< >>'
      assertNull(TypesTestHelper.getInstance().parseTypeParameters("<<Class1>>"));
      
      // Negative test with type argument syntax instead of type parameter
      assertNull(TypesTestHelper.getInstance().parseTypeParameters("<Class1<Class2>>"));
      
      // Negative test with one '>' unsufficient
      assertNull(TypesTestHelper.getInstance()
          .parseTypeParameters("<Class1,  ExtendedClass extends UpperBound<Arg1<Arg2<Arg3>>>"));
          
      // Negative test with one '>' unsufficient
      assertNull(TypesTestHelper.getInstance()
          .parseTypeParameters(
              "<Class1,  ExtendedClass extends UpperBound<Arg1<Arg2<Arg3<Arg>>>>"));
              
      // Negative test with a missing comma
      assertNull(TypesTestHelper.getInstance()
          .parseTypeParameters("<Class1,ExtendedClass extends UpperBound<Arg1> Class2 >"));
    }
    catch (Exception e) {
      fail(e.getMessage());
    }
  }
  
}
