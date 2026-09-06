/* (c) https://github.com/MontiCore/monticore */
package de.monticore.mccommonliterals;

import de.monticore.literals.mccommonliterals._ast.ASTSignedLiteral;
import de.monticore.literals.mccommonliterals._cocos.MCCommonLiteralsCoCoChecker;
import de.monticore.literals.mccommonliterals.cocos.*;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.literals.testmccommonliterals.TestMCCommonLiteralsMill;
import de.monticore.runtime.junit.MCAssertions;
import de.monticore.runtime.junit.TestWithMCLanguage;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestWithMCLanguage(TestMCCommonLiteralsMill.class)
public class RangeCoCosTest {
  
  static Stream<Arguments> checkLiteralRangeArgs() {
    return Stream.of(
        // Integer
        Arguments.of("2000", new BigInteger("-2000"), new BigInteger("2000")),
        
        // Long
        Arguments.of("2000L", new BigInteger("-2000"), new BigInteger("2000")),
        
        // Double
        Arguments.of("2000.0", new BigInteger("-2000"), new BigInteger("2000")),
        
        // Float
        Arguments.of("2000.0f", new BigInteger("-2000"), new BigInteger("2000"))
    );
  }
  
  @ParameterizedTest
  @MethodSource("checkLiteralRangeArgs")
  public final void checkLiteral(String expression, BigInteger min, BigInteger max) throws IOException {
    Optional<ASTLiteral> astex = TestMCCommonLiteralsMill.parser().parse_StringLiteral(expression);
    assertTrue(astex.isPresent());

    MCCommonLiteralsCoCoChecker checker = new MCCommonLiteralsCoCoChecker();
    checker.addCoCo(new BasicFloatLiteralRangeCoCo(new BigDecimal(min), new BigDecimal(max)));
    checker.addCoCo(new BasicDoubleLiteralRangeCoCo(new BigDecimal(min), new BigDecimal(max)));
    checker.addCoCo(new BasicLongLiteralRangeCoCo(min, max));
    checker.addCoCo(new NatLiteralRangeCoCo(min, max));

    checker.checkAll(astex.get());
  }
  
  static Stream<Arguments> checkLiteralArgs() {
    return Stream.of(
        // Integer
        Arguments.of(String.valueOf(Integer.MAX_VALUE)),
        Arguments.of("1"),
        Arguments.of("123"),
        
        // Long
        Arguments.of(Long.MAX_VALUE + "L"),
        Arguments.of("1L"),
        Arguments.of("123L"),
        
        // Double
        Arguments.of("1.0"),
        Arguments.of("123.0"),
        
        // Float
        Arguments.of("1.0f"),
        Arguments.of("123.0f")
    );
  }

  @ParameterizedTest
  @MethodSource("checkLiteralArgs")
  public void checkLiteral(String expression) throws IOException {
    Optional<ASTLiteral> astex = TestMCCommonLiteralsMill.parser().parse_StringLiteral(expression);
    assertTrue(astex.isPresent());

    MCCommonLiteralsCoCoChecker checker = new MCCommonLiteralsCoCoChecker();
    checker.addCoCo(new BasicFloatLiteralRangeCoCo());
    checker.addCoCo(new BasicDoubleLiteralRangeCoCo());
    checker.addCoCo(new BasicLongLiteralRangeCoCo());
    checker.addCoCo(new NatLiteralRangeCoCo());

    checker.checkAll(astex.get());
  }
  
  static Stream<Arguments> checkSignedLiteralArgs() {
    return Stream.of(
        // Integer
        Arguments.of(String.valueOf(Integer.MIN_VALUE)),
        Arguments.of("-1"),
        Arguments.of("-123"),
        
        // Long
        Arguments.of(Long.MIN_VALUE + "L"),
        Arguments.of("-1L"),
        Arguments.of("-123L"),
        
        // Double
        Arguments.of("-1.0"),
        Arguments.of("-123.0"),
        
        // Float
        Arguments.of("-1.0f"),
        Arguments.of("-123.0f")
    );
  }
  
  @ParameterizedTest
  @MethodSource("checkSignedLiteralArgs")
  protected final void checkSignedLiteral(String expression) throws IOException {
    Optional<ASTSignedLiteral> astex = TestMCCommonLiteralsMill.parser().parse_StringSignedLiteral(expression);
    assertTrue(astex.isPresent());

    MCCommonLiteralsCoCoChecker checker = new MCCommonLiteralsCoCoChecker();
    checker.addCoCo(new SignedBasicFloatLiteralRangeCoCo());
    checker.addCoCo(new SignedBasicDoubleLiteralRangeCoCo());
    checker.addCoCo(new SignedBasicLongLiteralRangeCoCo());
    checker.addCoCo(new SignedNatLiteralRangeCoCo());

    checker.checkAll(astex.get());
  }
  
  static Stream<Arguments> checkErrorLiteralRangeArgs() {
    return Stream.of(
        // Integer
        Arguments.of("2001", new BigInteger("-2000"), new BigInteger("2000"), "0xA0208"),
        
        // Long
        Arguments.of("2001L", new BigInteger("-2000"), new BigInteger("2000"), "0xA0209"),
        
        // Double
        Arguments.of("2001.0", new BigInteger("-2000"), new BigInteger("2000"), "0xA0212"),
        
        // Float
        Arguments.of("2001.0f", new BigInteger("-2000"), new BigInteger("2000"), "0xA0213")
    );
  }
  
