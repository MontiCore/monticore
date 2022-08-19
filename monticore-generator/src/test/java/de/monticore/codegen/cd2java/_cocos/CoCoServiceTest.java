/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._cocos;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
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
    astAutomaton = astcdCompilationUnit.getCDDefinition().getCDClassesList().get(0);

    cocoService = new CoCoService(astcdCompilationUnit);
  }

  @Test
  public void testCDSymbolPresent() {
    assertTrue(cocoService.getCDSymbol().isPresentAstNode());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testConstructorsCreateEqualService() {
    CoCoService astServiceFromDefinitionSymbol = new CoCoService(astcdCompilationUnit.getCDDefinition().getSymbol());
    assertTrue(astServiceFromDefinitionSymbol.getCDSymbol().isPresentAstNode());
    assertDeepEquals(cocoService.getCDSymbol().getAstNode(), astServiceFromDefinitionSymbol.getCDSymbol().getAstNode());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testCreateCoCoService() {
    CoCoService createdCoCoService = CoCoService.createCoCoService(astcdCompilationUnit.getCDDefinition().getSymbol());
    assertTrue(createdCoCoService.getCDSymbol().isPresentAstNode());
    assertDeepEquals(cocoService.getCDSymbol().getAstNode(), createdCoCoService.getCDSymbol().getAstNode());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSubPackage() {
    assertEquals("_cocos", cocoService.getSubPackage());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetCoCoSimpleTypeName() {
    assertEquals("AutomatonASTAutomatonCoCo", cocoService.getCoCoSimpleTypeName(astAutomaton));
    assertEquals("AutomatonASTAutomatonCoCo", cocoService.getCoCoSimpleTypeName(astAutomaton.getName()));
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetCoCoFullTypeName() {
    assertEquals(COCO_AUT_PACKAGE + "AutomatonASTAutomatonCoCo", cocoService.getCoCoFullTypeName(astAutomaton));
    assertDeepEquals(COCO_AUT_PACKAGE + "AutomatonASTAutomatonCoCo", cocoService.getCoCoType(astAutomaton));
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetASTBaseInterfaceSimpleName() {
    assertEquals("AutomatonCoCoChecker", cocoService.getCheckerSimpleTypeName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetASTBaseInterfaceFullName() {
    assertEquals(COCO_AUT_PACKAGE + "AutomatonCoCoChecker", cocoService.getCheckerFullTypeName());
    assertDeepEquals(COCO_AUT_PACKAGE + "AutomatonCoCoChecker", cocoService.getCheckerType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

}
