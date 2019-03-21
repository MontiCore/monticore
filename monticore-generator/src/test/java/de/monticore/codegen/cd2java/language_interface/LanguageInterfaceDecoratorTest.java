package de.monticore.codegen.cd2java.language_interface;

import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java.factories.CDTypeFactory;
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

public class LanguageInterfaceDecoratorTest extends DecoratorTestCase {

  private CDTypeFactory cdTypeFacade;

  private ASTCDInterface languageInterace;

  @Before
  public void setUp() {
    this.cdTypeFacade = CDTypeFactory.getInstance();
    ASTCDCompilationUnit compilationUnit = this.parse("de", "monticore", "codegen", "ast", "Automaton");
    LanguageInteraceDecorator decorator = new LanguageInteraceDecorator();
    this.languageInterace = decorator.decorate(compilationUnit);
  }

  @Test
  public void testMillName() {
    assertEquals("ASTAutomatonNode", languageInterace.getName());
  }

  @Test
  public void testAttributesEmpty() {
    assertTrue(languageInterace.isEmptyCDAttributes());
  }

  @Test
  public void testMethodCount(){
    assertEquals(1, languageInterace.sizeCDMethods());
  }

  @Test
  public void testAcceptMethod() {
    ASTCDMethod method = getMethodBy("accept", languageInterace);
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
    StringBuilder sb = generatorEngine.generate(CoreTemplates.INTERFACE, languageInterace, languageInterace);
    System.out.println(sb.toString());
  }
}
