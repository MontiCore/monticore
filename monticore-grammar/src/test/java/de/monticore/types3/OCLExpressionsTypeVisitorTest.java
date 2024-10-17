package de.monticore.types3;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.mccollectiontypes.types3.MCCollectionSymTypeRelations;
import de.monticore.types3.util.DefsVariablesForTests;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static de.monticore.types3.util.DefsTypesForTests._intSymType;
import static de.monticore.types3.util.DefsTypesForTests.function;
import static de.monticore.types3.util.DefsTypesForTests.inScope;
import static de.monticore.types3.util.DefsTypesForTests.variable;
import static org.junit.Assume.assumeFalse;

public class OCLExpressionsTypeVisitorTest extends AbstractTypeVisitorTest {

  @BeforeEach
  public void setup() {
    MCCollectionSymTypeRelations.init();
    setupValues();
    assertNoFindings();
  }

  protected void setupValues() {
    IBasicSymbolsScope gs = BasicSymbolsMill.globalScope();
    DefsVariablesForTests.set_thePrimitives(gs);
    DefsVariablesForTests.set_boxedPrimitives(gs);
    DefsVariablesForTests.set_unboxedObjects(gs);
    DefsVariablesForTests.set_objectTypes(gs);
    inScope(gs, variable("intArray1", SymTypeExpressionFactory.createTypeArray(_intSymType, 1)));
    inScope(gs, variable("intArray2", SymTypeExpressionFactory.createTypeArray(_intSymType, 2)));
    inScope(gs, variable("intArray3", SymTypeExpressionFactory.createTypeArray(_intSymType, 3)));
  }

  @Test
  public void checkTypeIfExpressions() throws IOException {
    checkExpr("typeif vardouble instanceof double then 5.0 else 2*2", "double");
    checkExpr("typeif vardouble instanceof int then 5 else 5.0", "double");
  }

  @Test
  public void checkTypeIfExpressionCorrectTypeInThen() throws IOException {
    // add Student::getSemester
    BasicSymbolsMill.globalScope()
        .resolveType("Student")
        .get()
        .addFunctionSymbol(function("getSemester", _intSymType));
    // varPerson.getSemester() allowed iff varPerson is a Student
    checkExpr(
        "typeif varPerson instanceof Student "
            + "then varPerson.getSemester() else -1",
        "int"
    );
  }

  @Test
  public void checkTypeIfExpressionRedundant() throws IOException {
    checkErrorExpr(
        "typeif varStudent instanceof Person "
            + "then varStudent else varPerson",
        "0xFD290"
    );
  }

  @Test
  public void checkIfThenElseExpressions()
      throws IOException {
    checkExpr("if true then 5.0 else 2*2", "double");
    checkExpr("if true then 5 else 5.0", "double");
    checkExpr("if true then varPerson else varStudent", "Person");
  }

  @Test
  public void checkImpliesExpressions() throws IOException {
    checkExpr("true implies false", "boolean");
  }

  @Test
  public void checkEquivalentExpressions()
      throws IOException {
    checkExpr("true <=> false", "boolean");
  }

  @Test
  public void checkForallExpressions() throws IOException {
    checkExpr("forall int num in {1,2,3} : (num + 1) > 1", "boolean");
  }

  @Test
  public void checkExistsExpressions() throws IOException {
    checkExpr("exists int num in {1,2,3} : (num + 1) < 3", "boolean");
  }

  @Test
  public void checkAnyExpressions() throws IOException {
    checkExpr("any {1}", "int");
  }

  @Test
  public void checkLetInExpression() throws IOException {
    checkExpr("let double a = 5.0 in 2*2", "int");
    checkExpr("let double a = 5.0 in a", "double");
    checkExpr("let double a = 5.0; int b = 5 in a*b", "double");
  }

  @Test
  public void checkIterateExpression() throws IOException {
    checkExpr(
        "iterate { int a in {1,2,3}; int count = 0 : count=count+1 }",
        "int"
    );
    checkExpr(
        "iterate { a in {1,2,3} ; count = 0 : count=count+1 }",
        "int"
    );
    checkExpr(
        "iterate { int a in {1,2,3}; List<int> l = [0] : l = l }",
        "List<int>"
    );
  }

  @Test
  public void checkInstanceofExpression() throws IOException {
    checkExpr("true instanceof boolean", "boolean");
  }

  @Test
  public void checkArrayQualificationExpression()
      throws IOException {
    checkExpr("intArray1[0]", "int");
    checkExpr("intArray2[0]", "int[]");
    checkExpr("intArray2[0][0]", "int");
  }

  @Test
  public void checkAtPreQualificationExpression()
      throws IOException {
    checkExpr("varboolean@pre", "boolean");
    checkExpr("varint@pre", "int");
    checkExpr("varPerson@pre", "Person");
  }

  @Test
  public void complexAtPreTest1() throws IOException {
    TypeSymbol person = BasicSymbolsMill.globalScope()
        .resolveType("Person")
        .get();
    person.addFunctionSymbol(function("getAge", _intSymType));
    person.addVariableSymbol(variable("age", _intSymType));
    // todo enable test after type dispatcher fix
    Assumptions.assumeFalse(true);
    checkExpr("varPerson@pre.getAge()", "int");
    checkExpr("(varPerson@pre).getAge()", "int");
    checkExpr("varPerson@pre.age", "int");
    checkExpr("(varPerson@pre).age", "int");
  }

  @Test
  public void checkTransitiveQualificationExpression()
      throws IOException {
    checkExpr("varPerson**", "Person");
  }

}
