/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.mill;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.google.common.collect.Lists;
import de.monticore.cdbasis._ast.*;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.cd4codebasis._ast.*;
import de.monticore.codegen.cd2java.CdUtilsPrinter;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecorationHelper;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._ast.ASTCDDecorator;
import de.monticore.codegen.cd2java._ast.ast_class.*;
import de.monticore.codegen.cd2java._ast.ast_class.reference.ASTReferenceDecorator;
import de.monticore.codegen.cd2java._ast.ast_interface.ASTInterfaceDecorator;
import de.monticore.codegen.cd2java._ast.ast_interface.ASTLanguageInterfaceDecorator;
import de.monticore.codegen.cd2java._ast.ast_interface.FullASTInterfaceDecorator;
import de.monticore.codegen.cd2java._ast.builder.ASTBuilderDecorator;
import de.monticore.codegen.cd2java._ast.builder.BuilderDecorator;
import de.monticore.codegen.cd2java._ast.constants.ASTConstantsDecorator;
import de.monticore.codegen.cd2java._ast.enums.EnumDecorator;
import de.monticore.codegen.cd2java._parser.ParserService;
import de.monticore.codegen.cd2java._symboltable.SymbolTableCDDecorator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._symboltable.scope.*;
import de.monticore.codegen.cd2java._symboltable.scopesgenitor.ScopesGenitorDecorator;
import de.monticore.codegen.cd2java._symboltable.scopesgenitor.ScopesGenitorDelegatorDecorator;
import de.monticore.codegen.cd2java._symboltable.serialization.ScopeDeSerDecorator;
import de.monticore.codegen.cd2java._symboltable.serialization.SymbolDeSerDecorator;
import de.monticore.codegen.cd2java._symboltable.serialization.Symbols2JsonDecorator;
import de.monticore.codegen.cd2java._symboltable.symbol.*;
import de.monticore.codegen.cd2java._symboltable.symbol.symbolsurrogatemutator.MandatoryMutatorSymbolSurrogateDecorator;
import de.monticore.codegen.cd2java._visitor.*;
import de.monticore.codegen.cd2java.data.DataDecorator;
import de.monticore.codegen.cd2java.data.DataDecoratorUtil;
import de.monticore.codegen.cd2java.data.InterfaceDecorator;
import de.monticore.codegen.cd2java.methods.AccessorDecorator;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.io.paths.IterablePath;
import de.monticore.umlmodifier._ast.ASTModifier;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static de.monticore.codegen.cd2java.CDModifier.*;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getAttributeBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MillDecoratorTest extends DecoratorTestCase {

  private ASTCDClass millClass;

  private GlobalExtensionManagement glex;

  private ASTCDCompilationUnit originalCompilationUnit;

  private ASTCDCompilationUnit decoratedCompilationUnit;

  private ASTCDCompilationUnit decoratedSymbolCompilationUnit;

  private ASTCDCompilationUnit decoratedScopeCompilationUnit;

  @Before
  public void setUp() {
    LogStub.init();
    LogStub.enableFailQuick(false);
    this.glex = new GlobalExtensionManagement();

    this.glex.setGlobalValue("astHelper", DecorationHelper.getInstance());
    this.glex.setGlobalValue("cdPrinter", new CdUtilsPrinter());
    decoratedCompilationUnit = this.parse("de", "monticore", "codegen", "symboltable", "Automaton");
    decoratedScopeCompilationUnit = this.parse("de", "monticore", "codegen", "symboltable", "AutomatonScopeCD");
    decoratedSymbolCompilationUnit = this.parse("de", "monticore", "codegen", "symboltable", "AutomatonSymbolCD");
    originalCompilationUnit = decoratedCompilationUnit.deepClone();
    this.glex.setGlobalValue("service", new VisitorService(decoratedCompilationUnit));

    SymbolTableService symbolTableService = new SymbolTableService(decoratedCompilationUnit);
    VisitorService visitorService = new VisitorService(decoratedCompilationUnit);
    ParserService parserService = new ParserService(decoratedCompilationUnit);
    MillDecorator decorator = new MillDecorator(this.glex, symbolTableService, visitorService, parserService);
    this.millClass = decorator.decorate(Lists.newArrayList(getASTCD(), getTraverserCD(), getSymbolCD()));
  }

  protected ASTCDCompilationUnit getASTCD() {
    ASTService astService = new ASTService(decoratedCompilationUnit);
    SymbolTableService symbolTableService = new SymbolTableService(decoratedCompilationUnit);
    VisitorService visitorService = new VisitorService(decoratedCompilationUnit);
    MethodDecorator methodDecorator = new MethodDecorator(glex, astService);
    DataDecorator dataDecorator = new DataDecorator(glex, methodDecorator, astService, new DataDecoratorUtil());
    ASTSymbolDecorator astSymbolDecorator = new ASTSymbolDecorator(glex, symbolTableService);
    ASTScopeDecorator astScopeDecorator = new ASTScopeDecorator(glex, symbolTableService);
    ASTDecorator astDecorator = new ASTDecorator(glex, astService, visitorService,
        astSymbolDecorator, astScopeDecorator, methodDecorator, symbolTableService);
    ASTReferenceDecorator<ASTCDClass> astClassReferencedSymbolDecorator = new ASTReferenceDecorator<ASTCDClass>(glex, symbolTableService);
    ASTReferenceDecorator<ASTCDInterface> astInterfaceReferencedSymbolDecorator = new ASTReferenceDecorator<ASTCDInterface>(glex, symbolTableService);
    ASTFullDecorator fullDecorator = new ASTFullDecorator(dataDecorator, astDecorator, astClassReferencedSymbolDecorator);
    ASTLanguageInterfaceDecorator astLanguageInterfaceDecorator = new ASTLanguageInterfaceDecorator(astService, visitorService);
    BuilderDecorator builderDecorator = new BuilderDecorator(glex, new AccessorDecorator(glex, astService), new ASTService(decoratedCompilationUnit));
    ASTBuilderDecorator astBuilderDecorator = new ASTBuilderDecorator(glex, builderDecorator, astService);
    ASTConstantsDecorator astConstantsDecorator = new ASTConstantsDecorator(glex, astService);
    EnumDecorator enumDecorator = new EnumDecorator(glex, new AccessorDecorator(glex, astService), astService);
    ASTInterfaceDecorator astInterfaceDecorator = new ASTInterfaceDecorator(glex, astService, visitorService,
        astSymbolDecorator, astScopeDecorator, methodDecorator);
    InterfaceDecorator dataInterfaceDecorator = new InterfaceDecorator(glex, new DataDecoratorUtil(), methodDecorator, astService);
    FullASTInterfaceDecorator fullASTInterfaceDecorator = new FullASTInterfaceDecorator(dataInterfaceDecorator, astInterfaceDecorator, astInterfaceReferencedSymbolDecorator);
    ASTCDDecorator astcdDecorator = new ASTCDDecorator(glex, fullDecorator, astLanguageInterfaceDecorator, astBuilderDecorator,
        astConstantsDecorator, enumDecorator, fullASTInterfaceDecorator);
    return astcdDecorator.decorate(decoratedCompilationUnit);
  }

  protected ASTCDCompilationUnit getTraverserCD() {
    IterablePath targetPath = Mockito.mock(IterablePath.class);
    VisitorService visitorService = new VisitorService(decoratedCompilationUnit);
    SymbolTableService symbolTableService = new SymbolTableService(decoratedCompilationUnit);
    MethodDecorator methodDecorator = new MethodDecorator(glex, visitorService);

    TraverserInterfaceDecorator traverserInterfaceDecorator = new TraverserInterfaceDecorator(glex, visitorService, symbolTableService);
    TraverserClassDecorator traverserClassDecorator = new TraverserClassDecorator(glex, visitorService, symbolTableService);
    Visitor2Decorator visitor2Decorator = new Visitor2Decorator(glex, visitorService, symbolTableService);
    HandlerDecorator handlerDecorator = new HandlerDecorator(glex, visitorService, symbolTableService);
    InheritanceHandlerDecorator inheritanceHandlerDecorator = new InheritanceHandlerDecorator(glex, methodDecorator, visitorService, symbolTableService);

    CDTraverserDecorator decorator = new CDTraverserDecorator(this.glex, targetPath, visitorService, traverserInterfaceDecorator,
        traverserClassDecorator, visitor2Decorator, handlerDecorator, inheritanceHandlerDecorator);
    return decorator.decorate(decoratedCompilationUnit);
  }

  protected ASTCDCompilationUnit getSymbolCD() {
    SymbolTableService symbolTableService = new SymbolTableService(decoratedCompilationUnit);
    VisitorService visitorService = new VisitorService(decoratedCompilationUnit);
    MethodDecorator methodDecorator = new MethodDecorator(glex, symbolTableService);
    AccessorDecorator accessorDecorator = new AccessorDecorator(glex, symbolTableService);

    SymbolDecorator symbolDecorator = new SymbolDecorator(glex, symbolTableService, visitorService, methodDecorator);
    BuilderDecorator builderDecorator = new BuilderDecorator(glex, accessorDecorator, symbolTableService);
    SymbolBuilderDecorator symbolBuilderDecorator = new SymbolBuilderDecorator(glex, symbolTableService, builderDecorator);
    ScopeInterfaceDecorator scopeInterfaceDecorator = new ScopeInterfaceDecorator(glex, symbolTableService, visitorService, methodDecorator);
    ScopeClassDecorator scopeClassDecorator = new ScopeClassDecorator(glex, symbolTableService, visitorService, methodDecorator);
    GlobalScopeInterfaceDecorator globalScopeInterfaceDecorator = new GlobalScopeInterfaceDecorator(glex, symbolTableService, methodDecorator);
    GlobalScopeClassDecorator globalScopeClassDecorator = new GlobalScopeClassDecorator(glex, symbolTableService, methodDecorator);
    ArtifactScopeInterfaceDecorator artifactScopeInterfaceDecorator = new ArtifactScopeInterfaceDecorator(glex, symbolTableService, visitorService, methodDecorator);
    ArtifactScopeClassDecorator artifactScopeDecorator = new ArtifactScopeClassDecorator(glex, symbolTableService, visitorService, methodDecorator);
    SymbolSurrogateDecorator symbolReferenceDecorator = new SymbolSurrogateDecorator(glex, symbolTableService, methodDecorator, new MandatoryMutatorSymbolSurrogateDecorator(glex));
    SymbolSurrogateBuilderDecorator symbolReferenceBuilderDecorator = new SymbolSurrogateBuilderDecorator(glex, symbolTableService, accessorDecorator);
    CommonSymbolInterfaceDecorator commonSymbolInterfaceDecorator = new CommonSymbolInterfaceDecorator(glex, symbolTableService, visitorService, methodDecorator);
    SymbolResolverInterfaceDecorator symbolResolverInterfaceDecorator = new SymbolResolverInterfaceDecorator(glex, symbolTableService);
    SymbolDeSerDecorator symbolDeSerDecorator = new SymbolDeSerDecorator(glex, symbolTableService, IterablePath.empty());
    ScopeDeSerDecorator scopeDeSerDecorator = new ScopeDeSerDecorator(glex, symbolTableService, methodDecorator, visitorService, IterablePath.empty());
    ScopesGenitorDecorator scopesGenitorDecorator = new ScopesGenitorDecorator(glex, symbolTableService, visitorService, methodDecorator);
    ScopesGenitorDelegatorDecorator scopesGenitorDelegatorDecorator = new ScopesGenitorDelegatorDecorator(glex, symbolTableService, visitorService);
    Symbols2JsonDecorator symbolTablePrinterDecorator = new Symbols2JsonDecorator(glex, symbolTableService, visitorService, methodDecorator);

    IterablePath targetPath = Mockito.mock(IterablePath.class);

    SymbolTableCDDecorator symbolTableCDDecorator = new SymbolTableCDDecorator(glex, targetPath, symbolTableService, symbolDecorator,
        symbolBuilderDecorator, symbolReferenceDecorator, symbolReferenceBuilderDecorator,
        scopeInterfaceDecorator, scopeClassDecorator,
        globalScopeInterfaceDecorator, globalScopeClassDecorator,
        artifactScopeInterfaceDecorator, artifactScopeDecorator,
        commonSymbolInterfaceDecorator,
        symbolResolverInterfaceDecorator,
        symbolDeSerDecorator, scopeDeSerDecorator, symbolTablePrinterDecorator, scopesGenitorDecorator, scopesGenitorDelegatorDecorator);

    // cd with no handcoded classes
    return symbolTableCDDecorator.decorate(decoratedCompilationUnit, decoratedSymbolCompilationUnit, decoratedScopeCompilationUnit);
  }


  @Test
  public void testCompilationUnitNotChanged() {
    // TODO NJ: Remove the following loc as soon as stereotype deep equals is fixed
    String cachedValue = ((ASTCDClass) originalCompilationUnit.getCDDefinition().getCDElement(6)).getModifier().getStereotype().getValues(0).getValue();
    assertEquals("de.monticore.codegen.symboltable.automaton._symboltable.SymbolInterfaceSymbol", cachedValue);
    
    assertDeepEquals(originalCompilationUnit, decoratedCompilationUnit);
  }

  @Test
  public void testMillName() {
    assertEquals("AutomatonMill", millClass.getName());
  }

  @Test
  public void testAttributeSize() {
    assertEquals(23, millClass.getCDAttributeList().size());
  }

  @Test
  public void testAttributeName() {
    // ast
    getAttributeBy("mill", millClass);
    getAttributeBy("millASTAutomatonBuilder", millClass);
    getAttributeBy("millASTStateBuilder", millClass);
    getAttributeBy("millASTTransitionBuilder", millClass);
    getAttributeBy("millASTScopeBuilder", millClass);
    getAttributeBy("millASTInheritedSymbolClassBuilder", millClass);
    //symboltable
    getAttributeBy("millAutomatonSymbolBuilder", millClass);
    getAttributeBy("millStateSymbolBuilder", millClass);
    getAttributeBy("millFooSymbolBuilder", millClass);
    getAttributeBy("millAutomatonScope", millClass);
    getAttributeBy("millAutomatonSymbolSurrogateBuilder", millClass);
    getAttributeBy("millStateSymbolSurrogateBuilder", millClass);
    getAttributeBy("millFooSymbolSurrogateBuilder", millClass);
    getAttributeBy("millAutomatonGlobalScope", millClass);
    getAttributeBy("millAutomatonArtifactScope", millClass);

    getAttributeBy("automatonGlobalScope", millClass);

    getAttributeBy("millAutomatonTraverserImplementation", millClass);
    getAttributeBy("millAutomatonInheritanceHandler", millClass);

    getAttributeBy("millAutomatonScopesGenitor", millClass);
    getAttributeBy("millAutomatonScopesGenitorDelegator", millClass);
  }

  @Test
  public void testAttributeModifier() {
    for (ASTCDAttribute astcdAttribute : millClass.getCDAttributeList()) {
      if(!astcdAttribute.getName().equals("automatonGlobalScope")) {
        assertTrue(astcdAttribute.isPresentModifier());
        assertTrue(PROTECTED_STATIC.build().deepEquals(astcdAttribute.getModifier()));
      }
    }
    assertDeepEquals(PROTECTED, getAttributeBy("automatonGlobalScope", millClass).getModifier());
  }

  @Test
  public void testConstructor() {
    assertEquals(1, millClass.getCDConstructorList().size());
    assertTrue(PROTECTED.build().deepEquals(millClass.getCDConstructorList().get(0).getModifier()));
    assertEquals("AutomatonMill", millClass.getCDConstructorList().get(0).getName());
  }

  @Test
  public void testGetMillMethod() {
    ASTCDMethod getMill = getMethodBy("getMill", millClass);
    //test Method Name
    assertEquals("getMill", getMill.getName());
    //test Parameters
    assertTrue(getMill.isEmptyCDParameters());
    //test ReturnType
    assertTrue(getMill.getMCReturnType().isPresentMCType());
    assertDeepEquals("AutomatonMill", getMill.getMCReturnType().getMCType());
    //test Modifier
    assertTrue(PROTECTED_STATIC.build().deepEquals(getMill.getModifier()));
  }

  @Test
  public void testInitMeMethod() {
    ASTCDMethod initMe = getMethodBy("initMe", millClass);
    //test Method Name
    assertEquals("initMe", initMe.getName());
    //test Parameters
    assertEquals(1, initMe.sizeCDParameters());
    assertDeepEquals("AutomatonMill", initMe.getCDParameter(0).getMCType());
    assertEquals("a", initMe.getCDParameter(0).getName());
    //test ReturnType
    assertTrue(initMe.getMCReturnType().isPresentMCVoidType());
    //test Modifier
    assertTrue(PUBLIC_STATIC.build().deepEquals(initMe.getModifier()));
  }

  @Test
  public void testInitMethod() {
    ASTCDMethod init = getMethodBy("init", millClass);
    //test Method Name
    assertEquals("init", init.getName());
    //test Parameters
    assertTrue(init.isEmptyCDParameters());
    //test ReturnType
    assertTrue(init.getMCReturnType().isPresentMCVoidType());
    //test Modifier
    assertTrue(PUBLIC_STATIC.build().deepEquals(init.getModifier()));
  }

  @Test
  public void testResetMethod() {
    ASTCDMethod reset = getMethodBy("reset", millClass);
    //test Method Name
    assertEquals("reset", reset.getName());
    //test Parameters
    assertTrue(reset.isEmptyCDParameters());
    //test ReturnType
    assertTrue(reset.getMCReturnType().isPresentMCVoidType());
    //test Modifier
    assertTrue(PUBLIC_STATIC.build().deepEquals(reset.getModifier()));
  }

  @Test
  public void testAutomatonBuilderMethod() {
    ASTCDMethod fooBarBuilder = getMethodBy("automatonBuilder", millClass);
    //test Method Name
    assertEquals("automatonBuilder", fooBarBuilder.getName());
    //test Parameters
    assertTrue(fooBarBuilder.isEmptyCDParameters());
    //test ReturnType
    assertTrue(fooBarBuilder.getMCReturnType().isPresentMCType());
    assertDeepEquals("de.monticore.codegen.symboltable.automaton._ast.ASTAutomatonBuilder", fooBarBuilder.getMCReturnType().getMCType());
    //test Modifier
    assertTrue(PUBLIC_STATIC.build().deepEquals(fooBarBuilder.getModifier()));
  }

  @Test
  public void testProtectedAutomatonMethod() {
    ASTCDMethod fooBarBuilder = getMethodBy("_automatonBuilder", millClass);
    //test Method Name
    assertEquals("_automatonBuilder", fooBarBuilder.getName());
    //test Parameters
    assertTrue(fooBarBuilder.isEmptyCDParameters());
    //test ReturnType
    assertTrue(fooBarBuilder.getMCReturnType().isPresentMCType());
    assertDeepEquals("de.monticore.codegen.symboltable.automaton._ast.ASTAutomatonBuilder", fooBarBuilder.getMCReturnType().getMCType());
    //test Modifier
    assertTrue(PROTECTED.build().deepEquals(fooBarBuilder.getModifier()));
  }


  @Test
  public void testStateMethod() {
    ASTCDMethod fooBarBuilder = getMethodBy("stateBuilder", millClass);
    //test Method Name
    assertEquals("stateBuilder", fooBarBuilder.getName());
    //test Parameters
    assertTrue(fooBarBuilder.isEmptyCDParameters());
    //test ReturnType
    assertTrue(fooBarBuilder.getMCReturnType().isPresentMCType());
    assertDeepEquals("de.monticore.codegen.symboltable.automaton._ast.ASTStateBuilder", fooBarBuilder.getMCReturnType().getMCType());
    //test Modifier
    assertTrue(PUBLIC_STATIC.build().deepEquals(fooBarBuilder.getModifier()));
  }

  @Test
  public void testProtectedStateBuilderMethod() {
    ASTCDMethod fooBarBuilder = getMethodBy("_stateBuilder", millClass);
    //test Method Name
    assertEquals("_stateBuilder", fooBarBuilder.getName());
    //test Parameters
    assertTrue(fooBarBuilder.isEmptyCDParameters());
    //test ReturnType
    assertTrue(fooBarBuilder.getMCReturnType().isPresentMCType());
    assertDeepEquals("de.monticore.codegen.symboltable.automaton._ast.ASTStateBuilder", fooBarBuilder.getMCReturnType().getMCType());
    //test Modifier
    assertTrue(PROTECTED.build().deepEquals(fooBarBuilder.getModifier()));
  }


  @Test
  public void testTransitionMethod() {
    ASTCDMethod fooBarBuilder = getMethodBy("transitionBuilder", millClass);
    //test Method Name
    assertEquals("transitionBuilder", fooBarBuilder.getName());
    //test Parameters
    assertTrue(fooBarBuilder.isEmptyCDParameters());
    //test ReturnType
    assertTrue(fooBarBuilder.getMCReturnType().isPresentMCType());
    assertDeepEquals("de.monticore.codegen.symboltable.automaton._ast.ASTTransitionBuilder", fooBarBuilder.getMCReturnType().getMCType());
    //test Modifier
    assertTrue(PUBLIC_STATIC.build().deepEquals(fooBarBuilder.getModifier()));
  }

  @Test
  public void testProtectedTransitionBuilderMethod() {
    ASTCDMethod fooBarBuilder = getMethodBy("_transitionBuilder", millClass);
    //test Method Name
    assertEquals("_transitionBuilder", fooBarBuilder.getName());
    //test Parameters
    assertTrue(fooBarBuilder.isEmptyCDParameters());
    //test ReturnType
    assertTrue(fooBarBuilder.getMCReturnType().isPresentMCType());
    assertDeepEquals("de.monticore.codegen.symboltable.automaton._ast.ASTTransitionBuilder", fooBarBuilder.getMCReturnType().getMCType());
    //test Modifier
    assertTrue(PROTECTED.build().deepEquals(fooBarBuilder.getModifier()));
  }

  @Test
  public void testAutomatonSymbolMethod() {
    ASTCDMethod fooBarBuilder = getMethodBy("automatonSymbolBuilder", millClass);
    //test Method Name
    assertEquals("automatonSymbolBuilder", fooBarBuilder.getName());
    //test Parameters
    assertTrue(fooBarBuilder.isEmptyCDParameters());
    //test ReturnType
    assertTrue(fooBarBuilder.getMCReturnType().isPresentMCType());
    assertDeepEquals("de.monticore.codegen.symboltable.automaton._symboltable.AutomatonSymbolBuilder",
        fooBarBuilder.getMCReturnType().getMCType());
    //test Modifier
    assertTrue(PUBLIC_STATIC.build().deepEquals(fooBarBuilder.getModifier()));
  }

  @Test
  public void testAutomatonSymbolBuilderMethod() {
    ASTCDMethod fooBarBuilder = getMethodBy("_automatonSymbolBuilder", millClass);
    //test Method Name
    assertEquals("_automatonSymbolBuilder", fooBarBuilder.getName());
    //test Parameters
    assertTrue(fooBarBuilder.isEmptyCDParameters());
    //test ReturnType
    assertTrue(fooBarBuilder.getMCReturnType().isPresentMCType());
    assertDeepEquals("de.monticore.codegen.symboltable.automaton._symboltable.AutomatonSymbolBuilder",
        fooBarBuilder.getMCReturnType().getMCType());
    //test Modifier
    assertTrue(PROTECTED.build().deepEquals(fooBarBuilder.getModifier()));
  }


  @Test
  public void testStateSymbolMethod() {
    ASTCDMethod fooBarBuilder = getMethodBy("stateSymbolBuilder", millClass);
    //test Method Name
    assertEquals("stateSymbolBuilder", fooBarBuilder.getName());
    //test Parameters
    assertTrue(fooBarBuilder.isEmptyCDParameters());
    //test ReturnType
    assertTrue(fooBarBuilder.getMCReturnType().isPresentMCType());
    assertDeepEquals("de.monticore.codegen.symboltable.automaton._symboltable.StateSymbolBuilder",
        fooBarBuilder.getMCReturnType().getMCType());
    //test Modifier
    assertTrue(PUBLIC_STATIC.build().deepEquals(fooBarBuilder.getModifier()));
  }

  @Test
  public void testStateSymbolBuilderMethod() {
    ASTCDMethod fooBarBuilder = getMethodBy("_stateSymbolBuilder", millClass);
    //test Method Name
    assertEquals("_stateSymbolBuilder", fooBarBuilder.getName());
    //test Parameters
    assertTrue(fooBarBuilder.isEmptyCDParameters());
    //test ReturnType
    assertTrue(fooBarBuilder.getMCReturnType().isPresentMCType());
    assertDeepEquals("de.monticore.codegen.symboltable.automaton._symboltable.StateSymbolBuilder",
        fooBarBuilder.getMCReturnType().getMCType());
    //test Modifier
    assertTrue(PROTECTED.build().deepEquals(fooBarBuilder.getModifier()));
  }

  @Test
  public void testAutomatonSymbolSurrogateMethod() {
    ASTCDMethod fooBarBuilder = getMethodBy("automatonSymbolSurrogateBuilder", millClass);
    //test Method Name
    assertEquals("automatonSymbolSurrogateBuilder", fooBarBuilder.getName());
    //test Parameters
    assertTrue(fooBarBuilder.isEmptyCDParameters());
    //test ReturnType
    assertTrue(fooBarBuilder.getMCReturnType().isPresentMCType());
    assertDeepEquals("de.monticore.codegen.symboltable.automaton._symboltable.AutomatonSymbolSurrogateBuilder",
        fooBarBuilder.getMCReturnType().getMCType());
    //test Modifier
    assertTrue(PUBLIC_STATIC.build().deepEquals(fooBarBuilder.getModifier()));
  }

  @Test
  public void testAutomatonSymbolSurrogateBuilderMethod() {
    ASTCDMethod fooBarBuilder = getMethodBy("_automatonSymbolSurrogateBuilder", millClass);
    //test Method Name
    assertEquals("_automatonSymbolSurrogateBuilder", fooBarBuilder.getName());
    //test Parameters
    assertTrue(fooBarBuilder.isEmptyCDParameters());
    //test ReturnType
    assertTrue(fooBarBuilder.getMCReturnType().isPresentMCType());
    assertDeepEquals("de.monticore.codegen.symboltable.automaton._symboltable.AutomatonSymbolSurrogateBuilder",
        fooBarBuilder.getMCReturnType().getMCType());
    //test Modifier
    assertTrue(PROTECTED.build().deepEquals(fooBarBuilder.getModifier()));
  }


  @Test
  public void testStateSymbolSurrogateMethod() {
    ASTCDMethod fooBarBuilder = getMethodBy("stateSymbolSurrogateBuilder", millClass);
    //test Method Name
    assertEquals("stateSymbolSurrogateBuilder", fooBarBuilder.getName());
    //test Parameters
    assertTrue(fooBarBuilder.isEmptyCDParameters());
    //test ReturnType
    assertTrue(fooBarBuilder.getMCReturnType().isPresentMCType());
    assertDeepEquals("de.monticore.codegen.symboltable.automaton._symboltable.StateSymbolSurrogateBuilder",
        fooBarBuilder.getMCReturnType().getMCType());
    //test Modifier
    assertTrue(PUBLIC_STATIC.build().deepEquals(fooBarBuilder.getModifier()));
  }

  @Test
  public void testStateSymbolSurrogateBuilderMethod() {
    ASTCDMethod fooBarBuilder = getMethodBy("_stateSymbolSurrogateBuilder", millClass);
    //test Method Name
    assertEquals("_stateSymbolSurrogateBuilder", fooBarBuilder.getName());
    //test Parameters
    assertTrue(fooBarBuilder.isEmptyCDParameters());
    //test ReturnType
    assertTrue(fooBarBuilder.getMCReturnType().isPresentMCType());
    assertDeepEquals("de.monticore.codegen.symboltable.automaton._symboltable.StateSymbolSurrogateBuilder",
        fooBarBuilder.getMCReturnType().getMCType());
    //test Modifier
    assertTrue(PROTECTED.build().deepEquals(fooBarBuilder.getModifier()));
  }


  @Test
  public void testFooSymbolMethod() {
    ASTCDMethod fooBarBuilder = getMethodBy("fooSymbolBuilder", millClass);
    //test Method Name
    assertEquals("fooSymbolBuilder", fooBarBuilder.getName());
    //test Parameters
    assertTrue(fooBarBuilder.isEmptyCDParameters());
    //test ReturnType
    assertTrue(fooBarBuilder.getMCReturnType().isPresentMCType());
    assertDeepEquals("de.monticore.codegen.symboltable.automaton._symboltable.FooSymbolBuilder",
        fooBarBuilder.getMCReturnType().getMCType());
    //test Modifier
    assertTrue(PUBLIC_STATIC.build().deepEquals(fooBarBuilder.getModifier()));
  }

  @Test
  public void testFooSymbolBuilderMethod() {
    ASTCDMethod fooBarBuilder = getMethodBy("_fooSymbolBuilder", millClass);
    //test Method Name
    assertEquals("_fooSymbolBuilder", fooBarBuilder.getName());
    //test Parameters
    assertTrue(fooBarBuilder.isEmptyCDParameters());
    //test ReturnType
    assertTrue(fooBarBuilder.getMCReturnType().isPresentMCType());
    assertDeepEquals("de.monticore.codegen.symboltable.automaton._symboltable.FooSymbolBuilder",
        fooBarBuilder.getMCReturnType().getMCType());
    //test Modifier
    assertTrue(PROTECTED.build().deepEquals(fooBarBuilder.getModifier()));
  }

  @Test
  public void testFooSymbolSurrogateMethod() {
    ASTCDMethod fooBarBuilder = getMethodBy("fooSymbolSurrogateBuilder", millClass);
    //test Method Name
    assertEquals("fooSymbolSurrogateBuilder", fooBarBuilder.getName());
    //test Parameters
    assertTrue(fooBarBuilder.isEmptyCDParameters());
    //test ReturnType
    assertTrue(fooBarBuilder.getMCReturnType().isPresentMCType());
    assertDeepEquals("de.monticore.codegen.symboltable.automaton._symboltable.FooSymbolSurrogateBuilder",
        fooBarBuilder.getMCReturnType().getMCType());
    //test Modifier
    assertTrue(PUBLIC_STATIC.build().deepEquals(fooBarBuilder.getModifier()));
  }

  @Test
  public void testFooSymbolSurrogateBuilderMethod() {
    ASTCDMethod fooBarBuilder = getMethodBy("_fooSymbolBuilder", millClass);
    //test Method Name
    assertEquals("_fooSymbolBuilder", fooBarBuilder.getName());
    //test Parameters
    assertTrue(fooBarBuilder.isEmptyCDParameters());
    //test ReturnType
    assertTrue(fooBarBuilder.getMCReturnType().isPresentMCType());
    assertDeepEquals("de.monticore.codegen.symboltable.automaton._symboltable.FooSymbolBuilder",
        fooBarBuilder.getMCReturnType().getMCType());
    //test Modifier
    assertTrue(PROTECTED.build().deepEquals(fooBarBuilder.getModifier()));
  }

  @Test
  public void testAutomataScopeMethod() {
    ASTCDMethod fooBarBuilder = getMethodBy("scope", millClass);
    //test Method Name
    assertEquals("scope", fooBarBuilder.getName());
    //test Parameters
    assertTrue(fooBarBuilder.isEmptyCDParameters());
    //test ReturnType
    assertTrue(fooBarBuilder.getMCReturnType().isPresentMCType());
    assertDeepEquals("de.monticore.codegen.symboltable.automaton._symboltable.IAutomatonScope",
        fooBarBuilder.getMCReturnType().getMCType());
    //test Modifier
    assertTrue(PUBLIC_STATIC.build().deepEquals(fooBarBuilder.getModifier()));
  }

  @Test
  public void testAutomatonScopeBuilderMethod() {
    ASTCDMethod fooBarBuilder = getMethodBy("_scope", millClass);
    //test Method Name
    assertEquals("_scope", fooBarBuilder.getName());
    //test Parameters
    assertTrue(fooBarBuilder.isEmptyCDParameters());
    //test ReturnType
    assertTrue(fooBarBuilder.getMCReturnType().isPresentMCType());
    assertDeepEquals("de.monticore.codegen.symboltable.automaton._symboltable.IAutomatonScope",
        fooBarBuilder.getMCReturnType().getMCType());
    //test Modifier
    assertTrue(PROTECTED.build().deepEquals(fooBarBuilder.getModifier()));
  }


  @Test
  public void testAutomatonGlobalScopeMethod() {
    ASTCDMethod fooBarBuilder = getMethodBy("globalScope", millClass);
    //test Method Name
    assertEquals("globalScope", fooBarBuilder.getName());
    //test Parameters
    assertTrue(fooBarBuilder.isEmptyCDParameters());
    //test ReturnType
    assertTrue(fooBarBuilder.getMCReturnType().isPresentMCType());
    assertDeepEquals("de.monticore.codegen.symboltable.automaton._symboltable.IAutomatonGlobalScope",
        fooBarBuilder.getMCReturnType().getMCType());
    //test Modifier
    ASTModifier modifier = PUBLIC_STATIC.build();
    assertTrue(modifier.deepEquals(fooBarBuilder.getModifier()));
  }

  @Test
  public void testAutomatonGlobalScopeBuilderMethod() {
    ASTCDMethod fooBarBuilder = getMethodBy("_globalScope", millClass);
    //test Method Name
    assertEquals("_globalScope", fooBarBuilder.getName());
    //test Parameters
    assertTrue(fooBarBuilder.isEmptyCDParameters());
    //test ReturnType
    assertTrue(fooBarBuilder.getMCReturnType().isPresentMCType());
    assertDeepEquals("de.monticore.codegen.symboltable.automaton._symboltable.IAutomatonGlobalScope",
        fooBarBuilder.getMCReturnType().getMCType());
    //test Modifier
    ASTModifier modifier = PROTECTED.build();
    assertTrue(modifier.deepEquals(fooBarBuilder.getModifier()));
  }


  @Test
  public void testAutomatonArtifactScopeMethod() {
    ASTCDMethod fooBarBuilder = getMethodBy("artifactScope", millClass);
    //test Method Name
    assertEquals("artifactScope", fooBarBuilder.getName());
    //test Parameters
    assertTrue(fooBarBuilder.isEmptyCDParameters());
    //test ReturnType
    assertTrue(fooBarBuilder.getMCReturnType().isPresentMCType());
    assertDeepEquals("de.monticore.codegen.symboltable.automaton._symboltable.IAutomatonArtifactScope",
        fooBarBuilder.getMCReturnType().getMCType());
    //test Modifier
    assertTrue(PUBLIC_STATIC.build().deepEquals(fooBarBuilder.getModifier()));
  }

  @Test
  public void testAutomatonArtifactScopeBuilderMethod() {
    ASTCDMethod fooBarBuilder = getMethodBy("_artifactScope", millClass);
    //test Method Name
    assertEquals("_artifactScope", fooBarBuilder.getName());
    //test Parameters
    assertTrue(fooBarBuilder.isEmptyCDParameters());
    //test ReturnType
    assertTrue(fooBarBuilder.getMCReturnType().isPresentMCType());
    assertDeepEquals("de.monticore.codegen.symboltable.automaton._symboltable.IAutomatonArtifactScope",
        fooBarBuilder.getMCReturnType().getMCType());
    //test Modifier
    assertTrue(PROTECTED.build().deepEquals(fooBarBuilder.getModifier()));
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.CLASS, millClass, millClass);
    // test parsing
    ParserConfiguration configuration = new ParserConfiguration();
    JavaParser parser = new JavaParser(configuration);
    ParseResult parseResult = parser.parse(sb.toString());
    assertTrue(parseResult.isSuccessful());
  }
}
