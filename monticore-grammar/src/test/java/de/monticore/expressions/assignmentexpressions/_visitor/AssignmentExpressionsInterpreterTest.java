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
        arguments("f &= 1", null),
        arguments("f &= 2L", null),
        arguments("f &= 1.5f", null),
        arguments("f &= 3.14", null),
        arguments("f &= 'a'", null),

        arguments("d &= false", null),
        arguments("d &= 1", null),
        arguments("d &= 2L", null),
        arguments("d &= 1.5f", null),
        arguments("d &= 3.14", null),
        arguments("d &= 'a'", null));
  }

  protected static Stream<Arguments> gTGTEqualsExpression() {
    return Stream.of(
        arguments("b >>= false", null),
        arguments("b >>= 1", null),
        arguments("b >>= 2L", null),
        arguments("b >>= 1.5f", null),
        arguments("b >>= 3.14", null),
        arguments("b >>= 'a'", null),

        arguments("i >>= false", null),
        arguments("i >>= 1", createValue(0)),
        arguments("i >>= 2L", createValue(0)),
        arguments("i >>= 1.5f", null),
        arguments("i >>= 3.14", null),
        arguments("i >>= 'a'", createValue(0)),

        arguments("l >>= false", null),
        arguments("l >>= 1", createValue(2L)),
        arguments("l >>= 2L", createValue(1L)),
        arguments("l >>= 1.5f", null),
        arguments("l >>= 3.14", null),
        arguments("l >>= 'a'", createValue(0L)),

        arguments("f >>= false", null),
        arguments("f >>= 1", null),
        arguments("f >>= 2L", null),
        arguments("f >>= 1.5f", null),
        arguments("f >>= 3.14", null),
        arguments("f >>= 'a'", null),

        arguments("d >>= false", null),
        arguments("d >>= 1", null),
        arguments("d >>= 2L", null),
        arguments("d >>= 1.5f", null),
        arguments("d >>= 3.14", null),
        arguments("d >>= 'a'", null),

        arguments("c >>= false", null),
        arguments("c >>= 1", createValue((char)48)),
        arguments("c >>= 2L", createValue((char)24)),
        arguments("c >>= 1.5f", null),
        arguments("c >>= 3.14", null),
        arguments("c >>= 'a'", createValue((char)48)));
  }

  protected static Stream<Arguments> gTGTGTEqualsExpression() {
    return Stream.of(
        arguments("b >>>= false", null),
        arguments("b >>>= 1", null),
        arguments("b >>>= 2L", null),
        arguments("b >>>= 1.5f", null),
        arguments("b >>>= 3.14", null),
        arguments("b >>>= 'a'", null),

        arguments("i >>>= false", null),
        arguments("i >>>= 1", createValue(0)),
        arguments("i >>>= 2L", createValue(0)),
        arguments("i >>>= 1.5f", null),
        arguments("i >>>= 3.14", null),
        arguments("i >>>= 'a'", createValue(0)),

        arguments("l >>>= false", null),
        arguments("l >>>= 1", createValue(2L)),
        arguments("l >>>= 2L", createValue(1L)),
        arguments("l >>>= 1.5f", null),
        arguments("l >>>= 3.14", null),
        arguments("l >>>= 'a'", createValue(0L)),

        arguments("f >>>= false", null),
        arguments("f >>>= 1", null),
        arguments("f >>>= 2L", null),
        arguments("f >>>= 1.5f", null),
        arguments("f >>>= 3.14", null),
        arguments("f >>>= 'a'", null),

        arguments("d >>>= false", null),
        arguments("d >>>= 1", null),
        arguments("d >>>= 2L", null),
        arguments("d >>>= 1.5f", null),
        arguments("d >>>= 3.14", null),
        arguments("d >>>= 'a'", null),

        arguments("c >>>= false", null),
        arguments("c >>>= 1", createValue((char)48)),
        arguments("c >>>= 2L", createValue((char)24)),
        arguments("c >>>= 1.5f", null),
        arguments("c >>>= 3.14", null),
        arguments("c >>>= 'a'", createValue((char)48)));
  }

  protected static Stream<Arguments> lTLTEqualsExpression() {
    return Stream.of(
        arguments("b <<= false", null),
        arguments("b <<= 1", null),
        arguments("b <<= 2L", null),
        arguments("b <<= 1.5f", null),
        arguments("b <<= 3.14", null),
        arguments("b <<= 'a'", null),

        arguments("i <<= false", null),
        arguments("i <<= 1", createValue(2)),
        arguments("i <<= 2L", createValue(4)),
        arguments("i <<= 1.5f", null),
        arguments("i <<= 3.14", null),
        arguments("i <<= 'a'", createValue(2)),

        arguments("l <<= false", null),
        arguments("l <<= 1", createValue(10L)),
        arguments("l <<= 2L", createValue(20L)),
        arguments("l <<= 1.5f", null),
        arguments("l <<= 3.14", null),
        arguments("l <<= 'a'", createValue(42949672960L)),

        arguments("f <<= false", null),
        arguments("f <<= 1", null),
        arguments("f <<= 2L", null),
        arguments("f <<= 1.5f", null),
        arguments("f <<= 3.14", null),
        arguments("f <<= 'a'", null),

        arguments("d <<= false", null),
        arguments("d <<= 1", null),
        arguments("d <<= 2L", null),
        arguments("d <<= 1.5f", null),
        arguments("d <<= 3.14", null),
        arguments("d <<= 'a'", null),

        arguments("c <<= false", null),
        arguments("c <<= 1", createValue((char)194)),
        arguments("c <<= 2L", createValue((char)388)),
        arguments("c <<= 1.5f", null),
        arguments("c <<= 3.14", null),
        arguments("c <<= 'a'", createValue((char)194)));
  }

  protected static Stream<Arguments> minusEqualsExpression() {
    return Stream.of(
        arguments("b -= false", null),
        arguments("b -= 1", null),
        arguments("b -= 2L", null),
        arguments("b -= 1.5f", null),
        arguments("b -= 3.14", null),
        arguments("b -= 'a'", null),

        arguments("i -= false", null),
        arguments("i -= 1", createValue(0)),
        arguments("i -= 2L", createValue(-1)),
        arguments("i -= 1.5f", createValue(0)),
        arguments("i -= 3.14", createValue(-2)),
        arguments("i -= 'a'", createValue(-96)),

        arguments("l -= false", null),
        arguments("l -= 1", createValue(4L)),
        arguments("l -= 2L", createValue(3L)),
        arguments("l -= 1.5f", createValue(3L)),
        arguments("l -= 3.14", createValue(1L)),
        arguments("l -= 'a'", createValue(-92L)),

        arguments("f -= false", null),
        arguments("f -= 1", createValue(0.5f)),
        arguments("f -= 2L", createValue(-0.5f)),
        arguments("f -= 1.2f", createValue(.3f)),
        arguments("f -= 3.14", createValue(-1.64f)),
        arguments("f -= 'a'", createValue(-95.5f)),

        arguments("d -= false", null),
        arguments("d -= 1", createValue(2.14)),
        arguments("d -= 2L", createValue(1.14)),
        arguments("d -= 1.5f", createValue(1.64)),
        arguments("d -= 3.04", createValue(.1)),
        arguments("d -= 'a'", createValue(-93.86)),

        arguments("c -= false", null),
        arguments("c -= 1", createValue((char)96)),
        arguments("c -= 2L", createValue((char)95)),
        arguments("c -= 1.5f", createValue((char)95.5f)),
        arguments("c -= 3.14", createValue((char)93.86)),
        arguments("c -= 'a'", createValue((char)0)));
  }

  protected static Stream<Arguments> percentEqualsExpression() {
    return Stream.of(
        arguments("b %= false", null),
        arguments("b %= 1", null),
        arguments("b %= 2L", null),
        arguments("b %= 1.5f", null),
        arguments("b %= 3.14", null),
        arguments("b %= 'a'", null),

        arguments("i %= false", null),
        arguments("i %= 1", createValue(0)),
        arguments("i %= 2L", createValue(1)),
        arguments("i %= 1.5f", createValue(1)),
        arguments("i %= 3.14", createValue(1)),
        arguments("i %= 'a'", createValue(1)),

        arguments("l %= false", null),
        arguments("l %= 1", createValue(0L)),
        arguments("l %= 2L", createValue(1L)),
        arguments("l %= 1.5f", createValue(0L)),
        arguments("l %= 3.14", createValue(1L)),
        arguments("l %= 'a'", createValue(5L)),

        arguments("f %= false", null),
        arguments("f %= 1", createValue(0.5f)),
        arguments("f %= 2L", createValue(1.5f)),
        arguments("f %= 1.5f", createValue(0f)),
        arguments("f %= 3.14", createValue(1.5f)),
        arguments("f %= 'a'", createValue(1.5f)),

        arguments("d %= false", null),
        arguments("d %= 1", createValue(0.14)),
        arguments("d %= 2L", createValue(1.14)),
        arguments("d %= 1.5f", createValue(0.14)),
        arguments("d %= 3.14", createValue(0.)),
        arguments("d %= 'a'", createValue(3.14)),

        arguments("c %= false", null),
        arguments("c %= 1", createValue((char)0)),
        arguments("c %= 2L", createValue((char)1)),
        arguments("c %= 1.5f", createValue((char)1)),
        arguments("c %= 3.14", createValue((char)2)),
        arguments("c %= 'a'", createValue((char)0)));
  }

  protected static Stream<Arguments> pipeEqualsExpression() {
    return Stream.of(
        arguments("b |= false", createValue(true)),
        arguments("b |= 1", null),
        arguments("b |= 2L", null),
        arguments("b |= 1.5f", null),
        arguments("b |= 3.14", null),
        arguments("b |= 'a'", null),

        arguments("i |= false", null),
        arguments("i |= 1", createValue(1)),
        arguments("i |= 2L", createValue(3)),
        arguments("i |= 1.5f", null),
        arguments("i |= 3.14", null),
        arguments("i |= 'a'", createValue(97)),

        arguments("l |= false", null),
        arguments("l |= 1", createValue(5L)),
        arguments("l |= 2L", createValue(7L)),
        arguments("l |= 1.5f", null),
        arguments("l |= 3.14", null),
        arguments("l |= 'a'", createValue(101L)),

        arguments("f |= false", null),
        arguments("f |= 1", null),
        arguments("f |= 2L", null),
        arguments("f |= 1.5f", null),
        arguments("f |= 3.14", null),
        arguments("f |= 'a'", null),

        arguments("d |= false", null),
        arguments("d |= 1", null),
        arguments("d |= 2L", null),
        arguments("d |= 1.5f", null),
        arguments("d |= 3.14", null),
        arguments("d |= 'a'", null),

        arguments("c |= false", null),
        arguments("c |= 1", createValue((char)97)),
        arguments("c |= 2L", createValue((char)99)),
        arguments("c |= 1.5f", null),
        arguments("c |= 3.14", null),
        arguments("c |= 'a'", createValue((char)97)));
  }

  protected static Stream<Arguments> plusEqualsExpression() {
    return Stream.of(
        arguments("b += false", null),
        arguments("b += 1", null),
        arguments("b += 2L", null),
        arguments("b += 1.5f", null),
        arguments("b += 3.14", null),
        arguments("b += 'a'", null),
        
        arguments("by += false", null),
        arguments("by += 1", createValue((byte)4)),
        arguments("by += 2L", createValue((byte)5)),
        arguments("by += 1.5f", createValue((byte)4)),
        arguments("by += 3.14", createValue((byte)6)),
        arguments("by += 'a'", createValue((byte)100)),

        arguments("i += false", null),
        arguments("i += 1", createValue(2)),
        arguments("i += 2L", createValue(3)),
        arguments("i += 1.5f", createValue(2)),
        arguments("i += 3.14", createValue(4)),
        arguments("i += 'a'", createValue(98)),

        arguments("l += false", null),
        arguments("l += 1", createValue(6L)),
        arguments("l += 2L", createValue(7L)),
        arguments("l += 1.5f", createValue(6L)),
        arguments("l += 3.14", createValue(8L)),
        arguments("l += 'a'", createValue(102L)),

        arguments("f += false", null),
        arguments("f += 1", createValue(2.5f)),
        arguments("f += 2L", createValue(3.5f)),
        arguments("f += 1.5f", createValue(3.0f)),
        arguments("f += 3.14", createValue(4.64f)),
        arguments("f += 'a'", createValue(98.5f)),

        arguments("d += false", null),
        arguments("d += 1", createValue(4.14)),
        arguments("d += 2L", createValue(5.14)),
        arguments("d += 1.5f", createValue(4.64)),
        arguments("d += 3.14", createValue(6.28)),
        arguments("d += 'a'", createValue(100.14)),

        arguments("c += false", null),
        arguments("c += 1", createValue((char)98)),
        arguments("c += 2L", createValue((char)99)),
        arguments("c += 1.5f", createValue((char)98)),
        arguments("c += 3.14", createValue((char)100)),
        arguments("c += 'a'", createValue((char)194)));
  }

  protected static Stream<Arguments> roofEqualsExpression() {
    return Stream.of(
        arguments("b ^= false", createValue(true)),
        arguments("b ^= 1", null),
        arguments("b ^= 2L", null),
        arguments("b ^= 1.5f", null),
        arguments("b ^= 3.14", null),
        arguments("b ^= 'a'", null),

        arguments("i ^= false", null),
        arguments("i ^= 3", createValue(2)),
        arguments("i ^= 4L", createValue(5)),
        arguments("i ^= 1.5f", null),
        arguments("i ^= 3.14", null),
        arguments("i ^= 'a'", createValue(96)),

        arguments("l ^= false", null),
        arguments("l ^= 1", createValue(4L)),
        arguments("l ^= 2L", createValue(7L)),
        arguments("l ^= 1.5f", null),
        arguments("l ^= 3.14", null),
        arguments("l ^= 'a'", createValue(100L)),

        arguments("f ^= false", null),
        arguments("f ^= 1", null),
        arguments("f ^= 2L", null),
        arguments("f ^= 1.5f", null),
        arguments("f ^= 3.14", null),
        arguments("f ^= 'a'", null),

        arguments("d ^= false", null),
        arguments("d ^= 1", null),
        arguments("d ^= 2L", null),
        arguments("d ^= 1.5f", null),
        arguments("d ^= 3.14", null),
        arguments("d ^= 'a'", null),

        arguments("c ^= false", null),
        arguments("c ^= 1", createValue((char)96)),
        arguments("c ^= 2L", createValue((char)99)),
        arguments("c ^= 1.5f", null),
        arguments("c ^= 3.14", null),
        arguments("c ^= 'a'", createValue((char)0)));
  }

  protected static Stream<Arguments> slashEqualsExpression() {
    return Stream.of(
        arguments("b /= false", null),
        arguments("b /= 1", null),
        arguments("b /= 2L", null),
        arguments("b /= 1.5f", null),
        arguments("b /= 3.14", null),
        arguments("b /= 'a'", null),

        arguments("i /= false", null),
        arguments("i /= 0.25f", createValue(4)),
        arguments("i /= 0.4", createValue(2)),
        arguments("i /= 2", createValue(0)),
        arguments("i /= 5L", createValue(0)),
        arguments("i /= 'A'", createValue(0)),

        arguments("l /= false", null),
        arguments("l /= 1.25f", createValue(4L)),
        arguments("l /= 0.4", createValue(12L)),
        arguments("l /= 2", createValue(2L)),
        arguments("l /= 5L", createValue(1L)),
        arguments("l /= 'A'", createValue(0L)),

        arguments("f /= false", null),
        arguments("f /= 3", createValue(.5f)),
        arguments("f /= 2L", createValue(0.75f)),
        arguments("f /= 0.025f", createValue(60.f)),
        arguments("f /= 2.5", createValue(.6f)),
        arguments("f /= 'A'", createValue(0.0230769f)),

        arguments("d /= false", null),
        arguments("d /= 1", createValue(3.14)),
        arguments("d /= 2L", createValue(1.57)),
        arguments("d /= 1.57f", createValue(2.)),
        arguments("d /= 0.02", createValue(157.)),
        arguments("d /= 'A'", createValue(0.048307692307)),

        arguments("c /= false", null),
        arguments("c /= 1", createValue((char)97)),
        arguments("c /= 97L", createValue((char)1)),
        arguments("c /= 0.25f", createValue((char)388)),
        arguments("c /= 0.4", createValue((char)242)),
        arguments("c /= 'A'", createValue((char)1)));
  }

  protected static Stream<Arguments> starEqualsExpression() {
    return Stream.of(
        arguments("b *= false", null),
        arguments("b *= 1", null),
        arguments("b *= 2L", null),
        arguments("b *= 1.5f", null),
        arguments("b *= 3.14", null),
        arguments("b *= 'a'", null),

        arguments("i *= false", null),
        arguments("i *= 0.25f", createValue(0)),
        arguments("i *= 4.5", createValue(4)),
        arguments("i *= 2", createValue(2)),
        arguments("i *= 2L", createValue(2)),
        arguments("i *= 'A'", createValue(65)),

        arguments("l *= false", null),
        arguments("l *= 0.5f", createValue(2L)),
        arguments("l *= 0.2", createValue(1L)),
        arguments("l *= 2", createValue(10L)),
        arguments("l *= 10L", createValue(50L)),
        arguments("l *= 'A'", createValue(325L)),

        arguments("f *= false", null),
        arguments("f *= 3", createValue(4.5f)),
        arguments("f *= 2L", createValue(3f)),
        arguments("f *= 0.5f", createValue(.75f)),
        arguments("f *= 0.5", createValue(.75f)),
        arguments("f *= 'A'", createValue(97.5f)),

        arguments("d *= false", null),
        arguments("d *= 1", createValue(3.14)),
        arguments("d *= 2L", createValue(6.28)),
        arguments("d *= 0.5f", createValue(1.57)),
        arguments("d *= 0.5", createValue(1.57)),
        arguments("d *= 'A'", createValue(204.1)),

        arguments("c *= false", null),
        arguments("c *= 2", createValue((char)194)),
        arguments("c *= 2L", createValue((char)194)),
        arguments("c *= 0.25f", createValue((char)24)),
        arguments("c *= 0.5", createValue((char)48)),
        arguments("c *= 'A'", createValue((char)6305)));
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
