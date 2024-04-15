/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.assignmentexpressions._visitor;

import de.monticore.expressions.AbstractInterpreterTest;
import de.monticore.interpreter.ValueFactory;
import org.junit.Test;

public class AssignmentExpressionsInterpreterTest extends AbstractInterpreterTest {

  @Test
  public void testIncSuffixExpression() {
    testValidExpression("c++", ValueFactory.createValue(98));
    testInvalidExpression("s++");
    testValidExpression("i++", ValueFactory.createValue(2));
    testValidExpression("l++", ValueFactory.createValue(6L));
    testValidExpression("f++", ValueFactory.createValue(2.5f));
    testValidExpression("d++", ValueFactory.createValue(4.14));
    testInvalidExpression("b++");
  }

  @Test
  public void testIncPrefixExpression() {
    testValidExpression("++c", ValueFactory.createValue(98));
    testInvalidExpression("++s");
    testValidExpression("++i", ValueFactory.createValue(2));
    testValidExpression("++l", ValueFactory.createValue(6L));
    testValidExpression("++f", ValueFactory.createValue(2.5f));
    testValidExpression("++d", ValueFactory.createValue(4.14));
    testInvalidExpression("++b");
  }

  @Test
  public void testDecSuffixExpression() {
    testValidExpression("c--", ValueFactory.createValue(96));
    testInvalidExpression("s--");
    testValidExpression("i--", ValueFactory.createValue(0));
    testValidExpression("l--", ValueFactory.createValue(4L));
    testValidExpression("f--", ValueFactory.createValue(0.5f));
    testValidExpression("d--", ValueFactory.createValue(2.14));
    testInvalidExpression("b--");
  }

  @Test
  public void testDecPrefixExpression() {
    testValidExpression("--c", ValueFactory.createValue(96));
    testInvalidExpression("--s");
    testValidExpression("--i", ValueFactory.createValue(0));
    testValidExpression("--l", ValueFactory.createValue(4L));
    testValidExpression("--f", ValueFactory.createValue(0.5f));
    testValidExpression("--d", ValueFactory.createValue(2.14));
    testInvalidExpression("--b");
  }

  @Test
  public void testAndEqualsExpression() {
    testInvalidExpression("b &= false");
    testInvalidExpression("b &= 1");
    testInvalidExpression("b &= 2L");
    testInvalidExpression("b &= 1.5f");
    testInvalidExpression("b &= 3.14");
    testInvalidExpression("b &= 'a'");
    testInvalidExpression("b &= \"test\"");

    testInvalidExpression("i &= false");
    testValidExpression("i &= 1", ValueFactory.createValue(1));
    testValidExpression("i &= 2L", ValueFactory.createValue(0));
    testInvalidExpression("i &= 1.5f");
    testInvalidExpression("i &= 3.14");
    testValidExpression("i &= 'a'", ValueFactory.createValue(0));
    testInvalidExpression("i &= \"test\"");

    testInvalidExpression("l &= false");
    testValidExpression("l &= 1", ValueFactory.createValue(1));
    testValidExpression("l &= 2L", ValueFactory.createValue(0));
    testInvalidExpression("l &= 1.5f");
    testInvalidExpression("l &= 3.14");
    testValidExpression("l &= 'a'", ValueFactory.createValue(0));
    testInvalidExpression("l &= \"test\"");

    testInvalidExpression("f &= false");
    testInvalidExpression("f &= 1");
    testInvalidExpression("f &= 2L");
    testInvalidExpression("f &= 1.5f");
    testInvalidExpression("f &= 3.14");
    testInvalidExpression("f &= 'a'");
    testInvalidExpression("f &= \"test\"");

    testInvalidExpression("d &= false");
    testInvalidExpression("d &= 1");
    testInvalidExpression("d &= 2L");
    testInvalidExpression("d &= 1.5f");
    testInvalidExpression("d &= 3.14");
    testInvalidExpression("d &= 'a'");
    testInvalidExpression("d &= \"test\"");

    testInvalidExpression("c &= false");
    testValidExpression("c &= 1", ValueFactory.createValue(1));
    testValidExpression("c &= 2L", ValueFactory.createValue(0));
    testInvalidExpression("c &= 1.5f");
    testInvalidExpression("c &= 3.14");
    testValidExpression("c &= 'a'", ValueFactory.createValue(0));
    testInvalidExpression("c &= \"test\"");

    testInvalidExpression("s &= false");
    testInvalidExpression("s &= 1");
    testInvalidExpression("s &= 2L");
    testInvalidExpression("s &= 1.5f");
    testInvalidExpression("s &= 3.14");
    testInvalidExpression("s &= 'a'");
    testInvalidExpression("s &= \"test\"");
  }

