/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.assignmentexpressions._visitor;

import de.monticore.expressions.AbstractExpressionInterpreterTest;
import de.monticore.interpreter.MIValue;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;
import static de.monticore.interpreter.MIValueFactory.createValue;

/**
 * Tests for all AssignmentExpressions with primitive types
 */
public class AssignmentExpressionsInterpreterTest extends AbstractExpressionInterpreterTest {

  protected static Stream<Arguments> incSuffixExpression() {
    return Stream.of(
        arguments("b++", null),
        arguments("by++", createValue((byte)3)),
        arguments("s++", createValue((short)256)),
        arguments("c++", createValue('a')),
        arguments("i++", createValue(1)),
        arguments("l++", createValue(5L)),
        arguments("f++", createValue(1.5f)),
        arguments("d++", createValue(3.14)));
  }

  protected static Stream<Arguments> incPrefixExpression() {
    return Stream.of(
        arguments("++b", null),
        arguments("++by", createValue((byte)4)),
        arguments("++s", createValue((short)257)),
        arguments("++c", createValue('b')),
        arguments("++i", createValue(2)),
        arguments("++l", createValue(6L)),
        arguments("++f", createValue(2.5f)),
        arguments("++d", createValue(4.14)));
  }

  protected static Stream<Arguments> decSuffixExpression() {
    return Stream.of(
        arguments("b--", null),
        arguments("by--", createValue((byte)3)),
        arguments("s--", createValue((short)256)),
        arguments("c--", createValue('a')),
        arguments("i--", createValue(1)),
        arguments("l--", createValue(5L)),
        arguments("f--", createValue(1.5f)),
        arguments("d--", createValue(3.14)));
  }

  protected static Stream<Arguments> decPrefixExpression() {
    return Stream.of(
        arguments("--b", null),
        arguments("--by", createValue((byte)2)),
        arguments("--s", createValue((short)255)),
        arguments("--c", createValue('`')),
        arguments("--i", createValue(0)),
        arguments("--l", createValue(4L)),
        arguments("--f", createValue(0.5f)),
        arguments("--d", createValue(2.14)));
  }

  protected static Stream<Arguments> andEqualsExpression() {
    return Stream.of(
        arguments("b &= true", createValue(true)),
        arguments("b &= (byte)3", null),
        arguments("b &= (short)256", null),
        arguments("b &= 'c'", null),
        arguments("b &= 1", null),
        arguments("b &= 2L", null),
        arguments("b &= 1.5f", null),
        arguments("b &= 3.14", null),

        arguments("by &= false", null),
        arguments("by &= (byte)3", createValue((byte)3)),
        arguments("by &= (short)256", createValue((byte)256)),
        arguments("by &= 'a'", createValue((byte)1)),
        arguments("by &= 1", createValue((byte)1)),
        arguments("by &= 2L", createValue((byte)2L)),
        arguments("by &= 1.5f", null),
        arguments("by &= 3.14", null),

        arguments("s &= false", null),
        arguments("s &= (byte)3", createValue((short)0)),
        arguments("s &= (short)256", createValue((short)256)),
        arguments("s &= 'a'", createValue((short)0)),
        arguments("s &= 1", createValue((short)0)),
        arguments("s &= 2L", createValue((short)0L)),
        arguments("s &= 1.5f", null),
        arguments("s &= 3.14", null),

        arguments("c &= false", null),
        arguments("c &= (byte)3", createValue((char)1)),
        arguments("c &= (short)256", createValue((char)0)),
        arguments("c &= 'a'", createValue('a')),
        arguments("c &= 1", createValue((char)1)),
        arguments("c &= 2L", createValue((char)0L)),
        arguments("c &= 1.5f", null),
        arguments("c &= 3.14", null),

        arguments("i &= false", null),
        arguments("i &= (byte)3", createValue(1)),
        arguments("i &= (short)256", createValue(0)),
        arguments("i &= 'a'", createValue(1)),
        arguments("i &= 1", createValue(1)),
        arguments("i &= 2L", createValue(0)),
        arguments("i &= 1.5f", null),
        arguments("i &= 3.14", null),

        arguments("l &= false", null),
        arguments("l &= (byte)3", createValue(1L)),
        arguments("l &= (short)256", createValue(0L)),
        arguments("l &= 'a'", createValue(1L)),
        arguments("l &= 1", createValue(1L)),
        arguments("l &= 4L", createValue(4L)),
        arguments("l &= 1.5f", null),
        arguments("l &= 3.14", null),

        arguments("f &= false", null),
        arguments("f &= (byte)3", null),
        arguments("f &= (short)256", null),
        arguments("f &= 1", null),
        arguments("f &= 2L", null),
        arguments("f &= 1.5f", null),
        arguments("f &= 3.14", null),
        arguments("f &= 'a'", null),

        arguments("d &= false", null),
        arguments("d &= (byte)3", null),
        arguments("d &= (short)256", null),
        arguments("d &= 'a'", null),
        arguments("d &= 1", null),
        arguments("d &= 2L", null),
        arguments("d &= 1.5f", null),
        arguments("d &= 3.14", null)
    );
  }

