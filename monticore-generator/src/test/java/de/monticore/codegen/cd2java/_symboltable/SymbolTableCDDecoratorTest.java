/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.prettyprint.CD4CodePrinter;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.DecorationHelper;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._ast.builder.BuilderDecorator;
import de.monticore.codegen.cd2java._parser.ParserService;
import de.monticore.codegen.cd2java._symboltable.language.LanguageBuilderDecorator;
import de.monticore.codegen.cd2java._symboltable.language.LanguageDecorator;
import de.monticore.codegen.cd2java._symboltable.modelloader.ModelLoaderBuilderDecorator;
import de.monticore.codegen.cd2java._symboltable.modelloader.ModelLoaderDecorator;
import de.monticore.codegen.cd2java._symboltable.scope.*;
import de.monticore.codegen.cd2java._symboltable.serialization.ScopeDeSerDecorator;
import de.monticore.codegen.cd2java._symboltable.serialization.SymbolDeSerDecorator;
import de.monticore.codegen.cd2java._symboltable.serialization.SymbolTablePrinterDecorator;
import de.monticore.codegen.cd2java._symboltable.symbol.*;
import de.monticore.codegen.cd2java._symboltable.symbol.symbolloadermutator.MandatoryMutatorSymbolLoaderDecorator;
import de.monticore.codegen.cd2java._symboltable.symboltablecreator.*;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.cd2java.methods.AccessorDecorator;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.io.paths.IterablePath;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.nio.file.Path;
import java.util.Optional;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getClassBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getInterfaceBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SymbolTableCDDecoratorTest extends DecoratorTestCase {

  private GlobalExtensionManagement glex;

  private ASTCDCompilationUnit decoratedASTCompilationUnit;

  private ASTCDCompilationUnit decoratedSymbolCompilationUnit;

  private ASTCDCompilationUnit decoratedScopeCompilationUnit;

  private ASTCDCompilationUnit originalASTCompilationUnit;

  private ASTCDCompilationUnit originalSymbolCompilationUnit;

  private ASTCDCompilationUnit originalScopeCompilationUnit;

  private ASTCDCompilationUnit symTabCD;

  private ASTCDCompilationUnit symTabCDWithHC;

  private ASTCDCompilationUnit symTabCDComponent;

  @Before
  public void setUp() {
    Log.init();
    this.glex = new GlobalExtensionManagement();
    IterablePath targetPath = Mockito.mock(IterablePath.class);

    this.glex.setGlobalValue("astHelper", DecorationHelper.getInstance());
    this.glex.setGlobalValue("cdPrinter", new CD4CodePrinter());
    decoratedASTCompilationUnit = this.parse("de", "monticore", "codegen", "symboltable", "Automaton");
    decoratedScopeCompilationUnit = this.parse("de", "monticore", "codegen", "symboltable", "AutomatonScopeCD");
    decoratedSymbolCompilationUnit = this.parse("de", "monticore", "codegen", "symboltable", "AutomatonSymbolCD");
    originalASTCompilationUnit = decoratedASTCompilationUnit.deepClone();
    originalSymbolCompilationUnit = decoratedSymbolCompilationUnit.deepClone();
    originalScopeCompilationUnit = decoratedScopeCompilationUnit.deepClone();

    this.glex.setGlobalValue("service", new AbstractService(decoratedASTCompilationUnit));

    SymbolTableService symbolTableService = new SymbolTableService(decoratedASTCompilationUnit);
    VisitorService visitorService = new VisitorService(decoratedASTCompilationUnit);
    ParserService parserService = new ParserService(decoratedASTCompilationUnit);
    MethodDecorator methodDecorator = new MethodDecorator(glex, symbolTableService);
    AccessorDecorator accessorDecorator = new AccessorDecorator(glex, symbolTableService);

    SymbolDecorator symbolDecorator = new SymbolDecorator(glex, symbolTableService, visitorService, methodDecorator);
    BuilderDecorator builderDecorator = new BuilderDecorator(glex, accessorDecorator, symbolTableService);
    SymbolBuilderDecorator symbolBuilderDecorator = new SymbolBuilderDecorator(glex, builderDecorator);
    ScopeClassDecorator scopeClassDecorator = new ScopeClassDecorator(glex, symbolTableService, visitorService, methodDecorator);
    ScopeClassBuilderDecorator scopeClassBuilderDecorator = new ScopeClassBuilderDecorator(glex, builderDecorator);
    ScopeInterfaceDecorator scopeInterfaceDecorator = new ScopeInterfaceDecorator(glex, symbolTableService, visitorService, methodDecorator);
    GlobalScopeClassDecorator globalScopeClassDecorator = new GlobalScopeClassDecorator(glex, symbolTableService, methodDecorator);
    GlobalScopeClassBuilderDecorator globalScopeClassBuilderDecorator = new GlobalScopeClassBuilderDecorator(glex, symbolTableService, builderDecorator);
    ArtifactScopeDecorator artifactScopeDecorator = new ArtifactScopeDecorator(glex, symbolTableService, visitorService, methodDecorator);
    ArtifactScopeBuilderDecorator artifactScopeBuilderDecorator = new ArtifactScopeBuilderDecorator(glex, symbolTableService, builderDecorator, accessorDecorator);
    SymbolLoaderDecorator symbolReferenceDecorator = new SymbolLoaderDecorator(glex, symbolTableService, methodDecorator, new MandatoryMutatorSymbolLoaderDecorator(glex));
    SymbolLoaderBuilderDecorator symbolReferenceBuilderDecorator = new SymbolLoaderBuilderDecorator(glex, symbolTableService, accessorDecorator);
    CommonSymbolInterfaceDecorator commonSymbolInterfaceDecorator = new CommonSymbolInterfaceDecorator(glex, symbolTableService, visitorService, methodDecorator);
    LanguageDecorator languageDecorator = new LanguageDecorator(glex, symbolTableService, parserService, accessorDecorator);
    LanguageBuilderDecorator languageBuilderDecorator = new LanguageBuilderDecorator(glex, builderDecorator);
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

    SymbolTableCDDecorator symbolTableCDDecorator = new SymbolTableCDDecorator(glex, targetPath, symbolTableService, symbolDecorator,
        symbolBuilderDecorator, symbolReferenceDecorator, symbolReferenceBuilderDecorator,
        scopeClassDecorator, scopeClassBuilderDecorator, scopeInterfaceDecorator,
        globalScopeClassDecorator, globalScopeClassBuilderDecorator, artifactScopeDecorator, artifactScopeBuilderDecorator,
        commonSymbolInterfaceDecorator, languageDecorator, languageBuilderDecorator, modelLoaderDecorator, modelLoaderBuilderDecorator,
        symbolResolvingDelegateInterfaceDecorator, symbolTableCreatorDecorator, symbolTableCreatorBuilderDecorator,
        symbolTableCreatorDelegatorDecorator, symbolTableCreatorForSuperTypes, symbolTableCreatorDelegatorBuilderDecorator,
        symbolTableCreatorForSuperTypesBuilder, symbolDeSerDecorator, scopeDeSerDecorator, symbolTablePrinterDecorator);

    // cd with no handcoded classes
    this.symTabCD = symbolTableCDDecorator.decorate(decoratedASTCompilationUnit, decoratedSymbolCompilationUnit, decoratedScopeCompilationUnit);

    // cd with handcoded classes and component and no start prod
    Mockito.when(targetPath.getResolvedPath(Mockito.any(Path.class))).thenReturn(Optional.of(Mockito.mock(Path.class)));
    this.symTabCDWithHC = symbolTableCDDecorator.decorate(decoratedASTCompilationUnit, decoratedSymbolCompilationUnit, decoratedScopeCompilationUnit);

    SymbolTableService mockService = Mockito.spy(new SymbolTableService(decoratedASTCompilationUnit));
    SymbolTableCDDecorator mockDecorator = new SymbolTableCDDecorator(glex, targetPath, mockService, symbolDecorator,
        symbolBuilderDecorator, symbolReferenceDecorator, symbolReferenceBuilderDecorator,
        scopeClassDecorator, scopeClassBuilderDecorator, scopeInterfaceDecorator,
        globalScopeClassDecorator, globalScopeClassBuilderDecorator, artifactScopeDecorator, artifactScopeBuilderDecorator,
        commonSymbolInterfaceDecorator, languageDecorator, languageBuilderDecorator, modelLoaderDecorator, modelLoaderBuilderDecorator,
        symbolResolvingDelegateInterfaceDecorator, symbolTableCreatorDecorator, symbolTableCreatorBuilderDecorator,
        symbolTableCreatorDelegatorDecorator, symbolTableCreatorForSuperTypes, symbolTableCreatorDelegatorBuilderDecorator,
        symbolTableCreatorForSuperTypesBuilder,
        symbolDeSerDecorator, scopeDeSerDecorator, symbolTablePrinterDecorator);
    Mockito.doReturn(false).when(mockService).hasStartProd(Mockito.any(ASTCDDefinition.class));
    Mockito.doReturn(true).when(mockService).hasComponentStereotype(Mockito.any(ASTModifier.class));
    this.symTabCDComponent = mockDecorator.decorate(decoratedASTCompilationUnit, decoratedSymbolCompilationUnit, decoratedScopeCompilationUnit);
  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalASTCompilationUnit, decoratedASTCompilationUnit);
    assertDeepEquals(originalSymbolCompilationUnit, originalSymbolCompilationUnit);
    assertDeepEquals(originalScopeCompilationUnit, originalScopeCompilationUnit);
  }

  @Test
  public void testCDName() {
    assertEquals("Automaton", symTabCD.getCDDefinition().getName());
  }

  @Test
  public void testClassCount() {
    assertEquals(30, symTabCD.getCDDefinition().getCDClassList().size());
  }

  @Test
  public void testClassNames() {
    ASTCDClass automatonSymbol = getClassBy("AutomatonSymbol", symTabCD);
    ASTCDClass stateSymbol = getClassBy("StateSymbol", symTabCD);
    ASTCDClass fooSymbol = getClassBy("FooSymbol", symTabCD);
    ASTCDClass automatonSymbolBuilder = getClassBy("AutomatonSymbolBuilder", symTabCD);
    ASTCDClass stateSymbolBuilder = getClassBy("StateSymbolBuilder", symTabCD);
    ASTCDClass fooSymbolBuilder = getClassBy("FooSymbolBuilder", symTabCD);
    ASTCDClass automatonScopeCDScope = getClassBy("AutomatonScopeCDScope", symTabCD);
    ASTCDClass automatonScopeCDScopeBuilder = getClassBy("AutomatonScopeCDScopeBuilder", symTabCD);
    ASTCDClass automatonSymbolLoader = getClassBy("AutomatonSymbolLoader", symTabCD);
    ASTCDClass stateSymbolLoader = getClassBy("StateSymbolLoader", symTabCD);
    ASTCDClass fooSymbolLoader = getClassBy("FooSymbolLoader", symTabCD);
    ASTCDClass automatonSymbolLoaderBuilder = getClassBy("AutomatonSymbolLoaderBuilder", symTabCD);
    ASTCDClass stateSymbolLoaderBuilder = getClassBy("StateSymbolLoaderBuilder", symTabCD);
    ASTCDClass fooSymbolLoaderBuilder = getClassBy("FooSymbolLoaderBuilder", symTabCD);
    ASTCDClass automatonGlobalScope = getClassBy("AutomatonGlobalScope", symTabCD);
    ASTCDClass automatonGlobalScopeBuilder = getClassBy("AutomatonGlobalScopeBuilder", symTabCD);
    ASTCDClass automatonArtifactScope = getClassBy("AutomatonArtifactScope", symTabCD);
    ASTCDClass automatonArtifactScopeBuilder = getClassBy("AutomatonArtifactScopeBuilder", symTabCD);
    ASTCDClass automatonLanguage = getClassBy("AutomatonLanguage", symTabCD);
    ASTCDClass automatonModelLoader = getClassBy("AutomatonModelLoader", symTabCD);
    ASTCDClass automatonModelLoaderBuilder = getClassBy("AutomatonModelLoaderBuilder", symTabCD);
    ASTCDClass automatonSymbolTableCreator = getClassBy("AutomatonSymbolTableCreator", symTabCD);
    ASTCDClass automatonSymbolTableCreatorBuilder = getClassBy("AutomatonSymbolTableCreatorBuilder", symTabCD);
    ASTCDClass automatonSymbolTableCreatorDelegator = getClassBy("AutomatonSymbolTableCreatorDelegator", symTabCD);
    ASTCDClass automatonSymbolTableCreatorDelegatorBuilder = getClassBy("AutomatonSymbolTableCreatorDelegatorBuilder", symTabCD);
  }

  @Test
  public void testInterfaceCount() {
    assertEquals(5, symTabCD.getCDDefinition().getCDInterfaceList().size());
  }

  @Test
  public void testInterfaces() {
    ASTCDInterface iAutomatonScope = getInterfaceBy("IAutomatonScope", symTabCD);
    ASTCDInterface iCommonAutomatonSymbol = getInterfaceBy("ICommonAutomatonSymbol", symTabCD);
    ASTCDInterface iAutomatonSymbolResolvingDelegate = getInterfaceBy("IAutomatonSymbolResolvingDelegate", symTabCD);
    ASTCDInterface iStateSymbolResolvingDelegate = getInterfaceBy("IStateSymbolResolvingDelegate", symTabCD);
    ASTCDInterface symbolInterfaceSymbol = getInterfaceBy("ISymbolInterfaceSymbolResolvingDelegate", symTabCD);
  }

  @Test
  public void testNoEnum() {
    assertTrue(symTabCD.getCDDefinition().isEmptyCDEnums());
  }

  @Test
  public void testPackage() {
    assertEquals(6, symTabCD.getPackageList().size());
    assertEquals("de", symTabCD.getPackageList().get(0));
    assertEquals("monticore", symTabCD.getPackageList().get(1));
    assertEquals("codegen", symTabCD.getPackageList().get(2));
    assertEquals("symboltable", symTabCD.getPackageList().get(3));
    assertEquals("automaton", symTabCD.getPackageList().get(4));
    assertEquals("_symboltable", symTabCD.getPackageList().get(5));

  }

  @Test
  public void testImports() {
    assertEquals(0, symTabCD.getMCImportStatementList().size());
  }


  @Test
  public void testCDNameWithHC() {
    assertEquals("Automaton", symTabCDWithHC.getCDDefinition().getName());
  }

  @Test
  public void testClassCountWithHC() {
    assertEquals(31, symTabCDWithHC.getCDDefinition().getCDClassList().size());
  }

  @Test
  public void testClassNamesWithHC() {
    ASTCDClass automatonSymbol = getClassBy("AutomatonSymbol", symTabCDWithHC);
    ASTCDClass stateSymbol = getClassBy("StateSymbol", symTabCDWithHC);
    ASTCDClass fooSymbol = getClassBy("FooSymbol", symTabCDWithHC);
    ASTCDClass automatonSymbolBuilder = getClassBy("AutomatonSymbolBuilder", symTabCDWithHC);
    ASTCDClass stateSymbolBuilder = getClassBy("StateSymbolBuilder", symTabCDWithHC);
    ASTCDClass fooSymbolBuilder = getClassBy("FooSymbolBuilder", symTabCDWithHC);
    ASTCDClass automatonScopeCDScope = getClassBy("AutomatonScopeCDScope", symTabCDWithHC);
    ASTCDClass automatonScopeCDScopeBuilder = getClassBy("AutomatonScopeCDScopeBuilder", symTabCDWithHC);
    ASTCDClass automatonSymbolLoader = getClassBy("AutomatonSymbolLoader", symTabCDWithHC);
    ASTCDClass stateSymbolLoader = getClassBy("StateSymbolLoader", symTabCDWithHC);
    ASTCDClass fooSymbolLoader = getClassBy("FooSymbolLoader", symTabCDWithHC);
    ASTCDClass automatonSymbolLoaderBuilder = getClassBy("AutomatonSymbolLoaderBuilder", symTabCDWithHC);
    ASTCDClass stateSymbolLoaderBuilder = getClassBy("StateSymbolLoaderBuilder", symTabCDWithHC);
    ASTCDClass fooSymbolLoaderBuilder = getClassBy("FooSymbolLoaderBuilder", symTabCDWithHC);
    ASTCDClass automatonGlobalScope = getClassBy("AutomatonGlobalScope", symTabCDWithHC);
    ASTCDClass automatonGlobalScopeBuilder = getClassBy("AutomatonGlobalScopeBuilder", symTabCDWithHC);
    ASTCDClass automatonArtifactScope = getClassBy("AutomatonArtifactScope", symTabCDWithHC);
    ASTCDClass automatonArtifactScopeBuilder = getClassBy("AutomatonArtifactScopeBuilder", symTabCDWithHC);
    ASTCDClass automatonLanguage = getClassBy("AutomatonLanguage", symTabCDWithHC);
    ASTCDClass automatonLanguageBuilder = getClassBy("AutomatonLanguageBuilder", symTabCDWithHC);
    ASTCDClass automatonModelLoader = getClassBy("AutomatonModelLoader", symTabCDWithHC);
    ASTCDClass automatonModelLoaderBuilder = getClassBy("AutomatonModelLoaderBuilder", symTabCDWithHC);
    ASTCDClass automatonSymbolTableCreator = getClassBy("AutomatonSymbolTableCreator", symTabCDWithHC);
    ASTCDClass automatonSymbolTableCreatorBuilder = getClassBy("AutomatonSymbolTableCreatorBuilder", symTabCDWithHC);
    ASTCDClass automatonSymbolTableCreatorDelegator = getClassBy("AutomatonSymbolTableCreatorDelegator", symTabCDWithHC);
    ASTCDClass automatonSymbolTableCreatorDelegatorBuilder = getClassBy("AutomatonSymbolTableCreatorDelegatorBuilder", symTabCDWithHC);
  }

  @Test
  public void testInterfaceCountWithHC() {
    assertEquals(5, symTabCDWithHC.getCDDefinition().getCDInterfaceList().size());
  }

  @Test
  public void testInterfacesWithHC() {
    ASTCDInterface iAutomatonScope = getInterfaceBy("IAutomatonScope", symTabCDWithHC);
    ASTCDInterface iCommonAutomatonSymbol = getInterfaceBy("ICommonAutomatonSymbol", symTabCDWithHC);
    ASTCDInterface iAutomatonSymbolResolvingDelegate = getInterfaceBy("IAutomatonSymbolResolvingDelegate", symTabCDWithHC);
    ASTCDInterface iStateSymbolResolvingDelegate = getInterfaceBy("IStateSymbolResolvingDelegate", symTabCDWithHC);
    ASTCDInterface symbolInterfaceSymbol = getInterfaceBy("ISymbolInterfaceSymbolResolvingDelegate", symTabCDComponent);
  }

  @Test
  public void testNoEnumWithHC() {
    assertTrue(symTabCDWithHC.getCDDefinition().isEmptyCDEnums());
  }

  @Test
  public void testCDNameComponent() {
    assertEquals("Automaton", symTabCDComponent.getCDDefinition().getName());
  }

  @Test
  public void testClassCountComponent() {
    assertEquals(18, symTabCDComponent.getCDDefinition().getCDClassList().size());
  }

  @Test
  public void testClassNamesComponent() {
    ASTCDClass automatonSymbol = getClassBy("AutomatonSymbol", symTabCDComponent);
    ASTCDClass stateSymbol = getClassBy("StateSymbol", symTabCDComponent);
    ASTCDClass fooSymbol = getClassBy("FooSymbol", symTabCDComponent);
    ASTCDClass automatonSymbolBuilder = getClassBy("AutomatonSymbolBuilder", symTabCDComponent);
    ASTCDClass stateSymbolBuilder = getClassBy("StateSymbolBuilder", symTabCDComponent);
    ASTCDClass fooSymbolBuilder = getClassBy("FooSymbolBuilder", symTabCDComponent);
    ASTCDClass automatonScopeCDScope = getClassBy("AutomatonScopeCDScope", symTabCDComponent);
    ASTCDClass automatonScopeCDScopeBuilder = getClassBy("AutomatonScopeCDScopeBuilder", symTabCDComponent);
    ASTCDClass automatonSymbolLoader = getClassBy("AutomatonSymbolLoader", symTabCDComponent);
    ASTCDClass stateSymbolLoader = getClassBy("StateSymbolLoader", symTabCDComponent);
    ASTCDClass fooSymbolLoader = getClassBy("FooSymbolLoader", symTabCDComponent);
    ASTCDClass automatonSymbolLoaderBuilder = getClassBy("AutomatonSymbolLoaderBuilder", symTabCDComponent);
    ASTCDClass stateSymbolLoaderBuilder = getClassBy("StateSymbolLoaderBuilder", symTabCDComponent);
    ASTCDClass fooSymbolLoaderBuilder = getClassBy("FooSymbolLoaderBuilder", symTabCDComponent);
  }

  @Test
  public void testInterfaceCountComponent() {
    assertEquals(5, symTabCDComponent.getCDDefinition().getCDInterfaceList().size());
  }

  @Test
  public void testInterfacesComponent() {
    ASTCDInterface iAutomatonScope = getInterfaceBy("IAutomatonScope", symTabCDComponent);
    ASTCDInterface iCommonAutomatonSymbol = getInterfaceBy("ICommonAutomatonSymbol", symTabCDComponent);
    ASTCDInterface iAutomatonSymbolResolvingDelegate = getInterfaceBy("IAutomatonSymbolResolvingDelegate", symTabCDComponent);
    ASTCDInterface iStateSymbolResolvingDelegate = getInterfaceBy("IStateSymbolResolvingDelegate", symTabCDComponent);
    ASTCDInterface symbolInterfaceSymbol = getInterfaceBy("ISymbolInterfaceSymbolResolvingDelegate", symTabCDComponent);
  }

  @Test
  public void testNoEnumComponent() {
    assertTrue(symTabCDComponent.getCDDefinition().isEmptyCDEnums());
  }
}
