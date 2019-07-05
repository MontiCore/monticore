package de.monticore.codegen.cd2java._ast_emf.emf_package;

import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDInterface;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import org.junit.Before;
import org.junit.Test;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertInt;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getAttributeBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodBy;
import static de.monticore.codegen.cd2java.factories.CDModifier.PACKAGE_PRIVATE;
import static de.monticore.codegen.cd2java.factories.CDModifier.PACKAGE_PRIVATE_ABSTRACT;
import static org.junit.Assert.*;

public class PackageInterfaceDecoratorTest extends DecoratorTestCase {

  private GlobalExtensionManagement glex = new GlobalExtensionManagement();

  private ASTCDInterface packageInterface;

  @Before
  public void setup() {
    ASTCDCompilationUnit ast = this.parse("de", "monticore", "codegen", "ast", "Automaton");

    this.glex.setGlobalValue("service", new AbstractService(ast));
    this.glex.setGlobalValue("astHelper", new DecorationHelper());
    PackageInterfaceDecorator decorator = new PackageInterfaceDecorator(this.glex, new DecorationHelper());
    packageInterface = decorator.decorate(ast);
  }

  @Test
  public void testClassName() {
    assertEquals("AutomatonPackage", packageInterface.getName());
  }

  @Test
  public void testSuperInterface() {
    assertEquals(1, packageInterface.sizeInterfaces());
    assertDeepEquals("de.monticore.emf._ast.ASTEPackage", packageInterface.getInterface(0));
  }


  @Test
  public void testAttributeSize() {
    assertEquals(17, packageInterface.getCDAttributeList().size());
  }

  @Test
  public void testMethodSize() {
    assertFalse(packageInterface.getCDMethodList().isEmpty());
    assertEquals(14, packageInterface.getCDMethodList().size());
  }

  @Test
  public void testENameAttribute() {
    ASTCDAttribute attribute = getAttributeBy("eName", packageInterface);
    assertTrue(attribute.isPresentModifier());
    assertDeepEquals(PACKAGE_PRIVATE, attribute.getModifier());
    assertDeepEquals(String.class, attribute.getType());
  }

  @Test
  public void testENSUriAttribute() {
    ASTCDAttribute attribute = getAttributeBy("eNS_URI", packageInterface);
    assertTrue(attribute.isPresentModifier());
    assertDeepEquals(PACKAGE_PRIVATE, attribute.getModifier());
    assertDeepEquals(String.class, attribute.getType());
  }

  @Test
  public void testENSPrefixAttribute() {
    ASTCDAttribute attribute = getAttributeBy("eNS_PREFIX", packageInterface);
    assertTrue(attribute.isPresentModifier());
    assertDeepEquals(PACKAGE_PRIVATE, attribute.getModifier());
    assertDeepEquals(String.class, attribute.getType());
  }

  @Test
  public void testEInstanceAttribute() {
    ASTCDAttribute attribute = getAttributeBy("eINSTANCE", packageInterface);
    assertTrue(attribute.isPresentModifier());
    assertDeepEquals(PACKAGE_PRIVATE, attribute.getModifier());
    assertDeepEquals("AutomatonPackage", attribute.getType());
  }

  @Test
  public void testConstantsAttribute() {
    ASTCDAttribute attribute = getAttributeBy("ConstantsAutomaton", packageInterface);
    assertTrue(attribute.isPresentModifier());
    assertDeepEquals(PACKAGE_PRIVATE, attribute.getModifier());
    assertInt(attribute.getType());
  }

  @Test
  public void testASTAutomatonAttribute() {
    ASTCDAttribute attribute = getAttributeBy("ASTAutomaton", packageInterface);
    assertTrue(attribute.isPresentModifier());
    assertDeepEquals(PACKAGE_PRIVATE, attribute.getModifier());
    assertInt(attribute.getType());
  }

  @Test
  public void testASTStateAttribute() {
    ASTCDAttribute attribute = getAttributeBy("ASTState", packageInterface);
    assertTrue(attribute.isPresentModifier());
    assertDeepEquals(PACKAGE_PRIVATE, attribute.getModifier());
    assertInt(attribute.getType());
  }

  @Test
  public void testASTTransitionAttribute() {
    ASTCDAttribute attribute = getAttributeBy("ASTTransition", packageInterface);
    assertTrue(attribute.isPresentModifier());
    assertDeepEquals(PACKAGE_PRIVATE, attribute.getModifier());
    assertInt(attribute.getType());
  }

  @Test
  public void testASTAutomaton_NameAttribute() {
    ASTCDAttribute attribute = getAttributeBy("ASTAutomaton_Name", packageInterface);
    assertTrue(attribute.isPresentModifier());
    assertDeepEquals(PACKAGE_PRIVATE, attribute.getModifier());
    assertInt(attribute.getType());
  }

  @Test
  public void testASTAutomaton_StatesAttribute() {
    ASTCDAttribute attribute = getAttributeBy("ASTAutomaton_States", packageInterface);
    assertTrue(attribute.isPresentModifier());
    assertDeepEquals(PACKAGE_PRIVATE, attribute.getModifier());
    assertInt(attribute.getType());
  }

