/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.commonexpressions._visitor;

import de.monticore.expressions.AbstractInterpreterTest;
import de.monticore.interpreter.ValueFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CommonExpressionsInterpreterTest extends AbstractInterpreterTest {

  @BeforeEach
  public void before() {
    init(127);
  }

  @Test
  public void testInterpretPlusExpression() {
    testInvalidExpression("true + false");
    testInvalidExpression("true + 1");
    testInvalidExpression("1 + false");
    testInvalidExpression("true + 1L");
    testInvalidExpression("1L + false");
    testInvalidExpression("true + 1.2f");
    testInvalidExpression("1.5f + false");
    testInvalidExpression("true + 1.2");
    testInvalidExpression("1.5 + false");
    testInvalidExpression("true + 'a'");
    testInvalidExpression("'a' + false");
    testValidExpression("true + \"a\"", ValueFactory.createValue("truea"));
    testValidExpression("\"a\" + false", ValueFactory.createValue("afalse"));

    testValidExpression("1 + 2", ValueFactory.createValue(3));
    testValidExpression("1L + 2", ValueFactory.createValue(3L));
    testValidExpression("1 + 2L", ValueFactory.createValue(3L));
    testValidExpression("1.5f + 2", ValueFactory.createValue(3.5f));
    testValidExpression("1 + 1.2f", ValueFactory.createValue(2.2f));
    testValidExpression("1.5 + 2", ValueFactory.createValue(3.5));
    testValidExpression("1 + 1.2", ValueFactory.createValue(2.2));
    testValidExpression("'a' + 2", ValueFactory.createValue(99));
    testValidExpression("1 + 'a'", ValueFactory.createValue(98));
    testValidExpression("\"a\" + 2", ValueFactory.createValue("a2"));
    testValidExpression("1 + \"a\"", ValueFactory.createValue("1a"));

    testValidExpression("1L + 2L", ValueFactory.createValue(3L));
    testValidExpression("1.2f + 2L", ValueFactory.createValue(3.2f));
    testValidExpression("1L + 1.5f", ValueFactory.createValue(2.5f));
    testValidExpression("1L + 1.2", ValueFactory.createValue(2.2));
    testValidExpression("1.5 + 2L", ValueFactory.createValue(3.5));
    testValidExpression("1L + 'a'", ValueFactory.createValue(98L));
    testValidExpression("'a' + 2L", ValueFactory.createValue(99L));
    testValidExpression("1L + \"a\"", ValueFactory.createValue("1a"));
    testValidExpression("\"a\" + 2L", ValueFactory.createValue("a2"));

    testValidExpression("1.2f + 1.5f", ValueFactory.createValue(2.7f));
    testValidExpression("1.2 + 1.5f", ValueFactory.createValue(2.7));
    testValidExpression("1.2f + 1.5", ValueFactory.createValue(2.7));
    testValidExpression("'a' + 1.5f", ValueFactory.createValue(98.5f));
    testValidExpression("1.2f + 'a'", ValueFactory.createValue(98.2f));
    testValidExpression("\"a\" + 1.5f", ValueFactory.createValue("a1.5"));
    testValidExpression("1.2f + \"a\"", ValueFactory.createValue("1.2a"));

    testValidExpression("1.2 + 1.5", ValueFactory.createValue(2.7));
    testValidExpression("'a' + 1.5", ValueFactory.createValue(98.5));
    testValidExpression("1.2 + 'a'", ValueFactory.createValue(98.2));
    testValidExpression("\"a\" + 1.5", ValueFactory.createValue("a1.5"));
    testValidExpression("1.2 + \"a\"", ValueFactory.createValue("1.2a"));

    testValidExpression("'a' + 'a'", ValueFactory.createValue(194));
    testValidExpression("\"a\" + 'b'", ValueFactory.createValue("ab"));
    testValidExpression("'c' + \"a\"", ValueFactory.createValue("ca"));

    testValidExpression("\"a\" + \"b\"", ValueFactory.createValue("ab"));
  }

  @Test
  public void testInterpretBracketExpression() {
    testValidExpression("(true)", ValueFactory.createValue(true));
    testValidExpression("(1)", ValueFactory.createValue(1));
    testValidExpression("(2L)", ValueFactory.createValue(2L));
    testValidExpression("(2.5f)", ValueFactory.createValue(2.5f));
    testValidExpression("(3.14)", ValueFactory.createValue(3.14));
    testValidExpression("('a')", ValueFactory.createValue('a'));
    testValidExpression("(\"abc\")", ValueFactory.createValue("abc"));
  }

  @Test
  public void testInterpretMinusExpression() {
    testInvalidExpression("true - false");
    testInvalidExpression("true - 1");
    testInvalidExpression("1 - false");
    testInvalidExpression("true - 1L");
    testInvalidExpression("1L - false");
    testInvalidExpression("true - 1.2f");
    testInvalidExpression("1.5f - false");
    testInvalidExpression("true - 1.2");
    testInvalidExpression("1.5 - false");
    testInvalidExpression("true - 'a'");
    testInvalidExpression("'a' - false");
    testInvalidExpression("true - \"a\"");
    testInvalidExpression("\"a\" - false");

    testValidExpression("1 - 2", ValueFactory.createValue(-1));
    testValidExpression("1L - 2", ValueFactory.createValue(-1L));
    testValidExpression("1 - 2L", ValueFactory.createValue(-1L));
    testValidExpression("1.5f - 2", ValueFactory.createValue(-0.5f));
    testValidExpression("1 - 1.2f", ValueFactory.createValue(-0.2f));
    testValidExpression("1.5 - 2", ValueFactory.createValue(-0.5));
    testValidExpression("1 - 1.2", ValueFactory.createValue(-0.2));
    testValidExpression("'a' - 2", ValueFactory.createValue(95));
    testValidExpression("1 - 'a'", ValueFactory.createValue(-96));
    testInvalidExpression("\"a\" - 2");
    testInvalidExpression("1 - \"a\"");

    testValidExpression("1L - 2L", ValueFactory.createValue(-1L));
    testValidExpression("1.2f - 2L", ValueFactory.createValue(-0.8f));
    testValidExpression("1L - 1.5f", ValueFactory.createValue(-0.5f));
    testValidExpression("1L - 1.2", ValueFactory.createValue(-0.2));
    testValidExpression("1.5 - 2L", ValueFactory.createValue(-0.5));
    testValidExpression("1L - 'a'", ValueFactory.createValue(-96L));
    testValidExpression("'a' - 2L", ValueFactory.createValue(95L));
    testInvalidExpression("1L - \"a\"");
    testInvalidExpression("\"a\" - 2L");

    testValidExpression("1.2f - 1.5f", ValueFactory.createValue(-0.3f));
    testValidExpression("1.2 - 1.5f", ValueFactory.createValue(-0.3));
    testValidExpression("1.2f - 1.5", ValueFactory.createValue(-0.3));
    testValidExpression("'a' - 1.5f", ValueFactory.createValue(95.5f));
    testValidExpression("1.2f - 'a'", ValueFactory.createValue(-95.8f));
    testInvalidExpression("\"a\" - 1.5f");
    testInvalidExpression("1.2f - \"a\"");

    testValidExpression("1.2 - 1.5", ValueFactory.createValue(-0.3));
    testValidExpression("'a' - 1.5", ValueFactory.createValue(95.5));
    testValidExpression("1.2 - 'a'", ValueFactory.createValue(-95.8));
    testInvalidExpression("\"a\" - 1.5");
    testInvalidExpression("1.2 - \"a\"");

    testValidExpression("'a' - 'a'", ValueFactory.createValue(0));
    testInvalidExpression("\"a\" - 'a'");
    testInvalidExpression("'a' - \"a\"");

    testInvalidExpression("\"a\" - \"a\"");
  }

  @Test
  public void testInterpretMultExpression() {
    testInvalidExpression("true * false");
    testInvalidExpression("true * 1");
    testInvalidExpression("1 * false");
    testInvalidExpression("true * 1L");
    testInvalidExpression("1L * false");
    testInvalidExpression("true * 1.2f");
    testInvalidExpression("1.5f * false");
    testInvalidExpression("true * 1.2");
    testInvalidExpression("1.5 * false");
    testInvalidExpression("true * 'a'");
    testInvalidExpression("'a' * false");
    testInvalidExpression("true * \"a\"");
    testInvalidExpression("\"a\" * false");

    testValidExpression("1 * 2", ValueFactory.createValue(2));
    testValidExpression("1L * 2", ValueFactory.createValue(2L));
    testValidExpression("1 * 2L", ValueFactory.createValue(2L));
    testValidExpression("1.5f * 2", ValueFactory.createValue(3.f));
    testValidExpression("1 * 1.2f", ValueFactory.createValue(1.2f));
    testValidExpression("1.5 * 2", ValueFactory.createValue(3.));
    testValidExpression("1 * 1.2", ValueFactory.createValue(1.2));
    testValidExpression("'a' * 2", ValueFactory.createValue(194));
    testValidExpression("1 * 'a'", ValueFactory.createValue(97));
    testInvalidExpression("\"a\" * 2");
    testInvalidExpression("1 * \"a\"");

    testValidExpression("1L * 2L", ValueFactory.createValue(2L));
    testValidExpression("1.2f * 2L", ValueFactory.createValue(2.4f));
    testValidExpression("1L * 1.5f", ValueFactory.createValue(1.5f));
    testValidExpression("1L * 1.2", ValueFactory.createValue(1.2));
    testValidExpression("1.5 * 2L", ValueFactory.createValue(3.0));
    testValidExpression("1L * 'a'", ValueFactory.createValue(97L));
    testValidExpression("'a' * 2L", ValueFactory.createValue(194L));
    testInvalidExpression("1L * \"a\"");
    testInvalidExpression("\"a\" * 2L");

    testValidExpression("1.2f * 1.5f", ValueFactory.createValue(1.8f));
    testValidExpression("1.2 * 1.5f", ValueFactory.createValue(1.8));
    testValidExpression("1.2f * 1.5", ValueFactory.createValue(1.8));
    testValidExpression("'a' * 1.5f", ValueFactory.createValue(145.5f));
    testValidExpression("1.2f * 'a'", ValueFactory.createValue(116.4f));
    testInvalidExpression("\"a\" * 1.5f");
    testInvalidExpression("1.2f * \"a\"");

    testValidExpression("1.2 * 1.5", ValueFactory.createValue(1.8));
    testValidExpression("'a' * 1.5", ValueFactory.createValue(145.5));
    testValidExpression("1.2 * 'a'", ValueFactory.createValue(116.4));
    testInvalidExpression("\"a\" * 1.5");
    testInvalidExpression("1.2 * \"a\"");

    testValidExpression("'a' * 'a'", ValueFactory.createValue(9409));
    testInvalidExpression("\"a\" * 'a'");
    testInvalidExpression("'a' * \"a\"");

    testInvalidExpression("\"a\" * \"a\"");
  }

  @Test
  public void testInterpretDivideExpression() {
    testInvalidExpression("true / false");
    testInvalidExpression("true / 1");
    testInvalidExpression("1 / false");
    testInvalidExpression("true / 1L");
    testInvalidExpression("1L / false");
    testInvalidExpression("true / 1.2f");
    testInvalidExpression("1.5f / false");
    testInvalidExpression("true / 1.2");
    testInvalidExpression("1.5 / false");
    testInvalidExpression("true / 'a'");
    testInvalidExpression("'a' / false");
    testInvalidExpression("true / \"a\"");
    testInvalidExpression("\"a\" / false");

    testValidExpression("1 / 2", ValueFactory.createValue(0));
    testValidExpression("1L / 2", ValueFactory.createValue(0L));
    testValidExpression("1 / 2L", ValueFactory.createValue(0L));
    testValidExpression("1.5f / 2", ValueFactory.createValue(0.75f));
    testValidExpression("3 / 1.5f", ValueFactory.createValue(2.f));
    testValidExpression("1.5 / 2", ValueFactory.createValue(0.75));
    testValidExpression("3 / 1.5", ValueFactory.createValue(2.));
    testValidExpression("'a' / 2", ValueFactory.createValue(48));
    testValidExpression("1 / 'a'", ValueFactory.createValue(0));
    testInvalidExpression("\"a\" / 2");
    testInvalidExpression("1 / \"a\"");

    testValidExpression("1L / 2L", ValueFactory.createValue(0L));
    testValidExpression("1.2f / 2L", ValueFactory.createValue(0.6f));
    testValidExpression("3L / 1.5f", ValueFactory.createValue(2.f));
    testValidExpression("3L / 1.5", ValueFactory.createValue(2.));
    testValidExpression("3.0 / 2L", ValueFactory.createValue(1.5));
    testValidExpression("1L / 'a'", ValueFactory.createValue(0L));
    testValidExpression("'a' / 2L", ValueFactory.createValue(48L));
    testInvalidExpression("1L / \"a\"");
    testInvalidExpression("\"a\" / 2L");

    testValidExpression("1.2f / 1.5f", ValueFactory.createValue(0.8f));
    testValidExpression("1.2 / 1.5f", ValueFactory.createValue(0.8));
    testValidExpression("1.2f / 1.5", ValueFactory.createValue(0.8));
    testValidExpression("'a' / 0.5f", ValueFactory.createValue(194.f));
    testValidExpression("194.0f / 'a'", ValueFactory.createValue(2.f));
    testInvalidExpression("\"a\" / 1.5f");
    testInvalidExpression("1.2f / \"a\"");

    testValidExpression("1.2 / 1.5", ValueFactory.createValue(0.8));
    testValidExpression("'a' / 2.0", ValueFactory.createValue(48.5));
    testValidExpression("97.0 / 'a'", ValueFactory.createValue(1.));
    testInvalidExpression("\"a\" / 1.5");
    testInvalidExpression("1.2 / \"a\"");

    testValidExpression("'a' / 'a'", ValueFactory.createValue(1));
    testInvalidExpression("\"a\" / 'a'");
    testInvalidExpression("'a' / \"a\"");

    testInvalidExpression("\"a\" / \"a\"");

    testInvalidExpression("1 / 0");
    testInvalidExpression("'a' / 0");
    testInvalidExpression("1L / 0");
    testInvalidExpression("1 / 0L");
    testInvalidExpression("1L / 0L");
    testInvalidExpression("'a' / 0L");
  }

  @Test
  public void testInterpretModuloExpression() {
    testInvalidExpression("true % false");
    testInvalidExpression("true % 1");
    testInvalidExpression("1 % false");
    testInvalidExpression("true % 1L");
    testInvalidExpression("1L % false");
    testInvalidExpression("true % 1.2f");
    testInvalidExpression("1.5f % false");
    testInvalidExpression("true % 1.2");
    testInvalidExpression("1.5 % false");
    testInvalidExpression("true % 'a'");
    testInvalidExpression("'a' % false");
    testInvalidExpression("true % \"a\"");
    testInvalidExpression("\"a\" % false");

    testValidExpression("1 % 2", ValueFactory.createValue(1));
    testValidExpression("1L % 2", ValueFactory.createValue(1L));
    testValidExpression("1 % 2L", ValueFactory.createValue(1L));
    testValidExpression("1.5f % 2", ValueFactory.createValue(1.5f));
    testValidExpression("1 % 1.2f", ValueFactory.createValue(1.0f));
    testValidExpression("1.5 % 2", ValueFactory.createValue(1.5));
    testValidExpression("1 % 1.2", ValueFactory.createValue(1.0));
    testValidExpression("'a' % 2", ValueFactory.createValue(1));
    testValidExpression("1 % 'a'", ValueFactory.createValue(1));
    testInvalidExpression("\"a\" % 2");
    testInvalidExpression("1 % \"a\"");

    testValidExpression("1L % 2L", ValueFactory.createValue(1L));
    testValidExpression("1.2f % 2L", ValueFactory.createValue(1.2f));
    testValidExpression("1L % 1.5f", ValueFactory.createValue(1.0f));
    testValidExpression("1L % 1.2", ValueFactory.createValue(1.0));
    testValidExpression("1.5 % 2L", ValueFactory.createValue(1.5));
    testValidExpression("1L % 'a'", ValueFactory.createValue(1L));
    testValidExpression("'a' % 2L", ValueFactory.createValue(1L));
    testInvalidExpression("1L % \"a\"");
    testInvalidExpression("\"a\" % 2L");

    testValidExpression("1.2f % 1.5f", ValueFactory.createValue(1.2f));
    testValidExpression("1.2 % 1.5f", ValueFactory.createValue(1.2));
    testValidExpression("1.2f % 1.5", ValueFactory.createValue(1.2));
    testValidExpression("'a' % 1.5f", ValueFactory.createValue(1.0f));
    testValidExpression("1.2f % 'a'", ValueFactory.createValue(1.2f));
    testInvalidExpression("\"a\" % 1.5f");
    testInvalidExpression("1.2f % \"a\"");

    testValidExpression("1.2 % 1.5", ValueFactory.createValue(1.2));
    testValidExpression("'a' % 1.5", ValueFactory.createValue(1.0));
    testValidExpression("1.2 % 'a'", ValueFactory.createValue(1.2));
    testInvalidExpression("\"a\" % 1.5");
    testInvalidExpression("1.2 % \"a\"");

    testValidExpression("'a' % 'a'", ValueFactory.createValue(0));
    testInvalidExpression("\"a\" % 'a'");
    testInvalidExpression("'a' % \"a\"");

    testInvalidExpression("\"a\" % \"a\"");
  }

  @Test
  public void testInterpretEqualsExpression() {
    testValidExpression("true == false", ValueFactory.createValue(false));
    testInvalidExpression("true == 1");
    testInvalidExpression("1 == false");
    testInvalidExpression("true == 1L");
    testInvalidExpression("1L == false");
    testInvalidExpression("true == 1.2f");
    testInvalidExpression("1.5f == false");
    testInvalidExpression("true == 1.2");
    testInvalidExpression("1.5 == false");
    testInvalidExpression("true == 'a'");
    testInvalidExpression("'a' == false");
    testInvalidExpression("true == \"a\"");
    testInvalidExpression("\"a\" == false");

    testValidExpression("1 == 2", ValueFactory.createValue(false));
    testValidExpression("1L == 2", ValueFactory.createValue(false));
    testValidExpression("1 == 2L", ValueFactory.createValue(false));
    testValidExpression("1.5f == 2", ValueFactory.createValue(false));
    testValidExpression("1 == 1.2f", ValueFactory.createValue(false));
    testValidExpression("1.5 == 2", ValueFactory.createValue(false));
    testValidExpression("1 == 1.2", ValueFactory.createValue(false));
    testValidExpression("'a' == 2", ValueFactory.createValue(false));
    testValidExpression("1 == 'a'", ValueFactory.createValue(false));
    testInvalidExpression("\"a\" == 2");
    testInvalidExpression("1 == \"a\"");

    testValidExpression("1L == 2L", ValueFactory.createValue(false));
    testValidExpression("1.2f == 2L", ValueFactory.createValue(false));
    testValidExpression("1L == 1.5f", ValueFactory.createValue(false));
    testValidExpression("1L == 1.2", ValueFactory.createValue(false));
    testValidExpression("1.5 == 2L", ValueFactory.createValue(false));
    testValidExpression("1L == 'a'", ValueFactory.createValue(false));
    testValidExpression("'a' == 2L", ValueFactory.createValue(false));
    testInvalidExpression("1L == \"a\"");
    testInvalidExpression("\"a\" == 2L");

    testValidExpression("1.2f == 1.5f", ValueFactory.createValue(false));
    testValidExpression("1.2 == 1.5f", ValueFactory.createValue(false));
    testValidExpression("1.2f == 1.5", ValueFactory.createValue(false));
    testValidExpression("'a' == 1.5f", ValueFactory.createValue(false));
    testValidExpression("1.2f == 'a'", ValueFactory.createValue(false));
    testInvalidExpression("\"a\" == 1.5f");
    testInvalidExpression("1.2f == \"a\"");

    testValidExpression("1.2 == 1.5", ValueFactory.createValue(false));
    testValidExpression("'a' == 1.5", ValueFactory.createValue(false));
    testValidExpression("1.2 == 'a'", ValueFactory.createValue(false));
    testInvalidExpression("\"a\" == 1.5");
    testInvalidExpression("1.2 == \"a\"");

    testValidExpression("'a' == 'a'", ValueFactory.createValue(true));
    testInvalidExpression("\"a\" == 'a'");
    testInvalidExpression("'a' == \"a\"");

    testInvalidExpression("\"a\" == \"a\"");
  }

  @Test
  public void testInterpretNotEqualsExpression() {
    testValidExpression("true != false", ValueFactory.createValue(true));
    testInvalidExpression("true != 1");
    testInvalidExpression("1 != false");
    testInvalidExpression("true != 1L");
    testInvalidExpression("1L != false");
    testInvalidExpression("true != 1.2f");
    testInvalidExpression("1.5f != false");
    testInvalidExpression("true != 1.2");
    testInvalidExpression("1.5 != false");
    testInvalidExpression("true != 'a'");
    testInvalidExpression("'a' != false");
    testInvalidExpression("true != \"a\"");
    testInvalidExpression("\"a\" != false");

    testValidExpression("1 != 2", ValueFactory.createValue(true));
    testValidExpression("1L != 2", ValueFactory.createValue(true));
    testValidExpression("1 != 2L", ValueFactory.createValue(true));
    testValidExpression("1.5f != 2", ValueFactory.createValue(true));
    testValidExpression("1 != 1.2f", ValueFactory.createValue(true));
    testValidExpression("1.5 != 2", ValueFactory.createValue(true));
    testValidExpression("1 != 1.2", ValueFactory.createValue(true));
    testValidExpression("'a' != 2", ValueFactory.createValue(true));
    testValidExpression("1 != 'a'", ValueFactory.createValue(true));
    testInvalidExpression("\"a\" != 2");
    testInvalidExpression("1 != \"a\"");

    testValidExpression("1L != 2L", ValueFactory.createValue(true));
    testValidExpression("1.2f != 2L", ValueFactory.createValue(true));
    testValidExpression("1L != 1.5f", ValueFactory.createValue(true));
    testValidExpression("1L != 1.2", ValueFactory.createValue(true));
    testValidExpression("1.5 != 2L", ValueFactory.createValue(true));
    testValidExpression("1L != 'a'", ValueFactory.createValue(true));
    testValidExpression("'a' != 2L", ValueFactory.createValue(true));
    testInvalidExpression("1L != \"a\"");
    testInvalidExpression("\"a\" != 2L");

    testValidExpression("1.2f != 1.5f", ValueFactory.createValue(true));
    testValidExpression("1.2 != 1.5f", ValueFactory.createValue(true));
    testValidExpression("1.2f != 1.5", ValueFactory.createValue(true));
    testValidExpression("'a' != 1.5f", ValueFactory.createValue(true));
    testValidExpression("1.2f != 'a'", ValueFactory.createValue(true));
    testInvalidExpression("\"a\" != 1.5f");
    testInvalidExpression("1.2f != \"a\"");

    testValidExpression("1.2 != 1.5", ValueFactory.createValue(true));
    testValidExpression("'a' != 1.5", ValueFactory.createValue(true));
    testValidExpression("1.2 != 'a'", ValueFactory.createValue(true));
    testInvalidExpression("\"a\" != 1.5");
    testInvalidExpression("1.2 != \"a\"");

    testValidExpression("'a' != 'a'", ValueFactory.createValue(false));
    testInvalidExpression("\"a\" != 'a'");
    testInvalidExpression("'a' != \"a\"");

    testInvalidExpression("\"a\" != \"a\"");
  }

  @Test
  public void testInterpretLessThanExpression() {
    testInvalidExpression("true < false");
    testInvalidExpression("true < 1");
    testInvalidExpression("1 < false");
    testInvalidExpression("true < 1L");
    testInvalidExpression("1L < false");
    testInvalidExpression("true < 1.2f");
    testInvalidExpression("1.5f < false");
    testInvalidExpression("true < 1.2");
    testInvalidExpression("1.5 < false");
    testInvalidExpression("true < 'a'");
    testInvalidExpression("'a' < false");
    testInvalidExpression("true < \"a\"");
    testInvalidExpression("\"a\" < false");

    testValidExpression("1 < 2", ValueFactory.createValue(true));
    testValidExpression("1L < 2", ValueFactory.createValue(true));
    testValidExpression("1 < 2L", ValueFactory.createValue(true));
    testValidExpression("1.5f < 2", ValueFactory.createValue(true));
    testValidExpression("1 < 1.2f", ValueFactory.createValue(true));
    testValidExpression("1.5 < 2", ValueFactory.createValue(true));
    testValidExpression("1 < 1.2", ValueFactory.createValue(true));
    testValidExpression("'a' < 2", ValueFactory.createValue(false));
    testValidExpression("1 < 'a'", ValueFactory.createValue(true));
    testInvalidExpression("\"a\" < 2");
    testInvalidExpression("1 < \"a\"");

    testValidExpression("1L < 2L", ValueFactory.createValue(true));
    testValidExpression("1.2f < 2L", ValueFactory.createValue(true));
    testValidExpression("1L < 1.5f", ValueFactory.createValue(true));
    testValidExpression("1L < 1.2", ValueFactory.createValue(true));
    testValidExpression("1.5 < 2L", ValueFactory.createValue(true));
    testValidExpression("1L < 'a'", ValueFactory.createValue(true));
    testValidExpression("'a' < 2L", ValueFactory.createValue(false));
    testInvalidExpression("1L < \"a\"");
    testInvalidExpression("\"a\" < 2L");

    testValidExpression("1.2f < 1.5f", ValueFactory.createValue(true));
    testValidExpression("1.2 < 1.5f", ValueFactory.createValue(true));
    testValidExpression("1.2f < 1.5", ValueFactory.createValue(true));
    testValidExpression("'a' < 1.5f", ValueFactory.createValue(false));
    testValidExpression("1.2f < 'a'", ValueFactory.createValue(true));
    testInvalidExpression("\"a\" < 1.5f");
    testInvalidExpression("1.2f < \"a\"");

    testValidExpression("1.2 < 1.5", ValueFactory.createValue(true));
    testValidExpression("'a' < 1.5", ValueFactory.createValue(false));
    testValidExpression("1.2 < 'a'", ValueFactory.createValue(true));
    testInvalidExpression("\"a\" < 1.5");
    testInvalidExpression("1.2 < \"a\"");

    testValidExpression("'a' < 'a'", ValueFactory.createValue(false));
    testInvalidExpression("\"a\" < 'a'");
    testInvalidExpression("'a' < \"a\"");

    testInvalidExpression("\"a\" < \"a\"");
  }

  @Test
  public void testInterpretGreaterThanExpression() {
    testInvalidExpression("true > false");
    testInvalidExpression("true > 1");
    testInvalidExpression("1 > false");
    testInvalidExpression("true > 1L");
    testInvalidExpression("1L > false");
    testInvalidExpression("true > 1.2f");
    testInvalidExpression("1.5f > false");
    testInvalidExpression("true > 1.2");
    testInvalidExpression("1.5 > false");
    testInvalidExpression("true > 'a'");
    testInvalidExpression("'a' > false");
    testInvalidExpression("true > \"a\"");
    testInvalidExpression("\"a\" > false");

    testValidExpression("1 > 2", ValueFactory.createValue(false));
    testValidExpression("1L > 2", ValueFactory.createValue(false));
    testValidExpression("1 > 2L", ValueFactory.createValue(false));
    testValidExpression("1.5f > 2", ValueFactory.createValue(false));
    testValidExpression("1 > 1.2f", ValueFactory.createValue(false));
    testValidExpression("1.5 > 2", ValueFactory.createValue(false));
    testValidExpression("1 > 1.2", ValueFactory.createValue(false));
    testValidExpression("'a' > 2", ValueFactory.createValue(true));
    testValidExpression("1 > 'a'", ValueFactory.createValue(false));
    testInvalidExpression("\"a\" > 2");
    testInvalidExpression("1 > \"a\"");

    testValidExpression("1L > 2L", ValueFactory.createValue(false));
    testValidExpression("1.2f > 2L", ValueFactory.createValue(false));
    testValidExpression("1L > 1.5f", ValueFactory.createValue(false));
    testValidExpression("1L > 1.2", ValueFactory.createValue(false));
    testValidExpression("1.5 > 2L", ValueFactory.createValue(false));
    testValidExpression("1L > 'a'", ValueFactory.createValue(false));
    testValidExpression("'a' > 2L", ValueFactory.createValue(true));
    testInvalidExpression("1L > \"a\"");
    testInvalidExpression("\"a\" > 2L");

    testValidExpression("1.2f > 1.5f", ValueFactory.createValue(false));
    testValidExpression("1.2 > 1.5f", ValueFactory.createValue(false));
    testValidExpression("1.2f > 1.5", ValueFactory.createValue(false));
    testValidExpression("'a' > 1.5f", ValueFactory.createValue(true));
    testValidExpression("1.2f > 'a'", ValueFactory.createValue(false));
    testInvalidExpression("\"a\" > 1.5f");
    testInvalidExpression("1.2f > \"a\"");

    testValidExpression("1.2 > 1.5", ValueFactory.createValue(false));
    testValidExpression("'a' > 1.5", ValueFactory.createValue(true));
    testValidExpression("1.2 > 'a'", ValueFactory.createValue(false));
    testInvalidExpression("\"a\" > 1.5");
    testInvalidExpression("1.2 > \"a\"");

    testValidExpression("'a' > 'a'", ValueFactory.createValue(false));
    testInvalidExpression("\"a\" > 'a'");
    testInvalidExpression("'a' > \"a\"");

    testInvalidExpression("\"a\" > \"a\"");
  }

  @Test
  public void testInterpretGreaterEqualExpression() {
    testInvalidExpression("true >= false");
    testInvalidExpression("true >= 1");
    testInvalidExpression("1 >= false");
    testInvalidExpression("true >= 1L");
    testInvalidExpression("1L >= false");
    testInvalidExpression("true >= 1.2f");
    testInvalidExpression("1.5f >= false");
    testInvalidExpression("true >= 1.2");
    testInvalidExpression("1.5 >= false");
    testInvalidExpression("true >= 'a'");
    testInvalidExpression("'a' >= false");
    testInvalidExpression("true >= \"a\"");
    testInvalidExpression("\"a\" >= false");

    testValidExpression("1 >= 2", ValueFactory.createValue(false));
    testValidExpression("1L >= 2", ValueFactory.createValue(false));
    testValidExpression("1 >= 2L", ValueFactory.createValue(false));
    testValidExpression("1.5f >= 2", ValueFactory.createValue(false));
    testValidExpression("1 >= 1.2f", ValueFactory.createValue(false));
    testValidExpression("1.5 >= 2", ValueFactory.createValue(false));
    testValidExpression("1 >= 1.2", ValueFactory.createValue(false));
    testValidExpression("'a' >= 2", ValueFactory.createValue(true));
    testValidExpression("1 >= 'a'", ValueFactory.createValue(false));
    testInvalidExpression("\"a\" >= 2");
    testInvalidExpression("1 >= \"a\"");

    testValidExpression("1L >= 2L", ValueFactory.createValue(false));
    testValidExpression("1.2f >= 2L", ValueFactory.createValue(false));
    testValidExpression("1L >= 1.5f", ValueFactory.createValue(false));
    testValidExpression("1L >= 1.2", ValueFactory.createValue(false));
    testValidExpression("1.5 >= 2L", ValueFactory.createValue(false));
    testValidExpression("1L >= 'a'", ValueFactory.createValue(false));
    testValidExpression("'a' >= 2L", ValueFactory.createValue(true));
    testInvalidExpression("1L >= \"a\"");
    testInvalidExpression("\"a\" >= 2L");

    testValidExpression("1.2f >= 1.5f", ValueFactory.createValue(false));
    testValidExpression("1.2 >= 1.5f", ValueFactory.createValue(false));
    testValidExpression("1.2f >= 1.5", ValueFactory.createValue(false));
    testValidExpression("'a' >= 1.5f", ValueFactory.createValue(true));
    testValidExpression("1.2f >= 'a'", ValueFactory.createValue(false));
    testInvalidExpression("\"a\" >= 1.5f");
    testInvalidExpression("1.2f >= \"a\"");

    testValidExpression("1.2 >= 1.5", ValueFactory.createValue(false));
    testValidExpression("'a' >= 1.5", ValueFactory.createValue(true));
    testValidExpression("1.2 >= 'a'", ValueFactory.createValue(false));
    testInvalidExpression("\"a\" >= 1.5");
    testInvalidExpression("1.2 >= \"a\"");

    testValidExpression("'a' >= 'a'", ValueFactory.createValue(true));
    testInvalidExpression("\"a\" >= 'a'");
    testInvalidExpression("'a' >= \"a\"");

    testInvalidExpression("\"a\" >= \"a\"");
  }

  @Test
  public void testInterpretLessEqualExpression() {
    testInvalidExpression("true <= false");
    testInvalidExpression("true <= 1");
    testInvalidExpression("1 <= false");
    testInvalidExpression("true <= 1L");
    testInvalidExpression("1L <= false");
    testInvalidExpression("true <= 1.2f");
    testInvalidExpression("1.5f <= false");
    testInvalidExpression("true <= 1.2");
    testInvalidExpression("1.5 <= false");
    testInvalidExpression("true <= 'a'");
    testInvalidExpression("'a' <= false");
    testInvalidExpression("true <= \"a\"");
    testInvalidExpression("\"a\" <= false");

    testValidExpression("1 <= 2", ValueFactory.createValue(true));
    testValidExpression("1L <= 2", ValueFactory.createValue(true));
    testValidExpression("1 <= 2L", ValueFactory.createValue(true));
    testValidExpression("1.5f <= 2", ValueFactory.createValue(true));
    testValidExpression("1 <= 1.2f", ValueFactory.createValue(true));
    testValidExpression("1.5 <= 2", ValueFactory.createValue(true));
    testValidExpression("1 <= 1.2", ValueFactory.createValue(true));
    testValidExpression("'a' <= 2", ValueFactory.createValue(false));
    testValidExpression("1 <= 'a'", ValueFactory.createValue(true));
    testInvalidExpression("\"a\" <= 2");
    testInvalidExpression("1 <= \"a\"");

    testValidExpression("1L <= 2L", ValueFactory.createValue(true));
    testValidExpression("1.2f <= 2L", ValueFactory.createValue(true));
    testValidExpression("1L <= 1.5f", ValueFactory.createValue(true));
    testValidExpression("1L <= 1.2", ValueFactory.createValue(true));
    testValidExpression("1.5 <= 2L", ValueFactory.createValue(true));
    testValidExpression("1L <= 'a'", ValueFactory.createValue(true));
    testValidExpression("'a' <= 2L", ValueFactory.createValue(false));
    testInvalidExpression("1L <= \"a\"");
    testInvalidExpression("\"a\" <= 2L");

    testValidExpression("1.2f <= 1.5f", ValueFactory.createValue(true));
    testValidExpression("1.2 <= 1.5f", ValueFactory.createValue(true));
    testValidExpression("1.2f <= 1.5", ValueFactory.createValue(true));
    testValidExpression("'a' <= 1.5f", ValueFactory.createValue(false));
    testValidExpression("1.2f <= 'a'", ValueFactory.createValue(true));
    testInvalidExpression("\"a\" <= 1.5f");
    testInvalidExpression("1.2f <= \"a\"");

    testValidExpression("1.2 <= 1.5", ValueFactory.createValue(true));
    testValidExpression("'a' <= 1.5", ValueFactory.createValue(false));
    testValidExpression("1.2 <= 'a'", ValueFactory.createValue(true));
    testInvalidExpression("\"a\" <= 1.5");
    testInvalidExpression("1.2 <= \"a\"");

    testValidExpression("'a' <= 'a'", ValueFactory.createValue(true));
    testInvalidExpression("\"a\" <= 'a'");
    testInvalidExpression("'a' <= \"a\"");

    testInvalidExpression("\"a\" <= \"a\"");
  }

  @Test
  public void testInterpretBooleanNotExpression() {
    testInvalidExpression("~true");
    testValidExpression("~1", ValueFactory.createValue(-2));
    testValidExpression("~-5", ValueFactory.createValue(4));
    testValidExpression("~708", ValueFactory.createValue(-709));
    testValidExpression("~1L", ValueFactory.createValue(-2L));
    testValidExpression("~-5L", ValueFactory.createValue(4L));
    testValidExpression("~708L", ValueFactory.createValue(-709L));
    testInvalidExpression("~1.2f");
    testInvalidExpression("~1.5");
    testValidExpression("~'a'", ValueFactory.createValue(-98));
    testInvalidExpression("~\"a\"");
  }

  @Test
  public void testInterpretLogicalNotExpression() {
    testValidExpression("!true", ValueFactory.createValue(false));
    testValidExpression("!false", ValueFactory.createValue(true));
    testInvalidExpression("!1");
    testInvalidExpression("!1L");
    testInvalidExpression("!1.2f");
    testInvalidExpression("!1.5");
    testInvalidExpression("!'a'");
    testInvalidExpression("!\"a\"");
  }

  @Test
  public void testInterpretLogicalAndOpExpression() {
    testValidExpression("true && true", ValueFactory.createValue(true));
    testValidExpression("false && false", ValueFactory.createValue(false));
    testValidExpression("true && false", ValueFactory.createValue(false));
    testValidExpression("false && true", ValueFactory.createValue(false));
    testInvalidExpression("true && 1");
    testInvalidExpression("1 && false");
    testInvalidExpression("true && 1L");
    testInvalidExpression("1L && false");
    testInvalidExpression("true && 1.2f");
    testInvalidExpression("1.5f && false");
    testInvalidExpression("true && 1.2");
    testInvalidExpression("1.5 && false");
    testInvalidExpression("true && 'a'");
    testInvalidExpression("'a' && false");
    testInvalidExpression("true && \"a\"");
    testInvalidExpression("\"a\" && false");

    testInvalidExpression("1 && 2");
    testInvalidExpression("1L && 2");
    testInvalidExpression("1 && 2L");
    testInvalidExpression("1.5f && 2");
    testInvalidExpression("1 && 1.2f");
    testInvalidExpression("1.5 && 2");
    testInvalidExpression("1 && 1.2");
    testInvalidExpression("'a' && 2");
    testInvalidExpression("1 && 'a'");
    testInvalidExpression("\"a\" && 2");
    testInvalidExpression("1 && \"a\"");

    testInvalidExpression("1L && 2L");
    testInvalidExpression("1.2f && 2L");
    testInvalidExpression("1L && 1.5f");
    testInvalidExpression("1L && 1.2");
    testInvalidExpression("1.5 && 2L");
    testInvalidExpression("1L && 'a'");
    testInvalidExpression("'a' && 2L");
    testInvalidExpression("1L && \"a\"");
    testInvalidExpression("\"a\" && 2L");

    testInvalidExpression("1.2f && 1.5f");
    testInvalidExpression("1.2 && 1.5f");
    testInvalidExpression("1.2f && 1.5");
    testInvalidExpression("'a' && 1.5f");
    testInvalidExpression("1.2f && 'a'");
    testInvalidExpression("\"a\" && 1.5f");
    testInvalidExpression("1.2f && \"a\"");

    testInvalidExpression("1.2 && 1.5");
    testInvalidExpression("'a' && 1.5");
    testInvalidExpression("1.2 && 'a'");
    testInvalidExpression("\"a\" && 1.5");
    testInvalidExpression("1.2 && \"a\"");

    testInvalidExpression("'a' && 'a'");
    testInvalidExpression("\"a\" && 'a'");
    testInvalidExpression("'a' && \"a\"");

    testInvalidExpression("\"a\" && \"a\"");
  }

  @Test
  public void testInterpretLogicalOrOpExpression() {
    testValidExpression("true || true", ValueFactory.createValue(true));
    testValidExpression("false || false", ValueFactory.createValue(false));
    testValidExpression("true || false", ValueFactory.createValue(true));
    testValidExpression("false || true", ValueFactory.createValue(true));
    testInvalidExpression("true || 1");
    testInvalidExpression("1 || false");
    testInvalidExpression("true || 1L");
    testInvalidExpression("1L || false");
    testInvalidExpression("true || 1.2f");
    testInvalidExpression("1.5f || false");
    testInvalidExpression("true || 1.2");
    testInvalidExpression("1.5 || false");
    testInvalidExpression("true || 'a'");
    testInvalidExpression("'a' || false");
    testInvalidExpression("true || \"a\"");
    testInvalidExpression("\"a\" || false");

    testInvalidExpression("1 || 2");
    testInvalidExpression("1L || 2");
    testInvalidExpression("1 || 2L");
    testInvalidExpression("1.5f || 2");
    testInvalidExpression("1 || 1.2f");
    testInvalidExpression("1.5 || 2");
    testInvalidExpression("1 || 1.2");
    testInvalidExpression("'a' || 2");
    testInvalidExpression("1 || 'a'");
    testInvalidExpression("\"a\" || 2");
    testInvalidExpression("1 || \"a\"");

    testInvalidExpression("1L || 2L");
    testInvalidExpression("1.2f || 2L");
    testInvalidExpression("1L || 1.5f");
    testInvalidExpression("1L || 1.2");
    testInvalidExpression("1.5 || 2L");
    testInvalidExpression("1L || 'a'");
    testInvalidExpression("'a' || 2L");
    testInvalidExpression("1L || \"a\"");
    testInvalidExpression("\"a\" || 2L");

    testInvalidExpression("1.2f || 1.5f");
    testInvalidExpression("1.2 || 1.5f");
    testInvalidExpression("1.2f || 1.5");
    testInvalidExpression("'a' || 1.5f");
    testInvalidExpression("1.2f || 'a'");
    testInvalidExpression("\"a\" || 1.5f");
    testInvalidExpression("1.2f || \"a\"");

    testInvalidExpression("1.2 || 1.5");
    testInvalidExpression("'a' || 1.5");
    testInvalidExpression("1.2 || 'a'");
    testInvalidExpression("\"a\" || 1.5");
    testInvalidExpression("1.2 || \"a\"");

    testInvalidExpression("'a' || 'a'");
    testInvalidExpression("\"a\" || 'a'");
    testInvalidExpression("'a' || \"a\"");

    testInvalidExpression("\"a\" || \"a\"");
  }

  @Test
  public void testConditionalExpression() {
    testValidExpression("(true) ? 1 : 2", ValueFactory.createValue(1));
    testValidExpression("5 <= 10%5 || !true && true ? (3 + 2 * 2) / 14.0 : ((1 > 2L) && ('z' <= 15.243f))", ValueFactory.createValue(false));
  }

  @Test
  public void testCombinedExpressions() {
    testValidExpression("((1 > 2L) && ('z' <= 15.243f)) || true", ValueFactory.createValue(true));
    testValidExpression("(3 + 2 * 2) / 14.0", ValueFactory.createValue(0.5));
    testValidExpression("true && false || !true", ValueFactory.createValue(false));
  }
}
