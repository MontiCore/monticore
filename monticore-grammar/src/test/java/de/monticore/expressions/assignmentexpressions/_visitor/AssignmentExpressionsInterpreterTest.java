/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.assignmentexpressions._visitor;

import de.monticore.expressions.AbstractInterpreterTest;
import de.monticore.interpreter.Value;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static de.monticore.interpreter.ValueFactory.createValue;

public class AssignmentExpressionsInterpreterTest extends AbstractInterpreterTest {

  protected static Stream<Arguments> incSuffixExpression() {
    return Stream.of(
        arguments("b++", null, BOOL),
        arguments("i++", createValue(2), INT),
        arguments("l++", createValue(6L), LONG),
        arguments("f++", createValue(2.5f), FLOAT),
        arguments("d++", createValue(4.14), DOUBLE),
        arguments("c++", createValue(98), CHAR),
        arguments("s++", null, STRING));
  }

  protected static Stream<Arguments> incPrefixExpression() {
    return Stream.of(
        arguments("++b", null, BOOL),
        arguments("++i", createValue(2), INT),
        arguments("++l", createValue(6L), LONG),
        arguments("++f", createValue(2.5f), FLOAT),
        arguments("++d", createValue(4.14), DOUBLE),
        arguments("++c", createValue(98), CHAR),
        arguments("++s", null, STRING));
  }

  protected static Stream<Arguments> decSuffixExpression() {
    return Stream.of(
        arguments("c--", createValue(96), CHAR),
        arguments("s--", null, STRING),
        arguments("i--", createValue(0), INT),
        arguments("l--", createValue(4L), LONG),
        arguments("f--", createValue(0.5f), FLOAT),
        arguments("d--", createValue(2.14), DOUBLE),
        arguments("b--", null, BOOL));
  }

  protected static Stream<Arguments> decPrefixExpression() {
    return Stream.of(
        arguments("--c", createValue(96), CHAR),
        arguments("--s", null, STRING),
        arguments("--i", createValue(0), INT),
        arguments("--l", createValue(4L), LONG),
        arguments("--f", createValue(0.5f), FLOAT),
        arguments("--d", createValue(2.14), DOUBLE),
        arguments("--b", null, BOOL));
  }

  protected static Stream<Arguments> andEqualsExpression() {
    return Stream.of(
        arguments("b &= false", null, BOOL),
        arguments("b &= 1", null, BOOL),
        arguments("b &= 2L", null, BOOL),
        arguments("b &= 1.5f", null, BOOL),
        arguments("b &= 3.14", null, BOOL),
        arguments("b &= 'c'", null, BOOL),
        arguments("b &= \"test\"", null, BOOL),

        arguments("i &= false", null, INT),
        arguments("i &= 1", createValue(1), INT),
        arguments("i &= 2L", createValue(0L), INT),
        arguments("i &= 1.5f", null, INT),
        arguments("i &= 3.14", null, INT),
        arguments("i &= 'a'", createValue(1), INT),
        arguments("i &= \"test\"", null, INT),

        arguments("l &= false", null, LONG),
        arguments("l &= 1", createValue(1L), LONG),
        arguments("l &= 2L", createValue(0L), LONG),
        arguments("l &= 1.5f", null, LONG),
        arguments("l &= 3.14", null, LONG),
        arguments("l &= 'a'", createValue(1L), LONG),
        arguments("l &= \"test\"", null, LONG),

        arguments("f &= false", null, FLOAT),
        arguments("f &= 1", null, FLOAT),
        arguments("f &= 2L", null, FLOAT),
        arguments("f &= 1.5f", null, FLOAT),
        arguments("f &= 3.14", null, FLOAT),
        arguments("f &= 'a'", null, FLOAT),
        arguments("f &= \"test\"", null, FLOAT),

        arguments("d &= false", null, DOUBLE),
        arguments("d &= 1", null, DOUBLE),
        arguments("d &= 2L", null, DOUBLE),
        arguments("d &= 1.5f", null, DOUBLE),
        arguments("d &= 3.14", null, DOUBLE),
        arguments("d &= 'a'", null, DOUBLE),
        arguments("d &= \"test\"", null, DOUBLE),

        arguments("c &= false", null, CHAR),
        arguments("c &= 1", createValue(1), CHAR),
        arguments("c &= 2L", createValue(0L), CHAR),
        arguments("c &= 1.5f", null, CHAR),
        arguments("c &= 3.14", null, CHAR),
        arguments("c &= 'a'", createValue(97), CHAR),
        arguments("c &= \"test\"", null, CHAR),

        arguments("s &= false", null, STRING),
        arguments("s &= 1", null, STRING),
        arguments("s &= 2L", null, STRING),
        arguments("s &= 1.5f", null, STRING),
        arguments("s &= 3.14", null, STRING),
        arguments("s &= 'a'", null, STRING),
        arguments("s &= \"test\"", null, STRING));
  }

