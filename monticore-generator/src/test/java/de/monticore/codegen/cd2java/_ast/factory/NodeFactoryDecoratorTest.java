/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast.factory;

import static de.monticore.codegen.cd2java.CDModifier.PRIVATE_STATIC;
import static de.monticore.codegen.cd2java.CDModifier.PROTECTED;
import static de.monticore.codegen.cd2java.CDModifier.PROTECTED_STATIC;
import static de.monticore.codegen.cd2java.CDModifier.PUBLIC_STATIC;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.CdUtilsPrinter;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecorationHelper;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.se_rwth.commons.logging.LogStub;

public class NodeFactoryDecoratorTest extends DecoratorTestCase {

  private ASTCDClass factoryClass;

  private GlobalExtensionManagement glex;

  private ASTCDCompilationUnit decoratedCompilationUnit;

  private ASTCDCompilationUnit originalCompilationUnit;

  @Before
  public void setUp() {
    LogStub.init();
    LogStub.enableFailQuick(false);
    this.glex = new GlobalExtensionManagement();

    decoratedCompilationUnit = this.parse("de", "monticore", "codegen", "ast", "Automaton");
    originalCompilationUnit = decoratedCompilationUnit.deepClone();
    this.glex.setGlobalValue("service", new AbstractService(decoratedCompilationUnit));
    this.glex.setGlobalValue("astHelper", DecorationHelper.getInstance());
    this.glex.setGlobalValue("cdPrinter", new CdUtilsPrinter());

    NodeFactoryDecorator decorator = new NodeFactoryDecorator(this.glex, new NodeFactoryService(decoratedCompilationUnit));
    this.factoryClass = decorator.decorate(decoratedCompilationUnit);
  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalCompilationUnit, decoratedCompilationUnit);
  }

  @Test
  public void testFactoryName() {
    assertEquals("AutomatonNodeFactory", factoryClass.getName());
  }

  @Test
  public void testAttributeName() {
    assertEquals("factory", factoryClass.getCDAttributeList().get(0).getName());
    assertEquals("factoryASTAutomaton", factoryClass.getCDAttributeList().get(1).getName());
    assertEquals("factoryASTState", factoryClass.getCDAttributeList().get(2).getName());
    assertEquals("factoryASTTransition", factoryClass.getCDAttributeList().get(3).getName());
  }

  @Test
  public void testAttributeModifier() {
    for (ASTCDAttribute astcdAttribute : factoryClass.getCDAttributeList()) {
      assertTrue(astcdAttribute.isPresentModifier());
      assertTrue(PROTECTED_STATIC.build().deepEquals(astcdAttribute.getModifier()));
    }
  }

  @Test
  public void testConstructor() {
    assertEquals(1, factoryClass.getCDConstructorList().size());
    ASTCDConstructor astcdConstructor = CD4CodeMill.cDConstructorBuilder()
        .setModifier(PROTECTED.build())
        .setName("AutomatonNodeFactory")
        .build();
    assertDeepEquals(astcdConstructor, factoryClass.getCDConstructorList().get(0));
  }

  @Test
  public void testMethodGetFactory() {
    ASTCDMethod method = factoryClass.getCDMethodList().get(0);
    //test name
    assertEquals("getFactory", method.getName());
    //test modifier
    assertTrue(PRIVATE_STATIC.build().deepEquals(method.getModifier()));
    //test parameters
    assertTrue(method.isEmptyCDParameters());
    //test returnType
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals("AutomatonNodeFactory", method.getMCReturnType().getMCType());
  }

  @Test
  public void testMethodCreateASTAutomatonWithoutParameter() {
    ASTCDMethod method = factoryClass.getCDMethodList().get(1);
    //test name
    assertEquals("createASTAutomaton", method.getName());
    //test modifier
    assertTrue(PUBLIC_STATIC.build().deepEquals(method.getModifier()));
    //test parameters
    assertTrue(method.isEmptyCDParameters());
    //test returnType
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals("ASTAutomaton", method.getMCReturnType().getMCType());
  }

  @Test
  public void testMethodDoCreateASTAutomatonWithoutParameter() {
    ASTCDMethod method = factoryClass.getCDMethodList().get(2);
    //test name
    assertEquals("doCreateASTAutomaton", method.getName());
    //test modifier
    assertTrue(PROTECTED.build().deepEquals(method.getModifier()));
    //test parameters
    assertTrue(method.isEmptyCDParameters());
    //test returnType
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals("ASTAutomaton", method.getMCReturnType().getMCType());
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.CLASS, factoryClass, factoryClass);
    // test parsing
    ParserConfiguration configuration = new ParserConfiguration();
    JavaParser parser = new JavaParser(configuration);
    ParseResult parseResult = parser.parse(sb.toString());
    assertTrue(parseResult.isSuccessful());
  }
}
