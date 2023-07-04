/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types3;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class AssignmentExpressionTypeVisitorTest extends AbstractTypeVisitorTest {

  @Before
  public void init() {
    setupValues();
  }

  @Test
  public void deriveFromIncSuffixExpression() throws IOException {
    //example with byte
    checkExpr("varbyte++", "byte");

    //example with short
    checkExpr("varshort++", "short");

    //example with char
    checkExpr("varchar++", "char");

    //example with int
    checkExpr("varint++", "int");

    //example with float
    checkExpr("varfloat++", "float");

    //example with char
    checkExpr("varchar++", "char");
  }

  @Test
  public void testInvalidIncSuffixExpression() throws IOException {
    //not applicable to Strings
    checkErrorExpr("varString++", "0xA0184");

    //not applicable to boolean
    checkErrorExpr("varboolean++", "0xA0184");
  }

  @Test
  public void deriveFromDecSuffixExpression() throws IOException {
    //example with byte
    checkExpr("varbyte--", "byte");

    //example with short
    checkExpr("varshort--", "short");

    //example with char
    checkExpr("varchar--", "char");

    //example with int
    checkExpr("varint--", "int");

    //example with float
    checkExpr("varfloat++", "float");

    //example with double
    checkExpr("vardouble--", "double");
  }

  @Test
  public void testInvalidDecSuffixExpression() throws IOException {
    //not applicable to Strings
    checkErrorExpr("varString--", "0xA0184");
  }

  @Test
  public void deriveFromIncPrefixExpression() throws IOException {
    //example with byte
    checkExpr("++varbyte", "byte");

    //example with short
    checkExpr("++varshort", "short");

    //example with char
    checkExpr("++varchar", "char");

    //example with int
    checkExpr("++varint", "int");

    //example with long
    checkExpr("++varlong", "long");
  }

  @Test
  public void testInvalidIncPrefixExpression() throws IOException {
    //not applicable to Strings
    checkErrorExpr("++varString", "0xA0184");
  }

  @Test
  public void deriveFromDecPrefixExpression() throws IOException {
    //example with byte
    checkExpr("--varbyte", "byte");

    //example with short
    checkExpr("--varshort", "short");

    //example with char
    checkExpr("--varchar", "char");

    //example with int
    checkExpr("--varint", "int");

    //example with float
    checkExpr("--varfloat", "float");
  }

  @Test
  public void testInvalidDecPrefixExpression() throws IOException {
    //not applicable to Strings
    checkErrorExpr("--varString", "0xA0184");
  }

  @Test
  public void deriveFromMinusPrefixExpression() throws IOException {
    //example with int
    checkExpr("-5", "int");

    //example with double
    checkExpr("-15.7", "double");
  }

  @Test
  public void testInvalidMinusPrefixExpression() throws IOException {
    //only possible with numeric types
    checkErrorExpr("-\"Hello\"", "0xA017D");
  }

  @Test
  public void deriveFromPlusPrefixExpression() throws IOException {
    //example with int
    checkExpr("+34", "int");

    //example with long
    checkExpr("+4L", "long");
  }

  @Test
  public void testInvalidPlusPrefixExpression() throws IOException {
    //only possible with numeric types
    checkErrorExpr("+\"Hello\"", "0xA017D");
  }

  @Test
  public void deriveFromPlusAssignmentExpression() throws IOException {
    //example with int - int
    checkExpr("varint+=7", "int");
    //example with long - double
    checkExpr("varlong+=5.6", "long");
    //example with String - Person
    checkExpr("varString+=person1", "String");
  }

  @Test
  public void testInvalidPlusAssignmentExpression() throws IOException {
    //not possible because int = int + (int) String returns a casting error
    checkErrorExpr("varint+=\"Hello\"", "0xA0178");
  }

  @Test
  public void deriveFromMinusAssignmentExpression() throws IOException {
    //example with int - int
    checkExpr("varint-=9", "int");
    //example with char - float
    checkExpr("varchar-=4.5f", "char");
  }

  @Test
  public void testInvalidMinusAssignmentExpression() throws IOException {
    //not possible because int = int - (int) String returns a casting error
    checkErrorExpr("varint-=\"Hello\"", "0xA0178");
  }

  @Test
  public void deriveFromMultAssignmentExpression() throws IOException {
    //example with int - int
    checkExpr("varint*=9", "int");
    //example with double - int
    checkExpr("vardouble*=5", "double");
  }

  @Test
  public void testInvalidMultAssignmentExpression() throws IOException {
    //not possible because int = int * (int) String returns a casting error
    checkErrorExpr("varint*=\"Hello\"", "0xA0178");
  }

  @Test
  public void deriveFromDivideAssignmentExpression() throws IOException {
    //example with int - int
    checkExpr("varint/=9", "int");
    //example with float - long
    checkExpr("varfloat/=4L", "float");
  }

  @Test
  public void testInvalidDivideAssignmentExpression() throws IOException {
    //not possible because int = int / (int) String returns a casting error
    checkErrorExpr("varint/=\"Hello\"", "0xA0178");
  }

  @Test
  public void deriveFromModuloAssignmentExpression() throws IOException {
    //example with int - int
    checkExpr("varint%=9", "int");
    //example with int - float
    checkExpr("varint%=9.8f", "int");
  }

  @Test
  public void testInvalidModuloAssignmentExpression() throws IOException {
    //not possible because int = int % (int) String returns a casting error
    checkErrorExpr("varint%=\"Hello\"", "0xA0178");
  }

  @Test
  public void deriveFromAndAssignmentExpression() throws IOException {
    //example with int - int
    checkExpr("varint&=9", "int");
    //example with boolean - boolean
    checkExpr("varboolean&=false", "boolean");
    //example with char - int
    checkExpr("varchar&=4", "char");
  }

  @Test
  public void testInvalidAndAssignmentExpression() throws IOException {
    //not possible because int = int & (int) String returns a casting error
    checkErrorExpr("varint&=\"Hello\"", "0xA0176");
  }

  @Test
  public void deriveFromOrAssignmentExpression() throws IOException {
    //example with int - int
    checkExpr("varint|=9", "int");
    //example with boolean - boolean
    checkExpr("varboolean|=true", "boolean");
  }

  @Test
  public void testInvalidOrAssignmentExpression() throws IOException {
    //not possible because int = int | (int) String returns a casting error
    checkErrorExpr("varint|=\"Hello\"", "0xA0176");
  }

  @Test
  public void deriveFromBinaryXorAssignmentExpression() throws IOException {
    //example with int - int
    checkExpr("varint^=9", "int");
    //example with boolean - boolean
    checkExpr("varboolean^=false", "boolean");
  }

  @Test
  public void testInvalidBinaryXorAssignmentExpression() throws IOException {
    //not possible because int = int ^ (int) String returns a casting error
    checkErrorExpr("varint^=\"Hello\"", "0xA0176");
  }

  @Test
  public void deriveFromDoubleLeftAssignmentExpression() throws IOException {
    //example with int - int
    checkExpr("varint<<=9", "int");
    //example with int - char
    checkExpr("varint<<='c'", "int");
  }

  @Test
  public void testInvalidDoubleLeftAssignmentExpression() throws IOException {
    //not possible because int = int << (int) String returns a casting error
    checkErrorExpr("varint<<=\"Hello\"", "0xA0177");
  }

  @Test
  public void deriveFromDoubleRightAssignmentExpression() throws IOException {
    //example with int - int
    checkExpr("varint>>=9", "int");
    //example with char - int
    checkExpr("varchar>>=12", "char");
  }

  @Test
  public void testInvalidDoubleRightAssignmentExpression() throws IOException {
    //not possible because int = int >> (int) String returns a casting error
    checkErrorExpr("varint>>=\"Hello\"", "0xA0177");
  }

  @Test
  public void deriveFromLogicalRightAssignmentExpression() throws IOException {
    //example with int - int
    checkExpr("varint>>>=9", "int");
    //example with char - char
    checkExpr("varchar>>>='3'", "char");
  }

  @Test
  public void testInvalidLogicalRightAssignmentExpression() throws IOException {
    //not possible because int = int >>> (int) String returns a casting error
    checkErrorExpr("varint>>>=\"Hello\"", "0xA0177");
  }

  @Test
  public void deriveFromRegularAssignmentExpression() throws IOException {
    //example with int - int
    checkExpr("varint=9", "int");
    //example with double - int
    checkExpr("vardouble=12", "double");
    //example with person - student
    checkExpr("person1 = student2", "Person");
    //example with person - firstsemesterstudent
    checkExpr("person2 = csStudent1", "Person");
    //example with Boolean - boolean
    checkExpr("varBoolean = varboolean", "java.lang.Boolean");
    //example with boolean - Boolean
    checkExpr("varboolean = varBoolean", "boolean");
  }

  @Test
  public void testInvalidRegularAssignmentExpression() throws IOException {
    //not possible because int = (int) String returns a casting error
    checkErrorExpr("varint=\"Hello\"", "0xA0179");
  }

}
