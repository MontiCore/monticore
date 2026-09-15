/* (c) https://github.com/MontiCore/monticore */
package mc.testcases.expressiondsl;

import de.monticore.expressions.commonexpressions._ast.ASTPlusExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.literals.mccommonliterals._ast.ASTStringLiteral;
import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.tf.tfcommons._ast.ASTAssign;
import mc.testcases.tr.expressiondsltr.ExpressionDSLTRMill;
import mc.testcases.tr.expressiondsltr._parser.ExpressionDSLTRParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for literal support in left recursive grammars (aka expressions)
 */
@TestWithMCLanguage(ExpressionDSLTRMill.class)
public class ExpressionDSLTRParseTest {

  @ParameterizedTest
  @ValueSource(strings = {
      "a=b",
      "a=\"string\"",
      "a='c'",
      "a=false",
      "a=true",
      "a=null",
      "a=12"
  })
  public void testITFExpressions(String expr) throws IOException {
    // Test if various literals can be parsed
    test(expr, ExpressionDSLTRParser::parse_StringITFExpression);
  }
  
  @ParameterizedTest
  @ValueSource(strings = {
      "String"
  })
  public void testTfIdentifierName(String name) throws IOException {
    test(name, ExpressionDSLTRParser::parse_StringTfIdentifierName);
  }
  
  @ParameterizedTest
  @ValueSource(strings = {
      "fully.qualified.type",
      "boolean",
      "String"
  })
  public void testITFMCType(String type) throws IOException {
    // Test if various literals can be parsed
    test(type, ExpressionDSLTRParser::parse_StringITFMCType);
  }
  
  @ParameterizedTest
  @ValueSource(strings = {
      "public String s1;",
      "private String s2;",
      "public Boolean b1;",
      "public boolean b2;"
  })
  public void testITFCDAttribute(String attr) throws IOException {
    test(attr, ExpressionDSLTRParser::parse_StringITFCDAttribute);
  }

  @Test
  public void testAssign1() throws IOException {
    ASTAssign ast = test("$exp1 = $exp2 ;", ExpressionDSLTRParser::parse_StringAssign);
    assertEquals(ASTNameExpression.class.getName(), ast.getValue().getClass().getName());
  }
  
  @Test
  public void testAssign2() throws IOException {
    ASTAssign ast = test("$exp1 = \"string\" ;", ExpressionDSLTRParser::parse_StringAssign);
    assertEquals(ASTLiteralExpression.class.getName(), ast.getValue().getClass().getName());
    assertEquals(ASTStringLiteral.class.getName(), ((ASTLiteralExpression)ast.getValue()).getLiteral().getClass().getName());
    assertEquals("string", ((ASTStringLiteral)((ASTLiteralExpression)ast.getValue()).getLiteral()).getValue());
  }
  
  @Test
  public void testAssign3() throws IOException {
    ASTAssign ast = test("$exp1 = $exp1 + \"string\" ;", ExpressionDSLTRParser::parse_StringAssign);
    assertEquals(ASTPlusExpression.class.getName(), ast.getValue().getClass().getName());
    assertEquals(ASTNameExpression.class.getName(), ((ASTPlusExpression)ast.getValue()).getLeft().getClass().getName());
    assertEquals(ASTLiteralExpression.class.getName(), ((ASTPlusExpression)ast.getValue()).getRight().getClass().getName());
    assertEquals("string", ((ASTStringLiteral)((ASTLiteralExpression)((ASTPlusExpression)ast.getValue()).getRight()).getLiteral()).getValue());
  }

  @Test
  public void testNoKeyword() throws IOException {
    // Test if (no)keywords rules apply
    test("MyFancyKeyword", ExpressionDSLTRParser::parse_StringITFMyFancyKeywordP);
    test("MyFancyKeyword", ExpressionDSLTRParser::parse_StringITFNameExpression);
    test("42<=42", ExpressionDSLTRParser::parse_StringITFExpression);
    test("42 <42", ExpressionDSLTRParser::parse_StringITFExpression);
  }

  protected <A>  A test(String exp, ParserFunction<A> parserFunction) throws IOException {
    ExpressionDSLTRParser parser = ExpressionDSLTRMill.parser();
    Optional<A> typeOptional = parserFunction.parse(parser, exp);
    assertFalse(parser.hasErrors(), "Parser error while parsing: " + exp);
    assertTrue(typeOptional.isPresent(), "Failed to parse: " + exp);
    return typeOptional.get();
  }

  @FunctionalInterface
  protected interface ParserFunction<A> {
    Optional<A> parse(ExpressionDSLTRParser parser, String input) throws IOException;
  }
}
