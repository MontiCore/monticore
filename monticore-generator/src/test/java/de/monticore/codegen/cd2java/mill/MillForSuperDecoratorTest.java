// (c) https://github.com/MontiCore/monticore
package de.monticore.codegen.cd2java.mill;

import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.prettyprint.CD4CodePrinter;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.DecorationHelper;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._ast.ast_class.ASTService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MillForSuperDecoratorTest extends DecoratorTestCase {

  private ASTCDClass millClass;

  private GlobalExtensionManagement glex;

  private ASTCDCompilationUnit originalCompilationUnit;

  private ASTCDCompilationUnit decoratedCompilationUnit;

  @Before
  public void setUp() {
    LogStub.init();
    LogStub.enableFailQuick(false);
    this.glex = new GlobalExtensionManagement();

    this.glex.setGlobalValue("astHelper", DecorationHelper.getInstance());
    this.glex.setGlobalValue("cdPrinter", new CD4CodePrinter());
    decoratedCompilationUnit = this.parse("de", "monticore", "codegen", "ast", "Automaton");
    originalCompilationUnit = decoratedCompilationUnit.deepClone();
    this.glex.setGlobalValue("service", new AbstractService(decoratedCompilationUnit));

    MillForSuperDecorator decorator = new MillForSuperDecorator(this.glex, new ASTService(decoratedCompilationUnit));
    List<ASTCDClass> millClassList = decorator.decorate(decoratedCompilationUnit);

    assertEquals(1, millClassList.size());
    millClass = millClassList.get(0);
  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalCompilationUnit, decoratedCompilationUnit);
  }

  @Test
  public void testMillName() {
    assertEquals("LexicalsMillForAutomaton", millClass.getName());
  }

  @Test
  public void testSuperClass() {
    assertTrue(millClass.isPresentSuperclass());
    assertDeepEquals("de.monticore.codegen.ast.lexicals.LexicalsMill", millClass.getSuperclass());
  }

  @Test
  public void testNoInterfaces() {
    assertEquals(0, millClass.sizeInterfaces());
  }

  @Test
  public void testNoAttributes() {
    assertEquals(0, millClass.sizeCDAttributes());
  }

  @Test
  public void testMethodSize() {
    assertEquals(0, millClass.sizeCDAttributes());
  }

}
