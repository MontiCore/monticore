package de.monticore.codegen.cd2java.symboltable;

import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java.builder.BuilderDecorator;
import de.monticore.codegen.cd2java.symboltable.SymbolBuilderDecorator;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static de.monticore.codegen.cd2java.DecoratorTestUtil.getClassBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class SymbolBuilderDecoratorTest extends DecoratorTestCase {

  private final GlobalExtensionManagement glex = new GlobalExtensionManagement();

  private ASTCDClass builderClass;

  @Before
  public void setup() {
    LogStub.init();
    ASTCDCompilationUnit ast = parse("de", "monticore", "codegen", "symboltable", "Builder");
    ASTCDClass cdClass = getClassBy("A", ast);

    BuilderDecorator builderDecorator = new BuilderDecorator(glex);
    SymbolBuilderDecorator astNodeBuilderDecorator = new SymbolBuilderDecorator(glex, builderDecorator);
    this.builderClass = astNodeBuilderDecorator.decorate(cdClass);
  }

  @Test
  public void testClassName() {
    assertEquals("ABuilder", builderClass.getName());
  }

  @Test
  public void testSuperClassName() {
    assertFalse(builderClass.getSuperclassOpt().isPresent());
  }

  @Test
  public void testMethods() {
    assertEquals(10, builderClass.getCDMethodList().size());
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.CLASS, builderClass, builderClass);
    System.out.println(sb.toString());
  }
}
