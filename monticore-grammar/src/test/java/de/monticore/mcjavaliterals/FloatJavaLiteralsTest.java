/* (c) https://github.com/MontiCore/monticore */

package de.monticore.mcjavaliterals;

import de.monticore.literals.mcjavaliterals._ast.ASTFloatLiteral;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.literals.testmcjavaliterals.TestMCJavaLiteralsMill;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class FloatJavaLiteralsTest {

  @BeforeEach
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    TestMCJavaLiteralsMill.reset();
    TestMCJavaLiteralsMill.init();
  }

  private void checkFloatLiteral(float f, String s) throws IOException {
    ASTLiteral lit = MCJavaLiteralsTestHelper.getInstance().parseLiteral(s);
    Assertions.assertTrue(lit instanceof ASTFloatLiteral);
    Assertions.assertEquals(f, ((ASTFloatLiteral) lit).getValue(), 0);
    Assertions.assertTrue(true);
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testFloatLiterals() {
    // decimal number
    try {
      checkFloatLiteral(0F, "0F");
      checkFloatLiteral(0.0F, "0.0F");
      checkFloatLiteral(5F, "5F");
      checkFloatLiteral(.4F, ".4F");
      checkFloatLiteral(000009.3F, "000009.3F");
      checkFloatLiteral(5.F, "5.F");
      checkFloatLiteral(009.f, "009.f");
      checkFloatLiteral(009f, "009f");
      checkFloatLiteral(009e2f, "009e2f");
      checkFloatLiteral(23.4F, "23.4F");
      checkFloatLiteral(2e3F, "2e3F");
      checkFloatLiteral(2E-3F, "2E-3F");
      checkFloatLiteral(009f, "009f");
      checkFloatLiteral(.1e1F, ".1e1F");
      checkFloatLiteral(.1F, ".1F");
      checkFloatLiteral(.11e12F, ".11e12F");
      checkFloatLiteral(.11e+12F, ".11e+12F");
      checkFloatLiteral(29.18e08F, "29.18e08F");
      checkFloatLiteral(0029.0008e-00008F, "0029.0008e-00008F");
      
      // hexadezimal number
      checkFloatLiteral(0x5.p1f, "0x5.p1f");
      // checkFloatLiteral(0x.5p1f, "0x.5p1f");
      checkFloatLiteral(0xFp-9f, "0xFp-9f");
      checkFloatLiteral(0xfP2F, "0xfP2F");
      checkFloatLiteral(0xfp1F, "0xfp1F");
      checkFloatLiteral(0x.fP1F, "0x.fP1F");
      checkFloatLiteral(0x0p0F, "0x0p0F");
      checkFloatLiteral(0x0.0p1F, "0x0.0p1F");
      checkFloatLiteral(0x.0p1F, "0x.0p1F");
      checkFloatLiteral(0x.5AFp1f, "0x.5AFp1f");
      checkFloatLiteral(0x0050AF.CD9p-008f, "0x0050AF.CD9p-008f");
      checkFloatLiteral(0x1.fffffeP+127f, "0x1.fffffeP+127f");
      checkFloatLiteral(0x0p-5f, "0x0p-5f");
      checkFloatLiteral(0x0p1F, "0x0p1F");
      checkFloatLiteral(0x0p-5F, "0x0p-5F");
      
      // Examples from Java Language Specification
      checkFloatLiteral(1e1f, "1e1f");
      checkFloatLiteral(2.f, "2.f");
      checkFloatLiteral(.3f, ".3f");
      checkFloatLiteral(0f, "0f");
      checkFloatLiteral(3.14f, "3.14f");
      checkFloatLiteral(6.022137e+23f, "6.022137e+23f");
    }
    catch (IOException e)
    {
      Assertions.fail(e.getMessage());
    }
  }
}
