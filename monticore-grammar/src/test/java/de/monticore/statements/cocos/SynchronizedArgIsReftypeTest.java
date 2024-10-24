/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.cocos;

import de.monticore.statements.mccommonstatements.cocos.SynchronizedArgIsReftype;
import de.monticore.statements.mcsynchronizedstatements._ast.ASTMCSynchronizedStatementsNode;
import de.monticore.statements.mcsynchronizedstatements._ast.ASTSynchronizedStatement;
import de.monticore.statements.testmcexceptionstatements.TestMCExceptionStatementsMill;
import de.monticore.statements.testmcsynchronizedstatements.TestMCSynchronizedStatementsMill;
import de.monticore.statements.testmcsynchronizedstatements._cocos.TestMCSynchronizedStatementsCoCoChecker;
import de.monticore.statements.testmcsynchronizedstatements._parser.TestMCSynchronizedStatementsParser;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.*;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SynchronizedArgIsReftypeTest {

  protected TestMCSynchronizedStatementsCoCoChecker checker;
  
  @BeforeEach
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    TestMCSynchronizedStatementsMill.reset();
    TestMCSynchronizedStatementsMill.init();
    BasicSymbolsMill.initializePrimitives();
    checker = new TestMCSynchronizedStatementsCoCoChecker();
    checker.addCoCo(new SynchronizedArgIsReftype(new TypeCalculator(null, new FullDeriveFromCombineExpressionsWithLiterals())));

    SymTypeOfObject sType = SymTypeExpressionFactory.createTypeObject("java.lang.Object", TestMCExceptionStatementsMill.globalScope());
    TestMCExceptionStatementsMill.globalScope().add(TestMCExceptionStatementsMill.oOTypeSymbolBuilder().setName("java.lang.Object").build());
    TestMCExceptionStatementsMill.globalScope().add(TestMCExceptionStatementsMill.fieldSymbolBuilder().setName("a1").setType(sType).build());
  }

  public void checkValid(String expressionString) throws IOException {

    TestMCSynchronizedStatementsParser parser = new TestMCSynchronizedStatementsParser();
    Optional<ASTSynchronizedStatement> optAST = parser.parse_StringSynchronizedStatement(expressionString);
    Assertions.assertTrue(optAST.isPresent());
    ASTSynchronizedStatement ast = optAST.get();
    ast.getExpression().setEnclosingScope(TestMCExceptionStatementsMill.globalScope());
    Log.getFindings().clear();
    checker.checkAll((ASTMCSynchronizedStatementsNode) optAST.get());
    Assertions.assertTrue(Log.getFindings().isEmpty());

  }

  public void checkInvalid(String expressionString) throws IOException {

    TestMCSynchronizedStatementsParser parser = new TestMCSynchronizedStatementsParser();
    Optional<ASTSynchronizedStatement> optAST = parser.parse_StringSynchronizedStatement(expressionString);
    Assertions.assertTrue(optAST.isPresent());
    ASTSynchronizedStatement ast = optAST.get();
    ast.getExpression().setEnclosingScope(TestMCExceptionStatementsMill.globalScope());
    Log.getFindings().clear();
    checker.checkAll((ASTMCSynchronizedStatementsNode) optAST.get());
    Assertions.assertFalse(Log.getFindings().isEmpty());

  }

  @Test
  public void testValid() throws IOException {

    checkValid("synchronized(a1){}");

  }

  @Test
  public void testInvalid() throws IOException {

    checkInvalid("synchronized('f'){}");
    checkInvalid("synchronized(5.5){}");
    checkInvalid("synchronized(false){}");

  }

}