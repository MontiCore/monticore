/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.helper;

import de.monticore.types.MCBasicTypesNodeIdentHelper;
import de.monticore.types.mcbasictypes._ast.*;
import de.monticore.types.mcbasictypestest._parser.MCBasicTypesTestParser;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.*;

public class MCBasicTypesNodeIdentHelperTest {
  @Test
  public void testGetIdent() throws IOException {
    MCBasicTypesTestParser parser = new MCBasicTypesTestParser();
    Optional<ASTMCQualifiedName> astmcQualifiedName = parser.parse_StringMCQualifiedName("java.util.List");
    Optional<ASTMCPrimitiveType> astmcPrimitiveType = parser.parse_StringMCPrimitiveType("int");
    Optional<ASTMCQualifiedType> astmcQualifiedType = parser.parse_StringMCQualifiedType("java.util.Set");
    Optional<ASTMCVoidType> astmcVoidType = parser.parse_StringMCVoidType("void");
    Optional<ASTMCReturnType> astmcReturnType = parser.parse_StringMCReturnType("float");
    Optional<ASTMCReturnType> astmcReturnTypeVoid = parser.parse_StringMCReturnType("void");

    assertFalse(parser.hasErrors());
    assertTrue(astmcQualifiedName.isPresent());
    assertTrue(astmcPrimitiveType.isPresent());
    assertTrue(astmcQualifiedType.isPresent());
    assertTrue(astmcVoidType.isPresent());
    assertTrue(astmcReturnType.isPresent());
    assertTrue(astmcReturnTypeVoid.isPresent());

    MCBasicTypesNodeIdentHelper helper = new MCBasicTypesNodeIdentHelper();
    assertEquals("@java.util.List!MCQualifiedName",helper.getIdent(astmcQualifiedName.get()));
    assertEquals("@int!MCPrimitiveType",helper.getIdent(astmcPrimitiveType.get()));
    assertEquals("@java.util.Set!MCQualifiedType",helper.getIdent(astmcQualifiedType.get()));
    assertEquals("@void!MCVoidType",helper.getIdent(astmcVoidType.get()));
    assertEquals("@float!MCPrimitiveType",helper.getIdent(astmcReturnType.get()));
    assertEquals("@void!MCVoidType",helper.getIdent(astmcReturnTypeVoid.get()));
  }

}
