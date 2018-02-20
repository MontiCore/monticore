/* (c) https://github.com/MontiCore/monticore */

package de.monticore.mcliterals;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Optional;

import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.mcliterals._ast.ASTNatLiteral;
import de.monticore.testmcliterals._parser.TestMCLiteralsParser;
import de.se_rwth.commons.logging.Log;

public class NatLiteralsTest {
  
  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
  }

  private void checkNatLiteral(int i, String s) throws IOException {
    TestMCLiteralsParser parser = new TestMCLiteralsParser();
    Optional<ASTNatLiteral> ast = parser.parse_StringNatLiteral(s);
    assertTrue(!parser.hasErrors());
    assertEquals(i, ast.get().getValue());
  }
  
  private void checkFailingNatLiteral(String s) throws IOException {
    TestMCLiteralsParser parser = new TestMCLiteralsParser();
    parser.parse_StringNatLiteral(s);
    assertTrue(parser.hasErrors());    
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
      fail(e.getMessage());
    }
  }
  
  @Test
  public void testFailingNatLiterals() throws IOException {
    try {
      checkFailingNatLiteral("0x5");
      checkFailingNatLiteral("-5");
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
  }
}
