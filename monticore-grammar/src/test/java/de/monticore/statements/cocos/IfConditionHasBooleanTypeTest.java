/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.cocos;

import de.monticore.statements.mccommonstatements.cocos.IfConditionHasBooleanType;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCBlockStatement;
import de.monticore.statements.testmccommonstatements.TestMCCommonStatementsMill;
import de.monticore.statements.testmccommonstatements._cocos.TestMCCommonStatementsCoCoChecker;
import de.monticore.statements.testmccommonstatements._parser.TestMCCommonStatementsParser;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.FullDeriveFromCombineExpressionsWithLiterals;
import de.monticore.types.check.TypeCalculator;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class IfConditionHasBooleanTypeTest {
  
  protected TestMCCommonStatementsCoCoChecker checker;
  
  @BeforeEach
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    TestMCCommonStatementsMill.reset();
    TestMCCommonStatementsMill.init();
    BasicSymbolsMill.initializePrimitives();
    checker = new TestMCCommonStatementsCoCoChecker();
    checker.addCoCo(new IfConditionHasBooleanType(new TypeCalculator(null,new FullDeriveFromCombineExpressionsWithLiterals())));
  }
  
  public void checkValid(String expressionString) throws IOException {
    
    TestMCCommonStatementsParser parser = new TestMCCommonStatementsParser();
    Optional<ASTMCBlockStatement> optAST = parser.parse_StringMCBlockStatement(expressionString);
    Assertions.assertTrue(optAST.isPresent());
    Log.getFindings().clear();
    checker.checkAll(optAST.get());
    Assertions.assertTrue(Log.getFindings().isEmpty());
    
  }
  
  public void checkInvalid(String expressionString) throws IOException {
    
    TestMCCommonStatementsParser parser = new TestMCCommonStatementsParser();
    Optional<ASTMCBlockStatement> optAST = parser.parse_StringMCBlockStatement(expressionString);
    Assertions.assertTrue(optAST.isPresent());
    Log.getFindings().clear();
    checker.checkAll(optAST.get());
    Assertions.assertFalse(Log.getFindings().isEmpty());
    
  }
  
  @Test
  public void testValid() throws IOException{
    
    checkValid("if(true){}");
    checkValid("if(1<2){}");
    checkValid("if(!true&&(5==6)){}");
    checkValid("if((1<2)||(5%2==1)){}");
  
  }
  
  @Test
  public void testInvalid()throws IOException{
    
    checkInvalid("if(1+1){}");
    checkInvalid("if('c'+10){}");
    checkInvalid("if(1.2-5.5){}");
    
  }
}