  @Test
  public void testGTGTEqualsExpression() {
    testInvalidExpression("b >>= false");
    testInvalidExpression("b >>= 1");
    testInvalidExpression("b >>= 2L");
    testInvalidExpression("b >>= 1.5f");
    testInvalidExpression("b >>= 3.14");
    testInvalidExpression("b >>= 'a'");
    testInvalidExpression("b >>= \"test\"");

    testInvalidExpression("i >>= false");
    testValidExpression("i >>= 1", ValueFactory.createValue(0));
    testValidExpression("i >>= 2L", ValueFactory.createValue(0));
    testInvalidExpression("i >>= 1.5f");
    testInvalidExpression("i >>= 3.14");
    testValidExpression("i >>= 'a'", ValueFactory.createValue(0));
    testInvalidExpression("i >>= \"test\"");

    testInvalidExpression("l >>= false");
    testValidExpression("l >>= 1", ValueFactory.createValue(2));
    testValidExpression("l >>= 2L", ValueFactory.createValue(0));
    testInvalidExpression("l >>= 1.5f");
    testInvalidExpression("l >>= 3.14");
    testValidExpression("l >>= 'a'", ValueFactory.createValue(0));
    testInvalidExpression("l >>= \"test\"");

    testInvalidExpression("f >>= false");
    testInvalidExpression("f >>= 1");
    testInvalidExpression("f >>= 2L");
    testInvalidExpression("f >>= 1.5f");
    testInvalidExpression("f >>= 3.14");
    testInvalidExpression("f >>= 'a'");
    testInvalidExpression("f >>= \"test\"");

    testInvalidExpression("d >>= false");
    testInvalidExpression("d >>= 1");
    testInvalidExpression("d >>= 2L");
    testInvalidExpression("d >>= 1.5f");
    testInvalidExpression("d >>= 3.14");
    testInvalidExpression("d >>= 'a'");
    testInvalidExpression("d >>= \"test\"");

    testInvalidExpression("c >>= false");
    testValidExpression("c >>= 1", ValueFactory.createValue(48));
    testValidExpression("c >>= 2L", ValueFactory.createValue(12));
    testInvalidExpression("c >>= 1.5f");
    testInvalidExpression("c >>= 3.14");
    testValidExpression("c >>= 'a'", ValueFactory.createValue(6));
    testInvalidExpression("c >>= \"test\"");

    testInvalidExpression("s >>= false");
    testInvalidExpression("s >>= 1");
    testInvalidExpression("s >>= 2L");
    testInvalidExpression("s >>= 1.5f");
    testInvalidExpression("s >>= 3.14");
    testInvalidExpression("s >>= 'a'");
    testInvalidExpression("s >>= \"test\"");
  }
  
