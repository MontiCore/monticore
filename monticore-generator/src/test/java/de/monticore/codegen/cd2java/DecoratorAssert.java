package de.monticore.codegen.cd2java;

import de.monticore.ast.ASTNode;

import static org.junit.Assert.assertTrue;

public final class DecoratorAssert {

  protected DecoratorAssert() {}

  public static void assertDeepEquals(ASTNode expected, ASTNode actual) {
    assertTrue(expected.deepEquals(actual));
  }
}
