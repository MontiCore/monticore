/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.commonexpressions._visitor;

import de.monticore.expressions.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import de.monticore.expressions.combineexpressionswithliterals._parser.CombineExpressionsWithLiteralsParser;
import de.monticore.expressions.combineexpressionswithliterals._visitor.CombineExpressionsWithLiteralsInterpreter;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.interpreter.Value;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static junit.framework.TestCase.*;

public class CommonExpressionsInterpreterTest {
  protected static final double delta = 0.000001;

  @Before
  public void before() {
    CombineExpressionsWithLiteralsMill.reset();
    CombineExpressionsWithLiteralsMill.init();
    LogStub.init();
    Log.clearFindings();
    Log.enableFailQuick(false);

    /*
    DefsTypesForTests.setup();
    InterpretationResFactory factory = new InterpretationResFactory();
    interpreter.store(CombineExpressionsWithLiteralsMill.variableSymbolBuilder()
        .setType(DefsTypesForTests._personSymType)
        .setName("a")
        .setFullName("a")
        .setPackageName("")
        .setEnclosingScope(CombineExpressionsWithLiteralsMill.globalScope())
        .setAccessModifier(AccessModifier.ALL_INCLUSION)
        .build(), factory.createInterpretationRes(PERSON_A));
    interpreter.store(CombineExpressionsWithLiteralsMill.variableSymbolBuilder()
        .setType(DefsTypesForTests._personSymType)
        .setName("b")
        .setFullName("b")
        .setPackageName("")
        .setEnclosingScope(CombineExpressionsWithLiteralsMill.globalScope())
        .setAccessModifier(AccessModifier.ALL_INCLUSION)
        .build(), factory.createInterpretationRes(PERSON_B));
    interpreter.store(CombineExpressionsWithLiteralsMill.variableSymbolBuilder()
        .setType(DefsTypesForTests._personSymType)
        .setName("c")
        .setFullName("c")
        .setPackageName("")
        .setEnclosingScope(CombineExpressionsWithLiteralsMill.globalScope())
        .setAccessModifier(AccessModifier.ALL_INCLUSION)
        .build(), factory.createInterpretationRes(PERSON_C));
    */
  }

  @Test
  public void testInterpretPlusExpression() throws IOException {
    Value result;
    assertEquals(Log.getFindings().size(), 0);
    result = parseExpressionAndInterpret("true + false");
    assertEquals(Log.getFindings().size(), 1);
    result = parseExpressionAndInterpret("true + 1");
    assertEquals(Log.getFindings().size(), 2);
    result = parseExpressionAndInterpret("1 + false");
    assertEquals(Log.getFindings().size(), 3);
    result = parseExpressionAndInterpret("true + 1L");
    assertEquals(Log.getFindings().size(), 4);
    result = parseExpressionAndInterpret("1L + false");
    assertEquals(Log.getFindings().size(), 5);
    result = parseExpressionAndInterpret("true + 1.2f");
    assertEquals(Log.getFindings().size(), 6);
    result = parseExpressionAndInterpret("1.5f + false");
    assertEquals(Log.getFindings().size(), 7);
    result = parseExpressionAndInterpret("true + 1.2");
    assertEquals(Log.getFindings().size(), 8);
    result = parseExpressionAndInterpret("1.5 + false");
    assertEquals(Log.getFindings().size(), 9);
    result = parseExpressionAndInterpret("true + 'a'");
    assertEquals(Log.getFindings().size(), 10);
    result = parseExpressionAndInterpret("'a' + false");
    assertEquals(Log.getFindings().size(), 11);
    result = parseExpressionAndInterpret("true + \"a\"");
    assertEquals(result.asString(), "truea");
    result = parseExpressionAndInterpret("\"a\" + false");
    assertEquals(result.asString(), "afalse");

    result = parseExpressionAndInterpret("1 + 2");
    assertEquals(result.asInt(), 3);
    result = parseExpressionAndInterpret("1L + 2");
    assertEquals(result.asLong(), 3L);
    result = parseExpressionAndInterpret("1 + 2L");
    assertEquals(result.asLong(), 3L);
    result = parseExpressionAndInterpret("1.5f + 2");
    assertEquals(result.asFloat(), 3.5f);
    result = parseExpressionAndInterpret("1 + 1.2f");
    assertEquals(result.asFloat(), 2.2f);
    result = parseExpressionAndInterpret("1.5 + 2");
    assertEquals(result.asDouble(), 3.5);
    result = parseExpressionAndInterpret("1 + 1.2");
    assertEquals(result.asDouble(), 2.2);
    result = parseExpressionAndInterpret("'a' + 2");
    assertEquals(result.asInt(), 99);
    result = parseExpressionAndInterpret("1 + 'a'");
    assertEquals(result.asInt(), 98);
    result = parseExpressionAndInterpret("\"a\" + 2");
    assertEquals(result.asString(), "a2");
    result = parseExpressionAndInterpret("1 + \"a\"");
    assertEquals(result.asString(), "1a");

    result = parseExpressionAndInterpret("1L + 2L");
    assertEquals(result.asLong(), 3L);
    result = parseExpressionAndInterpret("1.2f + 2L");
    assertEquals(result.asFloat(), 3.2f);
    result = parseExpressionAndInterpret("1L + 1.5f");
    assertEquals(result.asFloat(), 2.5f);
    result = parseExpressionAndInterpret("1L + 1.2");
    assertEquals(result.asDouble(), 2.2);
    result = parseExpressionAndInterpret("1.5 + 2L");
    assertEquals(result.asDouble(), 3.5);
    result = parseExpressionAndInterpret("1L + 'a'");
    assertEquals(result.asLong(), 98L);
    result = parseExpressionAndInterpret("'a' + 2L");
    assertEquals(result.asLong(), 99L);
    result = parseExpressionAndInterpret("1L + \"a\"");
    assertEquals(result.asString(), "1a");
    result = parseExpressionAndInterpret("\"a\" + 2L");
    assertEquals(result.asString(), "a2");

    result = parseExpressionAndInterpret("1.2f + 1.5f");
    assertEquals(result.asFloat(), 2.7f);
    result = parseExpressionAndInterpret("1.2 + 1.5f");
    assertEquals(result.asDouble(), 2.7);
    result = parseExpressionAndInterpret("1.2f + 1.5");
    assertEquals(result.asDouble(), 2.7, delta);
    result = parseExpressionAndInterpret("'a' + 1.5f");
    assertEquals(result.asFloat(), 98.5f);
    result = parseExpressionAndInterpret("1.2f + 'a'");
    assertEquals(result.asFloat(), 98.2f);
    result = parseExpressionAndInterpret("\"a\" + 1.5f");
    assertEquals(result.asString(), "a1.5");
    result = parseExpressionAndInterpret("1.2f + \"a\"");
    assertEquals(result.asString(), "1.2a");

    result = parseExpressionAndInterpret("1.2 + 1.5");
    assertEquals(result.asDouble(), 2.7);
    result = parseExpressionAndInterpret("'a' + 1.5");
    assertEquals(result.asDouble(), 98.5);
    result = parseExpressionAndInterpret("1.2 + 'a'");
    assertEquals(result.asDouble(), 98.2);
    result = parseExpressionAndInterpret("\"a\" + 1.5");
    assertEquals(result.asString(), "a1.5");
    result = parseExpressionAndInterpret("1.2 + \"a\"");
    assertEquals(result.asString(), "1.2a");

    result = parseExpressionAndInterpret("'a' + 'a'");
    assertEquals(result.asInt(), 194);
    result = parseExpressionAndInterpret("\"a\" + 'a'");
    assertEquals(result.asString(), "aa");
    result = parseExpressionAndInterpret("'a' + \"a\"");
    assertEquals(result.asString(), "aa");

    result = parseExpressionAndInterpret("\"a\" + \"a\"");
    assertEquals(result.asString(), "aa");
  }

  @Test
  public void testInterpretBracketExpression() throws IOException {
    Value result;
    assertEquals(Log.getFindings().size(), 0);
    result = parseExpressionAndInterpret("(true)");
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("(1)");
    assertEquals(result.asInt(), 1);
    result = parseExpressionAndInterpret("(2L)");
    assertEquals(result.asLong(), 2L);
    result = parseExpressionAndInterpret("(2.5f)");
    assertEquals(result.asFloat(), 2.5f);
    result = parseExpressionAndInterpret("(3.14)");
    assertEquals(result.asDouble(), 3.14);
    result = parseExpressionAndInterpret("('a')");
    assertEquals(result.asChar(), 'a');
    result = parseExpressionAndInterpret("(\"abc\")");
    assertEquals(result.asString(), "abc");
  }

