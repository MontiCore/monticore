/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal.compatibility;

import de.monticore.ast.ASTNode;
import de.monticore.runtime.junit.PrettyPrinterTester.ParseFunction;
import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.temporal.AbstractTemporalCompatibilityTest;
import de.monticore.temporal.compatibility.temporalswithcompleteness.TemporalsWithCompletenessMill;
import de.monticore.temporal.compatibility.temporalswithcompleteness._parser.TemporalsWithCompletenessParser;
import de.monticore.temporal.isotemporals._visitor.ISOTemporalsTraverser;
import org.junit.jupiter.api.BeforeEach;

import java.util.Optional;

@TestWithMCLanguage(TemporalsWithCompletenessMill.class)
public class TemporalsWithCompletenessTest extends AbstractTemporalCompatibilityTest {

  protected TemporalsWithCompletenessParser parser;

  @BeforeEach
  public void setUpParser() {
    parser = TemporalsWithCompletenessMill.parser();
  }

  @Override
  protected TemporalsWithCompletenessParser getParser() {
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
    return TemporalsWithCompletenessMill.traverser();
  }

  @Override
  protected String prettyPrint(ASTNode ast) {
    return TemporalsWithCompletenessMill.prettyPrint(ast, false);
  }
}