  protected static Stream<Arguments> gTGTEqualsExpression() {
    return Stream.of(
        arguments("b >>= false", null),
        arguments("b >>= (byte)3", null),
        arguments("b >>= (short)256", null),
        arguments("b >>= 'a'", null),
        arguments("b >>= 1", null),
        arguments("b >>= 2L", null),
        arguments("b >>= 1.5f", null),
        arguments("b >>= 3.14", null),

        arguments("by >>= false", null),
        arguments("by >>= (byte)3", createValue((byte)0)),
        arguments("by >>= (short)256", createValue((byte)3)),
        arguments("by >>= 'a'", createValue((byte)1)),
        arguments("by >>= 1", createValue((byte)1)),
        arguments("by >>= 2L", createValue((byte)0)),
        arguments("by >>= 1.5f", null),
        arguments("by >>= 3.14", null),

        arguments("s >>= false", null),
        arguments("s >>= (byte)3", createValue((short)32)),
        arguments("s >>= (short)256", createValue((short)256)),
        arguments("s >>= 'a'", createValue((short)128)),
        arguments("s >>= 1", createValue((short)128)),
        arguments("s >>= 2L", createValue((short)64)),
        arguments("s >>= 1.5f", null),
        arguments("s >>= 3.14", null),

        arguments("c >>= false", null),
        arguments("c >>= (byte)3", createValue((char)12)),
        arguments("c >>= (short)256", createValue('a')),
        arguments("c >>= 'a'", createValue((char)48)),
        arguments("c >>= 1", createValue((char)48)),
        arguments("c >>= 2L", createValue((char)24)),
        arguments("c >>= 1.5f", null),
        arguments("c >>= 3.14", null),

        arguments("i >>= false", null),
        arguments("i >>= (byte)3", createValue(0)),
        arguments("i >>= (short)256", createValue(1)),
        arguments("i >>= 'a'", createValue(0)),
        arguments("i >>= 1", createValue(0)),
        arguments("i >>= 2L", createValue(0)),
        arguments("i >>= 1.5f", null),
        arguments("i >>= 3.14", null),

        arguments("l >>= false", null),
        arguments("l >>= (byte)3", createValue(0L)),
        arguments("l >>= (short)256", createValue(5L)),
        arguments("l >>= 1", createValue(2L)),
        arguments("l >>= 2L", createValue(1L)),
        arguments("l >>= 1.5f", null),
        arguments("l >>= 3.14", null),
        arguments("l >>= 'a'", createValue(0L)),

        arguments("f >>= false", null),
        arguments("f >>= (byte)3", null),
        arguments("f >>= (short)256", null),
        arguments("f >>= 'a'", null),
        arguments("f >>= 1", null),
        arguments("f >>= 2L", null),
        arguments("f >>= 1.5f", null),
        arguments("f >>= 3.14", null),

        arguments("d >>= false", null),
        arguments("d >>= (byte)3", null),
        arguments("d >>= (short)256", null),
        arguments("d >>= 'a'", null),
        arguments("d >>= 1", null),
        arguments("d >>= 2L", null),
        arguments("d >>= 1.5f", null),
        arguments("d >>= 3.14", null));

  }

