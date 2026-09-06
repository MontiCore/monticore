/* (c) [https://github.com/MontiCore/monticore](https://github.com/MontiCore/monticore) */
package de.monticore.statements.cocos;

import de.monticore.runtime.junit.MCAssertions;
import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.statements.mccommonstatements._ast.ASTEnhancedForControl;
import de.monticore.statements.mccommonstatements.cocos.ForEachIsValid;
import de.monticore.statements.testmccommonstatements.TestMCCommonStatementsMill;
import de.monticore.statements.testmccommonstatements._cocos.TestMCCommonStatementsCoCoChecker;
import de.monticore.statements.testmccommonstatements._symboltable.ITestMCCommonStatementsScope;
import de.monticore.statements.testmccommonstatements._visitor.TestMCCommonStatementsTraverser;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.types.check.FlatExpressionScopeSetter;
import de.monticore.types.check.SymTypeOfGenerics;
import de.monticore.types3.util.CombineExpressionsWithLiteralsTypeTraverserFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import static de.monticore.statements.mccommonstatements.cocos.ForEachIsValid.FOR_EACH_EXPR_NOT_ITERABLE_ERROR_CODE;
import static de.monticore.statements.mccommonstatements.cocos.ForEachIsValid.FOR_EACH_TYPE_MISMATCH_ERROR_CODE;
import static de.monticore.statements.testmccommonstatements.TestMCCommonStatementsMill.*;
import static de.monticore.symbols.basicsymbols.BasicSymbolsMill.BOOLEAN;
import static de.monticore.symbols.basicsymbols.BasicSymbolsMill.INT;
import static de.monticore.types.check.SymTypeExpressionFactory.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

/**
 * The class under test is {@link ForEachIsValid}.
 */
@TestWithMCLanguage(TestMCCommonStatementsMill.class)
class ForEachIsValidTest {

  @BeforeEach
  void init() {
    CombineExpressionsWithLiteralsTypeTraverserFactory.initTypeCheck3();
    BasicSymbolsMill.initializePrimitives();

    // ===== Types ===== //

    // type var T
    TypeVarSymbol T = typeVarSymbolBuilder().setName("T").build();

    // type java.lang.Iterable<T>
    TypeSymbol Iterable = oOTypeSymbolBuilder()
      .setName("Iterable")
      .setPackageName("java.lang")
      .setEnclosingScope(globalScope())
      .setSpannedScope(scope())
      .build();
    Iterable.getEnclosingScope().add(Iterable);
    Iterable.getEnclosingScope().addSubScope(Iterable.getSpannedScope());
    Iterable.getSpannedScope().add(T);

    // type A1 implements java.lang.Iterable<boolean>
    TypeSymbol A1 = oOTypeSymbolBuilder()
      .setName("A1")
      .setEnclosingScope(globalScope())
      .setSpannedScope(scope())
      .setSuperTypesList(List.of(createGenerics(Iterable, createPrimitive(BOOLEAN))))
      .build();
    A1.getEnclosingScope().add(A1);
    A1.getEnclosingScope().addSubScope(A1.getSpannedScope());

    // type A2 implements A1
    TypeSymbol A2 = oOTypeSymbolBuilder()
      .setName("A2")
      .setEnclosingScope(globalScope())
      .setSpannedScope(scope())
      .setSuperTypesList(List.of(createTypeObject(A1)))
      .build();
    A2.getEnclosingScope().add(A2);
    A2.getEnclosingScope().addSubScope(A2.getSpannedScope());

    // type var TB
    TypeVarSymbol TB = typeVarSymbolBuilder().setName("TB").build();

    SymTypeOfGenerics superIterableOfA = createGenerics(Iterable, createTypeVariable(TB));

    // type B<TB> implements java.lang.Iterable<TB>
    TypeSymbol B = oOTypeSymbolBuilder()
      .setName("B")
      .setEnclosingScope(globalScope())
      .setSpannedScope(scope())
      .setSuperTypesList(List.of(superIterableOfA))
      .build();
    B.getEnclosingScope().add(B);
    B.getEnclosingScope().addSubScope(B.getSpannedScope());
    B.getSpannedScope().add(TB);

    // type var TC
    TypeVarSymbol TC = typeVarSymbolBuilder().setName("TC").build();

    SymTypeOfGenerics superAOfB = createGenerics(B, createTypeVariable(TC));

    // type C<TC> implements B<TC>
    TypeSymbol C = oOTypeSymbolBuilder()
      .setName("C")
      .setEnclosingScope(globalScope())
      .setSpannedScope(scope())
      .setSuperTypesList(List.of(superAOfB))
      .build();
    C.getEnclosingScope().add(C);
    C.getEnclosingScope().addSubScope(C.getSpannedScope());
    C.getSpannedScope().add(TC);

    // type D implements C<int>
    TypeSymbol D = oOTypeSymbolBuilder()
      .setName("D")
      .setEnclosingScope(globalScope())
      .setSpannedScope(scope())
      .setSuperTypesList(List.of(createGenerics(C, createPrimitive(INT))))
      .build();
    D.getEnclosingScope().add(D);
    D.getEnclosingScope().addSubScope(D.getSpannedScope());

    // ===== Fields ====== //
    FieldSymbol bool = fieldSymbolBuilder()
      .setName("bool")
      .setType(createPrimitive(BOOLEAN))
      .setEnclosingScope(globalScope())
      .build();
    bool.getEnclosingScope().add(bool);

    FieldSymbol a1Booleans = fieldSymbolBuilder()
      .setName("a1Booleans")
      .setType(createTypeObject(A1))
      .setEnclosingScope(globalScope())
      .build();
    a1Booleans.getEnclosingScope().add(a1Booleans);

    FieldSymbol a2Booleans = fieldSymbolBuilder()
      .setName("a2Booleans")
      .setType(createTypeObject(A1))
      .setEnclosingScope(globalScope())
      .build();
    a2Booleans.getEnclosingScope().add(a2Booleans);

    FieldSymbol integers = fieldSymbolBuilder()
      .setName("integers")
      .setType(createTypeObject(D))
      .setEnclosingScope(globalScope())
      .build();
    integers.getEnclosingScope().add(integers);

    FieldSymbol a1ArrayBooleans1 = fieldSymbolBuilder()
      .setName("a1ArrayBooleans1")
      .setType(createTypeArray(createTypeObject(A1), 1))
      .setEnclosingScope(globalScope())
      .build();
    a1ArrayBooleans1.getEnclosingScope().add(a1ArrayBooleans1);

    FieldSymbol a1ArrayBooleans2 = fieldSymbolBuilder()
      .setName("a1ArrayBooleans2")
      .setType(createTypeArray(createTypeObject(A1), 2))
      .setEnclosingScope(globalScope())
      .build();
    a1ArrayBooleans2.getEnclosingScope().add(a1ArrayBooleans2);

    FieldSymbol a2ArrayBooleans1 = fieldSymbolBuilder()
      .setName("a2ArrayBooleans1")
      .setType(createTypeArray(createTypeObject(A2), 1))
      .setEnclosingScope(globalScope())
      .build();
    a2ArrayBooleans1.getEnclosingScope().add(a2ArrayBooleans1);

    FieldSymbol a2ArrayBooleans2 = fieldSymbolBuilder()
      .setName("a2ArrayBooleans2")
      .setType(createTypeArray(createTypeObject(A2), 2))
      .setEnclosingScope(globalScope())
      .build();
    a2ArrayBooleans2.getEnclosingScope().add(a2ArrayBooleans2);
  }

