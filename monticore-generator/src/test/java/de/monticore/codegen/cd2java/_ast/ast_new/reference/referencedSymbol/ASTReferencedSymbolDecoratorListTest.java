package de.monticore.codegen.cd2java._ast.ast_new.reference.referencedSymbol;

import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.ASTCDStereotype;
import de.monticore.cd.prettyprint.CD4CodePrinter;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._ast.ast_class.reference.ASTReferenceDecorator;
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

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getAttributeBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getClassBy;
import static org.junit.Assert.*;

public class ASTReferencedSymbolDecoratorListTest extends DecoratorTestCase {

  private GlobalExtensionManagement glex = new GlobalExtensionManagement();

  private ASTCDClass astClass;

  private CDTypeFacade cdTypeFacade = CDTypeFacade.getInstance();

  private static final String NAME_SYMBOL_MAP = "Map<String, Optional<de.monticore.codegen.ast.referencedsymbol._symboltable.FooSymbol>>";

  @Before
  public void setup() {
    Log.init();
    Log.enableFailQuick(false);
    this.cdTypeFacade = CDTypeFacade.getInstance();
    this.glex.setGlobalValue("astHelper", new DecorationHelper());
    this.glex.setGlobalValue("cdPrinter", new CD4CodePrinter());
    ASTCDCompilationUnit ast = this.parse("de", "monticore", "codegen", "ast", "ReferencedSymbol");
    this.glex.setGlobalValue("service", new AbstractService(ast));

    ASTReferenceDecorator decorator = new ASTReferenceDecorator(this.glex, new SymbolTableService(ast));
    ASTCDClass clazz = getClassBy("ASTBarList", ast);
    this.astClass = decorator.decorate(clazz);
  }

  @Test
  public void testClass() {
    assertEquals("ASTBarList", astClass.getName());
  }

  @Test
  public void testAttributes() {
    assertFalse(astClass.isEmptyCDAttributes());
    assertEquals(2, astClass.sizeCDAttributes());
  }

  @Test
  public void testNameAttribute() {
    ASTCDAttribute nameAttribute = getAttributeBy("name", astClass);
    assertTrue(nameAttribute.getModifier().isProtected());
    assertTrue(nameAttribute.getModifier().isPresentStereotype());
    ASTCDStereotype stereotype = nameAttribute.getModifier().getStereotype();
    assertEquals(1, stereotype.sizeValues());
    assertEquals("referencedSymbol", stereotype.getValue(0).getName());
    assertTrue(stereotype.getValue(0).isPresentValue());
    assertEquals("de.monticore.codegen.ast.referencedSymbol.FooSymbol", stereotype.getValue(0).getValue());
    assertDeepEquals(cdTypeFacade.createTypeByDefinition("java.util.List<String>"), nameAttribute.getMCType());
  }

  @Test
  public void testSymbolAttribute() {
    ASTCDAttribute symbolAttribute = getAttributeBy("nameSymbol", astClass);
    assertTrue(symbolAttribute.getModifier().isProtected());
    ASTMCType symboType = this.cdTypeFacade.createTypeByDefinition(NAME_SYMBOL_MAP);
    assertDeepEquals(symboType, symbolAttribute.getMCType());
  }

  @Test
  public void testMethods() {
    assertEquals(38, astClass.getCDMethodList().size());
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