  @Test
  public void testGTGTGTEqualsExpression() {
    testInvalidExpression("b >>>= false");
    testInvalidExpression("b >>>= 1");
    testInvalidExpression("b >>>= 2L");
    testInvalidExpression("b >>>= 1.5f");
    testInvalidExpression("b >>>= 3.14");
    testInvalidExpression("b >>>= 'a'");
    testInvalidExpression("b >>>= \"test\"");

    testInvalidExpression("i >>>= false");
    testValidExpression("i >>>= 1", ValueFactory.createValue(0));
    testValidExpression("i >>>= 2L", ValueFactory.createValue(0));
    testInvalidExpression("i >>>= 1.5f");
    testInvalidExpression("i >>>= 3.14");
    testValidExpression("i >>>= 'a'", ValueFactory.createValue(0));
    testInvalidExpression("i >>>= \"test\"");

    testInvalidExpression("l >>>= false");
    testValidExpression("l >>>= 1", ValueFactory.createValue(2));
    testValidExpression("l >>>= 2L", ValueFactory.createValue(0));
    testInvalidExpression("l >>>= 1.5f");
    testInvalidExpression("l >>>= 3.14");
    testValidExpression("l >>>= 'a'", ValueFactory.createValue(0));
    testInvalidExpression("l >>>= \"test\"");

    testInvalidExpression("f >>>= false");
    testInvalidExpression("f >>>= 1");
    testInvalidExpression("f >>>= 2L");
    testInvalidExpression("f >>>= 1.5f");
    testInvalidExpression("f >>>= 3.14");
    testInvalidExpression("f >>>= 'a'");
    testInvalidExpression("f >>>= \"test\"");

    testInvalidExpression("d >>>= false");
    testInvalidExpression("d >>>= 1");
    testInvalidExpression("d >>>= 2L");
    testInvalidExpression("d >>>= 1.5f");
    testInvalidExpression("d >>>= 3.14");
    testInvalidExpression("d >>>= 'a'");
    testInvalidExpression("d >>>= \"test\"");

    testInvalidExpression("c >>>= false");
    testValidExpression("c >>>= 1", ValueFactory.createValue(48));
    testValidExpression("c >>>= 2L", ValueFactory.createValue(12));
    testInvalidExpression("c >>>= 1.5f");
    testInvalidExpression("c >>>= 3.14");
    testValidExpression("c >>>= 'a'", ValueFactory.createValue(6));
    testInvalidExpression("c >>>= \"test\"");

    testInvalidExpression("s >>>= false");
    testInvalidExpression("s >>>= 1");
    testInvalidExpression("s >>>= 2L");
    testInvalidExpression("s >>>= 1.5f");
    testInvalidExpression("s >>>= 3.14");
    testInvalidExpression("s >>>= 'a'");
    testInvalidExpression("s >>>= \"test\"");
  }

  @Test
  public void testLTLTEqualsExpression() {
    testInvalidExpression("b <<= false");
    testInvalidExpression("b <<= 1");
    testInvalidExpression("b <<= 2L");
    testInvalidExpression("b <<= 1.5f");
    testInvalidExpression("b <<= 3.14");
    testInvalidExpression("b <<= 'a'");
    testInvalidExpression("b <<= \"test\"");

    testInvalidExpression("i <<= false");
    testValidExpression("i <<= 1", ValueFactory.createValue(2));
    testValidExpression("i <<= 2L", ValueFactory.createValue(8));
    testInvalidExpression("i <<= 1.5f");
    testInvalidExpression("i <<= 3.14");
    testValidExpression("i <<= 'a'", ValueFactory.createValue(16));
    testInvalidExpression("i <<= \"test\"");

    testInvalidExpression("l <<= false");
    testValidExpression("l <<= 1", ValueFactory.createValue(10));
    testValidExpression("l <<= 2L", ValueFactory.createValue(40));
    testInvalidExpression("l <<= 1.5f");
    testInvalidExpression("l <<= 3.14");
    testValidExpression("l <<= 'a'", ValueFactory.createValue(343597383680L));
    testInvalidExpression("l <<= \"test\"");

    testInvalidExpression("f <<= false");
    testInvalidExpression("f <<= 1");
    testInvalidExpression("f <<= 2L");
    testInvalidExpression("f <<= 1.5f");
    testInvalidExpression("f <<= 3.14");
    testInvalidExpression("f <<= 'a'");
    testInvalidExpression("f <<= \"test\"");

    testInvalidExpression("d <<= false");
    testInvalidExpression("d <<= 1");
    testInvalidExpression("d <<= 2L");
    testInvalidExpression("d <<= 1.5f");
    testInvalidExpression("d <<= 3.14");
    testInvalidExpression("d <<= 'a'");
    testInvalidExpression("d <<= \"test\"");

    testInvalidExpression("c <<= false");
    testValidExpression("c <<= 1", ValueFactory.createValue(194));
    testValidExpression("c <<= 2L", ValueFactory.createValue(776));
    testInvalidExpression("c <<= 1.5f");
    testInvalidExpression("c <<= 3.14");
    testValidExpression("c <<= 'a'", ValueFactory.createValue(1552));
    testInvalidExpression("c <<= \"test\"");

    testInvalidExpression("s <<= false");
    testInvalidExpression("s <<= 1");
    testInvalidExpression("s <<= 2L");
    testInvalidExpression("s <<= 1.5f");
    testInvalidExpression("s <<= 3.14");
    testInvalidExpression("s <<= 'a'");
    testInvalidExpression("s <<= \"test\"");
  }

