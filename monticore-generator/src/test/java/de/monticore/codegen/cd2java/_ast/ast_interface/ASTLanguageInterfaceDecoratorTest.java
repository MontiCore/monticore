/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast.ast_interface;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.CdUtilsPrinter;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._ast.ast_class.ASTService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodsBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ASTLanguageInterfaceDecoratorTest extends DecoratorTestCase {

  private de.monticore.types.MCTypeFacade MCTypeFacade;

  private ASTCDInterface languageInterface;

  private ASTCDCompilationUnit originalCompilationUnit;

  private ASTCDCompilationUnit decoratedCompilationUnit;

  private GlobalExtensionManagement glex = new GlobalExtensionManagement();

  @Before
  public void setUp() {
    this.MCTypeFacade = MCTypeFacade.getInstance();
    originalCompilationUnit = this.parse("de", "monticore", "codegen", "ast", "Automaton");
    this.glex.setGlobalValue("service", new AbstractService(originalCompilationUnit));
    this.glex.setGlobalValue("cdPrinter", new CdUtilsPrinter());

    ASTService astService = new ASTService(originalCompilationUnit);
    VisitorService visitorService = new VisitorService(originalCompilationUnit);
    ASTLanguageInterfaceDecorator decorator = new ASTLanguageInterfaceDecorator(astService, visitorService);
    decoratedCompilationUnit = originalCompilationUnit.deepClone();
    this.languageInterface = decorator.decorate(decoratedCompilationUnit);
  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalCompilationUnit, decoratedCompilationUnit);
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMillName() {
    assertEquals("ASTAutomatonNode", languageInterface.getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testAttributesEmpty() {
    assertTrue(languageInterface.getCDAttributeList().isEmpty());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMethodCount() {
    assertEquals(1, languageInterface.getCDMethodList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testAcceptMethod() {
    ASTCDMethod method = getMethodsBy("accept", languageInterface.getCDMethodList()).get(0);
    assertTrue(method.getModifier().isAbstract());
    assertTrue(method.getModifier().isPublic());
    assertTrue(method.getMCReturnType().isPresentMCVoidType()
    );

    assertEquals(1, method.sizeCDParameters());
    assertEquals("visitor", method.getCDParameter(0).getName());
    ASTMCType visitorType = this.MCTypeFacade.createQualifiedType("de.monticore.codegen.ast.automaton._visitor.AutomatonTraverser");
    assertDeepEquals(visitorType, method.getCDParameter(0).getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
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
  
    assertTrue(Log.getFindings().isEmpty());
  }
}
