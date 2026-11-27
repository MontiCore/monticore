/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal.compatibility;

import de.monticore.ast.ASTNode;
import de.monticore.runtime.junit.PrettyPrinterTester.ParseFunction;
import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.temporal.AbstractTemporalCompatibilityTest;
import de.monticore.temporal.compatibility.temporalswithsiunitliterals.TemporalsWithSIUnitLiteralsMill;
import de.monticore.temporal.compatibility.temporalswithsiunitliterals._parser.TemporalsWithSIUnitLiteralsParser;
import de.monticore.temporal.isotemporals._visitor.ISOTemporalsTraverser;
import org.junit.jupiter.api.BeforeEach;

import java.util.Optional;

@TestWithMCLanguage(TemporalsWithSIUnitLiteralsMill.class)
public class TemporalsWithSIUnitLiteralsTest extends AbstractTemporalCompatibilityTest {

  protected TemporalsWithSIUnitLiteralsParser parser;

  @BeforeEach
  public void setUpParser() {
    parser = TemporalsWithSIUnitLiteralsMill.parser();
  }

  @Override
  protected TemporalsWithSIUnitLiteralsParser getParser() {
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
    return TemporalsWithSIUnitLiteralsMill.traverser();
  }

  @Override
  protected String prettyPrint(ASTNode ast) {
    return TemporalsWithSIUnitLiteralsMill.prettyPrint(ast, false);
  }
}