  @Test
  public void testInterpretMinusExpression() throws IOException {
    Value result;
    assertEquals(Log.getFindings().size(), 0);
    result = parseExpressionAndInterpret("true - false");
    assertEquals(Log.getFindings().size(), 1);
    result = parseExpressionAndInterpret("true - 1");
    assertEquals(Log.getFindings().size(), 2);
    result = parseExpressionAndInterpret("1 - false");
    assertEquals(Log.getFindings().size(), 3);
    result = parseExpressionAndInterpret("true - 1L");
    assertEquals(Log.getFindings().size(), 4);
    result = parseExpressionAndInterpret("1L - false");
    assertEquals(Log.getFindings().size(), 5);
    result = parseExpressionAndInterpret("true - 1.2f");
    assertEquals(Log.getFindings().size(), 6);
    result = parseExpressionAndInterpret("1.5f - false");
    assertEquals(Log.getFindings().size(), 7);
    result = parseExpressionAndInterpret("true - 1.2");
    assertEquals(Log.getFindings().size(), 8);
    result = parseExpressionAndInterpret("1.5 - false");
    assertEquals(Log.getFindings().size(), 9);
    result = parseExpressionAndInterpret("true - 'a'");
    assertEquals(Log.getFindings().size(), 10);
    result = parseExpressionAndInterpret("'a' - false");
    assertEquals(Log.getFindings().size(), 11);
    result = parseExpressionAndInterpret("true - \"a\"");
    assertEquals(Log.getFindings().size(), 12);
    result = parseExpressionAndInterpret("\"a\" - false");
    assertEquals(Log.getFindings().size(), 13);


    result = parseExpressionAndInterpret("1 - 2");
    assertEquals(result.asInt(), -1);
    result = parseExpressionAndInterpret("1L - 2");
    assertEquals(result.asLong(), -1L);
    result = parseExpressionAndInterpret("1 - 2L");
    assertEquals(result.asLong(), -1L);
    result = parseExpressionAndInterpret("1.5f - 2");
    assertEquals(result.asFloat(), -0.5f);
    result = parseExpressionAndInterpret("1 - 1.2f");
    assertEquals(result.asFloat(), -0.2f, delta);
    result = parseExpressionAndInterpret("1.5 - 2");
    assertEquals(result.asDouble(), -0.5);
    result = parseExpressionAndInterpret("1 - 1.2");
    assertEquals(result.asDouble(), -0.2, delta);
    result = parseExpressionAndInterpret("'a' - 2");
    assertEquals(result.asInt(), 95);
    result = parseExpressionAndInterpret("1 - 'a'");
    assertEquals(result.asInt(), -96);
    result = parseExpressionAndInterpret("\"a\" - 2");
    assertEquals(Log.getFindings().size(), 14);
    result = parseExpressionAndInterpret("1 - \"a\"");
    assertEquals(Log.getFindings().size(), 15);

    result = parseExpressionAndInterpret("1L - 2L");
    assertEquals(result.asLong(), -1L);
    result = parseExpressionAndInterpret("1.2f - 2L");
    assertEquals(result.asFloat(), -0.8f, delta);
    result = parseExpressionAndInterpret("1L - 1.5f");
    assertEquals(result.asFloat(), -0.5f);
    result = parseExpressionAndInterpret("1L - 1.2");
    assertEquals(result.asDouble(), -0.2, delta);
    result = parseExpressionAndInterpret("1.5 - 2L");
    assertEquals(result.asDouble(), -0.5);
    result = parseExpressionAndInterpret("1L - 'a'");
    assertEquals(result.asLong(), -96L);
    result = parseExpressionAndInterpret("'a' - 2L");
    assertEquals(result.asLong(), 95L);
    result = parseExpressionAndInterpret("1L - \"a\"");
    assertEquals(Log.getFindings().size(), 16);
    result = parseExpressionAndInterpret("\"a\" - 2L");
    assertEquals(Log.getFindings().size(), 17);

    result = parseExpressionAndInterpret("1.2f - 1.5f");
    assertEquals(result.asFloat(), -0.3f, delta);
    result = parseExpressionAndInterpret("1.2 - 1.5f");
    assertEquals(result.asDouble(), -0.3, delta);
    result = parseExpressionAndInterpret("1.2f - 1.5");
    assertEquals(result.asDouble(), -0.3, delta);
    result = parseExpressionAndInterpret("'a' - 1.5f");
    assertEquals(result.asFloat(), 95.5f);
    result = parseExpressionAndInterpret("1.2f - 'a'");
    assertEquals(result.asFloat(), -95.8f);
    result = parseExpressionAndInterpret("\"a\" - 1.5f");
    assertEquals(Log.getFindings().size(), 18);
    result = parseExpressionAndInterpret("1.2f - \"a\"");
    assertEquals(Log.getFindings().size(), 19);

    result = parseExpressionAndInterpret("1.2 - 1.5");
    assertEquals(result.asDouble(), -0.3, delta);
    result = parseExpressionAndInterpret("'a' - 1.5");
    assertEquals(result.asDouble(), 95.5);
    result = parseExpressionAndInterpret("1.2 - 'a'");
    assertEquals(result.asDouble(), -95.8);
    result = parseExpressionAndInterpret("\"a\" - 1.5");
    assertEquals(Log.getFindings().size(), 20);
    result = parseExpressionAndInterpret("1.2 - \"a\"");
    assertEquals(Log.getFindings().size(), 21);

    result = parseExpressionAndInterpret("'a' - 'a'");
    assertEquals(result.asInt(), 0);
    result = parseExpressionAndInterpret("\"a\" - 'a'");
    assertEquals(Log.getFindings().size(), 22);
    result = parseExpressionAndInterpret("'a' - \"a\"");
    assertEquals(Log.getFindings().size(), 23);

    result = parseExpressionAndInterpret("\"a\" - \"a\"");
    assertEquals(Log.getFindings().size(), 24);
  }

  @Test
  public void testInterpretMultExpression() throws IOException {
    Value result;
    assertEquals(Log.getFindings().size(), 0);
    result = parseExpressionAndInterpret("true * false");
    assertEquals(Log.getFindings().size(), 1);
    result = parseExpressionAndInterpret("true * 1");
    assertEquals(Log.getFindings().size(), 2);
    result = parseExpressionAndInterpret("1 * false");
    assertEquals(Log.getFindings().size(), 3);
    result = parseExpressionAndInterpret("true * 1L");
    assertEquals(Log.getFindings().size(), 4);
    result = parseExpressionAndInterpret("1L * false");
    assertEquals(Log.getFindings().size(), 5);
    result = parseExpressionAndInterpret("true * 1.2f");
    assertEquals(Log.getFindings().size(), 6);
    result = parseExpressionAndInterpret("1.5f * false");
    assertEquals(Log.getFindings().size(), 7);
    result = parseExpressionAndInterpret("true * 1.2");
    assertEquals(Log.getFindings().size(), 8);
    result = parseExpressionAndInterpret("1.5 * false");
    assertEquals(Log.getFindings().size(), 9);
    result = parseExpressionAndInterpret("true * 'a'");
    assertEquals(Log.getFindings().size(), 10);
    result = parseExpressionAndInterpret("'a' * false");
    assertEquals(Log.getFindings().size(), 11);
    result = parseExpressionAndInterpret("true * \"a\"");
    assertEquals(Log.getFindings().size(), 12);
    result = parseExpressionAndInterpret("\"a\" * false");
    assertEquals(Log.getFindings().size(), 13);


    result = parseExpressionAndInterpret("1 * 2");
    assertEquals(result.asInt(), 2);
    result = parseExpressionAndInterpret("1L * 2");
    assertEquals(result.asLong(), 2L);
    result = parseExpressionAndInterpret("1 * 2L");
    assertEquals(result.asLong(), 2L);
    result = parseExpressionAndInterpret("1.5f * 2");
    assertEquals(result.asFloat(), 3.f);
    result = parseExpressionAndInterpret("1 * 1.2f");
    assertEquals(result.asFloat(), 1.2f);
    result = parseExpressionAndInterpret("1.5 * 2");
    assertEquals(result.asDouble(), 3.);
    result = parseExpressionAndInterpret("1 * 1.2");
    assertEquals(result.asDouble(), 1.2);
    result = parseExpressionAndInterpret("'a' * 2");
    assertEquals(result.asInt(), 194);
    result = parseExpressionAndInterpret("1 * 'a'");
    assertEquals(result.asInt(), 97);
    result = parseExpressionAndInterpret("\"a\" * 2");
    assertEquals(Log.getFindings().size(), 14);
    result = parseExpressionAndInterpret("1 * \"a\"");
    assertEquals(Log.getFindings().size(), 15);

    result = parseExpressionAndInterpret("1L * 2L");
    assertEquals(result.asLong(), 2L);
    result = parseExpressionAndInterpret("1.2f * 2L");
    assertEquals(result.asFloat(), 2.4f);
    result = parseExpressionAndInterpret("1L * 1.5f");
    assertEquals(result.asFloat(), 1.5f);
    result = parseExpressionAndInterpret("1L * 1.2");
    assertEquals(result.asDouble(), 1.2);
    result = parseExpressionAndInterpret("1.5 * 2L");
    assertEquals(result.asDouble(), 3.0);
    result = parseExpressionAndInterpret("1L * 'a'");
    assertEquals(result.asLong(), 97L);
    result = parseExpressionAndInterpret("'a' * 2L");
    assertEquals(result.asLong(), 194L);
    result = parseExpressionAndInterpret("1L * \"a\"");
    assertEquals(Log.getFindings().size(), 16);
    result = parseExpressionAndInterpret("\"a\" * 2L");
    assertEquals(Log.getFindings().size(), 17);

    result = parseExpressionAndInterpret("1.2f * 1.5f");
    assertEquals(result.asFloat(), 1.8f, delta);
    result = parseExpressionAndInterpret("1.2 * 1.5f");
    assertEquals(result.asDouble(), 1.8, delta);
    result = parseExpressionAndInterpret("1.2f * 1.5");
    assertEquals(result.asDouble(), 1.8, delta);
    result = parseExpressionAndInterpret("'a' * 1.5f");
    assertEquals(result.asFloat(), 145.5f);
    result = parseExpressionAndInterpret("1.2f * 'a'");
    assertEquals(result.asFloat(), 116.4f);
    result = parseExpressionAndInterpret("\"a\" * 1.5f");
    assertEquals(Log.getFindings().size(), 18);
    result = parseExpressionAndInterpret("1.2f * \"a\"");
    assertEquals(Log.getFindings().size(), 19);

    result = parseExpressionAndInterpret("1.2 * 1.5");
    assertEquals(result.asDouble(), 1.8, delta);
    result = parseExpressionAndInterpret("'a' * 1.5");
    assertEquals(result.asDouble(), 145.5);
    result = parseExpressionAndInterpret("1.2 * 'a'");
    assertEquals(result.asDouble(), 116.4, delta);
    result = parseExpressionAndInterpret("\"a\" * 1.5");
    assertEquals(Log.getFindings().size(), 20);
    result = parseExpressionAndInterpret("1.2 * \"a\"");
    assertEquals(Log.getFindings().size(), 21);

    result = parseExpressionAndInterpret("'a' * 'a'");
    assertEquals(result.asInt(), 9409);
    result = parseExpressionAndInterpret("\"a\" * 'a'");
    assertEquals(Log.getFindings().size(), 22);
    result = parseExpressionAndInterpret("'a' * \"a\"");
    assertEquals(Log.getFindings().size(), 23);

    result = parseExpressionAndInterpret("\"a\" * \"a\"");
    assertEquals(Log.getFindings().size(), 24);
  }

