/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.cocos;

import de.monticore.statements.mcvardeclarationstatements._cocos.VarDeclarationNameAlreadyDefinedInScope;
import de.monticore.statements.mcvardeclarationstatements._symboltable.MCVarDeclarationStatementsSTCompleteTypes;
import de.monticore.statements.testmcvardeclarationstatements.TestMCVarDeclarationStatementsMill;
import de.monticore.statements.testmcvardeclarationstatements._ast.ASTRootVarDeclaration;
import de.monticore.statements.testmcvardeclarationstatements._cocos.TestMCVarDeclarationStatementsCoCoChecker;
import de.monticore.statements.testmcvardeclarationstatements._visitor.TestMCVarDeclarationStatementsTraverser;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types3.util.CombineExpressionsWithLiteralsTypeTraverserFactory;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static de.monticore.runtime.junit.MCAssertions.assertHasFindingStartingWith;
import static de.monticore.runtime.junit.MCAssertions.assertNoFindings;
import static de.monticore.statements.testmcvardeclarationstatements.TestMCVarDeclarationStatementsMill.parser;

class VarDeclarationNameAlreadyDefinedInScopeTest {

  @BeforeEach
  void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    TestMCVarDeclarationStatementsMill.reset();
    TestMCVarDeclarationStatementsMill.init();
    CombineExpressionsWithLiteralsTypeTraverserFactory.initTypeCheck3();
    BasicSymbolsMill.initializePrimitives();
  }

  protected ASTRootVarDeclaration parseAndBuildAST(String decl) throws IOException {
    ASTRootVarDeclaration ast = parser().parse_StringRootVarDeclaration(decl).orElseThrow();
    TestMCVarDeclarationStatementsMill.scopesGenitorDelegator().createFromAST(ast);
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

    // Then
    assertNoFindings();
  }

  @Test
  void testInvalidMultiVarDeclarationWithoutValue() throws IOException {
    // Given
    TestMCVarDeclarationStatementsCoCoChecker checker = new TestMCVarDeclarationStatementsCoCoChecker();
    checker.addCoCo(new VarDeclarationNameAlreadyDefinedInScope());

    ASTRootVarDeclaration astDecl = parseAndBuildAST("int a, a;");

    // When
    checker.checkAll(astDecl);

    // Then
    assertHasFindingStartingWith(VarDeclarationNameAlreadyDefinedInScope.ERROR_CODE);
    assertHasFindingStartingWith(VarDeclarationNameAlreadyDefinedInScope.ERROR_CODE);
    assertNoFindings();
  }

  @Test
  void testInvalidMultiVarDeclarationWithValue() throws IOException {
    // Given
    TestMCVarDeclarationStatementsCoCoChecker checker = new TestMCVarDeclarationStatementsCoCoChecker();
    checker.addCoCo(new VarDeclarationNameAlreadyDefinedInScope());

    ASTRootVarDeclaration astDecl = parseAndBuildAST("int a = 10, a, a = -12;");

    // When
    checker.checkAll(astDecl);

    // Then
    assertHasFindingStartingWith(VarDeclarationNameAlreadyDefinedInScope.ERROR_CODE);
    assertHasFindingStartingWith(VarDeclarationNameAlreadyDefinedInScope.ERROR_CODE);
    assertHasFindingStartingWith(VarDeclarationNameAlreadyDefinedInScope.ERROR_CODE);
    assertNoFindings();
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
    assertHasFindingStartingWith(VarDeclarationNameAlreadyDefinedInScope.ERROR_CODE);
    assertNoFindings();
  }
}
