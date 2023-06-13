/* (c) https://github.com/MontiCore/monticore */

package de.monticore.mcliterals;

import de.monticore.literals.mccommonliterals._ast.ASTNatLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTSignedNatLiteral;
import de.monticore.literals.testmccommonliterals.TestMCCommonLiteralsMill;
import de.monticore.literals.testmccommonliterals._parser.TestMCCommonLiteralsParser;
import de.monticore.testmcliteralsv2.TestMCLiteralsV2Mill;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.*;

public class SignedNatLiteralsTest {
  
  @Before
  public void initLog() {
    LogStub.init();
    Log.enableFailQuick(false);
    TestMCCommonLiteralsMill.reset();
    TestMCCommonLiteralsMill.init();
  }
  
  private void checkNatLiteral(int i, String s) throws IOException {
    TestMCCommonLiteralsParser parser = new TestMCCommonLiteralsParser();
    Optional<ASTSignedNatLiteral> ast = parser.parse_StringSignedNatLiteral(s);
    assertTrue(!parser.hasErrors());
    assertEquals(i, ast.get().getValue());
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
  private void checkFailingNatLiteral(String s) throws IOException {
    TestMCCommonLiteralsParser parser = new TestMCCommonLiteralsParser();
    parser.parse_StringSignedNatLiteral(s);
    assertTrue(parser.hasErrors());
  }

  @Test
  public void testNatLiterals() {
    try {
      // decimal number
      checkNatLiteral(-0, "-0");
      checkNatLiteral(-123, "-123");
      checkNatLiteral(-10, "-10");
      checkNatLiteral(-5, "-5");


      checkNatLiteral(0, "0");
      checkNatLiteral(123, "123");
      checkNatLiteral(10, "10");
      checkNatLiteral(5, "5");

    }
    catch (IOException e) {
      fail(e.getMessage());
    }
  }
  
  @Test
  public void testFailingNatLiterals() throws IOException {
    try {
      checkFailingNatLiteral("0x5");
//      checkFailingNatLiteral("-5");
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
  }
}
