/* (c) https://github.com/MontiCore/monticore */

import cdlight.CdLightMill;
import cdlight._ast.ASTCdCompilationUnit;
import cdlight._parser.CdLightParser;
import cdlight._symboltable.*;
import de.monticore.io.paths.MCPath;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.symboltable.ImportStatement;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.se_rwth.commons.logging.Log;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class DeepImportsTest {

  @Before
  public void setup() {
    Log.init();
    CdLightMill.init();
    CdLightMill.globalScope().clear();
  }

  @After
  public void tearDown() {
    CdLightMill.reset();
  }

  @Test
  public void testDefault() throws IOException {
    String model = "src/test/resources/cdlight/Simple.cd";

    ASTCdCompilationUnit cd = parse(model);
    ICdLightArtifactScope as = createSymTab(cd);
    ICdLightGlobalScope gs = CdLightMill.globalScope();

    assertTrue(as.getImports(0).getStatement().equals("my.test"));

    assertTrue(gs.resolveOOType("a.b.X").isPresent());
    assertTrue(gs.resolveOOType("a.b.Y").isPresent());
    assertTrue(gs.resolveField("a.b.X.z").isPresent());
    assertTrue(gs.resolveMethod("a.b.Y.bar").isPresent());

    assertEquals(1, gs.resolveOOTypeMany("a.b.X").size());
    assertEquals(1, gs.resolveOOTypeMany("a.b.Y").size());
    assertEquals(1, gs.resolveFieldMany("a.b.X.z").size());
    assertEquals(1, gs.resolveMethodMany("a.b.Y.bar").size());
  }

  @Test
  public void testDeepResolve() throws IOException {
    String model = "src/test/resources/cdlight/Simple.cd";

    ASTCdCompilationUnit cd = parse(model);
    ICdLightArtifactScope as = createSymTabWithPkg(cd);
    ICdLightGlobalScope gs = CdLightMill.globalScope();

    assertEquals("cdlight", as.getPackageName());
    assertTrue(as.getImports(0).getStatement().equals("my.test"));

    assertTrue(gs.resolveOOType("cdlight.Simple.a.b.X").isPresent());
    assertTrue(gs.resolveOOType("cdlight.Simple.a.b.Y").isPresent());
    assertTrue(gs.resolveField("cdlight.Simple.a.b.X.z").isPresent());
    assertTrue(gs.resolveMethod("cdlight.Simple.a.b.Y.bar").isPresent());
  }

  @Test
  public void testDeepResolveWithoutArtifactName() throws IOException {
    String model = "src/test/resources/cdlight/Simple.cd";

    ASTCdCompilationUnit cd = parse(model);
    ICdLightArtifactScope as = createSymTabWithPkg(cd);
    ICdLightGlobalScope gs = CdLightMill.globalScope();

    assertEquals("cdlight", as.getPackageName());
    assertTrue(as.getImports(0).getStatement().equals("my.test"));

    assertTrue(gs.resolveOOType("cdlight.a.b.X").isPresent());
    assertTrue(gs.resolveOOType("cdlight.a.b.Y").isPresent());
    assertTrue(gs.resolveField("cdlight.a.b.X.z").isPresent());
    assertTrue(gs.resolveMethod("cdlight.a.b.Y.bar").isPresent());
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

    assertEquals("cdlight", as.getPackageName());
    assertTrue(as.getImports(0).getStatement().equals("cdlight.Simple.a.b"));

    assertFalse(as.resolveOOType("a.b.X").isPresent());
    assertFalse(as.resolveOOType("a.b.Y").isPresent());

    assertTrue(as.resolveOOType("X").isPresent());
    assertTrue(as.resolveOOType("Y").isPresent());

    assertTrue(as.resolveOOType("cdlight.Simple.a.b.X").isPresent());
    assertTrue(as.resolveOOType("cdlight.Simple.a.b.Y").isPresent());
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

    assertEquals("cdlight", as.getPackageName());
    assertTrue(as.getImports(0).getStatement().equals("cdlight.Simple.a.b"));

    assertTrue(as.resolveOOType("X").isPresent());
    assertTrue(as.resolveOOType("Y").isPresent());
    assertFalse(as.resolveOOType("a.b.X").isPresent());
    assertFalse(as.resolveOOType("a.b.Y").isPresent());

    assertEquals(0, gs.resolveOOTypeMany("X").size());
    assertEquals(0, gs.resolveOOTypeMany("Y").size());
    assertEquals(0, gs.resolveOOTypeMany("a.b.X").size());
    assertEquals(0, gs.resolveOOTypeMany("a.b.Y").size());

    assertEquals(1, gs.resolveOOTypeMany("cdlight.Simple.a.b.X").size());
    assertEquals(1, gs.resolveOOTypeMany("cdlight.Simple.a.b.Y").size());
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

    assertTrue(as.getImports(0).getStatement().equals("SimpleNoPkg"));

    assertTrue(gs.resolveOOType("SimpleNoPkg.a.b.X").isPresent());
    assertTrue(gs.resolveOOType("SimpleNoPkg.a.b.Y").isPresent());

    assertTrue(gs.resolveOOType("a.b.X").isPresent());
    assertTrue(gs.resolveOOType("a.b.Y").isPresent());
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

    assertTrue(as.getImports(0).getStatement().equals("cdlight.Simple.a.b"));

    assertTrue(as.resolveOOType("X").isPresent());
    assertTrue(as.resolveOOType("Y").isPresent());

    assertTrue(gs.resolveOOType("cdlight.Simple.a.b.X").isPresent());
    assertTrue(gs.resolveOOType("cdlight.Simple.a.b.Y").isPresent());

    assertTrue(gs.resolveOOType("SimpleNoPkg.a.b.X").isPresent());
    assertTrue(gs.resolveOOType("SimpleNoPkg.a.b.Y").isPresent());
  }


  @Test
  public void testSimpleNoPkgNoStar() throws IOException {
    String model = "src/test/resources/cdlight/Simple.cd";

    ASTCdCompilationUnit cd = parse(model);
    ICdLightArtifactScope as = createSymTab(cd);
    ICdLightGlobalScope gs = CdLightMill.globalScope();

    // assume import to be cdlight.Simple.a.b.X
    assertFalse(gs.resolveOOType("X").isPresent());
    assertFalse(gs.resolveOOType("cdlight.Simple.a.b.X").isPresent());
    assertFalse(gs.resolveOOType("Y").isPresent());

    // assume import to be Simple.a.b.X
    assertFalse(gs.resolveOOType("X").isPresent());
    assertTrue(gs.resolveOOType("Simple.a.b.X").isPresent());
    assertFalse(gs.resolveOOType("Y").isPresent());

    // the following only work, if the symbol table is already loaded

    // assume import to be a.b.X
    assertFalse(gs.resolveOOType("X").isPresent());
    assertTrue(gs.resolveOOType("a.b.X").isPresent());
    assertFalse(gs.resolveOOType("Y").isPresent());
  }


  @Test
  public void testSimpleNoPkgStar() throws IOException {
    String model = "src/test/resources/cdlight/Simple.cd";

    ASTCdCompilationUnit cd = parse(model);
    ICdLightArtifactScope as = createSymTab(cd);
    ICdLightGlobalScope gs = CdLightMill.globalScope();

    // assume import to be cdlight.Simple.a.b.*
    assertFalse(gs.resolveOOType("X").isPresent());
    assertFalse(gs.resolveOOType("cdlight.Simple.a.b.X").isPresent());
    assertFalse(gs.resolveOOType("Y").isPresent());
    assertFalse(gs.resolveOOType("cdlight.Simple.a.b.Y").isPresent());

    // assume import to be Simple.a.b.X.*
    assertFalse(gs.resolveOOType("X").isPresent());
    assertTrue(gs.resolveOOType("Simple.a.b.X").isPresent());
    assertFalse(gs.resolveOOType("Y").isPresent());
    assertTrue(gs.resolveOOType("Simple.a.b.Y").isPresent());

    // the following only work, if the symbol table is already loaded

    // assume import to be a.b.*
    assertFalse(gs.resolveOOType("X").isPresent());
    assertTrue(gs.resolveOOType("a.b.X").isPresent());
    assertFalse(gs.resolveOOType("Y").isPresent());
    assertTrue(gs.resolveOOType("a.b.Y").isPresent());
  }

  @Test
  public void testSimplePkgNoStar() throws IOException {
    String model = "src/test/resources/cdlight/Simple.cd";

    ASTCdCompilationUnit cd = parse(model);
    ICdLightArtifactScope as = createSymTabWithPkg(cd);
    ICdLightGlobalScope gs = CdLightMill.globalScope();

    // assume import to be cdlight.Simple.a.b.X
    assertFalse(gs.resolveOOType("X").isPresent());
    assertTrue(gs.resolveOOType("cdlight.Simple.a.b.X").isPresent());
    assertFalse(gs.resolveOOType("Y").isPresent());

    // assume import to be Simple.a.b.X
    assertFalse(gs.resolveOOType("X").isPresent());
    assertFalse(gs.resolveOOType("Simple.a.b.X").isPresent());
    assertFalse(gs.resolveOOType("Y").isPresent());

    // the following only work, if the symbol table is already loaded

    // assume import to be a.b.X
    assertFalse(gs.resolveOOType("X").isPresent());
    assertFalse(gs.resolveOOType("a.b.X").isPresent());
    assertFalse(gs.resolveOOType("Y").isPresent());

    // assume import to be cdlight.a.b.X
    assertTrue(gs.resolveOOType("cdlight.a.b.X").isPresent());

  }

  @Test
  public void testSimplePkgStar() throws IOException {
    String model = "src/test/resources/cdlight/Simple.cd";

    ASTCdCompilationUnit cd = parse(model);
    ICdLightArtifactScope as = createSymTabWithPkg(cd);
    ICdLightGlobalScope gs = CdLightMill.globalScope();

    // assume import to be cdlight.Simple.a.b.*
    assertFalse(gs.resolveOOType("X").isPresent());
    assertTrue(gs.resolveOOType("cdlight.Simple.a.b.X").isPresent());
    assertFalse(gs.resolveOOType("Y").isPresent());
    assertTrue(gs.resolveOOType("cdlight.Simple.a.b.Y").isPresent());

    // assume import to be Simple.a.b.X.*
    assertFalse(gs.resolveOOType("X").isPresent());
    assertFalse(gs.resolveOOType("Simple.a.b.X").isPresent());
    assertFalse(gs.resolveOOType("Y").isPresent());
    assertFalse(gs.resolveOOType("Simple.a.b.Y").isPresent());

    // the following only work, if the symbol table is already loaded

    // assume import to be a.b.*
    assertFalse(gs.resolveOOType("X").isPresent());
    assertFalse(gs.resolveOOType("a.b.X").isPresent());
    assertFalse(gs.resolveOOType("Y").isPresent());
    assertFalse(gs.resolveOOType("a.b.Y").isPresent());

    // assume import to be cdlight.a.b.*
    assertFalse(gs.resolveOOType("X").isPresent());
    assertTrue(gs.resolveOOType("cdlight.a.b.X").isPresent());
    assertFalse(gs.resolveOOType("Y").isPresent());
    assertTrue(gs.resolveOOType("cdlight.a.b.Y").isPresent());
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
    assertFalse(gs.resolveOOType("X").isPresent());
    assertTrue(gs.resolveOOType("cdlight.Simple.a.b.X").isPresent());
    assertFalse(gs.resolveOOType("Y").isPresent());
    assertTrue(gs.resolveOOType("cdlight.Simple.a.b.Y").isPresent());

    // assume import to be Simple.a.b.X.*
    assertFalse(gs.resolveOOType("X").isPresent());
    assertFalse(gs.resolveOOType("Simple.a.b.X").isPresent());
    assertFalse(gs.resolveOOType("Y").isPresent());
    assertFalse(gs.resolveOOType("Simple.a.b.Y").isPresent());

    // the following only work, if the symbol table is already loaded

    // assume import to be a.b.*
    assertFalse(gs.resolveOOType("X").isPresent());
    assertFalse(gs.resolveOOType("a.b.X").isPresent());
    assertFalse(gs.resolveOOType("Y").isPresent());
    assertFalse(gs.resolveOOType("a.b.Y").isPresent());

    // assume import to be cdlight.a.b.*
    assertFalse(gs.resolveOOType("X").isPresent());
    assertTrue(gs.resolveOOType("cdlight.a.b.X").isPresent());
    assertFalse(gs.resolveOOType("Y").isPresent());
    assertTrue(gs.resolveOOType("cdlight.a.b.Y").isPresent());
  }

  public ASTCdCompilationUnit parse(String model) throws IOException {
    CdLightParser parser = CdLightMill.parser();
    Optional<ASTCdCompilationUnit> cd = parser.parse(model);
    assertFalse(parser.hasErrors());
    assertTrue(cd.isPresent());
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
