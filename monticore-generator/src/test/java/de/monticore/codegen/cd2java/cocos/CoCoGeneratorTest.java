/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.cd2java.cocos;

import de.monticore.MontiCoreScript;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._symboltable.CD4AnalysisGlobalScope;
import de.monticore.cd.cd4analysis._symboltable.CD4AnalysisLanguage;
import de.monticore.codegen.AstDependentGeneratorTest;
import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar_withconcepts._symboltable.Grammar_WithConceptsGlobalScope;
import de.monticore.grammar.grammar_withconcepts._symboltable.Grammar_WithConceptsLanguage;
import de.monticore.io.paths.IterablePath;
import de.monticore.io.paths.ModelPath;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CoCoGeneratorTest extends AstDependentGeneratorTest {

  private ASTMCGrammar grammar;

  private ASTCDCompilationUnit cdCompilationUnit;

  private GlobalExtensionManagement glex;

  private ModelPath modelPath = new ModelPath(Paths.get("src/test/resources"));

  private CD4AnalysisGlobalScope cd4AnalysisGlobalScope;

  private Grammar_WithConceptsGlobalScope grammar_withConceptsGlobalScope;

  @BeforeClass
  public static void setup() {
    LogStub.init();
    Log.enableFailQuick(false);
  }

  @Ignore("not ready yet")
  @Test
  public void test() {
    String grammarToTest = "src/test/resources/Automaton.mc4";
    cd4AnalysisGlobalScope = new CD4AnalysisGlobalScope(modelPath,new CD4AnalysisLanguage());
    grammar_withConceptsGlobalScope = new Grammar_WithConceptsGlobalScope(modelPath,new Grammar_WithConceptsLanguage());

    astTest.testCorrect(grammarToTest);

    glex = new GlobalExtensionManagement();
    Path model = Paths.get(new File(
        grammarToTest).getAbsolutePath());

    MontiCoreScript mc = new MontiCoreScript();
    IterablePath targetPath = IterablePath.from(new File("src/test/resource"), "java");
    Optional<ASTMCGrammar> ast = mc.parseGrammar(model);
    assertTrue(ast.isPresent());
    grammar = ast.get();
    File targetFile = new File(OUTPUT_FOLDER);
    cdCompilationUnit = mc.deriveCD(grammar, new GlobalExtensionManagement(), cd4AnalysisGlobalScope,grammar_withConceptsGlobalScope);
    assertEquals("CD4Analysis", cdCompilationUnit.getCDDefinition().getName());

    CoCoGenerator.generate(glex, cd4AnalysisGlobalScope, cdCompilationUnit, targetFile);
  }

  /**
   * @see de.monticore.codegen.GeneratorTest#doGenerate(java.lang.String)
   */
  @Override
  protected void doGenerate(String model) {
    Log.info("Runs CoCoGenerator test for the model " + model, LOG);

    cd4AnalysisGlobalScope = new CD4AnalysisGlobalScope(modelPath,new CD4AnalysisLanguage());
    grammar_withConceptsGlobalScope = new Grammar_WithConceptsGlobalScope(modelPath,new Grammar_WithConceptsLanguage());

    MontiCoreScript mc = new MontiCoreScript();

    Grammar_WithConceptsGlobalScope symbolTable = TestHelper.createGlobalScope(modelPath);
    glex = new GlobalExtensionManagement();
    Optional<ASTMCGrammar> ast = mc.parseGrammar(Paths.get(new File(model).getAbsolutePath()));
    assertTrue(ast.isPresent());
    grammar = ast.get();

    IterablePath targetPath = IterablePath.from(new File("target"), "java");
    File targetFile = new File(OUTPUT_FOLDER);
    cdCompilationUnit = mc.deriveCD(grammar, glex,cd4AnalysisGlobalScope, grammar_withConceptsGlobalScope);

    CoCoGenerator.generate(glex, cd4AnalysisGlobalScope, cdCompilationUnit, targetFile);
    // TODO needs asts and visitors to be generated first
    // CLIArguments cliArguments =
    // CLIArguments.forArguments(getCLIArguments(model));
    // MontiCoreScript.run(MontiCoreConfiguration.withProperties(cliArguments.asMap()));
  }

  /**
   * @see de.monticore.codegen.GeneratorTest#getPathToGeneratedCode(java.lang.String)
   */
  @Override
  protected Path getPathToGeneratedCode(String grammar) {
    return Paths.get(OUTPUT_FOLDER,
        Names.getPathFromFilename(Names.getQualifier(grammar), "/").toLowerCase());
  }

  // private String[] getCLIArguments(String grammar) {
  // List<String> args = Lists.newArrayList(getGeneratorArguments());
  // args.add(getConfigProperty(MontiCoreConfiguration.GRAMMAR_PROPERTY));
  // args.add(grammar);
  // return args.toArray(new String[0]);
  // }

}
