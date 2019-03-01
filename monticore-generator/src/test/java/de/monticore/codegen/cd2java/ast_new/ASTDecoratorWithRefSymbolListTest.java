package de.monticore.codegen.cd2java.ast_new;

import de.monticore.MontiCoreScript;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.codegen.cd2java.typecd2java.TypeCD2JavaDecorator;
import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.types.TypesPrinter;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertBoolean;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getAttributeBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodBy;
import static de.monticore.codegen.cd2java.factories.CDModifier.PRIVATE;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ASTDecoratorWithRefSymbolListTest {

  private ASTCDCompilationUnit cdCompilationUnit;

  private ASTCDClass astcdClass;

  private List<ASTCDMethod> methods;

  private GlobalExtensionManagement glex;

  private static final String PUBLIC = "public";

  private static final String MAP_SYMBOL_ATTR_TYPE = "Map<String, Optional<de.monticore.codegen.ast.asttest._symboltable.MandSymbol>>";

  private static final String OPTIONAL_SYMBOL_TYPE = "Optional<de.monticore.codegen.ast.asttest._symboltable.MandSymbol>";

  @Before
  public void setUp() {
    this.glex = new GlobalExtensionManagement();
    //create grammar from ModelPath
    Path modelPathPath = Paths.get("src/test/resources");
    ModelPath modelPath = new ModelPath(modelPathPath);
    Optional<ASTMCGrammar> grammar = new MontiCoreScript()
        .parseGrammar(Paths.get(new File(
            "src/test/resources/de/monticore/codegen/ast/ASTTest.mc4").getAbsolutePath()));
    assertTrue(grammar.isPresent());

    //create ASTCDDefinition from MontiCoreScript
    MontiCoreScript script = new MontiCoreScript();
    GlobalScope globalScope = TestHelper.createGlobalScope(modelPath);
    script.createSymbolsFromAST(globalScope, grammar.get());
    cdCompilationUnit = script.deriveCD(grammar.get(), new GlobalExtensionManagement(),
        globalScope);

    cdCompilationUnit.setEnclosingScope(globalScope);
    //make types java compatible
    TypeCD2JavaDecorator typeDecorator = new TypeCD2JavaDecorator();
    typeDecorator.decorate(cdCompilationUnit);

    glex.setGlobalValue("astHelper", new DecorationHelper());
    ASTDecorator symbolDecorator = new ASTDecorator(glex, cdCompilationUnit);
    this.astcdClass = symbolDecorator.decorate(cdCompilationUnit.getCDDefinition().getCDClass(5));
    this.methods = astcdClass.getCDMethodList();
  }

  @Test
  public void testClass() {
    assertEquals("ASTRefList", cdCompilationUnit.getCDDefinition().getCDClass(5).getName());
  }

  @Test
  public void testAttributes() {
    assertFalse(astcdClass.isEmptyCDAttributes());
    assertEquals(2, astcdClass.sizeCDAttributes());
  }

  @Test
  public void testSymbolAttribute() {
    ASTCDAttribute symbolAttribute = getAttributeBy("namesSymbol", astcdClass);
    assertTrue(PRIVATE.build().deepEquals(symbolAttribute.getModifier()));
    assertEquals(MAP_SYMBOL_ATTR_TYPE, symbolAttribute.printType());
  }

  @Test
  public void testMethods() {
    assertEquals(83, astcdClass.sizeCDMethods());
  }

  @Test
  public void testGetMethod() {
    ASTCDMethod method = getMethodBy("getNamesSymbolList", 0, methods);
    assertEquals("List<" + OPTIONAL_SYMBOL_TYPE + ">", method.printReturnType());
    assertEquals(PUBLIC, method.printModifier().trim());
  }

  @Test
  public void testContainsMethod() {
    ASTCDMethod method = getMethodBy("containsNamesSymbol", methods);
    assertBoolean(method.getReturnType());
    assertEquals(PUBLIC, method.printModifier().trim());

    assertEquals(1, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertEquals("Object", TypesPrinter.printType(parameter.getType()));
    assertEquals("element", parameter.getName());
  }

  @Test
  public void testContainsAllMethod() {
    ASTCDMethod method = getMethodBy("containsAllNamesSymbol", methods);
    assertBoolean(method.getReturnType());
    assertEquals(PUBLIC, method.printModifier().trim());

    assertEquals(1, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertEquals("Collection<?>", TypesPrinter.printType(parameter.getType()));
    assertEquals("collection", parameter.getName());
  }

  @Test
  public void testIsEmptyMethod() {
    ASTCDMethod method = getMethodBy("isEmptyNamesSymbol", methods);
    assertTrue(method.getCDParameterList().isEmpty());
    assertBoolean(method.getReturnType());
    assertEquals(PUBLIC, method.printModifier().trim());
  }

  @Test
  public void testIteratorMethod() {
    ASTCDMethod method = getMethodBy("iteratorNamesSymbol", methods);
    assertTrue(method.getCDParameterList().isEmpty());
    assertEquals("Iterator<" + OPTIONAL_SYMBOL_TYPE + ">", method.printReturnType());
    assertEquals(PUBLIC, method.printModifier().trim());
  }

  @Test
  public void testSizeMethod() {
    ASTCDMethod method = getMethodBy("sizeNamesSymbol", methods);
    assertTrue(method.getCDParameterList().isEmpty());
    assertEquals("int", method.printReturnType());
    assertEquals(PUBLIC, method.printModifier().trim());
  }

  @Test
  public void testToArrayWithParamMethod() {
    Optional<ASTCDMethod> methodOpt = this.methods.stream()
        .filter(m -> "toArrayNamesSymbol".equals(m.getName()))
        .filter(m -> 1 == m.getCDParameterList().size())
        .findFirst();
    assertTrue(methodOpt.isPresent());
    ASTCDMethod method = methodOpt.get();
    assertEquals(OPTIONAL_SYMBOL_TYPE + "[]", method.printReturnType());
    assertEquals(PUBLIC, method.printModifier().trim());

    assertEquals(1, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertEquals(OPTIONAL_SYMBOL_TYPE + "[]", TypesPrinter.printType(parameter.getType()));
    assertEquals("array", parameter.getName());
  }

  @Test
  public void testToArrayMethod() {
    ASTCDMethod method = getMethodBy("toArrayNamesSymbol", 0, methods);
    assertEquals("Object[]", method.printReturnType());
    assertEquals(PUBLIC, method.printModifier().trim());
  }

  @Test
  public void testSpliteratorMethod() {
    ASTCDMethod method = getMethodBy("spliteratorNamesSymbol", methods);
    assertTrue(method.getCDParameterList().isEmpty());
    assertEquals("Spliterator<" + OPTIONAL_SYMBOL_TYPE + ">", method.printReturnType());
    assertEquals(PUBLIC, method.printModifier().trim());
  }

  @Test
  public void testStreamMethod() {
    ASTCDMethod method = getMethodBy("streamNamesSymbol", methods);
    assertTrue(method.getCDParameterList().isEmpty());
    assertEquals("Stream<" + OPTIONAL_SYMBOL_TYPE + ">", method.printReturnType());
    assertEquals(PUBLIC, method.printModifier().trim());
  }

  @Test
  public void testParallelStreamMethod() {
    ASTCDMethod method = getMethodBy("parallelStreamNamesSymbol", methods);
    assertTrue(method.getCDParameterList().isEmpty());
    assertEquals("Stream<" + OPTIONAL_SYMBOL_TYPE + ">", method.printReturnType());
    assertEquals(PUBLIC, method.printModifier().trim());
  }

  @Test
  public void testGetWithIndexMethod() {
    ASTCDMethod method = getMethodBy("getNamesSymbol", 1, methods);
    assertEquals(OPTIONAL_SYMBOL_TYPE, method.printReturnType());
    assertEquals(PUBLIC, method.printModifier().trim());

    ASTCDParameter parameter = method.getCDParameter(0);
    assertEquals("int", TypesPrinter.printType(parameter.getType()));
    assertEquals("index", parameter.getName());
  }

  @Test
  public void testIndexOfMethod() {
    ASTCDMethod method = getMethodBy("indexOfNamesSymbol", methods);
    assertEquals("int", method.printReturnType());
    assertEquals(PUBLIC, method.printModifier().trim());

    assertEquals(1, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertEquals("Object", TypesPrinter.printType(parameter.getType()));
    assertEquals("element", parameter.getName());
  }

  @Test
  public void testLastIndexOfMethod() {
    ASTCDMethod method = getMethodBy("lastIndexOfNamesSymbol", methods);
    assertEquals("int", method.printReturnType());
    assertEquals(PUBLIC, method.printModifier().trim());

    assertEquals(1, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertEquals("Object", TypesPrinter.printType(parameter.getType()));
    assertEquals("element", parameter.getName());
  }

  @Test
  public void testEqualsMethod() {
    ASTCDMethod method = getMethodBy("equalsNamesSymbol", methods);
    assertBoolean(method.getReturnType());
    assertEquals(PUBLIC, method.printModifier().trim());

    assertEquals(1, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertEquals("Object", TypesPrinter.printType(parameter.getType()));
    assertEquals("o", parameter.getName());
  }

  @Test
  public void testHashCodeMethod() {
    ASTCDMethod method = getMethodBy("hashCodeNamesSymbol", methods);
    assertTrue(method.getCDParameterList().isEmpty());
    assertEquals("int", method.printReturnType());
    assertEquals(PUBLIC, method.printModifier().trim());
  }

  @Test
  public void testListIteratorMethod() {
    ASTCDMethod method = getMethodBy("listIteratorNamesSymbol", 0, methods);
    assertTrue(method.getCDParameterList().isEmpty());
    assertEquals("ListIterator<" + OPTIONAL_SYMBOL_TYPE + ">", method.printReturnType());
    assertEquals(PUBLIC, method.printModifier().trim());
  }

  @Test
  public void testListIteratorWithIndexMethod() {
    ASTCDMethod method = getMethodBy("listIteratorNamesSymbol", 1, methods);
    assertEquals("ListIterator<" + OPTIONAL_SYMBOL_TYPE + ">", method.printReturnType());
    assertEquals(PUBLIC, method.printModifier().trim());

    assertEquals(1, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertEquals("int", TypesPrinter.printType(parameter.getType()));
    assertEquals("index", parameter.getName());
  }

  @Test
  public void testSubListMethod() {
    ASTCDMethod method = getMethodBy("subListNamesSymbol", methods);
    assertEquals("List<" + OPTIONAL_SYMBOL_TYPE + ">", method.printReturnType());
    assertEquals(PUBLIC, method.printModifier().trim());

    assertEquals(2, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertEquals("int", TypesPrinter.printType(parameter.getType()));
    assertEquals("start", parameter.getName());

    parameter = method.getCDParameter(1);
    assertEquals("int", TypesPrinter.printType(parameter.getType()));
    assertEquals("end", parameter.getName());
  }

  @Test
  public void testNoSetter() {
    Optional<ASTCDMethod> methodOpt = this.methods.stream()
        .filter(m -> "setNamesSymbolList".equals(m.getName()))
        .findFirst();
    assertFalse(methodOpt.isPresent());

    methodOpt = this.methods.stream()
        .filter(m -> "setNamesSymbol".equals(m.getName()))
        .findFirst();
    assertFalse(methodOpt.isPresent());

    methodOpt = this.methods.stream()
        .filter(m -> "removeNamesSymbol".equals(m.getName()))
        .findFirst();
    assertFalse(methodOpt.isPresent());

    methodOpt = this.methods.stream()
        .filter(m -> "addNamesSymbol".equals(m.getName()))
        .findFirst();
    assertFalse(methodOpt.isPresent());

    methodOpt = this.methods.stream()
        .filter(m -> "addAllNamesSymbol".equals(m.getName()))
        .findFirst();
    assertFalse(methodOpt.isPresent());
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.CLASS, astcdClass, astcdClass);
    System.out.println(sb.toString());
  }

  @Test
  public void testGeneratedCodeInFile() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    generatorSetup.setOutputDirectory(Paths.get("target/generated-test-sources/de/monticore/codegen/ast").toFile());
    Path generatedFiles = Paths.get("ASTRefList.java");
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    generatorEngine.generate(CoreTemplates.CLASS, generatedFiles, astcdClass, astcdClass);
  }
}
