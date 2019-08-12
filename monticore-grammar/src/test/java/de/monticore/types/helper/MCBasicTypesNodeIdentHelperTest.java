/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.helper;

import de.monticore.types.MCBasicTypesNodeIdentHelper;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
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

    assertFalse(parser.hasErrors());
    assertTrue(astmcQualifiedName.isPresent());

    MCBasicTypesNodeIdentHelper helper = new MCBasicTypesNodeIdentHelper();
    assertEquals("@List!MCQualifiedName",helper.getIdent(astmcQualifiedName.get()));
    assertEquals("@6!MCPrimitiveType",helper.getIdent(astmcPrimitiveType.get()));
  }

}
