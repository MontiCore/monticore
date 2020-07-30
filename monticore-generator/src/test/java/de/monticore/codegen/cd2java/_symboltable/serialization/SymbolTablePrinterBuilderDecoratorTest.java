/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable.serialization;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.prettyprint.CD4CodePrinter;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecorationHelper;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._ast.builder.BuilderDecorator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java.methods.AccessorDecorator;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import static de.monticore.cd.facade.CDModifier.PROTECTED;
import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertBoolean;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.*;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodBy;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.JSON_PRINTER;
import static org.junit.Assert.*;

public class SymbolTablePrinterBuilderDecoratorTest extends DecoratorTestCase {

  private ASTCDClass builderClass;

  private final GlobalExtensionManagement glex = new GlobalExtensionManagement();

  @Before
  public void setup() {
    LogStub.init();
    LogStub.enableFailQuick(false);

    ASTCDCompilationUnit ast = parse("de", "monticore", "codegen", "symboltable","cdForBuilder", "SymbolTablePrinter_Builder");
    ASTCDClass cdClass = getClassBy("ASymbolTablePrinter", ast);
    this.glex.setGlobalValue("service", new AbstractService(ast));
    this.glex.setGlobalValue("cdPrinter", new CD4CodePrinter());
    this.glex.setGlobalValue("astHelper", DecorationHelper.getInstance());


    AccessorDecorator methodDecorator = new AccessorDecorator(glex, new SymbolTableService(ast));
    BuilderDecorator builderDecorator = new BuilderDecorator(glex, methodDecorator, new SymbolTableService(ast));
    SymbolTablePrinterBuilderDecorator astNodeBuilderDecorator = new SymbolTablePrinterBuilderDecorator(glex,
        builderDecorator);
    this.builderClass = astNodeBuilderDecorator.decorate(cdClass);
  }

  @Test
  public void testAttributeCount(){
    assertEquals(2, builderClass.sizeCDAttributes());
  }

  @Test
  public void testRealBuilderAttribute(){
    ASTCDAttribute attribute = getAttributeBy("realBuilder", builderClass);
    assertDeepEquals(PROTECTED, attribute.getModifier());
    assertDeepEquals(builderClass.getName(), attribute.getMCType());
  }

  @Test
  public void testPrinterAttribute(){
    ASTCDAttribute attribute = getAttributeBy("printer", builderClass);
    assertDeepEquals(PROTECTED, attribute.getModifier());
    assertDeepEquals(JSON_PRINTER, attribute.getMCType());
  }

  @Test
  public void testMethodCount(){
    assertEquals(4, builderClass.sizeCDMethods());
  }

  @Test
  public void testSetPrinterMethod(){
    ASTCDMethod method = getMethodBy("setPrinter", builderClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertEquals(1, method.sizeCDParameters());
    ASTCDParameter parameter = method.getCDParameters(0);
    assertDeepEquals(JSON_PRINTER, parameter.getMCType());
    assertFalse(method.getMCReturnType().isPresentMCVoidType());
    assertDeepEquals(builderClass.getName(), method.getMCReturnType().getMCType());
  }

  @Test
  public void testGetPrinterMethod(){
    ASTCDMethod method = getMethodBy("getPrinter", builderClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertEquals(0, method.sizeCDParameters());
    assertFalse(method.getMCReturnType().isPresentMCVoidType());
    assertDeepEquals(JSON_PRINTER, method.getMCReturnType().getMCType());
  }

  @Test
  public void testIsValidMethod(){
    ASTCDMethod method = getMethodBy("isValid", builderClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertEquals(0, method.sizeCDParameters());
    assertFalse(method.getMCReturnType().isPresentMCVoidType());
    assertBoolean(method.getMCReturnType().getMCType());
  }

  @Test
  public void testBuildMethod(){
    ASTCDMethod method = getMethodBy("build", builderClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertEquals(0, method.sizeCDParameters());
    assertFalse(method.getMCReturnType().isPresentMCVoidType());
    assertDeepEquals("ASymbolTablePrinter", method.getMCReturnType().getMCType());
  }

  @Test
  public void testGeneratedCode(){
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.CLASS, builderClass, builderClass);
    // test parsing
    ParserConfiguration configuration = new ParserConfiguration();
    JavaParser parser = new JavaParser(configuration);
    ParseResult parseResult = parser.parse(sb.toString());
    assertTrue(parseResult.isSuccessful());
  }

}
