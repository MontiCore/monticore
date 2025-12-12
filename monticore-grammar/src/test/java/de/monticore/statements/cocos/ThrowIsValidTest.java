/* (c) [https://github.com/MontiCore/monticore](https://github.com/MontiCore/monticore) */
package de.monticore.statements.cocos;

import de.monticore.statements.mccommonstatements.cocos.ThrowIsValid;
import de.monticore.statements.mcexceptionstatements._ast.ASTMCExceptionStatementsNode;
import de.monticore.statements.mcexceptionstatements._ast.ASTThrowStatement;
import de.monticore.statements.testmccommonstatements.TestMCCommonStatementsMill;
import de.monticore.statements.testmccommonstatements._symboltable.ITestMCCommonStatementsScope;
import de.monticore.statements.testmcexceptionstatements.TestMCExceptionStatementsMill;
import de.monticore.statements.testmcexceptionstatements._cocos.TestMCExceptionStatementsCoCoChecker;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
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

import static de.monticore.statements.testmcexceptionstatements.TestMCExceptionStatementsMill.parser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class ThrowIsValidTest {

  @BeforeEach
  void init() {
    LogStub.init();
    Log.enableFailQuick(false);

    TestMCExceptionStatementsMill.reset();
    TestMCExceptionStatementsMill.init();
    CombineExpressionsWithLiteralsTypeTraverserFactory.initTypeCheck3();
    BasicSymbolsMill.initializePrimitives();

    // Define basic Throwable and hierarchy
    SymTypeOfObject throwableType =
      SymTypeExpressionFactory.createTypeObjectViaSurrogate("java.lang.Throwable", TestMCExceptionStatementsMill.globalScope());
    SymTypeOfObject aType =
      SymTypeExpressionFactory.createTypeObjectViaSurrogate("A", TestMCExceptionStatementsMill.globalScope());
    SymTypeOfObject bType =
      SymTypeExpressionFactory.createTypeObjectViaSurrogate("B", TestMCExceptionStatementsMill.globalScope());

    // A extends Throwable
    TestMCExceptionStatementsMill.globalScope().add(
      TestMCExceptionStatementsMill.oOTypeSymbolBuilder()
        .setName("A")
        .setSpannedScope(TestMCCommonStatementsMill.globalScope())
        .addSuperTypes(throwableType)
        .build()
    );

    // B does not extend Throwable
    TestMCExceptionStatementsMill.globalScope().add(
      TestMCExceptionStatementsMill.oOTypeSymbolBuilder()
        .setName("B")
        .setSpannedScope(TestMCCommonStatementsMill.globalScope())
        .build()
    );

    ITestMCCommonStatementsScope javaScope = TestMCCommonStatementsMill.scope();
    javaScope.setName("java");
    ITestMCCommonStatementsScope langScope = TestMCCommonStatementsMill.scope();
    langScope.setName("lang");

    TestMCCommonStatementsMill.globalScope().addSubScope(javaScope);
    javaScope.addSubScope(langScope);

    langScope.add(TestMCExceptionStatementsMill.oOTypeSymbolBuilder()
      .setName("Throwable")
      .setSpannedScope(TestMCCommonStatementsMill.globalScope())
      .build());

    // Add variables a and b
    TestMCExceptionStatementsMill.globalScope().add(
      TestMCExceptionStatementsMill.fieldSymbolBuilder()
        .setName("a")
        .setType(aType)
        .build()
    );

    TestMCExceptionStatementsMill.globalScope().add(
      TestMCExceptionStatementsMill.fieldSymbolBuilder()
        .setName("b")
        .setType(bType)
        .build()
    );
  }

  @ParameterizedTest
  @ValueSource(strings = {"throw a;"})
  void testValid(String expr) throws IOException {
    // Given
    TestMCExceptionStatementsCoCoChecker checker = new TestMCExceptionStatementsCoCoChecker();
    checker.setTraverser(TestMCExceptionStatementsMill.traverser());
    checker.addCoCo(new ThrowIsValid());

    ASTThrowStatement ast = parser().parse_StringThrowStatement(expr).orElseThrow();
    ast.setEnclosingScope(TestMCExceptionStatementsMill.globalScope());
    ast.getExpression().setEnclosingScope(TestMCExceptionStatementsMill.globalScope());

    // When
    checker.checkAll((ASTMCExceptionStatementsNode) ast);

    // Then
    assertTrue(Log.getFindings().isEmpty(), () -> Log.getFindings().toString());
  }

  @ParameterizedTest
  @MethodSource("exprAndErrorProvider")
  void testInvalid(String expr, String error) throws IOException {
    // Given
    TestMCExceptionStatementsCoCoChecker checker = new TestMCExceptionStatementsCoCoChecker();
    checker.setTraverser(TestMCExceptionStatementsMill.traverser());
    checker.addCoCo(new ThrowIsValid());

    ASTThrowStatement ast = parser().parse_StringThrowStatement(expr).orElseThrow();
    ast.setEnclosingScope(TestMCExceptionStatementsMill.globalScope());
    ast.getExpression().setEnclosingScope(TestMCExceptionStatementsMill.globalScope());

    // When
    checker.checkAll((ASTMCExceptionStatementsNode) ast);

    // Then
    assertEquals(List.of(error), Log.getFindings()
      .stream()
      .map(f -> f.getMsg().substring(0, 7))
      .collect(Collectors.toList()));
  }

  static Stream<Arguments> exprAndErrorProvider() {
    return Stream.of(
      arguments("throw b;", ThrowIsValid.ERROR_CODE)
    );
  }
}
