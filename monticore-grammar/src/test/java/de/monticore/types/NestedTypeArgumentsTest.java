/* (c) https://github.com/MontiCore/monticore */

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
