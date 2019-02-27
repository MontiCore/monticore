package de.monticore.codegen.cd2java.ast_new;

import de.monticore.MontiCoreScript;
import de.monticore.codegen.cd2java.typecd2java.TypeCD2JavaDecorator;
import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.types.TypesPrinter;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDParameter;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;


public class ASTDecoratorListMethodsTest {

  private ASTCDCompilationUnit cdCompilationUnit;

  private List<ASTCDMethod> methods;

  private GlobalExtensionManagement glex;

  private static final String PUBLIC = "public";

  private static final String VOID = "void";

  private static final String BOOLEAN = "boolean";

  private static final String ASTNAME = "de.monticore.codegen.ast.asttest._ast.ASTMand";


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

    ASTDecorator factoryDecorator = new ASTDecorator(glex, cdCompilationUnit);
    this.methods = factoryDecorator.decorate(cdCompilationUnit.getCDDefinition().getCDClass(2)).getCDMethodList();
  }


  @Test
  public void testClass() {
    assertEquals("ASTList", cdCompilationUnit.getCDDefinition().getCDClass(2).getName());
  }

  @Test
  public void testGetMethod() {
    List<ASTCDMethod> methodOpt = this.methods.stream()
        .filter(m -> "getNamesList".equals(m.getName()))
        .filter(m -> m.getCDParameterList().isEmpty())
        .collect(Collectors.toList());
    assertEquals(1, methodOpt.size());
    ASTCDMethod method = methodOpt.get(0);
    assertEquals("List<" + ASTNAME + ">", method.printReturnType());
    assertEquals(PUBLIC, method.printModifier().trim());
  }

  @Test
  public void testSetMethod() {
    List<ASTCDMethod> methodOpt = this.methods.stream()
        .filter(m -> "setNamesList".equals(m.getName()))
        .filter(m -> 1 == m.getCDParameterList().size())
        .collect(Collectors.toList());
    assertEquals(1, methodOpt.size());
    ASTCDMethod method = methodOpt.get(0);
    assertEquals(VOID, method.printReturnType());
    assertEquals(PUBLIC, method.printModifier().trim());

    assertEquals(1, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertEquals("List<" + ASTNAME + ">", TypesPrinter.printType(parameter.getType()));
    assertEquals("element", parameter.getName());
  }

  @Test
  public void testClearMethod() {
    Optional<ASTCDMethod> methodOpt = this.methods.stream().filter(m -> "clearNames".equals(m.getName())).findFirst();
    assertTrue(methodOpt.isPresent());
    ASTCDMethod method = methodOpt.get();
    assertTrue(method.getCDParameterList().isEmpty());
    assertEquals(VOID, method.printReturnType());
    assertEquals(PUBLIC, method.printModifier().trim());
  }

  @Test
  public void testAddMethod() {
    List<ASTCDMethod> methods = this.methods.stream()
        .filter(m -> "addNames".equals(m.getName()))
        .filter(m -> 1 == m.getCDParameterList().size())
        .collect(Collectors.toList());
    assertEquals(1, methods.size());
    ASTCDMethod method = methods.get(0);
    assertEquals(BOOLEAN, method.printReturnType());
    assertEquals(PUBLIC, method.printModifier().trim());

    assertEquals(1, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertEquals(ASTNAME, TypesPrinter.printType(parameter.getType()));
    assertEquals("element", parameter.getName());
  }

  @Test
  public void testAddAllMethod() {
    Optional<ASTCDMethod> methodOpt = this.methods.stream().filter(m -> "addAllNames".equals(m.getName())).findFirst();
    assertTrue(methodOpt.isPresent());
    ASTCDMethod method = methodOpt.get();
    assertEquals(BOOLEAN, method.printReturnType());
    assertEquals(PUBLIC, method.printModifier().trim());

    assertEquals(1, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertEquals("Collection<? extends " + ASTNAME + ">", TypesPrinter.printType(parameter.getType()));
    assertEquals("collection", parameter.getName());
  }

  @Test
  public void testRemoveMethod() {
    List<ASTCDMethod> methodOpt = this.methods.stream()
        .filter(m -> "removeNames".equals(m.getName()))
        .collect(Collectors.toList());
    assertEquals(2, methodOpt.size());
    ASTCDMethod method = methodOpt.get(0);
    assertEquals(BOOLEAN, method.printReturnType());
    assertEquals(PUBLIC, method.printModifier().trim());

    assertEquals(1, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertEquals("Object", TypesPrinter.printType(parameter.getType()));
    assertEquals("element", parameter.getName());
  }

  @Test
  public void testRemoveAllMethod() {
    Optional<ASTCDMethod> methodOpt = this.methods.stream().filter(m -> "removeAllNames".equals(m.getName())).findFirst();
    assertTrue(methodOpt.isPresent());
    ASTCDMethod method = methodOpt.get();
    assertEquals(BOOLEAN, method.printReturnType());
    assertEquals(PUBLIC, method.printModifier().trim());

    assertEquals(1, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertEquals("Collection<?>", TypesPrinter.printType(parameter.getType()));
    assertEquals("collection", parameter.getName());
  }

  @Test
  public void testRetainAllMethod() {
    Optional<ASTCDMethod> methodOpt = this.methods.stream().filter(m -> "retainAllNames".equals(m.getName())).findFirst();
    assertTrue(methodOpt.isPresent());
    ASTCDMethod method = methodOpt.get();
    assertEquals(BOOLEAN, method.printReturnType());
    assertEquals(PUBLIC, method.printModifier().trim());

    assertEquals(1, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertEquals("Collection<?>", TypesPrinter.printType(parameter.getType()));
    assertEquals("collection", parameter.getName());
  }

  @Test
  public void testRemoveIfMethod() {
    Optional<ASTCDMethod> methodOpt = this.methods.stream().filter(m -> "removeIfNames".equals(m.getName())).findFirst();
    assertTrue(methodOpt.isPresent());
    ASTCDMethod method = methodOpt.get();
    assertEquals(BOOLEAN, method.printReturnType());
    assertEquals(PUBLIC, method.printModifier().trim());

    assertEquals(1, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertEquals("Predicate<? super " + ASTNAME + ">", TypesPrinter.printType(parameter.getType()));
    assertEquals("filter", parameter.getName());
  }

  @Test
  public void testForEachMethod() {
    Optional<ASTCDMethod> methodOpt = this.methods.stream().filter(m -> "forEachNames".equals(m.getName())).findFirst();
    assertTrue(methodOpt.isPresent());
    ASTCDMethod method = methodOpt.get();
    assertEquals(VOID, method.printReturnType());
    assertEquals(PUBLIC, method.printModifier().trim());

    assertEquals(1, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertEquals("Consumer<? super " + ASTNAME + ">", TypesPrinter.printType(parameter.getType()));
    assertEquals("action", parameter.getName());
  }

  @Test
  public void testAddWithIndexMethod() {
    List<ASTCDMethod> methods = this.methods.stream()
        .filter(m -> "addNames".equals(m.getName()))
        .filter(m -> 2 == m.getCDParameterList().size())
        .collect(Collectors.toList());
    assertEquals(1, methods.size());
    ASTCDMethod method = methods.get(0);
    assertEquals(VOID, method.printReturnType());
    assertEquals(PUBLIC, method.printModifier().trim());

    assertEquals(2, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertEquals("int", TypesPrinter.printType(parameter.getType()));
    assertEquals("index", parameter.getName());
    parameter = method.getCDParameter(1);
    assertEquals(ASTNAME, TypesPrinter.printType(parameter.getType()));
    assertEquals("element", parameter.getName());
  }

  @Test
  public void testAddAllWithIndexMethod() {
    List<ASTCDMethod> methods = this.methods.stream()
        .filter(m -> "addAllNames".equals(m.getName()))
        .filter(m -> 2 == m.getCDParameterList().size())
        .collect(Collectors.toList());
    assertEquals(1, methods.size());
    ASTCDMethod method = methods.get(0);
    assertEquals(BOOLEAN, method.printReturnType());
    assertEquals(PUBLIC, method.printModifier().trim());

    assertEquals(2, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertEquals("int", TypesPrinter.printType(parameter.getType()));
    assertEquals("index", parameter.getName());
    parameter = method.getCDParameter(1);
    assertEquals("Collection<? extends " + ASTNAME + ">", TypesPrinter.printType(parameter.getType()));
    assertEquals("collection", parameter.getName());
  }

  @Test
  public void testRemoveWithIndexMethod() {
    List<ASTCDMethod> methodOpt = this.methods.stream()
        .filter(m -> "removeNames".equals(m.getName()))
        .collect(Collectors.toList());
    assertEquals(2, methodOpt.size());
    ASTCDMethod method = methodOpt.get(1);
    assertEquals(ASTNAME, method.printReturnType());
    assertEquals(PUBLIC, method.printModifier().trim());

    assertEquals(1, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertEquals("int", TypesPrinter.printType(parameter.getType()));
    assertEquals("index", parameter.getName());
  }

  @Test
  public void testSetWithIndexMethod() {
    List<ASTCDMethod> methodOpt = this.methods.stream()
        .filter(m -> "setNames".equals(m.getName()))
        .filter(m -> 2 == m.getCDParameterList().size())
        .collect(Collectors.toList());
    assertEquals(1, methodOpt.size());
    ASTCDMethod method = methodOpt.get(0);
    assertEquals(ASTNAME, method.printReturnType());
    assertEquals(PUBLIC, method.printModifier().trim());

    ASTCDParameter parameter = method.getCDParameter(0);
    assertEquals("int", TypesPrinter.printType(parameter.getType()));
    assertEquals("index", parameter.getName());
    parameter = method.getCDParameter(1);
    assertEquals(ASTNAME, TypesPrinter.printType(parameter.getType()));
    assertEquals("element", parameter.getName());
  }

  @Test
  public void testReplaceAllMethod() {
    Optional<ASTCDMethod> methodOpt = this.methods.stream().filter(m -> "replaceAllNames".equals(m.getName())).findFirst();
    assertTrue(methodOpt.isPresent());
    ASTCDMethod method = methodOpt.get();
    assertEquals(VOID, method.printReturnType());
    assertEquals(PUBLIC, method.printModifier().trim());

    assertEquals(1, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertEquals("UnaryOperator<" + ASTNAME + ">", TypesPrinter.printType(parameter.getType()));
    assertEquals("operator", parameter.getName());
  }

  @Test
  public void testSortMethod() {
    Optional<ASTCDMethod> methodOpt = this.methods.stream().filter(m -> "sortNames".equals(m.getName())).findFirst();
    assertTrue(methodOpt.isPresent());
    ASTCDMethod method = methodOpt.get();
    assertEquals(VOID, method.printReturnType());
    assertEquals(PUBLIC, method.printModifier().trim());

    assertEquals(1, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertEquals("Comparator<? super " + ASTNAME + ">", TypesPrinter.printType(parameter.getType()));
    assertEquals("comparator", parameter.getName());
  }

  @Test
  public void testContainsMethod() {
    Optional<ASTCDMethod> methodOpt = this.methods.stream().filter(m -> "containsNames".equals(m.getName())).findFirst();
    assertTrue(methodOpt.isPresent());
    ASTCDMethod method = methodOpt.get();
    assertEquals(BOOLEAN, method.printReturnType());
    assertEquals(PUBLIC, method.printModifier().trim());

    assertEquals(1, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertEquals("Object", TypesPrinter.printType(parameter.getType()));
    assertEquals("element", parameter.getName());
  }

  @Test
  public void testContainsAllMethod() {
    Optional<ASTCDMethod> methodOpt = this.methods.stream().filter(m -> "containsAllNames".equals(m.getName())).findFirst();
    assertTrue(methodOpt.isPresent());
    ASTCDMethod method = methodOpt.get();
    assertEquals(BOOLEAN, method.printReturnType());
    assertEquals(PUBLIC, method.printModifier().trim());

    assertEquals(1, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertEquals("Collection<?>", TypesPrinter.printType(parameter.getType()));
    assertEquals("collection", parameter.getName());
  }

  @Test
  public void testIsEmptyMethod() {
    Optional<ASTCDMethod> methodOpt = this.methods.stream().filter(m -> "isEmptyNames".equals(m.getName())).findFirst();
    assertTrue(methodOpt.isPresent());
    ASTCDMethod method = methodOpt.get();
    assertTrue(method.getCDParameterList().isEmpty());
    assertEquals(BOOLEAN, method.printReturnType());
    assertEquals(PUBLIC, method.printModifier().trim());
  }

  @Test
  public void testIteratorMethod() {
    Optional<ASTCDMethod> methodOpt = this.methods.stream().filter(m -> "iteratorNames".equals(m.getName())).findFirst();
    assertTrue(methodOpt.isPresent());
    ASTCDMethod method = methodOpt.get();
    assertTrue(method.getCDParameterList().isEmpty());
    assertEquals("Iterator<" + ASTNAME + ">", method.printReturnType());
    assertEquals(PUBLIC, method.printModifier().trim());
  }

  @Test
  public void testSizeMethod() {
    Optional<ASTCDMethod> methodOpt = this.methods.stream().filter(m -> "sizeNames".equals(m.getName())).findFirst();
    assertTrue(methodOpt.isPresent());
    ASTCDMethod method = methodOpt.get();
    assertTrue(method.getCDParameterList().isEmpty());
    assertEquals("int", method.printReturnType());
    assertEquals(PUBLIC, method.printModifier().trim());
  }

  @Test
  public void testToArrayWithParamMethod() {
    Optional<ASTCDMethod> methodOpt = this.methods.stream()
        .filter(m -> "toArrayNames".equals(m.getName()))
        .filter(m -> 1 == m.getCDParameterList().size())
        .findFirst();
    assertTrue(methodOpt.isPresent());
    ASTCDMethod method = methodOpt.get();
    assertEquals(ASTNAME+"[]", method.printReturnType());
    assertEquals(PUBLIC, method.printModifier().trim());

    assertEquals(1, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertEquals(ASTNAME+"[]", TypesPrinter.printType(parameter.getType()));
    assertEquals("array", parameter.getName());
  }

  @Test
  public void testToArrayMethod() {
    Optional<ASTCDMethod> methodOpt = this.methods.stream()
        .filter(m -> "toArrayNames".equals(m.getName()))
        .filter(m -> m.getCDParameterList().isEmpty())
        .findFirst();
    assertTrue(methodOpt.isPresent());
    ASTCDMethod method = methodOpt.get();
    assertEquals("Object[]", method.printReturnType());
    assertEquals(PUBLIC, method.printModifier().trim());
  }

  @Test
  public void testSpliteratorMethod() {
    Optional<ASTCDMethod> methodOpt = this.methods.stream().filter(m -> "spliteratorNames".equals(m.getName())).findFirst();
    assertTrue(methodOpt.isPresent());
    ASTCDMethod method = methodOpt.get();
    assertTrue(method.getCDParameterList().isEmpty());
    assertEquals("Spliterator<"+ASTNAME+">", method.printReturnType());
    assertEquals(PUBLIC, method.printModifier().trim());
  }

  @Test
  public void testStreamMethod() {
    Optional<ASTCDMethod> methodOpt = this.methods.stream().filter(m -> "streamNames".equals(m.getName())).findFirst();
    assertTrue(methodOpt.isPresent());
    ASTCDMethod method = methodOpt.get();
    assertTrue(method.getCDParameterList().isEmpty());
    assertEquals("Stream<"+ASTNAME+">", method.printReturnType());
    assertEquals(PUBLIC, method.printModifier().trim());
  }

  @Test
  public void testParallelStreamMethod() {
    Optional<ASTCDMethod> methodOpt = this.methods.stream().filter(m -> "parallelStreamNames".equals(m.getName())).findFirst();
    assertTrue(methodOpt.isPresent());
    ASTCDMethod method = methodOpt.get();
    assertTrue(method.getCDParameterList().isEmpty());
    assertEquals("Stream<"+ASTNAME+">", method.printReturnType());
    assertEquals(PUBLIC, method.printModifier().trim());
  }

  @Test
  public void testGetWithIndexMethod() {
    List<ASTCDMethod> methodOpt = this.methods.stream()
        .filter(m -> "getNames".equals(m.getName()))
        .filter(m -> 1 == m.getCDParameterList().size())
        .collect(Collectors.toList());
    assertEquals(1, methodOpt.size());
    ASTCDMethod method = methodOpt.get(0);
    assertEquals(ASTNAME, method.printReturnType());
    assertEquals(PUBLIC, method.printModifier().trim());

    ASTCDParameter parameter = method.getCDParameter(0);
    assertEquals("int", TypesPrinter.printType(parameter.getType()));
    assertEquals("index", parameter.getName());
  }

  @Test
  public void testIndexOfMethod() {
    Optional<ASTCDMethod> methodOpt = this.methods.stream().filter(m -> "indexOfNames".equals(m.getName())).findFirst();
    assertTrue(methodOpt.isPresent());
    ASTCDMethod method = methodOpt.get();
    assertEquals("int", method.printReturnType());
    assertEquals(PUBLIC, method.printModifier().trim());

    assertEquals(1, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertEquals("Object", TypesPrinter.printType(parameter.getType()));
    assertEquals("element", parameter.getName());
  }

  @Test
  public void testLastIndexOfMethod() {
    Optional<ASTCDMethod> methodOpt = this.methods.stream().filter(m -> "lastIndexOfNames".equals(m.getName())).findFirst();
    assertTrue(methodOpt.isPresent());
    ASTCDMethod method = methodOpt.get();
    assertEquals("int", method.printReturnType());
    assertEquals(PUBLIC, method.printModifier().trim());

    assertEquals(1, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertEquals("Object", TypesPrinter.printType(parameter.getType()));
    assertEquals("element", parameter.getName());
  }

  @Test
  public void testEqualsMethod() {
    Optional<ASTCDMethod> methodOpt = this.methods.stream().filter(m -> "equalsNames".equals(m.getName())).findFirst();
    assertTrue(methodOpt.isPresent());
    ASTCDMethod method = methodOpt.get();
    assertEquals(BOOLEAN, method.printReturnType());
    assertEquals(PUBLIC, method.printModifier().trim());

    assertEquals(1, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertEquals("Object", TypesPrinter.printType(parameter.getType()));
    assertEquals("o", parameter.getName());
  }

  @Test
  public void testHashCodeMethod() {
    Optional<ASTCDMethod> methodOpt = this.methods.stream().filter(m -> "hashCodeNames".equals(m.getName())).findFirst();
    assertTrue(methodOpt.isPresent());
    ASTCDMethod method = methodOpt.get();
    assertTrue(method.getCDParameterList().isEmpty());
    assertEquals("int", method.printReturnType());
    assertEquals(PUBLIC, method.printModifier().trim());
  }

  @Test
  public void testListIteratorMethod() {
    Optional<ASTCDMethod> methodOpt = this.methods.stream()
        .filter(m -> "listIteratorNames".equals(m.getName()))
        .filter(m -> m.getCDParameterList().isEmpty())
        .findFirst();
    assertTrue(methodOpt.isPresent());
    ASTCDMethod method = methodOpt.get();
    assertTrue(method.getCDParameterList().isEmpty());
    assertEquals("ListIterator<"+ASTNAME+">", method.printReturnType());
    assertEquals(PUBLIC, method.printModifier().trim());
  }

  @Test
  public void testListIteratorWithIndexMethod() {
    Optional<ASTCDMethod> methodOpt = this.methods.stream()
        .filter(m -> "listIteratorNames".equals(m.getName()))
        .filter(m -> 1 == m.getCDParameterList().size())
        .findFirst();
    assertTrue(methodOpt.isPresent());
    ASTCDMethod method = methodOpt.get();
    assertEquals("ListIterator<" + ASTNAME + ">", method.printReturnType());
    assertEquals(PUBLIC, method.printModifier().trim());

    assertEquals(1, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertEquals("int", TypesPrinter.printType(parameter.getType()));
    assertEquals("index", parameter.getName());
  }

  @Test
  public void testSubListMethod() {
    Optional<ASTCDMethod> methodOpt = this.methods.stream().filter(m -> "subListNames".equals(m.getName())).findFirst();
    assertTrue(methodOpt.isPresent());
    ASTCDMethod method = methodOpt.get();
    assertEquals("List<" + ASTNAME + ">", method.printReturnType());
    assertEquals(PUBLIC, method.printModifier().trim());

    assertEquals(2, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertEquals("int", TypesPrinter.printType(parameter.getType()));
    assertEquals("start", parameter.getName());

    parameter = method.getCDParameter(1);
    assertEquals("int", TypesPrinter.printType(parameter.getType()));
    assertEquals("end", parameter.getName());
  }
}
