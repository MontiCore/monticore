/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.cocos;

import de.monticore.statements.mccommonstatements.cocos.ResourceInTryStatementCloseable;
import de.monticore.statements.mcexceptionstatements._ast.ASTMCExceptionStatementsNode;
import de.monticore.statements.mcexceptionstatements._ast.ASTTryLocalVariableDeclaration;
import de.monticore.statements.mcexceptionstatements._ast.ASTTryStatement3;
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
import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.statements.testmcexceptionstatements.TestMCExceptionStatementsMill.parser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResourceInTryStatementCloseableTest {

  @BeforeEach
  void init() {
    LogStub.init();
    Log.enableFailQuick(false);

    TestMCExceptionStatementsMill.reset();
    TestMCExceptionStatementsMill.init();
    CombineExpressionsWithLiteralsTypeTraverserFactory.initTypeCheck3();
    BasicSymbolsMill.initializePrimitives();

    // Setup type hierarchy
    SymTypeOfObject closeableType =
      SymTypeExpressionFactory.createTypeObjectViaSurrogate("java.io.Closeable", TestMCExceptionStatementsMill.globalScope());
    SymTypeOfObject typeA =
      SymTypeExpressionFactory.createTypeObjectViaSurrogate("A", TestMCExceptionStatementsMill.globalScope());
    SymTypeOfObject typeB =
      SymTypeExpressionFactory.createTypeObjectViaSurrogate("B", TestMCExceptionStatementsMill.globalScope());

    // Define A extending Closeable
    TestMCExceptionStatementsMill.globalScope().add(
      TestMCExceptionStatementsMill.oOTypeSymbolBuilder()
        .setName("A")
        .setSpannedScope(TestMCCommonStatementsMill.globalScope())
        .addSuperTypes(closeableType)
        .build()
    );

    // Define B not extending Closeable
    TestMCExceptionStatementsMill.globalScope().add(
      TestMCExceptionStatementsMill.oOTypeSymbolBuilder()
        .setName("B")
        .setSpannedScope(TestMCCommonStatementsMill.globalScope())
        .build()
    );

    // Setup java.io hierarchy
    ITestMCCommonStatementsScope javaScope = TestMCCommonStatementsMill.scope();
    javaScope.setName("java");
    ITestMCCommonStatementsScope ioScope = TestMCCommonStatementsMill.scope();
    ioScope.setName("io");
    javaScope.addSubScope(ioScope);
    TestMCCommonStatementsMill.globalScope().addSubScope(javaScope);

    ioScope.add(
      TestMCExceptionStatementsMill.oOTypeSymbolBuilder()
        .setName("Closeable")
        .setSpannedScope(TestMCCommonStatementsMill.globalScope())
        .build()
    );

    // Add fields
    TestMCExceptionStatementsMill.globalScope().add(
      TestMCExceptionStatementsMill.fieldSymbolBuilder()
        .setName("a")
        .setType(typeA)
        .build()
    );
    TestMCExceptionStatementsMill.globalScope().add(
      TestMCExceptionStatementsMill.fieldSymbolBuilder()
        .setName("b")
        .setType(typeB)
        .build()
    );
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "try(A c = a){}"
  })
  void testValid(String expr) throws IOException {
    // Given
    TestMCExceptionStatementsCoCoChecker checker = new TestMCExceptionStatementsCoCoChecker();
    checker.setTraverser(TestMCExceptionStatementsMill.traverser());
    checker.addCoCo(new ResourceInTryStatementCloseable());

    ASTTryStatement3 ast = parser().parse_StringTryStatement3(expr).orElseThrow();
    ast.setEnclosingScope(TestMCExceptionStatementsMill.globalScope());

    for (ASTTryLocalVariableDeclaration dec : ast.getTryLocalVariableDeclarationList()) {
      dec.getExpression().setEnclosingScope(TestMCExceptionStatementsMill.globalScope());
    }

    Log.getFindings().clear();

    // When
    checker.checkAll((ASTMCExceptionStatementsNode) ast);

    // Then
    assertTrue(Log.getFindings().isEmpty(), () -> Log.getFindings().toString());
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "try(B c = b){}"
  })
  void testInvalid(String expr) throws IOException {
    // Given
    TestMCExceptionStatementsCoCoChecker checker = new TestMCExceptionStatementsCoCoChecker();
    checker.setTraverser(TestMCExceptionStatementsMill.traverser());
    checker.addCoCo(new ResourceInTryStatementCloseable());

    ASTTryStatement3 ast = parser().parse_StringTryStatement3(expr).orElseThrow();
    ast.setEnclosingScope(TestMCExceptionStatementsMill.globalScope());

    for (ASTTryLocalVariableDeclaration dec : ast.getTryLocalVariableDeclarationList()) {
      dec.getExpression().setEnclosingScope(TestMCExceptionStatementsMill.globalScope());
    }

    Log.getFindings().clear();

    // When
    checker.checkAll((ASTMCExceptionStatementsNode) ast);

    // Then
    assertEquals(List.of(ResourceInTryStatementCloseable.ERROR_CODE), Log.getFindings()
      .stream().map(f -> f.getMsg().substring(0, 7)).collect(Collectors.toList())
    );
  }
}
