package de.monticore.codegen.cd2java.factory;

import de.monticore.MontiCoreScript;
import de.monticore.codegen.GeneratorHelper;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.factories.CDParameterFactory;
import de.monticore.codegen.cd2java.factories.CDTypeFactory;
import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static de.monticore.codegen.cd2java.factories.CDModifier.*;
import static org.junit.Assert.*;

public class NodeFactoryDecoratorTest {

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
    Path modelPathPath = Paths.get("src/test/resources");
    ModelPath modelPath = new ModelPath(modelPathPath);
    Optional<ASTMCGrammar> grammar = new MontiCoreScript()
        .parseGrammar(Paths.get(new File(
            "src/test/resources/Automaton.mc4").getAbsolutePath()));
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
    assertEquals("AutomatonNodeFactory", factoryClass.getName());
  }

  @Test
  public void testAttributeName() {
    assertEquals("factory", factoryClass.getCDAttribute(0).getName());
    assertEquals("factoryASTAutomaton", factoryClass.getCDAttribute(1).getName());
    assertEquals("factoryASTState", factoryClass.getCDAttribute(2).getName());
    assertEquals("factoryASTTransition", factoryClass.getCDAttribute(3).getName());
  }

  @Test
  public void testAttributeModifier() {
    for (ASTCDAttribute astcdAttribute : factoryClass.getCDAttributeList()) {
      assertTrue(astcdAttribute.isPresentModifier());
      assertTrue(PROTECTED_STATIC.deepEquals(astcdAttribute.getModifier()));
    }
  }

  @Test
  public void testConstructor() {
    assertEquals(1, factoryClass.sizeCDConstructors());
    ASTCDConstructor astcdConstructor = CD4AnalysisMill.cDConstructorBuilder()
        .setModifier(PROTECTED)
        .setName("AutomatonNodeFactory")
        .build();
    assertTrue(astcdConstructor.deepEquals(factoryClass.getCDConstructor(0)));
  }

  @Test
  public void testMethodGetFactory() {
    ASTCDMethod method = factoryClass.getCDMethod(0);
    //test name
    assertEquals("getFactory", method.getName());
    //test modifier
    assertTrue(PRIVATE_STATIC.deepEquals(method.getModifier()));
    //test parameters
    assertTrue(method.isEmptyCDParameters());
    //test returnType
    ASTType returnType = cdTypeFacade.createTypeByDefinition("AutomatonNodeFactory");
    assertTrue(returnType.deepEquals(method.getReturnType()));
  }

  @Test
  public void testMethodCreateASTAutomatonWithoutParameter() {
    ASTCDMethod method = factoryClass.getCDMethod(1);
    //test name
    assertEquals("createASTAutomaton", method.getName());
    //test modifier
    assertTrue(PUBLIC_STATIC.deepEquals(method.getModifier()));
    //test parameters
    assertTrue(method.isEmptyCDParameters());
    //test returnType
    ASTType returnType = cdTypeFacade.createTypeByDefinition("ASTAutomaton");
    assertTrue(returnType.deepEquals(method.getReturnType()));
  }

  @Test
  public void testMethodDoCreateASTAutomatonWithoutParameter() {
    ASTCDMethod method = factoryClass.getCDMethod(7);
    //test name
    assertEquals("doCreateASTAutomaton", method.getName());
    //test modifier
    assertTrue(PROTECTED.deepEquals(method.getModifier()));
    //test parameters
    assertTrue(method.isEmptyCDParameters());
    //test returnType
    ASTType returnType = cdTypeFacade.createTypeByDefinition("ASTAutomaton");
    assertTrue(returnType.deepEquals(method.getReturnType()));
  }

  @Test
  public void testMethodCreateASTAutomatonWithParameter() {
    ASTCDMethod method = factoryClass.getCDMethod(2);
    //test name
    assertEquals("createASTAutomaton", method.getName());
    //test modifier
    assertTrue(PUBLIC_STATIC.deepEquals(method.getModifier()));
    //test returnType
    ASTType returnType = cdTypeFacade.createTypeByDefinition("ASTAutomaton");
    assertTrue(returnType.deepEquals(method.getReturnType()));
    //testParameter
    assertFalse(method.isEmptyCDParameters());

    ASTType nameType = cdTypeFacade.createTypeByDefinition("String");
    ASTCDParameter nameParameter = cdParameterFacade.createParameter(nameType, "name");
    assertTrue(nameParameter.getType().deepEquals(method.getCDParameter(0).getType()));
    assertEquals(nameParameter.getName(), method.getCDParameter(0).getName());

    ASTType statesType = cdTypeFacade.createTypeByDefinition("java.util.List<automaton._ast.ASTState>");
    ASTCDParameter statesParameter = cdParameterFacade.createParameter(statesType, "states");
    assertTrue(statesParameter.getType().deepEquals(statesType));
    assertEquals(statesParameter.getName(), method.getCDParameter(1).getName());

    ASTType transitionsType = cdTypeFacade.createTypeByDefinition("java.util.List<automaton._ast.ASTTransition>");
    ASTCDParameter transitionsParameter = cdParameterFacade.createParameter(transitionsType, "transitions");
    assertTrue(transitionsParameter.getType().deepEquals(transitionsType));
    assertEquals(transitionsParameter.getName(), method.getCDParameter(2).getName());
  }

  @Test
  public void testMethodDoCreateASTAutomatonWithParameter() {
    ASTCDMethod method = factoryClass.getCDMethod(8);
    //test name
    assertEquals("doCreateASTAutomaton", method.getName());
    //test modifier
    assertTrue(PROTECTED.deepEquals(method.getModifier()));
    //test returnType
    ASTType returnType = cdTypeFacade.createTypeByDefinition("ASTAutomaton");
    assertTrue(returnType.deepEquals(method.getReturnType()));
    //testParameter
    assertFalse(method.isEmptyCDParameters());

    ASTType nameType = cdTypeFacade.createTypeByDefinition("String");
    ASTCDParameter nameParameter = cdParameterFacade.createParameter(nameType, "name");
    assertTrue(nameParameter.getType().deepEquals(method.getCDParameter(0).getType()));
    assertEquals(nameParameter.getName(), method.getCDParameter(0).getName());

    ASTType statesType = cdTypeFacade.createTypeByDefinition("java.util.List<automaton._ast.ASTState>");
    ASTCDParameter statesParameter = cdParameterFacade.createParameter(statesType, "states");
    assertTrue(statesParameter.getType().deepEquals(statesType));
    assertEquals(statesParameter.getName(), method.getCDParameter(1).getName());

    ASTType transitionsType = cdTypeFacade.createTypeByDefinition("java.util.List<automaton._ast.ASTTransition>");
    ASTCDParameter transitionsParameter = cdParameterFacade.createParameter(transitionsType, "transitions");
    assertTrue(transitionsParameter.getType().deepEquals(transitionsType));
    assertEquals(transitionsParameter.getName(), method.getCDParameter(2).getName());
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
    Path generatedFiles = Paths.get("AutomatonNodeFactory.java");
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    generatorEngine.generate(CoreTemplates.CLASS, generatedFiles, factoryClass, factoryClass);
  }

}