  @Test
  public void testInterpretDivideExpression() throws IOException {
    Value result;
    assertEquals(Log.getFindings().size(), 0);
    result = parseExpressionAndInterpret("true / false");
    assertEquals(Log.getFindings().size(), 1);
    result = parseExpressionAndInterpret("true / 1");
    assertEquals(Log.getFindings().size(), 2);
    result = parseExpressionAndInterpret("1 / false");
    assertEquals(Log.getFindings().size(), 3);
    result = parseExpressionAndInterpret("true / 1L");
    assertEquals(Log.getFindings().size(), 4);
    result = parseExpressionAndInterpret("1L / false");
    assertEquals(Log.getFindings().size(), 5);
    result = parseExpressionAndInterpret("true / 1.2f");
    assertEquals(Log.getFindings().size(), 6);
    result = parseExpressionAndInterpret("1.5f / false");
    assertEquals(Log.getFindings().size(), 7);
    result = parseExpressionAndInterpret("true / 1.2");
    assertEquals(Log.getFindings().size(), 8);
    result = parseExpressionAndInterpret("1.5 / false");
    assertEquals(Log.getFindings().size(), 9);
    result = parseExpressionAndInterpret("true / 'a'");
    assertEquals(Log.getFindings().size(), 10);
    result = parseExpressionAndInterpret("'a' / false");
    assertEquals(Log.getFindings().size(), 11);
    result = parseExpressionAndInterpret("true / \"a\"");
    assertEquals(Log.getFindings().size(), 12);
    result = parseExpressionAndInterpret("\"a\" / false");
    assertEquals(Log.getFindings().size(), 13);


    result = parseExpressionAndInterpret("1 / 2");
    assertEquals(result.asInt(), 0);
    result = parseExpressionAndInterpret("1L / 2");
    assertEquals(result.asLong(), 0L);
    result = parseExpressionAndInterpret("1 / 2L");
    assertEquals(result.asLong(), 0L);
    result = parseExpressionAndInterpret("1.5f / 2");
    assertEquals(result.asFloat(), 0.75f);
    result = parseExpressionAndInterpret("3 / 1.5f");
    assertEquals(result.asFloat(), 2.f);
    result = parseExpressionAndInterpret("1.5 / 2");
    assertEquals(result.asDouble(), 0.75);
    result = parseExpressionAndInterpret("3 / 1.5");
    assertEquals(result.asDouble(), 2.);
    result = parseExpressionAndInterpret("'a' / 2");
    assertEquals(result.asInt(), 48);
    result = parseExpressionAndInterpret("1 / 'a'");
    assertEquals(result.asInt(), 0);
    result = parseExpressionAndInterpret("\"a\" / 2");
    assertEquals(Log.getFindings().size(), 14);
    result = parseExpressionAndInterpret("1 / \"a\"");
    assertEquals(Log.getFindings().size(), 15);

    result = parseExpressionAndInterpret("1L / 2L");
    assertEquals(result.asLong(), 0L);
    result = parseExpressionAndInterpret("1.2f / 2L");
    assertEquals(result.asFloat(), 0.6f);
    result = parseExpressionAndInterpret("3L / 1.5f");
    assertEquals(result.asFloat(), 2.f);
    result = parseExpressionAndInterpret("3L / 1.5");
    assertEquals(result.asDouble(), 2.);
    result = parseExpressionAndInterpret("3.0 / 2L");
    assertEquals(result.asDouble(), 1.5);
    result = parseExpressionAndInterpret("1L / 'a'");
    assertEquals(result.asLong(), 0);
    result = parseExpressionAndInterpret("'a' / 2L");
    assertEquals(result.asLong(), 48);
    result = parseExpressionAndInterpret("1L / \"a\"");
    assertEquals(Log.getFindings().size(), 16);
    result = parseExpressionAndInterpret("\"a\" / 2L");
    assertEquals(Log.getFindings().size(), 17);

    result = parseExpressionAndInterpret("1.2f / 1.5f");
    assertEquals(result.asFloat(), 0.8f);
    result = parseExpressionAndInterpret("1.2 / 1.5f");
    assertEquals(result.asDouble(), 0.8, delta);
    result = parseExpressionAndInterpret("1.2f / 1.5");
    assertEquals(result.asDouble(), 0.8, delta);
    result = parseExpressionAndInterpret("'a' / 0.5f");
    assertEquals(result.asFloat(), 194.f);
    result = parseExpressionAndInterpret("194.0f / 'a'");
    assertEquals(result.asFloat(), 2.f);
    result = parseExpressionAndInterpret("\"a\" / 1.5f");
    assertEquals(Log.getFindings().size(), 18);
    result = parseExpressionAndInterpret("1.2f / \"a\"");
    assertEquals(Log.getFindings().size(), 19);

    result = parseExpressionAndInterpret("1.2 / 1.5");
    assertEquals(result.asDouble(), 0.8, delta);
    result = parseExpressionAndInterpret("'a' / 2.0");
    assertEquals(result.asDouble(), 48.5);
    result = parseExpressionAndInterpret("97.0 / 'a'");
    assertEquals(result.asDouble(), 1.);
    result = parseExpressionAndInterpret("\"a\" / 1.5");
    assertEquals(Log.getFindings().size(), 20);
    result = parseExpressionAndInterpret("1.2 / \"a\"");
    assertEquals(Log.getFindings().size(), 21);

    result = parseExpressionAndInterpret("'a' / 'a'");
    assertEquals(result.asInt(), 1);
    result = parseExpressionAndInterpret("\"a\" / 'a'");
    assertEquals(Log.getFindings().size(), 22);
    result = parseExpressionAndInterpret("'a' / \"a\"");
    assertEquals(Log.getFindings().size(), 23);

    result = parseExpressionAndInterpret("\"a\" / \"a\"");
    assertEquals(Log.getFindings().size(), 24);

    result = parseExpressionAndInterpret("1 / 0");
    assertEquals(Log.getFindings().size(), 25);
    result = parseExpressionAndInterpret("'a' / 0");
    assertEquals(Log.getFindings().size(), 26);
    result = parseExpressionAndInterpret("1L / 0");
    assertEquals(Log.getFindings().size(), 27);
    result = parseExpressionAndInterpret("1 / 0L");
    assertEquals(Log.getFindings().size(), 28);
    result = parseExpressionAndInterpret("1L / 0L");
    assertEquals(Log.getFindings().size(), 29);
    result = parseExpressionAndInterpret("'a' / 0L");
    assertEquals(Log.getFindings().size(), 30);
  }

