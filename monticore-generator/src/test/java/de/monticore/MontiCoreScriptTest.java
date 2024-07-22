/* (c) https://github.com/MontiCore/monticore */

package de.monticore;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4analysis._symboltable.ICD4AnalysisGlobalScope;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cli.MontiCoreTool;
import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar_withconcepts.Grammar_WithConceptsMill;
import de.monticore.grammar.grammar_withconcepts._symboltable.Grammar_WithConceptsGlobalScope;
import de.monticore.grammar.grammar_withconcepts._symboltable.IGrammar_WithConceptsGlobalScope;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.apache.commons.cli.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static de.monticore.MontiCoreConfiguration.*;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;

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

  @BeforeEach
  public void setup() {
    additionalMethods.add("deepEquals");
    additionalMethods.add("deepEqualsWithComments");
    additionalMethods.add("equalAttributes");
    additionalMethods.add("deepClone");
    additionalMethods.add("_construct");
    additionalMethods.add("accept");
  }

  @BeforeEach
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    
    Grammar_WithConceptsMill.reset();
    CD4CodeMill.reset();
    Grammar_WithConceptsMill.init();
    CD4CodeMill.init();
    glex = new GlobalExtensionManagement();
    CD4C.init(new GeneratorSetup());
    Optional<ASTMCGrammar> ast = new MontiCoreScript()
        .parseGrammar(Paths.get(new File(
            "src/test/resources/de/monticore/statechart/Statechart.mc4").getAbsolutePath()));
    Assertions.assertTrue(ast.isPresent());
    grammar = ast.get();
  }

  /**
   * {@link MontiCoreScript#parseGrammar(java.nio.file.Path)}
   */
  @Test
  public void testParseGrammar() {
    Assertions.assertNotNull(grammar);
    Assertions.assertEquals("Statechart", grammar.getName());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  /**
   * {@link MontiCoreScript#generateParser(GlobalExtensionManagement, ASTCDCompilationUnit, ASTMCGrammar, Grammar_WithConceptsGlobalScope, MCPath, MCPath, File)}
   */
  @Test
  public void testGenerateParser() {
    Assertions.assertNotNull(grammar);
    MontiCoreScript mc = new MontiCoreScript();
    IGrammar_WithConceptsGlobalScope symbolTable = TestHelper.createGlobalScope(modelPath);
    mc.createSymbolsFromAST(symbolTable, grammar);
    ICD4AnalysisGlobalScope cd4AGlobalScope = mc.createCD4AGlobalScope(modelPath);
    cdCompilationUnit = mc.deriveASTCD(grammar, glex, cd4AGlobalScope);
    File f = new File("target/generated-sources/monticore/testcode");
    mc.generateParser(glex, cdCompilationUnit, grammar, symbolTable, new MCPath(),
            new MCPath(), f);
    f.delete();
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetOrCreateCD() {
    MontiCoreScript mc = new MontiCoreScript();
    IGrammar_WithConceptsGlobalScope symbolTable = TestHelper.createGlobalScope(modelPath);
    mc.createSymbolsFromAST(symbolTable, grammar);
    ICD4AnalysisGlobalScope cd4AGlobalScope = mc.createCD4AGlobalScope(modelPath);
    cdCompilationUnit = mc.getOrCreateCD(grammar, new GlobalExtensionManagement(), cd4AGlobalScope);
    Assertions.assertNotNull(cdCompilationUnit);
    Assertions.assertNotNull(cdCompilationUnit.getCDDefinition());
    Assertions.assertEquals("Statechart", cdCompilationUnit.getCDDefinition().getName());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testDeriveASTCD() {
    MontiCoreScript mc = new MontiCoreScript();
    IGrammar_WithConceptsGlobalScope symbolTable = TestHelper.createGlobalScope(modelPath);
    mc.createSymbolsFromAST(symbolTable, grammar);
    ICD4AnalysisGlobalScope cd4AGlobalScope = mc.createCD4AGlobalScope(modelPath);
    cdCompilationUnit = mc.deriveASTCD(grammar, new GlobalExtensionManagement(), cd4AGlobalScope);
    Assertions.assertNotNull(cdCompilationUnit);
    Assertions.assertNotNull(cdCompilationUnit.getCDDefinition());
    Assertions.assertEquals("Statechart", cdCompilationUnit.getCDDefinition().getName());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testDecorateCd() {
    MontiCoreScript mc = new MontiCoreScript();
    IGrammar_WithConceptsGlobalScope symbolTable = TestHelper.createGlobalScope(modelPath);
    mc.createSymbolsFromAST(symbolTable, grammar);
    ICD4AnalysisGlobalScope cd4AGlobalScope = mc.createCD4AGlobalScope(modelPath);
    cdCompilationUnit = mc.deriveASTCD(grammar, new GlobalExtensionManagement(), cd4AGlobalScope);
    Assertions.assertNotNull(cdCompilationUnit);
    Assertions.assertEquals("de.monticore.statechart", String.join(".", cdCompilationUnit.getCDPackageList()));
    Assertions.assertNotNull(cdCompilationUnit.getCDDefinition());
    ASTCDDefinition cdDefinition = cdCompilationUnit.getCDDefinition();
    Assertions.assertEquals(8, cdDefinition.getCDClassesList().size());
    Assertions.assertEquals(5, cdDefinition.getCDInterfacesList().size());

    ASTCDCompilationUnit astcdCompilationUnit = mc.decorateForASTPackage(glex, cd4AGlobalScope, cdCompilationUnit, hwPath);
    // Added Builder classes to the each not list class
    Assertions.assertEquals(17, astcdCompilationUnit.getCDDefinition().getCDClassesList().size());

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
          Assertions.assertTrue(methods.contains(additionalMethod.substring(0,
              additionalMethod.indexOf(withOrder))));
        } else {
          Assertions.assertTrue(methods.contains(additionalMethod));
        }
      }
    }
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testDefaultScriptSimpleArgs() {
    Log.getFindings().clear();
    testDefaultScript(simpleArgs);
    Assertions.assertEquals(0, Log.getErrorCount());
    // test whether the original template is used
    try {
      List<String> lines = Files.readAllLines(Paths.get(outputPath.getAbsolutePath() + "/de/monticore/statechart/statechart/_ast/ASTState.java"));
      Assertions.assertFalse(lines.stream().filter(l -> "// Test:Replace Template".equals(l)).findAny().isPresent());
    } catch (Exception e) {
      Assertions.fail();
    }
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
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
    Assertions.assertEquals(0, Log.getErrorCount());
    // test whether the changed template is used
    try {
      List<String> lines = Files.readAllLines(Paths.get(outputPath.getAbsolutePath() + "/de/monticore/statechart/statechart/_ast/ASTState.java"));
      Assertions.assertTrue(lines.stream().filter(l -> "// Test:Replace Template".equals(l)).findAny().isPresent());
    } catch (Exception e) {
      Assertions.fail();
    }
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  static String[] subsubgrammarArgs = {"-" + GRAMMAR,
          "src/test/resources/de/monticore/inherited/subsub/Subsubgrammar.mc4",
          "-" + MODELPATH, modelPathPath.toAbsolutePath().toString(),
          "-" + OUT, outputPath.getAbsolutePath()};

  @Test
  public void testDefaultScriptSubsubgrammarArgs() {
    Log.getFindings().clear();
    testDefaultScript(subsubgrammarArgs);
    Assertions.assertEquals(0, Log.getErrorCount());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testDefaultScriptSubsubgrammarArgs_EMF() {
    Log.getFindings().clear();
    testDefaultScriptWithEmf(subsubgrammarArgs);
    Assertions.assertEquals(0, Log.getErrorCount());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
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
    Assertions.assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testDefaultScriptSupergrammarArgs_EMF() {
    Log.getFindings().clear();
    testDefaultScriptWithEmf(inheritedgrammarArgs);
    Assertions.assertEquals(0, Log.getErrorCount());
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
    Assertions.assertEquals(0, Log.getErrorCount());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testDefaultScriptSupersubgrammarArgs_EMF() {
    Log.getFindings().clear();
    testDefaultScriptWithEmf(supersubgrammarArgs);
    Assertions.assertEquals(0, Log.getErrorCount());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
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
    Assertions.assertTrue(!false);
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
    Assertions.assertTrue(!false);
  }

  @Test
  public void testDeriveSymbolCD() {
    MontiCoreScript mc = new MontiCoreScript();
    IGrammar_WithConceptsGlobalScope symbolTable = TestHelper.createGlobalScope(modelPath);
    mc.createSymbolsFromAST(symbolTable, grammar);
    ICD4AnalysisGlobalScope cd4AGlobalScope = mc.createCD4AGlobalScope(modelPath);
    ASTCDCompilationUnit cdCompilationUnit = mc.deriveSymbolCD(grammar, cd4AGlobalScope);
    // check directly created scope
    Assertions.assertNotNull(cdCompilationUnit);
    Assertions.assertNotNull(cdCompilationUnit.getCDDefinition());
    Assertions.assertEquals("StatechartSymbols", cdCompilationUnit.getCDDefinition().getName());
    // no symbol defined
    Assertions.assertEquals(0, cdCompilationUnit.getCDDefinition().getCDClassesList().size());

    // check saved cd for grammar
    ASTCDCompilationUnit symbolCDOfParsedGrammar = mc.getSymbolCDOfParsedGrammar(grammar);
    Assertions.assertNotNull(symbolCDOfParsedGrammar);
    Assertions.assertNotNull(symbolCDOfParsedGrammar.getCDDefinition());
    Assertions.assertEquals("StatechartSymbols", symbolCDOfParsedGrammar.getCDDefinition().getName());
    // no symbol defined
    Assertions.assertEquals(0, symbolCDOfParsedGrammar.getCDDefinition().getCDClassesList().size());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testDeriveScopeCD() {
    MontiCoreScript mc = new MontiCoreScript();
    IGrammar_WithConceptsGlobalScope symbolTable = TestHelper.createGlobalScope(modelPath);
    mc.createSymbolsFromAST(symbolTable, grammar);
    ICD4AnalysisGlobalScope cd4AGlobalScope = mc.createCD4AGlobalScope(modelPath);
    ASTCDCompilationUnit cdCompilationUnit = mc.deriveScopeCD(grammar, cd4AGlobalScope);
    // test normal created scope cd
    Assertions.assertNotNull(cdCompilationUnit);
    Assertions.assertNotNull(cdCompilationUnit.getCDDefinition());
    Assertions.assertEquals("StatechartScope", cdCompilationUnit.getCDDefinition().getName());
    Assertions.assertEquals(1, cdCompilationUnit.getCDDefinition().getCDClassesList().size());
    Assertions.assertEquals("Statechart", cdCompilationUnit.getCDDefinition().getCDClassesList().get(0).getName());

    // test correct saved scope cd
    ASTCDCompilationUnit scopeCDOfParsedGrammar = mc.getScopeCDOfParsedGrammar(grammar);
    Assertions.assertNotNull(scopeCDOfParsedGrammar);
    Assertions.assertNotNull(scopeCDOfParsedGrammar.getCDDefinition());
    Assertions.assertEquals("StatechartScope", scopeCDOfParsedGrammar.getCDDefinition().getName());
    Assertions.assertEquals(1, scopeCDOfParsedGrammar.getCDDefinition().getCDClassesList().size());
    Assertions.assertEquals("Statechart", scopeCDOfParsedGrammar.getCDDefinition().getCDClassesList().get(0).getName());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testAddListSuffixToAttributeName() {
    MontiCoreScript mc = new MontiCoreScript();
    IGrammar_WithConceptsGlobalScope symbolTable = TestHelper.createGlobalScope(modelPath);
    mc.createSymbolsFromAST(symbolTable, grammar);
    ICD4AnalysisGlobalScope cd4AGlobalScope = mc.createCD4AGlobalScope(modelPath);
    ASTCDCompilationUnit cdCompilationUnit = mc.deriveASTCD(grammar,
        new GlobalExtensionManagement(), cd4AGlobalScope);
    Assertions.assertNotNull(cdCompilationUnit);
    Assertions.assertNotNull(cdCompilationUnit.getCDDefinition());
    Assertions.assertEquals("Statechart", cdCompilationUnit.getCDDefinition().getName());
    ASTCDClass stateChartClass = cdCompilationUnit.getCDDefinition().getCDClassesList().get(0);
    Assertions.assertEquals("ASTStatechart", stateChartClass.getName());
    Assertions.assertEquals("state", stateChartClass.getCDAttributeList().get(1).getName());

    // add list suffix
    ASTCDCompilationUnit listSuffixCD = mc.addListSuffixToAttributeName(cdCompilationUnit);

    Assertions.assertNotNull(listSuffixCD);
    Assertions.assertNotNull(listSuffixCD.getCDDefinition());
    Assertions.assertEquals("Statechart", listSuffixCD.getCDDefinition().getName());
    ASTCDClass listSuffixStateChartClass = listSuffixCD.getCDDefinition().getCDClassesList().get(0);
    Assertions.assertEquals("ASTStatechart", listSuffixStateChartClass.getName());
    assertDeepEquals("java.util.List<de.monticore.statechart.Statechart.ASTState>", listSuffixStateChartClass.getCDAttributeList().get(1).getMCType());
    // attribute with 's' at the end now
    Assertions.assertEquals("states", listSuffixStateChartClass.getCDAttributeList().get(1).getName());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testDecorateForSymbolTablePackage() {
    MontiCoreScript mc = new MontiCoreScript();
    GlobalExtensionManagement glex = new GlobalExtensionManagement();
    IGrammar_WithConceptsGlobalScope symbolTable = TestHelper.createGlobalScope(modelPath);
    mc.createSymbolsFromAST(symbolTable, grammar);
    ICD4AnalysisGlobalScope cd4AGlobalScope = mc.createCD4AGlobalScope(modelPath);
    ICD4AnalysisGlobalScope cd4AGlobalScopeSymbolCD = mc.createCD4AGlobalScope(modelPath);
    ICD4AnalysisGlobalScope cd4AGlobalScopeScopeCD = mc.createCD4AGlobalScope(modelPath);

    ASTCDCompilationUnit symbolCD = mc.deriveSymbolCD(grammar, cd4AGlobalScopeSymbolCD);
    ASTCDCompilationUnit scopeCD = mc.deriveScopeCD(grammar, cd4AGlobalScopeScopeCD);
    ASTCDCompilationUnit cd = mc.deriveASTCD(grammar, glex, cd4AGlobalScope);
    ASTCDCompilationUnit symbolPackageCD = createEmptyCompilationUnit(cd);

    MCPath handcodedPath = new MCPath("src/test/resources");
    mc.decorateForSymbolTablePackage(glex, cd4AGlobalScope, cd, symbolCD, scopeCD, symbolPackageCD, handcodedPath);
    Assertions.assertNotNull(symbolPackageCD);
    Assertions.assertNotNull(symbolPackageCD.getCDDefinition());
    Assertions.assertEquals("Statechart", symbolPackageCD.getCDDefinition().getName());

    int index = 0;
    Assertions.assertEquals(7, symbolPackageCD.getCDDefinition().getCDClassesList().size());
    Assertions.assertEquals("StatechartScope", symbolPackageCD.getCDDefinition().getCDClassesList().get(index++).getName());
    Assertions.assertEquals("StatechartSymbols2Json", symbolPackageCD.getCDDefinition().getCDClassesList().get(index++).getName());
    Assertions.assertEquals("StatechartScopesGenitorDelegator", symbolPackageCD.getCDDefinition().getCDClassesList().get(index++).getName());
    Assertions.assertEquals("StatechartArtifactScope", symbolPackageCD.getCDDefinition().getCDClassesList().get(index++).getName());
    Assertions.assertEquals("StatechartDeSer", symbolPackageCD.getCDDefinition().getCDClassesList().get(index++).getName());
    Assertions.assertEquals("StatechartGlobalScope", symbolPackageCD.getCDDefinition().getCDClassesList().get(index++).getName());
    Assertions.assertEquals("StatechartScopesGenitor", symbolPackageCD.getCDDefinition().getCDClassesList().get(index++).getName());

    index = 0;
    Assertions.assertEquals(4, symbolPackageCD.getCDDefinition().getCDInterfacesList().size());
    Assertions.assertEquals("IStatechartScope", symbolPackageCD.getCDDefinition().getCDInterfacesList().get(index++).getName());
    Assertions.assertEquals("ICommonStatechartSymbol", symbolPackageCD.getCDDefinition().getCDInterfacesList().get(index++).getName());
    Assertions.assertEquals("IStatechartGlobalScope", symbolPackageCD.getCDDefinition().getCDInterfacesList().get(index++).getName());
    Assertions.assertEquals("IStatechartArtifactScope", symbolPackageCD.getCDDefinition().getCDInterfacesList().get(index++).getName());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testDecorateForVisitorPackage() {
    MontiCoreScript mc = new MontiCoreScript();
    GlobalExtensionManagement glex = new GlobalExtensionManagement();
    IGrammar_WithConceptsGlobalScope symbolTable = TestHelper.createGlobalScope(modelPath);
    mc.createSymbolsFromAST(symbolTable, grammar);
    ICD4AnalysisGlobalScope cd4AGlobalScope = mc.createCD4AGlobalScope(modelPath);
    ASTCDCompilationUnit cd = mc.deriveASTCD(grammar, glex, cd4AGlobalScope);
    MCPath handcodedPath = new MCPath("src/test/resources");

    ASTCDCompilationUnit visitorPackageCD = createEmptyCompilationUnit(cd);
    mc.configureGenerator(glex,visitorPackageCD, templatePath);
    mc.decorateTraverserForVisitorPackage(glex, cd4AGlobalScope, cd, visitorPackageCD, handcodedPath);

    Assertions.assertNotNull(visitorPackageCD);
    Assertions.assertNotNull(visitorPackageCD.getCDDefinition());
    Assertions.assertEquals("Statechart", visitorPackageCD.getCDDefinition().getName());
    Assertions.assertEquals(2, visitorPackageCD.getCDDefinition().getCDClassesList().size());
    Assertions.assertEquals("StatechartTraverserImplementation", visitorPackageCD.getCDDefinition().getCDClassesList().get(0).getName());
    Assertions.assertEquals("StatechartInheritanceHandler", visitorPackageCD.getCDDefinition().getCDClassesList().get(1).getName());
    Assertions.assertEquals(3, visitorPackageCD.getCDDefinition().getCDInterfacesList().size());
    Assertions.assertEquals("StatechartTraverser", visitorPackageCD.getCDDefinition().getCDInterfacesList().get(0).getName());
    Assertions.assertEquals("StatechartVisitor2", visitorPackageCD.getCDDefinition().getCDInterfacesList().get(1).getName());
    Assertions.assertEquals("StatechartHandler", visitorPackageCD.getCDDefinition().getCDInterfacesList().get(2).getName());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testDecorateForCoCoPackage() {
    MontiCoreScript mc = new MontiCoreScript();
    GlobalExtensionManagement glex = new GlobalExtensionManagement();
    IGrammar_WithConceptsGlobalScope symbolTable = TestHelper.createGlobalScope(modelPath);
    mc.createSymbolsFromAST(symbolTable, grammar);
    ICD4AnalysisGlobalScope cd4AGlobalScope = mc.createCD4AGlobalScope(modelPath);
    ASTCDCompilationUnit cd = mc.deriveASTCD(grammar, glex, cd4AGlobalScope);
    MCPath handcodedPath = new MCPath("src/test/resources");

    ASTCDCompilationUnit cocoPackageCD = createEmptyCompilationUnit(cd);
    mc.decorateForCoCoPackage(glex, cd4AGlobalScope, cd, cocoPackageCD, handcodedPath);

    Assertions.assertNotNull(cocoPackageCD);
    Assertions.assertNotNull(cocoPackageCD.getCDDefinition());
    Assertions.assertEquals("Statechart", cocoPackageCD.getCDDefinition().getName());
    Assertions.assertEquals(1, cocoPackageCD.getCDDefinition().getCDClassesList().size());
    Assertions.assertEquals("StatechartCoCoChecker", cocoPackageCD.getCDDefinition().getCDClassesList().get(0).getName());
    Assertions.assertEquals(13, cocoPackageCD.getCDDefinition().getCDInterfacesList().size());
    Assertions.assertEquals("StatechartASTStatechartCoCo", cocoPackageCD.getCDDefinition().getCDInterfacesList().get(0).getName());
    Assertions.assertEquals("StatechartASTEntryActionCoCo", cocoPackageCD.getCDDefinition().getCDInterfacesList().get(1).getName());
    Assertions.assertEquals("StatechartASTExitActionCoCo", cocoPackageCD.getCDDefinition().getCDInterfacesList().get(2).getName());
    Assertions.assertEquals("StatechartASTStateCoCo", cocoPackageCD.getCDDefinition().getCDInterfacesList().get(3).getName());
    Assertions.assertEquals("StatechartASTTransitionCoCo", cocoPackageCD.getCDDefinition().getCDInterfacesList().get(4).getName());
    Assertions.assertEquals("StatechartASTArgumentCoCo", cocoPackageCD.getCDDefinition().getCDInterfacesList().get(5).getName());
    Assertions.assertEquals("StatechartASTCodeCoCo", cocoPackageCD.getCDDefinition().getCDInterfacesList().get(6).getName());
    Assertions.assertEquals("StatechartASTAbstractAnythingCoCo", cocoPackageCD.getCDDefinition().getCDInterfacesList().get(7).getName());
    Assertions.assertEquals("StatechartASTSCStructureCoCo", cocoPackageCD.getCDDefinition().getCDInterfacesList().get(8).getName());
    Assertions.assertEquals("StatechartASTBlockStatementExtCoCo", cocoPackageCD.getCDDefinition().getCDInterfacesList().get(9).getName());
    Assertions.assertEquals("StatechartASTExpressionExtCoCo", cocoPackageCD.getCDDefinition().getCDInterfacesList().get(10).getName());
    Assertions.assertEquals("StatechartASTClassbodyExtCoCo", cocoPackageCD.getCDDefinition().getCDInterfacesList().get(11).getName());
    Assertions.assertEquals("StatechartASTStatechartNodeCoCo", cocoPackageCD.getCDDefinition().getCDInterfacesList().get(12).getName());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testDecorateForASTPackage() {
    MontiCoreScript mc = new MontiCoreScript();
    GlobalExtensionManagement glex = new GlobalExtensionManagement();
    IGrammar_WithConceptsGlobalScope symbolTable = TestHelper.createGlobalScope(modelPath);
    mc.createSymbolsFromAST(symbolTable, grammar);
    ICD4AnalysisGlobalScope cd4AGlobalScope = mc.createCD4AGlobalScope(modelPath);
    ASTCDCompilationUnit cd = mc.deriveASTCD(grammar, glex, cd4AGlobalScope);
    MCPath handcodedPath = new MCPath("src/test/resources");

    ASTCDCompilationUnit astPackageCD = mc.decorateForASTPackage(glex, cd4AGlobalScope, cd, handcodedPath);
    mc.configureGenerator(glex,astPackageCD, templatePath);

    Assertions.assertNotNull(astPackageCD);
    Assertions.assertNotNull(astPackageCD.getCDDefinition());
    Assertions.assertEquals("Statechart", astPackageCD.getCDDefinition().getName());
    Assertions.assertEquals(17, astPackageCD.getCDDefinition().getCDClassesList().size());
    Assertions.assertEquals("ASTStatechart", astPackageCD.getCDDefinition().getCDClassesList().get(0).getName());
    Assertions.assertEquals("ASTEntryAction", astPackageCD.getCDDefinition().getCDClassesList().get(1).getName());
    Assertions.assertEquals("ASTExitAction", astPackageCD.getCDDefinition().getCDClassesList().get(2).getName());
    Assertions.assertEquals("ASTState", astPackageCD.getCDDefinition().getCDClassesList().get(3).getName());
    Assertions.assertEquals("ASTTransition", astPackageCD.getCDDefinition().getCDClassesList().get(4).getName());
    Assertions.assertEquals("ASTArgument", astPackageCD.getCDDefinition().getCDClassesList().get(5).getName());
    Assertions.assertEquals("ASTCode", astPackageCD.getCDDefinition().getCDClassesList().get(6).getName());
    Assertions.assertEquals("ASTAbstractAnything", astPackageCD.getCDDefinition().getCDClassesList().get(7).getName());
    Assertions.assertEquals("ASTStatechartBuilder", astPackageCD.getCDDefinition().getCDClassesList().get(8).getName());
    Assertions.assertEquals("ASTEntryActionBuilder", astPackageCD.getCDDefinition().getCDClassesList().get(9).getName());
    Assertions.assertEquals("ASTExitActionBuilder", astPackageCD.getCDDefinition().getCDClassesList().get(10).getName());
    Assertions.assertEquals("ASTStateBuilder", astPackageCD.getCDDefinition().getCDClassesList().get(11).getName());
    Assertions.assertEquals("ASTTransitionBuilder", astPackageCD.getCDDefinition().getCDClassesList().get(12).getName());
    Assertions.assertEquals("ASTArgumentBuilder", astPackageCD.getCDDefinition().getCDClassesList().get(13).getName());
    Assertions.assertEquals("ASTCodeBuilder", astPackageCD.getCDDefinition().getCDClassesList().get(14).getName());
    Assertions.assertEquals("ASTAbstractAnythingBuilder", astPackageCD.getCDDefinition().getCDClassesList().get(15).getName());
    Assertions.assertEquals("ASTConstantsStatechart", astPackageCD.getCDDefinition().getCDClassesList().get(16).getName());

    Assertions.assertEquals(5, astPackageCD.getCDDefinition().getCDInterfacesList().size());
    Assertions.assertEquals("ASTSCStructure", astPackageCD.getCDDefinition().getCDInterfacesList().get(0).getName());
    Assertions.assertEquals("ASTBlockStatementExt", astPackageCD.getCDDefinition().getCDInterfacesList().get(1).getName());
    Assertions.assertEquals("ASTExpressionExt", astPackageCD.getCDDefinition().getCDInterfacesList().get(2).getName());
    Assertions.assertEquals("ASTClassbodyExt", astPackageCD.getCDDefinition().getCDInterfacesList().get(3).getName());
    Assertions.assertEquals("ASTStatechartNode", astPackageCD.getCDDefinition().getCDInterfacesList().get(4).getName());
    Assertions.assertEquals(1, astPackageCD.getCDDefinition().getCDEnumsList().size());
    Assertions.assertEquals("StatechartLiterals", astPackageCD.getCDDefinition().getCDEnumsList().get(0).getName());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testDecorateForEmfASTPackage() {
    MontiCoreScript mc = new MontiCoreScript();
    GlobalExtensionManagement glex = new GlobalExtensionManagement();
    IGrammar_WithConceptsGlobalScope symbolTable = TestHelper.createGlobalScope(modelPath);
    mc.createSymbolsFromAST(symbolTable, grammar);
    ICD4AnalysisGlobalScope cd4AGlobalScope = mc.createCD4AGlobalScope(modelPath);
    ASTCDCompilationUnit cd = mc.deriveASTCD(grammar, glex, cd4AGlobalScope);
    MCPath handcodedPath = new MCPath("src/test/resources");

    ASTCDCompilationUnit astEmfPackageCD = mc.decorateEmfForASTPackage(glex, cd4AGlobalScope, cd, handcodedPath);

    Assertions.assertNotNull(astEmfPackageCD);
    Assertions.assertNotNull(astEmfPackageCD.getCDDefinition());
    Assertions.assertEquals("Statechart", astEmfPackageCD.getCDDefinition().getName());
    Assertions.assertEquals(18, astEmfPackageCD.getCDDefinition().getCDClassesList().size());
    Assertions.assertEquals("ASTStatechart", astEmfPackageCD.getCDDefinition().getCDClassesList().get(0).getName());
    Assertions.assertEquals("ASTEntryAction", astEmfPackageCD.getCDDefinition().getCDClassesList().get(1).getName());
    Assertions.assertEquals("ASTExitAction", astEmfPackageCD.getCDDefinition().getCDClassesList().get(2).getName());
    Assertions.assertEquals("ASTState", astEmfPackageCD.getCDDefinition().getCDClassesList().get(3).getName());
    Assertions.assertEquals("ASTTransition", astEmfPackageCD.getCDDefinition().getCDClassesList().get(4).getName());
    Assertions.assertEquals("ASTArgument", astEmfPackageCD.getCDDefinition().getCDClassesList().get(5).getName());
    Assertions.assertEquals("ASTCode", astEmfPackageCD.getCDDefinition().getCDClassesList().get(6).getName());
    Assertions.assertEquals("ASTAbstractAnything", astEmfPackageCD.getCDDefinition().getCDClassesList().get(7).getName());
    Assertions.assertEquals("ASTStatechartBuilder", astEmfPackageCD.getCDDefinition().getCDClassesList().get(8).getName());
    Assertions.assertEquals("ASTEntryActionBuilder", astEmfPackageCD.getCDDefinition().getCDClassesList().get(9).getName());
    Assertions.assertEquals("ASTExitActionBuilder", astEmfPackageCD.getCDDefinition().getCDClassesList().get(10).getName());
    Assertions.assertEquals("ASTStateBuilder", astEmfPackageCD.getCDDefinition().getCDClassesList().get(11).getName());
    Assertions.assertEquals("ASTTransitionBuilder", astEmfPackageCD.getCDDefinition().getCDClassesList().get(12).getName());
    Assertions.assertEquals("ASTArgumentBuilder", astEmfPackageCD.getCDDefinition().getCDClassesList().get(13).getName());
    Assertions.assertEquals("ASTCodeBuilder", astEmfPackageCD.getCDDefinition().getCDClassesList().get(14).getName());
    Assertions.assertEquals("ASTAbstractAnythingBuilder", astEmfPackageCD.getCDDefinition().getCDClassesList().get(15).getName());
    Assertions.assertEquals("ASTConstantsStatechart", astEmfPackageCD.getCDDefinition().getCDClassesList().get(16).getName());
    Assertions.assertEquals("StatechartPackageImpl", astEmfPackageCD.getCDDefinition().getCDClassesList().get(17).getName());

    Assertions.assertEquals(6, astEmfPackageCD.getCDDefinition().getCDInterfacesList().size());
    Assertions.assertEquals("ASTSCStructure", astEmfPackageCD.getCDDefinition().getCDInterfacesList().get(0).getName());
    Assertions.assertEquals("ASTBlockStatementExt", astEmfPackageCD.getCDDefinition().getCDInterfacesList().get(1).getName());
    Assertions.assertEquals("ASTExpressionExt", astEmfPackageCD.getCDDefinition().getCDInterfacesList().get(2).getName());
    Assertions.assertEquals("ASTClassbodyExt", astEmfPackageCD.getCDDefinition().getCDInterfacesList().get(3).getName());
    Assertions.assertEquals("ASTStatechartNode", astEmfPackageCD.getCDDefinition().getCDInterfacesList().get(4).getName());
    Assertions.assertEquals("StatechartPackage", astEmfPackageCD.getCDDefinition().getCDInterfacesList().get(5).getName());

    Assertions.assertEquals(1, astEmfPackageCD.getCDDefinition().getCDEnumsList().size());
    Assertions.assertEquals("StatechartLiterals", astEmfPackageCD.getCDDefinition().getCDEnumsList().get(0).getName());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testDecorateForODPackage() {
    MontiCoreScript mc = new MontiCoreScript();
    GlobalExtensionManagement glex = new GlobalExtensionManagement();
    IGrammar_WithConceptsGlobalScope symbolTable = TestHelper.createGlobalScope(modelPath);
    mc.createSymbolsFromAST(symbolTable, grammar);
    ICD4AnalysisGlobalScope cd4AGlobalScope = mc.createCD4AGlobalScope(modelPath);

    ASTCDCompilationUnit cd = mc.deriveASTCD(grammar, glex, cd4AGlobalScope);
    MCPath handcodedPath = new MCPath("src/test/resources");

    ASTCDCompilationUnit odPackage = createEmptyCompilationUnit(cd);
    mc.decorateForODPackage(glex, cd4AGlobalScope, cd, odPackage, handcodedPath);

    Assertions.assertNotNull(odPackage);
    Assertions.assertNotNull(odPackage.getCDDefinition());
    Assertions.assertEquals("Statechart", odPackage.getCDDefinition().getName());
    Assertions.assertEquals(1, odPackage.getCDDefinition().getCDClassesList().size());
    Assertions.assertEquals("Statechart2OD", odPackage.getCDDefinition().getCDClassesList().get(0).getName());
    Assertions.assertTrue(odPackage.getCDDefinition().getCDInterfacesList().isEmpty());
    Assertions.assertTrue(odPackage.getCDDefinition().getCDEnumsList().isEmpty());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testDecorateForMill() {
    MontiCoreScript mc = new MontiCoreScript();
    GlobalExtensionManagement glex = new GlobalExtensionManagement();
    IGrammar_WithConceptsGlobalScope symbolTable = TestHelper.createGlobalScope(modelPath);
    mc.createSymbolsFromAST(symbolTable, grammar);
    ICD4AnalysisGlobalScope cd4AGlobalScope = mc.createCD4AGlobalScope(modelPath);

    ASTCDCompilationUnit cd = mc.deriveASTCD(grammar, glex, cd4AGlobalScope);
    ASTCDCompilationUnit cdSymbol = mc.deriveSymbolCD(grammar, cd4AGlobalScope);
    ASTCDCompilationUnit cdScope = mc.deriveScopeCD(grammar, cd4AGlobalScope);
    MCPath handcodedPath = new MCPath("src/test/resources");
    ASTCDCompilationUnit decoratedCompilationUnit = mc.decorateForASTPackage(glex, cd4AGlobalScope, cd, handcodedPath);
    mc.decorateTraverserForVisitorPackage(glex, cd4AGlobalScope, cd, decoratedCompilationUnit, handcodedPath);
    mc.decorateForSymbolTablePackage(glex, cd4AGlobalScope, cd, cdSymbol, cdScope, decoratedCompilationUnit, handcodedPath);

    mc.decorateMill(glex, cd4AGlobalScope, cd, decoratedCompilationUnit, handcodedPath);

    Assertions.assertNotNull(decoratedCompilationUnit);
    Assertions.assertNotNull(decoratedCompilationUnit.getCDDefinition());
    Optional<ASTCDPackage> millPackage = decoratedCompilationUnit.getCDDefinition().getPackageWithName("de.monticore.statechart.statechart");
    Assertions.assertTrue(millPackage.isPresent());
    Assertions.assertEquals("Statechart", decoratedCompilationUnit.getCDDefinition().getName());
    Assertions.assertEquals(1, millPackage.get().getCDElementList().size());

    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testDecorateForAuxiliaryPackage(){
    MontiCoreScript mc = new MontiCoreScript();
    GlobalExtensionManagement glex = new GlobalExtensionManagement();
    IGrammar_WithConceptsGlobalScope symbolTable = TestHelper.createGlobalScope(modelPath);
    mc.createSymbolsFromAST(symbolTable, grammar);
    ICD4AnalysisGlobalScope cd4AGlobalScope = mc.createCD4AGlobalScope(modelPath);

    ASTCDCompilationUnit cd = mc.deriveASTCD(grammar, glex, cd4AGlobalScope);
    MCPath handcodedPath = new MCPath("src/test/resources");
    ASTCDCompilationUnit decoratedCd = createEmptyCompilationUnit(cd);

    mc.decorateAuxiliary(glex, cd4AGlobalScope, cd, decoratedCd, handcodedPath);

    Assertions.assertNotNull(decoratedCd);
    Assertions.assertNotNull(decoratedCd.getCDDefinition());
    Optional<ASTCDPackage> pp = decoratedCd.getCDDefinition().getPackageWithName("de.monticore.statechart.statechart._auxiliary");
    Assertions.assertEquals("Statechart", decoratedCd.getCDDefinition().getName());
    Assertions.assertEquals(1, pp.get().getCDElementList().size());
    Assertions.assertTrue(decoratedCd.getCDDefinition().getCDInterfacesList().isEmpty());
    Assertions.assertTrue(decoratedCd.getCDDefinition().getCDEnumsList().isEmpty());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  /**
   * Initializes the available CLI options for the MontiCore tool.
   *
   * This method must always remain a copy of the initOptions
   * method of {@link MontiCoreTool}
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

  public ASTCDCompilationUnit createEmptyCompilationUnit(ASTCDCompilationUnit ast) {
    ASTCDDefinition astCD = CD4AnalysisMill.cDDefinitionBuilder()
            .setName(ast.getCDDefinition().getName())
            .setModifier(CD4CodeMill.modifierBuilder().build()).build();
    return CD4AnalysisMill.cDCompilationUnitBuilder()
            .setMCPackageDeclaration(ast.getMCPackageDeclaration().deepClone())
            .setCDDefinition(astCD)
            .build();
  }
}
