/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.cocos;

import de.monticore.runtime.junit.MCAssertions;
import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.statements.mcvardeclarationstatements._cocos.VarDeclarationNameAlreadyDefinedInScope;
import de.monticore.statements.mcvardeclarationstatements._symboltable.MCVarDeclarationStatementsSTCompleteTypes;
import de.monticore.statements.testmcvardeclarationstatements.TestMCVarDeclarationStatementsMill;
import de.monticore.statements.testmcvardeclarationstatements._ast.ASTRootVarDeclaration;
import de.monticore.statements.testmcvardeclarationstatements._cocos.TestMCVarDeclarationStatementsCoCoChecker;
import de.monticore.statements.testmcvardeclarationstatements._visitor.TestMCVarDeclarationStatementsTraverser;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types3.util.CombineExpressionsWithLiteralsTypeTraverserFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static de.monticore.statements.testmcvardeclarationstatements.TestMCVarDeclarationStatementsMill.parser;

@TestWithMCLanguage(TestMCVarDeclarationStatementsMill.class)
class VarDeclarationNameAlreadyDefinedInScopeTest {

  @BeforeEach
  void init() {
    CombineExpressionsWithLiteralsTypeTraverserFactory.initTypeCheck3();
    BasicSymbolsMill.initializePrimitives();
  }

  protected ASTRootVarDeclaration parseAndBuildAST(String decl) throws IOException {
    ASTRootVarDeclaration ast = parser().parse_StringRootVarDeclaration(decl).orElseThrow();
    TestMCVarDeclarationStatementsMill.scopesGenitorDelegator().createFromAST(ast).setName("Artifact");
    TestMCVarDeclarationStatementsTraverser completerTraverser = TestMCVarDeclarationStatementsMill.traverser();
    completerTraverser.add4MCVarDeclarationStatements(new MCVarDeclarationStatementsSTCompleteTypes());
    ast.accept(completerTraverser);
    return ast;
  }

  @Test
  void testValidMultiVarDeclaration() throws IOException {
    // Given
    TestMCVarDeclarationStatementsCoCoChecker checker = new TestMCVarDeclarationStatementsCoCoChecker();
    checker.addCoCo(new VarDeclarationNameAlreadyDefinedInScope());

    ASTRootVarDeclaration astDecl = parseAndBuildAST("int a = 10, b, c = -12;");

    // When
    checker.checkAll(astDecl);
  }

  @Test
  void testInvalidMultiVarDeclarationWithoutValue() throws IOException {
    // Given
    TestMCVarDeclarationStatementsCoCoChecker checker = new TestMCVarDeclarationStatementsCoCoChecker();
    checker.addCoCo(new VarDeclarationNameAlreadyDefinedInScope());

    ASTRootVarDeclaration astDecl = parseAndBuildAST("int a = 10, a, a = -12;");
    astDecl.getEnclosingScope().setOrdered(true);

    // When
    checker.checkAll(astDecl);

    // Then
    MCAssertions.assertHasFindingStartingWith(VarDeclarationNameAlreadyDefinedInScope.ERROR_CODE);
    MCAssertions.assertHasFindingStartingWith(VarDeclarationNameAlreadyDefinedInScope.ERROR_CODE);
  }

  @Test
  void testInvalidUnorderedMultiVarDeclaration() throws IOException {
    // Given
    TestMCVarDeclarationStatementsCoCoChecker checker = new TestMCVarDeclarationStatementsCoCoChecker();
    checker.addCoCo(new VarDeclarationNameAlreadyDefinedInScope());

    ASTRootVarDeclaration astDecl = parseAndBuildAST("int a = 10, a, a = -12;");
    astDecl.getEnclosingScope().setOrdered(false);

    // When
    checker.checkAll(astDecl);

    // Then
    MCAssertions.assertHasFindingStartingWith(VarDeclarationNameAlreadyDefinedInScope.ERROR_CODE);
    MCAssertions.assertHasFindingStartingWith(VarDeclarationNameAlreadyDefinedInScope.ERROR_CODE);
    MCAssertions.assertHasFindingStartingWith(VarDeclarationNameAlreadyDefinedInScope.ERROR_CODE);
  }

  @Test
  void testInvalidVarDeclarationWithSymbolInScope() throws IOException {
    // Given
    TestMCVarDeclarationStatementsCoCoChecker checker = new TestMCVarDeclarationStatementsCoCoChecker();
    checker.addCoCo(new VarDeclarationNameAlreadyDefinedInScope());

    ASTRootVarDeclaration astDecl = parseAndBuildAST("int a = 10;");
    astDecl.getEnclosingScope().add(TestMCVarDeclarationStatementsMill.variableSymbolBuilder()
      .setName("a")
      .setEnclosingScope(astDecl.getEnclosingScope())
      .build());

    // When
    checker.checkAll(astDecl);

    // Then
    MCAssertions.assertHasFindingStartingWith(VarDeclarationNameAlreadyDefinedInScope.ERROR_CODE);
  }

  @Test
  void testInvalidVarDeclarationWithSymbolInSuperNonShadowingScope() throws IOException {
    // Given
    TestMCVarDeclarationStatementsCoCoChecker checker = new TestMCVarDeclarationStatementsCoCoChecker();
    checker.addCoCo(new VarDeclarationNameAlreadyDefinedInScope());

    ASTRootVarDeclaration astDecl = parseAndBuildAST("int a = 10;");
    astDecl.getEnclosingScope().setShadowing(false);
    TestMCVarDeclarationStatementsMill.globalScope().add(TestMCVarDeclarationStatementsMill.variableSymbolBuilder()
        .setName("a")
        .setEnclosingScope(astDecl.getEnclosingScope())
        .build());

    // When
    checker.checkAll(astDecl);

    // Then
    MCAssertions.assertHasFindingStartingWith(VarDeclarationNameAlreadyDefinedInScope.ERROR_CODE);
  }

  @Test
  void testValidVarDeclarationWithSymbolInSuperShadowingScope() throws IOException {
    // Given
    TestMCVarDeclarationStatementsCoCoChecker checker = new TestMCVarDeclarationStatementsCoCoChecker();
    checker.addCoCo(new VarDeclarationNameAlreadyDefinedInScope());

    ASTRootVarDeclaration astDecl = parseAndBuildAST("int a = 10;");
    astDecl.getEnclosingScope().setShadowing(true);
    TestMCVarDeclarationStatementsMill.globalScope().add(TestMCVarDeclarationStatementsMill.variableSymbolBuilder()
        .setName("a")
        .setEnclosingScope(astDecl.getEnclosingScope())
        .build());

    // When
    checker.checkAll(astDecl);
  }
}