  @ParameterizedTest
  @MethodSource("checkErrorLiteralRangeArgs")
  public final void checkErrorLiteral(String expression, BigInteger min, BigInteger max, String expectedError) throws IOException {
    Optional<ASTLiteral> astex = TestMCCommonLiteralsMill.parser().parse_StringLiteral(expression);
    assertTrue(astex.isPresent());

    MCCommonLiteralsCoCoChecker checker = new MCCommonLiteralsCoCoChecker();
    checker.addCoCo(new BasicFloatLiteralRangeCoCo(new BigDecimal(min), new BigDecimal(max)));
    checker.addCoCo(new BasicDoubleLiteralRangeCoCo(new BigDecimal(min), new BigDecimal(max)));
    checker.addCoCo(new BasicLongLiteralRangeCoCo(min, max));
    checker.addCoCo(new NatLiteralRangeCoCo(min, max));

    checker.checkAll(astex.get());
    
    MCAssertions.assertHasFindingStartingWith(expectedError);
  }
  
  static Stream<Arguments> checkErrorLiteralArgs() {
    return Stream.of(
        // Integer
        Arguments.of("2147483648", "0xA0208"),
        
        // Long
        Arguments.of("9223372036854775808L", "0xA0209")
    );
  }
  
  @ParameterizedTest
  @MethodSource("checkErrorLiteralArgs")
  public final void checkErrorLiteral(String expression, String expectedError) throws IOException {
    Optional<ASTLiteral> astex = TestMCCommonLiteralsMill.parser().parse_StringLiteral(expression);
    assertTrue(astex.isPresent());

    MCCommonLiteralsCoCoChecker checker = new MCCommonLiteralsCoCoChecker();
    checker.addCoCo(new BasicFloatLiteralRangeCoCo());
    checker.addCoCo(new BasicDoubleLiteralRangeCoCo());
    checker.addCoCo(new BasicLongLiteralRangeCoCo());
    checker.addCoCo(new NatLiteralRangeCoCo());

    checker.checkAll(astex.get());
    
    MCAssertions.assertHasFindingStartingWith(expectedError);
  }
  
  static Stream<Arguments> checkErrorSignedLiteralRangeArgs() {
    return Stream.of(
        // Integer
        Arguments.of("-2001", new BigInteger("-2000"), new BigInteger("2000"), "0xA0210"),
        
        // Long
        Arguments.of("-2001L", new BigInteger("-2000"), new BigInteger("2000"), "0xA0211"),
        
        // Double
        Arguments.of("-2001.0", new BigInteger("-2000"), new BigInteger("2000"), "0xA0214"),
        
        // Float
        Arguments.of("-2001.0f", new BigInteger("-2000"), new BigInteger("2000"), "0xA0215")
    );
  }
  
  @ParameterizedTest
  @MethodSource("checkErrorSignedLiteralRangeArgs")
  public final void checkErrorSignedLiteral(String expression, BigInteger min, BigInteger max, String expectedError) throws IOException {
    Optional<ASTSignedLiteral> astex = TestMCCommonLiteralsMill.parser().parse_StringSignedLiteral(expression);
    assertTrue(astex.isPresent());

    MCCommonLiteralsCoCoChecker checker = new MCCommonLiteralsCoCoChecker();
    checker.addCoCo(new SignedBasicFloatLiteralRangeCoCo(new BigDecimal(min), new BigDecimal(max)));
    checker.addCoCo(new SignedBasicDoubleLiteralRangeCoCo(new BigDecimal(min), new BigDecimal(max)));
    checker.addCoCo(new SignedBasicLongLiteralRangeCoCo(min, max));
    checker.addCoCo(new SignedNatLiteralRangeCoCo(min, max));

    checker.checkAll(astex.get());
    
    MCAssertions.assertHasFindingStartingWith(expectedError);
  }
  
  static Stream<Arguments> checkErrorSignedLiteralArgs() {
    return Stream.of(
        // Integer
        Arguments.of("-2147483649", "0xA0210"),
        
        // Long
        Arguments.of("-9223372036854775809L", "0xA0211")
    );
  }
  
  @ParameterizedTest
  @MethodSource("checkErrorSignedLiteralArgs")
  protected final void checkErrorSignedLiteral(String expression, String expectedError) throws IOException {
    Optional<ASTSignedLiteral> astex = TestMCCommonLiteralsMill.parser().parse_StringSignedLiteral(expression);
    assertTrue(astex.isPresent());

    MCCommonLiteralsCoCoChecker checker = new MCCommonLiteralsCoCoChecker();
    checker.addCoCo(new SignedBasicFloatLiteralRangeCoCo());
    checker.addCoCo(new SignedBasicDoubleLiteralRangeCoCo());
    checker.addCoCo(new SignedBasicLongLiteralRangeCoCo());
    checker.addCoCo(new SignedNatLiteralRangeCoCo());

    checker.checkAll(astex.get());
    
    MCAssertions.assertHasFindingStartingWith(expectedError);
  }

}
