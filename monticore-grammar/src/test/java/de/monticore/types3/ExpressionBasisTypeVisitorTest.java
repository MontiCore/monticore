/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types3;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class ExpressionBasisTypeVisitorTest extends AbstractTypeVisitorTest {

  @Before
  public void setupForEach() {
    setupValues();
  }

  @Test
  public void deriveTFromASTNameExpression() throws IOException {
    checkExpr("varint", "int");
  }

  @Test
  public void deriveTFromASTNameExpression2() throws IOException {
    checkExpr("varboolean", "boolean");
  }

  @Test
  public void deriveTFromASTNameExpression3() throws IOException {
    checkExpr("person1", "Person");
  }

  @Test
  public void deriveTFromASTNameExpression4() throws IOException {
    checkExpr("student1", "Student");
  }

  @Test
  public void deriveTFromASTNameExpression5() throws IOException {
    checkExpr("csStudent1", "CsStudent");
  }

  @Test
  public void deriveTFromASTNameExpression6() throws IOException {
    checkExpr("intList", "java.util.List<int>");
  }

  @Test
  public void deriveTFromASTNameExpression7() throws IOException {
    checkExpr("intLinkedList", "LinkedList<int>");
  }

  @Test
  public void deriveTFromLiteralInt() throws IOException {
    checkExpr("42", "int");
  }

  @Test
  public void deriveTFromLiteralString() throws IOException {
    checkExpr("\"aStringi\"", "String");
  }

}
