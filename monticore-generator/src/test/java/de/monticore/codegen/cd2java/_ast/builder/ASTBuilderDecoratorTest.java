/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast.builder;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import de.monticore.cd.codegen.CD2JavaTemplates;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._ast.ast_class.ASTService;
import de.monticore.codegen.cd2java.methods.AccessorDecorator;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getClassBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ASTBuilderDecoratorTest extends DecoratorTestCase {

  private ASTCDClass builderClass;

  private ASTCDCompilationUnit decoratedCompilationUnit;

  private ASTCDCompilationUnit originalCompilationUnit;

  @Before
  public void setup() {
    decoratedCompilationUnit = parse("de", "monticore", "codegen", "ast", "Builder");
    originalCompilationUnit = decoratedCompilationUnit.deepClone();

    ASTCDClass cdClass = getClassBy("A", decoratedCompilationUnit);
    this.glex.setGlobalValue("service", new AbstractService(decoratedCompilationUnit));

    AccessorDecorator methodDecorator = new AccessorDecorator(glex, new ASTService(decoratedCompilationUnit));
    BuilderDecorator builderDecorator = new BuilderDecorator(glex, methodDecorator, new ASTService(decoratedCompilationUnit));
    ASTBuilderDecorator builderASTNodeDecorator = new ASTBuilderDecorator(glex, builderDecorator, new ASTService(decoratedCompilationUnit));
    this.builderClass = builderASTNodeDecorator.decorate(cdClass);
  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalCompilationUnit, decoratedCompilationUnit);
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testClassName() {
    assertEquals("ABuilder", builderClass.getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSuperClassName() {
    assertEquals("de.monticore.ast.ASTNodeBuilder<ABuilder>", builderClass.printSuperclasses());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMethods() {
    assertEquals(37, builderClass.getCDMethodList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    CD4C.init(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CD2JavaTemplates.CLASS, builderClass, packageDir);
    // test parsing
    ParserConfiguration configuration = new ParserConfiguration();
    JavaParser parser = new JavaParser(configuration);
    ParseResult parseResult = parser.parse(sb.toString());
    assertTrue(parseResult.isSuccessful());
  
    assertTrue(Log.getFindings().isEmpty());
  }
}
