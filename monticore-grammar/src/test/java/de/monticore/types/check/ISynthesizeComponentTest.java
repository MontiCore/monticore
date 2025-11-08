/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.expressions.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import de.monticore.expressions.combineexpressionswithliterals._parser.CombineExpressionsWithLiteralsParser;
import de.monticore.types.mcbasictypes._visitor.MCBasicTypesTraverser;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.monticore.symbols.compsymbols.CompSymbolsMill;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import de.se_rwth.commons.logging.Finding;
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
  public void testSynthesizeLogsSingleD0104WhenResultAbsent() throws Exception {
    var astType = parser.parse_StringMCType("double").orElseThrow();

    ISynthesizeComponent synth = new ISynthesizeComponent() {
      private final MCBasicTypesTraverser traverser = MCBasicTypesMill.traverser();

      @Override public void init() { /* no-op */ }
      @Override public MCBasicTypesTraverser getTraverser() { return traverser; }
      @Override public Optional<CompKindExpression> getResult() { return Optional.empty(); }
    };

    Log.clearFindings();
    synth.synthesize(astType);

    List<Finding> findings = Log.getFindings();
    Assertions.assertFalse(findings.isEmpty(), "Expected at least one finding on failed synthesis");
    Assertions.assertEquals(1, findings.size(), "Expected exactly 1 finding");

    String msg = findings.get(0).getMsg();
    Assertions.assertNotNull(msg, "Finding message should not be null");
    Assertions.assertTrue(msg.contains("0xD0104"), "Expected finding to contain 0xD0104; actual: " + msg);
    Assertions.assertTrue(msg.contains("double"), "Expected message to mention 'double'");
  }

  @Test
  public void testSynthesizeDoesNotLogCentralErrorWhenResultPresent() throws Exception {
    var astType = parser.parse_StringMCType("A").orElseThrow();

    ComponentTypeSymbol compSym = CompSymbolsMill.componentTypeSymbolBuilder()
      .setName("A")
      .setSpannedScope(CompSymbolsMill.scope())
      .build();

    ISynthesizeComponent synth = new ISynthesizeComponent() {
      private final MCBasicTypesTraverser traverser = MCBasicTypesMill.traverser();
      private final CompKindExpression compKind = new CompKindOfComponentType(compSym);

      @Override public void init() { }
      @Override public MCBasicTypesTraverser getTraverser() { return traverser; }
      @Override public Optional<CompKindExpression> getResult() { return Optional.of(compKind); }
    };

    Log.clearFindings();
    synth.synthesize(astType);

    boolean hasCentral = Log.getFindings().stream()
      .anyMatch(f -> {
        String m = f.getMsg();
        return m != null && (m.contains("0xD0104"));
      });

    Assertions.assertFalse(hasCentral, "Did not expect central error 0xD0104 when synthesis returns a result");
  }
}
