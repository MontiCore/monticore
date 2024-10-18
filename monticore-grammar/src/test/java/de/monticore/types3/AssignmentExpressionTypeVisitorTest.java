/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types3;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class AssignmentExpressionTypeVisitorTest extends AbstractTypeVisitorTest {

  @BeforeEach
  public void init() {
    setupValues();
  }

  @Test
  public void deriveFromIncSuffixExpression() throws IOException {
    checkExpr("varchar++", "char"); // ++ applicable to char
    checkExpr("varbyte++", "byte"); // ++ applicable to byte
    checkExpr("varshort++", "short"); // ++ applicable to short
    checkExpr("varint++", "int"); // ++ applicable to int
    checkExpr("varlong++", "long"); // ++ applicable to long
    checkExpr("varfloat++", "float"); // ++ applicable to float
    checkExpr("vardouble++", "double"); // ++ applicable to double
    checkExpr("varchar = varchar++", "char"); // ++ applicable to char, result is char
    checkExpr("varbyte = varbyte++", "byte"); // ++ applicable to byte, result is byte
    checkExpr("varshort = varshort++", "short"); // ++ applicable to short, result is short
    checkExpr("varint = varint++", "int"); // ++ applicable to int, result is int
    checkExpr("varlong = varlong++", "long"); // ++ applicable to long, result is long
    checkExpr("varfloat = varfloat++", "float"); // ++ applicable to float, result is float
    checkExpr("vardouble = vardouble++", "double"); // ++ applicable to double, result is double
  }

  @Test
  public void testInvalidIncSuffixExpression() throws IOException {
    checkErrorExpr("varboolean++", "0xA0184"); // ++ not applicable to boolean
    checkErrorExpr("varString++", "0xA0184"); // not applicable to String
    checkErrorExpr("varintMetre++", "0xA0184"); // debatable
  }

  @Test
  public void deriveFromDecSuffixExpression() throws IOException {
    checkExpr("varchar--", "char"); // -- applicable to char
    checkExpr("varbyte--", "byte"); // -- applicable to byte
    checkExpr("varshort--", "short"); // -- applicable to short
    checkExpr("varint--", "int"); // -- applicable to int
    checkExpr("varlong--", "long"); // -- applicable to long
    checkExpr("varfloat--", "float"); // -- applicable to float
    checkExpr("vardouble--", "double"); // -- applicable to double

    checkExpr("varchar = varchar--", "char"); // -- applicable to char, result is char
    checkExpr("varbyte = varbyte--", "byte"); // -- applicable to byte, result is byte
    checkExpr("varshort = varshort--", "short"); // -- applicable to short, result is short
    checkExpr("varint = varint--", "int"); // -- applicable to int, result is int
    checkExpr("varlong = varlong--", "long"); // -- applicable to long, result is long
    checkExpr("varfloat = varfloat--", "float"); // -- applicable to float, result is float
    checkExpr("vardouble = vardouble--", "double"); // -- applicable to double, result is double
  }

  @Test
  public void testInvalidDecSuffixExpression() throws IOException {
    checkErrorExpr("varboolean--", "0xA0184"); // -- not applicable to boolean
    checkErrorExpr("varString--", "0xA0184"); //not applicable to Strings
    checkErrorExpr("varintMetre--", "0xA0184"); // debatable
  }

  @Test
  public void deriveFromIncPrefixExpression() throws IOException {
    checkExpr("++varchar", "char"); // ++ applicable to char
    checkExpr("++varbyte", "byte"); // ++ applicable to byte
    checkExpr("++varshort", "short"); // ++ applicable to short
    checkExpr("++varint", "int"); // ++ applicable to int
    checkExpr("++varlong", "long"); // ++ applicable to long
    checkExpr("++varfloat", "float"); // ++ applicable to float
    checkExpr("++vardouble", "double"); // ++ applicable to double

    checkExpr("varchar = ++varchar", "char"); // ++ applicable to char, result is char
    checkExpr("varbyte = ++varbyte", "byte"); // ++ applicable to byte, result is byte
    checkExpr("varshort = ++varshort", "short"); // ++ applicable to short, result is short
    checkExpr("varint = ++varint", "int"); // ++ applicable to int, result is int
    checkExpr("varlong = ++varlong", "long"); // ++ applicable to long, result is long
    checkExpr("varfloat = ++varfloat", "float"); // ++ applicable to float, result is float
    checkExpr("vardouble = ++vardouble", "double"); // ++ applicable to double, result is double
  }

  @Test
  public void testInvalidIncPrefixExpression() throws IOException {
    checkErrorExpr("++varboolean", "0xA0184"); // ++ not applicable to boolean
    checkErrorExpr("++varString", "0xA0184"); //not applicable to Strings
    checkErrorExpr("++varintMetre", "0xA0184"); // debatable
  }

  @Test
  public void deriveFromDecPrefixExpression() throws IOException {
    checkExpr("--varchar", "char"); // -- applicable to char
    checkExpr("--varbyte", "byte"); // -- applicable to byte
    checkExpr("--varshort", "short"); // -- applicable to short
    checkExpr("--varint", "int"); // -- applicable to int
    checkExpr("--varlong", "long"); // -- applicable to long
    checkExpr("--varfloat", "float"); // -- applicable to float
    checkExpr("--vardouble", "double"); // -- applicable to double

    checkExpr("varchar = --varchar", "char"); // -- applicable to char, result is char
    checkExpr("varbyte = --varbyte", "byte"); // -- applicable to byte, result is byte
    checkExpr("varshort = --varshort", "short"); // -- applicable to short, result is short
    checkExpr("varint = --varint", "int"); // -- applicable to int, result is int
    checkExpr("varlong = --varlong", "long"); // -- applicable to long, result is long
    checkExpr("varfloat = --varfloat", "float"); // -- applicable to float, result is float
    checkExpr("vardouble = --vardouble", "double"); // -- applicable to double, result is double
  }

  @Test
  public void testInvalidDecPrefixExpression() throws IOException {
    checkErrorExpr("--varboolean", "0xA0184"); // -- not applicable to boolean
    checkErrorExpr("--varString", "0xA0184"); // not applicable to Strings
    checkErrorExpr("--varintMetre", "0xA0184"); // debatable
  }

  @Test
  public void deriveFromMinusPrefixExpression() throws IOException {
    //example with int
    checkExpr("-5", "int");

    //example with double
    checkExpr("-15.7", "double");

    checkExpr("-1m", "[m]<int>");
  }

  @Test
  public void testInvalidMinusPrefixExpression() throws IOException {
    checkErrorExpr("-aBoolean", "0xFD118"); // - not applicable to boolean
    checkErrorExpr("-\"Hello\"", "0xA017D"); //only possible with numeric types
  }

  @Test
  public void deriveFromPlusPrefixExpression() throws IOException {
    //example with int
    checkExpr("+34", "int");

    //example with long
    checkExpr("+4L", "long");

    checkExpr("+1m", "[m]<int>");
  }

  @Test
  public void testInvalidPlusPrefixExpression() throws IOException {
    //only possible with numeric types
    checkErrorExpr("+\"Hello\"", "0xA017D");
  }

  @Test
  public void deriveFromPlusAssignmentExpression() throws IOException {
    checkExpr("varchar += varchar", "char"); // += applicable to char, char
    checkExpr("varchar += varbyte", "char"); // += applicable to char, byte
    checkExpr("varchar += varshort", "char"); // += applicable to char, short
    checkExpr("varchar += varint", "char"); // += applicable to char, int
    checkExpr("varchar += varfloat", "char"); // += applicable to char, float
    checkExpr("varchar += vardouble", "char"); // += applicable to char, double
    checkExpr("varshort += varchar", "short"); // += applicable to short, char
    checkExpr("varshort += varbyte", "short"); // += applicable to short, byte
    checkExpr("varshort += varshort", "short"); // += applicable to short, short
    checkExpr("varshort += varint", "short"); // += applicable to short, int
    checkExpr("varshort += varlong", "short"); // += applicable to short, long
    checkExpr("varshort += varfloat", "short"); // += applicable to short, float
    checkExpr("varshort += vardouble", "short"); // += applicable to short, double
    checkExpr("varint += varchar", "int"); // += applicable to int, char
    checkExpr("varint += varbyte", "int"); // += applicable to int, byte
    checkExpr("varint += varshort", "int"); // += applicable to int, short
    checkExpr("varint += varint", "int"); // += applicable to int, int
    checkExpr("varint += varlong", "int"); // += applicable to int, long
    checkExpr("varint += varfloat", "int"); // += applicable to int, float
    checkExpr("varint += vardouble", "int"); // += applicable to int, double
    checkExpr("varfloat += varchar", "float"); // += applicable to float, char
    checkExpr("varfloat += varbyte", "float"); // += applicable to float, byte
    checkExpr("varfloat += varshort", "float"); // += applicable to float, short
    checkExpr("varfloat += varint", "float"); // += applicable to float, int
    checkExpr("varfloat += varlong", "float"); // += applicable to float, long
    checkExpr("varfloat += varfloat", "float"); // += applicable to float, float
    checkExpr("varfloat += vardouble", "float"); // += applicable to float, double
    checkExpr("vardouble += varchar", "double"); // += applicable to double, char
    checkExpr("vardouble += varbyte", "double"); // += applicable to double, byte
    checkExpr("vardouble += varshort", "double"); // += applicable to double, short
    checkExpr("vardouble += varint", "double"); // += applicable to double, int
    checkExpr("vardouble += varlong", "double"); // += applicable to double, long
    checkExpr("vardouble += varfloat", "double"); // += applicable to double, float
    checkExpr("vardouble += vardouble", "double"); // += applicable to double, double
    checkExpr("varintMetre += varintMetre", "[m]<int>");
    checkExpr("varString+=person1", "String"); // example with String - Person
  }

  @Test
  public void testInvalidPlusAssignmentExpression() throws IOException {
    checkErrorExpr("varboolean += varboolean", "0xA0178"); // += not applicable to boolean, boolean
    checkErrorExpr("varboolean += varchar", "0xA0178"); // += not applicable to boolean, char
    checkErrorExpr("varboolean += varbyte", "0xA0178"); // += not applicable to boolean, byte
    checkErrorExpr("varboolean += varshort", "0xA0178"); // += not applicable to boolean, short
    checkErrorExpr("varboolean += varint", "0xA0178"); // += not applicable to boolean, int
    checkErrorExpr("varboolean += varlong", "0xA0178"); // += not applicable to boolean, long
    checkErrorExpr("varboolean += varfloat", "0xA0178"); // += not applicable to boolean, float
    checkErrorExpr("varboolean += vardouble", "0xA0178"); // += not applicable to boolean, double
    checkErrorExpr("varchar += varboolean", "0xA0178"); // += not applicable to char, boolean
    checkErrorExpr("varbyte += varboolean", "0xA0178"); // += not applicable to byte, boolean
    checkErrorExpr("varshort += varboolean", "0xA0178"); // += not applicable to short, boolean
    checkErrorExpr("varint += varboolean", "0xA0178"); // += not applicable to int, boolean
    checkErrorExpr("varlong += varboolean", "0xA0178"); // += not applicable to long, boolean
    checkErrorExpr("varfloat += varboolean", "0xA0178"); // += not applicable to float, boolean
    checkErrorExpr("vardouble += varboolean", "0xA0178"); // += not applicable to double, boolean
    checkErrorExpr("varint+=\"Hello\"", "0xA0178"); // not possible because int = int + (int) String returns a casting error
    checkErrorExpr("varintMetre += 1s", "0xA0178");
    checkErrorExpr("varintMetre += 1", "0xA0178");
  }

  @Test
  public void deriveFromMinusAssignmentExpression() throws IOException {
    checkExpr("varchar -= varchar", "char"); // -= applicable to char, char
    checkExpr("varchar -= varbyte", "char"); // -= applicable to char, byte
    checkExpr("varchar -= varshort", "char"); // -= applicable to char, short
    checkExpr("varchar -= varint", "char"); // -= applicable to char, int
    checkExpr("varchar -= varlong", "char"); // -= applicable to char, long
    checkExpr("varchar -= varfloat", "char"); // -= applicable to char, float
    checkExpr("varchar -= vardouble", "char"); // -= applicable to char, double
    checkExpr("varshort -= varchar", "short"); // -= applicable to short, char
    checkExpr("varshort -= varbyte", "short"); // -= applicable to short, byte
    checkExpr("varshort -= varshort", "short"); // -= applicable to short, short
    checkExpr("varshort -= varint", "short"); // -= applicable to short, int
    checkExpr("varshort -= varlong", "short"); // -= applicable to short, long
    checkExpr("varshort -= varfloat", "short"); // -= applicable to short, float
    checkExpr("varshort -= vardouble", "short"); // -= applicable to short, double
    checkExpr("varint -= varchar", "int"); // -= applicable to int, char
    checkExpr("varint -= varbyte", "int"); // -= applicable to int, byte
    checkExpr("varint -= varshort", "int"); // -= applicable to int, short
    checkExpr("varint -= varint", "int"); // -= applicable to int, int
    checkExpr("varint -= varlong", "int"); // -= applicable to int, long
    checkExpr("varint -= varfloat", "int"); // -= applicable to int, float
    checkExpr("varint -= vardouble", "int"); // -= applicable to int, double
    checkExpr("varfloat -= varchar", "float"); // -= applicable to float, char
    checkExpr("varfloat -= varbyte", "float"); // -= applicable to float, byte
    checkExpr("varfloat -= varshort", "float"); // -= applicable to float, short
    checkExpr("varfloat -= varint", "float"); // -= applicable to float, int
    checkExpr("varfloat -= varlong", "float"); // -= applicable to float, long
    checkExpr("varfloat -= varfloat", "float"); // -= applicable to float, float
    checkExpr("varfloat -= vardouble", "float"); // -= applicable to float, double
    checkExpr("vardouble -= varchar", "double"); // -= applicable to double, char
    checkExpr("vardouble -= varbyte", "double"); // -= applicable to double, byte
    checkExpr("vardouble -= varshort", "double"); // -= applicable to double, short
    checkExpr("vardouble -= varint", "double"); // -= applicable to double, int
    checkExpr("vardouble -= varlong", "double"); // -= applicable to double, long
    checkExpr("vardouble -= varfloat", "double"); // -= applicable to double, float
    checkExpr("vardouble -= vardouble", "double"); // -= applicable to double, double
    checkExpr("varintMetre -= varintMetre", "[m]<int>");
  }

  @Test
  public void testInvalidMinusAssignmentExpression() throws IOException {
    checkErrorExpr("varBoolean -= varBoolean", "0xA0178"); // -= not applicable to boolean, boolean
    checkErrorExpr("varBoolean -= varchar", "0xA0178"); // -= not applicable to boolean, char
    checkErrorExpr("varBoolean -= varbyte", "0xA0178"); // -= not applicable to boolean, byte
    checkErrorExpr("varBoolean -= varshort", "0xA0178"); // -= not applicable to boolean, short
    checkErrorExpr("varBoolean -= varint", "0xA0178"); // -= not applicable to boolean, int
    checkErrorExpr("varBoolean -= varlong", "0xA0178"); // -= not applicable to boolean, long
    checkErrorExpr("varBoolean -= varfloat", "0xA0178"); // -= not applicable to boolean, float
    checkErrorExpr("varBoolean -= vardouble", "0xA0178"); // -= not applicable to boolean, double
    checkErrorExpr("varint-=\"Hello\"", "0xA0178"); //not possible because int = int - (int) String returns a casting error
    checkErrorExpr("varintMetre -= 1s", "0xA0178");
    checkErrorExpr("varintMetre -= 1", "0xA0178");
  }

  @Test
  public void deriveFromMultAssignmentExpression() throws IOException {
    checkExpr("varchar *= varchar", "char"); // *= applicable to char, char
    checkExpr("varchar *= varbyte", "char"); // *= applicable to char, byte
    checkExpr("varchar *= varshort", "char"); // *= applicable to char, short
    checkExpr("varchar *= varint", "char"); // *= applicable to char, int
    checkExpr("varchar *= varlong", "char"); // *= applicable to char, long
    checkExpr("varchar *= varfloat", "char"); // *= applicable to char, float
    checkExpr("varchar *= vardouble", "char"); // *= applicable to char, double
    checkExpr("varshort *= varchar", "short"); // *= applicable to short, char
    checkExpr("varshort *= varbyte", "short"); // *= applicable to short, byte
    checkExpr("varshort *= varshort", "short"); // *= applicable to short, short
    checkExpr("varshort *= varint", "short"); // *= applicable to short, int
    checkExpr("varshort *= varlong", "short"); // *= applicable to short, long
    checkExpr("varshort *= varfloat", "short"); // *= applicable to short, float
    checkExpr("varshort *= vardouble", "short"); // *= applicable to short, double
    checkExpr("varint *= varchar", "int"); // *= applicable to int, char
    checkExpr("varint *= varbyte", "int"); // *= applicable to int, byte
    checkExpr("varint *= varshort", "int"); // *= applicable to int, short
    checkExpr("varint *= varint", "int"); // *= applicable to int, int
    checkExpr("varint *= varlong", "int"); // *= applicable to int, long
    checkExpr("varint *= varfloat", "int"); // *= applicable to int, float
    checkExpr("varint *= vardouble", "int"); // *= applicable to int, double
    checkExpr("varfloat *= varchar", "float"); // *= applicable to float, char
    checkExpr("varfloat *= varbyte", "float"); // *= applicable to float, byte
    checkExpr("varfloat *= varshort", "float"); // *= applicable to float, short
    checkExpr("varfloat *= varint", "float"); // *= applicable to float, int
    checkExpr("varfloat *= varlong", "float"); // *= applicable to float, long
    checkExpr("varfloat *= varfloat", "float"); // *= applicable to float, float
    checkExpr("varfloat *= vardouble", "float"); // *= applicable to float, double
    checkExpr("vardouble *= varchar", "double"); // *= applicable to double, char
    checkExpr("vardouble *= varbyte", "double"); // *= applicable to double, byte
    checkExpr("vardouble *= varshort", "double"); // *= applicable to double, short
    checkExpr("vardouble *= varint", "double"); // *= applicable to double, int
    checkExpr("vardouble *= varlong", "double"); // *= applicable to double, long
    checkExpr("vardouble *= varfloat", "double"); // *= applicable to double, float
    checkExpr("vardouble *= vardouble", "double"); // *= applicable to double, double
    checkExpr("varintMetre *= varint", "[m]<int>");
  }

  @Test
  public void testInvalidMultAssignmentExpression() throws IOException {
    checkErrorExpr("varboolean *= varboolean", "0xA0178"); // *= not applicable to boolean, boolean
    checkErrorExpr("varboolean *= varchar", "0xA0178"); // *= not applicable to boolean, char
    checkErrorExpr("varboolean *= varbyte", "0xA0178"); // *= not applicable to boolean, byte
    checkErrorExpr("varboolean *= varshort", "0xA0178"); // *= not applicable to boolean, short
    checkErrorExpr("varboolean *= varint", "0xA0178"); // *= not applicable to boolean, int
    checkErrorExpr("varboolean *= varlong", "0xA0178"); // *= not applicable to boolean, long
    checkErrorExpr("varboolean *= varfloat", "0xA0178"); // *= not applicable to boolean, float
    checkErrorExpr("varboolean *= vardouble", "0xA0178"); // *= not applicable to boolean, double
    checkErrorExpr("varint*=\"Hello\"", "0xA0178"); /// not possible because int = int * (int) String returns a casting error
    checkErrorExpr("varintMetre *= varintMetre", "0xA0178");
  }

  @Test
  public void deriveFromDivideAssignmentExpression() throws IOException {
    checkExpr("varchar /= varchar", "char"); // /= applicable to char, char
    checkExpr("varchar /= varbyte", "char"); // /= applicable to char, byte
    checkExpr("varchar /= varshort", "char"); // /= applicable to char, short
    checkExpr("varchar /= varint", "char"); // /= applicable to char, int
    checkExpr("varchar /= varlong", "char"); // /= applicable to char, long
    checkExpr("varchar /= varfloat", "char"); // /= applicable to char, float
    checkExpr("varchar /= vardouble", "char"); // /= applicable to char, double
    checkExpr("varshort /= varchar", "short"); // /= applicable to short, char
    checkExpr("varshort /= varbyte", "short"); // /= applicable to short, byte
    checkExpr("varshort /= varshort", "short"); // /= applicable to short, short
    checkExpr("varshort /= varint", "short"); // /= applicable to short, int
    checkExpr("varshort /= varlong", "short"); // /= applicable to short, long
    checkExpr("varshort /= varfloat", "short"); // /= applicable to short, float
    checkExpr("varshort /= vardouble", "short"); // /= applicable to short, double
    checkExpr("varint /= varchar", "int"); // /= applicable to int, char
    checkExpr("varint /= varbyte", "int"); // /= applicable to int, byte
    checkExpr("varint /= varshort", "int"); // /= applicable to int, short
    checkExpr("varint /= varint", "int"); // /= applicable to int, int
    checkExpr("varint /= varlong", "int"); // /= applicable to int, long
    checkExpr("varint /= varfloat", "int"); // /= applicable to int, float
    checkExpr("varint /= vardouble", "int"); // /= applicable to int, double
    checkExpr("varfloat /= varchar", "float"); // /= applicable to float, char
    checkExpr("varfloat /= varbyte", "float"); // /= applicable to float, byte
    checkExpr("varfloat /= varshort", "float"); // /= applicable to float, short
    checkExpr("varfloat /= varint", "float"); // /= applicable to float, int
    checkExpr("varfloat /= varlong", "float"); // /= applicable to float, long
    checkExpr("varfloat /= varfloat", "float"); // /= applicable to float, float
    checkExpr("varfloat /= vardouble", "float"); // /= applicable to float, double
    checkExpr("vardouble /= varchar", "double"); // /= applicable to double, char
    checkExpr("vardouble /= varbyte", "double"); // /= applicable to double, byte
    checkExpr("vardouble /= varshort", "double"); // /= applicable to double, short
    checkExpr("vardouble /= varint", "double"); // /= applicable to double, int
    checkExpr("vardouble /= varlong", "double"); // /= applicable to double, long
    checkExpr("vardouble /= varfloat", "double"); // /= applicable to double, float
    checkExpr("vardouble /= vardouble", "double"); // /= applicable to double, double
    checkExpr("varintMetre /= varint", "[m]<int>");
  }

  @Test
  public void testInvalidDivideAssignmentExpression() throws IOException {
    checkErrorExpr("varboolean /= varboolean", "0xA0178"); // /= not applicable to boolean, boolean
    checkErrorExpr("varboolean /= varchar", "0xA0178"); // /= not applicable to boolean, char
    checkErrorExpr("varboolean /= varbyte", "0xA0178"); // /= not applicable to boolean, byte
    checkErrorExpr("varboolean /= varshort", "0xA0178"); // /= not applicable to boolean, short
    checkErrorExpr("varboolean /= varint", "0xA0178"); // /= not applicable to boolean, int
    checkErrorExpr("varboolean /= varlong", "0xA0178"); // /= not applicable to boolean, long
    checkErrorExpr("varboolean /= varfloat", "0xA0178"); // /= not applicable to boolean, float
    checkErrorExpr("varboolean /= vardouble", "0xA0178"); // /= not applicable to boolean, double
    checkErrorExpr("varint/=\"Hello\"", "0xA0178"); // not possible because int = int / (int) String returns a casting error
    checkErrorExpr("varintMetre /= varintMetre", "0xA0178");
  }

  @Test
  public void deriveFromModuloAssignmentExpression() throws IOException {
    checkExpr("varchar %= varchar", "char"); // %= applicable to char, char
    checkExpr("varchar %= varbyte", "char"); // %= applicable to char, byte
    checkExpr("varchar %= varshort", "char"); // %= applicable to char, short
    checkExpr("varchar %= varint", "char"); // %= applicable to char, int
    checkExpr("varchar %= varlong", "char"); // %= applicable to char, long
    checkExpr("varchar %= varfloat", "char"); // %= applicable to char, float
    checkExpr("varchar %= vardouble", "char"); // %= applicable to char, double
    checkExpr("varshort %= varchar", "short"); // %= applicable to short, char
    checkExpr("varshort %= varbyte", "short"); // %= applicable to short, byte
    checkExpr("varshort %= varshort", "short"); // %= applicable to short, short
    checkExpr("varshort %= varint", "short"); // %= applicable to short, int
    checkExpr("varshort %= varlong", "short"); // %= applicable to short, long
    checkExpr("varshort %= varfloat", "short"); // %= applicable to short, float
    checkExpr("varshort %= vardouble", "short"); // %= applicable to short, double
    checkExpr("varint %= varchar", "int"); // %= applicable to int, char
    checkExpr("varint %= varbyte", "int"); // %= applicable to int, byte
    checkExpr("varint %= varshort", "int"); // %= applicable to int, short
    checkExpr("varint %= varint", "int"); // %= applicable to int, int
    checkExpr("varint %= varlong", "int"); // %= applicable to int, long
    checkExpr("varint %= varfloat", "int"); // %= applicable to int, float
    checkExpr("varint %= vardouble", "int"); // %= applicable to int, double
    checkExpr("varfloat %= varchar", "float"); // %= applicable to float, char
    checkExpr("varfloat %= varbyte", "float"); // %= applicable to float, byte
    checkExpr("varfloat %= varshort", "float"); // %= applicable to float, short
    checkExpr("varfloat %= varint", "float"); // %= applicable to float, int
    checkExpr("varfloat %= varlong", "float"); // %= applicable to float, long
    checkExpr("varfloat %= varfloat", "float"); // %= applicable to float, float
    checkExpr("varfloat %= vardouble", "float"); // %= applicable to float, double
    checkExpr("vardouble %= varchar", "double"); // %= applicable to double, char
    checkExpr("vardouble %= varbyte", "double"); // %= applicable to double, byte
    checkExpr("vardouble %= varshort", "double"); // %= applicable to double, short
    checkExpr("vardouble %= varint", "double"); // %= applicable to double, int
    checkExpr("vardouble %= varlong", "double"); // %= applicable to double, long
    checkExpr("vardouble %= varfloat", "double"); // %= applicable to double, float
    checkExpr("vardouble %= vardouble", "double"); // %= applicable to double, double
    checkExpr("varintMetre %= varintMetre", "[m]<int>");
  }

  @Test
  public void testInvalidModuloAssignmentExpression() throws IOException {
    checkErrorExpr("varboolean %= varboolean", "0xA0178"); // %= not applicable to boolean, boolean
    checkErrorExpr("varboolean %= varchar", "0xA0178"); // %= not applicable to boolean, char
    checkErrorExpr("varboolean %= varbyte", "0xA0178"); // %= not applicable to boolean, byte
    checkErrorExpr("varboolean %= varshort", "0xA0178"); // %= not applicable to boolean, short
    checkErrorExpr("varboolean %= varint", "0xA0178"); // %= not applicable to boolean, int
    checkErrorExpr("varboolean %= varlong", "0xA0178"); // %= not applicable to boolean, long
    checkErrorExpr("varboolean %= varfloat", "0xA0178"); // %= not applicable to boolean, float
    checkErrorExpr("varboolean %= vardouble", "0xA0178"); // %= not applicable to boolean, double
    checkErrorExpr("varint%=\"Hello\"", "0xA0178"); // not possible because int = int % (int) String returns a casting error
    checkErrorExpr("varintMetre %= 1s", "0xA0178");
    checkErrorExpr("varintMetre %= 1", "0xA0178");
  }

  @Test
  public void deriveFromAndAssignmentExpression() throws IOException {
    checkExpr("varboolean &= varboolean", "boolean"); // &= applicable to boolean, boolean
    checkExpr("varchar &= varchar", "char"); // &= applicable to char, char
    checkExpr("varchar &= varbyte", "char"); // &= applicable to char, byte
    checkExpr("varchar &= varshort", "char"); // &= applicable to char, short
    checkExpr("varchar &= varint", "char"); // &= applicable to char, int
    checkExpr("varchar &= varlong", "char"); // &= applicable to char, long
    checkExpr("varbyte &= varchar", "byte"); // &= applicable to byte, char
    checkExpr("varbyte &= varbyte", "byte"); // &= applicable to byte, byte
    checkExpr("varbyte &= varshort", "byte"); // &= applicable to byte, short
    checkExpr("varbyte &= varint", "byte"); // &= applicable to byte, int
    checkExpr("varbyte &= varlong", "byte"); // &= applicable to byte, long
    checkExpr("varshort &= varchar", "short"); // &= applicable to short, char
    checkExpr("varshort &= varbyte", "short"); // &= applicable to short, byte
    checkExpr("varshort &= varshort", "short"); // &= applicable to short, short
    checkExpr("varshort &= varint", "short"); // &= applicable to short, int
    checkExpr("varshort &= varlong", "short"); // &= applicable to short, long
    checkExpr("varint &= varchar", "int"); // &= applicable to int, char
    checkExpr("varint &= varbyte", "int"); // &= applicable to int, byte
    checkExpr("varint &= varshort", "int"); // &= applicable to int, short
    checkExpr("varint &= varint", "int"); // &= applicable to int, int
    checkExpr("varint &= varlong", "int"); // &= applicable to int, long
    checkExpr("varlong &= varchar", "long"); // &= applicable to long, char
    checkExpr("varlong &= varbyte", "long"); // &= applicable to long, byte
    checkExpr("varlong &= varshort", "long"); // &= applicable to long, short
    checkExpr("varlong &= varint", "long"); // &= applicable to long, int
    checkExpr("varlong &= varlong", "long"); // &= applicable to long, long
  }

  @Test
  public void testInvalidAndAssignmentExpression() throws IOException {
    checkErrorExpr("varboolean &= varchar", "0xA0176"); // &= not applicable to boolean, char
    checkErrorExpr("varboolean &= varbyte", "0xA0176"); // &= not applicable to boolean, byte
    checkErrorExpr("varboolean &= varshort", "0xA0176"); // &= not applicable to boolean, short
    checkErrorExpr("varboolean &= varint", "0xA0176"); // &= not applicable to boolean, int
    checkErrorExpr("varboolean &= varlong", "0xA0176"); // &= not applicable to boolean, long
    checkErrorExpr("varboolean &= varfloat", "0xA0176"); // &= not applicable to boolean, float
    checkErrorExpr("varboolean &= vardouble", "0xA0176"); // &= not applicable to boolean, double
    checkErrorExpr("varchar &= varboolean", "0xA0176"); // &= not applicable to char, boolean
    checkErrorExpr("varchar &= varfloat", "0xA0176"); // &= not applicable to char, float
    checkErrorExpr("varchar &= vardouble", "0xA0176"); // &= not applicable to char, double
    checkErrorExpr("varbyte &= varboolean", "0xA0176"); // &= not applicable to byte, boolean
    checkErrorExpr("varbyte &= varfloat", "0xA0176"); // &= not applicable to byte, float
    checkErrorExpr("varbyte &= vardouble", "0xA0176"); // &= not applicable to byte, double
    checkErrorExpr("varshort &= varboolean", "0xA0176"); // &= not applicable to short, boolean
    checkErrorExpr("varshort &= varfloat", "0xA0176"); // &= not applicable to short, float
    checkErrorExpr("varshort &= vardouble", "0xA0176"); // &= not applicable to short, double
    checkErrorExpr("varint &= varboolean", "0xA0176"); // &= not applicable to int, boolean
    checkErrorExpr("varint &= varfloat", "0xA0176"); // &= not applicable to int, float
    checkErrorExpr("varint &= vardouble", "0xA0176"); // &= not applicable to int, double
    checkErrorExpr("varlong &= varboolean", "0xA0176"); // &= not applicable to long, boolean
    checkErrorExpr("varlong &= varfloat", "0xA0176"); // &= not applicable to long, float
    checkErrorExpr("varlong &= vardouble", "0xA0176"); // &= not applicable to long, double
    checkErrorExpr("varfloat &= varboolean", "0xA0176"); // &= not applicable to float, boolean
    checkErrorExpr("varfloat &= varchar", "0xA0176"); // &= not applicable to float, char
    checkErrorExpr("varfloat &= varbyte", "0xA0176"); // &= not applicable to float, byte
    checkErrorExpr("varfloat &= varshort", "0xA0176"); // &= not applicable to float, short
    checkErrorExpr("varfloat &= varint", "0xA0176"); // &= not applicable to float, int
    checkErrorExpr("varfloat &= varlong", "0xA0176"); // &= not applicable to float, long
    checkErrorExpr("varfloat &= varfloat", "0xA0176"); // &= not applicable to float, float
    checkErrorExpr("varfloat &= vardouble", "0xA0176"); // &= not applicable to float, double
    checkErrorExpr("vardouble &= varboolean", "0xA0176"); // &= not applicable to double, boolean
    checkErrorExpr("vardouble &= varchar", "0xA0176"); // &= not applicable to double, char
    checkErrorExpr("vardouble &= varbyte", "0xA0176"); // &= not applicable to double, byte
    checkErrorExpr("vardouble &= varshort", "0xA0176"); // &= not applicable to double, short
    checkErrorExpr("vardouble &= varint", "0xA0176"); // &= not applicable to double, int
    checkErrorExpr("vardouble &= varlong", "0xA0176"); // &= not applicable to double, long
    checkErrorExpr("vardouble &= varfloat", "0xA0176"); // &= not applicable to double, float
    checkErrorExpr("vardouble &= vardouble", "0xA0176"); // &= not applicable double, double
    checkErrorExpr("varint&=\"Hello\"", "0xA0176"); // not possible because int = int & (int) String returns a casting error
    checkErrorExpr("varintMetre &= varintMetre", "0xA0176");
    checkErrorExpr("varintMetre &= varintSecond", "0xA0176");
    checkErrorExpr("varintMetre &= varint", "0xA0176");
  }

  @Test
  public void deriveFromOrAssignmentExpression() throws IOException {
    checkExpr("varboolean |= varboolean", "boolean"); // |= applicable to boolean, boolean
    checkExpr("varchar |= varchar", "char"); // |= applicable to char, char
    checkExpr("varchar |= varbyte", "char"); // |= applicable to char, byte
    checkExpr("varchar |= varshort", "char"); // |= applicable to char, short
    checkExpr("varchar |= varint", "char"); // |= applicable to char, int
    checkExpr("varchar |= varlong", "char"); // |= applicable to char, long
    checkExpr("varbyte |= varchar", "byte"); // |= applicable to byte, char
    checkExpr("varbyte |= varbyte", "byte"); // |= applicable to byte, byte
    checkExpr("varbyte |= varshort", "byte"); // |= applicable to byte, short
    checkExpr("varbyte |= varint", "byte"); // |= applicable to byte, int
    checkExpr("varbyte |= varlong", "byte"); // |= applicable to byte, long
    checkExpr("varshort |= varchar", "short"); // |= applicable to short, char
    checkExpr("varshort |= varbyte", "short"); // |= applicable to short, byte
    checkExpr("varshort |= varshort", "short"); // |= applicable to short, short
    checkExpr("varshort |= varint", "short"); // |= applicable to short, int
    checkExpr("varshort |= varlong", "short"); // |= applicable to short, long
    checkExpr("varint |= varchar", "int"); // |= applicable to int, char
    checkExpr("varint |= varbyte", "int"); // |= applicable to int, byte
    checkExpr("varint |= varshort", "int"); // |= applicable to int, short
    checkExpr("varint |= varint", "int"); // |= applicable to int, int
    checkExpr("varint |= varlong", "int"); // |= applicable to int, long
    checkExpr("varlong |= varchar", "long"); // |= applicable to long, char
    checkExpr("varlong |= varbyte", "long"); // |= applicable to long, byte
    checkExpr("varlong |= varshort", "long"); // |= applicable to long, short
    checkExpr("varlong |= varint", "long"); // |= applicable to long, int
    checkExpr("varlong |= varlong", "long"); // |= applicable to long, long
  }

  @Test
  public void testInvalidOrAssignmentExpression() throws IOException {
    checkErrorExpr("varboolean |= varchar", "0xA0176"); // |= not applicable to boolean, char
    checkErrorExpr("varboolean |= varbyte", "0xA0176"); // |= not applicable to boolean, byte
    checkErrorExpr("varboolean |= varshort", "0xA0176"); // |= not applicable to boolean, short
    checkErrorExpr("varboolean |= varint", "0xA0176"); // |= not applicable to boolean, int
    checkErrorExpr("varboolean |= varlong", "0xA0176"); // |= not applicable to boolean, long
    checkErrorExpr("varboolean |= varfloat", "0xA0176"); // |= not applicable to boolean, float
    checkErrorExpr("varboolean |= vardouble", "0xA0176"); // |= not applicable to boolean, double
    checkErrorExpr("varchar |= varboolean", "0xA0176"); // |= not applicable to char, boolean
    checkErrorExpr("varchar |= varfloat", "0xA0176"); // |= not applicable to char, float
    checkErrorExpr("varchar |= vardouble", "0xA0176"); // |= not applicable to char, double
    checkErrorExpr("varbyte |= varboolean", "0xA0176"); // |= not applicable to byte, boolean
    checkErrorExpr("varbyte |= varfloat", "0xA0176"); // |= not applicable to byte, float
    checkErrorExpr("varbyte |= vardouble", "0xA0176"); // |= not applicable to byte, double
    checkErrorExpr("varshort |= varboolean", "0xA0176"); // |= not applicable to short, boolean
    checkErrorExpr("varshort |= varfloat", "0xA0176"); // |= not applicable to short, float
    checkErrorExpr("varshort |= vardouble", "0xA0176"); // |= not applicable to short, double
    checkErrorExpr("varint |= varboolean", "0xA0176"); // |= not applicable to int, boolean
    checkErrorExpr("varint |= varfloat", "0xA0176"); // |= not applicable to int, float
    checkErrorExpr("varint |= vardouble", "0xA0176"); // |= not applicable to int, double
    checkErrorExpr("varlong |= varboolean", "0xA0176"); // |= not applicable to long, boolean
    checkErrorExpr("varlong |= varfloat", "0xA0176"); // |= not applicable to long, float
    checkErrorExpr("varlong |= vardouble", "0xA0176"); // |= not applicable to long, double
    checkErrorExpr("varfloat |= varboolean", "0xA0176"); // |= not applicable to float, boolean
    checkErrorExpr("varfloat |= varchar", "0xA0176"); // |= not applicable to float, char
    checkErrorExpr("varfloat |= varbyte", "0xA0176"); // |= not applicable to float, byte
    checkErrorExpr("varfloat |= varshort", "0xA0176"); // |= not applicable to float, short
    checkErrorExpr("varfloat |= varint", "0xA0176"); // |= not applicable to float, int
    checkErrorExpr("varfloat |= varlong", "0xA0176"); // |= not applicable to float, long
    checkErrorExpr("varfloat |= varfloat", "0xA0176"); // |= not applicable to float, float
    checkErrorExpr("varfloat |= vardouble", "0xA0176"); // |= not applicable to float, double
    checkErrorExpr("vardouble |= varboolean", "0xA0176"); // |= not applicable to double, boolean
    checkErrorExpr("vardouble |= varchar", "0xA0176"); // |= not applicable to double, char
    checkErrorExpr("vardouble |= varbyte", "0xA0176"); // |= not applicable to double, byte
    checkErrorExpr("vardouble |= varshort", "0xA0176"); // |= not applicable to double, short
    checkErrorExpr("vardouble |= varint", "0xA0176"); // |= not applicable to double, int
    checkErrorExpr("vardouble |= varlong", "0xA0176"); // |= not applicable to double, long
    checkErrorExpr("vardouble |= varfloat", "0xA0176"); // |= not applicable to double, float
    checkErrorExpr("vardouble |= vardouble", "0xA0176"); // |= not applicable double, double
    checkErrorExpr("varint|=\"Hello\"", "0xA0176"); // not possible because int = int | (int) String returns a casting error
    checkErrorExpr("varintMetre |= varintMetre", "0xA0176");
    checkErrorExpr("varintMetre |= varintSecond", "0xA0176");
    checkErrorExpr("varintMetre |= varint", "0xA0176");
  }

  @Test
  public void deriveFromBinaryXorAssignmentExpression() throws IOException {
    checkExpr("varboolean ^= varboolean", "boolean"); // ^= applicable to boolean, boolean
    checkExpr("varchar ^= varchar", "char"); // ^= applicable to char, char
    checkExpr("varchar ^= varbyte", "char"); // ^= applicable to char, byte
    checkExpr("varchar ^= varshort", "char"); // ^= applicable to char, short
    checkExpr("varchar ^= varint", "char"); // ^= applicable to char, int
    checkExpr("varchar ^= varlong", "char"); // ^= applicable to char, long
    checkExpr("varbyte ^= varchar", "byte"); // ^= applicable to byte, char
    checkExpr("varbyte ^= varbyte", "byte"); // ^= applicable to byte, byte
    checkExpr("varbyte ^= varshort", "byte"); // ^= applicable to byte, short
    checkExpr("varbyte ^= varint", "byte"); // ^= applicable to byte, int
    checkExpr("varbyte ^= varlong", "byte"); // ^= applicable to byte, long
    checkExpr("varshort ^= varchar", "short"); // ^= applicable to short, char
    checkExpr("varshort ^= varbyte", "short"); // ^= applicable to short, byte
    checkExpr("varshort ^= varshort", "short"); // ^= applicable to short, short
    checkExpr("varshort ^= varint", "short"); // ^= applicable to short, int
    checkExpr("varshort ^= varlong", "short"); // ^= applicable to short, long
    checkExpr("varint ^= varchar", "int"); // ^= applicable to int, char
    checkExpr("varint ^= varbyte", "int"); // ^= applicable to int, byte
    checkExpr("varint ^= varshort", "int"); // ^= applicable to int, short
    checkExpr("varint ^= varint", "int"); // ^= applicable to int, int
    checkExpr("varint ^= varlong", "int"); // ^= applicable to int, long
    checkExpr("varlong ^= varchar", "long"); // ^= applicable to long, char
    checkExpr("varlong ^= varbyte", "long"); // ^= applicable to long, byte
    checkExpr("varlong ^= varshort", "long"); // ^= applicable to long, short
    checkExpr("varlong ^= varint", "long"); // ^= applicable to long, int
    checkExpr("varlong ^= varlong", "long"); // ^= applicable to long, long
  }

  @Test
  public void testInvalidBinaryXorAssignmentExpression() throws IOException {
    checkErrorExpr("varboolean ^= varchar", "0xA0176"); // ^= not applicable to boolean, char
    checkErrorExpr("varboolean ^= varbyte", "0xA0176"); // ^= not applicable to boolean, byte
    checkErrorExpr("varboolean ^= varshort", "0xA0176"); // ^= not applicable to boolean, short
    checkErrorExpr("varboolean ^= varint", "0xA0176"); // ^= not applicable to boolean, int
    checkErrorExpr("varboolean ^= varlong", "0xA0176"); // ^= not applicable to boolean, long
    checkErrorExpr("varboolean ^= varfloat", "0xA0176"); // ^= not applicable to boolean, float
    checkErrorExpr("varboolean ^= vardouble", "0xA0176"); // ^= not applicable to boolean, double
    checkErrorExpr("varchar ^= varboolean", "0xA0176"); // ^= not applicable to char, boolean
    checkErrorExpr("varchar ^= varfloat", "0xA0176"); // ^= not applicable to char, float
    checkErrorExpr("varchar ^= vardouble", "0xA0176"); // ^= not applicable to char, double
    checkErrorExpr("varbyte ^= varboolean", "0xA0176"); // ^= not applicable to byte, boolean
    checkErrorExpr("varbyte ^= varfloat", "0xA0176"); // ^= not applicable to byte, float
    checkErrorExpr("varbyte ^= vardouble", "0xA0176"); // ^= not applicable to byte, double
    checkErrorExpr("varshort ^= varboolean", "0xA0176"); // ^= not applicable to short, boolean
    checkErrorExpr("varshort ^= varfloat", "0xA0176"); // ^= not applicable to short, float
    checkErrorExpr("varshort ^= vardouble", "0xA0176"); // ^= not applicable to short, double
    checkErrorExpr("varint ^= varboolean", "0xA0176"); // ^= not applicable to int, boolean
    checkErrorExpr("varint ^= varfloat", "0xA0176"); // ^= not applicable to int, float
    checkErrorExpr("varint ^= vardouble", "0xA0176"); // ^= not applicable to int, double
    checkErrorExpr("varlong ^= varboolean", "0xA0176"); // ^= not applicable to long, boolean
    checkErrorExpr("varlong ^= varfloat", "0xA0176"); // ^= not applicable to long, float
    checkErrorExpr("varlong ^= vardouble", "0xA0176"); // ^= not applicable to long, double
    checkErrorExpr("varfloat ^= varboolean", "0xA0176"); // ^= not applicable to float, boolean
    checkErrorExpr("varfloat ^= varchar", "0xA0176"); // ^= not applicable to float, char
    checkErrorExpr("varfloat ^= varbyte", "0xA0176"); // ^= not applicable to float, byte
    checkErrorExpr("varfloat ^= varshort", "0xA0176"); // ^= not applicable to float, short
    checkErrorExpr("varfloat ^= varint", "0xA0176"); // ^= not applicable to float, int
    checkErrorExpr("varfloat ^= varlong", "0xA0176"); // ^= not applicable to float, long
    checkErrorExpr("varfloat ^= varfloat", "0xA0176"); // ^= not applicable to float, float
    checkErrorExpr("varfloat ^= vardouble", "0xA0176"); // ^= not applicable to float, double
    checkErrorExpr("vardouble ^= varboolean", "0xA0176"); // ^= not applicable to double, boolean
    checkErrorExpr("vardouble ^= varchar", "0xA0176"); // ^= not applicable to double, char
    checkErrorExpr("vardouble ^= varbyte", "0xA0176"); // ^= not applicable to double, byte
    checkErrorExpr("vardouble ^= varshort", "0xA0176"); // ^= not applicable to double, short
    checkErrorExpr("vardouble ^= varint", "0xA0176"); // ^= not applicable to double, int
    checkErrorExpr("vardouble ^= varlong", "0xA0176"); // ^= not applicable to double, long
    checkErrorExpr("vardouble ^= varfloat", "0xA0176"); // ^= not applicable to double, float
    checkErrorExpr("vardouble ^= vardouble", "0xA0176"); // ^= not applicable to double, double
    checkErrorExpr("varint^=\"Hello\"", "0xA0176"); // not possible because int = int ^ (int) String returns a casting error
    checkErrorExpr("varintMetre ^= varintMetre", "0xA0176");
    checkErrorExpr("varintMetre ^= varintSecond", "0xA0176");
    checkErrorExpr("varintMetre ^= varint", "0xA0176");
  }

  @Test
  public void deriveFromDoubleLeftAssignmentExpression() throws IOException {
    checkExpr("varchar <<= varchar", "char"); // <<= applicable to char, char
    checkExpr("varchar <<= varbyte", "char"); // <<= applicable to char, byte
    checkExpr("varchar <<= varshort", "char"); // <<= applicable to char, short
    checkExpr("varchar <<= varint", "char"); // <<= applicable to char, int
    checkExpr("varchar <<= varlong", "char"); // <<= applicable to char, long
    checkExpr("varbyte <<= varchar", "byte"); // <<= applicable to byte, char
    checkExpr("varbyte <<= varbyte", "byte"); // <<= applicable to byte, byte
    checkExpr("varbyte <<= varshort", "byte"); // <<= applicable to byte, short
    checkExpr("varbyte <<= varint", "byte"); // <<= applicable to byte, int
    checkExpr("varbyte <<= varlong", "byte"); // <<= applicable to byte, long
    checkExpr("varshort <<= varchar", "short"); // <<= applicable to short, char
    checkExpr("varshort <<= varbyte", "short"); // <<= applicable to short, byte
    checkExpr("varshort <<= varshort", "short"); // <<= applicable to short, short
    checkExpr("varshort <<= varint", "short"); // <<= applicable to short, int
    checkExpr("varshort <<= varlong", "short"); // <<= applicable to short, long
    checkExpr("varint <<= varchar", "int"); // <<= applicable to int, char
    checkExpr("varint <<= varbyte", "int"); // <<= applicable to int, byte
    checkExpr("varint <<= varshort", "int"); // <<= applicable to int, short
    checkExpr("varint <<= varint", "int"); // <<= applicable to int, int
    checkExpr("varint <<= varlong", "int"); // <<= applicable to int, long
    checkExpr("varlong <<= varchar", "long"); // <<= applicable to long, char
    checkExpr("varlong <<= varbyte", "long"); // <<= applicable to long, byte
    checkExpr("varlong <<= varshort", "long"); // <<= applicable to long, short
    checkExpr("varlong <<= varint", "long"); // <<= applicable to long, int
    checkExpr("varlong <<= varlong", "long"); // <<= applicable to long, long
  }

  @Test
  public void testInvalidDoubleLeftAssignmentExpression() throws IOException {
    checkErrorExpr("varboolean <<= varboolean", "0xA0177"); // <<= not applicable to boolean, boolean
    checkErrorExpr("varboolean <<= varchar", "0xA0177"); // <<= not applicable to boolean, char
    checkErrorExpr("varboolean <<= varbyte", "0xA0177"); // <<= not applicable to boolean, byte
    checkErrorExpr("varboolean <<= varshort", "0xA0177"); // <<= not applicable to boolean, short
    checkErrorExpr("varboolean <<= varint", "0xA0177"); // <<= not applicable to boolean, int
    checkErrorExpr("varboolean <<= varlong", "0xA0177"); // <<= not applicable to boolean, long
    checkErrorExpr("varboolean <<= varfloat", "0xA0177"); // <<= not applicable to boolean, float
    checkErrorExpr("varboolean <<= vardouble", "0xA0177"); // <<= not applicable to boolean, double
    checkErrorExpr("varfloat <<= varboolean", "0xA0177"); // <<= not applicable to float, boolean
    checkErrorExpr("varfloat <<= varchar", "0xA0177"); // <<= not applicable to float, char
    checkErrorExpr("varfloat <<= varbyte", "0xA0177"); // <<= not applicable to float, byte
    checkErrorExpr("varfloat <<= varshort", "0xA0177"); // <<= not applicable to float, short
    checkErrorExpr("varfloat <<= varint", "0xA0177"); // <<= not applicable to float, int
    checkErrorExpr("varfloat <<= varlong", "0xA0177"); // <<= not applicable to float, long
    checkErrorExpr("varfloat <<= varfloat", "0xA0177"); // <<= not applicable to float, float
    checkErrorExpr("varfloat <<= vardouble", "0xA0177"); // <<= not applicable to float, double
    checkErrorExpr("vardouble <<= varboolean", "0xA0177"); // <<= not applicable to double, boolean
    checkErrorExpr("vardouble <<= varchar", "0xA0177"); // <<= not applicable to double, char
    checkErrorExpr("vardouble <<= varbyte", "0xA0177"); // <<= not applicable to double, byte
    checkErrorExpr("vardouble <<= varshort", "0xA0177"); // <<= not applicable to double, short
    checkErrorExpr("vardouble <<= varint", "0xA0177"); // <<= not applicable to double, int
    checkErrorExpr("vardouble <<= varlong", "0xA0177"); // <<= not applicable to double, long
    checkErrorExpr("vardouble <<= varfloat", "0xA0177"); // <<= not applicable to double, float
    checkErrorExpr("vardouble <<= vardouble", "0xA0177"); // <<= not applicable to double, double
    checkErrorExpr("varint<<=\"Hello\"", "0xA0177"); // not possible because int = int << (int) String returns a cvarsting error
    checkErrorExpr("varintMetre <<= varintMetre", "0xA0177");
    checkErrorExpr("varintMetre <<= varintSecond", "0xA0177");
    checkErrorExpr("varintMetre <<= varint", "0xA0177");
  }

  @Test
  public void deriveFromDoubleRightAssignmentExpression() throws IOException {
    checkExpr("varchar >>= varchar", "char"); // >>= applicable to char, char
    checkExpr("varchar >>= varbyte", "char"); // >>= applicable to char, byte
    checkExpr("varchar >>= varshort", "char"); // >>= applicable to char, short
    checkExpr("varchar >>= varint", "char"); // >>= applicable to char, int
    checkExpr("varchar >>= varlong", "char"); // >>= applicable to char, long
    checkExpr("varbyte >>= varchar", "byte"); // >>= applicable to byte, char
    checkExpr("varbyte >>= varbyte", "byte"); // >>= applicable to byte, byte
    checkExpr("varbyte >>= varshort", "byte"); // >>= applicable to byte, short
    checkExpr("varbyte >>= varint", "byte"); // >>= applicable to byte, int
    checkExpr("varbyte >>= varlong", "byte"); // >>= applicable to byte, long
    checkExpr("varshort >>= varchar", "short"); // >>= applicable to short, char
    checkExpr("varshort >>= varbyte", "short"); // >>= applicable to short, byte
    checkExpr("varshort >>= varshort", "short"); // >>= applicable to short, short
    checkExpr("varshort >>= varint", "short"); // >>= applicable to short, int
    checkExpr("varshort >>= varlong", "short"); // >>= applicable to short, long
    checkExpr("varint >>= varchar", "int"); // >>= applicable to int, char
    checkExpr("varint >>= varbyte", "int"); // >>= applicable to int, byte
    checkExpr("varint >>= varshort", "int"); // >>= applicable to int, short
    checkExpr("varint >>= varint", "int"); // >>= applicable to int, int
    checkExpr("varint >>= varlong", "int"); // >>= applicable to int, long
    checkExpr("varlong >>= varchar", "long"); // >>= applicable to long, char
    checkExpr("varlong >>= varbyte", "long"); // >>= applicable to long, byte
    checkExpr("varlong >>= varshort", "long"); // >>= applicable to long, short
    checkExpr("varlong >>= varint", "long"); // >>= applicable to long, int
    checkExpr("varlong >>= varlong", "long"); // >>= applicable to long, long
  }

  @Test
  public void testInvalidDoubleRightAssignmentExpression() throws IOException {
    checkErrorExpr("varboolean >>= varboolean", "0xA0177"); // >>= not applicable to boolean, boolean
    checkErrorExpr("varboolean >>= varchar", "0xA0177"); // >>= not applicable to boolean, char
    checkErrorExpr("varboolean >>= varbyte", "0xA0177"); // >>= not applicable to boolean, byte
    checkErrorExpr("varboolean >>= varshort", "0xA0177"); // >>= not applicable to boolean, short
    checkErrorExpr("varboolean >>= varint", "0xA0177"); // >>= not applicable to boolean, int
    checkErrorExpr("varboolean >>= varlong", "0xA0177"); // >>= not applicable to boolean, long
    checkErrorExpr("varboolean >>= varfloat", "0xA0177"); // >>= not applicable to boolean, float
    checkErrorExpr("varboolean >>= vardouble", "0xA0177"); // >>= not applicable to boolean, double
    checkErrorExpr("varfloat >>= varboolean", "0xA0177"); // >>= not applicable to float, boolean
    checkErrorExpr("varfloat >>= varchar", "0xA0177"); // >>= not applicable to float, char
    checkErrorExpr("varfloat >>= varbyte", "0xA0177"); // >>= not applicable to float, byte
    checkErrorExpr("varfloat >>= varshort", "0xA0177"); // >>= not applicable to float, short
    checkErrorExpr("varfloat >>= varint", "0xA0177"); // >>= not applicable to float, int
    checkErrorExpr("varfloat >>= varlong", "0xA0177"); // >>= not applicable to float, long
    checkErrorExpr("varfloat >>= varfloat", "0xA0177"); // >>= not applicable to float, float
    checkErrorExpr("varfloat >>= vardouble", "0xA0177"); // >>= not applicable to float, double
    checkErrorExpr("vardouble >>= varboolean", "0xA0177"); // >>= not applicable to double, boolean
    checkErrorExpr("vardouble >>= varchar", "0xA0177"); // >>= not applicable to double, char
    checkErrorExpr("vardouble >>= varbyte", "0xA0177"); // >>= not applicable to double, byte
    checkErrorExpr("vardouble >>= varshort", "0xA0177"); // >>= not applicable to double, short
    checkErrorExpr("vardouble >>= varint", "0xA0177"); // >>= not applicable to double, int
    checkErrorExpr("vardouble >>= varlong", "0xA0177"); // >>= not applicable to double, long
    checkErrorExpr("vardouble >>= varfloat", "0xA0177"); // >>= not applicable to double, float
    checkErrorExpr("vardouble >>= vardouble", "0xA0177"); // >>= not applicable to double, double
    checkErrorExpr("varint>>=\"Hello\"", "0xA0177"); // not possible because int = int >> (int) String returns a cvarsting error
    checkErrorExpr("varintMetre >>= varintMetre", "0xA0177");
    checkErrorExpr("varintMetre >>= varintSecond", "0xA0177");
    checkErrorExpr("varintMetre >>= varint", "0xA0177");
  }

  @Test
  public void deriveFromLogicalRightAssignmentExpression() throws IOException {
    checkExpr("varchar >>>= varchar", "char"); // >>>= applicable to char, char
    checkExpr("varchar >>>= varbyte", "char"); // >>>= applicable to char, byte
    checkExpr("varchar >>>= varshort", "char"); // >>>= applicable to char, short
    checkExpr("varchar >>>= varint", "char"); // >>>= applicable to char, int
    checkExpr("varchar >>>= varlong", "char"); // >>>= applicable to char, long
    checkExpr("varbyte >>>= varchar", "byte"); // >>>= applicable to byte, char
    checkExpr("varbyte >>>= varbyte", "byte"); // >>>= applicable to byte, byte
    checkExpr("varbyte >>>= varshort", "byte"); // >>>= applicable to byte, short
    checkExpr("varbyte >>>= varint", "byte"); // >>>= applicable to byte, int
    checkExpr("varbyte >>>= varlong", "byte"); // >>>= applicable to byte, long
    checkExpr("varshort >>>= varchar", "short"); // >>>= applicable to short, char
    checkExpr("varshort >>>= varbyte", "short"); // >>>= applicable to short, byte
    checkExpr("varshort >>>= varshort", "short"); // >>>= applicable to short, short
    checkExpr("varshort >>>= varint", "short"); // >>>= applicable to short, int
    checkExpr("varshort >>>= varlong", "short"); // >>>= applicable to short, long
    checkExpr("varint >>>= varchar", "int"); // >>>= applicable to int, char
    checkExpr("varint >>>= varbyte", "int"); // >>>= applicable to int, byte
    checkExpr("varint >>>= varshort", "int"); // >>>= applicable to int, short
    checkExpr("varint >>>= varint", "int"); // >>>= applicable to int, int
    checkExpr("varint >>>= varlong", "int"); // >>>= applicable to int, long
    checkExpr("varlong >>>= varchar", "long"); // >>>= applicable to long, char
    checkExpr("varlong >>>= varbyte", "long"); // >>>= applicable to long, byte
    checkExpr("varlong >>>= varshort", "long"); // >>>= applicable to long, short
    checkExpr("varlong >>>= varint", "long"); // >>>= applicable to long, int
    checkExpr("varlong >>>= varlong", "long"); // >>>= applicable to long, long
  }

  @Test
  public void testInvalidLogicalRightAssignmentExpression() throws IOException {
    checkErrorExpr("varboolean >>>= varboolean", "0xA0177"); // >>>= not applicable to boolean, boolean
    checkErrorExpr("varboolean >>>= varchar", "0xA0177"); // >>>= not applicable to boolean, char
    checkErrorExpr("varboolean >>>= varbyte", "0xA0177"); // >>>= not applicable to boolean, byte
    checkErrorExpr("varboolean >>>= varshort", "0xA0177"); // >>>= not applicable to boolean, short
    checkErrorExpr("varboolean >>>= varint", "0xA0177"); // >>>= not applicable to boolean, int
    checkErrorExpr("varboolean >>>= varlong", "0xA0177"); // >>>= not applicable to boolean, long
    checkErrorExpr("varboolean >>>= varfloat", "0xA0177"); // >>>= not applicable to boolean, float
    checkErrorExpr("varboolean >>>= vardouble", "0xA0177"); // >>>= not applicable to boolean, double
    checkErrorExpr("varfloat >>>= varboolean", "0xA0177"); // >>>= not applicable to float, boolean
    checkErrorExpr("varfloat >>>= varchar", "0xA0177"); // >>>= not applicable to float, char
    checkErrorExpr("varfloat >>>= varbyte", "0xA0177"); // >>>= not applicable to float, byte
    checkErrorExpr("varfloat >>>= varshort", "0xA0177"); // >>>= not applicable to float, short
    checkErrorExpr("varfloat >>>= varint", "0xA0177"); // >>>= not applicable to float, int
    checkErrorExpr("varfloat >>>= varlong", "0xA0177"); // >>>= not applicable to float, long
    checkErrorExpr("varfloat >>>= varfloat", "0xA0177"); // >>>= not applicable to float, float
    checkErrorExpr("varfloat >>>= vardouble", "0xA0177"); // >>>= not applicable to float, double
    checkErrorExpr("vardouble >>>= varboolean", "0xA0177"); // >>>= not applicable to double, boolean
    checkErrorExpr("vardouble >>>= varchar", "0xA0177"); // >>>= not applicable to double, char
    checkErrorExpr("vardouble >>>= varbyte", "0xA0177"); // >>>= not applicable to double, byte
    checkErrorExpr("vardouble >>>= varshort", "0xA0177"); // >>>= not applicable to double, short
    checkErrorExpr("vardouble >>>= varint", "0xA0177"); // >>>= not applicable to double, int
    checkErrorExpr("vardouble >>>= varlong", "0xA0177"); // >>>= not applicable to double, long
    checkErrorExpr("vardouble >>>= varfloat", "0xA0177"); // >>>= not applicable to double, float
    checkErrorExpr("vardouble >>>= vardouble", "0xA0177"); // >>>= not applicable to double, double
    checkErrorExpr("varint>>>=\"Hello\"", "0xA0177"); // not possible because int = int >>> (int) String returns a casting error
    checkErrorExpr("varintMetre >>>= varintMetre", "0xA0177");
    checkErrorExpr("varintMetre >>>= varintSecond", "0xA0177");
    checkErrorExpr("varintMetre >>>= varint", "0xA0177");
  }

  @Test
  public void deriveFromRegularAssignmentExpression() throws IOException {
    checkExpr("varlong = 0l", "long"); // expected long and provided long
    checkExpr("varlong = +1l", "long"); // expected long and provided long
    checkExpr("varlong = -1l", "long"); // expected long and provided long
    checkExpr("varlong = 9223372036854775807l", "long"); // expected long and provided long (max value))
    checkExpr("varlong = -9223372036854775808l", "long"); // expected long and provided long (min value))
    checkExpr("varlong = varchar", "long"); // expected long and provided long (char conversion)
    checkExpr("varlong = varbyte", "long"); // expected long and provided long (byte conversion)
    checkExpr("varlong = varshort", "long"); // expected long and provided long (short conversion)
    checkExpr("varlong = varint", "long"); // expected long and provided long (int conversion)
    checkExpr("varlong = varlong", "long"); // expected long and provided long
    checkExpr("varfloat = 'a'", "float"); // expected float and provided float (char conversion)
    checkExpr("varfloat = 0", "float"); // expected float and provided float (int conversion)
    checkExpr("varfloat = 0l", "float"); // expected float and provided float (long conversion)
    checkExpr("varfloat = 0.0f", "float"); // expected float and provided float
    checkExpr("varfloat = +0.1f", "float"); // expected float and provided float
    checkExpr("varfloat = -0.1f", "float"); // expected float and provided float
    checkExpr("varfloat = varchar", "float"); // expected float and provided float (char conversion)
    checkExpr("varfloat = varbyte", "float"); // expected float and provided float (byte conversion)
    checkExpr("varfloat = varshort", "float"); // expected float and provided float (short conversion)
    checkExpr("varfloat = varint", "float"); // expected float and provided float (int conversion)
    checkExpr("varfloat = varlong", "float"); // expected float and provided float (long conversion)
    checkExpr("varfloat = varfloat", "float"); // expected float and provided float
    checkExpr("vardouble = 'a'", "double"); // expected double and provided double (char conversion)
    checkExpr("vardouble = 0", "double"); // expected double and provided double (int conversion)
    checkExpr("vardouble = 0l", "double"); // expected double and provided double (long conversion)
    checkExpr("vardouble = 0.0f", "double"); // expected double and provided double (float conversion)
    checkExpr("vardouble = 0.0", "double"); // expected double and provided double
    checkExpr("vardouble = +0.1", "double"); // expected double and provided double
    checkExpr("vardouble = -0.1", "double"); // expected double and provided double
    checkExpr("vardouble = varchar", "double"); // expected double and provided double (char conversion)
    checkExpr("vardouble = varbyte", "double"); // expected double and provided double (byte conversion)
    checkExpr("vardouble = varshort", "double"); // expected double and provided double (short conversion)
    checkExpr("vardouble = varint", "double"); // expected double and provided double (int conversion)
    checkExpr("vardouble = varlong", "double"); // expected double and provided double (long conversion)
    checkExpr("vardouble = varfloat", "double"); // expected double and provided double (float conversion)
    checkExpr("vardouble = vardouble", "double"); // expected double and provided double
    checkExpr("person1 = student2", "Person"); // example with person - student
    checkExpr("person2 = csStudent1", "Person"); // example with person - firstsemesterstudent
    checkExpr("varBoolean = varboolean", "java.lang.Boolean"); // example with Boolean - boolean
    checkExpr("varboolean = varBoolean", "boolean"); // example with boolean - Boolean
    checkExpr("varintMetre = varintMetre", "[m]<int>");
  }

  @Test
  public void testInvalidRegularAssignmentExpression() throws IOException {
    checkErrorExpr("varbyte = varlong", "0xA0179"); // expected byte but provided long
    checkErrorExpr("varbyte = varfloat", "0xA0179"); // expected byte but provided float
    checkErrorExpr("varbyte = vardouble", "0xA0179"); // expected byte but provided double
    checkErrorExpr("varshort = true", "0xA0179"); // expected short but provided boolean
    checkErrorExpr("varshort = false", "0xA0179"); // expected short but provided boolean
    checkErrorExpr("varshort = 32768", "0xA0179"); // expected short but provided int
    checkErrorExpr("varshort = -32769", "0xA0179"); // expected short but provided int
    checkErrorExpr("varshort = 1l", "0xA0179"); // expected short but provided long
    checkErrorExpr("varshort = 0.1f", "0xA0179"); // expected short but provided float
    checkErrorExpr("varshort = 0.1", "0xA0179"); // expected short but provided double
    checkErrorExpr("varshort = varboolean", "0xA0179"); // expected short but provided boolean
    checkErrorExpr("varshort = varchar", "0xA0179"); // expected short but provided char
    checkErrorExpr("varshort = varint", "0xA0179"); // expected short but provided int
    checkErrorExpr("varshort = varlong", "0xA0179"); // expected short but provided long
    checkErrorExpr("varshort = varfloat", "0xA0179"); // expected short but provided float
    checkErrorExpr("varshort = vardouble", "0xA0179"); // expected short but provided double
    checkErrorExpr("varint = true", "0xA0179"); // expected int but provided boolean
    checkErrorExpr("varint = false", "0xA0179"); // expected int but provided boolean
    checkErrorExpr("varint = 1l", "0xA0179"); // expected int but provided long
    checkErrorExpr("varint = 0.1f", "0xA0179"); // expected int but provided float
    checkErrorExpr("varint = 0.1", "0xA0179"); // expected int but provided double
    checkErrorExpr("varint = varboolean", "0xA0179"); // expected int but provided boolean
    checkErrorExpr("varint = varlong", "0xA0179"); // expected int but provided long
    checkErrorExpr("varint = varfloat", "0xA0179"); // expected int but provided float
    checkErrorExpr("varint = vardouble", "0xA0179"); // expected int but provided double
    checkErrorExpr("varlong = true", "0xA0179"); // expected long but provided boolean
    checkErrorExpr("varlong = false", "0xA0179"); // expected long but provided boolean
    checkErrorExpr("varlong = 0.1f", "0xA0179"); // expected long but provided float
    checkErrorExpr("varlong = 0.1", "0xA0179"); // expected long but provided double
    checkErrorExpr("varlong = varboolean", "0xA0179"); // expected long but provided boolean
    checkErrorExpr("varlong = varfloat", "0xA0179"); // expected long but provided float
    checkErrorExpr("varlong = vardouble", "0xA0179"); // expected long but provided double
    checkErrorExpr("varfloat = true", "0xA0179"); // expected float but provided boolean
    checkErrorExpr("varfloat = false", "0xA0179"); // expected float but provided boolean
    checkErrorExpr("varfloat = 0.1", "0xA0179"); // expected float but provided double
    checkErrorExpr("varfloat = varboolean", "0xA0179"); // expected float but provided boolean
    checkErrorExpr("varfloat = vardouble", "0xA0179"); // expected float but provided double
    checkErrorExpr("vardouble = true", "0xA0179"); // expected double but provided boolean
    checkErrorExpr("vardouble = false", "0xA0179"); // expected double but provided boolean
    checkErrorExpr("vardouble = varboolean", "0xA0179"); // expected double but provided boolean
    checkErrorExpr("varint=\"Hello\"", "0xA0179"); // not possible because int = (int) String returns a cvarsting error
    checkErrorExpr("varintMetre = varintSecond", "0xA0179");
    checkErrorExpr("varintMetre = varint", "0xA0179");
    checkErrorExpr("varintMetre = 1.0m", "0xA0179");
  }

}
