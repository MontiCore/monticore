/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal.compatibility;

import de.monticore.ast.ASTNode;
import de.monticore.runtime.junit.PrettyPrinterTester.ParseFunction;
import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.temporal.AbstractTemporalCompatibilityTest;
import de.monticore.temporal.compatibility.temporalswithmclowlevelstatements.TemporalsWithMCLowLevelStatementsMill;
import de.monticore.temporal.compatibility.temporalswithmclowlevelstatements._parser.TemporalsWithMCLowLevelStatementsParser;
import de.monticore.temporal.isotemporals._visitor.ISOTemporalsTraverser;
import org.junit.jupiter.api.BeforeEach;

import java.util.Optional;

@TestWithMCLanguage(TemporalsWithMCLowLevelStatementsMill.class)
public class TemporalsWithMCLowLevelStatementsTest extends AbstractTemporalCompatibilityTest {

  protected TemporalsWithMCLowLevelStatementsParser parser;

  @BeforeEach
  public void setUpParser() {
    parser = TemporalsWithMCLowLevelStatementsMill.parser();
  }

  @Override
  protected TemporalsWithMCLowLevelStatementsParser getParser() {
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
    return Optional.empty();
  }

  @Override
  protected ISOTemporalsTraverser traverser() {
    return TemporalsWithMCLowLevelStatementsMill.traverser();
  }

  @Override
  protected String prettyPrint(ASTNode ast) {
    return TemporalsWithMCLowLevelStatementsMill.prettyPrint(ast, false);
  }
}
