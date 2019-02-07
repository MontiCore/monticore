package de.monticore.codegen.cd2java.mill;

import de.monticore.MontiCoreScript;
import de.monticore.codegen.GeneratorHelper;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.factories.CDTypeFactory;
import de.monticore.codegen.cd2java.factories.ModifierBuilder;
import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.types.TypesPrinter;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MillWithInheritanceTest {

  private CDTypeFactory cdTypeFacade;


  private ASTCDCompilationUnit cdCompilationUnit;

  private ASTCDClass millClass;

  private GlobalExtensionManagement glex;

  @Before
  public void setUp() {
    this.glex = new GlobalExtensionManagement();
    this.cdTypeFacade = CDTypeFactory.getInstance();

    //create grammar from ModelPath
    Path modelPathPath = Paths.get("src/test/resources/de/monticore/codegen/factory");
    ModelPath modelPath = new ModelPath(modelPathPath);
    Optional<ASTMCGrammar> grammar = new MontiCoreScript()
        .parseGrammar(Paths.get(new File(
            "src/test/resources/de/monticore/codegen/factory/BGrammar.mc4").getAbsolutePath()));
    assertTrue(grammar.isPresent());

    //create ASTCDDefinition from MontiCoreScript
    MontiCoreScript script = new MontiCoreScript();
    GlobalScope symbolTable = TestHelper.createGlobalScope(modelPath);
    script.createSymbolsFromAST(symbolTable, grammar.get());
    cdCompilationUnit = script.deriveCD(grammar.get(), new GlobalExtensionManagement(),
        symbolTable);
    ASTCDDefinition astcdDefinition = cdCompilationUnit.getCDDefinition().deepClone();

    GeneratorHelper genHelper = new GeneratorHelper(cdCompilationUnit, symbolTable);
    MillDecorator millDecorator = new MillDecorator(glex, genHelper);
    this.millClass = millDecorator.decorate(astcdDefinition);
    //test if not changed the original Definition
    assertTrue(astcdDefinition.deepEquals(cdCompilationUnit.getCDDefinition()));
  }

  @Test
  public void testAttributeName() {
    assertEquals("mill", millClass.getCDAttribute(0).getName());
    assertEquals("millASTBlub", millClass.getCDAttribute(1).getName());
    assertEquals("millASTBli", millClass.getCDAttribute(2).getName());
  }

  @Test
  public void testAttributeModifier() {
    for (ASTCDAttribute astcdAttribute : millClass.getCDAttributeList()) {
      assertTrue(astcdAttribute.isPresentModifier());
      assertTrue(ModifierBuilder.builder().Protected().Static().build().deepEquals(astcdAttribute.getModifier()));
    }
  }

  @Test
  public void testConstructor() {
    assertEquals(1, millClass.sizeCDConstructors());
    assertTrue(ModifierBuilder.builder().Protected().build().deepEquals(millClass.getCDConstructor(0).getModifier()));
    assertEquals("BGrammarMill", millClass.getCDConstructor(0).getName());
  }

  @Test
  public void testGetMillMethod() {
    ASTCDMethod getMill = millClass.getCDMethod(0);
    //test Method Name
    assertEquals("getMill", getMill.getName());
    //test Parameters
    assertTrue(getMill.isEmptyCDParameters());
    //test ReturnType
    assertEquals("BGrammarMill", TypesPrinter.printReturnType(getMill.getReturnType()));
    //test Modifier
    assertTrue(ModifierBuilder.builder().Protected().Static().build().deepEquals(getMill.getModifier()));
  }

  @Test
  public void testInitMeMethod() {
    ASTCDMethod initMe = millClass.getCDMethod(1);
    //test Method Name
    assertEquals("initMe", initMe.getName());
    //test Parameters
    assertEquals(1, initMe.sizeCDParameters());
    assertEquals("BGrammarMill", TypesPrinter.printType(initMe.getCDParameter(0).getType()));
    assertEquals("a", initMe.getCDParameter(0).getName());
    //test ReturnType
    assertTrue(cdTypeFacade.createVoidType().deepEquals(initMe.getReturnType()));
    //test Modifier
    assertTrue(ModifierBuilder.builder().Public().Static().build().deepEquals(initMe.getModifier()));
  }

  @Test
  public void testInitMethod() {
    ASTCDMethod init = millClass.getCDMethod(2);
    //test Method Name
    assertEquals("init", init.getName());
    //test Parameters
    assertTrue(init.isEmptyCDParameters());
    //test ReturnType
    assertTrue(cdTypeFacade.createVoidType().deepEquals(init.getReturnType()));
    //test Modifier
    assertTrue(ModifierBuilder.builder().Public().Static().build().deepEquals(init.getModifier()));
  }

  @Test
  public void testResetMethod() {
    ASTCDMethod init = millClass.getCDMethod(3);
    //test Method Name
    assertEquals("reset", init.getName());
    //test Parameters
    assertTrue(init.isEmptyCDParameters());
    //test ReturnType
    assertTrue(cdTypeFacade.createVoidType().deepEquals(init.getReturnType()));
    //test Modifier
    assertTrue(ModifierBuilder.builder().Public().Static().build().deepEquals(init.getModifier()));
  }

  @Test
  public void testCBuilderMethod() {
    ASTCDMethod fooBarBuilder = millClass.getCDMethod(8);
    //test Method Name
    assertEquals("aSTCBuilder", fooBarBuilder.getName());
    //test Parameters
    assertTrue(fooBarBuilder.isEmptyCDParameters());
    //test ReturnType
    assertEquals("de.monticore.codegen.factory.cgrammar._ast.ASTC", TypesPrinter.printReturnType(fooBarBuilder.getReturnType()));
    //test Modifier
    assertTrue(ModifierBuilder.builder().Public().Static().build().deepEquals(fooBarBuilder.getModifier()));
  }

  @Test
  public void testNameBuilderMethod() {
    ASTCDMethod fooBarBuilder = millClass.getCDMethod(9);
    //test Method Name
    assertEquals("aSTNameBuilder", fooBarBuilder.getName());
    //test Parameters
    assertTrue(fooBarBuilder.isEmptyCDParameters());
    //test ReturnType
    assertEquals("de.monticore.codegen.factory.agrammar._ast.ASTName", TypesPrinter.printReturnType(fooBarBuilder.getReturnType()));
    //test Modifier
    assertTrue(ModifierBuilder.builder().Public().Static().build().deepEquals(fooBarBuilder.getModifier()));
  }

  @Test
  public void testFooBuilderMethod() {
    ASTCDMethod fooBarBuilder = millClass.getCDMethod(10);
    //test Method Name
    assertEquals("aSTFooBuilder", fooBarBuilder.getName());
    //test Parameters
    assertTrue(fooBarBuilder.isEmptyCDParameters());
    //test ReturnType
    assertEquals("de.monticore.codegen.factory.agrammar._ast.ASTFoo", TypesPrinter.printReturnType(fooBarBuilder.getReturnType()));
    //test Modifier
    assertTrue(ModifierBuilder.builder().Public().Static().build().deepEquals(fooBarBuilder.getModifier()));
  }

  @Test
  public void tesBarBuilderMethod() {
    ASTCDMethod fooBarBuilder = millClass.getCDMethod(11);
    //test Method Name
    assertEquals("aSTBarBuilder", fooBarBuilder.getName());
    //test Parameters
    assertTrue(fooBarBuilder.isEmptyCDParameters());
    //test ReturnType
    assertEquals("de.monticore.codegen.factory.agrammar._ast.ASTBar", TypesPrinter.printReturnType(fooBarBuilder.getReturnType()));
    //test Modifier
    assertTrue(ModifierBuilder.builder().Public().Static().build().deepEquals(fooBarBuilder.getModifier()));
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.CLASS, millClass, millClass);
    System.out.println(sb.toString());
  }

  @Test
  public void testGeneratedCodeInFile() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    generatorSetup.setOutputDirectory(Paths.get("target/generated-test-sources/generatortest/mill").toFile());
    Path generatedFiles = Paths.get("BGrammarMill.java");
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    generatorEngine.generate(CoreTemplates.CLASS, generatedFiles, millClass, millClass);
  }
}
