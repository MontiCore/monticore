/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable.scopesgenitor;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import de.monticore.cd.codegen.CD2JavaTemplates;
import de.monticore.cd.codegen.CdUtilsPrinter;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.DecorationHelper;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static de.monticore.cd.facade.CDModifier.PROTECTED;
import static de.monticore.cd.facade.CDModifier.PROTECTED_FINAL;
import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getAttributeBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ScopesGenitorDelegatorDecoratorTest extends DecoratorTestCase {

  private ASTCDClass scopesGenitorClass;

  private ASTCDCompilationUnit decoratedCompilationUnit;

  private ASTCDCompilationUnit originalCompilationUnit;

  private de.monticore.types.MCTypeFacade MCTypeFacade;

  private ScopesGenitorDelegatorDecorator decorator;

  private static final String AUTOMATON_GLOBAL_SCOPE = "de.monticore.codegen.symboltable.automaton._symboltable.IAutomatonGlobalScope";

  private static final String AUTOMATON_SCOPES_GENITOR = "AutomatonScopesGenitor";

  private static final String AUTOMATON_TRAVERSER = "de.monticore.codegen.symboltable.automaton._visitor.AutomatonTraverser";

  private static final String AST_AUTOMATON = "de.monticore.codegen.symboltable.automaton._ast.ASTAutomaton";

  @Before
  public void setUp() {
    this.MCTypeFacade = MCTypeFacade.getInstance();

    decoratedCompilationUnit = this.parse("de", "monticore", "codegen", "symboltable", "Automaton");
    originalCompilationUnit = decoratedCompilationUnit.deepClone();
    this.glex.setGlobalValue("service", new AbstractService(decoratedCompilationUnit));

    this.decorator = new ScopesGenitorDelegatorDecorator(this.glex,
        new SymbolTableService(decoratedCompilationUnit), new VisitorService(decoratedCompilationUnit));

    //creates normal Symbol
    Optional<ASTCDClass> optScopeSkeletonCreator = decorator.decorate(decoratedCompilationUnit);
    assertTrue(optScopeSkeletonCreator.isPresent());
    this.scopesGenitorClass = optScopeSkeletonCreator.get();
  }

  @Test
  public void testNoStartProd() {
    LogStub.init();         // replace log by a sideffect free variant
    // LogStub.initPlusLog();  // for manual testing purpose only
    GlobalExtensionManagement glex = new GlobalExtensionManagement();

    glex.setGlobalValue("astHelper", DecorationHelper.getInstance());
    glex.setGlobalValue("cdPrinter", new CdUtilsPrinter());
    ASTCDCompilationUnit cd = this.parse("de", "monticore", "codegen", "symboltable", "Automaton");
    glex.setGlobalValue("service", new AbstractService(cd));

    SymbolTableService mockService = Mockito.spy(new SymbolTableService(cd));
    Mockito.doReturn(Optional.empty()).when(mockService).getStartProdASTFullName(Mockito.any(ASTCDDefinition.class));

    ScopesGenitorDelegatorDecorator decorator = new ScopesGenitorDelegatorDecorator(glex,
        mockService, new VisitorService(cd));

    //create non present SymbolTableCreator
    Optional<ASTCDClass> optScopeSkeletonCreatorDelegator = decorator.decorate(cd);
    assertFalse(optScopeSkeletonCreatorDelegator.isPresent());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalCompilationUnit, decoratedCompilationUnit);
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testClassName() {
    assertEquals("AutomatonScopesGenitorDelegator", scopesGenitorClass.getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testNoSuperInterfaces() {
    assertFalse(scopesGenitorClass.isPresentCDInterfaceUsage());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testNoSuperClass() {
    assertFalse(scopesGenitorClass.isPresentCDExtendUsage());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testConstructorCount() {
    assertEquals(1, scopesGenitorClass.getCDConstructorList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testZeroArgsConstructor(){
    ASTCDConstructor constructor = scopesGenitorClass.getCDConstructorList().get(0);
    assertDeepEquals(PUBLIC, constructor.getModifier());
    assertEquals("AutomatonScopesGenitorDelegator", constructor.getName());
    assertTrue(constructor.isEmptyCDParameters());
    assertFalse(constructor.isPresentCDThrowsDeclaration());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testAttributeSize() {
    assertEquals(4, scopesGenitorClass.getCDAttributeList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testScopeStackAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("scopeStack", scopesGenitorClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals("Deque<de.monticore.codegen.symboltable.automaton._symboltable.IAutomatonScope>", astcdAttribute.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSymTabAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("symbolTable", scopesGenitorClass);
    assertDeepEquals(PROTECTED_FINAL, astcdAttribute.getModifier());
    assertDeepEquals(AUTOMATON_SCOPES_GENITOR, astcdAttribute.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGlobalScopeAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("globalScope", scopesGenitorClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals(AUTOMATON_GLOBAL_SCOPE, astcdAttribute.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testTraverserAttribute(){
    ASTCDAttribute astcdAttribute = getAttributeBy("traverser", scopesGenitorClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals(AUTOMATON_TRAVERSER, astcdAttribute.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMethods() {
    assertEquals(1, scopesGenitorClass.getCDMethodList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testCreateFromASTMethod() {
    ASTCDMethod method = getMethodBy("createFromAST", scopesGenitorClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals("de.monticore.codegen.symboltable.automaton._symboltable.IAutomatonArtifactScope", method.getMCReturnType().getMCType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(AST_AUTOMATON, method.getCDParameter(0).getMCType());
    assertEquals("rootNode", method.getCDParameter(0).getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGeneratedCodeState() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    CD4C.init(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CD2JavaTemplates.CLASS, scopesGenitorClass, packageDir);
    // test parsing
    ParserConfiguration configuration = new ParserConfiguration();
    JavaParser parser = new JavaParser(configuration);
    ParseResult parseResult = parser.parse(sb.toString());
    assertTrue(parseResult.isSuccessful());
  
    assertTrue(Log.getFindings().isEmpty());
  }
}
