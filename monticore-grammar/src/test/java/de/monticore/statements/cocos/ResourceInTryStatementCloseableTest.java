/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.cocos;

import de.monticore.grammar.cocos.CocoTest;
import de.monticore.statements.mccommonstatements.cocos.ResourceInTryStatementCloseable;
import de.monticore.statements.mccommonstatements.cocos.ThrowIsValid;
import de.monticore.statements.mcexceptionstatements._ast.ASTMCExceptionStatementsNode;
import de.monticore.statements.mcexceptionstatements._ast.ASTThrowStatement;
import de.monticore.statements.mcexceptionstatements._ast.ASTTryLocalVariableDeclaration;
import de.monticore.statements.mcexceptionstatements._ast.ASTTryStatement3;
import de.monticore.statements.mcexceptionstatements._cocos.MCExceptionStatementsCoCoChecker;
import de.monticore.statements.testmcexceptionstatements.TestMCExceptionStatementsMill;
import de.monticore.statements.testmcexceptionstatements._parser.TestMCExceptionStatementsParser;
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

public class ResourceInTryStatementCloseableTest extends CocoTest {
  
  private static final MCExceptionStatementsCoCoChecker checker = new MCExceptionStatementsCoCoChecker();
  
  @BeforeClass
  public static void disableFailQuick(){
    
    LogStub.init();
    Log.enableFailQuick(false);
    TestMCExceptionStatementsMill.reset();
    TestMCExceptionStatementsMill.init();
    BasicSymbolsMill.initializePrimitives();
    checker.setTraverser(TestMCExceptionStatementsMill.traverser());
    checker.addCoCo(new ResourceInTryStatementCloseable(new TypeCheck(null, new DeriveSymTypeOfCombineExpressionsDelegator())));
    SymTypeOfObject sType = SymTypeExpressionFactory.createTypeObject("java.io.Closeable", TestMCExceptionStatementsMill.globalScope());
    SymTypeOfObject sTypeA = SymTypeExpressionFactory.createTypeObject("A", TestMCExceptionStatementsMill.globalScope());
    TestMCExceptionStatementsMill.globalScope().add(TestMCExceptionStatementsMill.oOTypeSymbolBuilder().setName("A").addSuperTypes(sType).build());
    TestMCExceptionStatementsMill.globalScope().add(TestMCExceptionStatementsMill.oOTypeSymbolBuilder().setName("java.io.Closeable").build());
    TestMCExceptionStatementsMill.globalScope().add(TestMCExceptionStatementsMill.fieldSymbolBuilder().setName("a").setType(sTypeA).build());
  
    SymTypeOfObject sTypeB = SymTypeExpressionFactory.createTypeObject("B", TestMCExceptionStatementsMill.globalScope());
    TestMCExceptionStatementsMill.globalScope().add(TestMCExceptionStatementsMill.oOTypeSymbolBuilder().setName("B").build());
    TestMCExceptionStatementsMill.globalScope().add(TestMCExceptionStatementsMill.fieldSymbolBuilder().setName("b").setType(sTypeB).build());
  
  }
  
  public void checkValid(String expressionString) throws IOException {
    
    TestMCExceptionStatementsParser parser = new TestMCExceptionStatementsParser();
    Optional<ASTTryStatement3> optAST = parser.parse_StringTryStatement3(expressionString);
    assertTrue(optAST.isPresent());
    ASTTryStatement3 ast = optAST.get();
    ast.setEnclosingScope(TestMCExceptionStatementsMill.globalScope());
    for (ASTTryLocalVariableDeclaration dec: ast.getTryLocalVariableDeclarationList()){
  
      dec.getExpression().setEnclosingScope(TestMCExceptionStatementsMill.globalScope());
      Log.getFindings().clear();
      checker.checkAll((ASTMCExceptionStatementsNode) optAST.get());
      assertTrue(Log.getFindings().isEmpty());
      
    }
  }
  
  public void checkInvalid(String expressionString) throws IOException {
    
    TestMCExceptionStatementsParser parser = new TestMCExceptionStatementsParser();
    Optional<ASTTryStatement3> optAST = parser.parse_StringTryStatement3(expressionString);
    assertTrue(optAST.isPresent());
    ASTTryStatement3 ast = optAST.get();
    ast.setEnclosingScope(TestMCExceptionStatementsMill.globalScope());
    for (ASTTryLocalVariableDeclaration dec: ast.getTryLocalVariableDeclarationList()){
      
      dec.getExpression().setEnclosingScope(TestMCExceptionStatementsMill.globalScope());
      Log.getFindings().clear();
      checker.checkAll((ASTMCExceptionStatementsNode) optAST.get());
      assertFalse(Log.getFindings().isEmpty());
      
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