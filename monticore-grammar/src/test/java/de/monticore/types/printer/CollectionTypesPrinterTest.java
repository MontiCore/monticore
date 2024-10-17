/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.printer;

import de.monticore.types.mccollectiontypes.MCCollectionTypesMill;
import de.monticore.types.mccollectiontypes._ast.*;
import de.monticore.types.mccollectiontypestest.MCCollectionTypesTestMill;
import de.monticore.types.mccollectiontypestest._parser.MCCollectionTypesTestParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

public class CollectionTypesPrinterTest {
  
  @BeforeEach
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    MCCollectionTypesTestMill.reset();
    MCCollectionTypesTestMill.init();
  }
  
  @Test
  public void testPrintType() throws IOException {
    MCCollectionTypesTestParser parser = new MCCollectionTypesTestParser();
    Optional<ASTMCBasicTypeArgument> astmcBasicTypeArgument = parser.parse_StringMCBasicTypeArgument("java.util.List");
    Optional<ASTMCPrimitiveTypeArgument> astmcPrimitiveTypeArgument = parser.parse_StringMCPrimitiveTypeArgument("int");
    Optional<ASTMCListType> astmcListType = parser.parse_StringMCListType("List<java.lang.String>");
    Optional<ASTMCSetType> astmcSetType = parser.parse_StringMCSetType("Set<int>");
    Optional<ASTMCOptionalType> astmcOptionalType = parser.parse_StringMCOptionalType("Optional<Character>");
    Optional<ASTMCMapType> astmcMapType = parser.parse_StringMCMapType("Map<String,String>");

    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(astmcBasicTypeArgument.isPresent());
    Assertions.assertTrue(astmcPrimitiveTypeArgument.isPresent());
    Assertions.assertTrue(astmcListType.isPresent());
    Assertions.assertTrue(astmcSetType.isPresent());
    Assertions.assertTrue(astmcOptionalType.isPresent());
    Assertions.assertTrue(astmcMapType.isPresent());

    Assertions.assertEquals("java.util.List", MCCollectionTypesMill.prettyPrint(astmcBasicTypeArgument.get(), true));
    Assertions.assertEquals("int", MCCollectionTypesMill.prettyPrint(astmcPrimitiveTypeArgument.get(), true));
    Assertions.assertEquals("List<java.lang.String>", MCCollectionTypesMill.prettyPrint(astmcListType.get(), true));
    Assertions.assertEquals("Set<int>", MCCollectionTypesMill.prettyPrint(astmcSetType.get(), true));
    Assertions.assertEquals("Optional<Character>", MCCollectionTypesMill.prettyPrint(astmcOptionalType.get(), true));
    Assertions.assertEquals("Map<String,String>", MCCollectionTypesMill.prettyPrint(astmcMapType.get(), true));

    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
}
