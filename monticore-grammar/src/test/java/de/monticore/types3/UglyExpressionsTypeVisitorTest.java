package de.monticore.types3;

import de.monticore.symbols.oosymbols.OOSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsGlobalScope;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsScope;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types3.util.DefsVariablesForTests;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static de.monticore.types3.util.DefsTypesForTests._floatSymType;
import static de.monticore.types3.util.DefsTypesForTests._intSymType;
import static de.monticore.types3.util.DefsTypesForTests._personSymType;
import static de.monticore.types3.util.DefsTypesForTests._unboxedListSymType;
import static de.monticore.types3.util.DefsTypesForTests.method;

public class UglyExpressionsTypeVisitorTest extends AbstractTypeVisitorTest {

  @BeforeEach
  public void init() {
    DefsVariablesForTests.setup();
  }

  @Test
  public void deriveFromInstanceOfExpression() throws IOException {
    // subType
    checkExpr("varPerson instanceof Student", "boolean");
    checkExpr("varPerson instanceof CsStudent", "boolean");
    // same type (redundant)
    checkExpr("varPerson instanceof Person", "boolean");
    // superType (redundant)
    checkExpr("varStudent instanceof Person", "boolean");
    checkExpr("varCsStudent instanceof Person", "boolean");
  }

  @Test
  public void testInvalidInstanceOfExpression() throws IOException {
    // unrelated type
    checkErrorExpr("varintList instanceof Person", "0xFD203");
  }

  @Test
  public void deriveFromTypeCastExpression() throws IOException {
    // same type
    checkExpr("(Person)varPerson", "Person");
    // downcasting
    checkExpr("(Student)varPerson", "Student");
    checkExpr("(CsStudent)varPerson", "CsStudent");
    // upcasting
    checkExpr("(Person)varStudent", "Person");
    checkExpr("(Person)varCsStudent", "Person");
    // cast numbers
    checkExpr("(int)varfloat", "int");
    checkExpr("(float)varint", "float");
    checkExpr("(char)varshort", "char");
    checkExpr("(short)varchar", "short");
    // un-/boxing
    checkExpr("(int)varInteger", "int");
    checkExpr("(java.lang.Integer)varint", "java.lang.Integer");
  }

  @Test
  public void testInvalidTypeCastExpression() throws IOException {
    // unrelated type
    checkErrorExpr("(Person)varintList", "0xFD204");
  }

  @Test
  public void deriveFromClassCreatorExpression() throws IOException {
    IOOSymbolsGlobalScope gs = OOSymbolsMill.globalScope();
    IOOSymbolsScope personScope = (IOOSymbolsScope)
        gs.resolveType("Person").get().getSpannedScope();
    // constructors: () -> Person
    MethodSymbol constructor0 = method("Person", _personSymType);
    constructor0.setIsConstructor(true);
    personScope.add(constructor0);
    checkExpr("new Person()", "Person");
    // constructors: () -> Person, (int) -> Person
    MethodSymbol constructor1 = method("Person", _personSymType, _intSymType);
    constructor1.setIsConstructor(true);
    personScope.add(constructor1);
    checkExpr("new Person()", "Person");
    checkExpr("new Person(2)", "Person");
    // constructors: () -> Person, (int) -> Person, (float) -> Person
    MethodSymbol constructor2 = method("Person", _personSymType, _floatSymType);
    constructor2.setIsConstructor(true);
    personScope.add(constructor2);
    checkExpr("new Person()", "Person");
    checkExpr("new Person(2)", "Person");
    checkExpr("new Person(1.2f)", "Person");
  }

  @Test
  public void testInvalidClassCreatorExpression() throws IOException {
    IOOSymbolsGlobalScope gs = OOSymbolsMill.globalScope();
    IOOSymbolsScope personScope = (IOOSymbolsScope)
        gs.resolveType("Person").get().getSpannedScope();
    // constructors: () -> Person, (int) -> Person
    // no applicable constructor exists
    MethodSymbol constructor0 = method("Person", _personSymType);
    constructor0.setIsConstructor(true);
    personScope.add(constructor0);
    MethodSymbol constructor1 = method("Person", _personSymType, _intSymType);
    constructor1.setIsConstructor(true);
    personScope.add(constructor1);
    checkErrorExpr("new Person(1.2f)", "0xFD444");
    // constructors: () -> Person, (int) -> Person, (int) -> Person
    // no most specific constructor exists
    MethodSymbol constructor2 = method("Person", _personSymType, _intSymType);
    constructor2.setIsConstructor(true);
    personScope.add(constructor2);
    checkErrorExpr("new Person(2)", "0xFD446");
  }

  @Test
  public void testInvalidClassCreatorExpression2() throws IOException {
    // requires Object Type
    checkErrorExpr("new int()", "0xFD552");
    checkErrorExpr("new R\"h(e|a)llo\"()", "0xFD552");
  }

  @Test
  public void testGenericConstructorClassCreatorExpression() throws IOException {
    IOOSymbolsGlobalScope gs = OOSymbolsMill.globalScope();
    IOOSymbolsScope listScope = (IOOSymbolsScope)
        gs.resolveType("List").get().getSpannedScope();
    // constructors: () -> List<T>, (T) -> List<T>
    MethodSymbol constructor0 = method("List", _unboxedListSymType);
    constructor0.setIsConstructor(true);
    listScope.add(constructor0);
    MethodSymbol constructor1 = method("List", _unboxedListSymType,
        SymTypeExpressionFactory.createTypeVariable(listScope.getTypeVarSymbols().values().get(0))
    );
    constructor1.setIsConstructor(true);
    listScope.add(constructor1);

    checkExpr("new List()", "List<int>", "List<int>");
    checkExpr("new List(1)", "List<int>", "List<int>");
    checkExpr("new List(1)", "List<int>");
  }

  @Test
  public void deriveFromArrayCreatorExpression() throws IOException {
    checkExpr("new Person[1]", "Person[]");
    checkExpr("new Person[varInteger]", "Person[]");
    checkExpr("new Person[1][2]", "Person[][]");
    checkExpr("new Person[1][2][][]", "Person[][][][]");
    checkExpr("new R\"(R|r)egex\"[1]", "R\"(R|r)egex\"[]");
  }

  @Test
  public void testInvalidArrayCreatorExpression() throws IOException {
    checkErrorExpr("new Person[varfloat]", "0xFD556");
    checkErrorExpr("new Person[varPerson]", "0xFD556");
  }
}

