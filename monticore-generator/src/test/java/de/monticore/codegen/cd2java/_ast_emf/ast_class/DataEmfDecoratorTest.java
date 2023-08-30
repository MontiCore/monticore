/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast_emf.ast_class;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import de.monticore.cd.codegen.CD2JavaTemplates;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._ast.ast_class.ASTService;
import de.monticore.codegen.cd2java._ast_emf.ast_class.mutatordecorator.EmfMutatorDecorator;
import de.monticore.codegen.cd2java.data.DataDecoratorUtil;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import static de.monticore.codegen.cd2java.DecoratorTestUtil.getClassBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DataEmfDecoratorTest extends DecoratorTestCase {

  private ASTCDClass emfClass;

  @Before
  public void setup() {
    ASTCDCompilationUnit compilationUnit = this.parse("de", "monticore", "codegen", "_ast_emf", "Automata");

    ASTCDClass clazz = getClassBy("ASTAutomaton", compilationUnit);
    this.glex.setGlobalValue("service", new AbstractService(compilationUnit));

    MethodDecorator methodDecorator = new MethodDecorator(glex, new ASTService(compilationUnit));
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
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testAttributes() {
    assertEquals(3, emfClass.getCDAttributeList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    CD4C.init(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CD2JavaTemplates.CLASS, emfClass, packageDir);
    //check if list types where changed
    assertTrue(sb.toString().contains("EObjectContainmentEList"));
    // test parsing
    ParserConfiguration configuration = new ParserConfiguration();
    JavaParser parser = new JavaParser(configuration);
    ParseResult parseResult = parser.parse(sb.toString());
    assertTrue(parseResult.isSuccessful());
  
    assertTrue(Log.getFindings().isEmpty());
  }
}
