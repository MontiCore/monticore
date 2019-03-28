package de.monticore.codegen.cd2java.ast_new.reference.referencedSymbol;

import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java.ast_new.reference.ASTReferenceDecorator;
import de.monticore.codegen.cd2java.factories.CDTypeFactory;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.codegen.cd2java.symboltable.SymbolTableService;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDStereotype;
import org.junit.Before;
import org.junit.Test;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getAttributeBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getClassBy;
import static org.junit.Assert.*;

public class ASTReferencedSymbolDecoratorListTest extends DecoratorTestCase {

  private GlobalExtensionManagement glex = new GlobalExtensionManagement();

  private ASTCDClass astClass;

  private CDTypeFactory cdTypeFactory = CDTypeFactory.getInstance();

  private static final String NAME_SYMBOL_MAP = "Map<String, Optional<de.monticore.codegen.ast.referencedsymbol._symboltable.FooSymbol>>";

  @Before
  public void setup() {
    this.cdTypeFactory = CDTypeFactory.getInstance();
    this.glex.setGlobalValue("astHelper", new DecorationHelper());
    ASTCDCompilationUnit ast = this.parse("de", "monticore", "codegen", "ast", "ReferencedSymbol");
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
    assertDeepEquals(cdTypeFactory.createTypeByDefinition("java.util.List<String>"), nameAttribute.getType());
  }

  @Test
  public void testSymbolAttribute() {
    ASTCDAttribute symbolAttribute = getAttributeBy("nameSymbol", astClass);
    assertTrue(symbolAttribute.getModifier().isPrivate());
    ASTType symboType = this.cdTypeFactory.createTypeByDefinition(NAME_SYMBOL_MAP);
    assertDeepEquals(symboType, symbolAttribute.getType());
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
