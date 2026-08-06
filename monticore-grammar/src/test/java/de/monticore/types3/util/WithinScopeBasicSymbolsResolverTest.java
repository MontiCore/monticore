/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types3.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.types.check.SymTypeExpression;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class WithinScopeBasicSymbolsResolverTest {

  @AfterEach
  void resetResolver() {
    WithinScopeBasicSymbolsResolver.reset();
  }

  @Test
  void delegatesWhetherResultsAreOptional() {
    OptionalResultResolver resolver = new OptionalResultResolver();
    WithinScopeBasicSymbolsResolver.setDelegate(resolver);
    IBasicSymbolsScope scope = BasicSymbolsMill.scope();

    WithinScopeBasicSymbolsResolver.resolveNameAsExpr(scope, "mandatory");
    assertFalse(resolver.resultsAreOptional);

    WithinScopeBasicSymbolsResolver.resolveNameAsExpr(scope, "optional", true);
    assertTrue(resolver.resultsAreOptional);
  }

  protected static class OptionalResultResolver extends WithinScopeBasicSymbolsResolver {

    protected boolean resultsAreOptional;

    @Override
    protected Optional<SymTypeExpression> _resolveNameAsExpr(
        IBasicSymbolsScope enclosingScope,
        String name,
        boolean resultsAreOptional) {
      this.resultsAreOptional = resultsAreOptional;
      return Optional.empty();
    }
  }
}
