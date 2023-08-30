/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._visitor;

import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class VisitorServiceTest extends DecoratorTestCase {

  private static String VISITOR_AUT_PACKAGE = "de.monticore.codegen.ast.automaton._visitor.";

  private VisitorService astService;

  private ASTCDCompilationUnit astcdCompilationUnit;

  private ASTCDClass astAutomaton;

  private MCTypeFacade mcTypeFacade;

  @Before
  public void setup() {
    this.mcTypeFacade = MCTypeFacade.getInstance();
    astcdCompilationUnit = this.parse("de", "monticore", "codegen", "ast", "Automaton");
    astAutomaton = astcdCompilationUnit.getCDDefinition().getCDClassesList().get(0);

    astService = new VisitorService(astcdCompilationUnit);
  }

  @Test
  public void testCDSymbolPresent() {
    assertTrue(astService.getCDSymbol().isPresentAstNode());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testConstructorsCreateEqualService() {
    VisitorService astServiceFromDefinitionSymbol = new VisitorService(astcdCompilationUnit.getCDDefinition().getSymbol());
    assertTrue(astServiceFromDefinitionSymbol.getCDSymbol().isPresentAstNode());
    assertDeepEquals(astService.getCDSymbol().getAstNode(), astServiceFromDefinitionSymbol.getCDSymbol().getAstNode());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testCreateVisitorService() {
    VisitorService createdVisitorService = VisitorService.createVisitorService(astcdCompilationUnit.getCDDefinition().getSymbol());
    assertTrue(createdVisitorService.getCDSymbol().isPresentAstNode());
    assertDeepEquals(astService.getCDSymbol().getAstNode(), createdVisitorService.getCDSymbol().getAstNode());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSubPackage() {
    assertEquals("_visitor", astService.getSubPackage());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetVisitorSimpleName() {
    assertEquals("AutomatonVisitor", astService.getVisitorSimpleName());
    assertEquals("AutomatonVisitor", astService.getVisitorSimpleName(astcdCompilationUnit.getCDDefinition().getSymbol()));
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetVisitorFullName() {
    assertEquals(VISITOR_AUT_PACKAGE + "AutomatonVisitor", astService.getVisitorFullName());
    assertEquals(VISITOR_AUT_PACKAGE + "AutomatonVisitor", astService.getVisitorFullName(astcdCompilationUnit.getCDDefinition().getSymbol()));
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetVisitorType() {
    assertDeepEquals(VISITOR_AUT_PACKAGE + "AutomatonVisitor", astService.getVisitorType());
    assertDeepEquals(VISITOR_AUT_PACKAGE + "AutomatonVisitor", astService.getVisitorType(astcdCompilationUnit.getCDDefinition().getSymbol()));
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetVisitorMethod() {
    ASTCDMethod visitMethod = astService.getVisitorMethod("visit", mcTypeFacade.createQualifiedType("_ast.ASTFoo"));
    assertEquals("visit", visitMethod.getName());
    assertTrue(visitMethod.getMCReturnType().isPresentMCVoidType());

    assertEquals(1, visitMethod.sizeCDParameters());
    assertEquals("node", visitMethod.getCDParameter(0).getName());
    assertDeepEquals("_ast.ASTFoo", visitMethod.getCDParameter(0).getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetSuperInheritanceVisitors() {
    List<ASTMCObjectType> allVisitorTypesInHierarchy = astService.getSuperTraverserInterfaces();
    assertEquals(1, allVisitorTypesInHierarchy.size());
    assertDeepEquals("de.monticore.codegen.ast.lexicals._visitor.LexicalsTraverser", allVisitorTypesInHierarchy.get(0));
  
    assertTrue(Log.getFindings().isEmpty());
  }
}
