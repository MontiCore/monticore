package de.monticore.codegen.cd2java.ast_new;

import de.monticore.MontiCoreScript;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.codegen.cd2java.typecd2java.TypeCD2JavaDecorator;
import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class ASTDecoratorWithRefSymbolTest {

  private ASTCDCompilationUnit cdCompilationUnit;

  private ASTCDClass astcdClass;

  private GlobalExtensionManagement glex;


  @Before
  public void setUp() {
    this.glex = new GlobalExtensionManagement();

    //create grammar from ModelPath
    Path modelPathPath = Paths.get("src/test/resources");
    ModelPath modelPath = new ModelPath(modelPathPath);
    Optional<ASTMCGrammar> grammar = new MontiCoreScript()
        .parseGrammar(Paths.get(new File(
            "src/test/resources/de/monticore/codegen/ast/ASTTest.mc4").getAbsolutePath()));
    assertTrue(grammar.isPresent());

    //create ASTCDDefinition from MontiCoreScript
    MontiCoreScript script = new MontiCoreScript();
    GlobalScope globalScope = TestHelper.createGlobalScope(modelPath);
    script.createSymbolsFromAST(globalScope, grammar.get());
    cdCompilationUnit = script.deriveCD(grammar.get(), new GlobalExtensionManagement(),
        globalScope);

    cdCompilationUnit.setEnclosingScope(globalScope);
    //make types java compatible
    TypeCD2JavaDecorator typeDecorator = new TypeCD2JavaDecorator();
    typeDecorator.decorate(cdCompilationUnit);

    glex.setGlobalValue("astHelper", new DecorationHelper());
    ASTWithSymbolDecorator symbolDecorator = new ASTWithSymbolDecorator(glex, cdCompilationUnit);
    this.astcdClass = symbolDecorator.decorate(cdCompilationUnit.getCDDefinition().getCDClass(3));
  }

  @Test
  public void testClass() {
    assertEquals("ASTRef", cdCompilationUnit.getCDDefinition().getCDClass(3).getName());
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.CLASS, astcdClass, astcdClass);
    System.out.println(sb.toString());
  }

  @Test
  public void testGeneratedCodeInFile() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    generatorSetup.setOutputDirectory(Paths.get("target/generated-test-sources/generatortest/ast").toFile());
    Path generatedFiles = Paths.get("ASTRef.java");
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    generatorEngine.generate(CoreTemplates.CLASS, generatedFiles, astcdClass, astcdClass);
  }
}
