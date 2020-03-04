package de.monticore.codegen.cd2java._symboltable.symboltablecreator;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.prettyprint.CD4CodePrinter;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecorationHelper;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static de.monticore.cd.facade.CDModifier.*;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getAttributeBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodBy;
import static org.junit.Assert.*;

public class SymbolTableCreatorDelegatorDecoratorTest extends DecoratorTestCase {

  private ASTCDClass symTabCreator;

  private GlobalExtensionManagement glex;

  private ASTCDCompilationUnit decoratedCompilationUnit;

  private ASTCDCompilationUnit originalCompilationUnit;

  private de.monticore.types.MCTypeFacade MCTypeFacade;

  private SymbolTableCreatorDelegatorDecorator decorator;

  private static final String AUTOMATON_GLOBAL_SCOPE = "de.monticore.codegen.symboltable.automaton._symboltable.AutomatonGlobalScope";

  private static final String AUTOMATON_SYMBOL_TABLE_CREATOR = "AutomatonSymbolTableCreator";

  private static final String AUTOMATON_DELEGATOR_VISITOR = "de.monticore.codegen.symboltable.automaton._visitor.AutomatonDelegatorVisitor";

  private static final String AST_AUTOMATON = "de.monticore.codegen.symboltable.automaton._ast.ASTAutomaton";

  @Before
  public void setUp() {
    Log.init();
    this.glex = new GlobalExtensionManagement();
    this.MCTypeFacade = MCTypeFacade.getInstance();

    this.glex.setGlobalValue("astHelper", DecorationHelper.getInstance());
    this.glex.setGlobalValue("cdPrinter", new CD4CodePrinter());
    decoratedCompilationUnit = this.parse("de", "monticore", "codegen", "symboltable", "Automaton");
    originalCompilationUnit = decoratedCompilationUnit.deepClone();
    this.glex.setGlobalValue("service", new AbstractService(decoratedCompilationUnit));

    this.decorator = new SymbolTableCreatorDelegatorDecorator(this.glex,
        new SymbolTableService(decoratedCompilationUnit), new VisitorService(decoratedCompilationUnit));

    //creates normal Symbol
    Optional<ASTCDClass> optSymTabCreator = decorator.decorate(decoratedCompilationUnit);
    assertTrue(optSymTabCreator.isPresent());
    this.symTabCreator = optSymTabCreator.get();
  }

  @Test
  public void testNoStartProd() {
    ASTCDCompilationUnit noStartProd = this.parse("de", "monticore", "codegen", "ast", "NoStartProd");

    Optional<ASTCDClass> astcdClass = decorator.decorate(noStartProd);
    assertFalse(astcdClass.isPresent());
  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalCompilationUnit, decoratedCompilationUnit);
  }

  @Test
  public void testClassName() {
    assertEquals("AutomatonSymbolTableCreatorDelegator", symTabCreator.getName());
  }

  @Test
  public void testNoSuperInterfaces() {
    assertTrue(symTabCreator.isEmptyInterfaces());
  }

  @Test
  public void testSuperClass() {
    assertTrue(symTabCreator.isPresentSuperclass());
    assertDeepEquals(AUTOMATON_DELEGATOR_VISITOR, symTabCreator.getSuperclass());
  }

  @Test
  public void testConstructorCount() {
    assertEquals(1, symTabCreator.sizeCDConstructors());
  }

  @Test
  public void testConstructor() {
    ASTCDConstructor cdConstructor = symTabCreator.getCDConstructor(0);
    assertDeepEquals(PUBLIC, cdConstructor.getModifier());
    assertEquals("AutomatonSymbolTableCreatorDelegator", cdConstructor.getName());

    assertEquals(1, cdConstructor.sizeCDParameters());
    assertDeepEquals(AUTOMATON_GLOBAL_SCOPE, cdConstructor.getCDParameter(0).getMCType());
    assertEquals("globalScope", cdConstructor.getCDParameter(0).getName());


    assertTrue(cdConstructor.isEmptyExceptions());
  }

  @Test
  public void testAttributeSize() {
    assertEquals(3, symTabCreator.sizeCDAttributes());
  }

  @Test
  public void testScopeStackAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("scopeStack", symTabCreator);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals("Deque<de.monticore.codegen.symboltable.automaton._symboltable.IAutomatonScope>", astcdAttribute.getMCType());
  }

  @Test
  public void testSymTabAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("symbolTable", symTabCreator);
    assertDeepEquals(PROTECTED_FINAL, astcdAttribute.getModifier());
    assertDeepEquals(AUTOMATON_SYMBOL_TABLE_CREATOR, astcdAttribute.getMCType());
  }

  @Test
  public void testGlobalScopeAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("globalScope", symTabCreator);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals(AUTOMATON_GLOBAL_SCOPE, astcdAttribute.getMCType());
  }

  @Test
  public void testMethods() {
    assertEquals(1, symTabCreator.getCDMethodList().size());
  }

  @Test
  public void testCreateFromASTMethod() {
    ASTCDMethod method = getMethodBy("createFromAST", symTabCreator);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals("de.monticore.codegen.symboltable.automaton._symboltable.AutomatonArtifactScope", method.getMCReturnType().getMCType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(AST_AUTOMATON, method.getCDParameter(0).getMCType());
    assertEquals("rootNode", method.getCDParameter(0).getName());
  }

  @Test
  public void testGeneratedCodeState() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.CLASS, symTabCreator, symTabCreator);
    // test parsing
    ParserConfiguration configuration = new ParserConfiguration();
    JavaParser parser = new JavaParser(configuration);
    ParseResult parseResult = parser.parse(sb.toString());
    assertTrue(parseResult.isSuccessful());
  }
}
