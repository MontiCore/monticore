// (c) https://github.com/MontiCore/monticore
package de.monticore.codegen.cd2java._visitor;

import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
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
    astAutomaton = astcdCompilationUnit.getCDDefinition().getCDClass(0);

    astService = new VisitorService(astcdCompilationUnit);
  }

  @Test
  public void testCDSymbolPresent() {
    assertTrue(astService.getCDSymbol().isPresentAstNode());
  }

  @Test
  public void testConstructorsCreateEqualService() {
    VisitorService astServiceFromDefinitionSymbol = new VisitorService(astcdCompilationUnit.getCDDefinition().getSymbol());
    assertTrue(astServiceFromDefinitionSymbol.getCDSymbol().isPresentAstNode());
    assertDeepEquals(astService.getCDSymbol().getAstNode(), astServiceFromDefinitionSymbol.getCDSymbol().getAstNode());
  }

  @Test
  public void testCreateVisitorService() {
    VisitorService createdVisitorService = VisitorService.createVisitorService(astcdCompilationUnit.getCDDefinition().getSymbol());
    assertTrue(createdVisitorService.getCDSymbol().isPresentAstNode());
    assertDeepEquals(astService.getCDSymbol().getAstNode(), createdVisitorService.getCDSymbol().getAstNode());
  }

  @Test
  public void testSubPackage() {
    assertEquals("_visitor", astService.getSubPackage());
  }

  @Test
  public void testGetVisitorSimpleName() {
    assertEquals("AutomatonVisitor", astService.getVisitorSimpleName());
    assertEquals("AutomatonVisitor", astService.getVisitorSimpleName(astcdCompilationUnit.getCDDefinition().getSymbol()));
  }

  @Test
  public void testGetVisitorFullName() {
    assertEquals(VISITOR_AUT_PACKAGE + "AutomatonVisitor", astService.getVisitorFullName());
    assertEquals(VISITOR_AUT_PACKAGE + "AutomatonVisitor", astService.getVisitorFullName(astcdCompilationUnit.getCDDefinition().getSymbol()));
  }

  @Test
  public void testGetVisitorType() {
    assertDeepEquals(VISITOR_AUT_PACKAGE + "AutomatonVisitor", astService.getVisitorType());
    assertDeepEquals(VISITOR_AUT_PACKAGE + "AutomatonVisitor", astService.getVisitorType(astcdCompilationUnit.getCDDefinition().getSymbol()));
  }

  @Test
  public void testGetInheritanceVisitorSimpleName() {
    assertEquals("AutomatonInheritanceVisitor", astService.getInheritanceVisitorSimpleName());
    assertEquals("AutomatonInheritanceVisitor", astService.getInheritanceVisitorSimpleName(astcdCompilationUnit.getCDDefinition().getSymbol()));
  }

  @Test
  public void testGetInheritanceVisitorFullName() {
    assertEquals(VISITOR_AUT_PACKAGE + "AutomatonInheritanceVisitor", astService.getInheritanceVisitorFullName());
    assertEquals(VISITOR_AUT_PACKAGE + "AutomatonInheritanceVisitor", astService.getInheritanceVisitorFullName(astcdCompilationUnit.getCDDefinition().getSymbol()));
  }

  @Test
  public void testGetDelegatorVisitorSimpleName() {
    assertEquals("AutomatonDelegatorVisitor", astService.getDelegatorVisitorSimpleName());
    assertEquals("AutomatonDelegatorVisitor", astService.getDelegatorVisitorSimpleName(astcdCompilationUnit.getCDDefinition().getSymbol()));
  }

  @Test
  public void testGetDelegatorVisitorFullName() {
    assertEquals(VISITOR_AUT_PACKAGE + "AutomatonDelegatorVisitor", astService.getDelegatorVisitorFullName());
    assertEquals(VISITOR_AUT_PACKAGE + "AutomatonDelegatorVisitor", astService.getDelegatorVisitorFullName(astcdCompilationUnit.getCDDefinition().getSymbol()));
  }

  @Test
  public void testGetParentAwareVisitorSimpleName() {
    assertEquals("AutomatonParentAwareVisitor", astService.getParentAwareVisitorSimpleName());
    assertEquals("AutomatonParentAwareVisitor", astService.getParentAwareVisitorSimpleName(astcdCompilationUnit.getCDDefinition().getSymbol()));
  }

  @Test
  public void testGetParentAwareVisitorFullName() {
    assertEquals(VISITOR_AUT_PACKAGE + "AutomatonParentAwareVisitor", astService.getParentAwareVisitorFullName());
    assertEquals(VISITOR_AUT_PACKAGE + "AutomatonParentAwareVisitor", astService.getParentAwareVisitorFullName(astcdCompilationUnit.getCDDefinition().getSymbol()));
  }

  @Test
  public void testGetVisitorMethod() {
    ASTCDMethod visitMethod = astService.getVisitorMethod("visit", mcTypeFacade.createQualifiedType("_ast.ASTFoo"));
    assertEquals("visit", visitMethod.getName());
    assertTrue(visitMethod.getMCReturnType().isPresentMCVoidType());

    assertEquals(1, visitMethod.sizeCDParameters());
    assertEquals("node", visitMethod.getCDParameter(0).getName());
    assertDeepEquals("_ast.ASTFoo", visitMethod.getCDParameter(0).getMCType());
  }

  @Test
  public void testGetSuperVisitors() {
    List<ASTMCQualifiedType> allVisitorTypesInHierarchy = astService.getSuperVisitors();
    assertEquals(1, allVisitorTypesInHierarchy.size());
    assertDeepEquals("de.monticore.codegen.ast.lexicals._visitor.LexicalsVisitor", allVisitorTypesInHierarchy.get(0));
  }

  @Test
  public void testGetSuperInheritanceVisitors() {
    List<ASTMCQualifiedType> allVisitorTypesInHierarchy = astService.getSuperInheritanceVisitors();
    assertEquals(1, allVisitorTypesInHierarchy.size());
    assertDeepEquals("de.monticore.codegen.ast.lexicals._visitor.LexicalsInheritanceVisitor", allVisitorTypesInHierarchy.get(0));
  }
}