  protected static Stream<Arguments> gTGTEqualsExpression() {
    return Stream.of(
        arguments("b >>= false", null, BOOL),
        arguments("b >>= 1", null, BOOL),
        arguments("b >>= 2L", null, BOOL),
        arguments("b >>= 1.5f", null, BOOL),
        arguments("b >>= 3.14", null, BOOL),
        arguments("b >>= 'a'", null, BOOL),
        arguments("b >>= \"test\"", null, BOOL),

        arguments("i >>= false", null, INT),
        arguments("i >>= 1", createValue(0), INT),
        arguments("i >>= 2L", createValue(0), INT),
        arguments("i >>= 1.5f", null, INT),
        arguments("i >>= 3.14", null, INT),
        arguments("i >>= 'a'", createValue(0), INT),
        arguments("i >>= \"test\"", null, INT),

        arguments("l >>= false", null, LONG),
        arguments("l >>= 1", createValue(2L), LONG),
        arguments("l >>= 2L", createValue(1L), LONG),
        arguments("l >>= 1.5f", null, LONG),
        arguments("l >>= 3.14", null, LONG),
        arguments("l >>= 'a'", createValue(0L), LONG),
        arguments("l >>= \"test\"", null, LONG),

        arguments("f >>= false", null, FLOAT),
        arguments("f >>= 1", null, FLOAT),
        arguments("f >>= 2L", null, FLOAT),
        arguments("f >>= 1.5f", null, FLOAT),
        arguments("f >>= 3.14", null, FLOAT),
        arguments("f >>= 'a'", null, FLOAT),
        arguments("f >>= \"test\"", null, FLOAT),

        arguments("d >>= false", null, DOUBLE),
        arguments("d >>= 1", null, DOUBLE),
        arguments("d >>= 2L", null, DOUBLE),
        arguments("d >>= 1.5f", null, DOUBLE),
        arguments("d >>= 3.14", null, DOUBLE),
        arguments("d >>= 'a'", null, DOUBLE),
        arguments("d >>= \"test\"", null, DOUBLE),

        arguments("c >>= false", null, CHAR),
        arguments("c >>= 1", createValue(48), CHAR),
        arguments("c >>= 2L", createValue(24), CHAR),
        arguments("c >>= 1.5f", null, CHAR),
        arguments("c >>= 3.14", null, CHAR),
        arguments("c >>= 'a'", createValue(48), CHAR),
        arguments("c >>= \"test\"", null, CHAR),

        arguments("s >>= false", null, STRING),
        arguments("s >>= 1", null, STRING),
        arguments("s >>= 2L", null, STRING),
        arguments("s >>= 1.5f", null, STRING),
        arguments("s >>= 3.14", null, STRING),
        arguments("s >>= 'a'", null, STRING),
        arguments("s >>= \"test\"", null, STRING));
  }

