package de.monticore.codegen.cd2java;

import de.monticore.ast.ASTNode;
import de.monticore.codegen.cd2java.factories.CDModifier;
import de.monticore.codegen.cd2java.factories.CDTypeFactory;

import static org.junit.Assert.assertTrue;

public final class DecoratorAssert {

  private static final int DEFAULT_ARRAY_DIMENSION = 1;

  private static final CDTypeFactory cdTypeFactory = CDTypeFactory.getInstance();

  private DecoratorAssert() {
  }

  public static void assertDeepEquals(ASTNode expected, ASTNode actual) {
    assertTrue(expected.deepEquals(actual));
  }

  public static void assertDeepEquals(CDModifier expected, ASTNode actual) {
    assertDeepEquals(expected.build(), actual);
  }

  public static void assertDeepEquals(Class<?> expected, ASTNode actual) {
    assertDeepEquals(cdTypeFactory.createSimpleReferenceType(expected), actual);
  }

  public static void assertBoolean(ASTNode actual) {
    assertDeepEquals(cdTypeFactory.createBooleanType(), actual);
  }

  public static void assertInt(ASTNode actual) {
    assertDeepEquals(cdTypeFactory.createIntType(), actual);
  }

  public static void assertVoid(ASTNode acutal) {
    assertDeepEquals(cdTypeFactory.createVoidType(), acutal);
  }

  public static void assertOptionalOf(Class<?> clazz, ASTNode actual) {
    assertDeepEquals(cdTypeFactory.createOptionalTypeOf(clazz), actual);
  }

  public static void assertOptionalOf(String name, ASTNode actual) {
    assertDeepEquals(cdTypeFactory.createOptionalTypeOf(name), actual);
  }

  public static void assertListOf(Class<?> clazz, ASTNode actual) {
    assertDeepEquals(cdTypeFactory.createListTypeOf(clazz), actual);
  }

  public static void assertListOf(String name, ASTNode actual) {
    assertDeepEquals(cdTypeFactory.createListTypeOf(name), actual);
  }

  public static void assertArrayOf(Class<?> clazz, ASTNode actual) {
    assertDeepEquals(cdTypeFactory.createArrayType(clazz, DEFAULT_ARRAY_DIMENSION), actual);
  }

  public static void assertArrayOf(String name, ASTNode actual) {
    assertDeepEquals(cdTypeFactory.createArrayType(name, DEFAULT_ARRAY_DIMENSION), actual);
  }
}
