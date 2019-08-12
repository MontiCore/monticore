package de.monticore.codegen.cd2java._ast_emf.ast_class;

import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.CD4AnalysisMill;
import de.monticore.cd.prettyprint.CD4CodePrinter;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._ast.ast_class.ASTService;
import de.monticore.codegen.cd2java._ast_emf.ast_class.emfMutatorMethodDecorator.EmfMutatorDecorator;
import de.monticore.codegen.cd2java.data.DataDecoratorUtil;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import org.junit.Before;
import org.junit.Test;

import static de.monticore.codegen.cd2java.DecoratorTestUtil.getClassBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DataEmfDecoratorTest extends DecoratorTestCase {

  private GlobalExtensionManagement glex = new GlobalExtensionManagement();

  private ASTCDClass emfClass;

  @Before
  public void setup() {
    ASTCDCompilationUnit compilationUnit = this.parse("de", "monticore", "codegen", "ast", "Automaton");

    ASTCDClass clazz = getClassBy("ASTAutomaton", compilationUnit);
    this.glex.setGlobalValue("service", new AbstractService(compilationUnit));
    this.glex.setGlobalValue("astHelper", new DecorationHelper());
    this.glex.setGlobalValue("cdPrinter", new CD4CodePrinter());

    MethodDecorator methodDecorator = new MethodDecorator(glex);
    EmfMutatorDecorator emfMutatorDecorator= new EmfMutatorDecorator(glex, new ASTService(compilationUnit));

    DataEmfDecorator dataEmfDecorator = new DataEmfDecorator(this.glex, methodDecorator, new ASTService(compilationUnit),
        new DataDecoratorUtil(), emfMutatorDecorator);
    ASTCDClass changedClass = CD4AnalysisMill.cDClassBuilder().setName(clazz.getName())
        .setModifier(clazz.getModifier())
        .build();
    this.emfClass = dataEmfDecorator.decorate(clazz, changedClass);
  }

  @Test
  public void testClassSignature() {
    assertEquals("ASTAutomaton", emfClass.getName());
  }

  @Test
  public void testAttributes() {
    assertEquals(3, emfClass.sizeCDAttributes());
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.CLASS, emfClass, emfClass);
    //check if list types where changed
    assertTrue(sb.toString().contains("EObjectContainmentEList"));
    // TODO Check System.out.println(sb.toString());
  }
}