  protected static Stream<Arguments> gTGTGTEqualsExpression() {
    return Stream.of(
        arguments("b >>>= false", null),
        arguments("b >>>= (byte)3", null),
        arguments("b >>>= (short)256", null),
        arguments("b >>>= 'a'", null),
        arguments("b >>>= 1", null),
        arguments("b >>>= 2L", null),
        arguments("b >>>= 1.5f", null),
        arguments("b >>>= 3.14", null),

        arguments("by >>>= false", null),
        arguments("by >>>= (byte)3", createValue((byte)0)),
        arguments("by >>>= (short)256", createValue((byte)3)),
        arguments("by >>>= 'a'", createValue((byte)1)),
        arguments("by >>>= 1", createValue((byte)1)),
        arguments("by >>>= 2L", createValue((byte)0)),
        arguments("by >>>= 1.5f", null),
        arguments("by >>>= 3.14", null),

        arguments("s >>>= false", null),
        arguments("s >>>= (byte)3", createValue((short)32)),
        arguments("s >>>= (short)256", createValue((short)256)),
        arguments("s >>>= 'a'", createValue((short)128)),
        arguments("s >>>= 1", createValue((short)128)),
        arguments("s >>>= 2L", createValue((short)64)),
        arguments("s >>>= 1.5f", null),
        arguments("s >>>= 3.14", null),

        arguments("c >>>= false", null),
        arguments("c >>>= (byte)3", createValue((char)12)),
        arguments("c >>>= (short)256", createValue('a')),
        arguments("c >>>= 'a'", createValue((char)48)),
        arguments("c >>>= 1", createValue((char)48)),
        arguments("c >>>= 2L", createValue((char)24)),
        arguments("c >>>= 1.5f", null),
        arguments("c >>>= 3.14", null),

        arguments("i >>>= false", null),
        arguments("i >>>= (byte)3", createValue(0)),
        arguments("i >>>= (short)256", createValue(1)),
        arguments("i >>>= 'a'", createValue(0)),
        arguments("i >>>= 1", createValue(0)),
        arguments("i >>>= 2L", createValue(0)),
        arguments("i >>>= 1.5f", null),
        arguments("i >>>= 3.14", null),

        arguments("l >>>= false", null),
        arguments("l >>>= (byte)3", createValue(0L)),
        arguments("l >>>= (short)256", createValue(5L)),
        arguments("l >>>= 'a'", createValue(0L)),
        arguments("l >>>= 1", createValue(2L)),
        arguments("l >>>= 2L", createValue(1L)),
        arguments("l >>>= 1.5f", null),
        arguments("l >>>= 3.14", null),

        arguments("f >>>= false", null),
        arguments("f >>>= (byte)3", null),
        arguments("f >>>= (short)256", null),
        arguments("f >>>= 'a'", null),
        arguments("f >>>= 1", null),
        arguments("f >>>= 2L", null),
        arguments("f >>>= 1.5f", null),
        arguments("f >>>= 3.14", null),

        arguments("d >>>= false", null),
        arguments("d >>>= (byte)3", null),
        arguments("d >>>= (short)256", null),
        arguments("d >>>= 'a'", null),
        arguments("d >>>= 1", null),
        arguments("d >>>= 2L", null),
        arguments("d >>>= 1.5f", null),
        arguments("d >>>= 3.14", null)
    );
  }

  protected static Stream<Arguments> lTLTEqualsExpression() {
    return Stream.of(
        arguments("b <<= false", null),
        arguments("b <<= (byte)3", null),
        arguments("b <<= (short)256", null),
        arguments("b <<= 'a'", null),
        arguments("b <<= 1", null),
        arguments("b <<= 2L", null),
        arguments("b <<= 1.5f", null),
        arguments("b <<= 3.14", null),

        arguments("by <<= false", null),
        arguments("by <<= (byte)3", createValue((byte)24)),
        arguments("by <<= (short)256", createValue((byte)3)),
        arguments("by <<= 'a'", createValue((byte)6)),
        arguments("by <<= 1", createValue((byte)6)),
        arguments("by <<= 2L", createValue((byte)12)),
        arguments("by <<= 1.5f", null),
        arguments("by <<= 3.14", null),

        arguments("s <<= false", null),
        arguments("s <<= (byte)3", createValue((short)2048)),
        arguments("s <<= (short)256", createValue((short)256)),
        arguments("s <<= 'a'", createValue((short)512)),
        arguments("s <<= 1", createValue((short)512)),
        arguments("s <<= 2L", createValue((short)1024)),
        arguments("s <<= 1.5f", null),
        arguments("s <<= 3.14", null),

        arguments("c <<= false", null),
        arguments("c <<= (byte)3", createValue((char)776)),
        arguments("c <<= (short)256", createValue('a')),
        arguments("c <<= 'a'", createValue((char)194)),
        arguments("c <<= 1", createValue((char)194)),
        arguments("c <<= 2L", createValue((char)388)),
        arguments("c <<= 1.5f", null),
        arguments("c <<= 3.14", null),

        arguments("i <<= false", null),
        arguments("i <<= (byte)3", createValue(8)),
        arguments("i <<= (short)256", createValue(1)),
        arguments("i <<= 'a'", createValue(2)),
        arguments("i <<= 1", createValue(2)),
        arguments("i <<= 2L", createValue(4)),
        arguments("i <<= 1.5f", null),
        arguments("i <<= 3.14", null),

        arguments("l <<= false", null),
        arguments("l <<= (byte)3", createValue(40L)),
        arguments("l <<= (short)256", createValue(5L)),
        arguments("l <<= 'a'", createValue(42949672960L)),
        arguments("l <<= 1", createValue(10L)),
        arguments("l <<= 2L", createValue(20L)),
        arguments("l <<= 1.5f", null),
        arguments("l <<= 3.14", null),

        arguments("f <<= false", null),
        arguments("f <<= (byte)3", null),
        arguments("f <<= (short)256", null),
        arguments("f <<= 'a'", null),
        arguments("f <<= 1", null),
        arguments("f <<= 2L", null),
        arguments("f <<= 1.5f", null),
        arguments("f <<= 3.14", null),

        arguments("d <<= false", null),
        arguments("d <<= (byte)3", null),
        arguments("d <<= (short)256", null),
        arguments("d <<= 'a'", null),
        arguments("d <<= 1", null),
        arguments("d <<= 2L", null),
        arguments("d <<= 1.5f", null),
        arguments("d <<= 3.14", null)
    );
  }

