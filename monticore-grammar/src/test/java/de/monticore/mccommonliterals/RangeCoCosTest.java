/* (c) https://github.com/MontiCore/monticore */
package de.monticore.mccommonliterals;

import de.monticore.literals.mccommonliterals._ast.ASTSignedLiteral;
import de.monticore.literals.mccommonliterals._cocos.MCCommonLiteralsCoCoChecker;
import de.monticore.literals.mccommonliterals.cocos.*;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.literals.testmccommonliterals.TestMCCommonLiteralsMill;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;

public class RangeCoCosTest {

  @BeforeEach
  public void setup(){
    Log.init();
    Log.enableFailQuick(false);
    TestMCCommonLiteralsMill.reset();
    TestMCCommonLiteralsMill.init();
  }

  protected final void checkLiteral(String expression, BigInteger min, BigInteger max) throws IOException {
    Log.clearFindings();
    Optional<ASTLiteral> astex = TestMCCommonLiteralsMill.parser().parse_StringLiteral(expression);
    Assertions.assertTrue(astex.isPresent());

    MCCommonLiteralsCoCoChecker checker = new MCCommonLiteralsCoCoChecker();
    checker.addCoCo(new BasicFloatLiteralRangeCoCo(new BigDecimal(min), new BigDecimal(max)));
    checker.addCoCo(new BasicDoubleLiteralRangeCoCo(new BigDecimal(min), new BigDecimal(max)));
    checker.addCoCo(new BasicLongLiteralRangeCoCo(min, max));
    checker.addCoCo(new NatLiteralRangeCoCo(min, max));

    checker.checkAll(astex.get());

    Assertions.assertFalse(Log.getErrorCount() > 0);
  }

  protected final void checkLiteral(String expression) throws IOException {
    Log.clearFindings();
    Optional<ASTLiteral> astex = TestMCCommonLiteralsMill.parser().parse_StringLiteral(expression);
    Assertions.assertTrue(astex.isPresent());

    MCCommonLiteralsCoCoChecker checker = new MCCommonLiteralsCoCoChecker();
    checker.addCoCo(new BasicFloatLiteralRangeCoCo());
    checker.addCoCo(new BasicDoubleLiteralRangeCoCo());
    checker.addCoCo(new BasicLongLiteralRangeCoCo());
    checker.addCoCo(new NatLiteralRangeCoCo());

    checker.checkAll(astex.get());

    Assertions.assertFalse(Log.getErrorCount() > 0);
  }

  protected final void checkSignedLiteral(String expression) throws IOException {
    Log.clearFindings();
    Optional<ASTSignedLiteral> astex = TestMCCommonLiteralsMill.parser().parse_StringSignedLiteral(expression);
    Assertions.assertTrue(astex.isPresent());

    MCCommonLiteralsCoCoChecker checker = new MCCommonLiteralsCoCoChecker();
    checker.addCoCo(new SignedBasicFloatLiteralRangeCoCo());
    checker.addCoCo(new SignedBasicDoubleLiteralRangeCoCo());
    checker.addCoCo(new SignedBasicLongLiteralRangeCoCo());
    checker.addCoCo(new SignedNatLiteralRangeCoCo());

    checker.checkAll(astex.get());

    Assertions.assertFalse(Log.getErrorCount() > 0);
  }

  protected final void checkErrorLiteral(String expression, BigInteger min, BigInteger max, String expectedError) throws IOException {
    Log.clearFindings();
    Optional<ASTLiteral> astex = TestMCCommonLiteralsMill.parser().parse_StringLiteral(expression);
    Assertions.assertTrue(astex.isPresent());

    MCCommonLiteralsCoCoChecker checker = new MCCommonLiteralsCoCoChecker();
    checker.addCoCo(new BasicFloatLiteralRangeCoCo(new BigDecimal(min), new BigDecimal(max)));
    checker.addCoCo(new BasicDoubleLiteralRangeCoCo(new BigDecimal(min), new BigDecimal(max)));
    checker.addCoCo(new BasicLongLiteralRangeCoCo(min, max));
    checker.addCoCo(new NatLiteralRangeCoCo(min, max));

    checker.checkAll(astex.get());

    Assertions.assertEquals(1, Log.getErrorCount());
    Assertions.assertTrue(Log.getFindings().get(0).getMsg().startsWith(expectedError));
  }

  protected final void checkErrorLiteral(String expression, String expectedError) throws IOException {
    Log.clearFindings();
    Optional<ASTLiteral> astex = TestMCCommonLiteralsMill.parser().parse_StringLiteral(expression);
    Assertions.assertTrue(astex.isPresent());

    MCCommonLiteralsCoCoChecker checker = new MCCommonLiteralsCoCoChecker();
    checker.addCoCo(new BasicFloatLiteralRangeCoCo());
    checker.addCoCo(new BasicDoubleLiteralRangeCoCo());
    checker.addCoCo(new BasicLongLiteralRangeCoCo());
    checker.addCoCo(new NatLiteralRangeCoCo());

    checker.checkAll(astex.get());

    Assertions.assertEquals(1, Log.getErrorCount());
    Assertions.assertTrue(Log.getFindings().get(0).getMsg().startsWith(expectedError));
  }

