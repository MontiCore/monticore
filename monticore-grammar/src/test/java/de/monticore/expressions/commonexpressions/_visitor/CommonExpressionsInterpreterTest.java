/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.commonexpressions._visitor;

import de.monticore.expressions.AbstractExpressionInterpreterTest;
import de.monticore.interpreter.MIValueFactory;
import org.junit.jupiter.api.Test;

public class CommonExpressionsInterpreterTest extends AbstractExpressionInterpreterTest {

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

    testValidExpression("1 + 2", MIValueFactory.createValue(3));
    testValidExpression("1L + 2", MIValueFactory.createValue(3L));
    testValidExpression("1 + 2L", MIValueFactory.createValue(3L));
    testValidExpression("1.5f + 2", MIValueFactory.createValue(3.5f));
    testValidExpression("1 + 1.2f", MIValueFactory.createValue(2.2f));
    testValidExpression("1.5 + 2", MIValueFactory.createValue(3.5));
    testValidExpression("1 + 1.2", MIValueFactory.createValue(2.2));
    testValidExpression("'a' + 2", MIValueFactory.createValue(99));
    testValidExpression("1 + 'a'", MIValueFactory.createValue(98));

    testValidExpression("1L + 2L", MIValueFactory.createValue(3L));
    testValidExpression("1.2f + 2L", MIValueFactory.createValue(3.2f));
    testValidExpression("1L + 1.5f", MIValueFactory.createValue(2.5f));
    testValidExpression("1L + 1.2", MIValueFactory.createValue(2.2));
    testValidExpression("1.5 + 2L", MIValueFactory.createValue(3.5));
    testValidExpression("1L + 'a'", MIValueFactory.createValue(98L));
    testValidExpression("'a' + 2L", MIValueFactory.createValue(99L));

    testValidExpression("1.2f + 1.5f", MIValueFactory.createValue(2.7f));
    testValidExpression("1.2 + 1.5f", MIValueFactory.createValue(2.7));
    testValidExpression("1.2f + 1.5", MIValueFactory.createValue(2.7));
    testValidExpression("'a' + 1.5f", MIValueFactory.createValue(98.5f));
    testValidExpression("1.2f + 'a'", MIValueFactory.createValue(98.2f));

    testValidExpression("1.2 + 1.5", MIValueFactory.createValue(2.7));
    testValidExpression("'a' + 1.5", MIValueFactory.createValue(98.5));
    testValidExpression("1.2 + 'a'", MIValueFactory.createValue(98.2));

