/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal.compatibility;

import de.monticore.ast.ASTNode;
import de.monticore.runtime.junit.PrettyPrinterTester.ParseFunction;
import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.temporal.AbstractTemporalCompatibilityTest;
import de.monticore.temporal.compatibility.temporalswithmcarraytypes.TemporalsWithMCArrayTypesMill;
import de.monticore.temporal.compatibility.temporalswithmcarraytypes._parser.TemporalsWithMCArrayTypesParser;
import de.monticore.temporal.isotemporals._visitor.ISOTemporalsTraverser;
import org.junit.jupiter.api.BeforeEach;

import java.util.Optional;

@TestWithMCLanguage(TemporalsWithMCArrayTypesMill.class)
public class TemporalsWithMCArrayTypesTest extends AbstractTemporalCompatibilityTest {

  protected TemporalsWithMCArrayTypesParser parser;

  @BeforeEach
  public void setUpParser() {
    parser = TemporalsWithMCArrayTypesMill.parser();
  }

  @Override
  protected TemporalsWithMCArrayTypesParser getParser() {
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
    return TemporalsWithMCArrayTypesMill.traverser();
  }

  @Override
  protected String prettyPrint(ASTNode ast) {
    return TemporalsWithMCArrayTypesMill.prettyPrint(ast, false);
  }
}
