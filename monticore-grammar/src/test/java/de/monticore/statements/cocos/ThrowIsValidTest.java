/* (c) https://github.com/MontiCore/monticore */
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
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;

import static de.monticore.statements.testmcexceptionstatements.TestMCExceptionStatementsMill.parser;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
  @ValueSource(strings = {"throw b;"})
  void testInvalid(String expr) throws IOException {
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
    assertFalse(Log.getFindings().isEmpty(), () -> Log.getFindings().toString());
  }
}
