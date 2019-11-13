// (c) https://github.com/MontiCore/monticore
package de.monticore.codegen.cd2java._symboltable;

import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.ASTCDDefinition;
import de.monticore.cd.cd4analysis._ast.ASTModifier;
import de.monticore.cd.cd4analysis._parser.CD4AnalysisParser;
import de.monticore.cd.cd4analysis._symboltable.CD4AnalysisSymTabMill;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java.factories.MCTypeFacade;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static org.junit.Assert.*;

public class SymbolTableServiceTest extends DecoratorTestCase {

  private SymbolTableService symTabService;

  private ASTCDCompilationUnit astcdCompilationUnit;

  private ASTCDClass astAutomaton;

  private MCTypeFacade mcTypeFacade;

  @Before
  public void setup() {
    this.mcTypeFacade = MCTypeFacade.getInstance();

    astcdCompilationUnit = this.parse("de", "monticore", "codegen", "symboltable", "Automaton");
    astAutomaton = astcdCompilationUnit.getCDDefinition().getCDClass(0);

    symTabService = new SymbolTableService(astcdCompilationUnit);
  }

  @Test
  public void testCDSymbolPresent() {
    assertTrue(symTabService.getCDSymbol().isPresentAstNode());
  }

  @Test
  public void testConstructorsCreateEqualService() {
    SymbolTableService astServiceFromDefinitionSymbol = new SymbolTableService(astcdCompilationUnit.getCDDefinition().getSymbol());
    assertTrue(astServiceFromDefinitionSymbol.getCDSymbol().isPresentAstNode());
    assertDeepEquals(symTabService.getCDSymbol().getAstNode(), astServiceFromDefinitionSymbol.getCDSymbol().getAstNode());
  }

  @Test
  public void testCreateSymbolTableService() {
    SymbolTableService createdSymbolTableService = SymbolTableService.createSymbolTableService(astcdCompilationUnit.getCDDefinition().getSymbol());
    assertTrue(createdSymbolTableService.getCDSymbol().isPresentAstNode());
    assertDeepEquals(symTabService.getCDSymbol().getAstNode(), createdSymbolTableService.getCDSymbol().getAstNode());
  }

  @Test
  public void testSubPackage() {
    assertEquals("_symboltable", symTabService.getSubPackage());
  }

  @Test
  public void testStartProdValue() throws IOException {
    CD4AnalysisParser cd4AnalysisParser = new CD4AnalysisParser();
    Optional<ASTModifier> astModifier = cd4AnalysisParser.parse_StringModifier("<<startProd=\"_ast.ASTFoo\">> public");
    assertTrue(astModifier.isPresent());
    assertFalse(cd4AnalysisParser.hasErrors());

    Optional<String> startProdValue = symTabService.getStartProdValue(astModifier.get());
    assertTrue(startProdValue.isPresent());
    assertEquals("_ast.ASTFoo", startProdValue.get());
  }

  @Test
  public void testHasStartProdWithCDDefinitionStereotype() throws IOException {
    CD4AnalysisParser cd4AnalysisParser = new CD4AnalysisParser();
    Optional<ASTCDDefinition> astcdDefinition = cd4AnalysisParser.parse_StringCDDefinition(
        "<<startProd=\"_ast.ASTFoo\">> public classdiagram Bar {}");
    assertTrue(astcdDefinition.isPresent());
    assertFalse(cd4AnalysisParser.hasErrors());

    assertTrue(symTabService.hasStartProd(astcdDefinition.get()));
  }

  @Test
  public void testHasStartProdFromClass() throws IOException {
    CD4AnalysisParser cd4AnalysisParser = new CD4AnalysisParser();
    Optional<ASTCDDefinition> astcdDefinition = cd4AnalysisParser.parse_StringCDDefinition(
        " public classdiagram Bar {" +
            "<<startProd>> public class ASTFoo{}" +
            "}");
    assertTrue(astcdDefinition.isPresent());
    assertFalse(cd4AnalysisParser.hasErrors());

    assertTrue(symTabService.hasStartProd(astcdDefinition.get()));
  }

  @Test
  public void testHasStartProdFromInterface() throws IOException {
    CD4AnalysisParser cd4AnalysisParser = new CD4AnalysisParser();
    Optional<ASTCDDefinition> astcdDefinition = cd4AnalysisParser.parse_StringCDDefinition(
        " public classdiagram Bar {" +
            "<<startProd>> public interface ASTFoo{}" +
            "}");
    assertTrue(astcdDefinition.isPresent());
    assertFalse(cd4AnalysisParser.hasErrors());

    assertTrue(symTabService.hasStartProd(astcdDefinition.get()));
  }

