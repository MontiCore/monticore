/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.prettyprint;

import de.monticore.expressions.assignmentexpressions._ast.*;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.testassignmentexpressions.TestAssignmentExpressionsMill;
import de.monticore.expressions.testassignmentexpressions._parser.TestAssignmentExpressionsParser;
import de.monticore.runtime.junit.TestWithMCLanguage;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static de.monticore.expressions.assignmentexpressions._ast.ASTConstantsAssignmentExpressions.*;
import static org.junit.jupiter.api.Assertions.*;

@TestWithMCLanguage(TestAssignmentExpressionsMill.class)
public class AssignmentExpressionsPrettyPrinterTest {

  protected TestAssignmentExpressionsParser parser;
  
  @BeforeEach
  public void init() {
    parser = TestAssignmentExpressionsMill.parser();
  }

  @Test
  public void testIncPrefixExpression() throws IOException {
    Optional<ASTIncPrefixExpression> result = parser.parse_StringIncPrefixExpression("++a");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTIncPrefixExpression ast = result.get();

    String output = TestAssignmentExpressionsMill.prettyPrint(ast, false);

    result = parser.parse_StringIncPrefixExpression(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testDecPrefixExpression() throws IOException {
    Optional<ASTDecPrefixExpression> result = parser.parse_StringDecPrefixExpression("--a");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTDecPrefixExpression ast = result.get();

    String output = TestAssignmentExpressionsMill.prettyPrint(ast, false);

    result = parser.parse_StringDecPrefixExpression(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testIncSuffixExpression() throws IOException {
    Optional<ASTExpression> a = parser.parse_StringExpression("a");
    assertFalse(parser.hasErrors());
    assertTrue(a.isPresent());
    ASTIncSuffixExpression result = TestAssignmentExpressionsMill.incSuffixExpressionBuilder()
        .setExpression(a.get())
        .build();

    String output = TestAssignmentExpressionsMill.prettyPrint(result, false).trim();

    assertEquals("a++", output);
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testDecSuffixExpression() throws IOException {
    Optional<ASTExpression> a = parser.parse_StringExpression("a");
    assertFalse(parser.hasErrors());
    assertTrue(a.isPresent());
    ASTDecSuffixExpression result = TestAssignmentExpressionsMill.decSuffixExpressionBuilder()
        .setExpression(a.get())
        .build();

    String output = TestAssignmentExpressionsMill.prettyPrint(result, false).trim();

    assertEquals("a--", output);
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testRegularAssignmentEqualsExpression() throws IOException {
    Optional<ASTExpression> a = parser.parse_StringExpression("a");
    Optional<ASTExpression> b = parser.parse_StringExpression("b");
    assertFalse(parser.hasErrors());
    assertTrue(a.isPresent());
    assertTrue(b.isPresent());
    ASTAssignmentExpression result = TestAssignmentExpressionsMill.assignmentExpressionBuilder()
        .setLeft(a.get())
        .setRight(b.get())
        .setOperator(EQUALS)
        .build();

    String output = TestAssignmentExpressionsMill.prettyPrint(result, false).trim();

    assertEquals("a=b", output);
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testRegularAssignmentPlusEqualsExpression() throws IOException {
    Optional<ASTExpression> a = parser.parse_StringExpression("a");
    Optional<ASTExpression> b = parser.parse_StringExpression("b");
    assertFalse(parser.hasErrors());
    assertTrue(a.isPresent());
    assertTrue(b.isPresent());
    ASTAssignmentExpression result = TestAssignmentExpressionsMill.assignmentExpressionBuilder()
        .setLeft(a.get())
        .setRight(b.get())
        .setOperator(PLUSEQUALS)
        .build();

    String output = TestAssignmentExpressionsMill.prettyPrint(result, false).trim();

    assertEquals("a+=b", output);
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testRegularAssignmentMinusExpression() throws IOException {
    Optional<ASTExpression> a = parser.parse_StringExpression("a");
    Optional<ASTExpression> b = parser.parse_StringExpression("b");
    assertFalse(parser.hasErrors());
    assertTrue(a.isPresent());
    assertTrue(b.isPresent());
    ASTAssignmentExpression result = TestAssignmentExpressionsMill.assignmentExpressionBuilder()
        .setLeft(a.get())
        .setRight(b.get())
        .setOperator(MINUSEQUALS)
        .build();

    String output = TestAssignmentExpressionsMill.prettyPrint(result, false).trim();

    assertEquals("a-=b", output);
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testRegularAssignmentPercentEqualsExpression() throws IOException {
    Optional<ASTExpression> a = parser.parse_StringExpression("a");
    Optional<ASTExpression> b = parser.parse_StringExpression("b");
    assertFalse(parser.hasErrors());
    assertTrue(a.isPresent());
    assertTrue(b.isPresent());
    ASTAssignmentExpression result = TestAssignmentExpressionsMill.assignmentExpressionBuilder()
        .setLeft(a.get())
        .setRight(b.get())
        .setOperator(PERCENTEQUALS)
        .build();

    String output = TestAssignmentExpressionsMill.prettyPrint(result, false).trim();

    assertEquals("a%=b", output);
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testRegularAssignmentAndEqualsExpression() throws IOException {
    Optional<ASTExpression> a = parser.parse_StringExpression("a");
    Optional<ASTExpression> b = parser.parse_StringExpression("b");
    assertFalse(parser.hasErrors());
    assertTrue(a.isPresent());
    assertTrue(b.isPresent());
    ASTAssignmentExpression result = TestAssignmentExpressionsMill.assignmentExpressionBuilder()
        .setLeft(a.get())
        .setRight(b.get())
        .setOperator(AND_EQUALS)
        .build();

    String output = TestAssignmentExpressionsMill.prettyPrint(result, false).trim();

    assertEquals("a&=b", output);
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testRegularAssignmentRoofEqualsExpression() throws IOException {
    Optional<ASTExpression> a = parser.parse_StringExpression("a");
    Optional<ASTExpression> b = parser.parse_StringExpression("b");
    assertFalse(parser.hasErrors());
    assertTrue(a.isPresent());
    assertTrue(b.isPresent());
    ASTAssignmentExpression result = TestAssignmentExpressionsMill.assignmentExpressionBuilder()
        .setLeft(a.get())
        .setRight(b.get())
        .setOperator(ROOFEQUALS)
        .build();

    String output = TestAssignmentExpressionsMill.prettyPrint(result, false).trim();

    assertEquals("a^=b", output);
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testRegularAssignmentSlashEqualsExpression() throws IOException {
    Optional<ASTExpression> a = parser.parse_StringExpression("a");
    Optional<ASTExpression> b = parser.parse_StringExpression("b");
    assertFalse(parser.hasErrors());
    assertTrue(a.isPresent());
    assertTrue(b.isPresent());
    ASTAssignmentExpression result = TestAssignmentExpressionsMill.assignmentExpressionBuilder()
        .setLeft(a.get())
        .setRight(b.get())
        .setOperator(SLASHEQUALS)
        .build();

    String output = TestAssignmentExpressionsMill.prettyPrint(result, false).trim();

    assertEquals("a/=b", output);
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testRegularAssignmentStarEqualsExpression() throws IOException {
    Optional<ASTExpression> a = parser.parse_StringExpression("a");
    Optional<ASTExpression> b = parser.parse_StringExpression("b");
    assertFalse(parser.hasErrors());
    assertTrue(a.isPresent());
    assertTrue(b.isPresent());
    ASTAssignmentExpression result = TestAssignmentExpressionsMill.assignmentExpressionBuilder()
        .setLeft(a.get())
        .setRight(b.get())
        .setOperator(STAREQUALS)
        .build();

    String output = TestAssignmentExpressionsMill.prettyPrint(result, false).trim();

    assertEquals("a*=b", output);
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testRegularAssignmentPipeEqualsExpression() throws IOException {
    Optional<ASTExpression> a = parser.parse_StringExpression("a");
    Optional<ASTExpression> b = parser.parse_StringExpression("b");
    assertFalse(parser.hasErrors());
    assertTrue(a.isPresent());
    assertTrue(b.isPresent());
    ASTAssignmentExpression result = TestAssignmentExpressionsMill.assignmentExpressionBuilder()
        .setLeft(a.get())
        .setRight(b.get())
        .setOperator(PIPEEQUALS)
        .build();

    String output = TestAssignmentExpressionsMill.prettyPrint(result, false).trim();

    assertEquals("a|=b", output);
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testRegularAssignmentLTLTEqualsExpression() throws IOException {
    Optional<ASTExpression> a = parser.parse_StringExpression("a");
    Optional<ASTExpression> b = parser.parse_StringExpression("b");
    assertFalse(parser.hasErrors());
    assertTrue(a.isPresent());
    assertTrue(b.isPresent());
    ASTAssignmentExpression result = TestAssignmentExpressionsMill.assignmentExpressionBuilder()
        .setLeft(a.get())
        .setRight(b.get())
        .setOperator(LTLTEQUALS)
        .build();

    String output = TestAssignmentExpressionsMill.prettyPrint(result, false).trim();

    assertEquals("a<<=b", output);
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testRegularAssignmentGTGTEqualsExpression() throws IOException {
    Optional<ASTExpression> a = parser.parse_StringExpression("a");
    Optional<ASTExpression> b = parser.parse_StringExpression("b");
    assertFalse(parser.hasErrors());
    assertTrue(a.isPresent());
    assertTrue(b.isPresent());
    ASTAssignmentExpression result = TestAssignmentExpressionsMill.assignmentExpressionBuilder()
        .setLeft(a.get())
        .setRight(b.get())
        .setOperator(GTGTEQUALS)
        .build();

    String output = TestAssignmentExpressionsMill.prettyPrint(result, false).trim();

    assertEquals("a>>=b", output);
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testRegularAssignmentGTGTGTEqualsExpression() throws IOException {
    Optional<ASTExpression> a = parser.parse_StringExpression("a");
    Optional<ASTExpression> b = parser.parse_StringExpression("b");
    assertFalse(parser.hasErrors());
    assertTrue(a.isPresent());
    assertTrue(b.isPresent());
    ASTAssignmentExpression result = TestAssignmentExpressionsMill.assignmentExpressionBuilder()
        .setLeft(a.get())
        .setRight(b.get())
        .setOperator(GTGTGTEQUALS)
        .build();

    String output = TestAssignmentExpressionsMill.prettyPrint(result, false).trim();

    assertEquals("a>>>=b", output);
  
    assertTrue(Log.getFindings().isEmpty());
  }
}
