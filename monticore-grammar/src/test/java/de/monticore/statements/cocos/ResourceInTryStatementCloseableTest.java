/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.cocos;

import de.monticore.statements.mccommonstatements.cocos.ResourceInTryStatementCloseable;
import de.monticore.statements.mcexceptionstatements._ast.ASTMCExceptionStatementsNode;
import de.monticore.statements.mcexceptionstatements._ast.ASTTryLocalVariableDeclaration;
import de.monticore.statements.mcexceptionstatements._ast.ASTTryStatement3;
import de.monticore.statements.testmcexceptionstatements.TestMCExceptionStatementsMill;
import de.monticore.statements.testmcexceptionstatements._cocos.TestMCExceptionStatementsCoCoChecker;
import de.monticore.statements.testmcexceptionstatements._parser.TestMCExceptionStatementsParser;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.*;
import de.monticore.types3.util.DefsTypesForTests;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static de.monticore.types3.util.DefsTypesForTests._autoCloseableSymType;

public class ResourceInTryStatementCloseableTest {

  protected TestMCExceptionStatementsCoCoChecker checker;

  @BeforeEach
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);

    TestMCExceptionStatementsMill.reset();
    TestMCExceptionStatementsMill.init();
    BasicSymbolsMill.initializePrimitives();
    DefsTypesForTests.setup();

    checker = new TestMCExceptionStatementsCoCoChecker();
    checker.setTraverser(TestMCExceptionStatementsMill.traverser());
    checker.addCoCo(new ResourceInTryStatementCloseable(new TypeCalculator(null, new FullDeriveFromCombineExpressionsWithLiterals())));

    SymTypeOfObject sTypeA = SymTypeExpressionFactory.createTypeObject("A", TestMCExceptionStatementsMill.globalScope());

    TestMCExceptionStatementsMill.globalScope().add(
        TestMCExceptionStatementsMill
            .oOTypeSymbolBuilder()
            .setName("A")
            .addSuperTypes(_autoCloseableSymType)
            .build());

    TestMCExceptionStatementsMill.globalScope().add(
        TestMCExceptionStatementsMill
            .fieldSymbolBuilder()
            .setName("a")
            .setType(sTypeA)
            .build());

    SymTypeOfObject sTypeB = SymTypeExpressionFactory.createTypeObject("B", TestMCExceptionStatementsMill.globalScope());

    TestMCExceptionStatementsMill.globalScope().add(
        TestMCExceptionStatementsMill
            .oOTypeSymbolBuilder()
            .setName("B")
            .build());

    TestMCExceptionStatementsMill.globalScope().add(
        TestMCExceptionStatementsMill
            .fieldSymbolBuilder()
            .setName("b")
            .setType(sTypeB)
            .build());
  }

  public void checkValid(String expressionString) throws IOException {
    TestMCExceptionStatementsParser parser = TestMCExceptionStatementsMill.parser();

    Optional<ASTTryStatement3> optAST = parser.parse_StringTryStatement3(expressionString);
    Assertions.assertTrue(optAST.isPresent());
    ASTTryStatement3 ast = optAST.get();

    ast.setEnclosingScope(TestMCExceptionStatementsMill.globalScope());

    for (ASTTryLocalVariableDeclaration dec : ast.getTryLocalVariableDeclarationList()) {
      dec.getExpression().setEnclosingScope(TestMCExceptionStatementsMill.globalScope());

      Log.getFindings().clear();
      checker.checkAll((ASTMCExceptionStatementsNode) optAST.get());
      Assertions.assertTrue(Log.getFindings().isEmpty());
    }
  }

  public void checkInvalid(String expressionString) throws IOException {
    TestMCExceptionStatementsParser parser = TestMCExceptionStatementsMill.parser();

    Optional<ASTTryStatement3> optAST = parser.parse_StringTryStatement3(expressionString);
    Assertions.assertTrue(optAST.isPresent());
    ASTTryStatement3 ast = optAST.get();

    ast.setEnclosingScope(TestMCExceptionStatementsMill.globalScope());

    for (ASTTryLocalVariableDeclaration dec : ast.getTryLocalVariableDeclarationList()) {
      dec.getExpression().setEnclosingScope(TestMCExceptionStatementsMill.globalScope());

      Log.getFindings().clear();
      checker.checkAll((ASTMCExceptionStatementsNode) optAST.get());
      Assertions.assertFalse(Log.getFindings().isEmpty());
    }
  }

  @Test
  public void testValid() throws IOException {
    checkValid("try(A c = a){}");
  }

  @Test
  public void testInvalid() throws IOException {
    checkInvalid("try(B c = b){}");
  }

}
