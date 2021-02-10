/* (c) https://github.com/MontiCore/monticore */
package mc.feature.scopes;

import de.monticore.io.paths.ModelPath;
import de.se_rwth.commons.logging.*;
import mc.feature.referencesymbol.reference.ReferenceMill;
import mc.feature.referencesymbol.reference._symboltable.IReferenceGlobalScope;
import mc.feature.referencesymbol.supgrammarref.SupGrammarRefMill;
import mc.feature.scopes.scopeattributes.ScopeAttributesMill;
import mc.feature.scopes.scopeattributes._ast.ASTStartProd;
import mc.feature.scopes.scopeattributes._parser.ScopeAttributesParser;
import mc.feature.scopes.scopeattributes._symboltable.*;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * test that the attributes for scopes in the grammar are relevant
 * scope (shadowed, non_exported, ordered) -> should create a scope with that values
 */
public class ScopeAttributesTest {

  private IScopeAttributesArtifactScope scope;

  @Before
  public void setUp() throws IOException {
    LogStub.init();         // replace log by a sideffect free variant
        // LogStub.initPlusLog();  // for manual testing purpose only
    Log.enableFailQuick(false);
    ScopeAttributesParser scopeAttributesParser = new ScopeAttributesParser();
    Optional<ASTStartProd> astSup = scopeAttributesParser.parse("src/test/resources/mc/feature/scopes/ScopeAttributesModel.sc");
    assertFalse(scopeAttributesParser.hasErrors());
    assertTrue(astSup.isPresent());

    IScopeAttributesGlobalScope globalScope = ScopeAttributesMill.globalScope();
    globalScope.setFileExt("sc");
    globalScope.getModelPath().addEntry(Paths.get("src/test/resources/mc/feature/scopes"));
    scope = ScopeAttributesMill.scopeAttributesSymbolTableCreatorDelegator().createFromAST(astSup.get());
  }

  /**
   * simple scopes
   */
  @Test
  public void testScopeShadowing() {
    IScopeAttributesScope scopeShadowed = getSubScopeByName("ScopeShadowed");
    assertTrue(scopeShadowed.isShadowing());
    assertFalse(scopeShadowed.isOrdered());
    assertTrue(scopeShadowed.isExportingSymbols());
  }

  @Test
  public void testScopeNonExporting() {
    IScopeAttributesScope scopeShadowed = getSubScopeByName("ScopeNonExporting");
    assertFalse(scopeShadowed.isShadowing());
    assertFalse(scopeShadowed.isOrdered());
    assertFalse(scopeShadowed.isExportingSymbols());
  }

  @Test
  public void testScopeOrdered() {
    IScopeAttributesScope scopeShadowed = getSubScopeByName("ScopeOrdered");
    assertFalse(scopeShadowed.isShadowing());
    assertTrue(scopeShadowed.isOrdered());
    assertTrue(scopeShadowed.isExportingSymbols());
  }

  /**
   * scope spanning symbols
   */
  @Test
  public void testScopeSpanningSymbolShadowing() {
    IScopeAttributesScope scopeShadowed = getSubScopeByName("SymbolScopeShadowed");
    assertTrue(scopeShadowed.isShadowing());
    assertFalse(scopeShadowed.isOrdered());
    assertTrue(scopeShadowed.isExportingSymbols());
  }

  @Test
  public void testScopeSpanningSymbolNonExporting() {
    IScopeAttributesScope scopeShadowed = getSubScopeByName("SymbolScopeNonExporting");
    assertFalse(scopeShadowed.isShadowing());
    assertFalse(scopeShadowed.isOrdered());
    assertFalse(scopeShadowed.isExportingSymbols());
  }

  @Test
  public void testScopeSpanningSymbolOrdered() {
    IScopeAttributesScope scopeShadowed = getSubScopeByName("SymbolScopeOrdered");
    assertFalse(scopeShadowed.isShadowing());
    assertTrue(scopeShadowed.isOrdered());
    assertTrue(scopeShadowed.isExportingSymbols());
  }


  private IScopeAttributesScope getSubScopeByName(String name) {
    Optional<? extends IScopeAttributesScope> subScope = scope.getSubScopes()
        .stream()
        .filter(x -> x.getName().equals(name))
        .findFirst();
    assertTrue(subScope.isPresent());
    return subScope.get();
  }

}