  @Test
  public void testInterpretModuloExpression() throws IOException {
    Value result;
    assertEquals(Log.getFindings().size(), 0);
    result = parseExpressionAndInterpret("true % false");
    assertEquals(Log.getFindings().size(), 1);
    result = parseExpressionAndInterpret("true % 1");
    assertEquals(Log.getFindings().size(), 2);
    result = parseExpressionAndInterpret("1 % false");
    assertEquals(Log.getFindings().size(), 3);
    result = parseExpressionAndInterpret("true % 1L");
    assertEquals(Log.getFindings().size(), 4);
    result = parseExpressionAndInterpret("1L % false");
    assertEquals(Log.getFindings().size(), 5);
    result = parseExpressionAndInterpret("true % 1.2f");
    assertEquals(Log.getFindings().size(), 6);
    result = parseExpressionAndInterpret("1.5f % false");
    assertEquals(Log.getFindings().size(), 7);
    result = parseExpressionAndInterpret("true % 1.2");
    assertEquals(Log.getFindings().size(), 8);
    result = parseExpressionAndInterpret("1.5 % false");
    assertEquals(Log.getFindings().size(), 9);
    result = parseExpressionAndInterpret("true % 'a'");
    assertEquals(Log.getFindings().size(), 10);
    result = parseExpressionAndInterpret("'a' % false");
    assertEquals(Log.getFindings().size(), 11);
    result = parseExpressionAndInterpret("true % \"a\"");
    assertEquals(Log.getFindings().size(), 12);
    result = parseExpressionAndInterpret("\"a\" % false");
    assertEquals(Log.getFindings().size(), 13);


    result = parseExpressionAndInterpret("1 % 2");
    assertEquals(result.asInt(), 1);
    result = parseExpressionAndInterpret("1L % 2");
    assertEquals(result.asLong(), 1);
    result = parseExpressionAndInterpret("1 % 2L");
    assertEquals(result.asLong(), 1L);
    result = parseExpressionAndInterpret("1.5f % 2");
    assertEquals(result.asFloat(), 1.5f);
    result = parseExpressionAndInterpret("1 % 1.2f");
    assertEquals(result.asFloat(), 1.0f);
    result = parseExpressionAndInterpret("1.5 % 2");
    assertEquals(result.asDouble(), 1.5);
    result = parseExpressionAndInterpret("1 % 1.2");
    assertEquals(result.asDouble(), 1.0);
    result = parseExpressionAndInterpret("'a' % 2");
    assertEquals(result.asInt(), 1);
    result = parseExpressionAndInterpret("1 % 'a'");
    assertEquals(result.asInt(), 1);
    result = parseExpressionAndInterpret("\"a\" % 2");
    assertEquals(Log.getFindings().size(), 14);
    result = parseExpressionAndInterpret("1 % \"a\"");
    assertEquals(Log.getFindings().size(), 15);

    result = parseExpressionAndInterpret("1L % 2L");
    assertEquals(result.asLong(), 1L);
    result = parseExpressionAndInterpret("1.2f % 2L");
    assertEquals(result.asFloat(), 1.2f);
    result = parseExpressionAndInterpret("1L % 1.5f");
    assertEquals(result.asFloat(), 1.0f);
    result = parseExpressionAndInterpret("1L % 1.2");
    assertEquals(result.asDouble(), 1.0);
    result = parseExpressionAndInterpret("1.5 % 2L");
    assertEquals(result.asDouble(), 1.5);
    result = parseExpressionAndInterpret("1L % 'a'");
    assertEquals(result.asLong(), 1L);
    result = parseExpressionAndInterpret("'a' % 2L");
    assertEquals(result.asLong(), 1L);
    result = parseExpressionAndInterpret("1L % \"a\"");
    assertEquals(Log.getFindings().size(), 16);
    result = parseExpressionAndInterpret("\"a\" % 2L");
    assertEquals(Log.getFindings().size(), 17);

    result = parseExpressionAndInterpret("1.2f % 1.5f");
    assertEquals(result.asFloat(), 1.2f, delta);
    result = parseExpressionAndInterpret("1.2 % 1.5f");
    assertEquals(result.asDouble(), 1.2);
    result = parseExpressionAndInterpret("1.2f % 1.5");
    assertEquals(result.asDouble(), 1.2, delta);
    result = parseExpressionAndInterpret("'a' % 1.5f");
    assertEquals(result.asFloat(), 1.0f);
    result = parseExpressionAndInterpret("1.2f % 'a'");
    assertEquals(result.asFloat(), 1.2f);
    result = parseExpressionAndInterpret("\"a\" % 1.5f");
    assertEquals(Log.getFindings().size(), 18);
    result = parseExpressionAndInterpret("1.2f % \"a\"");
    assertEquals(Log.getFindings().size(), 19);

    result = parseExpressionAndInterpret("1.2 % 1.5");
    assertEquals(result.asDouble(), 1.2);
    result = parseExpressionAndInterpret("'a' % 1.5");
    assertEquals(result.asDouble(), 1.0);
    result = parseExpressionAndInterpret("1.2 % 'a'");
    assertEquals(result.asDouble(), 1.2);
    result = parseExpressionAndInterpret("\"a\" % 1.5");
    assertEquals(Log.getFindings().size(), 20);
    result = parseExpressionAndInterpret("1.2 % \"a\"");
    assertEquals(Log.getFindings().size(), 21);

    result = parseExpressionAndInterpret("'a' % 'a'");
    assertEquals(result.asInt(), 0);
    result = parseExpressionAndInterpret("\"a\" % 'a'");
    assertEquals(Log.getFindings().size(), 22);
    result = parseExpressionAndInterpret("'a' % \"a\"");
    assertEquals(Log.getFindings().size(), 23);

    result = parseExpressionAndInterpret("\"a\" % \"a\"");
    assertEquals(Log.getFindings().size(), 24);
  }

  @Test
  public void testInterpretEqualsExpression() throws IOException {
    Value result;
    assertEquals(Log.getFindings().size(), 0);
    result = parseExpressionAndInterpret("true == false");
    assertFalse(result.asBoolean());
    result = parseExpressionAndInterpret("true == 1");
    assertEquals(Log.getFindings().size(), 1);
    result = parseExpressionAndInterpret("1 == false");
    assertEquals(Log.getFindings().size(), 2);
    result = parseExpressionAndInterpret("true == 1L");
    assertEquals(Log.getFindings().size(), 3);
    result = parseExpressionAndInterpret("1L == false");
    assertEquals(Log.getFindings().size(), 4);
    result = parseExpressionAndInterpret("true == 1.2f");
    assertEquals(Log.getFindings().size(), 5);
    result = parseExpressionAndInterpret("1.5f == false");
    assertEquals(Log.getFindings().size(), 6);
    result = parseExpressionAndInterpret("true == 1.2");
    assertEquals(Log.getFindings().size(), 7);
    result = parseExpressionAndInterpret("1.5 == false");
    assertEquals(Log.getFindings().size(), 8);
    result = parseExpressionAndInterpret("true == 'a'");
    assertEquals(Log.getFindings().size(), 9);
    result = parseExpressionAndInterpret("'a' == false");
    assertEquals(Log.getFindings().size(), 10);
    result = parseExpressionAndInterpret("true == \"a\"");
    assertEquals(Log.getFindings().size(), 11);
    result = parseExpressionAndInterpret("\"a\" == false");
    assertEquals(Log.getFindings().size(), 12);


    result = parseExpressionAndInterpret("1 == 2");
    assertFalse(result.asBoolean());
    result = parseExpressionAndInterpret("1L == 2");
    assertFalse(result.asBoolean());
    result = parseExpressionAndInterpret("1 == 2L");
    assertFalse(result.asBoolean());
    result = parseExpressionAndInterpret("1.5f == 2");
    assertFalse(result.asBoolean());
    result = parseExpressionAndInterpret("1 == 1.2f");
    assertFalse(result.asBoolean());
    result = parseExpressionAndInterpret("1.5 == 2");
    assertFalse(result.asBoolean());
    result = parseExpressionAndInterpret("1 == 1.2");
    assertFalse(result.asBoolean());
    result = parseExpressionAndInterpret("'a' == 2");
    assertFalse(result.asBoolean());
    result = parseExpressionAndInterpret("1 == 'a'");
    assertFalse(result.asBoolean());
    result = parseExpressionAndInterpret("\"a\" == 2");
    assertEquals(Log.getFindings().size(), 13);
    result = parseExpressionAndInterpret("1 == \"a\"");
    assertEquals(Log.getFindings().size(), 14);

    result = parseExpressionAndInterpret("1L == 2L");
    assertFalse(result.asBoolean());
    result = parseExpressionAndInterpret("1.2f == 2L");
    assertFalse(result.asBoolean());
    result = parseExpressionAndInterpret("1L == 1.5f");
    assertFalse(result.asBoolean());
    result = parseExpressionAndInterpret("1L == 1.2");
    assertFalse(result.asBoolean());
    result = parseExpressionAndInterpret("1.5 == 2L");
    assertFalse(result.asBoolean());
    result = parseExpressionAndInterpret("1L == 'a'");
    assertFalse(result.asBoolean());
    result = parseExpressionAndInterpret("'a' == 2L");
    assertFalse(result.asBoolean());
    result = parseExpressionAndInterpret("1L == \"a\"");
    assertEquals(Log.getFindings().size(), 15);
    result = parseExpressionAndInterpret("\"a\" == 2L");
    assertEquals(Log.getFindings().size(), 16);

    result = parseExpressionAndInterpret("1.2f == 1.5f");
    assertFalse(result.asBoolean());
    result = parseExpressionAndInterpret("1.2 == 1.5f");
    assertFalse(result.asBoolean());
    result = parseExpressionAndInterpret("1.2f == 1.5");
    assertFalse(result.asBoolean());
    result = parseExpressionAndInterpret("'a' == 1.5f");
    assertFalse(result.asBoolean());
    result = parseExpressionAndInterpret("1.2f == 'a'");
    assertFalse(result.asBoolean());
    result = parseExpressionAndInterpret("\"a\" == 1.5f");
    assertEquals(Log.getFindings().size(), 17);
    result = parseExpressionAndInterpret("1.2f == \"a\"");
    assertEquals(Log.getFindings().size(), 18);

    result = parseExpressionAndInterpret("1.2 == 1.5");
    assertFalse(result.asBoolean());
    result = parseExpressionAndInterpret("'a' == 1.5");
    assertFalse(result.asBoolean());
    result = parseExpressionAndInterpret("1.2 == 'a'");
    assertFalse(result.asBoolean());
    result = parseExpressionAndInterpret("\"a\" == 1.5");
    assertEquals(Log.getFindings().size(), 19);
    result = parseExpressionAndInterpret("1.2 == \"a\"");
    assertEquals(Log.getFindings().size(), 20);

    result = parseExpressionAndInterpret("'a' == 'a'");
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("\"a\" == 'a'");
    assertEquals(Log.getFindings().size(), 21);
    result = parseExpressionAndInterpret("'a' == \"a\"");
    assertEquals(Log.getFindings().size(), 22);

    result = parseExpressionAndInterpret("\"a\" == \"a\"");
    assertFalse(result.asBoolean());
  }

