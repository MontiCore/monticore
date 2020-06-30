/* (c) https://github.com/MontiCore/monticore */

package de.monticore;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.ASTCDDefinition;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.cd.cd4analysis._symboltable.CD4AnalysisGlobalScope;
import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar_withconcepts._symboltable.Grammar_WithConceptsGlobalScope;
import de.monticore.io.paths.IterablePath;
import de.monticore.io.paths.ModelPath;
import de.se_rwth.commons.cli.CLIArguments;
import de.se_rwth.commons.configuration.ConfigurationPropertiesMapContributor;
import de.se_rwth.commons.logging.Log;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

  private static ModelPath modelPath = new ModelPath(modelPathPath, outputPath.toPath());

  private static IterablePath targetPath = IterablePath
      .from(new File("src/test/resources"), "java");

  private static IterablePath templatePath = IterablePath
      .from(new File("src/test/resources"), "ftl");

  static String[] simpleArgs = {"-grammars",
      "src/test/resources/de/monticore/statechart/Statechart.mc4",
      "src/test/resources/mc/grammars/lexicals/TestLexicals.mc4",
      "-modelPath", modelPathPath.toAbsolutePath().toString(),
      "-out", outputPath.getAbsolutePath(), "-targetPath", "src/test/resources", "-force"};

  @BeforeClass
  public static void setup() {
    Log.init();
    Log.enableFailQuick(false);
    additionalMethods.add("deepEquals");
    additionalMethods.add("deepEqualsWithComments");
    additionalMethods.add("equalAttributes");
    additionalMethods.add("deepClone");
    additionalMethods.add("_construct");
    additionalMethods.add("get_Children");
    additionalMethods.add("accept");
  }

  @Before
  public void init() {
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
   * {@link MontiCoreScript#generateParser(GlobalExtensionManagement, ASTMCGrammar, Grammar_WithConceptsGlobalScope, IterablePath, File)}
   */
  @Test
  public void testGenerateParser() {
    assertNotNull(grammar);
    MontiCoreScript mc = new MontiCoreScript();
    Grammar_WithConceptsGlobalScope symbolTable = TestHelper.createGlobalScope(modelPath);
    mc.generateParser(glex, grammar, symbolTable, IterablePath.empty(), new File("target/generated-sources/monticore/testcode"));
  }

  @Test
  public void testGetOrCreateCD() {
    MontiCoreScript mc = new MontiCoreScript();
    Grammar_WithConceptsGlobalScope symbolTable = TestHelper.createGlobalScope(modelPath);
    mc.createSymbolsFromAST(symbolTable, grammar);
    CD4AnalysisGlobalScope cd4AGlobalScope = mc.createCD4AGlobalScope(modelPath);
    cdCompilationUnit = mc.getOrCreateCD(grammar, new GlobalExtensionManagement(), cd4AGlobalScope);
    assertNotNull(cdCompilationUnit);
    assertNotNull(cdCompilationUnit.getCDDefinition());
    assertEquals("Statechart", cdCompilationUnit.getCDDefinition().getName());
  }

  @Test
  public void testDeriveCD() {
    MontiCoreScript mc = new MontiCoreScript();
    Grammar_WithConceptsGlobalScope symbolTable = TestHelper.createGlobalScope(modelPath);
    mc.createSymbolsFromAST(symbolTable, grammar);
    CD4AnalysisGlobalScope cd4AGlobalScope = mc.createCD4AGlobalScope(modelPath);
    cdCompilationUnit = mc.deriveCD(grammar, new GlobalExtensionManagement(), cd4AGlobalScope);
    assertNotNull(cdCompilationUnit);
    assertNotNull(cdCompilationUnit.getCDDefinition());
    assertEquals("Statechart", cdCompilationUnit.getCDDefinition().getName());
  }

  @Test
  public void testDecorateCd() {
    MontiCoreScript mc = new MontiCoreScript();
    Grammar_WithConceptsGlobalScope symbolTable = TestHelper.createGlobalScope(modelPath);
    mc.createSymbolsFromAST(symbolTable, grammar);
    CD4AnalysisGlobalScope cd4AGlobalScope = mc.createCD4AGlobalScope(modelPath);
    cdCompilationUnit = mc.deriveCD(grammar, new GlobalExtensionManagement(), cd4AGlobalScope);
    assertNotNull(cdCompilationUnit);
    assertEquals("de.monticore.statechart", String.join(".", cdCompilationUnit.getPackageList()));
    assertNotNull(cdCompilationUnit.getCDDefinition());
    ASTCDDefinition cdDefinition = cdCompilationUnit.getCDDefinition();
    assertEquals(8, cdDefinition.getCDClassList().size());
    assertEquals(5, cdDefinition.getCDInterfaceList().size());

    ASTCDCompilationUnit astcdCompilationUnit = mc.decorateForASTPackage(glex, cd4AGlobalScope, cdCompilationUnit, targetPath);
    // Added Builder classes to the each not list class
    assertEquals(18, astcdCompilationUnit.getCDDefinition().getCDClassList().size());

    // Check if there are all additional methods defined in the given CD class
    List<String> methods = Lists.newArrayList();
    for (ASTCDClass cdClass : astcdCompilationUnit.getCDDefinition().getCDClassList()) {
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
    Assert.assertTrue(Log.getFindings().isEmpty());
  }

  static String[] subsubgrammarArgs = {"-grammars",
      "src/test/resources/de/monticore/inherited/subsub/Subsubgrammar.mc4",
      "-modelPath", modelPathPath.toAbsolutePath().toString(),
      "-out", outputPath.getAbsolutePath(), "-targetPath", "src/test/resources", "-force"};

  @Test
  public void testDefaultScriptSubsubgrammarArgs() {
    Log.getFindings().clear();
    testDefaultScript(subsubgrammarArgs);
    testDefaultScriptWithEmf(subsubgrammarArgs);
    Assert.assertTrue(Log.getFindings().isEmpty());
  }

  static String[] inheritedgrammarArgs = {"-grammars",
      "src/test/resources/de/monticore/inherited/Supergrammar.mc4",
      "src/test/resources/de/monticore/inherited/sub/Subgrammar.mc4",
      "src/test/resources/de/monticore/inherited/subsub/Subsubgrammar.mc4",
      "-modelPath", modelPathPath.toAbsolutePath().toString(),
      "-out", outputPath.getAbsolutePath(), "-targetPath", "src/test/resources", "-force"};

  @Test
  public void testDefaultScriptSupergrammarArgs() {
    Log.getFindings().clear();
    testDefaultScript(inheritedgrammarArgs);
    testDefaultScriptWithEmf(inheritedgrammarArgs);
    assertEquals(Log.getErrorCount(), 0);
  }

  static String[] supersubgrammarArgs = {"-grammars",
      "src/test/resources/de/monticore/inherited/subsub/Subsubgrammar.mc4",
      "src/test/resources/de/monticore/inherited/Supergrammar.mc4",
      "-modelPath", modelPathPath.toAbsolutePath().toString(),
      "-out", outputPath.getAbsolutePath(), "-targetPath", "src/test/resources", "-force"};

  @Test
  public void testDefaultScriptSupersubgrammarArgs() {
    Log.getFindings().clear();
    testDefaultScript(supersubgrammarArgs);
    testDefaultScriptWithEmf(supersubgrammarArgs);
    Assert.assertTrue(Log.getFindings().isEmpty());
  }

  private void testDefaultScript(String[] args) {
    ConfigurationPropertiesMapContributor configuration = ConfigurationPropertiesMapContributor
        .fromSplitMap(CLIArguments.forArguments(args).asMap());
    MontiCoreConfiguration cfg = MontiCoreConfiguration.withConfiguration(configuration);
    new MontiCoreScript().run(cfg);
    // Reporting is enabled in the monticore_noemf.groovy script but needs to be disabled for other tests
    // because Reporting is static directly disable it again here
    Reporting.off();
    assertTrue(!false);
  }

  private void testDefaultScriptWithEmf(String[] args) {
    ConfigurationPropertiesMapContributor configuration = ConfigurationPropertiesMapContributor
        .fromSplitMap(CLIArguments.forArguments(args).asMap());
    MontiCoreConfiguration cfg = MontiCoreConfiguration.withConfiguration(configuration);
    new MontiCoreScript().run_emf(cfg);
    // Reporting is enabled in the monticore_noemf.groovy script but needs to be disabled for other tests
    // because Reporting is static directly disable it again here
    Reporting.off();
    assertTrue(!false);
  }

  @Test
  public void testDeriveSymbolCD() {
    MontiCoreScript mc = new MontiCoreScript();
    Grammar_WithConceptsGlobalScope symbolTable = TestHelper.createGlobalScope(modelPath);
    mc.createSymbolsFromAST(symbolTable, grammar);
    CD4AnalysisGlobalScope cd4AGlobalScope = mc.createCD4AGlobalScope(modelPath);
    ASTCDCompilationUnit cdCompilationUnit = mc.deriveSymbolCD(grammar, cd4AGlobalScope);
    // check directly created scope
    assertNotNull(cdCompilationUnit);
    assertNotNull(cdCompilationUnit.getCDDefinition());
    assertEquals("Statechart", cdCompilationUnit.getCDDefinition().getName());
    // no symbol defined
    assertEquals(0, cdCompilationUnit.getCDDefinition().sizeCDClasss());

    // check saved cd for grammar
    ASTCDCompilationUnit symbolCDOfParsedGrammar = mc.getSymbolCDOfParsedGrammar(grammar);
    assertNotNull(symbolCDOfParsedGrammar);
    assertNotNull(symbolCDOfParsedGrammar.getCDDefinition());
    assertEquals("Statechart", symbolCDOfParsedGrammar.getCDDefinition().getName());
    // no symbol defined
    assertEquals(0, symbolCDOfParsedGrammar.getCDDefinition().sizeCDClasss());
  }

  @Test
  public void testDeriveScopeCD() {
    MontiCoreScript mc = new MontiCoreScript();
    Grammar_WithConceptsGlobalScope symbolTable = TestHelper.createGlobalScope(modelPath);
    mc.createSymbolsFromAST(symbolTable, grammar);
    CD4AnalysisGlobalScope cd4AGlobalScope = mc.createCD4AGlobalScope(modelPath);
    ASTCDCompilationUnit cdCompilationUnit = mc.deriveScopeCD(grammar, cd4AGlobalScope);
    // test normal created scope cd
    assertNotNull(cdCompilationUnit);
    assertNotNull(cdCompilationUnit.getCDDefinition());
    assertEquals("Statechart", cdCompilationUnit.getCDDefinition().getName());
    assertEquals(1, cdCompilationUnit.getCDDefinition().sizeCDClasss());
    assertEquals("Statechart", cdCompilationUnit.getCDDefinition().getCDClass(0).getName());

    // test correct saved scope cd
    ASTCDCompilationUnit scopeCDOfParsedGrammar = mc.getScopeCDOfParsedGrammar(grammar);
    assertNotNull(scopeCDOfParsedGrammar);
    assertNotNull(scopeCDOfParsedGrammar.getCDDefinition());
    assertEquals("Statechart", scopeCDOfParsedGrammar.getCDDefinition().getName());
    assertEquals(1, scopeCDOfParsedGrammar.getCDDefinition().sizeCDClasss());
    assertEquals("Statechart", scopeCDOfParsedGrammar.getCDDefinition().getCDClass(0).getName());
  }

  @Test
  public void testAddListSuffixToAttributeName() {
    MontiCoreScript mc = new MontiCoreScript();
    Grammar_WithConceptsGlobalScope symbolTable = TestHelper.createGlobalScope(modelPath);
    mc.createSymbolsFromAST(symbolTable, grammar);
    CD4AnalysisGlobalScope cd4AGlobalScope = mc.createCD4AGlobalScope(modelPath);
    ASTCDCompilationUnit cdCompilationUnit = mc.deriveCD(grammar, new GlobalExtensionManagement(), cd4AGlobalScope);
    assertNotNull(cdCompilationUnit);
    assertNotNull(cdCompilationUnit.getCDDefinition());
    assertEquals("Statechart", cdCompilationUnit.getCDDefinition().getName());
    ASTCDClass stateChartClass = cdCompilationUnit.getCDDefinition().getCDClass(0);
    assertEquals("ASTStatechart", stateChartClass.getName());
    assertEquals("state", stateChartClass.getCDAttributeList().get(1).getName());

    // add list suffix
    ASTCDCompilationUnit listSuffixCD = mc.addListSuffixToAttributeName(cdCompilationUnit);

    assertNotNull(listSuffixCD);
    assertNotNull(listSuffixCD.getCDDefinition());
    assertEquals("Statechart", listSuffixCD.getCDDefinition().getName());
    ASTCDClass listSuffixStateChartClass = listSuffixCD.getCDDefinition().getCDClass(0);
    assertEquals("ASTStatechart", listSuffixStateChartClass.getName());
    assertDeepEquals("java.util.List<de.monticore.statechart.Statechart.ASTState>", listSuffixStateChartClass.getCDAttributeList().get(1).getMCType());
    // attribute with 's' at the end now
    assertEquals("states", listSuffixStateChartClass.getCDAttributeList().get(1).getName());
  }

  @Test
  public void testDecorateForSymbolTablePackage() {
    MontiCoreScript mc = new MontiCoreScript();
    GlobalExtensionManagement glex = new GlobalExtensionManagement();
    Grammar_WithConceptsGlobalScope symbolTable = TestHelper.createGlobalScope(modelPath);
    mc.createSymbolsFromAST(symbolTable, grammar);
    CD4AnalysisGlobalScope cd4AGlobalScope = mc.createCD4AGlobalScope(modelPath);
    CD4AnalysisGlobalScope cd4AGlobalScopeSymbolCD = mc.createCD4AGlobalScope(modelPath);
    CD4AnalysisGlobalScope cd4AGlobalScopeScopeCD = mc.createCD4AGlobalScope(modelPath);

    ASTCDCompilationUnit symbolCD = mc.deriveSymbolCD(grammar, cd4AGlobalScopeSymbolCD);
    ASTCDCompilationUnit scopeCD = mc.deriveScopeCD(grammar, cd4AGlobalScopeScopeCD);
    ASTCDCompilationUnit cd = mc.deriveCD(grammar, glex, cd4AGlobalScope);

    IterablePath handcodedPath = IterablePath.from(new File("src/test/resources"), "java");
    ASTCDCompilationUnit symbolPackageCD = mc.decorateForSymbolTablePackage(glex, cd4AGlobalScope, cd, symbolCD, scopeCD, handcodedPath);
    assertNotNull(symbolPackageCD);
    assertNotNull(symbolPackageCD.getCDDefinition());
    assertEquals("Statechart", symbolPackageCD.getCDDefinition().getName());

    int index = 0;
    assertEquals(14, symbolPackageCD.getCDDefinition().sizeCDClasss());
    assertEquals("StatechartScope", symbolPackageCD.getCDDefinition().getCDClass(index++).getName());
    assertEquals("StatechartScopeBuilder", symbolPackageCD.getCDDefinition().getCDClass(index++).getName());
    assertEquals("StatechartSymbolTablePrinter", symbolPackageCD.getCDDefinition().getCDClass(index++).getName());
    assertEquals("StatechartSymbolTableCreatorDelegator", symbolPackageCD.getCDDefinition().getCDClass(index++).getName());
    assertEquals("StatechartSymbolTableCreatorDelegatorBuilder", symbolPackageCD.getCDDefinition().getCDClass(index++).getName());
    assertEquals("StatechartGlobalScope", symbolPackageCD.getCDDefinition().getCDClass(index++).getName());
    assertEquals("StatechartGlobalScopeBuilder", symbolPackageCD.getCDDefinition().getCDClass(index++).getName());
    assertEquals("StatechartArtifactScope", symbolPackageCD.getCDDefinition().getCDClass(index++).getName());
    assertEquals("StatechartArtifactScopeBuilder", symbolPackageCD.getCDDefinition().getCDClass(index++).getName());
    assertEquals("StatechartScopeDeSer", symbolPackageCD.getCDDefinition().getCDClass(index++).getName());
    assertEquals("StatechartModelLoader", symbolPackageCD.getCDDefinition().getCDClass(index++).getName());
    assertEquals("StatechartModelLoaderBuilder", symbolPackageCD.getCDDefinition().getCDClass(index++).getName());
    assertEquals("StatechartSymbolTableCreator", symbolPackageCD.getCDDefinition().getCDClass(index++).getName());
    assertEquals("StatechartSymbolTableCreatorBuilder", symbolPackageCD.getCDDefinition().getCDClass(index++).getName());

    index = 0;
    assertEquals(3, symbolPackageCD.getCDDefinition().sizeCDInterfaces());
    assertEquals("IStatechartScope", symbolPackageCD.getCDDefinition().getCDInterface(index++).getName());
    assertEquals("ICommonStatechartSymbol", symbolPackageCD.getCDDefinition().getCDInterface(index++).getName());
    assertEquals("IStatechartGlobalScope", symbolPackageCD.getCDDefinition().getCDInterface(index++).getName());
  }

  @Test
  public void testDecorateForVisitorPackage() {
    MontiCoreScript mc = new MontiCoreScript();
    GlobalExtensionManagement glex = new GlobalExtensionManagement();
    Grammar_WithConceptsGlobalScope symbolTable = TestHelper.createGlobalScope(modelPath);
    mc.createSymbolsFromAST(symbolTable, grammar);
    CD4AnalysisGlobalScope cd4AGlobalScope = mc.createCD4AGlobalScope(modelPath);
    ASTCDCompilationUnit cd = mc.deriveCD(grammar, glex, cd4AGlobalScope);
    IterablePath handcodedPath = IterablePath.from(new File("src/test/resources"), "java");

    ASTCDCompilationUnit visitorPackageCD = mc.decorateForVisitorPackage(glex, cd4AGlobalScope, cd, handcodedPath);

    assertNotNull(visitorPackageCD);
    assertNotNull(visitorPackageCD.getCDDefinition());
    assertEquals("Statechart", visitorPackageCD.getCDDefinition().getName());
    assertEquals(3, visitorPackageCD.getCDDefinition().sizeCDClasss());
    assertEquals("StatechartDelegatorVisitor", visitorPackageCD.getCDDefinition().getCDClass(0).getName());
    assertEquals("StatechartParentAwareVisitor", visitorPackageCD.getCDDefinition().getCDClass(1).getName());
    assertEquals(2, visitorPackageCD.getCDDefinition().sizeCDInterfaces());
    assertEquals("StatechartVisitor", visitorPackageCD.getCDDefinition().getCDInterface(0).getName());
    assertEquals("StatechartInheritanceVisitor", visitorPackageCD.getCDDefinition().getCDInterface(1).getName());
  }

  @Test
  public void testDecorateForCoCoPackage() {
    MontiCoreScript mc = new MontiCoreScript();
    GlobalExtensionManagement glex = new GlobalExtensionManagement();
    Grammar_WithConceptsGlobalScope symbolTable = TestHelper.createGlobalScope(modelPath);
    mc.createSymbolsFromAST(symbolTable, grammar);
    CD4AnalysisGlobalScope cd4AGlobalScope = mc.createCD4AGlobalScope(modelPath);
    ASTCDCompilationUnit cd = mc.deriveCD(grammar, glex, cd4AGlobalScope);
    IterablePath handcodedPath = IterablePath.from(new File("src/test/resources"), "java");

    ASTCDCompilationUnit cocoPackageCD = mc.decorateForCoCoPackage(glex, cd4AGlobalScope, cd, handcodedPath);

    assertNotNull(cocoPackageCD);
    assertNotNull(cocoPackageCD.getCDDefinition());
    assertEquals("Statechart", cocoPackageCD.getCDDefinition().getName());
    assertEquals(1, cocoPackageCD.getCDDefinition().sizeCDClasss());
    assertEquals("StatechartCoCoChecker", cocoPackageCD.getCDDefinition().getCDClass(0).getName());
    assertEquals(13, cocoPackageCD.getCDDefinition().sizeCDInterfaces());
    assertEquals("StatechartASTStatechartCoCo", cocoPackageCD.getCDDefinition().getCDInterface(0).getName());
    assertEquals("StatechartASTEntryActionCoCo", cocoPackageCD.getCDDefinition().getCDInterface(1).getName());
    assertEquals("StatechartASTExitActionCoCo", cocoPackageCD.getCDDefinition().getCDInterface(2).getName());
    assertEquals("StatechartASTStateCoCo", cocoPackageCD.getCDDefinition().getCDInterface(3).getName());
    assertEquals("StatechartASTTransitionCoCo", cocoPackageCD.getCDDefinition().getCDInterface(4).getName());
    assertEquals("StatechartASTArgumentCoCo", cocoPackageCD.getCDDefinition().getCDInterface(5).getName());
    assertEquals("StatechartASTCodeCoCo", cocoPackageCD.getCDDefinition().getCDInterface(6).getName());
    assertEquals("StatechartASTAbstractAnythingCoCo", cocoPackageCD.getCDDefinition().getCDInterface(7).getName());
    assertEquals("StatechartASTSCStructureCoCo", cocoPackageCD.getCDDefinition().getCDInterface(8).getName());
    assertEquals("StatechartASTBlockStatementExtCoCo", cocoPackageCD.getCDDefinition().getCDInterface(9).getName());
    assertEquals("StatechartASTExpressionExtCoCo", cocoPackageCD.getCDDefinition().getCDInterface(10).getName());
    assertEquals("StatechartASTClassbodyExtCoCo", cocoPackageCD.getCDDefinition().getCDInterface(11).getName());
    assertEquals("StatechartASTStatechartNodeCoCo", cocoPackageCD.getCDDefinition().getCDInterface(12).getName());
  }

  @Test
  public void testDecorateForASTPackage() {
    MontiCoreScript mc = new MontiCoreScript();
    GlobalExtensionManagement glex = new GlobalExtensionManagement();
    Grammar_WithConceptsGlobalScope symbolTable = TestHelper.createGlobalScope(modelPath);
    mc.createSymbolsFromAST(symbolTable, grammar);
    CD4AnalysisGlobalScope cd4AGlobalScope = mc.createCD4AGlobalScope(modelPath);
    ASTCDCompilationUnit cd = mc.deriveCD(grammar, glex, cd4AGlobalScope);
    IterablePath handcodedPath = IterablePath.from(new File("src/test/resources"), "java");

    ASTCDCompilationUnit astPackageCD = mc.decorateForASTPackage(glex, cd4AGlobalScope, cd, handcodedPath);

    assertNotNull(astPackageCD);
    assertNotNull(astPackageCD.getCDDefinition());
    assertEquals("Statechart", astPackageCD.getCDDefinition().getName());
    assertEquals(18, astPackageCD.getCDDefinition().sizeCDClasss());
    assertEquals("ASTStatechart", astPackageCD.getCDDefinition().getCDClass(0).getName());
    assertEquals("ASTEntryAction", astPackageCD.getCDDefinition().getCDClass(1).getName());
    assertEquals("ASTExitAction", astPackageCD.getCDDefinition().getCDClass(2).getName());
    assertEquals("ASTState", astPackageCD.getCDDefinition().getCDClass(3).getName());
    assertEquals("ASTTransition", astPackageCD.getCDDefinition().getCDClass(4).getName());
    assertEquals("ASTArgument", astPackageCD.getCDDefinition().getCDClass(5).getName());
    assertEquals("ASTCode", astPackageCD.getCDDefinition().getCDClass(6).getName());
    assertEquals("ASTAbstractAnything", astPackageCD.getCDDefinition().getCDClass(7).getName());
    assertEquals("ASTStatechartBuilder", astPackageCD.getCDDefinition().getCDClass(8).getName());
    assertEquals("ASTEntryActionBuilder", astPackageCD.getCDDefinition().getCDClass(9).getName());
    assertEquals("ASTExitActionBuilder", astPackageCD.getCDDefinition().getCDClass(10).getName());
    assertEquals("ASTStateBuilder", astPackageCD.getCDDefinition().getCDClass(11).getName());
    assertEquals("ASTTransitionBuilder", astPackageCD.getCDDefinition().getCDClass(12).getName());
    assertEquals("ASTArgumentBuilder", astPackageCD.getCDDefinition().getCDClass(13).getName());
    assertEquals("ASTCodeBuilder", astPackageCD.getCDDefinition().getCDClass(14).getName());
    assertEquals("ASTAbstractAnythingBuilder", astPackageCD.getCDDefinition().getCDClass(15).getName());
    assertEquals("StatechartNodeFactory", astPackageCD.getCDDefinition().getCDClass(16).getName());
    assertEquals("ASTConstantsStatechart", astPackageCD.getCDDefinition().getCDClass(17).getName());

    assertEquals(5, astPackageCD.getCDDefinition().sizeCDInterfaces());
    assertEquals("ASTSCStructure", astPackageCD.getCDDefinition().getCDInterface(0).getName());
    assertEquals("ASTBlockStatementExt", astPackageCD.getCDDefinition().getCDInterface(1).getName());
    assertEquals("ASTExpressionExt", astPackageCD.getCDDefinition().getCDInterface(2).getName());
    assertEquals("ASTClassbodyExt", astPackageCD.getCDDefinition().getCDInterface(3).getName());
    assertEquals("ASTStatechartNode", astPackageCD.getCDDefinition().getCDInterface(4).getName());
    assertEquals(1, astPackageCD.getCDDefinition().sizeCDEnums());
    assertEquals("StatechartLiterals", astPackageCD.getCDDefinition().getCDEnum(0).getName());
  }

  @Test
  public void testDecorateForEmfASTPackage() {
    MontiCoreScript mc = new MontiCoreScript();
    GlobalExtensionManagement glex = new GlobalExtensionManagement();
    Grammar_WithConceptsGlobalScope symbolTable = TestHelper.createGlobalScope(modelPath);
    mc.createSymbolsFromAST(symbolTable, grammar);
    CD4AnalysisGlobalScope cd4AGlobalScope = mc.createCD4AGlobalScope(modelPath);
    ASTCDCompilationUnit cd = mc.deriveCD(grammar, glex, cd4AGlobalScope);
    IterablePath handcodedPath = IterablePath.from(new File("src/test/resources"), "java");

    ASTCDCompilationUnit astEmfPackageCD = mc.decorateEmfForASTPackage(glex, cd4AGlobalScope, cd, handcodedPath);

    assertNotNull(astEmfPackageCD);
    assertNotNull(astEmfPackageCD.getCDDefinition());
    assertEquals("Statechart", astEmfPackageCD.getCDDefinition().getName());
    assertEquals(19, astEmfPackageCD.getCDDefinition().sizeCDClasss());
    assertEquals("ASTStatechart", astEmfPackageCD.getCDDefinition().getCDClass(0).getName());
    assertEquals("ASTEntryAction", astEmfPackageCD.getCDDefinition().getCDClass(1).getName());
    assertEquals("ASTExitAction", astEmfPackageCD.getCDDefinition().getCDClass(2).getName());
    assertEquals("ASTState", astEmfPackageCD.getCDDefinition().getCDClass(3).getName());
    assertEquals("ASTTransition", astEmfPackageCD.getCDDefinition().getCDClass(4).getName());
    assertEquals("ASTArgument", astEmfPackageCD.getCDDefinition().getCDClass(5).getName());
    assertEquals("ASTCode", astEmfPackageCD.getCDDefinition().getCDClass(6).getName());
    assertEquals("ASTAbstractAnything", astEmfPackageCD.getCDDefinition().getCDClass(7).getName());
    assertEquals("ASTStatechartBuilder", astEmfPackageCD.getCDDefinition().getCDClass(8).getName());
    assertEquals("ASTEntryActionBuilder", astEmfPackageCD.getCDDefinition().getCDClass(9).getName());
    assertEquals("ASTExitActionBuilder", astEmfPackageCD.getCDDefinition().getCDClass(10).getName());
    assertEquals("ASTStateBuilder", astEmfPackageCD.getCDDefinition().getCDClass(11).getName());
    assertEquals("ASTTransitionBuilder", astEmfPackageCD.getCDDefinition().getCDClass(12).getName());
    assertEquals("ASTArgumentBuilder", astEmfPackageCD.getCDDefinition().getCDClass(13).getName());
    assertEquals("ASTCodeBuilder", astEmfPackageCD.getCDDefinition().getCDClass(14).getName());
    assertEquals("ASTAbstractAnythingBuilder", astEmfPackageCD.getCDDefinition().getCDClass(15).getName());
    assertEquals("StatechartNodeFactory", astEmfPackageCD.getCDDefinition().getCDClass(16).getName());
    assertEquals("ASTConstantsStatechart", astEmfPackageCD.getCDDefinition().getCDClass(17).getName());
    assertEquals("StatechartPackageImpl", astEmfPackageCD.getCDDefinition().getCDClass(18).getName());

    assertEquals(6, astEmfPackageCD.getCDDefinition().sizeCDInterfaces());
    assertEquals("ASTSCStructure", astEmfPackageCD.getCDDefinition().getCDInterface(0).getName());
    assertEquals("ASTBlockStatementExt", astEmfPackageCD.getCDDefinition().getCDInterface(1).getName());
    assertEquals("ASTExpressionExt", astEmfPackageCD.getCDDefinition().getCDInterface(2).getName());
    assertEquals("ASTClassbodyExt", astEmfPackageCD.getCDDefinition().getCDInterface(3).getName());
    assertEquals("ASTStatechartNode", astEmfPackageCD.getCDDefinition().getCDInterface(4).getName());
    assertEquals("StatechartPackage", astEmfPackageCD.getCDDefinition().getCDInterface(5).getName());

    assertEquals(1, astEmfPackageCD.getCDDefinition().sizeCDEnums());
    assertEquals("StatechartLiterals", astEmfPackageCD.getCDDefinition().getCDEnum(0).getName());
  }

  @Test
  public void testDecorateForODPackage() {
    MontiCoreScript mc = new MontiCoreScript();
    GlobalExtensionManagement glex = new GlobalExtensionManagement();
    Grammar_WithConceptsGlobalScope symbolTable = TestHelper.createGlobalScope(modelPath);
    mc.createSymbolsFromAST(symbolTable, grammar);
    CD4AnalysisGlobalScope cd4AGlobalScope = mc.createCD4AGlobalScope(modelPath);

    ASTCDCompilationUnit cd = mc.deriveCD(grammar, glex, cd4AGlobalScope);
    IterablePath handcodedPath = IterablePath.from(new File("src/test/resources"), "java");

    ASTCDCompilationUnit odPackage = mc.decorateForODPackage(glex, cd4AGlobalScope, cd, handcodedPath);

    assertNotNull(odPackage);
    assertNotNull(odPackage.getCDDefinition());
    assertEquals("Statechart", odPackage.getCDDefinition().getName());
    assertEquals(1, odPackage.getCDDefinition().sizeCDClasss());
    assertEquals("Statechart2OD", odPackage.getCDDefinition().getCDClass(0).getName());
    assertTrue(odPackage.getCDDefinition().isEmptyCDInterfaces());
    assertTrue(odPackage.getCDDefinition().isEmptyCDEnums());
  }

  @Test
  public void testDecorateForMill() {
    MontiCoreScript mc = new MontiCoreScript();
    GlobalExtensionManagement glex = new GlobalExtensionManagement();
    Grammar_WithConceptsGlobalScope symbolTable = TestHelper.createGlobalScope(modelPath);
    mc.createSymbolsFromAST(symbolTable, grammar);
    CD4AnalysisGlobalScope cd4AGlobalScope = mc.createCD4AGlobalScope(modelPath);

    ASTCDCompilationUnit cd = mc.deriveCD(grammar, glex, cd4AGlobalScope);
    IterablePath handcodedPath = IterablePath.from(new File("src/test/resources"), "java");

    ASTCDCompilationUnit millCd = mc.decorateMill(glex, cd4AGlobalScope, cd, getASTCD(cd), getVisitorCD(cd), getSymbolCD(cd), handcodedPath);

    assertNotNull(millCd);
    assertNotNull(millCd.getCDDefinition());
    assertEquals(4, millCd.sizePackages());
    assertEquals("de", millCd.getPackage(0));
    assertEquals("monticore", millCd.getPackage(1));
    assertEquals("statechart", millCd.getPackage(2));
    assertEquals("statechart", millCd.getPackage(3));
    assertEquals("Statechart", millCd.getCDDefinition().getName());
    assertEquals(2, millCd.getCDDefinition().sizeCDClasss());
    assertEquals("StatechartMill", millCd.getCDDefinition().getCDClass(0).getName());
    assertEquals("TestLexicalsMillForStatechart", millCd.getCDDefinition().getCDClass(1).getName());
    assertTrue(millCd.getCDDefinition().isEmptyCDInterfaces());
    assertTrue(millCd.getCDDefinition().isEmptyCDEnums());
  }

  protected ASTCDCompilationUnit getVisitorCD(ASTCDCompilationUnit decoratedCompilationUnit) {
    MontiCoreScript mc = new MontiCoreScript();
    GlobalExtensionManagement glex = new GlobalExtensionManagement();
    Grammar_WithConceptsGlobalScope symbolTable = TestHelper.createGlobalScope(modelPath);
    mc.createSymbolsFromAST(symbolTable, grammar);
    CD4AnalysisGlobalScope cd4AGlobalScope = mc.createCD4AGlobalScope(modelPath);
    ASTCDCompilationUnit cd = mc.deriveCD(grammar, glex, cd4AGlobalScope);
    IterablePath handcodedPath = IterablePath.from(new File("src/test/resources"), "java");

    return mc.decorateForVisitorPackage(glex, cd4AGlobalScope, cd, handcodedPath);
  }

  protected ASTCDCompilationUnit getSymbolCD(ASTCDCompilationUnit decoratedCompilationUnit) {
    MontiCoreScript mc = new MontiCoreScript();
    GlobalExtensionManagement glex = new GlobalExtensionManagement();
    Grammar_WithConceptsGlobalScope symbolTable = TestHelper.createGlobalScope(modelPath);
    mc.createSymbolsFromAST(symbolTable, grammar);
    CD4AnalysisGlobalScope cd4AGlobalScope = mc.createCD4AGlobalScope(modelPath);
    CD4AnalysisGlobalScope cd4AGlobalScopeSymbolCD = mc.createCD4AGlobalScope(modelPath);
    CD4AnalysisGlobalScope cd4AGlobalScopeScopeCD = mc.createCD4AGlobalScope(modelPath);

    ASTCDCompilationUnit symbolCD = mc.deriveSymbolCD(grammar, cd4AGlobalScopeSymbolCD);
    ASTCDCompilationUnit scopeCD = mc.deriveScopeCD(grammar, cd4AGlobalScopeScopeCD);
    ASTCDCompilationUnit cd = mc.deriveCD(grammar, glex, cd4AGlobalScope);

    IterablePath handcodedPath = IterablePath.from(new File("src/test/resources"), "java");
    return mc.decorateForSymbolTablePackage(glex, cd4AGlobalScope, cd, symbolCD, scopeCD, handcodedPath);
  }

  protected ASTCDCompilationUnit getASTCD(ASTCDCompilationUnit decoratedCompilationUnit) {
    MontiCoreScript mc = new MontiCoreScript();
    GlobalExtensionManagement glex = new GlobalExtensionManagement();
    Grammar_WithConceptsGlobalScope symbolTable = TestHelper.createGlobalScope(modelPath);
    mc.createSymbolsFromAST(symbolTable, grammar);
    CD4AnalysisGlobalScope cd4AGlobalScope = mc.createCD4AGlobalScope(modelPath);
    ASTCDCompilationUnit cd = mc.deriveCD(grammar, glex, cd4AGlobalScope);
    IterablePath handcodedPath = IterablePath.from(new File("src/test/resources"), "java");

    return mc.decorateForASTPackage(glex, cd4AGlobalScope, cd, handcodedPath);
  }
}
