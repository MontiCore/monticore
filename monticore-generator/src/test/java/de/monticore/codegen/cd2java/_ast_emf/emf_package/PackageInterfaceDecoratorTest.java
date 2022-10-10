/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast_emf.emf_package;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.cd.codegen.CdUtilsPrinter;
import de.monticore.codegen.cd2java.DecorationHelper;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._ast_emf.EmfService;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
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
    this.glex.setGlobalValue("cdPrinter", new CdUtilsPrinter());

    PackageInterfaceDecorator decorator = new PackageInterfaceDecorator(this.glex, new EmfService(ast));
    packageInterface = decorator.decorate(ast);
  }

  @Test
  public void testClassName() {
    assertEquals("AutomataPackage", packageInterface.getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSuperInterface() {
    assertEquals(1, packageInterface.getInterfaceList().size());
    assertDeepEquals("de.monticore.emf._ast.ASTEPackage", packageInterface.getCDExtendUsage().getSuperclass(0));
  
    assertTrue(Log.getFindings().isEmpty());
  }


  @Test
  public void testAttributeSize() {
    assertEquals(28, packageInterface.getCDAttributeList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMethodSize() {
    assertFalse(packageInterface.getCDMethodList().isEmpty());
    assertEquals(21, packageInterface.getCDMethodList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testENameAttribute() {
    ASTCDAttribute attribute = getAttributeBy("eNAME", packageInterface);
    assertDeepEquals(PACKAGE_PRIVATE, attribute.getModifier());
    assertDeepEquals(String.class, attribute.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testENSUriAttribute() {
    ASTCDAttribute attribute = getAttributeBy("eNS_URI", packageInterface);
    assertDeepEquals(PACKAGE_PRIVATE, attribute.getModifier());
    assertDeepEquals(String.class, attribute.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testENSPrefixAttribute() {
    ASTCDAttribute attribute = getAttributeBy("eNS_PREFIX", packageInterface);
    assertDeepEquals(PACKAGE_PRIVATE, attribute.getModifier());
    assertDeepEquals(String.class, attribute.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testEInstanceAttribute() {
    ASTCDAttribute attribute = getAttributeBy("eINSTANCE", packageInterface);
    assertDeepEquals(PACKAGE_PRIVATE, attribute.getModifier());
    assertDeepEquals("AutomataPackage", attribute.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testConstantsAttribute() {
    ASTCDAttribute attribute = getAttributeBy("ConstantsAutomata", packageInterface);
    assertDeepEquals(PACKAGE_PRIVATE, attribute.getModifier());
    assertInt(attribute.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testASTAutomatonAttribute() {
    ASTCDAttribute attribute = getAttributeBy("ASTAutomaton", packageInterface);
    assertDeepEquals(PACKAGE_PRIVATE, attribute.getModifier());
    assertInt(attribute.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testASTStateAttribute() {
    ASTCDAttribute attribute = getAttributeBy("ASTState", packageInterface);
    assertDeepEquals(PACKAGE_PRIVATE, attribute.getModifier());
    assertInt(attribute.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testASTTransitionAttribute() {
    ASTCDAttribute attribute = getAttributeBy("ASTTransition", packageInterface);
    assertDeepEquals(PACKAGE_PRIVATE, attribute.getModifier());
    assertInt(attribute.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testASTBodyExtAttribute() {
    //ASTBodyExt is interface but has to be generated as well
    ASTCDAttribute attribute = getAttributeBy("ASTBodyExt", packageInterface);
    assertDeepEquals(PACKAGE_PRIVATE, attribute.getModifier());
    assertInt(attribute.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testASTTransitionWithActionAttribute() {
    ASTCDAttribute attribute = getAttributeBy("ASTTransitionWithAction", packageInterface);
    assertDeepEquals(PACKAGE_PRIVATE, attribute.getModifier());
    assertInt(attribute.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testASTAutNameAttribute() {
    ASTCDAttribute attribute = getAttributeBy("ASTAutName", packageInterface);
    assertDeepEquals(PACKAGE_PRIVATE, attribute.getModifier());
    assertInt(attribute.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testASTAutomaton_NameAttribute() {
    ASTCDAttribute attribute = getAttributeBy("ASTAutomaton_Name", packageInterface);
    assertDeepEquals(PACKAGE_PRIVATE, attribute.getModifier());
    assertInt(attribute.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testASTAutomaton_StatesAttribute() {
    ASTCDAttribute attribute = getAttributeBy("ASTAutomaton_States", packageInterface);
    assertDeepEquals(PACKAGE_PRIVATE, attribute.getModifier());
    assertInt(attribute.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testASTAutomaton_TransitionsAttribute() {
    ASTCDAttribute attribute = getAttributeBy("ASTAutomaton_Transitions", packageInterface);
    assertDeepEquals(PACKAGE_PRIVATE, attribute.getModifier());
    assertInt(attribute.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testASTState_NameAttribute() {
    ASTCDAttribute attribute = getAttributeBy("ASTState_Name", packageInterface);
    assertDeepEquals(PACKAGE_PRIVATE, attribute.getModifier());
    assertInt(attribute.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testASTState_StatesAttribute() {
    ASTCDAttribute attribute = getAttributeBy("ASTState_States", packageInterface);
    assertDeepEquals(PACKAGE_PRIVATE, attribute.getModifier());
    assertInt(attribute.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testASTState_TransitionsAttribute() {
    ASTCDAttribute attribute = getAttributeBy("ASTState_Transitions", packageInterface);
    assertDeepEquals(PACKAGE_PRIVATE, attribute.getModifier());
    assertInt(attribute.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testASTTransition_FromAttribute() {
    ASTCDAttribute attribute = getAttributeBy("ASTTransition_From", packageInterface);
    assertDeepEquals(PACKAGE_PRIVATE, attribute.getModifier());
    assertInt(attribute.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testASTTransition_InputAttribute() {
    ASTCDAttribute attribute = getAttributeBy("ASTTransition_Input", packageInterface);
    assertDeepEquals(PACKAGE_PRIVATE, attribute.getModifier());
    assertInt(attribute.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testASTTransition_ToAttribute() {
    ASTCDAttribute attribute = getAttributeBy("ASTTransition_To", packageInterface);
    assertDeepEquals(PACKAGE_PRIVATE, attribute.getModifier());
    assertInt(attribute.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testInheritedAttributeExists() {
    ASTCDAttribute attribute = getAttributeBy("ASTAutName_Input", packageInterface);
    assertDeepEquals(PACKAGE_PRIVATE, attribute.getModifier());
    assertInt(attribute.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }


  @Test
  public void testGetConstantsAutomatonMethod() {
    ASTCDMethod method = getMethodBy("getConstantsAutomata", packageInterface);
    assertDeepEquals(PACKAGE_PRIVATE_ABSTRACT, method.getModifier());
    assertDeepEquals("org.eclipse.emf.ecore.EEnum", method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetASTAutomatonMethod() {
    ASTCDMethod method = getMethodBy("getASTAutomaton", packageInterface);
    assertDeepEquals(PACKAGE_PRIVATE_ABSTRACT, method.getModifier());
    assertDeepEquals("org.eclipse.emf.ecore.EClass", method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetASTStateMethod() {
    ASTCDMethod method = getMethodBy("getASTState", packageInterface);
    assertDeepEquals(PACKAGE_PRIVATE_ABSTRACT, method.getModifier());
    assertDeepEquals("org.eclipse.emf.ecore.EClass", method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetASTTransitionMethod() {
    ASTCDMethod method = getMethodBy("getASTTransition", packageInterface);
    assertDeepEquals(PACKAGE_PRIVATE_ABSTRACT, method.getModifier());
    assertDeepEquals("org.eclipse.emf.ecore.EClass", method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetASTTransitionWithActionMethod() {
    ASTCDMethod method = getMethodBy("getASTTransitionWithAction", packageInterface);
    assertDeepEquals(PACKAGE_PRIVATE_ABSTRACT, method.getModifier());
    assertDeepEquals("org.eclipse.emf.ecore.EClass", method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetASTAutNameMethod() {
    ASTCDMethod method = getMethodBy("getASTAutName", packageInterface);
    assertDeepEquals(PACKAGE_PRIVATE_ABSTRACT, method.getModifier());
    assertDeepEquals("org.eclipse.emf.ecore.EClass", method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetASTBodyExtMethod() {
    ASTCDMethod method = getMethodBy("getASTBodyExt", packageInterface);
    assertDeepEquals(PACKAGE_PRIVATE_ABSTRACT, method.getModifier());
    assertDeepEquals("org.eclipse.emf.ecore.EClass", method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetASTAutomaton_NameMethod() {
    ASTCDMethod method = getMethodBy("getASTAutomaton_Name", packageInterface);
    assertDeepEquals(PACKAGE_PRIVATE_ABSTRACT, method.getModifier());
    assertDeepEquals("org.eclipse.emf.ecore.EAttribute", method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetASTAutomaton_StatesMethod() {
    ASTCDMethod method = getMethodBy("getASTAutomaton_States", packageInterface);
    assertDeepEquals(PACKAGE_PRIVATE_ABSTRACT, method.getModifier());
    assertDeepEquals("org.eclipse.emf.ecore.EReference", method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetASTAutomaton_TransitionsMethod() {
    ASTCDMethod method = getMethodBy("getASTAutomaton_Transitions", packageInterface);
    assertDeepEquals(PACKAGE_PRIVATE_ABSTRACT, method.getModifier());
    assertDeepEquals("org.eclipse.emf.ecore.EReference", method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetInterfaceAttributeMethod() {
    ASTCDMethod method = getMethodBy("getASTBodyExt_Varname", packageInterface);
    assertDeepEquals(PACKAGE_PRIVATE_ABSTRACT, method.getModifier());
    assertDeepEquals("org.eclipse.emf.ecore.EAttribute", method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testNoInheritedAttributeMethod() {
    assertTrue(packageInterface.getCDMethodList()
        .stream()
        .noneMatch(m -> m.getName().equals("getASTAutName_Input")));
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    CD4C.init(generatorSetup);
    StringBuilder sb = generatorEngine.generate("_ast_emf.emf_package.EmfPackage", packageInterface, packageInterface, astcdDefinition, packageDir);
    // test parsing
    ParserConfiguration configuration = new ParserConfiguration();
    JavaParser parser = new JavaParser(configuration);
    ParseResult parseResult = parser.parse(sb.toString());
    assertTrue(parseResult.isSuccessful());
  
    assertTrue(Log.getFindings().isEmpty());
  }

}
