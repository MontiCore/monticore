package de.monticore.codegen.cd2java._symboltable.symbol;

import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.ASTCDInterface;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.cd.prettyprint.CD4CodePrinter;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java.factories.CDTypeFacade;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertBoolean;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getClassBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodBy;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC_ABSTRACT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SymbolResolvingDelegateInterfaceDecoratorTest extends DecoratorTestCase {

  private ASTCDInterface symbolClassAutomaton;

  private GlobalExtensionManagement glex;

  private CDTypeFacade cdTypeFacade;

  private ASTCDCompilationUnit decoratedCompilationUnit;

  private ASTCDCompilationUnit originalCompilationUnit;

  private static final String AUTOMATON_SYMBOL = "de.monticore.codegen.ast.automaton._symboltable.AutomatonSymbol";

  private static final String PREDICATE = "java.util.function.Predicate<de.monticore.codegen.ast.automaton._symboltable.AutomatonSymbol>";

  private static final String ACCESS_MODIFIER = "de.monticore.symboltable.modifiers.AccessModifier";

  @Before
  public void setUp() {
    Log.init();
    this.cdTypeFacade = CDTypeFacade.getInstance();
    this.glex = new GlobalExtensionManagement();

    this.glex.setGlobalValue("astHelper", new DecorationHelper());
    this.glex.setGlobalValue("cdPrinter", new CD4CodePrinter());
    decoratedCompilationUnit = this.parse("de", "monticore", "codegen", "ast", "Automaton");
    originalCompilationUnit = decoratedCompilationUnit.deepClone();
    this.glex.setGlobalValue("service", new AbstractService(decoratedCompilationUnit));


    SymbolResolvingDelegateInterfaceDecorator decorator = new SymbolResolvingDelegateInterfaceDecorator(this.glex, new SymbolTableService(decoratedCompilationUnit));
    //creates ScopeSpanningSymbol
    ASTCDClass automatonClass = getClassBy("ASTAutomaton", decoratedCompilationUnit);
    this.symbolClassAutomaton = decorator.decorate(automatonClass);

  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalCompilationUnit, decoratedCompilationUnit);
  }

  // ScopeSpanningSymbol

  @Test
  public void testClassNameAutomatonSymbol() {
    assertEquals("IAutomatonSymbolResolvingDelegate", symbolClassAutomaton.getName());
  }

  @Test
  public void testNoSuperInterfacesl() {
    assertTrue( symbolClassAutomaton.isEmptyInterfaces());
  }

  @Test
  public void testNoAttribute() {
    assertTrue( symbolClassAutomaton.isEmptyCDAttributes());
  }

  @Test
  public void testMethods() {
    assertEquals(1, symbolClassAutomaton.getCDMethodList().size());
  }

  @Test
  public void testResolveAdaptedLocallyManyMethod() {
    ASTCDMethod method = getMethodBy("resolveAdaptedAutomatonSymbol", symbolClassAutomaton);

    assertDeepEquals(PUBLIC_ABSTRACT, method.getModifier());
    assertDeepEquals(cdTypeFacade.createListTypeOf(AUTOMATON_SYMBOL), method.getMCReturnType().getMCType());
    assertEquals(4, method.sizeCDParameters());
    assertBoolean(method.getCDParameter(0).getMCType());
    assertEquals("foundSymbols", method.getCDParameter(0).getName());
    assertDeepEquals(String.class, method.getCDParameter(1).getMCType());
    assertEquals("name", method.getCDParameter(1).getName());
    assertDeepEquals(ACCESS_MODIFIER, method.getCDParameter(2).getMCType());
    assertEquals("modifier", method.getCDParameter(2).getName());
    assertDeepEquals(PREDICATE, method.getCDParameter(3).getMCType());
    assertEquals("predicate", method.getCDParameter(3).getName());
  }
}
