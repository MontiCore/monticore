package de.monticore.codegen.cd2java._symboltable.symboltablecreator;

import com.github.javaparser.StaticJavaParser;
import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.ASTCDConstructor;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.cd.prettyprint.CD4CodePrinter;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java.factories.CDTypeFacade;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertBoolean;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodBy;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SymbolTableCreatorForSuperTypesTest extends DecoratorTestCase {

  private ASTCDClass symTabCreator;

  private GlobalExtensionManagement glex;

  private ASTCDCompilationUnit decoratedCompilationUnit;

  private ASTCDCompilationUnit originalCompilationUnit;

  private CDTypeFacade cdTypeFacade;

  private static final String I_SUBAUTOMATON_SCOPE = "de.monticore.codegen.symboltable.subautomaton._symboltable.ISubAutomatonScope";

  private static final String AUTOMATON_SYMBOL_TABLE_CREATOR = "de.monticore.codegen.symboltable.automaton._symboltable.AutomatonSymbolTableCreator";

  @Before
  public void setUp() {
    Log.init();
    this.glex = new GlobalExtensionManagement();
    this.cdTypeFacade = CDTypeFacade.getInstance();

    this.glex.setGlobalValue("astHelper", new DecorationHelper());
    this.glex.setGlobalValue("cdPrinter", new CD4CodePrinter());
    decoratedCompilationUnit = this.parse("de", "monticore", "codegen", "symboltable", "SubAutomaton");
    originalCompilationUnit = decoratedCompilationUnit.deepClone();
    this.glex.setGlobalValue("service", new AbstractService(decoratedCompilationUnit));

    SymbolTableCreatorForSuperTypes decorator = new SymbolTableCreatorForSuperTypes(this.glex,
        new SymbolTableService(decoratedCompilationUnit));

    //creates normal Symbol
    List<ASTCDClass> listSuperSTCForSub = decorator.decorate(decoratedCompilationUnit);
    assertEquals(1, listSuperSTCForSub.size());

    assertTrue(listSuperSTCForSub.stream().anyMatch(c -> "AutomatonSTCForSubAutomaton".equals(c.getName())));
    Optional<ASTCDClass> superClassOpt = listSuperSTCForSub.stream().filter(c -> "AutomatonSTCForSubAutomaton".equals(c.getName())).findFirst();
    assertTrue(superClassOpt.isPresent());
    this.symTabCreator = superClassOpt.get();
  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalCompilationUnit, decoratedCompilationUnit);
  }

  @Test
  public void testClassName() {
    assertEquals("AutomatonSTCForSubAutomaton", symTabCreator.getName());
  }

  @Test
  public void testNoSuperInterfaces() {
    assertTrue(symTabCreator.isEmptyInterfaces());
  }

  @Test
  public void testSuperClass() {
    assertTrue(symTabCreator.isPresentSuperclass());
    assertDeepEquals(AUTOMATON_SYMBOL_TABLE_CREATOR, symTabCreator.getSuperclass());
  }

  @Test
  public void testConstructorCount() {
    assertEquals(1, symTabCreator.sizeCDConstructors());
  }

  @Test
  public void testConstructor() {
    ASTCDConstructor cdConstructor = symTabCreator.getCDConstructor(0);
    assertDeepEquals(PUBLIC, cdConstructor.getModifier());
    assertEquals("AutomatonSTCForSubAutomaton", cdConstructor.getName());

    assertEquals(1, cdConstructor.sizeCDParameters());
    assertDeepEquals("Deque<? extends de.monticore.codegen.symboltable.automaton._symboltable.IAutomatonScope>"
        , cdConstructor.getCDParameter(0).getMCType());
    assertEquals("scopeStack", cdConstructor.getCDParameter(0).getName());


    assertTrue(cdConstructor.isEmptyExceptions());
  }

  @Test
  public void testNoAttributes() {
    assertTrue(symTabCreator.isEmptyCDAttributes());
  }

  @Test
  public void testMethods() {
    assertEquals(1, symTabCreator.getCDMethodList().size());
  }

  @Test
  public void testCreateScopeThis() {
    ASTCDMethod method = getMethodBy("createScope", symTabCreator);
    assertDeepEquals(PUBLIC, method.getModifier());

    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals(I_SUBAUTOMATON_SCOPE, method.getMCReturnType().getMCType());

    assertEquals(1, method.sizeCDParameters());
    assertBoolean(method.getCDParameter(0).getMCType());
    assertEquals("shadowing", method.getCDParameter(0).getName());
  }


  @Test
  public void testGeneratedCodeState() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.CLASS, symTabCreator, symTabCreator);
    StaticJavaParser.parse(sb.toString());
  }
}