  protected final void checkErrorSignedLiteral(String expression, BigInteger min, BigInteger max, String expectedError) throws IOException {
    Log.clearFindings();
    Optional<ASTSignedLiteral> astex = TestMCCommonLiteralsMill.parser().parse_StringSignedLiteral(expression);
    Assertions.assertTrue(astex.isPresent());

    MCCommonLiteralsCoCoChecker checker = new MCCommonLiteralsCoCoChecker();
    checker.addCoCo(new SignedBasicFloatLiteralRangeCoCo(new BigDecimal(min), new BigDecimal(max)));
    checker.addCoCo(new SignedBasicDoubleLiteralRangeCoCo(new BigDecimal(min), new BigDecimal(max)));
    checker.addCoCo(new SignedBasicLongLiteralRangeCoCo(min, max));
    checker.addCoCo(new SignedNatLiteralRangeCoCo(min, max));

    checker.checkAll(astex.get());

    Assertions.assertEquals(1, Log.getErrorCount());
    Assertions.assertTrue(Log.getFindings().get(0).getMsg().startsWith(expectedError));
  }

  protected final void checkErrorSignedLiteral(String expression, String expectedError) throws IOException {
    Log.clearFindings();
    Optional<ASTSignedLiteral> astex = TestMCCommonLiteralsMill.parser().parse_StringSignedLiteral(expression);
    Assertions.assertTrue(astex.isPresent());

    MCCommonLiteralsCoCoChecker checker = new MCCommonLiteralsCoCoChecker();
    checker.addCoCo(new SignedBasicFloatLiteralRangeCoCo());
    checker.addCoCo(new SignedBasicDoubleLiteralRangeCoCo());
    checker.addCoCo(new SignedBasicLongLiteralRangeCoCo());
    checker.addCoCo(new SignedNatLiteralRangeCoCo());

    checker.checkAll(astex.get());

    Assertions.assertEquals(1, Log.getErrorCount());
    Assertions.assertTrue(Log.getFindings().get(0).getMsg().startsWith(expectedError));
  }



  @Test
  public void testIntegerLiteralRange() throws IOException {
    checkLiteral(String.valueOf(Integer.MAX_VALUE));
    checkLiteral("1");
    checkLiteral("123");
    checkSignedLiteral(String.valueOf(Integer.MIN_VALUE));
    checkSignedLiteral("-1");
    checkSignedLiteral("-123");

    checkErrorLiteral("2147483648", "0xA0208");
    checkErrorSignedLiteral("-2147483649", "0xA0210");

    checkLiteral("2000", new BigInteger("-2000"), new BigInteger("2000"));
    checkErrorLiteral("2001", new BigInteger("-2000"), new BigInteger("2000"), "0xA0208");
    checkErrorSignedLiteral("-2001", new BigInteger("-2000"), new BigInteger("2000"), "0xA0210");
  }

  @Test
  public void testLongLiteralRange() throws IOException {
    checkLiteral(String.valueOf(Long.MAX_VALUE) + "L");
    checkLiteral("1L");
    checkLiteral("123L");
    checkSignedLiteral(String.valueOf(Long.MIN_VALUE) + "L");
    checkSignedLiteral("-1L");
    checkSignedLiteral("-123L");

    checkErrorLiteral("9223372036854775808L", "0xA0209");
    checkErrorSignedLiteral("-9223372036854775809L", "0xA0211");

    checkLiteral("2000L", new BigInteger("-2000"), new BigInteger("2000"));
    checkErrorLiteral("2001L", new BigInteger("-2000"), new BigInteger("2000"), "0xA0209");
    checkErrorSignedLiteral("-2001L", new BigInteger("-2000"), new BigInteger("2000"), "0xA0211");
  }

  @Test
  public void testDoubleLiteralRange() throws IOException {
    checkLiteral("1.0");
    checkLiteral("123.0");
    checkSignedLiteral("-1.0");
    checkSignedLiteral("-123.0");

    checkLiteral("2000.0", new BigInteger("-2000"), new BigInteger("2000"));
    checkErrorLiteral("2001.0", new BigInteger("-2000"), new BigInteger("2000"), "0xA0212");
    checkErrorSignedLiteral("-2001.0", new BigInteger("-2000"), new BigInteger("2000"), "0xA0214");
  }

  @Test
  public void testFloatLiteralRange() throws IOException {
    checkLiteral("1.0f");
    checkLiteral("123.0f");
    checkSignedLiteral("-1.0f");
    checkSignedLiteral("-123.0f");

    checkLiteral("2000.0f", new BigInteger("-2000"), new BigInteger("2000"));
    checkErrorLiteral("2001.0f", new BigInteger("-2000"), new BigInteger("2000"), "0xA0213");
    checkErrorSignedLiteral("-2001.0f", new BigInteger("-2000"), new BigInteger("2000"), "0xA0215");
  }

}
