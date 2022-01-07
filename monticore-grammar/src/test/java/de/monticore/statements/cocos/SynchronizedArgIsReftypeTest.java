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
import de.monticore.types.check.DeriveSymTypeOfCombineExpressionsDelegator;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfObject;
import de.monticore.types.check.TypeCheck;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SynchronizedArgIsReftypeTest {

  private static final TestMCSynchronizedStatementsCoCoChecker checker = new TestMCSynchronizedStatementsCoCoChecker();

  @BeforeClass
  public static void disableFailQuick() {

    LogStub.init();
    Log.enableFailQuick(false);
    TestMCSynchronizedStatementsMill.reset();
    TestMCSynchronizedStatementsMill.init();
    BasicSymbolsMill.initializePrimitives();
    checker.addCoCo(new SynchronizedArgIsReftype(new TypeCheck(null, new DeriveSymTypeOfCombineExpressionsDelegator())));

    SymTypeOfObject sType = SymTypeExpressionFactory.createTypeObject("java.lang.Object", TestMCExceptionStatementsMill.globalScope());
    TestMCExceptionStatementsMill.globalScope().add(TestMCExceptionStatementsMill.oOTypeSymbolBuilder().setName("java.lang.Object").build());
    TestMCExceptionStatementsMill.globalScope().add(TestMCExceptionStatementsMill.fieldSymbolBuilder().setName("a1").setType(sType).build());

  }

  public void checkValid(String expressionString) throws IOException {

    TestMCSynchronizedStatementsParser parser = new TestMCSynchronizedStatementsParser();
    Optional<ASTSynchronizedStatement> optAST = parser.parse_StringSynchronizedStatement(expressionString);
    assertTrue(optAST.isPresent());
    ASTSynchronizedStatement ast = optAST.get();
    ast.getExpression().setEnclosingScope(TestMCExceptionStatementsMill.globalScope());
    Log.getFindings().clear();
    checker.checkAll((ASTMCSynchronizedStatementsNode) optAST.get());
    assertTrue(Log.getFindings().isEmpty());

  }

  public void checkInvalid(String expressionString) throws IOException {

    TestMCSynchronizedStatementsParser parser = new TestMCSynchronizedStatementsParser();
    Optional<ASTSynchronizedStatement> optAST = parser.parse_StringSynchronizedStatement(expressionString);
    assertTrue(optAST.isPresent());
    ASTSynchronizedStatement ast = optAST.get();
    ast.getExpression().setEnclosingScope(TestMCExceptionStatementsMill.globalScope());
    Log.getFindings().clear();
    checker.checkAll((ASTMCSynchronizedStatementsNode) optAST.get());
    assertFalse(Log.getFindings().isEmpty());

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