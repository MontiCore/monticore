/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal.compatibility;

import de.monticore.ast.ASTNode;
import de.monticore.runtime.junit.PrettyPrinterTester.ParseFunction;
import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.temporal.AbstractTemporalCompatibilityTest;
import de.monticore.temporal.compatibility.temporalswithassignmentexpressions.TemporalsWithAssignmentExpressionsMill;
import de.monticore.temporal.compatibility.temporalswithassignmentexpressions._parser.TemporalsWithAssignmentExpressionsParser;
import de.monticore.temporal.isotemporals._visitor.ISOTemporalsTraverser;
import org.junit.jupiter.api.BeforeEach;

import java.util.Optional;

@TestWithMCLanguage(TemporalsWithAssignmentExpressionsMill.class)
public class TemporalsWithAssignmentExpressionsTest extends AbstractTemporalCompatibilityTest {

  protected TemporalsWithAssignmentExpressionsParser parser;

  @BeforeEach
  public void setUpParser() {
    parser = TemporalsWithAssignmentExpressionsMill.parser();
  }

  @Override
  protected TemporalsWithAssignmentExpressionsParser getParser() {
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
    return TemporalsWithAssignmentExpressionsMill.traverser();
  }

  @Override
  protected String prettyPrint(ASTNode ast) {
    return TemporalsWithAssignmentExpressionsMill.prettyPrint(ast, false);
  }
}