  @Test
  public void testMinusEqualsExpression() {
    testInvalidExpression("b -= false");
    testInvalidExpression("b -= 1");
    testInvalidExpression("b -= 2L");
    testInvalidExpression("b -= 1.5f");
    testInvalidExpression("b -= 3.14");
    testInvalidExpression("b -= 'a'");
    testInvalidExpression("b -= \"test\"");

    testInvalidExpression("i -= false");
    testValidExpression("i -= 1", ValueFactory.createValue(0));
    testValidExpression("i -= 2L", ValueFactory.createValue(-2));
    testValidExpression("i -= 1.5f", ValueFactory.createValue(-3.5f));
    testValidExpression("i -= 3.14", ValueFactory.createValue(-6.64));
    testValidExpression("i -= 'a'", ValueFactory.createValue(-103.64));
    testInvalidExpression("i -= \"test\"");

    testInvalidExpression("l -= false");
    testValidExpression("l -= 1", ValueFactory.createValue(4));
    testValidExpression("l -= 2L", ValueFactory.createValue(2));
    testValidExpression("l -= 1.5f", ValueFactory.createValue(0.5f));
    testValidExpression("l -= 3.14", ValueFactory.createValue(-2.64));
    testValidExpression("l -= 'a'", ValueFactory.createValue(-99.64));
    testInvalidExpression("l -= \"test\"");

    testInvalidExpression("f -= false");
    testValidExpression("f -= 1", ValueFactory.createValue(0.5f));
    testValidExpression("f -= 2L", ValueFactory.createValue(-1.5f));
    testValidExpression("f -= 1.5f", ValueFactory.createValue(-3f));
    testValidExpression("f -= 3.14", ValueFactory.createValue(-6.14));
    testValidExpression("f -= 'a'", ValueFactory.createValue(-103.14));
    testInvalidExpression("f -= \"test\"");

    testInvalidExpression("d -= false");
    testValidExpression("d -= 1", ValueFactory.createValue(2.14));
    testValidExpression("d -= 2L", ValueFactory.createValue(0.14));
    testValidExpression("d -= 1.5f", ValueFactory.createValue(-1.36));
    testValidExpression("d -= 3.14", ValueFactory.createValue(-4.5));
    testValidExpression("d -= 'a'", ValueFactory.createValue(-101.5));
    testInvalidExpression("d -= \"test\"");

    testInvalidExpression("c -= false");
    testValidExpression("c -= 1", ValueFactory.createValue(96));
    testValidExpression("c -= 2L", ValueFactory.createValue(94));
    testValidExpression("c -= 1.5f", ValueFactory.createValue(92.5f));
    testValidExpression("c -= 3.14", ValueFactory.createValue(89.36));
    testValidExpression("c -= 'a'", ValueFactory.createValue(-7.64));
    testInvalidExpression("c -= \"test\"");

    testInvalidExpression("s -= false");
    testInvalidExpression("s -= 1");
    testInvalidExpression("s -= 2L");
    testInvalidExpression("s -= 1.5f");
    testInvalidExpression("s -= 3.14");
    testInvalidExpression("s -= 'a'");
    testInvalidExpression("s -= \"test\"");
  }

