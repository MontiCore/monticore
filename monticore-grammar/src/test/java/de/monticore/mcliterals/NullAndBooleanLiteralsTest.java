/* (c) https://github.com/MontiCore/monticore */

package de.monticore.mcliterals;

import de.monticore.literals.mccommonliterals._ast.ASTBooleanLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTNullLiteral;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.literals.testmccommonliterals.TestMCCommonLiteralsMill;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class NullAndBooleanLiteralsTest {
  
  @BeforeEach
  public void initLog() {
    LogStub.init();
    Log.enableFailQuick(false);
    TestMCCommonLiteralsMill.reset();
    TestMCCommonLiteralsMill.init();
  }
  
  @Test
  public void testNullLiteral() {
    try {
      ASTLiteral lit = MCLiteralsTestHelper.getInstance().parseLiteral("null");
      Assertions.assertTrue(lit instanceof ASTNullLiteral);
    }
    catch (Exception e) {
      Assertions.fail(e.getMessage());
    }
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testBooleanLiterals() {
    try {
      // literal "true":
      ASTLiteral lit = MCLiteralsTestHelper.getInstance().parseLiteral("true");
      Assertions.assertTrue(lit instanceof ASTBooleanLiteral);
      Assertions.assertTrue(((ASTBooleanLiteral) lit).getValue());
      
      // literal "false":
      lit = MCLiteralsTestHelper.getInstance().parseLiteral("false");
      Assertions.assertTrue(lit instanceof ASTBooleanLiteral);
      Assertions.assertFalse(((ASTBooleanLiteral) lit).getValue());
    }
    catch (IOException e) {
      Assertions.fail(e.getMessage());
    }
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
}
