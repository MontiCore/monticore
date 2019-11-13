// (c) https://github.com/MontiCore/monticore
package de.monticore.codegen.cd2java._ast.factory;

import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import org.junit.Before;
import org.junit.Test;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class NodeFactoryServiceTest extends DecoratorTestCase {

  private static String AST_AUT_PACKAGE = "de.monticore.codegen.ast.automaton._ast.";

  private NodeFactoryService factoryService;

  private ASTCDCompilationUnit astcdCompilationUnit;

  @Before
  public void setup() {
    astcdCompilationUnit = this.parse("de", "monticore", "codegen", "ast", "Automaton");

    factoryService = new NodeFactoryService(astcdCompilationUnit);
  }

  @Test
  public void testCDSymbolPresent() {
    assertTrue(factoryService.getCDSymbol().isPresentAstNode());
  }

  @Test
  public void testConstructorsCreateEqualService() {
    NodeFactoryService factoryServiceFromDefinitionSymbol = new NodeFactoryService(astcdCompilationUnit.getCDDefinition().getSymbol());
    assertTrue(factoryServiceFromDefinitionSymbol.getCDSymbol().isPresentAstNode());
    assertDeepEquals(factoryService.getCDSymbol().getAstNode(), factoryServiceFromDefinitionSymbol.getCDSymbol().getAstNode());
  }

  @Test
  public void testSubPackage() {
    assertEquals("_ast", factoryService.getSubPackage());
  }

  @Test
  public void testCreateNodeFactoryService() {
    NodeFactoryService nodeFactoryService = NodeFactoryService.createNodeFactoryService(astcdCompilationUnit.getCDDefinition().getSymbol());
    assertTrue(nodeFactoryService.getCDSymbol().isPresentAstNode());
    assertDeepEquals(factoryService.getCDSymbol().getAstNode(), nodeFactoryService.getCDSymbol().getAstNode());
  }

  @Test
  public void testGetASTBaseInterfaceSimpleName() {
    assertEquals("AutomatonNodeFactory", factoryService.getNodeFactorySimpleTypeName());
    assertEquals("AutomatonNodeFactory", factoryService.getNodeFactorySimpleTypeName(astcdCompilationUnit.getCDDefinition().getSymbol()));
  }

  @Test
  public void testGetASTBaseInterfaceFullName() {
    assertEquals(AST_AUT_PACKAGE + "AutomatonNodeFactory", factoryService.getNodeFactoryFullTypeName());
    assertEquals(AST_AUT_PACKAGE + "AutomatonNodeFactory", factoryService.getNodeFactoryFullTypeName(astcdCompilationUnit.getCDDefinition().getSymbol()));
  }
}
