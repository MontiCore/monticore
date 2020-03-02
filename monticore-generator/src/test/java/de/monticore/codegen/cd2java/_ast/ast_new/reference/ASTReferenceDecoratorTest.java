/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast.ast_new.reference;

import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.CD4AnalysisMill;
import de.monticore.codegen.cd2java.DecorationHelper;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._ast.ast_class.reference.ASTReferenceDecorator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getAttributeBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getClassBy;
import static org.junit.Assert.*;

public class ASTReferenceDecoratorTest extends DecoratorTestCase {


  private GlobalExtensionManagement glex = new GlobalExtensionManagement();

  private ASTCDClass astMandClass;

  private ASTCDClass astOptClass;

  private ASTCDClass astListClass;

  private ASTReferenceDecorator<ASTCDClass> referenceDecorator;

  @Before
  public void setup() {
    LogStub.init();
    this.glex.setGlobalValue("astHelper", DecorationHelper.getInstance());
    ASTCDCompilationUnit ast = this.parse("de", "monticore", "codegen", "ast", "ReferencedSymbol");
    this.referenceDecorator = new ASTReferenceDecorator(this.glex, new SymbolTableService(ast));
    ASTCDClass mandclazz = getClassBy("ASTBarMand", ast);
    ASTCDClass changedClass = CD4AnalysisMill.cDClassBuilder().setName(mandclazz.getName())
        .setModifier(mandclazz.getModifier())
        .build();
    this.astMandClass = referenceDecorator.decorate(mandclazz, changedClass);
    ASTCDClass optclazz = getClassBy("ASTBarOpt", ast);
    ASTCDClass changedOptClass = CD4AnalysisMill.cDClassBuilder().setName(optclazz.getName())
        .setModifier(changedClass.getModifier())
        .build();
    this.astOptClass = referenceDecorator.decorate(optclazz, changedOptClass);
    ASTCDClass listclazz = getClassBy("ASTBarList", ast);
    ASTCDClass changedListClass = CD4AnalysisMill.cDClassBuilder().setName(listclazz.getName())
        .setModifier(listclazz.getModifier())
        .build();
    this.astListClass = referenceDecorator.decorate(listclazz, changedListClass);
  }

  @Test
  public void testMandatoryAttributeSize() {
    assertFalse(astMandClass.isEmptyCDAttributes());
    assertEquals(1, astMandClass.sizeCDAttributes());
  }

  @Test
  public void testMandatorySymbolAttribute() {
    ASTCDAttribute nameSymbol = getAttributeBy("nameSymbol", astMandClass);
    assertTrue(nameSymbol.getModifier().isProtected());
    assertDeepEquals("Optional<de.monticore.codegen.ast.referencedsymbol._symboltable.FooSymbol>", nameSymbol.getMCType());
  }

  @Test
  public void testOptionalAttributeSize() {
    assertFalse(astOptClass.isEmptyCDAttributes());
    assertEquals(1, astOptClass.sizeCDAttributes());
  }

  @Test
  public void testOptionalSymbolAttribute() {
    ASTCDAttribute nameSymbol = getAttributeBy("nameSymbol", astOptClass);
    assertTrue(nameSymbol.getModifier().isProtected());
    assertDeepEquals("Optional<de.monticore.codegen.ast.referencedsymbol._symboltable.FooSymbol>", nameSymbol.getMCType());
  }

  @Test
  public void testListAttributeSize() {
    assertFalse(astListClass.isEmptyCDAttributes());
    assertEquals(1, astListClass.sizeCDAttributes());
  }

  @Test
  public void testListSymbolAttribute() {
    ASTCDAttribute nameSymbol = getAttributeBy("nameSymbol", astListClass);
    assertTrue(nameSymbol.getModifier().isProtected());
    assertDeepEquals("Map<String,Optional<de.monticore.codegen.ast.referencedsymbol._symboltable.FooSymbol>>", nameSymbol.getMCType());
  }

  @Test
  public void testMandatoryMethods() {
    assertEquals(4, astMandClass.getCDMethodList().size());
  }


  @Test
  public void testOptionalMethods() {
    assertEquals(4, astOptClass.getCDMethodList().size());
  }


  @Test
  public void testListMethods() {
    assertEquals(38, astListClass.getCDMethodList().size());
  }

}
