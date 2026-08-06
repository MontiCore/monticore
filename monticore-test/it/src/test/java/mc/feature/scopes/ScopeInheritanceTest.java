/* (c) https://github.com/MontiCore/monticore */
package mc.feature.scopes;

import com.google.common.base.Preconditions;
import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.feature.scopes.scopeinheritance.ScopeInheritanceMill;
import mc.feature.scopes.scopeinheritance._ast.ASTStartProd;
import mc.feature.scopes.scopeinheritance._parser.ScopeInheritanceParser;
import mc.feature.scopes.scopeinheritance._symboltable.IScopeInheritanceArtifactScope;
import mc.feature.scopes.scopeinheritance._symboltable.IScopeInheritanceGlobalScope;
import mc.feature.scopes.scopeinheritance._symboltable.IScopeInheritanceScope;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * test that the attributes of scope in the grammar like shadowing, exporting, ordering are inherited correctly
 * NOTE: there can be no conflicts as default values can not be set explicitly.
 * only the attributes shadowing non_exporting and ordered are inherited
 */
@TestWithMCLanguage(ScopeInheritanceMill.class)
public class ScopeInheritanceTest {

    private ASTStartProd startProd;
    private IScopeInheritanceArtifactScope scope;


    @BeforeEach
    public void Setup() throws IOException {
        ScopeInheritanceParser scopeInheritanceParser = ScopeInheritanceMill.parser();
        Optional<ASTStartProd> astInheritance = scopeInheritanceParser.parse("src/test/resources/mc/feature/scopes/ScopeInheritanceModel.st");
        assertFalse(scopeInheritanceParser.hasErrors());
        assertTrue(astInheritance.isPresent());
        startProd = astInheritance.get();

        IScopeInheritanceGlobalScope globalScope = ScopeInheritanceMill.globalScope();
        scope = ScopeInheritanceMill.scopesGenitorDelegator().createFromAST(astInheritance.get());
    }

    /**
     * test all combinations of inherited scope attributes
     */
    @Test
    public void testScopeInheritanceA() {
        assertEquals(2, startProd.getAList().size());
        IScopeInheritanceScope scopeShadowingNonExportingOrdered = startProd.getAList().get(0).getSpannedScope();
        Preconditions.checkNotNull(scopeShadowingNonExportingOrdered);
        assertFalse(scopeShadowingNonExportingOrdered.isExportingSymbols());
        assertTrue(scopeShadowingNonExportingOrdered.isShadowing());
        assertTrue(scopeShadowingNonExportingOrdered.isOrdered());

        scopeShadowingNonExportingOrdered = startProd.getAList().get(1).getSpannedScope();
        Preconditions.checkNotNull(scopeShadowingNonExportingOrdered);
        assertFalse(scopeShadowingNonExportingOrdered.isExportingSymbols());
        assertTrue(scopeShadowingNonExportingOrdered.isShadowing());
        assertTrue(scopeShadowingNonExportingOrdered.isOrdered());
    }

    @Test
    public void testScopeInheritanceB() {
        assertEquals(1, startProd.getBList().size());
        IScopeInheritanceScope scopeShadowingNonExporting = startProd.getBList().get(0).getSpannedScope();
        Preconditions.checkNotNull(scopeShadowingNonExporting);
        assertFalse(scopeShadowingNonExporting.isExportingSymbols());
        assertTrue(scopeShadowingNonExporting.isShadowing());
        assertFalse(scopeShadowingNonExporting.isOrdered());
    }

    @Test
    public void testScopeInheritanceC() {
        assertEquals(1, startProd.getCList().size());
        IScopeInheritanceScope scopeShadowingOrdered = startProd.getCList().get(0).getSpannedScope();
        Preconditions.checkNotNull(scopeShadowingOrdered);
        assertTrue(scopeShadowingOrdered.isExportingSymbols());
        assertTrue(scopeShadowingOrdered.isShadowing());
        assertTrue(scopeShadowingOrdered.isOrdered());
    }

    @Test
    public void testScopeInheritanceD() {
        assertEquals(1, startProd.getDList().size());
        IScopeInheritanceScope scopeNonExportingOrdered = startProd.getDList().get(0).getSpannedScope();
        Preconditions.checkNotNull(scopeNonExportingOrdered);
        assertFalse(scopeNonExportingOrdered.isExportingSymbols());
        assertFalse(scopeNonExportingOrdered.isShadowing());
        assertTrue(scopeNonExportingOrdered.isOrdered());
    }

    @Test
    public void testScopeInheritanceE() {
        assertEquals(1, startProd.getEList().size());
        IScopeInheritanceScope scopeShadowing = startProd.getEList().get(0).getSpannedScope();
        Preconditions.checkNotNull(scopeShadowing);
        assertTrue(scopeShadowing.isExportingSymbols());
        assertTrue(scopeShadowing.isShadowing());
        assertFalse(scopeShadowing.isOrdered());
    }

    @Test
    public void testScopeInheritanceF() {
        assertEquals(1, startProd.getFList().size());
        IScopeInheritanceScope scopeNonExporting = startProd.getFList().get(0).getSpannedScope();
        Preconditions.checkNotNull(scopeNonExporting);
        assertFalse(scopeNonExporting.isExportingSymbols());
        assertFalse(scopeNonExporting.isShadowing());
        assertFalse(scopeNonExporting.isOrdered());
    }

    @Test
    public void testScopeInheritanceG() {
        assertEquals(1, startProd.getGList().size());
        IScopeInheritanceScope scopeOrdered = startProd.getGList().get(0).getSpannedScope();
        Preconditions.checkNotNull(scopeOrdered);
        assertTrue(scopeOrdered.isExportingSymbols());
        assertFalse(scopeOrdered.isShadowing());
        assertTrue(scopeOrdered.isOrdered());
    }

    @Test
    public void testScopeInheritanceH() {
        assertEquals(1, startProd.getHList().size());
        IScopeInheritanceScope scopeDefault = startProd.getHList().get(0).getSpannedScope();
        Preconditions.checkNotNull(scopeDefault);
        assertTrue(scopeDefault.isExportingSymbols());
        assertFalse(scopeDefault.isShadowing());
        assertFalse(scopeDefault.isOrdered());
    }
}