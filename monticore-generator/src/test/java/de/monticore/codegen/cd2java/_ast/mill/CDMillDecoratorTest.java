/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast.mill;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.google.common.collect.Lists;
import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.cd.prettyprint.CD4CodePrinter;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecorationHelper;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._ast.ast_class.ASTService;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._visitor.*;
import de.monticore.codegen.cd2java._visitor.builder.DelegatorVisitorBuilderDecorator;
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
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getClassBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodBy;
import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.AST_PACKAGE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CDMillDecoratorTest extends DecoratorTestCase {

  private ASTCDCompilationUnit millCD;

  private GlobalExtensionManagement glex;

  private ASTCDCompilationUnit originalCompilationUnit;

  private ASTCDCompilationUnit decoratedCompilationUnit;

  @Before
  public void setUp() {
    LogStub.init();
    LogStub.enableFailQuick(false);
    this.glex = new GlobalExtensionManagement();

    this.glex.setGlobalValue("astHelper", DecorationHelper.getInstance());
    this.glex.setGlobalValue("cdPrinter", new CD4CodePrinter());
    decoratedCompilationUnit = this.parse("de", "monticore", "codegen", "ast", "Automaton");
    originalCompilationUnit = decoratedCompilationUnit.deepClone();
    this.glex.setGlobalValue("service", new VisitorService(decoratedCompilationUnit));

    ASTService astService = new ASTService(decoratedCompilationUnit);
    MillForSuperDecorator forSuperDecorator = new MillForSuperDecorator(this.glex, new ASTService(decoratedCompilationUnit));
    MillDecorator millDecorator = new MillDecorator(this.glex, astService);

    // create ast package
    ASTCDCompilationUnit astCD = decoratedCompilationUnit.deepClone();
    astCD.addPackage(astCD.getCDDefinition().getName().toLowerCase());
    astCD.addPackage(AST_PACKAGE);

    CDMillDecorator cdMillDecorator = new CDMillDecorator(this.glex, millDecorator, forSuperDecorator);
    this.millCD = cdMillDecorator.decorate(Lists.newArrayList(astCD, getVisitorCD()));
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

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalCompilationUnit, decoratedCompilationUnit);
  }

  @Test
  public void testPackageName() {
    assertEquals(5, millCD.sizePackages());
    assertEquals("de", millCD.getPackage(0));
    assertEquals("monticore", millCD.getPackage(1));
    assertEquals("codegen", millCD.getPackage(2));
    assertEquals("ast", millCD.getPackage(3));
    assertEquals("automaton", millCD.getPackage(4));
  }

  @Test
  public void testDefinitionName() {
    assertEquals("Automaton", millCD.getCDDefinition().getName());
  }

  @Test
  public void testClassSize() {
    assertEquals(2, millCD.getCDDefinition().sizeCDClasss());
  }

  @Test
  public void testMillClass() {
    ASTCDClass automatonMill = getClassBy("AutomatonMill", millCD);
  }

  @Test
  public void testMillForSuperClass() {
    ASTCDClass automatonMill = getClassBy("LexicalsMillForAutomaton", millCD);
  }
}
