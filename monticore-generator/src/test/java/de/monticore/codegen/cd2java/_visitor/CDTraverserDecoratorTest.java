/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._visitor;

import de.monticore.cd.codegen.CD2JavaTemplates;
import de.monticore.cd.codegen.CdUtilsPrinter;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.codegen.cd2java.DecorationHelper;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import static de.monticore.codegen.cd2java.DecoratorTestUtil.getClassBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getInterfaceBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CDTraverserDecoratorTest extends DecoratorTestCase {

  private ASTCDCompilationUnit originalCompilationUnit;

  private ASTCDCompilationUnit decoratedCompilationUnit;

  private GlobalExtensionManagement glex;

  @Before
  public void setUp() {
    this.glex = new GlobalExtensionManagement();

    originalCompilationUnit = this.parse("de", "monticore", "codegen", "ast", "Automaton");

    MCPath targetPath = Mockito.mock(MCPath.class);
    VisitorService visitorService = new VisitorService(originalCompilationUnit);
    SymbolTableService symbolTableService = new SymbolTableService(originalCompilationUnit);
    this.glex.setGlobalValue("service", visitorService);
    this.glex.setGlobalValue("cdPrinter", new CdUtilsPrinter());
    this.glex.setGlobalValue("astHelper", DecorationHelper.getInstance());
    TraverserInterfaceDecorator traverserInterfaceDecorator = new TraverserInterfaceDecorator(this.glex, visitorService, symbolTableService);
    MethodDecorator methodDecorator = new MethodDecorator(this.glex, visitorService);
    TraverserClassDecorator traverserClassDecorator = new TraverserClassDecorator(this.glex, visitorService, symbolTableService);
    InheritanceHandlerDecorator inheritanceHandlerDecorator = new InheritanceHandlerDecorator(this.glex, methodDecorator, visitorService, symbolTableService);
    HandlerDecorator handlerDecorator = new HandlerDecorator(this.glex, visitorService, symbolTableService);
    Visitor2Decorator visitor2Decorator = new Visitor2Decorator(this.glex, visitorService, symbolTableService);
    CDTraverserDecorator decorator = new CDTraverserDecorator(this.glex, targetPath, visitorService,
        traverserInterfaceDecorator, traverserClassDecorator, visitor2Decorator, handlerDecorator, inheritanceHandlerDecorator);
    this.decoratedCompilationUnit = createEmptyCompilationUnit(originalCompilationUnit);
    decorator.decorate(originalCompilationUnit, decoratedCompilationUnit);
  }

  @Test
  public void testClassCount() {
    assertEquals(2, decoratedCompilationUnit.getCDDefinition().getCDClassesList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testInterfaceCount() {
    assertEquals(3, decoratedCompilationUnit.getCDDefinition().getCDInterfacesList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testEnumEmpty() {
    assertTrue(decoratedCompilationUnit.getCDDefinition().getCDEnumsList().isEmpty());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testPackage() {
    assertTrue (decoratedCompilationUnit.getCDDefinition().getPackageWithName("de.monticore.codegen.ast.automaton._visitor").isPresent());
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    CD4C.init(generatorSetup);
    for (ASTCDClass clazz : decoratedCompilationUnit.getCDDefinition().getCDClassesList()) {
      StringBuilder sb = generatorEngine.generate(CD2JavaTemplates.CLASS, clazz, packageDir);
    }
    for (ASTCDInterface astcdInterface : decoratedCompilationUnit.getCDDefinition().getCDInterfacesList()) {
      StringBuilder sb = generatorEngine.generate(CD2JavaTemplates.INTERFACE, astcdInterface, packageDir);
    }
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testClassNames() {
    ASTCDInterface traverserInterface = getInterfaceBy("AutomatonTraverser", decoratedCompilationUnit);
    ASTCDInterface visitor2 = getInterfaceBy("AutomatonVisitor2", decoratedCompilationUnit);
    ASTCDInterface handler = getInterfaceBy("AutomatonHandler", decoratedCompilationUnit);
    ASTCDClass inheritanceHandler = getClassBy("AutomatonInheritanceHandler", decoratedCompilationUnit);
    ASTCDClass traverserImplementation = getClassBy("AutomatonTraverserImplementation", decoratedCompilationUnit);
  
    assertTrue(Log.getFindings().isEmpty());
  }

}