  protected static Stream<Arguments> gTGTGTEqualsExpression() {
    return Stream.of(
        arguments("b >>>= false", null, BOOL),
        arguments("b >>>= 1", null, BOOL),
        arguments("b >>>= 2L", null, BOOL),
        arguments("b >>>= 1.5f", null, BOOL),
        arguments("b >>>= 3.14", null, BOOL),
        arguments("b >>>= 'a'", null, BOOL),
        arguments("b >>>= \"test\"", null, BOOL),

        arguments("i >>>= false", null, INT),
        arguments("i >>>= 1", createValue(0), INT),
        arguments("i >>>= 2L", createValue(0), INT),
        arguments("i >>>= 1.5f", null, INT),
        arguments("i >>>= 3.14", null, INT),
        arguments("i >>>= 'a'", createValue(0), INT),
        arguments("i >>>= \"test\"", null, INT),

        arguments("l >>>= false", null, LONG),
        arguments("l >>>= 1", createValue(2L), LONG),
        arguments("l >>>= 2L", createValue(1L), LONG),
        arguments("l >>>= 1.5f", null, LONG),
        arguments("l >>>= 3.14", null, LONG),
        arguments("l >>>= 'a'", createValue(0L), LONG),
        arguments("l >>>= \"test\"", null, LONG),

        arguments("f >>>= false", null, FLOAT),
        arguments("f >>>= 1", null, FLOAT),
        arguments("f >>>= 2L", null, FLOAT),
        arguments("f >>>= 1.5f", null, FLOAT),
        arguments("f >>>= 3.14", null, FLOAT),
        arguments("f >>>= 'a'", null, FLOAT),
        arguments("f >>>= \"test\"", null, FLOAT),

        arguments("d >>>= false", null, DOUBLE),
        arguments("d >>>= 1", null, DOUBLE),
        arguments("d >>>= 2L", null, DOUBLE),
        arguments("d >>>= 1.5f", null, DOUBLE),
        arguments("d >>>= 3.14", null, DOUBLE),
        arguments("d >>>= 'a'", null, DOUBLE),
        arguments("d >>>= \"test\"", null, DOUBLE),

        arguments("c >>>= false", null, CHAR),
        arguments("c >>>= 1", createValue(48), CHAR),
        arguments("c >>>= 2L", createValue(24), CHAR),
        arguments("c >>>= 1.5f", null, CHAR),
        arguments("c >>>= 3.14", null, CHAR),
        arguments("c >>>= 'a'", createValue(48), CHAR),
        arguments("c >>>= \"test\"", null, CHAR),

        arguments("s >>>= false", null, STRING),
        arguments("s >>>= 1", null, STRING),
        arguments("s >>>= 2L", null, STRING),
        arguments("s >>>= 1.5f", null, STRING),
        arguments("s >>>= 3.14", null, STRING),
        arguments("s >>>= 'a'", null, STRING),
        arguments("s >>>= \"test\"", null, STRING));
  }

  protected static Stream<Arguments> lTLTEqualsExpression() {
    return Stream.of(
        arguments("b <<= false", null, BOOL),
        arguments("b <<= 1", null, BOOL),
        arguments("b <<= 2L", null, BOOL),
        arguments("b <<= 1.5f", null, BOOL),
        arguments("b <<= 3.14", null, BOOL),
        arguments("b <<= 'a'", null, BOOL),
        arguments("b <<= \"test\"", null, BOOL),

        arguments("i <<= false", null, INT),
        arguments("i <<= 1", createValue(2), INT),
        arguments("i <<= 2L", createValue(4), INT),
        arguments("i <<= 1.5f", null, INT),
        arguments("i <<= 3.14", null, INT),
        arguments("i <<= 'a'", createValue(2), INT),
        arguments("i <<= \"test\"", null, INT),

        arguments("l <<= false", null, LONG),
        arguments("l <<= 1", createValue(10L), LONG),
        arguments("l <<= 2L", createValue(20L), LONG),
        arguments("l <<= 1.5f", null, LONG),
        arguments("l <<= 3.14", null, LONG),
        arguments("l <<= 'a'", createValue(42949672960L), LONG),
        arguments("l <<= \"test\"", null, LONG),

        arguments("f <<= false", null, FLOAT),
        arguments("f <<= 1", null, FLOAT),
        arguments("f <<= 2L", null, FLOAT),
        arguments("f <<= 1.5f", null, FLOAT),
        arguments("f <<= 3.14", null, FLOAT),
        arguments("f <<= 'a'", null, FLOAT),
        arguments("f <<= \"test\"", null, FLOAT),

        arguments("d <<= false", null, DOUBLE),
        arguments("d <<= 1", null, DOUBLE),
        arguments("d <<= 2L", null, DOUBLE),
        arguments("d <<= 1.5f", null, DOUBLE),
        arguments("d <<= 3.14", null, DOUBLE),
        arguments("d <<= 'a'", null, DOUBLE),
        arguments("d <<= \"test\"", null, DOUBLE),

        arguments("c <<= false", null, CHAR),
        arguments("c <<= 1", createValue(194), CHAR),
        arguments("c <<= 2L", createValue(388), CHAR),
        arguments("c <<= 1.5f", null, CHAR),
        arguments("c <<= 3.14", null, CHAR),
        arguments("c <<= 'a'", createValue(194), CHAR),
        arguments("c <<= \"test\"", null, CHAR),

        arguments("s <<= false", null, STRING),
        arguments("s <<= 1", null, STRING),
        arguments("s <<= 2L", null, STRING),
        arguments("s <<= 1.5f", null, STRING),
        arguments("s <<= 3.14", null, STRING),
        arguments("s <<= 'a'", null, STRING),
        arguments("s <<= \"test\"", null, STRING));
  }

