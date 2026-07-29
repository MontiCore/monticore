/* (c) [https://github.com/MontiCore/monticore](https://github.com/MontiCore/monticore) */
package de.monticore.statements.cocos;

import de.monticore.runtime.junit.MCAssertions;
import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.statements.mccommonstatements.cocos.SwitchCaseTypesValid;
import de.monticore.statements.mcvardeclarationstatements._cocos.VarDeclarationInitializationHasCorrectType;
import de.monticore.statements.mcvardeclarationstatements._symboltable.MCVarDeclarationStatementsSTCompleteTypes;
import de.monticore.statements.testmcvardeclarationstatements.TestMCVarDeclarationStatementsMill;
import de.monticore.statements.testmcvardeclarationstatements._ast.ASTRootVarDeclaration;
import de.monticore.statements.testmcvardeclarationstatements._cocos.TestMCVarDeclarationStatementsCoCoChecker;
import de.monticore.statements.testmcvardeclarationstatements._visitor.TestMCVarDeclarationStatementsTraverser;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types3.util.CombineExpressionsWithLiteralsTypeTraverserFactory;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.monticore.statements.testmcvardeclarationstatements.TestMCVarDeclarationStatementsMill.parser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@TestWithMCLanguage(TestMCVarDeclarationStatementsMill.class)
class VarDeclarationInitializationHasCorrectTypeTest {

  @BeforeEach
  void init() {
    CombineExpressionsWithLiteralsTypeTraverserFactory.initTypeCheck3();
    BasicSymbolsMill.initializePrimitives();
    addMyTypeToGlobalScope();
    addStringToGlobalScope();
  }

  protected static void addMyTypeToGlobalScope() {
    OOTypeSymbol type = TestMCVarDeclarationStatementsMill.oOTypeSymbolBuilder()
      .setName("MyType")
      .setSpannedScope(TestMCVarDeclarationStatementsMill.scope())
      .build();
    TestMCVarDeclarationStatementsMill.globalScope().add(type);
    TestMCVarDeclarationStatementsMill.globalScope().addSubScope(type.getSpannedScope());
  }

  protected static void addStringToGlobalScope() {
    OOTypeSymbol type = TestMCVarDeclarationStatementsMill.oOTypeSymbolBuilder()
      .setName("String")
      .setSpannedScope(TestMCVarDeclarationStatementsMill.scope())
      .build();
    TestMCVarDeclarationStatementsMill.globalScope().add(type);
    TestMCVarDeclarationStatementsMill.globalScope().addSubScope(type.getSpannedScope());
  }

  protected ASTRootVarDeclaration parseAndBuildAST(String decl) throws IOException {
    ASTRootVarDeclaration ast = parser().parse_StringRootVarDeclaration(decl).orElseThrow();
    TestMCVarDeclarationStatementsMill.scopesGenitorDelegator().createFromAST(ast);
    TestMCVarDeclarationStatementsTraverser completerTraverser = TestMCVarDeclarationStatementsMill.traverser();
    completerTraverser.add4MCVarDeclarationStatements(new MCVarDeclarationStatementsSTCompleteTypes());
    ast.accept(completerTraverser);
    ast.getEnclosingScope().setName("Foo");
    return ast;
  }

  @Test
  void testValidMultiVarDeclaration() throws IOException {
    // Given
    TestMCVarDeclarationStatementsCoCoChecker checker = new TestMCVarDeclarationStatementsCoCoChecker();
    checker.addCoCo(new VarDeclarationInitializationHasCorrectType());

    ASTRootVarDeclaration ast = parseAndBuildAST("int a = 10, b, c = -12;");

    // When
    checker.checkAll(ast);
  }

  @ParameterizedTest
  @MethodSource("invalidExpressionAndErrorProvider")
  void testInvalidDeclarations(String declaration, List<String> expectedErrors) throws IOException {
    // Given
    TestMCVarDeclarationStatementsCoCoChecker checker = new TestMCVarDeclarationStatementsCoCoChecker();
    checker.addCoCo(new VarDeclarationInitializationHasCorrectType());
    ASTRootVarDeclaration ast = parseAndBuildAST(declaration);

    // When
    checker.checkAll(ast);

    // Then
    for (String error : expectedErrors) {
      Log.getFindings().remove(
          MCAssertions.assertHasFindingStartingWith(error));
    }
  }

  static Stream<Arguments> invalidExpressionAndErrorProvider() {
    return Stream.of(
      arguments(
        "int a = \"oh no\", b = 10, c, d = \"no no no\";",
        List.of(
          VarDeclarationInitializationHasCorrectType.ERROR_CODE,
          VarDeclarationInitializationHasCorrectType.ERROR_CODE
        )
      ),
      arguments(
        "int a = 3, b, c = MyType, d = \"no no no\";",
        List.of("0xFD118", VarDeclarationInitializationHasCorrectType.ERROR_CODE)
      )
    );
  }
}