  @Test
  public void testPercentEqualsExpression() {
    testInvalidExpression("b %= false");
    testInvalidExpression("b %= 1");
    testInvalidExpression("b %= 2L");
    testInvalidExpression("b %= 1.5f");
    testInvalidExpression("b %= 3.14");
    testInvalidExpression("b %= 'a'");
    testInvalidExpression("b %= \"test\"");

    testInvalidExpression("i %= false");
    testValidExpression("i %= 1", ValueFactory.createValue(0));
    testValidExpression("i %= 2L", ValueFactory.createValue(0));
    testValidExpression("i %= 1.5f", ValueFactory.createValue(0));
    testValidExpression("i %= 3.14", ValueFactory.createValue(0));
    testValidExpression("i %= 'a'", ValueFactory.createValue(0));
    testInvalidExpression("i %= \"test\"");

    testInvalidExpression("l %= false");
    testValidExpression("l %= 1", ValueFactory.createValue(0));
    testValidExpression("l %= 2L", ValueFactory.createValue(0));
    testValidExpression("l %= 1.5f", ValueFactory.createValue(0));
    testValidExpression("l %= 3.14", ValueFactory.createValue(0));
    testValidExpression("l %= 'a'", ValueFactory.createValue(0));
    testInvalidExpression("l %= \"test\"");

    testInvalidExpression("f %= false");
    testValidExpression("f %= 1", ValueFactory.createValue(0.5f));
    testValidExpression("f %= 2L", ValueFactory.createValue(0.5f));
    testValidExpression("f %= 1.5f", ValueFactory.createValue(0.5f));
    testValidExpression("f %= 3.14", ValueFactory.createValue(0.5));
    testValidExpression("f %= 'a'", ValueFactory.createValue(0.5));
    testInvalidExpression("f %= \"test\"");

    testInvalidExpression("d %= false");
    testValidExpression("d %= 1", ValueFactory.createValue(0.14));
    testValidExpression("d %= 2L", ValueFactory.createValue(0.14));
    testValidExpression("d %= 1.5f", ValueFactory.createValue(0.14));
    testValidExpression("d %= 3.14", ValueFactory.createValue(0.14));
    testValidExpression("d %= 'a'", ValueFactory.createValue(0.14));
    testInvalidExpression("d %= \"test\"");

    testInvalidExpression("c %= false");
    testValidExpression("c %= 1", ValueFactory.createValue(0));
    testValidExpression("c %= 2L", ValueFactory.createValue(0));
    testValidExpression("c %= 1.5f", ValueFactory.createValue(0));
    testValidExpression("c %= 3.14", ValueFactory.createValue(0));
    testValidExpression("c %= 'a'", ValueFactory.createValue(0));
    testInvalidExpression("c %= \"test\"");

    testInvalidExpression("s %= false");
    testInvalidExpression("s %= 1");
    testInvalidExpression("s %= 2L");
    testInvalidExpression("s %= 1.5f");
    testInvalidExpression("s %= 3.14");
    testInvalidExpression("s %= 'a'");
    testInvalidExpression("s %= \"test\"");
  }

  @Test
  public void testPipeEqualsExpression() {
    testInvalidExpression("b |= false");
    testInvalidExpression("b |= 1");
    testInvalidExpression("b |= 2L");
    testInvalidExpression("b |= 1.5f");
    testInvalidExpression("b |= 3.14");
    testInvalidExpression("b |= 'a'");
    testInvalidExpression("b |= \"test\"");

    testInvalidExpression("i |= false");
    testValidExpression("i |= 1", ValueFactory.createValue(1));
    testValidExpression("i |= 2L", ValueFactory.createValue(3));
    testInvalidExpression("i |= 1.5f");
    testInvalidExpression("i |= 3.14");
    testValidExpression("i |= 'a'", ValueFactory.createValue(99));
    testInvalidExpression("i |= \"test\"");

    testInvalidExpression("l |= false");
    testValidExpression("l |= 1", ValueFactory.createValue(5));
    testValidExpression("l |= 2L", ValueFactory.createValue(7));
    testInvalidExpression("l |= 1.5f");
    testInvalidExpression("l |= 3.14");
    testValidExpression("l |= 'a'", ValueFactory.createValue(103));
    testInvalidExpression("l |= \"test\"");

    testInvalidExpression("f |= false");
    testInvalidExpression("f |= 1");
    testInvalidExpression("f |= 2L");
    testInvalidExpression("f |= 1.5f");
    testInvalidExpression("f |= 3.14");
    testInvalidExpression("f |= 'a'");
    testInvalidExpression("f |= \"test\"");

    testInvalidExpression("d |= false");
    testInvalidExpression("d |= 1");
    testInvalidExpression("d |= 2L");
    testInvalidExpression("d |= 1.5f");
    testInvalidExpression("d |= 3.14");
    testInvalidExpression("d |= 'a'");
    testInvalidExpression("d |= \"test\"");

    testInvalidExpression("c |= false");
    testValidExpression("c |= 1", ValueFactory.createValue(97));
    testValidExpression("c |= 2L", ValueFactory.createValue(99));
    testInvalidExpression("c |= 1.5f");
    testInvalidExpression("c |= 3.14");
    testValidExpression("c |= 'a'", ValueFactory.createValue(99));
    testInvalidExpression("c |= \"test\"");

    testInvalidExpression("s |= false");
    testInvalidExpression("s |= 1");
    testInvalidExpression("s |= 2L");
    testInvalidExpression("s |= 1.5f");
    testInvalidExpression("s |= 3.14");
    testInvalidExpression("s |= 'a'");
    testInvalidExpression("s |= \"test\"");
  }