  protected static Stream<Arguments> minusEqualsExpression() {
    return Stream.of(
        arguments("b -= false", null, BOOL),
        arguments("b -= 1", null, BOOL),
        arguments("b -= 2L", null, BOOL),
        arguments("b -= 1.5f", null, BOOL),
        arguments("b -= 3.14", null, BOOL),
        arguments("b -= 'a'", null, BOOL),
        arguments("b -= \"test\"", null, BOOL),

        arguments("i -= false", null, INT),
        arguments("i -= 1", createValue(0), INT),
        arguments("i -= 2L", createValue(-1L), INT),
        arguments("i -= 1.5f", createValue(-.5f), INT),
        arguments("i -= 3.14", createValue(-2.14), INT),
        arguments("i -= 'a'", createValue(-96), INT),
        arguments("i -= \"test\"", null, INT),

        arguments("l -= false", null, LONG),
        arguments("l -= 1", createValue(4L), LONG),
        arguments("l -= 2L", createValue(3L), LONG),
        arguments("l -= 1.5f", createValue(3.5f), LONG),
        arguments("l -= 3.14", createValue(1.86), LONG),
        arguments("l -= 'a'", createValue(-92L), LONG),
        arguments("l -= \"test\"", null, LONG),

        arguments("f -= false", null, FLOAT),
        arguments("f -= 1", createValue(0.5f), FLOAT),
        arguments("f -= 2L", createValue(-0.5f), FLOAT),
        arguments("f -= 1.2f", createValue(.3f), FLOAT),
        arguments("f -= 3.14", createValue(-1.64), FLOAT),
        arguments("f -= 'a'", createValue(-95.5f), FLOAT),
        arguments("f -= \"test\"", null, FLOAT),

        arguments("d -= false", null, DOUBLE),
        arguments("d -= 1", createValue(2.14), DOUBLE),
        arguments("d -= 2L", createValue(1.14), DOUBLE),
        arguments("d -= 1.5f", createValue(1.64), DOUBLE),
        arguments("d -= 3.04", createValue(.1), DOUBLE),
        arguments("d -= 'a'", createValue(-93.86), DOUBLE),
        arguments("d -= \"test\"", null, DOUBLE),

        arguments("c -= false", null, CHAR),
        arguments("c -= 1", createValue(96), CHAR),
        arguments("c -= 2L", createValue(95L), CHAR),
        arguments("c -= 1.5f", createValue(95.5f), CHAR),
        arguments("c -= 3.14", createValue(93.86), CHAR),
        arguments("c -= 'a'", createValue(0), CHAR),
        arguments("c -= \"test\"", null, CHAR),

        arguments("s -= false", null, STRING),
        arguments("s -= 1", null, STRING),
        arguments("s -= 2L", null, STRING),
        arguments("s -= 1.5f", null, STRING),
        arguments("s -= 3.14", null, STRING),
        arguments("s -= 'a'", null, STRING),
        arguments("s -= \"test\"", null, STRING));
  }

