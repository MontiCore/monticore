/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types3;

import com.google.common.collect.Lists;
import de.monticore.expressions.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import de.monticore.expressions.combineexpressionswithliterals._symboltable.ICombineExpressionsWithLiteralsArtifactScope;
import de.monticore.expressions.combineexpressionswithliterals._symboltable.ICombineExpressionsWithLiteralsGlobalScope;
import de.monticore.expressions.combineexpressionswithliterals._symboltable.ICombineExpressionsWithLiteralsScope;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.expressionsbasis.ExpressionsBasisMill;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisTraverser;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsGlobalScope;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.oosymbols.OOSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsGlobalScope;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsScope;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static de.monticore.types.check.SymTypeExpressionFactory.createGenerics;
import static de.monticore.types.check.SymTypeExpressionFactory.createIntersection;
import static de.monticore.types.check.SymTypeExpressionFactory.createTuple;
import static de.monticore.types.check.SymTypeExpressionFactory.createTypeArray;
import static de.monticore.types.check.SymTypeExpressionFactory.createTypeObject;
import static de.monticore.types.check.SymTypeExpressionFactory.createTypeVariable;
import static de.monticore.types.check.SymTypeExpressionFactory.createUnion;
import static de.monticore.types3.util.DefsTypesForTests.*;

