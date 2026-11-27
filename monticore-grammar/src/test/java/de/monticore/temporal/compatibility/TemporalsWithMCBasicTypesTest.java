/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal.compatibility;

import de.monticore.ast.ASTNode;
import de.monticore.runtime.junit.PrettyPrinterTester.ParseFunction;
import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.temporal.AbstractTemporalCompatibilityTest;
import de.monticore.temporal.compatibility.temporalswithmcbasictypes.TemporalsWithMCBasicTypesMill;
import de.monticore.temporal.compatibility.temporalswithmcbasictypes._parser.TemporalsWithMCBasicTypesParser;
import de.monticore.temporal.isotemporals._visitor.ISOTemporalsTraverser;
import org.junit.jupiter.api.BeforeEach;

import java.util.Optional;

@TestWithMCLanguage(TemporalsWithMCBasicTypesMill.class)
public class TemporalsWithMCBasicTypesTest extends AbstractTemporalCompatibilityTest {

  protected TemporalsWithMCBasicTypesParser parser;

  @BeforeEach
  public void setUpParser() {
    parser = TemporalsWithMCBasicTypesMill.parser();
  }

  @Override
  protected TemporalsWithMCBasicTypesParser getParser() {
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
    return TemporalsWithMCBasicTypesMill.traverser();
  }

  @Override
  protected String prettyPrint(ASTNode ast) {
    return TemporalsWithMCBasicTypesMill.prettyPrint(ast, false);
  }
}