  protected static Stream<Arguments> percentEqualsExpression() {
    return Stream.of(
        arguments("b %= false", null, BOOL),
        arguments("b %= 1", null, BOOL),
        arguments("b %= 2L", null, BOOL),
        arguments("b %= 1.5f", null, BOOL),
        arguments("b %= 3.14", null, BOOL),
        arguments("b %= 'a'", null, BOOL),
        arguments("b %= \"test\"", null, BOOL),

        arguments("i %= false", null, INT),
        arguments("i %= 1", createValue(0), INT),
        arguments("i %= 2L", createValue(1L), INT),
        arguments("i %= 1.5f", createValue(1f), INT),
        arguments("i %= 3.14", createValue(1.), INT),
        arguments("i %= 'a'", createValue(1), INT),
        arguments("i %= \"test\"", null, INT),

        arguments("l %= false", null, LONG),
        arguments("l %= 1", createValue(0L), LONG),
        arguments("l %= 2L", createValue(1L), LONG),
        arguments("l %= 1.5f", createValue(0.5f), LONG),
        arguments("l %= 3.14", createValue(1.86), LONG),
        arguments("l %= 'a'", createValue(5L), LONG),
        arguments("l %= \"test\"", null, LONG),

        arguments("f %= false", null, FLOAT),
        arguments("f %= 1", createValue(0.5f), FLOAT),
        arguments("f %= 2L", createValue(1.5f), FLOAT),
        arguments("f %= 1.5f", createValue(0f), FLOAT),
        arguments("f %= 3.14", createValue(1.5), FLOAT),
        arguments("f %= 'a'", createValue(1.5f), FLOAT),
        arguments("f %= \"test\"", null, FLOAT),

        arguments("d %= false", null, DOUBLE),
        arguments("d %= 1", createValue(0.14), DOUBLE),
        arguments("d %= 2L", createValue(1.14), DOUBLE),
        arguments("d %= 1.5f", createValue(0.14), DOUBLE),
        arguments("d %= 3.14", createValue(0.), DOUBLE),
        arguments("d %= 'a'", createValue(3.14), DOUBLE),
        arguments("d %= \"test\"", null, DOUBLE),

        arguments("c %= false", null, CHAR),
        arguments("c %= 1", createValue(0), CHAR),
        arguments("c %= 2L", createValue(1L), CHAR),
        arguments("c %= 1.5f", createValue(1f), CHAR),
        arguments("c %= 3.14", createValue(2.8), CHAR),
        arguments("c %= 'a'", createValue(0), CHAR),
        arguments("c %= \"test\"", null, CHAR),

        arguments("s %= false", null, STRING),
        arguments("s %= 1", null, STRING),
        arguments("s %= 2L", null, STRING),
        arguments("s %= 1.5f", null, STRING),
        arguments("s %= 3.14", null, STRING),
        arguments("s %= 'a'", null, STRING),
        arguments("s %= \"test\"", null, STRING));
  }

  protected static Stream<Arguments> pipeEqualsExpression() {
    return Stream.of(
        arguments("b |= false", null, BOOL),
        arguments("b |= 1", null, BOOL),
        arguments("b |= 2L", null, BOOL),
        arguments("b |= 1.5f", null, BOOL),
        arguments("b |= 3.14", null, BOOL),
        arguments("b |= 'a'", null, BOOL),
        arguments("b |= \"test\"", null, BOOL),

        arguments("i |= false", null, INT),
        arguments("i |= 1", createValue(1), INT),
        arguments("i |= 2L", createValue(3L), INT),
        arguments("i |= 1.5f", null, INT),
        arguments("i |= 3.14", null, INT),
        arguments("i |= 'a'", createValue(97), INT),
        arguments("i |= \"test\"", null, INT),

        arguments("l |= false", null, LONG),
        arguments("l |= 1", createValue(5L), LONG),
        arguments("l |= 2L", createValue(7L), LONG),
        arguments("l |= 1.5f", null, LONG),
        arguments("l |= 3.14", null, LONG),
        arguments("l |= 'a'", createValue(101L), LONG),
        arguments("l |= \"test\"", null, LONG),

        arguments("f |= false", null, FLOAT),
        arguments("f |= 1", null, FLOAT),
        arguments("f |= 2L", null, FLOAT),
        arguments("f |= 1.5f", null, FLOAT),
        arguments("f |= 3.14", null, FLOAT),
        arguments("f |= 'a'", null, FLOAT),
        arguments("f |= \"test\"", null, FLOAT),

        arguments("d |= false", null, DOUBLE),
        arguments("d |= 1", null, DOUBLE),
        arguments("d |= 2L", null, DOUBLE),
        arguments("d |= 1.5f", null, DOUBLE),
        arguments("d |= 3.14", null, DOUBLE),
        arguments("d |= 'a'", null, DOUBLE),
        arguments("d |= \"test\"", null, DOUBLE),

        arguments("c |= false", null, CHAR),
        arguments("c |= 1", createValue(97), CHAR),
        arguments("c |= 2L", createValue(99L), CHAR),
        arguments("c |= 1.5f", null, CHAR),
        arguments("c |= 3.14", null, CHAR),
        arguments("c |= 'a'", createValue(97), CHAR),
        arguments("c |= \"test\"", null, CHAR),

        arguments("s |= false", null, STRING),
        arguments("s |= 1", null, STRING),
        arguments("s |= 2L", null, STRING),
        arguments("s |= 1.5f", null, STRING),
        arguments("s |= 3.14", null, STRING),
        arguments("s |= 'a'", null, STRING),
        arguments("s |= \"test\"", null, STRING));
  }