  @Test
  public void testInterpretNotEqualsExpression() throws IOException {
    Value result;
    assertEquals(Log.getFindings().size(), 0);
    result = parseExpressionAndInterpret("true != false");
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("true != 1");
    assertEquals(Log.getFindings().size(), 1);
    result = parseExpressionAndInterpret("1 != false");
    assertEquals(Log.getFindings().size(), 2);
    result = parseExpressionAndInterpret("true != 1L");
    assertEquals(Log.getFindings().size(), 3);
    result = parseExpressionAndInterpret("1L != false");
    assertEquals(Log.getFindings().size(), 4);
    result = parseExpressionAndInterpret("true != 1.2f");
    assertEquals(Log.getFindings().size(), 5);
    result = parseExpressionAndInterpret("1.5f != false");
    assertEquals(Log.getFindings().size(), 6);
    result = parseExpressionAndInterpret("true != 1.2");
    assertEquals(Log.getFindings().size(), 7);
    result = parseExpressionAndInterpret("1.5 != false");
    assertEquals(Log.getFindings().size(), 8);
    result = parseExpressionAndInterpret("true != 'a'");
    assertEquals(Log.getFindings().size(), 9);
    result = parseExpressionAndInterpret("'a' != false");
    assertEquals(Log.getFindings().size(), 10);
    result = parseExpressionAndInterpret("true != \"a\"");
    assertEquals(Log.getFindings().size(), 11);
    result = parseExpressionAndInterpret("\"a\" != false");
    assertEquals(Log.getFindings().size(), 12);


    result = parseExpressionAndInterpret("1 != 2");
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("1L != 2");
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("1 != 2L");
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("1.5f != 2");
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("1 != 1.2f");
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("1.5 != 2");
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("1 != 1.2");
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("'a' != 2");
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("1 != 'a'");
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("\"a\" != 2");
    assertEquals(Log.getFindings().size(), 13);
    result = parseExpressionAndInterpret("1 != \"a\"");
    assertEquals(Log.getFindings().size(), 14);

    result = parseExpressionAndInterpret("1L != 2L");
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("1.2f != 2L");
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("1L != 1.5f");
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("1L != 1.2");
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("1.5 != 2L");
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("1L != 'a'");
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("'a' != 2L");
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("1L != \"a\"");
    assertEquals(Log.getFindings().size(), 15);
    result = parseExpressionAndInterpret("\"a\" != 2L");
    assertEquals(Log.getFindings().size(), 16);

    result = parseExpressionAndInterpret("1.2f != 1.5f");
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("1.2 != 1.5f");
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("1.2f != 1.5");
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("'a' != 1.5f");
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("1.2f != 'a'");
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("\"a\" != 1.5f");
    assertEquals(Log.getFindings().size(), 17);
    result = parseExpressionAndInterpret("1.2f != \"a\"");
    assertEquals(Log.getFindings().size(), 18);

    result = parseExpressionAndInterpret("1.2 != 1.5");
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("'a' != 1.5");
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("1.2 != 'a'");
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("\"a\" != 1.5");
    assertEquals(Log.getFindings().size(), 19);
    result = parseExpressionAndInterpret("1.2 != \"a\"");
    assertEquals(Log.getFindings().size(), 20);

    result = parseExpressionAndInterpret("'a' != 'a'");
    assertFalse(result.asBoolean());
    result = parseExpressionAndInterpret("\"a\" != 'a'");
    assertEquals(Log.getFindings().size(), 21);
    result = parseExpressionAndInterpret("'a' != \"a\"");
    assertEquals(Log.getFindings().size(), 22);

    result = parseExpressionAndInterpret("\"a\" != \"a\"");
    assertEquals(Log.getFindings().size(), 23);
  }

  @Test
  public void testInterpretLessThanExpression() throws IOException {
    Value result;
    assertEquals(Log.getFindings().size(), 0);
    result = parseExpressionAndInterpret("true < false");
    assertEquals(Log.getFindings().size(), 1);
    result = parseExpressionAndInterpret("true < 1");
    assertEquals(Log.getFindings().size(), 2);
    result = parseExpressionAndInterpret("1 < false");
    assertEquals(Log.getFindings().size(), 3);
    result = parseExpressionAndInterpret("true < 1L");
    assertEquals(Log.getFindings().size(), 4);
    result = parseExpressionAndInterpret("1L < false");
    assertEquals(Log.getFindings().size(), 5);
    result = parseExpressionAndInterpret("true < 1.2f");
    assertEquals(Log.getFindings().size(), 6);
    result = parseExpressionAndInterpret("1.5f < false");
    assertEquals(Log.getFindings().size(), 7);
    result = parseExpressionAndInterpret("true < 1.2");
    assertEquals(Log.getFindings().size(), 8);
    result = parseExpressionAndInterpret("1.5 < false");
    assertEquals(Log.getFindings().size(), 9);
    result = parseExpressionAndInterpret("true < 'a'");
    assertEquals(Log.getFindings().size(), 10);
    result = parseExpressionAndInterpret("'a' < false");
    assertEquals(Log.getFindings().size(), 11);
    result = parseExpressionAndInterpret("true < \"a\"");
    assertEquals(Log.getFindings().size(), 12);
    result = parseExpressionAndInterpret("\"a\" < false");
    assertEquals(Log.getFindings().size(), 13);


    result = parseExpressionAndInterpret("1 < 2");
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("1L < 2");
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("1 < 2L");
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("1.5f < 2");
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("1 < 1.2f");
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("1.5 < 2");
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("1 < 1.2");
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("'a' < 2");
    assertFalse(result.asBoolean());
    result = parseExpressionAndInterpret("1 < 'a'");
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("\"a\" < 2");
    assertEquals(Log.getFindings().size(), 14);
    result = parseExpressionAndInterpret("1 < \"a\"");
    assertEquals(Log.getFindings().size(), 15);

    result = parseExpressionAndInterpret("1L < 2L");
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("1.2f < 2L");
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("1L < 1.5f");
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("1L < 1.2");
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("1.5 < 2L");
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("1L < 'a'");
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("'a' < 2L");
    assertFalse(result.asBoolean());
    result = parseExpressionAndInterpret("1L < \"a\"");
    assertEquals(Log.getFindings().size(), 16);
    result = parseExpressionAndInterpret("\"a\" < 2L");
    assertEquals(Log.getFindings().size(), 17);

    result = parseExpressionAndInterpret("1.2f < 1.5f");
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("1.2 < 1.5f");
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("1.2f < 1.5");
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("'a' < 1.5f");
    assertFalse(result.asBoolean());
    result = parseExpressionAndInterpret("1.2f < 'a'");
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("\"a\" < 1.5f");
    assertEquals(Log.getFindings().size(), 18);
    result = parseExpressionAndInterpret("1.2f < \"a\"");
    assertEquals(Log.getFindings().size(), 19);

    result = parseExpressionAndInterpret("1.2 < 1.5");
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("'a' < 1.5");
    assertFalse(result.asBoolean());
    result = parseExpressionAndInterpret("1.2 < 'a'");
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("\"a\" < 1.5");
    assertEquals(Log.getFindings().size(), 20);
    result = parseExpressionAndInterpret("1.2 < \"a\"");
    assertEquals(Log.getFindings().size(), 21);

    result = parseExpressionAndInterpret("'a' < 'a'");
    assertFalse(result.asBoolean());
    result = parseExpressionAndInterpret("\"a\" < 'a'");
    assertEquals(Log.getFindings().size(), 22);
    result = parseExpressionAndInterpret("'a' < \"a\"");
    assertEquals(Log.getFindings().size(), 23);

    result = parseExpressionAndInterpret("\"a\" < \"a\"");
    assertEquals(Log.getFindings().size(), 24);
  }

