/* (c) https://github.com/MontiCore/monticore */
package mc.feature.scopes;

import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.feature.scopes.scopeattributes.ScopeAttributesMill;
import mc.feature.scopes.scopeattributes._ast.ASTStartProd;
import mc.feature.scopes.scopeattributes._parser.ScopeAttributesParser;
import mc.feature.scopes.scopeattributes._symboltable.IScopeAttributesArtifactScope;
import mc.feature.scopes.scopeattributes._symboltable.IScopeAttributesGlobalScope;
import mc.feature.scopes.scopeattributes._symboltable.IScopeAttributesScope;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * test that the attributes for scopes in the grammar are relevant
 * scope (shadowed, non_exported, ordered) -> should create a scope with that values
 */
@TestWithMCLanguage(ScopeAttributesMill.class)
public class ScopeAttributesTest {

  private IScopeAttributesArtifactScope scope;
  private ASTStartProd startProd;

  @BeforeEach
  public void setUp() throws IOException {
    ScopeAttributesParser scopeAttributesParser = ScopeAttributesMill.parser();
    Optional<ASTStartProd> astSup = scopeAttributesParser.parse("src/test/resources/mc/feature/scopes/ScopeAttributesModel.sc");
    assertFalse(scopeAttributesParser.hasErrors());
    assertTrue(astSup.isPresent());
    startProd = astSup.get();

    ScopeAttributesMill.reset();
    ScopeAttributesMill.init();

    IScopeAttributesGlobalScope globalScope = ScopeAttributesMill.globalScope();
    globalScope.setFileExt("sc");
    globalScope.getSymbolPath().addEntry(Paths.get("src/test/resources/mc/feature/scopes"));
    scope = ScopeAttributesMill.scopesGenitorDelegator().createFromAST(astSup.get());
  }

  /**
   * simple scopes
   */
  @Test
  public void testScopeShadowing() {
    assertEquals(1, startProd.getAList().size());
    IScopeAttributesScope scopeShadowed = startProd.getAList().get(0).getSpannedScope();
    assertTrue(scopeShadowed.isShadowing());
    assertFalse(scopeShadowed.isOrdered());
    assertTrue(scopeShadowed.isExportingSymbols());
  }

  @Test
  public void testScopeNonExporting() {
    assertEquals(1, startProd.getBList().size());
    IScopeAttributesScope scopeShadowed = startProd.getBList().get(0).getSpannedScope();
    assertFalse(scopeShadowed.isShadowing());
    assertFalse(scopeShadowed.isOrdered());
    assertFalse(scopeShadowed.isExportingSymbols());
  }

  @Test
  public void testScopeOrdered() {
    assertEquals(1, startProd.getCList().size());
    IScopeAttributesScope scopeShadowed = startProd.getCList().get(0).getSpannedScope();
    assertFalse(scopeShadowed.isShadowing());
    assertTrue(scopeShadowed.isOrdered());
    assertTrue(scopeShadowed.isExportingSymbols());
  }

  /**
   * scope spanning symbols
   */
  @Test
  public void testScopeSpanningSymbolShadowing() {
    assertEquals(1, startProd.getDList().size());
    IScopeAttributesScope scopeShadowed = startProd.getDList().get(0).getSpannedScope();
    assertTrue(scopeShadowed.isShadowing());
    assertFalse(scopeShadowed.isOrdered());
    assertTrue(scopeShadowed.isExportingSymbols());
  }

  @Test
  public void testScopeSpanningSymbolNonExporting() {
    assertEquals(1, startProd.getEList().size());
    IScopeAttributesScope scopeShadowed = startProd.getEList().get(0).getSpannedScope();
    assertFalse(scopeShadowed.isShadowing());
    assertFalse(scopeShadowed.isOrdered());
    assertFalse(scopeShadowed.isExportingSymbols());
  }

  @Test
  public void testScopeSpanningSymbolOrdered() {
    assertEquals(1, startProd.getFList().size());
    IScopeAttributesScope scopeShadowed = startProd.getFList().get(0).getSpannedScope();
    assertFalse(scopeShadowed.isShadowing());
    assertTrue(scopeShadowed.isOrdered());
    assertTrue(scopeShadowed.isExportingSymbols());
  }

}
