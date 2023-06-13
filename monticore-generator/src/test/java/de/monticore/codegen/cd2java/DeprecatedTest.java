/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java;

import de.monticore.cd.codegen.CdUtilsPrinter;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cdbasis._ast.*;
import de.monticore.cdinterfaceandenum._ast.*;
import de.monticore.codegen.cd2java._ast.ast_class.ASTDecorator;
import de.monticore.codegen.cd2java._ast.ast_class.ASTScopeDecorator;
import de.monticore.codegen.cd2java._ast.ast_class.ASTService;
import de.monticore.codegen.cd2java._ast.ast_class.ASTSymbolDecorator;
import de.monticore.codegen.cd2java._ast.ast_interface.ASTInterfaceDecorator;
import de.monticore.codegen.cd2java._ast.builder.ASTBuilderDecorator;
import de.monticore.codegen.cd2java._ast.builder.BuilderDecorator;
import de.monticore.codegen.cd2java._ast.enums.EnumDecorator;
import de.monticore.codegen.cd2java._cocos.CoCoInterfaceDecorator;
import de.monticore.codegen.cd2java._cocos.CoCoService;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._symboltable.symbol.*;
import de.monticore.codegen.cd2java._symboltable.symbol.symbolsurrogatemutator.MandatoryMutatorSymbolSurrogateDecorator;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.cd2java.methods.AccessorDecorator;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.MCTypeFacade;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static de.monticore.codegen.cd2java.DecoratorTestUtil.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class
DeprecatedTest extends DecoratorTestCase {

  private GlobalExtensionManagement glex = new GlobalExtensionManagement();

  private MCTypeFacade mcTypeFacade = MCTypeFacade.getInstance();

  private ASTCDClass clazzA;
  private ASTCDInterface interfaceI;
  private ASTCDEnum enumE;

  private ASTDecorator astDecorator;
  private ASTBuilderDecorator astBuilderDecorator;
  private CoCoInterfaceDecorator coCoInterfaceDecorator;
  private SymbolDecorator symbolDecorator;
  private SymbolBuilderDecorator symbolBuilderDecorator;
  private SymbolSurrogateDecorator symbolSurrogateDecorator;
  private SymbolSurrogateBuilderDecorator symbolSurrogateBuilderDecorator;
  private SymbolResolverInterfaceDecorator symbolResolverInterfaceDecorator;
  private EnumDecorator enumDecorator;
  private ASTInterfaceDecorator astInterfaceDecorator;

  private ASTCDCompilationUnit compilationUnit;

  @Before
  public void setup() {
    compilationUnit = this.parse("de", "monticore", "codegen", "deprecated", "DeprecatedProds");

    this.glex.setGlobalValue("service", new AbstractService(compilationUnit));
    this.glex.setGlobalValue("astHelper", DecorationHelper.getInstance());
    this.glex.setGlobalValue("cdPrinter", new CdUtilsPrinter());

    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    CD4C.init(generatorSetup);

    clazzA = getClassBy("A", compilationUnit);
    interfaceI = getInterfaceBy("I", compilationUnit);
    enumE = getEnumBy("E", compilationUnit);

    SymbolTableService symbolTableService = new SymbolTableService(compilationUnit);
    ASTService astService = new ASTService(compilationUnit);
    VisitorService visitorService = new VisitorService(compilationUnit);
    MethodDecorator methodDecorator = new MethodDecorator(glex, astService);
    ASTSymbolDecorator astSymbolDecorator = new ASTSymbolDecorator(glex, symbolTableService);
    ASTScopeDecorator astScopeDecorator = new ASTScopeDecorator(glex, symbolTableService);
    astDecorator = new ASTDecorator(this.glex, astService, visitorService,
        astSymbolDecorator, astScopeDecorator, methodDecorator,
        symbolTableService);

    AccessorDecorator accessorDecorator = new AccessorDecorator(glex, astService);
    BuilderDecorator builderDecorator = new BuilderDecorator(glex, accessorDecorator, astService);
    astBuilderDecorator = new ASTBuilderDecorator(glex, builderDecorator, astService);

    coCoInterfaceDecorator = new CoCoInterfaceDecorator(glex, new CoCoService(compilationUnit), astService);

    symbolDecorator = new SymbolDecorator(this.glex, symbolTableService, visitorService, methodDecorator);

    symbolBuilderDecorator = new SymbolBuilderDecorator(glex, symbolTableService, builderDecorator);

    symbolSurrogateDecorator = new SymbolSurrogateDecorator(this.glex, symbolTableService, methodDecorator,
        new MandatoryMutatorSymbolSurrogateDecorator(glex));

    symbolSurrogateBuilderDecorator = new SymbolSurrogateBuilderDecorator(this.glex, symbolTableService,
        new AccessorDecorator(glex, symbolTableService));

    symbolResolverInterfaceDecorator = new SymbolResolverInterfaceDecorator(this.glex, symbolTableService);

    astInterfaceDecorator = new ASTInterfaceDecorator(glex, astService, visitorService, astSymbolDecorator, astScopeDecorator, methodDecorator);

    enumDecorator = new EnumDecorator(glex, accessorDecorator, astService);
  }

  @Test
  public void testASTADeprecated() {
    ASTCDClass changedClass = CD4AnalysisMill.cDClassBuilder().setName(clazzA.getName())
        .setModifier(clazzA.getModifier())
        .build();
    ASTCDClass astA = astDecorator.decorate(clazzA, changedClass);
    assertTrue(astA.getModifier().isPresentStereotype());

    assertEquals(2, astA.getModifier().getStereotype().sizeValues());
    assertEquals("deprecated", astA.getModifier().getStereotype().getValues(0).getName());
    assertFalse(astA.getModifier().getStereotype().getValues(0).getValue().isEmpty());
    assertEquals("CommentA", astA.getModifier().getStereotype().getValues(0).getValue());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testASTABuilderDeprecated() {
    ASTCDClass astABuilder = astBuilderDecorator.decorate(clazzA);

    assertTrue(astABuilder.getModifier().isPresentStereotype());

    assertEquals(1, astABuilder.getModifier().getStereotype().sizeValues());
    assertEquals("deprecated", astABuilder.getModifier().getStereotype().getValues(0).getName());
    assertFalse(astABuilder.getModifier().getStereotype().getValues(0).getValue().isEmpty());
    assertEquals("CommentA", astABuilder.getModifier().getStereotype().getValues(0).getValue());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testACoCoInterfaceDeprecated() {
    List<ASTCDInterface> cocoInterfaceList = coCoInterfaceDecorator.decorate(compilationUnit.getCDDefinition());
    Optional<ASTCDInterface> astaCoCo = cocoInterfaceList.stream().filter(c -> c.getName().equals("DeprecatedProdsACoCo")).findFirst();
    assertTrue(astaCoCo.isPresent());
    assertTrue(astaCoCo.get().getModifier().isPresentStereotype());

    assertEquals(1, astaCoCo.get().getModifier().getStereotype().sizeValues());
    assertEquals("deprecated", astaCoCo.get().getModifier().getStereotype().getValues(0).getName());
    assertFalse(astaCoCo.get().getModifier().getStereotype().getValues(0).getValue().isEmpty());
    assertEquals("CommentA", astaCoCo.get().getModifier().getStereotype().getValues(0).getValue());
  
    assertTrue(Log.getFindings().isEmpty());
  }


  @Test
  public void testASymbolDeprecated() {
    ASTCDClass symbolA = symbolDecorator.decorate(clazzA);
    assertTrue(symbolA.getModifier().isPresentStereotype());

    assertEquals(1, symbolA.getModifier().getStereotype().sizeValues());
    assertEquals("deprecated", symbolA.getModifier().getStereotype().getValues(0).getName());
    assertFalse(symbolA.getModifier().getStereotype().getValues(0).getValue().isEmpty());
    assertEquals("CommentA", symbolA.getModifier().getStereotype().getValues(0).getValue());
  
    assertTrue(Log.getFindings().isEmpty());
  }


  @Test
  public void testASymbolBuilderDeprecated() {
    ASTCDClass symbolBuilderA = symbolBuilderDecorator.decorate(clazzA);
    assertTrue(symbolBuilderA.getModifier().isPresentStereotype());

    assertEquals(1, symbolBuilderA.getModifier().getStereotype().sizeValues());
    assertEquals("deprecated", symbolBuilderA.getModifier().getStereotype().getValues(0).getName());
    assertFalse(symbolBuilderA.getModifier().getStereotype().getValues(0).getValue().isEmpty());
    assertEquals("CommentA", symbolBuilderA.getModifier().getStereotype().getValues(0).getValue());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testASymbolLoaderDeprecated() {
    ASTCDClass symbolLoaderA = symbolSurrogateDecorator.decorate(clazzA);
    assertTrue(symbolLoaderA.getModifier().isPresentStereotype());

    assertEquals(1, symbolLoaderA.getModifier().getStereotype().sizeValues());
    assertEquals("deprecated", symbolLoaderA.getModifier().getStereotype().getValues(0).getName());
    assertFalse(symbolLoaderA.getModifier().getStereotype().getValues(0).getValue().isEmpty());
    assertEquals("CommentA", symbolLoaderA.getModifier().getStereotype().getValues(0).getValue());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testASymbolLoaderBuilderDeprecated() {
    ASTCDClass symbolLoaderBuilderA = symbolSurrogateBuilderDecorator.decorate(clazzA);
    assertTrue(symbolLoaderBuilderA.getModifier().isPresentStereotype());

    assertEquals(1, symbolLoaderBuilderA.getModifier().getStereotype().sizeValues());
    assertEquals("deprecated", symbolLoaderBuilderA.getModifier().getStereotype().getValues(0).getName());
    assertFalse(symbolLoaderBuilderA.getModifier().getStereotype().getValues(0).getValue().isEmpty());
    assertEquals("CommentA", symbolLoaderBuilderA.getModifier().getStereotype().getValues(0).getValue());
  
    assertTrue(Log.getFindings().isEmpty());
  }


  @Test
  public void testASymbolResolverInterfaceDeprecated() {
    ASTCDInterface resolverA = symbolResolverInterfaceDecorator.decorate(clazzA);
    assertTrue(resolverA.getModifier().isPresentStereotype());

    assertEquals(1, resolverA.getModifier().getStereotype().sizeValues());
    assertEquals("deprecated", resolverA.getModifier().getStereotype().getValues(0).getName());
    assertFalse(resolverA.getModifier().getStereotype().getValues(0).getValue().isEmpty());
    assertEquals("CommentA", resolverA.getModifier().getStereotype().getValues(0).getValue());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testASTIDeprecated() {
    ASTCDInterface changedClass = CD4AnalysisMill.cDInterfaceBuilder().setName(interfaceI.getName())
        .setModifier(interfaceI.getModifier())
        .build();
    ASTCDInterface astA = astInterfaceDecorator.decorate(interfaceI, changedClass);
    assertTrue(astA.getModifier().isPresentStereotype());

    assertEquals(2, astA.getModifier().getStereotype().sizeValues());
    assertEquals("deprecated", astA.getModifier().getStereotype().getValues(0).getName());
    assertFalse(astA.getModifier().getStereotype().getValues(0).getValue().isEmpty());
    assertEquals("CommentI", astA.getModifier().getStereotype().getValues(0).getValue());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testICoCoInterfaceDeprecated() {
    List<ASTCDInterface> cocoInterfaceList = coCoInterfaceDecorator.decorate(compilationUnit.getCDDefinition());
    Optional<ASTCDInterface> astaCoCo = cocoInterfaceList.stream().filter(c -> c.getName().equals("DeprecatedProdsICoCo")).findFirst();
    assertTrue(astaCoCo.isPresent());
    assertTrue(astaCoCo.get().getModifier().isPresentStereotype());

    assertEquals(1, astaCoCo.get().getModifier().getStereotype().sizeValues());
    assertEquals("deprecated", astaCoCo.get().getModifier().getStereotype().getValues(0).getName());
    assertFalse(astaCoCo.get().getModifier().getStereotype().getValues(0).getValue().isEmpty());
    assertEquals("CommentI", astaCoCo.get().getModifier().getStereotype().getValues(0).getValue());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testISymbolLoaderBuilderDeprecated() {
    ASTCDClass symbolLoaderBuilderA = symbolSurrogateBuilderDecorator.decorate(interfaceI);
    assertTrue(symbolLoaderBuilderA.getModifier().isPresentStereotype());

    assertEquals(1, symbolLoaderBuilderA.getModifier().getStereotype().sizeValues());
    assertEquals("deprecated", symbolLoaderBuilderA.getModifier().getStereotype().getValues(0).getName());
    assertFalse(symbolLoaderBuilderA.getModifier().getStereotype().getValues(0).getValue().isEmpty());
    assertEquals("CommentI", symbolLoaderBuilderA.getModifier().getStereotype().getValues(0).getValue());
  
    assertTrue(Log.getFindings().isEmpty());
  }


  @Test
  public void testISymbolResolverInterfaceDeprecated() {
    ASTCDInterface resolverA = symbolResolverInterfaceDecorator.decorate(interfaceI);
    assertTrue(resolverA.getModifier().isPresentStereotype());

    assertEquals(1, resolverA.getModifier().getStereotype().sizeValues());
    assertEquals("deprecated", resolverA.getModifier().getStereotype().getValues(0).getName());
    assertFalse(resolverA.getModifier().getStereotype().getValues(0).getValue().isEmpty());
    assertEquals("CommentI", resolverA.getModifier().getStereotype().getValues(0).getValue());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testASTEDeprecated() {
    ASTCDEnum astcdEnum = enumDecorator.decorate(enumE);
    assertTrue(astcdEnum.getModifier().isPresentStereotype());

    assertEquals(1, astcdEnum.getModifier().getStereotype().sizeValues());
    assertEquals("deprecated", astcdEnum.getModifier().getStereotype().getValues(0).getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }
}
