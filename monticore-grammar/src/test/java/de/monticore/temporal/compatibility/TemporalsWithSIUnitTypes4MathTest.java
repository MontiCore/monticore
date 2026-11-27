/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal.compatibility;

import de.monticore.ast.ASTNode;
import de.monticore.runtime.junit.PrettyPrinterTester.ParseFunction;
import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.temporal.AbstractTemporalCompatibilityTest;
import de.monticore.temporal.compatibility.temporalswithsiunittypes4math.TemporalsWithSIUnitTypes4MathMill;
import de.monticore.temporal.compatibility.temporalswithsiunittypes4math._parser.TemporalsWithSIUnitTypes4MathParser;
import de.monticore.temporal.isotemporals._visitor.ISOTemporalsTraverser;
import org.junit.jupiter.api.BeforeEach;

import java.util.Optional;

@TestWithMCLanguage(TemporalsWithSIUnitTypes4MathMill.class)
public class TemporalsWithSIUnitTypes4MathTest extends AbstractTemporalCompatibilityTest {

  protected TemporalsWithSIUnitTypes4MathParser parser;

  @BeforeEach
  public void setUpParser() {
    parser = TemporalsWithSIUnitTypes4MathMill.parser();
  }

  @Override
  protected TemporalsWithSIUnitTypes4MathParser getParser() {
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
    return TemporalsWithSIUnitTypes4MathMill.traverser();
  }

  @Override
  protected String prettyPrint(ASTNode ast) {
    return TemporalsWithSIUnitTypes4MathMill.prettyPrint(ast, false);
  }
}