  @Test
  public void testPlusEqualsExpression() {
    testInvalidExpression("b += false");
    testInvalidExpression("b += 1");
    testInvalidExpression("b += 2L");
    testInvalidExpression("b += 1.5f");
    testInvalidExpression("b += 3.14");
    testInvalidExpression("b += 'a'");
    testInvalidExpression("b += \"test\"");

    testInvalidExpression("i += false");
    testValidExpression("i += 1", ValueFactory.createValue(2));
    testValidExpression("i += 2L", ValueFactory.createValue(4));
    testValidExpression("i += 1.5f", ValueFactory.createValue(5.5f));
    testValidExpression("i += 3.14", ValueFactory.createValue(8.64));
    testValidExpression("i += 'a'", ValueFactory.createValue(105.64));
    testInvalidExpression("i += \"test\"");

    testInvalidExpression("l += false");
    testValidExpression("l += 1", ValueFactory.createValue(6));
    testValidExpression("l += 2L", ValueFactory.createValue(8));
    testValidExpression("l += 1.5f", ValueFactory.createValue(9.5f));
    testValidExpression("l += 3.14", ValueFactory.createValue(12.64));
    testValidExpression("l += 'a'", ValueFactory.createValue(109.64));
    testInvalidExpression("l += \"test\"");

    testInvalidExpression("f += false");
    testValidExpression("f += 1", ValueFactory.createValue(2.5f));
    testValidExpression("f += 2L", ValueFactory.createValue(4.5f));
    testValidExpression("f += 1.5f", ValueFactory.createValue(6.0f));
    testValidExpression("f += 3.14", ValueFactory.createValue(9.14));
    testValidExpression("f += 'a'", ValueFactory.createValue(106.14));
    testInvalidExpression("f += \"test\"");

    testInvalidExpression("d += false");
    testValidExpression("d += 1", ValueFactory.createValue(4.14));
    testValidExpression("d += 2L", ValueFactory.createValue(6.14));
    testValidExpression("d += 1.5f", ValueFactory.createValue(7.64));
    testValidExpression("d += 3.14", ValueFactory.createValue(10.78));
    testValidExpression("d += 'a'", ValueFactory.createValue(107.78));
    testInvalidExpression("d += \"test\"");

    testInvalidExpression("c += false");
    testValidExpression("c += 1", ValueFactory.createValue(98));
    testValidExpression("c += 2L", ValueFactory.createValue(100));
    testValidExpression("c += 1.5f", ValueFactory.createValue(101.5f));
    testValidExpression("c += 3.14", ValueFactory.createValue(104.64));
    testValidExpression("c += 'a'", ValueFactory.createValue(201.64));
    testInvalidExpression("c += \"test\"");

    testValidExpression("s += false", ValueFactory.createValue("hellofalse"));
    testValidExpression("s += 1", ValueFactory.createValue("hellofalse1"));
    testValidExpression("s += 2L", ValueFactory.createValue("hellofalse12"));
    testValidExpression("s += 1.5f", ValueFactory.createValue("hellofalse121.5"));
    testValidExpression("s += 3.14", ValueFactory.createValue("hellofalse121.53.14"));
    testValidExpression("s += 'a'", ValueFactory.createValue("hellofalse121.53.14a"));
    testValidExpression("s += \"test\"", ValueFactory.createValue("hellofalse121.53.14atest"));
  }

