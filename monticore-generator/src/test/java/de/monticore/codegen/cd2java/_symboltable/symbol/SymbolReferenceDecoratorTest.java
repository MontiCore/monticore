package de.monticore.codegen.cd2java._symboltable.symbol;

import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.ASTCDConstructor;
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

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getClassBy;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SymbolReferenceDecoratorTest extends DecoratorTestCase {

  private ASTCDClass symbolClassAutomaton;

  private ASTCDClass symbolClassState;

  private GlobalExtensionManagement glex;

  private CDTypeFacade cdTypeFacade;

  private ASTCDCompilationUnit decoratedCompilationUnit;

  private ASTCDCompilationUnit originalCompilationUnit;

  private static final String ENCLOSING_SCOPE_TYPE = "de.monticore.codegen.ast.automaton._symboltable.IAutomatonScope";

  private static final String A_NODE_TYPE_OPT = "Optional<de.monticore.codegen.ast.automaton._ast.ASTAutomaton>";

  private static final String A_NODE_TYPE = "de.monticore.codegen.ast.automaton._ast.ASTAutomaton";

  private static final String ACCESS_MODIFIER_TYPE = "de.monticore.symboltable.modifiers.AccessModifier";

  private static final String I_AUTOMATON_SCOPE = "de.monticore.codegen.ast.automaton._symboltable.IAutomatonScope";

  private static final String AUTOMATON_VISITOR = "de.monticore.codegen.ast.automaton._visitor.AutomatonSymbolVisitor";

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


    SymbolReferenceDecorator decorator = new SymbolReferenceDecorator(this.glex, new SymbolTableService(decoratedCompilationUnit));
    //creates ScopeSpanningSymbol
    ASTCDClass automatonClass = getClassBy("ASTAutomaton", decoratedCompilationUnit);
    this.symbolClassAutomaton = decorator.decorate(automatonClass);

    //creates normal Symbol
    ASTCDClass stateClass = getClassBy("ASTState", decoratedCompilationUnit);
    this.symbolClassState = decorator.decorate(stateClass);
  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalCompilationUnit, decoratedCompilationUnit);
  }

  // ScopeSpanningSymbol

  @Test
  public void testClassNameAutomatonSymbol() {
    assertEquals("AutomatonSymbolReference", symbolClassAutomaton.getName());
  }

  @Test
  public void testSuperInterfacesCountAutomatonSymbol() {
    assertEquals(1, symbolClassAutomaton.sizeInterfaces());
  }

  @Test
  public void testSuperInterfacesAutomatonSymbol() {
    assertDeepEquals("de.monticore.symboltable.references.ISymbolReference", symbolClassAutomaton.getInterface(0));
  }

  @Test
  public void testSuperClassPresent() {
    assertTrue(symbolClassAutomaton.isPresentSuperclass());
  }

  @Test
  public void testSuperClass() {
    assertDeepEquals("de.monticore.codegen.ast.automaton._symboltable.AutomatonSymbol", symbolClassAutomaton.getSuperclass());
  }

  @Test
  public void testConstructorCount() {
    assertEquals(1, symbolClassAutomaton.sizeCDConstructors());
  }

  @Test
  public void testConstructor() {
    ASTCDConstructor cdConstructor = symbolClassAutomaton.getCDConstructor(0);
    assertDeepEquals(PUBLIC, cdConstructor.getModifier());
    assertEquals("AutomatonSymbolReference", cdConstructor.getName());

    assertEquals(2, cdConstructor.sizeCDParameters());
    assertDeepEquals(String.class, cdConstructor.getCDParameter(0).getMCType());
    assertEquals("name", cdConstructor.getCDParameter(0).getName());

    assertDeepEquals("de.monticore.codegen.ast.automaton._symboltable.IAutomatonScope", cdConstructor.getCDParameter(1).getMCType());
    assertEquals("enclosingScope", cdConstructor.getCDParameter(1).getName());

    assertTrue(cdConstructor.isEmptyExceptions());
  }

//  @Test
//  public void testAttributeCount() {
//    assertEquals(7, symbolClassAutomaton.sizeCDAttributes());
//  }
//
//  @Test
//  public void testNameAttribute() {
//    ASTCDAttribute astcdAttribute = getAttributeBy("name", symbolClassAutomaton);
//    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
//    assertDeepEquals(String.class, astcdAttribute.getMCType());
//  }
}
