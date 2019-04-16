package de.monticore.codegen.cd2java.ast_interface;

import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java.ast_new.ASTService;
import de.monticore.codegen.cd2java.factories.CDTypeFacade;
import de.monticore.codegen.cd2java.visitor_new.VisitorService;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDInterface;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import org.junit.Before;
import org.junit.Test;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertVoid;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ASTLanguageInterfaceDecoratorTest extends DecoratorTestCase {

  private CDTypeFacade cdTypeFacade;

  private ASTCDInterface languageInterface;

  private ASTCDCompilationUnit originalCompilationUnit;

  private ASTCDCompilationUnit decoratedCompilationUnit;

  @Before
  public void setUp() {
    this.cdTypeFacade = CDTypeFacade.getInstance();
    originalCompilationUnit = this.parse("de", "monticore", "codegen", "ast", "Automaton");
    ASTService astService = new ASTService(originalCompilationUnit);
    VisitorService visitorService = new VisitorService(originalCompilationUnit);
    ASTLanguageInterfaceDecorator decorator = new ASTLanguageInterfaceDecorator(astService, visitorService);
    decoratedCompilationUnit = originalCompilationUnit.deepClone();
    this.languageInterface = decorator.decorate(decoratedCompilationUnit);
  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalCompilationUnit, decoratedCompilationUnit);
  }

  @Test
  public void testMillName() {
    assertEquals("ASTAutomatonNode", languageInterface.getName());
  }

  @Test
  public void testAttributesEmpty() {
    assertTrue(languageInterface.isEmptyCDAttributes());
  }

  @Test
  public void testMethodCount(){
    assertEquals(1, languageInterface.sizeCDMethods());
  }

  @Test
  public void testAcceptMethod() {
    ASTCDMethod method = getMethodBy("accept", languageInterface);
    assertTrue(method.getModifier().isAbstract());
    assertTrue(method.getModifier().isPublic());
    assertVoid(method.getReturnType());

    assertEquals(1, method.sizeCDParameters());
    assertEquals("visitor", method.getCDParameter(0).getName());
    ASTType visitorType = this.cdTypeFacade.createSimpleReferenceType("de.monticore.codegen.ast.automaton._visitor.AutomatonVisitor");
    assertDeepEquals(visitorType, method.getCDParameter(0).getType());
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(new GlobalExtensionManagement());
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.INTERFACE, languageInterface, languageInterface);
    System.out.println(sb.toString());
  }
}