  private void addToTraverser(TestMCCommonStatementsTraverser traverser, ITestMCCommonStatementsScope enclosingScope) {
    FlatExpressionScopeSetter scopeSetter = new FlatExpressionScopeSetter(enclosingScope);
    traverser.add4ExpressionsBasis(scopeSetter);
    traverser.add4CommonExpressions(scopeSetter);
    traverser.add4MCBasicTypes(scopeSetter);
    traverser.add4MCCollectionTypes(scopeSetter);
    traverser.add4MCArrayTypes(scopeSetter);
    traverser.add4MCCommonLiterals(scopeSetter);
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "boolean b : a1Booleans",
    "boolean b : a2Booleans",
    "int i : integers",
    "A1 a : a1ArrayBooleans1",
    "A1[] a : a1ArrayBooleans2",
    "A1 a : a2ArrayBooleans1",
    "A1[] a : a2ArrayBooleans2",
    "A2 a : a2ArrayBooleans1",
    "A2[] a : a2ArrayBooleans2"
  })
  void testValid(String expr) throws IOException {
    // Given
    TestMCCommonStatementsCoCoChecker checker = new TestMCCommonStatementsCoCoChecker();
    checker.addCoCo(new ForEachIsValid());

    ASTEnhancedForControl ast = TestMCCommonStatementsMill.parser()
      .parse_StringEnhancedForControl(expr)
      .orElseThrow();

    TestMCCommonStatementsTraverser traverser = TestMCCommonStatementsMill.traverser();
    addToTraverser(traverser, globalScope());
    ast.accept(traverser);
    ast.setEnclosingScope(globalScope());

    // When
    checker.checkAll(ast);
  }

  @ParameterizedTest
  @MethodSource("exprAndErrorProvider")
  void testInvalid(String expr, String[] error) throws IOException {
    // Given
    TestMCCommonStatementsCoCoChecker checker = new TestMCCommonStatementsCoCoChecker();
    checker.addCoCo(new ForEachIsValid());

    ASTEnhancedForControl ast = TestMCCommonStatementsMill.parser()
      .parse_StringEnhancedForControl(expr)
      .orElseThrow();

    TestMCCommonStatementsTraverser traverser = TestMCCommonStatementsMill.traverser();
    addToTraverser(traverser, globalScope());
    ast.accept(traverser);
    ast.setEnclosingScope(globalScope());

    // When
    checker.checkAll(ast);
    
    // Then
    for (String errorCode : error) {
      MCAssertions.assertHasFindingStartingWith(errorCode);
    }
  }

  static Stream<Arguments> exprAndErrorProvider() {
    return Stream.of(
      arguments("boolean b : bool", new String[]{FOR_EACH_EXPR_NOT_ITERABLE_ERROR_CODE}),
      arguments("int i : a1Booleans", new String[]{FOR_EACH_TYPE_MISMATCH_ERROR_CODE}),
      arguments("A2 a : a1ArrayBooleans1", new String[]{FOR_EACH_TYPE_MISMATCH_ERROR_CODE}),
      arguments("A2[] a : a1ArrayBooleans2", new String[]{FOR_EACH_TYPE_MISMATCH_ERROR_CODE}),
      arguments("boolean b : missing", new String[]{"0xFD118"}),
      arguments("Missing m : a1Booleans", new String[]{"0xA0324"}),
      arguments("Missing m : missing", new String[]{"0xA0324", "0xFD118"})
    );
  }
}
