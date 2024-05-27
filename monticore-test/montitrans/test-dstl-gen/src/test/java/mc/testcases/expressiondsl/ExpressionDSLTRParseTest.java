/* (c) https://github.com/MontiCore/monticore */
package mc.testcases.expressiondsl;

import de.monticore.expressions.commonexpressions._ast.ASTPlusExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.literals.mccommonliterals._ast.ASTStringLiteral;
import de.monticore.tf.tfcommons._ast.ASTAssign;
import de.se_rwth.commons.logging.Log;
import mc.testcases.tr.expressiondsltr.ExpressionDSLTRMill;
import mc.testcases.tr.expressiondsltr._parser.ExpressionDSLTRParser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

/**
 * Test for literal support in left recursive grammars (aka expressions)
 */
public class ExpressionDSLTRParseTest {
  @Before
  public void beforeEach() {
    Log.init();
    Log.enableFailQuick(false);
    ExpressionDSLTRMill.init();
    Log.clearFindings();
  }

  @Test
  public void testITFExpressions() throws IOException {
    // Test if various literals can be parsed
    test("a=b", ExpressionDSLTRParser::parse_StringITFExpression);
    test("a=\"string\"", ExpressionDSLTRParser::parse_StringITFExpression);
    test("a='c'", ExpressionDSLTRParser::parse_StringITFExpression);
    test("a=false", ExpressionDSLTRParser::parse_StringITFExpression);
    test("a=true", ExpressionDSLTRParser::parse_StringITFExpression);
    test("a=null", ExpressionDSLTRParser::parse_StringITFExpression);
    test("a=12", ExpressionDSLTRParser::parse_StringITFExpression);
  }

  @Test
  public void testTypes() throws IOException {
    // Test if various literals can be parsed
    test("String", ExpressionDSLTRParser::parse_StringTfIdentifierName);
    test("fully.qualified.type", ExpressionDSLTRParser::parse_StringITFMCType);
    test("boolean", ExpressionDSLTRParser::parse_StringITFMCType);
    test("String", ExpressionDSLTRParser::parse_StringITFMCType);
    test("public String s1;", ExpressionDSLTRParser::parse_StringITFCDAttribute);
    test("private String s2;", ExpressionDSLTRParser::parse_StringITFCDAttribute);
    test("public Boolean b1;", ExpressionDSLTRParser::parse_StringITFCDAttribute);
    test("public boolean b2;", ExpressionDSLTRParser::parse_StringITFCDAttribute);
  }

  @Test
  public void testAssigns() throws IOException {
    ASTAssign ast = test("$exp1 = $exp2 ;", ExpressionDSLTRParser::parse_StringAssign);
    Assert.assertEquals(ASTNameExpression.class.getName(), ast.getValue().getClass().getName());
    ast = test("$exp1 = \"string\" ;", ExpressionDSLTRParser::parse_StringAssign);
    Assert.assertEquals(ASTLiteralExpression.class.getName(), ast.getValue().getClass().getName());
    Assert.assertEquals(ASTStringLiteral.class.getName(), ((ASTLiteralExpression)ast.getValue()).getLiteral().getClass().getName());
    Assert.assertEquals("string", ((ASTStringLiteral)((ASTLiteralExpression)ast.getValue()).getLiteral()).getValue());
    ast = test("$exp1 = $exp1 + \"string\" ;", ExpressionDSLTRParser::parse_StringAssign);
    Assert.assertEquals(ASTPlusExpression.class.getName(), ast.getValue().getClass().getName());
    Assert.assertEquals(ASTNameExpression.class.getName(), ((ASTPlusExpression)ast.getValue()).getLeft().getClass().getName());
    Assert.assertEquals(ASTLiteralExpression.class.getName(), ((ASTPlusExpression)ast.getValue()).getRight().getClass().getName());
    Assert.assertEquals("string", ((ASTStringLiteral)((ASTLiteralExpression)((ASTPlusExpression)ast.getValue()).getRight()).getLiteral()).getValue());
  }

  protected <A>  A test(String exp, ParserFunction<A> parserFunction) throws IOException {
    ExpressionDSLTRParser parser = ExpressionDSLTRMill.parser();
    Optional<A> typeOptional = parserFunction.parse(parser, exp);
    Assert.assertFalse("Parser error while parsing: " + exp, parser.hasErrors());
    Assert.assertTrue("Failed to parse: " + exp, typeOptional.isPresent());
    return typeOptional.get();
  }

  @FunctionalInterface
  protected interface ParserFunction<A> {
    Optional<A> parse(ExpressionDSLTRParser parser, String input) throws IOException;
  }
}
