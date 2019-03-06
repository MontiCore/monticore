package de.monticore.codegen.cd2java.ast_new;

import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertOptionalOf;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getAttributeBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getClassBy;
import static de.monticore.codegen.cd2java.factories.CDModifier.PROTECTED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ASTScopeDecoratorTest extends DecoratorTestCase {

  private GlobalExtensionManagement glex = new GlobalExtensionManagement();

  private ASTCDClass astClass;

  @Before
  public void setup() throws IOException {
    this.glex.setGlobalValue("astHelper", new DecorationHelper());
    ASTCDCompilationUnit ast = this.parse("de", "monticore", "codegen", "ast", "AST");
    ASTScopeDecorator decorator = new ASTScopeDecorator(this.glex, ast);
    ASTCDClass clazz = getClassBy("A", ast);
    this.astClass = decorator.decorate(clazz);
  }

  @Test
  public void testClass() {
    assertEquals("A", astClass.getName());
  }

  @Test
  public void testAttributes() {
    assertFalse(astClass.isEmptyCDAttributes());
    assertEquals(1, astClass.sizeCDAttributes());
  }

  @Test
  public void testScopeAttribute() {
    ASTCDAttribute symbolAttribute = getAttributeBy("aSTScope", astClass);
    assertDeepEquals(PROTECTED, symbolAttribute.getModifier());
    assertOptionalOf("de.monticore.codegen.ast.ast._symboltable.ASTScope", symbolAttribute.getType());
  }

  @Test
  public void testMethods() {
    assertEquals(6, astClass.getCDMethodList().size());
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