  protected static Stream<Arguments> minusEqualsExpression() {
    return Stream.of(
        arguments("b -= false", null),
        arguments("b -= (byte)3", null),
        arguments("b -= (short)256", null),
        arguments("b -= 'a'", null),
        arguments("b -= 1", null),
        arguments("b -= 2L", null),
        arguments("b -= 1.5f", null),
        arguments("b -= 3.14", null),

        arguments("by -= false", null),
        arguments("by -= (byte)3", createValue((byte)0)),
        arguments("by -= (short)256", createValue((byte)3)),
        arguments("by -= 'a'", createValue((byte)-94)),
        arguments("by -= 1", createValue((byte)2)),
        arguments("by -= 2L", createValue((byte)1)),
        arguments("by -= 1.5f", createValue((byte)1)),
        arguments("by -= 3.14", createValue((byte)0)),

        arguments("s -= false", null),
        arguments("s -= (byte)3", createValue((short)253)),
        arguments("s -= (short)256", createValue((short)0)),
        arguments("s -= 'a'", createValue((short)159)),
        arguments("s -= 1", createValue((short)255)),
        arguments("s -= 2L", createValue((short)254)),
        arguments("s -= 1.5f", createValue((short)254)),
        arguments("s -= 3.14", createValue((short)252)),

        arguments("c -= false", null),
        arguments("c -= (byte)3", createValue((char)94)),
        arguments("c -= (short)256", createValue((char)-159)),
        arguments("c -= 'a'", createValue((char)0)),
        arguments("c -= 1", createValue((char)96)),
        arguments("c -= 2L", createValue((char)95)),
        arguments("c -= 1.5f", createValue((char)95.5f)),
        arguments("c -= 3.14", createValue((char)93.86)),

        arguments("i -= false", null),
        arguments("i -= (byte)3", createValue(-2)),
        arguments("i -= (short)256", createValue(-255)),
        arguments("i -= 'a'", createValue(-96)),
        arguments("i -= 1", createValue(0)),
        arguments("i -= 2L", createValue(-1)),
        arguments("i -= 1.5f", createValue(0)),
        arguments("i -= 3.14", createValue(-2)),

        arguments("l -= false", null),
        arguments("l -= (byte)3", createValue(2L)),
        arguments("l -= (short)256", createValue(-251L)),
        arguments("l -= 'a'", createValue(-92L)),
        arguments("l -= 1", createValue(4L)),
        arguments("l -= 2L", createValue(3L)),
        arguments("l -= 1.5f", createValue(3L)),
        arguments("l -= 3.14", createValue(1L)),

        arguments("f -= false", null),
        arguments("f -= (byte)3", createValue(-1.5f)),
        arguments("f -= (short)256", createValue(-254.5f)),
        arguments("f -= 'a'", createValue(-95.5f)),
        arguments("f -= 1", createValue(0.5f)),
        arguments("f -= 2L", createValue(-0.5f)),
        arguments("f -= 1.2f", createValue(.3f)),
        arguments("f -= 3.14", createValue(-1.64f)),

        arguments("d -= false", null),
        arguments("d -= (byte)3", createValue(0.14)),
        arguments("d -= (short)256", createValue(-252.86)),
        arguments("d -= 'a'", createValue(-93.86)),
        arguments("d -= 1", createValue(2.14)),
        arguments("d -= 2L", createValue(1.14)),
        arguments("d -= 1.5f", createValue(1.64)),
        arguments("d -= 3.04", createValue(.1))
    );
  }