  @Test
  public void testRoofEqualsExpression() {
    testInvalidExpression("b ^= false");
    testInvalidExpression("b ^= 1");
    testInvalidExpression("b ^= 2L");
    testInvalidExpression("b ^= 1.5f");
    testInvalidExpression("b ^= 3.14");
    testInvalidExpression("b ^= 'a'");
    testInvalidExpression("b ^= \"test\"");

    testInvalidExpression("i ^= false");
    testValidExpression("i ^= 3", ValueFactory.createValue(2));
    testValidExpression("i ^= 4L", ValueFactory.createValue(6));
    testInvalidExpression("i ^= 1.5f");
    testInvalidExpression("i ^= 3.14");
    testValidExpression("i ^= 'a'", ValueFactory.createValue(103));
    testInvalidExpression("i ^= \"test\"");

    testInvalidExpression("l ^= false");
    testValidExpression("l ^= 1", ValueFactory.createValue(4));
    testValidExpression("l ^= 2L", ValueFactory.createValue(6));
    testInvalidExpression("l ^= 1.5f");
    testInvalidExpression("l ^= 3.14");
    testValidExpression("l ^= 'a'", ValueFactory.createValue(103));
    testInvalidExpression("l ^= \"test\"");

    testInvalidExpression("f ^= false");
    testInvalidExpression("f ^= 1");
    testInvalidExpression("f ^= 2L");
    testInvalidExpression("f ^= 1.5f");
    testInvalidExpression("f ^= 3.14");
    testInvalidExpression("f ^= 'a'");
    testInvalidExpression("f ^= \"test\"");

    testInvalidExpression("d ^= false");
    testInvalidExpression("d ^= 1");
    testInvalidExpression("d ^= 2L");
    testInvalidExpression("d ^= 1.5f");
    testInvalidExpression("d ^= 3.14");
    testInvalidExpression("d ^= 'a'");
    testInvalidExpression("d ^= \"test\"");

    testInvalidExpression("c ^= false");
    testValidExpression("c ^= 1", ValueFactory.createValue(96));
    testValidExpression("c ^= 2L", ValueFactory.createValue(98));
    testInvalidExpression("c ^= 1.5f");
    testInvalidExpression("c ^= 3.14" );
    testValidExpression("c ^= 'a'", ValueFactory.createValue(3));
    testInvalidExpression("c ^= \"test\"");

    testInvalidExpression("s ^= false");
    testInvalidExpression("s ^= 1");
    testInvalidExpression("s ^= 2L");
    testInvalidExpression("s ^= 1.5f");
    testInvalidExpression("s ^= 3.14");
    testInvalidExpression("s ^= 'a'");
    testInvalidExpression("s ^= \"test\"");
  }

  @Test
  public void testSlashEqualsExpression() {
    testInvalidExpression("b /= false");
    testInvalidExpression("b /= 1");
    testInvalidExpression("b /= 2L");
    testInvalidExpression("b /= 1.5f");
    testInvalidExpression("b /= 3.14");
    testInvalidExpression("b /= 'a'");
    testInvalidExpression("b /= \"test\"");

    testInvalidExpression("i /= false");
    testValidExpression("i /= 0.25f", ValueFactory.createValue(4.f));
    testValidExpression("i /= 0.4", ValueFactory.createValue(10.));
    testValidExpression("i /= 2", ValueFactory.createValue(5));
    testValidExpression("i /= 5L", ValueFactory.createValue(1L));
    testValidExpression("i /= 'A'", ValueFactory.createValue(0.01538461));
    testInvalidExpression("i /= \"test\"");

    testInvalidExpression("l /= false");
    testValidExpression("l /= 1.25f", ValueFactory.createValue(4.f));
    testValidExpression("l /= 0.4", ValueFactory.createValue(10.));
    testValidExpression("l /= 2", ValueFactory.createValue(5));
    testValidExpression("l /= 5L", ValueFactory.createValue(1L));
    testValidExpression("l /= 'A'", ValueFactory.createValue(0.01538461));
    testInvalidExpression("l /= \"test\"");

    testInvalidExpression("f /= false");
    testValidExpression("f /= 3", ValueFactory.createValue(.5f));
    testValidExpression("f /= 2L", ValueFactory.createValue(0.25f));
    testValidExpression("f /= 0.025f", ValueFactory.createValue(10.f));
    testValidExpression("f /= 2.5", ValueFactory.createValue(4));
    testValidExpression("f /= 'A'", ValueFactory.createValue(0.061538f));
    testInvalidExpression("f /= \"test\"");

    testInvalidExpression("d /= false");
    testValidExpression("d /= 1", ValueFactory.createValue(3.14));
    testValidExpression("d /= 2L", ValueFactory.createValue(1.57));
    testValidExpression("d /= 1.57f", ValueFactory.createValue(1.));
    testValidExpression("d /= 0.02", ValueFactory.createValue(50));
    testValidExpression("d /= 'A'", ValueFactory.createValue(0.76923));
    testInvalidExpression("d /= \"test\"");

    testInvalidExpression("c /= false");
    testValidExpression("c /= 1", ValueFactory.createValue(97));
    testValidExpression("c /= 97L", ValueFactory.createValue(1L));
    testValidExpression("c /= 0.25f", ValueFactory.createValue(4));
    testValidExpression("c /= 0.4", ValueFactory.createValue(10));
    testValidExpression("c /= 'A'", ValueFactory.createValue(0.153846));
    testInvalidExpression("c /= \"test\"");

    testInvalidExpression("s /= false");
    testInvalidExpression("s /= 1");
    testInvalidExpression("s /= 2L");
    testInvalidExpression("s /= 1.5f");
    testInvalidExpression("s /= 3.14");
    testInvalidExpression("s /= 'a'");
    testInvalidExpression("s /= \"test\"");
  }

