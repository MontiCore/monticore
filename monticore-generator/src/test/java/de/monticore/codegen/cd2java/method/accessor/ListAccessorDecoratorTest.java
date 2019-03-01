package de.monticore.codegen.cd2java.method.accessor;

import de.monticore.codegen.cd2java.factories.CDAttributeFactory;
import de.monticore.codegen.cd2java.factories.CDTypeBuilder;
import de.monticore.codegen.cd2java.methods.accessor.ListAccessorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDParameter;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.stream.Stream;

import static de.monticore.codegen.cd2java.DecoratorAssert.*;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.*;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class ListAccessorDecoratorTest {

  private final GlobalExtensionManagement glex = new GlobalExtensionManagement();

  private List<ASTCDMethod> methods;

  @Before
  public void setup() {
    LogStub.init();
    ASTCDAttribute attribute = CDAttributeFactory.getInstance().createAttributeByDefinition("protected List<String> a;");
    ListAccessorDecorator listAccessorDecorator = new ListAccessorDecorator(glex);
    this.methods = listAccessorDecorator.decorate(attribute);
  }

  @Test
  public void testMethods() {
    assertEquals(19, methods.size());
  }

  @Test
  public void testGetListMethod() {
    ASTCDMethod method = getMethodBy("getAList", 0, this.methods);
    ASTType expectedReturnType = CDTypeBuilder.newTypeBuilder()
        .simpleName(List.class)
        .simpleGenericType(String.class)
        .build();
    assertDeepEquals(expectedReturnType, method.getReturnType());
    assertDeepEquals(PUBLIC, method.getModifier());
  }

  @Test
  public void testContainsMethod() {
    ASTCDMethod method = getMethodBy("containsA", this.methods);
    assertBoolean(method.getReturnType());
    assertDeepEquals(PUBLIC, method.getModifier());

    assertEquals(1, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertDeepEquals(Object.class, parameter.getType());
    assertEquals("element", parameter.getName());
  }

  @Test
  public void testContainsAllMethod() {
    ASTCDMethod method = getMethodBy("containsAllA", this.methods);
    assertBoolean(method.getReturnType());
    assertDeepEquals(PUBLIC, method.getModifier());

    assertEquals(1, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    ASTType expectedParameterType = CDTypeBuilder.newTypeBuilder()
        .simpleName(Collection.class)
        .wildCardGenericType()
        .build();
    assertDeepEquals(expectedParameterType, parameter.getType());
    assertEquals("collection", parameter.getName());
  }

  @Test
  public void testIsEmptyMethod() {
    ASTCDMethod method = getMethodBy("isEmptyA", this.methods);
    assertTrue(method.getCDParameterList().isEmpty());
    assertBoolean(method.getReturnType());
    assertDeepEquals(PUBLIC, method.getModifier());
  }

  @Test
  public void testIteratorMethod() {
    ASTCDMethod method = getMethodBy("iteratorA", this.methods);
    assertTrue(method.getCDParameterList().isEmpty());
    ASTType expectedReturnType = CDTypeBuilder.newTypeBuilder()
        .simpleName(Iterator.class)
        .simpleGenericType(String.class)
        .build();
    assertDeepEquals(expectedReturnType, method.getReturnType());
    assertDeepEquals(PUBLIC, method.getModifier());
  }

  @Test
  public void testSizeMethod() {
    ASTCDMethod method = getMethodBy("sizeA", this.methods);
    assertTrue(method.getCDParameterList().isEmpty());
    assertInt(method.getReturnType());
    assertDeepEquals(PUBLIC, method.getModifier());
  }

  @Test
  public void testToArrayWithParamMethod() {
    ASTCDMethod method = getMethodBy("toArrayA", 1, this.methods);
    assertArrayOf(String.class, method.getReturnType());
    assertDeepEquals(PUBLIC, method.getModifier());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertArrayOf(String.class, parameter.getType());
    assertEquals("array", parameter.getName());
  }

  @Test
  public void testToArrayMethod() {
    ASTCDMethod method = getMethodBy("toArrayA", 0, this.methods);
    assertArrayOf(Object.class, method.getReturnType());
    assertDeepEquals(PUBLIC, method.getModifier());
  }

  @Test
  public void testSpliteratorMethod() {
    ASTCDMethod method = getMethodBy("spliteratorA", this.methods);
    assertTrue(method.getCDParameterList().isEmpty());
    ASTType expectedReturnType = CDTypeBuilder.newTypeBuilder()
        .simpleName(Spliterator.class)
        .simpleGenericType(String.class)
        .build();
    assertDeepEquals(expectedReturnType, method.getReturnType());
    assertDeepEquals(PUBLIC, method.getModifier());
  }

  @Test
  public void testStreamMethod() {
    ASTCDMethod method = getMethodBy("streamA", this.methods);
    assertTrue(method.getCDParameterList().isEmpty());
    ASTType expectedReturnType = CDTypeBuilder.newTypeBuilder()
        .simpleName(Stream.class)
        .simpleGenericType(String.class)
        .build();
    assertDeepEquals(expectedReturnType, method.getReturnType());
    assertDeepEquals(PUBLIC, method.getModifier());
  }

  @Test
  public void testParallelStreamMethod() {
    ASTCDMethod method = getMethodBy("parallelStreamA", this.methods);
    assertTrue(method.getCDParameterList().isEmpty());
    ASTType expectedReturnType = CDTypeBuilder.newTypeBuilder()
        .simpleName(Stream.class)
        .simpleGenericType(String.class)
        .build();
    assertDeepEquals(expectedReturnType, method.getReturnType());
    assertDeepEquals(PUBLIC, method.getModifier());
  }

  @Test
  public void testGetWithIndexMethod() {
    ASTCDMethod method = getMethodBy("getA", 1, this.methods);
    assertDeepEquals(String.class, method.getReturnType());
    assertDeepEquals(PUBLIC, method.getModifier());

    ASTCDParameter parameter = method.getCDParameter(0);
    assertInt(parameter.getType());
    assertEquals("index", parameter.getName());
  }

  @Test
  public void testIndexOfMethod() {
    ASTCDMethod method = getMethodBy("indexOfA", this.methods);
    assertInt(method.getReturnType());
    assertDeepEquals(PUBLIC, method.getModifier());

    assertEquals(1, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertDeepEquals(Object.class, parameter.getType());
    assertEquals("element", parameter.getName());
  }

  @Test
  public void testLastIndexOfMethod() {
    ASTCDMethod method = getMethodBy("lastIndexOfA", this.methods);
    assertInt(method.getReturnType());
    assertDeepEquals(PUBLIC, method.getModifier());

    assertEquals(1, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertDeepEquals(Object.class, parameter.getType());
    assertEquals("element", parameter.getName());
  }

  @Test
  public void testEqualsMethod() {
    ASTCDMethod method = getMethodBy("equalsA", this.methods);
    assertBoolean(method.getReturnType());
    assertDeepEquals(PUBLIC, method.getModifier());

    assertEquals(1, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertDeepEquals(Object.class, parameter.getType());
    assertEquals("o", parameter.getName());
  }

  @Test
  public void testHashCodeMethod() {
    ASTCDMethod method = getMethodBy("hashCodeA", this.methods);
    assertTrue(method.getCDParameterList().isEmpty());
    assertEquals("int", method.printReturnType());
    assertDeepEquals(PUBLIC, method.getModifier());
  }

  @Test
  public void testListIteratorMethod() {
    ASTCDMethod method = getMethodBy("listIteratorA", 0, this.methods);
    assertTrue(method.getCDParameterList().isEmpty());
    ASTType expectedReturnType = CDTypeBuilder.newTypeBuilder()
        .simpleName(ListIterator.class)
        .simpleGenericType(String.class)
        .build();
    assertDeepEquals(expectedReturnType, method.getReturnType());
    assertDeepEquals(PUBLIC, method.getModifier());
  }

  @Test
  public void testListIteratorWithIndexMethod() {
    ASTCDMethod method = getMethodBy("listIteratorA", 1, this.methods);
    ASTType expectedReturnType = CDTypeBuilder.newTypeBuilder()
        .simpleName(ListIterator.class)
        .simpleGenericType(String.class)
        .build();
    assertDeepEquals(expectedReturnType, method.getReturnType());
    assertDeepEquals(PUBLIC, method.getModifier());

    assertEquals(1, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertInt(parameter.getType());
    assertEquals("index", parameter.getName());
  }

  @Test
  public void testSubListMethod() {
    ASTCDMethod method = getMethodBy("subListA", this.methods);
    ASTType expectedReturnType = CDTypeBuilder.newTypeBuilder()
        .simpleName(List.class)
        .simpleGenericType(String.class)
        .build();
    assertDeepEquals(expectedReturnType, method.getReturnType());
    assertDeepEquals(PUBLIC, method.getModifier());

    assertEquals(2, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertInt(parameter.getType());
    assertEquals("start", parameter.getName());

    parameter = method.getCDParameter(1);
    assertInt(parameter.getType());
    assertEquals("end", parameter.getName());
  }
}
