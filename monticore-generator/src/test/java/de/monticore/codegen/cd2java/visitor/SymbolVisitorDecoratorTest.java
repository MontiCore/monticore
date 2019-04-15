package de.monticore.codegen.cd2java.visitor;

import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java.factories.CDTypeFacade;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.codegen.cd2java.visitor_new.SymbolVisitorDecorator;
import de.monticore.codegen.cd2java.visitor_new.VisitorDecorator;
import de.monticore.codegen.cd2java.visitor_new.VisitorService;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDInterface;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SymbolVisitorDecoratorTest extends DecoratorTestCase {

  private CDTypeFacade cdTypeFacade;

  private ASTCDInterface visitorInterface;

  private GlobalExtensionManagement glex;

  @Before
  public void setUp() {
    this.glex = new GlobalExtensionManagement();
    this.cdTypeFacade = CDTypeFacade.getInstance();

    this.glex.setGlobalValue("astHelper", new DecorationHelper());
    ASTCDCompilationUnit compilationUnit = this.parse("de", "monticore", "codegen", "ast", "SymbolTest");
    this.glex.setGlobalValue("genHelper", new DecorationHelper());
    SymbolVisitorDecorator decorator = new SymbolVisitorDecorator(this.glex, new VisitorDecorator(this.glex, new VisitorService(compilationUnit)), new VisitorService(compilationUnit));
    this.visitorInterface = decorator.decorate(compilationUnit);
  }

  @Test
  public void testVisitorName() {
    assertEquals("SymbolTestVisitor", visitorInterface.getName());
  }

  @Test
  public void testAttributesEmpty() {
    assertTrue(visitorInterface.isEmptyCDAttributes());
  }

  @Test
  public void testMethodCount() {
    assertEquals(12, visitorInterface.sizeCDMethods());
  }


  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.INTERFACE, visitorInterface, visitorInterface);
    System.out.println(sb.toString());
  }
}
