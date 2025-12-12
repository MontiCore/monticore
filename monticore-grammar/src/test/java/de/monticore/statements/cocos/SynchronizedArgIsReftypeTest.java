/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.cocos;

import de.monticore.statements.mccommonstatements.cocos.SynchronizedArgIsReftype;
import de.monticore.statements.mcsynchronizedstatements._ast.ASTMCSynchronizedStatementsNode;
import de.monticore.statements.mcsynchronizedstatements._ast.ASTSynchronizedStatement;
import de.monticore.statements.testmcexceptionstatements.TestMCExceptionStatementsMill;
import de.monticore.statements.testmcsynchronizedstatements.TestMCSynchronizedStatementsMill;
import de.monticore.statements.testmcsynchronizedstatements._cocos.TestMCSynchronizedStatementsCoCoChecker;
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

import static de.monticore.statements.testmcsynchronizedstatements.TestMCSynchronizedStatementsMill.parser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SynchronizedArgIsReftypeTest {

  @BeforeEach
  void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    TestMCSynchronizedStatementsMill.reset();
    TestMCSynchronizedStatementsMill.init();
    CombineExpressionsWithLiteralsTypeTraverserFactory.initTypeCheck3();
    BasicSymbolsMill.initializePrimitives();

    SymTypeOfObject objectType =
      SymTypeExpressionFactory.createTypeObjectViaSurrogate("java.lang.Object", TestMCExceptionStatementsMill.globalScope());

    TestMCExceptionStatementsMill.globalScope().add(
      TestMCExceptionStatementsMill.oOTypeSymbolBuilder()
        .setName("java.lang.Object")
        .build()
    );

    TestMCExceptionStatementsMill.globalScope().add(
      TestMCExceptionStatementsMill.fieldSymbolBuilder()
        .setName("a1")
        .setType(objectType)
        .build()
    );
  }

  @ParameterizedTest
  @ValueSource(strings = {"synchronized(a1){}"})
  void testValid(String expr) throws IOException {
    // Given
    TestMCSynchronizedStatementsCoCoChecker checker = new TestMCSynchronizedStatementsCoCoChecker();
    checker.addCoCo(new SynchronizedArgIsReftype());

    ASTSynchronizedStatement ast = parser().parse_StringSynchronizedStatement(expr).orElseThrow();
    ast.getExpression().setEnclosingScope(TestMCExceptionStatementsMill.globalScope());

    // When
    checker.checkAll((ASTMCSynchronizedStatementsNode) ast);

    // Then
    assertTrue(Log.getFindings().isEmpty(), () -> Log.getFindings().toString());
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "synchronized('f'){}",
    "synchronized(5.5){}",
    "synchronized(false){}"
  })
  void testInvalid(String expr) throws IOException {
    // Given
    TestMCSynchronizedStatementsCoCoChecker checker = new TestMCSynchronizedStatementsCoCoChecker();
    checker.addCoCo(new SynchronizedArgIsReftype());

    ASTSynchronizedStatement ast = parser().parse_StringSynchronizedStatement(expr).orElseThrow();
    ast.getExpression().setEnclosingScope(TestMCExceptionStatementsMill.globalScope());

    // When
    checker.checkAll((ASTMCSynchronizedStatementsNode) ast);

    // Then
    assertEquals(List.of(SynchronizedArgIsReftype.ERROR_CODE), Log.getFindings()
      .stream().map(f -> f.getMsg().substring(0, 7)).collect(Collectors.toList())
    );
  }
}
