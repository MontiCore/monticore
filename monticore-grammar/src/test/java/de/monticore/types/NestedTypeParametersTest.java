/* (c) https://github.com/MontiCore/monticore */

package de.monticore.types;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;

/**
 * @author Martin Schindler
 */
public class NestedTypeParametersTest {
  
  @Test
  public void testNestedTypeParameter1() {
    try {
      assertTrue(TypesTestHelper.getInstance().testTypeParameter("<T extends A.B<C[]>.D<E<F<G<H>>,I>> & J<K>>"));
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
  }
  
  @Test
  public void testNestedTypeParameter2() {
    try {
      assertTrue(TypesTestHelper.getInstance().testTypeParameter("<T1 extends A<B<D, E>>, T2 extends " + "A.B<C[]>.D<E<F<G<H>>,I>> & J<K>>"));
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
  }
  
}
