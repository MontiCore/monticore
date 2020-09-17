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
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.JSON_PRINTER;
import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

public class ScopeDeSerBuilderDecoratorTest extends DecoratorTestCase {

  private ASTCDClass builderClass;

  private final GlobalExtensionManagement glex = new GlobalExtensionManagement();

  private static final String A_DELEGATOR_VISITOR = "de.monticore.codegen.builder.builder._visitor.ADelegatorVisitor";

  @Before
  public void setup() {
    LogStub.init();
    LogStub.enableFailQuick(false);

    ASTCDCompilationUnit ast = parse("de", "monticore", "codegen", "symboltable","cdForBuilder", "ScopeDeSer_Builder");
    ASTCDClass cdClass = getClassBy("AScopeDeSer", ast);
    this.glex.setGlobalValue("service", new AbstractService(ast));
    this.glex.setGlobalValue("cdPrinter", new CD4CodePrinter());
    this.glex.setGlobalValue("astHelper", DecorationHelper.getInstance());


    AccessorDecorator methodDecorator = new AccessorDecorator(glex, new SymbolTableService(ast));
    BuilderDecorator builderDecorator = new BuilderDecorator(glex, methodDecorator, new SymbolTableService(ast));
    ScopeDeSerBuilderDecorator astNodeBuilderDecorator = new ScopeDeSerBuilderDecorator(glex,
        builderDecorator);
    this.builderClass = astNodeBuilderDecorator.decorate(cdClass);
  }

  @Test
  public void testClassName() {
    assertEquals("AScopeDeSerBuilder", builderClass.getName());
  }

  @Test
  public void testSuperInterfacesEmpty() {
    assertTrue(builderClass.isEmptyInterface());
  }

  @Test
  public void testNoSuperClass() {
    assertFalse(builderClass.isPresentSuperclass());
  }

  @Test
  public void testConstructorCount() {
    assertEquals(1, builderClass.sizeCDConstructors());
  }

  @Test
  public void testDefaultConstructor() {
    ASTCDConstructor cdConstructor = builderClass.getCDConstructor(0);
    assertDeepEquals(PUBLIC, cdConstructor.getModifier());
    assertEquals("AScopeDeSerBuilder", cdConstructor.getName());
    assertTrue(cdConstructor.isEmptyCDParameters());
    assertTrue(cdConstructor.isEmptyException());
  }

  @Test
  public void testAttributeCount(){
    assertEquals(5, builderClass.sizeCDAttributes());
  }

  @Test
  public void testRealBuilderAttribute(){
    ASTCDAttribute attribute = getAttributeBy("realBuilder", builderClass);
    assertDeepEquals(PROTECTED, attribute.getModifier());
    assertDeepEquals(builderClass.getName(), attribute.getMCType());
  }

  @Test
  public void testSymbolFileExtensionAttribute(){
    ASTCDAttribute attribute = getAttributeBy("symbolFileExtension", builderClass);
    assertDeepEquals(PROTECTED, attribute.getModifier());
    assertDeepEquals(String.class, attribute.getMCType());
  }

  @Test
  public void testPrinterAttribute(){
    ASTCDAttribute attribute = getAttributeBy("printer", builderClass);
    assertDeepEquals(PROTECTED, attribute.getModifier());
    assertDeepEquals(JSON_PRINTER, attribute.getMCType());
  }

  @Test
  public void testSymbolTablePrinterAttribute(){
    ASTCDAttribute attribute = getAttributeBy("symbolTablePrinter", builderClass);
    assertDeepEquals(PROTECTED, attribute.getModifier());
    assertDeepEquals(A_DELEGATOR_VISITOR, attribute.getMCType());
  }

  @Test
  public void testASymbolDeSerAttribute(){
    ASTCDAttribute attribute = getAttributeBy("aSymbolDeSer", builderClass);
    assertDeepEquals(PROTECTED, attribute.getModifier());
    assertDeepEquals("ASymbolDeSer", attribute.getMCType());
  }

  @Test
  public void testMethodCount(){
    assertEquals(10, builderClass.sizeCDMethods());
  }

  @Test
  public void testSetPrinterMethod(){
    ASTCDMethod method = getMethodBy("setPrinter", builderClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertEquals(1, method.sizeCDParameters());
    ASTCDParameter parameter = method.getCDParameter(0);
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
  public void testSetSymbolTablePrinterMethod(){
    ASTCDMethod method = getMethodBy("setSymbolTablePrinter", builderClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertEquals(1, method.sizeCDParameters());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertDeepEquals(A_DELEGATOR_VISITOR, parameter.getMCType());
    assertFalse(method.getMCReturnType().isPresentMCVoidType());
    assertDeepEquals(builderClass.getName(), method.getMCReturnType().getMCType());
  }

  @Test
  public void testGetSymbolTablePrinterMehtod(){
    ASTCDMethod method = getMethodBy("getSymbolTablePrinter", builderClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertEquals(0, method.sizeCDParameters());
    assertFalse(method.getMCReturnType().isPresentMCVoidType());
    assertDeepEquals(A_DELEGATOR_VISITOR, method.getMCReturnType().getMCType());
  }

  @Test
  public void testSetASymbolDeSerMethod(){
    ASTCDMethod method = getMethodBy("setASymbolDeSer", builderClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertEquals(1, method.sizeCDParameters());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertDeepEquals("ASymbolDeSer", parameter.getMCType());
    assertFalse(method.getMCReturnType().isPresentMCVoidType());
    assertDeepEquals(builderClass.getName(), method.getMCReturnType().getMCType());
  }

  @Test
  public void testGetASymbolDeSerMethod(){
    ASTCDMethod method = getMethodBy("getASymbolDeSer", builderClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertEquals(0, method.sizeCDParameters());
    assertFalse(method.getMCReturnType().isPresentMCVoidType());
    assertDeepEquals("ASymbolDeSer", method.getMCReturnType().getMCType());
  }

  @Test
  public void testSetSymbolFileExtensionMethod(){
    ASTCDMethod method = getMethodBy("setSymbolFileExtension", builderClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertEquals(1, method.sizeCDParameters());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertDeepEquals(String.class, parameter.getMCType());
    assertFalse(method.getMCReturnType().isPresentMCVoidType());
    assertDeepEquals(builderClass.getName(), method.getMCReturnType().getMCType());
  }

  @Test
  public void testGetSymbolFileExtensionMethod(){
    ASTCDMethod method = getMethodBy("getSymbolFileExtension", builderClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertEquals(0, method.sizeCDParameters());
    assertFalse(method.getMCReturnType().isPresentMCVoidType());
    assertDeepEquals(String.class, method.getMCReturnType().getMCType());
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
    assertDeepEquals("AScopeDeSer", method.getMCReturnType().getMCType());
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
