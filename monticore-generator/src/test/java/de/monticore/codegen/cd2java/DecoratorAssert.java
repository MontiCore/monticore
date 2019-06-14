package de.monticore.codegen.cd2java;

import de.monticore.ast.ASTNode;
import de.monticore.codegen.cd2java.factories.CDModifier;
import de.monticore.codegen.cd2java.factories.CDTypeFacade;
import de.monticore.types.MCCollectionTypesHelper;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import static org.junit.Assert.assertTrue;

public final class DecoratorAssert {

  private static final int DEFAULT_ARRAY_DIMENSION = 1;

  private static final CDTypeFacade CD_TYPE_FACADE = CDTypeFacade.getInstance();

  private DecoratorAssert() {
  }

  public static void assertDeepEquals(ASTNode expected, ASTNode actual) {
    assertTrue(String.format("Expected: [%s], Actual: [%s]", getAsString(expected), getAsString(actual)), expected.deepEquals(actual));
  }

  private static String getAsString(ASTNode node) {
    return node instanceof ASTMCType ? MCCollectionTypesHelper.printType((ASTMCType) node) : node.toString();
  }

  public static void assertDeepEquals(CDModifier expected, ASTNode actual) {
    assertDeepEquals(expected.build(), actual);
  }

  public static void assertDeepEquals(Class<?> expected, ASTNode actual) {
    assertDeepEquals(CD_TYPE_FACADE.createSimpleReferenceType(expected), actual);
  }

  public static void assertDeepEquals(String name, ASTNode actual) {
    assertDeepEquals(CD_TYPE_FACADE.createSimpleReferenceType(name), actual);
  }

  public static void assertBoolean(ASTNode actual) {
    assertDeepEquals(CD_TYPE_FACADE.createBooleanType(), actual);
  }

  public static void assertInt(ASTNode actual) {
    assertDeepEquals(CD_TYPE_FACADE.createIntType(), actual);
  }

  public static void assertVoid(ASTNode acutal) {
    assertDeepEquals(CD_TYPE_FACADE.createVoidType(), acutal);
  }

  public static void assertOptionalOf(Class<?> clazz, ASTNode actual) {
    assertDeepEquals(CD_TYPE_FACADE.createOptionalTypeOf(clazz), actual);
  }

  public static void assertOptionalOf(String name, ASTNode actual) {
    assertDeepEquals(CD_TYPE_FACADE.createOptionalTypeOf(name), actual);
  }

  public static void assertListOf(Class<?> clazz, ASTNode actual) {
    assertDeepEquals(CD_TYPE_FACADE.createListTypeOf(clazz), actual);
  }

  public static void assertListOf(String name, ASTNode actual) {
    assertDeepEquals(CD_TYPE_FACADE.createListTypeOf(name), actual);
  }

  public static void assertArrayOf(Class<?> clazz, ASTNode actual) {
    assertDeepEquals(CD_TYPE_FACADE.createArrayType(clazz, DEFAULT_ARRAY_DIMENSION), actual);
  }

  public static void assertArrayOf(String name, ASTNode actual) {
    assertDeepEquals(CD_TYPE_FACADE.createArrayType(name, DEFAULT_ARRAY_DIMENSION), actual);
  }
}
