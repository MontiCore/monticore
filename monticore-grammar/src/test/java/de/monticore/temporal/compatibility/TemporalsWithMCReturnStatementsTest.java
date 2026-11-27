/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal.compatibility;

import de.monticore.ast.ASTNode;
import de.monticore.runtime.junit.PrettyPrinterTester.ParseFunction;
import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.temporal.AbstractTemporalCompatibilityTest;
import de.monticore.temporal.compatibility.temporalswithmcreturnstatements.TemporalsWithMCReturnStatementsMill;
import de.monticore.temporal.compatibility.temporalswithmcreturnstatements._parser.TemporalsWithMCReturnStatementsParser;
import de.monticore.temporal.isotemporals._visitor.ISOTemporalsTraverser;
import org.junit.jupiter.api.BeforeEach;

import java.util.Optional;

@TestWithMCLanguage(TemporalsWithMCReturnStatementsMill.class)
public class TemporalsWithMCReturnStatementsTest extends AbstractTemporalCompatibilityTest {

  protected TemporalsWithMCReturnStatementsParser parser;

  @BeforeEach
  public void setUpParser() {
    parser = TemporalsWithMCReturnStatementsMill.parser();
  }

  @Override
  protected TemporalsWithMCReturnStatementsParser getParser() {
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
    return TemporalsWithMCReturnStatementsMill.traverser();
  }

  @Override
  protected String prettyPrint(ASTNode ast) {
    return TemporalsWithMCReturnStatementsMill.prettyPrint(ast, false);
  }
}
