/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast.enums;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import de.monticore.cd.codegen.CD2JavaTemplates;
import de.monticore.cd.codegen.CdUtilsPrinter;
import de.monticore.cd.facade.CDModifier;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
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
import static de.monticore.codegen.cd2java.DecoratorAssert.assertInt;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getEnumBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LiteralsEnumDecoratorTest extends DecoratorTestCase {
  
  private ASTCDEnum cdEnum;

  private ASTCDCompilationUnit decoratedCompilationUnit;
  
  private ASTCDCompilationUnit originalCompilationUnit;
  
  @Before
  public void setUp() {
    decoratedCompilationUnit = this.parse("de", "monticore", "codegen", "ast", "Automaton");
    originalCompilationUnit = decoratedCompilationUnit.deepClone();
    this.glex.setGlobalValue("service", new AbstractService(decoratedCompilationUnit));
    this.glex.setGlobalValue("cdPrinter", new CdUtilsPrinter());
    
    EnumDecorator decorator = new EnumDecorator(this.glex, new AccessorDecorator(glex, new ASTService(decoratedCompilationUnit)), new ASTService(decoratedCompilationUnit));
    this.cdEnum = decorator.decorate(getEnumBy("AutomatonLiterals", decoratedCompilationUnit));
  }
  
  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalCompilationUnit, decoratedCompilationUnit);
    
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testEnumName() {
    assertEquals("AutomatonLiterals", cdEnum.getName());
    
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testAttributeCount() {
    assertEquals(1, cdEnum.getCDAttributeList().size());
    
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testIntValueAttribute() {
    ASTCDAttribute intValueAttribute = cdEnum.getCDAttributeList().get(0);
    assertEquals("intValue", intValueAttribute.getName());
    assertDeepEquals(CDModifier.PROTECTED, intValueAttribute.getModifier());
    assertInt(intValueAttribute.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testConstructorCount() {
    assertEquals(1, cdEnum.getCDConstructorList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testLiteralsConstructor() {
    ASTCDConstructor constructor = cdEnum.getCDConstructorList().get(0);
    assertDeepEquals(CDModifier.PRIVATE, constructor.getModifier());
    assertEquals("AutomatonLiterals", constructor.getName());
    assertEquals(1, constructor.sizeCDParameters());
    assertInt(constructor.getCDParameter(0).getMCType());
    assertEquals("intValue", constructor.getCDParameter(0).getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testMethodCount() {
    assertEquals(1, cdEnum.getCDMethodList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testIntValueMethod() {
    ASTCDMethod method = cdEnum.getCDMethodList().get(0);
    assertDeepEquals(CDModifier.PUBLIC, method.getModifier());
    assertEquals("getIntValue", method.getName());
    assertTrue((method.getMCReturnType().isPresentMCType()));
    assertInt(method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    CD4C.init(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CD2JavaTemplates.ENUM, cdEnum, packageDir);
    // test parsing
    ParserConfiguration configuration = new ParserConfiguration();
    JavaParser parser = new JavaParser(configuration);
    ParseResult parseResult = parser.parse(sb.toString());
    assertTrue(parseResult.isSuccessful());
  
    assertTrue(Log.getFindings().isEmpty());
  }
}
