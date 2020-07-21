/* (c) https://github.com/MontiCore/monticore */

package de.monticore.mccommonliterals;

import de.monticore.literals.mccommonliterals._ast.ASTBasicFloatLiteral;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.literals.testmccommonliterals._parser.TestMCCommonLiteralsParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import static org.junit.Assert.*;

public class FloatCommonLiteralsTest {

  @BeforeClass
  public static void init() {
    LogStub.init();
    Log.enableFailQuick(false);
  }

  private void checkFloatLiteral(float f, String s) throws IOException {
    TestMCCommonLiteralsParser parser = new TestMCCommonLiteralsParser();
    Optional<ASTLiteral> lit = parser.parseLiteral(new StringReader(s));
    assertTrue(lit.isPresent());
    assertTrue(lit.get() instanceof ASTBasicFloatLiteral);
    assertEquals(f, ((ASTBasicFloatLiteral) lit.get()).getValue(), 0);
    assertTrue(true);
  }

  private void checkFalse(String s) throws IOException {
    TestMCCommonLiteralsParser parser = new TestMCCommonLiteralsParser();
    Optional<ASTBasicFloatLiteral> lit = parser.parseBasicFloatLiteral(new StringReader(s));
    assertTrue(!lit.isPresent());
   }

  @Test
  public void testFloatLiterals() {
    try {
      checkFloatLiteral(0.0f, "0.0f");
      checkFloatLiteral(23.4f, "23.4f");
    }
    catch (IOException e)
    {
      fail(e.getMessage());
    }
  }

  @Test
  public void checkFalseLiterals() {
    try {
      checkFalse("0F");
      checkFalse(".4F");
      checkFalse("5.F");
      checkFalse("009.f");
      checkFalse("009f");
      checkFalse("009e2f");
      checkFalse("2e3F");
      checkFalse("2E-3F");
      checkFalse("009f");
      checkFalse(".1e1F");
      checkFalse(".1F");
      checkFalse(".11e12F");
      checkFalse(".11e+12F");
      checkFalse("29.18e08F");
      checkFalse("0029.0008e-00008F");
      checkFalse("0. 0f");
      checkFalse("0 .0f");
      checkFalse("23.4 f");

      // hexadezimal number
      checkFalse("0x5.p1f");
      checkFalse("0x.5p1f");
      checkFalse("0xFp-9f");
      checkFalse("0xfP2F");
      checkFalse("0xfp1F");
      checkFalse("0x.fP1F");
      checkFalse("0x0p0F");
      checkFalse("0x0.0p1F");
      checkFalse("0x.0p1F");
      checkFalse("0x.5AFp1f");
      checkFalse("0x0050AF.CD9p-008f");
      checkFalse("0x1.fffffeP+127f");
      checkFalse("0x0p-5f");
      checkFalse("0x0p1F");
      checkFalse("0x0p-5F");

      // Examples from Java Language Specification
      checkFalse("1e1f");
      checkFalse("2.f");
      checkFalse(".3f");
      checkFalse("0f");
      checkFalse("6.022137e+23f");
    }
    catch (IOException e)
    {
      fail(e.getMessage());
    }
  }
}
