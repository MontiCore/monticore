/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.helper;

import de.monticore.types.MCCollectionTypesNodeIdentHelper;
import de.monticore.types.mccollectiontypes._ast.ASTMCBasicTypeArgument;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;
import de.monticore.types.mccollectiontypes._ast.ASTMCPrimitiveTypeArgument;
import de.monticore.types.mccollectiontypestest.MCCollectionTypesTestMill;
import de.monticore.types.mccollectiontypestest._parser.MCCollectionTypesTestParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

public class MCCollectionTypesNodeIdentHelperTest {
  
  @BeforeEach
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    MCCollectionTypesTestMill.reset();
    MCCollectionTypesTestMill.init();
  }
  
  @Test
  public void testGetIdent() throws IOException {
    MCCollectionTypesTestParser parser = new MCCollectionTypesTestParser();
    Optional<ASTMCGenericType> astmcGenericType = parser.parse_StringMCGenericType("Map<String,Integer>");
    Optional<ASTMCGenericType> astmcGenericType1 = parser.parse_StringMCGenericType("List<Character>");
    Optional<ASTMCGenericType> astmcGenericType2 = parser.parse_StringMCGenericType("Set<Double>");
    Optional<ASTMCGenericType> astmcGenericType3 = parser.parse_StringMCGenericType("Optional<Byte>");
    Optional<ASTMCBasicTypeArgument> astmcBasicTypeArgument = parser.parse_StringMCBasicTypeArgument("a.B.C");
    Optional<ASTMCPrimitiveTypeArgument> astmcPrimitiveTypeArgument = parser.parse_StringMCPrimitiveTypeArgument("boolean");

    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(astmcGenericType.isPresent());
    Assertions.assertTrue(astmcGenericType1.isPresent());
    Assertions.assertTrue(astmcGenericType2.isPresent());
    Assertions.assertTrue(astmcGenericType3.isPresent());

    MCCollectionTypesNodeIdentHelper helper = new MCCollectionTypesNodeIdentHelper();
    Assertions.assertEquals("@Map!MCMapType", helper.getIdent(astmcGenericType.get()));
    Assertions.assertEquals("@List!MCListType", helper.getIdent(astmcGenericType1.get()));
    Assertions.assertEquals("@Set!MCSetType", helper.getIdent(astmcGenericType2.get()));
    Assertions.assertEquals("@Optional!MCOptionalType", helper.getIdent(astmcGenericType3.get()));
    Assertions.assertEquals("@a.B.C!MCBasicTypeArgument", helper.getIdent(astmcBasicTypeArgument.get()));
    Assertions.assertEquals("@boolean!MCPrimitiveTypeArgument", helper.getIdent(astmcPrimitiveTypeArgument.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
}
