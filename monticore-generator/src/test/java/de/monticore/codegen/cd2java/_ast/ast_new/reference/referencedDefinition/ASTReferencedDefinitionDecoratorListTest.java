package de.monticore.codegen.cd2java._ast.ast_new.reference.referencedDefinition;

import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._ast.ast_class.reference.referencedDefinition.ASTReferencedDefinitionDecorator;
import de.monticore.codegen.cd2java._ast.ast_class.reference.referencedDefinition.referencedDefinitionMethodDecorator.ReferencedDefinitionAccessorDecorator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java.factories.CDTypeFacade;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import org.junit.Before;
import org.junit.Test;

import static de.monticore.codegen.cd2java.DecoratorTestUtil.getClassBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ASTReferencedDefinitionDecoratorListTest extends DecoratorTestCase {

  private GlobalExtensionManagement glex = new GlobalExtensionManagement();

  private ASTCDClass astClass;

  private CDTypeFacade cdTypeFacade = CDTypeFacade.getInstance();

  @Before
  public void setup() {
    this.cdTypeFacade = CDTypeFacade.getInstance();
    ASTCDCompilationUnit ast = this.parse("de", "monticore", "codegen", "ast", "ReferencedSymbol");

    this.glex.setGlobalValue("astHelper", new DecorationHelper());
    this.glex.setGlobalValue("service", new AbstractService(ast));

    SymbolTableService symbolTableService = new SymbolTableService(ast);
    ASTReferencedDefinitionDecorator decorator = new ASTReferencedDefinitionDecorator(this.glex, new ReferencedDefinitionAccessorDecorator(glex, symbolTableService), symbolTableService);
    ASTCDClass clazz = getClassBy("ASTBarList", ast);
    this.astClass = decorator.decorate(clazz);
  }

  @Test
  public void testClass() {
    assertEquals("ASTBarList", astClass.getName());
  }

  @Test
  public void testAttributes() {
    assertFalse(astClass.isEmptyCDAttributes());
    assertEquals(1, astClass.sizeCDAttributes());
  }

  @Test
  public void testMethods() {
    assertEquals(19, astClass.getCDMethodList().size());
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.CLASS, astClass, astClass);
    System.out.println(sb.toString());
  }
}
