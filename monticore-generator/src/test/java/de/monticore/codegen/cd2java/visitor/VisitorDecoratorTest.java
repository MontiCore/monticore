package de.monticore.codegen.cd2java.visitor;

import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java.factories.CDParameterFactory;
import de.monticore.codegen.cd2java.factories.CDTypeFactory;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.codegen.cd2java.visitor_new.VisitorDecorator;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDInterface;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class VisitorDecoratorTest extends DecoratorTestCase {

  private CDTypeFactory cdTypeFacade;

  private CDParameterFactory cdParameterFacade;

  private ASTCDInterface visitorClass;

  private GlobalExtensionManagement glex;

  @Before
  public void setUp() {
    this.glex = new GlobalExtensionManagement();
    this.cdTypeFacade = CDTypeFactory.getInstance();
    this.cdParameterFacade = CDParameterFactory.getInstance();

    this.glex.setGlobalValue("astHelper", new DecorationHelper());
    ASTCDCompilationUnit compilationUnit = this.parse("de", "monticore", "codegen", "ast", "Automaton");
    this.glex.setGlobalValue("genHelper", new DecorationHelper());
    VisitorDecorator decorator = new VisitorDecorator(this.glex);
    this.visitorClass = decorator.decorate(compilationUnit);
  }

  @Test
  public void testFactoryName() {
    assertEquals("AutomatonVisitor", visitorClass.getName());
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.INTERFACE, visitorClass, visitorClass);
    System.out.println(sb.toString());
  }
}
