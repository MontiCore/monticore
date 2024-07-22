/* (c) https://github.com/MontiCore/monticore */

package de.monticore.mcjavaliterals;

import de.monticore.literals.mcjavaliterals._ast.ASTLongLiteral;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.literals.testmcjavaliterals.TestMCJavaLiteralsMill;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class LongJavaLiteralsTest {


  @BeforeEach
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    TestMCJavaLiteralsMill.reset();
    TestMCJavaLiteralsMill.init();
  }

  private void checkLongLiteral(long l, String s) throws IOException {
    ASTLiteral lit = MCJavaLiteralsTestHelper.getInstance().parseLiteral(s);
    Assertions.assertTrue(lit instanceof ASTLongLiteral);
    Assertions.assertEquals(l, ((ASTLongLiteral) lit).getValue());
    
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testLongLiterals() {
    try {
      // decimal number
      checkLongLiteral(0L, "0L");
      checkLongLiteral(123L, "123L");
      checkLongLiteral(10L, "10L");
      checkLongLiteral(5L, "5L");
      
      // hexadezimal number
      checkLongLiteral(0x12L, "0x12L");
      checkLongLiteral(0XeffL, "0XeffL");
      checkLongLiteral(0x1234567890L, "0x1234567890L");
      checkLongLiteral(0xabcdefL, "0xabcdefL");
      checkLongLiteral(0x0L, "0x0L");
      checkLongLiteral(0xaL, "0xaL");
      checkLongLiteral(0xC0FFEEL, "0xC0FFEEL");
      checkLongLiteral(0x005fL, "0x005fL");
      
      // octal number
      checkLongLiteral(02L, "02L");
      checkLongLiteral(07L, "07L");
      checkLongLiteral(00L, "00L");
      checkLongLiteral(076543210L, "076543210L");
      checkLongLiteral(00017L, "00017L");
    }
    catch (IOException e) {
      Assertions.fail(e.getMessage());
    }
  }
}
