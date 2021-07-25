/* (c) https://github.com/MontiCore/monticore */

package de.monticore;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import de.monticore.cd4analysis._symboltable.ICD4AnalysisGlobalScope;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cli.MontiCoreStandardCLI;
import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammarfamily.GrammarFamilyMill;
import de.monticore.grammar.grammarfamily._symboltable.GrammarFamilyGlobalScope;
import de.monticore.grammar.grammarfamily._symboltable.IGrammarFamilyGlobalScope;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.cli.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static de.monticore.MontiCoreConfiguration.*;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static org.junit.Assert.*;

/**
 * Test for the {@link MontiCoreScript} class.
 */
public class MontiCoreScriptTest {

  private ASTMCGrammar grammar;

  private ASTCDCompilationUnit cdCompilationUnit;

  private GlobalExtensionManagement glex;

  private static Set<String> additionalMethods = Sets.newLinkedHashSet();

  private static Path modelPathPath = Paths.get("src/test/resources");

  private static File outputPath = new File("target/generated-test-sources");

  private static MCPath modelPath = new MCPath(modelPathPath, outputPath.toPath());

  private static MCPath hwPath = new MCPath();

  private static MCPath templatePath = new MCPath("src/test/resources");

  static String[] simpleArgs = {"-" + GRAMMAR,
      "src/test/resources/de/monticore/statechart/Statechart.mc4",
      "src/test/resources/mc/grammars/lexicals/TestLexicals.mc4",
      "-" + MODELPATH, modelPathPath.toAbsolutePath().toString(),
      "-" + OUT, outputPath.getAbsolutePath(),
      "-" + HANDCODEDPATH, "src/test/resources"};

  @BeforeClass
  public static void setup() {
    Log.init();         // replace log by a sideffect free variant
    // LogStub.initPlusLog();  // for manual testing purpose only
    Log.enableFailQuick(false);
    additionalMethods.add("deepEquals");
    additionalMethods.add("deepEqualsWithComments");
    additionalMethods.add("equalAttributes");
    additionalMethods.add("deepClone");
    additionalMethods.add("_construct");
    additionalMethods.add("accept");
  }

  @Before
  public void init() {
    GrammarFamilyMill.reset();
    GrammarFamilyMill.init();
    glex = new GlobalExtensionManagement();
    Optional<ASTMCGrammar> ast = new MontiCoreScript()
        .parseGrammar(Paths.get(new File(
            "src/test/resources/de/monticore/statechart/Statechart.mc4").getAbsolutePath()));
    assertTrue(ast.isPresent());
    grammar = ast.get();
  }

  /**
   * {@link MontiCoreScript#parseGrammar(java.nio.file.Path)}
   */
  @Test
  public void testParseGrammar() {
    assertNotNull(grammar);
    assertEquals("Statechart", grammar.getName());
  }

  /**
   * {@link MontiCoreScript#generateParser(GlobalExtensionManagement, ASTCDCompilationUnit, ASTMCGrammar, GrammarFamilyGlobalScope, MCPath, MCPath, File)}
   */
  @Test
  public void testGenerateParser() {
    assertNotNull(grammar);
    MontiCoreScript mc = new MontiCoreScript();
    IGrammarFamilyGlobalScope symbolTable = TestHelper.createGlobalScope(modelPath);
    mc.createSymbolsFromAST(symbolTable, grammar);
    ICD4AnalysisGlobalScope cd4AGlobalScope = mc.createCD4AGlobalScope(modelPath);
    cdCompilationUnit = mc.deriveASTCD(grammar, glex, cd4AGlobalScope);
    File f = new File("target/generated-sources/monticore/testcode");
    mc.generateParser(glex, cdCompilationUnit, grammar, (GrammarFamilyGlobalScope) symbolTable, new MCPath(),
            new MCPath(), f);
    f.delete();
  }

  @Test
  public void testGetOrCreateCD() {
    MontiCoreScript mc = new MontiCoreScript();
    IGrammarFamilyGlobalScope symbolTable = TestHelper.createGlobalScope(modelPath);
    mc.createSymbolsFromAST(symbolTable, grammar);
    ICD4AnalysisGlobalScope cd4AGlobalScope = mc.createCD4AGlobalScope(modelPath);
    cdCompilationUnit = mc.getOrCreateCD(grammar, new GlobalExtensionManagement(), cd4AGlobalScope);
    assertNotNull(cdCompilationUnit);
    assertNotNull(cdCompilationUnit.getCDDefinition());
    assertEquals("Statechart", cdCompilationUnit.getCDDefinition().getName());
  }

  @Test
  public void testDeriveASTCD() {
    MontiCoreScript mc = new MontiCoreScript();
    IGrammarFamilyGlobalScope symbolTable = TestHelper.createGlobalScope(modelPath);
    mc.createSymbolsFromAST(symbolTable, grammar);
    ICD4AnalysisGlobalScope cd4AGlobalScope = mc.createCD4AGlobalScope(modelPath);
    cdCompilationUnit = mc.deriveASTCD(grammar, new GlobalExtensionManagement(), cd4AGlobalScope);
    assertNotNull(cdCompilationUnit);
    assertNotNull(cdCompilationUnit.getCDDefinition());
    assertEquals("Statechart", cdCompilationUnit.getCDDefinition().getName());
  }