  protected static Stream<Arguments> plusEqualsExpression() {
    return Stream.of(
        arguments("b += false", null, BOOL),
        arguments("b += 1", null, BOOL),
        arguments("b += 2L", null, BOOL),
        arguments("b += 1.5f", null, BOOL),
        arguments("b += 3.14", null, BOOL),
        arguments("b += 'a'", null, BOOL),
        arguments("b += \"test\"", null, BOOL),

        arguments("i += false", null, INT),
        arguments("i += 1", createValue(2), INT),
        arguments("i += 2L", createValue(3L), INT),
        arguments("i += 1.5f", createValue(2.5f), INT),
        arguments("i += 3.14", createValue(4.14), INT),
        arguments("i += 'a'", createValue(98), INT),
        arguments("i += \"test\"", null, INT),

        arguments("l += false", null, LONG),
        arguments("l += 1", createValue(6L), LONG),
        arguments("l += 2L", createValue(7L), LONG),
        arguments("l += 1.5f", createValue(6.5f), LONG),
        arguments("l += 3.14", createValue(8.14), LONG),
        arguments("l += 'a'", createValue(102L), LONG),
        arguments("l += \"test\"", null, LONG),

        arguments("f += false", null, FLOAT),
        arguments("f += 1", createValue(2.5f), FLOAT),
        arguments("f += 2L", createValue(3.5f), FLOAT),
        arguments("f += 1.5f", createValue(3.0f), FLOAT),
        arguments("f += 3.14", createValue(4.64), FLOAT),
        arguments("f += 'a'", createValue(98.5f), FLOAT),
        arguments("f += \"test\"", null, FLOAT),

        arguments("d += false", null, DOUBLE),
        arguments("d += 1", createValue(4.14), DOUBLE),
        arguments("d += 2L", createValue(5.14), DOUBLE),
        arguments("d += 1.5f", createValue(4.64), DOUBLE),
        arguments("d += 3.14", createValue(6.28), DOUBLE),
        arguments("d += 'a'", createValue(100.14), DOUBLE),
        arguments("d += \"test\"", null, DOUBLE),

        arguments("c += false", null, CHAR),
        arguments("c += 1", createValue(98), CHAR),
        arguments("c += 2L", createValue(99L), CHAR),
        arguments("c += 1.5f", createValue(98.5f), CHAR),
        arguments("c += 3.14", createValue(100.14), CHAR),
        arguments("c += 'a'", createValue(194), CHAR),
        arguments("c += \"test\"", null, CHAR),

        arguments("s += false", createValue("hellofalse"), STRING),
        arguments("s += 1", createValue("hello1"), STRING),
        arguments("s += 2L", createValue("hello2"), STRING),
        arguments("s += 1.5f", createValue("hello1.5"), STRING),
        arguments("s += 3.14", createValue("hello3.14"), STRING),
        arguments("s += 'a'", createValue("helloa"), STRING),
        arguments("s += \"test\"", createValue("hellotest"), STRING));
  }

