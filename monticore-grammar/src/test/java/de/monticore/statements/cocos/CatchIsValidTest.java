/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.cocos;

import de.monticore.statements.mccommonstatements.cocos.CatchIsValid;
import de.monticore.statements.mcexceptionstatements._ast.ASTCatchClause;
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

import static de.monticore.statements.testmcexceptionstatements.TestMCExceptionStatementsMill.*;
import static de.monticore.statements.testmcfulljavastatements.TestMCFullJavaStatementsMill.parser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CatchIsValidTest {

  @BeforeEach
  void init() {
    LogStub.init();
    Log.enableFailQuick(false);

    TestMCExceptionStatementsMill.reset();
    TestMCExceptionStatementsMill.init();
    CombineExpressionsWithLiteralsTypeTraverserFactory.initTypeCheck3();
    BasicSymbolsMill.initializePrimitives();

    SymTypeOfObject sType = SymTypeExpressionFactory.createTypeObjectViaSurrogate("java.lang.Throwable", globalScope());
    SymTypeOfObject sTypeA = SymTypeExpressionFactory.createTypeObjectViaSurrogate("A", globalScope());

    globalScope().add(oOTypeSymbolBuilder().setName("A")
      .setSpannedScope(globalScope()).addSuperTypes(sType).build()
    );

    ITestMCCommonStatementsScope javaScope = TestMCCommonStatementsMill.scope();
    javaScope.setName("java");

    ITestMCCommonStatementsScope langScope = TestMCCommonStatementsMill.scope();
    langScope.setName("lang");

    javaScope.addSubScope(langScope);
    TestMCCommonStatementsMill.globalScope().addSubScope(javaScope);

    langScope.add(oOTypeSymbolBuilder().setName("Throwable")
      .setSpannedScope(globalScope()).build()
    );

    globalScope().add(fieldSymbolBuilder().setName("a")
      .setType(sTypeA).build()
    );

    SymTypeOfObject symType = SymTypeExpressionFactory.createTypeObjectViaSurrogate("java.lang.Object", globalScope());
    SymTypeOfObject symTypeB = SymTypeExpressionFactory.createTypeObjectViaSurrogate("B", globalScope());

    globalScope().add(oOTypeSymbolBuilder().setName("B")
      .setSpannedScope(globalScope()).addSuperTypes(symType).build()
    );

    langScope.add(oOTypeSymbolBuilder().setName("Object")
      .setSpannedScope(globalScope()).build()
    );
    globalScope().add(fieldSymbolBuilder().setName("b")
      .setType(symTypeB).build()
    );
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "catch(A a) {}"
  })
  void testValid(String expr) throws IOException {
    // Given
    TestMCExceptionStatementsCoCoChecker checker = new TestMCExceptionStatementsCoCoChecker();
    checker.addCoCo(new CatchIsValid());

    ASTCatchClause ast = parser().parse_StringCatchClause(expr).orElseThrow();

    ast.setEnclosingScope(globalScope());
    ast.getCatchTypeList().setEnclosingScope(globalScope());
    ast.getCatchTypeList().forEachMCQualifiedNames(n -> n.setEnclosingScope(globalScope()));

    // When
    checker.checkAll(ast);

    // Then
    assertTrue(Log.getFindings().isEmpty(), () -> Log.getFindings().toString());
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "catch (B b) {}"
  })
  void testInvalid(String expr) throws IOException {
    // Given
    TestMCExceptionStatementsCoCoChecker checker = new TestMCExceptionStatementsCoCoChecker();
    checker.addCoCo(new CatchIsValid());

    ASTCatchClause ast = parser().parse_StringCatchClause(expr).orElseThrow();

    ast.setEnclosingScope(globalScope());
    ast.getCatchTypeList().setEnclosingScope(globalScope());
    ast.getCatchTypeList().forEachMCQualifiedNames(n -> n.setEnclosingScope(globalScope()));

    // When
    checker.checkAll(ast);

    // Then
    assertEquals(List.of(CatchIsValid.ERROR_CODE), Log.getFindings()
      .stream().map(f -> f.getMsg().substring(0, 7)).collect(Collectors.toList())
    );
  }
}