  @Test
  public void testDecorateCd() {
    MontiCoreScript mc = new MontiCoreScript();
    IGrammarFamilyGlobalScope symbolTable = TestHelper.createGlobalScope(modelPath);
    mc.createSymbolsFromAST(symbolTable, grammar);
    ICD4AnalysisGlobalScope cd4AGlobalScope = mc.createCD4AGlobalScope(modelPath);
    cdCompilationUnit = mc.deriveASTCD(grammar, new GlobalExtensionManagement(), cd4AGlobalScope);
    assertNotNull(cdCompilationUnit);
    assertEquals("de.monticore.statechart", String.join(".", cdCompilationUnit.getCDPackageList()));
    assertNotNull(cdCompilationUnit.getCDDefinition());
    ASTCDDefinition cdDefinition = cdCompilationUnit.getCDDefinition();
    assertEquals(8, cdDefinition.getCDClassesList().size());
    assertEquals(5, cdDefinition.getCDInterfacesList().size());

    ASTCDCompilationUnit astcdCompilationUnit = mc.decorateForASTPackage(glex, cd4AGlobalScope, cdCompilationUnit, hwPath);
    // Added Builder classes to the each not list class
    assertEquals(17, astcdCompilationUnit.getCDDefinition().getCDClassesList().size());

    // Check if there are all additional methods defined in the given CD class
    List<String> methods = Lists.newArrayList();
    for (ASTCDClass cdClass : astcdCompilationUnit.getCDDefinition().getCDClassesList()) {
      // All methods of CD class
      for (ASTCDMethod method : cdClass.getCDMethodList()) {
        methods.add(method.getName());
      }
      String withOrder = "WithOrder";
      for (String additionalMethod : additionalMethods) {
        if (additionalMethod.endsWith(withOrder)) {
          assertTrue(methods.contains(additionalMethod.substring(0,
              additionalMethod.indexOf(withOrder))));
        } else {
          assertTrue(methods.contains(additionalMethod));
        }
      }
    }
  }

  @Test
  public void testDefaultScriptSimpleArgs() {
    Log.getFindings().clear();
    testDefaultScript(simpleArgs);
    assertEquals(0, Log.getErrorCount());
    // test whether the original template is used
    try {
      List<String> lines = Files.readAllLines(Paths.get(outputPath.getAbsolutePath() + "/de/monticore/statechart/statechart/_ast/ASTState.java"));
      assertFalse(lines.stream().filter(l -> "// Test:Replace Template".equals(l)).findAny().isPresent());
    } catch (Exception e) {
      fail();
    }
  }

  static String[] templateArgs = {"-" + GRAMMAR,
          "src/test/resources/de/monticore/statechart/Statechart.mc4",
          "src/test/resources/mc/grammars/lexicals/TestLexicals.mc4",
          "-" + MODELPATH, modelPathPath.toAbsolutePath().toString(),
          "-" + TEMPLATEPATH, "src/test/resources/ftltest",
          "-" + OUT, outputPath.getAbsolutePath()};

  @Test
  public void testDefaultScriptTemplateArgs() {
    Log.getFindings().clear();
    testDefaultScript(templateArgs);
    assertEquals(0, Log.getErrorCount());
    // test whether the changed template is used
    try {
      List<String> lines = Files.readAllLines(Paths.get(outputPath.getAbsolutePath() + "/de/monticore/statechart/statechart/_ast/ASTState.java"));
      assertTrue(lines.stream().filter(l -> "// Test:Replace Template".equals(l)).findAny().isPresent());
    } catch (Exception e) {
      fail();
    }
  }

  static String[] subsubgrammarArgs = {"-" + GRAMMAR,
          "src/test/resources/de/monticore/inherited/subsub/Subsubgrammar.mc4",
          "-" + MODELPATH, modelPathPath.toAbsolutePath().toString(),
          "-" + OUT, outputPath.getAbsolutePath()};