  protected static Stream<Arguments> percentEqualsExpression() {
    return Stream.of(
        arguments("b %= false", null),
        arguments("b %= (byte)3", null),
        arguments("b %= (short)256", null),
        arguments("b %= 'a'", null),
        arguments("b %= 1", null),
        arguments("b %= 2L", null),
        arguments("b %= 1.5f", null),
        arguments("b %= 3.14", null),

        arguments("by %= false", null),
        arguments("by %= (byte)3", createValue((byte)0)),
        arguments("by %= (short)256", createValue((byte)3)),
        arguments("by %= 'a'", createValue((byte)3)),
        arguments("by %= 1", createValue((byte)0)),
        arguments("by %= 2L", createValue((byte)1)),
        arguments("by %= 1.5f", createValue((byte)0)),
        arguments("by %= 3.14", createValue((byte)3)),

        arguments("s %= false", null),
        arguments("s %= (byte)3", createValue((short)1)),
        arguments("s %= (short)256", createValue((short)0)),
        arguments("s %= 'a'", createValue((short)62)),
        arguments("s %= 1", createValue((short)0)),
        arguments("s %= 2L", createValue((short)0)),
        arguments("s %= 1.5f", createValue((short)1)),
        arguments("s %= 3.14", createValue((short)1)),

        arguments("c %= false", null),
        arguments("c %= (byte)3", createValue((char)1)),
        arguments("c %= (short)256", createValue('a')),
        arguments("c %= 'a'", createValue((char)0)),
        arguments("c %= 1", createValue((char)0)),
        arguments("c %= 2L", createValue((char)1)),
        arguments("c %= 1.5f", createValue((char)1)),
        arguments("c %= 3.14", createValue((char)2)),

        arguments("i %= false", null),
        arguments("i %= (byte)3", createValue(1)),
        arguments("i %= (short)256", createValue(1)),
        arguments("i %= 'a'", createValue(1)),
        arguments("i %= 1", createValue(0)),
        arguments("i %= 2L", createValue(1)),
        arguments("i %= 1.5f", createValue(1)),
        arguments("i %= 3.14", createValue(1)),

        arguments("l %= false", null),
        arguments("l %= (byte)3", createValue(2L)),
        arguments("l %= (short)256", createValue(5L)),
        arguments("l %= 'a'", createValue(5L)),
        arguments("l %= 1", createValue(0L)),
        arguments("l %= 4L", createValue(1L)),
        arguments("l %= 1.5f", createValue(0L)),
        arguments("l %= 3.14", createValue(1L)),

        arguments("f %= false", null),
        arguments("f %= (byte)3", createValue(1.5f)),
        arguments("f %= (short)256", createValue(1.5f)),
        arguments("f %= 'a'", createValue(1.5f)),
        arguments("f %= 1", createValue(0.5f)),
        arguments("f %= 2L", createValue(1.5f)),
        arguments("f %= 1.5f", createValue(0.0f)),
        arguments("f %= 3.14", createValue(1.5f)),

        arguments("d %= false", null),
        arguments("d %= (byte)3", createValue(0.14)),
        arguments("d %= (short)256", createValue(3.14)),
        arguments("d %= 'a'", createValue(3.14)),
        arguments("d %= 1", createValue(0.14)),
        arguments("d %= 2L", createValue(1.14)),
        arguments("d %= 1.5f", createValue(0.14)),
        arguments("d %= 3.04", createValue(0.1))
    );
  }

  protected static Stream<Arguments> pipeEqualsExpression() {
    return Stream.of(
        arguments("b |= true", createValue(true)),
        arguments("b |= (byte)3", null),
        arguments("b |= (short)256", null),
        arguments("b |= 'c'", null),
        arguments("b |= 1", null),
        arguments("b |= 2L", null),
        arguments("b |= 1.5f", null),
        arguments("b |= 3.14", null),

        arguments("by |= false", null),
        arguments("by |= (byte)3", createValue((byte)3)),
        arguments("by |= (short)256", createValue((byte)259)),
        arguments("by |= 'a'", createValue((byte)99)),
        arguments("by |= 1", createValue((byte)3)),
        arguments("by |= 2L", createValue((byte)3)),
        arguments("by |= 1.5f", null),
        arguments("by |= 3.14", null),

        arguments("s |= false", null),
        arguments("s |= (byte)3", createValue((short)259)),
        arguments("s |= (short)256", createValue((short)256)),
        arguments("s |= 'a'", createValue((short)353)),
        arguments("s |= 1", createValue((short)257)),
        arguments("s |= 2L", createValue((short)258)),
        arguments("s |= 1.5f", null),
        arguments("s |= 3.14", null),

        arguments("c |= false", null),
        arguments("c |= (byte)3", createValue((char)99)),
        arguments("c |= (short)256", createValue((char)353)),
        arguments("c |= 'a'", createValue('a')),
        arguments("c |= 1", createValue((char)97)),
        arguments("c |= 2L", createValue((char)99)),
        arguments("c |= 1.5f", null),
        arguments("c |= 3.14", null),

        arguments("i |= false", null),
        arguments("i |= (byte)3", createValue(3)),
        arguments("i |= (short)256", createValue(257)),
        arguments("i |= 'a'", createValue(97)),
        arguments("i |= 1", createValue(1)),
        arguments("i |= 2L", createValue(3)),
        arguments("i |= 1.5f", null),
        arguments("i |= 3.14", null),

        arguments("l |= false", null),
        arguments("l |= (byte)3", createValue(7L)),
        arguments("l |= (short)256", createValue(261L)),
        arguments("l |= 'a'", createValue(101L)),
        arguments("l |= 1", createValue(5L)),
        arguments("l |= 4L", createValue(5L)),
        arguments("l |= 1.5f", null),
        arguments("l |= 3.14", null),

        arguments("f |= false", null),
        arguments("f |= (byte)3", null),
        arguments("f |= (short)256", null),
        arguments("f |= 'a'", null),
        arguments("f |= 1", null),
        arguments("f |= 2L", null),
        arguments("f |= 1.5f", null),
        arguments("f |= 3.14", null),

        arguments("d |= false", null),
        arguments("d |= (byte)3", null),
        arguments("d |= (short)256", null),
        arguments("d |= 'a'", null),
        arguments("d |= 1", null),
        arguments("d |= 2L", null),
        arguments("d |= 1.5f", null),
        arguments("d |= 3.14", null)
    );
  }