  @Test
  public void testInterpretGreaterThanExpression() throws IOException {
    Value result;
    assertEquals(Log.getFindings().size(), 0);
    result = parseExpressionAndInterpret("true > false");
    assertEquals(Log.getFindings().size(), 1);
    result = parseExpressionAndInterpret("true > 1");
    assertEquals(Log.getFindings().size(), 2);
    result = parseExpressionAndInterpret("1 > false");
    assertEquals(Log.getFindings().size(), 3);
    result = parseExpressionAndInterpret("true > 1L");
    assertEquals(Log.getFindings().size(), 4);
    result = parseExpressionAndInterpret("1L > false");
    assertEquals(Log.getFindings().size(), 5);
    result = parseExpressionAndInterpret("true > 1.2f");
    assertEquals(Log.getFindings().size(), 6);
    result = parseExpressionAndInterpret("1.5f > false");
    assertEquals(Log.getFindings().size(), 7);
    result = parseExpressionAndInterpret("true > 1.2");
    assertEquals(Log.getFindings().size(), 8);
    result = parseExpressionAndInterpret("1.5 > false");
    assertEquals(Log.getFindings().size(), 9);
    result = parseExpressionAndInterpret("true > 'a'");
    assertEquals(Log.getFindings().size(), 10);
    result = parseExpressionAndInterpret("'a' > false");
    assertEquals(Log.getFindings().size(), 11);
    result = parseExpressionAndInterpret("true > \"a\"");
    assertEquals(Log.getFindings().size(), 12);
    result = parseExpressionAndInterpret("\"a\" > false");
    assertEquals(Log.getFindings().size(), 13);


    result = parseExpressionAndInterpret("1 > 2");
    assertFalse(result.asBoolean());
    result = parseExpressionAndInterpret("1L > 2");
    assertFalse(result.asBoolean());
    result = parseExpressionAndInterpret("1 > 2L");
    assertFalse(result.asBoolean());
    result = parseExpressionAndInterpret("1.5f > 2");
    assertFalse(result.asBoolean());
    result = parseExpressionAndInterpret("1 > 1.2f");
    assertFalse(result.asBoolean());
    result = parseExpressionAndInterpret("1.5 > 2");
    assertFalse(result.asBoolean());
    result = parseExpressionAndInterpret("1 > 1.2");
    assertFalse(result.asBoolean());
    result = parseExpressionAndInterpret("'a' > 2");
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("1 > 'a'");
    assertFalse(result.asBoolean());
    result = parseExpressionAndInterpret("\"a\" > 2");
    assertEquals(Log.getFindings().size(), 14);
    result = parseExpressionAndInterpret("1 > \"a\"");
    assertEquals(Log.getFindings().size(), 15);

    result = parseExpressionAndInterpret("1L > 2L");
    assertFalse(result.asBoolean());
    result = parseExpressionAndInterpret("1.2f > 2L");
    assertFalse(result.asBoolean());
    result = parseExpressionAndInterpret("1L > 1.5f");
    assertFalse(result.asBoolean());
    result = parseExpressionAndInterpret("1L > 1.2");
    assertFalse(result.asBoolean());
    result = parseExpressionAndInterpret("1.5 > 2L");
    assertFalse(result.asBoolean());
    result = parseExpressionAndInterpret("1L > 'a'");
    assertFalse(result.asBoolean());
    result = parseExpressionAndInterpret("'a' > 2L");
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("1L > \"a\"");
    assertEquals(Log.getFindings().size(), 16);
    result = parseExpressionAndInterpret("\"a\" > 2L");
    assertEquals(Log.getFindings().size(), 17);

    result = parseExpressionAndInterpret("1.2f > 1.5f");
    assertFalse(result.asBoolean());
    result = parseExpressionAndInterpret("1.2 > 1.5f");
    assertFalse(result.asBoolean());
    result = parseExpressionAndInterpret("1.2f > 1.5");
    assertFalse(result.asBoolean());
    result = parseExpressionAndInterpret("'a' > 1.5f");
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("1.2f > 'a'");
    assertFalse(result.asBoolean());
    result = parseExpressionAndInterpret("\"a\" > 1.5f");
    assertEquals(Log.getFindings().size(), 18);
    result = parseExpressionAndInterpret("1.2f > \"a\"");
    assertEquals(Log.getFindings().size(), 19);

    result = parseExpressionAndInterpret("1.2 > 1.5");
    assertFalse(result.asBoolean());
    result = parseExpressionAndInterpret("'a' > 1.5");
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("1.2 > 'a'");
    assertFalse(result.asBoolean());
    result = parseExpressionAndInterpret("\"a\" > 1.5");
    assertEquals(Log.getFindings().size(), 20);
    result = parseExpressionAndInterpret("1.2 > \"a\"");
    assertEquals(Log.getFindings().size(), 21);

    result = parseExpressionAndInterpret("'a' > 'a'");
    assertFalse(result.asBoolean());
    result = parseExpressionAndInterpret("\"a\" > 'a'");
    assertEquals(Log.getFindings().size(), 22);
    result = parseExpressionAndInterpret("'a' > \"a\"");
    assertEquals(Log.getFindings().size(), 23);

    result = parseExpressionAndInterpret("\"a\" > \"a\"");
    assertEquals(Log.getFindings().size(), 24);
  }

  @Test
  public void testInterpretGreaterEqualExpression() throws IOException {
    Value result;
    assertEquals(Log.getFindings().size(), 0);
    result = parseExpressionAndInterpret("true >= false");
    assertEquals(Log.getFindings().size(), 1);
    result = parseExpressionAndInterpret("true >= 1");
    assertEquals(Log.getFindings().size(), 2);
    result = parseExpressionAndInterpret("1 >= false");
    assertEquals(Log.getFindings().size(), 3);
    result = parseExpressionAndInterpret("true >= 1L");
    assertEquals(Log.getFindings().size(), 4);
    result = parseExpressionAndInterpret("1L >= false");
    assertEquals(Log.getFindings().size(), 5);
    result = parseExpressionAndInterpret("true >= 1.2f");
    assertEquals(Log.getFindings().size(), 6);
    result = parseExpressionAndInterpret("1.5f >= false");
    assertEquals(Log.getFindings().size(), 7);
    result = parseExpressionAndInterpret("true >= 1.2");
    assertEquals(Log.getFindings().size(), 8);
    result = parseExpressionAndInterpret("1.5 >= false");
    assertEquals(Log.getFindings().size(), 9);
    result = parseExpressionAndInterpret("true >= 'a'");
    assertEquals(Log.getFindings().size(), 10);
    result = parseExpressionAndInterpret("'a' >= false");
    assertEquals(Log.getFindings().size(), 11);
    result = parseExpressionAndInterpret("true >= \"a\"");
    assertEquals(Log.getFindings().size(), 12);
    result = parseExpressionAndInterpret("\"a\" >= false");
    assertEquals(Log.getFindings().size(), 13);


    result = parseExpressionAndInterpret("1 >= 2");
    assertFalse(result.asBoolean());
    result = parseExpressionAndInterpret("1L >= 2");
    assertFalse(result.asBoolean());
    result = parseExpressionAndInterpret("1 >= 2L");
    assertFalse(result.asBoolean());
    result = parseExpressionAndInterpret("1.5f >= 2");
    assertFalse(result.asBoolean());
    result = parseExpressionAndInterpret("1 >= 1.2f");
    assertFalse(result.asBoolean());
    result = parseExpressionAndInterpret("1.5 >= 2");
    assertFalse(result.asBoolean());
    result = parseExpressionAndInterpret("1 >= 1.2");
    assertFalse(result.asBoolean());
    result = parseExpressionAndInterpret("'a' >= 2");
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("1 >= 'a'");
    assertFalse(result.asBoolean());
    result = parseExpressionAndInterpret("\"a\" >= 2");
    assertEquals(Log.getFindings().size(), 14);
    result = parseExpressionAndInterpret("1 >= \"a\"");
    assertEquals(Log.getFindings().size(), 15);

    result = parseExpressionAndInterpret("1L >= 2L");
    assertFalse(result.asBoolean());
    result = parseExpressionAndInterpret("1.2f >= 2L");
    assertFalse(result.asBoolean());
    result = parseExpressionAndInterpret("1L >= 1.5f");
    assertFalse(result.asBoolean());
    result = parseExpressionAndInterpret("1L >= 1.2");
    assertFalse(result.asBoolean());
    result = parseExpressionAndInterpret("1.5 >= 2L");
    assertFalse(result.asBoolean());
    result = parseExpressionAndInterpret("1L >= 'a'");
    assertFalse(result.asBoolean());
    result = parseExpressionAndInterpret("'a' >= 2L");
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("1L >= \"a\"");
    assertEquals(Log.getFindings().size(), 16);
    result = parseExpressionAndInterpret("\"a\" >= 2L");
    assertEquals(Log.getFindings().size(), 17);

    result = parseExpressionAndInterpret("1.2f >= 1.5f");
    assertFalse(result.asBoolean());
    result = parseExpressionAndInterpret("1.2 >= 1.5f");
    assertFalse(result.asBoolean());
    result = parseExpressionAndInterpret("1.2f >= 1.5");
    assertFalse(result.asBoolean());
    result = parseExpressionAndInterpret("'a' >= 1.5f");
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("1.2f >= 'a'");
    assertFalse(result.asBoolean());
    result = parseExpressionAndInterpret("\"a\" >= 1.5f");
    assertEquals(Log.getFindings().size(), 18);
    result = parseExpressionAndInterpret("1.2f >= \"a\"");
    assertEquals(Log.getFindings().size(), 19);

    result = parseExpressionAndInterpret("1.2 >= 1.5");
    assertFalse(result.asBoolean());
    result = parseExpressionAndInterpret("'a' >= 1.5");
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("1.2 >= 'a'");
    assertFalse(result.asBoolean());
    result = parseExpressionAndInterpret("\"a\" >= 1.5");
    assertEquals(Log.getFindings().size(), 20);
    result = parseExpressionAndInterpret("1.2 >= \"a\"");
    assertEquals(Log.getFindings().size(), 21);

    result = parseExpressionAndInterpret("'a' >= 'a'");
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("\"a\" >= 'a'");
    assertEquals(Log.getFindings().size(), 22);
    result = parseExpressionAndInterpret("'a' >= \"a\"");
    assertEquals(Log.getFindings().size(), 23);

    result = parseExpressionAndInterpret("\"a\" >= \"a\"");
    assertEquals(Log.getFindings().size(), 24);
  }

