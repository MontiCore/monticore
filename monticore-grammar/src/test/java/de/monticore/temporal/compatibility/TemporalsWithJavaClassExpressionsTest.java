/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal.compatibility;

import de.monticore.ast.ASTNode;
import de.monticore.runtime.junit.PrettyPrinterTester.ParseFunction;
import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.temporal.AbstractTemporalCompatibilityTest;
import de.monticore.temporal.compatibility.temporalswithjavaclassexpressions.TemporalsWithJavaClassExpressionsMill;
import de.monticore.temporal.compatibility.temporalswithjavaclassexpressions._parser.TemporalsWithJavaClassExpressionsParser;
import de.monticore.temporal.isotemporals._visitor.ISOTemporalsTraverser;
import org.junit.jupiter.api.BeforeEach;

import java.util.Optional;

@TestWithMCLanguage(TemporalsWithJavaClassExpressionsMill.class)
public class TemporalsWithJavaClassExpressionsTest extends AbstractTemporalCompatibilityTest {

  protected TemporalsWithJavaClassExpressionsParser parser;

  @BeforeEach
  public void setUpParser() {
    parser = TemporalsWithJavaClassExpressionsMill.parser();
  }

  @Override
  protected TemporalsWithJavaClassExpressionsParser getParser() {
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
    return TemporalsWithJavaClassExpressionsMill.traverser();
  }

  @Override
  protected String prettyPrint(ASTNode ast) {
    return TemporalsWithJavaClassExpressionsMill.prettyPrint(ast, false);
  }
}