public class CommonExpressionTypeVisitorTest
    extends AbstractTypeVisitorTest {

  @BeforeEach
  public void init() {
    setupValues();
    SymTypeRelations.init();
  }

  @Test
  public void deriveFromPlusExpression() throws IOException {
    checkExpr("varbyte + varbyte", "int"); // + applicable to byte, byte, result is int
    checkExpr("varshort + varshort", "int"); // + applicable to short, short, result is int
    checkExpr("varint + varint", "int"); // + applicable to int, int, result is int
    checkExpr("varlong + varlong", "long"); // + applicable to long, long, result is long
    checkExpr("varfloat + varfloat", "float"); // + applicable to float, float, result is float
    checkExpr("vardouble + vardouble", "double"); // + applicable to double, double, result is double
    checkExpr("0 + 0", "int"); // expected int and provided int
    checkExpr("127 + 1", "int"); // expected int and provided int
    checkExpr("32767 + 1", "int"); // expected int and provided int
    checkExpr("65536 + 1", "int"); // expected int and provided int
    checkExpr("2147483647 + 0", "int"); // expected int and provided int
    checkExpr("varchar + varchar", "int"); // + applicable to char, char, result is int
    checkExpr("3 + \"Hallo\"", "String"); // example with String
    checkExpr("1m + 1m", "[m]<int>");
    checkExpr("1m + 1.0m", "[m]<double>");
    checkExpr("1m^2 + 1m^2", "[m^2]<int>");
    checkExpr("1m + 1km", "[m]<int>");
  }

  @Test
  public void deriveFromPlusExpressionLifted() throws IOException {
    checkExpr("varbyte + (true ? varint : vardouble)", "double");
    checkExpr("(true ? varint : vardouble) + varbyte", "double");
    checkExpr("(true ? varint : vardouble) + (true ? varint : vardouble)", "double");
  }

  @Test
  public void testInvalidPlusExpression() throws IOException {
    checkErrorExpr("varchar = 65535 + 1", "0xA0179"); // expected char but provided int
    checkErrorExpr("varchar = 1 + 65535", "0xA0179"); // expected char but provided int
    checkErrorExpr("varchar = varchar + varchar", "0xA0179"); // expected char but provided int
    checkErrorExpr("varboolean + varboolean", "0xB0163"); // + not applicable to boolean, boolean
    checkErrorExpr("varboolean + varchar", "0xB0163"); // + not applicable to boolean, char
    checkErrorExpr("varboolean + varbyte", "0xB0163"); // + not applicable to boolean, byte
    checkErrorExpr("varboolean + varshort", "0xB0163"); // + not applicable to boolean, short
    checkErrorExpr("varboolean + varint", "0xB0163"); // + not applicable to boolean, int
    checkErrorExpr("varboolean + varlong", "0xB0163"); // + not applicable to boolean, long
    checkErrorExpr("varboolean + varfloat", "0xB0163"); // + not applicable to boolean, float
    checkErrorExpr("varboolean + vardouble", "0xB0163"); // + not applicable to boolean, double
    checkErrorExpr("varchar + varboolean", "0xB0163"); // + not applicable to char, boolean
    checkErrorExpr("varbyte + varboolean", "0xB0163"); // + not applicable to byte, boolean
    checkErrorExpr("varshort + varboolean", "0xB0163"); // + not applicable to short, boolean
    checkErrorExpr("varint + varboolean", "0xB0163"); // + not applicable to int, boolean
    checkErrorExpr("varlong + varboolean", "0xB0163"); // + not applicable to long, boolean
    checkErrorExpr("varfloat + varboolean", "0xB0163"); // + not applicable to float, boolean
    checkErrorExpr("vardouble + varboolean", "0xB0163"); // + not applicable to double, boolean
    checkErrorExpr("varchar = 1 + 1l", "0xA0179"); // expected char but provided long
    checkErrorExpr("varchar = 1l + 1", "0xA0179"); // expected char but provided long
    checkErrorExpr("varchar = 1 + 0.1f", "0xA0179"); // expected char but provided float
    checkErrorExpr("varchar = 0.1f + 1", "0xA0179"); // expected char but provided float
    checkErrorExpr("varchar = 1 + 0.1", "0xA0179"); // expected char but provided double
    checkErrorExpr("varchar = 0.1 + 1", "0xA0179"); // expected char but provided double
    checkErrorExpr("varbyte = 127 + 1", "0xA0179"); // expected byte but provided int
    checkErrorExpr("varbyte = 1 + 127", "0xA0179"); // expected byte but provided int
    checkErrorExpr("varbyte = varbyte + varbyte", "0xA0179"); // expected byte but provided int
    checkErrorExpr("varbyte = 1 + 1l", "0xA0179"); // expected byte but provided long
    checkErrorExpr("varbyte = 1l + 1", "0xA0179"); // expected byte but provided long
    checkErrorExpr("varbyte = 1 + 0.1f", "0xA0179"); // expected byte but provided float
    checkErrorExpr("varbyte = 0.1f + 1", "0xA0179"); // expected byte but provided float
    checkErrorExpr("varbyte = 1 + 0.1", "0xA0179"); // expected byte but provided double
    checkErrorExpr("varbyte = 0.1 + 1", "0xA0179"); // expected byte but provided double
    checkErrorExpr("varshort = 32767 + 1", "0xA0179"); // expected short but provided int
    checkErrorExpr("varshort = 1 + 32767", "0xA0179"); // expected short but provided int
    checkErrorExpr("varshort = varshort + varshort", "0xA0179"); // expected short but provided int
    checkErrorExpr("varshort = 1 + 1l", "0xA0179"); // expected short but provided long
    checkErrorExpr("varshort = 1l + 1", "0xA0179"); // expected short but provided long
    checkErrorExpr("varshort = 1 + 0.1f", "0xA0179"); // expected short but provided float
    checkErrorExpr("varshort = 0.1f + 1", "0xA0179"); // expected short but provided float
    checkErrorExpr("varshort = 1 + 0.1", "0xA0179"); // expected short but provided double
    checkErrorExpr("varshort = 0.1 + 1", "0xA0179"); // expected short but provided double
    checkErrorExpr("varint = 1 + 1l", "0xA0179"); // expected int but provided long
    checkErrorExpr("varint = 1l + 1", "0xA0179"); // expected int but provided long
    checkErrorExpr("varint = 1 + 0.1f", "0xA0179"); // expected int but provided float
    checkErrorExpr("varint = 0.1f + 1", "0xA0179"); // expected int but provided float
    checkErrorExpr("varint = 1 + 0.1", "0xA0179"); // expected int but provided double
    checkErrorExpr("varint = 0.1 + 1", "0xA0179"); // expected int but provided double
    checkErrorExpr("varlong = 1l + 0.1f", "0xA0179"); // expected long but provided float
    checkErrorExpr("varlong = 0.1f + 1l", "0xA0179"); // expected long but provided float
    checkErrorExpr("varlong = 1l + 0.1", "0xA0179"); // expected long but provided double
    checkErrorExpr("varlong = 0.1 + 1l", "0xA0179"); // expected long but provided double
    checkErrorExpr("varfloat = 0.1f + 0.1", "0xA0179"); // expected float but provided double
    checkErrorExpr("varfloat = 0.1 + 0.1f", "0xA0179"); // expected float but provided double
    checkErrorExpr("1m + 1", "0xB0163");
    checkErrorExpr("1 + 1m", "0xB0163");
    checkErrorExpr("1m + 1s", "0xB0163");
  }

  @Test
  public void deriveFromMinusExpression() throws IOException {
    checkExpr("1 - 1", "int"); // expected int and provided int
    checkExpr("-128 - 1", "int"); // expected int and provided int
    checkExpr("-32768 - 1", "int"); // expected int and provided int
    checkExpr("-2147483648 - 0", "int"); // expected int and provided int
    checkExpr("varchar - varchar", "int"); // - applicable to char, char, result is int
    checkExpr("varbyte - varbyte", "int"); // - applicable to byte, byte, result is int
    checkExpr("varshort - varshort", "int"); // - applicable to short, short, result is int
    checkExpr("varint - varint", "int"); // - applicable to int, int, result is int
    checkExpr("varlong - varlong", "long"); // - applicable to long, long, result is long
    checkExpr("varfloat - varfloat", "float"); // - applicable to float, float, result is float
    checkExpr("vardouble - vardouble", "double"); // - applicable to double, double, result is double
    checkExpr("1m - 1m", "[m]<int>");
    checkExpr("1m - 1.0m", "[m]<double>");
    checkExpr("1m^2 - 1m^2", "[m^2]<int>");
    checkExpr("1m - 1km", "[m]<int>");
  }

  @Test
  public void deriveFromMinusExpressionLifted() throws IOException {
    checkExpr("varbyte - (true ? varint : vardouble)", "double");
    checkExpr("(true ? varint : vardouble) - varbyte", "double");
    checkExpr("(true ? varint : vardouble) - (true ? varint : vardouble)", "double");
  }

  @Test
  public void testInvalidMinusExpression() throws IOException {
    checkErrorExpr("varboolean - varboolean", "0xB0163"); // - not applicable to boolean, boolean
    checkErrorExpr("varboolean - varchar", "0xB0163"); // - not applicable to boolean, char
    checkErrorExpr("varboolean - varbyte", "0xB0163"); // - not applicable to boolean, byte
    checkErrorExpr("varboolean - varshort", "0xB0163"); // - not applicable to boolean, short
    checkErrorExpr("varboolean - varint", "0xB0163"); // - not applicable to boolean, int
    checkErrorExpr("varboolean - varlong", "0xB0163"); // - not applicable to boolean, long
    checkErrorExpr("varboolean - varfloat", "0xB0163"); // - not applicable to boolean, float
    checkErrorExpr("varboolean - vardouble", "0xB0163"); // - not applicable to boolean, double
    checkErrorExpr("varchar - varboolean", "0xB0163"); // - not applicable to char, boolean
    checkErrorExpr("varbyte - varboolean", "0xB0163"); // - not applicable to byte, boolean
    checkErrorExpr("varshort - varboolean", "0xB0163"); // - not applicable to short, boolean
    checkErrorExpr("varint - varboolean", "0xB0163"); // - not applicable to int, boolean
    checkErrorExpr("varlong - varboolean", "0xB0163"); // - not applicable to long, boolean
    checkErrorExpr("varfloat - varboolean", "0xB0163"); // - not applicable to float, boolean
    checkErrorExpr("vardouble - varboolean", "0xB0163"); // - not applicable to double, boolean
    checkErrorExpr("varchar = varchar - varchar", "0xA0179"); // expected char but provided int
    checkErrorExpr("varchar = 1 - 1l", "0xA0179"); // expected char but provided long
    checkErrorExpr("varchar = 1l - 1", "0xA0179"); // expected char but provided long
    checkErrorExpr("varchar = 1 - 0.1f", "0xA0179"); // expected char but provided float
    checkErrorExpr("varchar = 0.1f - 1", "0xA0179"); // expected char but provided float
    checkErrorExpr("varchar = 1 - 0.1", "0xA0179"); // expected char but provided double
    checkErrorExpr("varchar = 0.1 - 1", "0xA0179"); // expected char but provided double
    checkErrorExpr("varbyte = 1 - 1l", "0xA0179"); // expected byte but provided long
    checkErrorExpr("varbyte = 1l - 1", "0xA0179"); // expected byte but provided long
    checkErrorExpr("varbyte = 1 - 0.1f", "0xA0179"); // expected byte but provided float
    checkErrorExpr("varbyte = 0.1f - 1", "0xA0179"); // expected byte but provided float
    checkErrorExpr("varbyte = 1 - 0.1", "0xA0179"); // expected byte but provided double
    checkErrorExpr("varbyte = 0.1 - 1", "0xA0179"); // expected byte but provided double
    checkErrorExpr("varshort = varshort - varshort", "0xA0179"); // expected short but provided int
    checkErrorExpr("varshort = 1 - 1l", "0xA0179"); // expected short but provided long
    checkErrorExpr("varshort = 1l - 1", "0xA0179"); // expected short but provided long
    checkErrorExpr("varshort = 1 - 0.1f", "0xA0179"); // expected short but provided float
    checkErrorExpr("varshort = 0.1f - 1", "0xA0179"); // expected short but provided float
    checkErrorExpr("varshort = 1 - 0.1", "0xA0179"); // expected short but provided double
    checkErrorExpr("varshort = 0.1 - 1", "0xA0179"); // expected short but provided double
    checkErrorExpr("varint = 1 - 1l", "0xA0179"); // expected int but provided long
    checkErrorExpr("varint = 1l - 1", "0xA0179"); // expected int but provided long
    checkErrorExpr("varint = 1 - 0.1f", "0xA0179"); // expected int but provided float
    checkErrorExpr("varint = 0.1f - 1", "0xA0179"); // expected int but provided float
    checkErrorExpr("varint = 1 - 0.1", "0xA0179"); // expected int but provided double
    checkErrorExpr("varint = 0.1 - 1", "0xA0179"); // expected int but provided double
    checkErrorExpr("varlong = 1l - 0.1f", "0xA0179"); // expected long but provided float
    checkErrorExpr("varlong = 0.1f - 1l", "0xA0179"); // expected long but provided float
    checkErrorExpr("varlong = 1l - 0.1", "0xA0179"); // expected long but provided double
    checkErrorExpr("varlong = 0.1 - 1l", "0xA0179"); // expected long but provided double
    checkErrorExpr("varfloat = 0.1f - 0.1", "0xA0179"); // expected float but provided double
    checkErrorExpr("varfloat = 0.1 - 0.1f", "0xA0179"); // expected float but provided double
    checkErrorExpr("1m - 1", "0xB0163");
    checkErrorExpr("1 - 1m", "0xB0163");
    checkErrorExpr("1m - 1s", "0xB0163");
  }

  @Test
  public void deriveFromMultExpression() throws IOException {
    checkExpr("0 * 0", "int"); // expected int and provided int
    checkExpr("2147483647 * 1", "int"); // expected int and provided int
    checkExpr("1 * 2147483647", "int"); // expected int and provided int
    checkExpr("varchar * varchar", "int"); // * applicable to char, char, result is int
    checkExpr("varbyte * varbyte", "int"); // * applicable to byte, byte, result is int
    checkExpr("varshort * varshort", "int"); // * applicable to short, short, result is int
    checkExpr("varint * varint", "int"); // * applicable to int, int, result is int
    checkExpr("varlong * varlong", "long"); // * applicable to long, long, result is long
    checkExpr("varfloat * varfloat", "float"); // * applicable to float, float, result is float
    checkExpr("vardouble * vardouble", "double"); // * applicable to double, double, result is double
    checkExpr("1m * 1m", "[m^2]<int>");
    checkExpr("1m * 1.0m", "[m^2]<double>");
    checkExpr("1m^2 * 1m", "[m^3]<int>");
    checkExpr("1m * 1km", "[m^2]<int>");
    checkExpr("1m * 1", "[m]<int>");
    checkExpr("1 * 1m", "[m]<int>");
    checkExpr("1m * 1s", "[m^1s]<int>");
  }

  @Test
  public void testInvalidMultExpression() throws IOException {
    checkErrorExpr("varboolean * varboolean", "0xB0163"); // * not applicable to boolean, boolean
    checkErrorExpr("varboolean * varchar", "0xB0163"); // * not applicable to boolean, char
    checkErrorExpr("varboolean * varbyte", "0xB0163"); // * not applicable to boolean, byte
    checkErrorExpr("varboolean * varshort", "0xB0163"); // * not applicable to boolean, short
    checkErrorExpr("varboolean * varint", "0xB0163"); // * not applicable to boolean, int
    checkErrorExpr("varboolean * varlong", "0xB0163"); // * not applicable to boolean, long
    checkErrorExpr("varboolean * varfloat", "0xB0163"); // * not applicable to boolean, float
    checkErrorExpr("varboolean * vardouble", "0xB0163"); // * not applicable to boolean, double
    checkErrorExpr("varchar * varboolean", "0xB0163"); // * not applicable to char, boolean
    checkErrorExpr("varbyte * varboolean", "0xB0163"); // * not applicable to byte, boolean
    checkErrorExpr("varshort * varboolean", "0xB0163"); // * not applicable to short, boolean
    checkErrorExpr("varint * varboolean", "0xB0163"); // * not applicable to int, boolean
    checkErrorExpr("varlong * varboolean", "0xB0163"); // * not applicable to long, boolean
    checkErrorExpr("varfloat * varboolean", "0xB0163"); // * not applicable to float, boolean
    checkErrorExpr("vardouble * varboolean", "0xB0163"); // * not applicable to double, boolean
    checkErrorExpr("varbyte = 64 * 2", "0xA0179"); // expected byte but provided int
    checkErrorExpr("varbyte = varbyte * varbyte", "0xA0179"); // expected byte but provided int
    checkErrorExpr("varbyte = 1 * 1l", "0xA0179"); // expected byte but provided long
    checkErrorExpr("varbyte = 1l * 1", "0xA0179"); // expected byte but provided long
    checkErrorExpr("varbyte = 1 * 0.1f", "0xA0179"); // expected byte but provided float
    checkErrorExpr("varbyte = 0.1f * 1", "0xA0179"); // expected byte but provided float
    checkErrorExpr("varbyte = 1 * 0.1", "0xA0179"); // expected byte but provided double
    checkErrorExpr("varbyte = 0.1 * 1", "0xA0179"); // expected byte but provided double
    checkErrorExpr("varshort = 16384 * 2", "0xA0179"); // expected short but provided int
    checkErrorExpr("varshort = varshort * varshort", "0xA0179"); // expected short but provided int
    checkErrorExpr("varshort = 1 * 1l", "0xA0179"); // expected short but provided long
    checkErrorExpr("varshort = 1l * 1", "0xA0179"); // expected short but provided long
    checkErrorExpr("varshort = 1 * 0.1f", "0xA0179"); // expected short but provided float
    checkErrorExpr("varshort = 0.1f * 1", "0xA0179"); // expected short but provided float
    checkErrorExpr("varshort = 1 * 0.1", "0xA0179"); // expected short but provided double
    checkErrorExpr("varshort = 0.1 * 1", "0xA0179"); // expected short but provided double
    checkErrorExpr("varint = 1 * 1l", "0xA0179"); // expected int but provided long
    checkErrorExpr("varint = 1l * 1", "0xA0179"); // expected int but provided long
    checkErrorExpr("varint = 1 * 0.1f", "0xA0179"); // expected int but provided float
    checkErrorExpr("varint = 0.1f * 1", "0xA0179"); // expected int but provided float
    checkErrorExpr("varint = 1 * 0.1", "0xA0179"); // expected int but provided double
    checkErrorExpr("varint = 0.1 * 1", "0xA0179"); // expected int but provided double
    checkErrorExpr("varlong = 1l * 0.1f", "0xA0179"); // expected long but provided float
    checkErrorExpr("varlong = 0.1f * 1l", "0xA0179"); // expected long but provided float
    checkErrorExpr("varlong = 1l * 0.1", "0xA0179"); // expected long but provided double
    checkErrorExpr("varlong = 0.1 * 1l", "0xA0179"); // expected long but provided double
    checkErrorExpr("varfloat = 0.1f * 0.1", "0xA0179"); // expected float but provided double
    checkErrorExpr("varfloat = 0.1 * 0.1f", "0xA0179"); // expected float but provided double
    checkErrorExpr("varchar = 32768 * 2", "0xA0179"); // expected char but provided int
    checkErrorExpr("varchar = varchar * varchar", "0xA0179"); // expected char but provided int
    checkErrorExpr("varchar = 1 * 1l", "0xA0179"); // expected char but provided long
    checkErrorExpr("varchar = 1l * 1", "0xA0179"); // expected char but provided long
    checkErrorExpr("varchar = 1 * 0.1f", "0xA0179"); // expected char but provided float
    checkErrorExpr("varchar = 0.1f * 1", "0xA0179"); // expected char but provided float
    checkErrorExpr("varchar = 1 * 0.1", "0xA0179"); // expected char but provided double
    checkErrorExpr("varchar = 0.1 * 1", "0xA0179"); // expected char but provided double
    checkExpr("1m * 1m", "[m^2]<int>");
    checkExpr("1m * 1.0m", "[m^2]<double>");
    checkExpr("1m^2 * 1m", "[m^3]<int>");
    checkExpr("1m * 1km", "[m^2]<int>");
    checkExpr("1m * 1", "[m]<int>");
    checkExpr("1 * 1m", "[m]<int>");
    checkExpr("1m * 1s", "[m^1s]<int>");
  }

  @Test
  public void deriveFromDivideExpression() throws IOException {
    checkExpr("2147483647 / 1", "int"); // expected int and provided int
    checkExpr("1 / 2147483647", "int"); // expected int and provided int
    checkExpr("varchar / varchar", "int"); // / applicable to char, char, result is int
    checkExpr("varbyte / varbyte", "int"); // / applicable to byte, byte, result is int
    checkExpr("varshort / varshort", "int"); // / applicable to short, short, result is int
    checkExpr("varint / varint", "int"); // / applicable to int, int, result is int
    checkExpr("varlong / varlong", "long"); // / applicable to long, long, result is long
    checkExpr("varfloat / varfloat", "float"); // / applicable to float, float, result is float
    checkExpr("vardouble / vardouble", "double"); // / applicable to double, double, result is double
    checkExpr("1m / 1m", "int");
    checkExpr("1m / 1.0m", "double");
    checkExpr("1m^2 / 1m", "[m]<int>");
    checkExpr("1m / 1km", "int");
    checkExpr("1m / 1", "[m]<int>");
    checkExpr("1 / 1m", "[1/m]<int>");
    checkExpr("1m / 1s", "[m/s]<int>");
    checkExpr("1m^1s / 1s", "[m]<int>");
  }

  @Test
  public void testInvalidDivideExpression() throws IOException {
    checkErrorExpr("varboolean / varboolean", "0xB0163"); // / not applicable to boolean, boolean
    checkErrorExpr("varboolean / varchar", "0xB0163"); // / not applicable to boolean, char
    checkErrorExpr("varboolean / varbyte", "0xB0163"); // / not applicable to boolean, byte
    checkErrorExpr("varboolean / varshort", "0xB0163"); // / not applicable to boolean, short
    checkErrorExpr("varboolean / varint", "0xB0163"); // / not applicable to boolean, int
    checkErrorExpr("varboolean / varlong", "0xB0163"); // / not applicable to boolean, long
    checkErrorExpr("varboolean / varfloat", "0xB0163"); // / not applicable to boolean, float
    checkErrorExpr("varboolean / vardouble", "0xB0163"); // / not applicable to boolean, double
    checkErrorExpr("varchar / varboolean", "0xB0163"); // / not applicable to char, boolean
    checkErrorExpr("varbyte / varboolean", "0xB0163"); // / not applicable to byte, boolean
    checkErrorExpr("varshort / varboolean", "0xB0163"); // / not applicable to short, boolean
    checkErrorExpr("varint / varboolean", "0xB0163"); // / not applicable to int, boolean
    checkErrorExpr("varlong / varboolean", "0xB0163"); // / not applicable to long, boolean
    checkErrorExpr("varfloat / varboolean", "0xB0163"); // / not applicable to float, boolean
    checkErrorExpr("vardouble / varboolean", "0xB0163"); // / not applicable to double, boolean
    checkErrorExpr("varchar = 65536 / 1", "0xA0179"); // expected char but provided int
    checkErrorExpr("varchar = varchar / varchar", "0xA0179"); // expected char but provided int
    checkErrorExpr("varchar = 1 / 1l", "0xA0179"); // expected char but provided long
    checkErrorExpr("varchar = 1l / 1", "0xA0179"); // expected char but provided long
    checkErrorExpr("varchar = 1 / 0.1f", "0xA0179"); // expected char but provided float
    checkErrorExpr("varchar = 0.1f / 1", "0xA0179"); // expected char but provided float
    checkErrorExpr("varchar = 1 / 0.1", "0xA0179"); // expected char but provided double
    checkErrorExpr("varchar = 0.1 / 1", "0xA0179"); // expected char but provided double
    checkErrorExpr("varbyte = 128 / 1", "0xA0179"); // expected byte but provided int
    checkErrorExpr("varbyte = varbyte / varbyte", "0xA0179"); // expected byte but provided int
    checkErrorExpr("varbyte = 1 / 1l", "0xA0179"); // expected byte but provided long
    checkErrorExpr("varbyte = 1l / 1", "0xA0179"); // expected byte but provided long
    checkErrorExpr("varbyte = 1 / 0.1f", "0xA0179"); // expected byte but provided float
    checkErrorExpr("varbyte = 0.1f / 1", "0xA0179"); // expected byte but provided float
    checkErrorExpr("varbyte = 1 / 0.1", "0xA0179"); // expected byte but provided double
    checkErrorExpr("varbyte = 0.1 / 1", "0xA0179"); // expected byte but provided double
    checkErrorExpr("varshort = 32768 / 1", "0xA0179"); // expected short but provided int
    checkErrorExpr("varshort = varshort / varshort", "0xA0179"); // expected short but provided int
    checkErrorExpr("varshort = 1 / 1l", "0xA0179"); // expected short but provided long
    checkErrorExpr("varshort = 1l / 1", "0xA0179"); // expected short but provided long
    checkErrorExpr("varshort = 1 / 0.1f", "0xA0179"); // expected short but provided float
    checkErrorExpr("varshort = 0.1f / 1", "0xA0179"); // expected short but provided float
    checkErrorExpr("varshort = 1 / 0.1", "0xA0179"); // expected short but provided double
    checkErrorExpr("varshort = 0.1 / 1", "0xA0179"); // expected short but provided double
    checkErrorExpr("varint = 1 / 1l", "0xA0179"); // expected int but provided long
    checkErrorExpr("varint = 1l / 1", "0xA0179"); // expected int but provided long
    checkErrorExpr("varint = 1 / 0.1f", "0xA0179"); // expected int but provided float
    checkErrorExpr("varint = 0.1f / 1", "0xA0179"); // expected int but provided float
    checkErrorExpr("varint = 1 / 0.1", "0xA0179"); // expected int but provided double
    checkErrorExpr("varint = 0.1 / 1", "0xA0179"); // expected int but provided double
    checkErrorExpr("varlong = 1l / 0.1f", "0xA0179"); // expected long but provided float
    checkErrorExpr("varlong = 0.1f / 1l", "0xA0179"); // expected long but provided float
    checkErrorExpr("varlong = 1l / 0.1", "0xA0179"); // expected long but provided double
    checkErrorExpr("varlong = 0.1 / 1l", "0xA0179"); // expected long but provided double
    checkErrorExpr("varfloat = 0.1f / 0.1", "0xA0179"); // expected float but provided double
    checkErrorExpr("varfloat = 0.1 / 0.1f", "0xA0179"); // expected float but provided double
  }

  @Test
  public void deriveFromModuloExpression() throws IOException {
    checkExpr("2147483647 % 1", "int"); // expected int and provided int
    checkExpr("1 % 2147483647", "int"); // expected int and provided int
    checkExpr("varchar % varchar", "int"); // % applicable to char, char, result is int
    checkExpr("varbyte % varbyte", "int"); // % applicable to byte, byte, result is int
    checkExpr("varshort % varshort", "int"); // % applicable to short, short, result is int
    checkExpr("varint % varint", "int"); // % applicable to int, int, result is int
    checkExpr("varlong % varlong", "long"); // % applicable to long, long, result is long
    checkExpr("varfloat % varfloat", "float"); // % applicable to float, float, result is float
    checkExpr("vardouble % vardouble", "double"); // % applicable to double, double, result is double
    checkExpr("1m % 1m", "[m]<int>");
    checkExpr("1m % 1.0m", "[m]<double>");
    checkExpr("1m^2 % 1m^2", "[m^2]<int>");
    checkExpr("1m % 1km", "[m]<int>");
  }

  @Test
  public void testInvalidModuloExpression() throws IOException {
    checkErrorExpr("varboolean % varboolean", "0xB0163"); // % not applicable to boolean, boolean
    checkErrorExpr("varboolean % varchar", "0xB0163"); // % not applicable to boolean, char
    checkErrorExpr("varboolean % varbyte", "0xB0163"); // % not applicable to boolean, byte
    checkErrorExpr("varboolean % varshort", "0xB0163"); // % not applicable to boolean, short
    checkErrorExpr("varboolean % varint", "0xB0163"); // % not applicable to boolean, int
    checkErrorExpr("varboolean % varlong", "0xB0163"); // % not applicable to boolean, long
    checkErrorExpr("varboolean % varfloat", "0xB0163"); // % not applicable to boolean, float
    checkErrorExpr("varboolean % vardouble", "0xB0163"); // % not applicable to boolean, double
    checkErrorExpr("varchar % varboolean", "0xB0163"); // % not applicable to char, boolean
    checkErrorExpr("varbyte % varboolean", "0xB0163"); // % not applicable to byte, boolean
    checkErrorExpr("varshort % varboolean", "0xB0163"); // % not applicable to short, boolean
    checkErrorExpr("varint % varboolean", "0xB0163"); // % not applicable to int, boolean
    checkErrorExpr("varlong % varboolean", "0xB0163"); // % not applicable to long, boolean
    checkErrorExpr("varfloat % varboolean", "0xB0163"); // % not applicable to float, boolean
    checkErrorExpr("vardouble % varboolean", "0xB0163"); // % not applicable to double, boolean
    checkErrorExpr("varchar = varchar % varchar", "0xA0179"); // expected char but provided int
    checkErrorExpr("varchar = 1 % 1l", "0xA0179"); // expected char but provided long
    checkErrorExpr("varchar = 1l % 1", "0xA0179"); // expected char but provided long
    checkErrorExpr("varchar = 1 % 0.1f", "0xA0179"); // expected char but provided float
    checkErrorExpr("varchar = 0.1f % 1", "0xA0179"); // expected char but provided float
    checkErrorExpr("varchar = 1 % 0.1", "0xA0179"); // expected char but provided double
    checkErrorExpr("varchar = 0.1 % 1", "0xA0179"); // expected char but provided double
    checkErrorExpr("varbyte = varbyte % varbyte", "0xA0179"); // expected byte but provided int
    checkErrorExpr("varbyte = 1 % 1l", "0xA0179"); // expected byte but provided long
    checkErrorExpr("varbyte = 1l % 1", "0xA0179"); // expected byte but provided long
    checkErrorExpr("varbyte = 1 % 0.1f", "0xA0179"); // expected byte but provided float
    checkErrorExpr("varbyte = 0.1f % 1", "0xA0179"); // expected byte but provided float
    checkErrorExpr("varbyte = 1 % 0.1", "0xA0179"); // expected byte but provided double
    checkErrorExpr("varbyte = 0.1 % 1", "0xA0179"); // expected byte but provided double
    checkErrorExpr("varshort = varshort % varshort", "0xA0179"); // expected short but provided int
    checkErrorExpr("varshort = 1 % 1l", "0xA0179"); // expected short but provided long
    checkErrorExpr("varshort = 1l % 1", "0xA0179"); // expected short but provided long
    checkErrorExpr("varshort = 1 % 0.1f", "0xA0179"); // expected short but provided float
    checkErrorExpr("varshort = 0.1f % 1", "0xA0179"); // expected short but provided float
    checkErrorExpr("varshort = 1 % 0.1", "0xA0179"); // expected short but provided double
    checkErrorExpr("varshort = 0.1 % 1", "0xA0179"); // expected short but provided double
    checkErrorExpr("varint = 1 % 1l", "0xA0179"); // expected int but provided long
    checkErrorExpr("varint = 1l % 1", "0xA0179"); // expected int but provided long
    checkErrorExpr("varint = 1 % 0.1f", "0xA0179"); // expected int but provided float
    checkErrorExpr("varint = 0.1f % 1", "0xA0179"); // expected int but provided float
    checkErrorExpr("varint = 1 % 0.1", "0xA0179"); // expected int but provided double
    checkErrorExpr("varint = 0.1 % 1", "0xA0179"); // expected int but provided double
    checkErrorExpr("varlong = 1l % 0.1f", "0xA0179"); // expected long but provided float
    checkErrorExpr("varlong = 0.1f % 1l", "0xA0179"); // expected long but provided float
    checkErrorExpr("varlong = 1l % 0.1", "0xA0179"); // expected long but provided double
    checkErrorExpr("varlong = 0.1 % 1l", "0xA0179"); // expected long but provided double
    checkErrorExpr("varfloat = 0.1f % 0.1", "0xA0179"); // expected float but provided double
    checkErrorExpr("varfloat = 0.1 % 0.1f", "0xA0179"); // expected float but provided double
    checkErrorExpr("1m % 1", "0xB0163");
    checkErrorExpr("1 % 1m", "0xB0163");
    checkErrorExpr("1m % 1s", "0xB0163");
  }

  @Test
  public void deriveFromLessEqualExpression() throws IOException {
    checkExpr("varbyte <= varbyte", "boolean"); // <= applicable to byte, byte, result is boolean
    checkExpr("varbyte <= varshort", "boolean"); // <= applicable to byte, short, result is boolean
    checkExpr("varbyte <= varchar", "boolean"); // <= applicable to byte, char, result is boolean
    checkExpr("varbyte <= varint", "boolean"); // <= applicable to byte, int, result is boolean
    checkExpr("varbyte <= varlong", "boolean"); // <= applicable to byte, long, result is boolean
    checkExpr("varbyte <= varfloat", "boolean"); // <= applicable to byte, float, result is boolean
    checkExpr("varbyte <= vardouble", "boolean"); // <= applicable to byte, double, result is boolean
    checkExpr("varshort <= varbyte", "boolean"); // <= applicable to short, byte, result is boolean
    checkExpr("varshort <= varshort", "boolean"); // <= applicable to short, short, result is boolean
    checkExpr("varshort <= varchar", "boolean"); // <= applicable to short, char, result is boolean
    checkExpr("varshort <= varint", "boolean"); // <= applicable to short, int, result is boolean
    checkExpr("varshort <= varlong", "boolean"); // <= applicable to short, long, result is boolean
    checkExpr("varshort <= varfloat", "boolean"); // <= applicable to short, float, result is boolean
    checkExpr("varshort <= vardouble", "boolean"); // <= applicable to short, double, result is boolean
    checkExpr("varchar <= varbyte", "boolean"); // <= applicable to char, byte, result is boolean
    checkExpr("varchar <= varshort", "boolean"); // <= applicable to char, short, result is boolean
    checkExpr("varchar <= varchar", "boolean"); // <= applicable to char, char, result is boolean
    checkExpr("varchar <= varint", "boolean"); // <= applicable to char, int, result is boolean
    checkExpr("varchar <= varlong", "boolean"); // <= applicable to char, long, result is boolean
    checkExpr("varchar <= varfloat", "boolean"); // <= applicable to char, float, result is boolean
    checkExpr("varchar <= vardouble", "boolean"); // <= applicable to char, double, result is boolean
    checkExpr("varint <= varbyte", "boolean"); // <= applicable to int, byte, result is boolean
    checkExpr("varint <= varshort", "boolean"); // <= applicable to int, short, result is boolean
    checkExpr("varint <= varchar", "boolean"); // <= applicable to int, char, result is boolean
    checkExpr("varint <= varint", "boolean"); // <= applicable to int, int, result is boolean
    checkExpr("varint <= varlong", "boolean"); // <= applicable to int, long, result is boolean
    checkExpr("varint <= varfloat", "boolean"); // <= applicable to int, float, result is boolean
    checkExpr("varint <= vardouble", "boolean"); // <= applicable to int, double, result is boolean
    checkExpr("varlong <= varbyte", "boolean"); // <= applicable to long, byte, result is boolean
    checkExpr("varlong <= varshort", "boolean"); // <= applicable to long, short, result is boolean
    checkExpr("varlong <= varchar", "boolean"); // <= applicable to long, char, result is boolean
    checkExpr("varlong <= varint", "boolean"); // <= applicable to long, int, result is boolean
    checkExpr("varlong <= varlong", "boolean"); // <= applicable to long, long, result is boolean
    checkExpr("varlong <= varfloat", "boolean"); // <= applicable to long, float, result is boolean
    checkExpr("varlong <= vardouble", "boolean"); // <= applicable to long, double, result is boolean
    checkExpr("varfloat <= varbyte", "boolean"); // <= applicable to float, byte, result is boolean
    checkExpr("varfloat <= varshort", "boolean"); // <= applicable to float, short, result is boolean
    checkExpr("varfloat <= varchar", "boolean"); // <= applicable to float, char, result is boolean
    checkExpr("varfloat <= varint", "boolean"); // <= applicable to float, int, result is boolean
    checkExpr("varfloat <= varlong", "boolean"); // <= applicable to float, long, result is boolean
    checkExpr("varfloat <= varfloat", "boolean"); // <= applicable to float, float, result is boolean
    checkExpr("varfloat <= vardouble", "boolean"); // <= applicable to float, double, result is boolean
    checkExpr("vardouble <= varbyte", "boolean"); // <= applicable to double, byte, result is boolean
    checkExpr("vardouble <= varshort", "boolean"); // <= applicable to double, short, result is boolean
    checkExpr("vardouble <= varchar", "boolean"); // <= applicable to double, char, result is boolean
    checkExpr("vardouble <= varint", "boolean"); // <= applicable to double, int, result is boolean
    checkExpr("vardouble <= varlong", "boolean"); // <= applicable to double, long, result is boolean
    checkExpr("vardouble <= varfloat", "boolean"); // <= applicable to double, float, result is boolean
    checkExpr("vardouble <= vardouble", "boolean"); // <= applicable to double, double, result is boolean
    checkExpr("1m <= 1m", "boolean");
    checkExpr("1m <= 1km", "boolean");
  }

  @Test
  public void testInvalidLessEqualExpression() throws IOException {
    checkErrorExpr("varboolean <= varchar", "0xB0167"); // <= not applicable to boolean, char
    checkErrorExpr("varboolean <= varbyte", "0xB0167"); // <= not applicable to boolean, byte
    checkErrorExpr("varboolean <= varshort", "0xB0167"); // <= not applicable to boolean, short
    checkErrorExpr("varboolean <= varint", "0xB0167"); // <= not applicable to boolean, int
    checkErrorExpr("varboolean <= varlong", "0xB0167"); // <= not applicable to boolean, long
    checkErrorExpr("varboolean <= varfloat", "0xB0167"); // <= not applicable to boolean, float
    checkErrorExpr("varboolean <= vardouble", "0xB0167"); // <= not applicable to boolean, double
    checkErrorExpr("varchar <= varboolean", "0xB0167"); // <= not applicable to char, boolean
    checkErrorExpr("varbyte <= varboolean", "0xB0167"); // <= not applicable to byte, boolean
    checkErrorExpr("varshort <= varboolean", "0xB0167"); // <= not applicable to short, boolean
    checkErrorExpr("varint <= varboolean", "0xB0167"); // <= not applicable to int, boolean
    checkErrorExpr("varlong <= varboolean", "0xB0167"); // <= not applicable to long, boolean
    checkErrorExpr("varfloat <= varboolean", "0xB0167"); // <= not applicable to float, boolean
    checkErrorExpr("vardouble <= varboolean", "0xB0167"); // <= not applicable to double, boolean
    checkErrorExpr("varchar = varchar <= varchar", "0xA0179"); // expected char but provided boolean
    checkErrorExpr("varbyte = varbyte <= varbyte", "0xA0179"); // expected byte but provided boolean
    checkErrorExpr("varshort = varshort <= varshort", "0xA0179"); // expected short but provided boolean
    checkErrorExpr("varint = varint <= varint", "0xA0179"); // expected int but provided boolean
    checkErrorExpr("varlong = varlong <= varlong", "0xA0179"); // expected long but provided boolean
    checkErrorExpr("varfloat = varfloat <= varfloat", "0xA0179"); // expected float but provided boolean
    checkErrorExpr("vardouble = vardouble <= vardouble", "0xA0179"); // expected double but provided boolean
    checkErrorExpr("1m <= 1s", "0xB0167");
    checkErrorExpr("1m <= 1", "0xB0167");
  }

  @Test
  public void deriveFromGreaterEqualExpression() throws IOException {
    checkExpr("varbyte >= varbyte", "boolean"); // >= applicable to byte, byte, result is boolean
    checkExpr("varbyte >= varshort", "boolean"); // >= applicable to byte, short, result is boolean
    checkExpr("varbyte >= varchar", "boolean"); // >= applicable to byte, char, result is boolean
    checkExpr("varbyte >= varint", "boolean"); // >= applicable to byte, int, result is boolean
    checkExpr("varbyte >= varlong", "boolean"); // >= applicable to byte, long, result is boolean
    checkExpr("varbyte >= varfloat", "boolean"); // >= applicable to byte, float, result is boolean
    checkExpr("varbyte >= vardouble", "boolean"); // >= applicable to byte, double, result is boolean
    checkExpr("varshort >= varbyte", "boolean"); // >= applicable to short, byte, result is boolean
    checkExpr("varshort >= varshort", "boolean"); // >= applicable to short, short, result is boolean
    checkExpr("varshort >= varchar", "boolean"); // >= applicable to short, char, result is boolean
    checkExpr("varshort >= varint", "boolean"); // >= applicable to short, int, result is boolean
    checkExpr("varshort >= varlong", "boolean"); // >= applicable to short, long, result is boolean
    checkExpr("varshort >= varfloat", "boolean"); // >= applicable to short, float, result is boolean
    checkExpr("varshort >= vardouble", "boolean"); // >= applicable to short, double, result is boolean
    checkExpr("varchar >= varbyte", "boolean"); // >= applicable to char, byte, result is boolean
    checkExpr("varchar >= varshort", "boolean"); // >= applicable to char, short, result is boolean
    checkExpr("varchar >= varchar", "boolean"); // >= applicable to char, char, result is boolean
    checkExpr("varchar >= varint", "boolean"); // >= applicable to char, int, result is boolean
    checkExpr("varchar >= varlong", "boolean"); // >= applicable to char, long, result is boolean
    checkExpr("varchar >= varfloat", "boolean"); // >= applicable to char, float, result is boolean
    checkExpr("varchar >= vardouble", "boolean"); // >= applicable to char, double, result is boolean
    checkExpr("varint >= varbyte", "boolean"); // >= applicable to int, byte, result is boolean
    checkExpr("varint >= varshort", "boolean"); // >= applicable to int, short, result is boolean
    checkExpr("varint >= varchar", "boolean"); // >= applicable to int, char, result is boolean
    checkExpr("varint >= varint", "boolean"); // >= applicable to int, int, result is boolean
    checkExpr("varint >= varlong", "boolean"); // >= applicable to int, long, result is boolean
    checkExpr("varint >= varfloat", "boolean"); // >= applicable to int, float, result is boolean
    checkExpr("varint >= vardouble", "boolean"); // >= applicable to int, double, result is boolean
    checkExpr("varlong >= varbyte", "boolean"); // >= applicable to long, byte, result is boolean
    checkExpr("varlong >= varshort", "boolean"); // >= applicable to long, short, result is boolean
    checkExpr("varlong >= varchar", "boolean"); // >= applicable to long, char, result is boolean
    checkExpr("varlong >= varint", "boolean"); // >= applicable to long, int, result is boolean
    checkExpr("varlong >= varlong", "boolean"); // >= applicable to long, long, result is boolean
    checkExpr("varlong >= varfloat", "boolean"); // >= applicable to long, float, result is boolean
    checkExpr("varlong >= vardouble", "boolean"); // >= applicable to long, double, result is boolean
    checkExpr("varfloat >= varbyte", "boolean"); // >= applicable to float, byte, result is boolean
    checkExpr("varfloat >= varshort", "boolean"); // >= applicable to float, short, result is boolean
    checkExpr("varfloat >= varchar", "boolean"); // >= applicable to float, char, result is boolean
    checkExpr("varfloat >= varint", "boolean"); // >= applicable to float, int, result is boolean
    checkExpr("varfloat >= varlong", "boolean"); // >= applicable to float, long, result is boolean
    checkExpr("varfloat >= varfloat", "boolean"); // >= applicable to float, float, result is boolean
    checkExpr("varfloat >= vardouble", "boolean"); // >= applicable to float, double, result is boolean
    checkExpr("vardouble >= varbyte", "boolean"); // >= applicable to double, byte, result is boolean
    checkExpr("vardouble >= varshort", "boolean"); // >= applicable to double, short, result is boolean
    checkExpr("vardouble >= varchar", "boolean"); // >= applicable to double, char, result is boolean
    checkExpr("vardouble >= varint", "boolean"); // >= applicable to double, int, result is boolean
    checkExpr("vardouble >= varlong", "boolean"); // >= applicable to double, long, result is boolean
    checkExpr("vardouble >= varfloat", "boolean"); // >= applicable to double, float, result is boolean
    checkExpr("vardouble >= vardouble", "boolean"); // >= applicable to double, double, result is boolean
    checkExpr("1m >= 1m", "boolean");
    checkExpr("1m >= 1km", "boolean");
  }

  @Test
  public void testInvalidGreaterEqualExpression() throws IOException {
    checkErrorExpr("varboolean >= varchar", "0xB0167"); // >= not applicable to boolean, char
    checkErrorExpr("varboolean >= varbyte", "0xB0167"); // >= not applicable to boolean, byte
    checkErrorExpr("varboolean >= varshort", "0xB0167"); // >= not applicable to boolean, short
    checkErrorExpr("varboolean >= varint", "0xB0167"); // >= not applicable to boolean, int
    checkErrorExpr("varboolean >= varlong", "0xB0167"); // >= not applicable to boolean, long
    checkErrorExpr("varboolean >= varfloat", "0xB0167"); // >= not applicable to boolean, float
    checkErrorExpr("varboolean >= vardouble", "0xB0167"); // >= not applicable to boolean, double
    checkErrorExpr("varchar >= varboolean", "0xB0167"); // >= not applicable to char, boolean
    checkErrorExpr("varbyte >= varboolean", "0xB0167"); // >= not applicable to byte, boolean
    checkErrorExpr("varshort >= varboolean", "0xB0167"); // >= not applicable to short, boolean
    checkErrorExpr("varint >= varboolean", "0xB0167"); // >= not applicable to int, boolean
    checkErrorExpr("varlong >= varboolean", "0xB0167"); // >= not applicable to long, boolean
    checkErrorExpr("varfloat >= varboolean", "0xB0167"); // >= not applicable to float, boolean
    checkErrorExpr("vardouble >= varboolean", "0xB0167"); // >= not applicable to double, boolean
    checkErrorExpr("varchar = varchar >= varchar", "0xA0179"); // expected char but provided boolean
    checkErrorExpr("varbyte = varbyte >= varbyte", "0xA0179"); // expected byte but provided boolean
    checkErrorExpr("varshort = varshort >= varshort", "0xA0179"); // expected short but provided boolean
    checkErrorExpr("varint = varint >= varint", "0xA0179"); // expected int but provided boolean
    checkErrorExpr("varlong = varlong >= varlong", "0xA0179"); // expected long but provided boolean
    checkErrorExpr("varfloat = varfloat >= varfloat", "0xA0179"); // expected float but provided boolean
    checkErrorExpr("vardouble = vardouble >= vardouble", "0xA0179"); // expected double but provided boolean
    checkErrorExpr("1m >= 1s", "0xB0167");
    checkErrorExpr("1m >= 1", "0xB0167");
  }

  @Test
  public void deriveFromLessThanExpression() throws IOException {
    checkExpr("varbyte < varbyte", "boolean"); // < applicable to byte, byte, result is boolean
    checkExpr("varbyte < varshort", "boolean"); // < applicable to byte, short, result is boolean
    checkExpr("varbyte < varchar", "boolean"); // < applicable to byte, char, result is boolean
    checkExpr("varbyte < varint", "boolean"); // < applicable to byte, int, result is boolean
    checkExpr("varbyte < varlong", "boolean"); // < applicable to byte, long, result is boolean
    checkExpr("varbyte < varfloat", "boolean"); // < applicable to byte, float, result is boolean
    checkExpr("varbyte < vardouble", "boolean"); // < applicable to byte, double, result is boolean
    checkExpr("varshort < varbyte", "boolean"); // < applicable to short, byte, result is boolean
    checkExpr("varshort < varshort", "boolean"); // < applicable to short, short, result is boolean
    checkExpr("varshort < varchar", "boolean"); // < applicable to short, char, result is boolean
    checkExpr("varshort < varint", "boolean"); // < applicable to short, int, result is boolean
    checkExpr("varshort < varlong", "boolean"); // < applicable to short, long, result is boolean
    checkExpr("varshort < varfloat", "boolean"); // < applicable to short, float, result is boolean
    checkExpr("varshort < vardouble", "boolean"); // < applicable to short, double, result is boolean
    checkExpr("varchar < varbyte", "boolean"); // < applicable to char, byte, result is boolean
    checkExpr("varchar < varshort", "boolean"); // < applicable to char, short, result is boolean
    checkExpr("varchar < varchar", "boolean"); // < applicable to char, char, result is boolean
    checkExpr("varchar < varint", "boolean"); // < applicable to char, int, result is boolean
    checkExpr("varchar < varlong", "boolean"); // < applicable to char, long, result is boolean
    checkExpr("varchar < varfloat", "boolean"); // < applicable to char, float, result is boolean
    checkExpr("varchar < vardouble", "boolean"); // < applicable to char, double, result is boolean
    checkExpr("varint < varbyte", "boolean"); // < applicable to int, byte, result is boolean
    checkExpr("varint < varshort", "boolean"); // < applicable to int, short, result is boolean
    checkExpr("varint < varchar", "boolean"); // < applicable to int, char, result is boolean
    checkExpr("varint < varint", "boolean"); // < applicable to int, int, result is boolean
    checkExpr("varint < varlong", "boolean"); // < applicable to int, long, result is boolean
    checkExpr("varint < varfloat", "boolean"); // < applicable to int, float, result is boolean
    checkExpr("varint < vardouble", "boolean"); // < applicable to int, double, result is boolean
    checkExpr("varlong < varbyte", "boolean"); // < applicable to long, byte, result is boolean
    checkExpr("varlong < varshort", "boolean"); // < applicable to long, short, result is boolean
    checkExpr("varlong < varchar", "boolean"); // < applicable to long, char, result is boolean
    checkExpr("varlong < varint", "boolean"); // < applicable to long, int, result is boolean
    checkExpr("varlong < varlong", "boolean"); // < applicable to long, long, result is boolean
    checkExpr("varlong < varfloat", "boolean"); // < applicable to long, float, result is boolean
    checkExpr("varlong < vardouble", "boolean"); // < applicable to long, double, result is boolean
    checkExpr("varfloat < varbyte", "boolean"); // < applicable to float, byte, result is boolean
    checkExpr("varfloat < varshort", "boolean"); // < applicable to float, short, result is boolean
    checkExpr("varfloat < varchar", "boolean"); // < applicable to float, char, result is boolean
    checkExpr("varfloat < varint", "boolean"); // < applicable to float, int, result is boolean
    checkExpr("varfloat < varlong", "boolean"); // < applicable to float, long, result is boolean
    checkExpr("varfloat < varfloat", "boolean"); // < applicable to float, float, result is boolean
    checkExpr("varfloat < vardouble", "boolean"); // < applicable to float, double, result is boolean
    checkExpr("vardouble < varbyte", "boolean"); // < applicable to double, byte, result is boolean
    checkExpr("vardouble < varshort", "boolean"); // < applicable to double, short, result is boolean
    checkExpr("vardouble < varchar", "boolean"); // < applicable to double, char, result is boolean
    checkExpr("vardouble < varint", "boolean"); // < applicable to double, int, result is boolean
    checkExpr("vardouble < varlong", "boolean"); // < applicable to double, long, result is boolean
    checkExpr("vardouble < varfloat", "boolean"); // < applicable to double, float, result is boolean
    checkExpr("vardouble < vardouble", "boolean"); // < applicable to double, double, result is boolean
    checkExpr("1m < 1m", "boolean");
    checkExpr("1m < 1km", "boolean");
  }

  @Test
  public void testInvalidLessThanExpression() throws IOException {
    checkErrorExpr("varboolean < varchar", "0xB0167"); // < not applicable to boolean, char
    checkErrorExpr("varboolean < varbyte", "0xB0167"); // < not applicable to boolean, byte
    checkErrorExpr("varboolean < varshort", "0xB0167"); // < not applicable to boolean, short
    checkErrorExpr("varboolean < varint", "0xB0167"); // < not applicable to boolean, int
    checkErrorExpr("varboolean < varlong", "0xB0167"); // < not applicable to boolean, long
    checkErrorExpr("varboolean < varfloat", "0xB0167"); // < not applicable to boolean, float
    checkErrorExpr("varboolean < vardouble", "0xB0167"); // < not applicable to boolean, double
    checkErrorExpr("varchar < varboolean", "0xB0167"); // < not applicable to char, boolean
    checkErrorExpr("varbyte < varboolean", "0xB0167"); // < not applicable to byte, boolean
    checkErrorExpr("varshort < varboolean", "0xB0167"); // < not applicable to short, boolean
    checkErrorExpr("varint < varboolean", "0xB0167"); // < not applicable to int, boolean
    checkErrorExpr("varlong < varboolean", "0xB0167"); // < not applicable to long, boolean
    checkErrorExpr("varfloat < varboolean", "0xB0167"); // < not applicable to float, boolean
    checkErrorExpr("vardouble < varboolean", "0xB0167"); // < not applicable to double, boolean
    checkErrorExpr("varchar = varchar < varchar", "0xA0179"); // expected char but provided boolean
    checkErrorExpr("varbyte = varbyte < varbyte", "0xA0179"); // expected byte but provided boolean
    checkErrorExpr("varshort = varshort < varshort", "0xA0179"); // expected short but provided boolean
    checkErrorExpr("varint = varint < varint", "0xA0179"); // expected int but provided boolean
    checkErrorExpr("varlong = varlong < varlong", "0xA0179"); // expected long but provided boolean
    checkErrorExpr("varfloat = varfloat < varfloat", "0xA0179"); // expected float but provided boolean
    checkErrorExpr("vardouble = vardouble < vardouble", "0xA0179"); // expected double but provided boolean
    checkErrorExpr("1m < 1s", "0xB0167");
    checkErrorExpr("1m < 1", "0xB0167");
  }

  @Test
  public void deriveFromGreaterThanExpression() throws IOException {
    checkExpr("varbyte > varbyte", "boolean"); // > applicable to byte, byte, result is boolean
    checkExpr("varbyte > varshort", "boolean"); // > applicable to byte, short, result is boolean
    checkExpr("varbyte > varchar", "boolean"); // > applicable to byte, char, result is boolean
    checkExpr("varbyte > varint", "boolean"); // > applicable to byte, int, result is boolean
    checkExpr("varbyte > varlong", "boolean"); // > applicable to byte, long, result is boolean
    checkExpr("varbyte > varfloat", "boolean"); // > applicable to byte, float, result is boolean
    checkExpr("varbyte > vardouble", "boolean"); // > applicable to byte, double, result is boolean
    checkExpr("varshort > varbyte", "boolean"); // > applicable to short, byte, result is boolean
    checkExpr("varshort > varshort", "boolean"); // > applicable to short, short, result is boolean
    checkExpr("varshort > varchar", "boolean"); // > applicable to short, char, result is boolean
    checkExpr("varshort > varint", "boolean"); // > applicable to short, int, result is boolean
    checkExpr("varshort > varlong", "boolean"); // > applicable to short, long, result is boolean
    checkExpr("varshort > varfloat", "boolean"); // > applicable to short, float, result is boolean
    checkExpr("varshort > vardouble", "boolean"); // > applicable to short, double, result is boolean
    checkExpr("varchar > varbyte", "boolean"); // > applicable to char, byte, result is boolean
    checkExpr("varchar > varshort", "boolean"); // > applicable to char, short, result is boolean
    checkExpr("varchar > varchar", "boolean"); // > applicable to char, char, result is boolean
    checkExpr("varchar > varint", "boolean"); // > applicable to char, int, result is boolean
    checkExpr("varchar > varlong", "boolean"); // > applicable to char, long, result is boolean
    checkExpr("varchar > varfloat", "boolean"); // > applicable to char, float, result is boolean
    checkExpr("varchar > vardouble", "boolean"); // > applicable to char, double, result is boolean
    checkExpr("varint > varbyte", "boolean"); // > applicable to int, byte, result is boolean
    checkExpr("varint > varshort", "boolean"); // > applicable to int, short, result is boolean
    checkExpr("varint > varchar", "boolean"); // > applicable to int, char, result is boolean
    checkExpr("varint > varint", "boolean"); // > applicable to int, int, result is boolean
    checkExpr("varint > varlong", "boolean"); // > applicable to int, long, result is boolean
    checkExpr("varint > varfloat", "boolean"); // > applicable to int, float, result is boolean
    checkExpr("varint > vardouble", "boolean"); // > applicable to int, double, result is boolean
    checkExpr("varlong > varbyte", "boolean"); // > applicable to long, byte, result is boolean
    checkExpr("varlong > varshort", "boolean"); // > applicable to long, short, result is boolean
    checkExpr("varlong > varchar", "boolean"); // > applicable to long, char, result is boolean
    checkExpr("varlong > varint", "boolean"); // > applicable to long, int, result is boolean
    checkExpr("varlong > varlong", "boolean"); // > applicable to long, long, result is boolean
    checkExpr("varlong > varfloat", "boolean"); // > applicable to long, float, result is boolean
    checkExpr("varlong > vardouble", "boolean"); // > applicable to long, double, result is boolean
    checkExpr("varfloat > varbyte", "boolean"); // > applicable to float, byte, result is boolean
    checkExpr("varfloat > varshort", "boolean"); // > applicable to float, short, result is boolean
    checkExpr("varfloat > varchar", "boolean"); // > applicable to float, char, result is boolean
    checkExpr("varfloat > varint", "boolean"); // > applicable to float, int, result is boolean
    checkExpr("varfloat > varlong", "boolean"); // > applicable to float, long, result is boolean
    checkExpr("varfloat > varfloat", "boolean"); // > applicable to float, float, result is boolean
    checkExpr("varfloat > vardouble", "boolean"); // > applicable to float, double, result is boolean
    checkExpr("vardouble > varbyte", "boolean"); // > applicable to double, byte, result is boolean
    checkExpr("vardouble > varshort", "boolean"); // > applicable to double, short, result is boolean
    checkExpr("vardouble > varchar", "boolean"); // > applicable to double, char, result is boolean
    checkExpr("vardouble > varint", "boolean"); // > applicable to double, int, result is boolean
    checkExpr("vardouble > varlong", "boolean"); // > applicable to double, long, result is boolean
    checkExpr("vardouble > varfloat", "boolean"); // > applicable to double, float, result is boolean
    checkExpr("vardouble > vardouble", "boolean"); // > applicable to double, double, result is boolean
    checkExpr("1m > 1m", "boolean");
    checkExpr("1m > 1km", "boolean");
  }

  @Test
  public void testInvalidGreaterThanExpression() throws IOException {
    checkErrorExpr("varboolean > varchar", "0xB0167"); // > not applicable to boolean, char
    checkErrorExpr("varboolean > varbyte", "0xB0167"); // > not applicable to boolean, byte
    checkErrorExpr("varboolean > varshort", "0xB0167"); // > not applicable to boolean, short
    checkErrorExpr("varboolean > varint", "0xB0167"); // > not applicable to boolean, int
    checkErrorExpr("varboolean > varlong", "0xB0167"); // > not applicable to boolean, long
    checkErrorExpr("varboolean > varfloat", "0xB0167"); // > not applicable to boolean, float
    checkErrorExpr("varboolean > vardouble", "0xB0167"); // > not applicable to boolean, double
    checkErrorExpr("varchar > varboolean", "0xB0167"); // > not applicable to char, boolean
    checkErrorExpr("varbyte > varboolean", "0xB0167"); // > not applicable to byte, boolean
    checkErrorExpr("varshort > varboolean", "0xB0167"); // > not applicable to short, boolean
    checkErrorExpr("varint > varboolean", "0xB0167"); // > not applicable to int, boolean
    checkErrorExpr("varlong > varboolean", "0xB0167"); // > not applicable to long, boolean
    checkErrorExpr("varfloat > varboolean", "0xB0167"); // > not applicable to float, boolean
    checkErrorExpr("vardouble > varboolean", "0xB0167"); // > not applicable to double, boolean
    checkErrorExpr("varchar = varchar > varchar", "0xA0179"); // expected char but provided boolean
    checkErrorExpr("varbyte = varbyte > varbyte", "0xA0179"); // expected byte but provided boolean
    checkErrorExpr("varshort = varshort > varshort", "0xA0179"); // expected short but provided boolean
    checkErrorExpr("varint = varint > varint", "0xA0179"); // expected int but provided boolean
    checkErrorExpr("varlong = varlong > varlong", "0xA0179"); // expected long but provided boolean
    checkErrorExpr("varfloat = varfloat > varfloat", "0xA0179"); // expected float but provided boolean
    checkErrorExpr("vardouble = vardouble > vardouble", "0xA0179"); // expected double but provided boolean
    checkErrorExpr("1m > 1s", "0xB0167");
    checkErrorExpr("1m > 1", "0xB0167");
  }

  @Test
  public void deriveFromEqualsExpression() throws IOException {
    checkExpr("varbyte == varbyte", "boolean"); // == applicable to byte, byte, result is boolean
    checkExpr("varbyte == varshort", "boolean"); // == applicable to byte, short, result is boolean
    checkExpr("varbyte == varchar", "boolean"); // == applicable to byte, char, result is boolean
    checkExpr("varbyte == varint", "boolean"); // == applicable to byte, int, result is boolean
    checkExpr("varbyte == varlong", "boolean"); // == applicable to byte, long, result is boolean
    checkExpr("varbyte == varfloat", "boolean"); // == applicable to byte, float, result is boolean
    checkExpr("varbyte == vardouble", "boolean"); // == applicable to byte, double, result is boolean
    checkExpr("varshort == varbyte", "boolean"); // == applicable to short, byte, result is boolean
    checkExpr("varshort == varshort", "boolean"); // == applicable to short, short, result is boolean
    checkExpr("varshort == varchar", "boolean"); // == applicable to short, char, result is boolean
    checkExpr("varshort == varint", "boolean"); // == applicable to short, int, result is boolean
    checkExpr("varshort == varlong", "boolean"); // == applicable to short, long, result is boolean
    checkExpr("varshort == varfloat", "boolean"); // == applicable to short, float, result is boolean
    checkExpr("varshort == vardouble", "boolean"); // == applicable to short, double, result is boolean
    checkExpr("varchar == varbyte", "boolean"); // == applicable to char, byte, result is boolean
    checkExpr("varchar == varshort", "boolean"); // == applicable to char, short, result is boolean
    checkExpr("varchar == varchar", "boolean"); // == applicable to char, char, result is boolean
    checkExpr("varchar == varint", "boolean"); // == applicable to char, int, result is boolean
    checkExpr("varchar == varlong", "boolean"); // == applicable to char, long, result is boolean
    checkExpr("varchar == varfloat", "boolean"); // == applicable to char, float, result is boolean
    checkExpr("varchar == vardouble", "boolean"); // == applicable to char, double, result is boolean
    checkExpr("varint == varbyte", "boolean"); // == applicable to int, byte, result is boolean
    checkExpr("varint == varshort", "boolean"); // == applicable to int, short, result is boolean
    checkExpr("varint == varchar", "boolean"); // == applicable to int, char, result is boolean
    checkExpr("varint == varint", "boolean"); // == applicable to int, int, result is boolean
    checkExpr("varint == varlong", "boolean"); // == applicable to int, long, result is boolean
    checkExpr("varint == varfloat", "boolean"); // == applicable to int, float, result is boolean
    checkExpr("varint == vardouble", "boolean"); // == applicable to int, double, result is boolean
    checkExpr("varlong == varbyte", "boolean"); // == applicable to long, byte, result is boolean
    checkExpr("varlong == varshort", "boolean"); // == applicable to long, short, result is boolean
    checkExpr("varlong == varchar", "boolean"); // == applicable to long, char, result is boolean
    checkExpr("varlong == varint", "boolean"); // == applicable to long, int, result is boolean
    checkExpr("varlong == varlong", "boolean"); // == applicable to long, long, result is boolean
    checkExpr("varlong == varfloat", "boolean"); // == applicable to long, float, result is boolean
    checkExpr("varlong == vardouble", "boolean"); // == applicable to long, double, result is boolean
    checkExpr("varfloat == varbyte", "boolean"); // == applicable to float, byte, result is boolean
    checkExpr("varfloat == varshort", "boolean"); // == applicable to float, short, result is boolean
    checkExpr("varfloat == varchar", "boolean"); // == applicable to float, char, result is boolean
    checkExpr("varfloat == varint", "boolean"); // == applicable to float, int, result is boolean
    checkExpr("varfloat == varlong", "boolean"); // == applicable to float, long, result is boolean
    checkExpr("varfloat == varfloat", "boolean"); // == applicable to float, float, result is boolean
    checkExpr("varfloat == vardouble", "boolean"); // == applicable to float, double, result is boolean
    checkExpr("vardouble == varbyte", "boolean"); // == applicable to double, byte, result is boolean
    checkExpr("vardouble == varshort", "boolean"); // == applicable to double, short, result is boolean
    checkExpr("vardouble == varchar", "boolean"); // == applicable to double, char, result is boolean
    checkExpr("vardouble == varint", "boolean"); // == applicable to double, int, result is boolean
    checkExpr("vardouble == varlong", "boolean"); // == applicable to double, long, result is boolean
    checkExpr("vardouble == varfloat", "boolean"); // == applicable to double, float, result is boolean
    checkExpr("vardouble == vardouble", "boolean"); // == applicable to double, double, result is boolean
    checkExpr("student1 == student2", "boolean"); // example with two objects of the same class
    checkExpr("person1 == student1", "boolean"); // example with two objects in sub-supertype relation
    checkExpr("1m == 1m", "boolean");
    checkExpr("1m == 1km", "boolean");
  }

  @Test
  public void testInvalidEqualsExpression() throws IOException {
    checkErrorExpr("varboolean == varchar", "0xB0166"); // == not applicable to boolean, char
    checkErrorExpr("varboolean == varbyte", "0xB0166"); // == not applicable to boolean, byte
    checkErrorExpr("varboolean == varshort", "0xB0166"); // == not applicable to boolean, short
    checkErrorExpr("varboolean == varint", "0xB0166"); // == not applicable to boolean, int
    checkErrorExpr("varboolean == varlong", "0xB0166"); // == not applicable to boolean, long
    checkErrorExpr("varboolean == varfloat", "0xB0166"); // == not applicable to boolean, float
    checkErrorExpr("varboolean == vardouble", "0xB0166"); // == not applicable to boolean, double
    checkErrorExpr("varchar == varboolean", "0xB0166"); // == not applicable to char, boolean
    checkErrorExpr("varbyte == varboolean", "0xB0166"); // == not applicable to byte, boolean
    checkErrorExpr("varshort == varboolean", "0xB0166"); // == not applicable to short, boolean
    checkErrorExpr("varint == varboolean", "0xB0166"); // == not applicable to int, boolean
    checkErrorExpr("varlong == varboolean", "0xB0166"); // == not applicable to long, boolean
    checkErrorExpr("varfloat == varboolean", "0xB0166"); // == not applicable to float, boolean
    checkErrorExpr("vardouble == varboolean", "0xB0166"); // == not applicable to double, boolean
    checkErrorExpr("varchar = varchar == varchar", "0xA0179"); // expected char but provided boolean
    checkErrorExpr("varbyte = varbyte == varbyte", "0xA0179"); // expected byte but provided boolean
    checkErrorExpr("varshort = varshort == varshort", "0xA0179"); // expected short but provided boolean
    checkErrorExpr("varint = varint == varint", "0xA0179"); // expected int but provided boolean
    checkErrorExpr("varlong = varlong == varlong", "0xA0179"); // expected long but provided boolean
    checkErrorExpr("varfloat = varfloat == varfloat", "0xA0179"); // expected float but provided boolean
    checkErrorExpr("vardouble = vardouble == vardouble", "0xA0179"); // expected double but provided boolean
    checkErrorExpr("person1==varboolean", "0xB0166");
    checkErrorExpr("1m == 1s", "0xB0166");
    checkErrorExpr("1m == 1", "0xB0166");
  }

  @Test
  public void deriveFromNotEqualsExpression() throws IOException {
    checkExpr("varbyte != varbyte", "boolean"); // != applicable to byte, byte, result is boolean
    checkExpr("varbyte != varshort", "boolean"); // != applicable to byte, short, result is boolean
    checkExpr("varbyte != varchar", "boolean"); // != applicable to byte, char, result is boolean
    checkExpr("varbyte != varint", "boolean"); // != applicable to byte, int, result is boolean
    checkExpr("varbyte != varlong", "boolean"); // != applicable to byte, long, result is boolean
    checkExpr("varbyte != varfloat", "boolean"); // != applicable to byte, float, result is boolean
    checkExpr("varbyte != vardouble", "boolean"); // != applicable to byte, double, result is boolean
    checkExpr("varshort != varbyte", "boolean"); // != applicable to short, byte, result is boolean
    checkExpr("varshort != varshort", "boolean"); // != applicable to short, short, result is boolean
    checkExpr("varshort != varchar", "boolean"); // != applicable to short, char, result is boolean
    checkExpr("varshort != varint", "boolean"); // != applicable to short, int, result is boolean
    checkExpr("varshort != varlong", "boolean"); // != applicable to short, long, result is boolean
    checkExpr("varshort != varfloat", "boolean"); // != applicable to short, float, result is boolean
    checkExpr("varshort != vardouble", "boolean"); // != applicable to short, double, result is boolean
    checkExpr("varchar != varbyte", "boolean"); // != applicable to char, byte, result is boolean
    checkExpr("varchar != varshort", "boolean"); // != applicable to char, short, result is boolean
    checkExpr("varchar != varchar", "boolean"); // != applicable to char, char, result is boolean
    checkExpr("varchar != varint", "boolean"); // != applicable to char, int, result is boolean
    checkExpr("varchar != varlong", "boolean"); // != applicable to char, long, result is boolean
    checkExpr("varchar != varfloat", "boolean"); // != applicable to char, float, result is boolean
    checkExpr("varchar != vardouble", "boolean"); // != applicable to char, double, result is boolean
    checkExpr("varint != varbyte", "boolean"); // != applicable to int, byte, result is boolean
    checkExpr("varint != varshort", "boolean"); // != applicable to int, short, result is boolean
    checkExpr("varint != varchar", "boolean"); // != applicable to int, char, result is boolean
    checkExpr("varint != varint", "boolean"); // != applicable to int, int, result is boolean
    checkExpr("varint != varlong", "boolean"); // != applicable to int, long, result is boolean
    checkExpr("varint != varfloat", "boolean"); // != applicable to int, float, result is boolean
    checkExpr("varint != vardouble", "boolean"); // != applicable to int, double, result is boolean
    checkExpr("varlong != varbyte", "boolean"); // != applicable to long, byte, result is boolean
    checkExpr("varlong != varshort", "boolean"); // != applicable to long, short, result is boolean
    checkExpr("varlong != varchar", "boolean"); // != applicable to long, char, result is boolean
    checkExpr("varlong != varint", "boolean"); // != applicable to long, int, result is boolean
    checkExpr("varlong != varlong", "boolean"); // != applicable to long, long, result is boolean
    checkExpr("varlong != varfloat", "boolean"); // != applicable to long, float, result is boolean
    checkExpr("varlong != vardouble", "boolean"); // != applicable to long, double, result is boolean
    checkExpr("varfloat != varbyte", "boolean"); // != applicable to float, byte, result is boolean
    checkExpr("varfloat != varshort", "boolean"); // != applicable to float, short, result is boolean
    checkExpr("varfloat != varchar", "boolean"); // != applicable to float, char, result is boolean
    checkExpr("varfloat != varint", "boolean"); // != applicable to float, int, result is boolean
    checkExpr("varfloat != varlong", "boolean"); // != applicable to float, long, result is boolean
    checkExpr("varfloat != varfloat", "boolean"); // != applicable to float, float, result is boolean
    checkExpr("varfloat != vardouble", "boolean"); // != applicable to float, double, result is boolean
    checkExpr("vardouble != varbyte", "boolean"); // != applicable to double, byte, result is boolean
    checkExpr("vardouble != varshort", "boolean"); // != applicable to double, short, result is boolean
    checkExpr("vardouble != varchar", "boolean"); // != applicable to double, char, result is boolean
    checkExpr("vardouble != varint", "boolean"); // != applicable to double, int, result is boolean
    checkExpr("vardouble != varlong", "boolean"); // != applicable to double, long, result is boolean
    checkExpr("vardouble != varfloat", "boolean"); // != applicable to double, float, result is boolean
    checkExpr("vardouble != vardouble", "boolean"); // != applicable to double, double, result is boolean
    checkExpr("person1!=person2", "boolean"); // example with two objects of the same class
    checkExpr("student2!=person2", "boolean"); //example with two objects in sub-supertype relation
    checkExpr("1m != 1m", "boolean");
    checkExpr("1m != 1km", "boolean");
  }

  @Test
  public void testInvalidNotEqualsExpression() throws IOException {
    checkErrorExpr("aBoolean != aChar", "0xFD118"); // != not applicable to boolean, char
    checkErrorExpr("aBoolean != aByte", "0xFD118"); // != not applicable to boolean, byte
    checkErrorExpr("aBoolean != aShort", "0xFD118"); // != not applicable to boolean, short
    checkErrorExpr("aBoolean != anInt", "0xFD118"); // != not applicable to boolean, int
    checkErrorExpr("aBoolean != aLong", "0xFD118"); // != not applicable to boolean, long
    checkErrorExpr("aBoolean != aFloat", "0xFD118"); // != not applicable to boolean, float
    checkErrorExpr("aBoolean != aDouble", "0xFD118"); // != not applicable to boolean, double
    checkErrorExpr("aChar != aBoolean", "0xFD118"); // != not applicable to char, boolean
    checkErrorExpr("aByte != aBoolean", "0xFD118"); // != not applicable to byte, boolean
    checkErrorExpr("aShort != aBoolean", "0xFD118"); // != not applicable to short, boolean
    checkErrorExpr("anInt != aBoolean", "0xFD118"); // != not applicable to int, boolean
    checkErrorExpr("aLong != aBoolean", "0xFD118"); // != not applicable to long, boolean
    checkErrorExpr("aFloat != aBoolean", "0xFD118"); // != not applicable to float, boolean
    checkErrorExpr("aDouble != aBoolean", "0xFD118"); // != not applicable to double, boolean
    checkErrorExpr("aChar = aChar != aChar", "0xFD118"); // expected char but provided boolean
    checkErrorExpr("aByte = aByte != aByte", "0xFD118"); // expected byte but provided boolean
    checkErrorExpr("aShort = aShort != aShort", "0xFD118"); // expected short but provided boolean
    checkErrorExpr("anInt = anInt != anInt", "0xFD118"); // expected int but provided boolean
    checkErrorExpr("aLong = aLong != aLong", "0xFD118"); // expected long but provided boolean
    checkErrorExpr("aFloat = aFloat != aFloat", "0xFD118"); // expected float but provided boolean
    checkErrorExpr("aDouble = aDouble != aDouble", "0xFD118"); // expected double but provided boolean
    checkErrorExpr("person1!=varboolean", "0xB0166"); // person1 is a Person, foo is a boolean
    checkErrorExpr("1m != 1s", "0xB0166");
    checkErrorExpr("1m != 1", "0xB0166");
  }

  @Test
  public void deriveFromBooleanAndOpExpression() throws IOException {
    checkExpr("varboolean && varboolean", "boolean"); // && applicable to boolean, boolean, result is boolean
    checkExpr("true&&true", "boolean"); //only possible with two booleans
    checkExpr("(3<=4&&5>6)", "boolean");
  }

  @Test
  public void testInvalidAndOpExpression() throws IOException {
    checkErrorExpr("varboolean && varchar", "0xB0113"); // && not applicable to boolean, char
    checkErrorExpr("varboolean && varbyte", "0xB0113"); // && not applicable to boolean, byte
    checkErrorExpr("varboolean && varshort", "0xB0113"); // && not applicable to boolean, short
    checkErrorExpr("varboolean && varint", "0xB0113"); // && not applicable to boolean, int
    checkErrorExpr("varboolean && varlong", "0xB0113"); // && not applicable to boolean, long
    checkErrorExpr("varboolean && varfloat", "0xB0113"); // && not applicable to boolean, float
    checkErrorExpr("varboolean && vardouble", "0xB0113"); // && not applicable to boolean, double
    checkErrorExpr("varchar && varboolean", "0xB0113"); // && not applicable to char, boolean
    checkErrorExpr("varchar && varchar", "0xB0113"); // && not applicable to char, char
    checkErrorExpr("varchar && varbyte", "0xB0113"); // && not applicable to char, byte
    checkErrorExpr("varchar && varshort", "0xB0113"); // && not applicable to char, short
    checkErrorExpr("varchar && varint", "0xB0113"); // && not applicable to char, int
    checkErrorExpr("varchar && varlong", "0xB0113"); // && not applicable to char, long
    checkErrorExpr("varchar && varfloat", "0xB0113"); // && not applicable to char, float
    checkErrorExpr("varchar && vardouble", "0xB0113"); // && not applicable to char, double
    checkErrorExpr("varbyte && varboolean", "0xB0113"); // && not applicable to byte, boolean
    checkErrorExpr("varbyte && varchar", "0xB0113"); // && not applicable to byte, char
    checkErrorExpr("varbyte && varbyte", "0xB0113"); // && not applicable to byte, byte
    checkErrorExpr("varbyte && varshort", "0xB0113"); // && not applicable to byte, short
    checkErrorExpr("varbyte && varint", "0xB0113"); // && not applicable to byte, int
    checkErrorExpr("varbyte && varlong", "0xB0113"); // && not applicable to byte, long
    checkErrorExpr("varbyte && varfloat", "0xB0113"); // && not applicable to byte, float
    checkErrorExpr("varbyte && vardouble", "0xB0113"); // && not applicable to byte, double
    checkErrorExpr("varshort && varboolean", "0xB0113"); // && not applicable to short, boolean
    checkErrorExpr("varshort && varchar", "0xB0113"); // && not applicable to short, char
    checkErrorExpr("varshort && varbyte", "0xB0113"); // && not applicable to short, byte
    checkErrorExpr("varshort && varshort", "0xB0113"); // && not applicable to short, short
    checkErrorExpr("varshort && varint", "0xB0113"); // && not applicable to short, int
    checkErrorExpr("varshort && varlong", "0xB0113"); // && not applicable to short, long
    checkErrorExpr("varshort && varfloat", "0xB0113"); // && not applicable to short, float
    checkErrorExpr("varshort && vardouble", "0xB0113"); // && not applicable to short, double
    checkErrorExpr("varint && varboolean", "0xB0113"); // && not applicable to short, boolean
    checkErrorExpr("varint && varchar", "0xB0113"); // && not applicable to int, char
    checkErrorExpr("varint && varbyte", "0xB0113"); // && not applicable to int, byte
    checkErrorExpr("varint && varshort", "0xB0113"); // && not applicable to int, short
    checkErrorExpr("varint && varint", "0xB0113"); // && not applicable to int, int
    checkErrorExpr("varint && varlong", "0xB0113"); // && not applicable to int, long
    checkErrorExpr("varint && varfloat", "0xB0113"); // && not applicable to int, float
    checkErrorExpr("varint && vardouble", "0xB0113"); // && not applicable to int, double
    checkErrorExpr("varlong && varboolean", "0xB0113"); // && not applicable to long, boolean
    checkErrorExpr("varlong && varchar", "0xB0113"); // && not applicable to long, char
    checkErrorExpr("varlong && varbyte", "0xB0113"); // && not applicable to long, byte
    checkErrorExpr("varlong && varshort", "0xB0113"); // && not applicable to long, short
    checkErrorExpr("varlong && varint", "0xB0113"); // && not applicable to long, int
    checkErrorExpr("varlong && varlong", "0xB0113"); // && not applicable to long, long
    checkErrorExpr("varlong && varfloat", "0xB0113"); // && not applicable to long, float
    checkErrorExpr("varlong && vardouble", "0xB0113"); // && not applicable to long, double
    checkErrorExpr("varfloat && varboolean", "0xB0113"); // && not applicable to float, boolean
    checkErrorExpr("varfloat && varchar", "0xB0113"); // && not applicable to float, char
    checkErrorExpr("varfloat && varbyte", "0xB0113"); // && not applicable to float, byte
    checkErrorExpr("varfloat && varshort", "0xB0113"); // && not applicable to float, short
    checkErrorExpr("varfloat && varint", "0xB0113"); // && not applicable to float, int
    checkErrorExpr("varfloat && varlong", "0xB0113"); // && not applicable to float, long
    checkErrorExpr("varfloat && varfloat", "0xB0113"); // && not applicable to float, float
    checkErrorExpr("varfloat && vardouble", "0xB0113"); // && not applicable to float, double
    checkErrorExpr("vardouble && varboolean", "0xB0113"); // && not applicable to double, boolean
    checkErrorExpr("vardouble && varchar", "0xB0113"); // && not applicable to double, char
    checkErrorExpr("vardouble && varbyte", "0xB0113"); // && not applicable to double, byte
    checkErrorExpr("vardouble && varshort", "0xB0113"); // && not applicable to double, short
    checkErrorExpr("vardouble && varint", "0xB0113"); // && not applicable to double, int
    checkErrorExpr("vardouble && varlong", "0xB0113"); // && not applicable to double, long
    checkErrorExpr("vardouble && varfloat", "0xB0113"); // && not applicable to double, float
    checkErrorExpr("vardouble && vardouble", "0xB0113"); // && not applicable to double, double
    checkErrorExpr("varchar = varboolean && varboolean", "0xA0179"); // expected char but provided boolean
    checkErrorExpr("varbyte = varboolean && varboolean", "0xA0179"); // expected byte but provided boolean
    checkErrorExpr("varshort = varboolean && varboolean", "0xA0179"); // expected short but provided boolean
    checkErrorExpr("varint = varboolean && varboolean", "0xA0179"); // expected int but provided boolean
    checkErrorExpr("varlong = varboolean && varboolean", "0xA0179"); // expected long but provided boolean
    checkErrorExpr("varfloat = varboolean && varboolean", "0xA0179"); // expected float but provided boolean
    checkErrorExpr("vardouble = varboolean && varboolean", "0xA0179"); // expected double but provided boolean
    checkErrorExpr("1m && 1m", "0xB0113");
  }

  @Test
  public void deriveFromBooleanOrOpExpression() throws IOException {
    checkExpr("varboolean || varboolean", "boolean"); // || applicable to boolean, boolean, result is boolean
    checkExpr("true||false", "boolean"); //only possible with two booleans
    checkExpr("(3<=4.5f||5.3>6)", "boolean");
  }

  @Test
  public void testInvalidOrOpExpression() throws IOException {
    checkErrorExpr("varboolean || varchar", "0xB0113"); // || not applicable to boolean, char
    checkErrorExpr("varboolean || varbyte", "0xB0113"); // || not applicable to boolean, byte
    checkErrorExpr("varboolean || varshort", "0xB0113"); // || not applicable to boolean, short
    checkErrorExpr("varboolean || varint", "0xB0113"); // || not applicable to boolean, int
    checkErrorExpr("varboolean || varlong", "0xB0113"); // || not applicable to boolean, long
    checkErrorExpr("varboolean || varfloat", "0xB0113"); // || not applicable to boolean, float
    checkErrorExpr("varboolean || vardouble", "0xB0113"); // || not applicable to boolean, double
    checkErrorExpr("varchar || varboolean", "0xB0113"); // || not applicable to char, boolean
    checkErrorExpr("varchar || varchar", "0xB0113"); // || not applicable to char, char
    checkErrorExpr("varchar || varbyte", "0xB0113"); // || not applicable to char, byte
    checkErrorExpr("varchar || varshort", "0xB0113"); // || not applicable to char, short
    checkErrorExpr("varchar || varint", "0xB0113"); // || not applicable to char, int
    checkErrorExpr("varchar || varlong", "0xB0113"); // || not applicable to char, long
    checkErrorExpr("varchar || varfloat", "0xB0113"); // || not applicable to char, float
    checkErrorExpr("varchar || vardouble", "0xB0113"); // || not applicable to char, double
    checkErrorExpr("varbyte || varboolean", "0xB0113"); // || not applicable to byte, boolean
    checkErrorExpr("varbyte || varchar", "0xB0113"); // || not applicable to byte, char
    checkErrorExpr("varbyte || varbyte", "0xB0113"); // || not applicable to byte, byte
    checkErrorExpr("varbyte || varshort", "0xB0113"); // || not applicable to byte, short
    checkErrorExpr("varbyte || varint", "0xB0113"); // || not applicable to byte, int
    checkErrorExpr("varbyte || varlong", "0xB0113"); // || not applicable to byte, long
    checkErrorExpr("varbyte || varfloat", "0xB0113"); // || not applicable to byte, float
    checkErrorExpr("varbyte || vardouble", "0xB0113"); // || not applicable to byte, double
    checkErrorExpr("varshort || varboolean", "0xB0113"); // || not applicable to short, boolean
    checkErrorExpr("varshort || varchar", "0xB0113"); // || not applicable to short, char
    checkErrorExpr("varshort || varbyte", "0xB0113"); // || not applicable to short, byte
    checkErrorExpr("varshort || varshort", "0xB0113"); // || not applicable to short, short
    checkErrorExpr("varshort || varint", "0xB0113"); // || not applicable to short, int
    checkErrorExpr("varshort || varlong", "0xB0113"); // || not applicable to short, long
    checkErrorExpr("varshort || varfloat", "0xB0113"); // || not applicable to short, float
    checkErrorExpr("varshort || vardouble", "0xB0113"); // || not applicable to short, double
    checkErrorExpr("varint || varboolean", "0xB0113"); // || not applicable to short, boolean
    checkErrorExpr("varint || varchar", "0xB0113"); // || not applicable to int, char
    checkErrorExpr("varint || varbyte", "0xB0113"); // || not applicable to int, byte
    checkErrorExpr("varint || varshort", "0xB0113"); // || not applicable to int, short
    checkErrorExpr("varint || varint", "0xB0113"); // || not applicable to int, int
    checkErrorExpr("varint || varlong", "0xB0113"); // || not applicable to int, long
    checkErrorExpr("varint || varfloat", "0xB0113"); // || not applicable to int, float
    checkErrorExpr("varint || vardouble", "0xB0113"); // || not applicable to int, double
    checkErrorExpr("varlong || varboolean", "0xB0113"); // || not applicable to long, boolean
    checkErrorExpr("varlong || varchar", "0xB0113"); // || not applicable to long, char
    checkErrorExpr("varlong || varbyte", "0xB0113"); // || not applicable to long, byte
    checkErrorExpr("varlong || varshort", "0xB0113"); // || not applicable to long, short
    checkErrorExpr("varlong || varint", "0xB0113"); // || not applicable to long, int
    checkErrorExpr("varlong || varlong", "0xB0113"); // || not applicable to long, long
    checkErrorExpr("varlong || varfloat", "0xB0113"); // || not applicable to long, float
    checkErrorExpr("varlong || vardouble", "0xB0113"); // || not applicable to long, double
    checkErrorExpr("varfloat || varboolean", "0xB0113"); // || not applicable to float, boolean
    checkErrorExpr("varfloat || varchar", "0xB0113"); // || not applicable to float, char
    checkErrorExpr("varfloat || varbyte", "0xB0113"); // || not applicable to float, byte
    checkErrorExpr("varfloat || varshort", "0xB0113"); // || not applicable to float, short
    checkErrorExpr("varfloat || varint", "0xB0113"); // || not applicable to float, int
    checkErrorExpr("varfloat || varlong", "0xB0113"); // || not applicable to float, long
    checkErrorExpr("varfloat || varfloat", "0xB0113"); // || not applicable to float, float
    checkErrorExpr("varfloat || vardouble", "0xB0113"); // || not applicable to float, double
    checkErrorExpr("vardouble || varboolean", "0xB0113"); // || not applicable to double, boolean
    checkErrorExpr("vardouble || varchar", "0xB0113"); // || not applicable to double, char
    checkErrorExpr("vardouble || varbyte", "0xB0113"); // || not applicable to double, byte
    checkErrorExpr("vardouble || varshort", "0xB0113"); // || not applicable to double, short
    checkErrorExpr("vardouble || varint", "0xB0113"); // || not applicable to double, int
    checkErrorExpr("vardouble || varlong", "0xB0113"); // || not applicable to double, long
    checkErrorExpr("vardouble || varfloat", "0xB0113"); // || not applicable to double, float
    checkErrorExpr("vardouble || vardouble", "0xB0113"); // || not applicable to double, double
    checkErrorExpr("varchar = varboolean || varboolean", "0xA0179"); // expected char but provided boolean
    checkErrorExpr("varbyte = varboolean || varboolean", "0xA0179"); // expected byte but provided boolean
    checkErrorExpr("varshort = varboolean || varboolean", "0xA0179"); // expected short but provided boolean
    checkErrorExpr("varint = varboolean || varboolean", "0xA0179"); // expected int but provided boolean
    checkErrorExpr("varlong = varboolean || varboolean", "0xA0179"); // expected long but provided boolean
    checkErrorExpr("varfloat = varboolean || varboolean", "0xA0179"); // expected float but provided boolean
    checkErrorExpr("vardouble = varboolean || varboolean", "0xA0179"); // expected double but provided boolean
    checkErrorExpr("1m || 1m", "0xB0113");
  }

  @Test
  public void deriveFromLogicalNotExpression() throws IOException {
    checkExpr("!varboolean", "boolean"); // ~ applicable to boolean
    checkExpr("!true", "boolean"); // only possible with boolean as inner expression
    checkExpr("!(2.5>=0.3)", "boolean");
    checkExpr("!varboolean", "boolean"); // ~ applicable to boolean, result is boolean
  }

  @Test
  public void testInvalidLogicalNotExpression() throws IOException {
    checkErrorExpr("!varchar", "0xB0164"); // ! not applicable to char
    checkErrorExpr("!varbyte", "0xB0164"); // ! not applicable to byte
    checkErrorExpr("!varshort", "0xB0164"); // ! not applicable to short
    checkErrorExpr("!varint", "0xB0164"); // ! not applicable to int
    checkErrorExpr("!varlong", "0xB0164"); // ! not applicable to long
    checkErrorExpr("!varfloat", "0xB0164"); // ! not applicable to float
    checkErrorExpr("!vardouble", "0xB0164"); // ! not applicable to double
    checkErrorExpr("!varchar", "0xB0164"); // ! not applicable to char
    checkErrorExpr("!varbyte", "0xB0164"); // ! not applicable to byte
    checkErrorExpr("!varshort", "0xB0164"); // ! not applicable to short
    checkErrorExpr("!varint", "0xB0164"); // ! not applicable to int
    checkErrorExpr("!varlong", "0xB0164"); // ! not applicable to long
    checkErrorExpr("!varfloat", "0xB0164"); // ! not applicable to float
    checkErrorExpr("!vardouble", "0xB0164"); // ! not applicable to double
    checkErrorExpr("!1m", "0xB0164");
  }

  @Test
  public void deriveFromBracketExpression() throws IOException {
    checkExpr("(3)", "int"); // test with only a literal in the inner expression
    checkExpr("(3+4*(18-7.5))", "double"); // test with a more complex inner expression
    checkExpr("(person1)", "Person"); // test without primitive types in inner expression
  }

  @Test
  public void testInvalidBracketExpression() throws IOException {
    checkErrorExpr("(a)", "0xFD118"); // a cannot be resolved -> a has no type
  }

  @Test
  public void deriveFromConditionalExpression() throws IOException {
    //test with byte and short
    ASTExpression astExpr = parseExpr("varboolean ? varbyte : varshort");
    generateScopes(astExpr);
    assertNoFindings();
    SymTypeExpression type = TypeCheck3.typeOf(astExpr);
    Assertions.assertTrue(SymTypeRelations.isCompatible(_shortSymType, type));
    Assertions.assertFalse(SymTypeRelations.isCompatible(_byteSymType, type));

    //test with two ints as true and false expression
    astExpr = parseExpr("3<4?9:10");
    generateScopes(astExpr);
    assertNoFindings();
    type = TypeCheck3.typeOf(astExpr);
    Assertions.assertTrue(SymTypeRelations.isCompatible(_intSymType, type));

    // test with boolean and int
    astExpr = parseExpr("3<4?true:7");
    generateScopes(astExpr);
    assertNoFindings();
    type = TypeCheck3.typeOf(astExpr);
    Assertions.assertFalse(SymTypeRelations.isCompatible(_booleanSymType, type));
    Assertions.assertFalse(SymTypeRelations.isCompatible(_intSymType, type));

    //test with float and long
    astExpr = parseExpr("3>4?4.5f:10L");
    generateScopes(astExpr);
    assertNoFindings();
    type = TypeCheck3.typeOf(astExpr);
    Assertions.assertTrue(SymTypeRelations.isCompatible(_floatSymType, type));
    Assertions.assertFalse(SymTypeRelations.isCompatible(_longSymType, type));

    //test without primitive types as true and false expression
    astExpr = parseExpr("3<9?person1:person2");
    generateScopes(astExpr);
    assertNoFindings();
    type = TypeCheck3.typeOf(astExpr);
    Assertions.assertTrue(SymTypeRelations.isCompatible(_personSymType, type));

    //test with two objects in a sub-supertype relation
    astExpr = parseExpr("3<9?student1:person2");
    generateScopes(astExpr);
    assertNoFindings();
    type = TypeCheck3.typeOf(astExpr);
    Assertions.assertTrue(SymTypeRelations.isCompatible(_personSymType, type));
    Assertions.assertFalse(SymTypeRelations.isCompatible(_studentSymType, type));

    astExpr = parseExpr("varboolean ? 0 : 1"); // ? applicable to boolean
    generateScopes(astExpr);
    assertNoFindings();
    type = TypeCheck3.typeOf(astExpr);
    Assertions.assertTrue(SymTypeRelations.isCompatible(_intSymType, type));

    astExpr = parseExpr("varboolean ? varboolean : varboolean"); // ? applicable to boolean, boolean, result is boolean
    generateScopes(astExpr);
    assertNoFindings();
    type = TypeCheck3.typeOf(astExpr);
    Assertions.assertTrue(SymTypeRelations.isCompatible(_booleanSymType, type));

    astExpr = parseExpr("varbyte = varboolean ? varbyte : varbyte"); // ? applicable to byte, byte, result is byte
    generateScopes(astExpr);
    assertNoFindings();
    type = TypeCheck3.typeOf(astExpr);
    Assertions.assertTrue(SymTypeRelations.isCompatible(_byteSymType, type));

    astExpr = parseExpr("varshort = varboolean ? varbyte : varshort"); // ? applicable to byte, short, result is short
    generateScopes(astExpr);
    assertNoFindings();
    type = TypeCheck3.typeOf(astExpr);
    Assertions.assertTrue(SymTypeRelations.isCompatible(_shortSymType, type));

    astExpr = parseExpr("varshort = varboolean ? varshort : varbyte"); // ? applicable to short, byte, result is short
    generateScopes(astExpr);
    assertNoFindings();
    type = TypeCheck3.typeOf(astExpr);
    Assertions.assertTrue(SymTypeRelations.isCompatible(_shortSymType, type));

    astExpr = parseExpr("varshort = varboolean ? varshort : varshort"); // ? applicable to short, short, result is short
    generateScopes(astExpr);
    assertNoFindings();
    type = TypeCheck3.typeOf(astExpr);
    Assertions.assertTrue(SymTypeRelations.isCompatible(_shortSymType, type));

    astExpr = parseExpr("varchar = varboolean ? varchar : varchar"); // ? applicable to char, char, result is char
    generateScopes(astExpr);
    assertNoFindings();
    type = TypeCheck3.typeOf(astExpr);
    Assertions.assertTrue(SymTypeRelations.isCompatible(_charSymType, type));

    astExpr = parseExpr("varint = varboolean ? varchar : varbyte"); // ? applicable to char, byte, result is int
    generateScopes(astExpr);
    assertNoFindings();
    type = TypeCheck3.typeOf(astExpr);
    Assertions.assertTrue(SymTypeRelations.isCompatible(_intSymType, type));

    astExpr = parseExpr("varint = varboolean ? varbyte : varchar"); // ? applicable to byte, char, result is int
    generateScopes(astExpr);
    assertNoFindings();
    type = TypeCheck3.typeOf(astExpr);
    Assertions.assertTrue(SymTypeRelations.isCompatible(_intSymType, type));

    astExpr = parseExpr("varint = varboolean ? varchar : varshort"); // ? applicable to char, short, result is int
    generateScopes(astExpr);
    assertNoFindings();
    type = TypeCheck3.typeOf(astExpr);
    Assertions.assertTrue(SymTypeRelations.isCompatible(_intSymType, type));

    astExpr = parseExpr("varint = varboolean ? varshort : varchar"); // ? applicable to short, char, result is int
    generateScopes(astExpr);
    assertNoFindings();
    type = TypeCheck3.typeOf(astExpr);
    Assertions.assertTrue(SymTypeRelations.isCompatible(_intSymType, type));

    astExpr = parseExpr("varint = varboolean ? varint : varbyte"); // ? applicable to int, byte, result is int
    generateScopes(astExpr);
    assertNoFindings();
    type = TypeCheck3.typeOf(astExpr);
    Assertions.assertTrue(SymTypeRelations.isCompatible(_intSymType, type));

    astExpr = parseExpr("varint = varboolean ? varint : varshort"); // ? applicable to int, short, result is int
    generateScopes(astExpr);
    assertNoFindings();
    type = TypeCheck3.typeOf(astExpr);
    Assertions.assertTrue(SymTypeRelations.isCompatible(_intSymType, type));

    astExpr = parseExpr("varint = varboolean ? varint : varchar"); // ? applicable to int, char, result is int
    generateScopes(astExpr);
    assertNoFindings();
    type = TypeCheck3.typeOf(astExpr);
    Assertions.assertTrue(SymTypeRelations.isCompatible(_intSymType, type));

    astExpr = parseExpr("varint = varboolean ? varbyte : varint"); // ? applicable to byte, int, result is int
    generateScopes(astExpr);
    assertNoFindings();
    type = TypeCheck3.typeOf(astExpr);
    Assertions.assertTrue(SymTypeRelations.isCompatible(_intSymType, type));

    astExpr = parseExpr("varint = varboolean ? varshort : varint"); // ? applicable to short, int, result is int
    generateScopes(astExpr);
    assertNoFindings();
    type = TypeCheck3.typeOf(astExpr);
    Assertions.assertTrue(SymTypeRelations.isCompatible(_intSymType, type));

    astExpr = parseExpr("varint = varboolean ? varchar : varint"); // ? applicable to char, int, result is int
    generateScopes(astExpr);
    assertNoFindings();
    type = TypeCheck3.typeOf(astExpr);
    Assertions.assertTrue(SymTypeRelations.isCompatible(_intSymType, type));

    astExpr = parseExpr("varint = varboolean ? varint : varint"); // ? applicable to int, int, result is int
    generateScopes(astExpr);
    assertNoFindings();
    type = TypeCheck3.typeOf(astExpr);
    Assertions.assertTrue(SymTypeRelations.isCompatible(_longSymType, type));

    astExpr = parseExpr("varlong = varboolean ? varlong : varbyte"); // ? applicable to long, byte, result is long
    generateScopes(astExpr);
    assertNoFindings();
    type = TypeCheck3.typeOf(astExpr);
    Assertions.assertTrue(SymTypeRelations.isCompatible(_longSymType, type));

    astExpr = parseExpr("varlong = varboolean ? varlong : varshort"); // ? applicable to long, short, result is long
    generateScopes(astExpr);
    assertNoFindings();
    type = TypeCheck3.typeOf(astExpr);
    Assertions.assertTrue(SymTypeRelations.isCompatible(_longSymType, type));

    astExpr = parseExpr("varlong = varboolean ? varlong : varchar"); // ? applicable to long, char, result is long
    generateScopes(astExpr);
    assertNoFindings();
    type = TypeCheck3.typeOf(astExpr);
    Assertions.assertTrue(SymTypeRelations.isCompatible(_longSymType, type));

    astExpr = parseExpr("varlong = varboolean ? varlong : varint"); // ? applicable to long, int, result is long
    generateScopes(astExpr);
    assertNoFindings();
    type = TypeCheck3.typeOf(astExpr);
    Assertions.assertTrue(SymTypeRelations.isCompatible(_longSymType, type));

    astExpr = parseExpr("varlong = varboolean ? varbyte : varlong"); // ? applicable to byte, long, result is long
    generateScopes(astExpr);
    assertNoFindings();
    type = TypeCheck3.typeOf(astExpr);
    Assertions.assertTrue(SymTypeRelations.isCompatible(_longSymType, type));

    astExpr = parseExpr("varlong = varboolean ? varshort : varlong"); // ? applicable to short, long, result is long
    generateScopes(astExpr);
    assertNoFindings();
    type = TypeCheck3.typeOf(astExpr);
    Assertions.assertTrue(SymTypeRelations.isCompatible(_longSymType, type));

    astExpr = parseExpr("varlong = varboolean ? varchar : varlong"); // ? applicable to char, long, result is long
    generateScopes(astExpr);
    assertNoFindings();
    type = TypeCheck3.typeOf(astExpr);
    Assertions.assertTrue(SymTypeRelations.isCompatible(_longSymType, type));

    astExpr = parseExpr("varlong = varboolean ? varint : varlong"); // ? applicable to int, long, result is long
    generateScopes(astExpr);
    assertNoFindings();
    type = TypeCheck3.typeOf(astExpr);
    Assertions.assertTrue(SymTypeRelations.isCompatible(_longSymType, type));

    astExpr = parseExpr("varlong = varboolean ? varlong : varlong"); // ? applicable to long, long, result is long
    generateScopes(astExpr);
    assertNoFindings();
    type = TypeCheck3.typeOf(astExpr);
    Assertions.assertTrue(SymTypeRelations.isCompatible(_longSymType, type));

    astExpr = parseExpr("varfloat = varboolean ? varfloat : varbyte"); // ? applicable to float, byte, result is float
    generateScopes(astExpr);
    assertNoFindings();
    type = TypeCheck3.typeOf(astExpr);
    Assertions.assertTrue(SymTypeRelations.isCompatible(_floatSymType, type));

    astExpr = parseExpr("varfloat = varboolean ? varfloat : varshort"); // ? applicable to float, short, result is float
    generateScopes(astExpr);
    assertNoFindings();
    type = TypeCheck3.typeOf(astExpr);
    Assertions.assertTrue(SymTypeRelations.isCompatible(_floatSymType, type));

    astExpr = parseExpr("varfloat = varboolean ? varfloat : varchar"); // ? applicable to float, char, result is float
    generateScopes(astExpr);
    assertNoFindings();
    type = TypeCheck3.typeOf(astExpr);
    Assertions.assertTrue(SymTypeRelations.isCompatible(_floatSymType, type));

    astExpr = parseExpr("varfloat = varboolean ? varfloat : varint"); // ? applicable to float, int, result is float
    generateScopes(astExpr);
    assertNoFindings();
    type = TypeCheck3.typeOf(astExpr);
    Assertions.assertTrue(SymTypeRelations.isCompatible(_floatSymType, type));

    astExpr = parseExpr("varfloat = varboolean ? varfloat : varlong"); // ? applicable to float, long, result is float
    generateScopes(astExpr);
    assertNoFindings();
    type = TypeCheck3.typeOf(astExpr);
    Assertions.assertTrue(SymTypeRelations.isCompatible(_floatSymType, type));

    astExpr = parseExpr("varfloat = varboolean ? varbyte : varfloat"); // ? applicable to byte, float, result is float
    generateScopes(astExpr);
    assertNoFindings();
    type = TypeCheck3.typeOf(astExpr);
    Assertions.assertTrue(SymTypeRelations.isCompatible(_floatSymType, type));

    astExpr = parseExpr("varfloat = varboolean ? varshort : varfloat"); // ? applicable to short, float, result is float
    generateScopes(astExpr);
    assertNoFindings();
    type = TypeCheck3.typeOf(astExpr);
    Assertions.assertTrue(SymTypeRelations.isCompatible(_floatSymType, type));

    astExpr = parseExpr("varfloat = varboolean ? varchar : varfloat"); // ? applicable to char, float, result is float
    generateScopes(astExpr);
    assertNoFindings();
    type = TypeCheck3.typeOf(astExpr);
    Assertions.assertTrue(SymTypeRelations.isCompatible(_floatSymType, type));

    astExpr = parseExpr("varfloat = varboolean ? varint : varfloat"); // ? applicable to int, float, result is float
    generateScopes(astExpr);
    assertNoFindings();
    type = TypeCheck3.typeOf(astExpr);
    Assertions.assertTrue(SymTypeRelations.isCompatible(_floatSymType, type));

    astExpr = parseExpr("varfloat = varboolean ? varlong : varfloat"); // ? applicable to long, float, result is float
    generateScopes(astExpr);
    assertNoFindings();
    type = TypeCheck3.typeOf(astExpr);
    Assertions.assertTrue(SymTypeRelations.isCompatible(_floatSymType, type));

    astExpr = parseExpr("varfloat = varboolean ? varfloat : varfloat"); // ? applicable to float, float, result is float
    generateScopes(astExpr);
    assertNoFindings();
    type = TypeCheck3.typeOf(astExpr);
    Assertions.assertTrue(SymTypeRelations.isCompatible(_floatSymType, type));

    astExpr = parseExpr("vardouble = varboolean ? vardouble : varbyte"); // ? applicable to double, byte, result is double
    generateScopes(astExpr);
    assertNoFindings();
    type = TypeCheck3.typeOf(astExpr);
    Assertions.assertTrue(SymTypeRelations.isCompatible(_doubleSymType, type));

    astExpr = parseExpr("vardouble = varboolean ? vardouble : varshort"); // ? applicable to double, short, result is double
    generateScopes(astExpr);
    assertNoFindings();
    type = TypeCheck3.typeOf(astExpr);
    Assertions.assertTrue(SymTypeRelations.isCompatible(_doubleSymType, type));

    astExpr = parseExpr("vardouble = varboolean ? vardouble : varchar"); // ? applicable to double, char, result is double
    generateScopes(astExpr);
    assertNoFindings();
    type = TypeCheck3.typeOf(astExpr);
    Assertions.assertTrue(SymTypeRelations.isCompatible(_doubleSymType, type));

    astExpr = parseExpr("vardouble = varboolean ? vardouble : varint"); // ? applicable to double, int, result is double
    generateScopes(astExpr);
    assertNoFindings();
    type = TypeCheck3.typeOf(astExpr);
    Assertions.assertTrue(SymTypeRelations.isCompatible(_doubleSymType, type));

    astExpr = parseExpr("vardouble = varboolean ? vardouble : varlong"); // ? applicable to double, long, result is double
    generateScopes(astExpr);
    assertNoFindings();
    type = TypeCheck3.typeOf(astExpr);
    Assertions.assertTrue(SymTypeRelations.isCompatible(_doubleSymType, type));

    astExpr = parseExpr("vardouble = varboolean ? vardouble : varfloat"); // ? applicable to double, long, result is double
    generateScopes(astExpr);
    assertNoFindings();
    type = TypeCheck3.typeOf(astExpr);
    Assertions.assertTrue(SymTypeRelations.isCompatible(_doubleSymType, type));

    astExpr = parseExpr("vardouble = varboolean ? varbyte : vardouble"); // ? applicable to byte, double, result is double
    generateScopes(astExpr);
    assertNoFindings();
    type = TypeCheck3.typeOf(astExpr);
    Assertions.assertTrue(SymTypeRelations.isCompatible(_doubleSymType, type));

    astExpr = parseExpr("vardouble = varboolean ? varshort : vardouble"); // ? applicable to short, double, result is double
    generateScopes(astExpr);
    assertNoFindings();
    type = TypeCheck3.typeOf(astExpr);
    Assertions.assertTrue(SymTypeRelations.isCompatible(_doubleSymType, type));

    astExpr = parseExpr("vardouble = varboolean ? varchar : vardouble"); // ? applicable to char, double, result is double
    generateScopes(astExpr);
    assertNoFindings();
    type = TypeCheck3.typeOf(astExpr);
    Assertions.assertTrue(SymTypeRelations.isCompatible(_doubleSymType, type));

    astExpr = parseExpr("vardouble = varboolean ? varint : vardouble"); // ? applicable to int, double, result is double
    generateScopes(astExpr);
    assertNoFindings();
    type = TypeCheck3.typeOf(astExpr);
    Assertions.assertTrue(SymTypeRelations.isCompatible(_doubleSymType, type));

    astExpr = parseExpr("vardouble = varboolean ? varlong : vardouble"); // ? applicable to long, double, result is double
    generateScopes(astExpr);
    assertNoFindings();
    type = TypeCheck3.typeOf(astExpr);
    Assertions.assertTrue(SymTypeRelations.isCompatible(_doubleSymType, type));

    astExpr = parseExpr("vardouble = varboolean ? varfloat : vardouble"); // ? applicable to float, double, result is double
    generateScopes(astExpr);
    assertNoFindings();
    type = TypeCheck3.typeOf(astExpr);
    Assertions.assertTrue(SymTypeRelations.isCompatible(_doubleSymType, type));

    astExpr = parseExpr("vardouble = varboolean ? vardouble : vardouble"); // ? applicable to double, double, result is double
    generateScopes(astExpr);
    assertNoFindings();
    type = TypeCheck3.typeOf(astExpr);
    Assertions.assertTrue(SymTypeRelations.isCompatible(_doubleSymType, type));
  }

  @Test
  public void testInvalidConditionalExpression() throws IOException {
    checkErrorExpr("3?true:fvarlse", "0xFD118");
    checkErrorExpr("varbyte ? 0 : 1", "0xB0165"); // ? not applicable to byte
    checkErrorExpr("varshort ? 0 : 1", "0xB0165"); // ? not applicable to short
    checkErrorExpr("varchar ? 0 : 1", "0xB0165"); // ? not applicable to char
    checkErrorExpr("varint ? 0 : 1", "0xB0165"); // ? not applicable to int
    checkErrorExpr("varlong ? 0 : 1", "0xB0165"); // ? not applicable to long
    checkErrorExpr("varfloat ? 0 : 1", "0xB0165"); // ? not applicable to float
    checkErrorExpr("vardouble ? 0 : 1", "0xB0165"); // ? not applicable to double
    checkErrorExpr("varboolean = varboolean ? varbyte : varboolean", "0xA0179"); // expected boolean but provided byte
    checkErrorExpr("varboolean = varboolean ? varboolean : varbyte", "0xA0179"); // expected boolean but provided byte
    checkErrorExpr("varboolean = varboolean ? varbyte : varbyte", "0xA0179"); // expected boolean but provided byte
  }

  @Test
  public void deriveFromBooleanNotExpression() throws IOException {
    checkExpr("~varchar", "int"); // ~ applicable to char
    checkExpr("~varbyte", "int"); // ~ applicable to byte
    checkExpr("~varshort", "int"); // ~ applicable to short
    checkExpr("~varint", "int"); // ~ applicable to int
    checkExpr("~varlong", "long"); // ~ applicable to long
    checkExpr("~varchar", "int"); // ~ applicable to char, result is int
    checkExpr("~varbyte", "int"); // ~ applicable to byte, result is int
    checkExpr("~varshort", "int"); // ~ applicable to short, result is int
    checkExpr("~varint", "int"); // ~ applicable to int, result is int
    checkExpr("~varlong", "long"); // ~ applicable to long, result is long
  }

  @Test
  public void testInvalidBooleanNotExpression() throws IOException {
    checkErrorExpr("~varboolean", "0xB0175"); // ! not applicable to boolean
    checkErrorExpr("~varfloat", "0xB0175"); // ! not applicable to boolean
    checkErrorExpr("~vardouble", "0xB0175"); // ! not applicable to boolean
    checkErrorExpr("varchar = ~varchar", "0xA0179"); // ~ applicable to char, but result is int
    checkErrorExpr("varbyte = ~varbyte", "0xA0179"); // ~ applicable to byte, but result is int
    checkErrorExpr("varshort = ~varshort", "0xA0179"); // ~ applicable to short, but result is int
  }

  /**
   * initialize symboltable including
   * global scope, artifact scopes and scopes with symbols for testing
   * (mostly used for FieldAccessExpressions)
   */
  public void init_advanced() {
    ICombineExpressionsWithLiteralsGlobalScope globalScope =
        CombineExpressionsWithLiteralsMill.globalScope();

    ICombineExpressionsWithLiteralsArtifactScope artifactScope2 =
        CombineExpressionsWithLiteralsMill.artifactScope();
    artifactScope2.setEnclosingScope(globalScope);
    artifactScope2.setImportsList(Lists.newArrayList());
    artifactScope2.setName("types");
    artifactScope2.setPackageName("");

    ICombineExpressionsWithLiteralsArtifactScope artifactScope3 =
        CombineExpressionsWithLiteralsMill.artifactScope();
    artifactScope3.setEnclosingScope(globalScope);
    artifactScope3.setImportsList(Lists.newArrayList());
    artifactScope3.setName("types2");

    ICombineExpressionsWithLiteralsArtifactScope artifactScope4 =
        CombineExpressionsWithLiteralsMill.artifactScope();
    artifactScope4.setEnclosingScope(globalScope);
    artifactScope4.setImportsList(Lists.newArrayList());
    artifactScope4.setName("types3");
    artifactScope4.setPackageName("types3");

    ICombineExpressionsWithLiteralsArtifactScope artifactScope5 =
        CombineExpressionsWithLiteralsMill.artifactScope();
    artifactScope5.setEnclosingScope(globalScope);
    artifactScope5.setImportsList(Lists.newArrayList());
    artifactScope5.setName("functions1");
    artifactScope5.setPackageName("functions1");

    ICombineExpressionsWithLiteralsArtifactScope artifactScope6 =
        CombineExpressionsWithLiteralsMill.artifactScope();
    artifactScope6.setEnclosingScope(globalScope);
    artifactScope6.setImportsList(Lists.newArrayList());
    artifactScope6.setName("functions2");
    artifactScope6.setPackageName("functions2");

    //todo
    // No enclosing Scope: Search ending here

    ICombineExpressionsWithLiteralsScope scope3 =
        CombineExpressionsWithLiteralsMill.scope();
    scope3.setName("types2");
    artifactScope4.addSubScope(scope3);
    scope3.setEnclosingScope(artifactScope4);

    TypeSymbol selfReflectiveStudent = BasicSymbolsMill.typeSymbolBuilder()
        .setName("SelfReflectiveStudent")
        .setSpannedScope(OOSymbolsMill.scope())
        .setSuperTypesList(List.of(_studentSymType))
        .setEnclosingScope(globalScope)
        .build();
    inScope(selfReflectiveStudent.getSpannedScope(), method("self",
        createTypeObject(selfReflectiveStudent))
    );
    inScope(selfReflectiveStudent.getSpannedScope(),
        variable("selfVar", createTypeObject(selfReflectiveStudent))
    );
    inScope(globalScope, variable("selfReflectiveStudent",
        createTypeObject("SelfReflectiveStudent", globalScope))
    );

    inScope(artifactScope2, type("AClass"));
    inScope(scope3, type("AClass"));
    inScope(globalScope, type("AClass"));

    inScope(artifactScope2, type("BClass"));
    inScope(scope3, type("BClass"));

    inScope(artifactScope2, type("CClass"));
    inScope(scope3, type("CClass"));

    inScope(artifactScope2, selfReflectiveStudent);

    MethodSymbol ms2 = method("isInt", _booleanSymType);
    inScope(globalScope, ms2);
    inScope(globalScope, method("isInt", _booleanSymType, _intSymType));
    MethodSymbol ms0 = method("areInt", _booleanSymType, _intSymType);
    ms0.setIsElliptic(true);
    inScope(globalScope, ms0);
    inScope(globalScope, method("getIsInt", ms2.getFunctionType()));
    inScope(globalScope, method("getAreInt", ms0.getFunctionType()));

    OOTypeSymbol testType =
        inScope(globalScope, oOtype("Test",
            Collections.emptyList(),
            Collections.emptyList(),
            List.of(
                method("store", _doubleSymType),
                method("pay", _voidSymType, _intSymType)
            ),
            List.of(field("variable", _intSymType))
        ));
    testType.getMethodList().forEach(m -> m.setIsStatic(true));
    testType.getFieldList().forEach(f -> f.setIsStatic(true));

    OOTypeSymbol testType2 =
        inScope(artifactScope2, oOtype("Test",
            Collections.emptyList(),
            Collections.emptyList(),
            List.of(
                method("store", _doubleSymType),
                method("pay", _voidSymType, _intSymType)
            ),
            List.of(field("variable", _intSymType))
        ));
    testType2.getMethodList().forEach(m -> m.setIsStatic(true));
    testType2.getFieldList().forEach(f -> f.setIsStatic(true));

    OOTypeSymbol testType3 =
        oOtype("Test",
            Collections.emptyList(),
            Collections.emptyList(),
            List.of(
                method("store", _doubleSymType),
                method("pay", _voidSymType, _intSymType)
            ),
            List.of(field("variable", _intSymType))
        );
    testType3.getMethodList().forEach(m -> m.setIsStatic(true));
    testType3.getFieldList().forEach(f -> f.setIsStatic(true));

    IOOSymbolsScope testScope = testType3.getSpannedScope();

    FieldSymbol testVariable = field("testVariable", _shortSymType);
    testVariable.setIsStatic(true);
    OOTypeSymbol testInnerType = OOSymbolsMill.oOTypeSymbolBuilder()
        .setName("TestInnerType")
        .setIsPublic(true)
        .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
        .setEnclosingScope(testScope)
        .build();
    testInnerType.addFieldSymbol(testVariable);
    testInnerType.setIsStatic(true);
    inScope(testScope, testInnerType);
    inScope(testInnerType.getSpannedScope(), testVariable);

    testType3.setSpannedScope(testScope);

    inScope(artifactScope2, testType2);
    inScope(scope3, testType3);
    inScope(globalScope, testType);

    inScope(artifactScope5, function("getPi", _floatSymType));
    inScope(artifactScope6, function("getPi", _floatSymType));

    // Creating types for legal access
    // on "types.DeepNesting.firstLayer.onlyMember",
    // where firstLayer and onlyMember are fields
    OOTypeSymbol oneFieldMember =
        inScope(globalScope, oOtype("OneFieldMember"));
    FieldSymbol onlyMember = field("onlyMember", _intSymType);
    inScope(oneFieldMember.getSpannedScope(), onlyMember);

    OOTypeSymbol deepNesting = oOtype("DeepNesting");
    inScope(artifactScope2, deepNesting);
    FieldSymbol firstLayer = field("firstLayer",
        SymTypeExpressionFactory.createTypeExpression(oneFieldMember));
    firstLayer.setIsStatic(true);
    inScope(deepNesting.getSpannedScope(), firstLayer);
  }

  @Test
  public void deriveFromFieldAccessExpression() throws IOException {
    //initialize symbol table
    init_advanced();

    //test for variable of a type with one package
    checkExpr("types.Test.variable", "int");

    //test for variable of type with more than one package
    checkExpr("types3.types2.Test.variable", "int");

    //test for variable in inner type
    checkExpr("types3.types2.Test.TestInnerType.testVariable", "short");

    // test for nested field access ("firstLayer" is a static member, "onlyMember" is an instance member)
    checkExpr("types.DeepNesting.firstLayer.onlyMember", "int");
  }

  /**
   * tests whether inner results are all present
   */
  @Test
  public void deriveFromFieldAccessExpressionInnerResults() throws IOException {
    //initialize symbol table
    init_advanced();
    ASTFieldAccessExpression astTestVariable = (ASTFieldAccessExpression)
        parseExpr("types3.types2.Test.TestInnerType.testVariable");
    ASTFieldAccessExpression astTestInnerType = (ASTFieldAccessExpression)
        astTestVariable.getExpression();
    ASTFieldAccessExpression astTest = (ASTFieldAccessExpression)
        astTestInnerType.getExpression();
    ASTFieldAccessExpression astTypes2 = (ASTFieldAccessExpression)
        astTest.getExpression();
    ASTNameExpression astTypes3 = (ASTNameExpression)
        astTypes2.getExpression();
    generateScopes(astTestVariable);
    // calculate all the types
    TypeCheck3.typeOf(astTestVariable);
    assertNoFindings();
    Assertions.assertTrue(getType4Ast().hasTypeOfExpression(astTestVariable));
    Assertions.assertFalse(getType4Ast().hasTypeOfTypeIdentifierForName(astTestVariable));
    Assertions.assertEquals("short", getType4Ast().getTypeOfExpression(astTestVariable).printFullName());
    Assertions.assertFalse(getType4Ast().hasTypeOfExpression(astTestInnerType));
    Assertions.assertTrue(getType4Ast().hasTypeOfTypeIdentifierForName(astTestInnerType));
    Assertions.assertEquals("types3.types2.Test.TestInnerType", getType4Ast().getPartialTypeOfTypeIdForName(astTestInnerType).printFullName());
    Assertions.assertFalse(getType4Ast().hasTypeOfExpression(astTest));
    Assertions.assertTrue(getType4Ast().hasTypeOfTypeIdentifierForName(astTest));
    Assertions.assertEquals("types3.types2.Test", getType4Ast().getPartialTypeOfTypeIdForName(astTest).printFullName());
    Assertions.assertFalse(getType4Ast().hasTypeOfExpression(astTypes2));
    Assertions.assertFalse(getType4Ast().hasTypeOfTypeIdentifierForName(astTypes2));
    Assertions.assertFalse(getType4Ast().hasTypeOfExpression(astTypes3));
    Assertions.assertFalse(getType4Ast().hasTypeOfTypeIdentifierForName(astTypes3));
    assertNoFindings();
  }

  /**
   * tests whether inner results are all present
   */
  @Test
  public void deriveFromInvalidFieldAccessExpressionInnerProtectedClassResults() throws IOException {
    //initialize symbol table
    init_advanced();
    // set the inner Class to protected, we only have public access.
    OOTypeSymbol testInnerTypeSym = OOSymbolsMill.globalScope()
        .resolveOOType("types3.types2.Test.TestInnerType")
        .get();
    testInnerTypeSym.setIsProtected(true);
    checkErrorExpr("types3.types2.Test.TestInnerType.testVariable", "0xF736E");
  }

  @Test
  public void deriveFromFieldAccessExpressionTypeVarUpperBound() throws IOException {
    IBasicSymbolsGlobalScope gs = BasicSymbolsMill.globalScope();
    // add Person.age
    inScope(_personSymType.getTypeInfo().getSpannedScope(),
        variable("age", _intSymType)
    );
    // TPSub extends Person
    // TPSub tPSub;
    inScope(gs, variable("tPSub",
        createTypeVariable(inScope(gs,
            typeVariable("TPSub", List.of(_personSymType))
        ))
    ));
    // TSSub extends Student
    // TSSub tSSub;
    inScope(gs, variable("tSSub",
        createTypeVariable(inScope(gs,
            typeVariable("TSSub", List.of(_studentSymType))
        ))
    ));

    checkExpr("tPSub.age", "int");
    checkExpr("tSSub.age", "int");
  }

  @Test
  public void deriveFromFieldAccessExpressionUnion() throws IOException {
    IBasicSymbolsGlobalScope gs = BasicSymbolsMill.globalScope();
    // add Person.age
    inScope(_personSymType.getTypeInfo().getSpannedScope(),
        variable("age", _intSymType)
    );
    // (Student | Child) teachablePerson;
    inScope(gs, variable("teachablePerson",
        createUnion(_studentSymType, _childSymType)
    ));

    checkExpr("teachablePerson.age", "int");
  }

  @Test
  public void testInvalidFieldAccessExpressionUnion() throws IOException {
    IBasicSymbolsGlobalScope gs = BasicSymbolsMill.globalScope();
    // add Person.age
    inScope(_personSymType.getTypeInfo().getSpannedScope(),
        variable("age", _intSymType)
    );
    // (Person | int) maybePerson;
    inScope(gs, variable("maybePerson",
        createUnion(_personSymType, _intSymType)
    ));

    checkErrorExpr("maybePerson.age", "0xF737F");
  }

  @Test
  public void deriveFromFieldAccessExpressionIntersection() throws IOException {
    IBasicSymbolsGlobalScope gs = BasicSymbolsMill.globalScope();
    // add Person.age
    inScope(_personSymType.getTypeInfo().getSpannedScope(),
        variable("age", _intSymType)
    );
    // (Person & Car) talkingCar;
    inScope(gs, variable("talkingCar",
        createIntersection(_personSymType, _carSymType)
    ));
    checkExpr("talkingCar.age", "int");
  }

  @Test
  public void syntheziseFromFieldAccessExpression() throws IOException {
    init_advanced();

    checkType("Test", "Test");

    //test for type with only one package
    checkType("types.Test", "Test");

    //test for type with more than one package
    checkType("types3.types2.Test", "types3.types2.Test");
  }

  @Test
  public void deriveFromCallExpression() throws IOException {
    //initialize symbol table
    init_advanced();

    //test for method with unqualified name without parameters
    checkExpr("isInt()", "boolean");

    //test for method with unqualified name with parameters
    checkExpr("isInt(4)", "boolean");

    //test for method with varargs with no optional value
    checkExpr("areInt()", "boolean");

    //test for method with varargs with one optional value
    checkExpr("areInt(1)", "boolean");

    //test for method with varargs with multiple optional values
    checkExpr("areInt(1, 2, 3)", "boolean");

    //test for method with qualified name without parameters
    checkExpr("types.Test.store()", "double");

    //test for method with qualified name with parameters
    checkExpr("types.Test.pay(4)", "void");

    //test for function with that exists in another scope with
    //the same name but different qualified name
    checkExpr("functions1.functions1.getPi()", "float");

    // test method chaining
    checkExpr("selfReflectiveStudent.self().self()", "SelfReflectiveStudent");

    // test function chaining
    checkExpr("getIsInt()()", "boolean");
    checkExpr("(()->()->1)()()", "int");

    // test indirect function chaining
    checkExpr("(getIsInt())()", "boolean");
    checkExpr("((()->()->1)())()", "int");

    // test function chaining with varargs
    checkExpr("getAreInt()()", "boolean");
    checkExpr("getAreInt()(1,2)", "boolean");

    // test function chaining using fields
    checkExpr("selfReflectiveStudent.self().self()", "SelfReflectiveStudent");
  }

  @Test
  public void deriveFromCallExpressionTypeVarUpperBound() throws IOException {
    IBasicSymbolsGlobalScope gs = BasicSymbolsMill.globalScope();
    // add Person::getAge
    inScope(_personSymType.getTypeInfo().getSpannedScope(),
        function("getAge", _intSymType)
    );
    // TPSub extends Person
    // TPSub tPSub;
    inScope(gs, variable("tPSub",
        createTypeVariable(inScope(gs,
            typeVariable("TPSub", List.of(_personSymType))
        ))
    ));
    // TSSub extends Student
    // TSSub tSSub;
    inScope(gs, variable("tSSub",
        createTypeVariable(inScope(gs,
            typeVariable("TSSub", List.of(_studentSymType))
        ))
    ));

    checkExpr("tPSub.getAge()", "int");
    checkExpr("tSSub.getAge()", "int");
  }

  @Test
  public void deriveFromCallExpressionUnion() throws IOException {
    IBasicSymbolsGlobalScope gs = BasicSymbolsMill.globalScope();
    // add Person::getAge
    inScope(_personSymType.getTypeInfo().getSpannedScope(),
        function("getAge", _intSymType)
    );
    // (Student | Child) teachablePerson;
    inScope(gs, variable("teachablePerson",
        createUnion(_studentSymType, _childSymType)
    ));

    checkExpr("teachablePerson.getAge()", "int");
  }

  @Test
  public void testInvalidCallExpressionUnion() throws IOException {
    IBasicSymbolsGlobalScope gs = BasicSymbolsMill.globalScope();
    // add Person::getAge
    inScope(_personSymType.getTypeInfo().getSpannedScope(),
        function("getAge", _intSymType)
    );
    // (Person | int) maybePerson;
    inScope(gs, variable("maybePerson",
        createUnion(_personSymType, _intSymType)
    ));

    checkErrorExpr("maybePerson.getAge()", "0xF737F");
  }

  @Test
  public void deriveFromCallExpressionIntersection() throws IOException {
    IBasicSymbolsGlobalScope gs = BasicSymbolsMill.globalScope();
    // add Person::getAge
    inScope(_personSymType.getTypeInfo().getSpannedScope(),
        function("getAge", _intSymType)
    );
    // (Person & Car) talkingCar;
    inScope(gs, variable("talkingCar",
        createIntersection(_personSymType, _carSymType)
    ));
    checkExpr("talkingCar.getAge()", "int");
  }

  @Test
  public void testInvalidCallExpression() throws IOException {
    //method isNot() is not in scope -> method cannot be resolved -> method has no return type
    init_advanced();
    checkErrorExpr("isNot()", "0xFD118");
  }

  @Test
  public void testInvalidCallExpressionWithMissingNameAndNotComposedOfCallback()
      throws IOException {
    // Expression (2 + 3)() and all other Expressions in front of brackets are parsable
    init_advanced();
    checkErrorExpr("(2 + 3)()", "0xFDABC");
  }

  @Test
  public void testInvalidCallExpressionWithInvalidQualifiedName() throws IOException {
    //method isInt() is not in the specified scope -> method cannot be resolved
    init_advanced();
    checkErrorExpr("notAScope.isInt()", "0xF735F");
  }

  @Test
  public void testInvalidCallExpressionWithFunctionChaining() throws IOException {
    //function isNot() is part of the return type of getIsInt() -> function cannot be resolved
    init_advanced();
    checkErrorExpr("getIsInt.isNot()", "0xFDB3A");
  }

  @Test
  public void testInvalidCallExpressionWithInvalidArgument() throws IOException {
    init_advanced();
    checkErrorExpr("isInt(\"foo\" / 2)", "0xB0163");
  }

  @Test
  public void testRegularAssignmentWithTwoMissingFields() throws IOException {
    checkErrorExpr("missingField = missingField2", "0xFD118");
  }

  @Test
  public void testMissingMethodWithMissingArgs() throws IOException {
    checkErrorExpr("missingMethod(missing1, missing2)", "0xFD118");
  }

  /**
   * initialize the symbol table for a basic inheritance example
   * we only have one scope and the symbols are all in this scope or in subscopes
   */
  public void init_inheritance() {
    //inheritance example
    IOOSymbolsGlobalScope globalScope = OOSymbolsMill.globalScope();
    //super
    OOTypeSymbol aList = inScope(globalScope,
        oOtype("AList",
            Collections.emptyList(),
            Collections.emptyList(),
            List.of(method("add", _voidSymType,
                List.of(_boxedString))),
            List.of(field("name", _boxedString))
        )
    );

    //sub
    OOTypeSymbol myList = inScope(globalScope,
        oOtype("MyList", List.of(createTypeObject(aList)))
    );
    inScope(globalScope, field("myList", createTypeObject(myList)));

    //subsub
    OOTypeSymbol mySubList = inScope(globalScope,
        oOtype("MySubList", List.of(createTypeObject(myList)))
    );
    inScope(globalScope, field("mySubList", createTypeObject(myList)));
  }

  /**
   * test if the methods and fields of superclasses can be used by subclasses
   */
  @Test
  public void testInheritance() throws IOException {
    //initialize symbol table
    init_inheritance();

    //methods
    //test normal inheritance
    checkExpr("myList.add(\"Hello\")", "void");

    //test inheritance over two levels
    checkExpr("mySubList.add(\"World\")", "void");

    //fields
    checkExpr("myList.name", "java.lang.String");

    checkExpr("mySubList.name", "java.lang.String");
  }

  /**
   * test the inheritance of a generic type with one type variable
   */
  @Test
  public void testListAndArrayListInheritance() throws IOException {
    //one generic parameter, supertype List<T>
    IBasicSymbolsScope listScope =
        _boxedListSymType.getTypeInfo().getSpannedScope();
    TypeVarSymbol listTVar = listScope.getLocalTypeVarSymbols().get(0);
    listScope.add(function("add", _booleanSymType,
        List.of(createTypeVariable(listTVar))
    ));
    listScope.add(variable("next", createTypeVariable(listTVar)));

    //test methods and fields of the supertype
    checkExpr("intList.add(2)", "boolean");

    checkExpr("intList.next", "int");

    //test inherited methods and fields of the subtype
    checkExpr("intLinkedList.add(3)", "boolean");

    checkExpr("intLinkedList.next", "int");
  }

  /**
   * test the inheritance of generic types with two type variables
   */
  @Test
  public void testGenericInheritanceTwoTypeVariables() throws IOException {
    // two generic parameters, supertype GenSup<S,V>,
    // create SymType GenSup<String,int>
    IOOSymbolsGlobalScope gs = OOSymbolsMill.globalScope();
    TypeVarSymbol tvS = typeVariable("S");
    TypeVarSymbol tvV = typeVariable("V");
    TypeSymbol genSup = inScope(gs,
        oOtype("GenSup",
            Collections.emptyList(),
            List.of(tvS, tvV),
            List.of(method("load",
                createTypeVariable(tvS),
                createTypeVariable(tvV))
            ),
            List.of(
                field("f1", createTypeVariable(tvS)),
                field("f2", createTypeVariable(tvV))
            )
        )
    );
    SymTypeExpression genSupType =
        createGenerics(genSup, _boxedString, _intSymType);
    inScope(gs, field("genSupVar", genSupType));

    // two generic parameters, subtype GenSub<S,V>,
    // create SymType GenSub<String,int>
    // same name of variables on purpose
    TypeVarSymbol tvS2 = typeVariable("S");
    TypeVarSymbol tvV2 = typeVariable("V");
    OOTypeSymbol genSub = inScope(gs,
        oOtype("GenSub",
            List.of(createGenerics(genSup,
                createTypeVariable(tvS2), createTypeVariable(tvV2))),
            List.of(tvS2, tvV2),
            // override f1
            Collections.emptyList(),
            List.of(field("f1", createTypeVariable(tvS2)))
        )
    );
    SymTypeExpression genSubType =
        createGenerics(genSub, _boxedString, _intSymType);
    inScope(gs, field("genSubVar", genSubType));

    //two generic parameters, subsubtype GenSubSub<V,S>, create GenSubSub<String,int>
    TypeVarSymbol tvS3 = typeVariable("S");
    TypeVarSymbol tvV3 = typeVariable("V");
    OOTypeSymbol genSubSub = inScope(gs,
        oOtype("GenSubSub",
            List.of(createGenerics(genSub,
                createTypeVariable(tvS3), createTypeVariable(tvV3))),
            List.of(tvV3, tvS3)
        )
    );
    SymTypeExpression genSubSubType =
        createGenerics(genSubSub, _boxedString, _intSymType);
    inScope(gs, field("genSubSubVar", genSubSubType));

    //supertype: test methods and fields
    checkExpr("genSupVar.load(3)", "java.lang.String");

    checkExpr("genSupVar.f1", "java.lang.String");

    checkExpr("genSupVar.f2", "int");

    //subtype: test inherited methods and fields
    checkExpr("genSubVar.load(3)", "java.lang.String");

    checkExpr("genSubVar.f1", "java.lang.String");

    checkExpr("genSubVar.f2", "int");

    //subsubtype: test inherited methods and fields
    checkExpr("genSubSubVar.load(\"Hello\")", "int");

    checkExpr("genSubSubVar.f1", "int");

    checkExpr("genSubSubVar.f2", "java.lang.String");
  }

  /**
   * test if methods and a field from a fixed subtype(generic type, but instead of type variable concrete type)
   * are inherited correctly
   */
  @Test
  public void testSubVarSupFix() throws IOException {
    //subtype with variable generic parameter, supertype with fixed generic parameter
    //supertype with fixed generic parameter FixGen<A> and SymType FixGen<int>
    IOOSymbolsGlobalScope gs = OOSymbolsMill.globalScope();

    TypeVarSymbol tvA = typeVariable("A");
    OOTypeSymbol fixGen = inScope(gs,
        oOtype("FixGen",
            Collections.emptyList(),
            List.of(tvA),
            List.of(method("add", _booleanSymType, createTypeVariable(tvA))),
            List.of(field("next", createTypeVariable(tvA)))
        )
    );
    SymTypeExpression fixGenType = createGenerics(fixGen, _intSymType);
    inScope(gs, field("fixGenVar", fixGenType));

    // subtype with variable generic parameter VarGen<N>
    // which extends FixGen<int>,
    // SymType VarGen<String>
    TypeVarSymbol tvN = typeVariable("N");
    OOTypeSymbol varGen = inScope(gs,
        oOtype("VarGen",
            List.of(fixGenType),
            List.of(tvN),
            List.of(method("calculate", createTypeVariable(tvN))),
            Collections.emptyList()
        )
    );
    SymTypeExpression varGenType = createGenerics(varGen, _boxedString);
    inScope(gs, field("varGen", varGenType));

    //test own methods first
    checkExpr("varGen.calculate()", "java.lang.String");

    //test inherited methods and fields
    checkExpr("varGen.add(4)", "boolean");

    checkExpr("varGen.next", "int");
  }

  /**
   * Test-Case: SubType has more generic parameters than its supertype
   */
  @Test
  public void testSubTypeWithMoreGenericParameters() throws IOException {
    IOOSymbolsGlobalScope gs = OOSymbolsMill.globalScope();
    //one generic parameter, supertype List<T>
    TypeSymbol list = _unboxedListSymType.getTypeInfo();
    TypeVarSymbol tvT = list.getTypeParameterList().get(0);
    inScope(list.getSpannedScope(),
        function("add", _booleanSymType, createTypeVariable(tvT))
    );
    inScope(list.getSpannedScope(), variable("next", createTypeVariable(tvT)));

    //two generic parameters, subtype MoreGen<T,F>
    TypeVarSymbol tvT2 = typeVariable("T");
    TypeVarSymbol tvF = typeVariable("F");
    TypeSymbol moreGenType = inScope(gs,
        type("MoreGen",
            List.of(createGenerics(list, createTypeVariable(tvT2))),
            List.of(tvT2, tvF),
            List.of(function("insert", createTypeVariable(tvT2), createTypeVariable(tvF))),
            Collections.emptyList()
        )
    );
    VariableSymbol moreGen = variable("moreGen",
        createGenerics(moreGenType, _intSymType, _longSymType)
    );
    inScope(gs, moreGen);

    //test own method
    checkExpr("moreGen.insert(12L)", "int");

    //test inherited methods and fields
    checkExpr("moreGen.add(12)", "boolean");

    checkExpr("moreGen.next", "int");
  }

  /**
   * Test-Case: SubType is a normal object type and extends a fixed generic type
   */
  @Test
  public void testSubTypeWithoutGenericParameter() throws IOException {
    IOOSymbolsGlobalScope gs = OOSymbolsMill.globalScope();
    //one generic parameter, supertype List<T>
    TypeSymbol list = _unboxedListSymType.getTypeInfo();
    TypeVarSymbol tvT = list.getTypeParameterList().get(0);
    inScope(list.getSpannedScope(),
        function("add", _booleanSymType, createTypeVariable(tvT))
    );
    inScope(list.getSpannedScope(), variable("next", createTypeVariable(tvT)));

    //subtype without generic parameter NotGen extends List<int>
    OOTypeSymbol notGeneric = inScope(gs,
        oOtype("NotGen",
            List.of(createGenerics(list, _intSymType))
        )
    );
    inScope(gs, field("notGen", createTypeObject(notGeneric)));

    //test inherited methods and fields
    checkExpr("notGen.add(14)", "boolean");

    checkExpr("notGen.next", "int");
  }

  /**
   * Test-Case: Multi-Inheritance 1, test if the methods and fields are inherited correctly
   * every type in the example has exactly one type variable
   */
  @Test
  public void testMultiInheritance() throws IOException {
    IOOSymbolsGlobalScope gs = OOSymbolsMill.globalScope();
    //supertype SupA<T>
    TypeVarSymbol tvSupA = typeVariable("T");
    OOTypeSymbol supAType = inScope(gs,
        oOtype("SupA",
            Collections.emptyList(),
            List.of(tvSupA),
            List.of(method("testA", createTypeVariable(tvSupA))),
            List.of(field("currentA", createTypeVariable(tvSupA)))
        )
    );

    //supertype SupB<T>
    TypeVarSymbol tvSupB = typeVariable("T");
    OOTypeSymbol supBType = inScope(gs,
        oOtype("SupB",
            Collections.emptyList(),
            List.of(tvSupB),
            List.of(method("testB", createTypeVariable(tvSupB))),
            List.of(field("currentB", createTypeVariable(tvSupB)))
        )
    );

    //subType SubA<T>
    TypeVarSymbol tvSubA = typeVariable("T");
    OOTypeSymbol subAType = inScope(gs,
        oOtype("SubA",
            List.of(createGenerics(supAType, createTypeVariable(tvSubA)),
                createGenerics(supBType, createTypeVariable(tvSubA))),
            List.of(tvSubA)
        )
    );
    inScope(gs, field("sub", createGenerics(subAType, _charSymType)));

    checkExpr("sub.testA()", "char");

    checkExpr("sub.currentA", "char");

    checkExpr("sub.testB()", "char");

    checkExpr("sub.currentB", "char");
  }

  /**
   * Test-Case: Multi-Inheritance 1, test if the methods and fields are inherited correctly
   * the supertypes have one type variable and the subtype has two type variables
   */
  @Test
  public void testMultiInheritanceSubTypeMoreGen() throws IOException {
    IOOSymbolsGlobalScope gs = OOSymbolsMill.globalScope();
    //supertype SupA<T>
    TypeVarSymbol tvSupA = typeVariable("T");
    OOTypeSymbol supAType = inScope(gs,
        oOtype("SupA",
            Collections.emptyList(),
            List.of(tvSupA),
            List.of(method("testA", createTypeVariable(tvSupA))),
            List.of(field("currentA", createTypeVariable(tvSupA)))
        )
    );

    //supertype SupB<T>
    TypeVarSymbol tvSupB = typeVariable("T");
    OOTypeSymbol supBType = inScope(gs,
        oOtype("SupB",
            Collections.emptyList(),
            List.of(tvSupB),
            List.of(method("testB", createTypeVariable(tvSupB))),
            List.of(field("currentB", createTypeVariable(tvSupB)))
        )
    );

    //subType SubA<T,V>
    TypeVarSymbol tvSubAT = typeVariable("T");
    TypeVarSymbol tvSubAV = typeVariable("V");
    OOTypeSymbol subAType = inScope(gs,
        oOtype("SubA",
            List.of(createGenerics(supAType, createTypeVariable(tvSubAT)),
                createGenerics(supBType, createTypeVariable(tvSubAV))),
            List.of(tvSubAT, tvSubAV)
        )
    );
    inScope(gs, field("sub",
        createGenerics(subAType, _booleanSymType, _charSymType)
    ));

    checkExpr("sub.testA()", "boolean");

    checkExpr("sub.currentA", "boolean");

    checkExpr("sub.testB()", "char");

    checkExpr("sub.currentB", "char");
  }

  /**
   * test if you can use methods, types and fields of the type or its supertypes in its method scopes
   */
  @Test
  public void testMethodScope() throws IOException {
    IOOSymbolsGlobalScope gs = OOSymbolsMill.globalScope();
    //one generic parameter, supertype List<T>
    TypeSymbol list = _boxedListSymType.getTypeInfo();
    TypeVarSymbol tvT = list.getTypeParameterList().get(0);
    inScope(list.getSpannedScope(),
        function("add", _voidSymType, createTypeVariable(tvT))
    );
    inScope(list.getSpannedScope(), field("name", _unboxedString));

    //sub
    TypeSymbol linkedList = _linkedListSymType.getTypeInfo();
    TypeVarSymbol tvT2 = linkedList.getTypeParameterList().get(0);
    inScope(linkedList.getSpannedScope(), field("next", createTypeVariable(tvT2)));

    //subsub
    TypeVarSymbol tvV = typeVariable("V");
    FunctionSymbol myAdd = function("myAdd", _voidSymType);
    VariableSymbol myAddParameter = variable("parameter", createTypeVariable(tvV));
    myAdd.getParameterList().add(myAddParameter);
    inScope(myAdd.getSpannedScope(), myAddParameter);
    TypeSymbol mySubListType = inScope(gs,
        type("MySubList",
            List.of(createGenerics(linkedList, createTypeVariable(tvV))),
            List.of(tvV),
            List.of(myAdd),
            List.of(field("myName", _unboxedString))
        )
    );
    mySubListType.getFullName();

    // all expressions are to be in the myAdd() scope,
    // we do not have subscopes in these expressions
    ExpressionsBasisTraverser scopeSetter =
        ExpressionsBasisMill.inheritanceTraverser();
    scopeSetter.add4ExpressionsBasis(
        new ExpressionsBasisVisitor2() {
          @Override
          public void visit(ASTExpression node) {
            node.setEnclosingScope(myAdd.getSpannedScope());
          }
        }
    );

    // we calculate subexpressions within the myAdd method
    ASTExpression astexpr = parseExpr("myAdd(null)");
    generateScopes(astexpr);
    astexpr.accept(scopeSetter);
    SymTypeExpression type = TypeCheck3.typeOf(astexpr);
    assertNoFindings();
    Assertions.assertEquals("void", type.printFullName());

    astexpr = parseExpr("myName");
    generateScopes(astexpr);
    astexpr.accept(scopeSetter);
    type = TypeCheck3.typeOf(astexpr);
    assertNoFindings();
    Assertions.assertEquals("String", type.printFullName());

    astexpr = parseExpr("next");
    generateScopes(astexpr);
    astexpr.accept(scopeSetter);
    type = TypeCheck3.typeOf(astexpr);
    assertNoFindings();
    Assertions.assertEquals("MySubList.V", type.printFullName());

    astexpr = parseExpr("name");
    generateScopes(astexpr);
    astexpr.accept(scopeSetter);
    type = TypeCheck3.typeOf(astexpr);
    assertNoFindings();
    Assertions.assertEquals("String", type.printFullName());

    astexpr = parseExpr("parameter");
    generateScopes(astexpr);
    astexpr.accept(scopeSetter);
    type = TypeCheck3.typeOf(astexpr);
    assertNoFindings();
    Assertions.assertEquals("MySubList.V", type.printFullName());

    astexpr = parseExpr("add(parameter)");
    generateScopes(astexpr);
    astexpr.accept(scopeSetter);
    type = TypeCheck3.typeOf(astexpr);
    assertNoFindings();
    Assertions.assertEquals("void", type.printFullName());
  }

  public void init_static_example() {
    IOOSymbolsGlobalScope gs = OOSymbolsMill.globalScope();
    //types A and B
    MethodSymbol atest = method("test", _voidSymType);
    atest.setIsStatic(true);
    FieldSymbol afield = field("field", _intSymType);
    afield.setIsStatic(true);
    OOTypeSymbol a = inScope(gs,
        oOtype("A",
            Collections.emptyList(),
            Collections.emptyList(),
            List.of(atest),
            List.of(afield)
        )
    );
    //A has static inner type D
    FieldSymbol aDX = field("x", _intSymType);
    aDX.setIsStatic(true);
    OOTypeSymbol aD = inScope(a.getSpannedScope(), oOtype("D",
        Collections.emptyList(),
        Collections.emptyList(),
        Collections.emptyList(),
        List.of(aDX)
    ));
    aD.setIsStatic(true);

    MethodSymbol btest = method("test", _voidSymType);
    FieldSymbol bfield = field("field", _intSymType);
    OOTypeSymbol b = inScope(gs,
        oOtype("B",
            Collections.emptyList(),
            Collections.emptyList(),
            List.of(btest),
            List.of(bfield)
        )
    );
    //B has non-static inner type D
    FieldSymbol bDX = field("x", _intSymType);
    OOTypeSymbol bD = inScope(b.getSpannedScope(), oOtype("D",
        Collections.emptyList(),
        Collections.emptyList(),
        Collections.emptyList(),
        List.of(bDX)
    ));

    //A has static method test, static field field, static type D
    //B has normal method test, normal field field, normal type D
    //type C extends A and has no method, field or type
    inScope(gs, oOtype("C", List.of(createTypeObject(a))));
  }

  @Test
  public void testStaticType() throws IOException {
    init_static_example();

    checkType("A.D", "A.D");
    checkType("B.D", "B.D");
  }

  @Test
  public void testInvalidStaticType() throws IOException {
    init_static_example();

    checkErrorMCType("A.NotAType", "0xA0324");
  }

  @Test
  public void testStaticField() throws IOException {
    init_static_example();

    checkExpr("A.field", "int");
  }

  @Test
  public void testInvalidStaticField() throws IOException {
    init_static_example();

    checkErrorExpr("B.field", "0xF736F");
  }

  @Test
  public void testStaticMethod() throws IOException {
    init_static_example();

    checkExpr("A.test()", "void");
  }

  @Test
  public void testInvalidStaticMethod() throws IOException {
    init_static_example();

    checkErrorExpr("B.test()", "0xF736F");
  }

  @Test
  public void testMissingTypeQualified() throws IOException {
    checkErrorMCType("pac.kage.not.present.Type", "0xA0324");
  }

  @Test
  public void testMissingFieldQualified() throws IOException {
    init_static_example();

    checkErrorExpr("B.notPresentField", "0xF736F");
  }

  @Test
  public void testMissingFieldUnqualified() throws IOException {
    checkErrorExpr("notPresentField", "0xFD118");
  }

  @Test
  public void testMissingMethodQualified() throws IOException {
    checkErrorExpr("pac.kage.not.present.Type.method()", "0xF735F");
  }

  @Test
  public void testSubClassesKnowsStaticMethodsOfSuperClasses() throws IOException {
    init_static_example();

    checkExpr("C.test()", "void");
  }

  @Test
  public void testSubClassesKnowsStaticFieldsOfSuperClasses() throws IOException {
    init_static_example();

    checkExpr("C.field", "int");
  }

  @Test
  public void testSubClassesKnowsStaticTypesOfSuperClasses() throws IOException {
    init_static_example();

    checkType("C.D", "A.D");
    checkExpr("C.D.x", "int");
  }

  /**
   * test if we can use functions and variables
   * as e.g. imported by Class2MC
   */
  @Test
  public void testDoNotFilterBasicTypes() throws IOException {
    IOOSymbolsGlobalScope gs = OOSymbolsMill.globalScope();
    TypeSymbol A = inScope(gs,
        type("A",
            Collections.emptyList(),
            Collections.emptyList(),
            List.of(function("func", _voidSymType)),
            List.of(variable("var", _booleanSymType))
        )
    );
    inScope(gs, variable("a", createTypeObject(A)));

    // todo copied from typecheck 1 tests, but to be discussed
    // functions are available as if they were static
    checkExpr("A.func()", "void");
    //checkExpr("a.func()", "void");

    // variables are available as if they were non-static
    //checkErrorExpr("A.var", "0xA0241");
    //checkExpr("a.var", "boolean");
  }

  protected void init_method_test() {
    //see MC Ticket #3298 for this example, use test instead of bar because bar is a keyword in CombineExpressions
    //create types A, B and C, B extends A, C extends B
    IOOSymbolsGlobalScope globalScope = OOSymbolsMill.globalScope();
    TypeSymbol a = inScope(globalScope, type("A"));
    SymTypeExpression aSym = createTypeObject(a);
    TypeSymbol b = inScope(globalScope, type("B", Lists.newArrayList(aSym)));
    SymTypeExpression bSym = createTypeObject(b);
    TypeSymbol c = inScope(globalScope, type("C", Lists.newArrayList(bSym)));
    SymTypeExpression cSym = createTypeObject(c);

    inScope(globalScope, method("foo", aSym, aSym));
    inScope(globalScope, method("foo", bSym, bSym));
    inScope(globalScope, method("foo", cSym, cSym));

    inScope(globalScope, method("foo", aSym, aSym, aSym));
    inScope(globalScope, method("foo", bSym, aSym, bSym));
    inScope(globalScope, method("foo", cSym, aSym, cSym));

    inScope(globalScope, method("foo2", aSym, aSym, bSym, cSym));
    inScope(globalScope, method("foo2", aSym, cSym, cSym, aSym));

    inScope(globalScope, method("foo2", bSym, aSym, bSym));
    inScope(globalScope, method("foo2", cSym, cSym, aSym));

    inScope(globalScope, field("a", aSym));
    inScope(globalScope, field("b", bSym));
    inScope(globalScope, field("c", cSym));
  }

  @Test
  public void testCorrectMethodChosen() throws IOException {
    init_method_test();

    /*
    available methods:
    A foo(A x)
    B foo(B x)
    C foo(C x)
    A foo(A x, A y)
    B foo(A x, B y)
    C foo(A x, C y)

    A foo2(A x, B y, C z)
    C foo2(C x, C y, A z)
    B foo2(A x, B y)
    C foo2(C x, A y)
    */

    checkExpr("foo(a)", "A");
    checkExpr("foo(b)", "B");
    checkExpr("foo(c)", "C");

    checkExpr("foo(a, a)", "A");
    checkExpr("foo(a, b)", "B");
    checkExpr("foo(a, c)", "C");
    checkExpr("foo(b, a)", "A");
    checkExpr("foo(b, b)", "B");
    checkExpr("foo(b, c)", "C");
    checkExpr("foo(c, a)", "A");
    checkExpr("foo(c, b)", "B");
    checkExpr("foo(c, c)", "C");

    checkErrorExpr("foo2(c, c, c)", "0xFD446");

    checkErrorExpr("foo2(a, a)", "0xFD444");
    checkErrorExpr("foo2(b, a)", "0xFD444");
    checkErrorExpr("foo2(c, b)", "0xFD446");
    checkErrorExpr("foo2(c, c)", "0xFD446");

    checkExpr("foo2(a, b)", "B");
    checkExpr("foo2(a, c)", "B");
    checkExpr("foo2(b, b)", "B");
    checkExpr("foo2(b, c)", "B");
    checkExpr("foo2(c, a)", "C");
  }

  @Test
  public void testCorrectMethodChosenPrimitives() throws IOException {
    IBasicSymbolsGlobalScope gs = BasicSymbolsMill.globalScope();
    // primitive id
    inScope(gs, function("pid", _charSymType, _charSymType));
    inScope(gs, function("pid", _byteSymType, _byteSymType));
    inScope(gs, function("pid", _shortSymType, _shortSymType));
    inScope(gs, function("pid", _intSymType, _intSymType));
    inScope(gs, function("pid", _longSymType, _longSymType));
    inScope(gs, function("pid", _floatSymType, _floatSymType));
    inScope(gs, function("pid", _doubleSymType, _doubleSymType));
    inScope(gs, function("pid", _booleanSymType, _booleanSymType));

    // based on String::valueOf
    // byte and short get converted to int
    inScope(gs, function("pid2", _charSymType, _charSymType));
    inScope(gs, function("pid2", _intSymType, _intSymType));
    inScope(gs, function("pid2", _longSymType, _longSymType));
    inScope(gs, function("pid2", _floatSymType, _floatSymType));
    inScope(gs, function("pid2", _doubleSymType, _doubleSymType));
    inScope(gs, function("pid2", _booleanSymType, _booleanSymType));

    checkExpr("pid(varchar)", "char");
    checkExpr("pid(varbyte)", "byte");
    checkExpr("pid(varshort)", "short");
    checkExpr("pid(varint)", "int");
    checkExpr("pid(varlong)", "long");
    checkExpr("pid(varfloat)", "float");
    checkExpr("pid(vardouble)", "double");
    checkExpr("pid(varboolean)", "boolean");

    checkExpr("pid2(varchar)", "char");
    checkExpr("pid2(varbyte)", "int");
    checkExpr("pid2(varshort)", "int");
    checkExpr("pid2(varint)", "int");
    checkExpr("pid2(varlong)", "long");
    checkExpr("pid2(varfloat)", "float");
    checkExpr("pid2(vardouble)", "double");
    checkExpr("pid2(varboolean)", "boolean");
  }

  @Test
  public void testCorrectMethodChosenStringValueOf() throws IOException {
    // explicitly tries to recreate String::valueOf
    IBasicSymbolsGlobalScope gs = BasicSymbolsMill.globalScope();
    // Object being the superType of reference types,
    SymTypeExpression objectSymType =
        createTypeObject(inScope(gs, type("Object")));
    _CharacterSymType.getTypeInfo().setSuperTypesList(List.of(objectSymType));
    _ByteSymType.getTypeInfo().setSuperTypesList(List.of(objectSymType));
    _ShortSymType.getTypeInfo().setSuperTypesList(List.of(objectSymType));
    _IntegerSymType.getTypeInfo().setSuperTypesList(List.of(objectSymType));
    _LongSymType.getTypeInfo().setSuperTypesList(List.of(objectSymType));
    _FloatSymType.getTypeInfo().setSuperTypesList(List.of(objectSymType));
    _DoubleSymType.getTypeInfo().setSuperTypesList(List.of(objectSymType));
    _IntegerSymType.getTypeInfo().setSuperTypesList(List.of(objectSymType));

    inScope(gs, function("valueOf", _boxedString, objectSymType));
    inScope(gs, function("valueOf", _boxedString, _charSymType));
    inScope(gs, function("valueOf", _boxedString, _intSymType));
    inScope(gs, function("valueOf", _boxedString, _longSymType));
    inScope(gs, function("valueOf", _boxedString, _floatSymType));
    inScope(gs, function("valueOf", _boxedString, _doubleSymType));
    inScope(gs, function("valueOf", _boxedString, _booleanSymType));
    // presumably not necessary, but for good measure:
    inScope(gs, function("valueOf", _boxedString,
        createTypeArray(_charSymType, 1)));
    inScope(gs, function("valueOf", _boxedString,
        createTypeArray(_charSymType, 1), _intSymType, _intSymType));

    checkExpr("valueOf(varchar)", "java.lang.String");
    checkExpr("valueOf(varbyte)", "java.lang.String");
    checkExpr("valueOf(varshort)", "java.lang.String");
    checkExpr("valueOf(varint)", "java.lang.String");
    checkExpr("valueOf(varlong)", "java.lang.String");
    checkExpr("valueOf(varfloat)", "java.lang.String");
    checkExpr("valueOf(vardouble)", "java.lang.String");
    checkExpr("valueOf(varboolean)", "java.lang.String");

    checkExpr("valueOf(varCharacter)", "java.lang.String");
    checkExpr("valueOf(varByte)", "java.lang.String");
    checkExpr("valueOf(varShort)", "java.lang.String");
    checkExpr("valueOf(varInteger)", "java.lang.String");
    checkExpr("valueOf(varLong)", "java.lang.String");
    checkExpr("valueOf(varFloat)", "java.lang.String");
    checkExpr("valueOf(varDouble)", "java.lang.String");
    checkExpr("valueOf(varBoolean)", "java.lang.String");
  }

  @Test
  public void testArrayAccessExpressionArray() throws IOException {
    IBasicSymbolsGlobalScope gs = BasicSymbolsMill.globalScope();
    inScope(gs, variable("intArray1", createTypeArray(_intSymType, 1)));
    inScope(gs, variable("intArray2", createTypeArray(_intSymType, 2)));
    inScope(gs, variable("intArray3", createTypeArray(_intSymType, 3)));

    checkExpr("intArray1[0]", "int");
    checkExpr("intArray2[0]", "int[]");
    checkExpr("intArray3[0]", "int[][]");
    checkExpr("intArray3[0][0]", "int[]");
  }

  @Test
  public void testArrayAccessExpressionTuple() throws IOException {
    IBasicSymbolsGlobalScope gs = BasicSymbolsMill.globalScope();
    inScope(gs, variable("intdoubleTuple", createTuple(_intSymType, _doubleSymType)));
    inScope(gs, variable("tupleOfTuples", createTuple(
            createTuple(_intSymType, _doubleSymType),
            createTuple(_charSymType, _floatSymType)
        )
    ));

    checkExpr("intdoubleTuple[0]", "int");
    checkExpr("intdoubleTuple[1]", "double");
    checkExpr("tupleOfTuples[0]", "(int, double)");
    checkExpr("tupleOfTuples[0][0]", "int");
  }

  @Test
  public void testArrayAccessExpressionArrayAndTuple() throws IOException {
    IBasicSymbolsGlobalScope gs = BasicSymbolsMill.globalScope();
    inScope(gs, variable("tupleOfArray",
        createTuple(createTypeArray(_intSymType, 1), _intSymType)
    ));
    inScope(gs, variable("arrayOfTuple",
        createTypeArray(createTuple(_intSymType, _floatSymType), 1)
    ));

    checkExpr("tupleOfArray[0][0]", "int");
    checkExpr("arrayOfTuple[0][0]", "int");
  }

  @Test
  public void testArrayAccessExpressionInvalid() throws IOException {
    IBasicSymbolsGlobalScope gs = BasicSymbolsMill.globalScope();
    inScope(gs, variable("intArray1", createTypeArray(_intSymType, 1)));
    inScope(gs, variable("intdoubleTuple", createTuple(_intSymType, _doubleSymType)));

    checkErrorExpr("varint[0]", "0xFDF86");
    checkErrorExpr("intArray1[0][0]", "0xFDF86");
    checkErrorExpr("intArray1[1.1]", "0xFD3F6");
    checkErrorExpr("intdoubleTuple[1.1]", "0xFD3F3");

    // for tuples, the actual value is relevant
    checkErrorExpr("intdoubleTuple[-1]", "0xFD3F0");
    checkErrorExpr("intdoubleTuple[2]", "0xFD3F0");
    checkErrorExpr("intdoubleTuple[varint]", "0xFD3F1");
  }

}
