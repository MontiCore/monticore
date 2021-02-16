/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.mill;

import com.google.common.collect.Lists;
import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.ASTCDInterface;
import de.monticore.cd.prettyprint.CD4CodePrinter;
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
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.cd2java.data.DataDecorator;
import de.monticore.codegen.cd2java.data.DataDecoratorUtil;
import de.monticore.codegen.cd2java.data.InterfaceDecorator;
import de.monticore.codegen.cd2java.methods.AccessorDecorator;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.io.paths.IterablePath;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getClassBy;
import static org.junit.Assert.assertEquals;

public class CDMillDecoratorTest extends DecoratorTestCase {

  private ASTCDCompilationUnit millCD;

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
    VisitorService visitorService = new VisitorService(decoratedCompilationUnit);
    ParserService parserService = new ParserService(decoratedCompilationUnit);
    MillDecorator millDecorator = new MillDecorator(this.glex, symbolTableService, visitorService, parserService);

    CDMillDecorator cdMillDecorator = new CDMillDecorator(this.glex, millDecorator);
    this.millCD = cdMillDecorator.decorate(Lists.newArrayList(getASTCD(), getSymbolCD()));
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
    Symbols2JsonDecorator symbolTablePrinterDecorator = new Symbols2JsonDecorator(glex, symbolTableService, visitorService, methodDecorator);
    ScopesGenitorDecorator scopesGenitorDecorator = new ScopesGenitorDecorator(glex, symbolTableService, visitorService, methodDecorator);
    ScopesGenitorDelegatorDecorator scopesGenitorDelegatorDecorator = new ScopesGenitorDelegatorDecorator(glex, symbolTableService, visitorService);

    IterablePath targetPath = Mockito.mock(IterablePath.class);

    SymbolTableCDDecorator symbolTableCDDecorator = new SymbolTableCDDecorator(glex, targetPath, symbolTableService, symbolDecorator,
        symbolBuilderDecorator, symbolReferenceDecorator, symbolReferenceBuilderDecorator,
        scopeInterfaceDecorator, scopeClassDecorator,
        globalScopeInterfaceDecorator, globalScopeClassDecorator,
        artifactScopeInterfaceDecorator, artifactScopeDecorator,
        commonSymbolInterfaceDecorator,  symbolResolverInterfaceDecorator,
        symbolDeSerDecorator, scopeDeSerDecorator, symbolTablePrinterDecorator, scopesGenitorDecorator, scopesGenitorDelegatorDecorator);

    // cd with no handcoded classes
    return symbolTableCDDecorator.decorate(decoratedCompilationUnit, decoratedSymbolCompilationUnit, decoratedScopeCompilationUnit);
  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalCompilationUnit, decoratedCompilationUnit);
  }

  @Test
  public void testPackageName() {
    assertEquals(5, millCD.sizePackage());
    assertEquals("de", millCD.getPackage(0));
    assertEquals("monticore", millCD.getPackage(1));
    assertEquals("codegen", millCD.getPackage(2));
    assertEquals("symboltable", millCD.getPackage(3));
    assertEquals("automaton", millCD.getPackage(4));
  }

  @Test
  public void testDefinitionName() {
    assertEquals("Automaton", millCD.getCDDefinition().getName());
  }

  @Test
  public void testClassSize() {
    assertEquals(1, millCD.getCDDefinition().sizeCDClasss());
  }

  @Test
  public void testMillClass() {
    ASTCDClass automatonMill = getClassBy("AutomatonMill", millCD);
  }
}
