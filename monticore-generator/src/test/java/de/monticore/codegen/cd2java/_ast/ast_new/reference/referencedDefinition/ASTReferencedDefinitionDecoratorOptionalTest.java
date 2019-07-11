package de.monticore.codegen.cd2java._ast.ast_new.reference.referencedDefinition;

import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.cd.prettyprint.CD4CodePrinter;
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
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import static de.monticore.codegen.cd2java.DecoratorAssert.*;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getClassBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodBy;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;
import static org.junit.Assert.*;

public class ASTReferencedDefinitionDecoratorOptionalTest extends DecoratorTestCase {

  private GlobalExtensionManagement glex = new GlobalExtensionManagement();

  private ASTCDClass astClass;

  private CDTypeFacade cdTypeFacade = CDTypeFacade.getInstance();

  private static final String NAME_DEFINITION = "de.monticore.codegen.ast.referencedsymbol._ast.ASTFoo";

  @Before
  public void setup() {
    Log.init();
    Log.enableFailQuick(false);
    this.cdTypeFacade = CDTypeFacade.getInstance();
    this.glex.setGlobalValue("astHelper", new DecorationHelper());
    this.glex.setGlobalValue("cdPrinter", new CD4CodePrinter());
    ASTCDCompilationUnit ast = this.parse("de", "monticore", "codegen", "ast", "ReferencedSymbol");
    this.glex.setGlobalValue("service", new AbstractService(ast));

    SymbolTableService symbolTableService = new SymbolTableService(ast);
    ASTReferencedDefinitionDecorator decorator = new ASTReferencedDefinitionDecorator(this.glex, new ReferencedDefinitionAccessorDecorator(glex, symbolTableService), symbolTableService);
    ASTCDClass clazz = getClassBy("ASTBarOpt", ast);
    this.astClass = decorator.decorate(clazz);
  }

  @Test
  public void testClass() {
    assertEquals("ASTBarOpt", astClass.getName());
  }

  @Test
  public void testAttributes() {
    assertFalse(astClass.isEmptyCDAttributes());
    assertEquals(1, astClass.sizeCDAttributes());
  }

  @Test
  public void testMethods() {
    assertEquals(3, astClass.getCDMethodList().size());
  }

  @Test
  public void testGetNameDefinitionMethod() {
    ASTCDMethod method = getMethodBy("getNameDefinition", astClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    ASTMCType astType = this.cdTypeFacade.createTypeByDefinition(NAME_DEFINITION);
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals(astType, method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGetNameDefinitionOptMethod() {
    ASTCDMethod method = getMethodBy("getNameDefinitionOpt", astClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertOptionalOf(NAME_DEFINITION, method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testIsPresentNameDefinitionMethod() {
    ASTCDMethod method = getMethodBy("isPresentNameDefinition", astClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertBoolean(method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
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