  protected static Stream<Arguments> plusEqualsExpression() {
    return Stream.of(
        arguments("b += false", null),
        arguments("b += (byte)3", null),
        arguments("b += (short)256", null),
        arguments("b += 1", null),
        arguments("b += 2L", null),
        arguments("b += 1.5f", null),
        arguments("b += 3.14", null),
        arguments("b += 'a'", null),

        arguments("by += false", null),
        arguments("by += (byte)3", createValue((byte)6)),
        arguments("by += (short)256", createValue((byte)3)),
        arguments("by += 'a'", createValue((byte)100)),
        arguments("by += 1", createValue((byte)4)),
        arguments("by += 2L", createValue((byte)5)),
        arguments("by += 1.5f", createValue((byte)4)),
        arguments("by += 3.14", createValue((byte)6)),

        arguments("s += false", null),
        arguments("s += (byte)3", createValue((short)259)),
        arguments("s += (short)256", createValue((short)512)),
        arguments("s += 'a'", createValue((short)353)),
        arguments("s += 1", createValue((short)257)),
        arguments("s += 2L", createValue((short)258)),
        arguments("s += 1.5f", createValue((short)257)),
        arguments("s += 3.14", createValue((short)259)),

        arguments("c += false", null),
        arguments("c += (byte)3", createValue('d')),
        arguments("c += (short)256", createValue((char)353)),
        arguments("c += 'a'", createValue((char)194)),
        arguments("c += 1", createValue((char)98)),
        arguments("c += 2L", createValue((char)99)),
        arguments("c += 1.5f", createValue((char)98)),
        arguments("c += 3.14", createValue((char)100)),

        arguments("i += false", null),
        arguments("i += (byte)3", createValue(4)),
        arguments("i += (short)256", createValue(257)),
        arguments("i += 'a'", createValue(98)),
        arguments("i += 1", createValue(2)),
        arguments("i += 2L", createValue(3)),
        arguments("i += 1.5f", createValue(2)),
        arguments("i += 3.14", createValue(4)),

        arguments("l += false", null),
        arguments("l += (byte)3", createValue(8L)),
        arguments("l += (short)256", createValue(261L)),
        arguments("l += 'a'", createValue(102L)),
        arguments("l += 1", createValue(6L)),
        arguments("l += 2L", createValue(7L)),
        arguments("l += 1.5f", createValue(6L)),
        arguments("l += 3.14", createValue(8L)),

        arguments("f += false", null),
        arguments("f += (byte)3", createValue(4.5f)),
        arguments("f += (short)256", createValue(257.5f)),
        arguments("f += 1", createValue(2.5f)),
        arguments("f += 2L", createValue(3.5f)),
        arguments("f += 1.5f", createValue(3.0f)),
        arguments("f += 3.14", createValue(4.64f)),
        arguments("f += 'a'", createValue(98.5f)),

        arguments("d += false", null),
        arguments("d += (byte)3", createValue(6.14)),
        arguments("d += (short)256", createValue(259.14)),
        arguments("d += 'a'", createValue(100.14)),
        arguments("d += 1", createValue(4.14)),
        arguments("d += 2L", createValue(5.14)),
        arguments("d += 1.5f", createValue(4.64)),
        arguments("d += 3.14", createValue(6.28))
    );
  }

