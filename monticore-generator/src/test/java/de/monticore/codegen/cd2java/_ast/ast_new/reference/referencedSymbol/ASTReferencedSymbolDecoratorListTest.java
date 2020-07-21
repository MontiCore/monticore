/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast.ast_new.reference.referencedSymbol;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import de.monticore.cd.cd4analysis.CD4AnalysisMill;
import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.prettyprint.CD4CodePrinter;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecorationHelper;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._ast.ast_class.reference.ASTReferenceDecorator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import static de.monticore.cd.facade.CDModifier.PROTECTED;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.*;
import static org.junit.Assert.*;

public class ASTReferencedSymbolDecoratorListTest extends DecoratorTestCase {

  private GlobalExtensionManagement glex = new GlobalExtensionManagement();

  private ASTCDClass astClass;

  private ASTCDClass originalClass;

  private static final String NAME_SYMBOL_MAP = "Map<String,de.monticore.codegen.ast.referencedsymbol._symboltable.FooSymbolSurrogate>";

  @Before
  public void setup() {
    LogStub.init();
    LogStub.enableFailQuick(false);
    this.glex.setGlobalValue("astHelper", DecorationHelper.getInstance());
    this.glex.setGlobalValue("cdPrinter", new CD4CodePrinter());
    ASTCDCompilationUnit ast = this.parse("de", "monticore", "codegen", "ast", "ReferencedSymbol");
    this.glex.setGlobalValue("service", new AbstractService(ast));

    ASTReferenceDecorator<ASTCDClass> decorator = new ASTReferenceDecorator(this.glex, new SymbolTableService(ast));
    originalClass= getClassBy("ASTBarList", ast);
    ASTCDClass changedClass = CD4AnalysisMill.cDClassBuilder().setName(originalClass.getName())
        .setModifier(originalClass.getModifier())
        .build();
    this.astClass = decorator.decorate(originalClass, changedClass);
  }

  @Test
  public void testClass() {
    assertEquals("ASTBarList", astClass.getName());
  }

  @Test
  public void testAttributes() {
    assertFalse(astClass.isEmptyCDAttributes());
    assertEquals(1, astClass.sizeCDAttributes());
  }

  @Test
  public void testNameAttribute() {
    ASTCDAttribute nameAttribute = getAttributeBy("name", originalClass);
    assertTrue(nameAttribute.getModifier().isProtected());
    assertTrue(nameAttribute.getModifier().isPresentStereotype());
    ASTCDStereotype stereotype = nameAttribute.getModifier().getStereotype();
    assertEquals(1, stereotype.sizeValues());
    assertEquals("referencedSymbol", stereotype.getValue(0).getName());
    assertTrue(stereotype.getValue(0).isPresentValue());
    assertEquals("de.monticore.codegen.ast.referencedsymbol._symboltable.FooSymbol", stereotype.getValue(0).getValue());
    assertDeepEquals("java.util.List<String>", nameAttribute.getMCType());
  }

  @Test
  public void testSymbolAttribute() {
    ASTCDAttribute symbolAttribute = getAttributeBy("nameSymbolSurrogate", astClass);
    assertTrue(symbolAttribute.getModifier().isProtected());
    assertDeepEquals(NAME_SYMBOL_MAP, symbolAttribute.getMCType());
  }

  @Test
  public void testMethods() {
    assertEquals(39, astClass.getCDMethodList().size());
  }

  @Test
  public void testUpdateNameSymbolSurrogateListMethod() {
    ASTCDMethod method = getMethodBy("updateNameSymbolSurrogate", astClass);
    assertDeepEquals(PROTECTED, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.CLASS, astClass, astClass);
    // test parsing
    ParserConfiguration configuration = new ParserConfiguration();
    JavaParser parser = new JavaParser(configuration);
    ParseResult parseResult = parser.parse(sb.toString());
    assertTrue(parseResult.isSuccessful());
  }
}
