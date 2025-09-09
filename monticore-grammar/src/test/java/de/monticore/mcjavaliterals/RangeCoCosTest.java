/* (c) https://github.com/MontiCore/monticore */
package de.monticore.mcjavaliterals;

import de.monticore.literals.mcjavaliterals._ast.*;
import de.monticore.literals.mcjavaliterals._cocos.MCJavaLiteralsCoCoChecker;
import de.monticore.literals.mcjavaliterals.cocos.DoubleLiteralRangeCoCo;
import de.monticore.literals.mcjavaliterals.cocos.FloatLiteralRangeCoCo;
import de.monticore.literals.mcjavaliterals.cocos.IntLiteralRangeCoCo;
import de.monticore.literals.mcjavaliterals.cocos.LongLiteralRangeCoCo;
import de.monticore.literals.testmcjavaliterals.TestMCJavaLiteralsMill;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class RangeCoCosTest {

  @BeforeEach
  public void setup(){
    Log.init();
    Log.enableFailQuick(false);
    TestMCJavaLiteralsMill.reset();
    TestMCJavaLiteralsMill.init();
  }


  protected final void checkIntLiteral(String expression, BigInteger min, BigInteger max) throws IOException {
    Log.clearFindings();
    Optional<ASTIntLiteral> astex = TestMCJavaLiteralsMill.parser().parse_StringIntLiteral(expression);
    Assertions.assertTrue(astex.isPresent());

    MCJavaLiteralsCoCoChecker checker = new MCJavaLiteralsCoCoChecker();
    checker.addCoCo(new IntLiteralRangeCoCo(min, max));

    checker.checkAll((ASTMCJavaLiteralsNode) astex.get());

    Assertions.assertFalse(Log.getErrorCount() > 0);
  }

  protected final void checkLongLiteral(String expression, BigInteger min, BigInteger max) throws IOException {
    Log.clearFindings();
    Optional<ASTLongLiteral> astex = TestMCJavaLiteralsMill.parser().parse_StringLongLiteral(expression);
    Assertions.assertTrue(astex.isPresent());

    MCJavaLiteralsCoCoChecker checker = new MCJavaLiteralsCoCoChecker();
    checker.addCoCo(new LongLiteralRangeCoCo(min, max));

    checker.checkAll((ASTMCJavaLiteralsNode) astex.get());

    Assertions.assertFalse(Log.getErrorCount() > 0);
  }

  protected final void checkDoubleLiteral(String expression, BigDecimal min, BigDecimal max) throws IOException {
    Log.clearFindings();
    Optional<ASTDoubleLiteral> astex = TestMCJavaLiteralsMill.parser().parse_StringDoubleLiteral(expression);
    Assertions.assertTrue(astex.isPresent());

    MCJavaLiteralsCoCoChecker checker = new MCJavaLiteralsCoCoChecker();
    checker.addCoCo(new DoubleLiteralRangeCoCo(min, max));

    checker.checkAll((ASTMCJavaLiteralsNode) astex.get());

    Assertions.assertFalse(Log.getErrorCount() > 0);
  }

  protected final void checkFloatLiteral(String expression, BigDecimal min, BigDecimal max) throws IOException {
    Log.clearFindings();
    Optional<ASTFloatLiteral> astex = TestMCJavaLiteralsMill.parser().parse_StringFloatLiteral(expression);
    Assertions.assertTrue(astex.isPresent());

    MCJavaLiteralsCoCoChecker checker = new MCJavaLiteralsCoCoChecker();
    checker.addCoCo(new FloatLiteralRangeCoCo(min, max));

    checker.checkAll((ASTMCJavaLiteralsNode) astex.get());

    Assertions.assertFalse(Log.getErrorCount() > 0);
  }

  protected final void checkErrorIntLiteral(String expression, BigInteger min, BigInteger max, String expectedError) throws IOException {
    Log.clearFindings();
    Optional<ASTIntLiteral> astex = TestMCJavaLiteralsMill.parser().parse_StringIntLiteral(expression);
    Assertions.assertTrue(astex.isPresent());

    MCJavaLiteralsCoCoChecker checker = new MCJavaLiteralsCoCoChecker();
    checker.addCoCo(new IntLiteralRangeCoCo(min, max));

    checker.checkAll((ASTMCJavaLiteralsNode) astex.get());

    Assertions.assertEquals(1, Log.getErrorCount());
    Assertions.assertTrue(Log.getFindings().get(0).getMsg().startsWith(expectedError));
  }

  protected final void checkErrorLongLiteral(String expression, BigInteger min, BigInteger max, String expectedError) throws IOException {
    Log.clearFindings();
    Optional<ASTLongLiteral> astex = TestMCJavaLiteralsMill.parser().parse_StringLongLiteral(expression);
    Assertions.assertTrue(astex.isPresent());

    MCJavaLiteralsCoCoChecker checker = new MCJavaLiteralsCoCoChecker();
    checker.addCoCo(new LongLiteralRangeCoCo(min, max));

    checker.checkAll((ASTMCJavaLiteralsNode) astex.get());

    Assertions.assertEquals(1, Log.getErrorCount());
    Assertions.assertTrue(Log.getFindings().get(0).getMsg().startsWith(expectedError));
  }

  protected final void checkErrorDoubleLiteral(String expression, BigDecimal min, BigDecimal max, String expectedError) throws IOException {
    Log.clearFindings();
    Optional<ASTDoubleLiteral> astex = TestMCJavaLiteralsMill.parser().parse_StringDoubleLiteral(expression);
    Assertions.assertTrue(astex.isPresent());

    MCJavaLiteralsCoCoChecker checker = new MCJavaLiteralsCoCoChecker();
    checker.addCoCo(new DoubleLiteralRangeCoCo(min, max));

    checker.checkAll((ASTMCJavaLiteralsNode) astex.get());

    Assertions.assertEquals(1, Log.getErrorCount());
    Assertions.assertTrue(Log.getFindings().get(0).getMsg().startsWith(expectedError));
  }

  protected final void checkErrorFloatLiteral(String expression, BigDecimal min, BigDecimal max, String expectedError) throws IOException {
    Log.clearFindings();
    Optional<ASTFloatLiteral> astex = TestMCJavaLiteralsMill.parser().parse_StringFloatLiteral(expression);
    Assertions.assertTrue(astex.isPresent());

    MCJavaLiteralsCoCoChecker checker = new MCJavaLiteralsCoCoChecker();
    checker.addCoCo(new FloatLiteralRangeCoCo(min, max));

    checker.checkAll((ASTMCJavaLiteralsNode) astex.get());

    Assertions.assertEquals(1, Log.getErrorCount());
    Assertions.assertTrue(Log.getFindings().get(0).getMsg().startsWith(expectedError));
  }
  
  @Test
  public void testInt() throws IOException{
    checkIntLiteral(String.valueOf(Integer.MAX_VALUE), BigInteger.valueOf(Integer.MIN_VALUE), BigInteger.valueOf(Integer.MAX_VALUE));
    checkIntLiteral("1", BigInteger.valueOf(Integer.MIN_VALUE), BigInteger.valueOf(Integer.MAX_VALUE));
    checkIntLiteral("123", BigInteger.valueOf(Integer.MIN_VALUE), BigInteger.valueOf(Integer.MAX_VALUE));
    checkIntLiteral("0xABCDEF", BigInteger.valueOf(Integer.MIN_VALUE), BigInteger.valueOf(Integer.MAX_VALUE));

    checkErrorIntLiteral("0x80000000", BigInteger.valueOf(Integer.MIN_VALUE), BigInteger.valueOf(Integer.MAX_VALUE), "0xA0216");

    checkIntLiteral("2000", new BigInteger("-2000"), new BigInteger("2000"));
    checkErrorIntLiteral("2001", new BigInteger("-2000"), new BigInteger("2000"), "0xA0216");
  }
  
  @Test
  public void testLong() throws IOException{
    checkLongLiteral(String.valueOf(Long.MAX_VALUE) + "L", BigInteger.valueOf(Long.MIN_VALUE), BigInteger.valueOf(Long.MAX_VALUE));
    checkLongLiteral("1L", BigInteger.valueOf(Long.MIN_VALUE), BigInteger.valueOf(Long.MAX_VALUE));
    checkLongLiteral("123L", BigInteger.valueOf(Long.MIN_VALUE), BigInteger.valueOf(Long.MAX_VALUE));
    checkLongLiteral("0xABCDEFL", BigInteger.valueOf(Long.MIN_VALUE), BigInteger.valueOf(Long.MAX_VALUE));

    checkErrorLongLiteral("0x8000000000000000L", BigInteger.valueOf(Long.MIN_VALUE), BigInteger.valueOf(Long.MAX_VALUE), "0xA0217");

    checkLongLiteral("2000L", new BigInteger("-2000"), new BigInteger("2000"));
    checkErrorLongLiteral("2001L", new BigInteger("-2000"), new BigInteger("2000"), "0xA0217");
  }

  @Test
  public void testDouble() throws IOException {
    checkDoubleLiteral(String.valueOf(Double.MAX_VALUE), BigDecimal.valueOf(-Double.MAX_VALUE), BigDecimal.valueOf(Double.MAX_VALUE));
    checkDoubleLiteral("1.0", BigDecimal.valueOf(-Double.MAX_VALUE), BigDecimal.valueOf(Double.MAX_VALUE));
    checkDoubleLiteral("123.0", BigDecimal.valueOf(-Double.MAX_VALUE), BigDecimal.valueOf(Double.MAX_VALUE));
    checkDoubleLiteral(String.valueOf(Double.MIN_VALUE), BigDecimal.valueOf(-Double.MAX_VALUE), BigDecimal.valueOf(Double.MAX_VALUE));

    checkErrorDoubleLiteral("1.7976931348623157e+309", BigDecimal.valueOf(-Double.MAX_VALUE), BigDecimal.valueOf(Double.MAX_VALUE), "0xA0218");

    checkDoubleLiteral("2000.0", new BigDecimal("-2000"), new BigDecimal("2000"));
    checkErrorDoubleLiteral("2001.0", new BigDecimal("-2000"), new BigDecimal("2000"), "0xA0218");
  }
  
  @Test
  public void testFloat() throws IOException{
    checkFloatLiteral("3.4028234663852886E+38f", BigDecimal.valueOf(-Float.MAX_VALUE), BigDecimal.valueOf(Float.MAX_VALUE));
    checkFloatLiteral("1.0f", BigDecimal.valueOf(-Float.MAX_VALUE), BigDecimal.valueOf(Float.MAX_VALUE));
    checkFloatLiteral("123.0f", BigDecimal.valueOf(-Float.MAX_VALUE), BigDecimal.valueOf(Float.MAX_VALUE));
    checkFloatLiteral(String.valueOf(Float.MIN_VALUE) + "f", BigDecimal.valueOf(-Float.MAX_VALUE), BigDecimal.valueOf(Float.MAX_VALUE));

    checkErrorFloatLiteral("3.4028235e+39f", BigDecimal.valueOf(-Float.MAX_VALUE), BigDecimal.valueOf(Float.MAX_VALUE), "0xA0219");

    checkFloatLiteral("2000.0f", new BigDecimal("-2000"), new BigDecimal("2000"));
    checkErrorFloatLiteral("2001.0f", new BigDecimal("-2000"), new BigDecimal("2000"), "0xA0219");
  }
  
}
