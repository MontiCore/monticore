package de.monticore.codegen.cd2java._symboltable.symboltablecreator;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.facade.CDModifier;
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
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getAttributeBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodBy;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class PhasedSymbolTableCreatorDelegatorDecoratorTest extends DecoratorTestCase {

  private ASTCDClass phasedSTCClass;

  private GlobalExtensionManagement glex;

  private ASTCDCompilationUnit decoratedCompilationUnit;

  private ASTCDCompilationUnit originalCompilationUnit;

  private MCTypeFacade mcTypeFacade;

  private PhasedSymbolTableCreatorDelegatorDecorator decorator;

  private static final String AUTOMATON_GLOBAL_SCOPE = "IAutomatonGlobalScope";

  private static final String AUTOMATON_ARTIFACT_SCOPE = "IAutomatonArtifactScope";

  private static final String AUTOMATON_SCOPE_SKELETON_CREATOR_DELEGATOR = "AutomatonScopeSkeletonCreatorDelegator";

  private static final String AUTOMATON_VISITOR = "de.monticore.codegen.symboltable.automaton._visitor.AutomatonVisitor";

  private static final String AST_AUTOMATON = "de.monticore.codegen.symboltable.automaton._ast.ASTAutomaton";

  @Before
  public void setUp() {
    LogStub.init();         // replace log by a sideffect free variant
    // LogStub.initPlusLog();  // for manual testing purpose only
    this.glex = new GlobalExtensionManagement();
    this.mcTypeFacade = MCTypeFacade.getInstance();

    this.glex.setGlobalValue("astHelper", DecorationHelper.getInstance());
    this.glex.setGlobalValue("cdPrinter", new CD4CodePrinter());
    decoratedCompilationUnit = this.parse("de", "monticore", "codegen", "symboltable", "Automaton");
    originalCompilationUnit = decoratedCompilationUnit.deepClone();
    this.glex.setGlobalValue("service", new AbstractService(decoratedCompilationUnit));

    this.decorator = new PhasedSymbolTableCreatorDelegatorDecorator(this.glex,
        new SymbolTableService(decoratedCompilationUnit), new VisitorService(decoratedCompilationUnit));

    //creates normal Symbol
    Optional<ASTCDClass> optPhasedSTCCreator = decorator.decorate(decoratedCompilationUnit);
    assertTrue(optPhasedSTCCreator.isPresent());
    this.phasedSTCClass = optPhasedSTCCreator.get();
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
    assertEquals("AutomatonPhasedSymbolTableCreatorDelegator", phasedSTCClass.getName());
  }

  @Test
  public void testNoSuperInterfaces() {
    assertTrue(phasedSTCClass.isEmptyInterface());
  }

  @Test
  public void testNoSuperClass() {
    assertFalse(phasedSTCClass.isPresentSuperclass());
  }

  @Test
  public void testConstructorCount() {
    assertEquals(1, phasedSTCClass.sizeCDConstructors());
  }

  @Test
  public void testConstructor(){
    ASTCDConstructor constructor = phasedSTCClass.getCDConstructor(0);
    assertDeepEquals(CDModifier.PUBLIC, phasedSTCClass.getModifier());
    assertEquals(phasedSTCClass.getName(), constructor.getName());
    assertEquals(1, constructor.sizeCDParameters());
    assertDeepEquals(AUTOMATON_GLOBAL_SCOPE, constructor.getCDParameter(0).getMCType());
    assertEquals("globalScope", constructor.getCDParameter(0).getName());
  }

  @Test
  public void testAttributeCount(){
    assertEquals(3, phasedSTCClass.sizeCDAttributes());
  }

  @Test
  public void testGlobalScopeAttribute(){
    ASTCDAttribute attribute = getAttributeBy("globalScope", phasedSTCClass);
    assertDeepEquals(CDModifier.PROTECTED, attribute.getModifier());
    assertDeepEquals(AUTOMATON_GLOBAL_SCOPE, attribute.getMCType());
  }

  @Test
  public void testScopeSkeletonCreatorAttribute(){
    ASTCDAttribute attribute = getAttributeBy("scopeSkeletonCreator", phasedSTCClass);
    assertDeepEquals(CDModifier.PROTECTED, attribute.getModifier());
    assertDeepEquals(AUTOMATON_SCOPE_SKELETON_CREATOR_DELEGATOR, attribute.getMCType());
  }

  @Test
  public void testPriorityListAttribute(){
    ASTCDAttribute attribute = getAttributeBy("priorityList", phasedSTCClass);
    assertDeepEquals(CDModifier.PROTECTED, attribute.getModifier());
    ASTMCType type = mcTypeFacade.createListTypeOf(AUTOMATON_VISITOR);
    assertDeepEquals(type, attribute.getMCType());
  }

  @Test
  public void testMethodSize(){
    assertEquals(1, phasedSTCClass.sizeCDMethods());
  }

  @Test
  public void testCreateFromASTMethod(){
    ASTCDMethod method = getMethodBy("createFromAST", phasedSTCClass);
    assertDeepEquals(CDModifier.PUBLIC,method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals(AUTOMATON_ARTIFACT_SCOPE, method.getMCReturnType().getMCType());
    assertEquals(1, method.sizeCDParameters());
    assertEquals("rootNode", method.getCDParameter(0).getName());
    assertDeepEquals(AST_AUTOMATON, method.getCDParameter(0).getMCType());
  }

  @Test
  public void testGeneratedCodeState() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.CLASS, phasedSTCClass, phasedSTCClass);
    // test parsing
    ParserConfiguration configuration = new ParserConfiguration();
    JavaParser parser = new JavaParser(configuration);
    ParseResult parseResult = parser.parse(sb.toString());
    assertTrue(parseResult.isSuccessful());
  }

}
