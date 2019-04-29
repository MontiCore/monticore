/* (c) https://github.com/MontiCore/monticore */

package de.monticore.mcbasicliterals;

import de.monticore.mcbasicliterals._ast.ASTBasicDoubleLiteral;
import de.monticore.mcbasicliterals._ast.ASTLiteral;
import de.monticore.testmcbasicliterals._parser.TestMCBasicLiteralsParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import static org.junit.Assert.*;

public class DoubleLiteralsTest {

  @BeforeClass
  public static void init() {
    Log.init();
    Log.enableFailQuick(false);
  }

  private void checkDoubleLiteral(double d, String s) throws IOException {
    TestMCBasicLiteralsParser parser = new TestMCBasicLiteralsParser();
    Optional<ASTLiteral> lit = parser.parseLiteral(new StringReader(s));
    assertTrue(lit.isPresent());
    assertTrue(lit.get() instanceof ASTBasicDoubleLiteral);
    assertEquals(d, ((ASTBasicDoubleLiteral) lit.get()).getValue(), 0);
  }

  private void checkFalse(String s) throws IOException {
    TestMCBasicLiteralsParser parser = new TestMCBasicLiteralsParser();
    Optional<ASTBasicDoubleLiteral> lit = parser.parseBasicDoubleLiteral(new StringReader(s));
    assertTrue(!lit.isPresent());
  }

  @Test
  public void testDoubleLiterals() {
    try {
      // decimal number
      checkDoubleLiteral(0.0, "0.0");
      checkDoubleLiteral(0.0, "0.0");
      checkDoubleLiteral(3.0, "3.0");
      checkDoubleLiteral(3.0, "3.0");
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testFalseDoubleLiterals() {
    try {
      // decimal number
      checkFalse(".0d");
      checkFalse("0.d");
      checkFalse("5d");
      checkFalse("009e2d");

    }
    catch (IOException e) {
      fail(e.getMessage());
    }
  }
}
