/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal.compatibility;

import de.monticore.ast.ASTNode;
import de.monticore.runtime.junit.PrettyPrinterTester.ParseFunction;
import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.temporal.AbstractTemporalCompatibilityTest;
import de.monticore.temporal.compatibility.temporalswithsiunittypes4computing.TemporalsWithSIUnitTypes4ComputingMill;
import de.monticore.temporal.compatibility.temporalswithsiunittypes4computing._parser.TemporalsWithSIUnitTypes4ComputingParser;
import de.monticore.temporal.isotemporals._visitor.ISOTemporalsTraverser;
import org.junit.jupiter.api.BeforeEach;

import java.util.Optional;

@TestWithMCLanguage(TemporalsWithSIUnitTypes4ComputingMill.class)
public class TemporalsWithSIUnitTypes4ComputingTest extends AbstractTemporalCompatibilityTest {

  protected TemporalsWithSIUnitTypes4ComputingParser parser;

  @BeforeEach
  public void setUpParser() {
    parser = TemporalsWithSIUnitTypes4ComputingMill.parser();
  }

  @Override
  protected TemporalsWithSIUnitTypes4ComputingParser getParser() {
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
    return TemporalsWithSIUnitTypes4ComputingMill.traverser();
  }

  @Override
  protected String prettyPrint(ASTNode ast) {
    return TemporalsWithSIUnitTypes4ComputingMill.prettyPrint(ast, false);
  }
}
