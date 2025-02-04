/* (c) https://github.com/MontiCore/monticore */

package de.monticore.mccommonliterals;

import de.monticore.literals.mccommonliterals._ast.ASTBasicLongLiteral;
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

public class LongCommonLiteralsTest {
  
  @BeforeEach
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    TestMCCommonLiteralsMill.reset();
    TestMCCommonLiteralsMill.init();
  }

  private void checkLongLiteral(long l, String s) throws IOException {
    TestMCCommonLiteralsParser parser = new TestMCCommonLiteralsParser();
    Optional<ASTLiteral> lit = parser.parseLiteral(new StringReader(s));
    Assertions.assertTrue(lit.isPresent());
    Assertions.assertTrue(lit.get() instanceof ASTBasicLongLiteral);
    Assertions.assertEquals(l, ((ASTBasicLongLiteral) lit.get()).getValue());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  private void checkFalse(String s) throws IOException {
    TestMCCommonLiteralsParser parser = new TestMCCommonLiteralsParser();
    Optional<ASTBasicLongLiteral> lit = parser.parseBasicLongLiteral(new StringReader(s));
    Assertions.assertTrue(!lit.isPresent());
  }

  @Test
  public void testLongLiterals() {
    try {
      // decimal number
      checkLongLiteral(0L, "0L");
      checkLongLiteral(123L, "123L");
      checkLongLiteral(10L, "10L");
      checkLongLiteral(5L, "5L");
      checkLongLiteral(5L, "05L");
      checkLongLiteral(5L, "05L");

    }
    catch (IOException e) {
      Assertions.fail(e.getMessage());
    }
  }

  @Test
  public void testFalse() {
    try {
      // hexadezimal number
      checkFalse("0x12L");
      checkFalse("0XeffL");
      checkFalse("0x1234567890L");
      checkFalse("0xabcdefL");
      checkFalse("0x0L");
      checkFalse("0xaL");
      checkFalse("0xC0FFEEL");
      checkFalse("0x005fL");
      checkFalse("0 L");
    }
    catch (IOException e) {
      Assertions.fail(e.getMessage());
    }
  }
}