  protected static Stream<Arguments> roofEqualsExpression() {
    return Stream.of(
        arguments("b ^= false", null, BOOL),
        arguments("b ^= 1", null, BOOL),
        arguments("b ^= 2L", null, BOOL),
        arguments("b ^= 1.5f", null, BOOL),
        arguments("b ^= 3.14", null, BOOL),
        arguments("b ^= 'a'", null, BOOL),
        arguments("b ^= \"test\"", null, BOOL),

        arguments("i ^= false", null, INT),
        arguments("i ^= 3", createValue(2), INT),
        arguments("i ^= 4L", createValue(5L), INT),
        arguments("i ^= 1.5f", null, INT),
        arguments("i ^= 3.14", null, INT),
        arguments("i ^= 'a'", createValue(96), INT),
        arguments("i ^= \"test\"", null, INT),

        arguments("l ^= false", null, LONG),
        arguments("l ^= 1", createValue(4L), LONG),
        arguments("l ^= 2L", createValue(7L), LONG),
        arguments("l ^= 1.5f", null, LONG),
        arguments("l ^= 3.14", null, LONG),
        arguments("l ^= 'a'", createValue(100L), LONG),
        arguments("l ^= \"test\"", null, LONG),

        arguments("f ^= false", null, FLOAT),
        arguments("f ^= 1", null, FLOAT),
        arguments("f ^= 2L", null, FLOAT),
        arguments("f ^= 1.5f", null, FLOAT),
        arguments("f ^= 3.14", null, FLOAT),
        arguments("f ^= 'a'", null, FLOAT),
        arguments("f ^= \"test\"", null, FLOAT),

        arguments("d ^= false", null, DOUBLE),
        arguments("d ^= 1", null, DOUBLE),
        arguments("d ^= 2L", null, DOUBLE),
        arguments("d ^= 1.5f", null, DOUBLE),
        arguments("d ^= 3.14", null, DOUBLE),
        arguments("d ^= 'a'", null, DOUBLE),
        arguments("d ^= \"test\"", null, DOUBLE),

        arguments("c ^= false", null, CHAR),
        arguments("c ^= 1", createValue(96), CHAR),
        arguments("c ^= 2L", createValue(99L), CHAR),
        arguments("c ^= 1.5f", null, CHAR),
        arguments("c ^= 3.14", null, CHAR),
        arguments("c ^= 'a'", createValue(0), CHAR),
        arguments("c ^= \"test\"", null, CHAR),

        arguments("s ^= false", null, STRING),
        arguments("s ^= 1", null, STRING),
        arguments("s ^= 2L", null, STRING),
        arguments("s ^= 1.5f", null, STRING),
        arguments("s ^= 3.14", null, STRING),
        arguments("s ^= 'a'", null, STRING),
        arguments("s ^= \"test\"", null, STRING));
  }

  protected static Stream<Arguments> slashEqualsExpression() {
    return Stream.of(
        arguments("b /= false", null, BOOL),
        arguments("b /= 1", null, BOOL),
        arguments("b /= 2L", null, BOOL),
        arguments("b /= 1.5f", null, BOOL),
        arguments("b /= 3.14", null, BOOL),
        arguments("b /= 'a'", null, BOOL),
        arguments("b /= \"test\"", null, BOOL),

        arguments("i /= false", null, INT),
        arguments("i /= 0.25f", createValue(4.f), INT),
        arguments("i /= 0.4", createValue(2.5), INT),
        arguments("i /= 2", createValue(0), INT),
        arguments("i /= 5L", createValue(0L), INT),
        arguments("i /= 'A'", createValue(0), INT),
        arguments("i /= \"test\"", null, INT),

        arguments("l /= false", null, LONG),
        arguments("l /= 1.25f", createValue(4.f), LONG),
        arguments("l /= 0.4", createValue(12.5), LONG),
        arguments("l /= 2", createValue(2L), LONG),
        arguments("l /= 5L", createValue(1L), LONG),
        arguments("l /= 'A'", createValue(0L), LONG),
        arguments("l /= \"test\"", null, LONG),

        arguments("f /= false", null, FLOAT),
        arguments("f /= 3", createValue(.5f), FLOAT),
        arguments("f /= 2L", createValue(0.75f), FLOAT),
        arguments("f /= 0.025f", createValue(60.f), FLOAT),
        arguments("f /= 2.5", createValue(.6), FLOAT),
        arguments("f /= 'A'", createValue(0.0230769f), FLOAT),
        arguments("f /= \"test\"", null, FLOAT),

        arguments("d /= false", null, DOUBLE),
        arguments("d /= 1", createValue(3.14), DOUBLE),
        arguments("d /= 2L", createValue(1.57), DOUBLE),
        arguments("d /= 1.57f", createValue(2.), DOUBLE),
        arguments("d /= 0.02", createValue(157.), DOUBLE),
        arguments("d /= 'A'", createValue(0.048307692307), DOUBLE),
        arguments("d /= \"test\"", null, DOUBLE),

        arguments("c /= false", null, CHAR),
        arguments("c /= 1", createValue(97), CHAR),
        arguments("c /= 97L", createValue(1L), CHAR),
        arguments("c /= 0.25f", createValue(388.f), CHAR),
        arguments("c /= 0.4", createValue(242.5), CHAR),
        arguments("c /= 'A'", createValue(1), CHAR),
        arguments("c /= \"test\"", null, CHAR),

        arguments("s /= false", null, STRING),
        arguments("s /= 1", null, STRING),
        arguments("s /= 2L", null, STRING),
        arguments("s /= 1.5f", null, STRING),
        arguments("s /= 3.14", null, STRING),
        arguments("s /= 'a'", null, STRING),
        arguments("s /= \"test\"", null, STRING));
  }

