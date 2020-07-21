/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast_emf.emf_package;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.DecorationHelper;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._ast_emf.EmfService;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import org.junit.Before;
import org.junit.Test;

import static de.monticore.cd.facade.CDModifier.PACKAGE_PRIVATE;
import static de.monticore.cd.facade.CDModifier.PACKAGE_PRIVATE_ABSTRACT;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertInt;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getAttributeBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodBy;
import static org.junit.Assert.*;

public class PackageInterfaceDecoratorTest extends DecoratorTestCase {

  private GlobalExtensionManagement glex = new GlobalExtensionManagement();

  private ASTCDInterface packageInterface;

  private ASTCDDefinition astcdDefinition;

  @Before
  public void setup() {
    ASTCDCompilationUnit ast = this.parse("de", "monticore", "codegen", "_ast_emf", "Automata");

    astcdDefinition = ast.getCDDefinition();

    this.glex.setGlobalValue("service", new AbstractService(ast));
    this.glex.setGlobalValue("astHelper", DecorationHelper.getInstance());
    PackageInterfaceDecorator decorator = new PackageInterfaceDecorator(this.glex, new EmfService(ast));
    packageInterface = decorator.decorate(ast);
  }

  @Test
  public void testClassName() {
    assertEquals("AutomataPackage", packageInterface.getName());
  }

  @Test
  public void testSuperInterface() {
    assertEquals(1, packageInterface.sizeInterfaces());
    assertDeepEquals("de.monticore.emf._ast.ASTEPackage", packageInterface.getInterface(0));
  }


  @Test
  public void testAttributeSize() {
    assertEquals(28, packageInterface.getCDAttributeList().size());
  }

  @Test
  public void testMethodSize() {
    assertFalse(packageInterface.getCDMethodList().isEmpty());
    assertEquals(21, packageInterface.getCDMethodList().size());
  }

  @Test
  public void testENameAttribute() {
    ASTCDAttribute attribute = getAttributeBy("eNAME", packageInterface);
    assertTrue(attribute.isPresentModifier());
    assertDeepEquals(PACKAGE_PRIVATE, attribute.getModifier());
    assertDeepEquals(String.class, attribute.getMCType());
  }

  @Test
  public void testENSUriAttribute() {
    ASTCDAttribute attribute = getAttributeBy("eNS_URI", packageInterface);
    assertTrue(attribute.isPresentModifier());
    assertDeepEquals(PACKAGE_PRIVATE, attribute.getModifier());
    assertDeepEquals(String.class, attribute.getMCType());
  }

  @Test
  public void testENSPrefixAttribute() {
    ASTCDAttribute attribute = getAttributeBy("eNS_PREFIX", packageInterface);
    assertTrue(attribute.isPresentModifier());
    assertDeepEquals(PACKAGE_PRIVATE, attribute.getModifier());
    assertDeepEquals(String.class, attribute.getMCType());
  }

  @Test
  public void testEInstanceAttribute() {
    ASTCDAttribute attribute = getAttributeBy("eINSTANCE", packageInterface);
    assertTrue(attribute.isPresentModifier());
    assertDeepEquals(PACKAGE_PRIVATE, attribute.getModifier());
    assertDeepEquals("AutomataPackage", attribute.getMCType());
  }

  @Test
  public void testConstantsAttribute() {
    ASTCDAttribute attribute = getAttributeBy("ConstantsAutomata", packageInterface);
    assertTrue(attribute.isPresentModifier());
    assertDeepEquals(PACKAGE_PRIVATE, attribute.getModifier());
    assertInt(attribute.getMCType());
  }

  @Test
  public void testASTAutomatonAttribute() {
    ASTCDAttribute attribute = getAttributeBy("ASTAutomaton", packageInterface);
    assertTrue(attribute.isPresentModifier());
    assertDeepEquals(PACKAGE_PRIVATE, attribute.getModifier());
    assertInt(attribute.getMCType());
  }

  @Test
  public void testASTStateAttribute() {
    ASTCDAttribute attribute = getAttributeBy("ASTState", packageInterface);
    assertTrue(attribute.isPresentModifier());
    assertDeepEquals(PACKAGE_PRIVATE, attribute.getModifier());
    assertInt(attribute.getMCType());
  }

  @Test
  public void testASTTransitionAttribute() {
    ASTCDAttribute attribute = getAttributeBy("ASTTransition", packageInterface);
    assertTrue(attribute.isPresentModifier());
    assertDeepEquals(PACKAGE_PRIVATE, attribute.getModifier());
    assertInt(attribute.getMCType());
  }

