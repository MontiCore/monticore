/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.expressions.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import de.monticore.expressions.combineexpressionswithliterals._parser.CombineExpressionsWithLiteralsParser;
import de.monticore.types.componentsymbolswithexpressionsandmcbasictypes.ComponentSymbolsWithExpressionsAndMCBasicTypesMill;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.util.List;
import java.util.Optional;

public class ISynthesizeComponentTest {

  private CombineExpressionsWithLiteralsParser parser;

  @BeforeAll
  public static void beforeAll() {
    LogStub.init();
    Log.enableFailQuick(false);
  }

  @BeforeEach
  public void setup() {
    Log.clearFindings();

    parser = CombineExpressionsWithLiteralsMill.parser();

    ComponentSymbolsWithExpressionsAndMCBasicTypesMill.reset();
    ComponentSymbolsWithExpressionsAndMCBasicTypesMill.init();
  }

    @Test
    public void synthesizesCompKind_forResolvableComponentTypeSymbol() throws Exception {
      ComponentTypeSymbol typeA = ComponentSymbolsWithExpressionsAndMCBasicTypesMill.componentTypeSymbolBuilder()
        .setName("A")
        .setSpannedScope(ComponentSymbolsWithExpressionsAndMCBasicTypesMill.scope())
        .build();
      ComponentSymbolsWithExpressionsAndMCBasicTypesMill.globalScope().add(typeA);
      typeA.setEnclosingScope(ComponentSymbolsWithExpressionsAndMCBasicTypesMill.globalScope());

      ASTMCType ast = parser.parse_StringMCType("A").orElseThrow();
      ast.setEnclosingScope(ComponentSymbolsWithExpressionsAndMCBasicTypesMill.globalScope());

      FullSynthesizeCompKindFromMCBasicTypes synth = new FullSynthesizeCompKindFromMCBasicTypes();

      Log.clearFindings();
      Optional<CompKindExpression> res = synth.synthesize(ast);
      List<Finding> findings = Log.getFindings();
      boolean hasD0104 = findings.stream().anyMatch(f -> f.getMsg() != null && f.getMsg().contains("0xD0104"));
      Assertions.assertTrue(res.isPresent());
      Assertions.assertFalse(hasD0104, "Did not expect central error 0xD0104");
  }

  @Test
  public void shouldLogCentralError_whenPrimitiveType() throws Exception {
    ASTMCType astDouble = parser.parse_StringMCType("double").orElseThrow();

    FullSynthesizeCompKindFromMCBasicTypes synth = new FullSynthesizeCompKindFromMCBasicTypes();

    Log.clearFindings();
    Optional<CompKindExpression> result = synth.synthesize(astDouble);

    Assertions.assertTrue(result.isEmpty(), "Expected no CompKindExpression for primitive 'double'");

    List<Finding> findings = Log.getFindings();
    Assertions.assertFalse(findings.isEmpty(), "Expected at least one finding when synthesis fails for primitive 'double'");

    boolean found = findings.stream().anyMatch(f -> {
      String m = f.getMsg();
      return m != null && (m.contains("0xD0104"));
    });
    Assertions.assertTrue(found, "Expected a central finding containing 0xD0104");
  }
}
