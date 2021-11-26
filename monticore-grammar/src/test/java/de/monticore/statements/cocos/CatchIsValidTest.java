/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.cocos;

import de.monticore.grammar.cocos.CocoTest;
import de.monticore.statements.mccommonstatements.cocos.CatchIsValid;
import de.monticore.statements.mccommonstatements.cocos.ThrowIsValid;
import de.monticore.statements.mcexceptionstatements._ast.ASTCatchClause;
import de.monticore.statements.mcexceptionstatements._ast.ASTCatchTypeList;
import de.monticore.statements.mcexceptionstatements._ast.ASTThrowStatement;
import de.monticore.statements.mcexceptionstatements._cocos.MCExceptionStatementsCoCoChecker;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCBlockStatement;
import de.monticore.statements.testmccommonstatements._parser.TestMCCommonStatementsParser;
import de.monticore.statements.testmcexceptionstatements.TestMCExceptionStatementsMill;
import de.monticore.statements.testmcexceptionstatements._parser.TestMCExceptionStatementsParser;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.DeriveSymTypeOfCombineExpressionsDelegator;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfObject;
import de.monticore.types.check.TypeCheck;
import de.se_rwth.commons.logging.Log;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CatchIsValidTest extends CocoTest {
  
  private static final MCExceptionStatementsCoCoChecker checker = new MCExceptionStatementsCoCoChecker();
  
  @BeforeClass
  public static void disableFailQuick(){
    
    Log.init();
    Log.enableFailQuick(false);
    TestMCExceptionStatementsMill.reset();
    TestMCExceptionStatementsMill.init();
    BasicSymbolsMill.initializePrimitives();
    checker.setTraverser(TestMCExceptionStatementsMill.traverser());
    checker.addCoCo(new CatchIsValid(new TypeCheck(null, new DeriveSymTypeOfCombineExpressionsDelegator())));
    SymTypeOfObject sType = SymTypeExpressionFactory.createTypeObject("java.lang.Throwable", TestMCExceptionStatementsMill.globalScope());
    SymTypeOfObject sTypeA = SymTypeExpressionFactory.createTypeObject("A", TestMCExceptionStatementsMill.globalScope());
    TestMCExceptionStatementsMill.globalScope().add(TestMCExceptionStatementsMill.oOTypeSymbolBuilder().setName("A").addSuperTypes(sType).build());
    TestMCExceptionStatementsMill.globalScope().add(TestMCExceptionStatementsMill.oOTypeSymbolBuilder().setName("java.lang.Throwable").build());
    TestMCExceptionStatementsMill.globalScope().add(TestMCExceptionStatementsMill.fieldSymbolBuilder().setName("a").setType(sTypeA).build());
  }
  
  public void checkValid(String expressionString) throws IOException {
    
    TestMCExceptionStatementsParser parser = new TestMCExceptionStatementsParser();
    Optional<ASTCatchClause> optAST = parser.parse_StringCatchClause(expressionString);
    assertTrue(optAST.isPresent());
    ASTCatchClause ast = optAST.get();
    ast.setEnclosingScope(TestMCExceptionStatementsMill.globalScope());
    ast.getCatchTypeList().setEnclosingScope(TestMCExceptionStatementsMill.globalScope());
    ast.getCatchTypeList().forEachMCQualifiedNames(n -> n.setEnclosingScope(TestMCExceptionStatementsMill.globalScope()));
    Log.getFindings().clear();
    checker.checkAll(optAST.get());
    assertTrue(Log.getFindings().isEmpty());
    
  }
  
  @Test
  public void testValid() throws IOException {
    checkValid("catch(A a){}");
  }
  
}