/* (c) [https://github.com/MontiCore/monticore](https://github.com/MontiCore/monticore) */
package de.monticore.statements.cocos;

import de.monticore.runtime.junit.MCAssertions;
import de.monticore.runtime.junit.TestWithMCLanguage;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.monticore.statements.testmcexceptionstatements.TestMCExceptionStatementsMill.*;
import static de.monticore.statements.testmcfulljavastatements.TestMCFullJavaStatementsMill.parser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@TestWithMCLanguage(TestMCExceptionStatementsMill.class)
class CatchIsValidTest {

  @BeforeEach
  void init() {
    CombineExpressionsWithLiteralsTypeTraverserFactory.initTypeCheck3();
    BasicSymbolsMill.initializePrimitives();

    // Define Throwable hierarchy (java.lang.Throwable)
    SymTypeOfObject throwableType = SymTypeExpressionFactory.createTypeObjectViaSurrogate("java.lang.Throwable", globalScope());
    SymTypeOfObject aType = SymTypeExpressionFactory.createTypeObjectViaSurrogate("A", globalScope());

    globalScope().add(
      oOTypeSymbolBuilder()
        .setName("A")
        .setSpannedScope(globalScope())
        .addSuperTypes(throwableType)
        .build()
    );

    // Setup java.lang structure
    ITestMCCommonStatementsScope javaScope = TestMCCommonStatementsMill.scope();
    javaScope.setName("java");

    ITestMCCommonStatementsScope langScope = TestMCCommonStatementsMill.scope();
    langScope.setName("lang");

    javaScope.addSubScope(langScope);
    TestMCCommonStatementsMill.globalScope().addSubScope(javaScope);

    langScope.add(
      oOTypeSymbolBuilder()
        .setName("Throwable")
        .setSpannedScope(globalScope())
        .build()
    );

    // Field of type A
    globalScope().add(
      fieldSymbolBuilder()
        .setName("a")
        .setType(aType)
        .build()
    );

    // Create Object hierarchy for invalid type
    SymTypeOfObject objectType = SymTypeExpressionFactory.createTypeObjectViaSurrogate("java.lang.Object", globalScope());
    SymTypeOfObject bType = SymTypeExpressionFactory.createTypeObjectViaSurrogate("B", globalScope());

    globalScope().add(
      oOTypeSymbolBuilder()
        .setName("B")
        .setSpannedScope(globalScope())
        .addSuperTypes(objectType)
        .build()
    );

    langScope.add(
      oOTypeSymbolBuilder()
        .setName("Object")
        .setSpannedScope(globalScope())
        .build()
    );

    globalScope().add(
      fieldSymbolBuilder()
        .setName("b")
        .setType(bType)
        .build()
    );
  }

  @ParameterizedTest
  @ValueSource(strings = { "catch(A a) {}" })
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
  }

  @ParameterizedTest
  @MethodSource("exprAndErrorProvider")
  void testInvalid(String expr, String error) throws IOException {
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
    Log.getFindings().remove(
        MCAssertions.assertHasFindingStartingWith(error));
  }

  static Stream<Arguments> exprAndErrorProvider() {
    return Stream.of(
      arguments("catch(B b) {}", CatchIsValid.ERROR_CODE)
    );
  }
}
