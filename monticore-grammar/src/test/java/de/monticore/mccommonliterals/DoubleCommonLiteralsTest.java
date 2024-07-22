/* (c) https://github.com/MontiCore/monticore */

package de.monticore.mccommonliterals;

import de.monticore.literals.mccommonliterals._ast.ASTBasicDoubleLiteral;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.literals.testmccommonliterals.TestMCCommonLiteralsMill;
import de.monticore.literals.testmccommonliterals._parser.TestMCCommonLiteralsParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

public class DoubleCommonLiteralsTest {

  @BeforeEach
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    TestMCCommonLiteralsMill.reset();
    TestMCCommonLiteralsMill.init();
  }

  private void checkDoubleLiteral(double d, String s) throws IOException {
    TestMCCommonLiteralsParser parser = new TestMCCommonLiteralsParser();
    Optional<ASTLiteral> lit = parser.parseLiteral(new StringReader(s));
    Assertions.assertTrue(lit.isPresent());
    Assertions.assertTrue(lit.get() instanceof ASTBasicDoubleLiteral);
    Assertions.assertEquals(d, ((ASTBasicDoubleLiteral) lit.get()).getValue(), 0);
  }

  private void checkFalse(String s) throws IOException {
    TestMCCommonLiteralsParser parser = new TestMCCommonLiteralsParser();
    Optional<ASTBasicDoubleLiteral> lit = parser.parseBasicDoubleLiteral(new StringReader(s));
    Assertions.assertTrue(!lit.isPresent());
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
      Assertions.fail(e.getMessage());
    }
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
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
      Assertions.fail(e.getMessage());
    }
  }
}
