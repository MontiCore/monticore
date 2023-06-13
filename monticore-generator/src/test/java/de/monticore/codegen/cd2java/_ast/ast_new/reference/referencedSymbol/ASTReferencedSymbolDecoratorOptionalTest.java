/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast.ast_new.reference.referencedSymbol;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import de.monticore.cd.codegen.CD2JavaTemplates;
import de.monticore.cd.codegen.CdUtilsPrinter;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.DecorationHelper;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._ast.ast_class.reference.ASTReferenceDecorator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.umlstereotype._ast.ASTStereotype;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import static de.monticore.cd.facade.CDModifier.PROTECTED;
import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertBoolean;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.*;
import static org.junit.Assert.*;

public class ASTReferencedSymbolDecoratorOptionalTest extends DecoratorTestCase {

  private GlobalExtensionManagement glex = new GlobalExtensionManagement();

  private ASTCDClass astClass;

  private ASTCDClass originalClass;

  private ASTCDClass mandAttrClass;

  private static final String NAME_SYMBOL_LOADER = "de.monticore.codegen.ast.referencedsymbol._symboltable.FooSymbol";

  private static final String NAME_SYMBOL = "de.monticore.codegen.ast.referencedsymbol._symboltable.FooSymbol";

  @Before
  public void setup() {
    this.glex.setGlobalValue("astHelper", DecorationHelper.getInstance());
    this.glex.setGlobalValue("cdPrinter", new CdUtilsPrinter());
    ASTCDCompilationUnit ast = this.parse("de", "monticore", "codegen", "ast", "ReferencedSymbol");
    this.glex.setGlobalValue("service", new AbstractService(ast));

    ASTReferenceDecorator<ASTCDClass> decorator = new ASTReferenceDecorator(this.glex, new SymbolTableService(ast));
    originalClass = getClassBy("ASTBarOpt", ast);
    ASTCDClass changedClass = CD4AnalysisMill.cDClassBuilder().setName(originalClass.getName())
        .setModifier(originalClass.getModifier())
        .build();
    this.astClass = decorator.decorate(originalClass, changedClass);

    ASTCDClass mandAttrClass = getClassBy("ASTBarMand", ast);
    ASTCDClass mandAttrClassChanged = CD4AnalysisMill.cDClassBuilder().setName(mandAttrClass.getName())
        .setModifier(mandAttrClass.getModifier())
        .build();
    this.mandAttrClass = decorator.decorate(mandAttrClass, mandAttrClassChanged);
  }

  /**
   * test for generated optional methods for optional attribute
   */

  @Test
  public void testClass() {
    assertEquals("ASTBarOpt", astClass.getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testAttributes() {
    assertFalse(astClass.getCDAttributeList().isEmpty());
    assertEquals(1, astClass.getCDAttributeList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testNameAttribute() {
    ASTCDAttribute nameAttribute = getAttributeBy("name", originalClass);
    assertTrue(nameAttribute.getModifier().isProtected());
    assertTrue(nameAttribute.getModifier().isPresentStereotype());
    ASTStereotype stereotype = nameAttribute.getModifier().getStereotype();
    assertEquals(1, stereotype.sizeValues());
    assertEquals("referencedSymbol", stereotype.getValues(0).getName());
    assertTrue(stereotype.getValues(0).isPresentText());
    assertEquals("de.monticore.codegen.ast.referencedsymbol._symboltable.FooSymbol", stereotype.getValues(0).getValue());
    assertDeepEquals("Optional<String>", nameAttribute.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSymbolAttribute() {
    ASTCDAttribute symbolAttribute = getAttributeBy("nameSymbol", astClass);
    assertTrue(symbolAttribute.getModifier().isProtected());
    assertDeepEquals(NAME_SYMBOL_LOADER, symbolAttribute.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMethods() {
    assertEquals(5, astClass.getCDMethodList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetNameSymbolMethod() {
    ASTCDMethod method = getMethodBy("getNameSymbol", astClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals(NAME_SYMBOL, method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testIsPresentNameSymbolMethod() {
    ASTCDMethod method = getMethodBy("isPresentNameSymbol", astClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertBoolean(method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    CD4C.init(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CD2JavaTemplates.CLASS, astClass, packageDir);
    // test parsing
    ParserConfiguration configuration = new ParserConfiguration();
    JavaParser parser = new JavaParser(configuration);
    ParseResult parseResult = parser.parse(sb.toString());
    assertTrue(parseResult.isSuccessful());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  /**
   * test for generated optional methods for mandatory attribute
   */
  @Test
  public void testClassMand() {
    assertEquals("ASTBarMand", mandAttrClass.getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testAttributesMand() {
    assertFalse(mandAttrClass.getCDAttributeList().isEmpty());
    assertEquals(1, mandAttrClass.getCDAttributeList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSymbolAttributeMand() {
    ASTCDAttribute symbolAttribute = getAttributeBy("nameSymbol", mandAttrClass);
    assertTrue(symbolAttribute.getModifier().isProtected());
    assertDeepEquals(NAME_SYMBOL_LOADER, symbolAttribute.getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMethodsMand() {
    assertEquals(5, mandAttrClass.getCDMethodList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testUpdateNameSymbolSurrogateMethod() {
    ASTCDMethod method = getMethodBy("updateNameSymbol", mandAttrClass);
    assertDeepEquals(PROTECTED, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetNameSymbolMethodMand() {
    ASTCDMethod method = getMethodBy("getNameSymbol", mandAttrClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals(NAME_SYMBOL, method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testIsPresentNameSymbolMethodMand() {
    ASTCDMethod method = getMethodBy("isPresentNameSymbol", mandAttrClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertBoolean(method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGeneratedCodeMand() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    CD4C.init(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CD2JavaTemplates.CLASS, mandAttrClass, packageDir);
    // test parsing
    ParserConfiguration configuration = new ParserConfiguration();
    JavaParser parser = new JavaParser(configuration);
    ParseResult parseResult = parser.parse(sb.toString());
    assertTrue(parseResult.isSuccessful());
  
    assertTrue(Log.getFindings().isEmpty());
  }
}