  protected static Stream<Arguments> roofEqualsExpression() {
    return Stream.of(
        arguments("b ^= false", createValue(true)),
        arguments("b ^= (byte)3", null),
        arguments("b ^= (short)256", null),
        arguments("b ^= 'c'", null),
        arguments("b ^= 1", null),
        arguments("b ^= 2L", null),
        arguments("b ^= 1.5f", null),
        arguments("b ^= 3.14", null),

        arguments("by ^= false", null),
        arguments("by ^= (byte)3", createValue((byte)0)),
        arguments("by ^= (short)256", createValue((byte)259)),
        arguments("by ^= 'a'", createValue((byte)98)),
        arguments("by ^= 1", createValue((byte)2)),
        arguments("by ^= 2L", createValue((byte)1)),
        arguments("by ^= 1.5f", null),
        arguments("by ^= 3.14", null),

        arguments("s ^= false", null),
        arguments("s ^= (byte)3", createValue((short)259)),
        arguments("s ^= (short)256", createValue((short)0)),
        arguments("s ^= 'a'", createValue((short)353)),
        arguments("s ^= 1", createValue((short)257)),
        arguments("s ^= 2L", createValue((short)258)),
        arguments("s ^= 1.5f", null),
        arguments("s ^= 3.14", null),

        arguments("c ^= false", null),
        arguments("c ^= (byte)3", createValue((char)98)),
        arguments("c ^= (short)256", createValue((char)353)),
        arguments("c ^= 'a'", createValue((char)0)),
        arguments("c ^= 1", createValue((char)96)),
        arguments("c ^= 2L", createValue((char)99)),
        arguments("c ^= 1.5f", null),
        arguments("c ^= 3.14", null),

        arguments("i ^= false", null),
        arguments("i ^= (byte)3", createValue(2)),
        arguments("i ^= (short)256", createValue(257)),
        arguments("i ^= 'a'", createValue(96)),
        arguments("i ^= 1", createValue(0)),
        arguments("i ^= 2L", createValue(3)),
        arguments("i ^= 1.5f", null),
        arguments("i ^= 3.14", null),

        arguments("l ^= false", null),
        arguments("l ^= (byte)3", createValue(6L)),
        arguments("l ^= (short)256", createValue(261L)),
        arguments("l ^= 'a'", createValue(100L)),
        arguments("l ^= 1", createValue(4L)),
        arguments("l ^= 4L", createValue(1L)),
        arguments("l ^= 1.5f", null),
        arguments("l ^= 3.14", null),

        arguments("f ^= false", null),
        arguments("f ^= (byte)3", null),
        arguments("f ^= (short)256", null),
        arguments("f ^= 'a'", null),
        arguments("f ^= 1", null),
        arguments("f ^= 2L", null),
        arguments("f ^= 1.5f", null),
        arguments("f ^= 3.14", null),

        arguments("d ^= false", null),
        arguments("d ^= (byte)3", null),
        arguments("d ^= (short)256", null),
        arguments("d ^= 'a'", null),
        arguments("d ^= 1", null),
        arguments("d ^= 2L", null),
        arguments("d ^= 1.5f", null),
        arguments("d ^= 3.14", null)
    );
  }

  protected static Stream<Arguments> slashEqualsExpression() {
    return Stream.of(
        arguments("b /= false", null),
        arguments("b /= (byte)3", null),
        arguments("b /= (short)256", null),
        arguments("b /= 'a'", null),
        arguments("b /= 1", null),
        arguments("b /= 2L", null),
        arguments("b /= 1.5f", null),
        arguments("b /= 3.14", null),

        arguments("by /= false", null),
        arguments("by /= (byte)3", createValue((byte)1)),
        arguments("by /= (short)256", createValue((byte)0)),
        arguments("by /= 'a'", createValue((byte)0)),
        arguments("by /= 1", createValue((byte)3)),
        arguments("by /= 2L", createValue((byte)1)),
        arguments("by /= 1.5f", createValue((byte)2)),
        arguments("by /= 3.14", createValue((byte)0)),

        arguments("s /= false", null),
        arguments("s /= (byte)3", createValue((short)85)),
        arguments("s /= (short)256", createValue((short)1)),
        arguments("s /= 'a'", createValue((short)2)),
        arguments("s /= 1", createValue((short)256)),
        arguments("s /= 2L", createValue((short)128)),
        arguments("s /= 1.5f", createValue((short)170)),
        arguments("s /= 3.14", createValue((short)81)),

        arguments("c /= false", null),
        arguments("c /= (byte)3", createValue((char)32)),
        arguments("c /= (short)256", createValue((char)0)),
        arguments("c /= 'a'", createValue((char)1)),
        arguments("c /= 1", createValue('a')),
        arguments("c /= 2L", createValue((char)48)),
        arguments("c /= 1.5f", createValue((char)64)),
        arguments("c /= 3.14", createValue((char)30)),

        arguments("i /= false", null),
        arguments("i /= (byte)3", createValue(0)),
        arguments("i /= (short)256", createValue(0)),
        arguments("i /= 'a'", createValue(0)),
        arguments("i /= 1", createValue(1)),
        arguments("i /= 2L", createValue(0)),
        arguments("i /= 1.5f", createValue(0)),
        arguments("i /= 3.14", createValue(0)),

        arguments("l /= false", null),
        arguments("l /= (byte)3", createValue(1L)),
        arguments("l /= (short)256", createValue(0L)),
        arguments("l /= 'a'", createValue(0L)),
        arguments("l /= 1", createValue(5L)),
        arguments("l /= 2L", createValue(2L)),
        arguments("l /= 1.5f", createValue(3L)),
        arguments("l /= 3.14", createValue(1L)),

        arguments("f /= false", null),
        arguments("f /= (byte)3", createValue(0.5f)),
        arguments("f /= (short)256", createValue(0.005859375f)),
        arguments("f /= 'a'", createValue(0.0154639175f)),
        arguments("f /= 1", createValue(1.5f)),
        arguments("f /= 2L", createValue(0.75f)),
        arguments("f /= 1.5f", createValue(1.0f)),
        arguments("f /= 3.14", createValue(0.477707f)),

        arguments("d /= false", null),
        arguments("d /= (byte)3", createValue(1.046666666)),
        arguments("d /= (short)256", createValue(0.012265625)),
        arguments("d /= 'a'", createValue(0.032371134)),
        arguments("d /= 1", createValue(3.14)),
        arguments("d /= 2L", createValue(1.57)),
        arguments("d /= 1.5f", createValue(2.09333333)),
        arguments("d /= 3.14", createValue(1.0))
    );
  }

