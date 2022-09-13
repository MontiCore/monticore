/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast.ast_constants;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import de.monticore.cd.codegen.CD2JavaTemplates;
import de.monticore.cd.codegen.CdUtilsPrinter;
import de.monticore.cd.facade.CDModifier;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.DecorationHelper;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._ast.constants.ASTConstantsDecorator;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertInt;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getAttributeBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodBy;
import static org.junit.Assert.*;

public class ASTConstantsDecoratorTest extends DecoratorTestCase {

  private ASTCDClass constantClass;

  private GlobalExtensionManagement glex;

  private ASTCDCompilationUnit decoratedCompilationUnit;

  private ASTCDCompilationUnit originalCompilationUnit;
  
  @Before
  public void setUp() {
        // LogStub.initPlusLog();  // for manual testing purpose only
    this.glex = new GlobalExtensionManagement();

    this.glex.setGlobalValue("astHelper", DecorationHelper.getInstance());
    this.glex.setGlobalValue("cdPrinter", new CdUtilsPrinter());
    decoratedCompilationUnit = this.parse("de", "monticore", "codegen", "ast", "Automaton");
    originalCompilationUnit = decoratedCompilationUnit.deepClone();
    this.glex.setGlobalValue("service", new AbstractService(decoratedCompilationUnit));
    ASTConstantsDecorator decorator = new ASTConstantsDecorator(this.glex, new AbstractService(decoratedCompilationUnit));
    this.constantClass = decorator.decorate(decoratedCompilationUnit);
  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalCompilationUnit, decoratedCompilationUnit);
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testClassName() {
    assertEquals("ASTConstantsAutomaton", constantClass.getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testAttributeCount() {
    assertEquals(5, constantClass.getCDAttributeList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testLANGUAGEAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("LANGUAGE", constantClass);
    assertDeepEquals(CDModifier.PUBLIC_STATIC_FINAL, astcdAttribute.getModifier());
    assertDeepEquals("String", astcdAttribute.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testDEFAULTAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("DEFAULT", constantClass);
    assertDeepEquals(CDModifier.PUBLIC_STATIC_FINAL, astcdAttribute.getModifier());
    assertInt(astcdAttribute.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testFINALAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("FINAL", constantClass);
    assertDeepEquals(CDModifier.PUBLIC_STATIC_FINAL, astcdAttribute.getModifier());
    assertInt(astcdAttribute.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testINITIALAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("INITIAL", constantClass);
    assertDeepEquals(CDModifier.PUBLIC_STATIC_FINAL, astcdAttribute.getModifier());
    assertInt(astcdAttribute.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSuperGrammarsAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("superGrammars", constantClass);
    assertDeepEquals(CDModifier.PUBLIC_STATIC, astcdAttribute.getModifier());
    assertDeepEquals("String[]", astcdAttribute.getMCType());
    assertFalse(astcdAttribute.isPresentInitial());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMethodCount() {
    assertEquals(1, constantClass.getCDMethodList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetAllLanguagesMethod() {
    ASTCDMethod method = getMethodBy("getAllLanguages", constantClass);
    assertDeepEquals(CDModifier.PUBLIC_STATIC, method.getModifier());
    assertDeepEquals("Collection<String>", method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    CD4C.init(generatorSetup);

    StringBuilder sb = generatorEngine.generate(CD2JavaTemplates.CLASS, constantClass, packageDir);
    // test parsing
    ParserConfiguration configuration = new ParserConfiguration();
    JavaParser parser = new JavaParser(configuration);
    ParseResult parseResult = parser.parse(sb.toString());
    assertTrue(parseResult.isSuccessful());
  
    assertTrue(Log.getFindings().isEmpty());
  }
}
