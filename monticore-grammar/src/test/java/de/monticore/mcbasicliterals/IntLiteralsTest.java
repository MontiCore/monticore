/* (c) https://github.com/MontiCore/monticore */

package de.monticore.mcbasicliterals;

import de.monticore.mcbasicliterals._ast.ASTLiteral;
import de.monticore.mcbasicliterals._ast.ASTNatLiteral;
import de.monticore.testmcbasicliterals._parser.TestMCBasicLiteralsParser;
import de.se_rwth.commons.logging.Log;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import static org.junit.Assert.*;

public class IntLiteralsTest {

  @BeforeClass
  public static void init() {
    Log.init();
    Log.enableFailQuick(false);
  }

  private void checkIntLiteral(int i, String s) throws IOException {
    TestMCBasicLiteralsParser parser = new TestMCBasicLiteralsParser();
    Optional<ASTLiteral> lit = parser.parseLiteral(new StringReader(s));
    assertTrue(lit.isPresent());
    assertTrue(lit.get() instanceof ASTNatLiteral);
    assertEquals(i, ((ASTNatLiteral) lit.get()).getValue());
  }

  private void checkFalse(String s) throws IOException {
    TestMCBasicLiteralsParser parser = new TestMCBasicLiteralsParser();
    Optional<ASTNatLiteral> lit = parser.parseNatLiteral(new StringReader(s));
    assertTrue(!lit.isPresent());
  }

  @Test
  public void testIntLiterals() {
    try {
      // decimal number
      checkIntLiteral(0, "0");
      checkIntLiteral(123, "123");
      checkIntLiteral(10, "10");
      checkIntLiteral(5, "5");
      
      // number with leading 0
      checkIntLiteral(2, "02");
      checkIntLiteral(7, "07");
      checkIntLiteral(0, "00");
      checkIntLiteral(76543210, "076543210");
      checkIntLiteral(17, "00017");
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testFalse() {
    try {
      // hexadezimal number
      checkFalse("0x12");
      checkFalse("0Xeff");
      checkFalse("0x34567890");
      checkFalse("0xabcdef");
      checkFalse("0x0");
      checkFalse("0xa");
      checkFalse("0xC0FFEE");
      checkFalse("0x005f");
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
  }
}