  @Test
  public void testASTBodyExtAttribute() {
    //ASTBodyExt is interface but has to be generated as well
    ASTCDAttribute attribute = getAttributeBy("ASTBodyExt", packageInterface);
    assertTrue(attribute.isPresentModifier());
    assertDeepEquals(PACKAGE_PRIVATE, attribute.getModifier());
    assertInt(attribute.getMCType());
  }

  @Test
  public void testASTTransitionWithActionAttribute() {
    ASTCDAttribute attribute = getAttributeBy("ASTTransitionWithAction", packageInterface);
    assertTrue(attribute.isPresentModifier());
    assertDeepEquals(PACKAGE_PRIVATE, attribute.getModifier());
    assertInt(attribute.getMCType());
  }

  @Test
  public void testASTAutNameAttribute() {
    ASTCDAttribute attribute = getAttributeBy("ASTAutName", packageInterface);
    assertTrue(attribute.isPresentModifier());
    assertDeepEquals(PACKAGE_PRIVATE, attribute.getModifier());
    assertInt(attribute.getMCType());
  }

  @Test
  public void testASTAutomaton_NameAttribute() {
    ASTCDAttribute attribute = getAttributeBy("ASTAutomaton_Name", packageInterface);
    assertTrue(attribute.isPresentModifier());
    assertDeepEquals(PACKAGE_PRIVATE, attribute.getModifier());
    assertInt(attribute.getMCType());
  }

  @Test
  public void testASTAutomaton_StatesAttribute() {
    ASTCDAttribute attribute = getAttributeBy("ASTAutomaton_States", packageInterface);
    assertTrue(attribute.isPresentModifier());
    assertDeepEquals(PACKAGE_PRIVATE, attribute.getModifier());
    assertInt(attribute.getMCType());
  }

  @Test
  public void testASTAutomaton_TransitionsAttribute() {
    ASTCDAttribute attribute = getAttributeBy("ASTAutomaton_Transitions", packageInterface);
    assertTrue(attribute.isPresentModifier());
    assertDeepEquals(PACKAGE_PRIVATE, attribute.getModifier());
    assertInt(attribute.getMCType());
  }

  @Test
  public void testASTState_NameAttribute() {
    ASTCDAttribute attribute = getAttributeBy("ASTState_Name", packageInterface);
    assertTrue(attribute.isPresentModifier());
    assertDeepEquals(PACKAGE_PRIVATE, attribute.getModifier());
    assertInt(attribute.getMCType());
  }

  @Test
  public void testASTState_StatesAttribute() {
    ASTCDAttribute attribute = getAttributeBy("ASTState_States", packageInterface);
    assertTrue(attribute.isPresentModifier());
    assertDeepEquals(PACKAGE_PRIVATE, attribute.getModifier());
    assertInt(attribute.getMCType());
  }

  @Test
  public void testASTState_TransitionsAttribute() {
    ASTCDAttribute attribute = getAttributeBy("ASTState_Transitions", packageInterface);
    assertTrue(attribute.isPresentModifier());
    assertDeepEquals(PACKAGE_PRIVATE, attribute.getModifier());
    assertInt(attribute.getMCType());
  }

  @Test
  public void testASTTransition_FromAttribute() {
    ASTCDAttribute attribute = getAttributeBy("ASTTransition_From", packageInterface);
    assertTrue(attribute.isPresentModifier());
    assertDeepEquals(PACKAGE_PRIVATE, attribute.getModifier());
    assertInt(attribute.getMCType());
  }

  @Test
  public void testASTTransition_InputAttribute() {
    ASTCDAttribute attribute = getAttributeBy("ASTTransition_Input", packageInterface);
    assertTrue(attribute.isPresentModifier());
    assertDeepEquals(PACKAGE_PRIVATE, attribute.getModifier());
    assertInt(attribute.getMCType());
  }

  @Test
  public void testASTTransition_ToAttribute() {
    ASTCDAttribute attribute = getAttributeBy("ASTTransition_To", packageInterface);
    assertTrue(attribute.isPresentModifier());
    assertDeepEquals(PACKAGE_PRIVATE, attribute.getModifier());
    assertInt(attribute.getMCType());
  }

