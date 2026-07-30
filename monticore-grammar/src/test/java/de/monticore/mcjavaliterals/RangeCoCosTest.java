/* (c) https://github.com/MontiCore/monticore */
package de.monticore.mcjavaliterals;

import de.monticore.literals.mcjavaliterals._ast.*;
import de.monticore.literals.mcjavaliterals._cocos.MCJavaLiteralsCoCoChecker;
import de.monticore.literals.mcjavaliterals.cocos.DoubleLiteralRangeCoCo;
import de.monticore.literals.mcjavaliterals.cocos.FloatLiteralRangeCoCo;
import de.monticore.literals.mcjavaliterals.cocos.IntLiteralRangeCoCo;
import de.monticore.literals.mcjavaliterals.cocos.LongLiteralRangeCoCo;
import de.monticore.literals.testmcjavaliterals.TestMCJavaLiteralsMill;
import de.monticore.runtime.junit.MCAssertions;
import de.monticore.runtime.junit.TestWithMCLanguage;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestWithMCLanguage(TestMCJavaLiteralsMill.class)
public class RangeCoCosTest {

  static Stream<Arguments> checkIntLiteralArgs() {
    return Stream.of(
        Arguments.of(String.valueOf(Integer.MAX_VALUE), BigInteger.valueOf(Integer.MIN_VALUE),
            BigInteger.valueOf(Integer.MAX_VALUE)),
        Arguments.of("1", BigInteger.valueOf(Integer.MIN_VALUE),
            BigInteger.valueOf(Integer.MAX_VALUE)),
        Arguments.of("123", BigInteger.valueOf(Integer.MIN_VALUE),
            BigInteger.valueOf(Integer.MAX_VALUE)),
        Arguments.of("0xABCDEF", BigInteger.valueOf(Integer.MIN_VALUE),
            BigInteger.valueOf(Integer.MAX_VALUE)),
        Arguments.of("2000", new BigInteger("-2000"), new BigInteger("2000")));
  }
  
  @ParameterizedTest
  @MethodSource("checkIntLiteralArgs")
  public final void checkIntLiteral(String expression, BigInteger min, BigInteger max)
      throws IOException {
    Optional<ASTIntLiteral> astex =
        TestMCJavaLiteralsMill.parser().parse_StringIntLiteral(expression);
    assertTrue(astex.isPresent());

    MCJavaLiteralsCoCoChecker checker = new MCJavaLiteralsCoCoChecker();
    checker.addCoCo(new IntLiteralRangeCoCo(min, max));

    checker.checkAll((ASTMCJavaLiteralsNode) astex.get());
  }

  static Stream<Arguments> checkLongLiteralArgs() {
    return Stream.of(
        Arguments.of(Long.MAX_VALUE + "L", BigInteger.valueOf(Long.MIN_VALUE),
            BigInteger.valueOf(Long.MAX_VALUE)),
        Arguments.of("1L", BigInteger.valueOf(Long.MIN_VALUE), BigInteger.valueOf(Long.MAX_VALUE)),
        Arguments.of("123L", BigInteger.valueOf(Long.MIN_VALUE),
            BigInteger.valueOf(Long.MAX_VALUE)),
        Arguments.of("0xABCDEFL", BigInteger.valueOf(Long.MIN_VALUE),
            BigInteger.valueOf(Long.MAX_VALUE)),
        Arguments.of("2000L", new BigInteger("-2000"), new BigInteger("2000")));
  }
  
  @ParameterizedTest
  @MethodSource("checkLongLiteralArgs")
  public final void checkLongLiteral(String expression, BigInteger min, BigInteger max)
      throws IOException {
    Optional<ASTLongLiteral> astex =
        TestMCJavaLiteralsMill.parser().parse_StringLongLiteral(expression);
    assertTrue(astex.isPresent());

    MCJavaLiteralsCoCoChecker checker = new MCJavaLiteralsCoCoChecker();
    checker.addCoCo(new LongLiteralRangeCoCo(min, max));

    checker.checkAll((ASTMCJavaLiteralsNode) astex.get());
  }

