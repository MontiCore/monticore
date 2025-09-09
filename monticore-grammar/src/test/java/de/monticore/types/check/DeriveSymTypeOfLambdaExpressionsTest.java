/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.expressions.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import de.monticore.expressions.combineexpressionswithliterals._ast.ASTFoo;
import de.monticore.expressions.combineexpressionswithliterals._parser.CombineExpressionsWithLiteralsParser;
import de.monticore.expressions.combineexpressionswithliterals._symboltable.ICombineExpressionsWithLiteralsArtifactScope;
import de.monticore.expressions.combineexpressionswithliterals._visitor.CombineExpressionsWithLiteralsTraverser;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisTraverser;
import de.monticore.expressions.lambdaexpressions._symboltable.LambdaExpressionsSTCompleteTypes;
import de.monticore.grammar.grammar_withconcepts.FullSynthesizeFromMCSGT4Grammar;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import de.se_rwth.commons.logging.Log;

public class DeriveSymTypeOfLambdaExpressionsTest extends DeriveSymTypeAbstractTest {

  /**
   * Focus: Deriving Type of Literals, here:
   * literals/MCLiteralsBasis.mc4
   */

  @BeforeEach
  public void init() {
    CombineExpressionsWithLiteralsMill.reset();
    CombineExpressionsWithLiteralsMill.init();
    BasicSymbolsMill.initializePrimitives();
    TypeSymbol str = CombineExpressionsWithLiteralsMill.typeSymbolBuilder()
      .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
      .setName("String")
      .build();
    CombineExpressionsWithLiteralsMill.globalScope().add(str);
  }

  @Override
  protected void setupTypeCheck() {
    // This is an auxiliary
    FullDeriveFromCombineExpressionsWithLiterals derLit = new FullDeriveFromCombineExpressionsWithLiterals();

    // other arguments not used (and therefore deliberately null)
    // This is the TypeChecker under Test:
    setTypeCheck(new TypeCalculator(null, derLit));
  }

  // Parser used for convenience:
  // (may be any other Parser that understands CommonExpressions)
  CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();

  @Override
  protected Optional<ASTExpression> parseStringExpression(String expression) throws IOException {
    return p.parse_StringExpression(expression);
  }

  @Override
  protected ExpressionsBasisTraverser getUsedLanguageTraverser() {
    return CombineExpressionsWithLiteralsMill.traverser();
  }

  protected ASTExpression parseAndGenerateSymbolTable(String expression) throws IOException {
    ASTExpression astex = parseExpression(expression);
    // add symbol table for the lambda parameter symbols
    ASTFoo rootNode = CombineExpressionsWithLiteralsMill.fooBuilder()
        .setExpression(astex)
        .build();
    ICombineExpressionsWithLiteralsArtifactScope rootScope =
        CombineExpressionsWithLiteralsMill.scopesGenitorDelegator().createFromAST(rootNode);
    rootScope.setName("rootName");
    // complete the symbol table
    CombineExpressionsWithLiteralsTraverser traverser =
        CombineExpressionsWithLiteralsMill.traverser();
    traverser.add4LambdaExpressions(
        new LambdaExpressionsSTCompleteTypes(new FullSynthesizeFromMCSGT4Grammar()));
    astex.accept(traverser);
    return astex;
  }

  protected void checkWithST(String expression, String expectedType) throws IOException {
    setupTypeCheck();
    ASTExpression astex = parseAndGenerateSymbolTable(expression);

    Assertions.assertEquals(expectedType, getTypeCalculator().typeOf(astex).print());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  /*--------------------------------------------------- TESTS ---------------------------------------------------------*/

  @Test
  public void deriveFromLambdaExpressionNoParameterTest() throws IOException {
    setFlatExpressionScopeSetter(CombineExpressionsWithLiteralsMill.globalScope());
    // example with int
    check("() -> 5", "() -> int");
    // example with lambda nesting
    check("() -> () -> 5", "() -> () -> int");
  }

  @Test
  public void deriveFromLambdaExpressionOneParameterTest() throws IOException {
    setFlatExpressionScopeSetter(CombineExpressionsWithLiteralsMill.globalScope());
    // example with int, long
    check("(int x) -> 5L", "int -> long");
    // example with input equaling output
    checkWithST("(int x) -> x", "int -> int");
    // example with lambda nesting
    checkWithST("(int x) -> (int y) -> x + y", "int -> int -> int");
  }

  @Test
  public void deriveFromLambdaExpressionMultipleParameterTest() throws IOException {
    setFlatExpressionScopeSetter(CombineExpressionsWithLiteralsMill.globalScope());
    // example with int, long, int
    check("(int x, long y) -> 5", "(int, long) -> int");
    // example with lambda nesting
    check("(int x, long y) -> () -> 5", "(int, long) -> () -> int");
    // example with int, long, expression
    checkWithST("(int x, long y) -> x + y", "(int, long) -> long");
  }

}
