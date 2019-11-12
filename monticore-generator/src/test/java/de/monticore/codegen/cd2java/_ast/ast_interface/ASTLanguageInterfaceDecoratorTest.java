/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast.ast_interface;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.ASTCDInterface;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._ast.ast_class.ASTService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.cd2java.factories.MCTypeFacade;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import org.junit.Before;
import org.junit.Test;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ASTLanguageInterfaceDecoratorTest extends DecoratorTestCase {

  private MCTypeFacade MCTypeFacade;

  private ASTCDInterface languageInterface;

  private ASTCDCompilationUnit originalCompilationUnit;

  private ASTCDCompilationUnit decoratedCompilationUnit;

  private GlobalExtensionManagement glex = new GlobalExtensionManagement();

  @Before
  public void setUp() {
    this.MCTypeFacade = MCTypeFacade.getInstance();
    originalCompilationUnit = this.parse("de", "monticore", "codegen", "ast", "Automaton");
    this.glex.setGlobalValue("service", new AbstractService(originalCompilationUnit));

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
  public void testMethodCount() {
    assertEquals(1, languageInterface.sizeCDMethods());
  }

  @Test
  public void testAcceptMethod() {
    ASTCDMethod method = getMethodBy("accept", languageInterface);
    assertTrue(method.getModifier().isAbstract());
    assertTrue(method.getModifier().isPublic());
    assertTrue(method.getMCReturnType().isPresentMCVoidType()
    );

    assertEquals(1, method.sizeCDParameters());
    assertEquals("visitor", method.getCDParameter(0).getName());
    ASTMCType visitorType = this.MCTypeFacade.createQualifiedType("de.monticore.codegen.ast.automaton._visitor.AutomatonVisitor");
    assertDeepEquals(visitorType, method.getCDParameter(0).getMCType());
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.INTERFACE, languageInterface, languageInterface);
    // test parsing
    ParserConfiguration configuration = new ParserConfiguration();
    JavaParser parser = new JavaParser(configuration);
    ParseResult parseResult = parser.parse(sb.toString());
    assertTrue(parseResult.isSuccessful());
  }
}
