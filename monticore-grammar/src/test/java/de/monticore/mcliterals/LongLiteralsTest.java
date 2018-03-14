/* (c) https://github.com/MontiCore/monticore */

package de.monticore.mcliterals;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.mcliterals._ast.ASTLiteral;
import de.monticore.mcliterals._ast.ASTLongLiteral;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;

/**
 * @author Martin Schindler
 */
public class LongLiteralsTest {
  
  @BeforeClass
  public static void init() {
    // replace log by a sideffect free variant
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Before
  public void setUp() {
    Log.getFindings().clear();
  }
  
  private void checkLongLiteral(long l, String s) throws IOException {
    ASTLiteral lit = MCLiteralsTestHelper.getInstance().parseLiteral(s);
    assertTrue(lit instanceof ASTLongLiteral);
    assertEquals(l, ((ASTLongLiteral) lit).getValue());
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
      
    //binary number
      checkLongLiteral(0b0, "0b0l");
      checkLongLiteral(0b01010, "0b01010l");
      checkLongLiteral(0b00001, "0b00001L");
      checkLongLiteral(0b1111, "0b1111L");
      
      //underscores 
      checkLongLiteral(0257L, "02_57L");
      checkLongLiteral(0370744l, "0370__744l");
      checkLongLiteral(123456L, "123_456L");
      checkLongLiteral(876543210l, "87__65_43210l");
      checkLongLiteral(0b010101L, "0b010_101L");
      checkLongLiteral(0x27af489l, "0x27_af_489l");
      checkLongLiteral(0X79d48e9L, "0X79_d48e9L");
      
  
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
  }
}
