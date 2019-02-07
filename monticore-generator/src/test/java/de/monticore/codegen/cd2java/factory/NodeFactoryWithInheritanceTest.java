package de.monticore.codegen.cd2java.factory;

import de.monticore.MontiCoreScript;
import de.monticore.codegen.GeneratorHelper;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.factories.CDParameterFactory;
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
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.*;

public class NodeFactoryWithInheritanceTest {

  private CDTypeFactory cdTypeFacade;

  private CDParameterFactory cdParameterFacade;

  private ASTCDCompilationUnit cdCompilationUnit;

  private ASTCDClass factoryClass;

  private GlobalExtensionManagement glex;

  @Before
  public void setUp() {
    this.glex = new GlobalExtensionManagement();
    this.cdTypeFacade = CDTypeFactory.getInstance();
    this.cdParameterFacade = CDParameterFactory.getInstance();

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

    NodeFactoryDecorator factoryDecorator = new NodeFactoryDecorator(glex, genHelper);
    this.factoryClass = factoryDecorator.decorate(astcdDefinition);
    //test if not changed the original Definition
    assertTrue(astcdDefinition.deepEquals(cdCompilationUnit.getCDDefinition()));
  }


  @Test
  public void testFactoryName() {
    assertEquals("BGrammarNodeFactory", factoryClass.getName());
  }

  @Test
  public void testAttributeName() {
    assertEquals(3, factoryClass.sizeCDAttributes());
    assertEquals("factory", factoryClass.getCDAttribute(0).getName());
    assertEquals("factoryASTBlub", factoryClass.getCDAttribute(1).getName());
    assertEquals("factoryASTBli", factoryClass.getCDAttribute(2).getName());
  }

  @Test
  public void testAttributeModifier() {
    for (ASTCDAttribute astcdAttribute : factoryClass.getCDAttributeList()) {
      assertTrue(astcdAttribute.isPresentModifier());
      assertTrue(ModifierBuilder.builder().Protected().Static().build().deepEquals(astcdAttribute.getModifier()));
    }
  }

  @Test
  public void testConstructor() {
    assertEquals(1, factoryClass.sizeCDConstructors());
    ASTCDConstructor astcdConstructor = CD4AnalysisMill.cDConstructorBuilder()
        .setModifier(ModifierBuilder.builder().Protected().build())
        .setName("BGrammarNodeFactory")
        .build();
    assertTrue(astcdConstructor.deepEquals(factoryClass.getCDConstructor(0)));
  }

  @Test
  public void testMethodGetFactory() {
    ASTCDMethod method = factoryClass.getCDMethod(0);
    //test name
    assertEquals("getFactory", method.getName());
    //test modifier
    assertTrue(ModifierBuilder.builder().Private().Static().build().deepEquals(method.getModifier()));
    //test parameters
    assertTrue(method.isEmptyCDParameters());
    //test returnType
    ASTType returnType = cdTypeFacade.createTypeByDefinition("BGrammarNodeFactory");
    assertTrue(returnType.deepEquals(method.getReturnType()));
  }

  @Test
  public void testMethodCreateDelegateASTC() {
    ASTCDMethod method = factoryClass.getCDMethod(9);
    //test name
    assertEquals("createASTC", method.getName());
    //test modifier
    assertTrue(ModifierBuilder.builder().Public().Static().build().deepEquals(method.getModifier()));
    //test parameters
    assertTrue(method.isEmptyCDParameters());
    //test returnType
    ASTType returnType = cdTypeFacade.createTypeByDefinition("de.monticore.codegen.factory.cgrammar._ast.ASTC");
    assertEquals(TypesPrinter.printReturnType(returnType),TypesPrinter.printReturnType(method.getReturnType()));
  }

  @Test
  public void testMethodCreateDelegateASTName() {
    ASTCDMethod method = factoryClass.getCDMethod(10);
    //test name
    assertEquals("createASTName", method.getName());
    //test modifier
    assertTrue(ModifierBuilder.builder().Public().Static().build().deepEquals(method.getModifier()));
    //test parameters
    assertTrue(method.isEmptyCDParameters());
    //test returnType
    ASTType returnType = cdTypeFacade.createTypeByDefinition("de.monticore.codegen.factory.agrammar._ast.ASTName");
    assertEquals(TypesPrinter.printReturnType(returnType),TypesPrinter.printReturnType(method.getReturnType()));
  }

