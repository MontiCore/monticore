/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable.scopeskeletoncreator;

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
import de.monticore.codegen.cd2java._symboltable.symboltablecreator.PhasedSymbolTableCreatorDelegatorDecorator;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.se_rwth.commons.logging.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static de.monticore.cd.facade.CDModifier.*;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getAttributeBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodBy;
import static org.junit.Assert.*;

public class ScopeSkeletonCreatorDelegatorDecoratorTest extends DecoratorTestCase {

  private ASTCDClass scopeSkeletonCreator;

  private GlobalExtensionManagement glex;

  private ASTCDCompilationUnit decoratedCompilationUnit;

  private ASTCDCompilationUnit originalCompilationUnit;

  private de.monticore.types.MCTypeFacade MCTypeFacade;

  private ScopeSkeletonCreatorDelegatorDecorator decorator;

  private static final String AUTOMATON_GLOBAL_SCOPE = "de.monticore.codegen.symboltable.automaton._symboltable.IAutomatonGlobalScope";

  private static final String AUTOMATON_SCOPE_SKELETON_CREATOR = "AutomatonScopeSkeletonCreator";

  private static final String AUTOMATON_DELEGATOR_VISITOR = "de.monticore.codegen.symboltable.automaton._visitor.AutomatonDelegatorVisitor";

  private static final String AST_AUTOMATON = "de.monticore.codegen.symboltable.automaton._ast.ASTAutomaton";

  @Before
  public void setUp() {
    LogStub.init();         // replace log by a sideffect free variant
    // LogStub.initPlusLog();  // for manual testing purpose only
    this.glex = new GlobalExtensionManagement();
    this.MCTypeFacade = MCTypeFacade.getInstance();

    this.glex.setGlobalValue("astHelper", DecorationHelper.getInstance());
    this.glex.setGlobalValue("cdPrinter", new CD4CodePrinter());
    decoratedCompilationUnit = this.parse("de", "monticore", "codegen", "symboltable", "Automaton");
    originalCompilationUnit = decoratedCompilationUnit.deepClone();
    this.glex.setGlobalValue("service", new AbstractService(decoratedCompilationUnit));

    this.decorator = new ScopeSkeletonCreatorDelegatorDecorator(this.glex,
        new SymbolTableService(decoratedCompilationUnit), new VisitorService(decoratedCompilationUnit));

    //creates normal Symbol
    Optional<ASTCDClass> optScopeSkeletonCreator = decorator.decorate(decoratedCompilationUnit);
    assertTrue(optScopeSkeletonCreator.isPresent());
    this.scopeSkeletonCreator = optScopeSkeletonCreator.get();
  }

  @Test
  public void testNoStartProd() {
    LogStub.init();         // replace log by a sideffect free variant
    // LogStub.initPlusLog();  // for manual testing purpose only
    GlobalExtensionManagement glex = new GlobalExtensionManagement();

    glex.setGlobalValue("astHelper", DecorationHelper.getInstance());
    glex.setGlobalValue("cdPrinter", new CD4CodePrinter());
    ASTCDCompilationUnit cd = this.parse("de", "monticore", "codegen", "symboltable", "Automaton");
    glex.setGlobalValue("service", new AbstractService(cd));

    SymbolTableService mockService = Mockito.spy(new SymbolTableService(cd));
    Mockito.doReturn(Optional.empty()).when(mockService).getStartProdASTFullName(Mockito.any(ASTCDDefinition.class));

    ScopeSkeletonCreatorDelegatorDecorator decorator = new ScopeSkeletonCreatorDelegatorDecorator(glex,
        mockService, new VisitorService(cd));

    //create non present SymbolTableCreator
    Optional<ASTCDClass> optScopeSkeletonCreatorDelegator = decorator.decorate(cd);
    assertFalse(optScopeSkeletonCreatorDelegator.isPresent());
  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalCompilationUnit, decoratedCompilationUnit);
  }

  @Test
  public void testClassName() {
    assertEquals("AutomatonScopeSkeletonCreatorDelegator", scopeSkeletonCreator.getName());
  }

  @Test
  public void testNoSuperInterfaces() {
    assertTrue(scopeSkeletonCreator.isEmptyInterface());
  }

  @Test
  public void testSuperClass() {
    assertTrue(scopeSkeletonCreator.isPresentSuperclass());
    assertDeepEquals(AUTOMATON_DELEGATOR_VISITOR, scopeSkeletonCreator.getSuperclass());
  }

  @Test
  public void testConstructorCount() {
    assertEquals(2, scopeSkeletonCreator.sizeCDConstructors());
  }

  @Test
  public void testConstructor() {
    ASTCDConstructor cdConstructor = scopeSkeletonCreator.getCDConstructor(0);
    assertDeepEquals(PUBLIC, cdConstructor.getModifier());
    assertEquals("AutomatonScopeSkeletonCreatorDelegator", cdConstructor.getName());

    assertEquals(1, cdConstructor.sizeCDParameters());
    assertDeepEquals(AUTOMATON_GLOBAL_SCOPE, cdConstructor.getCDParameter(0).getMCType());
    assertEquals("globalScope", cdConstructor.getCDParameter(0).getName());


    assertTrue(cdConstructor.isEmptyException());
  }

  @Test
  public void testZeroArgsConstructor(){
    ASTCDConstructor constructor = scopeSkeletonCreator.getCDConstructor(1);
    assertDeepEquals(PUBLIC, constructor.getModifier());
    assertEquals("AutomatonScopeSkeletonCreatorDelegator", constructor.getName());
    assertTrue(constructor.isEmptyCDParameters());
    assertTrue(constructor.isEmptyException());
  }

  @Test
  public void testAttributeSize() {
    assertEquals(3, scopeSkeletonCreator.sizeCDAttributes());
  }

  @Test
  public void testScopeStackAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("scopeStack", scopeSkeletonCreator);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals("Deque<de.monticore.codegen.symboltable.automaton._symboltable.IAutomatonScope>", astcdAttribute.getMCType());
  }

  @Test
  public void testSymTabAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("symbolTable", scopeSkeletonCreator);
    assertDeepEquals(PROTECTED_FINAL, astcdAttribute.getModifier());
    assertDeepEquals(AUTOMATON_SCOPE_SKELETON_CREATOR, astcdAttribute.getMCType());
  }

  @Test
  public void testGlobalScopeAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("globalScope", scopeSkeletonCreator);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals(AUTOMATON_GLOBAL_SCOPE, astcdAttribute.getMCType());
  }

  @Test
  public void testMethods() {
    assertEquals(1, scopeSkeletonCreator.getCDMethodList().size());
  }

  @Test
  public void testCreateFromASTMethod() {
    ASTCDMethod method = getMethodBy("createFromAST", scopeSkeletonCreator);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals("de.monticore.codegen.symboltable.automaton._symboltable.IAutomatonArtifactScope", method.getMCReturnType().getMCType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(AST_AUTOMATON, method.getCDParameter(0).getMCType());
    assertEquals("rootNode", method.getCDParameter(0).getName());
  }

  @Test
  public void testGeneratedCodeState() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.CLASS, scopeSkeletonCreator, scopeSkeletonCreator);
    // test parsing
    ParserConfiguration configuration = new ParserConfiguration();
    JavaParser parser = new JavaParser(configuration);
    ParseResult parseResult = parser.parse(sb.toString());
    assertTrue(parseResult.isSuccessful());
  }
}