  @Test
  public void testDefaultScriptSubsubgrammarArgs() {
    Log.getFindings().clear();
    testDefaultScript(subsubgrammarArgs);
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testDefaultScriptSubsubgrammarArgs_EMF() {
    Log.getFindings().clear();
    testDefaultScriptWithEmf(subsubgrammarArgs);
    assertEquals(0, Log.getErrorCount());
  }

  static String[] inheritedgrammarArgs = {"-" + GRAMMAR,
      "src/test/resources/de/monticore/inherited/Supergrammar.mc4",
      "src/test/resources/de/monticore/inherited/sub/Subgrammar.mc4",
      "src/test/resources/de/monticore/inherited/subsub/Subsubgrammar.mc4",
      "-" + MODELPATH, modelPathPath.toAbsolutePath().toString(),
      "-" + OUT, outputPath.getAbsolutePath()};

  @Test
  public void testDefaultScriptSupergrammarArgs() {
    Log.getFindings().clear();
    testDefaultScript(inheritedgrammarArgs);
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testDefaultScriptSupergrammarArgs_EMF() {
    Log.getFindings().clear();
    testDefaultScriptWithEmf(inheritedgrammarArgs);
    assertEquals(0, Log.getErrorCount());
  }

  static String[] supersubgrammarArgs = {"-" + GRAMMAR,
      "src/test/resources/de/monticore/inherited/subsub/Subsubgrammar.mc4",
      "src/test/resources/de/monticore/inherited/Supergrammar.mc4",
      "-" + MODELPATH, modelPathPath.toAbsolutePath().toString(),
      "-" + OUT, outputPath.getAbsolutePath()};

  @Test
  public void testDefaultScriptSupersubgrammarArgs() {
    Log.getFindings().clear();
    testDefaultScript(supersubgrammarArgs);
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testDefaultScriptSupersubgrammarArgs_EMF() {
    Log.getFindings().clear();
    testDefaultScriptWithEmf(supersubgrammarArgs);
    assertEquals(0, Log.getErrorCount());
  }

  private void testDefaultScript(String[] args) {
    Optional<CommandLine> cmd = Optional.empty();
    try {
      org.apache.commons.cli.Options options = initOptions();
      CommandLineParser cliParser = new DefaultParser();
      cmd = Optional.ofNullable(cliParser.parse(options, args));
    } catch (ParseException e) {
      Log.error("0xA7152 Could not process test CLI parameters: " + e.getMessage());
    }

    MontiCoreConfiguration cfg = MontiCoreConfiguration.withCLI(cmd.get());
    new MontiCoreScript().run(cfg);
    // Reporting is enabled in the monticore_standard.groovy script but needs to be disabled for other tests
    // because Reporting is static directly disable it again here
    Reporting.off();
    assertTrue(!false);
  }

  private void testDefaultScriptWithEmf(String[] args) {
    Optional<CommandLine> cmd = Optional.empty();
    try {
      org.apache.commons.cli.Options options = initOptions();
      CommandLineParser cliParser = new DefaultParser();
      cmd = Optional.ofNullable(cliParser.parse(options, args));
    } catch (ParseException e) {
      Log.error("0xA7152 Could not process test CLI parameters: " + e.getMessage());
    }

    MontiCoreConfiguration cfg = MontiCoreConfiguration.withCLI(cmd.get());
    new MontiCoreScript().run_emf(cfg);
    // Reporting is enabled in the monticore_standard.groovy script but needs to be disabled for other tests
    // because Reporting is static directly disable it again here
    Reporting.off();
    assertTrue(!false);
  }

  @Test
  public void testDeriveSymbolCD() {
    MontiCoreScript mc = new MontiCoreScript();
    IGrammarFamilyGlobalScope symbolTable = TestHelper.createGlobalScope(modelPath);
    mc.createSymbolsFromAST(symbolTable, grammar);
    ICD4AnalysisGlobalScope cd4AGlobalScope = mc.createCD4AGlobalScope(modelPath);
    ASTCDCompilationUnit cdCompilationUnit = mc.deriveSymbolCD(grammar, cd4AGlobalScope);
    // check directly created scope
    assertNotNull(cdCompilationUnit);
    assertNotNull(cdCompilationUnit.getCDDefinition());
    assertEquals("StatechartSymbols", cdCompilationUnit.getCDDefinition().getName());
    // no symbol defined
    assertEquals(0, cdCompilationUnit.getCDDefinition().getCDClassesList().size());

    // check saved cd for grammar
    ASTCDCompilationUnit symbolCDOfParsedGrammar = mc.getSymbolCDOfParsedGrammar(grammar);
    assertNotNull(symbolCDOfParsedGrammar);
    assertNotNull(symbolCDOfParsedGrammar.getCDDefinition());
    assertEquals("StatechartSymbols", symbolCDOfParsedGrammar.getCDDefinition().getName());
    // no symbol defined
    assertEquals(0, symbolCDOfParsedGrammar.getCDDefinition().getCDClassesList().size());
  }

  @Test
  public void testDeriveScopeCD() {
    MontiCoreScript mc = new MontiCoreScript();
    IGrammarFamilyGlobalScope symbolTable = TestHelper.createGlobalScope(modelPath);
    mc.createSymbolsFromAST(symbolTable, grammar);
    ICD4AnalysisGlobalScope cd4AGlobalScope = mc.createCD4AGlobalScope(modelPath);
    ASTCDCompilationUnit cdCompilationUnit = mc.deriveScopeCD(grammar, cd4AGlobalScope);
    // test normal created scope cd
    assertNotNull(cdCompilationUnit);
    assertNotNull(cdCompilationUnit.getCDDefinition());
    assertEquals("StatechartScope", cdCompilationUnit.getCDDefinition().getName());
    assertEquals(1, cdCompilationUnit.getCDDefinition().getCDClassesList().size());
    assertEquals("Statechart", cdCompilationUnit.getCDDefinition().getCDClassesList().get(0).getName());

    // test correct saved scope cd
    ASTCDCompilationUnit scopeCDOfParsedGrammar = mc.getScopeCDOfParsedGrammar(grammar);
    assertNotNull(scopeCDOfParsedGrammar);
    assertNotNull(scopeCDOfParsedGrammar.getCDDefinition());
    assertEquals("StatechartScope", scopeCDOfParsedGrammar.getCDDefinition().getName());
    assertEquals(1, scopeCDOfParsedGrammar.getCDDefinition().getCDClassesList().size());
    assertEquals("Statechart", scopeCDOfParsedGrammar.getCDDefinition().getCDClassesList().get(0).getName());
  }

  @Test
  public void testAddListSuffixToAttributeName() {
    MontiCoreScript mc = new MontiCoreScript();
    IGrammarFamilyGlobalScope symbolTable = TestHelper.createGlobalScope(modelPath);
    mc.createSymbolsFromAST(symbolTable, grammar);
    ICD4AnalysisGlobalScope cd4AGlobalScope = mc.createCD4AGlobalScope(modelPath);
    ASTCDCompilationUnit cdCompilationUnit = mc.deriveASTCD(grammar,
        new GlobalExtensionManagement(), cd4AGlobalScope);
    assertNotNull(cdCompilationUnit);
    assertNotNull(cdCompilationUnit.getCDDefinition());
    assertEquals("Statechart", cdCompilationUnit.getCDDefinition().getName());
    ASTCDClass stateChartClass = cdCompilationUnit.getCDDefinition().getCDClassesList().get(0);
    assertEquals("ASTStatechart", stateChartClass.getName());
    assertEquals("state", stateChartClass.getCDAttributeList().get(1).getName());

    // add list suffix
    ASTCDCompilationUnit listSuffixCD = mc.addListSuffixToAttributeName(cdCompilationUnit);

    assertNotNull(listSuffixCD);
    assertNotNull(listSuffixCD.getCDDefinition());
    assertEquals("Statechart", listSuffixCD.getCDDefinition().getName());
    ASTCDClass listSuffixStateChartClass = listSuffixCD.getCDDefinition().getCDClassesList().get(0);
    assertEquals("ASTStatechart", listSuffixStateChartClass.getName());
    assertDeepEquals("java.util.List<de.monticore.statechart.Statechart.ASTState>", listSuffixStateChartClass.getCDAttributeList().get(1).getMCType());
    // attribute with 's' at the end now
    assertEquals("states", listSuffixStateChartClass.getCDAttributeList().get(1).getName());
  }

  @Test
  public void testDecorateForSymbolTablePackage() {
    MontiCoreScript mc = new MontiCoreScript();
    GlobalExtensionManagement glex = new GlobalExtensionManagement();
    IGrammarFamilyGlobalScope symbolTable = TestHelper.createGlobalScope(modelPath);
    mc.createSymbolsFromAST(symbolTable, grammar);
    ICD4AnalysisGlobalScope cd4AGlobalScope = mc.createCD4AGlobalScope(modelPath);
    ICD4AnalysisGlobalScope cd4AGlobalScopeSymbolCD = mc.createCD4AGlobalScope(modelPath);
    ICD4AnalysisGlobalScope cd4AGlobalScopeScopeCD = mc.createCD4AGlobalScope(modelPath);

    ASTCDCompilationUnit symbolCD = mc.deriveSymbolCD(grammar, cd4AGlobalScopeSymbolCD);
    ASTCDCompilationUnit scopeCD = mc.deriveScopeCD(grammar, cd4AGlobalScopeScopeCD);
    ASTCDCompilationUnit cd = mc.deriveASTCD(grammar, glex, cd4AGlobalScope);

    MCPath handcodedPath = new MCPath("src/test/resources");
    ASTCDCompilationUnit symbolPackageCD = mc.decorateForSymbolTablePackage(glex, cd4AGlobalScope, cd, symbolCD, scopeCD, handcodedPath);
    assertNotNull(symbolPackageCD);
    assertNotNull(symbolPackageCD.getCDDefinition());
    assertEquals("Statechart", symbolPackageCD.getCDDefinition().getName());

    int index = 0;
    assertEquals(7, symbolPackageCD.getCDDefinition().getCDClassesList().size());
    assertEquals("StatechartScope", symbolPackageCD.getCDDefinition().getCDClassesList().get(index++).getName());
    assertEquals("StatechartSymbols2Json", symbolPackageCD.getCDDefinition().getCDClassesList().get(index++).getName());
    assertEquals("StatechartScopesGenitorDelegator", symbolPackageCD.getCDDefinition().getCDClassesList().get(index++).getName());
    assertEquals("StatechartArtifactScope", symbolPackageCD.getCDDefinition().getCDClassesList().get(index++).getName());
    assertEquals("StatechartDeSer", symbolPackageCD.getCDDefinition().getCDClassesList().get(index++).getName());
    assertEquals("StatechartGlobalScope", symbolPackageCD.getCDDefinition().getCDClassesList().get(index++).getName());
    assertEquals("StatechartScopesGenitor", symbolPackageCD.getCDDefinition().getCDClassesList().get(index++).getName());

    index = 0;
    assertEquals(4, symbolPackageCD.getCDDefinition().getCDInterfacesList().size());
    assertEquals("IStatechartScope", symbolPackageCD.getCDDefinition().getCDInterfacesList().get(index++).getName());
    assertEquals("ICommonStatechartSymbol", symbolPackageCD.getCDDefinition().getCDInterfacesList().get(index++).getName());
    assertEquals("IStatechartGlobalScope", symbolPackageCD.getCDDefinition().getCDInterfacesList().get(index++).getName());
    assertEquals("IStatechartArtifactScope", symbolPackageCD.getCDDefinition().getCDInterfacesList().get(index++).getName());
  }

  @Test
  public void testDecorateForVisitorPackage() {
    MontiCoreScript mc = new MontiCoreScript();
    GlobalExtensionManagement glex = new GlobalExtensionManagement();
    IGrammarFamilyGlobalScope symbolTable = TestHelper.createGlobalScope(modelPath);
    mc.createSymbolsFromAST(symbolTable, grammar);
    ICD4AnalysisGlobalScope cd4AGlobalScope = mc.createCD4AGlobalScope(modelPath);
    ASTCDCompilationUnit cd = mc.deriveASTCD(grammar, glex, cd4AGlobalScope);
    MCPath handcodedPath = new MCPath("src/test/resources");

    ASTCDCompilationUnit visitorPackageCD = mc.decorateTraverserForVisitorPackage(glex, cd4AGlobalScope, cd, handcodedPath);

    assertNotNull(visitorPackageCD);
    assertNotNull(visitorPackageCD.getCDDefinition());
    assertEquals("Statechart", visitorPackageCD.getCDDefinition().getName());
    assertEquals(2, visitorPackageCD.getCDDefinition().getCDClassesList().size());
    assertEquals("StatechartTraverserImplementation", visitorPackageCD.getCDDefinition().getCDClassesList().get(0).getName());
    assertEquals("StatechartInheritanceHandler", visitorPackageCD.getCDDefinition().getCDClassesList().get(1).getName());
    assertEquals(3, visitorPackageCD.getCDDefinition().getCDInterfacesList().size());
    assertEquals("StatechartTraverser", visitorPackageCD.getCDDefinition().getCDInterfacesList().get(0).getName());
    assertEquals("StatechartVisitor2", visitorPackageCD.getCDDefinition().getCDInterfacesList().get(1).getName());
    assertEquals("StatechartHandler", visitorPackageCD.getCDDefinition().getCDInterfacesList().get(2).getName());
  }

  @Test
  public void testDecorateForCoCoPackage() {
    MontiCoreScript mc = new MontiCoreScript();
    GlobalExtensionManagement glex = new GlobalExtensionManagement();
    IGrammarFamilyGlobalScope symbolTable = TestHelper.createGlobalScope(modelPath);
    mc.createSymbolsFromAST(symbolTable, grammar);
    ICD4AnalysisGlobalScope cd4AGlobalScope = mc.createCD4AGlobalScope(modelPath);
    ASTCDCompilationUnit cd = mc.deriveASTCD(grammar, glex, cd4AGlobalScope);
    MCPath handcodedPath = new MCPath("src/test/resources");

    ASTCDCompilationUnit cocoPackageCD = mc.decorateForCoCoPackage(glex, cd4AGlobalScope, cd, handcodedPath);

    assertNotNull(cocoPackageCD);
    assertNotNull(cocoPackageCD.getCDDefinition());
    assertEquals("Statechart", cocoPackageCD.getCDDefinition().getName());
    assertEquals(1, cocoPackageCD.getCDDefinition().getCDClassesList().size());
    assertEquals("StatechartCoCoChecker", cocoPackageCD.getCDDefinition().getCDClassesList().get(0).getName());
    assertEquals(13, cocoPackageCD.getCDDefinition().getCDInterfacesList().size());
    assertEquals("StatechartASTStatechartCoCo", cocoPackageCD.getCDDefinition().getCDInterfacesList().get(0).getName());
    assertEquals("StatechartASTEntryActionCoCo", cocoPackageCD.getCDDefinition().getCDInterfacesList().get(1).getName());
    assertEquals("StatechartASTExitActionCoCo", cocoPackageCD.getCDDefinition().getCDInterfacesList().get(2).getName());
    assertEquals("StatechartASTStateCoCo", cocoPackageCD.getCDDefinition().getCDInterfacesList().get(3).getName());
    assertEquals("StatechartASTTransitionCoCo", cocoPackageCD.getCDDefinition().getCDInterfacesList().get(4).getName());
    assertEquals("StatechartASTArgumentCoCo", cocoPackageCD.getCDDefinition().getCDInterfacesList().get(5).getName());
    assertEquals("StatechartASTCodeCoCo", cocoPackageCD.getCDDefinition().getCDInterfacesList().get(6).getName());
    assertEquals("StatechartASTAbstractAnythingCoCo", cocoPackageCD.getCDDefinition().getCDInterfacesList().get(7).getName());
    assertEquals("StatechartASTSCStructureCoCo", cocoPackageCD.getCDDefinition().getCDInterfacesList().get(8).getName());
    assertEquals("StatechartASTBlockStatementExtCoCo", cocoPackageCD.getCDDefinition().getCDInterfacesList().get(9).getName());
    assertEquals("StatechartASTExpressionExtCoCo", cocoPackageCD.getCDDefinition().getCDInterfacesList().get(10).getName());
    assertEquals("StatechartASTClassbodyExtCoCo", cocoPackageCD.getCDDefinition().getCDInterfacesList().get(11).getName());
    assertEquals("StatechartASTStatechartNodeCoCo", cocoPackageCD.getCDDefinition().getCDInterfacesList().get(12).getName());
  }

  @Test
  public void testDecorateForASTPackage() {
    MontiCoreScript mc = new MontiCoreScript();
    GlobalExtensionManagement glex = new GlobalExtensionManagement();
    IGrammarFamilyGlobalScope symbolTable = TestHelper.createGlobalScope(modelPath);
    mc.createSymbolsFromAST(symbolTable, grammar);
    ICD4AnalysisGlobalScope cd4AGlobalScope = mc.createCD4AGlobalScope(modelPath);
    ASTCDCompilationUnit cd = mc.deriveASTCD(grammar, glex, cd4AGlobalScope);
    MCPath handcodedPath = new MCPath("src/test/resources");

    ASTCDCompilationUnit astPackageCD = mc.decorateForASTPackage(glex, cd4AGlobalScope, cd, handcodedPath);

    assertNotNull(astPackageCD);
    assertNotNull(astPackageCD.getCDDefinition());
    assertEquals("Statechart", astPackageCD.getCDDefinition().getName());
    assertEquals(17, astPackageCD.getCDDefinition().getCDClassesList().size());
    assertEquals("ASTStatechart", astPackageCD.getCDDefinition().getCDClassesList().get(0).getName());
    assertEquals("ASTEntryAction", astPackageCD.getCDDefinition().getCDClassesList().get(1).getName());
    assertEquals("ASTExitAction", astPackageCD.getCDDefinition().getCDClassesList().get(2).getName());
    assertEquals("ASTState", astPackageCD.getCDDefinition().getCDClassesList().get(3).getName());
    assertEquals("ASTTransition", astPackageCD.getCDDefinition().getCDClassesList().get(4).getName());
    assertEquals("ASTArgument", astPackageCD.getCDDefinition().getCDClassesList().get(5).getName());
    assertEquals("ASTCode", astPackageCD.getCDDefinition().getCDClassesList().get(6).getName());
    assertEquals("ASTAbstractAnything", astPackageCD.getCDDefinition().getCDClassesList().get(7).getName());
    assertEquals("ASTStatechartBuilder", astPackageCD.getCDDefinition().getCDClassesList().get(8).getName());
    assertEquals("ASTEntryActionBuilder", astPackageCD.getCDDefinition().getCDClassesList().get(9).getName());
    assertEquals("ASTExitActionBuilder", astPackageCD.getCDDefinition().getCDClassesList().get(10).getName());
    assertEquals("ASTStateBuilder", astPackageCD.getCDDefinition().getCDClassesList().get(11).getName());
    assertEquals("ASTTransitionBuilder", astPackageCD.getCDDefinition().getCDClassesList().get(12).getName());
    assertEquals("ASTArgumentBuilder", astPackageCD.getCDDefinition().getCDClassesList().get(13).getName());
    assertEquals("ASTCodeBuilder", astPackageCD.getCDDefinition().getCDClassesList().get(14).getName());
    assertEquals("ASTAbstractAnythingBuilder", astPackageCD.getCDDefinition().getCDClassesList().get(15).getName());
    assertEquals("ASTConstantsStatechart", astPackageCD.getCDDefinition().getCDClassesList().get(16).getName());

    assertEquals(5, astPackageCD.getCDDefinition().getCDInterfacesList().size());
    assertEquals("ASTSCStructure", astPackageCD.getCDDefinition().getCDInterfacesList().get(0).getName());
    assertEquals("ASTBlockStatementExt", astPackageCD.getCDDefinition().getCDInterfacesList().get(1).getName());
    assertEquals("ASTExpressionExt", astPackageCD.getCDDefinition().getCDInterfacesList().get(2).getName());
    assertEquals("ASTClassbodyExt", astPackageCD.getCDDefinition().getCDInterfacesList().get(3).getName());
    assertEquals("ASTStatechartNode", astPackageCD.getCDDefinition().getCDInterfacesList().get(4).getName());
    assertEquals(1, astPackageCD.getCDDefinition().getCDEnumsList().size());
    assertEquals("StatechartLiterals", astPackageCD.getCDDefinition().getCDEnumsList().get(0).getName());
  }

  @Test
  public void testDecorateForEmfASTPackage() {
    MontiCoreScript mc = new MontiCoreScript();
    GlobalExtensionManagement glex = new GlobalExtensionManagement();
    IGrammarFamilyGlobalScope symbolTable = TestHelper.createGlobalScope(modelPath);
    mc.createSymbolsFromAST(symbolTable, grammar);
    ICD4AnalysisGlobalScope cd4AGlobalScope = mc.createCD4AGlobalScope(modelPath);
    ASTCDCompilationUnit cd = mc.deriveASTCD(grammar, glex, cd4AGlobalScope);
    MCPath handcodedPath = new MCPath("src/test/resources");

    ASTCDCompilationUnit astEmfPackageCD = mc.decorateEmfForASTPackage(glex, cd4AGlobalScope, cd, handcodedPath);

    assertNotNull(astEmfPackageCD);
    assertNotNull(astEmfPackageCD.getCDDefinition());
    assertEquals("Statechart", astEmfPackageCD.getCDDefinition().getName());
    assertEquals(18, astEmfPackageCD.getCDDefinition().getCDClassesList().size());
    assertEquals("ASTStatechart", astEmfPackageCD.getCDDefinition().getCDClassesList().get(0).getName());
    assertEquals("ASTEntryAction", astEmfPackageCD.getCDDefinition().getCDClassesList().get(1).getName());
    assertEquals("ASTExitAction", astEmfPackageCD.getCDDefinition().getCDClassesList().get(2).getName());
    assertEquals("ASTState", astEmfPackageCD.getCDDefinition().getCDClassesList().get(3).getName());
    assertEquals("ASTTransition", astEmfPackageCD.getCDDefinition().getCDClassesList().get(4).getName());
    assertEquals("ASTArgument", astEmfPackageCD.getCDDefinition().getCDClassesList().get(5).getName());
    assertEquals("ASTCode", astEmfPackageCD.getCDDefinition().getCDClassesList().get(6).getName());
    assertEquals("ASTAbstractAnything", astEmfPackageCD.getCDDefinition().getCDClassesList().get(7).getName());
    assertEquals("ASTStatechartBuilder", astEmfPackageCD.getCDDefinition().getCDClassesList().get(8).getName());
    assertEquals("ASTEntryActionBuilder", astEmfPackageCD.getCDDefinition().getCDClassesList().get(9).getName());
    assertEquals("ASTExitActionBuilder", astEmfPackageCD.getCDDefinition().getCDClassesList().get(10).getName());
    assertEquals("ASTStateBuilder", astEmfPackageCD.getCDDefinition().getCDClassesList().get(11).getName());
    assertEquals("ASTTransitionBuilder", astEmfPackageCD.getCDDefinition().getCDClassesList().get(12).getName());
    assertEquals("ASTArgumentBuilder", astEmfPackageCD.getCDDefinition().getCDClassesList().get(13).getName());
    assertEquals("ASTCodeBuilder", astEmfPackageCD.getCDDefinition().getCDClassesList().get(14).getName());
    assertEquals("ASTAbstractAnythingBuilder", astEmfPackageCD.getCDDefinition().getCDClassesList().get(15).getName());
    assertEquals("ASTConstantsStatechart", astEmfPackageCD.getCDDefinition().getCDClassesList().get(16).getName());
    assertEquals("StatechartPackageImpl", astEmfPackageCD.getCDDefinition().getCDClassesList().get(17).getName());

    assertEquals(6, astEmfPackageCD.getCDDefinition().getCDInterfacesList().size());
    assertEquals("ASTSCStructure", astEmfPackageCD.getCDDefinition().getCDInterfacesList().get(0).getName());
    assertEquals("ASTBlockStatementExt", astEmfPackageCD.getCDDefinition().getCDInterfacesList().get(1).getName());
    assertEquals("ASTExpressionExt", astEmfPackageCD.getCDDefinition().getCDInterfacesList().get(2).getName());
    assertEquals("ASTClassbodyExt", astEmfPackageCD.getCDDefinition().getCDInterfacesList().get(3).getName());
    assertEquals("ASTStatechartNode", astEmfPackageCD.getCDDefinition().getCDInterfacesList().get(4).getName());
    assertEquals("StatechartPackage", astEmfPackageCD.getCDDefinition().getCDInterfacesList().get(5).getName());

    assertEquals(1, astEmfPackageCD.getCDDefinition().getCDEnumsList().size());
    assertEquals("StatechartLiterals", astEmfPackageCD.getCDDefinition().getCDEnumsList().get(0).getName());
  }

  @Test
  public void testDecorateForODPackage() {
    MontiCoreScript mc = new MontiCoreScript();
    GlobalExtensionManagement glex = new GlobalExtensionManagement();
    IGrammarFamilyGlobalScope symbolTable = TestHelper.createGlobalScope(modelPath);
    mc.createSymbolsFromAST(symbolTable, grammar);
    ICD4AnalysisGlobalScope cd4AGlobalScope = mc.createCD4AGlobalScope(modelPath);

    ASTCDCompilationUnit cd = mc.deriveASTCD(grammar, glex, cd4AGlobalScope);
    MCPath handcodedPath = new MCPath("src/test/resources");

    ASTCDCompilationUnit odPackage = mc.decorateForODPackage(glex, cd4AGlobalScope, cd, handcodedPath);

    assertNotNull(odPackage);
    assertNotNull(odPackage.getCDDefinition());
    assertEquals("Statechart", odPackage.getCDDefinition().getName());
    assertEquals(1, odPackage.getCDDefinition().getCDClassesList().size());
    assertEquals("Statechart2OD", odPackage.getCDDefinition().getCDClassesList().get(0).getName());
    assertTrue(odPackage.getCDDefinition().getCDInterfacesList().isEmpty());
    assertTrue(odPackage.getCDDefinition().getCDEnumsList().isEmpty());
  }

  @Test
  public void testDecorateForMill() {
    MontiCoreScript mc = new MontiCoreScript();
    GlobalExtensionManagement glex = new GlobalExtensionManagement();
    IGrammarFamilyGlobalScope symbolTable = TestHelper.createGlobalScope(modelPath);
    mc.createSymbolsFromAST(symbolTable, grammar);
    ICD4AnalysisGlobalScope cd4AGlobalScope = mc.createCD4AGlobalScope(modelPath);

    ASTCDCompilationUnit cd = mc.deriveASTCD(grammar, glex, cd4AGlobalScope);
    MCPath handcodedPath = new MCPath("src/test/resources");

    ASTCDCompilationUnit millCd = mc.decorateMill(glex, cd4AGlobalScope, cd, getASTCD(cd), getSymbolCD(cd), getTraverserCD(cd), handcodedPath);

    assertNotNull(millCd);
    assertNotNull(millCd.getCDDefinition());
    assertEquals(4, millCd.sizePackage());
    assertEquals("de", millCd.getMCPackageDeclaration().getMCQualifiedName().getParts(0));
    assertEquals("monticore", millCd.getMCPackageDeclaration().getMCQualifiedName().getParts(1));
    assertEquals("statechart", millCd.getMCPackageDeclaration().getMCQualifiedName().getParts(2));
    assertEquals("statechart", millCd.getMCPackageDeclaration().getMCQualifiedName().getParts(3));
    assertEquals("Statechart", millCd.getCDDefinition().getName());
    assertEquals(1, millCd.getCDDefinition().getCDClassesList().size());
    assertEquals("StatechartMill", millCd.getCDDefinition().getCDClassesList().get(0).getName());
    assertTrue(millCd.getCDDefinition().getCDInterfacesList().isEmpty());
    assertTrue(millCd.getCDDefinition().getCDEnumsList().isEmpty());
  }

  @Test
  public void testDecorateForAuxiliaryPackage(){
    MontiCoreScript mc = new MontiCoreScript();
    GlobalExtensionManagement glex = new GlobalExtensionManagement();
    IGrammarFamilyGlobalScope symbolTable = TestHelper.createGlobalScope(modelPath);
    mc.createSymbolsFromAST(symbolTable, grammar);
    ICD4AnalysisGlobalScope cd4AGlobalScope = mc.createCD4AGlobalScope(modelPath);

    ASTCDCompilationUnit cd = mc.deriveASTCD(grammar, glex, cd4AGlobalScope);
    MCPath handcodedPath = new MCPath("src/test/resources");

    ASTCDCompilationUnit auxiliaryCD = mc.decorateAuxiliary(glex, cd4AGlobalScope, cd, getASTCD(cd), handcodedPath);

    assertNotNull(auxiliaryCD);
    assertNotNull(auxiliaryCD.getCDDefinition());
    assertEquals("de", auxiliaryCD.getMCPackageDeclaration().getMCQualifiedName().getParts(0));
    assertEquals("monticore", auxiliaryCD.getMCPackageDeclaration().getMCQualifiedName().getParts(1));
    assertEquals("statechart", auxiliaryCD.getMCPackageDeclaration().getMCQualifiedName().getParts(2));
    assertEquals("statechart", auxiliaryCD.getMCPackageDeclaration().getMCQualifiedName().getParts(3));
    assertEquals("_auxiliary", auxiliaryCD.getMCPackageDeclaration().getMCQualifiedName().getParts(4));
    assertEquals("Statechart", auxiliaryCD.getCDDefinition().getName());
    assertEquals(1, auxiliaryCD.getCDDefinition().getCDClassesList().size());
    assertEquals("TestLexicalsMillForStatechart", auxiliaryCD.getCDDefinition().getCDClassesList().get(0).getName());
    assertTrue(auxiliaryCD.getCDDefinition().getCDInterfacesList().isEmpty());
    assertTrue(auxiliaryCD.getCDDefinition().getCDEnumsList().isEmpty());
  }

  protected ASTCDCompilationUnit getSymbolCD(ASTCDCompilationUnit decoratedCompilationUnit) {
    MontiCoreScript mc = new MontiCoreScript();
    GlobalExtensionManagement glex = new GlobalExtensionManagement();
    IGrammarFamilyGlobalScope symbolTable = TestHelper.createGlobalScope(modelPath);
    mc.createSymbolsFromAST(symbolTable, grammar);
    ICD4AnalysisGlobalScope cd4AGlobalScope = mc.createCD4AGlobalScope(modelPath);
    ICD4AnalysisGlobalScope cd4AGlobalScopeSymbolCD = mc.createCD4AGlobalScope(modelPath);
    ICD4AnalysisGlobalScope cd4AGlobalScopeScopeCD = mc.createCD4AGlobalScope(modelPath);

    ASTCDCompilationUnit symbolCD = mc.deriveSymbolCD(grammar, cd4AGlobalScopeSymbolCD);
    ASTCDCompilationUnit scopeCD = mc.deriveScopeCD(grammar, cd4AGlobalScopeScopeCD);
    ASTCDCompilationUnit cd = mc.deriveASTCD(grammar, glex, cd4AGlobalScope);

    MCPath handcodedPath = new MCPath("src/test/resources");
    return mc.decorateForSymbolTablePackage(glex, cd4AGlobalScope, cd, symbolCD, scopeCD, handcodedPath);
  }

  protected ASTCDCompilationUnit getASTCD(ASTCDCompilationUnit decoratedCompilationUnit) {
    MontiCoreScript mc = new MontiCoreScript();
    GlobalExtensionManagement glex = new GlobalExtensionManagement();
    IGrammarFamilyGlobalScope symbolTable = TestHelper.createGlobalScope(modelPath);
    mc.createSymbolsFromAST(symbolTable, grammar);
    ICD4AnalysisGlobalScope cd4AGlobalScope = mc.createCD4AGlobalScope(modelPath);
    ASTCDCompilationUnit cd = mc.deriveASTCD(grammar, glex, cd4AGlobalScope);
    MCPath handcodedPath = new MCPath("src/test/resources");

    return mc.decorateForASTPackage(glex, cd4AGlobalScope, cd, handcodedPath);
  }

  protected ASTCDCompilationUnit getTraverserCD(ASTCDCompilationUnit decoratedCompilationUnit) {
    MontiCoreScript mc = new MontiCoreScript();
    GlobalExtensionManagement glex = new GlobalExtensionManagement();
    IGrammarFamilyGlobalScope symbolTable = TestHelper.createGlobalScope(modelPath);
    mc.createSymbolsFromAST(symbolTable, grammar);
    ICD4AnalysisGlobalScope cd4AGlobalScope = mc.createCD4AGlobalScope(modelPath);
    ASTCDCompilationUnit cd = mc.deriveASTCD(grammar, glex, cd4AGlobalScope);
    MCPath handcodedPath = new MCPath("src/test/resources");

    return mc.decorateTraverserForVisitorPackage(glex, cd4AGlobalScope, cd, handcodedPath);
  }

  /**
   * Initializes the available CLI options for the MontiCore tool.
   *
   * This method must always remain a copy of the initOptions
   * method of {@link MontiCoreStandardCLI}
   *
   * @return The CLI options with arguments.
   */
  protected Options initOptions() {
    Options options = new Options();

    // parse input grammars
    options.addOption(Option.builder(GRAMMAR)
            .longOpt(GRAMMAR_LONG)
            .argName("filelist")
            .hasArgs()
            .desc("Processes the source grammars (mandatory) and triggers the MontiCore generation.")
            .build());

    // specify custom output directory
    options.addOption(Option.builder(OUT)
            .longOpt(OUT_LONG)
            .argName("path")
            .hasArg()
            .desc("Optional output directory for all generated artifacts.")
            .build());

    // specify model path
    options.addOption(Option.builder(MODELPATH)
            .longOpt(MODELPATH_LONG)
            .argName("pathlist")
            .hasArgs()
            .desc("Optional list of directories or files to be included for importing other grammars.")
            .build());

    // specify hand-written artifacts
    options.addOption(Option.builder(HANDCODEDPATH)
            .longOpt(HANDCODEDPATH_LONG)
            .argName("pathlist")
            .hasArgs()
            .desc("Optional list of directories to look for handwritten code to integrate.")
            .build());

    // specify custom output
    options.addOption(Option.builder(SCRIPT)
            .longOpt(SCRIPT_LONG)
            .argName("file.groovy")
            .hasArg()
            .desc("Optional Groovy script to control the generation workflow.")
            .build());

    // specify custom script for hook point one
    options.addOption(Option.builder(GROOVYHOOK1)
            .longOpt(GROOVYHOOK1_LONG)
            .argName("file.groovy")
            .hasArg()
            .desc("Optional Groovy script that is hooked into the workflow of the standard script at hook point one.")
            .build());

    // specify custom script for hook point two
    options.addOption(Option.builder(GROOVYHOOK2)
            .longOpt(GROOVYHOOK2_LONG)
            .argName("file.groovy")
            .hasArg()
            .desc("Optional Groovy script that is hooked into the workflow of the standard script at hook point two.")
            .build());

    // specify template path
    options.addOption(Option.builder(TEMPLATEPATH)
            .longOpt(TEMPLATEPATH_LONG)
            .argName("pathlist")
            .hasArgs()
            .desc("Optional list of directories to look for handwritten templates to integrate.")
            .build());

    // specify template config
    options.addOption(Option.builder(CONFIGTEMPLATE)
            .longOpt(CONFIGTEMPLATE_LONG)
            .argName("config.ftl")
            .hasArg()
            .desc("Optional template to configure the integration of handwritten templates.")
            .build());

    // developer level logging
    options.addOption(Option.builder(DEV)
            .longOpt(DEV_LONG)
            .desc("Specifies whether developer level logging should be used (default is false)")
            .build());

    // change logback conf
    options.addOption(Option.builder(CUSTOMLOG)
            .longOpt(CUSTOMLOG_LONG)
            .argName("file.xml")
            .hasArg()
            .desc("Changes the logback configuration to a customized file.")
            .build());

    // specify report path
    options.addOption(Option.builder(REPORT)
            .longOpt(REPORT_LONG)
            .argName("path")
            .hasArg(true)
            .desc("Specifies the directory for printing reports based on the given MontiCore grammars.")
            .build());

    // help dialog
    options.addOption(Option.builder(HELP)
            .longOpt(HELP_LONG)
            .desc("Prints this help dialog")
            .build());

    return options;
  }
}