  protected static Stream<Arguments> starEqualsExpression() {
    return Stream.of(
        arguments("b *= false", null),
        arguments("b *= (byte)3", null),
        arguments("b *= (short)256", null),
        arguments("b *= 'a'", null),
        arguments("b *= 1", null),
        arguments("b *= 2L", null),
        arguments("b *= 1.5f", null),
        arguments("b *= 3.14", null),

        arguments("by *= false", null),
        arguments("by *= (byte)3", createValue((byte)9)),
        arguments("by *= (short)256", createValue((byte)0)),
        arguments("by *= 'a'", createValue((byte)35)),
        arguments("by *= 1", createValue((byte)3)),
        arguments("by *= 2L", createValue((byte)6)),
        arguments("by *= 1.5f", createValue((byte)4)),
        arguments("by *= 3.14", createValue((byte)9)),

        arguments("s *= false", null),
        arguments("s *= (byte)3", createValue((short)768)),
        arguments("s *= (short)256", createValue((short)0)),
        arguments("s *= 'a'", createValue((short)24832)),
        arguments("s *= 1", createValue((short)256)),
        arguments("s *= 2L", createValue((short)512)),
        arguments("s *= 1.5f", createValue((short)384)),
        arguments("s *= 3.14", createValue((short)803)),

        arguments("c *= false", null),
        arguments("c *= (byte)3", createValue((char)291)),
        arguments("c *= (short)256", createValue((char)24832)),
        arguments("c *= 'a'", createValue((char)9409)),
        arguments("c *= 1", createValue((char)97)),
        arguments("c *= 2L", createValue((char)194)),
        arguments("c *= 1.5f", createValue((char)145.5f)),
        arguments("c *= 3.14", createValue((char)304.58)),

        arguments("i *= false", null),
        arguments("i *= (byte)3", createValue(3)),
        arguments("i *= (short)256", createValue(256)),
        arguments("i *= 'a'", createValue(97)),
        arguments("i *= 1", createValue(1)),
        arguments("i *= 2L", createValue(2)),
        arguments("i *= 1.5f", createValue(1)),
        arguments("i *= 3.14", createValue(3)),

        arguments("l *= false", null),
        arguments("l *= (byte)3", createValue(15L)),
        arguments("l *= (short)256", createValue(1280L)),
        arguments("l *= 'a'", createValue(485L)),
        arguments("l *= 1", createValue(5L)),
        arguments("l *= 2L", createValue(10L)),
        arguments("l *= 1.5f", createValue(7L)),
        arguments("l *= 3.14", createValue(15L)),

        arguments("f *= false", null),
        arguments("f *= (byte)3", createValue(4.5f)),
        arguments("f *= (short)256", createValue(384.0f)),
        arguments("f *= 'a'", createValue(145.5f)),
        arguments("f *= 1", createValue(1.5f)),
        arguments("f *= 2L", createValue(3.0f)),
        arguments("f *= 1.5f", createValue(2.25f)),
        arguments("f *= 3.14", createValue(4.71f)),

        arguments("d *= false", null),
        arguments("d *= (byte)3", createValue(9.42)),
        arguments("d *= (short)256", createValue(803.84)),
        arguments("d *= 'a'", createValue(304.58)),
        arguments("d *= 1", createValue(3.14)),
        arguments("d *= 2L", createValue(6.28)),
        arguments("d *= 1.5f", createValue(4.71)),
        arguments("d *= 3.14", createValue(9.8596))
    );
  }

  @ParameterizedTest
  @MethodSource({
      "incSuffixExpression", "incPrefixExpression", "decSuffixExpression",
      "decPrefixExpression", "andEqualsExpression", "gTGTEqualsExpression",
      "gTGTGTEqualsExpression", "lTLTEqualsExpression", "minusEqualsExpression",
      "percentEqualsExpression", "pipeEqualsExpression", "plusEqualsExpression",
          "roofEqualsExpression", "slashEqualsExpression", "starEqualsExpression"
  })
  public void testInterpreter(String expression, MIValue result) {
      if (result == null) {
          testInvalidExpression(expression);
      } else {
          testValidExpression(expression, result);
      }
  }
}
