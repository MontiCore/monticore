/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.helper;

import de.monticore.types.MCCollectionTypesNodeIdentHelper;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import de.monticore.types.mccollectiontypes._ast.ASTMCBasicTypeArgument;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;
import de.monticore.types.mccollectiontypes._ast.ASTMCPrimitiveTypeArgument;
import de.monticore.types.mccollectiontypestest._parser.MCCollectionTypesTestParser;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.*;

public class MCCollectionTypesNodeIdentHelperTest {
  @Test
  public void testGetIdent() throws IOException {
    MCCollectionTypesTestParser parser = new MCCollectionTypesTestParser();
    Optional<ASTMCGenericType> astmcGenericType = parser.parse_StringMCGenericType("Map<String,Integer>");
    Optional<ASTMCGenericType> astmcGenericType1 = parser.parse_StringMCGenericType("List<Character>");
    Optional<ASTMCGenericType> astmcGenericType2 = parser.parse_StringMCGenericType("Set<Double>");
    Optional<ASTMCGenericType> astmcGenericType3 = parser.parse_StringMCGenericType("Optional<Byte>");
    Optional<ASTMCBasicTypeArgument> astmcBasicTypeArgument = parser.parse_StringMCBasicTypeArgument("a.B.C");
    Optional<ASTMCPrimitiveTypeArgument> astmcPrimitiveTypeArgument = parser.parse_StringMCPrimitiveTypeArgument("boolean");

    assertFalse(parser.hasErrors());
    assertTrue(astmcGenericType.isPresent());
    assertTrue(astmcGenericType1.isPresent());
    assertTrue(astmcGenericType2.isPresent());
    assertTrue(astmcGenericType3.isPresent());

    MCCollectionTypesNodeIdentHelper helper = new MCCollectionTypesNodeIdentHelper();
    assertEquals("@Map!MCMapType", helper.getIdent(astmcGenericType.get()));
    assertEquals("@List!MCListType", helper.getIdent(astmcGenericType1.get()));
    assertEquals("@Set!MCSetType", helper.getIdent(astmcGenericType2.get()));
    assertEquals("@Optional!MCOptionalType",helper.getIdent(astmcGenericType3.get()));
    assertEquals("@a.B.C!MCBasicTypeArgument", helper.getIdent(astmcBasicTypeArgument.get()));
    assertEquals("@boolean!MCPrimitiveTypeArgument",helper.getIdent(astmcPrimitiveTypeArgument.get()));
  }
}
