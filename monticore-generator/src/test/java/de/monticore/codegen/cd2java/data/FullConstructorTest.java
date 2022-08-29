/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.data;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.CdUtilsPrinter;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecorationHelper;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._ast.ast_class.ASTService;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import static de.monticore.codegen.cd2java.CDModifier.PROTECTED;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertBoolean;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getAttributeBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getClassBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FullConstructorTest extends DecoratorTestCase {

  private GlobalExtensionManagement glex = new GlobalExtensionManagement();

  private ASTCDClass subBClass;

  private ASTCDClass subAClass;

  @Before
  public void setup() {
    LogStub.init();
    Log.enableFailQuick(false);
    this.glex.setGlobalValue("astHelper", DecorationHelper.getInstance());
    this.glex.setGlobalValue("cdPrinter", new CdUtilsPrinter());
    ASTCDCompilationUnit ast = this.parse("de", "monticore", "codegen", "data", "SupData");
    this.glex.setGlobalValue("service", new AbstractService(ast));

    DataDecorator dataDecorator = new DataDecorator(this.glex, new MethodDecorator(glex, new ASTService(ast)), new ASTService(ast), new DataDecoratorUtil());
    ASTCDClass clazz = getClassBy("SupB", ast);
    ASTCDClass changedClass = CD4AnalysisMill.cDClassBuilder().setName(clazz.getName())
        .setModifier(clazz.getModifier())
        .build();
    this.subBClass = dataDecorator.decorate(clazz, changedClass);
    clazz = getClassBy("SupA", ast);
    changedClass = CD4AnalysisMill.cDClassBuilder().setName(clazz.getName())
        .setModifier(clazz.getModifier())
        .build();
    this.subAClass = dataDecorator.decorate(clazz, changedClass);
  }

  @Test
  public void testClassNameSubB() {
    assertEquals("SupB", subBClass.getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testAttributesCountSubB() {
    assertEquals(1, subBClass.getCDAttributeList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testOwnAttributeSubB() {
    ASTCDAttribute attribute = getAttributeBy("b", subBClass);
    assertDeepEquals(PROTECTED, attribute.getModifier());
    assertBoolean(attribute.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }


  @Test
  public void testNoInheritedAttributeSubB() {
    //test that inherited attributes are not contained in new class
    assertTrue(subBClass.getCDAttributeList().stream().noneMatch(a -> a.getName().equals("i")));
    assertTrue(subBClass.getCDAttributeList().stream().noneMatch(a -> a.getName().equals("s")));
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testAttributesCountSubA() {
    assertEquals(1, subAClass.getCDAttributeList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testOwnAttributeSubA() {
    ASTCDAttribute attribute = getAttributeBy("c", subAClass);
    assertDeepEquals(PROTECTED, attribute.getModifier());
    assertDeepEquals("char", attribute.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testNoInheritedAttributeSubA() {
    //test that inherited attributes are not contained in new class
    assertTrue(subAClass.getCDAttributeList().stream().noneMatch(a -> a.getName().equals("i")));
    assertTrue(subAClass.getCDAttributeList().stream().noneMatch(a -> a.getName().equals("s")));
    assertTrue(subAClass.getCDAttributeList().stream().noneMatch(a -> a.getName().equals("b")));
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGeneratedCodeSubA() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.CLASS, subAClass, subAClass);
    // test parsing
    ParserConfiguration configuration = new ParserConfiguration();
    JavaParser parser = new JavaParser(configuration);
    ParseResult parseResult = parser.parse(sb.toString());
    assertTrue(parseResult.isSuccessful());
  
    assertTrue(Log.getFindings().isEmpty());
  }


  @Test
  public void testGeneratedCodeSubB() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.CLASS, subBClass, subBClass);
    // test parsing
    ParserConfiguration configuration = new ParserConfiguration();
    JavaParser parser = new JavaParser(configuration);
    ParseResult parseResult = parser.parse(sb.toString());
    assertTrue(parseResult.isSuccessful());
  
    assertTrue(Log.getFindings().isEmpty());
  }
}
