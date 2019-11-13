// (c) https://github.com/MontiCore/monticore
package de.monticore.codegen.cd2java._ast.ast_new;

import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._ast.ast_class.ASTService;
import de.monticore.codegen.cd2java.factories.CDModifier;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static org.junit.Assert.*;

public class ASTServiceTest extends DecoratorTestCase {

  private static String AST_AUT_PACKAGE = "de.monticore.codegen.ast.automaton._ast.";

  private ASTService astService;

  private ASTCDCompilationUnit astcdCompilationUnit;

  private ASTCDClass astAutomaton;

  @Before
  public void setup() {
    astcdCompilationUnit = this.parse("de", "monticore", "codegen", "ast", "Automaton");
    astAutomaton = astcdCompilationUnit.getCDDefinition().getCDClass(0);

    astService = new ASTService(astcdCompilationUnit);
  }

  @Test
  public void testCDSymbolPresent() {
    assertTrue(astService.getCDSymbol().isPresentAstNode());
  }

  @Test
  public void testConstructorsCreateEqualService() {
    ASTService astServiceFromDefinitionSymbol = new ASTService(astcdCompilationUnit.getCDDefinition().getSymbol());
    assertTrue(astServiceFromDefinitionSymbol.getCDSymbol().isPresentAstNode());
    assertDeepEquals(astService.getCDSymbol().getAstNode(), astServiceFromDefinitionSymbol.getCDSymbol().getAstNode());
  }

  @Test
  public void testCreateASTService() {
    ASTService createdASTService = ASTService.createASTService(astcdCompilationUnit.getCDDefinition().getSymbol());
    assertTrue(createdASTService.getCDSymbol().isPresentAstNode());
    assertDeepEquals(astService.getCDSymbol().getAstNode(), createdASTService.getCDSymbol().getAstNode());
  }

  @Test
  public void testSubPackage() {
    assertEquals("_ast", astService.getSubPackage());
  }

  @Test
  public void testGetASTBaseInterfaceSimpleName() {
    assertEquals("ASTAutomatonNode", astService.getASTBaseInterfaceSimpleName());
    assertEquals("ASTAutomatonNode", astService.getASTBaseInterfaceSimpleName(astcdCompilationUnit.getCDDefinition().getSymbol()));
  }

  @Test
  public void testGetASTBaseInterfaceFullName() {
    assertEquals(AST_AUT_PACKAGE + "ASTAutomatonNode", astService.getASTBaseInterfaceFullName());
    assertEquals(AST_AUT_PACKAGE + "ASTAutomatonNode", astService.getASTBaseInterfaceFullName(astcdCompilationUnit.getCDDefinition().getSymbol()));
  }

  @Test
  public void testGetASTBaseInterfaceName() {
    assertDeepEquals(AST_AUT_PACKAGE + "ASTAutomatonNode", astService.getASTBaseInterface());
  }

  @Test
  public void testGetASTConstantClassSimpleName() {
    assertEquals("ASTConstantsAutomaton", astService.getASTConstantClassSimpleName());
    assertEquals("ASTConstantsAutomaton", astService.getASTConstantClassSimpleName(astcdCompilationUnit.getCDDefinition().getSymbol()));
  }

  @Test
  public void testGetASTConstantClassFullName() {
    assertEquals(AST_AUT_PACKAGE + "ASTConstantsAutomaton", astService.getASTConstantClassFullName());
    assertEquals(AST_AUT_PACKAGE + "ASTConstantsAutomaton", astService.getASTConstantClassFullName(astcdCompilationUnit.getCDDefinition().getSymbol()));
  }

  @Test
  public void testGetASTSimpleName() {
    assertEquals("ASTAutomaton", astService.getASTSimpleName(astAutomaton));
  }

  @Test
  public void testGetASTFullName() {
    assertEquals(AST_AUT_PACKAGE + "ASTAutomaton", astService.getASTFullName(astAutomaton));
    assertEquals(AST_AUT_PACKAGE + "ASTAutomaton", astService.getASTFullName(astAutomaton, astcdCompilationUnit.getCDDefinition().getSymbol()));
  }

  @Test
  public void testIsSymbolWithoutName() {
    // test if has a name
    assertFalse(astService.isSymbolWithoutName(astAutomaton));
    // remove name attribute
    List<ASTCDAttribute> attributeList = astAutomaton.deepClone().getCDAttributeList().stream()
        .filter(a -> !"name".equals(a.getName()))
        .collect(Collectors.toList());
    ASTCDClass astAutomatonWithoutName = astAutomaton.deepClone();
    astAutomatonWithoutName.setCDAttributeList(attributeList);
    assertTrue(astService.isSymbolWithoutName(astAutomatonWithoutName));
  }

  @Test
  public void testCreateGetNameMethod() {
    ASTCDMethod getNameMethod = astService.createGetNameMethod();
    assertEquals("getName", getNameMethod.getName());
    assertDeepEquals(CDModifier.PUBLIC_ABSTRACT, getNameMethod.getModifier());
    assertTrue(getNameMethod.getMCReturnType().isPresentMCType());
    assertDeepEquals(String.class, getNameMethod.getMCReturnType().getMCType());
    assertTrue(getNameMethod.isEmptyCDParameters());
  }
}
