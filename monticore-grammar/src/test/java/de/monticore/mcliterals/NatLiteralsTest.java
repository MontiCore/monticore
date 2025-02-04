/* (c) https://github.com/MontiCore/monticore */

package de.monticore.mcliterals;

import de.monticore.literals.mccommonliterals._ast.ASTNatLiteral;
import de.monticore.literals.testmccommonliterals.TestMCCommonLiteralsMill;
import de.monticore.literals.testmccommonliterals._parser.TestMCCommonLiteralsParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

public class NatLiteralsTest {
  
  @BeforeEach
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    TestMCCommonLiteralsMill.reset();
    TestMCCommonLiteralsMill.init();
  }

  private void checkNatLiteral(int i, String s) throws IOException {
    TestMCCommonLiteralsParser parser = new TestMCCommonLiteralsParser();
    Optional<ASTNatLiteral> ast = parser.parse_StringNatLiteral(s);
    Assertions.assertTrue(!parser.hasErrors());
    Assertions.assertEquals(i, ast.get().getValue());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  private void checkFailingNatLiteral(String s) throws IOException {
    TestMCCommonLiteralsParser parser = new TestMCCommonLiteralsParser();
    parser.parse_StringNatLiteral(s);
    Assertions.assertTrue(parser.hasErrors());
  }

  @Test
  public void testNatLiterals() {
    try {
      // decimal number
      checkNatLiteral(0, "0");
      checkNatLiteral(123, "123");
      checkNatLiteral(10, "10");
      checkNatLiteral(5, "5");
    }
    catch (IOException e) {
      Assertions.fail(e.getMessage());
    }
  }
  
  @Test
  public void testFailingNatLiterals() throws IOException {
    try {
      checkFailingNatLiteral("0x5");
      checkFailingNatLiteral("-5");
    }
    catch (IOException e) {
      Assertions.fail(e.getMessage());
    }
  }
}
