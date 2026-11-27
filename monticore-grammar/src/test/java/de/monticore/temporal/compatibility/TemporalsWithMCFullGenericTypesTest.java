/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal.compatibility;

import de.monticore.ast.ASTNode;
import de.monticore.runtime.junit.PrettyPrinterTester.ParseFunction;
import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.temporal.AbstractTemporalCompatibilityTest;
import de.monticore.temporal.compatibility.temporalswithmcfullgenerictypes.TemporalsWithMCFullGenericTypesMill;
import de.monticore.temporal.compatibility.temporalswithmcfullgenerictypes._parser.TemporalsWithMCFullGenericTypesParser;
import de.monticore.temporal.isotemporals._visitor.ISOTemporalsTraverser;
import org.junit.jupiter.api.BeforeEach;

import java.util.Optional;

@TestWithMCLanguage(TemporalsWithMCFullGenericTypesMill.class)
public class TemporalsWithMCFullGenericTypesTest extends AbstractTemporalCompatibilityTest {

  protected TemporalsWithMCFullGenericTypesParser parser;

  @BeforeEach
  public void setUpParser() {
    parser = TemporalsWithMCFullGenericTypesMill.parser();
  }

  @Override
  protected TemporalsWithMCFullGenericTypesParser getParser() {
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
    return TemporalsWithMCFullGenericTypesMill.traverser();
  }

  @Override
  protected String prettyPrint(ASTNode ast) {
    return TemporalsWithMCFullGenericTypesMill.prettyPrint(ast, false);
  }
}
