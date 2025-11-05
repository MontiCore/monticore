/* (c) https://github.com/MontiCore/monticore */
package mc.feature.scopes;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.feature.scopes.scopeinheritance.ScopeInheritanceMill;
import mc.feature.scopes.scopeinheritance._ast.ASTStartProd;
import mc.feature.scopes.scopeinheritance._parser.ScopeInheritanceParser;
import mc.feature.scopes.scopeinheritance._symboltable.IScopeInheritanceArtifactScope;
import mc.feature.scopes.scopeinheritance._symboltable.IScopeInheritanceGlobalScope;
import mc.feature.scopes.scopeinheritance._symboltable.IScopeInheritanceScope;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;
import com.google.common.base.Preconditions;

/**
 * test that the attributes of scope in the grammar like shadowing, exporting, ordering are inherited correctly
 * NOTE: there can be no conflicts as default values can not be set explicitly.
 * only the attributes shadowing non_exporting and ordered are inherited
 */
public class ScopeInheritanceTest {

    private ASTStartProd startProd;
    private IScopeInheritanceArtifactScope scope;

    @BeforeEach
    public void before() {
        LogStub.init();
        Log.enableFailQuick(false);
        ScopeInheritanceMill.reset();
        ScopeInheritanceMill.init();
    }

    @BeforeEach
    public void Setup() throws IOException {
        ScopeInheritanceParser scopeInheritanceParser = ScopeInheritanceMill.parser();
        Optional<ASTStartProd> astInheritance = scopeInheritanceParser.parse("src/test/resources/mc/feature/scopes/ScopeInheritanceModel.st");
        Assertions.assertFalse(scopeInheritanceParser.hasErrors());
        Assertions.assertTrue(astInheritance.isPresent());
        startProd = astInheritance.get();

        IScopeInheritanceGlobalScope globalScope = ScopeInheritanceMill.globalScope();
        globalScope.setFileExt("st");
        globalScope.getSymbolPath().addEntry(Paths.get("src/test/resources/mc/feature/scopes"));
        scope = ScopeInheritanceMill.scopesGenitorDelegator().createFromAST(astInheritance.get());
    }

    /**
     * test all combinations of inherited scope attributes
     */
    @Test
    public void testScopeInheritanceA() {
        //TODO
        // I don't know why 2 elements are in the list.
        // The IA extends A but that should not lead to two elements in the A list and 0 in the IA list
        Assertions.assertEquals(2, startProd.getAList().size());
        IScopeInheritanceScope scopeShadowingNonExportingOrdered = startProd.getAList().get(0).getSpannedScope();
        Preconditions.checkNotNull(scopeShadowingNonExportingOrdered);
        Assertions.assertFalse(scopeShadowingNonExportingOrdered.isExportingSymbols());
        Assertions.assertTrue(scopeShadowingNonExportingOrdered.isShadowing());
        Assertions.assertTrue(scopeShadowingNonExportingOrdered.isOrdered());
        Assertions.assertTrue(Log.getFindings().isEmpty());
    }

    @Test
    public void testScopeInheritanceB() {
        Assertions.assertEquals(1, startProd.getBList().size());
        IScopeInheritanceScope scopeShadowingNonExporting = startProd.getBList().get(0).getSpannedScope();
        Preconditions.checkNotNull(scopeShadowingNonExporting);
        Assertions.assertFalse(scopeShadowingNonExporting.isExportingSymbols());
        Assertions.assertTrue(scopeShadowingNonExporting.isShadowing());
        Assertions.assertFalse(scopeShadowingNonExporting.isOrdered());
        Assertions.assertTrue(Log.getFindings().isEmpty());
    }

    @Test
    public void testScopeInheritanceC() {
        Assertions.assertEquals(1, startProd.getCList().size());
        IScopeInheritanceScope scopeShadowingOrdered = startProd.getCList().get(0).getSpannedScope();
        Preconditions.checkNotNull(scopeShadowingOrdered);
        Assertions.assertTrue(scopeShadowingOrdered.isExportingSymbols());
        Assertions.assertTrue(scopeShadowingOrdered.isShadowing());
        Assertions.assertTrue(scopeShadowingOrdered.isOrdered());
        Assertions.assertTrue(Log.getFindings().isEmpty());
    }

