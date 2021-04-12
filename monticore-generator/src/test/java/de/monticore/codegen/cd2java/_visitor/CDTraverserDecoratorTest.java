/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._visitor;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.codegen.cd2java.CdUtilsPrinter;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.io.paths.IterablePath;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getClassBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getInterfaceBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CDTraverserDecoratorTest extends DecoratorTestCase {

  private ASTCDCompilationUnit originalCompilationUnit;

  private ASTCDCompilationUnit decoratedCompilationUnit;

  private ASTCDCompilationUnit visitorCompilationUnit;

  private GlobalExtensionManagement glex;

  @Before
  public void setUp() {
    LogStub.init();
    LogStub.enableFailQuick(false);
    this.glex = new GlobalExtensionManagement();

    decoratedCompilationUnit = this.parse("de", "monticore", "codegen", "ast", "Automaton");
    originalCompilationUnit = decoratedCompilationUnit.deepClone();

    IterablePath targetPath = Mockito.mock(IterablePath.class);
    VisitorService visitorService = new VisitorService(decoratedCompilationUnit);
    SymbolTableService symbolTableService = new SymbolTableService(decoratedCompilationUnit);
    this.glex.setGlobalValue("service", visitorService);
    this.glex.setGlobalValue("cdPrinter", new CdUtilsPrinter());
    TraverserInterfaceDecorator traverserInterfaceDecorator = new TraverserInterfaceDecorator(this.glex, visitorService, symbolTableService);
    MethodDecorator methodDecorator = new MethodDecorator(this.glex, visitorService);
    TraverserClassDecorator traverserClassDecorator = new TraverserClassDecorator(this.glex, visitorService, symbolTableService);
    InheritanceHandlerDecorator inheritanceHandlerDecorator = new InheritanceHandlerDecorator(this.glex, methodDecorator, visitorService, symbolTableService);
    HandlerDecorator handlerDecorator = new HandlerDecorator(this.glex, visitorService, symbolTableService);
    Visitor2Decorator visitor2Decorator = new Visitor2Decorator(this.glex, visitorService, symbolTableService);
    CDTraverserDecorator decorator = new CDTraverserDecorator(this.glex, targetPath, visitorService,
        traverserInterfaceDecorator, traverserClassDecorator, visitor2Decorator, handlerDecorator, inheritanceHandlerDecorator);
    this.visitorCompilationUnit = decorator.decorate(decoratedCompilationUnit);
  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalCompilationUnit, decoratedCompilationUnit);
  }

  @Test
  public void testClassCount() {
    assertEquals(2, visitorCompilationUnit.getCDDefinition().getCDClassesList().size());
  }

  @Test
  public void testInterfaceCount() {
    assertEquals(3, visitorCompilationUnit.getCDDefinition().getCDInterfacesList().size());
  }

  @Test
  public void testEnumEmpty() {
    assertTrue(visitorCompilationUnit.getCDDefinition().getCDEnumsList().isEmpty());
  }

  @Test
  public void testPackageChanged() {
    String packageName = visitorCompilationUnit.getPackageList().stream().reduce((a, b) -> a + "." + b).get();
    assertEquals("de.monticore.codegen.ast.automaton._visitor", packageName);
  }


  @Test
  public void testPackage() {
    List<String> expectedPackage = Arrays.asList("de", "monticore", "codegen", "ast", "automaton", "_visitor");
    assertEquals(expectedPackage, visitorCompilationUnit.getPackageList());
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    for (ASTCDClass clazz : decoratedCompilationUnit.getCDDefinition().getCDClassesList()) {
      StringBuilder sb = generatorEngine.generate(CoreTemplates.CLASS, clazz, clazz);
    }
    for (ASTCDInterface astcdInterface : decoratedCompilationUnit.getCDDefinition().getCDInterfacesList()) {
      StringBuilder sb = generatorEngine.generate(CoreTemplates.INTERFACE, astcdInterface, astcdInterface);
    }

  }

  @Test
  public void testClassNames() {
    ASTCDInterface traverserInterface = getInterfaceBy("AutomatonTraverser", visitorCompilationUnit);
    ASTCDInterface visitor2 = getInterfaceBy("AutomatonVisitor2", visitorCompilationUnit);
    ASTCDInterface handler = getInterfaceBy("AutomatonHandler", visitorCompilationUnit);
    ASTCDClass inheritanceHandler = getClassBy("AutomatonInheritanceHandler", visitorCompilationUnit);
    ASTCDClass traverserImplementation = getClassBy("AutomatonTraverserImplementation", visitorCompilationUnit);
  }

}