  @Test
  public void testInterpretLessEqualExpression() throws IOException {
    Value result;
    assertEquals(Log.getFindings().size(), 0);
    result = parseExpressionAndInterpret("true <= false");
    assertEquals(Log.getFindings().size(), 1);
    result = parseExpressionAndInterpret("true <= 1");
    assertEquals(Log.getFindings().size(), 2);
    result = parseExpressionAndInterpret("1 <= false");
    assertEquals(Log.getFindings().size(), 3);
    result = parseExpressionAndInterpret("true <= 1L");
    assertEquals(Log.getFindings().size(), 4);
    result = parseExpressionAndInterpret("1L <= false");
    assertEquals(Log.getFindings().size(), 5);
    result = parseExpressionAndInterpret("true <= 1.2f");
    assertEquals(Log.getFindings().size(), 6);
    result = parseExpressionAndInterpret("1.5f <= false");
    assertEquals(Log.getFindings().size(), 7);
    result = parseExpressionAndInterpret("true <= 1.2");
    assertEquals(Log.getFindings().size(), 8);
    result = parseExpressionAndInterpret("1.5 <= false");
    assertEquals(Log.getFindings().size(), 9);
    result = parseExpressionAndInterpret("true <= 'a'");
    assertEquals(Log.getFindings().size(), 10);
    result = parseExpressionAndInterpret("'a' <= false");
    assertEquals(Log.getFindings().size(), 11);
    result = parseExpressionAndInterpret("true <= \"a\"");
    assertEquals(Log.getFindings().size(), 12);
    result = parseExpressionAndInterpret("\"a\" <= false");
    assertEquals(Log.getFindings().size(), 13);


    result = parseExpressionAndInterpret("1 <= 2");
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("1L <= 2");
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("1 <= 2L");
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("1.5f <= 2");
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("1 <= 1.2f");
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("1.5 <= 2");
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("1 <= 1.2");
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("'a' <= 2");
    assertFalse(result.asBoolean());
    result = parseExpressionAndInterpret("1 <= 'a'");
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("\"a\" <= 2");
    assertEquals(Log.getFindings().size(), 14);
    result = parseExpressionAndInterpret("1 <= \"a\"");
    assertEquals(Log.getFindings().size(), 15);

    result = parseExpressionAndInterpret("1L <= 2L");
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("1.2f <= 2L");
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("1L <= 1.5f");
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("1L <= 1.2");
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("1.5 <= 2L");
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("1L <= 'a'");
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("'a' <= 2L");
    assertFalse(result.asBoolean());
    result = parseExpressionAndInterpret("1L <= \"a\"");
    assertEquals(Log.getFindings().size(), 16);
    result = parseExpressionAndInterpret("\"a\" <= 2L");
    assertEquals(Log.getFindings().size(), 17);

    result = parseExpressionAndInterpret("1.2f <= 1.5f");
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("1.2 <= 1.5f");
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("1.2f <= 1.5");
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("'a' <= 1.5f");
    assertFalse(result.asBoolean());
    result = parseExpressionAndInterpret("1.2f <= 'a'"); //MARKER
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("\"a\" <= 1.5f");
    assertEquals(Log.getFindings().size(), 18);
    result = parseExpressionAndInterpret("1.2f <= \"a\"");
    assertEquals(Log.getFindings().size(), 19);

    result = parseExpressionAndInterpret("1.2 <= 1.5");
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("'a' <= 1.5");
    assertFalse(result.asBoolean());
    result = parseExpressionAndInterpret("1.2 <= 'a'");
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("\"a\" <= 1.5");
    assertEquals(Log.getFindings().size(), 20);
    result = parseExpressionAndInterpret("1.2 <= \"a\"");
    assertEquals(Log.getFindings().size(), 21);

    result = parseExpressionAndInterpret("'a' <= 'a'");
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("\"a\" <= 'a'");
    assertEquals(Log.getFindings().size(), 22);
    result = parseExpressionAndInterpret("'a' <= \"a\"");
    assertEquals(Log.getFindings().size(), 23);

    result = parseExpressionAndInterpret("\"a\" <= \"a\"");
    assertEquals(Log.getFindings().size(), 24);
  }

  @Test
  public void testInterpretBooleanNotExpression() throws IOException {
    Value result;
    assertEquals(Log.getFindings().size(), 0);
    result = parseExpressionAndInterpret("~true");
    assertEquals(Log.getFindings().size(), 1);
    result = parseExpressionAndInterpret("~1");
    assertEquals(result.asInt(), -2);
    result = parseExpressionAndInterpret("~-5");
    assertEquals(result.asInt(), 4);
    result = parseExpressionAndInterpret("~708");
    assertEquals(result.asInt(), -709);
    result = parseExpressionAndInterpret("~1L");
    assertEquals(result.asLong(), -2L);
    result = parseExpressionAndInterpret("~-5L");
    assertEquals(result.asLong(), 4L);
    result = parseExpressionAndInterpret("~708L");
    assertEquals(result.asLong(), -709L);
    result = parseExpressionAndInterpret("~1.2f");
    assertEquals(Log.getFindings().size(), 2);
    result = parseExpressionAndInterpret("~1.5");
    assertEquals(Log.getFindings().size(), 3);
    result = parseExpressionAndInterpret("~'a'");
    assertEquals(result.asInt(), -98);
    result = parseExpressionAndInterpret("~\"a\"");
    assertEquals(Log.getFindings().size(), 4);
  }

  @Test
  public void testInterpretLogicalNotExpression() throws IOException {
    Value result;
    assertEquals(Log.getFindings().size(), 0);
    result = parseExpressionAndInterpret("!true");
    assertFalse(result.asBoolean());
    result = parseExpressionAndInterpret("!false");
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("!1");
    assertEquals(Log.getFindings().size(), 1);
    result = parseExpressionAndInterpret("!1L");
    assertEquals(Log.getFindings().size(), 2);
    result = parseExpressionAndInterpret("!1.2f");
    assertEquals(Log.getFindings().size(), 3);
    result = parseExpressionAndInterpret("!1.5");
    assertEquals(Log.getFindings().size(), 4);
    result = parseExpressionAndInterpret("!'a'");
    assertEquals(Log.getFindings().size(), 5);
    result = parseExpressionAndInterpret("!\"a\"");
    assertEquals(Log.getFindings().size(), 6);
  }

  @Test
  public void testInterpretLogicalAndOpExpression() throws IOException {
    Value result;
    assertEquals(Log.getFindings().size(), 0);
    result = parseExpressionAndInterpret("true && true");
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("false && false");
    assertFalse(result.asBoolean());
    result = parseExpressionAndInterpret("true && false");
    assertFalse(result.asBoolean());
    result = parseExpressionAndInterpret("false && true");
    assertFalse(result.asBoolean());

    result = parseExpressionAndInterpret("true && 1");
    assertEquals(Log.getFindings().size(), 1);
    result = parseExpressionAndInterpret("1 && false");
    assertEquals(Log.getFindings().size(), 2);
    result = parseExpressionAndInterpret("true && 1L");
    assertEquals(Log.getFindings().size(), 3);
    result = parseExpressionAndInterpret("1L && false");
    assertEquals(Log.getFindings().size(), 4);
    result = parseExpressionAndInterpret("true && 1.2f");
    assertEquals(Log.getFindings().size(), 5);
    result = parseExpressionAndInterpret("1.5f && false");
    assertEquals(Log.getFindings().size(), 6);
    result = parseExpressionAndInterpret("true && 1.2");
    assertEquals(Log.getFindings().size(), 7);
    result = parseExpressionAndInterpret("1.5 && false");
    assertEquals(Log.getFindings().size(), 8);
    result = parseExpressionAndInterpret("true && 'a'");
    assertEquals(Log.getFindings().size(), 9);
    result = parseExpressionAndInterpret("'a' && false");
    assertEquals(Log.getFindings().size(), 10);
    result = parseExpressionAndInterpret("true && \"a\"");
    assertEquals(Log.getFindings().size(), 11);
    result = parseExpressionAndInterpret("\"a\" && false");
    assertEquals(Log.getFindings().size(), 12);


    result = parseExpressionAndInterpret("1 && 2");
    assertEquals(Log.getFindings().size(), 13);
    result = parseExpressionAndInterpret("1L && 2");
    assertEquals(Log.getFindings().size(), 14);
    result = parseExpressionAndInterpret("1 && 2L");
    assertEquals(Log.getFindings().size(), 15);
    result = parseExpressionAndInterpret("1.5f && 2");
    assertEquals(Log.getFindings().size(), 16);
    result = parseExpressionAndInterpret("1 && 1.2f");
    assertEquals(Log.getFindings().size(), 17);
    result = parseExpressionAndInterpret("1.5 && 2");
    assertEquals(Log.getFindings().size(), 18);
    result = parseExpressionAndInterpret("1 && 1.2");
    assertEquals(Log.getFindings().size(), 19);
    result = parseExpressionAndInterpret("'a' && 2");
    assertEquals(Log.getFindings().size(), 20);
    result = parseExpressionAndInterpret("1 && 'a'");
    assertEquals(Log.getFindings().size(), 21);
    result = parseExpressionAndInterpret("\"a\" && 2");
    assertEquals(Log.getFindings().size(), 22);
    result = parseExpressionAndInterpret("1 && \"a\"");
    assertEquals(Log.getFindings().size(), 23);

    result = parseExpressionAndInterpret("1L && 2L");
    assertEquals(Log.getFindings().size(), 24);
    result = parseExpressionAndInterpret("1.2f && 2L");
    assertEquals(Log.getFindings().size(), 25);
    result = parseExpressionAndInterpret("1L && 1.5f");
    assertEquals(Log.getFindings().size(), 26);
    result = parseExpressionAndInterpret("1L && 1.2");
    assertEquals(Log.getFindings().size(), 27);
    result = parseExpressionAndInterpret("1.5 && 2L");
    assertEquals(Log.getFindings().size(), 28);
    result = parseExpressionAndInterpret("1L && 'a'");
    assertEquals(Log.getFindings().size(), 29);
    result = parseExpressionAndInterpret("'a' && 2L");
    assertEquals(Log.getFindings().size(), 30);
    result = parseExpressionAndInterpret("1L && \"a\"");
    assertEquals(Log.getFindings().size(), 31);
    result = parseExpressionAndInterpret("\"a\" && 2L");
    assertEquals(Log.getFindings().size(), 32);

    result = parseExpressionAndInterpret("1.2f && 1.5f");
    assertEquals(Log.getFindings().size(), 33);
    result = parseExpressionAndInterpret("1.2 && 1.5f");
    assertEquals(Log.getFindings().size(), 34);
    result = parseExpressionAndInterpret("1.2f && 1.5");
    assertEquals(Log.getFindings().size(), 35);
    result = parseExpressionAndInterpret("'a' && 1.5f");
    assertEquals(Log.getFindings().size(), 36);
    result = parseExpressionAndInterpret("1.2f && 'a'");
    assertEquals(Log.getFindings().size(), 37);
    result = parseExpressionAndInterpret("\"a\" && 1.5f");
    assertEquals(Log.getFindings().size(), 38);
    result = parseExpressionAndInterpret("1.2f && \"a\"");
    assertEquals(Log.getFindings().size(), 39);

    result = parseExpressionAndInterpret("1.2 && 1.5");
    assertEquals(Log.getFindings().size(), 40);
    result = parseExpressionAndInterpret("'a' && 1.5");
    assertEquals(Log.getFindings().size(), 41);
    result = parseExpressionAndInterpret("1.2 && 'a'");
    assertEquals(Log.getFindings().size(), 42);
    result = parseExpressionAndInterpret("\"a\" && 1.5");
    assertEquals(Log.getFindings().size(), 43);
    result = parseExpressionAndInterpret("1.2 && \"a\"");
    assertEquals(Log.getFindings().size(), 44);

    result = parseExpressionAndInterpret("'a' && 'a'");
    assertEquals(Log.getFindings().size(), 45);
    result = parseExpressionAndInterpret("\"a\" && 'a'");
    assertEquals(Log.getFindings().size(), 46);
    result = parseExpressionAndInterpret("'a' && \"a\"");
    assertEquals(Log.getFindings().size(), 47);

    result = parseExpressionAndInterpret("\"a\" && \"a\"");
    assertEquals(Log.getFindings().size(), 48);
  }