    @Test
    public void testScopeInheritanceD() {
        Assertions.assertEquals(1, startProd.getDList().size());
        IScopeInheritanceScope scopeNonExportingOrdered = startProd.getDList().get(0).getSpannedScope();
        Preconditions.checkNotNull(scopeNonExportingOrdered);
        Assertions.assertFalse(scopeNonExportingOrdered.isExportingSymbols());
        Assertions.assertFalse(scopeNonExportingOrdered.isShadowing());
        Assertions.assertTrue(scopeNonExportingOrdered.isOrdered());
        Assertions.assertTrue(Log.getFindings().isEmpty());
    }

    @Test
    public void testScopeInheritanceE() {
        Assertions.assertEquals(1, startProd.getEList().size());
        IScopeInheritanceScope scopeShadowing = startProd.getEList().get(0).getSpannedScope();
        Preconditions.checkNotNull(scopeShadowing);
        Assertions.assertTrue(scopeShadowing.isExportingSymbols());
        Assertions.assertTrue(scopeShadowing.isShadowing());
        Assertions.assertFalse(scopeShadowing.isOrdered());
        Assertions.assertTrue(Log.getFindings().isEmpty());
    }

    @Test
    public void testScopeInheritanceF() {
        Assertions.assertEquals(1, startProd.getFList().size());
        IScopeInheritanceScope scopeNonExporting = startProd.getFList().get(0).getSpannedScope();
        Preconditions.checkNotNull(scopeNonExporting);
        Assertions.assertFalse(scopeNonExporting.isExportingSymbols());
        Assertions.assertFalse(scopeNonExporting.isShadowing());
        Assertions.assertFalse(scopeNonExporting.isOrdered());
        Assertions.assertTrue(Log.getFindings().isEmpty());
    }

    @Test
    public void testScopeInheritanceG() {
        Assertions.assertEquals(1, startProd.getGList().size());
        IScopeInheritanceScope scopeOrdered = startProd.getGList().get(0).getSpannedScope();
        Preconditions.checkNotNull(scopeOrdered);
        Assertions.assertTrue(scopeOrdered.isExportingSymbols());
        Assertions.assertFalse(scopeOrdered.isShadowing());
        Assertions.assertTrue(scopeOrdered.isOrdered());
        Assertions.assertTrue(Log.getFindings().isEmpty());
    }

    @Test
    public void testScopeInheritanceH() {
        Assertions.assertEquals(1, startProd.getHList().size());
        IScopeInheritanceScope scopeDefault = startProd.getHList().get(0).getSpannedScope();
        Preconditions.checkNotNull(scopeDefault);
        Assertions.assertTrue(scopeDefault.isExportingSymbols());
        Assertions.assertFalse(scopeDefault.isShadowing());
        Assertions.assertFalse(scopeDefault.isOrdered());
        Assertions.assertTrue(Log.getFindings().isEmpty());
    }

    @Test
    public void testScopeInheritanceIA() {
        //TODO
        // I don't know why 2 elements are in the list.
        // The IA extends A but that should not lead to two elements in the A list and 0 in the IA list
        Assertions.assertEquals(2,startProd.getAList().size());
        Assertions.assertEquals(0, startProd.getIAList().size());
        IScopeInheritanceScope scopeShadowingNonExportingOrdered = startProd.getAList().get(1).getSpannedScope();
        Preconditions.checkNotNull(scopeShadowingNonExportingOrdered);
        Assertions.assertFalse(scopeShadowingNonExportingOrdered.isExportingSymbols());
        Assertions.assertTrue(scopeShadowingNonExportingOrdered.isShadowing());
        Assertions.assertTrue(scopeShadowingNonExportingOrdered.isOrdered());
        Assertions.assertTrue(Log.getFindings().isEmpty());
    }
}