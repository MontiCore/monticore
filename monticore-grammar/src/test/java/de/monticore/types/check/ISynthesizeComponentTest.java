/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.expressions.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import de.monticore.expressions.combineexpressionswithliterals._parser.CombineExpressionsWithLiteralsParser;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.symbols.compsymbols.CompSymbolsMill;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
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

    CombineExpressionsWithLiteralsMill.reset();
    CombineExpressionsWithLiteralsMill.init();

    BasicSymbolsMill.initializePrimitives();
    MCBasicTypesMill.init();

    parser = CombineExpressionsWithLiteralsMill.parser();

    CompSymbolsMill.reset();
    CompSymbolsMill.init();
  }

    @Test
    public void synthesize_resolvesComponent_whenParentHasSubcomponentOfThatType() throws Exception {
      ComponentTypeSymbol typeA = CompSymbolsMill.componentTypeSymbolBuilder()
        .setName("A")
        .setSpannedScope(CompSymbolsMill.scope())
        .build();
      CompSymbolsMill.globalScope().add(typeA);
      typeA.setEnclosingScope(CompSymbolsMill.globalScope());

      ASTMCType astB = parser.parse_StringMCType("A").orElseThrow();

      FullSynthesizeCompKindFromMCBasicTypes synth = new FullSynthesizeCompKindFromMCBasicTypes();

      Log.clearFindings();
      Optional<CompKindExpression> res = synth.synthesize(astB);
      List<Finding> findings = Log.getFindings();
      boolean hasD0104 = findings.stream().anyMatch(f -> f.getMsg() != null && f.getMsg().contains("0xD0104"));
      Assertions.assertTrue(res.isPresent());
      Assertions.assertFalse(hasD0104, "Did not expect central error 0xD0104");

  }

  @Test
  public void synthesize_logsCentralError_whenNoComponentFound_forPrimitive() throws Exception {
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
