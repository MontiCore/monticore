package de.monticore.codegen.cd2java._ast.ast_new.reference;

import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._ast.ast_class.reference.ASTReferenceDecorator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import static de.monticore.codegen.cd2java.DecoratorTestUtil.getClassBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ASTReferenceDecoratorTest extends DecoratorTestCase {


  private GlobalExtensionManagement glex = new GlobalExtensionManagement();

  private ASTCDClass astMandClass;

  private ASTCDClass astOptClass;

  private ASTCDClass astListClass;

  private ASTReferenceDecorator referenceDecorator;

  @Before
  public void setup() {
    LogStub.init();
    this.glex.setGlobalValue("astHelper", new DecorationHelper());
    ASTCDCompilationUnit ast = this.parse("de", "monticore", "codegen", "ast", "ReferencedSymbol");
    this.referenceDecorator = new ASTReferenceDecorator(this.glex, new SymbolTableService(ast));
    ASTCDClass mandclazz = getClassBy("ASTBarMand", ast);
    this.astMandClass = referenceDecorator.decorate(mandclazz);
    ASTCDClass optclazz = getClassBy("ASTBarOpt", ast);
    this.astOptClass = referenceDecorator.decorate(optclazz);
    ASTCDClass listclazz = getClassBy("ASTBarList", ast);
    this.astListClass = referenceDecorator.decorate(listclazz);
  }

  @Test
  public void testMandatoryAttributes() {
    assertFalse(astMandClass.isEmptyCDAttributes());
    assertEquals(2, astMandClass.sizeCDAttributes());
  }

  @Test
  public void testOptionalAttributes() {
    assertFalse(astOptClass.isEmptyCDAttributes());
    assertEquals(2, astOptClass.sizeCDAttributes());
  }

  @Test
  public void testListAttributes() {
    assertFalse(astListClass.isEmptyCDAttributes());
    assertEquals(2, astListClass.sizeCDAttributes());
  }

  @Test
  public void testMandatoryMethods() {
    assertEquals(6, astMandClass.getCDMethodList().size());
  }


  @Test
  public void testOptionalMethods() {
    assertEquals(6, astOptClass.getCDMethodList().size());
  }


  @Test
  public void testListMethods() {
    assertEquals(38, astListClass.getCDMethodList().size());
  }

}