  protected static Stream<Arguments> starEqualsExpression() {
    return Stream.of(
        arguments("b *= false", null, BOOL),
        arguments("b *= 1", null, BOOL),
        arguments("b *= 2L", null, BOOL),
        arguments("b *= 1.5f", null, BOOL),
        arguments("b *= 3.14", null, BOOL),
        arguments("b *= 'a'", null, BOOL),
        arguments("b *= \"test\"", null, BOOL),

        arguments("i *= false", null, INT),
        arguments("i *= 0.25f", createValue(0.25f), INT),
        arguments("i *= 4.5", createValue(4.5), INT),
        arguments("i *= 2", createValue(2), INT),
        arguments("i *= 2L", createValue(2L), INT),
        arguments("i *= 'A'", createValue(65), INT),
        arguments("i *= \"test\"", null, INT),

        arguments("l *= false", null, LONG),
        arguments("l *= 0.5f", createValue(2.5f), LONG),
        arguments("l *= 0.2", createValue(1.), LONG),
        arguments("l *= 2", createValue(10L), LONG),
        arguments("l *= 10L", createValue(50L), LONG),
        arguments("l *= 'A'", createValue(325L), LONG),
        arguments("l *= \"test\"", null, LONG),

        arguments("f *= false", null, FLOAT),
        arguments("f *= 3", createValue(4.5f), FLOAT),
        arguments("f *= 2L", createValue(3f), FLOAT),
        arguments("f *= 0.5f", createValue(.75f), FLOAT),
        arguments("f *= 0.5", createValue(.75), FLOAT),
        arguments("f *= 'A'", createValue(97.5f), FLOAT),
        arguments("f *= \"test\"", null, FLOAT),

        arguments("d *= false", null, DOUBLE),
        arguments("d *= 1", createValue(3.14), DOUBLE),
        arguments("d *= 2L", createValue(6.28), DOUBLE),
        arguments("d *= 0.5f", createValue(1.57), DOUBLE),
        arguments("d *= 0.5", createValue(1.57), DOUBLE),
        arguments("d *= 'A'", createValue(204.1), DOUBLE),
        arguments("d *= \"test\"", null, DOUBLE),

        arguments("c *= false", null, CHAR),
        arguments("c *= 2", createValue(194), CHAR),
        arguments("c *= 2L", createValue(194L), CHAR),
        arguments("c *= 0.25f", createValue(24.25f), CHAR),
        arguments("c *= 0.5", createValue(48.5), CHAR),
        arguments("c *= 'A'", createValue(6305), CHAR),
        arguments("c *= \"test\"", null, CHAR),

        arguments("s *= false", null, STRING),
        arguments("s *= 1", null, STRING),
        arguments("s *= 2L", null, STRING),
        arguments("s *= 1.5f", null, STRING),
        arguments("s *= 3.14", null, STRING),
        arguments("s *= 'a'", null, STRING),
        arguments("s *= \"test\"", null, STRING));
  }

  @ParameterizedTest
  @MethodSource({
      "incSuffixExpression", "incPrefixExpression", "decSuffixExpression",
      "decPrefixExpression", "andEqualsExpression", "gTGTEqualsExpression",
      "gTGTGTEqualsExpression", "lTLTEqualsExpression", "minusEqualsExpression",
      "percentEqualsExpression", "pipeEqualsExpression", "plusEqualsExpression",
      "roofEqualsExpression", "slashEqualsExpression", "starEqualsExpression"
  })
  public void testInterpreter(String expression, Value result, int types) {
    init(types);
    if (isNull(result)) {
      testInvalidExpression(expression);
    } else {
      testValidExpression(expression, result);
    }
  }
}
