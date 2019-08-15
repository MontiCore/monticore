package de.monticore.codegen.cd2java._visitor;

import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.ASTCDInterface;
import de.monticore.cd.prettyprint.CD4CodePrinter;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._visitor.visitor_interface.ASTVisitorDecorator;
import de.monticore.codegen.cd2java._visitor.visitor_interface.SymbolVisitorDecorator;
import de.monticore.codegen.cd2java._visitor.visitor_interface.VisitorInterfaceDecorator;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CDVisitorDecoratorTest extends DecoratorTestCase {
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

    this.glex.setGlobalValue("service", new VisitorService(decoratedCompilationUnit));
    this.glex.setGlobalValue("cdPrinter", new CD4CodePrinter());

    ASTVisitorDecorator astVisitorDecorator = new ASTVisitorDecorator(this.glex,
        new VisitorInterfaceDecorator(this.glex, new VisitorService(decoratedCompilationUnit)),
        new VisitorService(decoratedCompilationUnit));
    SymbolVisitorDecorator symbolVisitorDecorator = new SymbolVisitorDecorator(this.glex,
        new VisitorInterfaceDecorator(this.glex, new VisitorService(decoratedCompilationUnit)),
        new VisitorService(decoratedCompilationUnit), new SymbolTableService(decoratedCompilationUnit));
    ScopeVisitorDecorator scopeVisitorDecorator = new ScopeVisitorDecorator(this.glex,
        new VisitorService(decoratedCompilationUnit), new SymbolTableService(decoratedCompilationUnit));
    DelegatorVisitorDecorator delegatorVisitorDecorator = new DelegatorVisitorDecorator(this.glex,
        new VisitorService(decoratedCompilationUnit));
    ParentAwareVisitorDecorator parentAwareVisitorDecorator = new ParentAwareVisitorDecorator(this.glex,
        new VisitorService(decoratedCompilationUnit));
    InheritanceVisitorDecorator inheritanceVisitorDecorator = new InheritanceVisitorDecorator(this.glex,
        new VisitorService(decoratedCompilationUnit));

    CDVisitorDecorator decorator = new CDVisitorDecorator(this.glex,
        astVisitorDecorator, symbolVisitorDecorator, scopeVisitorDecorator,
        delegatorVisitorDecorator, inheritanceVisitorDecorator, parentAwareVisitorDecorator);

    this.visitorCompilationUnit = decorator.decorate(decoratedCompilationUnit);
  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalCompilationUnit, decoratedCompilationUnit);
  }

  @Test
  public void testClassCount() {
    assertEquals(2, visitorCompilationUnit.getCDDefinition().getCDClassList().size());
  }

  @Test
  public void testInterfaceCount() {
    assertEquals(4, visitorCompilationUnit.getCDDefinition().getCDInterfaceList().size());
  }

  @Test
  public void testEnumEmpty() {
    assertTrue(visitorCompilationUnit.getCDDefinition().isEmptyCDEnums());
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
    for (ASTCDClass clazz : decoratedCompilationUnit.getCDDefinition().getCDClassList()) {
      StringBuilder sb = generatorEngine.generate(CoreTemplates.CLASS, clazz, clazz);
    }
    for (ASTCDInterface astcdInterface : decoratedCompilationUnit.getCDDefinition().getCDInterfaceList()) {
      StringBuilder sb = generatorEngine.generate(CoreTemplates.INTERFACE, astcdInterface, astcdInterface);
    }
  }

}