  @Test
  public void testStarEqualsExpression() {
    testInvalidExpression("b *= false");
    testInvalidExpression("b *= 1");
    testInvalidExpression("b *= 2L");
    testInvalidExpression("b *= 1.5f");
    testInvalidExpression("b *= 3.14");
    testInvalidExpression("b *= 'a'");
    testInvalidExpression("b *= \"test\"");

    testInvalidExpression("i *= false");
    testValidExpression("i *= 0.25f", ValueFactory.createValue(0.25f));
    testValidExpression("i *= 4.5", ValueFactory.createValue(1.125));
    testValidExpression("i *= 2", ValueFactory.createValue(2.25));
    testValidExpression("i *= 2L", ValueFactory.createValue(4.5));
    testValidExpression("i *= 'A'", ValueFactory.createValue(292.5));
    testInvalidExpression("i *= \"test\"");

    testInvalidExpression("l *= false");
    testValidExpression("l *= 0.5f", ValueFactory.createValue(2.5f));
    testValidExpression("l *= 0.2", ValueFactory.createValue(0.5));
    testValidExpression("l *= 2", ValueFactory.createValue(1));
    testValidExpression("l *= 10L", ValueFactory.createValue(10L));
    testValidExpression("l *= 'A'", ValueFactory.createValue(650));
    testInvalidExpression("l *= \"test\"");

    testInvalidExpression("f *= false");
    testValidExpression("f *= 3", ValueFactory.createValue(4.5f));
    testValidExpression("f *= 2L", ValueFactory.createValue(9f));
    testValidExpression("f *= 0.5f", ValueFactory.createValue(4.5f));
    testValidExpression("f *= 0.5", ValueFactory.createValue(2.25));
    testValidExpression("f *= 'A'", ValueFactory.createValue(146.25));
    testInvalidExpression("f *= \"test\"");

    testInvalidExpression("d *= false");
    testValidExpression("d *= 1", ValueFactory.createValue(3.14));
    testValidExpression("d *= 2L", ValueFactory.createValue(6.28));
    testValidExpression("d *= 0.5f", ValueFactory.createValue(3.14));
    testValidExpression("d *= 0.5", ValueFactory.createValue(1.57));
    testValidExpression("d *= 'A'", ValueFactory.createValue(102.05));
    testInvalidExpression("d *= \"test\"");

    testInvalidExpression("c *= false");
    testValidExpression("c *= 2", ValueFactory.createValue(194));
    testValidExpression("c *= 2L", ValueFactory.createValue(388L));
    testValidExpression("c *= 0.25f", ValueFactory.createValue(97));
    testValidExpression("c *= 0.5", ValueFactory.createValue(48.5));
    testValidExpression("c *= 'A'", ValueFactory.createValue(3152.5));
    testInvalidExpression("c *= \"test\"");

    testInvalidExpression("s *= false");
    testInvalidExpression("s *= 1");
    testInvalidExpression("s *= 2L");
    testInvalidExpression("s *= 1.5f");
    testInvalidExpression("s *= 3.14");
    testInvalidExpression("s *= 'a'");
    testInvalidExpression("s *= \"test\"");
  }
  
}
