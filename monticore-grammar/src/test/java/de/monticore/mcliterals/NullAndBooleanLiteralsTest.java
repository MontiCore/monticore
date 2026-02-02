/* (c) https://github.com/MontiCore/monticore */

package de.monticore.mcliterals;

import de.monticore.literals.mccommonliterals._ast.ASTBooleanLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTNullLiteral;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.literals.testmccommonliterals.TestMCCommonLiteralsMill;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

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
      assertInstanceOf(ASTNullLiteral.class, lit);
    }
    catch (Exception e) {
      fail(e.getMessage());
    }
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testBooleanLiterals() {
    try {
      // literal "true":
      ASTLiteral lit = MCLiteralsTestHelper.getInstance().parseLiteral("true");
      assertInstanceOf(ASTBooleanLiteral.class, lit);
      assertTrue(((ASTBooleanLiteral) lit).getValue());
      
      // literal "false":
      lit = MCLiteralsTestHelper.getInstance().parseLiteral("false");
      assertInstanceOf(ASTBooleanLiteral.class, lit);
      assertFalse(((ASTBooleanLiteral) lit).getValue());
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
  
    assertTrue(Log.getFindings().isEmpty());
  }
}
