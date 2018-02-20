/* (c) https://github.com/MontiCore/monticore */

package de.monticore.types;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.types.types._ast.ASTConstantsTypes;
import de.monticore.types.types._ast.ASTPrimitiveType;
import de.monticore.types.types._ast.ASTType;
import de.se_rwth.commons.logging.Log;

/**
 * @author Martin Schindler
 */
public class PrimitiveTypesTest {
  
  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testPrimitiveTypes() {
    try {
      String[] primitives = new String[] { "boolean", "byte", "char", "short", "int", "long",
          "float", "double" };
      for (String primitive : primitives) {
        ASTType type = TypesTestHelper.getInstance().parseType(primitive);
        assertNotNull(type);
        assertTrue(type instanceof ASTPrimitiveType);
      }
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
  }
  
  @Test
  public void testNegativePrimitiveTypes() {
    // these are no primitive types:
    try {
      String[] noprimitives = new String[] { "String", "bool", "char[]" };
      for (String noprimitive : noprimitives) {
        ASTType type = TypesTestHelper.getInstance().parseType(noprimitive);
        assertNotNull(type);
        assertFalse(type instanceof ASTPrimitiveType);
      }
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
  }
  
  @Test
  public void testBooleanType() {
    try {
      TypesTestHelper.getInstance().parseBooleanType("boolean");
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
  }
  
  @Test
  public void testNegativeBooleanType() {
    // these are no boolean types:
    try {
      ASTType ast = TypesTestHelper.getInstance().parseType("boolean[]");
      assertFalse(ast instanceof ASTPrimitiveType);
      
      String[] types = new String[] { "byte", "char", "short", "int", "long", "float", "double" };
      for (String type : types) {
        ast = TypesTestHelper.getInstance().parseType(type);
        assertTrue(ast instanceof ASTPrimitiveType);
        assertFalse(((ASTPrimitiveType) ast).getPrimitive() == ASTConstantsTypes.BOOLEAN);
      }
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
  }
  
  @Test
  public void testIntegralType() {
    try {
      String[] integrals = new String[] { "byte", "short", "int", "long", "char" };
      for (String integral : integrals) {
        assertNotNull(TypesTestHelper.getInstance().parseIntegralType(integral));
      }
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
  }
  
  @Test
  public void testNegativeIntegralType() {
    // these are no integral types:
    try {
      ASTType ast = TypesTestHelper.getInstance().parseType("byte[]");
      assertFalse(ast instanceof ASTPrimitiveType);
      
      String[] types = new String[] { "float", "double", "boolean" };
      for (String type : types) {
        ast = TypesTestHelper.getInstance().parseType(type);
        assertTrue(ast instanceof ASTPrimitiveType);
        assertFalse(((ASTPrimitiveType) ast).getPrimitive() == ASTConstantsTypes.INT);
      }
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
  }
  
  @Test
  public void testFloatingPointType() {
    try {
      String[] integrals = new String[] { "float", "double" };
      for (String integral : integrals) {
        assertNotNull(TypesTestHelper.getInstance().parseFloatingPointType(integral));
      }
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
  }
  
  @Test
  public void testNegativeFloatingPointType() {
    // these are no float types:
    try {
      ASTType ast = TypesTestHelper.getInstance().parseType("double[]");
      assertFalse(ast instanceof ASTPrimitiveType);
      
      String[] types = new String[] { "byte", "char", "short", "int", "long", "boolean" };
      for (String type : types) {
        ast = TypesTestHelper.getInstance().parseType(type);
        assertTrue(ast instanceof ASTPrimitiveType);
        assertFalse(((ASTPrimitiveType) ast).getPrimitive() == ASTConstantsTypes.FLOAT);
      }
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
  }
  
  @Test
  public void testNumericType() {
    try {
      String[] integrals = new String[] { "byte", "short", "int", "long", "char", "float", "double" };
      for (String integral : integrals) {
        TypesTestHelper.getInstance().parseNumericType(integral);
      }
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
  }
  
  @Test
  public void testNegativeNumericType() {
    // these are no integral types:
    try {
      ASTType ast = TypesTestHelper.getInstance().parseType("double[]");
      assertFalse(ast instanceof ASTPrimitiveType);
      ast = TypesTestHelper.getInstance().parseNumericType("double");
      assertTrue(ast instanceof ASTPrimitiveType);
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
  }
  
}
