/* (c) [https://github.com/MontiCore/monticore](https://github.com/MontiCore/monticore) */
package de.monticore.statements.cocos;

import de.monticore.statements.mccommonstatements._ast.ASTEnhancedForControl;
import de.monticore.statements.mccommonstatements.cocos.ForEachIsValid;
import de.monticore.statements.testmccommonstatements.TestMCCommonStatementsMill;
import de.monticore.statements.testmccommonstatements._cocos.TestMCCommonStatementsCoCoChecker;
import de.monticore.statements.testmccommonstatements._symboltable.ITestMCCommonStatementsScope;
import de.monticore.statements.testmccommonstatements._visitor.TestMCCommonStatementsTraverser;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.FlatExpressionScopeSetter;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfObject;
import de.monticore.types3.util.CombineExpressionsWithLiteralsTypeTraverserFactory;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.monticore.types.check.SymTypeExpressionFactory.createTypeObject;
import static de.monticore.types3.util.DefsTypesForTests.inScope;
import static de.monticore.types3.util.DefsTypesForTests.oOtype;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class ForEachIsValidTest {

  @BeforeEach
  void init() {
    LogStub.init();
    Log.enableFailQuick(false);

    TestMCCommonStatementsMill.reset();
    TestMCCommonStatementsMill.init();
    CombineExpressionsWithLiteralsTypeTraverserFactory.initTypeCheck3();
    BasicSymbolsMill.initializePrimitives();

    // Prepare type and symbol setup
    SymTypeOfObject iterableType =
      SymTypeExpressionFactory.createTypeObjectViaSurrogate("java.lang.Iterable", TestMCCommonStatementsMill.globalScope());
    SymTypeOfObject aObjectType =
      SymTypeExpressionFactory.createTypeObjectViaSurrogate("A", TestMCCommonStatementsMill.globalScope());

    TestMCCommonStatementsMill.globalScope().add(TestMCCommonStatementsMill
      .oOTypeSymbolBuilder()
      .setName("A")
      .setSpannedScope(TestMCCommonStatementsMill.scope())
      .addSuperTypes(iterableType)
      .build());

    ITestMCCommonStatementsScope javaScope = TestMCCommonStatementsMill.scope();
    javaScope.setName("java");

    ITestMCCommonStatementsScope langScope = TestMCCommonStatementsMill.scope();
    langScope.setName("lang");

    TestMCCommonStatementsMill.globalScope().addSubScope(javaScope);
    javaScope.addSubScope(langScope);

    langScope.add(TestMCCommonStatementsMill
      .oOTypeSymbolBuilder()
      .setName("Iterable")
      .setSpannedScope(TestMCCommonStatementsMill.scope())
      .build());

    ITestMCCommonStatementsScope utilScope = TestMCCommonStatementsMill.scope();
    utilScope.setName("util");
    javaScope.addSubScope(utilScope);

    utilScope.add(TestMCCommonStatementsMill
      .oOTypeSymbolBuilder()
      .setName("Arrays")
      .setSpannedScope(TestMCCommonStatementsMill.scope())
      .build());

    TestMCCommonStatementsMill.globalScope().add(TestMCCommonStatementsMill
      .fieldSymbolBuilder()
      .setName("a")
      .setType(aObjectType)
      .build());

    SymTypeOfObject objectType = createTypeObject(inScope(TestMCCommonStatementsMill.globalScope(), oOtype("Object")));

    TestMCCommonStatementsMill.globalScope().add(TestMCCommonStatementsMill
      .fieldSymbolBuilder()
      .setName("o")
      .setType(objectType)
      .build());
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
  @ValueSource(strings = {"Object o : a"})
  void testValid(String expr) throws IOException {
    // Given
    TestMCCommonStatementsCoCoChecker checker = new TestMCCommonStatementsCoCoChecker();
    checker.addCoCo(new ForEachIsValid());

    ASTEnhancedForControl ast = TestMCCommonStatementsMill.parser()
      .parse_StringEnhancedForControl(expr)
      .orElseThrow();

    TestMCCommonStatementsTraverser traverser = TestMCCommonStatementsMill.traverser();
    addToTraverser(traverser, TestMCCommonStatementsMill.globalScope());
    ast.accept(traverser);
    ast.setEnclosingScope(TestMCCommonStatementsMill.globalScope());

    // When
    checker.checkAll(ast);

    // Then
    assertTrue(Log.getFindings().isEmpty(), () -> Log.getFindings().toString());
  }

  @ParameterizedTest
  @MethodSource("exprAndErrorProvider")
  void testInvalid(String expr, String error) throws IOException {
    // Given
    TestMCCommonStatementsCoCoChecker checker = new TestMCCommonStatementsCoCoChecker();
    checker.addCoCo(new ForEachIsValid());

    ASTEnhancedForControl ast = TestMCCommonStatementsMill.parser()
      .parse_StringEnhancedForControl(expr)
      .orElseThrow();

    TestMCCommonStatementsTraverser traverser = TestMCCommonStatementsMill.traverser();
    addToTraverser(traverser, TestMCCommonStatementsMill.globalScope());
    ast.accept(traverser);
    ast.setEnclosingScope(TestMCCommonStatementsMill.globalScope());

    // When
    checker.checkAll(ast);

    // Then
    assertEquals(List.of(error), Log.getFindings()
      .stream().map(f -> f.getMsg().substring(0, 7)).collect(Collectors.toList())
    );
  }

  static Stream<Arguments> exprAndErrorProvider() {
    return Stream.of(
      arguments("Object o : 3", ForEachIsValid.ERROR_CODE),
      arguments("Object o : o", ForEachIsValid.ERROR_CODE),
      arguments("Object o : true + 1", "0xB0163")
    );
  }
}
