/* (c) https://github.com/MontiCore/monticore */
package de.monticore.prettyprint;

import de.monticore.javalight._ast.*;
import de.monticore.testjavalight.TestJavaLightMill;
import de.monticore.testjavalight._parser.TestJavaLightParser;
import de.monticore.javalight._prettyprint.JavaLightFullPrettyPrinter;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class JavaLightPrettyPrinterTest {

  private TestJavaLightParser parser = new TestJavaLightParser();

  private JavaLightFullPrettyPrinter prettyPrinter = new JavaLightFullPrettyPrinter(new IndentPrinter());

  @BeforeEach
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    TestJavaLightMill.reset();
    TestJavaLightMill.init();
    prettyPrinter.getPrinter().clearBuffer();
  }
  
  @Test
  public void testMethodDeclaration() throws IOException {
    Optional<ASTMethodDeclaration> result = parser.parse_StringMethodDeclaration("private static final int foo(String s[], boolean b)[][][] throws e.Exception { private Integer foo = a; }");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTMethodDeclaration ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringMethodDeclaration(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());

    Assertions.assertTrue(ast.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testAbstractInterfaceMethod() throws IOException {
    Optional<ASTMethodDeclaration> result = parser.parse_StringMethodDeclaration("public void foo(String s, boolean b);");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTMethodDeclaration ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringMethodDeclaration(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());


    Assertions.assertTrue(ast.deepEquals(result.get()), "Parse pp output: "  + output);

    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testDefaultMethod() throws IOException {
    Optional<ASTMethodDeclaration> result = parser.parse_StringMethodDeclaration("default int foo(String s, boolean b)[][] throws e.Exception { return expr; }");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTMethodDeclaration ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringMethodDeclaration(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());

    Assertions.assertTrue(ast.deepEquals(result.get()));

    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testConstructorDeclaration() throws IOException {
    Optional<ASTConstructorDeclaration> result = parser.parse_StringConstructorDeclaration("public ClassName(String s, boolean b) throws e.Exception { private Integer foo = a;}");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTConstructorDeclaration ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringConstructorDeclaration(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());

    Assertions.assertTrue(ast.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testConstDeclaration() throws IOException {
    Optional<ASTConstDeclaration> result = parser.parse_StringConstDeclaration("private static Foo foo [][][] = a;");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTConstDeclaration ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringConstDeclaration(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());

    Assertions.assertTrue(ast.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testThrows() throws IOException {
    Optional<ASTThrows> result = parser.parse_StringThrows("a.b.c.D, person.A ");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTThrows ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringThrows(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());

    Assertions.assertTrue(ast.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testFormalParameters() throws IOException {
    Optional<ASTFormalParameters> result = parser.parse_StringFormalParameters("(public float f, int i, private ASTNode n, Float ... a)");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTFormalParameters ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringFormalParameters(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());

    Assertions.assertTrue(ast.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testFormalParameterListing() throws IOException {
    Optional<ASTFormalParameterListing> result = parser.parse_StringFormalParameterListing("public float f, int i, private ASTNode n, Float ... a");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTFormalParameterListing ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringFormalParameterListing(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());

    Assertions.assertTrue(ast.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testLastFormalParameter() throws IOException {
    Optional<ASTLastFormalParameter> result = parser.parse_StringLastFormalParameter("private String ... a");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTLastFormalParameter ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringLastFormalParameter(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());

    Assertions.assertTrue(ast.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testAnnotation() throws IOException {
    Optional<ASTAnnotation> result = parser.parse_StringAnnotation("@java.util.List (a = ++a, b = {c, d})");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTAnnotation ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringAnnotation(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());

    Assertions.assertTrue(ast.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testAnnotationPairArguments() throws IOException {
    Optional<ASTAnnotationPairArguments> result = parser.parse_StringAnnotationPairArguments("a = ++a, b = {c, d}");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTAnnotationPairArguments ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringAnnotationPairArguments(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());

    Assertions.assertTrue(ast.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }


  @Test
  public void testElementValueOrExpr() throws IOException {
    Optional<ASTElementValueOrExpr> result = parser.parse_StringElementValueOrExpr("++a");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTElementValueOrExpr ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringElementValueOrExpr(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());

    Assertions.assertTrue(ast.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testElementValueArrayInitializer() throws IOException {
    Optional<ASTElementValueArrayInitializer> result = parser.parse_StringElementValueArrayInitializer("{c, d}");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTElementValueArrayInitializer ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringElementValueArrayInitializer(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());

    Assertions.assertTrue(ast.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testElementValuePair() throws IOException {
    Optional<ASTElementValuePair> result = parser.parse_StringElementValuePair("a = ++a");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTElementValuePair ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringElementValuePair(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());

    Assertions.assertTrue(ast.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testCreatorExpression2() throws IOException {
    Optional<ASTArrayDimensionByInitializer> result = parser.parse_StringArrayDimensionByInitializer("[][]{{}}");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTArrayDimensionByInitializer ast = result.get();
    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringArrayDimensionByInitializer(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());

    Assertions.assertTrue(ast.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

}
