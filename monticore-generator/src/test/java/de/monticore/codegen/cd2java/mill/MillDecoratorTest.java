package de.monticore.codegen.cd2java.mill;

import de.monticore.MontiCoreScript;
import de.monticore.codegen.cd2java.CoreTemplates;
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

import static de.monticore.codegen.cd2java.DecoratorAssert.*;

import static de.monticore.codegen.cd2java.factories.CDModifier.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MillDecoratorTest {

  private CDTypeFactory cdTypeFacade;

  private ASTCDCompilationUnit cdCompilationUnit;

  private ASTCDClass millClass;

  private GlobalExtensionManagement glex;

  @Before
  public void setUp() {
    this.glex = new GlobalExtensionManagement();
    this.cdTypeFacade = CDTypeFactory.getInstance();

    //create grammar from ModelPath
    Path modelPathPath = Paths.get("src/test/resources");
    ModelPath modelPath = new ModelPath(modelPathPath);
    Optional<ASTMCGrammar> grammar = new MontiCoreScript()
        .parseGrammar(Paths.get(new File(
            "src/test/resources/Automaton.mc4").getAbsolutePath()));
    assertTrue(grammar.isPresent());

    //create ASTCDDefinition from MontiCoreScript
    MontiCoreScript script = new MontiCoreScript();
    GlobalScope globalScope = TestHelper.createGlobalScope(modelPath);
    script.createSymbolsFromAST(globalScope, grammar.get());
    cdCompilationUnit = script.deriveCD(grammar.get(), new GlobalExtensionManagement(),
        globalScope);

    cdCompilationUnit.setEnclosingScope(globalScope);
    MillDecorator millDecorator = new MillDecorator(glex);
    this.millClass = millDecorator.decorate(cdCompilationUnit);
  }

  @Test
  public void testMillName() {
    assertEquals("AutomatonMill", millClass.getName());
  }

  @Test
  public void testAttributeName() {
    assertEquals("mill", millClass.getCDAttribute(0).getName());
    assertEquals("millASTAutomaton", millClass.getCDAttribute(1).getName());
    assertEquals("millASTState", millClass.getCDAttribute(2).getName());
    assertEquals("millASTTransition", millClass.getCDAttribute(3).getName());
  }

  @Test
  public void testAttributeModifier() {
    for (ASTCDAttribute astcdAttribute : millClass.getCDAttributeList()) {
      assertTrue(astcdAttribute.isPresentModifier());
      assertTrue(PROTECTED_STATIC.build().deepEquals(astcdAttribute.getModifier()));
    }
  }

  @Test
  public void testConstructor() {
    assertEquals(1, millClass.sizeCDConstructors());
    assertTrue(PROTECTED.build().deepEquals(millClass.getCDConstructor(0).getModifier()));
    assertEquals("AutomatonMill", millClass.getCDConstructor(0).getName());
  }

  @Test
  public void testGetMillMethod() {
    ASTCDMethod getMill = millClass.getCDMethod(0);
    //test Method Name
    assertEquals("getMill", getMill.getName());
    //test Parameters
    assertTrue(getMill.isEmptyCDParameters());
    //test ReturnType
    ASTType returnType = cdTypeFacade.createTypeByDefinition("AutomatonMill");
    assertDeepEquals(returnType, getMill.getReturnType());
    //test Modifier
    assertTrue(PROTECTED_STATIC.build().deepEquals(getMill.getModifier()));
  }

  @Test
  public void testInitMeMethod() {
    ASTCDMethod initMe = millClass.getCDMethod(1);
    //test Method Name
    assertEquals("initMe", initMe.getName());
    //test Parameters
    assertEquals(1, initMe.sizeCDParameters());
    ASTType type = cdTypeFacade.createSimpleReferenceType("AutomatonMill");
    assertDeepEquals(type, initMe.getCDParameter(0).getType());
    assertEquals("a", initMe.getCDParameter(0).getName());
    //test ReturnType
    assertVoid(initMe.getReturnType());
    //test Modifier
    assertTrue(PUBLIC_STATIC.build().deepEquals(initMe.getModifier()));
  }

  @Test
  public void testInitMethod() {
    ASTCDMethod init = millClass.getCDMethod(2);
    //test Method Name
    assertEquals("init", init.getName());
    //test Parameters
    assertTrue(init.isEmptyCDParameters());
    //test ReturnType
    assertVoid(init.getReturnType());
    //test Modifier
    assertTrue(PUBLIC_STATIC.build().deepEquals(init.getModifier()));
  }

  @Test
  public void testResetMethod() {
    ASTCDMethod reset = millClass.getCDMethod(3);
    //test Method Name
    assertEquals("reset", reset.getName());
    //test Parameters
    assertTrue(reset.isEmptyCDParameters());
    //test ReturnType
    assertVoid(reset.getReturnType());
    //test Modifier
    assertTrue(PUBLIC_STATIC.build().deepEquals(reset.getModifier()));
  }

  @Test
  public void testAutomatonBuilderMethod() {
    ASTCDMethod fooBarBuilder = millClass.getCDMethod(4);
    //test Method Name
    assertEquals("aSTAutomatonBuilder", fooBarBuilder.getName());
    //test Parameters
    assertTrue(fooBarBuilder.isEmptyCDParameters());
    //test ReturnType
    ASTType returnType = cdTypeFacade.createTypeByDefinition("ASTAutomatonBuilder");
    assertDeepEquals(returnType, fooBarBuilder.getReturnType());
    //test Modifier
    assertTrue(PUBLIC_STATIC.build().deepEquals(fooBarBuilder.getModifier()));
  }

  @Test
  public void testProtectedAutomatonMethod() {
    ASTCDMethod fooBarBuilder = millClass.getCDMethod(5);
    //test Method Name
    assertEquals("_aSTAutomatonBuilder", fooBarBuilder.getName());
    //test Parameters
    assertTrue(fooBarBuilder.isEmptyCDParameters());
    //test ReturnType
    ASTType returnType = cdTypeFacade.createTypeByDefinition("ASTAutomatonBuilder");
    assertDeepEquals(returnType, fooBarBuilder.getReturnType());
    //test Modifier
    assertTrue(PROTECTED.build().deepEquals(fooBarBuilder.getModifier()));
  }


  @Test
  public void testStateMethod() {
    ASTCDMethod fooBarBuilder = millClass.getCDMethod(6);
    //test Method Name
    assertEquals("aSTStateBuilder", fooBarBuilder.getName());
    //test Parameters
    assertTrue(fooBarBuilder.isEmptyCDParameters());
    //test ReturnType
    ASTType returnType = cdTypeFacade.createTypeByDefinition("ASTStateBuilder");
    assertDeepEquals(returnType, fooBarBuilder.getReturnType());
    //test Modifier
    assertTrue(PUBLIC_STATIC.build().deepEquals(fooBarBuilder.getModifier()));
  }

  @Test
  public void testProtectedStateBuilderMethod() {
    ASTCDMethod fooBarBuilder = millClass.getCDMethod(7);
    //test Method Name
    assertEquals("_aSTStateBuilder", fooBarBuilder.getName());
    //test Parameters
    assertTrue(fooBarBuilder.isEmptyCDParameters());
    //test ReturnType
    ASTType returnType = cdTypeFacade.createTypeByDefinition("ASTStateBuilder");
    assertDeepEquals(returnType, fooBarBuilder.getReturnType());
    //test Modifier
    assertTrue(PROTECTED.build().deepEquals(fooBarBuilder.getModifier()));
  }


  @Test
  public void testTransitionAMethod() {
    ASTCDMethod fooBarBuilder = millClass.getCDMethod(8);
    //test Method Name
    assertEquals("aSTTransitionBuilder", fooBarBuilder.getName());
    //test Parameters
    assertTrue(fooBarBuilder.isEmptyCDParameters());
    //test ReturnType
    ASTType returnType = cdTypeFacade.createTypeByDefinition("ASTTransitionBuilder");
    assertDeepEquals(returnType, fooBarBuilder.getReturnType());
    //test Modifier
    assertTrue(PUBLIC_STATIC.build().deepEquals(fooBarBuilder.getModifier()));
  }

  @Test
  public void testProtectedTransitionBuilderMethod() {
    ASTCDMethod fooBarBuilder = millClass.getCDMethod(9);
    //test Method Name
    assertEquals("_aSTTransitionBuilder", fooBarBuilder.getName());
    //test Parameters
    assertTrue(fooBarBuilder.isEmptyCDParameters());
    //test ReturnType
    ASTType returnType = cdTypeFacade.createTypeByDefinition("ASTTransitionBuilder");
    assertDeepEquals(returnType, fooBarBuilder.getReturnType());
    //test Modifier
    assertTrue(PROTECTED.build().deepEquals(fooBarBuilder.getModifier()));
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
    generatorSetup.setOutputDirectory(Paths.get("target/generated-test-sources/de/monticore/codegen/mill").toFile());
    Path generatedFiles = Paths.get("AutomatonMill.java");
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    generatorEngine.generate(CoreTemplates.CLASS, generatedFiles, millClass, millClass);
  }
}
