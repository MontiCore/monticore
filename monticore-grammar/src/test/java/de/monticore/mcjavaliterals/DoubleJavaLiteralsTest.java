/* (c) https://github.com/MontiCore/monticore */

package de.monticore.mcjavaliterals;

import de.monticore.literals.mcjavaliterals._ast.ASTDoubleLiteral;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.literals.testmcjavaliterals.TestMCJavaLiteralsMill;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class DoubleJavaLiteralsTest {

  @BeforeEach
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    TestMCJavaLiteralsMill.reset();
    TestMCJavaLiteralsMill.init();
  }

  private void checkDoubleLiteral(double d, String s) throws IOException {
    ASTLiteral lit = MCJavaLiteralsTestHelper.getInstance().parseLiteral(s);
    Assertions.assertTrue(lit instanceof ASTDoubleLiteral);
    Assertions.assertEquals(d, ((ASTDoubleLiteral) lit).getValue(), 0);
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testDoubleLiterals() {
    try {
      // decimal number
      checkDoubleLiteral(0.0, "0.0");
      checkDoubleLiteral(.0, ".0");
      checkDoubleLiteral(0., "0.");
      checkDoubleLiteral(5d, "5d");
      checkDoubleLiteral(.4, ".4");
      checkDoubleLiteral(000009.3, "000009.3");
      checkDoubleLiteral(5., "5.");
      checkDoubleLiteral(009., "009.");
      checkDoubleLiteral(009e2, "009e2");
      checkDoubleLiteral(23.4, "23.4");
      checkDoubleLiteral(2e3, "2e3");
      checkDoubleLiteral(2E-3, "2E-3");
      checkDoubleLiteral(009d, "009d");
      checkDoubleLiteral(.1e1, ".1e1");
      checkDoubleLiteral(.1, ".1");
      checkDoubleLiteral(.11e12, ".11e12");
      checkDoubleLiteral(.11e+12, ".11e+12");
      checkDoubleLiteral(29.18e08, "29.18e08");
      checkDoubleLiteral(0029.0008e-00008, "0029.0008e-00008");
      checkDoubleLiteral(0029.0008e-00008D, "0029.0008e-00008D");

      // hexadezimal number
      checkDoubleLiteral(0x5.p1, "0x5.p1");
      checkDoubleLiteral(0x.5p1, "0x.5p1");
      checkDoubleLiteral(0xFp-9, "0xFp-9");
      checkDoubleLiteral(0xfP2, "0xfP2");
      checkDoubleLiteral(0xfp1, "0xfp1");
      checkDoubleLiteral(0x.fP1, "0x.fP1");
      checkDoubleLiteral(0x0p0, "0x0p0");
      checkDoubleLiteral(0x0.0p1, "0x0.0p1");
      checkDoubleLiteral(0x.0p1, "0x.0p1");
      checkDoubleLiteral(0x.5AFp1, "0x.5AFp1");
      checkDoubleLiteral(0x0050AF.CD9p-008, "0x0050AF.CD9p-008");
      checkDoubleLiteral(0x0050AF.CD9p-008d, "0x0050AF.CD9p-008d");
      checkDoubleLiteral(0x0p-5, "0x0p-5");
      checkDoubleLiteral(0x0p1, "0x0p1");
      checkDoubleLiteral(0x0p1d, "0x0p1d");

      // Examples from Java Language Specification
      checkDoubleLiteral(.3, ".3");
      checkDoubleLiteral(1e1, "1e1");
      checkDoubleLiteral(2., "2.");
      checkDoubleLiteral(0.0, "0.0");
      checkDoubleLiteral(3.14, "3.14");
      checkDoubleLiteral(1e-9d, "1e-9d");
      checkDoubleLiteral(1e137, "1e137");
    }
    catch (IOException e) {
      Assertions.fail(e.getMessage());
    }
  }
}