  @Test
  public void testGetStartProdWithCDDefinitionStereotype() throws IOException {
    CD4AnalysisParser cd4AnalysisParser = new CD4AnalysisParser();
    Optional<ASTCDDefinition> astcdDefinition = cd4AnalysisParser.parse_StringCDDefinition(
        "<<startProd=\"_ast.ASTFoo\">> public classdiagram Bar {}");
    assertTrue(astcdDefinition.isPresent());
    assertFalse(cd4AnalysisParser.hasErrors());

    Optional<String> startProdValue = symTabService.getStartProd(astcdDefinition.get());
    assertTrue(startProdValue.isPresent());
    assertEquals("_ast.ASTFoo", startProdValue.get());
  }

  @Test
  public void testGetStartProdFromClass() throws IOException {
    CD4AnalysisParser cd4AnalysisParser = new CD4AnalysisParser();
    Optional<ASTCDDefinition> astcdDefinition = cd4AnalysisParser.parse_StringCDDefinition(
        " public classdiagram Bar {" +
            "<<startProd>> public class ASTFoo{}" +
            "}");
    assertTrue(astcdDefinition.isPresent());
    assertFalse(cd4AnalysisParser.hasErrors());

    Optional<String> startProdValue = symTabService.getStartProd(astcdDefinition.get());
    assertTrue(startProdValue.isPresent());
    assertEquals("de.monticore.codegen.symboltable.Automaton.ASTFoo", startProdValue.get());
  }

  @Test
  public void testGetStartProdFromInterface() throws IOException {
    CD4AnalysisParser cd4AnalysisParser = new CD4AnalysisParser();
    Optional<ASTCDDefinition> astcdDefinition = cd4AnalysisParser.parse_StringCDDefinition(
        " public classdiagram Bar {" +
            "<<startProd>> public interface ASTFoo{}" +
            "}");
    assertTrue(astcdDefinition.isPresent());
    assertFalse(cd4AnalysisParser.hasErrors());

    Optional<String> startProdValue = symTabService.getStartProd(astcdDefinition.get());
    assertTrue(startProdValue.isPresent());
    assertEquals("de.monticore.codegen.symboltable.Automaton.ASTFoo", startProdValue.get());
  }

  @Test
  public void testDetermineReturnType() {
    ASTMCType booleanType = mcTypeFacade.createBooleanType();
    assertEquals("false", symTabService.determineReturnType(booleanType));

    ASTMCType intType = mcTypeFacade.createIntType();
    assertEquals("0", symTabService.determineReturnType(intType));

    ASTMCQualifiedType qualifiedType = mcTypeFacade.createQualifiedType("a.b.C");
    assertEquals("null", symTabService.determineReturnType(qualifiedType));
  }

  @Test
  public void testHasProdOnlyInterface() throws IOException {
    CD4AnalysisParser cd4AnalysisParser = new CD4AnalysisParser();
    Optional<ASTCDDefinition> astcdDefinition = cd4AnalysisParser.parse_StringCDDefinition(
        " public classdiagram Bar {" +
            "<<startProd>> public interface ASTFoo{}" +
            "}");
    assertTrue(astcdDefinition.isPresent());
    assertFalse(cd4AnalysisParser.hasErrors());

    assertTrue(symTabService.hasProd(astcdDefinition.get()));
  }

  @Test
  public void testHasProdOnlyClass() throws IOException {
    CD4AnalysisParser cd4AnalysisParser = new CD4AnalysisParser();
    Optional<ASTCDDefinition> astcdDefinition = cd4AnalysisParser.parse_StringCDDefinition(
        " public classdiagram Bar {" +
            "<<startProd>> public class ASTFoo{}" +
            "}");
    assertTrue(astcdDefinition.isPresent());
    assertFalse(cd4AnalysisParser.hasErrors());

    assertTrue(symTabService.hasProd(astcdDefinition.get()));
  }

  @Test
  public void testHasProdNoneOfBoth() throws IOException {
    CD4AnalysisParser cd4AnalysisParser = new CD4AnalysisParser();
    Optional<ASTCDDefinition> astcdDefinition = cd4AnalysisParser.parse_StringCDDefinition(
        " public classdiagram Bar {}");
    assertTrue(astcdDefinition.isPresent());
    assertFalse(cd4AnalysisParser.hasErrors());

    assertFalse(symTabService.hasProd(astcdDefinition.get()));
  }

