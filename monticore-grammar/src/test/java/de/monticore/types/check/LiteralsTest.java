/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import com.google.common.collect.Lists;
import de.monticore.expressions.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import de.monticore.expressions.combineexpressionswithliterals._parser.CombineExpressionsWithLiteralsParser;
import de.monticore.expressions.combineexpressionswithliterals._symboltable.ICombineExpressionsWithLiteralsScope;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisTraverser;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.oosymbols.OOSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Optional;

import static de.monticore.types.check.DefsTypeBasic.*;
import static de.monticore.types.check.DefsTypeBasic.field;

public class LiteralsTest extends DeriveSymTypeAbstractTest {

  @Before
  public void setupForEach() {
    LogStub.init();         // replace log by a sideffect free variant
    // LogStub.initPlusLog();  // for manual testing purpose only
    Log.enableFailQuick(false);

    // Setting up a Scope Infrastructure (without a global Scope)
    CombineExpressionsWithLiteralsMill.reset();
    CombineExpressionsWithLiteralsMill.init();
    CombineExpressionsWithLiteralsMill.globalScope().clear();
    BasicSymbolsMill.initializePrimitives();
    CombineExpressionsWithLiteralsMill.globalScope().add(DefsTypeBasic.field("aShort", _shortSymType));
    CombineExpressionsWithLiteralsMill.globalScope().add(DefsTypeBasic.field("aChar", _charSymType));
    CombineExpressionsWithLiteralsMill.globalScope().add(DefsTypeBasic.field("anInt", _intSymType));
    CombineExpressionsWithLiteralsMill.globalScope().add(DefsTypeBasic.field("aLong", _longSymType));
    DefsTypeBasic.setup();
    // we add a variety of TypeSymbols to the same scope (which in reality doesn't happen)
    LogStub.init();
    setFlatExpressionScopeSetter(CombineExpressionsWithLiteralsMill.globalScope());
  }

  @Override
  protected void setupTypeCheck() {
    // This is an auxiliary
    FullDeriveFromCombineExpressionsWithLiterals derLit = new FullDeriveFromCombineExpressionsWithLiterals();

    // other arguments not used (and therefore deliberately null)
    // This is the TypeChecker under Test:
    setTypeCheck(new TypeCalculator(null, derLit));
  }

  @Override
  protected Optional<ASTExpression> parseStringExpression(String expression) throws IOException {
    return CombineExpressionsWithLiteralsMill.parser().parse_StringExpression(expression);
  }

  @Override
  protected ExpressionsBasisTraverser getUsedLanguageTraverser() {
    return CombineExpressionsWithLiteralsMill.traverser();
  }

  @Test
  public void testAutomaticLiteralDowncastShort() throws IOException {
    check("aShort = 'a'", "short");
    check("aShort = 0", "short");
    check("aShort = +1", "short");
    check("aShort = -1", "short");
    check("aShort = 32767", "short");
    check("aShort = -32768", "short");
    check("aShort = 0 + 0", "short");
    check("aShort = 32767 + 0", "short");
    check("aShort = 0 + 32767", "short");
    check("aShort = -32768 + 0", "short");
    check("aShort = 0 + -32768", "short");
    check("aShort = 0 - 32768", "short");
    check("aShort = 32768 - 1", "short");
    check("aShort = 32768 + -1", "short");
    check("aShort = -32769 + 1", "short");
    check("aShort = 1 + -32769", "short");
    check("aShort = 1 - 32769", "short");
    check("aShort = 32767 + 1 - 1", "short");
    check("aShort = -32768 + 1 - 1", "short");
    check("aShort = 0 * 0", "short");
    check("aShort = 32767 * 1", "short");
    check("aShort = 1 * 32767", "short");
    check("aShort = 32768 * -1", "short");
    check("aShort = -1 * 32768", "short");
    check("aShort = 16383 * 2 + 1", "short");
    check("aShort = 16384 * -2", "short");
    check("aShort = 32767 / 1", "short");
    check("aShort = 32768 / -1", "short");
    check("aShort = 1 / 32768", "short");
    check("aShort = 32767 % 1", "short");
    check("aShort = -32768 % 1", "short");
    check("aShort = 1 % 32768", "short");
  }

  @Test
  public void testAutomaticLiteralDowncastChar() throws IOException {
    check("aChar = 0", "char");
    check("aChar = 65535", "char");
    check("aChar = 0 + 0", "char");
    check("aChar = 65535 + 0", "char");
    check("aChar = 0 + 65535", "char");
    check("aChar = 65536 - 1", "char");
    check("aChar = -1 + 65536", "char");
    check("aChar = 65535 + 1 - 1", "char");
    check("aChar = 0 * 0", "char");
    check("aChar = 0 * -1", "char");
    check("aChar = -1 * 0", "char");
    check("aChar = 32767 * 2 + 1", "char");
    check("aChar = 65535 / 1", "char");
    check("aChar = 1 / 65536", "char");
    check("aChar = 65535 % 1", "char");
    check("aChar = 1 % 65536", "char");

    checkError("aChar = -1", "0xTODO");
    checkError("aChar = 65536", "0xTODO");
  }

  @Test
  public void testIntegerLiteralRange() throws IOException {
    check("anInt = 0", "int");
    check("anInt = 2147483647", "int");
    check("anInt = -2147483647", "int");
    check("anInt = 12038123", "int");
    check("anInt = -12391273", "int");

    checkError("anInt = 2147483648", "0xA0208");
    checkError("anInt = -2147483649", "0xA0208");
  }

  @Test
  public void testLongLiteralRange() throws IOException {
    check("aLong = 9223372036854775807l", "long");
    check("aLong = -9223372036854775807l", "long");
    check("aLong = 0", "long");
    check("aLong = 2131203810", "long");
    check("aLong = -123124982", "long");

    checkError("aLong = 9223372036854775808l", "0xA0209");
    checkError("aLong = -9223372036854775809l", "0xA0209");
  }

  @Test
  public void testChangeLiteralRange() throws IOException {
    check("anInt = 2147483647", "int");
    BasicSymbolsMill.getTypeCheckSettings().setMAX_INT(BigInteger.valueOf(2000));
    checkError("anInt = 2147483647", "0xA0208");
    checkError("anInt = 2001", "0xA0208");
    check("anInt = 2000", "int");
    BasicSymbolsMill.getTypeCheckSettings().setMAX_INT(BigInteger.valueOf(Integer.MAX_VALUE));
    check("anInt = 2147483647", "int");
  }

}
