/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.helper;

import de.monticore.types.MCBasicTypesHelper;
import de.monticore.types.mcbasictypes._ast.ASTConstantsMCBasicTypes;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MCBasicTypesHelperTest {

  @Test
  public void testGetPrimitive(){
    assertEquals(ASTConstantsMCBasicTypes.BOOLEAN,MCBasicTypesHelper.primitiveName2Const("boolean"));
    assertEquals(-1,MCBasicTypesHelper.primitiveName2Const(null));
    assertEquals(-1,MCBasicTypesHelper.primitiveName2Const(""));
    assertEquals(ASTConstantsMCBasicTypes.BYTE,MCBasicTypesHelper.primitiveName2Const("byte"));
    assertEquals(ASTConstantsMCBasicTypes.CHAR,MCBasicTypesHelper.primitiveName2Const("char"));
    assertEquals(ASTConstantsMCBasicTypes.DOUBLE,MCBasicTypesHelper.primitiveName2Const("double"));
    assertEquals(ASTConstantsMCBasicTypes.FLOAT,MCBasicTypesHelper.primitiveName2Const("float"));
    assertEquals(ASTConstantsMCBasicTypes.INT,MCBasicTypesHelper.primitiveName2Const("int"));
    assertEquals(ASTConstantsMCBasicTypes.LONG,MCBasicTypesHelper.primitiveName2Const("long"));
    assertEquals(ASTConstantsMCBasicTypes.SHORT,MCBasicTypesHelper.primitiveName2Const("short"));
  }
}
