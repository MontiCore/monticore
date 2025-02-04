/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.helper;

import de.monticore.types.MCBasicTypesHelper;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.monticore.types.mcbasictypes._ast.ASTConstantsMCBasicTypes;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MCBasicTypesHelperTest {
  
  @BeforeEach
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    MCBasicTypesMill.reset();
    MCBasicTypesMill.init();
  }
  
  @Test
  public void testGetPrimitive(){
    Assertions.assertEquals(ASTConstantsMCBasicTypes.BOOLEAN, MCBasicTypesHelper.primitiveName2Const("boolean"));
    Assertions.assertEquals(-1, MCBasicTypesHelper.primitiveName2Const(null));
    Assertions.assertEquals(-1, MCBasicTypesHelper.primitiveName2Const(""));
    Assertions.assertEquals(ASTConstantsMCBasicTypes.BYTE, MCBasicTypesHelper.primitiveName2Const("byte"));
    Assertions.assertEquals(ASTConstantsMCBasicTypes.CHAR, MCBasicTypesHelper.primitiveName2Const("char"));
    Assertions.assertEquals(ASTConstantsMCBasicTypes.DOUBLE, MCBasicTypesHelper.primitiveName2Const("double"));
    Assertions.assertEquals(ASTConstantsMCBasicTypes.FLOAT, MCBasicTypesHelper.primitiveName2Const("float"));
    Assertions.assertEquals(ASTConstantsMCBasicTypes.INT, MCBasicTypesHelper.primitiveName2Const("int"));
    Assertions.assertEquals(ASTConstantsMCBasicTypes.LONG, MCBasicTypesHelper.primitiveName2Const("long"));
    Assertions.assertEquals(ASTConstantsMCBasicTypes.SHORT, MCBasicTypesHelper.primitiveName2Const("short"));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
}
