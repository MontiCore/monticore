/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal.compatibility;

import de.monticore.ast.ASTNode;
import de.monticore.runtime.junit.PrettyPrinterTester.ParseFunction;
import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.temporal.AbstractTemporalCompatibilityTest;
import de.monticore.temporal.compatibility.temporalswithoclexpressions.TemporalsWithOCLExpressionsMill;
import de.monticore.temporal.compatibility.temporalswithoclexpressions._parser.TemporalsWithOCLExpressionsParser;
import de.monticore.temporal.isotemporals._visitor.ISOTemporalsTraverser;
import org.junit.jupiter.api.BeforeEach;

import java.util.Optional;

@TestWithMCLanguage(TemporalsWithOCLExpressionsMill.class)
public class TemporalsWithOCLExpressionsTest extends AbstractTemporalCompatibilityTest {

  protected TemporalsWithOCLExpressionsParser parser;

  @BeforeEach
  public void setUpParser() {
    parser = TemporalsWithOCLExpressionsMill.parser();
  }

  @Override
  protected TemporalsWithOCLExpressionsParser getParser() {
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
    return TemporalsWithOCLExpressionsMill.traverser();
  }

  @Override
  protected String prettyPrint(ASTNode ast) {
    return TemporalsWithOCLExpressionsMill.prettyPrint(ast, false);
  }
}
