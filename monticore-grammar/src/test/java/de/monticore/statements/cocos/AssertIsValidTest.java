/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.cocos;

import de.monticore.statements.mccommonstatements.cocos.AssertIsValid;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCBlockStatement;
import de.monticore.statements.testmcassertstatements.TestMCAssertStatementsMill;
import de.monticore.statements.testmcassertstatements._cocos.TestMCAssertStatementsCoCoChecker;
import de.monticore.statements.testmcassertstatements._parser.TestMCAssertStatementsParser;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.FullDeriveFromCombineExpressionsWithLiterals;
import de.monticore.types.check.TypeCalculator;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AssertIsValidTest {
  
  private static final TestMCAssertStatementsCoCoChecker checker = new TestMCAssertStatementsCoCoChecker();
  
  @Before
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @BeforeClass
  public static void disableFailQuick(){
    TestMCAssertStatementsMill.reset();
    TestMCAssertStatementsMill.init();
    BasicSymbolsMill.initializePrimitives();
    checker.setTraverser(TestMCAssertStatementsMill.traverser());
    checker.addCoCo(new AssertIsValid(new TypeCalculator(null, new FullDeriveFromCombineExpressionsWithLiterals())));
    
  }
  
  public void checkValid(String expressionString) throws IOException {
  
    TestMCAssertStatementsParser parser = new TestMCAssertStatementsParser();
    Optional<ASTMCBlockStatement> optAST = parser.parse_StringMCBlockStatement(expressionString);
    assertTrue(optAST.isPresent());
    Log.getFindings().clear();
    checker.checkAll(optAST.get());
    assertTrue(Log.getFindings().isEmpty());
    
  }
  
  public void checkInvalid(String expressionString) throws IOException {
    
    TestMCAssertStatementsParser parser = new TestMCAssertStatementsParser();
    Optional<ASTMCBlockStatement> optAST = parser.parse_StringMCBlockStatement(expressionString);
    assertTrue(optAST.isPresent());
    Log.getFindings().clear();
    checker.checkAll(optAST.get());
    assertFalse(Log.getFindings().isEmpty());
    
  }
  
  @Test
  public void testValid() throws IOException {
    
    checkValid("assert 5 >= 0;");
    checkValid("assert !(true||false)&&(5<6);");
    checkInvalid("assert 5 >= 0: 1+1;");
    
  }
  
  @Test
  public void testInvalid() throws IOException {
    
    checkInvalid("assert 4;");
    checkInvalid("assert 'c';");
    
  }
  
}