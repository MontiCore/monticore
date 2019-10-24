/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.helper;

import de.monticore.types.MCBasicTypesHelper;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypestest._parser.MCBasicTypesTestParser;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class MCBasicTypesHelperTest {
  @Test
  public void testIsPrimitive() throws IOException {
    MCBasicTypesTestParser parser = new MCBasicTypesTestParser();
    Optional<ASTMCPrimitiveType> astmcPrimitiveType = parser.parse_StringMCPrimitiveType("boolean");
    assertTrue(astmcPrimitiveType.isPresent());
    assertTrue(MCBasicTypesHelper.isPrimitive(astmcPrimitiveType.get()));
    assertFalse(parser.hasErrors());
  }

  @Test
  public void testIsNullable() throws IOException{
    MCBasicTypesTestParser parser = new MCBasicTypesTestParser();
    Optional<ASTMCQualifiedType> astmcQualifiedType = parser.parse_StringMCQualifiedType("String");
    assertTrue(astmcQualifiedType.isPresent());
    assertFalse(MCBasicTypesHelper.isPrimitive(astmcQualifiedType.get()));
    assertFalse(parser.hasErrors());
  }

  @Test
  public void testGetListFromSeperatedString() throws IOException{
    List<String> stringList = MCBasicTypesHelper.createListFromDotSeparatedString("java.util.List");
    assertFalse(stringList.isEmpty());
    assertTrue(stringList.get(0).equals("java"));
    assertTrue(stringList.get(1).equals("util"));
    assertTrue(stringList.get(2).equals("List"));
  }

  @Test
  public void testGetPrimitive(){
    assertEquals(1,MCBasicTypesHelper.primitiveName2Const("boolean"));
    assertEquals(-1,MCBasicTypesHelper.primitiveName2Const(null));
    assertEquals(-1,MCBasicTypesHelper.primitiveName2Const(""));
    assertEquals(2,MCBasicTypesHelper.primitiveName2Const("byte"));
    assertEquals(3,MCBasicTypesHelper.primitiveName2Const("char"));
    assertEquals(4,MCBasicTypesHelper.primitiveName2Const("double"));
    assertEquals(5,MCBasicTypesHelper.primitiveName2Const("float"));
    assertEquals(6,MCBasicTypesHelper.primitiveName2Const("int"));
    assertEquals(7,MCBasicTypesHelper.primitiveName2Const("long"));
    assertEquals(8,MCBasicTypesHelper.primitiveName2Const("short"));
  }
}
