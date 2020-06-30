/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.mill;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.google.common.collect.Lists;
import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.prettyprint.CD4CodePrinter;
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
import de.monticore.codegen.cd2java._ast.factory.NodeFactoryDecorator;
import de.monticore.codegen.cd2java._ast.factory.NodeFactoryService;
import de.monticore.codegen.cd2java._parser.ParserService;
import de.monticore.codegen.cd2java._symboltable.SymbolTableCDDecorator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._symboltable.modelloader.ModelLoaderBuilderDecorator;
import de.monticore.codegen.cd2java._symboltable.modelloader.ModelLoaderDecorator;
import de.monticore.codegen.cd2java._symboltable.scope.*;
import de.monticore.codegen.cd2java._symboltable.serialization.ScopeDeSerDecorator;
import de.monticore.codegen.cd2java._symboltable.serialization.SymbolDeSerDecorator;
import de.monticore.codegen.cd2java._symboltable.serialization.SymbolTablePrinterDecorator;
import de.monticore.codegen.cd2java._symboltable.symbol.*;
import de.monticore.codegen.cd2java._symboltable.symbol.symbolloadermutator.MandatoryMutatorSymbolLoaderDecorator;
import de.monticore.codegen.cd2java._symboltable.symboltablecreator.*;
import de.monticore.codegen.cd2java._visitor.*;
import de.monticore.codegen.cd2java._visitor.builder.DelegatorVisitorBuilderDecorator;
import de.monticore.codegen.cd2java.data.DataDecorator;
import de.monticore.codegen.cd2java.data.DataDecoratorUtil;
import de.monticore.codegen.cd2java.data.InterfaceDecorator;
import de.monticore.codegen.cd2java.methods.AccessorDecorator;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.io.paths.IterablePath;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static de.monticore.cd.facade.CDModifier.*;
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
    this.glex.setGlobalValue("cdPrinter", new CD4CodePrinter());
    decoratedCompilationUnit = this.parse("de", "monticore", "codegen", "symboltable", "Automaton");
    decoratedScopeCompilationUnit = this.parse("de", "monticore", "codegen", "symboltable", "AutomatonScopeCD");
    decoratedSymbolCompilationUnit = this.parse("de", "monticore", "codegen", "symboltable", "AutomatonSymbolCD");
    originalCompilationUnit = decoratedCompilationUnit.deepClone();
    this.glex.setGlobalValue("service", new VisitorService(decoratedCompilationUnit));

    SymbolTableService symbolTableService = new SymbolTableService(decoratedCompilationUnit);

    MillDecorator decorator = new MillDecorator(this.glex, symbolTableService);
    this.millClass = decorator.decorate(Lists.newArrayList(getASTCD(), getVisitorCD(), getSymbolCD()));
  }

  protected ASTCDCompilationUnit getASTCD() {
    ASTService astService = new ASTService(decoratedCompilationUnit);
    SymbolTableService symbolTableService = new SymbolTableService(decoratedCompilationUnit);
    VisitorService visitorService = new VisitorService(decoratedCompilationUnit);
    NodeFactoryService nodeFactoryService = new NodeFactoryService(decoratedCompilationUnit);
    MethodDecorator methodDecorator = new MethodDecorator(glex, astService);
    DataDecorator dataDecorator = new DataDecorator(glex, methodDecorator, astService, new DataDecoratorUtil());
    ASTSymbolDecorator astSymbolDecorator = new ASTSymbolDecorator(glex, symbolTableService);
    ASTScopeDecorator astScopeDecorator = new ASTScopeDecorator(glex, symbolTableService);
    ASTDecorator astDecorator = new ASTDecorator(glex, astService, visitorService, nodeFactoryService,
        astSymbolDecorator, astScopeDecorator, methodDecorator, symbolTableService);
    ASTReferenceDecorator<ASTCDClass> astClassReferencedSymbolDecorator = new ASTReferenceDecorator<ASTCDClass>(glex, symbolTableService);
    ASTReferenceDecorator<ASTCDInterface> astInterfaceReferencedSymbolDecorator = new ASTReferenceDecorator<ASTCDInterface>(glex, symbolTableService);
    ASTFullDecorator fullDecorator = new ASTFullDecorator(dataDecorator, astDecorator, astClassReferencedSymbolDecorator);
    ASTLanguageInterfaceDecorator astLanguageInterfaceDecorator = new ASTLanguageInterfaceDecorator(astService, visitorService);
    BuilderDecorator builderDecorator = new BuilderDecorator(glex, new AccessorDecorator(glex, astService), new ASTService(decoratedCompilationUnit));
    ASTBuilderDecorator astBuilderDecorator = new ASTBuilderDecorator(glex, builderDecorator);
    NodeFactoryDecorator nodeFactoryDecorator = new NodeFactoryDecorator(glex, nodeFactoryService);
    ASTConstantsDecorator astConstantsDecorator = new ASTConstantsDecorator(glex, astService);
    EnumDecorator enumDecorator = new EnumDecorator(glex, new AccessorDecorator(glex, astService), astService);
    ASTInterfaceDecorator astInterfaceDecorator = new ASTInterfaceDecorator(glex, astService, visitorService,
        astSymbolDecorator, astScopeDecorator, methodDecorator);
    InterfaceDecorator dataInterfaceDecorator = new InterfaceDecorator(glex, new DataDecoratorUtil(), methodDecorator, astService);
    FullASTInterfaceDecorator fullASTInterfaceDecorator = new FullASTInterfaceDecorator(dataInterfaceDecorator, astInterfaceDecorator, astInterfaceReferencedSymbolDecorator);
    ASTCDDecorator astcdDecorator = new ASTCDDecorator(glex, fullDecorator, astLanguageInterfaceDecorator, astBuilderDecorator, nodeFactoryDecorator,
        astConstantsDecorator, enumDecorator, fullASTInterfaceDecorator);
    return astcdDecorator.decorate(decoratedCompilationUnit);
  }

  protected ASTCDCompilationUnit getVisitorCD() {
    IterablePath targetPath = Mockito.mock(IterablePath.class);
    VisitorService visitorService = new VisitorService(decoratedCompilationUnit);
    SymbolTableService symbolTableService = new SymbolTableService(decoratedCompilationUnit);

    VisitorDecorator astVisitorDecorator = new VisitorDecorator(this.glex, visitorService, symbolTableService);
    DelegatorVisitorDecorator delegatorVisitorDecorator = new DelegatorVisitorDecorator(this.glex, visitorService, symbolTableService);
    ParentAwareVisitorDecorator parentAwareVisitorDecorator = new ParentAwareVisitorDecorator(this.glex, visitorService);
    InheritanceVisitorDecorator inheritanceVisitorDecorator = new InheritanceVisitorDecorator(this.glex, visitorService, symbolTableService);
    DelegatorVisitorBuilderDecorator delegatorVisitorBuilderDecorator = new DelegatorVisitorBuilderDecorator(this.glex, visitorService, symbolTableService);


    CDVisitorDecorator decorator = new CDVisitorDecorator(this.glex, targetPath, visitorService,
        astVisitorDecorator, delegatorVisitorDecorator, inheritanceVisitorDecorator,
        parentAwareVisitorDecorator, delegatorVisitorBuilderDecorator);
    return decorator.decorate(decoratedCompilationUnit);
  }

  protected ASTCDCompilationUnit getSymbolCD() {
    SymbolTableService symbolTableService = new SymbolTableService(decoratedCompilationUnit);
    VisitorService visitorService = new VisitorService(decoratedCompilationUnit);
    ParserService parserService = new ParserService(decoratedCompilationUnit);
    MethodDecorator methodDecorator = new MethodDecorator(glex, symbolTableService);
    AccessorDecorator accessorDecorator = new AccessorDecorator(glex, symbolTableService);

    SymbolDecorator symbolDecorator = new SymbolDecorator(glex, symbolTableService, visitorService, methodDecorator);
    BuilderDecorator builderDecorator = new BuilderDecorator(glex, accessorDecorator, symbolTableService);
    SymbolBuilderDecorator symbolBuilderDecorator = new SymbolBuilderDecorator(glex, symbolTableService, builderDecorator);
    ScopeInterfaceDecorator scopeInterfaceDecorator = new ScopeInterfaceDecorator(glex, symbolTableService, visitorService, methodDecorator);
    ScopeClassDecorator scopeClassDecorator = new ScopeClassDecorator(glex, symbolTableService, visitorService, methodDecorator);
    ScopeClassBuilderDecorator scopeClassBuilderDecorator = new ScopeClassBuilderDecorator(glex, builderDecorator);
    GlobalScopeInterfaceDecorator globalScopeInterfaceDecorator = new GlobalScopeInterfaceDecorator(glex, symbolTableService, methodDecorator);
    GlobalScopeClassDecorator globalScopeClassDecorator = new GlobalScopeClassDecorator(glex, symbolTableService, methodDecorator);
    GlobalScopeClassBuilderDecorator globalScopeClassBuilderDecorator = new GlobalScopeClassBuilderDecorator(glex, symbolTableService, builderDecorator);
    ArtifactScopeDecorator artifactScopeDecorator = new ArtifactScopeDecorator(glex, symbolTableService, visitorService, methodDecorator);
    ArtifactScopeBuilderDecorator artifactScopeBuilderDecorator = new ArtifactScopeBuilderDecorator(glex, symbolTableService, builderDecorator, accessorDecorator);
    SymbolLoaderDecorator symbolReferenceDecorator = new SymbolLoaderDecorator(glex, symbolTableService, methodDecorator, new MandatoryMutatorSymbolLoaderDecorator(glex));
    SymbolLoaderBuilderDecorator symbolReferenceBuilderDecorator = new SymbolLoaderBuilderDecorator(glex, symbolTableService, accessorDecorator);
    CommonSymbolInterfaceDecorator commonSymbolInterfaceDecorator = new CommonSymbolInterfaceDecorator(glex, symbolTableService, visitorService, methodDecorator);
    ModelLoaderDecorator modelLoaderDecorator = new ModelLoaderDecorator(glex, symbolTableService, accessorDecorator);
    ModelLoaderBuilderDecorator modelLoaderBuilderDecorator = new ModelLoaderBuilderDecorator(glex, builderDecorator);
    SymbolResolvingDelegateInterfaceDecorator symbolResolvingDelegateInterfaceDecorator = new SymbolResolvingDelegateInterfaceDecorator(glex, symbolTableService);
    SymbolTableCreatorDecorator symbolTableCreatorDecorator = new SymbolTableCreatorDecorator(glex, symbolTableService, visitorService, methodDecorator);
    SymbolTableCreatorBuilderDecorator symbolTableCreatorBuilderDecorator = new SymbolTableCreatorBuilderDecorator(glex, symbolTableService);
    SymbolTableCreatorDelegatorDecorator symbolTableCreatorDelegatorDecorator = new SymbolTableCreatorDelegatorDecorator(glex, symbolTableService, visitorService);
    SymbolTableCreatorForSuperTypes symbolTableCreatorForSuperTypes = new SymbolTableCreatorForSuperTypes(glex, symbolTableService);
    SymbolTableCreatorDelegatorBuilderDecorator symbolTableCreatorDelegatorBuilderDecorator = new SymbolTableCreatorDelegatorBuilderDecorator(glex, builderDecorator);
    SymbolTableCreatorForSuperTypesBuilder symbolTableCreatorForSuperTypesBuilder = new SymbolTableCreatorForSuperTypesBuilder(glex, builderDecorator, symbolTableService);
    SymbolDeSerDecorator symbolDeSerDecorator = new SymbolDeSerDecorator(glex, symbolTableService);
    ScopeDeSerDecorator scopeDeSerDecorator = new ScopeDeSerDecorator(glex, symbolTableService, methodDecorator);
    SymbolTablePrinterDecorator symbolTablePrinterDecorator = new SymbolTablePrinterDecorator(glex, symbolTableService, visitorService);

    IterablePath targetPath = Mockito.mock(IterablePath.class);

    SymbolTableCDDecorator symbolTableCDDecorator = new SymbolTableCDDecorator(glex, targetPath, symbolTableService, symbolDecorator,
        symbolBuilderDecorator, symbolReferenceDecorator, symbolReferenceBuilderDecorator,
        scopeInterfaceDecorator, scopeClassDecorator, scopeClassBuilderDecorator,
        globalScopeInterfaceDecorator, globalScopeClassDecorator, globalScopeClassBuilderDecorator,
        artifactScopeDecorator, artifactScopeBuilderDecorator,
        commonSymbolInterfaceDecorator, modelLoaderDecorator, modelLoaderBuilderDecorator,
        symbolResolvingDelegateInterfaceDecorator, symbolTableCreatorDecorator, symbolTableCreatorBuilderDecorator,
        symbolTableCreatorDelegatorDecorator, symbolTableCreatorForSuperTypes, symbolTableCreatorDelegatorBuilderDecorator,
        symbolTableCreatorForSuperTypesBuilder, symbolDeSerDecorator, scopeDeSerDecorator, symbolTablePrinterDecorator);

    // cd with no handcoded classes
    return symbolTableCDDecorator.decorate(decoratedCompilationUnit, decoratedSymbolCompilationUnit, decoratedScopeCompilationUnit);
  }


  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalCompilationUnit, decoratedCompilationUnit);
  }

  @Test
  public void testMillName() {
    assertEquals("AutomatonMill", millClass.getName());
  }

  @Test
  public void testAttributeSize(){
    assertEquals(19, millClass.sizeCDAttributes());
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
    // visitor
    getAttributeBy("millAutomatonDelegatorVisitorBuilder", millClass);
    //symboltable
    getAttributeBy("millAutomatonSymbolBuilder", millClass);
    getAttributeBy("millStateSymbolBuilder", millClass);
    getAttributeBy("millFooSymbolBuilder", millClass);
    getAttributeBy("millAutomatonScopeCDScopeBuilder", millClass);
    getAttributeBy("millAutomatonSymbolLoaderBuilder", millClass);
    getAttributeBy("millStateSymbolLoaderBuilder", millClass);
    getAttributeBy("millFooSymbolLoaderBuilder", millClass);
    getAttributeBy("millAutomatonGlobalScopeBuilder", millClass);
    getAttributeBy("millAutomatonArtifactScopeBuilder", millClass);
    getAttributeBy("millAutomatonModelLoaderBuilder", millClass);
    getAttributeBy("millAutomatonSymbolTableCreatorBuilder", millClass);
    getAttributeBy("millAutomatonSymbolTableCreatorDelegatorBuilder", millClass);
  }

  @Test
  public void testAttributeModifier() {
    for (ASTCDAttribute astcdAttribute : millClass.getCDAttributeList()) {
      assertTrue(astcdAttribute.isPresentModifier());
      assertTrue(PROTECTED_STATIC.build().deepEquals(astcdAttribute.getModifier()));
    }
  }

  @Test
  public void testConstructor() {
    assertEquals(1, millClass.sizeCDConstructors());
    assertTrue(PROTECTED.build().deepEquals(millClass.getCDConstructor(0).getModifier()));
    assertEquals("AutomatonMill", millClass.getCDConstructor(0).getName());
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
  public void testDelegatorVisitorMethod() {
    ASTCDMethod fooBarBuilder = getMethodBy("automatonDelegatorVisitorBuilder", millClass);
    //test Method Name
    assertEquals("automatonDelegatorVisitorBuilder", fooBarBuilder.getName());
    //test Parameters
    assertTrue(fooBarBuilder.isEmptyCDParameters());
    //test ReturnType
    assertTrue(fooBarBuilder.getMCReturnType().isPresentMCType());
    assertDeepEquals("de.monticore.codegen.symboltable.automaton._visitor.AutomatonDelegatorVisitorBuilder",
        fooBarBuilder.getMCReturnType().getMCType());
    //test Modifier
    assertTrue(PUBLIC_STATIC.build().deepEquals(fooBarBuilder.getModifier()));
  }

  @Test
  public void testDelegatorVisitorBuilderMethod() {
    ASTCDMethod fooBarBuilder = getMethodBy("_automatonDelegatorVisitorBuilder", millClass);
    //test Method Name
    assertEquals("_automatonDelegatorVisitorBuilder", fooBarBuilder.getName());
    //test Parameters
    assertTrue(fooBarBuilder.isEmptyCDParameters());
    //test ReturnType
    assertTrue(fooBarBuilder.getMCReturnType().isPresentMCType());
    assertDeepEquals("de.monticore.codegen.symboltable.automaton._visitor.AutomatonDelegatorVisitorBuilder",
        fooBarBuilder.getMCReturnType().getMCType());
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
  public void testAutomatonSymbolLoaderMethod() {
    ASTCDMethod fooBarBuilder = getMethodBy("automatonSymbolLoaderBuilder", millClass);
    //test Method Name
    assertEquals("automatonSymbolLoaderBuilder", fooBarBuilder.getName());
    //test Parameters
    assertTrue(fooBarBuilder.isEmptyCDParameters());
    //test ReturnType
    assertTrue(fooBarBuilder.getMCReturnType().isPresentMCType());
    assertDeepEquals("de.monticore.codegen.symboltable.automaton._symboltable.AutomatonSymbolLoaderBuilder",
        fooBarBuilder.getMCReturnType().getMCType());
    //test Modifier
    assertTrue(PUBLIC_STATIC.build().deepEquals(fooBarBuilder.getModifier()));
  }

  @Test
  public void testAutomatonSymbolLoaderBuilderMethod() {
    ASTCDMethod fooBarBuilder = getMethodBy("_automatonSymbolLoaderBuilder", millClass);
    //test Method Name
    assertEquals("_automatonSymbolLoaderBuilder", fooBarBuilder.getName());
    //test Parameters
    assertTrue(fooBarBuilder.isEmptyCDParameters());
    //test ReturnType
    assertTrue(fooBarBuilder.getMCReturnType().isPresentMCType());
    assertDeepEquals("de.monticore.codegen.symboltable.automaton._symboltable.AutomatonSymbolLoaderBuilder",
        fooBarBuilder.getMCReturnType().getMCType());
    //test Modifier
    assertTrue(PROTECTED.build().deepEquals(fooBarBuilder.getModifier()));
  }


  @Test
  public void testStateSymbolLoaderMethod() {
    ASTCDMethod fooBarBuilder = getMethodBy("stateSymbolLoaderBuilder", millClass);
    //test Method Name
    assertEquals("stateSymbolLoaderBuilder", fooBarBuilder.getName());
    //test Parameters
    assertTrue(fooBarBuilder.isEmptyCDParameters());
    //test ReturnType
    assertTrue(fooBarBuilder.getMCReturnType().isPresentMCType());
    assertDeepEquals("de.monticore.codegen.symboltable.automaton._symboltable.StateSymbolLoaderBuilder",
        fooBarBuilder.getMCReturnType().getMCType());
    //test Modifier
    assertTrue(PUBLIC_STATIC.build().deepEquals(fooBarBuilder.getModifier()));
  }

  @Test
  public void testStateSymbolLoaderBuilderMethod() {
    ASTCDMethod fooBarBuilder = getMethodBy("_stateSymbolLoaderBuilder", millClass);
    //test Method Name
    assertEquals("_stateSymbolLoaderBuilder", fooBarBuilder.getName());
    //test Parameters
    assertTrue(fooBarBuilder.isEmptyCDParameters());
    //test ReturnType
    assertTrue(fooBarBuilder.getMCReturnType().isPresentMCType());
    assertDeepEquals("de.monticore.codegen.symboltable.automaton._symboltable.StateSymbolLoaderBuilder",
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
  public void testFooSymbolLoaderMethod() {
    ASTCDMethod fooBarBuilder = getMethodBy("fooSymbolLoaderBuilder", millClass);
    //test Method Name
    assertEquals("fooSymbolLoaderBuilder", fooBarBuilder.getName());
    //test Parameters
    assertTrue(fooBarBuilder.isEmptyCDParameters());
    //test ReturnType
    assertTrue(fooBarBuilder.getMCReturnType().isPresentMCType());
    assertDeepEquals("de.monticore.codegen.symboltable.automaton._symboltable.FooSymbolLoaderBuilder",
        fooBarBuilder.getMCReturnType().getMCType());
    //test Modifier
    assertTrue(PUBLIC_STATIC.build().deepEquals(fooBarBuilder.getModifier()));
  }

  @Test
  public void testFooSymbolLoaderBuilderMethod() {
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
    ASTCDMethod fooBarBuilder = getMethodBy("automatonScopeCDScopeBuilder", millClass);
    //test Method Name
    assertEquals("automatonScopeCDScopeBuilder", fooBarBuilder.getName());
    //test Parameters
    assertTrue(fooBarBuilder.isEmptyCDParameters());
    //test ReturnType
    assertTrue(fooBarBuilder.getMCReturnType().isPresentMCType());
    assertDeepEquals("de.monticore.codegen.symboltable.automaton._symboltable.AutomatonScopeCDScopeBuilder",
        fooBarBuilder.getMCReturnType().getMCType());
    //test Modifier
    assertTrue(PUBLIC_STATIC.build().deepEquals(fooBarBuilder.getModifier()));
  }

  @Test
  public void testAutomatonScopeBuilderMethod() {
    ASTCDMethod fooBarBuilder = getMethodBy("_automatonScopeCDScopeBuilder", millClass);
    //test Method Name
    assertEquals("_automatonScopeCDScopeBuilder", fooBarBuilder.getName());
    //test Parameters
    assertTrue(fooBarBuilder.isEmptyCDParameters());
    //test ReturnType
    assertTrue(fooBarBuilder.getMCReturnType().isPresentMCType());
    assertDeepEquals("de.monticore.codegen.symboltable.automaton._symboltable.AutomatonScopeCDScopeBuilder",
        fooBarBuilder.getMCReturnType().getMCType());
    //test Modifier
    assertTrue(PROTECTED.build().deepEquals(fooBarBuilder.getModifier()));
  }



  @Test
  public void testAutomatonGlobalScopeMethod() {
    ASTCDMethod fooBarBuilder = getMethodBy("automatonGlobalScopeBuilder", millClass);
    //test Method Name
    assertEquals("automatonGlobalScopeBuilder", fooBarBuilder.getName());
    //test Parameters
    assertTrue(fooBarBuilder.isEmptyCDParameters());
    //test ReturnType
    assertTrue(fooBarBuilder.getMCReturnType().isPresentMCType());
    assertDeepEquals("de.monticore.codegen.symboltable.automaton._symboltable.AutomatonGlobalScopeBuilder",
        fooBarBuilder.getMCReturnType().getMCType());
    //test Modifier
    assertTrue(PUBLIC_STATIC.build().deepEquals(fooBarBuilder.getModifier()));
  }

  @Test
  public void testAutomatonGlobalScopeBuilderMethod() {
    ASTCDMethod fooBarBuilder = getMethodBy("_automatonGlobalScopeBuilder", millClass);
    //test Method Name
    assertEquals("_automatonGlobalScopeBuilder", fooBarBuilder.getName());
    //test Parameters
    assertTrue(fooBarBuilder.isEmptyCDParameters());
    //test ReturnType
    assertTrue(fooBarBuilder.getMCReturnType().isPresentMCType());
    assertDeepEquals("de.monticore.codegen.symboltable.automaton._symboltable.AutomatonGlobalScopeBuilder",
        fooBarBuilder.getMCReturnType().getMCType());
    //test Modifier
    assertTrue(PROTECTED.build().deepEquals(fooBarBuilder.getModifier()));
  }


  @Test
  public void testAutomatonArtifactScopeMethod() {
    ASTCDMethod fooBarBuilder = getMethodBy("automatonArtifactScopeBuilder", millClass);
    //test Method Name
    assertEquals("automatonArtifactScopeBuilder", fooBarBuilder.getName());
    //test Parameters
    assertTrue(fooBarBuilder.isEmptyCDParameters());
    //test ReturnType
    assertTrue(fooBarBuilder.getMCReturnType().isPresentMCType());
    assertDeepEquals("de.monticore.codegen.symboltable.automaton._symboltable.AutomatonArtifactScopeBuilder",
        fooBarBuilder.getMCReturnType().getMCType());
    //test Modifier
    assertTrue(PUBLIC_STATIC.build().deepEquals(fooBarBuilder.getModifier()));
  }

  @Test
  public void testAutomatonArtifactScopeBuilderMethod() {
    ASTCDMethod fooBarBuilder = getMethodBy("_automatonArtifactScopeBuilder", millClass);
    //test Method Name
    assertEquals("_automatonArtifactScopeBuilder", fooBarBuilder.getName());
    //test Parameters
    assertTrue(fooBarBuilder.isEmptyCDParameters());
    //test ReturnType
    assertTrue(fooBarBuilder.getMCReturnType().isPresentMCType());
    assertDeepEquals("de.monticore.codegen.symboltable.automaton._symboltable.AutomatonArtifactScopeBuilder",
        fooBarBuilder.getMCReturnType().getMCType());
    //test Modifier
    assertTrue(PROTECTED.build().deepEquals(fooBarBuilder.getModifier()));
  }

  @Test
  public void testAutomatonSymbolTableCreatorMethod() {
    ASTCDMethod fooBarBuilder = getMethodBy("automatonSymbolTableCreatorBuilder", millClass);
    //test Method Name
    assertEquals("automatonSymbolTableCreatorBuilder", fooBarBuilder.getName());
    //test Parameters
    assertTrue(fooBarBuilder.isEmptyCDParameters());
    //test ReturnType
    assertTrue(fooBarBuilder.getMCReturnType().isPresentMCType());
    assertDeepEquals("de.monticore.codegen.symboltable.automaton._symboltable.AutomatonSymbolTableCreatorBuilder",
        fooBarBuilder.getMCReturnType().getMCType());
    //test Modifier
    assertTrue(PUBLIC_STATIC.build().deepEquals(fooBarBuilder.getModifier()));
  }

  @Test
  public void testAutomatonSymbolTableCreatorBuilderMethod() {
    ASTCDMethod fooBarBuilder = getMethodBy("_automatonSymbolTableCreatorBuilder", millClass);
    //test Method Name
    assertEquals("_automatonSymbolTableCreatorBuilder", fooBarBuilder.getName());
    //test Parameters
    assertTrue(fooBarBuilder.isEmptyCDParameters());
    //test ReturnType
    assertTrue(fooBarBuilder.getMCReturnType().isPresentMCType());
    assertDeepEquals("de.monticore.codegen.symboltable.automaton._symboltable.AutomatonSymbolTableCreatorBuilder",
        fooBarBuilder.getMCReturnType().getMCType());
    //test Modifier
    assertTrue(PROTECTED.build().deepEquals(fooBarBuilder.getModifier()));
  }

  @Test
  public void testAutomatonSymbolTableCreatorDelegatorMethod() {
    ASTCDMethod fooBarBuilder = getMethodBy("automatonSymbolTableCreatorDelegatorBuilder", millClass);
    //test Method Name
    assertEquals("automatonSymbolTableCreatorDelegatorBuilder", fooBarBuilder.getName());
    //test Parameters
    assertTrue(fooBarBuilder.isEmptyCDParameters());
    //test ReturnType
    assertTrue(fooBarBuilder.getMCReturnType().isPresentMCType());
    assertDeepEquals("de.monticore.codegen.symboltable.automaton._symboltable.AutomatonSymbolTableCreatorDelegatorBuilder",
        fooBarBuilder.getMCReturnType().getMCType());
    //test Modifier
    assertTrue(PUBLIC_STATIC.build().deepEquals(fooBarBuilder.getModifier()));
  }

  @Test
  public void testAutomatonSymbolTableCreatorDelegatorBuilderMethod() {
    ASTCDMethod fooBarBuilder = getMethodBy("_automatonSymbolTableCreatorDelegatorBuilder", millClass);
    //test Method Name
    assertEquals("_automatonSymbolTableCreatorDelegatorBuilder", fooBarBuilder.getName());
    //test Parameters
    assertTrue(fooBarBuilder.isEmptyCDParameters());
    //test ReturnType
    assertTrue(fooBarBuilder.getMCReturnType().isPresentMCType());
    assertDeepEquals("de.monticore.codegen.symboltable.automaton._symboltable.AutomatonSymbolTableCreatorDelegatorBuilder",
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
