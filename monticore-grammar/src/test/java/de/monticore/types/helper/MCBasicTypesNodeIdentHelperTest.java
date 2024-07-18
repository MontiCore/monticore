/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.helper;

import de.monticore.types.MCBasicTypesNodeIdentHelper;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.monticore.types.mcbasictypes._ast.*;
import de.monticore.types.mcbasictypestest._parser.MCBasicTypesTestParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

public class MCBasicTypesNodeIdentHelperTest {
  
  @BeforeEach
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    MCBasicTypesMill.reset();
    MCBasicTypesMill.init();
  }
  
  @Test
  public void testGetIdent() throws IOException {
    MCBasicTypesTestParser parser = new MCBasicTypesTestParser();
    Optional<ASTMCQualifiedName> astmcQualifiedName = parser.parse_StringMCQualifiedName("java.util.List");
    Optional<ASTMCPrimitiveType> astmcPrimitiveType = parser.parse_StringMCPrimitiveType("int");
    Optional<ASTMCQualifiedType> astmcQualifiedType = parser.parse_StringMCQualifiedType("java.util.Set");
    Optional<ASTMCVoidType> astmcVoidType = parser.parse_StringMCVoidType("void");
    Optional<ASTMCReturnType> astmcReturnType = parser.parse_StringMCReturnType("float");
    Optional<ASTMCReturnType> astmcReturnTypeVoid = parser.parse_StringMCReturnType("void");

    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(astmcQualifiedName.isPresent());
    Assertions.assertTrue(astmcPrimitiveType.isPresent());
    Assertions.assertTrue(astmcQualifiedType.isPresent());
    Assertions.assertTrue(astmcVoidType.isPresent());
    Assertions.assertTrue(astmcReturnType.isPresent());
    Assertions.assertTrue(astmcReturnTypeVoid.isPresent());

    MCBasicTypesNodeIdentHelper helper = new MCBasicTypesNodeIdentHelper();
    Assertions.assertEquals("@java.util.List!MCQualifiedName", helper.getIdent(astmcQualifiedName.get()));
    Assertions.assertEquals("@int!MCPrimitiveType", helper.getIdent(astmcPrimitiveType.get()));
    Assertions.assertEquals("@java.util.Set!MCQualifiedType", helper.getIdent(astmcQualifiedType.get()));
    Assertions.assertEquals("@void!MCVoidType", helper.getIdent(astmcVoidType.get()));
    Assertions.assertEquals("@float!MCPrimitiveType", helper.getIdent(astmcReturnType.get()));
    Assertions.assertEquals("@void!MCVoidType", helper.getIdent(astmcReturnTypeVoid.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

}