  @Test
  public void testInheritedAttributeExists() {
    ASTCDAttribute attribute = getAttributeBy("ASTAutName_Input", packageInterface);
    assertTrue(attribute.isPresentModifier());
    assertDeepEquals(PACKAGE_PRIVATE, attribute.getModifier());
    assertInt(attribute.getMCType());
  }

  @Test
  public void testGetAutomatonFactoryMethod() {
    ASTCDMethod method = getMethodBy("getAutomataFactory", packageInterface);
    assertDeepEquals(PACKAGE_PRIVATE_ABSTRACT, method.getModifier());
    assertDeepEquals("AutomataNodeFactory", method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGetConstantsAutomatonMethod() {
    ASTCDMethod method = getMethodBy("getConstantsAutomata", packageInterface);
    assertDeepEquals(PACKAGE_PRIVATE_ABSTRACT, method.getModifier());
    assertDeepEquals("org.eclipse.emf.ecore.EEnum", method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGetASTAutomatonMethod() {
    ASTCDMethod method = getMethodBy("getASTAutomaton", packageInterface);
    assertDeepEquals(PACKAGE_PRIVATE_ABSTRACT, method.getModifier());
    assertDeepEquals("org.eclipse.emf.ecore.EClass",  method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGetASTStateMethod() {
    ASTCDMethod method = getMethodBy("getASTState", packageInterface);
    assertDeepEquals(PACKAGE_PRIVATE_ABSTRACT, method.getModifier());
    assertDeepEquals("org.eclipse.emf.ecore.EClass",  method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGetASTTransitionMethod() {
    ASTCDMethod method = getMethodBy("getASTTransition", packageInterface);
    assertDeepEquals(PACKAGE_PRIVATE_ABSTRACT, method.getModifier());
    assertDeepEquals("org.eclipse.emf.ecore.EClass",  method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGetASTTransitionWithActionMethod() {
    ASTCDMethod method = getMethodBy("getASTTransitionWithAction", packageInterface);
    assertDeepEquals(PACKAGE_PRIVATE_ABSTRACT, method.getModifier());
    assertDeepEquals("org.eclipse.emf.ecore.EClass",  method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGetASTAutNameMethod() {
    ASTCDMethod method = getMethodBy("getASTAutName", packageInterface);
    assertDeepEquals(PACKAGE_PRIVATE_ABSTRACT, method.getModifier());
    assertDeepEquals("org.eclipse.emf.ecore.EClass",  method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGetASTBodyExtMethod() {
    ASTCDMethod method = getMethodBy("getASTBodyExt", packageInterface);
    assertDeepEquals(PACKAGE_PRIVATE_ABSTRACT, method.getModifier());
    assertDeepEquals("org.eclipse.emf.ecore.EClass",  method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGetASTAutomaton_NameMethod() {
    ASTCDMethod method = getMethodBy("getASTAutomaton_Name", packageInterface);
    assertDeepEquals(PACKAGE_PRIVATE_ABSTRACT, method.getModifier());
    assertDeepEquals("org.eclipse.emf.ecore.EAttribute",  method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGetASTAutomaton_StatesMethod() {
    ASTCDMethod method = getMethodBy("getASTAutomaton_States", packageInterface);
    assertDeepEquals(PACKAGE_PRIVATE_ABSTRACT, method.getModifier());
    assertDeepEquals("org.eclipse.emf.ecore.EReference",  method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGetASTAutomaton_TransitionsMethod() {
    ASTCDMethod method = getMethodBy("getASTAutomaton_Transitions", packageInterface);
    assertDeepEquals(PACKAGE_PRIVATE_ABSTRACT, method.getModifier());
    assertDeepEquals("org.eclipse.emf.ecore.EReference",  method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGetInterfaceAttributeMethod() {
    ASTCDMethod method = getMethodBy("getASTBodyExt_Varname", packageInterface);
    assertDeepEquals(PACKAGE_PRIVATE_ABSTRACT, method.getModifier());
    assertDeepEquals("org.eclipse.emf.ecore.EAttribute",  method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testNoInheritedAttributeMethod() {
    assertTrue(packageInterface.getCDMethodList()
        .stream()
        .noneMatch(m -> m.getName().equals("getASTAutName_Input")));
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate("_ast_emf.emf_package.EmfPackage", packageInterface, packageInterface, astcdDefinition);
    // test parsing
    ParserConfiguration configuration = new ParserConfiguration();
    JavaParser parser = new JavaParser(configuration);
    ParseResult parseResult = parser.parse(sb.toString());
    assertTrue(parseResult.isSuccessful());
  }

}
