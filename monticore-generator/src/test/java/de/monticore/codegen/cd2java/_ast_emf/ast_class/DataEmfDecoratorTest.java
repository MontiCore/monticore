package de.monticore.codegen.cd2java._ast_emf.ast_class;

import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._ast.ast_class.ASTService;
import de.monticore.codegen.cd2java.data.DataDecoratorUtil;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
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

    MethodDecorator methodDecorator = new MethodDecorator(glex);
    DataEmfDecorator dataEmfDecorator = new DataEmfDecorator(this.glex, methodDecorator, new ASTService(compilationUnit),
        new DataDecoratorUtil());
    this.emfClass = dataEmfDecorator.decorate(clazz);
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
    System.out.println(sb.toString());
  }


}