  @Test
  public void testASTAutomaton_TransitionsAttribute() {
    ASTCDAttribute attribute = getAttributeBy("ASTAutomaton_Transitions", packageInterface);
    assertTrue(attribute.isPresentModifier());
    assertDeepEquals(PACKAGE_PRIVATE, attribute.getModifier());
    assertInt(attribute.getType());
  }

  @Test
  public void testASTState_NameAttribute() {
    ASTCDAttribute attribute = getAttributeBy("ASTState_Name", packageInterface);
    assertTrue(attribute.isPresentModifier());
    assertDeepEquals(PACKAGE_PRIVATE, attribute.getModifier());
    assertInt(attribute.getType());
  }

  @Test
  public void testASTState_StatesAttribute() {
    ASTCDAttribute attribute = getAttributeBy("ASTState_States", packageInterface);
    assertTrue(attribute.isPresentModifier());
    assertDeepEquals(PACKAGE_PRIVATE, attribute.getModifier());
    assertInt(attribute.getType());
  }

  @Test
  public void testASTState_TransitionsAttribute() {
    ASTCDAttribute attribute = getAttributeBy("ASTState_Transitions", packageInterface);
    assertTrue(attribute.isPresentModifier());
    assertDeepEquals(PACKAGE_PRIVATE, attribute.getModifier());
    assertInt(attribute.getType());
  }

  @Test
  public void testASTTransition_FromAttribute() {
    ASTCDAttribute attribute = getAttributeBy("ASTTransition_From", packageInterface);
    assertTrue(attribute.isPresentModifier());
    assertDeepEquals(PACKAGE_PRIVATE, attribute.getModifier());
    assertInt(attribute.getType());
  }

  @Test
  public void testASTTransition_InputAttribute() {
    ASTCDAttribute attribute = getAttributeBy("ASTTransition_Input", packageInterface);
    assertTrue(attribute.isPresentModifier());
    assertDeepEquals(PACKAGE_PRIVATE, attribute.getModifier());
    assertInt(attribute.getType());
  }

  @Test
  public void testASTTransition_ToAttribute() {
    ASTCDAttribute attribute = getAttributeBy("ASTTransition_To", packageInterface);
    assertTrue(attribute.isPresentModifier());
    assertDeepEquals(PACKAGE_PRIVATE, attribute.getModifier());
    assertInt(attribute.getType());
  }

  @Test
  public void testGetAutomatonFactoryMethod(){
    ASTCDMethod method = getMethodBy("getAutomatonFactory", packageInterface);
    assertDeepEquals(PACKAGE_PRIVATE_ABSTRACT,method.getModifier());
    assertDeepEquals("AutomatonNodeFactory",method.getReturnType() );
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGetConstantsAutomatonMethod(){
    ASTCDMethod method = getMethodBy("getConstantsAutomaton", packageInterface);
    assertDeepEquals(PACKAGE_PRIVATE_ABSTRACT,method.getModifier());
    assertDeepEquals("org.eclipse.emf.ecore.EEnum",method.getReturnType() );
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGetASTAutomatonMethod(){
    ASTCDMethod method = getMethodBy("getASTAutomaton", packageInterface);
    assertDeepEquals(PACKAGE_PRIVATE_ABSTRACT,method.getModifier());
    assertDeepEquals("org.eclipse.emf.ecore.EClass",method.getReturnType() );
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGetASTStateMethod(){
    ASTCDMethod method = getMethodBy("getASTState", packageInterface);
    assertDeepEquals(PACKAGE_PRIVATE_ABSTRACT,method.getModifier());
    assertDeepEquals("org.eclipse.emf.ecore.EClass",method.getReturnType() );
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGetASTTransitionMethod(){
    ASTCDMethod method = getMethodBy("getASTTransition", packageInterface);
    assertDeepEquals(PACKAGE_PRIVATE_ABSTRACT,method.getModifier());
    assertDeepEquals("org.eclipse.emf.ecore.EClass",method.getReturnType() );
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGetASTAutomaton_NameMethod(){
    ASTCDMethod method = getMethodBy("getASTAutomaton_Name", packageInterface);
    assertDeepEquals(PACKAGE_PRIVATE_ABSTRACT,method.getModifier());
    assertDeepEquals("org.eclipse.emf.ecore.EAttribute",method.getReturnType() );
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGetASTAutomaton_StatesMethod(){
    ASTCDMethod method = getMethodBy("getASTAutomaton_States", packageInterface);
    assertDeepEquals(PACKAGE_PRIVATE_ABSTRACT,method.getModifier());
    assertDeepEquals("org.eclipse.emf.ecore.EReference",method.getReturnType() );
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGetASTAutomaton_TransitionsMethod(){
    ASTCDMethod method = getMethodBy("getASTAutomaton_Transitions", packageInterface);
    assertDeepEquals(PACKAGE_PRIVATE_ABSTRACT,method.getModifier());
    assertDeepEquals("org.eclipse.emf.ecore.EReference",method.getReturnType() );
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.INTERFACE, packageInterface, packageInterface);
    System.out.println(sb.toString());
  }

}
