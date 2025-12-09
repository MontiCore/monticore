/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.cocos;

import de.monticore.runtime.junit.MCAssertions;
import de.monticore.statements.mcvardeclarationstatements._cocos.VarDeclarationNameAlreadyDefinedInScope;
import de.monticore.statements.mcvardeclarationstatements._symboltable.MCVarDeclarationStatementsSTCompleteTypes;
import de.monticore.statements.testmcvardeclarationstatements.TestMCVarDeclarationStatementsMill;
import de.monticore.statements.testmcvardeclarationstatements._ast.ASTRootVarDeclaration;
import de.monticore.statements.testmcvardeclarationstatements._cocos.TestMCVarDeclarationStatementsCoCoChecker;
import de.monticore.statements.testmcvardeclarationstatements._parser.TestMCVarDeclarationStatementsParser;
import de.monticore.statements.testmcvardeclarationstatements._visitor.TestMCVarDeclarationStatementsTraverser;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types3.util.CombineExpressionsWithLiteralsTypeTraverserFactory;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class VarDeclarationNameAlreadyDefinedInScopeTest {

  protected TestMCVarDeclarationStatementsCoCoChecker checker;
  protected TestMCVarDeclarationStatementsParser parser;

  @BeforeEach
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    TestMCVarDeclarationStatementsMill.reset();
    TestMCVarDeclarationStatementsMill.init();

    TestMCVarDeclarationStatementsMill.globalScope().clear();
    CombineExpressionsWithLiteralsTypeTraverserFactory.initTypeCheck3();
    BasicSymbolsMill.initializePrimitives();

    checker = new TestMCVarDeclarationStatementsCoCoChecker();
    checker.setTraverser(TestMCVarDeclarationStatementsMill.traverser());
    checker.addCoCo(new VarDeclarationNameAlreadyDefinedInScope());
    parser = TestMCVarDeclarationStatementsMill.parser();
  }

  protected ASTRootVarDeclaration parseAndBuildAST(String decl) throws IOException {
    ASTRootVarDeclaration ast = parser.parse_StringRootVarDeclaration(decl).get();
    TestMCVarDeclarationStatementsMill.scopesGenitorDelegator().createFromAST(ast);
    TestMCVarDeclarationStatementsTraverser completerTraverser = TestMCVarDeclarationStatementsMill.traverser();
    completerTraverser.add4MCVarDeclarationStatements(new MCVarDeclarationStatementsSTCompleteTypes());
    ast.accept(completerTraverser);
    // We must manually set a name for the ArtifactScope. Else we get an exception.
    ast.getEnclosingScope().setName("Foo");
    return ast;
  }

  @Test
  public void testValidMultiVarDeclaration() throws IOException {
    // Given
    ASTRootVarDeclaration astDecl = parseAndBuildAST("int a = 10, b, c = -12;");

    // When
    checker.checkAll(astDecl);

    // Then
    MCAssertions.assertNoFindings();
  }

  @Test
  public void testInvalidMultiVarDeclarationWithoutValue() throws IOException {
    // Given
    ASTRootVarDeclaration astDecl = parseAndBuildAST("int a, a;");

    // When
    checker.checkAll(astDecl);

    // Then
    MCAssertions.assertHasFindingStartingWith(VarDeclarationNameAlreadyDefinedInScope.ERROR_CODE);
    MCAssertions.assertHasFindingStartingWith(VarDeclarationNameAlreadyDefinedInScope.ERROR_CODE);
    MCAssertions.assertNoFindings();
  }

  @Test
  public void testInvalidMultiVarDeclarationWithValue() throws IOException {
    // Given
    ASTRootVarDeclaration astDecl = parseAndBuildAST("int a = 10, a, a = -12;");

    // When
    checker.checkAll(astDecl);

    // Then
    MCAssertions.assertHasFindingStartingWith(VarDeclarationNameAlreadyDefinedInScope.ERROR_CODE);
    MCAssertions.assertHasFindingStartingWith(VarDeclarationNameAlreadyDefinedInScope.ERROR_CODE);
    MCAssertions.assertHasFindingStartingWith(VarDeclarationNameAlreadyDefinedInScope.ERROR_CODE);
    MCAssertions.assertNoFindings();
  }

  @Test
  public void testInvalidVarDeclarationWithSymbolInScope() throws IOException {
    // Given
    ASTRootVarDeclaration astDecl = parseAndBuildAST("int a = 10;");
    astDecl.getEnclosingScope().add(TestMCVarDeclarationStatementsMill.variableSymbolBuilder()
        .setName("a")
        .setEnclosingScope(astDecl.getEnclosingScope())
        .build());

    // When
    checker.checkAll(astDecl);

    // Then
    MCAssertions.assertHasFindingStartingWith(VarDeclarationNameAlreadyDefinedInScope.ERROR_CODE);
    MCAssertions.assertNoFindings();
  }
}
