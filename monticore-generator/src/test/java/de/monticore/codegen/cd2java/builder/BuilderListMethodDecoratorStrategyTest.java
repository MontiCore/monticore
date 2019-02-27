//package de.monticore.codegen.cd2java.builder;
//
//import de.monticore.codegen.cd2java.factories.CDAttributeFactory;
//import de.monticore.codegen.cd2java.factories.CDTypeFactory;
//import de.monticore.generating.templateengine.GlobalExtensionManagement;
//import de.monticore.types.TypesPrinter;
//import de.monticore.types.types._ast.ASTType;
//import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
//import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
//import de.monticore.umlcd4a.cd4analysis._ast.ASTCDParameter;
//import de.se_rwth.commons.logging.LogStub;
//import org.junit.Before;
//import org.junit.Test;
//
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//import static de.monticore.codegen.cd2java.builder.BuilderDecoratorConstants.BUILDER_SUFFIX;
//import static junit.framework.TestCase.assertTrue;
//import static org.junit.Assert.assertEquals;
//
//public class BuilderListMethodDecoratorStrategyTest {
//
//  private static final String BUILDER_CLASS_NAME = "A" + BUILDER_SUFFIX;
//
//  private static final String PUBLIC = "public";
//
//  private final GlobalExtensionManagement glex = new GlobalExtensionManagement();
//
//  private List<ASTCDMethod> methods;
//
//  @Before
//  public void setup() {
//    LogStub.init();
//    ASTCDAttribute attribute = CDAttributeFactory.getInstance().createAttributeByDefinition("protected java.util.List<String> a;");
//    ASTType builderType = CDTypeFactory.getInstance().createTypeByDefinition(BUILDER_CLASS_NAME);
//    BuilderMandatoryMethodDecoratorStrategy mandatoryDecoratorStrategy = new BuilderMandatoryMethodDecoratorStrategy(glex, builderType);
//    BuilderListMethodDecoratorStrategy decoratorStrategy = new BuilderListMethodDecoratorStrategy(glex, mandatoryDecoratorStrategy, builderType);
//    this.methods = decoratorStrategy.decorate(attribute);
//  }
//
//  @Test
//  public void testMethods() {
//    assertEquals(34, methods.size());
//  }
//
//  @Test
//  public void testGetMethod() {
//    List<ASTCDMethod> methodOpt = this.methods.stream()
//        .filter(m -> "getAList".equals(m.getName()))
//        .filter(m -> m.getCDParameterList().isEmpty())
//        .collect(Collectors.toList());
//    assertEquals(1, methodOpt.size());
//    ASTCDMethod method = methodOpt.get(0);
//    assertEquals("java.util.List<String>", method.printReturnType());
//    assertEquals(PUBLIC, method.printModifier().trim());
//  }
//
//  @Test
//  public void testSetMethod() {
//    List<ASTCDMethod> methodOpt = this.methods.stream()
//        .filter(m -> "setAList".equals(m.getName()))
//        .filter(m -> 1 == m.getCDParameterList().size())
//        .collect(Collectors.toList());
//    assertEquals(1, methodOpt.size());
//    ASTCDMethod method = methodOpt.get(0);
//    assertEquals(BUILDER_CLASS_NAME, method.printReturnType());
//    assertEquals(PUBLIC, method.printModifier().trim());
//
//    assertEquals(1, method.getCDParameterList().size());
//    ASTCDParameter parameter = method.getCDParameter(0);
//    assertEquals("java.util.List<String>", TypesPrinter.printType(parameter.getType()));
//    assertEquals("aList", parameter.getName());
//  }
//
//  @Test
//  public void testClearMethod() {
//    Optional<ASTCDMethod> methodOpt = this.methods.stream().filter(m -> "clearA".equals(m.getName())).findFirst();
//    assertTrue(methodOpt.isPresent());
//    ASTCDMethod method = methodOpt.get();
//    assertTrue(method.getCDParameterList().isEmpty());
//    assertEquals(BUILDER_CLASS_NAME, method.printReturnType());
//    assertEquals(PUBLIC, method.printModifier().trim());
//  }
//
//  @Test
//  public void testAddMethod() {
//    List<ASTCDMethod> methods = this.methods.stream()
//        .filter(m -> "addA".equals(m.getName()))
//        .filter(m -> 1 == m.getCDParameterList().size())
//        .collect(Collectors.toList());
//    assertEquals(1, methods.size());
//    ASTCDMethod method = methods.get(0);
//    assertEquals(BUILDER_CLASS_NAME, method.printReturnType());
//    assertEquals(PUBLIC, method.printModifier().trim());
//
//    assertEquals(1, method.getCDParameterList().size());
//    ASTCDParameter parameter = method.getCDParameter(0);
//    assertEquals("String", TypesPrinter.printType(parameter.getType()));
//    assertEquals("element", parameter.getName());
//  }
//
//  @Test
//  public void testAddAllMethod() {
//    Optional<ASTCDMethod> methodOpt = this.methods.stream().filter(m -> "addAllA".equals(m.getName())).findFirst();
//    assertTrue(methodOpt.isPresent());
//    ASTCDMethod method = methodOpt.get();
//    assertEquals(BUILDER_CLASS_NAME, method.printReturnType());
//    assertEquals(PUBLIC, method.printModifier().trim());
//
//    assertEquals(1, method.getCDParameterList().size());
//    ASTCDParameter parameter = method.getCDParameter(0);
//    assertEquals("Collection<? extends String>", TypesPrinter.printType(parameter.getType()));
//    assertEquals("collection", parameter.getName());
//  }
//
//  @Test
//  public void testRemoveMethod() {
//    List<ASTCDMethod> methodOpt = this.methods.stream()
//        .filter(m -> "removeA".equals(m.getName()))
//        .collect(Collectors.toList());
//    assertEquals(2, methodOpt.size());
//    ASTCDMethod method = methodOpt.get(0);
//    assertEquals(BUILDER_CLASS_NAME, method.printReturnType());
//    assertEquals(PUBLIC, method.printModifier().trim());
//
//    assertEquals(1, method.getCDParameterList().size());
//    ASTCDParameter parameter = method.getCDParameter(0);
//    assertEquals("Object", TypesPrinter.printType(parameter.getType()));
//    assertEquals("element", parameter.getName());
//  }
//
//  @Test
//  public void testRemoveAllMethod() {
//    Optional<ASTCDMethod> methodOpt = this.methods.stream().filter(m -> "removeAllA".equals(m.getName())).findFirst();
//    assertTrue(methodOpt.isPresent());
//    ASTCDMethod method = methodOpt.get();
//    assertEquals(BUILDER_CLASS_NAME, method.printReturnType());
//    assertEquals(PUBLIC, method.printModifier().trim());
//
//    assertEquals(1, method.getCDParameterList().size());
//    ASTCDParameter parameter = method.getCDParameter(0);
//    assertEquals("Collection<?>", TypesPrinter.printType(parameter.getType()));
//    assertEquals("collection", parameter.getName());
//  }
//
//  @Test
//  public void testRetainAllMethod() {
//    Optional<ASTCDMethod> methodOpt = this.methods.stream().filter(m -> "retainAllA".equals(m.getName())).findFirst();
//    assertTrue(methodOpt.isPresent());
//    ASTCDMethod method = methodOpt.get();
//    assertEquals(BUILDER_CLASS_NAME, method.printReturnType());
//    assertEquals(PUBLIC, method.printModifier().trim());
//
//    assertEquals(1, method.getCDParameterList().size());
//    ASTCDParameter parameter = method.getCDParameter(0);
//    assertEquals("Collection<?>", TypesPrinter.printType(parameter.getType()));
//    assertEquals("collection", parameter.getName());
//  }
//
//  @Test
//  public void testRemoveIfMethod() {
//    Optional<ASTCDMethod> methodOpt = this.methods.stream().filter(m -> "removeIfA".equals(m.getName())).findFirst();
//    assertTrue(methodOpt.isPresent());
//    ASTCDMethod method = methodOpt.get();
//    assertEquals(BUILDER_CLASS_NAME, method.printReturnType());
//    assertEquals(PUBLIC, method.printModifier().trim());
//
//    assertEquals(1, method.getCDParameterList().size());
//    ASTCDParameter parameter = method.getCDParameter(0);
//    assertEquals("Predicate<? super String>", TypesPrinter.printType(parameter.getType()));
//    assertEquals("filter", parameter.getName());
//  }
//
//  @Test
//  public void testForEachMethod() {
//    Optional<ASTCDMethod> methodOpt = this.methods.stream().filter(m -> "forEachA".equals(m.getName())).findFirst();
//    assertTrue(methodOpt.isPresent());
//    ASTCDMethod method = methodOpt.get();
//    assertEquals(BUILDER_CLASS_NAME, method.printReturnType());
//    assertEquals(PUBLIC, method.printModifier().trim());
//
//    assertEquals(1, method.getCDParameterList().size());
//    ASTCDParameter parameter = method.getCDParameter(0);
//    assertEquals("Consumer<? super String>", TypesPrinter.printType(parameter.getType()));
//    assertEquals("action", parameter.getName());
//  }
//
//  @Test
//  public void testAddWithIndexMethod() {
//    List<ASTCDMethod> methods = this.methods.stream()
//        .filter(m -> "addA".equals(m.getName()))
//        .filter(m -> 2 == m.getCDParameterList().size())
//        .collect(Collectors.toList());
//    assertEquals(1, methods.size());
//    ASTCDMethod method = methods.get(0);
//    assertEquals(BUILDER_CLASS_NAME, method.printReturnType());
//    assertEquals(PUBLIC, method.printModifier().trim());
//
//    assertEquals(2, method.getCDParameterList().size());
//    ASTCDParameter parameter = method.getCDParameter(0);
//    assertEquals("int", TypesPrinter.printType(parameter.getType()));
//    assertEquals("index", parameter.getName());
//    parameter = method.getCDParameter(1);
//    assertEquals("String", TypesPrinter.printType(parameter.getType()));
//    assertEquals("element", parameter.getName());
//  }
//
//  @Test
//  public void testAddAllWithIndexMethod() {
//    List<ASTCDMethod> methods = this.methods.stream()
//        .filter(m -> "addAllA".equals(m.getName()))
//        .filter(m -> 2 == m.getCDParameterList().size())
//        .collect(Collectors.toList());
//    assertEquals(1, methods.size());
//    ASTCDMethod method = methods.get(0);
//    assertEquals(BUILDER_CLASS_NAME, method.printReturnType());
//    assertEquals(PUBLIC, method.printModifier().trim());
//
//    assertEquals(2, method.getCDParameterList().size());
//    ASTCDParameter parameter = method.getCDParameter(0);
//    assertEquals("int", TypesPrinter.printType(parameter.getType()));
//    assertEquals("index", parameter.getName());
//    parameter = method.getCDParameter(1);
//    assertEquals("Collection<? extends String>", TypesPrinter.printType(parameter.getType()));
//    assertEquals("collection", parameter.getName());
//  }
//
//  @Test
//  public void testRemoveWithIndexMethod() {
//    List<ASTCDMethod> methodOpt = this.methods.stream()
//        .filter(m -> "removeA".equals(m.getName()))
//        .collect(Collectors.toList());
//    assertEquals(2, methodOpt.size());
//    ASTCDMethod method = methodOpt.get(1);
//    assertEquals(BUILDER_CLASS_NAME, method.printReturnType());
//    assertEquals(PUBLIC, method.printModifier().trim());
//
//    assertEquals(1, method.getCDParameterList().size());
//    ASTCDParameter parameter = method.getCDParameter(0);
//    assertEquals("int", TypesPrinter.printType(parameter.getType()));
//    assertEquals("index", parameter.getName());
//  }
//
//  @Test
//  public void testSetWithIndexMethod() {
//    List<ASTCDMethod> methodOpt = this.methods.stream()
//        .filter(m -> "setA".equals(m.getName()))
//        .filter(m -> 2 == m.getCDParameterList().size())
//        .collect(Collectors.toList());
//    assertEquals(1, methodOpt.size());
//    ASTCDMethod method = methodOpt.get(0);
//    assertEquals(BUILDER_CLASS_NAME, method.printReturnType());
//    assertEquals(PUBLIC, method.printModifier().trim());
//
//    ASTCDParameter parameter = method.getCDParameter(0);
//    assertEquals("int", TypesPrinter.printType(parameter.getType()));
//    assertEquals("index", parameter.getName());
//    parameter = method.getCDParameter(1);
//    assertEquals("String", TypesPrinter.printType(parameter.getType()));
//    assertEquals("element", parameter.getName());
//  }
//
//  @Test
//  public void testReplaceAllMethod() {
//    Optional<ASTCDMethod> methodOpt = this.methods.stream().filter(m -> "replaceAllA".equals(m.getName())).findFirst();
//    assertTrue(methodOpt.isPresent());
//    ASTCDMethod method = methodOpt.get();
//    assertEquals(BUILDER_CLASS_NAME, method.printReturnType());
//    assertEquals(PUBLIC, method.printModifier().trim());
//
//    assertEquals(1, method.getCDParameterList().size());
//    ASTCDParameter parameter = method.getCDParameter(0);
//    assertEquals("UnaryOperator<String>", TypesPrinter.printType(parameter.getType()));
//    assertEquals("operator", parameter.getName());
//  }
//
//  @Test
//  public void testSortMethod() {
//    Optional<ASTCDMethod> methodOpt = this.methods.stream().filter(m -> "sortA".equals(m.getName())).findFirst();
//    assertTrue(methodOpt.isPresent());
//    ASTCDMethod method = methodOpt.get();
//    assertEquals(BUILDER_CLASS_NAME, method.printReturnType());
//    assertEquals(PUBLIC, method.printModifier().trim());
//
//    assertEquals(1, method.getCDParameterList().size());
//    ASTCDParameter parameter = method.getCDParameter(0);
//    assertEquals("Comparator<? super String>", TypesPrinter.printType(parameter.getType()));
//    assertEquals("comparator", parameter.getName());
//  }
//
//  @Test
//  public void testContainsMethod() {
//    Optional<ASTCDMethod> methodOpt = this.methods.stream().filter(m -> "containsA".equals(m.getName())).findFirst();
//    assertTrue(methodOpt.isPresent());
//    ASTCDMethod method = methodOpt.get();
//    assertEquals("boolean", method.printReturnType());
//    assertEquals(PUBLIC, method.printModifier().trim());
//
//    assertEquals(1, method.getCDParameterList().size());
//    ASTCDParameter parameter = method.getCDParameter(0);
//    assertEquals("Object", TypesPrinter.printType(parameter.getType()));
//    assertEquals("element", parameter.getName());
//  }
//
//  @Test
//  public void testContainsAllMethod() {
//    Optional<ASTCDMethod> methodOpt = this.methods.stream().filter(m -> "containsAllA".equals(m.getName())).findFirst();
//    assertTrue(methodOpt.isPresent());
//    ASTCDMethod method = methodOpt.get();
//    assertEquals("boolean", method.printReturnType());
//    assertEquals(PUBLIC, method.printModifier().trim());
//
//    assertEquals(1, method.getCDParameterList().size());
//    ASTCDParameter parameter = method.getCDParameter(0);
//    assertEquals("Collection<?>", TypesPrinter.printType(parameter.getType()));
//    assertEquals("collection", parameter.getName());
//  }
//
//  @Test
//  public void testIsEmptyMethod() {
//    Optional<ASTCDMethod> methodOpt = this.methods.stream().filter(m -> "isEmptyA".equals(m.getName())).findFirst();
//    assertTrue(methodOpt.isPresent());
//    ASTCDMethod method = methodOpt.get();
//    assertTrue(method.getCDParameterList().isEmpty());
//    assertEquals("boolean", method.printReturnType());
//    assertEquals(PUBLIC, method.printModifier().trim());
//  }
//
//  @Test
//  public void testIteratorMethod() {
//    Optional<ASTCDMethod> methodOpt = this.methods.stream().filter(m -> "iteratorA".equals(m.getName())).findFirst();
//    assertTrue(methodOpt.isPresent());
//    ASTCDMethod method = methodOpt.get();
//    assertTrue(method.getCDParameterList().isEmpty());
//    assertEquals("Iterator<String>", method.printReturnType());
//    assertEquals(PUBLIC, method.printModifier().trim());
//  }
//
//  @Test
//  public void testSizeMethod() {
//    Optional<ASTCDMethod> methodOpt = this.methods.stream().filter(m -> "sizeA".equals(m.getName())).findFirst();
//    assertTrue(methodOpt.isPresent());
//    ASTCDMethod method = methodOpt.get();
//    assertTrue(method.getCDParameterList().isEmpty());
//    assertEquals("int", method.printReturnType());
//    assertEquals(PUBLIC, method.printModifier().trim());
//  }
//
//  @Test
//  public void testToArrayWithParamMethod() {
//    Optional<ASTCDMethod> methodOpt = this.methods.stream()
//        .filter(m -> "toArrayA".equals(m.getName()))
//        .filter(m -> 1 == m.getCDParameterList().size())
//        .findFirst();
//    assertTrue(methodOpt.isPresent());
//    ASTCDMethod method = methodOpt.get();
//    assertEquals("String[]", method.printReturnType());
//    assertEquals(PUBLIC, method.printModifier().trim());
//
//    assertEquals(1, method.getCDParameterList().size());
//    ASTCDParameter parameter = method.getCDParameter(0);
//    assertEquals("String[]", TypesPrinter.printType(parameter.getType()));
//    assertEquals("array", parameter.getName());
//  }
//
//  @Test
//  public void testToArrayMethod() {
//    Optional<ASTCDMethod> methodOpt = this.methods.stream()
//        .filter(m -> "toArrayA".equals(m.getName()))
//        .filter(m -> m.getCDParameterList().isEmpty())
//        .findFirst();
//    assertTrue(methodOpt.isPresent());
//    ASTCDMethod method = methodOpt.get();
//    assertEquals("Object[]", method.printReturnType());
//    assertEquals(PUBLIC, method.printModifier().trim());
//  }
//
//  @Test
//  public void testSpliteratorMethod() {
//    Optional<ASTCDMethod> methodOpt = this.methods.stream().filter(m -> "spliteratorA".equals(m.getName())).findFirst();
//    assertTrue(methodOpt.isPresent());
//    ASTCDMethod method = methodOpt.get();
//    assertTrue(method.getCDParameterList().isEmpty());
//    assertEquals("Spliterator<String>", method.printReturnType());
//    assertEquals(PUBLIC, method.printModifier().trim());
//  }
//
//  @Test
//  public void testStreamMethod() {
//    Optional<ASTCDMethod> methodOpt = this.methods.stream().filter(m -> "streamA".equals(m.getName())).findFirst();
//    assertTrue(methodOpt.isPresent());
//    ASTCDMethod method = methodOpt.get();
//    assertTrue(method.getCDParameterList().isEmpty());
//    assertEquals("Stream<String>", method.printReturnType());
//    assertEquals(PUBLIC, method.printModifier().trim());
//  }
//
//  @Test
//  public void testParallelStreamMethod() {
//    Optional<ASTCDMethod> methodOpt = this.methods.stream().filter(m -> "parallelStreamA".equals(m.getName())).findFirst();
//    assertTrue(methodOpt.isPresent());
//    ASTCDMethod method = methodOpt.get();
//    assertTrue(method.getCDParameterList().isEmpty());
//    assertEquals("Stream<String>", method.printReturnType());
//    assertEquals(PUBLIC, method.printModifier().trim());
//  }
//
//  @Test
//  public void testGetWithIndexMethod() {
//    List<ASTCDMethod> methodOpt = this.methods.stream()
//        .filter(m -> "getA".equals(m.getName()))
//        .filter(m -> 1 == m.getCDParameterList().size())
//        .collect(Collectors.toList());
//    assertEquals(1, methodOpt.size());
//    ASTCDMethod method = methodOpt.get(0);
//    assertEquals("String", method.printReturnType());
//    assertEquals(PUBLIC, method.printModifier().trim());
//
//    ASTCDParameter parameter = method.getCDParameter(0);
//    assertEquals("int", TypesPrinter.printType(parameter.getType()));
//    assertEquals("index", parameter.getName());
//  }
//
//  @Test
//  public void testIndexOfMethod() {
//    Optional<ASTCDMethod> methodOpt = this.methods.stream().filter(m -> "indexOfA".equals(m.getName())).findFirst();
//    assertTrue(methodOpt.isPresent());
//    ASTCDMethod method = methodOpt.get();
//    assertEquals("int", method.printReturnType());
//    assertEquals(PUBLIC, method.printModifier().trim());
//
//    assertEquals(1, method.getCDParameterList().size());
//    ASTCDParameter parameter = method.getCDParameter(0);
//    assertEquals("Object", TypesPrinter.printType(parameter.getType()));
//    assertEquals("element", parameter.getName());
//  }
//
//  @Test
//  public void testLastIndexOfMethod() {
//    Optional<ASTCDMethod> methodOpt = this.methods.stream().filter(m -> "lastIndexOfA".equals(m.getName())).findFirst();
//    assertTrue(methodOpt.isPresent());
//    ASTCDMethod method = methodOpt.get();
//    assertEquals("int", method.printReturnType());
//    assertEquals(PUBLIC, method.printModifier().trim());
//
//    assertEquals(1, method.getCDParameterList().size());
//    ASTCDParameter parameter = method.getCDParameter(0);
//    assertEquals("Object", TypesPrinter.printType(parameter.getType()));
//    assertEquals("element", parameter.getName());
//  }
//
//  @Test
//  public void testEqualsMethod() {
//    Optional<ASTCDMethod> methodOpt = this.methods.stream().filter(m -> "equalsA".equals(m.getName())).findFirst();
//    assertTrue(methodOpt.isPresent());
//    ASTCDMethod method = methodOpt.get();
//    assertEquals("boolean", method.printReturnType());
//    assertEquals(PUBLIC, method.printModifier().trim());
//
//    assertEquals(1, method.getCDParameterList().size());
//    ASTCDParameter parameter = method.getCDParameter(0);
//    assertEquals("Object", TypesPrinter.printType(parameter.getType()));
//    assertEquals("o", parameter.getName());
//  }
//
//  @Test
//  public void testHashCodeMethod() {
//    Optional<ASTCDMethod> methodOpt = this.methods.stream().filter(m -> "hashCodeA".equals(m.getName())).findFirst();
//    assertTrue(methodOpt.isPresent());
//    ASTCDMethod method = methodOpt.get();
//    assertTrue(method.getCDParameterList().isEmpty());
//    assertEquals("int", method.printReturnType());
//    assertEquals(PUBLIC, method.printModifier().trim());
//  }
//
//  @Test
//  public void testListIteratorMethod() {
//    Optional<ASTCDMethod> methodOpt = this.methods.stream()
//        .filter(m -> "listIteratorA".equals(m.getName()))
//        .filter(m -> m.getCDParameterList().isEmpty())
//        .findFirst();
//    assertTrue(methodOpt.isPresent());
//    ASTCDMethod method = methodOpt.get();
//    assertTrue(method.getCDParameterList().isEmpty());
//    assertEquals("ListIterator<String>", method.printReturnType());
//    assertEquals(PUBLIC, method.printModifier().trim());
//  }
//
//  @Test
//  public void testListIteratorWithIndexMethod() {
//    Optional<ASTCDMethod> methodOpt = this.methods.stream()
//        .filter(m -> "listIteratorA".equals(m.getName()))
//        .filter(m -> 1 == m.getCDParameterList().size())
//        .findFirst();
//    assertTrue(methodOpt.isPresent());
//    ASTCDMethod method = methodOpt.get();
//    assertEquals("ListIterator<String>", method.printReturnType());
//    assertEquals(PUBLIC, method.printModifier().trim());
//
//    assertEquals(1, method.getCDParameterList().size());
//    ASTCDParameter parameter = method.getCDParameter(0);
//    assertEquals("int", TypesPrinter.printType(parameter.getType()));
//    assertEquals("index", parameter.getName());
//  }
//
//  @Test
//  public void testSubListMethod() {
//    Optional<ASTCDMethod> methodOpt = this.methods.stream().filter(m -> "subListA".equals(m.getName())).findFirst();
//    assertTrue(methodOpt.isPresent());
//    ASTCDMethod method = methodOpt.get();
//    assertEquals("java.util.List<String>", method.printReturnType());
//    assertEquals(PUBLIC, method.printModifier().trim());
//
//    assertEquals(2, method.getCDParameterList().size());
//    ASTCDParameter parameter = method.getCDParameter(0);
//    assertEquals("int", TypesPrinter.printType(parameter.getType()));
//    assertEquals("start", parameter.getName());
//
//    parameter = method.getCDParameter(1);
//    assertEquals("int", TypesPrinter.printType(parameter.getType()));
//    assertEquals("end", parameter.getName());
//  }
//}