  @Test
  public void testInterpretLogicalOrOpExpression() throws IOException {
    Value result;
    assertEquals(Log.getFindings().size(), 0);
    result = parseExpressionAndInterpret("true || true");
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("false || false");
    assertFalse(result.asBoolean());
    result = parseExpressionAndInterpret("true || false");
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("false || true");
    assertTrue(result.asBoolean());

    result = parseExpressionAndInterpret("true || 1");
    assertEquals(Log.getFindings().size(), 1);
    result = parseExpressionAndInterpret("1 || false");
    assertEquals(Log.getFindings().size(), 2);
    result = parseExpressionAndInterpret("true || 1L");
    assertEquals(Log.getFindings().size(), 3);
    result = parseExpressionAndInterpret("1L || false");
    assertEquals(Log.getFindings().size(), 4);
    result = parseExpressionAndInterpret("true || 1.2f");
    assertEquals(Log.getFindings().size(), 5);
    result = parseExpressionAndInterpret("1.5f || false");
    assertEquals(Log.getFindings().size(), 6);
    result = parseExpressionAndInterpret("true || 1.2");
    assertEquals(Log.getFindings().size(), 7);
    result = parseExpressionAndInterpret("1.5 || false");
    assertEquals(Log.getFindings().size(), 8);
    result = parseExpressionAndInterpret("true || 'a'");
    assertEquals(Log.getFindings().size(), 9);
    result = parseExpressionAndInterpret("'a' || false");
    assertEquals(Log.getFindings().size(), 10);
    result = parseExpressionAndInterpret("true || \"a\"");
    assertEquals(Log.getFindings().size(), 11);
    result = parseExpressionAndInterpret("\"a\" || false");
    assertEquals(Log.getFindings().size(), 12);


    result = parseExpressionAndInterpret("1 || 2");
    assertEquals(Log.getFindings().size(), 13);
    result = parseExpressionAndInterpret("1L || 2");
    assertEquals(Log.getFindings().size(), 14);
    result = parseExpressionAndInterpret("1 || 2L");
    assertEquals(Log.getFindings().size(), 15);
    result = parseExpressionAndInterpret("1.5f || 2");
    assertEquals(Log.getFindings().size(), 16);
    result = parseExpressionAndInterpret("1 || 1.2f");
    assertEquals(Log.getFindings().size(), 17);
    result = parseExpressionAndInterpret("1.5 || 2");
    assertEquals(Log.getFindings().size(), 18);
    result = parseExpressionAndInterpret("1 || 1.2");
    assertEquals(Log.getFindings().size(), 19);
    result = parseExpressionAndInterpret("'a' || 2");
    assertEquals(Log.getFindings().size(), 20);
    result = parseExpressionAndInterpret("1 || 'a'");
    assertEquals(Log.getFindings().size(), 21);
    result = parseExpressionAndInterpret("\"a\" || 2");
    assertEquals(Log.getFindings().size(), 22);
    result = parseExpressionAndInterpret("1 || \"a\"");
    assertEquals(Log.getFindings().size(), 23);

    result = parseExpressionAndInterpret("1L || 2L");
    assertEquals(Log.getFindings().size(), 24);
    result = parseExpressionAndInterpret("1.2f || 2L");
    assertEquals(Log.getFindings().size(), 25);
    result = parseExpressionAndInterpret("1L || 1.5f");
    assertEquals(Log.getFindings().size(), 26);
    result = parseExpressionAndInterpret("1L || 1.2");
    assertEquals(Log.getFindings().size(), 27);
    result = parseExpressionAndInterpret("1.5 || 2L");
    assertEquals(Log.getFindings().size(), 28);
    result = parseExpressionAndInterpret("1L || 'a'");
    assertEquals(Log.getFindings().size(), 29);
    result = parseExpressionAndInterpret("'a' || 2L");
    assertEquals(Log.getFindings().size(), 30);
    result = parseExpressionAndInterpret("1L || \"a\"");
    assertEquals(Log.getFindings().size(), 31);
    result = parseExpressionAndInterpret("\"a\" || 2L");
    assertEquals(Log.getFindings().size(), 32);

    result = parseExpressionAndInterpret("1.2f || 1.5f");
    assertEquals(Log.getFindings().size(), 33);
    result = parseExpressionAndInterpret("1.2 || 1.5f");
    assertEquals(Log.getFindings().size(), 34);
    result = parseExpressionAndInterpret("1.2f || 1.5");
    assertEquals(Log.getFindings().size(), 35);
    result = parseExpressionAndInterpret("'a' || 1.5f");
    assertEquals(Log.getFindings().size(), 36);
    result = parseExpressionAndInterpret("1.2f || 'a'");
    assertEquals(Log.getFindings().size(), 37);
    result = parseExpressionAndInterpret("\"a\" || 1.5f");
    assertEquals(Log.getFindings().size(), 38);
    result = parseExpressionAndInterpret("1.2f || \"a\"");
    assertEquals(Log.getFindings().size(), 39);

    result = parseExpressionAndInterpret("1.2 || 1.5");
    assertEquals(Log.getFindings().size(), 40);
    result = parseExpressionAndInterpret("'a' || 1.5");
    assertEquals(Log.getFindings().size(), 41);
    result = parseExpressionAndInterpret("1.2 || 'a'");
    assertEquals(Log.getFindings().size(), 42);
    result = parseExpressionAndInterpret("\"a\" || 1.5");
    assertEquals(Log.getFindings().size(), 43);
    result = parseExpressionAndInterpret("1.2 || \"a\"");
    assertEquals(Log.getFindings().size(), 44);

    result = parseExpressionAndInterpret("'a' || 'a'");
    assertEquals(Log.getFindings().size(), 45);
    result = parseExpressionAndInterpret("\"a\" || 'a'");
    assertEquals(Log.getFindings().size(), 46);
    result = parseExpressionAndInterpret("'a' || \"a\"");
    assertEquals(Log.getFindings().size(), 47);

    result = parseExpressionAndInterpret("\"a\" || \"a\"");
    assertEquals(Log.getFindings().size(), 48);
  }

  @Test
  public void testCombinedExpressions() throws IOException {
    Value result;
    assertEquals(Log.getFindings().size(), 0);
    result = parseExpressionAndInterpret("((1 > 2L) && ('z' <= 15.243f)) || true");
    assertTrue(result.asBoolean());
    result = parseExpressionAndInterpret("(3 + 2 * 2) / 14.0");
    assertEquals(result.asDouble(), 0.5, delta);
    result = parseExpressionAndInterpret("true && false || !true");
    assertFalse(result.asBoolean());
  }


  protected Value parseExpressionAndInterpret(String expr) throws IOException {
    CombineExpressionsWithLiteralsInterpreter interpreter = new CombineExpressionsWithLiteralsInterpreter();
    CombineExpressionsWithLiteralsParser parser = CombineExpressionsWithLiteralsMill.parser();
    final Optional<ASTExpression> optAST = parser.parse_StringExpression(expr);
    assertTrue(optAST.isPresent());
    final ASTExpression ast = optAST.get();
    return interpreter.interpret(ast);
  }
}
