/* (c) https://github.com/MontiCore/monticore */

package de.monticore.mccommonliterals;

import de.monticore.literals.mccommonliterals._ast.ASTNatLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTSignedNatLiteral;
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

public class IntCommonLiteralsTest {

  @BeforeEach
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    TestMCCommonLiteralsMill.reset();
    TestMCCommonLiteralsMill.init();
  }

  private void checkIntLiteral(int i, String s) throws IOException {
    TestMCCommonLiteralsParser parser = new TestMCCommonLiteralsParser();
    Optional<ASTLiteral> lit = parser.parseLiteral(new StringReader(s));
    Assertions.assertTrue(lit.isPresent());
    Assertions.assertTrue(lit.get() instanceof ASTNatLiteral);
    Assertions.assertEquals(i, ((ASTNatLiteral) lit.get()).getValue());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  private void checkSignedIntLiteral(int i, String s) throws IOException {
    TestMCCommonLiteralsParser parser = new TestMCCommonLiteralsParser();
    Optional<ASTSignedNatLiteral> lit = parser.parseSignedNatLiteral(new StringReader(s));
    Assertions.assertTrue(lit.isPresent());
    Assertions.assertTrue(lit.get() instanceof ASTSignedNatLiteral);
    Assertions.assertEquals(i, ((ASTSignedNatLiteral) lit.get()).getValue());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  private void checkFalse(String s) throws IOException {
    TestMCCommonLiteralsParser parser = new TestMCCommonLiteralsParser();
    Optional<ASTNatLiteral> lit = parser.parseNatLiteral(new StringReader(s));
    Assertions.assertTrue(!lit.isPresent());
  }

  private void checkSignedFalse(String s) throws IOException {
    TestMCCommonLiteralsParser parser = new TestMCCommonLiteralsParser();
    Optional<ASTSignedNatLiteral> lit = parser.parseSignedNatLiteral(new StringReader(s));
    Assertions.assertTrue(!lit.isPresent());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
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
      Assertions.fail(e.getMessage());
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
      Assertions.fail(e.getMessage());
    }
  }

  @Test
  public void testSignedIntLiterals() {
    try {
      // decimal number
      checkSignedIntLiteral(0, "0");
      checkSignedIntLiteral(-123, "-123");
      checkSignedIntLiteral(-10, "-10");
      checkSignedIntLiteral(-5, "-5");

      // number with leading 0
      checkSignedIntLiteral(-2, "-02");
      checkSignedIntLiteral(-7, "-07");
      checkSignedIntLiteral(0, "00");
      checkSignedIntLiteral(-76543210, "-076543210");
      checkSignedIntLiteral(-17, "-00017");
    }
    catch (IOException e) {
      Assertions.fail(e.getMessage());
    }
  }

  @Test
  public void testSignedFalse() {
    try {
      // hexadezimal number
      checkFalse("0x12");
      checkFalse("- 2");
      checkFalse("- 02");
    }
    catch (IOException e) {
      Assertions.fail(e.getMessage());
    }
  }


}
