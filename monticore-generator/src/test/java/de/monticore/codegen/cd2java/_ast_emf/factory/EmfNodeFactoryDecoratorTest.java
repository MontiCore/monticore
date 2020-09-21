/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast_emf.factory;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.cd.prettyprint.CD4CodePrinter;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecorationHelper;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._ast.factory.NodeFactoryService;
import de.monticore.codegen.cd2java._ast_emf.EmfService;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import org.junit.Before;
import org.junit.Test;

import static de.monticore.cd.facade.CDModifier.*;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getAttributeBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodBy;
import static org.junit.Assert.*;

public class EmfNodeFactoryDecoratorTest extends DecoratorTestCase {

  private GlobalExtensionManagement glex = new GlobalExtensionManagement();

  private ASTCDClass emfClass;

  @Before
  public void setup() {
    ASTCDCompilationUnit compilationUnit = this.parse("de", "monticore", "codegen", "_ast_emf", "Automata");

    this.glex.setGlobalValue("service", new EmfService(compilationUnit));
    this.glex.setGlobalValue("astHelper", DecorationHelper.getInstance());
    this.glex.setGlobalValue("cdPrinter", new CD4CodePrinter());

    EmfNodeFactoryDecorator decorator = new EmfNodeFactoryDecorator(this.glex, new NodeFactoryService(compilationUnit));

    this.emfClass = decorator.decorate(compilationUnit);
  }

  @Test
  public void testClassName() {
    assertEquals("AutomataNodeFactory", emfClass.getName());
  }


  @Test
  public void testSuperClass() {
    assertTrue(emfClass.isPresentSuperclass());
    assertEquals("org.eclipse.emf.ecore.impl.EFactoryImpl", emfClass.printSuperClass());
  }

  @Test
  public void testAttributeSize() {
    assertEquals(6, emfClass.getCDAttributeList().size());
  }


  @Test
  public void testFactoryAttribute() {
    ASTCDAttribute factoryAttr = getAttributeBy("factory", emfClass);
    assertDeepEquals(PROTECTED_STATIC, factoryAttr.getModifier());
    assertDeepEquals("AutomataNodeFactory", factoryAttr.getMCType());
  }

  @Test
  public void testFactoryAttributes() {
    ASTCDAttribute factoryASTAutomaton = getAttributeBy("factoryASTAutomaton", emfClass);
    assertDeepEquals(PROTECTED_STATIC, factoryASTAutomaton.getModifier());
    assertDeepEquals("AutomataNodeFactory", factoryASTAutomaton.getMCType());

    ASTCDAttribute factoryASTState = getAttributeBy("factoryASTState", emfClass);
    assertDeepEquals(PROTECTED_STATIC, factoryASTState.getModifier());
    assertDeepEquals("AutomataNodeFactory", factoryASTState.getMCType());

    ASTCDAttribute factoryASTTransition = getAttributeBy("factoryASTTransition", emfClass);
    assertDeepEquals(PROTECTED_STATIC, factoryASTTransition.getModifier());
    assertDeepEquals("AutomataNodeFactory", factoryASTTransition.getMCType());

    ASTCDAttribute factoryASTTransitionWithAction = getAttributeBy("factoryASTTransitionWithAction", emfClass);
    assertDeepEquals(PROTECTED_STATIC, factoryASTTransitionWithAction.getModifier());
    assertDeepEquals("AutomataNodeFactory", factoryASTTransitionWithAction.getMCType());

    ASTCDAttribute factoryASTAutName = getAttributeBy("factoryASTAutName", emfClass);
    assertDeepEquals(PROTECTED_STATIC, factoryASTAutName.getModifier());
    assertDeepEquals("AutomataNodeFactory", factoryASTAutName.getMCType());
  }

  @Test(expected = AssertionError.class)
  public void testNoFactoryAttributeForAbstractClass() {
    getAttributeBy("factoryASTAbstractClass", emfClass);
  }

  @Test
  public void testMethodSize() {
    assertFalse(emfClass.getCDMethodList().isEmpty());
    assertEquals(15, emfClass.getCDMethodList().size());
  }

  @Test
  public void testGetFactoryMethod() {
    ASTCDMethod method = getMethodBy("getFactory", emfClass);
    assertDeepEquals(PUBLIC_STATIC, method.getModifier());
    assertDeepEquals("AutomataNodeFactory", method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testCreateMethod() {
    ASTCDMethod method = getMethodBy("create", emfClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals("org.eclipse.emf.ecore.EObject", method.getMCReturnType().getMCType());

    assertEquals(1, method.sizeCDParameters());
    assertEquals("eClass", method.getCDParameter(0).getName());
    assertDeepEquals("org.eclipse.emf.ecore.EClass", method.getCDParameter(0).getMCType());
  }

  @Test
  public void testAutomatonPackageMethod() {
    ASTCDMethod method = getMethodBy("getAutomataPackage", emfClass);
    assertDeepEquals(PACKAGE_PRIVATE, method.getModifier());
    assertDeepEquals("AutomataPackage", method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.CLASS, emfClass, emfClass);
    // test parsing
    ParserConfiguration configuration = new ParserConfiguration();
    JavaParser parser = new JavaParser(configuration);
    ParseResult parseResult = parser.parse(sb.toString());
    assertTrue(parseResult.isSuccessful());
  }

}