    testValidExpression("'a' + 'a'", MIValueFactory.createValue(194));
  }

  @Test
  public void testInterpretBracketExpression() {
    testValidExpression("(true)", MIValueFactory.createValue(true));
    testValidExpression("(1)", MIValueFactory.createValue(1));
    testValidExpression("(2L)", MIValueFactory.createValue(2L));
    testValidExpression("(2.5f)", MIValueFactory.createValue(2.5f));
    testValidExpression("(3.14)", MIValueFactory.createValue(3.14));
    testValidExpression("('a')", MIValueFactory.createValue('a'));
  }

  @Test
  public void testInterpretMinusPrefixExpression() {
      testInvalidExpression("-(true)");
      testValidExpression("-(1)", MIValueFactory.createValue(-1));
      testValidExpression("-(2L)", MIValueFactory.createValue(-2L));
      testValidExpression("-(2.5f)", MIValueFactory.createValue(-2.5f));
      testValidExpression("-(3.14)", MIValueFactory.createValue(-3.14));
      testValidExpression("-('a')", MIValueFactory.createValue(-'a'));
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


    testValidExpression("1 - 2", MIValueFactory.createValue(-1));
    testValidExpression("1L - 2", MIValueFactory.createValue(-1L));
    testValidExpression("1 - 2L", MIValueFactory.createValue(-1L));
    testValidExpression("1.5f - 2", MIValueFactory.createValue(-0.5f));
    testValidExpression("1 - 1.2f", MIValueFactory.createValue(-0.2f));
    testValidExpression("1.5 - 2", MIValueFactory.createValue(-0.5));
    testValidExpression("1 - 1.2", MIValueFactory.createValue(-0.2));
    testValidExpression("'a' - 2", MIValueFactory.createValue(95));
    testValidExpression("1 - 'a'", MIValueFactory.createValue(-96));

    testValidExpression("1L - 2L", MIValueFactory.createValue(-1L));
    testValidExpression("1.2f - 2L", MIValueFactory.createValue(-0.8f));
    testValidExpression("1L - 1.5f", MIValueFactory.createValue(-0.5f));
    testValidExpression("1L - 1.2", MIValueFactory.createValue(-0.2));
    testValidExpression("1.5 - 2L", MIValueFactory.createValue(-0.5));
    testValidExpression("1L - 'a'", MIValueFactory.createValue(-96L));
    testValidExpression("'a' - 2L", MIValueFactory.createValue(95L));

    testValidExpression("1.2f - 1.5f", MIValueFactory.createValue(-0.3f));
    testValidExpression("1.2 - 1.5f", MIValueFactory.createValue(-0.3));
    testValidExpression("1.2f - 1.5", MIValueFactory.createValue(-0.3));
    testValidExpression("'a' - 1.5f", MIValueFactory.createValue(95.5f));
    testValidExpression("1.2f - 'a'", MIValueFactory.createValue(-95.8f));

    testValidExpression("1.2 - 1.5", MIValueFactory.createValue(-0.3));
    testValidExpression("'a' - 1.5", MIValueFactory.createValue(95.5));
    testValidExpression("1.2 - 'a'", MIValueFactory.createValue(-95.8));

    testValidExpression("'a' - 'a'", MIValueFactory.createValue(0));
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

    testValidExpression("1 * 2", MIValueFactory.createValue(2));
    testValidExpression("1L * 2", MIValueFactory.createValue(2L));
    testValidExpression("1 * 2L", MIValueFactory.createValue(2L));
    testValidExpression("1.5f * 2", MIValueFactory.createValue(3.f));
    testValidExpression("1 * 1.2f", MIValueFactory.createValue(1.2f));
    testValidExpression("1.5 * 2", MIValueFactory.createValue(3.));
    testValidExpression("1 * 1.2", MIValueFactory.createValue(1.2));
    testValidExpression("'a' * 2", MIValueFactory.createValue(194));
    testValidExpression("1 * 'a'", MIValueFactory.createValue(97));

    testValidExpression("1L * 2L", MIValueFactory.createValue(2L));
    testValidExpression("1.2f * 2L", MIValueFactory.createValue(2.4f));
    testValidExpression("1L * 1.5f", MIValueFactory.createValue(1.5f));
    testValidExpression("1L * 1.2", MIValueFactory.createValue(1.2));
    testValidExpression("1.5 * 2L", MIValueFactory.createValue(3.0));
    testValidExpression("1L * 'a'", MIValueFactory.createValue(97L));
    testValidExpression("'a' * 2L", MIValueFactory.createValue(194L));

    testValidExpression("1.2f * 1.5f", MIValueFactory.createValue(1.8f));
    testValidExpression("1.2 * 1.5f", MIValueFactory.createValue(1.8));
    testValidExpression("1.2f * 1.5", MIValueFactory.createValue(1.8));
    testValidExpression("'a' * 1.5f", MIValueFactory.createValue(145.5f));
    testValidExpression("1.2f * 'a'", MIValueFactory.createValue(116.4f));

    testValidExpression("1.2 * 1.5", MIValueFactory.createValue(1.8));
    testValidExpression("'a' * 1.5", MIValueFactory.createValue(145.5));
    testValidExpression("1.2 * 'a'", MIValueFactory.createValue(116.4));

    testValidExpression("'a' * 'a'", MIValueFactory.createValue(9409));
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

    testValidExpression("1 / 2", MIValueFactory.createValue(0));
    testValidExpression("1L / 2", MIValueFactory.createValue(0L));
    testValidExpression("1 / 2L", MIValueFactory.createValue(0L));
    testValidExpression("1.5f / 2", MIValueFactory.createValue(0.75f));
    testValidExpression("3 / 1.5f", MIValueFactory.createValue(2.f));
    testValidExpression("1.5 / 2", MIValueFactory.createValue(0.75));
    testValidExpression("3 / 1.5", MIValueFactory.createValue(2.));
    testValidExpression("'a' / 2", MIValueFactory.createValue(48));
    testValidExpression("1 / 'a'", MIValueFactory.createValue(0));

    testValidExpression("1L / 2L", MIValueFactory.createValue(0L));
    testValidExpression("1.2f / 2L", MIValueFactory.createValue(0.6f));
    testValidExpression("3L / 1.5f", MIValueFactory.createValue(2.f));
    testValidExpression("3L / 1.5", MIValueFactory.createValue(2.));
    testValidExpression("3.0 / 2L", MIValueFactory.createValue(1.5));
    testValidExpression("1L / 'a'", MIValueFactory.createValue(0L));
    testValidExpression("'a' / 2L", MIValueFactory.createValue(48L));

    testValidExpression("1.2f / 1.5f", MIValueFactory.createValue(0.8f));
    testValidExpression("1.2 / 1.5f", MIValueFactory.createValue(0.8));
    testValidExpression("1.2f / 1.5", MIValueFactory.createValue(0.8));
    testValidExpression("'a' / 0.5f", MIValueFactory.createValue(194.f));
    testValidExpression("194.0f / 'a'", MIValueFactory.createValue(2.f));

    testValidExpression("1.2 / 1.5", MIValueFactory.createValue(0.8));
    testValidExpression("'a' / 2.0", MIValueFactory.createValue(48.5));
    testValidExpression("97.0 / 'a'", MIValueFactory.createValue(1.));

    testValidExpression("'a' / 'a'", MIValueFactory.createValue(1));

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

    testValidExpression("1 % 2", MIValueFactory.createValue(1));
    testValidExpression("1L % 2", MIValueFactory.createValue(1L));
    testValidExpression("1 % 2L", MIValueFactory.createValue(1L));
    testValidExpression("1.5f % 2", MIValueFactory.createValue(1.5f));
    testValidExpression("1 % 1.2f", MIValueFactory.createValue(1.0f));
    testValidExpression("1.5 % 2", MIValueFactory.createValue(1.5));
    testValidExpression("1 % 1.2", MIValueFactory.createValue(1.0));
    testValidExpression("'a' % 2", MIValueFactory.createValue(1));
    testValidExpression("1 % 'a'", MIValueFactory.createValue(1));

    testValidExpression("1L % 2L", MIValueFactory.createValue(1L));
    testValidExpression("1.2f % 2L", MIValueFactory.createValue(1.2f));
    testValidExpression("1L % 1.5f", MIValueFactory.createValue(1.0f));
    testValidExpression("1L % 1.2", MIValueFactory.createValue(1.0));
    testValidExpression("1.5 % 2L", MIValueFactory.createValue(1.5));
    testValidExpression("1L % 'a'", MIValueFactory.createValue(1L));
    testValidExpression("'a' % 2L", MIValueFactory.createValue(1L));

    testValidExpression("1.2f % 1.5f", MIValueFactory.createValue(1.2f));
    testValidExpression("1.2 % 1.5f", MIValueFactory.createValue(1.2));
    testValidExpression("1.2f % 1.5", MIValueFactory.createValue(1.2));
    testValidExpression("'a' % 1.5f", MIValueFactory.createValue(1.0f));
    testValidExpression("1.2f % 'a'", MIValueFactory.createValue(1.2f));

    testValidExpression("1.2 % 1.5", MIValueFactory.createValue(1.2));
    testValidExpression("'a' % 1.5", MIValueFactory.createValue(1.0));
    testValidExpression("1.2 % 'a'", MIValueFactory.createValue(1.2));

    testValidExpression("'a' % 'a'", MIValueFactory.createValue(0));
  }

  @Test
  public void testInterpretEqualsExpression() {
    testValidExpression("true == false", MIValueFactory.createValue(false));
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

    testValidExpression("1 == 2", MIValueFactory.createValue(false));
    testValidExpression("1L == 2", MIValueFactory.createValue(false));
    testValidExpression("1 == 2L", MIValueFactory.createValue(false));
    testValidExpression("1.5f == 2", MIValueFactory.createValue(false));
    testValidExpression("1 == 1.2f", MIValueFactory.createValue(false));
    testValidExpression("1.5 == 2", MIValueFactory.createValue(false));
    testValidExpression("1 == 1.2", MIValueFactory.createValue(false));
    testValidExpression("'a' == 2", MIValueFactory.createValue(false));
    testValidExpression("1 == 'a'", MIValueFactory.createValue(false));

    testValidExpression("1L == 2L", MIValueFactory.createValue(false));
    testValidExpression("1.2f == 2L", MIValueFactory.createValue(false));
    testValidExpression("1L == 1.5f", MIValueFactory.createValue(false));
    testValidExpression("1L == 1.2", MIValueFactory.createValue(false));
    testValidExpression("1.5 == 2L", MIValueFactory.createValue(false));
    testValidExpression("1L == 'a'", MIValueFactory.createValue(false));
    testValidExpression("'a' == 2L", MIValueFactory.createValue(false));

    testValidExpression("1.2f == 1.5f", MIValueFactory.createValue(false));
    testValidExpression("1.2 == 1.5f", MIValueFactory.createValue(false));
    testValidExpression("1.2f == 1.5", MIValueFactory.createValue(false));
    testValidExpression("'a' == 1.5f", MIValueFactory.createValue(false));
    testValidExpression("1.2f == 'a'", MIValueFactory.createValue(false));

    testValidExpression("1.2 == 1.5", MIValueFactory.createValue(false));
    testValidExpression("'a' == 1.5", MIValueFactory.createValue(false));
    testValidExpression("1.2 == 'a'", MIValueFactory.createValue(false));

    testValidExpression("'a' == 'a'", MIValueFactory.createValue(true));
  }

  @Test
  public void testInterpretNotEqualsExpression() {
    testValidExpression("true != false", MIValueFactory.createValue(true));
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
    
    testValidExpression("1 != 2", MIValueFactory.createValue(true));
    testValidExpression("1L != 2", MIValueFactory.createValue(true));
    testValidExpression("1 != 2L", MIValueFactory.createValue(true));
    testValidExpression("1.5f != 2", MIValueFactory.createValue(true));
    testValidExpression("1 != 1.2f", MIValueFactory.createValue(true));
    testValidExpression("1.5 != 2", MIValueFactory.createValue(true));
    testValidExpression("1 != 1.2", MIValueFactory.createValue(true));
    testValidExpression("'a' != 2", MIValueFactory.createValue(true));
    testValidExpression("1 != 'a'", MIValueFactory.createValue(true));

    testValidExpression("1L != 2L", MIValueFactory.createValue(true));
    testValidExpression("1.2f != 2L", MIValueFactory.createValue(true));
    testValidExpression("1L != 1.5f", MIValueFactory.createValue(true));
    testValidExpression("1L != 1.2", MIValueFactory.createValue(true));
    testValidExpression("1.5 != 2L", MIValueFactory.createValue(true));
    testValidExpression("1L != 'a'", MIValueFactory.createValue(true));
    testValidExpression("'a' != 2L", MIValueFactory.createValue(true));

    testValidExpression("1.2f != 1.5f", MIValueFactory.createValue(true));
    testValidExpression("1.2 != 1.5f", MIValueFactory.createValue(true));
    testValidExpression("1.2f != 1.5", MIValueFactory.createValue(true));
    testValidExpression("'a' != 1.5f", MIValueFactory.createValue(true));
    testValidExpression("1.2f != 'a'", MIValueFactory.createValue(true));

    testValidExpression("1.2 != 1.5", MIValueFactory.createValue(true));
    testValidExpression("'a' != 1.5", MIValueFactory.createValue(true));
    testValidExpression("1.2 != 'a'", MIValueFactory.createValue(true));

    testValidExpression("'a' != 'a'", MIValueFactory.createValue(false));
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

    testValidExpression("1 < 2", MIValueFactory.createValue(true));
    testValidExpression("1L < 2", MIValueFactory.createValue(true));
    testValidExpression("1 < 2L", MIValueFactory.createValue(true));
    testValidExpression("1.5f < 2", MIValueFactory.createValue(true));
    testValidExpression("1 < 1.2f", MIValueFactory.createValue(true));
    testValidExpression("1.5 < 2", MIValueFactory.createValue(true));
    testValidExpression("1 < 1.2", MIValueFactory.createValue(true));
    testValidExpression("'a' < 2", MIValueFactory.createValue(false));
    testValidExpression("1 < 'a'", MIValueFactory.createValue(true));

    testValidExpression("1L < 2L", MIValueFactory.createValue(true));
    testValidExpression("1.2f < 2L", MIValueFactory.createValue(true));
    testValidExpression("1L < 1.5f", MIValueFactory.createValue(true));
    testValidExpression("1L < 1.2", MIValueFactory.createValue(true));
    testValidExpression("1.5 < 2L", MIValueFactory.createValue(true));
    testValidExpression("1L < 'a'", MIValueFactory.createValue(true));
    testValidExpression("'a' < 2L", MIValueFactory.createValue(false));

    testValidExpression("1.2f < 1.5f", MIValueFactory.createValue(true));
    testValidExpression("1.2 < 1.5f", MIValueFactory.createValue(true));
    testValidExpression("1.2f < 1.5", MIValueFactory.createValue(true));
    testValidExpression("'a' < 1.5f", MIValueFactory.createValue(false));
    testValidExpression("1.2f < 'a'", MIValueFactory.createValue(true));

    testValidExpression("1.2 < 1.5", MIValueFactory.createValue(true));
    testValidExpression("'a' < 1.5", MIValueFactory.createValue(false));
    testValidExpression("1.2 < 'a'", MIValueFactory.createValue(true));

    testValidExpression("'a' < 'a'", MIValueFactory.createValue(false));
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

    testValidExpression("1 > 2", MIValueFactory.createValue(false));
    testValidExpression("1L > 2", MIValueFactory.createValue(false));
    testValidExpression("1 > 2L", MIValueFactory.createValue(false));
    testValidExpression("1.5f > 2", MIValueFactory.createValue(false));
    testValidExpression("1 > 1.2f", MIValueFactory.createValue(false));
    testValidExpression("1.5 > 2", MIValueFactory.createValue(false));
    testValidExpression("1 > 1.2", MIValueFactory.createValue(false));
    testValidExpression("'a' > 2", MIValueFactory.createValue(true));
    testValidExpression("1 > 'a'", MIValueFactory.createValue(false));

    testValidExpression("1L > 2L", MIValueFactory.createValue(false));
    testValidExpression("1.2f > 2L", MIValueFactory.createValue(false));
    testValidExpression("1L > 1.5f", MIValueFactory.createValue(false));
    testValidExpression("1L > 1.2", MIValueFactory.createValue(false));
    testValidExpression("1.5 > 2L", MIValueFactory.createValue(false));
    testValidExpression("1L > 'a'", MIValueFactory.createValue(false));
    testValidExpression("'a' > 2L", MIValueFactory.createValue(true));

    testValidExpression("1.2f > 1.5f", MIValueFactory.createValue(false));
    testValidExpression("1.2 > 1.5f", MIValueFactory.createValue(false));
    testValidExpression("1.2f > 1.5", MIValueFactory.createValue(false));
    testValidExpression("'a' > 1.5f", MIValueFactory.createValue(true));
    testValidExpression("1.2f > 'a'", MIValueFactory.createValue(false));

    testValidExpression("1.2 > 1.5", MIValueFactory.createValue(false));
    testValidExpression("'a' > 1.5", MIValueFactory.createValue(true));
    testValidExpression("1.2 > 'a'", MIValueFactory.createValue(false));

    testValidExpression("'a' > 'a'", MIValueFactory.createValue(false));
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

    testValidExpression("1 >= 2", MIValueFactory.createValue(false));
    testValidExpression("1L >= 2", MIValueFactory.createValue(false));
    testValidExpression("1 >= 2L", MIValueFactory.createValue(false));
    testValidExpression("1.5f >= 2", MIValueFactory.createValue(false));
    testValidExpression("1 >= 1.2f", MIValueFactory.createValue(false));
    testValidExpression("1.5 >= 2", MIValueFactory.createValue(false));
    testValidExpression("1 >= 1.2", MIValueFactory.createValue(false));
    testValidExpression("'a' >= 2", MIValueFactory.createValue(true));
    testValidExpression("1 >= 'a'", MIValueFactory.createValue(false));

    testValidExpression("1L >= 2L", MIValueFactory.createValue(false));
    testValidExpression("1.2f >= 2L", MIValueFactory.createValue(false));
    testValidExpression("1L >= 1.5f", MIValueFactory.createValue(false));
    testValidExpression("1L >= 1.2", MIValueFactory.createValue(false));
    testValidExpression("1.5 >= 2L", MIValueFactory.createValue(false));
    testValidExpression("1L >= 'a'", MIValueFactory.createValue(false));
    testValidExpression("'a' >= 2L", MIValueFactory.createValue(true));

    testValidExpression("1.2f >= 1.5f", MIValueFactory.createValue(false));
    testValidExpression("1.2 >= 1.5f", MIValueFactory.createValue(false));
    testValidExpression("1.2f >= 1.5", MIValueFactory.createValue(false));
    testValidExpression("'a' >= 1.5f", MIValueFactory.createValue(true));
    testValidExpression("1.2f >= 'a'", MIValueFactory.createValue(false));

    testValidExpression("1.2 >= 1.5", MIValueFactory.createValue(false));
    testValidExpression("'a' >= 1.5", MIValueFactory.createValue(true));
    testValidExpression("1.2 >= 'a'", MIValueFactory.createValue(false));

    testValidExpression("'a' >= 'a'", MIValueFactory.createValue(true));
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

    testValidExpression("1 <= 2", MIValueFactory.createValue(true));
    testValidExpression("1L <= 2", MIValueFactory.createValue(true));
    testValidExpression("1 <= 2L", MIValueFactory.createValue(true));
    testValidExpression("1.5f <= 2", MIValueFactory.createValue(true));
    testValidExpression("1 <= 1.2f", MIValueFactory.createValue(true));
    testValidExpression("1.5 <= 2", MIValueFactory.createValue(true));
    testValidExpression("1 <= 1.2", MIValueFactory.createValue(true));
    testValidExpression("'a' <= 2", MIValueFactory.createValue(false));
    testValidExpression("1 <= 'a'", MIValueFactory.createValue(true));

    testValidExpression("1L <= 2L", MIValueFactory.createValue(true));
    testValidExpression("1.2f <= 2L", MIValueFactory.createValue(true));
    testValidExpression("1L <= 1.5f", MIValueFactory.createValue(true));
    testValidExpression("1L <= 1.2", MIValueFactory.createValue(true));
    testValidExpression("1.5 <= 2L", MIValueFactory.createValue(true));
    testValidExpression("1L <= 'a'", MIValueFactory.createValue(true));
    testValidExpression("'a' <= 2L", MIValueFactory.createValue(false));

    testValidExpression("1.2f <= 1.5f", MIValueFactory.createValue(true));
    testValidExpression("1.2 <= 1.5f", MIValueFactory.createValue(true));
    testValidExpression("1.2f <= 1.5", MIValueFactory.createValue(true));
    testValidExpression("'a' <= 1.5f", MIValueFactory.createValue(false));
    testValidExpression("1.2f <= 'a'", MIValueFactory.createValue(true));

    testValidExpression("1.2 <= 1.5", MIValueFactory.createValue(true));
    testValidExpression("'a' <= 1.5", MIValueFactory.createValue(false));
    testValidExpression("1.2 <= 'a'", MIValueFactory.createValue(true));

    testValidExpression("'a' <= 'a'", MIValueFactory.createValue(true));
  }

  @Test
  public void testInterpretBooleanNotExpression() {
    testInvalidExpression("~true");
    testValidExpression("~1", MIValueFactory.createValue(-2));
    testValidExpression("~-5", MIValueFactory.createValue(4));
    testValidExpression("~708", MIValueFactory.createValue(-709));
    testValidExpression("~1L", MIValueFactory.createValue(-2L));
    testValidExpression("~-5L", MIValueFactory.createValue(4L));
    testValidExpression("~708L", MIValueFactory.createValue(-709L));
    testInvalidExpression("~1.2f");
    testInvalidExpression("~1.5");
    testValidExpression("~'a'", MIValueFactory.createValue(-98));
  }

  @Test
  public void testInterpretLogicalNotExpression() {
    testValidExpression("!true", MIValueFactory.createValue(false));
    testValidExpression("!false", MIValueFactory.createValue(true));
    testInvalidExpression("!1");
    testInvalidExpression("!1L");
    testInvalidExpression("!1.2f");
    testInvalidExpression("!1.5");
    testInvalidExpression("!'a'");
  }

  @Test
  public void testInterpretLogicalAndOpExpression() {
    testValidExpression("true && true", MIValueFactory.createValue(true));
    testValidExpression("false && false", MIValueFactory.createValue(false));
    testValidExpression("true && false", MIValueFactory.createValue(false));
    testValidExpression("false && true", MIValueFactory.createValue(false));
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

    testInvalidExpression("1 && 2");
    testInvalidExpression("1L && 2");
    testInvalidExpression("1 && 2L");
    testInvalidExpression("1.5f && 2");
    testInvalidExpression("1 && 1.2f");
    testInvalidExpression("1.5 && 2");
    testInvalidExpression("1 && 1.2");
    testInvalidExpression("'a' && 2");
    testInvalidExpression("1 && 'a'");

    testInvalidExpression("1L && 2L");
    testInvalidExpression("1.2f && 2L");
    testInvalidExpression("1L && 1.5f");
    testInvalidExpression("1L && 1.2");
    testInvalidExpression("1.5 && 2L");
    testInvalidExpression("1L && 'a'");
    testInvalidExpression("'a' && 2L");

    testInvalidExpression("1.2f && 1.5f");
    testInvalidExpression("1.2 && 1.5f");
    testInvalidExpression("1.2f && 1.5");
    testInvalidExpression("'a' && 1.5f");
    testInvalidExpression("1.2f && 'a'");

    testInvalidExpression("1.2 && 1.5");
    testInvalidExpression("'a' && 1.5");
    testInvalidExpression("1.2 && 'a'");

    testInvalidExpression("'a' && 'a'");
  }

  @Test
  public void testInterpretLogicalOrOpExpression() {
    testValidExpression("true || true", MIValueFactory.createValue(true));
    testValidExpression("false || false", MIValueFactory.createValue(false));
    testValidExpression("true || false", MIValueFactory.createValue(true));
    testValidExpression("false || true", MIValueFactory.createValue(true));
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

    testInvalidExpression("1 || 2");
    testInvalidExpression("1L || 2");
    testInvalidExpression("1 || 2L");
    testInvalidExpression("1.5f || 2");
    testInvalidExpression("1 || 1.2f");
    testInvalidExpression("1.5 || 2");
    testInvalidExpression("1 || 1.2");
    testInvalidExpression("'a' || 2");
    testInvalidExpression("1 || 'a'");

    testInvalidExpression("1L || 2L");
    testInvalidExpression("1.2f || 2L");
    testInvalidExpression("1L || 1.5f");
    testInvalidExpression("1L || 1.2");
    testInvalidExpression("1.5 || 2L");
    testInvalidExpression("1L || 'a'");
    testInvalidExpression("'a' || 2L");

    testInvalidExpression("1.2f || 1.5f");
    testInvalidExpression("1.2 || 1.5f");
    testInvalidExpression("1.2f || 1.5");
    testInvalidExpression("'a' || 1.5f");
    testInvalidExpression("1.2f || 'a'");

    testInvalidExpression("1.2 || 1.5");
    testInvalidExpression("'a' || 1.5");
    testInvalidExpression("1.2 || 'a'");

    testInvalidExpression("'a' || 'a'");
  }

  @Test
  public void testConditionalExpression() {
    testValidExpression("(true) ? 1 : 2", MIValueFactory.createValue(1));

    // has result of union-type
    testValidExpression("5 <= 10%5 || !true && true ? (3 + 2 * 2) / 14.0 : ((1 > 2L) && ('z' <= 15.243f))",
            MIValueFactory.createValue(false));
  }

  @Test
  public void testCombinedExpressions() {
    testValidExpression("((1 > 2L) && ('z' <= 15.243f)) || true", MIValueFactory.createValue(true));
    testValidExpression("(3 + 2 * 2) / 14.0", MIValueFactory.createValue(0.5));
    testValidExpression("true && false || !true", MIValueFactory.createValue(false));
  }
}
