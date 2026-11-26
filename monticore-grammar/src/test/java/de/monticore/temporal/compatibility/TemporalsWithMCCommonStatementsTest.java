/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal.compatibility;

import de.monticore.ast.ASTNode;
import de.monticore.runtime.junit.PrettyPrinterTester.ParseFunction;
import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.temporal.AbstractTemporalCompatibilityTest;
import de.monticore.temporal.compatibility.temporalswithmccommonstatements.TemporalsWithMCCommonStatementsMill;
import de.monticore.temporal.compatibility.temporalswithmccommonstatements._parser.TemporalsWithMCCommonStatementsParser;
import de.monticore.temporal.isotemporals._visitor.ISOTemporalsTraverser;
import org.junit.jupiter.api.BeforeEach;

import java.util.Optional;

@TestWithMCLanguage(TemporalsWithMCCommonStatementsMill.class)
public class TemporalsWithMCCommonStatementsTest extends AbstractTemporalCompatibilityTest {

  protected TemporalsWithMCCommonStatementsParser parser;

  @BeforeEach
  public void setUpParser() {
    parser = TemporalsWithMCCommonStatementsMill.parser();
  }

  @Override
  protected TemporalsWithMCCommonStatementsParser getParser() {
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
    return TemporalsWithMCCommonStatementsMill.traverser();
  }

  @Override
  protected String prettyPrint(ASTNode ast) {
    return TemporalsWithMCCommonStatementsMill.prettyPrint(ast, false);
  }
}
