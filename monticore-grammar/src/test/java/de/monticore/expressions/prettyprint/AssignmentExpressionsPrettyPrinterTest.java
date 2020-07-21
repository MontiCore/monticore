/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.prettyprint;

import de.monticore.expressions.assignmentexpressions.AssignmentExpressionsMill;
import de.monticore.expressions.assignmentexpressions._ast.*;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.testassignmentexpressions._parser.TestAssignmentExpressionsParser;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static de.monticore.expressions.assignmentexpressions._ast.ASTConstantsAssignmentExpressions.*;
import static org.junit.Assert.*;

public class AssignmentExpressionsPrettyPrinterTest {

  private TestAssignmentExpressionsParser parser = new TestAssignmentExpressionsParser();

  private AssignmentExpressionsPrettyPrinter prettyPrinter= new AssignmentExpressionsPrettyPrinter(new IndentPrinter());

  @BeforeClass
  public static void setUp() {
    LogStub.init();
    Log.enableFailQuick(false);
  }

  @Before
  public void init() {
    prettyPrinter.getPrinter().clearBuffer();
  }

  @Test
  public void testIncPrefixExpression() throws IOException {
    Optional<ASTIncPrefixExpression> result = parser.parse_StringIncPrefixExpression("++a");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTIncPrefixExpression ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringIncPrefixExpression(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testDecPrefixExpression() throws IOException {
    Optional<ASTDecPrefixExpression> result = parser.parse_StringDecPrefixExpression("--a");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTDecPrefixExpression ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringDecPrefixExpression(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testIncSuffixExpression() throws IOException {
    Optional<ASTExpression> a = parser.parse_StringExpression("a");
    assertFalse(parser.hasErrors());
    assertTrue(a.isPresent());
    ASTIncSuffixExpression result = AssignmentExpressionsMill.incSuffixExpressionBuilder()
        .setExpression(a.get())
        .build();

    String output = prettyPrinter.prettyprint(result);

    assertEquals("a++", output);
  }

  @Test
  public void testDecSuffixExpression() throws IOException {
    Optional<ASTExpression> a = parser.parse_StringExpression("a");
    assertFalse(parser.hasErrors());
    assertTrue(a.isPresent());
    ASTDecSuffixExpression result = AssignmentExpressionsMill.decSuffixExpressionBuilder()
        .setExpression(a.get())
        .build();

    String output = prettyPrinter.prettyprint(result);

    assertEquals("a--", output);
  }

  @Test
  public void testRegularAssignmentEqualsExpression() throws IOException {
    Optional<ASTExpression> a = parser.parse_StringExpression("a");
    Optional<ASTExpression> b = parser.parse_StringExpression("b");
    assertFalse(parser.hasErrors());
    assertTrue(a.isPresent());
    assertTrue(b.isPresent());
    ASTAssignmentExpression result = AssignmentExpressionsMill.assignmentExpressionBuilder()
        .setLeft(a.get())
        .setRight(b.get())
        .setOperator(EQUALS)
        .build();

    String output = prettyPrinter.prettyprint(result);

    assertEquals("a=b", output);
  }

  @Test
  public void testRegularAssignmentPlusEqualsExpression() throws IOException {
    Optional<ASTExpression> a = parser.parse_StringExpression("a");
    Optional<ASTExpression> b = parser.parse_StringExpression("b");
    assertFalse(parser.hasErrors());
    assertTrue(a.isPresent());
    assertTrue(b.isPresent());
    ASTAssignmentExpression result = AssignmentExpressionsMill.assignmentExpressionBuilder()
        .setLeft(a.get())
        .setRight(b.get())
        .setOperator(PLUSEQUALS)
        .build();

    String output = prettyPrinter.prettyprint(result);

    assertEquals("a+=b", output);
  }

  @Test
  public void testRegularAssignmentMinusExpression() throws IOException {
    Optional<ASTExpression> a = parser.parse_StringExpression("a");
    Optional<ASTExpression> b = parser.parse_StringExpression("b");
    assertFalse(parser.hasErrors());
    assertTrue(a.isPresent());
    assertTrue(b.isPresent());
    ASTAssignmentExpression result = AssignmentExpressionsMill.assignmentExpressionBuilder()
        .setLeft(a.get())
        .setRight(b.get())
        .setOperator(MINUSEQUALS)
        .build();

    String output = prettyPrinter.prettyprint(result);

    assertEquals("a-=b", output);
  }

  @Test
  public void testRegularAssignmentPercentEqualsExpression() throws IOException {
    Optional<ASTExpression> a = parser.parse_StringExpression("a");
    Optional<ASTExpression> b = parser.parse_StringExpression("b");
    assertFalse(parser.hasErrors());
    assertTrue(a.isPresent());
    assertTrue(b.isPresent());
    ASTAssignmentExpression result = AssignmentExpressionsMill.assignmentExpressionBuilder()
        .setLeft(a.get())
        .setRight(b.get())
        .setOperator(PERCENTEQUALS)
        .build();

    String output = prettyPrinter.prettyprint(result);

    assertEquals("a%=b", output);
  }

  @Test
  public void testRegularAssignmentAndEqualsExpression() throws IOException {
    Optional<ASTExpression> a = parser.parse_StringExpression("a");
    Optional<ASTExpression> b = parser.parse_StringExpression("b");
    assertFalse(parser.hasErrors());
    assertTrue(a.isPresent());
    assertTrue(b.isPresent());
    ASTAssignmentExpression result = AssignmentExpressionsMill.assignmentExpressionBuilder()
        .setLeft(a.get())
        .setRight(b.get())
        .setOperator(ANDEQUALS)
        .build();

    String output = prettyPrinter.prettyprint(result);

    assertEquals("a&=b", output);
  }

  @Test
  public void testRegularAssignmentRoofEqualsExpression() throws IOException {
    Optional<ASTExpression> a = parser.parse_StringExpression("a");
    Optional<ASTExpression> b = parser.parse_StringExpression("b");
    assertFalse(parser.hasErrors());
    assertTrue(a.isPresent());
    assertTrue(b.isPresent());
    ASTAssignmentExpression result = AssignmentExpressionsMill.assignmentExpressionBuilder()
        .setLeft(a.get())
        .setRight(b.get())
        .setOperator(ROOFEQUALS)
        .build();

    String output = prettyPrinter.prettyprint(result);

    assertEquals("a^=b", output);
  }

  @Test
  public void testRegularAssignmentSlashEqualsExpression() throws IOException {
    Optional<ASTExpression> a = parser.parse_StringExpression("a");
    Optional<ASTExpression> b = parser.parse_StringExpression("b");
    assertFalse(parser.hasErrors());
    assertTrue(a.isPresent());
    assertTrue(b.isPresent());
    ASTAssignmentExpression result = AssignmentExpressionsMill.assignmentExpressionBuilder()
        .setLeft(a.get())
        .setRight(b.get())
        .setOperator(SLASHEQUALS)
        .build();

    String output = prettyPrinter.prettyprint(result);

    assertEquals("a/=b", output);
  }

  @Test
  public void testRegularAssignmentStarEqualsExpression() throws IOException {
    Optional<ASTExpression> a = parser.parse_StringExpression("a");
    Optional<ASTExpression> b = parser.parse_StringExpression("b");
    assertFalse(parser.hasErrors());
    assertTrue(a.isPresent());
    assertTrue(b.isPresent());
    ASTAssignmentExpression result = AssignmentExpressionsMill.assignmentExpressionBuilder()
        .setLeft(a.get())
        .setRight(b.get())
        .setOperator(STAREQUALS)
        .build();

    String output = prettyPrinter.prettyprint(result);

    assertEquals("a*=b", output);
  }

  @Test
  public void testRegularAssignmentPipeEqualsExpression() throws IOException {
    Optional<ASTExpression> a = parser.parse_StringExpression("a");
    Optional<ASTExpression> b = parser.parse_StringExpression("b");
    assertFalse(parser.hasErrors());
    assertTrue(a.isPresent());
    assertTrue(b.isPresent());
    ASTAssignmentExpression result = AssignmentExpressionsMill.assignmentExpressionBuilder()
        .setLeft(a.get())
        .setRight(b.get())
        .setOperator(PIPEEQUALS)
        .build();

    String output = prettyPrinter.prettyprint(result);

    assertEquals("a|=b", output);
  }

  @Test
  public void testRegularAssignmentLTLTEqualsExpression() throws IOException {
    Optional<ASTExpression> a = parser.parse_StringExpression("a");
    Optional<ASTExpression> b = parser.parse_StringExpression("b");
    assertFalse(parser.hasErrors());
    assertTrue(a.isPresent());
    assertTrue(b.isPresent());
    ASTAssignmentExpression result = AssignmentExpressionsMill.assignmentExpressionBuilder()
        .setLeft(a.get())
        .setRight(b.get())
        .setOperator(LTLTEQUALS)
        .build();

    String output = prettyPrinter.prettyprint(result);

    assertEquals("a<<=b", output);
  }

  @Test
  public void testRegularAssignmentGTGTEqualsExpression() throws IOException {
    Optional<ASTExpression> a = parser.parse_StringExpression("a");
    Optional<ASTExpression> b = parser.parse_StringExpression("b");
    assertFalse(parser.hasErrors());
    assertTrue(a.isPresent());
    assertTrue(b.isPresent());
    ASTAssignmentExpression result = AssignmentExpressionsMill.assignmentExpressionBuilder()
        .setLeft(a.get())
        .setRight(b.get())
        .setOperator(GTGTEQUALS)
        .build();

    String output = prettyPrinter.prettyprint(result);

    assertEquals("a>>=b", output);
  }

  @Test
  public void testRegularAssignmentGTGTGTEqualsExpression() throws IOException {
    Optional<ASTExpression> a = parser.parse_StringExpression("a");
    Optional<ASTExpression> b = parser.parse_StringExpression("b");
    assertFalse(parser.hasErrors());
    assertTrue(a.isPresent());
    assertTrue(b.isPresent());
    ASTAssignmentExpression result = AssignmentExpressionsMill.assignmentExpressionBuilder()
        .setLeft(a.get())
        .setRight(b.get())
        .setOperator(GTGTGTEQUALS)
        .build();

    String output = prettyPrinter.prettyprint(result);

    assertEquals("a>>>=b", output);
  }
}
