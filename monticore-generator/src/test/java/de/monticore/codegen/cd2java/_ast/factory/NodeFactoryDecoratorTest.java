/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast.factory;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import de.monticore.cd.cd4analysis.CD4AnalysisMill;
import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.prettyprint.CD4CodePrinter;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecorationHelper;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import static de.monticore.cd.facade.CDModifier.*;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
    this.glex.setGlobalValue("cdPrinter", new CD4CodePrinter());

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
    assertEquals("factory", factoryClass.getCDAttributes(0).getName());
    assertEquals("factoryASTAutomaton", factoryClass.getCDAttributes(1).getName());
    assertEquals("factoryASTState", factoryClass.getCDAttributes(2).getName());
    assertEquals("factoryASTTransition", factoryClass.getCDAttributes(3).getName());
  }

  @Test
  public void testAttributeModifier() {
    for (ASTCDAttribute astcdAttribute : factoryClass.getCDAttributesList()) {
      assertTrue(astcdAttribute.isPresentModifier());
      assertTrue(PROTECTED_STATIC.build().deepEquals(astcdAttribute.getModifier()));
    }
  }

  @Test
  public void testConstructor() {
    assertEquals(1, factoryClass.sizeCDConstructors());
    ASTCDConstructor astcdConstructor = CD4AnalysisMill.cDConstructorBuilder()
        .setModifier(PROTECTED.build())
        .setName("AutomatonNodeFactory")
        .build();
    assertDeepEquals(astcdConstructor, factoryClass.getCDConstructors(0));
  }

  @Test
  public void testMethodGetFactory() {
    ASTCDMethod method = factoryClass.getCDMethods(0);
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
    ASTCDMethod method = factoryClass.getCDMethods(1);
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
    ASTCDMethod method = factoryClass.getCDMethods(2);
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