  static Stream<Arguments> checkDoubleLiteralArgs() {
    return Stream.of(
        Arguments.of(String.valueOf(Double.MAX_VALUE), BigDecimal.valueOf(-Double.MAX_VALUE),
            BigDecimal.valueOf(Double.MAX_VALUE)),
        Arguments.of("1.0", BigDecimal.valueOf(-Double.MAX_VALUE),
            BigDecimal.valueOf(Double.MAX_VALUE)),
        Arguments.of("123.0", BigDecimal.valueOf(-Double.MAX_VALUE),
            BigDecimal.valueOf(Double.MAX_VALUE)),
        Arguments.of(String.valueOf(Double.MIN_VALUE), BigDecimal.valueOf(-Double.MAX_VALUE),
            BigDecimal.valueOf(Double.MAX_VALUE)),
        Arguments.of("2000.0", new BigDecimal("-2000"), new BigDecimal("2000")));
  }
  
  @ParameterizedTest
  @MethodSource("checkDoubleLiteralArgs")
  public final void checkDoubleLiteral(String expression, BigDecimal min, BigDecimal max)
      throws IOException {
    Optional<ASTDoubleLiteral> astex =
        TestMCJavaLiteralsMill.parser().parse_StringDoubleLiteral(expression);
    assertTrue(astex.isPresent());

    MCJavaLiteralsCoCoChecker checker = new MCJavaLiteralsCoCoChecker();
    checker.addCoCo(new DoubleLiteralRangeCoCo(min, max));

    checker.checkAll((ASTMCJavaLiteralsNode) astex.get());
  }

  static Stream<Arguments> checkFloatLiteralArgs() {
    return Stream.of(
        Arguments.of("3.4028234663852886E+38f", BigDecimal.valueOf(-Float.MAX_VALUE),
            BigDecimal.valueOf(Float.MAX_VALUE)),
        Arguments.of("1.0f", BigDecimal.valueOf(-Float.MAX_VALUE),
            BigDecimal.valueOf(Float.MAX_VALUE)),
        Arguments.of("123.0f", BigDecimal.valueOf(-Float.MAX_VALUE),
            BigDecimal.valueOf(Float.MAX_VALUE)),
        Arguments.of(Float.MIN_VALUE + "f", BigDecimal.valueOf(-Float.MAX_VALUE),
            BigDecimal.valueOf(Float.MAX_VALUE)),
        Arguments.of("2000.0f", new BigDecimal("-2000"), new BigDecimal("2000")));
  }
  
  @ParameterizedTest
  @MethodSource("checkFloatLiteralArgs")
  public final void checkFloatLiteral(String expression, BigDecimal min, BigDecimal max)
      throws IOException {
    Optional<ASTFloatLiteral> astex =
        TestMCJavaLiteralsMill.parser().parse_StringFloatLiteral(expression);
    assertTrue(astex.isPresent());

    MCJavaLiteralsCoCoChecker checker = new MCJavaLiteralsCoCoChecker();
    checker.addCoCo(new FloatLiteralRangeCoCo(min, max));

    checker.checkAll((ASTMCJavaLiteralsNode) astex.get());
  }

  static Stream<Arguments> checkErrorIntLiteralArgs() {
    return Stream.of(
        Arguments.of("0x80000000", BigInteger.valueOf(Integer.MIN_VALUE),
            BigInteger.valueOf(Integer.MAX_VALUE), "0xA0216"),
        Arguments.of("2001", new BigInteger("-2000"), new BigInteger("2000"), "0xA0216"));
  }
  
