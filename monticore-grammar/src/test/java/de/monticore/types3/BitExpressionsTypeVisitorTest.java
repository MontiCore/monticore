package de.monticore.types3;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class BitExpressionsTypeVisitorTest extends AbstractTypeVisitorTest {

  @BeforeEach
  public void init() {
    setupValues();
  }

  /**
   * test LeftShiftExpression
   */
  @Test
  public void deriveFromLeftShiftExpressionTest() throws IOException {
    //example with int - int
    checkExpr("3<<5", "int");

    //example with char - long
    checkExpr("\'a\'<<4L", "int");
  }

  @Test
  public void testInvalidLeftShiftExpression() throws IOException {
    //only possible with integral types
    checkErrorExpr("3<<4.5", "0xC0201");
  }

  /**
   * test rightShiftExpression
   */
  @Test
  public void deriveFromRightShiftExpression() throws IOException {
    //example with int - int
    checkExpr("3>>5", "int");

    //example with long - long
    checkExpr("6L>>4L", "long");
  }

  @Test
  public void testInvalidRightShiftExpression() throws IOException {
    //only possible with integral types
    checkErrorExpr("3>>4.5", "0xC0201");
  }

  /**
   * test LogicalRightExpression
   */
  @Test
  public void deriveFromLogicalRightExpression() throws IOException {
    //example with int - int
    checkExpr("3>>>5", "int");

    //example with int - long
    checkExpr("12>>>4L", "int");
  }

  @Test
  public void testInvalidLogicalRightExpression() throws IOException {
    //only possible with integral types
    checkErrorExpr("3>>>4.5", "0xC0201");
  }

  /**
   * test BinaryOrOpExpression
   */
  @Test
  public void deriveFromBinaryOrOpExpression() throws IOException {
    //example with int - int
    checkExpr("3|5", "int");

    //example with char - long
    checkExpr("\'a\'|4L", "long");
  }

  @Test
  public void testInvalidBinaryOrOpExpression() throws IOException {
    //only possible with integral types
    checkErrorExpr("3|4.5", "0xC0203");
  }

  /**
   * test BinaryAndExpression
   */
  @Test
  public void deriveFromBinaryAndExpression() throws IOException {
    //example with int - int
    checkExpr("3&5", "int");

    //example with long - long
    checkExpr("4L&12L", "long");
  }

  @Test
  public void testInvalidBinaryAndExpression() throws IOException {
    //only possible with integral types
    checkErrorExpr("3&4.5", "0xC0203");
  }

  /**
   * test BinaryXorExpression
   */
  @Test
  public void deriveFromBinaryXorExpression() throws IOException {
    //example with int - int
    checkExpr("3^5", "int");

    //example with boolean - boolean
    checkExpr("true^false", "boolean");
  }

  @Test
  public void testInvalidBinaryXorExpression() throws IOException {
    //only possible with integral types
    checkErrorExpr("3^4.5", "0xC0203");
  }
}

