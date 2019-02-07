package de.monticore.codegen.cd2java.mill;

import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.factories.CDTypeFactory;
import de.monticore.codegen.cd2java.factories.ModifierBuilder;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.TypesPrinter;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import de.monticore.umlcd4a.cd4analysis._parser.CD4AnalysisParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MillDecoratorTest {

  private static final String CD = Paths.get("src/test/resources/de/monticore/codegen/mill/Mill.cd").toAbsolutePath().toString();

  private final GlobalExtensionManagement glex = new GlobalExtensionManagement();

  private ASTCDClass millClass;

  private CDTypeFactory cdTypeFactory;


  @Before
  public void setup() throws IOException {
    LogStub.init();
    CD4AnalysisParser parser = new CD4AnalysisParser();
    Optional<ASTCDCompilationUnit> compilationUnit = parser.parse(CD);
    if (!compilationUnit.isPresent()) {
      Log.error(CD + " is not present");
    }

    MillDecorator millDecorator = new MillDecorator(glex);
    this.millClass = millDecorator.decorate(compilationUnit.get().getCDDefinition());
    this.cdTypeFactory = CDTypeFactory.getInstance();
  }

  @Test
  public void testFactoryName() {
    assertEquals("TestMill", millClass.getName());
  }

  @Test
  public void testAttributeName() {
    assertEquals("mill", millClass.getCDAttribute(0).getName());
    assertEquals("millFooBar", millClass.getCDAttribute(1).getName());
    assertEquals("millAutomaton", millClass.getCDAttribute(2).getName());
    assertEquals("millTestA", millClass.getCDAttribute(3).getName());
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
    assertEquals("TestMill", millClass.getCDConstructor(0).getName());
  }

  @Test
  public void testGetMillMethod() {
    ASTCDMethod getMill = millClass.getCDMethod(0);
    //test Method Name
    assertEquals("getMill", getMill.getName());
    //test Parameters
    assertTrue(getMill.isEmptyCDParameters());
    //test ReturnType
    assertEquals("TestMill", TypesPrinter.printReturnType(getMill.getReturnType()));
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
    assertEquals("TestMill", TypesPrinter.printType(initMe.getCDParameter(0).getType()));
    assertEquals("mill", initMe.getCDParameter(0).getName());
    //test ReturnType
    assertTrue(cdTypeFactory.createVoidType().deepEquals(initMe.getReturnType()));
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
    assertTrue(cdTypeFactory.createVoidType().deepEquals(init.getReturnType()));
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
    assertTrue(cdTypeFactory.createVoidType().deepEquals(init.getReturnType()));
    //test Modifier
    assertTrue(ModifierBuilder.builder().Public().Static().build().deepEquals(init.getModifier()));
  }

  @Test
  public void testFooBarBuilderMethod() {
    ASTCDMethod fooBarBuilder = millClass.getCDMethod(4);
    //test Method Name
    assertEquals("fooBarBuilder", fooBarBuilder.getName());
    //test Parameters
    assertTrue(fooBarBuilder.isEmptyCDParameters());
    //test ReturnType
    assertEquals("FooBarBuilder", TypesPrinter.printReturnType(fooBarBuilder.getReturnType()));
    //test Modifier
    assertTrue(ModifierBuilder.builder().Public().Static().build().deepEquals(fooBarBuilder.getModifier()));
  }

  @Test
  public void testProtectedFooBarBuilderMethod() {
    ASTCDMethod fooBarBuilder = millClass.getCDMethod(5);
    //test Method Name
    assertEquals("_fooBarBuilder", fooBarBuilder.getName());
    //test Parameters
    assertTrue(fooBarBuilder.isEmptyCDParameters());
    //test ReturnType
    assertEquals("FooBarBuilder", TypesPrinter.printReturnType(fooBarBuilder.getReturnType()));
    //test Modifier
    assertTrue(ModifierBuilder.builder().Protected().build().deepEquals(fooBarBuilder.getModifier()));
  }


  @Test
  public void testFooBarAutomatonMethod() {
    ASTCDMethod fooBarBuilder = millClass.getCDMethod(6);
    //test Method Name
    assertEquals("automatonBuilder", fooBarBuilder.getName());
    //test Parameters
    assertTrue(fooBarBuilder.isEmptyCDParameters());
    //test ReturnType
    assertEquals("AutomatonBuilder", TypesPrinter.printReturnType(fooBarBuilder.getReturnType()));
    //test Modifier
    assertTrue(ModifierBuilder.builder().Public().Static().build().deepEquals(fooBarBuilder.getModifier()));
  }

  @Test
  public void testProtectedAutomatonBuilderMethod() {
    ASTCDMethod fooBarBuilder = millClass.getCDMethod(7);
    //test Method Name
    assertEquals("_automatonBuilder", fooBarBuilder.getName());
    //test Parameters
    assertTrue(fooBarBuilder.isEmptyCDParameters());
    //test ReturnType
    assertEquals("AutomatonBuilder", TypesPrinter.printReturnType(fooBarBuilder.getReturnType()));
    //test Modifier
    assertTrue(ModifierBuilder.builder().Protected().build().deepEquals(fooBarBuilder.getModifier()));
  }


  @Test
  public void testFooBarTestAMethod() {
    ASTCDMethod fooBarBuilder = millClass.getCDMethod(8);
    //test Method Name
    assertEquals("testABuilder", fooBarBuilder.getName());
    //test Parameters
    assertTrue(fooBarBuilder.isEmptyCDParameters());
    //test ReturnType
    assertEquals("TestABuilder", TypesPrinter.printReturnType(fooBarBuilder.getReturnType()));
    //test Modifier
    assertTrue(ModifierBuilder.builder().Public().Static().build().deepEquals(fooBarBuilder.getModifier()));
  }

  @Test
  public void testProtectedTestABuilderMethod() {
    ASTCDMethod fooBarBuilder = millClass.getCDMethod(9);
    //test Method Name
    assertEquals("_testABuilder", fooBarBuilder.getName());
    //test Parameters
    assertTrue(fooBarBuilder.isEmptyCDParameters());
    //test ReturnType
    assertEquals("TestABuilder", TypesPrinter.printReturnType(fooBarBuilder.getReturnType()));
    //test Modifier
    assertTrue(ModifierBuilder.builder().Protected().build().deepEquals(fooBarBuilder.getModifier()));
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
    Path generatedFiles = Paths.get("TestMill.java");
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    generatorEngine.generate(CoreTemplates.CLASS, generatedFiles, millClass, millClass);
  }
}