  @Test
  public void testMethodCreateDelegateASTFoo() {
    ASTCDMethod method = factoryClass.getCDMethod(11);
    //test name
    assertEquals("createASTFoo", method.getName());
    //test modifier
    assertTrue(ModifierBuilder.builder().Public().Static().build().deepEquals(method.getModifier()));
    //test parameters
    assertTrue(method.isEmptyCDParameters());
    //test returnType
    ASTType returnType = cdTypeFacade.createTypeByDefinition("de.monticore.codegen.factory.agrammar._ast.ASTFoo");
    assertEquals(TypesPrinter.printReturnType(returnType),TypesPrinter.printReturnType(method.getReturnType()));
  }

  @Test
  public void testMethodCreateDelegateASTBar() {
    ASTCDMethod method = factoryClass.getCDMethod(13);
    //test name
    assertEquals("createASTBar", method.getName());
    //test modifier
    assertTrue(ModifierBuilder.builder().Public().Static().build().deepEquals(method.getModifier()));
    //test parameters
    assertTrue(method.isEmptyCDParameters());
    //test returnType
    ASTType returnType = cdTypeFacade.createTypeByDefinition("de.monticore.codegen.factory.agrammar._ast.ASTBar");
    assertEquals(TypesPrinter.printReturnType(returnType),TypesPrinter.printReturnType(method.getReturnType()));
  }

  @Test
  public void testMethodCreateDelegateASTFooWithParam() {
    ASTCDMethod method = factoryClass.getCDMethod(12);
    //test name
    assertEquals("createASTFoo", method.getName());
    //test modifier
    assertTrue(ModifierBuilder.builder().Public().Static().build().deepEquals(method.getModifier()));
    //test parameters
    assertFalse(method.isEmptyCDParameters());
    assertEquals(1,method.sizeCDParameters());

    ASTType nameType = cdTypeFacade.createTypeByDefinition("de.monticore.codegen.factory.AGrammar.ASTName"); //Todo fix type
    ASTCDParameter nameParameter = cdParameterFacade.createParameter(nameType, "name");
    assertEquals(TypesPrinter.printType(nameParameter.getType()),TypesPrinter.printType(method.getCDParameter(0).getType()));
    assertEquals(nameParameter.getName(), method.getCDParameter(0).getName());
    
    //test returnType
    ASTType returnType = cdTypeFacade.createTypeByDefinition("de.monticore.codegen.factory.agrammar._ast.ASTFoo");
    assertEquals(TypesPrinter.printReturnType(returnType),TypesPrinter.printReturnType(method.getReturnType()));
  }

  @Test
  public void testMethodCreateDelegateASTBarWithParam() {
    ASTCDMethod method = factoryClass.getCDMethod(14);
    //test name
    assertEquals("createASTBar", method.getName());
    //test modifier
    assertTrue(ModifierBuilder.builder().Public().Static().build().deepEquals(method.getModifier()));
    //test parameters
    assertFalse(method.isEmptyCDParameters());
    assertEquals(2,method.sizeCDParameters());

    ASTType fooType = cdTypeFacade.createTypeByDefinition("de.monticore.codegen.factory.AGrammar.ASTFoo"); //Todo fix type
    ASTCDParameter fooParameter = cdParameterFacade.createParameter(fooType, "foo");
    assertEquals(TypesPrinter.printType(fooParameter.getType()),TypesPrinter.printType(method.getCDParameter(0).getType()));
    assertEquals(fooParameter.getName(), method.getCDParameter(0).getName());

    ASTType barType = cdTypeFacade.createTypeByDefinition("de.monticore.codegen.factory.AGrammar.ASTName"); //Todo fix type
    ASTCDParameter barParameter = cdParameterFacade.createParameter(barType, "bar");
    assertEquals(TypesPrinter.printType(barParameter.getType()),TypesPrinter.printType(method.getCDParameter(1).getType()));
    assertEquals(barParameter.getName(), method.getCDParameter(1).getName());
    
    //test returnType
    ASTType returnType = cdTypeFacade.createTypeByDefinition("de.monticore.codegen.factory.agrammar._ast.ASTBar");
    assertEquals(TypesPrinter.printReturnType(returnType),TypesPrinter.printReturnType(method.getReturnType()));
  }


  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.CLASS, factoryClass, factoryClass);
    System.out.println(sb.toString());
  }

  @Test
  public void testGeneratedCodeInFile() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    generatorSetup.setOutputDirectory(Paths.get("target/generated-test-sources/generatortest/factory").toFile());
    Path generatedFiles = Paths.get("BGrammarNodeFactory.java");
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    generatorEngine.generate(CoreTemplates.CLASS, generatedFiles, factoryClass, factoryClass);
  }

}
