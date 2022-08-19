/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._parser;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getClassBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.CdUtilsPrinter;
import de.monticore.codegen.cd2java.DecorationHelper;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.umlmodifier._ast.ASTModifier;
import de.monticore.io.paths.MCPath;

import de.se_rwth.commons.logging.LogStub;

public class ParserCDDecoratorTest extends DecoratorTestCase {

  private GlobalExtensionManagement glex;

  private ASTCDCompilationUnit decoratedASTCompilationUnit;

  private ASTCDCompilationUnit originalASTCompilationUnit;

  private ASTCDCompilationUnit parserCD;

  private ASTCDCompilationUnit parserCDComponent;

  @Before
  public void setUp() {
    // to be issued (the warnings are not checked)
    LogStub.init();         // replace log by a sideffect free variant
//     LogStub.initPlusLog();  // for manual testing purpose only
//    Log.enableFailQuick(false);
    this.glex = new GlobalExtensionManagement();
    MCPath targetPath = Mockito.mock(MCPath.class);

    this.glex.setGlobalValue("astHelper", DecorationHelper.getInstance());
    this.glex.setGlobalValue("cdPrinter", new CdUtilsPrinter());
    decoratedASTCompilationUnit = this.parse("de", "monticore", "codegen", "symboltable", "Automaton");
    originalASTCompilationUnit = decoratedASTCompilationUnit.deepClone();

    this.glex.setGlobalValue("service", new AbstractService(decoratedASTCompilationUnit));

    ParserService service = new ParserService(decoratedASTCompilationUnit);

    ParserClassDecorator parserClassDecorator = new ParserClassDecorator(glex, service);
    ParserForSuperDecorator parserForSuperDecorator = new ParserForSuperDecorator(glex, service);
    ParserCDDecorator parserCDDecorator = new ParserCDDecorator(glex, parserClassDecorator, parserForSuperDecorator, service);

    this.parserCD = parserCDDecorator.decorate(decoratedASTCompilationUnit);

    ParserService mockService = Mockito.spy(new ParserService(decoratedASTCompilationUnit));
    parserClassDecorator = new ParserClassDecorator(glex, mockService);
    parserForSuperDecorator = new ParserForSuperDecorator(glex, mockService);
    ParserCDDecorator mockDecorator = new ParserCDDecorator(glex, parserClassDecorator, parserForSuperDecorator, mockService);
    Mockito.doReturn(false).when(mockService).hasStartProd(Mockito.any(ASTCDDefinition.class));
    Mockito.doReturn(true).when(mockService).hasComponentStereotype(Mockito.any(ASTModifier.class));
    this.parserCDComponent = mockDecorator.decorate(decoratedASTCompilationUnit);
  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalASTCompilationUnit, decoratedASTCompilationUnit);
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testCDName() {
    assertEquals("Automaton", parserCD.getCDDefinition().getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testClassCount() {
    assertEquals(1, parserCD.getCDDefinition().getCDClassesList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testClasses(){
    ASTCDClass automatonParser = getClassBy("AutomatonParser", parserCD);
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testNoEnum() {
    assertTrue(parserCD.getCDDefinition().getCDEnumsList().isEmpty());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testNoInterface(){
    assertTrue(parserCD.getCDDefinition().getCDInterfacesList().isEmpty());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testPackage() {
    assertEquals(6, parserCD.getCDPackageList().size());
    assertEquals("de", parserCD.getCDPackageList().get(0));
    assertEquals("monticore", parserCD.getCDPackageList().get(1));
    assertEquals("codegen", parserCD.getCDPackageList().get(2));
    assertEquals("symboltable", parserCD.getCDPackageList().get(3));
    assertEquals("automaton", parserCD.getCDPackageList().get(4));
    assertEquals("_parser", parserCD.getCDPackageList().get(5));
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testImports() {
    assertEquals(0, parserCD.getMCImportStatementList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testCDNameComponent() {
    assertEquals("Automaton", parserCDComponent.getCDDefinition().getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testNoClassesComponent() {
    assertTrue(parserCDComponent.getCDDefinition().getCDClassesList().isEmpty());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testNoEnumComponent(){
    assertTrue(parserCDComponent.getCDDefinition().getCDEnumsList().isEmpty());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testNoInterfaceComponent(){
    assertTrue(parserCDComponent.getCDDefinition().getCDInterfacesList().isEmpty());
  
    assertTrue(Log.getFindings().isEmpty());
  }
}
