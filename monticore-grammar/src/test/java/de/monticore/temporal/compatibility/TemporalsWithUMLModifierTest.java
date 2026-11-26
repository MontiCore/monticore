/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal.compatibility;

import de.monticore.ast.ASTNode;
import de.monticore.runtime.junit.PrettyPrinterTester.ParseFunction;
import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.temporal.AbstractTemporalCompatibilityTest;
import de.monticore.temporal.compatibility.temporalswithumlmodifier.TemporalsWithUMLModifierMill;
import de.monticore.temporal.compatibility.temporalswithumlmodifier._parser.TemporalsWithUMLModifierParser;
import de.monticore.temporal.isotemporals._visitor.ISOTemporalsTraverser;
import org.junit.jupiter.api.BeforeEach;

import java.util.Optional;

@TestWithMCLanguage(TemporalsWithUMLModifierMill.class)
public class TemporalsWithUMLModifierTest extends AbstractTemporalCompatibilityTest {

  protected TemporalsWithUMLModifierParser parser;

  @BeforeEach
  public void setUpParser() {
    parser = TemporalsWithUMLModifierMill.parser();
  }

  @Override
  protected TemporalsWithUMLModifierParser getParser() {
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
    return TemporalsWithUMLModifierMill.traverser();
  }

  @Override
  protected String prettyPrint(ASTNode ast) {
    return TemporalsWithUMLModifierMill.prettyPrint(ast, false);
  }
}
