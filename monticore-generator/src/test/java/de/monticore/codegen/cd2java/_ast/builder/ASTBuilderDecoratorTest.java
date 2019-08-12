/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast.builder;

import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.prettyprint.CD4CodePrinter;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._ast.ast_class.ASTService;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.codegen.cd2java.methods.AccessorDecorator;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getClassBy;
import static org.junit.Assert.assertEquals;

public class ASTBuilderDecoratorTest extends DecoratorTestCase {

  private final GlobalExtensionManagement glex = new GlobalExtensionManagement();

  private ASTCDClass builderClass;

  private ASTCDCompilationUnit decoratedCompilationUnit;

  private ASTCDCompilationUnit originalCompilationUnit;

  @Before
  public void setup() {
    LogStub.init();
    LogStub.enableFailQuick(false);
    decoratedCompilationUnit = parse("de", "monticore", "codegen", "ast", "Builder");
    originalCompilationUnit = decoratedCompilationUnit.deepClone();

    ASTCDClass cdClass = getClassBy("A", decoratedCompilationUnit);
    this.glex.setGlobalValue("service", new AbstractService(decoratedCompilationUnit));
    this.glex.setGlobalValue("astHelper", new DecorationHelper());
    this.glex.setGlobalValue("cdPrinter", new CD4CodePrinter());

    AccessorDecorator methodDecorator = new AccessorDecorator(glex);
    BuilderDecorator builderDecorator = new BuilderDecorator(glex, methodDecorator, new ASTService(decoratedCompilationUnit));
    ASTBuilderDecorator builderASTNodeDecorator = new ASTBuilderDecorator(glex, builderDecorator);
    this.builderClass = builderASTNodeDecorator.decorate(cdClass);
  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalCompilationUnit, decoratedCompilationUnit);
  }

  @Test
  public void testClassName() {
    assertEquals("ABuilder", builderClass.getName());
  }

  @Test
  public void testSuperClassName() {
    assertEquals("de.monticore.ast.ASTNodeBuilder<ABuilder>", builderClass.printSuperClass());
  }

  @Test
  public void testMethods() {
    assertEquals(47, builderClass.getCDMethodList().size());
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.CLASS, builderClass, builderClass);
    // TODO Check System.out.println(sb.toString());
  }
}
