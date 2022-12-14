/* (c) https://github.com/MontiCore/monticore */
package mc.feature.scopes;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.feature.scopes.scopeattributes.ScopeAttributesMill;
import mc.feature.scopes.scopeattributes._ast.ASTStartProd;
import mc.feature.scopes.scopeattributes._parser.ScopeAttributesParser;
import mc.feature.scopes.scopeattributes._symboltable.IScopeAttributesArtifactScope;
import mc.feature.scopes.scopeattributes._symboltable.IScopeAttributesGlobalScope;
import mc.feature.scopes.scopeattributes._symboltable.IScopeAttributesScope;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * test that the attributes for scopes in the grammar are relevant
 * scope (shadowed, non_exported, ordered) -> should create a scope with that values
 */
public class ScopeAttributesTest {

  private IScopeAttributesArtifactScope scope;
  private ASTStartProd startProd;
  
  @Before
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Before
  public void setUp() throws IOException {
    ScopeAttributesParser scopeAttributesParser = new ScopeAttributesParser();
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
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testScopeNonExporting() {
    assertEquals(1, startProd.getBList().size());
    IScopeAttributesScope scopeShadowed = startProd.getBList().get(0).getSpannedScope();
    assertFalse(scopeShadowed.isShadowing());
    assertFalse(scopeShadowed.isOrdered());
    assertFalse(scopeShadowed.isExportingSymbols());
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testScopeOrdered() {
    assertEquals(1, startProd.getCList().size());
    IScopeAttributesScope scopeShadowed = startProd.getCList().get(0).getSpannedScope();
    assertFalse(scopeShadowed.isShadowing());
    assertTrue(scopeShadowed.isOrdered());
    assertTrue(scopeShadowed.isExportingSymbols());
    assertTrue(Log.getFindings().isEmpty());
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
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testScopeSpanningSymbolNonExporting() {
    assertEquals(1, startProd.getEList().size());
    IScopeAttributesScope scopeShadowed = startProd.getEList().get(0).getSpannedScope();
    assertFalse(scopeShadowed.isShadowing());
    assertFalse(scopeShadowed.isOrdered());
    assertFalse(scopeShadowed.isExportingSymbols());
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testScopeSpanningSymbolOrdered() {
    assertEquals(1, startProd.getFList().size());
    IScopeAttributesScope scopeShadowed = startProd.getFList().get(0).getSpannedScope();
    assertFalse(scopeShadowed.isShadowing());
    assertTrue(scopeShadowed.isOrdered());
    assertTrue(scopeShadowed.isExportingSymbols());
    assertTrue(Log.getFindings().isEmpty());
  }

}
