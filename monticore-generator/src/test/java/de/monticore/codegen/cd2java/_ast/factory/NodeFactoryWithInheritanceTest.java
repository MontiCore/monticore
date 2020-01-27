/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast.factory;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
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

public class NodeFactoryWithInheritanceTest extends DecoratorTestCase {

  private ASTCDClass factoryClass;

  private GlobalExtensionManagement glex;

  @Before
  public void setUp() {
    LogStub.init();
    LogStub.enableFailQuick(false);
    this.glex = new GlobalExtensionManagement();

    ASTCDCompilationUnit compilationUnit = this.parse("de", "monticore", "codegen", "factory", "CGrammar");
    this.glex.setGlobalValue("service", new AbstractService(compilationUnit));
    this.glex.setGlobalValue("astHelper", DecorationHelper.getInstance());
    this.glex.setGlobalValue("cdPrinter", new CD4CodePrinter());

    NodeFactoryDecorator decorator = new NodeFactoryDecorator(this.glex, new NodeFactoryService(compilationUnit));
    this.factoryClass = decorator.decorate(compilationUnit);
  }

  @Test
  public void testFactoryName() {
    assertEquals("CGrammarNodeFactory", factoryClass.getName());
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
      assertTrue(PROTECTED_STATIC.build().deepEquals(astcdAttribute.getModifier()));
    }
  }

  @Test
  public void testConstructor() {
    assertEquals(1, factoryClass.sizeCDConstructors());
    ASTCDConstructor astcdConstructor = CD4AnalysisMill.cDConstructorBuilder()
        .setModifier(PROTECTED.build())
        .setName("CGrammarNodeFactory")
        .build();
    assertDeepEquals(astcdConstructor, factoryClass.getCDConstructor(0));
  }

  @Test
  public void testMethodGetFactory() {
    ASTCDMethod method = factoryClass.getCDMethod(0);
    //test name
    assertEquals("getFactory", method.getName());
    //test modifier
    assertTrue(PRIVATE_STATIC.build().deepEquals(method.getModifier()));
    //test parameters
    assertTrue(method.isEmptyCDParameters());
    //test returnType
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals("CGrammarNodeFactory", method.getMCReturnType().getMCType());
  }

  @Test
  public void testMethodCreateDelegateASTC() {
    ASTCDMethod method = factoryClass.getCDMethod(5);
    //test name
    assertEquals("createASTB", method.getName());
    //test modifier
    assertTrue(PUBLIC_STATIC.build().deepEquals(method.getModifier()));
    //test parameters
    assertTrue(method.isEmptyCDParameters());
    //test returnType
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals("de.monticore.codegen.factory.bgrammar._ast.ASTB", method.getMCReturnType().getMCType());
  }

  @Test
  public void testMethodCreateDelegateASTFoo() {
    ASTCDMethod method = factoryClass.getCDMethod(6);
    //test name
    assertEquals("createASTFoo", method.getName());
    //test modifier
    assertTrue(PUBLIC_STATIC.build().deepEquals(method.getModifier()));
    //test parameters
    assertTrue(method.isEmptyCDParameters());
    //test returnType
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals("de.monticore.codegen.factory.agrammar._ast.ASTFoo", method.getMCReturnType().getMCType());
  }

  @Test
  public void testMethodCreateDelegateASTBar() {
    ASTCDMethod method = factoryClass.getCDMethod(7);
    //test name
    assertEquals("createASTBar", method.getName());
    //test modifier
    assertTrue(PUBLIC_STATIC.build().deepEquals(method.getModifier()));
    //test parameters
    assertTrue(method.isEmptyCDParameters());
    //test returnType
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals("de.monticore.codegen.factory.agrammar._ast.ASTBar", method.getMCReturnType().getMCType());
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