  @ParameterizedTest
  @MethodSource("checkErrorIntLiteralArgs")
  public final void checkErrorIntLiteral(String expression, BigInteger min, BigInteger max,
      String expectedError) throws IOException {
    Optional<ASTIntLiteral> astex =
        TestMCJavaLiteralsMill.parser().parse_StringIntLiteral(expression);
    assertTrue(astex.isPresent());

    MCJavaLiteralsCoCoChecker checker = new MCJavaLiteralsCoCoChecker();
    checker.addCoCo(new IntLiteralRangeCoCo(min, max));

    checker.checkAll((ASTMCJavaLiteralsNode) astex.get());
    
    Log.getFindings().remove(MCAssertions.assertHasFindingStartingWith(expectedError));
  }

  static Stream<Arguments> checkErrorLongLiteralArgs() {
    return Stream.of(
        Arguments.of("0x8000000000000000L", BigInteger.valueOf(Long.MIN_VALUE),
            BigInteger.valueOf(Long.MAX_VALUE), "0xA0217"),
        Arguments.of("2001L", new BigInteger("-2000"), new BigInteger("2000"), "0xA0217"));
  }
  
  @ParameterizedTest
  @MethodSource("checkErrorLongLiteralArgs")
  public final void checkErrorLongLiteral(String expression, BigInteger min, BigInteger max,
      String expectedError) throws IOException {
    Optional<ASTLongLiteral> astex =
        TestMCJavaLiteralsMill.parser().parse_StringLongLiteral(expression);
    assertTrue(astex.isPresent());

    MCJavaLiteralsCoCoChecker checker = new MCJavaLiteralsCoCoChecker();
    checker.addCoCo(new LongLiteralRangeCoCo(min, max));

    checker.checkAll((ASTMCJavaLiteralsNode) astex.get());
    
    Log.getFindings().remove(MCAssertions.assertHasFindingStartingWith(expectedError));
  }

  static Stream<Arguments> checkErrorDoubleLiteralArgs() {
    return Stream.of(
        Arguments.of("1.7976931348623157e+309", BigDecimal.valueOf(-Double.MAX_VALUE),
            BigDecimal.valueOf(Double.MAX_VALUE), "0xA0218"),
        Arguments.of("2001.0", new BigDecimal("-2000"), new BigDecimal("2000"), "0xA0218"));
  }
  
  @ParameterizedTest
  @MethodSource("checkErrorDoubleLiteralArgs")
  public final void checkErrorDoubleLiteral(String expression, BigDecimal min, BigDecimal max,
      String expectedError) throws IOException {
    Optional<ASTDoubleLiteral> astex =
        TestMCJavaLiteralsMill.parser().parse_StringDoubleLiteral(expression);
    assertTrue(astex.isPresent());

    MCJavaLiteralsCoCoChecker checker = new MCJavaLiteralsCoCoChecker();
    checker.addCoCo(new DoubleLiteralRangeCoCo(min, max));

    checker.checkAll((ASTMCJavaLiteralsNode) astex.get());
    
    Log.getFindings().remove(MCAssertions.assertHasFindingStartingWith(expectedError));
  }

  static Stream<Arguments> checkErrorFloatLiteralArgs() {
    return Stream.of(
        Arguments.of("3.4028235e+39f", BigDecimal.valueOf(-Float.MAX_VALUE),
            BigDecimal.valueOf(Float.MAX_VALUE), "0xA0219"),
        Arguments.of("2001.0f", new BigDecimal("-2000"), new BigDecimal("2000"), "0xA0219"));
  }
  
  @ParameterizedTest
  @MethodSource("checkErrorFloatLiteralArgs")
  public final void checkErrorFloatLiteral(String expression, BigDecimal min, BigDecimal max,
      String expectedError) throws IOException {
    Optional<ASTFloatLiteral> astex =
        TestMCJavaLiteralsMill.parser().parse_StringFloatLiteral(expression);
    assertTrue(astex.isPresent());

    MCJavaLiteralsCoCoChecker checker = new MCJavaLiteralsCoCoChecker();
    checker.addCoCo(new FloatLiteralRangeCoCo(min, max));

    checker.checkAll((ASTMCJavaLiteralsNode) astex.get());
    
    Log.getFindings().remove(MCAssertions.assertHasFindingStartingWith(expectedError));
  }
}
