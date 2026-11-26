/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal.compatibility;

import de.monticore.ast.ASTNode;
import de.monticore.runtime.junit.PrettyPrinterTester.ParseFunction;
import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.temporal.AbstractTemporalCompatibilityTest;
import de.monticore.temporal.compatibility.temporalswithuglyexpressions.TemporalsWithUglyExpressionsMill;
import de.monticore.temporal.compatibility.temporalswithuglyexpressions._parser.TemporalsWithUglyExpressionsParser;
import de.monticore.temporal.isotemporals._visitor.ISOTemporalsTraverser;
import org.junit.jupiter.api.BeforeEach;

import java.util.Optional;

@TestWithMCLanguage(TemporalsWithUglyExpressionsMill.class)
public class TemporalsWithUglyExpressionsTest extends AbstractTemporalCompatibilityTest {

  protected TemporalsWithUglyExpressionsParser parser;

  @BeforeEach
  public void setUpParser() {
    parser = TemporalsWithUglyExpressionsMill.parser();
  }

  @Override
  protected TemporalsWithUglyExpressionsParser getParser() {
    return parser;
  }

  @Override
  protected ParseFunction<ASTNode> temporalParser() {
    return input -> parser.parse_StringTemporal(input).map(ast ->
        ast.isPresentInstant() ? ast.getInstant() : ast.getPeriod());
  }

  @Override
  protected ParseFunction<ASTNode> escapedParser() {
    return input -> parser.parse_StringEscapedTemporals(input).map(ast -> ast);
  }

  @Override
  protected Optional<ParseFunction<ASTNode>> expressionParser() {
    return Optional.of(input -> parser.parse_StringTemporalExpression(input)
        .map(ast -> ast.getExpression()));
  }

  @Override
  protected ISOTemporalsTraverser traverser() {
    return TemporalsWithUglyExpressionsMill.traverser();
  }

  @Override
  protected String prettyPrint(ASTNode ast) {
    return TemporalsWithUglyExpressionsMill.prettyPrint(ast, false);
  }
}
