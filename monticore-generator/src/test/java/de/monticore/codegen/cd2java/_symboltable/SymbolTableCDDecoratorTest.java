/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable;

import de.monticore.cd.codegen.CdUtilsPrinter;
import de.monticore.cdbasis._ast.*;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.DecorationHelper;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._ast.builder.BuilderDecorator;
import de.monticore.codegen.cd2java._symboltable.scope.*;
import de.monticore.codegen.cd2java._symboltable.scopesgenitor.ScopesGenitorDecorator;
import de.monticore.codegen.cd2java._symboltable.scopesgenitor.ScopesGenitorDelegatorDecorator;
import de.monticore.codegen.cd2java._symboltable.serialization.ScopeDeSerDecorator;
import de.monticore.codegen.cd2java._symboltable.serialization.SymbolDeSerDecorator;
import de.monticore.codegen.cd2java._symboltable.serialization.Symbols2JsonDecorator;
import de.monticore.codegen.cd2java._symboltable.symbol.*;
import de.monticore.codegen.cd2java._symboltable.symbol.symbolsurrogatemutator.MandatoryMutatorSymbolSurrogateDecorator;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.cd2java.methods.AccessorDecorator;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.io.paths.MCPath;
import de.monticore.umlmodifier._ast.ASTModifier;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getClassBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getInterfaceBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SymbolTableCDDecoratorTest extends DecoratorTestCase {

  private GlobalExtensionManagement glex;

  private ASTCDCompilationUnit originalASTCompilationUnit;

  private ASTCDCompilationUnit originalSymbolCompilationUnit;

  private ASTCDCompilationUnit originalScopeCompilationUnit;

  private ASTCDCompilationUnit cloneASTCompilationUnit;

  private ASTCDCompilationUnit cloneSymbolCompilationUnit;

  private ASTCDCompilationUnit cloneScopeCompilationUnit;

  private ASTCDCompilationUnit symTabCD;

  private ASTCDCompilationUnit symTabCDWithHC;

  private ASTCDCompilationUnit symTabCDComponent;

  @Before
  public void setUp() {
    this.glex = new GlobalExtensionManagement();
    MCPath targetPath = Mockito.mock(MCPath.class);

    this.glex.setGlobalValue("astHelper", DecorationHelper.getInstance());
    this.glex.setGlobalValue("cdPrinter", new CdUtilsPrinter());
    originalASTCompilationUnit = this.parse("de", "monticore", "codegen", "symboltable", "Automaton");
    originalScopeCompilationUnit = this.parse("de", "monticore", "codegen", "symboltable", "AutomatonScopeCD");
    originalSymbolCompilationUnit = this.parse("de", "monticore", "codegen", "symboltable", "AutomatonSymbolCD");

    cloneASTCompilationUnit = originalASTCompilationUnit.deepClone();
    cloneSymbolCompilationUnit = originalSymbolCompilationUnit.deepClone();
    cloneScopeCompilationUnit = originalScopeCompilationUnit.deepClone();

    symTabCD = createEmptyCompilationUnit(originalASTCompilationUnit);
    symTabCDWithHC = createEmptyCompilationUnit(originalASTCompilationUnit);
    symTabCDComponent = createEmptyCompilationUnit(originalASTCompilationUnit);

    this.glex.setGlobalValue("service", new AbstractService(originalASTCompilationUnit));

    SymbolTableService symbolTableService = new SymbolTableService(originalASTCompilationUnit);
    VisitorService visitorService = new VisitorService(originalASTCompilationUnit);
    MethodDecorator methodDecorator = new MethodDecorator(glex, symbolTableService);
    AccessorDecorator accessorDecorator = new AccessorDecorator(glex, symbolTableService);

    SymbolDecorator symbolDecorator = new SymbolDecorator(glex, symbolTableService, visitorService, methodDecorator);
    BuilderDecorator builderDecorator = new BuilderDecorator(glex, accessorDecorator, symbolTableService);
    SymbolBuilderDecorator symbolBuilderDecorator = new SymbolBuilderDecorator(glex, symbolTableService, builderDecorator);
    ScopeInterfaceDecorator scopeInterfaceDecorator = new ScopeInterfaceDecorator(glex, symbolTableService, visitorService, methodDecorator);
    ScopeClassDecorator scopeClassDecorator = new ScopeClassDecorator(glex, symbolTableService, visitorService, methodDecorator);
    GlobalScopeInterfaceDecorator globalScopeInterfaceDecorator = new GlobalScopeInterfaceDecorator(glex, symbolTableService, visitorService, methodDecorator);
    GlobalScopeClassDecorator globalScopeClassDecorator = new GlobalScopeClassDecorator(glex, symbolTableService, visitorService, methodDecorator);
    ArtifactScopeInterfaceDecorator artifactScopeInterfaceDecorator = new ArtifactScopeInterfaceDecorator(glex, symbolTableService, visitorService, methodDecorator);
    ArtifactScopeClassDecorator artifactScopeDecorator = new ArtifactScopeClassDecorator(glex, symbolTableService, visitorService, methodDecorator);
    SymbolSurrogateDecorator symbolReferenceDecorator = new SymbolSurrogateDecorator(glex, symbolTableService, methodDecorator, new MandatoryMutatorSymbolSurrogateDecorator(glex));
    SymbolSurrogateBuilderDecorator symbolReferenceBuilderDecorator = new SymbolSurrogateBuilderDecorator(glex, symbolTableService, accessorDecorator);
    CommonSymbolInterfaceDecorator commonSymbolInterfaceDecorator = new CommonSymbolInterfaceDecorator(glex, symbolTableService, visitorService, methodDecorator);
    SymbolResolverInterfaceDecorator symbolResolverInterfaceDecorator = new SymbolResolverInterfaceDecorator(glex, symbolTableService);
    SymbolDeSerDecorator symbolDeSerDecorator = new SymbolDeSerDecorator(glex, symbolTableService, new MCPath());
    ScopeDeSerDecorator scopeDeSerDecorator = new ScopeDeSerDecorator(glex, symbolTableService, methodDecorator, visitorService, new MCPath());
    Symbols2JsonDecorator symbolTablePrinterDecorator = new Symbols2JsonDecorator(glex, symbolTableService, visitorService, methodDecorator, new MCPath());
    ScopesGenitorDecorator scopesGenitorDecorator = new ScopesGenitorDecorator(glex, symbolTableService, visitorService, methodDecorator);
    ScopesGenitorDelegatorDecorator scopesGenitorDelegatorDecorator = new ScopesGenitorDelegatorDecorator(glex, symbolTableService, visitorService);

    SymbolTableCDDecorator symbolTableCDDecorator = new SymbolTableCDDecorator(glex, targetPath, symbolTableService, symbolDecorator,
        symbolBuilderDecorator, symbolReferenceDecorator, symbolReferenceBuilderDecorator,
        scopeInterfaceDecorator, scopeClassDecorator,
        globalScopeInterfaceDecorator, globalScopeClassDecorator,
        artifactScopeInterfaceDecorator, artifactScopeDecorator,
        commonSymbolInterfaceDecorator,
        symbolResolverInterfaceDecorator,
         symbolDeSerDecorator, scopeDeSerDecorator, symbolTablePrinterDecorator, scopesGenitorDecorator, scopesGenitorDelegatorDecorator);

    // cd with no handcoded classes
    symbolTableCDDecorator.decorate(originalASTCompilationUnit, originalSymbolCompilationUnit, originalScopeCompilationUnit, symTabCD);

    // cd with handcoded classes and component and no start prod
    symbolTableCDDecorator.decorate(originalASTCompilationUnit, originalSymbolCompilationUnit, originalScopeCompilationUnit, symTabCDWithHC);

    SymbolTableService mockService = Mockito.spy(new SymbolTableService(originalASTCompilationUnit));
    SymbolTableCDDecorator mockDecorator = new SymbolTableCDDecorator(glex, targetPath, mockService, symbolDecorator,
        symbolBuilderDecorator, symbolReferenceDecorator, symbolReferenceBuilderDecorator,
        scopeInterfaceDecorator, scopeClassDecorator,
        globalScopeInterfaceDecorator, globalScopeClassDecorator,
        artifactScopeInterfaceDecorator, artifactScopeDecorator,
        commonSymbolInterfaceDecorator,
        symbolResolverInterfaceDecorator,
        symbolDeSerDecorator, scopeDeSerDecorator, symbolTablePrinterDecorator, scopesGenitorDecorator, scopesGenitorDelegatorDecorator);
    Mockito.doReturn(false).when(mockService).hasStartProd(Mockito.any(ASTCDDefinition.class));
    Mockito.doReturn(true).when(mockService).hasComponentStereotype(Mockito.any(ASTModifier.class));
    mockDecorator.decorate(originalASTCompilationUnit, originalSymbolCompilationUnit, originalScopeCompilationUnit, symTabCDComponent);
  }


  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalASTCompilationUnit, cloneASTCompilationUnit);
    assertDeepEquals(originalSymbolCompilationUnit, cloneSymbolCompilationUnit);
    assertDeepEquals(originalScopeCompilationUnit, cloneScopeCompilationUnit);
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testCDName() {
    assertEquals("Automaton", symTabCD.getCDDefinition().getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testClassCount() {
    assertEquals(27, symTabCD.getCDDefinition().getCDClassesList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testClassNames() {
    ASTCDClass automatonSymbol = getClassBy("AutomatonSymbol", symTabCD);
    ASTCDClass stateSymbol = getClassBy("StateSymbol", symTabCD);
    ASTCDClass fooSymbol = getClassBy("FooSymbol", symTabCD);
    ASTCDClass automatonSymbolBuilder = getClassBy("AutomatonSymbolBuilder", symTabCD);
    ASTCDClass stateSymbolBuilder = getClassBy("StateSymbolBuilder", symTabCD);
    ASTCDClass fooSymbolBuilder = getClassBy("FooSymbolBuilder", symTabCD);
    ASTCDClass automatonScope = getClassBy("AutomatonScope", symTabCD);
    ASTCDClass automatonSymbolSurrogate = getClassBy("AutomatonSymbolSurrogate", symTabCD);
    ASTCDClass stateSymbolSurrogate = getClassBy("StateSymbolSurrogate", symTabCD);
    ASTCDClass fooSymbolSurrogate = getClassBy("FooSymbolSurrogate", symTabCD);
    ASTCDClass automatonSymbolSurrogateBuilder = getClassBy("AutomatonSymbolSurrogateBuilder", symTabCD);
    ASTCDClass stateSymbolSurrogateBuilder = getClassBy("StateSymbolSurrogateBuilder", symTabCD);
    ASTCDClass fooSymbolSurrogateBuilder = getClassBy("FooSymbolSurrogateBuilder", symTabCD);
    ASTCDClass automatonGlobalScope = getClassBy("AutomatonGlobalScope", symTabCD);
    ASTCDClass automatonArtifactScope = getClassBy("AutomatonArtifactScope", symTabCD);
    ASTCDClass automatonDeSer = getClassBy("AutomatonDeSer", symTabCD);
    ASTCDClass automatonSymbolDeSer = getClassBy("AutomatonSymbolDeSer", symTabCD);
    ASTCDClass stateSymbolDeSer = getClassBy("StateSymbolDeSer", symTabCD);
    ASTCDClass fooSymbolDeSer = getClassBy("FooSymbolDeSer", symTabCD);
    ASTCDClass automatonSymbolTablePrinter = getClassBy("AutomatonSymbols2Json", symTabCD);
    ASTCDClass automatonScopesGenitor = getClassBy("AutomatonScopesGenitor", symTabCD);
    ASTCDClass automatonScopesGenitorDelegator = getClassBy("AutomatonScopesGenitorDelegator", symTabCD);
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testInterfaceCount() {
    assertEquals(7, symTabCD.getCDDefinition().getCDInterfacesList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testInterfaces() {
    ASTCDInterface iAutomatonScope = getInterfaceBy("IAutomatonScope", symTabCD);
    ASTCDInterface iAutomatonGlobalScope = getInterfaceBy("IAutomatonGlobalScope", symTabCD);
    ASTCDInterface iAutomatonArtifactScope = getInterfaceBy("IAutomatonArtifactScope", symTabCD);
    ASTCDInterface iCommonAutomatonSymbol = getInterfaceBy("ICommonAutomatonSymbol", symTabCD);
    ASTCDInterface iAutomatonSymbolResolver = getInterfaceBy("IAutomatonSymbolResolver", symTabCD);
    ASTCDInterface iStateSymbolResolver = getInterfaceBy("IStateSymbolResolver", symTabCD);
    ASTCDInterface symbolInterfaceSymbol = getInterfaceBy("ISymbolInterfaceSymbolResolver", symTabCD);
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testNoEnum() {
    assertTrue(symTabCD.getCDDefinition().getCDEnumsList().isEmpty());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testPackage() {
    assertTrue (symTabCD.getCDDefinition().getPackageWithName("de.monticore.codegen.symboltable.automaton._symboltable").isPresent());
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testImports() {
    assertEquals(0, symTabCD.getMCImportStatementList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }


  @Test
  public void testCDNameWithHC() {
    assertEquals("Automaton", symTabCDWithHC.getCDDefinition().getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testClassCountWithHC() {
    assertEquals(27, symTabCDWithHC.getCDDefinition().getCDClassesList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testClassNamesWithHC() {
    ASTCDClass automatonSymbol = getClassBy("AutomatonSymbol", symTabCDWithHC);
    ASTCDClass stateSymbol = getClassBy("StateSymbol", symTabCDWithHC);
    ASTCDClass fooSymbol = getClassBy("FooSymbol", symTabCDWithHC);
    ASTCDClass automatonSymbolBuilder = getClassBy("AutomatonSymbolBuilder", symTabCDWithHC);
    ASTCDClass stateSymbolBuilder = getClassBy("StateSymbolBuilder", symTabCDWithHC);
    ASTCDClass fooSymbolBuilder = getClassBy("FooSymbolBuilder", symTabCDWithHC);
    ASTCDClass automatonScope = getClassBy("AutomatonScope", symTabCDWithHC);
    ASTCDClass automatonSymbolSurrogate = getClassBy("AutomatonSymbolSurrogate", symTabCDWithHC);
    ASTCDClass stateSymbolSurrogate = getClassBy("StateSymbolSurrogate", symTabCDWithHC);
    ASTCDClass fooSymbolSurrogate = getClassBy("FooSymbolSurrogate", symTabCDWithHC);
    ASTCDClass automatonSymbolSurrogateBuilder = getClassBy("AutomatonSymbolSurrogateBuilder", symTabCDWithHC);
    ASTCDClass stateSymbolSurrogateBuilder = getClassBy("StateSymbolSurrogateBuilder", symTabCDWithHC);
    ASTCDClass fooSymbolSurrogateBuilder = getClassBy("FooSymbolSurrogateBuilder", symTabCDWithHC);
    ASTCDClass automatonGlobalScope = getClassBy("AutomatonGlobalScope", symTabCDWithHC);
    ASTCDClass automatonArtifactScope = getClassBy("AutomatonArtifactScope", symTabCDWithHC);
    ASTCDClass automatonScopesGenitor = getClassBy("AutomatonScopesGenitor", symTabCDWithHC);
    ASTCDClass automatonScopesGenitorDelegator = getClassBy("AutomatonScopesGenitorDelegator", symTabCDWithHC);
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testInterfaceCountWithHC() {
    assertEquals(7, symTabCDWithHC.getCDDefinition().getCDInterfacesList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testInterfacesWithHC() {
    ASTCDInterface iAutomatonScope = getInterfaceBy("IAutomatonScope", symTabCDWithHC);
    ASTCDInterface iAutomatonGlobalScope = getInterfaceBy("IAutomatonGlobalScope", symTabCDWithHC);
    ASTCDInterface iAutomatonArtifactScope = getInterfaceBy("IAutomatonArtifactScope", symTabCDWithHC);
    ASTCDInterface iCommonAutomatonSymbol = getInterfaceBy("ICommonAutomatonSymbol", symTabCDWithHC);
    ASTCDInterface iAutomatonSymbolResolver = getInterfaceBy("IAutomatonSymbolResolver", symTabCDWithHC);
    ASTCDInterface iStateSymbolResolver = getInterfaceBy("IStateSymbolResolver", symTabCDWithHC);
    ASTCDInterface symbolInterfaceSymbol = getInterfaceBy("ISymbolInterfaceSymbolResolver", symTabCDComponent);
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testNoEnumWithHC() {
    assertTrue(symTabCDWithHC.getCDDefinition().getCDEnumsList().isEmpty());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testCDNameComponent() {
    assertEquals("Automaton", symTabCDComponent.getCDDefinition().getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testClassCountComponent() {
    assertEquals(27, symTabCDComponent.getCDDefinition().getCDClassesList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testClassNamesComponent() {
    ASTCDClass automatonSymbol = getClassBy("AutomatonSymbol", symTabCDComponent);
    ASTCDClass stateSymbol = getClassBy("StateSymbol", symTabCDComponent);
    ASTCDClass fooSymbol = getClassBy("FooSymbol", symTabCDComponent);
    ASTCDClass automatonSymbolBuilder = getClassBy("AutomatonSymbolBuilder", symTabCDComponent);
    ASTCDClass stateSymbolBuilder = getClassBy("StateSymbolBuilder", symTabCDComponent);
    ASTCDClass fooSymbolBuilder = getClassBy("FooSymbolBuilder", symTabCDComponent);
    ASTCDClass automatonScope = getClassBy("AutomatonScope", symTabCDComponent);
    ASTCDClass automatonSymbolSurrogate = getClassBy("AutomatonSymbolSurrogate", symTabCDComponent);
    ASTCDClass stateSymbolSurrogate = getClassBy("StateSymbolSurrogate", symTabCDComponent);
    ASTCDClass fooSymbolSurrogate = getClassBy("FooSymbolSurrogate", symTabCDComponent);
    ASTCDClass automatonSymbolSurrogateBuilder = getClassBy("AutomatonSymbolSurrogateBuilder", symTabCDComponent);
    ASTCDClass stateSymbolSurrogateBuilder = getClassBy("StateSymbolSurrogateBuilder", symTabCDComponent);
    ASTCDClass fooSymbolSurrogateBuilder = getClassBy("FooSymbolSurrogateBuilder", symTabCDComponent);
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testInterfaceCountComponent() {
    assertEquals(7, symTabCDComponent.getCDDefinition().getCDInterfacesList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testInterfacesComponent() {
    ASTCDInterface iAutomatonScope = getInterfaceBy("IAutomatonScope", symTabCDComponent);
    ASTCDInterface iAutomatonGlobalScope = getInterfaceBy("IAutomatonGlobalScope", symTabCDComponent);
    ASTCDInterface iAutomatonArtifactScope = getInterfaceBy("IAutomatonArtifactScope", symTabCDComponent);
    ASTCDInterface iCommonAutomatonSymbol = getInterfaceBy("ICommonAutomatonSymbol", symTabCDComponent);
    ASTCDInterface iAutomatonSymbolResolver = getInterfaceBy("IAutomatonSymbolResolver", symTabCDComponent);
    ASTCDInterface iStateSymbolResolver = getInterfaceBy("IStateSymbolResolver", symTabCDComponent);
    ASTCDInterface symbolInterfaceSymbol = getInterfaceBy("ISymbolInterfaceSymbolResolver", symTabCDComponent);
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testNoEnumComponent() {
    assertTrue(symTabCDComponent.getCDDefinition().getCDEnumsList().isEmpty());
  
    assertTrue(Log.getFindings().isEmpty());
  }
}
