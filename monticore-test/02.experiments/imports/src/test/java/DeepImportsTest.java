/* (c) https://github.com/MontiCore/monticore */

import cdlight.CdLightMill;
import cdlight._ast.ASTCdCompilationUnit;
import cdlight._parser.CdLightParser;
import cdlight._symboltable.*;
import de.monticore.io.paths.MCPath;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.symboltable.ImportStatement;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DeepImportsTest {

  @BeforeEach
  public void setup() {
    Log.init();
    CdLightMill.init();
    CdLightMill.globalScope().clear();
  }

  @AfterEach
  public void tearDown() {
    CdLightMill.reset();
  }

  @Test
  public void testDefault() throws IOException {
    String model = "src/test/resources/cdlight/Simple.cd";

    ASTCdCompilationUnit cd = parse(model);
    ICdLightArtifactScope as = createSymTab(cd);
    ICdLightGlobalScope gs = CdLightMill.globalScope();

    Assertions.assertTrue(as.getImports(0).getStatement().equals("my.test"));

    Assertions.assertTrue(gs.resolveOOType("a.b.X").isPresent());
    Assertions.assertTrue(gs.resolveOOType("a.b.Y").isPresent());
    Assertions.assertTrue(gs.resolveField("a.b.X.z").isPresent());
    Assertions.assertTrue(gs.resolveMethod("a.b.Y.bar").isPresent());

    Assertions.assertEquals(1, gs.resolveOOTypeMany("a.b.X").size());
    Assertions.assertEquals(1, gs.resolveOOTypeMany("a.b.Y").size());
    Assertions.assertEquals(1, gs.resolveFieldMany("a.b.X.z").size());
    Assertions.assertEquals(1, gs.resolveMethodMany("a.b.Y.bar").size());
  }

  @Test
  public void testDeepResolve() throws IOException {
    String model = "src/test/resources/cdlight/Simple.cd";

    ASTCdCompilationUnit cd = parse(model);
    ICdLightArtifactScope as = createSymTabWithPkg(cd);
    ICdLightGlobalScope gs = CdLightMill.globalScope();

    Assertions.assertEquals("cdlight", as.getPackageName());
    Assertions.assertTrue(as.getImports(0).getStatement().equals("my.test"));

    Assertions.assertTrue(gs.resolveOOType("cdlight.Simple.a.b.X").isPresent());
    Assertions.assertTrue(gs.resolveOOType("cdlight.Simple.a.b.Y").isPresent());
    Assertions.assertTrue(gs.resolveField("cdlight.Simple.a.b.X.z").isPresent());
    Assertions.assertTrue(gs.resolveMethod("cdlight.Simple.a.b.Y.bar").isPresent());
  }

  @Test
  public void testDeepResolveWithoutArtifactName() throws IOException {
    String model = "src/test/resources/cdlight/Simple.cd";

    ASTCdCompilationUnit cd = parse(model);
    ICdLightArtifactScope as = createSymTabWithPkg(cd);
    ICdLightGlobalScope gs = CdLightMill.globalScope();

    Assertions.assertEquals("cdlight", as.getPackageName());
    Assertions.assertTrue(as.getImports(0).getStatement().equals("my.test"));

    Assertions.assertTrue(gs.resolveOOType("cdlight.a.b.X").isPresent());
    Assertions.assertTrue(gs.resolveOOType("cdlight.a.b.Y").isPresent());
    Assertions.assertTrue(gs.resolveField("cdlight.a.b.X.z").isPresent());
    Assertions.assertTrue(gs.resolveMethod("cdlight.a.b.Y.bar").isPresent());
  }

  @Test
  public void testQualified() throws IOException {
    String model = "src/test/resources/cdlight/QualifiedReferences.cd";

    ASTCdCompilationUnit cd = parse(model);
    ICdLightArtifactScope as = createSymTabWithPkg(cd);
    ICdLightGlobalScope gs = CdLightMill.globalScope();
    MCPath symPath = new MCPath();
    symPath.addEntry(new File("src/test/resources/symbols/").toPath());
    gs.setSymbolPath(symPath);

    Assertions.assertEquals("cdlight", as.getPackageName());
    Assertions.assertTrue(as.getImports(0).getStatement().equals("cdlight.Simple.a.b"));

    Assertions.assertFalse(as.resolveOOType("a.b.X").isPresent());
    Assertions.assertFalse(as.resolveOOType("a.b.Y").isPresent());

    Assertions.assertTrue(as.resolveOOType("X").isPresent());
    Assertions.assertTrue(as.resolveOOType("Y").isPresent());

    Assertions.assertTrue(as.resolveOOType("cdlight.Simple.a.b.X").isPresent());
    Assertions.assertTrue(as.resolveOOType("cdlight.Simple.a.b.Y").isPresent());
  }

  @Test
  public void testUnqualified() throws IOException {
    String model = "src/test/resources/cdlight/UnqualifiedReferences.cd";

    ASTCdCompilationUnit cd = parse(model);
    ICdLightArtifactScope as = createSymTabWithPkg(cd);
    ICdLightGlobalScope gs = CdLightMill.globalScope();
    MCPath symPath = new MCPath();
    symPath.addEntry(new File("src/test/resources/symbols/").toPath());
    gs.setSymbolPath(symPath);

    Assertions.assertEquals("cdlight", as.getPackageName());
    Assertions.assertTrue(as.getImports(0).getStatement().equals("cdlight.Simple.a.b"));

    Assertions.assertTrue(as.resolveOOType("X").isPresent());
    Assertions.assertTrue(as.resolveOOType("Y").isPresent());
    Assertions.assertFalse(as.resolveOOType("a.b.X").isPresent());
    Assertions.assertFalse(as.resolveOOType("a.b.Y").isPresent());

    Assertions.assertEquals(0, gs.resolveOOTypeMany("X").size());
    Assertions.assertEquals(0, gs.resolveOOTypeMany("Y").size());
    Assertions.assertEquals(0, gs.resolveOOTypeMany("a.b.X").size());
    Assertions.assertEquals(0, gs.resolveOOTypeMany("a.b.Y").size());

    Assertions.assertEquals(1, gs.resolveOOTypeMany("cdlight.Simple.a.b.X").size());
    Assertions.assertEquals(1, gs.resolveOOTypeMany("cdlight.Simple.a.b.Y").size());
  }

  @Test
  public void testNoPkgResolve() throws IOException {
    String model = "src/test/resources/cdlight/QualifiedReferencesNoPkg.cd";

    ASTCdCompilationUnit cd = parse(model);
    ICdLightArtifactScope as = createSymTab(cd);
    ICdLightGlobalScope gs = CdLightMill.globalScope();
    MCPath symPath = new MCPath();
    symPath.addEntry(new File("src/test/resources/symbols/").toPath());
    gs.setSymbolPath(symPath);

    Assertions.assertTrue(as.getImports(0).getStatement().equals("SimpleNoPkg"));

    Assertions.assertTrue(gs.resolveOOType("SimpleNoPkg.a.b.X").isPresent());
    Assertions.assertTrue(gs.resolveOOType("SimpleNoPkg.a.b.Y").isPresent());

    Assertions.assertTrue(gs.resolveOOType("a.b.X").isPresent());
    Assertions.assertTrue(gs.resolveOOType("a.b.Y").isPresent());
  }

  @Test
  public void testUnqualifiedReferencesResolvePkg() throws IOException {
    String model = "src/test/resources/cdlight/UnqualifiedReferences.cd";

    ASTCdCompilationUnit cd = parse(model);
    ICdLightArtifactScope as = createSymTab(cd);
    ICdLightGlobalScope gs = CdLightMill.globalScope();
    MCPath symPath = new MCPath();
    symPath.addEntry(new File("src/test/resources/symbols/").toPath());
    gs.setSymbolPath(symPath);

    Assertions.assertTrue(as.getImports(0).getStatement().equals("cdlight.Simple.a.b"));

    Assertions.assertTrue(as.resolveOOType("X").isPresent());
    Assertions.assertTrue(as.resolveOOType("Y").isPresent());

    Assertions.assertTrue(gs.resolveOOType("cdlight.Simple.a.b.X").isPresent());
    Assertions.assertTrue(gs.resolveOOType("cdlight.Simple.a.b.Y").isPresent());

    Assertions.assertTrue(gs.resolveOOType("SimpleNoPkg.a.b.X").isPresent());
    Assertions.assertTrue(gs.resolveOOType("SimpleNoPkg.a.b.Y").isPresent());
  }


  @Test
  public void testSimpleNoPkgNoStar() throws IOException {
    String model = "src/test/resources/cdlight/Simple.cd";

    ASTCdCompilationUnit cd = parse(model);
    ICdLightArtifactScope as = createSymTab(cd);
    ICdLightGlobalScope gs = CdLightMill.globalScope();

    // assume import to be cdlight.Simple.a.b.X
    Assertions.assertFalse(gs.resolveOOType("X").isPresent());
    Assertions.assertFalse(gs.resolveOOType("cdlight.Simple.a.b.X").isPresent());
    Assertions.assertFalse(gs.resolveOOType("Y").isPresent());

    // assume import to be Simple.a.b.X
    Assertions.assertFalse(gs.resolveOOType("X").isPresent());
    Assertions.assertTrue(gs.resolveOOType("Simple.a.b.X").isPresent());
    Assertions.assertFalse(gs.resolveOOType("Y").isPresent());

    // the following only work, if the symbol table is already loaded

    // assume import to be a.b.X
    Assertions.assertFalse(gs.resolveOOType("X").isPresent());
    Assertions.assertTrue(gs.resolveOOType("a.b.X").isPresent());
    Assertions.assertFalse(gs.resolveOOType("Y").isPresent());
  }


  @Test
  public void testSimpleNoPkgStar() throws IOException {
    String model = "src/test/resources/cdlight/Simple.cd";

    ASTCdCompilationUnit cd = parse(model);
    ICdLightArtifactScope as = createSymTab(cd);
    ICdLightGlobalScope gs = CdLightMill.globalScope();

    // assume import to be cdlight.Simple.a.b.*
    Assertions.assertFalse(gs.resolveOOType("X").isPresent());
    Assertions.assertFalse(gs.resolveOOType("cdlight.Simple.a.b.X").isPresent());
    Assertions.assertFalse(gs.resolveOOType("Y").isPresent());
    Assertions.assertFalse(gs.resolveOOType("cdlight.Simple.a.b.Y").isPresent());

    // assume import to be Simple.a.b.X.*
    Assertions.assertFalse(gs.resolveOOType("X").isPresent());
    Assertions.assertTrue(gs.resolveOOType("Simple.a.b.X").isPresent());
    Assertions.assertFalse(gs.resolveOOType("Y").isPresent());
    Assertions.assertTrue(gs.resolveOOType("Simple.a.b.Y").isPresent());

    // the following only work, if the symbol table is already loaded

    // assume import to be a.b.*
    Assertions.assertFalse(gs.resolveOOType("X").isPresent());
    Assertions.assertTrue(gs.resolveOOType("a.b.X").isPresent());
    Assertions.assertFalse(gs.resolveOOType("Y").isPresent());
    Assertions.assertTrue(gs.resolveOOType("a.b.Y").isPresent());
  }

  @Test
  public void testSimplePkgNoStar() throws IOException {
    String model = "src/test/resources/cdlight/Simple.cd";

    ASTCdCompilationUnit cd = parse(model);
    ICdLightArtifactScope as = createSymTabWithPkg(cd);
    ICdLightGlobalScope gs = CdLightMill.globalScope();

    // assume import to be cdlight.Simple.a.b.X
    Assertions.assertFalse(gs.resolveOOType("X").isPresent());
    Assertions.assertTrue(gs.resolveOOType("cdlight.Simple.a.b.X").isPresent());
    Assertions.assertFalse(gs.resolveOOType("Y").isPresent());

    // assume import to be Simple.a.b.X
    Assertions.assertFalse(gs.resolveOOType("X").isPresent());
    Assertions.assertFalse(gs.resolveOOType("Simple.a.b.X").isPresent());
    Assertions.assertFalse(gs.resolveOOType("Y").isPresent());

    // the following only work, if the symbol table is already loaded

    // assume import to be a.b.X
    Assertions.assertFalse(gs.resolveOOType("X").isPresent());
    Assertions.assertFalse(gs.resolveOOType("a.b.X").isPresent());
    Assertions.assertFalse(gs.resolveOOType("Y").isPresent());

    // assume import to be cdlight.a.b.X
    Assertions.assertTrue(gs.resolveOOType("cdlight.a.b.X").isPresent());

  }

  @Test
  public void testSimplePkgStar() throws IOException {
    String model = "src/test/resources/cdlight/Simple.cd";

    ASTCdCompilationUnit cd = parse(model);
    ICdLightArtifactScope as = createSymTabWithPkg(cd);
    ICdLightGlobalScope gs = CdLightMill.globalScope();

    // assume import to be cdlight.Simple.a.b.*
    Assertions.assertFalse(gs.resolveOOType("X").isPresent());
    Assertions.assertTrue(gs.resolveOOType("cdlight.Simple.a.b.X").isPresent());
    Assertions.assertFalse(gs.resolveOOType("Y").isPresent());
    Assertions.assertTrue(gs.resolveOOType("cdlight.Simple.a.b.Y").isPresent());

    // assume import to be Simple.a.b.X.*
    Assertions.assertFalse(gs.resolveOOType("X").isPresent());
    Assertions.assertFalse(gs.resolveOOType("Simple.a.b.X").isPresent());
    Assertions.assertFalse(gs.resolveOOType("Y").isPresent());
    Assertions.assertFalse(gs.resolveOOType("Simple.a.b.Y").isPresent());

    // the following only work, if the symbol table is already loaded

    // assume import to be a.b.*
    Assertions.assertFalse(gs.resolveOOType("X").isPresent());
    Assertions.assertFalse(gs.resolveOOType("a.b.X").isPresent());
    Assertions.assertFalse(gs.resolveOOType("Y").isPresent());
    Assertions.assertFalse(gs.resolveOOType("a.b.Y").isPresent());

    // assume import to be cdlight.a.b.*
    Assertions.assertFalse(gs.resolveOOType("X").isPresent());
    Assertions.assertTrue(gs.resolveOOType("cdlight.a.b.X").isPresent());
    Assertions.assertFalse(gs.resolveOOType("Y").isPresent());
    Assertions.assertTrue(gs.resolveOOType("cdlight.a.b.Y").isPresent());
  }

  @Test
  public void testSimplePkgStarWithSpanningSymbol() throws IOException {
    String model = "src/test/resources/cdlight/Simple.cd";

    ASTCdCompilationUnit cd = parse(model);
    ICdLightArtifactScope as = createSymTabWithPkg(cd);
    OOTypeSymbol asSym = CdLightMill.oOTypeSymbolBuilder()
            .setName("Simple")
            .build();
    asSym.setSpannedScope(as);
    ICdLightGlobalScope gs = CdLightMill.globalScope();

    // assume import to be cdlight.Simple.a.b.*
    Assertions.assertFalse(gs.resolveOOType("X").isPresent());
    Assertions.assertTrue(gs.resolveOOType("cdlight.Simple.a.b.X").isPresent());
    Assertions.assertFalse(gs.resolveOOType("Y").isPresent());
    Assertions.assertTrue(gs.resolveOOType("cdlight.Simple.a.b.Y").isPresent());

    // assume import to be Simple.a.b.X.*
    Assertions.assertFalse(gs.resolveOOType("X").isPresent());
    Assertions.assertFalse(gs.resolveOOType("Simple.a.b.X").isPresent());
    Assertions.assertFalse(gs.resolveOOType("Y").isPresent());
    Assertions.assertFalse(gs.resolveOOType("Simple.a.b.Y").isPresent());

    // the following only work, if the symbol table is already loaded

    // assume import to be a.b.*
    Assertions.assertFalse(gs.resolveOOType("X").isPresent());
    Assertions.assertFalse(gs.resolveOOType("a.b.X").isPresent());
    Assertions.assertFalse(gs.resolveOOType("Y").isPresent());
    Assertions.assertFalse(gs.resolveOOType("a.b.Y").isPresent());

    // assume import to be cdlight.a.b.*
    Assertions.assertFalse(gs.resolveOOType("X").isPresent());
    Assertions.assertTrue(gs.resolveOOType("cdlight.a.b.X").isPresent());
    Assertions.assertFalse(gs.resolveOOType("Y").isPresent());
    Assertions.assertTrue(gs.resolveOOType("cdlight.a.b.Y").isPresent());
  }

  public ASTCdCompilationUnit parse(String model) throws IOException {
    CdLightParser parser = CdLightMill.parser();
    Optional<ASTCdCompilationUnit> cd = parser.parse(model);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(cd.isPresent());
    return cd.get();
  }

  public ICdLightArtifactScope createSymTab(ASTCdCompilationUnit cd) {
    ICdLightArtifactScope as = CdLightMill.scopesGenitorDelegator().createFromAST(cd);
    as.setName(cd.getCdDefinition().getName());
    List<ImportStatement> imports = cd.getImportsList().stream()
            .map(i -> new ImportStatement(i.getQName(), i.isStar()))
            .collect(Collectors.toList());
    as.setImportsList(imports);
    return as;
  }

  public ICdLightArtifactScope createSymTabWithPkg(ASTCdCompilationUnit cd) {
    ICdLightArtifactScope as = createSymTab(cd);
    as.setPackageName(cd.getMCPackageDeclaration().getMCQualifiedName().getQName());
    return as;
  }
}
