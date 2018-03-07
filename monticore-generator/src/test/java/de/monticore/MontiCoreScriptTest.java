/* (c) https://github.com/MontiCore/monticore */

package de.monticore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import de.monticore.codegen.GeneratorHelper;
import de.monticore.codegen.cd2java.ast.AstAdditionalMethods;
import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.io.paths.IterablePath;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDDefinition;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import de.se_rwth.commons.cli.CLIArguments;
import de.se_rwth.commons.configuration.Configuration;
import de.se_rwth.commons.configuration.ConfigurationPropertiesMapContributor;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;

/**
 * Test for the {@link MontiCoreScript} class.
 *
 * @author Galina Volkova, Andreas Horst
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
  
  static String[] simpleArgs = { "-grammars",
      "src/test/resources/de/monticore/statechart/Statechart.mc4",
      "src/test/resources/mc/grammars/lexicals/TestLexicals.mc4",
      "-modelPath", modelPathPath.toAbsolutePath().toString(),
      "-out", outputPath.getAbsolutePath(), "-targetPath", "src/test/resources", "-force" };
  
  @BeforeClass
  public static void setup() {
    LogStub.init();
    Log.enableFailQuick(false);
    for (AstAdditionalMethods additionalMethod : AstAdditionalMethods.class.getEnumConstants()) {
      additionalMethods.add(additionalMethod.name());
    }
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
  
  /** {@link MontiCoreScript#parseGrammar(java.nio.file.Path)} */
  @Test
  public void testParseGrammar() {
    assertNotNull(grammar);
    assertEquals("Statechart", grammar.getName());
  }
  
  /** {@link MontiCoreScript#generateParser(GlobalExtensionManagement, ASTMCGrammar, GlobalScope, IterablePath, File)} */
  @Test
  public void testGenerateParser() {
    assertNotNull(grammar);
    MontiCoreScript mc = new MontiCoreScript();
    GlobalScope symbolTable = TestHelper.createGlobalScope(modelPath);
    mc.generateParser(glex, grammar, symbolTable, IterablePath.empty(), new File("target/generated-sources/monticore/testcode"));
  }
  
  /** {@link MontiCoreScript#transformAstGrammarToAstCd(GlobalExtensionManagement, ASTMCGrammar, GlobalScope, IterablePath)} */
  @Test
  public void testGetOrCreateCD() {
    MontiCoreScript mc = new MontiCoreScript();
    GlobalScope symbolTable = TestHelper.createGlobalScope(modelPath);
    mc.createSymbolsFromAST(symbolTable, grammar);
    cdCompilationUnit = mc.getOrCreateCD(grammar,
        new GlobalExtensionManagement(), symbolTable);
    assertNotNull(cdCompilationUnit);
    assertNotNull(cdCompilationUnit.getCDDefinition());
    assertEquals("Statechart", cdCompilationUnit.getCDDefinition().getName());
  }
  
  /** {@link MontiCoreScript#transformAstGrammarToAstCd(GlobalExtensionManagement, ASTMCGrammar, GlobalScope, IterablePath)} */
  @Test
  public void testDeriveCD() {
    MontiCoreScript mc = new MontiCoreScript();
    GlobalScope symbolTable = TestHelper.createGlobalScope(modelPath);
    mc.createSymbolsFromAST(symbolTable, grammar);
    cdCompilationUnit = mc.deriveCD(grammar,
        new GlobalExtensionManagement(), symbolTable);
    assertNotNull(cdCompilationUnit);
    assertNotNull(cdCompilationUnit.getCDDefinition());
    assertEquals("Statechart", cdCompilationUnit.getCDDefinition().getName());
  }
  
  /** {@link MontiCoreScript#decorateCd(GlobalExtensionManagement, ASTCDCompilationUnit, GlobalScope, IterablePath)}  */
  @Test
  public void testDecorateCd() {
    MontiCoreScript mc = new MontiCoreScript();
    GlobalScope symbolTable = TestHelper.createGlobalScope(modelPath);
    mc.createSymbolsFromAST(symbolTable, grammar);
    cdCompilationUnit = mc.deriveCD(grammar, new GlobalExtensionManagement(),
        symbolTable);
    assertNotNull(cdCompilationUnit);
    GeneratorHelper genHelper = new GeneratorHelper(cdCompilationUnit, symbolTable);
    assertEquals("de.monticore.statechart.statechart._ast", GeneratorHelper.getPackageName(
        genHelper.getPackageName(), GeneratorHelper.AST_PACKAGE_SUFFIX));
    assertNotNull(cdCompilationUnit.getCDDefinition());
    ASTCDDefinition cdDefinition = cdCompilationUnit.getCDDefinition();
    assertEquals(8, cdDefinition.getCDClassList().size());
    assertEquals(5, cdDefinition.getCDInterfaceList().size());
    
    mc.decorateCd(glex, cdCompilationUnit, symbolTable, targetPath);
    // Added Builder classes to the each not list class
    assertEquals(19, cdDefinition.getCDClassList().size());
    
    // Check if there are all additional methods defined in the given CD class
    List<String> methods = Lists.newArrayList();
    for (ASTCDClass cdClass : cdDefinition.getCDClassList()) {
      // All methods of CD class
      for (ASTCDMethod method : cdClass.getCDMethodList()) {
        methods.add(method.getName());
      }
      String withOrder = "WithOrder";
      for (String additionalMethod : additionalMethods) {
        if (additionalMethod.endsWith(withOrder)) {
          assertTrue(methods.contains(additionalMethod.substring(0,
              additionalMethod.indexOf(withOrder))));
        }
        else {
          assertTrue(methods.contains(additionalMethod));
        }
      }
    }
    
  }
  
  /** {@link MontiCoreScript#generate(GlobalExtensionManagement, GlobalScope, ASTCDCompilationUnit, File, IterablePath)} */
  @Test
  public void testGenerate() {
    MontiCoreScript mc = new MontiCoreScript();
    GlobalScope symbolTable = TestHelper.createGlobalScope(modelPath);
    mc.createSymbolsFromAST(symbolTable, grammar);
    cdCompilationUnit = mc.deriveCD(grammar, new GlobalExtensionManagement(),
        symbolTable);
    mc.generate(glex, symbolTable, cdCompilationUnit, outputPath, templatePath);
  }
  
  /** {@link MontiCoreScript#run(Configuration)} */
  @Test
  public void testDefaultScriptSimpleArgs() {
    Log.getFindings().clear();
    testDefaultScript(simpleArgs);
    Assert.assertTrue(Log.getFindings().isEmpty());
  }
  
  static String[] subsubgrammarArgs = { "-grammars",
      "src/test/resources/de/monticore/inherited/subsub/Subsubgrammar.mc4",
      "-modelPath", modelPathPath.toAbsolutePath().toString(),
      "-out", outputPath.getAbsolutePath(), "-targetPath", "src/test/resources", "-force" };
  
  /** {@link MontiCoreScript#run(Configuration)} */
  @Test
  public void testDefaultScriptSubsubgrammarArgs() {
    Log.getFindings().clear();
    testDefaultScript(subsubgrammarArgs);
    Assert.assertTrue(Log.getFindings().isEmpty());
  }
  
  static String[] inheritedgrammarArgs = { "-grammars",
      "src/test/resources/de/monticore/inherited/Supergrammar.mc4",
      "src/test/resources/de/monticore/inherited/sub/Subgrammar.mc4",
      "src/test/resources/de/monticore/inherited/subsub/Subsubgrammar.mc4",
      "-modelPath", modelPathPath.toAbsolutePath().toString(),
      "-out", outputPath.getAbsolutePath(), "-targetPath", "src/test/resources", "-force" };
  
  /** {@link MontiCoreScript#run(Configuration)} */
  @Test
  public void testDefaultScriptSupergrammarArgs() {
    Log.getFindings().clear();
    testDefaultScript(inheritedgrammarArgs);
    Assert.assertTrue(Log.getFindings().isEmpty());
  }
  
  static String[] supersubgrammarArgs = { "-grammars",
      "src/test/resources/de/monticore/inherited/subsub/Subsubgrammar.mc4",
      "src/test/resources/de/monticore/inherited/Supergrammar.mc4",
      "-modelPath", modelPathPath.toAbsolutePath().toString(),
      "-out", outputPath.getAbsolutePath(), "-targetPath", "src/test/resources", "-force" };
  
  /** {@link MontiCoreScript#run(Configuration)} */
  @Test
  public void testDefaultScriptSupersubgrammarArgs() {
    Log.getFindings().clear();
    testDefaultScript(inheritedgrammarArgs);
    Assert.assertTrue(Log.getFindings().isEmpty());
  }
  
  protected void testDefaultScript(String[] args) {
    Configuration configuration =
        ConfigurationPropertiesMapContributor
            .fromSplitMap(CLIArguments.forArguments(args).asMap());
    MontiCoreConfiguration cfg = MontiCoreConfiguration.withConfiguration(configuration);
    new MontiCoreScript().run(cfg);
    assertTrue(!false);
  }
  
}
