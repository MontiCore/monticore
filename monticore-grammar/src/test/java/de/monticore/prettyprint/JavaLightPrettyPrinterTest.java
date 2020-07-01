/* (c) https://github.com/MontiCore/monticore */
package de.monticore.prettyprint;

import de.monticore.expressions.javaclassexpressions._ast.ASTCreatorExpression;
import de.monticore.javalight._ast.*;
import de.monticore.statements.mcvardeclarationstatements._ast.ASTLocalVariableDeclaration;
import de.monticore.testjavalight._parser.TestJavaLightParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class JavaLightPrettyPrinterTest {

  private TestJavaLightParser parser = new TestJavaLightParser();

  private JavaLightPrettyPrinterDelegator prettyPrinter = new JavaLightPrettyPrinterDelegator(new IndentPrinter());

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
  public void testClassBlock() throws IOException {
    Optional<ASTClassBlock> result = parser.parse_StringClassBlock("static { private Integer foo = a;}");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTClassBlock ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringClassBlock(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testMethodDeclaration() throws IOException {
    Optional<ASTMethodDeclaration> result = parser.parse_StringMethodDeclaration("private static final int foo(String s, boolean b)[][][] throws e.Exception { private Integer foo = a; }");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTMethodDeclaration ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringMethodDeclaration(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testConstructorDeclaration() throws IOException {
    Optional<ASTConstructorDeclaration> result = parser.parse_StringConstructorDeclaration("public ClassName(String s, boolean b) throws e.Exception { private Integer foo = a;}");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTConstructorDeclaration ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringConstructorDeclaration(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testFieldDeclaration() throws IOException {
    Optional<ASTLocalVariableDeclaration> result = parser.parse_StringLocalVariableDeclaration("private static List a = b, c = d");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTLocalVariableDeclaration ast = result.get();

    prettyPrinter.handle(ast);
    String output = prettyPrinter.getPrinter().getContent();

    result = parser.parse_StringLocalVariableDeclaration(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testConstDeclaration() throws IOException {
    Optional<ASTConstDeclaration> result = parser.parse_StringConstDeclaration("private static Foo foo [][][] = a;");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTConstDeclaration ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringConstDeclaration(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testInterfaceMethodDeclaration() throws IOException {
    Optional<ASTInterfaceMethodDeclaration> result = parser.parse_StringInterfaceMethodDeclaration("private static final int foo(String s, boolean b)[][][] throws e.Exception;");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTInterfaceMethodDeclaration ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringInterfaceMethodDeclaration(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testThrows() throws IOException {
    Optional<ASTThrows> result = parser.parse_StringThrows("a.b.c.D, person.A ");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTThrows ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringThrows(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testFormalParameters() throws IOException {
    Optional<ASTFormalParameters> result = parser.parse_StringFormalParameters("(public float f, int i, private ASTNode n, Float ... a)");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTFormalParameters ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringFormalParameters(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testFormalParameterListing() throws IOException {
    Optional<ASTFormalParameterListing> result = parser.parse_StringFormalParameterListing("public float f, int i, private ASTNode n, Float ... a");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTFormalParameterListing ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringFormalParameterListing(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testFormalParameter() throws IOException {
    Optional<ASTFormalParameter> result = parser.parse_StringFormalParameter("public float f");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTFormalParameter ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringFormalParameter(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testLastFormalParameter() throws IOException {
    Optional<ASTLastFormalParameter> result = parser.parse_StringLastFormalParameter("private String ... a");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTLastFormalParameter ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringLastFormalParameter(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testAnnotation() throws IOException {
    Optional<ASTAnnotation> result = parser.parse_StringAnnotation("@java.util.List (a = ++a, b = {c, d})");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTAnnotation ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringAnnotation(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testAnnotationPairArguments() throws IOException {
    Optional<ASTAnnotationPairArguments> result = parser.parse_StringAnnotationPairArguments("a = ++a, b = {c, d}");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTAnnotationPairArguments ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringAnnotationPairArguments(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }


  @Test
  public void testElementValueOrExpr() throws IOException {
    Optional<ASTElementValueOrExpr> result = parser.parse_StringElementValueOrExpr("++a");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTElementValueOrExpr ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringElementValueOrExpr(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testElementValueArrayInitializer() throws IOException {
    Optional<ASTElementValueArrayInitializer> result = parser.parse_StringElementValueArrayInitializer("{c, d}");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTElementValueArrayInitializer ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringElementValueArrayInitializer(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testElementValuePair() throws IOException {
    Optional<ASTElementValuePair> result = parser.parse_StringElementValuePair("a = ++a");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTElementValuePair ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringElementValuePair(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testEnhancedForControl() throws IOException {
    Optional<ASTEnhancedForControl> result = parser.parse_StringEnhancedForControl("protected int c[] : a");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTEnhancedForControl ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringEnhancedForControl(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

  @Test
  public void testCreatorExpression2() throws IOException {
    Optional<ASTArrayDimensionByInitializer> result = parser.parse_StringArrayDimensionByInitializer("[][]{{}}");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTArrayDimensionByInitializer ast = result.get();
    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringArrayDimensionByInitializer(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }

}