  @Test
  public void testGetSymbolTypeValueOfOwnSymbol() throws IOException {
    CD4AnalysisParser cd4AnalysisParser = new CD4AnalysisParser();
    Optional<ASTModifier> astcdDefinition = cd4AnalysisParser.parse_StringModifier(
        "<<symbol=\"FooSymbol\">> public");
    assertTrue(astcdDefinition.isPresent());
    assertFalse(cd4AnalysisParser.hasErrors());

    Optional<String> symbolTypeValue = symTabService.getSymbolTypeValue(astcdDefinition.get());
    assertTrue(symbolTypeValue.isPresent());
    assertEquals("FooSymbol", symbolTypeValue.get());
  }

  @Test
  public void testGetSymbolTypeValueOfInherited() throws IOException {
    CD4AnalysisParser cd4AnalysisParser = new CD4AnalysisParser();
    Optional<ASTModifier> astcdDefinition = cd4AnalysisParser.parse_StringModifier(
        "<<inheritedSymbol=\"FooSymbol\">> public");
    assertTrue(astcdDefinition.isPresent());
    assertFalse(cd4AnalysisParser.hasErrors());

    Optional<String> symbolTypeValue = symTabService.getSymbolTypeValue(astcdDefinition.get());
    assertTrue(symbolTypeValue.isPresent());
    assertEquals("FooSymbol", symbolTypeValue.get());
  }

  @Test
  public void testGetDefiningSymbolSimpleNameOnlyInheritedSymbol() throws IOException {
    CD4AnalysisParser cd4AnalysisParser = new CD4AnalysisParser();
    Optional<ASTCDClass> astcdClass = cd4AnalysisParser.parse_StringCDClass(
        "<<inheritedSymbol=\"FooSymbol\">> public class ASTFoo{}");
    assertTrue(astcdClass.isPresent());
    assertFalse(cd4AnalysisParser.hasErrors());

    Optional<String> symbolTypeValue = symTabService.getDefiningSymbolSimpleName(astcdClass.get());
    assertFalse(symbolTypeValue.isPresent());
  }

  @Test
  public void testGetDefiningSymbolSimpleNameNoSymbol() throws IOException {
    CD4AnalysisParser cd4AnalysisParser = new CD4AnalysisParser();
    Optional<ASTCDClass> astcdClass = cd4AnalysisParser.parse_StringCDClass(
        "public class ASTFoo{}");
    assertTrue(astcdClass.isPresent());
    assertFalse(cd4AnalysisParser.hasErrors());

    Optional<String> symbolTypeValue = symTabService.getDefiningSymbolSimpleName(astcdClass.get());
    assertFalse(symbolTypeValue.isPresent());
  }

  @Test
  public void testGetDefiningSymbolSimpleName() throws IOException {
    CD4AnalysisParser cd4AnalysisParser = new CD4AnalysisParser();
    Optional<ASTCDClass> astcdClass = cd4AnalysisParser.parse_StringCDClass(
        "<<symbol>> public class ASTFoo{}");
    assertTrue(astcdClass.isPresent());
    assertFalse(cd4AnalysisParser.hasErrors());

    Optional<String> symbolTypeValue = symTabService.getDefiningSymbolSimpleName(astcdClass.get());
    assertTrue(symbolTypeValue.isPresent());
    assertEquals("FooSymbol", symbolTypeValue.get());
  }

  @Test
  public void testGetSymbolFullName() throws IOException {
    CDDefinitionSymbol bar = CD4AnalysisSymTabMill.cDDefinitionSymbolBuilder().setName("Bar").build();
    CD4AnalysisParser cd4AnalysisParser = new CD4AnalysisParser();

    Optional<ASTCDClass> astcdClass = cd4AnalysisParser.parse_StringCDClass(
        "<<inheritedSymbol=\"a.b.FooSymbol\">> public class Faa {}");
    assertTrue(astcdClass.isPresent());
    assertFalse(cd4AnalysisParser.hasErrors());

    assertEquals("a.b.FooSymbol", symTabService.getSymbolFullName(astcdClass.get(), bar));
  }

  @Test
  public void testGetSymbolSimpleName() throws IOException {
    CD4AnalysisParser cd4AnalysisParser = new CD4AnalysisParser();

    Optional<ASTCDClass> astcdClass = cd4AnalysisParser.parse_StringCDClass(
        "<<inheritedSymbol=\"a.b.c.FooSymbol\">> public class Faa {}");
    assertTrue(astcdClass.isPresent());
    assertFalse(cd4AnalysisParser.hasErrors());

    assertEquals("FooSymbol", symTabService.getSymbolSimpleName(astcdClass.get()));
  }
}
