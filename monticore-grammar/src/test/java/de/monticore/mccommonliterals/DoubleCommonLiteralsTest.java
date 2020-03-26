/* (c) https://github.com/MontiCore/monticore */

package de.monticore.mccommonliterals;

import de.monticore.literals.mccommonliterals._ast.ASTBasicDoubleLiteral;
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

public class DoubleCommonLiteralsTest {

  @BeforeClass
  public static void init() {
    LogStub.init();
    Log.enableFailQuick(false);
  }

  private void checkDoubleLiteral(double d, String s) throws IOException {
    TestMCCommonLiteralsParser parser = new TestMCCommonLiteralsParser();
    Optional<ASTLiteral> lit = parser.parseLiteral(new StringReader(s));
    assertTrue(lit.isPresent());
    assertTrue(lit.get() instanceof ASTBasicDoubleLiteral);
    assertEquals(d, ((ASTBasicDoubleLiteral) lit.get()).getValue(), 0);
  }

  private void checkFalse(String s) throws IOException {
    TestMCCommonLiteralsParser parser = new TestMCCommonLiteralsParser();
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
      checkFalse("0 .0");
      checkFalse("0.0 d");
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
  }
}
