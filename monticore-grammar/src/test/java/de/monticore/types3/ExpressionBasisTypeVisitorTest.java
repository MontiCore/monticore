/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types3;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsGlobalScope;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static de.monticore.types3.util.DefsTypesForTests._intSymType;
import static de.monticore.types3.util.DefsTypesForTests._personSymType;
import static de.monticore.types3.util.DefsTypesForTests._shortSymType;
import static de.monticore.types3.util.DefsTypesForTests.function;
import static de.monticore.types3.util.DefsTypesForTests.inScope;
import static de.monticore.types3.util.DefsTypesForTests.variable;

public class ExpressionBasisTypeVisitorTest extends AbstractTypeVisitorTest {

  @BeforeEach
  public void setupForEach() {
    setupValues();
    setupOverloadedFuncs();
  }

  protected void setupOverloadedFuncs() {
    IBasicSymbolsGlobalScope gs = BasicSymbolsMill.globalScope();

    inScope(gs, function("funcSameNameAsVar1", _intSymType, _intSymType));
    inScope(gs, variable("funcSameNameAsVar1", _shortSymType));

    //specifically test function without params
    inScope(gs, function("funcSameNameAsVar2", _intSymType));
    inScope(gs, variable("funcSameNameAsVar2", _shortSymType));

    inScope(gs, function("overloadedFunc2", _intSymType, _intSymType));
    inScope(gs, function("overloadedFunc2", _personSymType, _personSymType));

    inScope(gs, function("overloadedFunc3", _intSymType, _intSymType));
    inScope(gs, function("overloadedFunc3", _shortSymType, _shortSymType));
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
    checkExpr("intLinkedList", "java.util.LinkedList<int>");
  }

  @Test
  public void deriveTFromASTNameExpressionTargetType1() throws IOException {
    checkExpr("funcSameNameAsVar1", "int -> int", "int -> int");
    checkExpr("funcSameNameAsVar1", "short", "short");
  }

  @Test
  public void deriveTFromASTNameExpressionTargetType2() throws IOException {
    checkExpr("funcSameNameAsVar2", "() -> int", "() -> int");
    checkExpr("funcSameNameAsVar2", "short", "short");
  }

  @Test
  public void deriveTFromASTNameExpressionTargetType3() throws IOException {
    checkExpr("overloadedFunc2", "int -> int", "int -> int");
    checkExpr("overloadedFunc2", "Person -> Person", "Person -> Person");
    checkExpr("overloadedFunc2", "(Person -> Person) & (int -> int)");
  }

  @Test
  public void deriveTFromASTNameExpressionTargetType4() throws IOException {
    checkExpr("overloadedFunc3", "int -> int", "int -> int");
    checkExpr("overloadedFunc3", "short -> short", "short -> short");
    checkExpr("overloadedFunc3", "short -> int", "short -> short");
  }

  @Test
  public void deriveTFromASTNameExpressionTargetTypeInvalid1() throws IOException {
    checkErrorExpr("overloadedFunc3", "int -> short", "0xFD451");
    // none is more specific than the other
    checkExpr("overloadedFunc3", "(int -> int) & (short -> short)");
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
