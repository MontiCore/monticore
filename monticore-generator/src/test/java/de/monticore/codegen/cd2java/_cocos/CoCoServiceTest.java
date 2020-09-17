// (c) https://github.com/MontiCore/monticore
package de.monticore.codegen.cd2java._cocos;

import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import org.junit.Before;
import org.junit.Test;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CoCoServiceTest extends DecoratorTestCase {

  private static String COCO_AUT_PACKAGE = "de.monticore.codegen.ast.automaton._cocos.";

  private CoCoService cocoService;

  private ASTCDCompilationUnit astcdCompilationUnit;

  private ASTCDClass astAutomaton;

  @Before
  public void setup() {
    astcdCompilationUnit = this.parse("de", "monticore", "codegen", "ast", "Automaton");
    astAutomaton = astcdCompilationUnit.getCDDefinition().getCDClass(0);

    cocoService = new CoCoService(astcdCompilationUnit);
  }

  @Test
  public void testCDSymbolPresent() {
    assertTrue(cocoService.getCDSymbol().isPresentAstNode());
  }

  @Test
  public void testConstructorsCreateEqualService() {
    CoCoService astServiceFromDefinitionSymbol = new CoCoService(astcdCompilationUnit.getCDDefinition().getSymbol());
    assertTrue(astServiceFromDefinitionSymbol.getCDSymbol().isPresentAstNode());
    assertDeepEquals(cocoService.getCDSymbol().getAstNode(), astServiceFromDefinitionSymbol.getCDSymbol().getAstNode());
  }

  @Test
  public void testCreateCoCoService() {
    CoCoService createdCoCoService = CoCoService.createCoCoService(astcdCompilationUnit.getCDDefinition().getSymbol());
    assertTrue(createdCoCoService.getCDSymbol().isPresentAstNode());
    assertDeepEquals(cocoService.getCDSymbol().getAstNode(), createdCoCoService.getCDSymbol().getAstNode());
  }

  @Test
  public void testSubPackage() {
    assertEquals("_cocos", cocoService.getSubPackage());
  }

  @Test
  public void testGetCoCoSimpleTypeName() {
    assertEquals("AutomatonASTAutomatonCoCo", cocoService.getCoCoSimpleTypeName(astAutomaton));
    assertEquals("AutomatonASTAutomatonCoCo", cocoService.getCoCoSimpleTypeName(astAutomaton.getName()));
  }

  @Test
  public void testGetCoCoFullTypeName() {
    assertEquals(COCO_AUT_PACKAGE + "AutomatonASTAutomatonCoCo", cocoService.getCoCoFullTypeName(astAutomaton));
    assertDeepEquals(COCO_AUT_PACKAGE + "AutomatonASTAutomatonCoCo", cocoService.getCoCoType(astAutomaton));
  }

  @Test
  public void testGetASTBaseInterfaceSimpleName() {
    assertEquals("AutomatonCoCoChecker", cocoService.getCheckerSimpleTypeName());
  }

  @Test
  public void testGetASTBaseInterfaceFullName() {
    assertEquals(COCO_AUT_PACKAGE + "AutomatonCoCoChecker", cocoService.getCheckerFullTypeName());
    assertDeepEquals(COCO_AUT_PACKAGE + "AutomatonCoCoChecker", cocoService.getCheckerType());
  }

}
