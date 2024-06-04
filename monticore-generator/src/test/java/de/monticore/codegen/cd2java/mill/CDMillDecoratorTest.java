/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.mill;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._ast.ASTCDDecorator;
import de.monticore.codegen.cd2java._ast.ast_class.ASTDecorator;
import de.monticore.codegen.cd2java._ast.ast_class.ASTFullDecorator;
import de.monticore.codegen.cd2java._ast.ast_class.ASTScopeDecorator;
import de.monticore.codegen.cd2java._ast.ast_class.ASTService;
import de.monticore.codegen.cd2java._ast.ast_class.ASTSymbolDecorator;
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
import de.monticore.codegen.cd2java._symboltable.scope.ArtifactScopeClassDecorator;
import de.monticore.codegen.cd2java._symboltable.scope.ArtifactScopeInterfaceDecorator;
import de.monticore.codegen.cd2java._symboltable.scope.GlobalScopeClassDecorator;
import de.monticore.codegen.cd2java._symboltable.scope.GlobalScopeInterfaceDecorator;
import de.monticore.codegen.cd2java._symboltable.scope.ScopeClassDecorator;
import de.monticore.codegen.cd2java._symboltable.scope.ScopeInterfaceDecorator;
import de.monticore.codegen.cd2java._symboltable.scopesgenitor.ScopesGenitorDecorator;
import de.monticore.codegen.cd2java._symboltable.scopesgenitor.ScopesGenitorDelegatorDecorator;
import de.monticore.codegen.cd2java._symboltable.serialization.ScopeDeSerDecorator;
import de.monticore.codegen.cd2java._symboltable.serialization.SymbolDeSerDecorator;
import de.monticore.codegen.cd2java._symboltable.serialization.Symbols2JsonDecorator;
import de.monticore.codegen.cd2java._symboltable.symbol.CommonSymbolInterfaceDecorator;
import de.monticore.codegen.cd2java._symboltable.symbol.SymbolBuilderDecorator;
import de.monticore.codegen.cd2java._symboltable.symbol.SymbolDecorator;
import de.monticore.codegen.cd2java._symboltable.symbol.SymbolResolverInterfaceDecorator;
import de.monticore.codegen.cd2java._symboltable.symbol.SymbolSurrogateBuilderDecorator;
import de.monticore.codegen.cd2java._symboltable.symbol.SymbolSurrogateDecorator;
import de.monticore.codegen.cd2java._symboltable.symbol.symbolsurrogatemutator.MandatoryMutatorSymbolSurrogateDecorator;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.cd2java.data.DataDecorator;
import de.monticore.codegen.cd2java.data.DataDecoratorUtil;
import de.monticore.codegen.cd2java.data.InterfaceDecorator;
import de.monticore.codegen.cd2java.methods.AccessorDecorator;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.logging.Log;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getClassBy;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class CDMillDecoratorTest extends DecoratorTestCase {
  private ASTCDCompilationUnit originalCompilationUnit;

  private ASTCDCompilationUnit decoratedCompilationUnit;

  private ASTCDCompilationUnit symbolCompilationUnit;

  private ASTCDCompilationUnit scopeCompilationUnit;

  private ASTCDCompilationUnit clonedCD;

  @Before
  public void setUp() {
    originalCompilationUnit = this.parse("de", "monticore", "codegen", "symboltable", "Automaton");
    scopeCompilationUnit = this.parse("de", "monticore", "codegen", "symboltable", "AutomatonScopeCD");
    symbolCompilationUnit = this.parse("de", "monticore", "codegen", "symboltable", "AutomatonSymbolCD");
    clonedCD = originalCompilationUnit.deepClone();
    decoratedCompilationUnit = getASTCD();
    getSymbolCD();
    this.glex.setGlobalValue("service", new VisitorService(originalCompilationUnit));

    SymbolTableService symbolTableService = new SymbolTableService(originalCompilationUnit);
    VisitorService visitorService = new VisitorService(originalCompilationUnit);
    ParserService parserService = new ParserService(originalCompilationUnit);
    MillDecorator millDecorator = new MillDecorator(this.glex, symbolTableService, visitorService, parserService);

    CDMillDecorator cdMillDecorator = new CDMillDecorator(this.glex, millDecorator);
    cdMillDecorator.decorate(originalCompilationUnit, decoratedCompilationUnit);
  }

  protected ASTCDCompilationUnit getASTCD() {
    ASTService astService = new ASTService(originalCompilationUnit);
    SymbolTableService symbolTableService = new SymbolTableService(originalCompilationUnit);
    VisitorService visitorService = new VisitorService(originalCompilationUnit);
    MethodDecorator methodDecorator = new MethodDecorator(glex, astService);
    DataDecorator dataDecorator = new DataDecorator(glex, methodDecorator, astService, new DataDecoratorUtil());
    ASTSymbolDecorator astSymbolDecorator = new ASTSymbolDecorator(glex, symbolTableService);
    ASTScopeDecorator astScopeDecorator = new ASTScopeDecorator(glex, symbolTableService);
    ASTDecorator astDecorator = new ASTDecorator(glex, astService, visitorService,
        astSymbolDecorator, astScopeDecorator, methodDecorator, symbolTableService);
    ASTReferenceDecorator<ASTCDClass> astClassReferencedSymbolDecorator = new ASTReferenceDecorator<ASTCDClass>(glex, symbolTableService);
    ASTReferenceDecorator<ASTCDInterface> astInterfaceReferencedSymbolDecorator = new ASTReferenceDecorator<ASTCDInterface>(glex, symbolTableService);
    ASTFullDecorator fullDecorator = new ASTFullDecorator(dataDecorator, astDecorator, astClassReferencedSymbolDecorator);
    ASTLanguageInterfaceDecorator astLanguageInterfaceDecorator = new ASTLanguageInterfaceDecorator(glex, astService, visitorService);
    BuilderDecorator builderDecorator = new BuilderDecorator(glex, new AccessorDecorator(glex, astService), new ASTService(originalCompilationUnit));
    ASTBuilderDecorator astBuilderDecorator = new ASTBuilderDecorator(glex, builderDecorator, astService);
    ASTConstantsDecorator astConstantsDecorator = new ASTConstantsDecorator(glex, astService);
    EnumDecorator enumDecorator = new EnumDecorator(glex, new AccessorDecorator(glex, astService), astService);
    ASTInterfaceDecorator astInterfaceDecorator = new ASTInterfaceDecorator(glex, astService, visitorService,
        astSymbolDecorator, astScopeDecorator, methodDecorator);
    InterfaceDecorator dataInterfaceDecorator = new InterfaceDecorator(glex, new DataDecoratorUtil(), methodDecorator, astService);
    FullASTInterfaceDecorator fullASTInterfaceDecorator = new FullASTInterfaceDecorator(dataInterfaceDecorator, astInterfaceDecorator, astInterfaceReferencedSymbolDecorator);
    ASTCDDecorator astcdDecorator = new ASTCDDecorator(glex, fullDecorator, astLanguageInterfaceDecorator, astBuilderDecorator,
        astConstantsDecorator, enumDecorator, fullASTInterfaceDecorator);
    return astcdDecorator.decorate(originalCompilationUnit);
  }


  protected void getSymbolCD() {
    SymbolTableService symbolTableService = new SymbolTableService(originalCompilationUnit);
    VisitorService visitorService = new VisitorService(originalCompilationUnit);
    ParserService parserService = new ParserService(originalCompilationUnit);
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
    SymbolSurrogateDecorator symbolReferenceDecorator = new SymbolSurrogateDecorator(glex, symbolTableService, visitorService, methodDecorator, new MandatoryMutatorSymbolSurrogateDecorator(glex));
    SymbolSurrogateBuilderDecorator symbolReferenceBuilderDecorator = new SymbolSurrogateBuilderDecorator(glex, symbolTableService, accessorDecorator);
    CommonSymbolInterfaceDecorator commonSymbolInterfaceDecorator = new CommonSymbolInterfaceDecorator(glex, symbolTableService, visitorService, methodDecorator);
    SymbolResolverInterfaceDecorator symbolResolverInterfaceDecorator = new SymbolResolverInterfaceDecorator(glex, symbolTableService);
    SymbolDeSerDecorator symbolDeSerDecorator = new SymbolDeSerDecorator(glex, symbolTableService, new MCPath());
    ScopeDeSerDecorator scopeDeSerDecorator = new ScopeDeSerDecorator(glex, symbolTableService, methodDecorator, visitorService, new MCPath());
    Symbols2JsonDecorator symbolTablePrinterDecorator = new Symbols2JsonDecorator(glex, symbolTableService, visitorService, methodDecorator, new MCPath());
    ScopesGenitorDecorator scopesGenitorDecorator = new ScopesGenitorDecorator(glex, symbolTableService, visitorService, methodDecorator);
    ScopesGenitorDelegatorDecorator scopesGenitorDelegatorDecorator = new ScopesGenitorDelegatorDecorator(glex, symbolTableService, visitorService);

    MCPath targetPath = Mockito.mock(MCPath.class);

    SymbolTableCDDecorator symbolTableCDDecorator = new SymbolTableCDDecorator(glex, targetPath, symbolTableService, symbolDecorator,
        symbolBuilderDecorator, symbolReferenceDecorator, symbolReferenceBuilderDecorator,
        scopeInterfaceDecorator, scopeClassDecorator,
        globalScopeInterfaceDecorator, globalScopeClassDecorator,
        artifactScopeInterfaceDecorator, artifactScopeDecorator,
        commonSymbolInterfaceDecorator,  symbolResolverInterfaceDecorator,
        symbolDeSerDecorator, scopeDeSerDecorator, symbolTablePrinterDecorator, scopesGenitorDecorator, scopesGenitorDelegatorDecorator);

    // cd with no handcoded classes
    symbolTableCDDecorator.decorate(originalCompilationUnit, symbolCompilationUnit, scopeCompilationUnit, decoratedCompilationUnit);
  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(clonedCD, originalCompilationUnit);
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testPackageName() {
    Assert.assertTrue (decoratedCompilationUnit.getCDDefinition().getPackageWithName("de.monticore.codegen.symboltable.automaton").isPresent());

    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testDefinitionName() {
    assertEquals("Automaton", decoratedCompilationUnit.getCDDefinition().getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testClassSize() {
    Optional<ASTCDPackage> p = decoratedCompilationUnit.getCDDefinition().getCDPackagesList().stream()
            .filter(pp -> "de.monticore.codegen.symboltable.automaton".equals(pp.getName())).findAny();
    assertTrue (p.isPresent());
    assertEquals(1, p.get().getCDElementList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMillClass() {
    ASTCDClass automatonMill = getClassBy("AutomatonMill", decoratedCompilationUnit);
  
    assertTrue(Log.getFindings().isEmpty());
  }
}
