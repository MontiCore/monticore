/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast_emf.ast_class;

import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.CD4AnalysisMill;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._ast.ast_class.ASTScopeDecorator;
import de.monticore.codegen.cd2java._ast.ast_class.ASTService;
import de.monticore.codegen.cd2java._ast.ast_class.ASTSymbolDecorator;
import de.monticore.codegen.cd2java._ast.ast_class.reference.ASTReferenceDecorator;
import de.monticore.codegen.cd2java._ast.factory.NodeFactoryService;
import de.monticore.codegen.cd2java._ast_emf.EmfService;
import de.monticore.codegen.cd2java._ast_emf.ast_class.emfMutatorMethodDecorator.EmfMutatorDecorator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.cd2java.data.DataDecoratorUtil;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import org.junit.Before;
import org.junit.Test;

import static de.monticore.codegen.cd2java.DecoratorTestUtil.getClassBy;
import static org.junit.Assert.*;

public class ASTFullEmfDecoratorTest extends DecoratorTestCase {

  private GlobalExtensionManagement glex = new GlobalExtensionManagement();

  private ASTCDClass astClass;

  private ASTCDCompilationUnit decoratedCompilationUnit;

  private ASTCDCompilationUnit originalCompilationUnit;


  @Before
  public void setup() {
    this.glex.setGlobalValue("astHelper", new DecorationHelper());
    decoratedCompilationUnit = this.parse("de", "monticore", "codegen", "ast", "Automaton");
    originalCompilationUnit = decoratedCompilationUnit.deepClone();
    ASTCDClass clazz = getClassBy("ASTAutomaton", decoratedCompilationUnit);

    ASTService astService = new ASTService(decoratedCompilationUnit);
    SymbolTableService symbolTableService = new SymbolTableService(decoratedCompilationUnit);
    VisitorService visitorService = new VisitorService(decoratedCompilationUnit);
    NodeFactoryService nodeFactoryService = new NodeFactoryService(decoratedCompilationUnit);
    ASTSymbolDecorator astSymbolDecorator = new ASTSymbolDecorator(glex, symbolTableService);
    ASTScopeDecorator astScopeDecorator = new ASTScopeDecorator(glex,  symbolTableService);
    EmfMutatorDecorator emfMutatorDecorator= new EmfMutatorDecorator(glex, astService);
    DataEmfDecorator dataEmfDecorator = new DataEmfDecorator(glex, new MethodDecorator(glex),
        new ASTService(decoratedCompilationUnit), new DataDecoratorUtil(), emfMutatorDecorator);
    ASTEmfDecorator astEmfDecorator = new ASTEmfDecorator(glex, astService, visitorService, nodeFactoryService,
        astSymbolDecorator, astScopeDecorator, new MethodDecorator(glex), symbolTableService, new EmfService(decoratedCompilationUnit));
    ASTReferenceDecorator astReferencedSymbolDecorator = new ASTReferenceDecorator(glex, symbolTableService);
    ASTFullEmfDecorator fullDecorator = new ASTFullEmfDecorator(dataEmfDecorator, astEmfDecorator, astReferencedSymbolDecorator);
    ASTCDClass changedClass = CD4AnalysisMill.cDClassBuilder().setName(clazz.getName())
        .setModifier(clazz.getModifier())
        .build();
    this.astClass = fullDecorator.decorate(clazz, changedClass);
  }

  @Test
  public void testCompilationCopy() {
    assertNotEquals(originalCompilationUnit, decoratedCompilationUnit);
  }


  @Test
  public void testClassName() {
    assertEquals("ASTAutomaton", astClass.getName());
  }

  @Test
  public void testAttributeSize() {
    assertEquals(7, astClass.getCDAttributeList().size());
  }

  @Test
  public void testConstructorSize() {
    assertEquals(1, astClass.getCDConstructorList().size());
  }

  @Test
  public void testMethodSize() {
    assertFalse(astClass.getCDMethodList().isEmpty());
    assertEquals(107, astClass.getCDMethodList().size());
  }

}
